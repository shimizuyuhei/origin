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
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements ServiceConnection,LocationListener,Runnable {

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
    private final int RESULTCODE = 1;   //受け取りコード
    public static boolean NotificationStopFlag = true;

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("CAMP_MainActivity","onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView comment_t=(TextView)findViewById(R.id.index_txt);
         if(comment_t!=null) {
             comment_t.setText("ボードを選択してください");
         }
         setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));
        SettingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);

        //startService(BeaconGetIntent);

        myreceiver = new Receiver();
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction("action");
        registerReceiver(myreceiver, intentfilter);

         //お役立ち情報
         ImageButton HelpInfButton1= (ImageButton)findViewById(R.id.HelpInfButton1);
         if(HelpInfButton1!=null){
         HelpInfButton1.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(MainActivity.this, PreparationListActivity.class);
                 // 次画面のアクティビティ起動
                 startActivity(intent);
             }

         });
         }
             ImageButton HelpInfButton2= (ImageButton)findViewById(R.id.HelpInfButton2);
         if(HelpInfButton2!=null) {
             HelpInfButton2.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     Intent intent = new Intent(MainActivity.this, CookingListActivity.class);
                     // 次画面のアクティビティ起動
                     startActivity(intent);
                 }

             });
         }
         ImageButton HelpInfButton3= (ImageButton)findViewById(R.id.HelpInfButton3);
         if (HelpInfButton3!=null) {
             HelpInfButton3.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     Intent intent = new Intent(MainActivity.this, DangerListActivity.class);
                     // 次画面のアクティビティ起動
                     startActivity(intent);
                 }

             });
             //ボード選択画面
         }
         ImageButton BoardSettingButton =(ImageButton)findViewById(R.id.BoardSettingButton);
         if(BoardSettingButton!=null) {
             BoardSettingButton.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     NotificationStopFlag = false;
                     startActivityForResult(SettingsIntent, RESULTCODE);

                 }

             });
         }
         LinearLayout BoardSettingLayout =(LinearLayout)findViewById(R.id.BoardSettingLayout);

         if(BoardSettingLayout!=null) {
             BoardSettingLayout.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     NotificationStopFlag = false;
                     startActivityForResult(SettingsIntent, RESULTCODE);
                 }

             });
         }


         LinearLayout weather_layout=(LinearLayout)findViewById(R.id.weather_layout);
         //天気領域クリック処理
         if(weather_layout!=null) {
             weather_layout.setClickable(true);
             weather_layout.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     startGPS();
                 }
             });
         }
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
        LinearLayout comment_layout=(LinearLayout)findViewById(R.id.BoardSettingLayout);
        TextView comment=(TextView)findViewById(R.id.index_txt);
