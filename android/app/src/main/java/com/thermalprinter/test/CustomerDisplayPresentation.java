package com.thermalprinter.test;

import android.app.Presentation;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;

public class CustomerDisplayPresentation extends Presentation {
    private static final String TAG = "CustomerDisplayPresentation";
    private String htmlContent;
    
    public CustomerDisplayPresentation(Context context, Display display, String htmlContent) {
        super(context, display);
        this.htmlContent = htmlContent;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // CRITICAL: Set window flags to prevent stealing touch from MainActivity
        // Presentation class already sets the correct window type for secondary displays
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        
        Display display = getDisplay();
        Log.d(TAG, "CustomerDisplayPresentation created on display:");
        Log.d(TAG, "  Display ID: " + display.getDisplayId());
        Log.d(TAG, "  Display Name: " + display.getName());
        Log.d(TAG, "  Window type: Presentation (non-Activity window)");
        Log.d(TAG, "  MainActivity should remain the RESUMED activity with touch control");
        
        // Create layout
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(Color.WHITE);
        layout.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        ));
        
        // Create WebView for customer-facing content
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
        
        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);
        
        // Ensure WebView is completely passive
        webView.setOnTouchListener((v, event) -> true);
        
        layout.addView(webView);
        setContentView(layout);
        
        Log.d(TAG, "Customer display Presentation ready (passive, MainActivity retains focus and touch)");
    }
    
    public void updateContent(String newHtml) {
        if (isShowing()) {
            WebView webView = (WebView) ((LinearLayout) findViewById(android.R.id.content)).getChildAt(0);
            if (webView != null) {
                webView.loadDataWithBaseURL(null, newHtml, "text/html", "UTF-8", null);
                Log.d(TAG, "Customer display content updated");
            }
        }
    }
}
