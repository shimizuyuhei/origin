package com.android.camp;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

/**
 * Created by Owner on 2016/07/05.
 */
public class JsonLoader {
    private String urlText;

    /**************************************************************/
    /*タイトル :JSONの初期化                                      */
    /*引数     :無し                                              */
    /*戻り値   :無し                                              */
    /**************************************************************/
    public JsonLoader(String urlText){
        this.urlText = urlText; /*URLのセット*/
    }

    /**************************************************************/
    /*タイトル :天気APIを使用してJSONの取得                       */
    /*引数     :無し                                              */
    /*戻り値   :JSONObject:APIから受け取ったJSONを返す            */
    /**************************************************************/
    public JSONObject loadInBackground(){
        HttpURLConnection connection = null;

        try{
            /* 指定されたURLに接続し、リソースの取得要求を出す */
            /*URLの作成*/
            URL url = new URL(urlText);
            /*接続用HttpURLConnectionオブジェクト作成*/
            connection = (HttpURLConnection)url.openConnection();
            /*リクエストメソッドの設定*/
            connection.setRequestMethod("GET");
            /*接続*/
            connection.connect();
        } catch (IOException exception){
            /*処理なし*/
        }

        try{
            /* 先のURLから取得した文字列をJSON用に変換する */
            assert connection != null;  /*GET送信がうまくいったか？*/
            BufferedInputStream inputStream = new BufferedInputStream(connection.getInputStream()); /*レスポンス受信*/
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1){
                if (length > 0){
                    /*Log.d("JSON", Arrays.toString(buffer));*/
                    outputStream.write(buffer, 0, length);
                }
            }
            /*Log.d("JSON", "OK");*/
            return new JSONObject(new String(outputStream.toByteArray()));
        }
        catch (IOException exception){
            /*処理なし*/
            /*Log.d("JSON", "ERROR");*/
        }
        catch (JSONException e) {
            /*Log.d("JSON", "ERROR " + e);*/
            e.printStackTrace();
        }
        return null;
    }
}