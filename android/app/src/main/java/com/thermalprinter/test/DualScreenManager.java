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
            // Use DISPLAY_CATEGORY_PRESENTATION to find external customer displays
            Display[] presentationDisplays = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                presentationDisplays = displayManager.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION);
            }
            
            Display[] allDisplays = displayManager.getDisplays();
            
            // Log all displays for diagnostics
            for (Display display : allDisplays) {
                Log.d(TAG, "Display found - ID: " + display.getDisplayId() + 
                      ", Name: " + display.getName() + 
                      ", State: " + display.getState() +
                      ", IsDefault: " + (display.getDisplayId() == Display.DEFAULT_DISPLAY));
            }
            
            // Find the external presentation display (true customer screen)
            Display customerDisplay = null;
            
            // First try presentation displays (Android 12+)
            if (presentationDisplays != null && presentationDisplays.length > 0) {
                customerDisplay = presentationDisplays[0];
                Log.d(TAG, "Using PRESENTATION display ID " + customerDisplay.getDisplayId() + " for customer screen");
            } else {
                // Fallback: Use non-default display
                for (Display display : allDisplays) {
                    if (display.getDisplayId() != Display.DEFAULT_DISPLAY) {
                        customerDisplay = display;
                        Log.d(TAG, "Using non-default display ID " + customerDisplay.getDisplayId() + " for customer screen");
                        break;
                    }
                }
            }
            
            JSObject result = new JSObject();
            result.put("hasSecondaryDisplay", customerDisplay != null);
            result.put("displayCount", allDisplays.length);
            
            if (customerDisplay != null) {
                JSObject displayInfo = new JSObject();
                displayInfo.put("displayId", customerDisplay.getDisplayId());
                displayInfo.put("name", customerDisplay.getName());
                displayInfo.put("width", customerDisplay.getMode().getPhysicalWidth());
                displayInfo.put("height", customerDisplay.getMode().getPhysicalHeight());
                result.put("secondaryDisplayInfo", displayInfo);
            }
            
            call.resolve(result);
        } catch (Exception e) {
            Log.e(TAG, "Error checking secondary display", e);
            call.reject("Failed to check secondary display: " + e.getMessage());
        }
    }

    public void showOnSecondaryDisplay(String html, PluginCall call) {
        try {
            // Use DISPLAY_CATEGORY_PRESENTATION to find the real external customer display
            Display[] presentationDisplays = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                presentationDisplays = displayManager.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION);
            }
            
            Display[] allDisplays = displayManager.getDisplays();
            
            // Find the external presentation display (true customer screen)
            Display customerDisplay = null;
            
            // First try presentation displays (Android 12+)
            if (presentationDisplays != null && presentationDisplays.length > 0) {
                customerDisplay = presentationDisplays[0];
                Log.d(TAG, "Selected PRESENTATION display ID " + customerDisplay.getDisplayId() + 
                      " (" + customerDisplay.getName() + ") for customer display");
            } else {
                // Fallback: Use non-default display
                for (Display display : allDisplays) {
                    if (display.getDisplayId() != Display.DEFAULT_DISPLAY) {
                        customerDisplay = display;
                        Log.d(TAG, "Selected non-default display ID " + customerDisplay.getDisplayId() + 
                              " (" + customerDisplay.getName() + ") for customer display");
                        break;
                    }
                }
            }
            
            if (customerDisplay == null) {
                call.reject("No external customer display found");
                return;
            }
            
            int displayId = customerDisplay.getDisplayId();
            
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
