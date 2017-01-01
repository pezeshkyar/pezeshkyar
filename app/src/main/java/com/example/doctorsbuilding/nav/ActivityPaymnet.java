package com.example.doctorsbuilding.nav;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.doctorsbuilding.nav.Util.MessageBox;
import com.example.doctorsbuilding.nav.Util.Util;
import com.example.doctorsbuilding.nav.Web.WebService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by hossein on 12/18/2016.
 */
public class ActivityPaymnet extends AppCompatActivity implements MyWebChromeClient.ProgressListener {
    private WebView mWebView;
    private ProgressBar mProgressBar;
    private LinearLayout mToolbar;
    private int amount = -1;
    private int requestCode = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_payment);
        amount = getIntent().getIntExtra("amount", -1);
        requestCode = getIntent().getIntExtra("requestCode", -1);
        mWebView = (WebView) findViewById(R.id.webView);
        mToolbar = (LinearLayout) findViewById(R.id.payment_toolbar);
        mProgressBar = (ProgressBar) findViewById(R.id.payment_pbar);
        mWebView.setWebChromeClient(new MyWebChromeClient(this));
        mWebView.setWebViewClient(new MyBrowser());
        mWebView.getSettings().setLoadsImagesAutomatically(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        GetPaymentInfo task = new GetPaymentInfo();
        task.execute();
    }

    @Override
    public void onUpdateProgress(int progressValue) {
        mProgressBar.setProgress(progressValue);
        if (progressValue < 100 && mProgressBar.getVisibility() == ProgressBar.GONE) {
            mProgressBar.setVisibility(ProgressBar.VISIBLE);
            mToolbar.setVisibility(View.VISIBLE);
        }

        mProgressBar.setProgress(progressValue);
        if (progressValue == 100) {
            mProgressBar.setVisibility(ProgressBar.GONE);
            mToolbar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        setResult(requestCode);
        finish();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private class MyBrowser extends WebViewClient {

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    private class GetPaymentInfo extends AsyncTask<String, Void, Void> {
        PaymentInfo result = null;
        String msg = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected Void doInBackground(String... strings) {
            try {
                if (amount != -1)
                    result = WebService.getRequestNumber(G.UserInfo.getUserName(), G.UserInfo.getPassword(), amount);
            } catch (PException ex) {
                msg = ex.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (msg != null) {
                new MessageBox(ActivityPaymnet.this, msg).show();
            } else {
                if (result != null) {
                    G.resNum = result.getResNum();
                    String mUrl = result.getUrl();
                    Map<String, String> mapParams = new HashMap<String, String>();

                    mapParams.put(Util.getStringWS(R.string.pay_Amount), String.valueOf(amount));
                    mapParams.put(Util.getStringWS(R.string.pay_MID), String.valueOf(result.getMid()));
                    mapParams.put(Util.getStringWS(R.string.pay_ResNum), String.valueOf(result.getResNum()));
                    mapParams.put(Util.getStringWS(R.string.pay_RedirectURL), result.getRedirecturl());

                    Collection<Map.Entry<String, String>> postData = mapParams.entrySet();

                    StringBuilder sb = new StringBuilder();

                    sb.append("<html><head></head>");
                    sb.append("<body onload='form1.submit()'>");
                    sb.append(String.format("<form id='form1' action='%s' method='%s'>", mUrl, "post"));
                    for (Map.Entry<String, String> item : postData) {
                        sb.append(String.format("<input name='%s' type='hidden' value='%s' />", item.getKey(), item.getValue()));
                    }
                    sb.append("</form></body></html>");

                    mWebView.loadData(sb.toString(), "text/html", "UTF-8");
                }
            }
        }
    }
}
