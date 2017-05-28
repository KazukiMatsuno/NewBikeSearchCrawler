/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.co.bughouse.batch.site;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jp.co.bughouse.batch.common.MyStringUtils;
import jp.co.bughouse.batch.entity.BikeEntity;
import jp.co.bughouse.batch.entity.ShopEntity;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author user
 */
public class GooBike extends AbstractSite {
    
    final static String BASE_URL = "http://www.goobike.com";
    final static String ZAIKO_URL = "zaiko.html";
    final static String ZAIKO_BASE_URL = "/cgi-bin/search/zaiko_bike.cgi";
    // ロガー宣言
    private static final Logger logger = Logger.getLogger(GooBike.class);

    public GooBike(String encode, int waitMS) {
        super(encode, waitMS);
    }

    @Override
    public Set<String> getPrefectureURLSet() throws IOException {
        logger.info("START");
        Document prefURLDoc = getJsoupConnection(BASE_URL + "/shop/", waitMS).get();
        Set<String> prefectureURLSet = new HashSet<>();
        
        for(Element aTag : prefURLDoc.select("#contents a")){
            prefectureURLSet.add(BASE_URL + aTag.attr("href"));
            logger.debug(BASE_URL + aTag.attr("href"));
        }
        logger.info("END");
        return prefectureURLSet;
    }

    @Override
    public Set<String> getShopURLSet(String prefectureURL) throws IOException {
        logger.info("START");
        Set<String> shopURLSet = new HashSet<>();
        Document shopURLDoc = getJsoupConnection(prefectureURL, waitMS).get();
        
        for(Element aTag : shopURLDoc.select("p.zaiko a")){
            shopURLSet.add(BASE_URL + aTag.attr("href").replace(ZAIKO_URL, ""));
            logger.debug(BASE_URL + aTag.attr("href").replace(ZAIKO_URL, ""));
        }
        
        logger.info("END");
        return shopURLSet;
    }

    @Override
    public ShopEntity getShopDto(String shopUrl) throws IOException {
        logger.info("START");
        ShopEntity shopDto = new ShopEntity();
        shopDto.setUrl(shopUrl);
        shopDto.setSiteName("GooBike");

        Document shopDoc = getJsoupConnection(shopUrl, waitMS).get();
        // ショップタイトル
        shopDto.setShopName(shopDoc.select("p.title").get(0).text());
        
        Elements ddTags = shopDoc.select("#shop_contents dd");
        // ショップ住所
        String[] postCodeAndAddress =   MyStringUtils.zenkakuToHankaku(
                                            MyStringUtils.unEscapeHtml(ddTags.get(1).text()                                            )
                                        ).split("   ");
        
        shopDto.setAddress(postCodeAndAddress[1]);
        // ショップ電話番号(0 = TEL, 1 = 000-0000-0000, 2 = FAX, 3 = null or 000-0000-0000
        String[] telAndFax = ddTags.get(2).text().split(" ");
        shopDto.setTel(telAndFax[1]);
        
        logger.info("END");
        return shopDto;
    }

    @Override
    public List<BikeEntity> getBikeDtoList(String shopUrl) throws IOException {
        logger.info("START");
        List<BikeEntity> bikeDtoList = new ArrayList<>();
        String shopId   = getShopIdFromShopUrl(shopUrl);
        logger.info("ショップID : " + shopId);
        // offsetは0スタート
        for (Integer offset = 0;; offset++) {
            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("offset", offset.toString());
            dataMap.put("client_id", shopId);
            
            Document bikeDoc = getJsoupConnection(BASE_URL + ZAIKO_BASE_URL, waitMS, dataMap).get();

            Elements tags = bikeDoc.select("table[width=100%][border=0][cellspacing=0][cellpadding=0] tr[bgcolor=#FFFFFF]");
                        
            /* 奇数版目は下記のような情報で不必要な項目のため、取得しない
                <td valign='top' nowrap bgcolor='#ececec' style="height:40px;"><span class="mj">販売店コメント</span>
            */

            // 0から始まって2ずつ増える
            for(int i = 0; i < tags.size(); i = i + 2){
                BikeEntity bikeEntity = new BikeEntity();
                {
                    // ショップ用URL設定
                    bikeEntity.setShopUrl(shopUrl);
                }
                {
                    // 画像URL
                    Elements imgTags = tags.get(i).select("td[width=64][rowspan=2] img");
                    bikeEntity.setPicUrl(imgTags.get(0).attr("src"));
                    
                }
                {
                    Elements tdTags = tags.get(i).select("td[valign=top]");
                    // 値段
                    String priceStr = tdTags.get(0).select(".price").text();
                    bikeEntity.setPrice(getPrice(priceStr));

                    // バイク用URLとメーカーとバイク名
                    Element aTag = tdTags.select("li a").get(0);
                    bikeEntity.setUrl(BASE_URL + aTag.attr("href"));
                    
                    String[] makerAndName = aTag.text().split(" ");
                    // メーカーを設定
                    bikeEntity.setMaker(MyStringUtils.zenkakuToHankaku(makerAndName[0]));
                    // バイク名を設定
                    bikeEntity.setName(MyStringUtils.zenkakuToHankaku(makerAndName[1]));

                    // 登録年数
                    bikeEntity.setYear(getYear(tdTags.get(4).text()));
                    
                    // 色
                    bikeEntity.setColor(tdTags.get(5).text());
                    
                    // 走行距離
                    bikeEntity.setDistance(getDistance(tdTags.get(6).text()));
                    
                    // 車検
                    bikeEntity.setInspection(getInspection(tdTags.get(7).text()));
                }
                
                bikeDtoList.add(bikeEntity);
            }
            
            // リストに続きがあるかどうかURLから判断する処理
            boolean nextFlag = false;
            for(Element aTag : bikeDoc.select("a.link2")){
                if(aTag.text().equals("次へ")){
                    nextFlag = true;
                }
            }
            if(nextFlag == false){
                break;
            }
        }
        
        
        logger.info("END");
        return bikeDtoList;
    }

