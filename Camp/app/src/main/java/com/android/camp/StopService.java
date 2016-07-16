package com.android.camp;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;


public class StopService extends Service {

    /*ノーティフィケーションから取得終了があった時に呼び出し*/
    public void onCreate() {
        super.onCreate();
        /*Linkingボード選択画面が開いていない時*/
        if(MainActivity.NotificationStopFlag){
            Intent in = new Intent(StopService.this, BeaconGetService.class);   /*Linkingボード取得用サービス宣言*/
            stopService(in);    //Linkingボード取得用サービス停止
        }
        stopSelf();
    }
    @Override
    public IBinder onBind(Intent intent) {
        /*Log.d("CAMP_StopService","onbind");*/

        return null;
    }
}
