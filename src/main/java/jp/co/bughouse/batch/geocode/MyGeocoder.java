/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.co.bughouse.batch.geocode;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;
import static com.google.code.geocoder.model.GeocoderStatus.OVER_QUERY_LIMIT;
import com.google.code.geocoder.model.LatLng;
import org.apache.log4j.Logger;

/**
 *
 * @author user
 */
public class MyGeocoder {
    private static boolean overQueryLimitFlag = false;
	// ロガー宣言
	private static final Logger logger = Logger.getLogger(MyGeocoder.class);
    
    public static LatLng getLocation(String address){
        try{
            if(overQueryLimitFlag){
                //エラーフラグがfalseの場合は、LatLngを識別せず終了する
                //Googleに無駄に403アクセスしてアクセス禁止にならないようにするため。
                return null;
            }
            
            GeocoderRequest request
                    = new GeocoderRequestBuilder().setAddress(address).setLanguage("ja").getGeocoderRequest();
            GeocodeResponse response;
            LatLng latLng = new LatLng();
            response = new Geocoder().geocode(request);
            if(response.getStatus().equals(OVER_QUERY_LIMIT)){
                overQueryLimitFlag = true;
            }
            
            for(GeocoderResult result : response.getResults()){
                return result.getGeometry().getLocation();
            }
        }catch(Exception e){
			logger.error(e);
        }
        return null;
    }
}
