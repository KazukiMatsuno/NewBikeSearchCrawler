/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.co.bughouse.batch.site;

import java.io.IOException;
import java.util.List;
import jp.co.bughouse.batch.entity.BikeEntity;
import jp.co.bughouse.batch.entity.ShopEntity;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

/**
 *
 * @author user
 */
public abstract class AbstractSite {

    protected String encode;
    protected int waitMS;

    public AbstractSite(String encode, int waitMS) {
        this.encode = encode;
        this.waitMS = waitMS;
    }

    abstract public List<String> getPrefectureURLList() throws IOException;

    abstract public List<String> getShopURLList(String prefectureURL) throws IOException;

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
        System.out.println(url);
        return Jsoup.connect(url).validateTLSCertificates(false);
    }
}