    @Override
    protected Integer getDistance(String distanceStr) {
        Integer distance = null;
        Matcher distanceMatcher = Pattern.compile("([0-9,]+)Km").matcher(distanceStr);
        if(distanceMatcher.find()){
            distance = Integer.parseInt(distanceMatcher.group(1).replaceAll(",", ""));
        }
        logger.info("走行距離：" + distanceStr + " : " + distance);
        return distance;
    }

    /*
        新車の場合は0, 中古車の場合は走行距離を返す
    */
    @Override
    protected Integer getYear(String yearStr) {
        Integer year = null;
        Matcher newBikeMatcher = Pattern.compile("新車").matcher(yearStr);
        Matcher yearMatcher = Pattern.compile("([0-9]+)年").matcher(yearStr);
        if(newBikeMatcher.find()){
            // 新車の場合
            return 0;
        }else if(yearMatcher.find()) {
            // 中古車の場合
            year = Integer.parseInt(yearMatcher.group(1));
        }

        // TODO: ロガーでyearStrを出力する
        logger.info("登録年数 : " + yearStr + " : " + year);
        return year;
    }

    @Override
    protected Integer getPrice(String priceStr) {
        Integer price = null;
        Matcher priceMatcher = Pattern.compile("([0-9\\.]+)(万|千)円").matcher(priceStr);
        if (priceMatcher.find()) {
            String priceMatchStr = priceMatcher.group(1);
            switch (priceMatcher.group(2)) {
                case "万":
                    price = (int) (Float.parseFloat(priceMatchStr) * 10000);
                    break;
                case "千":
                    price = (int) (Float.parseFloat(priceMatchStr) * 1000);
                    break;
            }
        }
        logger.info("価格 : " + priceStr + " : " + price);
        return price;
    }

    @Override
    protected Integer getInspection(String inspectionStr) {
        // 検30.8
        Integer inspection = null;
        String pattern = "検([1-9][0-9])\\.([1][0-2]|[1-9])";
        //年数を取得
        Matcher inspectionMatcher = Pattern.compile(pattern).matcher(inspectionStr);
        if (inspectionMatcher.find()) {
            Integer year = MyStringUtils.heiseiConvertAD(
                Integer.parseInt(inspectionMatcher.group(1))
            );

            Integer month = Integer.parseInt(inspectionMatcher.group(2));
            Calendar inspectionDate = Calendar.getInstance();
            inspectionDate.set(year, month - 1, 1);
            if (MyStringUtils.isExpiredInspection(inspectionDate)) {
                inspection = Integer.parseInt(new SimpleDateFormat("yyyyMM").format(inspectionDate.getTime()));
            }
        }
        // TODO:ロガーでinspectionStrを出力する
        logger.info("車検 : " + inspectionStr + " : " + inspection);
        return inspection;
    }

    private String getShopIdFromShopUrl(String shopUrl){
        Matcher makerMatcher = Pattern.compile("client_([0-9]+)").matcher(shopUrl);
        return makerMatcher.find() ? makerMatcher.group(1) : null;
    }    
}
