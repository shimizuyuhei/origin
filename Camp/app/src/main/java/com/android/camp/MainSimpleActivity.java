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
import android.graphics.Color;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 2016/07/08.
 */
public class MainSimpleActivity extends AppCompatActivity implements ServiceConnection,LocationListener,Runnable {

    private Intent SettingsIntent;
    private Receiver myreceiver;

    private LocationManager locationManager;
    private String url;
    private String pass = new String();
    private JsonLoader jsonLoader;
    private Thread thread;
    private TextView Streetview;
    private ImageView Crrenticon;
    private TextView CurrentWeather;
    private ImageView Futureicon;
    private TextView FutureWeather;
    private Weather weather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("CAMP_MainSimpleActivity","onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        MainActivity.NotificationStopFlag=true;
         TextView comment=(TextView)findViewById(R.id.index_txt);
        comment.setText("ボードを\n選択してください");

        setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));

        SettingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);

        //startService(BeaconGetIntent);

        myreceiver = new Receiver();
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction("action");
        registerReceiver(myreceiver, intentfilter);

        ImageView BoardSettingView=(ImageView)findViewById(R.id.ladybug);
        BoardSettingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.NotificationStopFlag=false;
                startActivityForResult(SettingsIntent,RESULTCODE);


            }

        });

        ImageButton HelpInfButton1= (ImageButton)findViewById(R.id.image_button1);
        ImageButton HelpInfButton2= (ImageButton)findViewById(R.id.image_button2);
        ImageButton HelpInfButton3= (ImageButton)findViewById(R.id.image_button3);

        HelpInfButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainSimpleActivity.this, PreparationListActivity.class);
                // 次画面のアクティビティ起動
                startActivity(intent);
            }

        });
        HelpInfButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainSimpleActivity.this, CookingListActivity.class);
                // 次画面のアクティビティ起動
                startActivity(intent);
            }

        });

        HelpInfButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainSimpleActivity.this, DangerListActivity.class);
                // 次画面のアクティビティ起動
                startActivity(intent);
            }

        });

        //天気領域クリック処理
        LinearLayout Weather_Layout=(LinearLayout)findViewById(R.id.weather_layout);
        Weather_Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGPS();
            }

        });

        Streetview = (TextView) findViewById(R.id.PlaceWeather);
        Crrenticon = (ImageView) findViewById(R.id.CurrentWeatherIcon);
        CurrentWeather = (TextView) findViewById(R.id.CurrentWeatherText);
        Futureicon = (ImageView) findViewById(R.id.FutureWeatherIcon);
        FutureWeather = (TextView) findViewById(R.id.FutureWeatherText);
        weather = new Weather();
        weather.Weather();

        pass = "6bc4bdb0435fb3599d879b987453b459";

        // LocationManager インスタンス生成
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }
    @Override protected void onActivityResult( int requestCode, int resultCode, Intent data) {
         TextView comment=(TextView)findViewById(R.id.index_txt);

        if(requestCode == this.RESULTCODE) {
            MainActivity.NotificationStopFlag=true;
            if (resultCode ==0) {
            } else {
                ImageView loading_gif1 = (ImageView) findViewById(R.id.Loading_gif1);
                ImageView loading_gif2 = (ImageView) findViewById(R.id.Loading_gif2);
                ImageView loading_gif3 = (ImageView) findViewById(R.id.Loading_gif3);

                GlideDrawableImageViewTarget target1 = new GlideDrawableImageViewTarget(loading_gif1);
                Glide.with(MainSimpleActivity.this).load(R.raw.loading).into(target1);
                GlideDrawableImageViewTarget target2 = new GlideDrawableImageViewTarget(loading_gif2);
                Glide.with(MainSimpleActivity.this).load(R.raw.loading).into(target2);
                GlideDrawableImageViewTarget target3 = new GlideDrawableImageViewTarget(loading_gif3);
                Glide.with(MainSimpleActivity.this).load(R.raw.loading).into(target3);
                comment.setText("");

            }
        }
    }
    private static final long MinTime = 30; //30分
    private static final float MinDistance = 100;   //100m

    //GPS開始
    protected void startGPS() {
        Log.d("CAMP_MainSimpleActivity", "startGPS");
        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!gpsEnabled) {
            // GPSを設定するように促す
            enableLocationSettings();
        }

        gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (locationManager != null && gpsEnabled) {
            onGPS();
        } else {
            Streetview.setText("GPSを\nONにしてください");
            Log.d("CAMP_MainSimpleActivity", "startGPS_エラー");
        }
    }

    protected void onGPS(){
        Log.d("CAMP_MainSimpleActivity", "onGPS");
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
            Streetview.setText("計測中\n");

            ImageView weather1 = (ImageView) findViewById(R.id.CurrentWeatherIcon);
            GlideDrawableImageViewTarget target1 = new GlideDrawableImageViewTarget(weather1);
            Glide.with(MainSimpleActivity.this).load(R.raw.load_weather).into(target1);
            ImageView weather2 = (ImageView) findViewById(R.id.FutureWeatherIcon);
            GlideDrawableImageViewTarget target2 = new GlideDrawableImageViewTarget(weather2);
            Glide.with(MainSimpleActivity.this).load(R.raw.load_weather).into(target2);

        } catch (Exception e) {
            e.printStackTrace();

            Toast toast = Toast.makeText(this, "例外が発生、位置情報のPermissionを許可していますか？", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    //GPS停止
    private void stopGPS(){
        if (locationManager != null) {
            Log.d("CAMP_MainSimpleActivity", "onStop");
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
        Log.d("CAMP_MainSimpleActivity","onStart");
    }

    //onStopの後
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("CAMP_MainSimpleActivity","onRestart");
    }

    //onStartの後
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("CAMP_MainSimpleActivity","onResume");

        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (locationManager != null && gpsEnabled) {
            onGPS();
        } else {
            Streetview.setText("GPSをONに\nしてください");
            Log.d("CAMP_MainSimpleActivity", "startGPS_エラー");
        }
    }

    //アクティビティ実行の後
    @Override
    protected void onPause() {
        super.onPause();
        Log.d("CAMP_MainSimpleActivity","onPause");
    }

    //onPauseの後
    @Override
    protected void onStop(){
        super.onStop();
        Log.d("CAMP_MainSimpleActivity","onStop");
    }

    //onStopの後
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("CAMP_MainSimpleActivity","onDestroy");
        unregisterReceiver(myreceiver);
        //unbindService(this);
    }

    //右上メニュー
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("CAMP_MainSimpleActivity","onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private final int RESULTCODE = 1;   //受け取りコード

    //右上メニュークリック
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("CAMP_MainSimpleActivity","onOptionsItemSelected");
        switch (item.getItemId()) {
            case R.id.menu_main_layout:
                final String[] items = {"ポップ", "シンプル"};
                int defaultItem = 1; // デフォルトでチェックされているアイテム
                final List<Integer> checkedItems = new ArrayList<>();
                checkedItems.add(defaultItem);
                new AlertDialog.Builder(MainSimpleActivity.this)
                        .setTitle("デザインの変更")
                        .setSingleChoiceItems(items, defaultItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                checkedItems.clear();
                                checkedItems.add(which);
                                if(checkedItems.get(0)==0){
                                    dialog.dismiss();
                                    Intent intent = new Intent(MainSimpleActivity.this, MainActivity.class);
                                    // 次画面のアクティビティ起動
                                    startActivity(intent);
                                    finish();
                                }else{
                                    dialog.dismiss();
                                }
                            }

                        })
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Messenger _messenger;

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d("CAMP_MainSimpleActivity","onServiceConnected");
        _messenger = new Messenger(service);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d("CAMP_MainSimpleActivity","onServiceDisconnected");
        _messenger = null;
    }

    /*GPS設定*/
    // 結果の受け取り
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d("CAMP",Integer.toString(requestCode) +Integer.toString(grantResults[0]) );
        if (requestCode == 1000) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("CAMP Permission","checkSelfPermission true");
                startGPS();

                return;

            } else {
                // それでも拒否された時の対応
                Toast toast = Toast.makeText(this, "これ以上なにもできません", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    double lat;
    double lon;
    String Street = new String();

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lon = location.getLongitude();

        Log.d("CAMP_MainSimpleActivity","onLocationChanged = " + lat);
        Log.d("CAMP_MainSimpleActivity","onLocationChanged = " + lon);

        url = "http://api.openweathermap.org/data/2.5/forecast"
                + "?lat=" + String.valueOf(lat)
                + "&lon=" + String.valueOf(lon)
                + "&cnt=2"
                + "&APPID=" + pass;

        jsonLoader = new JsonLoader(url);
        thread = new Thread(this);
        thread.start();

        Street = weather.getAddress(getApplicationContext(),lat,lon);

        Streetview.setText(Street);

        Log.d("CAMP_MainSimpleActivity","onLocationChanged = " + Street);
        //stopGPS();
    }

    //HandlerはUIスレッドで生成する。
    Handler handler = new Handler();
    JSONObject jsonObject;
    String[] id = new String[2];
    String[] icon = new String[2];

    @Override
    public void run() {
        jsonObject = jsonLoader.loadInBackground();
        handler.post(new Runnable() {
            @Override
            public void run() {
                JSONArray lists;
                try {
                    if(jsonObject!=null) {

                        lists = jsonObject.getJSONArray("list");

                        //Log.d("TEST",jsonObject.toString(4));

                        for (int i = 0; i < 2; i++) {
                            try {
                                JSONObject list = lists.getJSONObject(i);
                                JSONArray weatherlist = list.getJSONArray("weather");
                                JSONObject weather = weatherlist.getJSONObject(0);

                                id[i] = weather.get("id").toString();
                                icon[i] = weather.get("icon").toString();
                                Log.d("CAMP_MainSimpleActivity", "run=" + id + " , " + icon);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(jsonObject!=null) {
                    CurrentWeather.setText(weather.Getweather(id[0]));
                    Crrenticon.setImageResource(weather.Getweathericon(icon[0]));

                    FutureWeather.setText(weather.Getweather(id[1]));
                    Futureicon.setImageResource(weather.Getweathericon(icon[1]));
                }
                //Toast.makeText(MainActivity.this, weather.Getweather(id[0]), Toast.LENGTH_LONG).show();
            }
        });
        thread = null;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case LocationProvider.AVAILABLE:
                Log.d("CAMP", "LocationProvider.AVAILABLE");
                break;
            case LocationProvider.OUT_OF_SERVICE:
                Log.d("CAMP", "LocationProvider.OUT_OF_SERVICE");
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.d("CAMP", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                break;
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("CAMP_MainSimpleActivity","onProviderEnabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("CAMP_MainSimpleActivity","onProviderDisabled");
    }
    /*GPS終了*/

    public class Receiver extends BroadcastReceiver {
        String[] text = new String[3];
         int[] color = new int[4];
        String comment="" ;
        int id=2;

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

         //   Log.d("CAMP_MainSimpleActivity", String.format("onReceive=%s, %s, %s",color[1],color[2],color[3]));

            TextView index = (TextView)findViewById(R.id.index_txt);
            TextView temp = (TextView)findViewById(R.id.temp_txt);
            TextView humid = (TextView)findViewById(R.id.humid_txt);
            TextView comment_t = (TextView)findViewById(R.id.text_comment);
            ImageView gifView1 = (ImageView) findViewById(R.id.Loading_gif1);
            ImageView gifView2 = (ImageView) findViewById(R.id.Loading_gif2);
            ImageView gifView3 = (ImageView) findViewById(R.id.Loading_gif3);
            index.setTextColor(Color.argb(color[0],color[1],color[2],color[3]));
            if(text[0]==null){
                gifView3.setVisibility(View.VISIBLE);
                comment_t.setText("");
            }else{
                index.setText(text[0]);
                gifView3.setVisibility(View.INVISIBLE);
                comment_t.setText(comment);
            }
            if(text[1]==null){
                gifView1.setVisibility(View.VISIBLE);
                temp.setText("");
            }else{
                gifView1.setVisibility(View.INVISIBLE);
                temp.setText(text[1]+"℃");
            }
            if(text[2]==null){
                gifView2.setVisibility(View.VISIBLE);
                humid.setText("");
            }else{
                gifView2.setVisibility(View.INVISIBLE);

                humid.setText(text[2]+"％");
            }

            ImageView icon = (ImageView)findViewById(R.id.ladybug);
            switch (id){
                case 0:
                    icon.setImageResource(R.drawable.cold2);
                    break;
                case 1:
                    icon.setImageResource(R.drawable.cool2);
                    break;
                case 2:
                    icon.setImageResource(R.drawable.good2);
                    break;
                case 3:
                    icon.setImageResource(R.drawable.warm2);
                    break;
                case 4:
                    icon.setImageResource(R.drawable.hot2);
                    break;
                case 5:
                    icon.setImageResource(R.drawable.veryhot2);
                    break;

            }
        }
    }
}
