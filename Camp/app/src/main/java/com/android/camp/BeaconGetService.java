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
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.nttdocomo.android.sdaiflib.BeaconData;
import com.nttdocomo.android.sdaiflib.BeaconReceiverBase;
import com.nttdocomo.android.sdaiflib.BeaconScanner;
import com.nttdocomo.android.sdaiflib.Define;

import java.util.Calendar;

public class BeaconGetService extends Service{

    private BeaconScanner mScanner;
    private BeaconReceiver mReceiver;

    Context ctx;
    NotificationManager manager;
    NotificationCompat.Builder THbuilder;

    private String log="";
    private String[] Cosiness={null,null,null}; /*di_index,temp,humid*/
    private int[] color = {0,0,0};  /*赤,緑,青*/
    private String comment =null;   /*コメント*/
    private double di;            /*discomfort index(double)*/
    private String di_index="";   /*discomfort index(string)*/
    private int icon_id=2;      /*icon id*/

    private static int linkingID = 0;   /*LinkingボードID*/
    private static boolean gStarted = false;    /*Serviceの状態を取得*/

    Intent actionIntent;    /*ブロードキャスト用*/

    /*サービスの状態*/
    public static boolean isStarted() {
        return gStarted;
    }

    /*Linkingボードのビーコン情報取得クラス*/
    public final class BeaconReceiver extends BeaconReceiverBase {
        @Override
        protected void onReceiveScanResult(BeaconData beaconData) {

            BeaconGetService.this.BeaconSetID(beaconData);  /*LinkingボードIDの取得*/
            /*Log.d("TEST_BeaconGetService", "linkingID=" + linkingID);*/
            if(linkingID != 0) {    /*IDが割り当てられていれば*/
                if (beaconData.getExtraId() == linkingID) { /*設定されているIDであれば*/
                    BeaconGetService.this.onBeaconArrived(beaconData);  /*温湿度から危険度の取得*/
                }else { /*それ以外危険区域判定*/
                    /*デモ用フィルタ*/
                    if(beaconData.getExtraId() == 32849) {
                        BeaconGetService.this.setNotification(beaconData);  /*危険区域の距離取得*/
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

    /*Binderの生成*/
    private final IBinder mBinder = new MyServiceLocalBinder();

    /*サービスに接続するためのBinder*/
    public class MyServiceLocalBinder extends Binder {
        /*サービスの取得*/
        BeaconGetService getService() {
            return BeaconGetService.this;
        }
    }

    @Override
    public void onCreate() {
        /*Log.d("CAMP_BeaconGetService","onCreate");*/
        super.onCreate();
        /*Linkingボードの初期化*/
        mScanner = new BeaconScanner(this);
        mReceiver = new BeaconReceiver();   /*Linkingボードのブロードキャストレシーバ*/
        ctx = getApplicationContext();
        manager = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);  /*ノーティフィケーションマネージャの設定*/
        THbuilder = new NotificationCompat.Builder(getApplicationContext());    /*危険度表示用ノーティフィケーション*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*Log.d("CAMP_BeaconGetService","onDestroy");*/
        Toast.makeText(getApplicationContext(), "スキャン停止", Toast.LENGTH_SHORT).show();
        linkingID = 0;  /*IDの初期化*/
        gStarted = false;   /*サービスを停止状態にセット*/
        unregisterReceiver(mReceiver);  /*Linkingボードのブロードキャストレシーバ停止*/

        manager.cancel(2);  /*危険区域表示用ノーティフィケーション非表示*/

        /*それぞれのパラメータの初期化*/
        Cosiness[0] = "";
        Cosiness[1] = "";
        Cosiness[2] = "";
        actionIntent.putExtra("index1",Cosiness[0]);
        actionIntent.putExtra("index2",Cosiness[1]);
        actionIntent.putExtra("index3",Cosiness[2]);
        color[0] = 0;
        color[1] = 0;
        color[2] = 0;
        icon_id=2;
        comment="";
        actionIntent.putExtra("colorR",color[0]);
        actionIntent.putExtra("colorG",color[1]);
        actionIntent.putExtra("colorB",color[2]);
        actionIntent.putExtra("comment",comment);
        actionIntent.putExtra("icon_id",icon_id);
        actionIntent.putExtra("destroy",1);
        actionIntent.setAction("action");
        getBaseContext().sendBroadcast(actionIntent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {
        /*Log.d("CAMP_BeaconGetService","onStartCommand");*/
        gStarted = true;    /*サービスを起動状態にセット*/
        super.onStartCommand(intent, flags, startid);

        /*ブロードキャスト用インテント*/
        actionIntent = new Intent("action");

        Intent in =new Intent(this, StopService.class);

        /*ノーティフィケーションからサービス停止用ペンディングインテント*/
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, in, 0);


        /*Linkingインテントフィルタ指定*/
        IntentFilter filter = new IntentFilter();
        filter.addAction(Define.filterBeaconScanResult);
        filter.addAction(Define.filterBeaconScanState);
        registerReceiver(mReceiver, filter);

        mScanner.startScan(new int[]{
                0,/*デバイスID*/
                1, 2, 3, 4, 5   /*すべて取得*/
        });

        /*危険度通知用ノーティフィケーションのセット*/
        THbuilder.setSmallIcon(R.drawable.camp);    /*ミニアイコンの設定*/
        THbuilder.setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.campicon));   /*アイコンの設定*/
        THbuilder.addAction(R.drawable.stop, "通知の停止", pendingIntent);   /*サービス停止ボタンのセット*/
        THbuilder.setTicker("ビーコンからの通知が届きました。");    /*ノーティフィケーションの通知内容*/
        THbuilder.setContentTitle("キャンプ役立ちアプリ");    /*ノーティフィケーションタイトル*/

        /*タップ時に消える*/
        THbuilder.setAutoCancel(true);
        /*タップ時メイン画面の起動*/
        Intent MainIntent = new Intent(this, MainActivity.class);
        THbuilder.setContentIntent(PendingIntent.getActivity(this,0,MainIntent,0));
        startForeground(1,THbuilder.build());   /*サービスを落とさないようにノーティフィケーションを常駐させる*/

        return START_STICKY;
    }

    /**************************************************************/
    /*タイトル :LinkingボードのID取得                             */
    /*引数     :無し                                              */
    /*戻り値   :無し                                              */
    /**************************************************************/
    private void BeaconSetID(BeaconData beaconData)
    {
        int ID = beaconData.getExtraId();   /*IDの設定*/

        actionIntent.putExtra("index",ID);  /*ID情報をセット*/
        actionIntent.setAction("GETID");  /*送るブロードキャストレシーバを設定*/
        getBaseContext().sendBroadcast(actionIntent);  /*アクティビティへ送信*/
    }

    /*温度,湿度を宣言*/
    private Float temp  = null;
    private Float humid = null;
    private float temp_s=0;
    private float humid_s=0;

    private void onBeaconArrived(BeaconData beaconData) {

        comment=""; /*コメント*/
        temp = beaconData.getTemperature(); /*温度取得*/
        humid = beaconData.getHumidity();   /*湿度取得*/

        if(temp != null){
            /*温度の取得*/
            temp_s = temp;
            Cosiness[1] = String.format("%.2f",temp_s);
            /*Log.d("temparature", String.format("temp: %f", temp));*/
        }
        if(humid != null) {
            /*湿度の取得*/
            humid_s = humid;
            Cosiness[2] = String.format("%.2f",humid_s);
            /*Log.d("humidity", String.format("humid: %f", humid));*/
        }
        if(temp_s == 0 || humid_s == 0){
            /*両方取得できていないとき*/
            log = String.format("測定中");
            /*Log.d("inf",log);*/
        }else {
            /*温度,湿度両方取得*/
            di = (0.81 * temp_s) + (0.01 * humid_s) * ((0.99 * temp_s) - 14.3) + 46.3;  /*不快度指数の計算*/
            di_index = di_to_diindex(di);   /*不快度指数より危険度の取得*/
            /*Log.d("不快度", String.format("[%d]不快度:%.2f,%s", beaconData.getExtraId(), di, di_index));*/

            log = String.format("%s \n 気温:%.2f,湿度:%.2f", di_index, temp_s, humid_s);    /*ノーティフィケーション用の危険度表示*/
            Cosiness[0] = di_index; /*危険度*/
            Cosiness[1] = String.format("%.2f", temp_s);    /*温度*/
            Cosiness[2] = String.format("%.2f", humid_s);   /*湿度*/
        }
        THbuilder.setContentText(log);  /*ノーティフィケーションへセット*/

        /*ブロードキャストレシーバを使用してアクティビティへ送信*/
        actionIntent.putExtra("index1",Cosiness[0]);
        actionIntent.putExtra("index2",Cosiness[1]);
        actionIntent.putExtra("index3",Cosiness[2]);
        actionIntent.putExtra("colorR",color[0]);
        actionIntent.putExtra("colorG",color[1]);
        actionIntent.putExtra("colorB",color[2]);
        actionIntent.putExtra("comment",comment);
        actionIntent.putExtra("icon_id",icon_id);
        actionIntent.putExtra("destroy",0);
        actionIntent.setAction("action");
        getBaseContext().sendBroadcast(actionIntent);   /*情報の送信*/

        manager.notify(1, THbuilder.build());   /*ノーティフィケーションの更新*/
    }

    /**************************************************************/
    /*タイトル :不快度から危険度へ変換                            */
    /*引数     :double:不快度指数                                 */
    /*戻り値   :String:危険度                                     */
    /**************************************************************/
    private String di_to_diindex(double di)
    {
        String moji =null;

        if(di <60)
        {
            moji ="寒い";
            color[0] = 0;
            color[1] = 0;
           color[2] = 255;
            icon_id=0;
            comment="一枚羽織ろう";
        }
        else if(di<65)
        {
            moji ="肌寒い";
            color[0] = 0;
            color[1] = 255;
            color[2] = 255;
            icon_id=1;
            comment="一枚羽織ろう";
        }
        else if(di<70)
        {
            moji ="快適";
           color[0] = 0;
            color[1] = 255;
            color[2] = 0;
            icon_id=2;
            comment="快適です";
        }
        else if(di<75)
        {
            moji ="ちょっと暑い";
            color[0] = 255;
            color[1] = 255;
            color[2] = 0;
            icon_id=3;
            comment="ちょっと暑いかも";
        }
        else if(di<80)
        {
            moji ="暑く感じる";
            color[0] = 255;
            color[1] = 64;
            color[2] = 0;
            icon_id=4;
            comment= "こまめに\n水分補給しようね";
        }
        else
        {
            moji ="危険な暑さ";
            color[0] = 255;
            color[1] = 0;
            color[2] = 0;
            icon_id=5;
            comment="涼しい日陰で休憩をとろう";
        }

        return moji;
    }

    int notificnt = 0;  /*通知時間用カウント*/
    int notificomp = 4; /*過去の距離状態*/
    boolean notififlg = false;  /*通知フラグ*/
    NotificationCompat.Builder builder; /*危険区域通知用ノーティフィケーション*/

    private void setNotification(BeaconData data)
    {
        builder = new NotificationCompat.Builder(getApplicationContext());  /*ノーティフィケーションのセット*/
        String time = timeLogFormat(System.currentTimeMillis());    /*取得時間を表示*/
        builder.setSmallIcon(R.drawable.danger);    /*ミニアイコンのセット*/

        int val = data.getDistance();   /*危険区域の距離取得*/
        builder.setPriority(NotificationCompat.PRIORITY_MAX);   /*ヘッドアップ通知*/

        long[] vibrate_ptn = {0, 1200, 300, 200}; /*独自バイブレーションパターン*/
        switch (val)
        {
            case 1:
                /*アイコンの設定*/
                builder.setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.warning));
                /*アイコンの背景色*/
                builder.setColor(Color.argb(0,255,0,0));
                builder.setContentText("警告 これ以上近づく場合は命を保証しません");
                /*通知時のバイブレーション,音の設定*/
                builder.setLights(0xff0000,1000,500);
                builder.setVibrate(vibrate_ptn);
                builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
                /*10秒おきに通知*/
                notificnt = 0;
                notififlg = true;
                break;
            case 2:
                /*アイコンの設定*/
                builder.setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.warning2));
                /*アイコンの背景色*/
                builder.setColor(Color.argb(0,255,128,0));
                builder.setContentText("危険 それ以上近づかないでください");
                /*通知時のバイブレーション,音の設定*/
                builder.setLights(0xff6d00,1000,500);
                vibrate_ptn[1] = 500; // 独自バイブレーションパターン
                builder.setVibrate(vibrate_ptn);
                builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
                setTime(val,2);/*20秒おきに通知*/

