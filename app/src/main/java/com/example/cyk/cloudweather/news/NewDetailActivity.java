package com.example.cyk.cloudweather.news;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.cyk.cloudweather.R;


public class NewDetailActivity extends Activity {

    private WebView wbNews;
    private String loadUrl, title;
    private WebViewClient webViewClient;
    private TextView tv_bar_title;
    private ImageView iv_back;
    private ProgressBar pb_load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_detail);
        loadUrl = getIntent().getStringExtra("url");
        title = getIntent().getStringExtra("title");
        initView();
        setWebViewClient();
    }

    private void setWebViewClient() {
        webViewClient = new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                pb_load.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                pb_load.setVisibility(View.GONE);
            }
        };
        wbNews.setWebViewClient(webViewClient);
    }

    private void initView() {
        wbNews = (WebView) findViewById(R.id.wb_news);
        wbNews.getSettings().setJavaScriptEnabled(true);
        wbNews.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        wbNews.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        wbNews.canGoBack();
        wbNews.canGoForward();
        wbNews.loadUrl(loadUrl);
        tv_bar_title = (TextView) findViewById(R.id.tv_bar_title);
        tv_bar_title.setText(title);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        pb_load = (ProgressBar) findViewById(R.id.pb_load);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wbNews.destroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK && wbNews.canGoBack()) {
            wbNews.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
