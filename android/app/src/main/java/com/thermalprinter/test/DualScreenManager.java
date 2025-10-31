package com.thermalprinter.test;

import android.app.Activity;
import android.app.Presentation;
import android.content.Context;
import android.graphics.Color;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.widget.LinearLayout;

import com.getcapacitor.JSObject;
import com.getcapacitor.PluginCall;

public class DualScreenManager {
    private static final String TAG = "DualScreenManager";
    
    private Activity activity;
    private DisplayManager displayManager;
    private CustomerDisplayPresentation presentation;

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
            
            activity.runOnUiThread(() -> {
                if (presentation != null) {
                    presentation.dismiss();
                }
                
                presentation = new CustomerDisplayPresentation(activity, secondaryDisplay, html);
                presentation.show();
                
                JSObject result = new JSObject();
                result.put("success", true);
                result.put("message", "Content shown on secondary display");
                call.resolve(result);
                
                Log.d(TAG, "Showing content on secondary display");
            });
        } catch (Exception e) {
            Log.e(TAG, "Error showing on secondary display", e);
            call.reject("Failed to show on secondary display: " + e.getMessage());
        }
    }

    public void hideSecondaryDisplay(PluginCall call) {
        try {
            activity.runOnUiThread(() -> {
                if (presentation != null) {
                    presentation.dismiss();
                    presentation = null;
                }
                
                JSObject result = new JSObject();
                result.put("success", true);
                result.put("message", "Secondary display hidden");
                call.resolve(result);
                
                Log.d(TAG, "Hidden secondary display");
            });
        } catch (Exception e) {
            Log.e(TAG, "Error hiding secondary display", e);
            call.reject("Failed to hide secondary display: " + e.getMessage());
        }
    }

    private static class CustomerDisplayPresentation extends Presentation {
        private String html;

        public CustomerDisplayPresentation(Context outerContext, Display display, String html) {
            super(outerContext, display);
            this.html = html;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Configure for dual-screen independent touch operation
            // Critical flags for dual-touch POS systems:
            // 1. FLAG_NOT_FOCUSABLE: Main screen keeps keyboard/navigation focus
            // 2. FLAG_NOT_TOUCH_MODAL: Touch events outside this window pass to main screen
            // 3. FLAG_WATCH_OUTSIDE_TOUCH: Monitor touches to ensure proper routing
            if (getWindow() != null) {
                getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | 
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | 
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                );
                
                // Ensure the window doesn't block touches to the main activity
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
                
                Log.d(TAG, "Customer display configured for independent dual-touch (POS mode) with touch monitoring");
            }

            LinearLayout layout = new LinearLayout(getContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setBackgroundColor(Color.WHITE);
            layout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            ));

            WebView webView = new WebView(getContext());
            webView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            ));
            
            WebSettings settings = webView.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setDomStorageEnabled(true);
            settings.setLoadWithOverviewMode(true);
            settings.setUseWideViewPort(true);
            
            webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
            
            layout.addView(webView);
            setContentView(layout);
            
            Log.d(TAG, "Customer display: touch-enabled, won't steal focus from employee screen");
        }
    }
}
