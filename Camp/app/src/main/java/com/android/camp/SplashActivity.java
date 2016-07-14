package com.android.camp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;


public class SplashActivity extends Activity {
    private Handler hdl;
    private int time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // タイトルを非表示にします。
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        time = 1500;

        // splash.xmlをViewに指定します。
        setContentView(R.layout.splash);
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

    //onStartの後
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("TEST_SplashActivity","onResume");
        hdl = new Handler();
        // 1500ms遅延させてsplashHandlerを実行します。
        hdl.postDelayed(new splashHandler(), time);
    }

    //アクティビティ実行の後
    protected void onPause() {
        super.onPause();
        Log.d("TEST_SplashActivity","onPause");
        hdl.removeCallbacksAndMessages(null);
        time = 0;
    }

    //戻るボタンクリック
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("TEST_SplashActivity","onKeyDown");
        if(keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU) {
            // 戻るボタンの処理
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
