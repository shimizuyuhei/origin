package com.android.camp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;

/**
 * Created by shimizu.yuhei on 2016/06/30.
 */
public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // タイトルを非表示にします。
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // splash.xmlをViewに指定します。
        setContentView(R.layout.splash);
        Handler hdl = new Handler();
        // 1500ms遅延させてsplashHandlerを実行します。
        hdl.postDelayed(new splashHandler(), 1500);
    }

    class splashHandler implements Runnable {
        public void run() {
            // スプラッシュ完了後に実行するActivityを指定します。
            Intent intent = new Intent(getApplication(), MainActivity.class);
            startActivity(intent);
            // SplashActivityを終了させます。
            SplashActivity.this.finish();
        }
    }

    //戻るボタンクリック
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("TEST_SettingsActivity","onKeyDown");
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            // 戻るボタンの処理
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
