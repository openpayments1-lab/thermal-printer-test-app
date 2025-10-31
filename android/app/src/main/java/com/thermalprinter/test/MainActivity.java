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
        Log.d(TAG, "MainActivity created - ready for dual-screen with separate activity architecture");
    }
    
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "MainActivity onResume - employee screen active with independent touch");
    }
}
