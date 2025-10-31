package com.thermalprinter.test;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.util.Log;
import android.view.Display;

import com.getcapacitor.JSObject;
import com.getcapacitor.PluginCall;

public class DualScreenManager {
    private static final String TAG = "DualScreenManager";
    
    private Activity activity;
    private DisplayManager displayManager;
    private boolean isCustomerDisplayActive = false;

    public DualScreenManager(Activity activity) {
        this.activity = activity;
        this.displayManager = (DisplayManager) activity.getSystemService(Context.DISPLAY_SERVICE);
    }

    public void checkSecondaryDisplay(PluginCall call) {
        try {
            Display[] displays = displayManager.getDisplays();
            
            JSObject result = new JSObject();
            result.put("hasSecondaryDisplay", displays.length > 1);
            result.put("displayCount", displays.length);
            
            if (displays.length > 1) {
                Display secondaryDisplay = displays[1];
                JSObject displayInfo = new JSObject();
                displayInfo.put("displayId", secondaryDisplay.getDisplayId());
                displayInfo.put("name", secondaryDisplay.getName());
                displayInfo.put("width", secondaryDisplay.getMode().getPhysicalWidth());
                displayInfo.put("height", secondaryDisplay.getMode().getPhysicalHeight());
                result.put("secondaryDisplayInfo", displayInfo);
            }
            
            call.resolve(result);
            Log.d(TAG, "Display count: " + displays.length);
        } catch (Exception e) {
            Log.e(TAG, "Error checking secondary display", e);
            call.reject("Failed to check secondary display: " + e.getMessage());
        }
    }

    public void showOnSecondaryDisplay(String html, PluginCall call) {
        try {
            Display[] displays = displayManager.getDisplays();
            
            if (displays.length < 2) {
                call.reject("No secondary display found");
                return;
            }
            
            Display secondaryDisplay = displays[1];
            int displayId = secondaryDisplay.getDisplayId();
            
            activity.runOnUiThread(() -> {
                try {
                    // Store HTML content for the new activity
                    CustomerDisplayActivity.setPendingContent(html);
                    
                    // Create intent for CustomerDisplayActivity
                    Intent intent = new Intent(activity, CustomerDisplayActivity.class);
                    intent.putExtra("html_content", html);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    
                    // Launch on secondary display using ActivityOptions
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        ActivityOptions options = ActivityOptions.makeBasic();
                        options.setLaunchDisplayId(displayId);
                        activity.startActivity(intent, options.toBundle());
                        Log.d(TAG, "Launched CustomerDisplayActivity on display " + displayId + " for independent dual-touch");
                    } else {
                        activity.startActivity(intent);
                        Log.d(TAG, "Launched CustomerDisplayActivity (legacy mode)");
                    }
                    
                    isCustomerDisplayActive = true;
                    
                    JSObject result = new JSObject();
                    result.put("success", true);
                    result.put("message", "Customer display activity launched with independent touch input");
                    call.resolve(result);
                    
                } catch (Exception e) {
                    Log.e(TAG, "Error launching customer display activity", e);
                    call.reject("Failed to launch customer display: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error showing on secondary display", e);
            call.reject("Failed to show on secondary display: " + e.getMessage());
        }
    }

    public void hideSecondaryDisplay(PluginCall call) {
        try {
            activity.runOnUiThread(() -> {
                // Finish the customer display activity to properly close it
                CustomerDisplayActivity customerActivity = CustomerDisplayActivity.getInstance();
                if (customerActivity != null) {
                    customerActivity.finish();
                    Log.d(TAG, "CustomerDisplayActivity finished");
                }
                
                isCustomerDisplayActive = false;
                
                JSObject result = new JSObject();
                result.put("success", true);
                result.put("message", "Customer display closed");
                call.resolve(result);
            });
        } catch (Exception e) {
            Log.e(TAG, "Error hiding secondary display", e);
            call.reject("Failed to hide secondary display: " + e.getMessage());
        }
    }
}
