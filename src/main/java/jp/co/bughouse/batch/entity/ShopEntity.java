
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.co.bughouse.batch.entity;

/**
 *
 * @author user
 */
public class ShopEntity {
        private String siteName;
        private String shopName;
        private String address;
        private String tel;
		private String url;
		
		public void setSiteName(String siteName){
			this.siteName	= siteName;
		}
		
		public void setShopName(String shopName){
			this.shopName	= shopName;
		}
		
		public void setAddress(String address){
			this.address	= address;
		}
		
		public void setTel(String tel){
			this.tel		= tel;
		}
		
		public void setUrl(String url){
			this.url		= url;
		}
		
		public String getSiteName(){
			return this.siteName;
		}
		
		public String getShopName(){
			return this.shopName;
		}
		
		public String getAddress(){
			return this.address;
		}
		
		public String getTel(){
			return this.tel;
		}
		
		public String getUrl(){
			return this.url;
		}
		
		
		@Override
		public String toString(){
			return getSiteName() + "\t" + getShopName() + "\t" + getAddress() + "\t" + getTel() + "\t" + url;
		}
}
