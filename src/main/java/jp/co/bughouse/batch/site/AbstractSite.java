/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.co.bughouse.batch.site;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jp.co.bughouse.batch.entity.BikeEntity;
import jp.co.bughouse.batch.entity.ShopEntity;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

/**
 *
 * @author user
 */
public abstract class AbstractSite {

    protected String encode;
    protected int waitMS;
    // ロガー宣言
    private static final Logger logger = Logger.getLogger(AbstractSite.class);

    public AbstractSite(String encode, int waitMS) {
        this.encode = encode;
        this.waitMS = waitMS;
    }

    abstract public Set<String> getPrefectureURLSet() throws IOException;

    abstract public Set<String> getShopURLSet(String prefectureURL) throws IOException;

    abstract public ShopEntity getShopDto(String shopUrl) throws IOException;

    abstract public List<BikeEntity> getBikeDtoList(String shopUrl) throws IOException;

    abstract protected Integer getDistance(String distanceStr);

    abstract protected Integer getYear(String yearStr);

    abstract protected Integer getPrice(String priceStr);

    abstract protected Integer getInspection(String inspectionStr);

    public Connection getJsoupConnection(String url, int waitMS) {
        try {
            Thread.sleep(waitMS);
        } catch (InterruptedException e) {
        }
        logger.info(url);
        return Jsoup.connect(url).validateTLSCertificates(false);
    }
    
    public Connection getJsoupConnection(String url, int waitMS, Map<String, String> dataMap){
        try {
            Thread.sleep(waitMS);
        } catch(InterruptedException e){}
        
        // ロガー用
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String, String> data : dataMap.entrySet()) {
            sb.append(data.getKey()).append("=").append(data.getValue()).append("&");
        }
        logger.info(url + "?" + sb.toString());
        return Jsoup.connect(url).validateTLSCertificates(false).data(dataMap);
    }
}
