package com.thermalprinter.test;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.PluginCall;
import com.printer.sdk.PrinterInstance;
import com.printer.sdk.PrinterConstants;

import java.util.HashMap;

public class UsbPrinterManager {
    private static final String TAG = "UsbPrinterManager";
    private static final String ACTION_USB_PERMISSION = "com.thermalprinter.test.USB_PERMISSION";
    
    private Activity activity;
    private UsbManager usbManager;
    private UsbDevice currentDevice;
    private PrinterInstance printerInstance;
    private PluginCall pendingCall;
    
    private final Handler connectionHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (pendingCall == null) return;
            
            switch (msg.what) {
                case PrinterConstants.Connect.SUCCESS:
                    Log.d(TAG, "Printer connected successfully via VOLCORA SDK");
                    
                    if (printerInstance != null) {
                        printerInstance.initPrinter();
                        Log.d(TAG, "Printer initialized");
                    }
                    
                    JSObject successResult = new JSObject();
                    successResult.put("success", true);
                    successResult.put("deviceName", currentDevice != null ? currentDevice.getDeviceName() : "Unknown");
                    successResult.put("message", "Connected to printer successfully");
                    pendingCall.resolve(successResult);
                    pendingCall = null;
                    break;
                    
                case PrinterConstants.Connect.FAILED:
                    Log.e(TAG, "Printer connection failed");
                    pendingCall.reject("Failed to connect to printer");
                    pendingCall = null;
                    break;
                    
                case PrinterConstants.Connect.CLOSED:
                    Log.d(TAG, "Printer connection closed");
                    if (pendingCall != null) {
                        JSObject closedResult = new JSObject();
                        closedResult.put("success", true);
                        closedResult.put("message", "Printer connection closed");
                        pendingCall.resolve(closedResult);
                        pendingCall = null;
                    }
                    break;
            }
        }
    };
    
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
            
            currentDevice = targetDevice;
            pendingCall = call;
            
            printerInstance = PrinterInstance.getPrinterInstance(activity, targetDevice, connectionHandler);
            
            boolean connected = printerInstance.openConnection();
            
            if (!connected) {
                pendingCall = null;
                call.reject("Failed to open connection to printer");
                return;
            }
            
            Log.d(TAG, "VOLCORA SDK: Attempting to connect to printer: " + targetDevice.getDeviceName());
            
        } catch (Exception e) {
            Log.e(TAG, "Error connecting to printer", e);
            pendingCall = null;
            call.reject("Failed to connect to printer: " + e.getMessage());
        }
    }

    public void disconnectPrinter(PluginCall call) {
        try {
            if (printerInstance != null) {
                printerInstance.closeConnection();
                printerInstance = null;
            }
            
            currentDevice = null;
            
            JSObject result = new JSObject();
            result.put("success", true);
            result.put("message", "Disconnected from printer");
            call.resolve(result);
            
            Log.d(TAG, "Disconnected from printer via VOLCORA SDK");
        } catch (Exception e) {
            Log.e(TAG, "Error disconnecting from printer", e);
            call.reject("Failed to disconnect from printer: " + e.getMessage());
        }
    }

    public void printRawData(String base64Data, PluginCall call) {
        try {
            if (printerInstance == null) {
                call.reject("Printer not connected");
                return;
            }
            
            byte[] data = Base64.decode(base64Data, Base64.DEFAULT);
            
            int result = printerInstance.sendBytesData(data);
            
            if (result < 0) {
                String errorMsg;
                switch (result) {
                    case -1:
                        errorMsg = "Printer not initialized";
                        break;
                    case -2:
                        errorMsg = "Data is empty or invalid";
                        break;
                    case -3:
                        errorMsg = "Failed to send data to printer";
                        break;
                    default:
                        errorMsg = "Unknown error: " + result;
                        break;
                }
                call.reject(errorMsg);
                return;
            }
            
            JSObject resultObj = new JSObject();
            resultObj.put("success", true);
            resultObj.put("bytesTransferred", result);
            resultObj.put("message", "Data sent to printer successfully");
            call.resolve(resultObj);
            
            Log.d(TAG, "VOLCORA SDK: Sent " + result + " bytes to printer");
        } catch (Exception e) {
            Log.e(TAG, "Error printing data", e);
            call.reject("Failed to print data: " + e.getMessage());
        }
    }
}
