package com.thermalprinter.test;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Base64;
import android.util.Log;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.PluginCall;

import java.util.HashMap;

public class UsbPrinterManager {
    private static final String TAG = "UsbPrinterManager";
    private static final String ACTION_USB_PERMISSION = "com.thermalprinter.test.USB_PERMISSION";
    
    private Activity activity;
    private UsbManager usbManager;
    private UsbDevice currentDevice;
    private UsbDeviceConnection connection;
    private UsbEndpoint endpointOut;
    private UsbInterface usbInterface;
    
    private final BroadcastReceiver usbPermissionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            Log.d(TAG, "USB permission granted for device: " + device.getDeviceName());
                        }
                    } else {
                        Log.d(TAG, "USB permission denied for device: " + device);
                    }
                }
            }
        }
    };

    public UsbPrinterManager(Activity activity) {
        this.activity = activity;
        this.usbManager = (UsbManager) activity.getSystemService(Context.USB_SERVICE);
        
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            activity.registerReceiver(usbPermissionReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            activity.registerReceiver(usbPermissionReceiver, filter);
        }
    }

    public void listUsbDevices(PluginCall call) {
        try {
            HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
            JSArray devices = new JSArray();
            
            for (UsbDevice device : deviceList.values()) {
                JSObject deviceInfo = new JSObject();
                deviceInfo.put("deviceName", device.getDeviceName());
                deviceInfo.put("vendorId", device.getVendorId());
                deviceInfo.put("productId", device.getProductId());
                deviceInfo.put("deviceClass", device.getDeviceClass());
                deviceInfo.put("deviceSubclass", device.getDeviceSubclass());
                deviceInfo.put("manufacturerName", device.getManufacturerName());
                deviceInfo.put("productName", device.getProductName());
                devices.put(deviceInfo);
            }
            
            JSObject result = new JSObject();
            result.put("devices", devices);
            call.resolve(result);
        } catch (Exception e) {
            Log.e(TAG, "Error listing USB devices", e);
            call.reject("Failed to list USB devices: " + e.getMessage());
        }
    }

    public void connectToPrinter(int vendorId, int productId, PluginCall call) {
        try {
            HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
            UsbDevice targetDevice = null;
            
            for (UsbDevice device : deviceList.values()) {
                if (device.getVendorId() == vendorId && device.getProductId() == productId) {
                    targetDevice = device;
                    break;
                }
            }
            
            if (targetDevice == null) {
                call.reject("Device not found with vendorId: " + vendorId + " and productId: " + productId);
                return;
            }
            
            if (!usbManager.hasPermission(targetDevice)) {
                PendingIntent permissionIntent = PendingIntent.getBroadcast(
                    activity, 
                    0, 
                    new Intent(ACTION_USB_PERMISSION), 
                    PendingIntent.FLAG_MUTABLE
                );
                usbManager.requestPermission(targetDevice, permissionIntent);
                call.reject("USB permission requested. Please grant permission and try again.");
                return;
            }
            
            connection = usbManager.openDevice(targetDevice);
            if (connection == null) {
                call.reject("Failed to open USB device connection");
                return;
            }
            
            if (targetDevice.getInterfaceCount() == 0) {
                call.reject("No USB interfaces found");
                return;
            }
            
            usbInterface = targetDevice.getInterface(0);
            
            if (!connection.claimInterface(usbInterface, true)) {
                call.reject("Failed to claim USB interface");
                return;
            }
            
            for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
                UsbEndpoint endpoint = usbInterface.getEndpoint(i);
                if (endpoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK &&
                    endpoint.getDirection() == UsbConstants.USB_DIR_OUT) {
                    endpointOut = endpoint;
                    break;
                }
            }
            
            if (endpointOut == null) {
                call.reject("No bulk OUT endpoint found");
                return;
            }
            
            currentDevice = targetDevice;
            
            JSObject result = new JSObject();
            result.put("success", true);
            result.put("deviceName", targetDevice.getDeviceName());
            result.put("message", "Connected to printer successfully");
            call.resolve(result);
            
            Log.d(TAG, "Successfully connected to printer: " + targetDevice.getDeviceName());
        } catch (Exception e) {
            Log.e(TAG, "Error connecting to printer", e);
            call.reject("Failed to connect to printer: " + e.getMessage());
        }
    }

    public void disconnectPrinter(PluginCall call) {
        try {
            if (connection != null && usbInterface != null) {
                connection.releaseInterface(usbInterface);
                connection.close();
            }
            
            currentDevice = null;
            connection = null;
            usbInterface = null;
            endpointOut = null;
            
            JSObject result = new JSObject();
            result.put("success", true);
            result.put("message", "Disconnected from printer");
            call.resolve(result);
            
            Log.d(TAG, "Disconnected from printer");
        } catch (Exception e) {
            Log.e(TAG, "Error disconnecting from printer", e);
            call.reject("Failed to disconnect from printer: " + e.getMessage());
        }
    }

    public void printRawData(String base64Data, PluginCall call) {
        try {
            if (connection == null || endpointOut == null) {
                call.reject("Printer not connected");
                return;
            }
            
            byte[] data = Base64.decode(base64Data, Base64.DEFAULT);
            
            int transferred = connection.bulkTransfer(endpointOut, data, data.length, 5000);
            
            if (transferred < 0) {
                call.reject("Failed to send data to printer");
                return;
            }
            
            JSObject result = new JSObject();
            result.put("success", true);
            result.put("bytesTransferred", transferred);
            result.put("message", "Data sent to printer successfully");
            call.resolve(result);
            
            Log.d(TAG, "Sent " + transferred + " bytes to printer");
        } catch (Exception e) {
            Log.e(TAG, "Error printing data", e);
            call.reject("Failed to print data: " + e.getMessage());
        }
    }
}
