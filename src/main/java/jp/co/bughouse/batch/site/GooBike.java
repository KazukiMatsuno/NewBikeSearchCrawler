/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.co.bughouse.batch.site;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import jp.co.bughouse.batch.entity.BikeEntity;
import jp.co.bughouse.batch.entity.ShopEntity;
import static jp.co.bughouse.batch.site.MJBike.BASE_URL;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 *
 * @author user
 */
public class GooBike extends AbstractSite {
    
    public static void main(String[] args) throws IOException{
        GooBike gb = new GooBike("", 1000);
        for(String prefURL : gb.getPrefectureURLList()){
            for(String shopURL : getShopURL)
        }
    }
    
    

    final static String BASE_URL = "http://www.goobike.com";
    // ロガー宣言
    private static final Logger logger = Logger.getLogger(GooBike.class);

    public GooBike(String encode, int waitMS) {
        super(encode, waitMS);
    }

    @Override
    public Set<String> getPrefectureURLSet() throws IOException {
        Document prefURLDoc = getJsoupConnection(BASE_URL + "/shop/", waitMS).get();
        Set<String> prefectureURLSet = new HashSet<>();
        
        for(Element aTag : prefURLDoc.select("#contents a")){
            prefectureURLSet.add(BASE_URL + aTag.attr("href"));
        }
        return prefectureURLSet;
    }

    @Override
    public Set<String> getShopURLSet(String prefectureURL) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ShopEntity getShopDto(String shopUrl) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<BikeEntity> getBikeDtoList(String shopUrl) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
