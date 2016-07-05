package com.android.camp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity implements ServiceConnection {

    private ListView deviceList;
    private TextView usageTextView;

    ArrayList<String> list;
    //ArrayAdapterオブジェクト生成
    private ArrayAdapter<String> adapter;

    private SwitchCompat SettingsSwitch;
    private Intent BeaconGetIntent;
    private boolean serviceStart = false;
    Receiver myreceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setSupportActionBar((Toolbar) findViewById(R.id.settings_toolbar));
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        deviceList = (ListView) findViewById(R.id.deviceList);
        usageTextView = (TextView) findViewById(R.id.usageTextView);

        //list設定
        list = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list)
        {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView)super.getView(position, convertView, parent);
                view.setTextSize( 30 );
                return view;
            }
        };//List追加用アダプター

        SettingsSwitch = (SwitchCompat)findViewById(R.id.settings_switch);

        //サービス起動スイッチ
        SettingsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startScanning();
                } else {
                    stopScanning();
                }
            }
        });

        //deviceList.setAdapter();
        deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //Listクリック処理
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // save the target device
                int setid = Integer.parseInt(list.get(position));
                //String setid = list.get(position);
                //Log.d("TEST",String.valueOf(setid));

                BeaconGetIntent.putExtra("SETID",setid);
                bindService(BeaconGetIntent,SettingsActivity.this,0);
                unbindService(SettingsActivity.this);

                finish();
            }
        });

        //onServiceDisconnected(null);

        //Intent intent = getIntent();
        //int id = intent.getIntExtra("DATA",0);
        //Log.d("TEST",Integer.toString(id));

        BeaconGetIntent = new Intent(this, BeaconGetService.class);

        myreceiver = new Receiver();
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction("GETID");
        registerReceiver(myreceiver, intentfilter);

        serviceStart = BeaconGetService.isStarted();
        if(serviceStart) {
            stopService(BeaconGetIntent);
            Log.d("TEST_SettingsActivity","SettingsSwitch");
            SettingsSwitch.setChecked(true);
        }
    }

    public void onResume() {
        super.onResume();
        //startRequestBeacon();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myreceiver);
        //stopService(BeaconGetIntent);
        Log.d("TEST","SettingsActivity_onDestroy");
    }


    //戻るメニュークリック処理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //戻るボタンクリック
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("TEST_SettingsActivity","onKeyDown");
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            // 戻るボタンの処理
            finish();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    //ID取得スイッチON
    private void startScanning() {
        Log.d("TEST_SettingsActivity","startScanning");

        startService(BeaconGetIntent);

        //テキストを詰めて消してListを表示
        deviceList.setVisibility(View.VISIBLE);
        usageTextView.setVisibility(View.GONE);
    }

    //ID取得スイッチOFF
    private void stopScanning() {
        Log.d("TEST_SettingsActivity","stopScanning");
        stopService(BeaconGetIntent);

        //Listを詰めて消してテキストを表示
        deviceList.setVisibility(View.GONE);
        usageTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d("TEST","サービスに接続しました");
        //_messenger = new Messenger(service);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d("TEST","サービスから切断しました");
    }

    //サービスからデータの受け取り
    public class Receiver extends BroadcastReceiver {
        int Id;
        @Override
        public void onReceive(Context context , Intent intent){
            Bundle bundle = intent.getExtras();
            Id = bundle.getInt("index");
            //Log.d("TEST",Integer.toString(Id));

            String setid = String.format("%d", Id);

            if(list.indexOf(setid) == -1) {
                list.add(setid);
            }
            //Adapterセット
            deviceList.setAdapter(adapter);
        }
    }
}
