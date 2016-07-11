package com.android.camp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Messenger;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;

public class MainActivity extends AppCompatActivity implements ServiceConnection,LocationListener,Runnable {

    private Intent SettingsIntent;
    private Receiver myreceiver;

    private LocationManager locationManager;
    String url;
    String pass = new String();
    JsonLoader jsonLoader;
    Thread thread;

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("TEST_MainActivity","onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton imagebutton1= (ImageButton)findViewById(R.id.image_button1);
        ImageButton imagebutton2= (ImageButton)findViewById(R.id.image_button2);
        ImageButton imagebutton3= (ImageButton)findViewById(R.id.image_button3);

        WindowManager wm = getWindowManager();

        //終了
         ImageButton image_button_choice =(ImageButton)findViewById(R.id.image_button_choice);
         LinearLayout l1=(LinearLayout)findViewById(R.id.weather_layout);


        setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));

        SettingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);

        //startService(BeaconGetIntent);

        myreceiver = new Receiver();
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction("action");
        registerReceiver(myreceiver, intentfilter);

        imagebutton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CookingListActivity.class);
                // 次画面のアクティビティ起動
                startActivity(intent);
            }

        });
        imagebutton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PreparationListActivity.class);
                // 次画面のアクティビティ起動
                startActivity(intent);
            }

        });
        imagebutton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DangerListActivity.class);
                // 次画面のアクティビティ起動
                startActivity(intent);
            }

        });

        image_button_choice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(SettingsIntent,RESULTCODE);
            }

        });

         //天気領域クリック処理
         LinearLayout linerlayoutView = (LinearLayout) findViewById(R.id.weather_layout);
         linerlayoutView.setClickable(true);
         linerlayoutView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 startGPS();
             }
         });

         pass = "6bc4bdb0435fb3599d879b987453b459";

         // LocationManager インスタンス生成
         locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

    }

    private static final long MinTime = 0;//30*60*1000; //30分
    private static final float MinDistance = 100;

    //GPS開始
    protected void startGPS() {
        Log.d("TEST_MainActivity", "startGPS");
        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);

        if (!gpsEnabled) {
            // GPSを設定するように促す
            enableLocationSettings();
        }

        if (locationManager != null) {
            onGPS();
        } else {

        }
    }

    protected void onGPS(){
        Log.d("TEST_MainActivity", "onGPS");
        // バックグラウンドから戻ってしまうと例外が発生する場合がある
        try {
            //GPSの開始
            // minTime = 1000msec, minDistance = 50m
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, MinTime, MinDistance, this);
        } catch (Exception e) {
            e.printStackTrace();

            Toast toast = Toast.makeText(this, "例外が発生、位置情報のPermissionを許可していますか？", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    //GPS停止
    private void stopGPS(){
        if (locationManager != null) {
            Log.d("TEST_MainActivity", "onStop");
            // update を止める
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.removeUpdates(this);
        } else {

        }
    }

    //GPS設定画面表示
    private void enableLocationSettings() {
        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(settingsIntent);
    }

    //onCreateの後
    @Override
    protected void onStart(){
        super.onStart();
        Log.d("TEST_MainActivity","onStart");
    }

    //onStopの後
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("TEST_MainActivity","onRestart");
    }

    //onStartの後
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("TEST_MainActivity","onResume");
        startGPS();
    }

    //アクティビティ実行の後
    @Override
    protected void onPause() {
        super.onPause();
        Log.d("TEST_MainActivity","onPause");
    }

    //onPauseの後
    @Override
    protected void onStop(){
        super.onStop();
        Log.d("TEST_MainActivity","onStop");
        stopGPS();
    }

    //onStopの後
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("TEST_MainActivity","onDestroy");
        unregisterReceiver(myreceiver);
        //unbindService(this);
    }

    //右上メニュー
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("TEST_MainActivity","onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private final int RESULTCODE = 1;   //受け取りコード

    //右上メニュークリック
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("TEST_MainActivity","onOptionsItemSelected");
        switch (item.getItemId()) {
            case R.id.menu_main_settings:

                //startActivityForResult(SettingsIntent,RESULTCODE);
                return true;
            case R.id.menu_main_layout:
                final String[] items = {"ポップ", "シンプル"};
                int defaultItem = 0; // デフォルトでチェックされているアイテム
                final List<Integer> checkedItems = new ArrayList<>();
                checkedItems.add(defaultItem);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("デザインの変更")
                        .setSingleChoiceItems(items, defaultItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                checkedItems.clear();
                                checkedItems.add(which);
                            }
                        })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!checkedItems.isEmpty()) {
                                   // Log.d("checkedItem:", "" + checkedItems.get(0));
                                    if(checkedItems.get(0)==1){
                                        Intent intent = new Intent(MainActivity.this, MainSimpleActivity.class);
                                        // 次画面のアクティビティ起動
                                        startActivity(intent);
                                        finish();
                                    }else{

                                    }
                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Messenger _messenger;

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d("TEST_MainActivity","onServiceConnected");
        _messenger = new Messenger(service);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d("TEST_MainActivity","onServiceDisconnected");
        _messenger = null;
    }

    String lat = new String();
    String lon = new String();

    /*GPS設定*/
    // 結果の受け取り
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d("TEST",Integer.toString(requestCode) +Integer.toString(grantResults[0]) );
        if (requestCode == 1000) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("debug","checkSelfPermission true");
                startGPS();

                return;

            } else {
                // それでも拒否された時の対応
                Toast toast = Toast.makeText(this, "これ以上なにもできません", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = String.valueOf(location.getLatitude());
        lon = String.valueOf(location.getLongitude());

        Log.d("TEST_MainActivity","onLocationChanged = " + lat);
        Log.d("TEST_MainActivity","onLocationChanged = " + lon);

        url = "http://api.openweathermap.org/data/2.5/forecast"
                + "?lat=" + String.valueOf(lat)
                + "&lon=" + String.valueOf(lon)
                + "&cnt=2"
                + "&APPID=" + pass;

        jsonLoader = new JsonLoader(url);
        thread = new Thread(this);
        thread.start();

        //stopGPS();
    }

    //HandlerはUIスレッドで生成する。
    Handler handler = new Handler();
    JSONObject jsonObject;
    String id = new String();
    String icon = new String();

    @Override
    public void run() {
        jsonObject = jsonLoader.loadInBackground();
        handler.post(new Runnable() {
            @Override
            public void run() {

                try {
                    JSONArray lists = jsonObject.getJSONArray("list");

                    //Log.d("TEST",jsonObject.toString(4));

                    for (int i = 0; i < 2; i++) {
                        try {
                            JSONObject list = lists.getJSONObject(i);
                            JSONArray weatherlist = list.getJSONArray("weather");
                            JSONObject weather = weatherlist.getJSONObject(0);

                            id = weather.get("id").toString();
                            icon = weather.get("icon").toString();
                            Log.d("TEST", id + " , " + icon);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Calendar cal = Calendar.getInstance();

                Toast.makeText(MainActivity.this, "TEST", Toast.LENGTH_LONG).show();
            }
        });
        thread = null;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case LocationProvider.AVAILABLE:
                Log.d("debug", "LocationProvider.AVAILABLE");
                break;
            case LocationProvider.OUT_OF_SERVICE:
                Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                break;
        }
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    /*GPS終了*/

    public class Receiver extends BroadcastReceiver {
        String[] text = new String[3];
        int[] color = new int[4];
        String comment="" ;
        int id=2;
        String[] draw_ladybug={"@drawable/cold",
                "@drawable/cool",
                "@drawable/good",
                "@drawable/warm",
                "@drawable/hot",
                "@drawable/veryhot"};
        String[] draw_ladybug2={"@drawable/cold2",
                "@drawable/cool2",
                "@drawable/good2",
                "@drawable/warm2",
                "@drawable/hot2",
                "@drawable/veryhot2"};


        //  横幅のみ画面サイズに変更
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            text[0] = bundle.getString("index1");
            text[1] = bundle.getString("index2");
            text[2] = bundle.getString("index3");
            color[0] = bundle.getInt("colorA");
            color[1] = bundle.getInt("colorR");
            color[2] = bundle.getInt("colorG");
            color[3] = bundle.getInt("colorB");
            comment=bundle.getString("comment");
            id=bundle.getInt("id");

            Log.d("TEST_MainActivity", String.format("onReceive=%s, %s, %s",color[1],color[2],color[3]));

            TextView t1 = (TextView) findViewById(R.id.textView);
            TextView t2 = (TextView) findViewById(R.id.textView2);
            TextView t3 = (TextView) findViewById(R.id.textView3);
            TextView tc = (TextView) findViewById(R.id.text_comment);
            t1.setText(text[0]);
            t2.setText(text[1]);
            t3.setText(text[2]);
            tc.setText(comment);

            ImageView iv = (ImageView)findViewById(R.id.ladybug);

            switch (id){
                case 0:
                        iv.setImageResource(R.drawable.cold);
                        break;
                case 1:
                        iv.setImageResource(R.drawable.cool);
                        break;
                case 2:
                        iv.setImageResource(R.drawable.good);
                        break;
                case 3:
                        iv.setImageResource(R.drawable.warm);
                        break;
                case 4:
                        iv.setImageResource(R.drawable.hot);
                        break;
                case 5:
                        iv.setImageResource(R.drawable.veryhot);
                        break;

            }

        }
    }
}
