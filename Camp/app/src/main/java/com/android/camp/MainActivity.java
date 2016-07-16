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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements LocationListener,Runnable {

    private Intent SettingsIntent;  /*Linkingボード選択画面遷移用宣言*/
    private Receiver myreceiver;    /*ブロードキャストレシーバ宣言*/

    private LocationManager locationManager;    /*GPS取得用宣言*/
    private String url; /*天気APIのアクセス先URL宣言*/
    private String pass = new String(); /*天気APIの取得用のpasskey宣言*/
    private JsonLoader jsonLoader;  /*JSON形式のモデルデータ宣言*/
    private Thread thread;  /*スレッド宣言*/
    private TextView Streetview;    /*位置情報の表示宣言*/
    private ImageView Crrenticon;   /*現在の天気アイコン宣言*/
    private TextView CurrentWeather;    /*現在の天気情報宣言*/
    private ImageView Futureicon;   /*3時間後の天気アイコン宣言*/
    private TextView FutureWeather; /*3時間後の天気情報宣言*/
    private Weather weather;    /*天気クラス宣言*/
    private final int RESULTCODE = 1;   /*受け取りコード*/
    public static boolean NotificationStopFlag = true;  /*ノーティフィケーションからのLinking停止可能状態*/
    public static boolean scanningFlag = false; /*Linkingボード選択状態*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*Log.d("CAMP_MainActivity","onCreate");*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView index = (TextView) findViewById(R.id.text_comment);   /*Linkingボード状態*/

        if(index!=null) {
             index.setText("ボードを選択してください");
        }
        setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar)); /*Toolbarをアクションバーとして使用*/
        SettingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);   /*Linkingボード選択画面遷移用セット*/

        /*Linkingボード受け取り用ブロードキャストレシーバ*/
        myreceiver = new Receiver();    /*ブロードキャストレシーバのセット*/
        IntentFilter intentfilter = new IntentFilter(); /*インテントフィルターの作成*/
        intentfilter.addAction("action");   /*フィルタリングする名前の設定*/
        registerReceiver(myreceiver, intentfilter); /*フィルターのセット*/

         /*お役立ち情報*/
        /*準備編*/
         ImageButton HelpInfButton1 = (ImageButton)findViewById(R.id.HelpInfButton1);
         if(HelpInfButton1 != null){

             HelpInfButton1.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     Intent intent = new Intent(MainActivity.this, PreparationListActivity.class);
                     startActivity(intent); /*準備編のアクティビティ起動*/
                 }

             });
         }
        /*料理編*/
        ImageButton HelpInfButton2= (ImageButton)findViewById(R.id.HelpInfButton2);
         if(HelpInfButton2!=null) {
             HelpInfButton2.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     Intent intent = new Intent(MainActivity.this, CookingListActivity.class);
                     startActivity(intent); /*料理編のアクティビティ起動*/
                 }

             });
         }
        /*危険編*/
         ImageButton HelpInfButton3= (ImageButton)findViewById(R.id.HelpInfButton3);
         if (HelpInfButton3!=null) {
             HelpInfButton3.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     Intent intent = new Intent(MainActivity.this, DangerListActivity.class);
                     startActivity(intent); /*危険編のアクティビティ起動*/
                 }

             });
         }

        /*Linkingボード設定画面*/
         ImageButton BoardSettingButton =(ImageButton)findViewById(R.id.BoardSettingButton);
         if(BoardSettingButton!=null) {
             BoardSettingButton.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     NotificationStopFlag = false;  /*ノーティフィケーションからのLinking取得停止を無効化*/
                     startActivityForResult(SettingsIntent, RESULTCODE);    /*Linkingボード設定アクティビティ起動*/

                 }

             });
         }

        /*温湿度による危険度の表示領域クリック*/
         LinearLayout BoardSettingLayout =(LinearLayout)findViewById(R.id.BoardSettingLayout);
         if(BoardSettingLayout!=null) {
             BoardSettingLayout.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     NotificationStopFlag = false;  /*ノーティフィケーションからのLinking取得停止を無効化*/
                     startActivityForResult(SettingsIntent, RESULTCODE);    /*Linkingボード設定アクティビティ起動*/
                 }

             });
         }

         /*天気表示領域クリック処理*/
         LinearLayout weather_layout=(LinearLayout)findViewById(R.id.weather_layout);
         if(weather_layout!=null) {
             weather_layout.setClickable(true); /*クリックの許可*/
             weather_layout.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     startGPS();    /*GPS、天気情報取得開始*/
                 }
             });
         }

         Streetview = (TextView) findViewById(R.id.PlaceWeather);   /*位置情報の表示*/
         Crrenticon = (ImageView) findViewById(R.id.CurrentWeatherIcon);    /*現在の天気アイコン*/
         CurrentWeather = (TextView) findViewById(R.id.CurrentWeatherText); /*現在の天気情報*/
         Futureicon = (ImageView) findViewById(R.id.FutureWeatherIcon); /*3時間後の天気アイコン*/
         FutureWeather = (TextView) findViewById(R.id.FutureWeatherText);   /*3時間後の天気情報*/
         weather = new Weather();   /*天気クラスセット*/

         pass = "6bc4bdb0435fb3599d879b987453b459"; /*天気APIの取得用のpasskeyの設定*/

         locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);    /*GPS取得用セット*/

    }


    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data) {
        LinearLayout comment_layout = (LinearLayout)findViewById(R.id.BoardSettingLayout);    /*温湿度による危険度の表示領域クリック*/
        TextView comment = (TextView)findViewById(R.id.text_comment);   /*Linkingボード状態*/
        TextView temp = (TextView)findViewById(R.id.temp_txt);  /*温度情報*/
        TextView humid = (TextView)findViewById(R.id.humid_txt);    /*湿度情報*/

       if(requestCode == this.RESULTCODE) {
           NotificationStopFlag = true;
           if (resultCode == 0) {
               if (!scanningFlag) {
                   if (comment != null) {
                       comment.setText("ボードを選択してください");
                   }
                   if (temp != null) {
                       temp.setText("");
                   }
                   if (comment_layout != null) {
                       comment_layout.setClickable(true);  /*クリックの許可*/
                   }
                   if (humid != null) {
                       humid.setText("");
                   }
               }
           } else {
               if (comment_layout != null) {
                   comment_layout.setClickable(false); /*クリックの禁止*/
               }
               /*ロード時表示画像の設定*/
               ImageView loading_gif1 = (ImageView) findViewById(R.id.Loading_gif1);
               ImageView loading_gif2 = (ImageView) findViewById(R.id.Loading_gif2);
               ImageView loading_gif3 = (ImageView) findViewById(R.id.Loading_gif3);

               if (loading_gif1 != null) {
                   GlideDrawableImageViewTarget target1 = new GlideDrawableImageViewTarget(loading_gif1);   /*GIF動画を設定*/
                   Glide.with(MainActivity.this).load(R.raw.loading).into(target1); /*表示するGIF動画をセット*/
               }
               if (loading_gif2 != null) {
                   GlideDrawableImageViewTarget target2 = new GlideDrawableImageViewTarget(loading_gif2);   /*GIF動画を設定*/
                   Glide.with(MainActivity.this).load(R.raw.loading).into(target2); /*表示するGIF動画をセット*/
               }
               if (loading_gif3 != null) {
                   GlideDrawableImageViewTarget target3 = new GlideDrawableImageViewTarget(loading_gif3);   /*GIF動画を設定*/
                   Glide.with(MainActivity.this).load(R.raw.loading).into(target3); /*表示するGIF動画をセット*/
               }
               if (comment != null) {
                   comment.setText("");
               }

           }
       }
    }

    /*GPSの取得時間設定*/
    private static final long MinTime = 30; /*30分*/
    private static final float MinDistance = 100;   /*100m*/

    /**************************************************************/
    /*タイトル :GPS開始                                           */
    /*引数     :無し                                              */
    /*戻り値   :無し                                              */
    /**************************************************************/
    protected void startGPS() {
        /*Log.d("CAMP_MainActivity", "startGPS");*/
        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);   /*端末のGPSがONであるか判定*/

        if (!gpsEnabled) {
            /*GPSを設定するように促す*/
            enableLocationSettings();
        }

        gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);   /*再度端末のGPSがONであるか判定*/

        /*GPSが音であれば取得を開始*/
        if (locationManager != null && gpsEnabled) {
            onGPS();    /*取得を開始*/
        } else {
            Streetview.setText("GPSをONに\nしてください");
            /*Log.d("CAMP_MainActivity", "startGPS_エラー");*/
        }
    }

    /**************************************************************/
    /*タイトル :GPS取得                                           */
    /*引数     :無し                                              */
    /*戻り値   :無し                                              */
    /**************************************************************/
    protected void onGPS(){
        /*Log.d("CAMP_MainActivity", "onGPS");*/
        /*バックグラウンドから戻ってしまうと例外が発生する場合がある*/
        try {
            /*GPSの開始*/
            /* minTime = 1000msec, minDistance = 50m */
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            /*GPSを取得開始*/
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, MinTime, MinDistance, this);
            Streetview.setText("計測中\n");
            ImageView weather1 = (ImageView) findViewById(R.id.CurrentWeatherIcon); /*現在の天気アイコン*/
            if(weather1 !=null) {
                GlideDrawableImageViewTarget target1 = new GlideDrawableImageViewTarget(weather1);  /*計測時のGIF動画の設定*/
                Glide.with(MainActivity.this).load(R.raw.load_weather).into(target1);   /*表示するGIF動画をセット*/
            }
            ImageView weather2 = (ImageView) findViewById(R.id.FutureWeatherIcon); /*3時間後の天気アイコン*/
            if(weather2!=null) {
                GlideDrawableImageViewTarget target2 = new GlideDrawableImageViewTarget(weather2);  /*計測時のGIF動画の設定*/
                Glide.with(MainActivity.this).load(R.raw.load_weather).into(target2);   /*表示するGIF動画をセット*/
            }
        } catch (Exception e) {
            e.printStackTrace();

            Toast toast = Toast.makeText(this, "例外が発生、位置情報のPermissionを許可していますか？", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /**************************************************************/
    /*タイトル :GPS停止                                           */
    /*引数     :無し                                              */
    /*戻り値   :無し                                              */
    /**************************************************************/
    private void stopGPS(){
        if (locationManager != null) {
            /*Log.d("CAMP_MainActivity", "onStop");*/
            /*GPSのupdate を止める*/
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.removeUpdates(this);
        }
    }

    /**************************************************************/
    /*タイトル :GPS設定画面表示                                   */
    /*引数     :無し                                              */
    /*戻り値   :無し                                              */
    /**************************************************************/
    private void enableLocationSettings() {
        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);   /*GPSの設定画面のセット*/
        startActivity(settingsIntent);  /*GPSの設定画面へ移動*/
    }

    /*onStartの後*/
    @Override
    protected void onResume() {
        super.onResume();

        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (locationManager != null && gpsEnabled) {
            onGPS();    /*取得を開始*/
        } else {
            Streetview.setText("GPSをONに\nしてください");
            /*Log.d("CAMP_MainActivity", "startGPS_エラー");*/
        }
    }

    /*onStopの後*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*Log.d("CAMP_MainActivity","onDestroy");*/
        unregisterReceiver(myreceiver); /*レシーバの登録を解除*/
    }

    /*右上メニュー*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*Log.d("CAMP_MainActivity","onCreateOptionsMenu");*/
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /*右上メニュークリック*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*Log.d("CAMP_MainActivity","onOptionsItemSelected");*/
        switch (item.getItemId()) {
              case R.id.menu_main_layout:
                final String[] items = {"ポップ", "シンプル"};
                int defaultItem = 0;    /*デフォルトでチェックされているアイテム*/
                final List<Integer> checkedItems = new ArrayList<>();   /*選択リスト*/
                checkedItems.add(defaultItem);
                new AlertDialog.Builder(MainActivity.this)
                    .setTitle("デザインの変更")
                    .setSingleChoiceItems(items, defaultItem, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            checkedItems.clear();
                            checkedItems.add(which);
                            if(checkedItems.get(0)==1){
                                Intent intent = new Intent(MainActivity.this, MainSimpleActivity.class);    /*シンプルアクティビティ*/
                                /*次画面のアクティビティ起動*/
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

    /*GPS設定*/
    /*結果の受け取り*/
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        /*Log.d("GPS",Integer.toString(requestCode) +Integer.toString(grantResults[0]) );*/
        if (requestCode == 1000) {
            /*使用が許可された*/
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                /*Log.d("CAMP_Permission","checkSelfPermission true");*/
                startGPS();

            } else {
                /*それでも拒否された時の対応*/
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

        /*Log.d("CAMP_MainActivity","onLocationChanged = " + lat);*/
        /*Log.d("CAMP_MainActivity","onLocationChanged = " + lon);*/

        url = "http://api.openweathermap.org/data/2.5/forecast" /*天気API用URLセット*/
                + "?lat=" + String.valueOf(lat) /*緯度*/
                + "&lon=" + String.valueOf(lon) /*経度*/
                + "&cnt=2"  /*現在と3時間後を取得*/
                + "&APPID=" + pass; /*passkeyの設定*/

        jsonLoader = new JsonLoader(url);   /*APIへアクセス*/
        thread = new Thread(this);  /*スレッドのセット*/
        thread.start(); /*スレッドスタート*/

        Street = weather.getAddress(getApplicationContext(),lat,lon);   /*現在地の市区町村を取得*/

        Streetview.setText(Street); /*市区町村の表示*/

        /*Log.d("CAMP_MainActivity","onLocationChanged = " + Street);*/
    }

    Handler handler = new Handler();    /*HandlerはUIスレッドで生成する。*/
    JSONObject jsonObject;  /*JSON形式のモデルデータ宣言*/
    String[] id = new String[2];    /*現在と3時間後の天気情報ID*/
    String[] icon = new String[2];  /*現在と3時間後の天気アイコン*/

    @Override
    public void run() {
        jsonObject = jsonLoader.loadInBackground(); /*JSONのセット*/
        handler.post(new Runnable() {
            @Override
            public void run() {
                JSONArray lists;    /*JSONの配列をリスト形式にする*/
                if(jsonObject != null) {
                    try {
                        lists = jsonObject.getJSONArray("list");    /*リストへセット*/

                        /*Log.d("TEST",jsonObject.toString(4));*/
                        /*2回分の情報を取得*/
                        for (int i = 0; i < 2; i++) {
                            try {
                                JSONObject list = lists.getJSONObject(i);   /*リストから情報を取得*/
                                JSONArray weatherlist = list.getJSONArray("weather");   /*天気情報のリストをセット*/
                                JSONObject weather = weatherlist.getJSONObject(0);  /*天気情報を取得*/

                                id[i] = weather.get("id").toString();   /*天気情報IDの取得*/
                                icon[i] = weather.get("icon").toString();   /*天気アイコンIDの取得*/

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    /*現在*/
                    CurrentWeather.setText(weather.Getweather(id[0]));  /*天気情報IDから該当する天気を取得表示*/
                    Crrenticon.setImageResource(weather.Getweathericon(icon[0]));   /*天気アイコンIDから該当する天気アイコンを取得表示*/

                    /*3時間後*/
                    FutureWeather.setText(weather.Getweather(id[1]));   /*天気情報IDから該当する天気を取得表示*/
                    Futureicon.setImageResource(weather.Getweathericon(icon[1]));   /*天気アイコンIDから該当する天気アイコンを取得表示*/
                }
            }
        });
        thread = null;  /*スレッドの削除*/
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

    /*ブロードキャストレシーバ*/
    public class Receiver extends BroadcastReceiver {
        String[] text = new String[3];  /*テキスト*/
        int[] color = new int[3];   /*色*/
        String comment="" ; /*コメント*/
        int icon_id=2;  /*アイコンID*/
        private String temp_string="";  /*温度*/
        private String humid_string=""; /*湿度*/

        /*横幅のみ画面サイズに変更*/
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            text[0] = bundle.getString("index1");   /*危険度*/
            text[1] = bundle.getString("index2");   /*温度*/
            text[2] = bundle.getString("index3");   /*湿度*/
            color[0] = bundle.getInt("colorR"); /*赤*/
            color[1] = bundle.getInt("colorG"); /*緑*/
            color[2] = bundle.getInt("colorB"); /*青*/
            comment=bundle.getString("comment");   /*コメント*/
            icon_id=bundle.getInt("icon_id");   /*アイコンID*/

            /*Log.d("CAMP_MainActivity", String.format("onReceive=%s, %s, %s, %s",text[0],text[1],text[2],comment));*/

            TextView index = (TextView) findViewById(R.id.index_txt);   /*危険度の表示*/
            TextView temp = (TextView) findViewById(R.id.temp_txt); /*温度の表示*/
            TextView humid = (TextView) findViewById(R.id.humid_txt);   /*湿度の表示*/
            TextView comment_t = (TextView) findViewById(R.id.text_comment);    /*コメントの表示*/

            /*ロード画像のセット*/
            ImageView Loading_gif1 = (ImageView) findViewById(R.id.Loading_gif1);
            ImageView Loading_gif2 = (ImageView) findViewById(R.id.Loading_gif2);
            ImageView Loading_gif3 = (ImageView) findViewById(R.id.Loading_gif3);

            if(index!=null) {
                index.setTextColor(Color.rgb(color[0], color[1], color[2]));    /*危険度の表示食変更*/
            }

            if(text[0]==null){
                if(Loading_gif3 != null) {
                    Loading_gif3.setVisibility(View.VISIBLE);   /*計測中GIF動画を表示*/
                }
                if(comment_t != null) {
                    comment_t.setText("");
                }
            }else{
                if(index != null) {
                    index.setText(text[0]);
                }
                if(Loading_gif3 != null){
                    Loading_gif3.setVisibility(View.INVISIBLE); /*計測中GIF動画を非表示*/
                }
                if(comment_t != null){
                    comment_t.setText(comment); /*コメントのセット*/
                }
            }

            if(text[1]==null){
                if(Loading_gif1 != null){
                    Loading_gif1.setVisibility(View.VISIBLE);   /*計測中GIF動画を表示*/
                }
                if(temp != null){
                    temp.setText("");   /*温度の非表示*/
                }
            }else{
                if(Loading_gif1 != null){
                    Loading_gif1.setVisibility(View.INVISIBLE); /*計測中GIF動画を非表示*/
                }
                if(temp != null){
                    temp_string = text[1] + "℃";
                    temp.setText(temp_string);  /*温度の表示*/
                }
            }

            if(text[2]==null){
                if(Loading_gif2 != null){
                    Loading_gif2.setVisibility(View.VISIBLE);   /*計測中GIF動画を表示*/
                }
                if(humid != null){
                    humid.setText("");  /*湿度の非表示*/
                }
            }else{
                if(Loading_gif2 != null){
                    Loading_gif2.setVisibility(View.INVISIBLE); /*計測中GIF動画を非表示*/
                }
                if(humid != null){
                    humid_string =text[2]+"％";
                    humid.setText(humid_string);    /*湿度の表示*/
                }
            }

            /*危険度に合わせて画像の変更*/
            ImageView icon = (ImageView)findViewById(R.id.ladybug);
            if(icon != null) {
                switch (icon_id) {
                    case 0:
                        icon.setImageResource(R.drawable.cold); /*寒い*/
                        break;
                    case 1:
                        icon.setImageResource(R.drawable.cool); /*肌寒い*/
                        break;
                    case 2:
                        icon.setImageResource(R.drawable.good); /*快適*/
                        break;
                    case 3:
                        icon.setImageResource(R.drawable.warm); /*ちょっと暑い*/
                        break;
                    case 4:
                        icon.setImageResource(R.drawable.hot);  /*暑く感じる*/
                        break;
                    case 5:
                        icon.setImageResource(R.drawable.veryhot);  /*危険な暑さ*/
                        break;
                }
            }
        }
    }
}
