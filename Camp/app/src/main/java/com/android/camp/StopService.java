package com.android.camp;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
/**
 * Created by USER on 2016/07/12.
 */
public class StopService extends Service {
    public void onCreate() {
        Log.d("AAAAVFDECVGEADFAEF","onCreate");
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

    @Override
    public void onRebind(Intent intent) {
        Log.d("TEST_BeaconGetService","onRebind");
    }

    @Override
    public boolean onUnbind(Intent intent){
        Log.d("TEST_BeaconGetService","onUnbind");
        return true;
    }


}
