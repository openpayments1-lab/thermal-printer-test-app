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
        Log.d(TAG, "MainActivity created");
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        // Don't let the activity pause when secondary display is shown
        // This keeps the main WebView alive and responsive
        Log.d(TAG, "MainActivity onPause - keeping WebView active");
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "MainActivity onResume");
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "MainActivity onStop");
    }
    
    @Override
    protected void onDestroy() {
        Log.d(TAG, "MainActivity onDestroy");
        super.onDestroy();
    }
}
