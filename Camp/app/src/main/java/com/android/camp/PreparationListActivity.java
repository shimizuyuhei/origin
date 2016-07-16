package com.android.camp;

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

public class PreparationListActivity extends AppCompatActivity {

    /*表示させるリスト内容*/
    private static final CampMenu[] menus = {
            new CampMenu("・事前準備", "必需品を紹介したぺージです。", "http://www.geocities.jp/hmrmyamada/camp/campdougu.html"),
            new CampMenu("・事前準備(家族向け)", "家族でキャンプへ行くときの必需品を紹介したページです。", "http://www.sohappydays.net/archives/3257"),
            new CampMenu("・持ち物チェックリスト", "持ち物チェックリストページです。", "http://www.ne.jp/asahi/kobe/yanase/camplist.htm"),
            new CampMenu("・キャンプのマナー", "キャンプの心がけを紹介したページです。", "http://camphack.nap-camp.com/227"),
            new CampMenu("・女性向け持ち物","女性がキャンプへ行くときにあると便利なものを紹介したページです。","http://shittoku.xyz/archives/2591.html"),
            new CampMenu("・100均で揃う!キャンプの持ち物","100均で買える便利グッズを紹介したページです。","http://camphack.nap-camp.com/406"),
            new CampMenu("・全国のキャンプ場を検索","全国のキャンプ場を検索できるページです。","http://www.nap-camp.com/")
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*Log.d("PreparationListActivity","onCreate");*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cookinglist);

        /*Toolbarをアクションバーとして使用*/
        setSupportActionBar((Toolbar) findViewById(R.id.settings_toolbar));
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);  /*戻るボタンのセット*/

        /*お役立ち情報リスト*/
        ListView listView = (ListView) findViewById(R.id.listView);
        if(listView!=null) {
            listView.setAdapter(new ArrayAdapter<CampMenu>(this, R.layout.item_menu, Arrays.asList(menus)) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View row;
                    if (convertView == null) {
                        /*システムサービスから取得*/
                        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        row = inflater.inflate(R.layout.item_menu, null);   /*アイテムメニューのセット*/
                    } else {
                        row = convertView;
                    }
                    CampMenu menu = menus[position];    /*リストへセット*/
                    ((TextView) row.findViewById(R.id.title)).setText(menu.title);  /*タイトルのセット*/
                    ((TextView) row.findViewById(R.id.caption)).setText(menu.caption);  /*内容のセット*/

                    return row;
                }
            });

            /*リストクリック処理*/
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    /*Log.d("PreparationListActivity", "listView_onItemClick");*/
                    Intent intent = new Intent(getApplicationContext(), WebViewActivity.class); /*内部ブラウザのセット*/
                    intent.putExtra("url", menus[position].uri.toString()); /*表示させるURLのセット*/
                    startActivity(intent);  /*ブラウザへ移動*/
                }
            });
        }
    }

    /*戻るメニュークリック処理*/
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

    /*戻るボタンクリック*/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        /*Log.d("PreparationListActivity","onKeyDown");*/
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            /*戻るボタンの処理*/
            finish();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    /*onStartの後*/
    @Override
    protected void onResume() {
        super.onResume();
        ImageView im = (ImageView)findViewById(R.id.src) ;
        if(im!=null) {
            im.setImageResource(R.drawable.gw_family);  /*イメージ画像のセット*/
        }
        /*Log.d("PreparationListActivity","onResume");*/
    }
}
