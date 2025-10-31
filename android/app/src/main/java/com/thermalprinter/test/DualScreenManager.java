package com.thermalprinter.test;

import android.app.Activity;
import android.app.Presentation;
import android.content.Context;
import android.graphics.Color;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
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

            // Disable touch input on the customer display window
            // This prevents the customer display from capturing touch events
            if (getWindow() != null) {
                getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                );
                Log.d(TAG, "Customer display set to non-touchable (view-only mode)");
            }

            LinearLayout layout = new LinearLayout(getContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setBackgroundColor(Color.WHITE);
            layout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            ));

            // Disable touch on the layout as well
            layout.setOnTouchListener((v, event) -> true);

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
            
            // Disable all touch interactions on the WebView
            webView.setOnTouchListener((v, event) -> true);
            webView.setFocusable(false);
            webView.setFocusableInTouchMode(false);
            
            webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
            
            layout.addView(webView);
            setContentView(layout);
            
            Log.d(TAG, "Customer display configured as view-only (no touch input)");
        }
    }
}
