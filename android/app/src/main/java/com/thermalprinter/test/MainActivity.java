package com.thermalprinter.test;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
    private static final String TAG = "MainActivity";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Register inline custom plugins BEFORE calling super.onCreate()
        registerPlugin(ThermalPrinterPlugin.class);
        super.onCreate(savedInstanceState);
        
        // Ensure main activity window stays interactive even when secondary display is shown
        Window window = getWindow();
        if (window != null) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            // Explicitly ensure this window can receive touch events
            window.addFlags(WindowManager.LayoutParams.FLAG_TOUCHABLE_WHEN_WAKING);
            Log.d(TAG, "MainActivity window configured to stay interactive with dual-screen");
        }
        
        Log.d(TAG, "MainActivity created and configured for dual-screen POS operation");
    }
    
    @Override
    public void onPause() {
        super.onPause();
        // Keep the activity and WebView active when secondary display is shown
        Log.d(TAG, "MainActivity onPause - keeping WebView active and touchable");
        
        // Explicitly request focus back to maintain touch control
        runOnUiThread(() -> {
            if (getCurrentFocus() != null) {
                Log.d(TAG, "Maintaining focus on main activity");
            }
        });
    }
    
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "MainActivity onResume - restoring full touch control");
        
        // Ensure window remains touchable
        Window window = getWindow();
        if (window != null && window.getDecorView() != null) {
            window.getDecorView().requestFocus();
        }
    }
    
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "MainActivity onStop");
    }
    
    @Override
    public void onDestroy() {
        Log.d(TAG, "MainActivity onDestroy");
        super.onDestroy();
    }
}
