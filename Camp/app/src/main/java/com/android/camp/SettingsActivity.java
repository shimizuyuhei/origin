package com.android.camp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingsActivity extends AppCompatActivity implements ServiceConnection{

    private ListView deviceList;
    private TextView usageTextView;

    ArrayList<String> list;
    //ArrayAdapterオブジェクト生成
    private ArrayAdapter<String> adapter;
    private SharedPreferences id_pref; //プリファレンス
    private SwitchCompat SettingsSwitch;
    private Intent BeaconGetIntent;
    private boolean serviceStart = false;
    Receiver myreceiver;
    private int setidparse;
    private int index;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        id_pref = PreferenceManager.getDefaultSharedPreferences(this); //プリファレンスの取得

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

                int setid=0;
                // save the target device
                //int setid = Integer.parseInt(list.get(position));
                //String setid = list.get(position);
                try
                {
                    setid = Integer.parseInt(list.get(position));
                } catch (Exception e) {
                    String setidstr = list.get(position);
                    String[] str = setidstr.split("/", 0);
                    setid = Integer.valueOf(str[1]);
                }

                //Log.d("TEST",String.valueOf(setid));

               BeaconGetIntent.putExtra("SETID",setid);
                bindService(BeaconGetIntent,SettingsActivity.this,0);
                unbindService(SettingsActivity.this);
                setResult(1);
                finish();
            }
        });
        deviceList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //テキスト入力を受け付けるビューを作成します。

                try
                {
                    setidparse = Integer.parseInt(list.get(position));
                    index = position;

                } catch (Exception e) {
                    String setidstr = list.get(position);
                    String[] str = setidstr.split("/", 0);
                    setidparse = Integer.valueOf(str[1]);
                    index = position;
                }

                final EditText editView = new EditText(SettingsActivity.this);
                new AlertDialog.Builder(SettingsActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setTitle("ボードの表示名を変更します")
                        //setViewにてビューを設定します。
                        .setView(editView)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //入力した文字をトースト出力する
                                list.remove(index);
                                list.add(editView.getText().toString()+"/"+setidparse);

                                //Adapterセット
                                deviceList.setAdapter(adapter);
                                Toast.makeText(SettingsActivity.this,
                                        editView.getText().toString()+"に変更しました。",
                                        Toast.LENGTH_LONG).show();
                                SharedPreferences.Editor editor = id_pref.edit();
                                editor.putString(String.valueOf(setidparse),editView.getText().toString());
                                editor.commit();
                            }
                        })
                        .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        })
                        .show();
                return true;
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
            //stopService(BeaconGetIntent);
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
                setResult(0);
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
            setResult(0);
            finish();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    //ID取得スイッチON
    private void startScanning() {
        Log.d("TEST_SettingsActivity","startScanning");

        if(!serviceStart) {
            startService(BeaconGetIntent);
        }

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
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);

            String useId = pref.getString(setid,setid);
            try
            {
                int i = Integer.parseInt(useId);
                if(list.indexOf(useId) == -1) {
                    list.add(useId);
                }
            } catch (Exception e) {
                if(list.indexOf(useId+"/"+setid) == -1) {
                    list.add(useId+"/"+setid);
            }
            }

            //Adapterセット
            deviceList.setAdapter(adapter);
        }
    }
}
