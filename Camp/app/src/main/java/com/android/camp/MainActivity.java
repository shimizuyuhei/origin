package com.android.camp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements ServiceConnection {

    private Intent BeaconGetIntent;
    private Intent SettingsIntent;
    private Receiver myreceiver;

    private static final class CampMenu {
        public final String title;
        public final String caption;
        public final Uri uri;

        public CampMenu(String title, String caption, String uri) {
            this.title = title;
            this.caption = caption;
            this.uri = Uri.parse(uri);
        }
    }

    private static final CampMenu[] menus = {
            new CampMenu("・テント　場所選び", "テント設営の場所選びのコツが書かれたページです。", "http://variousinfo.biz/archives/272.html"),
            new CampMenu("・テント　張り方", "テントの張り方を解説しているページです。", "http://www.tabikaze.net/CAMP-SUSUME/susume-03.html"),
            new CampMenu("・テント　雨の対策", "雨の日のテント運用の解説ページです。", "http://nandemoarikayo.com/725.html"),
            new CampMenu("・キャンプ料理　クックパッド", "クックパッドの「キャンプ料理」の検索ページです。", "http://cookpad.com/category/1760"),
            new CampMenu("・キャンプ料理　アウトドアレシピ", "おすすめのアウトドア料理のレシピをまとめたページです。", "http://camphack.nap-camp.com/762"),
            new CampMenu("・キャンプ料理　ホイル焼き", "たき火で出来るホイル焼きレシピのページです。", "http://camphack.nap-camp.com/889"),
            new CampMenu("・キャンプ料理　煮込み料理", "キャンプにおすすめの煮込み料理のページです。", "http://camphack.nap-camp.com/945"),
            new CampMenu("・蜂刺され　対処", "蜂に刺された場合の対処を解説したページです。", "http://t-meister.jp/hachi/lab/sasaretara"),
            new CampMenu("・ブヨ刺され　対処", "ブヨに刺された場合の対処を解説したページです。", "http://kenkoucheck-navi.com/%E3%83%96%E3%83%A8%E3%81%AB%E5%88%BA%E3%81%95%E3%82%8C%E3%81%9F%E8%B7%A1%E3%81%AE%E5%87%A6%E7%BD%AE/"),
            new CampMenu("・蛇　かまれた場合の対処", "蛇にかまれた場合の種類別対策ページです。", "http://www.asobon.net/c3/con5_4.html"),
            new CampMenu("・キャンプお役立ち情報", "キャンプに役立つ情報です。", "http://google.com/")
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("TEST_MainActivity","onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(new ArrayAdapter<CampMenu>(this, R.layout.item_menu, Arrays.asList(menus)) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View row;
                if(convertView == null){
                    LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    row = inflater.inflate(R.layout.item_menu, null);
                }else{
                    row = convertView;
                }
                CampMenu menu = menus[position];
                ((TextView) row.findViewById(R.id.title)).setText(menu.title);
                ((TextView) row.findViewById(R.id.caption)).setText(menu.caption);

                return row;
            }
        });

        //ニュークリック
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("TEST_MainActivity","listView_onItemClick");
                //startActivity(new Intent(Intent.ACTION_VIEW, menus[position].uri));
                Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
                intent.putExtra("url", menus[position].uri.toString());
                startActivity(intent);
            }
        });

        BeaconGetIntent = new Intent(this, BeaconGetService.class);
        SettingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);

        //startService(BeaconGetIntent);

        myreceiver = new Receiver();
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction("action");
        registerReceiver(myreceiver, intentfilter);    }

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

    public class Receiver extends BroadcastReceiver {
        String[] text = new String[3];
        int[] color = new int[4];

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

            Log.d("TEST_MainActivity", String.format("onReceive=%s, %s, %s",color[1],color[2],color[3]));
            RelativeLayout rl = (RelativeLayout) findViewById(R.id.relativelayout);
            rl.setBackgroundColor(Color.argb(color[0],color[1],color[2],color[3]));

            TextView t1 = (TextView) findViewById(R.id.textView);
            TextView t2 = (TextView) findViewById(R.id.textView2);
            TextView t3 = (TextView) findViewById(R.id.textView3);
            t1.setText(text[0]);
            t2.setText(text[1]);
            t3.setText(text[2]);
        }
    }
}
