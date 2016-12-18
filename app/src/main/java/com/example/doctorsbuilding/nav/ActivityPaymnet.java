package com.example.doctorsbuilding.nav;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by hossein on 12/18/2016.
 */
public class ActivityPaymnet extends AppCompatActivity implements MyWebChromeClient.ProgressListener {
    private WebView mWebView;
    private String mUrl = "https://sep.shaparak.ir/Payment.aspx";
    private ProgressBar mProgressBar;
    private RelativeLayout mToolbar;
    private TextView txtUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_payment);
        mWebView = (WebView) findViewById(R.id.webView);
        txtUrl = (TextView) findViewById(R.id.payment_url);
        mToolbar = (RelativeLayout) findViewById(R.id.payment_toolbar);
        txtUrl.setText(mUrl);
        mProgressBar = (ProgressBar) findViewById(R.id.payment_pbar);
        mWebView.setWebChromeClient(new MyWebChromeClient(this));
        mWebView.setWebViewClient(new MyBrowser());
        mWebView.getSettings().setLoadsImagesAutomatically(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        //mWebView.loadUrl(mUrl);
        SendPostRequest task = new SendPostRequest();
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

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

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

    private class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            try {

                URL url = new URL("https://sep.shaparak.ir/Payment.aspx");

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("Amount", "10000");
                postDataParams.put("MID", "10723614");
                postDataParams.put("ResNum", "1");
                postDataParams.put("RedirectURL", "http://pezeshkiar.com/test.php");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in=new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line="";

//                    while((line = in.readLine()) != null) {
//
//                        sb.append(line);
//                        break;
//                    }
                    try {
                        int chr;
                        while ((chr = in.read()) != -1) {
                            sb.append((char) chr);
                        }
                    } finally {
                        in.close();
                    }

                    in.close();
                    return sb.toString();

                }
                else {
                    return new String("false : "+responseCode);
                }
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onPostExecute(String result) {
            final String mimeType = "text/html";
            final String encoding = "UTF-8";
            String html = result;
            mWebView.loadDataWithBaseURL("", html, mimeType, encoding, "");
        }
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }
}