                break;

            case 3:
                /*アイコンの設定*/
                builder.setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.warning3));
                /*アイコンの背景色*/
                builder.setColor(Color.argb(0,0,200,0));
                builder.setContentText("注意 その先は危険です");
                /*通知時のバイブレーション,音の設定*/
                builder.setLights(0x00ff38,1000,500);
                vibrate_ptn[1] = 200; // 独自バイブレーションパターン
                builder.setVibrate(vibrate_ptn);
                builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
                setTime(val,3); /*30秒おきに通知*/
                break;
        }
        /*Notificationを開いたときに表示するもの*/
        builder.setContentTitle("キャンプ役立ちアプリ");
        builder.setSubText(String.format("%s Id[%d]までの距離[%d]です。", time, data.getExtraId(), val));
        /*通知するタイミング*/
        builder.setWhen(System.currentTimeMillis());

        /*受信時のステータスバーに表示されるテキスト*/
        /*Android5.0から表示しない*/
        builder.setTicker("DANGER");

        new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        builder.setPriority(Notification.PRIORITY_DEFAULT); /*表示を通常状態へ戻す*/
                        builder.setSound(null);
                        builder.setVibrate(null);
                        manager.notify(2, builder.build()); /*再表示*/
                    }
        }, 2500);

        /*タップ時に消える*/
        builder.setAutoCancel(true);
        Intent intent = new Intent(this, MainActivity.class);
        builder.setContentIntent(PendingIntent.getActivity(this,0,intent,0));

        /*危険区域通知*/
        if (notififlg)
        {
            manager.notify(2, builder.build());
            notififlg = false;
        }

        notificomp = val;
    }

    /**************************************************************/
    /*タイトル :通知時間の設定                                    */
    /*引数     :val:危険区域の距離                                */
    /*          time:表示までのカウント時間                       */
    /*戻り値   :無し                                              */
    /**************************************************************/
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
        /*Log.d("CAMP_BeaconGetService","onBind");*/
        Toast.makeText(getApplicationContext(), "計測開始", Toast.LENGTH_SHORT).show();
        linkingID = intent.getIntExtra("SETID",0);  /*選択されたLinkingボードのID取得*/
        /*Log.d("CAMP_BeaconGetService",String.format("%s",linkingID));*/

        /*表示の初期化*/
        Cosiness[0] =null;
        Cosiness[1] = null;
        Cosiness[2] = null;
        comment=null;
        actionIntent.putExtra("index1",Cosiness[0]);
        actionIntent.putExtra("index2",Cosiness[1]);
        actionIntent.putExtra("index3",Cosiness[2]);
        actionIntent.putExtra("comment",comment);
        actionIntent.setAction("action");
        getBaseContext().sendBroadcast(actionIntent);   /*ブロードキャストレシーバの送信*/

        return mBinder;
    }

    @Override
    public void onRebind(Intent intent)
    {
        /*Log.d("CAMP_BeaconGetService","onRebind");*/
    }

    @Override
    public boolean onUnbind(Intent intent){
        /*Log.d("CAMP_BeaconGetService","onUnbind");*/
        return true;
    }
}
