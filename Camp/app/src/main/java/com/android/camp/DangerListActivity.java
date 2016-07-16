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

public class DangerListActivity extends AppCompatActivity{

    /*表示させるリスト内容*/
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

        /*Log.d("CAMP_DangerListActivity","onCreate");*/
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
                    /*Log.d("CAMP_DangerListActivity", "listView_onItemClick");*/
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
        /*Log.d("CAMP_DangerListActivity","onKeyDown");*/
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
            im.setImageResource(R.drawable.bee);  /*イメージ画像のセット*/
        }
        /*Log.d("CAMP_DangerListActivity","onResume");*/
    }
}
