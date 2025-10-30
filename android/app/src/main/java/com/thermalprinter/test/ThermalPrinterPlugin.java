package com.thermalprinter.test;

import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "ThermalPrinter")
public class ThermalPrinterPlugin extends Plugin {

    private UsbPrinterManager usbPrinterManager;
    private DualScreenManager dualScreenManager;

    @Override
    public void load() {
        super.load();
        usbPrinterManager = new UsbPrinterManager(getActivity());
        dualScreenManager = new DualScreenManager(getActivity());
    }

    @PluginMethod
    public void listUsbDevices(PluginCall call) {
        usbPrinterManager.listUsbDevices(call);
    }

    @PluginMethod
    public void connectToPrinter(PluginCall call) {
        Integer vendorId = call.getInt("vendorId");
        Integer productId = call.getInt("productId");
        
        if (vendorId == null || productId == null) {
            call.reject("vendorId and productId are required");
            return;
        }
        
        usbPrinterManager.connectToPrinter(vendorId, productId, call);
    }

    @PluginMethod
    public void disconnectPrinter(PluginCall call) {
        usbPrinterManager.disconnectPrinter(call);
    }

    @PluginMethod
    public void printRawData(PluginCall call) {
        String data = call.getString("data");
        
        if (data == null) {
            call.reject("data is required");
            return;
        }
        
        usbPrinterManager.printRawData(data, call);
    }

    @PluginMethod
    public void checkSecondaryDisplay(PluginCall call) {
        dualScreenManager.checkSecondaryDisplay(call);
    }

    @PluginMethod
    public void showOnSecondaryDisplay(PluginCall call) {
        String html = call.getString("html");
        
        if (html == null) {
            call.reject("html is required");
            return;
        }
        
        dualScreenManager.showOnSecondaryDisplay(html, call);
    }

    @PluginMethod
    public void hideSecondaryDisplay(PluginCall call) {
        dualScreenManager.hideSecondaryDisplay(call);
    }
}
