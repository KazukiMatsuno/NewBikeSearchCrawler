/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.co.bughouse.batch.site;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
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
public class WeBike extends AbstractSite {

    // ロガー宣言
    private static final Logger logger = Logger.getLogger(WeBike.class);

    public WeBike(String encode, int waitMS) {
        super(encode, waitMS);
    }

    final static String BASE_URL = "https://moto.webike.net/shop/";

    @Override
    public Set<String> getPrefectureURLSet() throws IOException {
        Document prefURLDoc = getJsoupConnection(BASE_URL, waitMS).get();

        Set<String> prefectureURLSet = new HashSet<>();
        for (Element element : prefURLDoc.select(".mf_menu a")) {
            String href = element.attr("href");
            if (href.startsWith(BASE_URL)) {
                prefectureURLSet.add(href);
                logger.debug(href);
            }
        }
        return prefectureURLSet;
    }

    @Override
    public Set<String> getShopURLSet(String prefectureURL) throws IOException {
        Set<String> shopURLSet = new HashSet<>();

        //pdxCountは1開始～pn nextが無くなるまでインクリメントする
        for (Integer pdxCount = 1;; pdxCount++) {
            Document shopURLDoc = getJsoupConnection(prefectureURL, waitMS).data("pdx", pdxCount.toString()).get();

            for (Element element : shopURLDoc.select("div.list a")) {
                String href = element.attr("href");
                if (href.startsWith(BASE_URL)) {
                    shopURLSet.add(href);
                    logger.debug(href);
                }
            }

            //pageの中にpn nextが無い場合は検索を終了する
            if (shopURLDoc.select("a.pn.next").isEmpty()) {
                break;
            }
        }
        return shopURLSet;
    }

    @Override
    public ShopEntity getShopDto(String shopURL) throws IOException {
        Document shopDoc = getJsoupConnection(shopURL, waitMS).get();

        ShopEntity shopDto = new ShopEntity();
        shopDto.setSiteName("WeBike");
        shopDto.setUrl(shopURL);
        // 住所をHTMLから検索
        for (Element addressElement : shopDoc.select("[itemprop=address]")) {
            shopDto.setAddress(
                MyStringUtils.zenkakuToHankaku(
                    MyStringUtils.unEscapeHtml(addressElement.text()).replace(" ", "")
                )
            );
        }

        // 電話番号をHTMLから検索
        for (Element telephoneElement : shopDoc.select("[itemprop=telephone]")) {
            shopDto.setTel(
                MyStringUtils.zenkakuToHankaku(telephoneElement.text())
            );
        }

        // ショップ名をHTMLから検索
        for (Element shopNameElement : shopDoc.select("[itemprop=name]")) {
            shopDto.setShopName(
                MyStringUtils.zenkakuToHankaku(shopNameElement.text())
            );
        }

        logger.debug(shopDto);
        return shopDto;
    }

    @Override
    public List<BikeEntity> getBikeDtoList(String shopURL) throws IOException {
        List<BikeEntity> bikeDtoList = new ArrayList<>();
        //pdxCountは1開始～pn nextが無くなるまでインクリメントする
        for (Integer pdxCount = 1;; pdxCount++) {
            Document bikeDoc = getJsoupConnection(shopURL, waitMS).data("per", "100").data("pdx", pdxCount.toString()).get();
            for (Element tbody : bikeDoc.select("tbody.listset")) {
                BikeEntity bikeDto = new BikeEntity();
                bikeDto.setShopUrl(shopURL);
                {
                    Elements tdPhotoElements = tbody.select("td.photo");
                    // 画像URL
                    bikeDto.setPicUrl("https:" + tdPhotoElements.select("img").attr("src"));
                }

                {
                    Elements tdListPageElements = tbody.select("td.listpage");
                    // 本体価格
                    bikeDto.setPrice(getPrice(
                        MyStringUtils.deleteBlank(
                            tdListPageElements.get(1).select("span.plice").text()
                        )
                    ));
                    // 年式
                    bikeDto.setYear(getYear(tdListPageElements.get(2).text()));
                    // 走行距離
                    bikeDto.setDistance(getDistance(tdListPageElements.get(3).text()));
                    // 車検
                    bikeDto.setInspection(getInspection(tdListPageElements.get(4).text()));
                    // 排気量
//					System.out.println(tdListPageElements.get(5).text());
                }

                {
                    Elements tdNameElements = tbody.select("td.name");
                    // 詳細URL
                    bikeDto.setUrl(tdNameElements.select("a").attr("href"));

                    String tmp = MyStringUtils.unEscapeHtml(tdNameElements.select("a").text());
                    // メーカー
                    Matcher makerMatcher = Pattern.compile("\\[ (.*) \\]").matcher(tmp);
                    bikeDto.setMaker(makerMatcher.find() ? makerMatcher.group(1) : "");
                    // 車種
                    Matcher modelMatcher = Pattern.compile("\\] (.*) ").matcher(tmp);
                    bikeDto.setName(modelMatcher.find() ? modelMatcher.group(1) : "");
                    // 説明
                    Matcher descMatcher = Pattern.compile("\\].*   (.*)").matcher(tmp);
                    bikeDto.setComment(descMatcher.find() ? descMatcher.group(1) : "");

                }
                logger.debug(bikeDto);
                bikeDtoList.add(bikeDto);
            }
            //pageの中にpn nextが無い場合は検索を終了する
            if (bikeDoc.select("a.pn.next").isEmpty()) {
                break;
            }
        }

        return bikeDtoList;
    }

    @Override
    protected Integer getInspection(String inspectionStr) {
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

    @Override
    protected Integer getDistance(String distanceStr) {
        Integer distance = null;
        Matcher distanceMatcher = Pattern.compile("([0-9,]+)km").matcher(distanceStr);
        if (distanceMatcher.find()) {
            try {
                // カンマ区切りを戻す
                distance = NumberFormat.getInstance().parse(distanceMatcher.group(1)).intValue();
            } catch (ParseException e) {
                // TODO: ロガーでdistanceStrを出力する
                logger.info("走行距離 フォーマット不明 : " + distanceStr);
                return null;
            }
        } else if (distanceStr.equals("-")) {
            //新車
            distance = 0;
        }
        // TODO: ロガーでdistanceStrを出力する
        logger.info("走行距離 : " + distanceStr + " : " + distance);
        return distance;
    }

    @Override
    protected Integer getYear(String yearStr) {
        Integer year = null;
        Matcher yearMatcher = Pattern.compile("([0-9]+)年").matcher(yearStr);
        if (yearMatcher.find()) {
            year = Integer.parseInt(yearMatcher.group(1));
        }

        // TODO: ロガーでyearStrを出力する
        logger.info("登録年数 : " + yearStr + " : " + year);
        return year;
    }

    @Override
    protected Integer getPrice(String priceStr) {
        Integer price = null;
        Matcher priceMatcher = Pattern.compile("([0-9\\.]+)(万|千)").matcher(priceStr);
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

}
