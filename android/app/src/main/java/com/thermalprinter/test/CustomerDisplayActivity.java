package com.thermalprinter.test;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;

public class CustomerDisplayActivity extends Activity {
    private static final String TAG = "CustomerDisplayActivity";
    private static String pendingHtmlContent = "";
    private static CustomerDisplayActivity instance;

    public static void setPendingContent(String html) {
        pendingHtmlContent = html;
    }
    
    public static CustomerDisplayActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        
        // CRITICAL: Use FLAG_NOT_TOUCH_MODAL to allow touch events to pass through properly
        // This prevents the customer display from blocking employee screen touch input
        // NOT_TOUCH_MODAL allows the main screen to receive touch while secondary is open
        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        
        android.view.Display display = getDisplay();
        Log.d(TAG, "CustomerDisplayActivity created on display:");
        Log.d(TAG, "  Display ID: " + display.getDisplayId());
        Log.d(TAG, "  Display Name: " + display.getName());
        Log.d(TAG, "  Is DEFAULT_DISPLAY: " + (display.getDisplayId() == android.view.Display.DEFAULT_DISPLAY));
        Log.d(TAG, "  Window flags: NOT_FOCUSABLE + NOT_TOUCH_MODAL (allows employee touch)");

        // Get HTML content from intent or static storage
        String htmlContent = getIntent().getStringExtra("html_content");
        if (htmlContent == null || htmlContent.isEmpty()) {
            htmlContent = pendingHtmlContent;
        }

        // Create layout
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(Color.WHITE);
        layout.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        ));

        // Create WebView for customer-facing content
        WebView webView = new WebView(this);
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
        
        // Ensure WebView doesn't intercept touch events
        webView.setOnTouchListener((v, event) -> true);

        layout.addView(webView);
        setContentView(layout);

        Log.d(TAG, "Customer display ready (passive view-only, employee screen retains touch control)");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "CustomerDisplayActivity onResume - independent touch active");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "CustomerDisplayActivity onPause");
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (instance == this) {
            instance = null;
        }
        Log.d(TAG, "CustomerDisplayActivity destroyed");
    }
}
