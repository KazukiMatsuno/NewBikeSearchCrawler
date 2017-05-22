/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.co.bughouse.batch.db.jdbc;

import com.google.code.geocoder.model.LatLng;
import com.iciql.Db;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;
import jp.co.bughouse.batch.geocode.MyGeocoder;
import jp.co.bughouse.batch.db.dao.MShop;
import jp.co.bughouse.batch.db.dao.MSite;
import jp.co.bughouse.batch.db.dao.TAds;
import jp.co.bughouse.batch.db.dao.TShop;
import jp.co.bughouse.batch.entity.BikeEntity;
import jp.co.bughouse.batch.entity.ShopEntity;
import org.apache.log4j.Logger;

/**
 *
 * @author user
 */
public class MyJDBC {
	private final Db database;
	// ロガー宣言
	private static final Logger logger = Logger.getLogger(MyJDBC.class);
	public MyJDBC(Db database){
		this.database = database;
	}
	
	public int updateShopFlag(String siteName){
		Integer fkSiteId = this.getSiteId(siteName);
		TShop tShop = new TShop();
		
		// ショップフラグをfalseにして、件数を返す
		return database.from(tShop)
			.set(tShop.flag).to(false)
			.where(tShop.fk_siteid).is(fkSiteId)
			.update();
	}
	
	public int updateBikeFlag(String siteName){
		// TODO: ここのべたにSQLを記述しているところを何とかしたい。
		String updateSql  = 
			"UPDATE T_Ads " +
			"INNER JOIN T_Shop on T_Ads.FK_shopId    = T_Shop.id " +
			"INNER JOIN M_Shop on T_Shop.FK_ShopId   = M_Shop.id " +
			"INNER JOIN M_Site on T_Shop.FK_SiteId   = M_Site.id " +
			"SET T_Ads.flag = false " +
			"WHERE M_Site.name = ?";
		return database.executeUpdate(updateSql, siteName);
//		MSite mSite = new MSite();
//		MShop mShop = new MShop();
//		TShop tShop = new TShop();
//		TAds tAds = new TAds();
						
//		database.from(tAds)
//			.set(tAds.flag)
//			.toParameter()
//			.innerJoin(tShop).on(tShop.id).is(tAds.fk_shopid)
//			.innerJoin(mShop).on(mShop.id).is(tShop.fk_shopid)
//			.innerJoin(mSite).on(mSite.id).is(tShop.fk_siteid)
//			.where(mSite.name).is(siteName)
//			.toSQL()
		// バイクフラグをfalseにして、件数を返す
//		return database.from(tAds).
	}
	
	public void updateMShop(ShopEntity shopEntity){
		MShop mShop = new MShop();
		mShop.address			= shopEntity.getAddress();					// 住所
		mShop.name				= shopEntity.getShopName();					// 店名
		mShop.tel				= shopEntity.getTel();						// 電話番号(ハイフン有り)
		mShop.telnondelimited	= shopEntity.getTel().replaceAll("-", "");	// 電話番号(ハイフンなし)
		mShop.id				= this.getMShopId(mShop.telnondelimited);	// ID
		LatLng latLng			= MyGeocoder.getLocation(shopEntity.getAddress());
		if(latLng != null){
			mShop.lat				= latLng.getLat().doubleValue();
			mShop.lng				= latLng.getLng().doubleValue();
		}

		database.merge(mShop);
	}
	
	public void updateTShop(ShopEntity shopEntity){
		TShop tShop = new TShop();
		tShop.fk_siteid = getSiteId(shopEntity.getSiteName());
		tShop.fk_shopid	= getMShopId(shopEntity.getTel().replaceAll("-", ""));
		tShop.url		= shopEntity.getUrl();
		tShop.flag		= true;
		
		database.merge(tShop);
	}

	public void updateTAds(BikeEntity bikeEntity){
		TShop shopTable = new TShop();
		TShop tShop = database.from(shopTable)
			.where(shopTable.url).is(bikeEntity.getShopUrl())
			.selectFirst();
		
		TAds tAds = new TAds();
		tAds.maker		= bikeEntity.getMaker();
		tAds.name		= bikeEntity.getName();
		tAds.color		= bikeEntity.getColor();
		tAds.comment	= bikeEntity.getComment();
		tAds.picurl		= bikeEntity.getPicUrl();
		tAds.url		= bikeEntity.getUrl();
		tAds.distance	= bikeEntity.getDistance();
		tAds.price		= bikeEntity.getPrice();
		tAds.year		= bikeEntity.getYear();
		tAds.inspection	= bikeEntity.getInspection();
		tAds.fk_shopid	= tShop.id;
		tAds.flag		= true;
		
		TAds oldAds		= getTAdsId(tShop.id, bikeEntity.getUrl());
		if(oldAds != null){
			// アップデートの場合の処理
			// アップデートの場合はinsertdateはいじらない
			tAds.id			= oldAds.id;
			tAds.insertdate	= oldAds.insertdate;
		}else{
			// インサートの場合の処理
			// インサートの場合はinsertdateに現在日付を入れる
			tAds.insertdate = exchangeUtilDateToSqlDate(new Date());
		}
		
		database.merge(tAds);
	}
	
	
	public ResultSet selectShopUrlRS(String siteName){
		MSite mSite = new MSite();
		TShop tShop = new TShop();
		
		// 手動でSQLを設定してもよかったがせっかくなのでiciqlに作ってもらう
		// T_SHOPからSiteIdとフラグを指定して、該当のSHOP_URLを抜き取るSQL
		String shopUrlSelectSQL = database.from(tShop)
									.innerJoin(mSite).on(mSite.id).is(tShop.fk_siteid)
									.where(mSite.name).is(siteName)
									.and(tShop.flag).is(true)
									.toSQL();
		logger.debug(shopUrlSelectSQL);
		return database.executeQuery(shopUrlSelectSQL);
	}
	
	private Integer getSiteId(String siteName){
		MSite mSite = new MSite();
		return database.from(mSite)
				.where(mSite.name).is(siteName)
				.selectFirst().id;
		
	}
	
	private Integer getMShopId(String telNonDelimited){
		MShop mShop = new MShop();
		MShop oldShop = database.from(mShop)
							.where(mShop.telnondelimited).is(telNonDelimited)
							.selectFirst();

		// oldShpoが入っている場合はID, 入っていない場合はnullを返す
		return oldShop != null ? oldShop.id : null;
	}
	
	private Integer getTShopId(Integer siteId, Integer shopId){
		TShop tShop = new TShop();
		TShop oldShop = database.from(tShop)
							.where(tShop.fk_shopid).is(shopId)
							.and(tShop.fk_siteid).is(siteId)
							.selectFirst();
		
		// oldShopが入っている場合はID, 入っていない場合はnullを返す
		return oldShop != null ? oldShop.id : null;
	}
	
	
	private TAds getTAdsId(Integer shopId, String bikeDetailUrl){
		TAds tAds = new TAds();
		TAds oldAds = database.from(tAds)
							.where(tAds.fk_shopid).is(shopId)
							.and(tAds.url).is(bikeDetailUrl)
							.selectFirst();
		
		// oldAdsが入っている場合はID, 入っていない場合はnullを返す
		return oldAds != null ? oldAds : null;
	}
	
	// java.util.Dateをjava.sql.Dateに変換する
	private java.sql.Date exchangeUtilDateToSqlDate(java.util.Date utilDate){
		Calendar cal = Calendar.getInstance();
		cal.setTime(utilDate);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return new java.sql.Date(cal.getTimeInMillis());
	}
	
}
