package com.android.camp;

import android.net.Uri;

/**
 * Created by shimizu.yuhei on 2016/07/16.
 */
/*お役立ち情報リスト用クラス*/
public final class CampMenu {
    public final String title;  /*タイトル*/
    public final String caption;    /*内容*/
    public final Uri uri;   /*URI*/

    /**************************************************************/
    /*タイトル :お役立ち情報リスト化                              */
    /*引数     :title:リストタイトル                              */
    /*引数     :caption:リスト内容                                */
    /*引数     :uri:リストURI                                     */
    /*戻り値   :無し                                              */
    /**************************************************************/
    public CampMenu(String title, String caption, String uri) {
        this.title = title;
        this.caption = caption;
        this.uri = Uri.parse(uri);
    }
}