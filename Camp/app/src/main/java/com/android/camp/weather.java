package com.android.camp;

import java.util.HashMap;

/**
 * Created by shimizu.yuhei on 2016/07/07.
 */
public class weather {
    public HashMap<String,String> weathermap = new HashMap<String,String>();

    public void weather()
    {
        weathermap.put("200","小雨と雷雨");
        weathermap.put("201","雨と雷雨");
        weathermap.put("202","大雨と雷雨");
        weathermap.put("210","光雷雨");
        weathermap.put("211","雷雨");
        weathermap.put("212","重い雷雨");
        weathermap.put("221","ぼろぼろの雷雨");
        weathermap.put("230","小雨と雷雨");
        weathermap.put("231","霧雨と雷雨");
        weathermap.put("232","重い霧雨と雷雨");

        weathermap.put("300","光強度霧雨");
        weathermap.put("301","霧雨");
        weathermap.put("302","重い強度霧雨");
        weathermap.put("310","光強度霧雨の雨");
        weathermap.put("311","霧雨の雨");
        weathermap.put("312","重い強度霧雨の雨");
        weathermap.put("313","にわかの雨と霧雨");
        weathermap.put("314","重いにわかの雨と霧雨");
        weathermap.put("321","にわか霧雨");

        weathermap.put("500","小雨");
        weathermap.put("501","適度な雨");
        weathermap.put("502","重い強度の雨");
        weathermap.put("503","非常に激しい雨");
        weathermap.put("504","極端な雨");
        weathermap.put("511","雨氷");
        weathermap.put("520","光強度のにわかの雨");
        weathermap.put("521","にわかの雨");
        weathermap.put("522","重い強度にわかの雨");
        weathermap.put("531","不規則なにわかの雨");

        weathermap.put("600","小雪");
        weathermap.put("601","雪");
        weathermap.put("602","大雪");
        weathermap.put("611","みぞれ");
        weathermap.put("612","にわかみぞれ");
        weathermap.put("615","光雨と雪");
        weathermap.put("616","雨や雪");
        weathermap.put("620","光のにわか雪");
        weathermap.put("621","にわか雪");
        weathermap.put("622","重いにわか雪");

        weathermap.put("701","ミスト");
        weathermap.put("711","煙");
        weathermap.put("721","ヘイズ");
        weathermap.put("731","砂、ほこり旋回する");
        weathermap.put("741","霧");
        weathermap.put("751","砂");
        weathermap.put("761","ほこり");
        weathermap.put("762","火山灰");
        weathermap.put("771","スコール");
        weathermap.put("781","竜巻");

        weathermap.put("800","晴天");
        weathermap.put("801","薄い雲");
        weathermap.put("802","雲");
        weathermap.put("803","曇りがち");
        weathermap.put("804","厚い雲");

        weathermap.put("900","竜巻");
        weathermap.put("901","熱帯暴風雨");
        weathermap.put("902","ハリケーン");
        weathermap.put("903","寒い");
        weathermap.put("904","暑い");
        weathermap.put("905","風が強い");
        weathermap.put("906","雹");
    }
}
