package com.thermalprinter.test;

import android.os.Bundle;
import android.util.Log;
import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
    private static final String TAG = "MainActivity";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Register inline custom plugins BEFORE calling super.onCreate()
        registerPlugin(ThermalPrinterPlugin.class);
        super.onCreate(savedInstanceState);
        
        android.view.Display display = getWindowManager().getDefaultDisplay();
        Log.d(TAG, "MainActivity created on display:");
        Log.d(TAG, "  Display ID: " + display.getDisplayId());
        Log.d(TAG, "  Display Name: " + display.getName());
        Log.d(TAG, "  Is DEFAULT_DISPLAY: " + (display.getDisplayId() == android.view.Display.DEFAULT_DISPLAY));
        Log.d(TAG, "  This should be the EMPLOYEE screen with full touch control");
    }
    
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "MainActivity onResume - employee screen should be touchable");
    }
}
