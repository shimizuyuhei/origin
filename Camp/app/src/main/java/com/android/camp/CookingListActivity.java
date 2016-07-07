package com.android.camp;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.ListView;
import android.widget.TextView;

import java.util.Arrays;

/**
 * Created by USER on 2016/07/06.
 */
public class CookingListActivity extends AppCompatActivity{
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
            new CampMenu("・キャンプ料理　クックパッド", "クックパッドの「キャンプ料理」の検索ページです。", "http://cookpad.com/category/1760"),
            new CampMenu("・キャンプ料理　アウトドアレシピ", "おすすめのアウトドア料理のレシピをまとめたページです。", "http://camphack.nap-camp.com/762"),
            new CampMenu("・キャンプ料理　ホイル焼き", "たき火で出来るホイル焼きレシピのページです。", "http://camphack.nap-camp.com/889"),
            new CampMenu("・キャンプ料理　煮込み料理", "キャンプにおすすめの煮込み料理のページです。", "http://camphack.nap-camp.com/945"),
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
