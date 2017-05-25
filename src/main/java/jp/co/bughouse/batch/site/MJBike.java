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
import java.util.List;
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
 * @author UU092141
 */
public class MJBike extends AbstractSite {
    
    public static void main(String[] args) throws IOException{
        MJBike mjBike = new MJBike("", 1000);
        for(String prefUrl : mjBike.getPrefectureURLList()){
            for(String shopUrl : mjBike.getShopURLList(prefUrl)){
//                System.out.println(mjBike.getShopDto(shopUrl));
                for(BikeEntity bike : mjBike.getBikeDtoList(shopUrl)){
                    System.out.println(bike);
                }
                
            }
        }
    }
    
    final static String BASE_URL = "http://www.mjbike.com";
    // ロガー宣言
    private static final Logger logger = Logger.getLogger(MJBike.class);

    public MJBike(String encode, int waitMS){
        super(encode, waitMS);
    }
    
    @Override
    public List<String> getPrefectureURLList() throws IOException {
        Document prefURLDoc = getJsoupConnection(BASE_URL + "/ubike/sch_dealersearch.asp", waitMS).get();
        List<String> prefectureURLList = new ArrayList<>();
        
        for(Element aTag : prefURLDoc.select(".footerbox_right a")){
            prefectureURLList.add(aTag.attr("href"));
        }
        return prefectureURLList;
    }

    @Override
    public List<String> getShopURLList(String prefectureURL) throws IOException {
        Document shopURLDoc = getJsoupConnection(prefectureURL, waitMS).get();
        List<String> shopURLList = new ArrayList<>();
        
        for(Element aTag : shopURLDoc.select(".bk_sch_dealerschlist_mframe1_1 a")){
            shopURLList.add(BASE_URL + "/ubike/" + aTag.attr("href"));
        }
        for(Element aTag : shopURLDoc.select(".bk_sch_dealerschlist_mframe1_2 a")){
            shopURLList.add(BASE_URL + "/ubike/" + aTag.attr("href"));
        }
        
        return shopURLList;
    }

    @Override
    public ShopEntity getShopDto(String shopUrl) throws IOException {
        Document shopDoc = getJsoupConnection(shopUrl, waitMS).get();
        
        ShopEntity shopDto = new ShopEntity();
        shopDto.setSiteName("MJBike");
        shopDto.setUrl(shopUrl);
        
        // ショップ名をHTMLから検索
        shopDto.setShopName(MyStringUtils.zenkakuToHankaku(
                shopDoc.select(".shopnamebox h1").text())
        );
        
        // 住所をHTMLから検索
        shopDto.setAddress(MyStringUtils.zenkakuToHankaku(
                shopDoc.select("#ShopData li").get(0).text())
        );
        
        // 電話番号をHTMLから検索
        shopDto.setTel(MyStringUtils.zenkakuToHankaku(
                shopDoc.select(".shoptelno").text())
        );

        return shopDto;
        
    }

    @Override
    public List<BikeEntity> getBikeDtoList(String shopUrl) throws IOException {
        String bikeUrl  = "http://www.mjbike.com/ubike/sch_stock.asp";
        String shopId   = getShopIdFromShopUrl(shopUrl);
        
        List<BikeEntity> bikeEntityList = new ArrayList<>();
        
        Document bikeDoc = getJsoupConnection(bikeUrl, waitMS).data("code", shopId).get();
System.out.println(shopId);
        for(Element table : bikeDoc.select("#BukkenTable1108")){
            BikeEntity bikeEntity = new BikeEntity();
            {
                // ショップ用URL設定
                bikeEntity.setShopUrl(shopUrl);
            }

            {
                // 画像URL
                Elements imgTags = table.select(".listphoto img");
                bikeEntity.setPicUrl(BASE_URL + imgTags.attr("src"));
            }

            {
                // メーカーと車名と詳細URL設定
                Elements aTags = table.select(".fb a");
                
                String[] elements = MyStringUtils.unEscapeHtml(aTags.attr("title")).split(" ");
                bikeEntity.setMaker(elements[0]);   // 要素1はメーカー名
                bikeEntity.setName(elements[1]);    // 要素2は車名
                bikeEntity.setUrl(aTags.attr("href"));
            }
            
            {
                // 車検の有無
                Elements tags = table.select(".f11");
                // MJBikeには特殊文字が2つある(&nbsp|&nbsp;)ため、unEscapeHtmlだけだと変換できない
                String[] elements = MyStringUtils.unEscapeHtml(tags.text())
                                        .replaceAll("&nbsp", " ")
                                        .split(" ");
                // 1カラム目：メーカー
                // 2カラム目：排気量
                // 3カラム目：カラー
                bikeEntity.setColor(elements.length > 2 ? elements[2] : null);
                // 4カラム目：修復歴の有無
                // 5カラム目：保障の有無
                // 6カラム目：整備含 or 別
                // 7カラム目：車検有無
                System.out.println(elements.length > 6 ? getInspection(elements[6]) : null);
                
                
                
//System.out.println(MyStringUtils.unEscapeHtml(tags.text()));
            }
            
            {
                // 販売価格
            }

            {
                // 走行距離
            }
            
            {
                // 登録年数
            }

//            System.out.println(table);
        }
        
        
        return bikeEntityList;
    }

    @Override
    protected Integer getDistance(String distanceStr) {

        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected Integer getYear(String yearStr) {
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected Integer getPrice(String priceStr) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected Integer getInspection(String inspectionStr) {
        // 車検(30/11)
        Integer inspection = null;
        Matcher inspectionMatcher = Pattern.compile("車検\\(([0-9]+/[0-9]+)\\)").matcher(inspectionStr);
        if(inspectionMatcher.find()){
            String[] yearAndMonth = inspectionMatcher.group(1).split("/");
            Integer year = MyStringUtils.heiseiConvertAD(Integer.parseInt(yearAndMonth[0]));
            Integer month = Integer.parseInt(yearAndMonth[1]);
            
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
        Matcher makerMatcher = Pattern.compile("sch_shop_code-([0-9]+).html").matcher(shopUrl);
        return makerMatcher.find() ? makerMatcher.group(1) : null;
    }    
}
