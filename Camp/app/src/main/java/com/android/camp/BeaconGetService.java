package com.android.camp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.nttdocomo.android.sdaiflib.BeaconData;
import com.nttdocomo.android.sdaiflib.BeaconReceiverBase;
import com.nttdocomo.android.sdaiflib.BeaconScanner;
import com.nttdocomo.android.sdaiflib.Define;

import java.util.Calendar;

/**
 * Created by USER on 2016/06/14.
 */
public class BeaconGetService extends Service{

    private BeaconScanner mScanner;
    private BeaconReceiver mReceiver;

    Context ctx;
    NotificationManager manager;
    NotificationCompat.Builder THbuilder;

    private String log="";
    private String[] Cosiness={null,null,null}; //di_index,temp,humid
    private int[] color = {0,0,0,0};
    private String comment =null;
    private double di;            //discomfort index(double)
    private String di_index="";   //discomfort index(string)
    private int id=2;             //icon id

    private static int linkingID = 0;
    private static boolean gStarted = false;

    Intent actionIntent;

    //サービスの状態
    public static boolean isStarted() {
        return gStarted;
    }

    public final class BeaconReceiver extends BeaconReceiverBase {
        @Override
        protected void onReceiveScanResult(BeaconData beaconData) {

            BeaconGetService.this.BeaconSetID(beaconData);
            //Log.d("TEST_BeaconGetService", "linkingID=" + linkingID);
            if(linkingID != 0) {
                if (beaconData.getExtraId() == linkingID) {
                    BeaconGetService.this.onBeaconArrived(beaconData);
                }else {
                    //デモ用フィルター
                    if(beaconData.getExtraId() == 32849) {
                        BeaconGetService.this.setNotification(beaconData);
                    }
                }
            }
        }

        @Override
        protected void onReceiveScanState(int scanState, int detail) {
            String state = "";
            if (scanState == 0) {
                if (detail == 0) {
                    state = "スキャン実行中";
                } else {
                    if(detail==3) {
                        state = "エラーが発生しました：\n" + "端末のブルートゥースをONにしてください";
                    }else if(detail==4){
                        state = "エラーが発生しました：\n" + "Linkingアプリでビーコンの受信を行ってください";
                    }else{
                        state="エラーが発生しました:\n"+detail;
                    }
                }
            } else {
                if(detail == 0) {
                    state = "スキャン要求に失敗しました : " + detail;
                } else if (detail == 1) {
                    state = "スキャン要求がタイムアウトしました";
                }
            }
            Toast.makeText(getApplicationContext(), state, Toast.LENGTH_SHORT).show();
        }
    }

    //Binderの生成
    private final IBinder mBinder = new MyServiceLocalBinder();

    //サービスに接続するためのBinder
    public class MyServiceLocalBinder extends Binder {
        //サービスの取得
        BeaconGetService getService() {
            return BeaconGetService.this;
        }
    }

