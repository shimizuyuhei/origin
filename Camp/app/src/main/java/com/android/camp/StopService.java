package com.android.camp;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;


public class StopService extends Service {

    public void onCreate() {
        super.onCreate();
        if(MainActivity.NotificationStopFlag){
            Intent in = new Intent(StopService.this, BeaconGetService.class);
            stopService(in);
        }
        stopSelf();
    }
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("CAMP_StopService","onbind");

        return null;
    }
}
