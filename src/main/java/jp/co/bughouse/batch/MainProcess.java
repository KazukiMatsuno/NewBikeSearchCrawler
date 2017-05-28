/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.co.bughouse.batch;

import jp.co.bughouse.batch.site.AbstractSite;
import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.concurrent.Callable;
import jp.co.bughouse.batch.db.jdbc.MyJDBC;
import jp.co.bughouse.batch.entity.BikeEntity;
import jp.co.bughouse.batch.entity.ShopEntity;
import org.apache.log4j.Logger;

/**
 *
 * @author user
 */
public class MainProcess implements Callable {

    private final String SITE_PACKAGE = "jp.co.bughouse.batch.site.{0}";
    private final int WAIT_MS = 500;
    private final AbstractSite site;
    private final MyJDBC myJdbc;
    private final String siteName;
    private boolean bikeDataFetchFlag;
    private boolean shopDataFetchFlag;
    // ロガー宣言
    private static final Logger logger = Logger.getLogger(Crawler.class);

    public MainProcess(String siteName, MyJDBC myJdbc) throws Exception {
        logger.info(siteName);
        this.siteName = siteName;
        site = (AbstractSite) Class.forName(MessageFormat.format(SITE_PACKAGE, this.siteName))
            .getConstructor(new Class[]{String.class, int.class})
            .newInstance(new Object[]{"UTF-8", WAIT_MS});

        this.myJdbc = myJdbc;
    }

    public MainProcess setShopDataFetchFlag(boolean shopDataFetchFlag) {
        this.shopDataFetchFlag = shopDataFetchFlag;
        return this;
    }

    public MainProcess setBikeDataFetchFlag(boolean bikeDataFetchFlag) {
        this.bikeDataFetchFlag = bikeDataFetchFlag;
        return this;
    }

    @Override
    public Object call() throws Exception {
        logger.info("ShopDataFetchFlag : " + shopDataFetchFlag);
        logger.info("BikeDataFetchFlag : " + bikeDataFetchFlag);
        if (shopDataFetchFlag) {
            // 該当のサイトのショップフラグをいったんすべて落とす
            int updateCount = myJdbc.updateShopFlag(siteName);
            logger.info("ShopFlagUpdateCount : " + updateCount);

            for (String prefectureUrl : site.getPrefectureURLSet()) {
                for (String shopUrl : site.getShopURLSet(prefectureUrl)) {
                    ShopEntity shopEntity = site.getShopDto(shopUrl);
                    // MShopをアップデート
                    myJdbc.updateMShop(shopEntity);
                    // TShopをアップデート
                    myJdbc.updateTShop(shopEntity);
                }
            }
        }

        if (bikeDataFetchFlag) {
            // 該当のサイトのバイクフラグをいったんすべて落とす
            int updateCount = myJdbc.updateBikeFlag(siteName);
            logger.info("BikeFlagUpdateCount : " + updateCount);
            //DBから値取得
            ResultSet rs = myJdbc.selectShopUrlRS(siteName);
            while (rs.next()) {
                String url = rs.getString("url");
                try {
                    for (BikeEntity bikeEntity : site.getBikeDtoList(url)) {
                        // TAdsをアップデート
                        myJdbc.updateTAds(bikeEntity);
                    }
                } catch (Exception e) {
                    logger.error(e);
                }
            }
        }
        return null;
    }
}
