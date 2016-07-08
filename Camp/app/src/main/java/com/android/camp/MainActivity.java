package com.android.camp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements ServiceConnection,LocationListener {

    private Intent SettingsIntent;
    private Receiver myreceiver;

    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("TEST_MainActivity","onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton imagebutton1= (ImageButton)findViewById(R.id.image_button1);
        ImageButton imagebutton2= (ImageButton)findViewById(R.id.image_button2);
        ImageButton imagebutton3= (ImageButton)findViewById(R.id.image_button3);

        WindowManager wm = getWindowManager();

        // LocationManager インスタンス生成
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        //終了

        ImageButton image_button_choice =(ImageButton)findViewById(R.id.image_button_choice);

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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        }
        else{
            locationStart();
        }

    }

    private void locationStart(){
        Log.d("debug","locationStart()");

        // LocationManager インスタンス生成
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            // GPSを設定するように促す
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
            Log.d("debug", "gpsEnable, startActivity");
        } else {
            Log.d("debug", "gpsEnabled");
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);

            Log.d("debug", "checkSelfPermission false");
            return;
        }
        try{
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,this);
        }catch (Exception e) {

        }
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

                startActivityForResult(SettingsIntent,RESULTCODE);
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

    /*GPS設定*/
    //取得
    @Override
    public void onLocationChanged(Location location) {
        location.getLatitude();
        Log.d("TEST",String.valueOf());
        Log.d("TEST",String.valueOf(location.getLongitude()));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

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
