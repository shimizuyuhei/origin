package com.android.camp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 2016/07/08.
 */
public class MainSimpleActivity extends AppCompatActivity implements ServiceConnection {

    private Intent BeaconGetIntent;
    private Intent SettingsIntent;
    private Receiver myreceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("TEST_MainActivity","onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        ImageButton imagebutton1= (ImageButton)findViewById(R.id.image_button1);
        ImageButton imagebutton2= (ImageButton)findViewById(R.id.image_button2);
        ImageButton imagebutton3= (ImageButton)findViewById(R.id.image_button3);
        ImageView iv=(ImageView)findViewById(R.id.ladybug);


        setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));

        BeaconGetIntent = new Intent(this, BeaconGetService.class);
        SettingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);

        LinearLayout l2=(LinearLayout)findViewById(R.id.weather_layout2);

        //startService(BeaconGetIntent);

        myreceiver = new Receiver();
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction("action");
        registerReceiver(myreceiver, intentfilter);

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(SettingsIntent,RESULTCODE);
            }

        });

        imagebutton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainSimpleActivity.this, CookingListActivity.class);
                // 次画面のアクティビティ起動
                startActivity(intent);
            }

        });
        imagebutton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainSimpleActivity.this, PreparationListActivity.class);
                // 次画面のアクティビティ起動
                startActivity(intent);
            }

        });
        imagebutton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainSimpleActivity.this, DangerListActivity.class);
                // 次画面のアクティビティ起動
                startActivity(intent);
            }

        });

        l2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }

        });

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

               // startActivityForResult(SettingsIntent,RESULTCODE);
                return true;
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
                            }
                        })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!checkedItems.isEmpty()) {
                                    // Log.d("checkedItem:", "" + checkedItems.get(0));
                                    if(checkedItems.get(0)==0){
                                        Intent intent = new Intent(MainSimpleActivity.this, MainActivity.class);
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

    public class Receiver extends BroadcastReceiver {
        String[] text = new String[3];
        int[] color = new int[4];
        String comment="" ;
        int id=2;
        String[] draw_ladybug={"@drawable/cold2",
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

            TextView t1 = (TextView)findViewById(R.id.textView);
            TextView t2 = (TextView)findViewById(R.id.textView2);
            TextView t3 = (TextView)findViewById(R.id.textView3);
            TextView tc = (TextView)findViewById(R.id.text_comment);
            t1.setText(text[0]);
            t2.setText(text[1]);
            t3.setText(text[2]);
            tc.setText(comment);

            ImageView iv = (ImageView)findViewById(R.id.ladybug);
            switch (id){
                case 0:
                    iv.setImageResource(R.drawable.cold2);
                    break;
                case 1:
                    iv.setImageResource(R.drawable.cool2);
                    break;
                case 2:
                    iv.setImageResource(R.drawable.good2);
                    break;
                case 3:
                    iv.setImageResource(R.drawable.warm2);
                    break;
                case 4:
                    iv.setImageResource(R.drawable.hot2);
                    break;
                case 5:
                    iv.setImageResource(R.drawable.veryhot2);
                    break;

            }




        }
    }
}