    @Override
    public void onCreate() {
        Log.d("TEST_BeaconGetService","onCreate");
        super.onCreate();
        mScanner = new BeaconScanner(this);
        mReceiver = new BeaconReceiver();
        ctx = getApplicationContext();
        manager = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        THbuilder = new NotificationCompat.Builder(getApplicationContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("TEST_BeaconGetService","onDestroy");
        //linkingID = 0;
        gStarted = false;
        unregisterReceiver(mReceiver);

        Cosiness[0] = "";
        Cosiness[1] = "";
        Cosiness[2] = "";
        actionIntent.putExtra("index1",Cosiness[0]);
        actionIntent.putExtra("index2",Cosiness[1]);
        actionIntent.putExtra("index3",Cosiness[2]);
        color[0] = 0;
        color[1] = 0;
        color[2] = 0;
        color[3] = 0;
        comment=null;
        actionIntent.putExtra("colorA",color[0]);
        actionIntent.putExtra("colorR",color[1]);
        actionIntent.putExtra("colorG",color[2]);
        actionIntent.putExtra("colorB",color[3]);
        actionIntent.putExtra("comment",comment);
        actionIntent.setAction("action");
        getBaseContext().sendBroadcast(actionIntent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {
        Log.d("TEST_BeaconGetService","onStartCommand");
        gStarted = true;
        super.onStartCommand(intent, flags, startid);

        //ブロードキャスト用インテント
        actionIntent = new Intent("action");

        //Linkingインテントフィルタ指定
        IntentFilter filter = new IntentFilter();
        filter.addAction(Define.filterBeaconScanResult);
        filter.addAction(Define.filterBeaconScanState);
        registerReceiver(mReceiver, filter);

        mScanner.startScan(new int[]{
                0,// デバイスID
                1, 2, 3, 4, 5// 適当
        });

        THbuilder.setSmallIcon(R.drawable.camp);
        THbuilder.setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.campicon));
        THbuilder.setTicker("ビーコンからの通知が届きました。");
        THbuilder.setContentTitle("キャンプ役立ちアプリ");

        //タップ時に消える
        THbuilder.setAutoCancel(true);
        Intent MainIntent = new Intent(this, MainActivity.class);
        THbuilder.setContentIntent(PendingIntent.getActivity(this,0,MainIntent,0));
        startForeground(1,THbuilder.build());

        return START_STICKY;
    }

    private void BeaconSetID(BeaconData beaconData)
    {

        int ID = beaconData.getExtraId();

        actionIntent.putExtra("index",ID);
        actionIntent.setAction("GETID");
        getBaseContext().sendBroadcast(actionIntent);
    }

    private Float temp  = null;
    private Float humid = null;
    private float temp_s=0;
    private float humid_s=0;
    private void onBeaconArrived(BeaconData beaconData) {

//        long[] vibrate_ptn = {0, 100, 300, 1000};
        //      THbuilder.setVibrate(vibrate_ptn);

        //時間
        //        String time = timeLogFormat(System.currentTimeMillis());
        comment="";
        temp = beaconData.getTemperature();//取得
        humid = beaconData.getHumidity();//取得

        if(temp != null){
            temp_s = temp;
            Cosiness[1] = String.format("%.2f",temp_s);
            Log.d("temparature", String.format("temp: %f", temp));
        }
        if(humid != null) {
            humid_s = humid;
            Cosiness[2] = String.format("%.2f",humid_s);
            Log.d("humidity", String.format("humid: %f", humid));
        }
        if(temp_s == 0 || humid_s == 0){
            log = String.format("測定中");
            Log.d("inf",log);
        }else {
            di = (0.81 * temp_s) + (0.01 * humid_s) * ((0.99 * temp_s) - 14.3) + 46.3;
            di_index = di_to_diindex(di);
            Log.d("不快度", String.format("[%d]不快度:%.2f,%s", beaconData.getExtraId(), di, di_index));

            log = String.format("%s \n 気温:%.2f,湿度:%.2f", di_index, temp_s, humid_s);
            Cosiness[0] = di_index;
            Cosiness[1] = String.format("%.2f", temp_s);
            Cosiness[2] = String.format("%.2f", humid_s);
        }
        THbuilder.setContentText(log);

        actionIntent.putExtra("index1",Cosiness[0]);
        actionIntent.putExtra("index2",Cosiness[1]);
        actionIntent.putExtra("index3",Cosiness[2]);
        actionIntent.putExtra("colorA",color[0]);
        actionIntent.putExtra("colorR",color[1]);
        actionIntent.putExtra("colorG",color[2]);
        actionIntent.putExtra("colorB",color[3]);
        actionIntent.putExtra("comment",comment);
        actionIntent.putExtra("id",id);
        actionIntent.setAction("action");
        getBaseContext().sendBroadcast(actionIntent);

        manager.notify(1, THbuilder.build());
    }

    //static用意
    private   String  di_to_diindex(double di)
    {
        String moji =null;
        color[0] = 200;

        if(di <60)
        {
            moji ="寒い";
            color[1] = 100;
            color[2] = 100;
           color[3] = 255;
            id=0;
            comment="一枚羽織ろう";
        }
        else if(di<65)
        {
            moji ="肌寒い";
            color[1] = 126;
            color[2] = 128;
            color[3] = 255;
            id=1;
            comment="一枚羽織ろう";
        }
        else if(di<70)
        {
            moji ="快適";
           color[1] = 255;
            color[2] = 255;
            color[3] = 255;
            id=2;
            comment="快適です";
        }
        else if(di<75)
        {
            moji ="ちょっと暑い";
            color[1] = 255;
            color[2] = 255;
            color[3] = 128;
            id=3;
            comment="ちょっと暑いかも";
        }
        else if(di<80)
        {
            moji ="暑く感じる";
            color[1] = 255;
            color[2] = 64;
            color[3] = 64;
            id=4;
            comment=
                    "こまめに\n水分補給しようね";
        }
        else
        {
            moji ="危険な暑さ";
          color[1] = 255;
            color[2] = 0;
            color[3] = 0;
            id=5;
            comment="涼しい日陰で休憩をとろう";
        }

        return moji;
    }

    int notificnt = 0;
    int notificomp = 4;
    boolean notififlg = false;

    private void setNotification(BeaconData data)
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());

