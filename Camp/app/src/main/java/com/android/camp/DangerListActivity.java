package com.android.camp;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Arrays;

/**
 * Created by USER on 2016/07/07.
 */
public class DangerListActivity extends AppCompatActivity{


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
            new CampMenu("・この生き物に気をつけろ！", "キャンプで気を付けるべき生物を紹介したページです。", "http://news.livedoor.com/article/detail/10460394/"),
            new CampMenu("・蜂刺され　対処", "蜂に刺された場合の対処を解説したページです。", "http://t-meister.jp/hachi/lab/sasaretara"),
            new CampMenu("・ブヨ刺され　対処", "ブヨに刺された場合の対処を解説したページです。", "http://kenkoucheck-navi.com/%E3%83%96%E3%83%A8%E3%81%AB%E5%88%BA%E3%81%95%E3%82%8C%E3%81%9F%E8%B7%A1%E3%81%AE%E5%87%A6%E7%BD%AE/"),
            new CampMenu("・蛇　かまれた場合の対処", "蛇にかまれた場合の種類別対策ページです。", "http://www.asobon.net/c3/con5_4.html"),
            new CampMenu("・川で遊ぶ前に知っておきたい","川での鉄砲水の危険について紹介したページです。","http://matome.naver.jp/odai/2137090992394329301"),
            new CampMenu("・キャンプで熊に襲われないためには","キャンプで熊に襲われないよう対策するページです","http://allabout.co.jp/gm/gc/448334/"),
            new CampMenu("・気をつけるべき行動(調理編)","","http://nihon-hosyu.net/post-1703")
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d("TEST_MainActivity","onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cookinglist);

        setSupportActionBar((Toolbar) findViewById(R.id.settings_toolbar));
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);


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
        ImageView im = (ImageView)findViewById(R.id.src) ;
        im.setImageResource(R.drawable.bee);
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
        //unbindService(this);
    }
}