Log.d("onActivityResultttttttt",String.valueOf(resultCode));
        Log.d("onActivityResultttttttt",String.valueOf(requestCode));

        if(requestCode == this.RESULTCODE) {
            NotificationStopFlag=true;
            if (resultCode ==0) {

                if(comment_layout!=null){
                    comment_layout.setClickable(true);
                }
            } else if (resultCode==1){
                if(comment_layout!=null) {
                    comment_layout.setClickable(false);
                }
                ImageView loading_gif1 = (ImageView) findViewById(R.id.Loading_gif1);
                ImageView loading_gif2 = (ImageView) findViewById(R.id.Loading_gif2);
                ImageView loading_gif3 = (ImageView) findViewById(R.id.Loading_gif3);

                GlideDrawableImageViewTarget target1 = new GlideDrawableImageViewTarget(loading_gif1);
                Glide.with(MainActivity.this).load(R.raw.loading).into(target1);
                GlideDrawableImageViewTarget target2 = new GlideDrawableImageViewTarget(loading_gif2);
                Glide.with(MainActivity.this).load(R.raw.loading).into(target2);
                GlideDrawableImageViewTarget target3 = new GlideDrawableImageViewTarget(loading_gif3);
                Glide.with(MainActivity.this).load(R.raw.loading).into(target3);
                if(comment!=null) {
                    comment.setText("");
                }

            }
        }
    }
    private static final long MinTime = 30; //30分
    private static final float MinDistance = 100;   //100m

    //GPS開始
    protected void startGPS() {
        Log.d("CAMP_MainActivity", "startGPS");
        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!gpsEnabled) {
            // GPSを設定するように促す
            enableLocationSettings();
        }

        gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (locationManager != null && gpsEnabled) {
            onGPS();
        } else {
            Streetview.setText("GPSをONに\nしてください");
            Log.d("CAMP_MainActivity", "startGPS_エラー");
        }
    }

    protected void onGPS(){
        Log.d("CAMP_MainActivity", "onGPS");
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
            Glide.with(MainActivity.this).load(R.raw.load_weather).into(target1);
            ImageView weather2 = (ImageView) findViewById(R.id.FutureWeatherIcon);
            GlideDrawableImageViewTarget target2 = new GlideDrawableImageViewTarget(weather2);
            Glide.with(MainActivity.this).load(R.raw.load_weather).into(target2);

        } catch (Exception e) {
            e.printStackTrace();

            Toast toast = Toast.makeText(this, "例外が発生、位置情報のPermissionを許可していますか？", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    //GPS停止
    private void stopGPS(){
        if (locationManager != null) {
            Log.d("CAMP_MainActivity", "onStop");
            // update を止める
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.removeUpdates(this);
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
        Log.d("CAMP_MainActivity","onStart");
    }

    //onStopの後
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("CAMP_MainActivity","onRestart");
    }

    //onStartの後
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("CAMP_MainActivity","onResume");

        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (locationManager != null && gpsEnabled) {
            onGPS();
        } else {
            Streetview.setText("GPSをONに\nしてください");
            Log.d("CAMP_MainActivity", "startGPS_エラー");
        }
    }

    //アクティビティ実行の後
    @Override
    protected void onPause() {
        super.onPause();
        Log.d("CAMP_MainActivity","onPause");
    }

    //onPauseの後
    @Override
    protected void onStop(){
        super.onStop();
        Log.d("CAMP_MainActivity","onStop");
        stopGPS();
    }

    //onStopの後
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("CAMP_MainActivity","onDestroy");
        unregisterReceiver(myreceiver);
        //unbindService(this);
    }

    //右上メニュー
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("CAMP_MainActivity","onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    //右上メニュークリック
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("CAMP_MainActivity","onOptionsItemSelected");
        switch (item.getItemId()) {
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
                                if(checkedItems.get(0)==1){
                                    Intent intent = new Intent(MainActivity.this, MainSimpleActivity.class);
                                    // 次画面のアクティビティ起動
                                    startActivity(intent);
                                    finish();
                                }else{
                                    dialog.dismiss();
                                }
                            }

                        }) .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Messenger _messenger;

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d("CAMP_MainActivity","onServiceConnected");
        _messenger = new Messenger(service);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d("CAMP_MainActivity","onServiceDisconnected");
        _messenger = null;
    }

    /*GPS設定*/
    // 結果の受け取り
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d("GPS",Integer.toString(requestCode) +Integer.toString(grantResults[0]) );
        if (requestCode == 1000) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("CAMP_Permission","checkSelfPermission true");
                startGPS();

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

        Log.d("CAMP_MainActivity","onLocationChanged = " + lat);
        Log.d("CAMP_MainActivity","onLocationChanged = " + lon);

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

        Log.d("CAMP_MainActivity","onLocationChanged = " + Street);
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
                if(jsonObject != null) {
                    try {

                        lists = jsonObject.getJSONArray("list");

                        //Log.d("TEST",jsonObject.toString(4));

                        for (int i = 0; i < 2; i++) {
                            try {
                                JSONObject list = lists.getJSONObject(i);
                                JSONArray weatherlist = list.getJSONArray("weather");
                                JSONObject weather = weatherlist.getJSONObject(0);

                                id[i] = weather.get("id").toString();
                                icon[i] = weather.get("icon").toString();
                                Log.d("CAMP_MainActivity", "run=" + id + " , " + icon);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

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
        Log.d("CAMP_MainActivity","onProviderEnabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("CAMP_MainActivity","onProviderDisabled");
    }
    /*GPS終了*/

    public class Receiver extends BroadcastReceiver {
        String[] text = new String[3];
        int[] color = new int[4];
        String comment="" ;
        int icon_id=2;

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
            icon_id=bundle.getInt("icon_id");

       //     Log.d("color_MainActivity", String.format("onReceive=%s, %s, %s",color[1],color[2],color[3]));
            Log.d("CAMP_MainActivity", String.format("onReceive=%s, %s, %s, %s",text[0],text[1],text[2],comment));

            TextView index = (TextView) findViewById(R.id.index_txt);
            TextView temp = (TextView) findViewById(R.id.temp_txt);
            TextView humid = (TextView) findViewById(R.id.humid_txt);
            TextView comment_t = (TextView) findViewById(R.id.text_comment);
            if(index!=null) {
                index.setTextColor(Color.argb(color[0], color[1], color[2], color[3]));
            }
            ImageView Loading_gif1 = (ImageView) findViewById(R.id.Loading_gif1);
            ImageView Loading_gif2 = (ImageView) findViewById(R.id.Loading_gif2);
            ImageView Loading_gif3 = (ImageView) findViewById(R.id.Loading_gif3);

            //データが届いていなければ、ローディングアイコンの表示
            if(text[0]==null){
                if(Loading_gif3!=null) {
                    Loading_gif3.setVisibility(View.VISIBLE);
                }
            }else{
                if(Loading_gif3!=null){
                    Loading_gif3.setVisibility(View.INVISIBLE);
                }
                if(index!=null) {
                    index.setText(text[0]);
                }
                if(comment_t!=null) {
                    comment_t.setText(comment);
                }
            }
            if(text[1]==null){
                if(Loading_gif1!=null) {
                    Loading_gif1.setVisibility(View.VISIBLE);
                }
                if(temp!=null) {
                    temp.setText("");
                }
            }else {
                if(Loading_gif1!=null) {
                    Loading_gif1.setVisibility(View.INVISIBLE);
                }
                if(temp!=null){
                    temp.setText(text[1]+"℃");
                }
            }

            if(text[2]==null){
                if(Loading_gif2!=null) {
                    Loading_gif2.setVisibility(View.VISIBLE);
                }
                if(humid!=null){
                    humid.setText("");
                }
            }else{
                if(Loading_gif2!=null){
                    Loading_gif2.setVisibility(View.INVISIBLE);
                }
                if(humid!=null){
                    humid.setText(text[2]+"％");
                }
            }

            //表示するアイコンを変える
            ImageView icon = (ImageView)findViewById(R.id.ladybug);
            if(icon!=null) {
                switch (icon_id) {
                    case 0:
                        icon.setImageResource(R.drawable.cold);
                        break;
                    case 1:
                        icon.setImageResource(R.drawable.cool);
                        break;
                    case 2:
                        icon.setImageResource(R.drawable.good);
                        break;
                    case 3:
                        icon.setImageResource(R.drawable.warm);
                        break;
                    case 4:
                        icon.setImageResource(R.drawable.hot);
                        break;
                    case 5:
                        icon.setImageResource(R.drawable.veryhot);
                        break;
                }

            }
        }
    }
}
