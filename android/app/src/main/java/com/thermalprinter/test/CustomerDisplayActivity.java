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

    public static void setPendingContent(String html) {
        pendingHtmlContent = html;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "CustomerDisplayActivity created on display: " + getDisplay().getDisplayId());

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

        layout.addView(webView);
        setContentView(layout);

        Log.d(TAG, "Customer display activity ready with independent touch input");
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
}