        String time = timeLogFormat(System.currentTimeMillis());
        //アイコン
        builder.setSmallIcon(R.drawable.danger);
        int val = data.getDistance();
        long[] vibrate_ptn = {0, 1200, 300, 200}; // 独自バイブレーションパターン
        switch (val)
        {
            case 1:
                builder.setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.warning));
                //アイコンの背景色
                builder.setColor(Color.argb(0,255,0,0));
                builder.setContentText("警告 これ以上近づく場合は命を保証しません");
                builder.setLights(0xff0000,1000,500);
                builder.setVibrate(vibrate_ptn);
                notificnt = 0;
                notififlg = true;
                break;
            case 2:
                builder.setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.warning2));
                //アイコンの背景色
                builder.setColor(Color.argb(0,255,128,0));
                builder.setLights(0xff6d00,1000,500);
                vibrate_ptn[1] = 500; // 独自バイブレーションパターン
                builder.setVibrate(vibrate_ptn);
                builder.setContentText("危険 それ以上近づかないでください");
                setTime(val,2);

                break;

            case 3:
                builder.setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.warning3));
                //アイコンの背景色
                builder.setColor(Color.argb(0,0,200,0));
                builder.setLights(0x00ff38,1000,500);
                vibrate_ptn[1] = 200; // 独自バイブレーションパターン
                builder.setVibrate(vibrate_ptn);
                builder.setContentText("注意 その先は危険です");
                setTime(val,3);

                break;
        }
        //Notificationを開いたときに表示するもの
        builder.setContentTitle("キャンプ役立ちアプリ");
        builder.setSubText(String.format("%s Id[%d]までの距離[%d]です。", time, data.getExtraId(), val));
        //builder.setSubText("サブ情報");
        //builder.setContentInfo("右の表示");

        //通知するタイミング
        builder.setWhen(System.currentTimeMillis());

        //受信時のステータスバーに表示されるテキスト
        //Android5.0から表示しない
        builder.setTicker("DANGER");


        //タップ時に消える
        builder.setAutoCancel(true);
        Intent intent = new Intent(this, MainActivity.class);
        builder.setContentIntent(PendingIntent.getActivity(this,0,intent,0));

        if (notififlg)
        {
            manager.notify(2, builder.build());
            notififlg = false;
        }

        notificomp = val;
    }

    private void setTime(int val, int time)
    {
        if(notificomp == val) {
            if(0 < notificnt) {
                notificnt -= 1;
            }else{
                notificnt = time-1;
                notififlg = true;
            }
        }else{
            notificnt = time-1;
            notififlg = true;
        }
    }

    /**
     * ミリ秒表示で与えられた時間を見やすくフォーマットする
     * @param data System.currentTimeMillis()などで取得された値
     * @return 引数で与えられた時間を[MM-DD hh:mm:ss]に変換した文字列
     */
    public static String timeLogFormat(long data) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(data);
        String time = String.format("%02d-%02d %02d:%02d:%02d",
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND));
        return time;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("TEST_BeaconGetService","onBind");
        Toast.makeText(getApplicationContext(), "計測開始", Toast.LENGTH_SHORT).show();
        linkingID = intent.getIntExtra("SETID",0);
        Log.d("TEST_BeaconGetService",String.format("%s",linkingID));

        Cosiness[0] =null;
        Cosiness[1] = null;
        Cosiness[2] = null;
        actionIntent.putExtra("index1",Cosiness[0]);
        actionIntent.putExtra("index2",Cosiness[1]);
        actionIntent.putExtra("index3",Cosiness[2]);
        actionIntent.setAction("action");
        getBaseContext().sendBroadcast(actionIntent);

        return mBinder;
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
