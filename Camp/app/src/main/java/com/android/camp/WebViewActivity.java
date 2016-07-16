package com.android.camp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class WebViewActivity extends AppCompatActivity {

    private ActionBar actionBar;    /*アクションバー宣言*/
    private ProgressBar progressBar;    /*プログレスバー宣言*/
    private WebView webView;        /*WEBビュー表示宣言*/

    @SuppressLint("SetJavaScriptEnabled")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view); /*WEB表示用XML参照*/

        /*Toolbarをアクションバーとして使用*/
        setSupportActionBar((Toolbar)findViewById(R.id.webToolbar));
        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");

        /*プログレスバー設定*/
        progressBar = (ProgressBar)findViewById(R.id.webProgress);
        if(progressBar!=null) {
            progressBar.setVisibility(View.INVISIBLE);  /*プログレスバー非表示に設定*/
        }

        /*WEBビューの設定*/
        webView = (WebView) findViewById(R.id.webView);
        /*WEBビューの処理*/
        webView.setWebChromeClient(new WebChromeClient() {

            /*進捗更新*/
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);   /*プログレスバー進捗更新*/
            }

            /*タイトル取得*/
            @Override
            public void onReceivedTitle(WebView view, String title) {
                actionBar.setTitle(title);  /*アクションバーへページタイトルセット*/
            }
        });

        /*WEBビューの処理*/
        webView.setWebViewClient(new WebViewClient() {
            /*ページスタート*/
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                /*プログレスバー初期設定*/
                progressBar.setProgress(0);
                progressBar.setVisibility(View.VISIBLE);    /*プログレスバー表示に設定*/

                /*アクションバー初期設定*/
                actionBar.setTitle("");
                actionBar.setSubtitle(url); /*サブタイトルにURL表示*/
            }

            /*ページ終了*/
            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.INVISIBLE);  /*プログレスバー表示に設定*/

                /*アクションバー設定*/
                actionBar.setTitle(view.getTitle());    /*WEBページタイトルをアクションバーにセット*/
                actionBar.setSubtitle(url);             /*サブタイトルにURL表示*/
            }

            /*URL読み込み処理*/
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);  /*表示するURL設定*/
                return true;
            }
        });

        webView.getSettings().setJavaScriptEnabled(true);   /*JavaScript有効*/

        /*選択されたリストのURL取得*/
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        webView.loadUrl(url);   /*ブラウザへURL設定*/
    }


    /*ハードウエアキー入力処理*/
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        /*Backキー入力かつWEBビューに履歴が存在する*/
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
                webView.goBack();   /*WEBビューの表示を一つ前に戻す*/
            return true;
        }

        return super.dispatchKeyEvent(event);
    }

    /*オプションメニュー入力処理*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: /*戻るボタン*/
                finish();   /*アクティビティ終了*/
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
