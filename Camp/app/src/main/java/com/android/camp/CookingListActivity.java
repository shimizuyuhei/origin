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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Arrays;

/**
 * Created by USER on 2016/07/06.
 */
public class CookingListActivity extends AppCompatActivity{

    /*表示させるリスト内容*/
    private static final CampMenu[] menus = {
            new CampMenu("・キャンプ料理　クックパッド", "クックパッドの「キャンプ料理」の検索ページです。", "http://cookpad.com/category/1760"),
            new CampMenu("・キャンプ料理　アウトドアレシピ", "おすすめのアウトドア料理のレシピをまとめたページです。", "http://camphack.nap-camp.com/762"),
            new CampMenu("・キャンプ料理　ホイル焼き", "たき火で出来るホイル焼きレシピのページです。", "http://camphack.nap-camp.com/889"),
            new CampMenu("・キャンプ料理　煮込み料理", "キャンプにおすすめの煮込み料理のページです。", "http://camphack.nap-camp.com/945"),
            new CampMenu("・キャンプ料理　初心者","料理初心者にも簡単な料理のページです。","http://marumarumaru.com/310.html"),
            new CampMenu("・キャンプ料理　バーベキュー","バーベキューにおすすめな料理のページです。","http://park.ajinomoto.co.jp/recipe/corner/season/barbecue"),
            new CampMenu("・キャンプ料理　子供と作る","子供でも簡単に作れる料理のページです。","http://kosodatemama-journal.com/417.html")
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*Log.d("CAMP_CokingListActivity","onCreate");*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cookinglist);

        /*Toolbarをアクションバーとして使用*/
        setSupportActionBar((Toolbar) findViewById(R.id.settings_toolbar));
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

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
                    /*Log.d("CAMP_CokingListActivity", "listView_onItemClick");*/
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
        /*Log.d("CAMP_CokingListActivity","onKeyDown");*/
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
            im.setImageResource(R.drawable.bbq);  /*イメージ画像のセット*/
        }
        /*Log.d("CAMP_CokingListActivity","onResume");*/
    }
}
