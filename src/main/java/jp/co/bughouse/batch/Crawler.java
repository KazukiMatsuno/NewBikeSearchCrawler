 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.co.bughouse.batch;

import jp.co.bughouse.batch.common.MyProperties;
import com.iciql.Db;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import jp.co.bughouse.batch.db.jdbc.MyJDBC;
import org.apache.log4j.Logger;

/**
 *
 * @author user
 */
public class Crawler {
	// ロガー宣言
	private static final Logger logger = Logger.getLogger(Crawler.class);
	
    public static void main(String[] args){
		// 設定ファイルが読めない場合はエラーログを出力して終了する
		Properties prop = null;
		try{
	        prop	= MyProperties.getProperties("BikeSearch");
		}catch(Exception e){
			logger.error(e);
			return;
		}
		
		boolean shopFlag = false;
        boolean bikeFlag = false;

		//Shopフラグ
        if(isOption(args, "SHOP")){
            shopFlag	= true;
            args        = removeOption(args, "SHOP");
        }

		//Bikeフラグ
        if(isOption(args, "BIKE")){
            bikeFlag	= true;
            args        = removeOption(args, "BIKE");
        }

		Db database = getDb(prop);
		
        MainProcess[] process = new MainProcess[args.length];
		for(int i = 0; i < args.length; i ++){
			logger.info(args[i]);
			try{
				process[i] = new MainProcess(args[i], new MyJDBC(database))
					.setShopDataFetchFlag(shopFlag)
					.setBikeDataFetchFlag(bikeFlag);
				process[i].call();
			}catch(Exception e){
				logger.error(e);
			}
		}
    }
	
	private static Db getDb(Properties prop){
		return Db.open(
			prop.getProperty("URL"),
			prop.getProperty("USER"),
			prop.getProperty("PASS")
		);
	}

    private static boolean isOption(String[] args, String option){
        for(String arg : args){
            if(arg.equals(option)){
                return true;
            }
        }
        return false;
    }

    private static String[] removeOption(String[] args, String option){
        List<String> list = new ArrayList<>(Arrays.asList(args));
		// removeメソッドは消せないとfalseが返るので、下記で重複オプションをすべて消すことができる。
        while(list.remove(option)){}
        args = (String[]) list.toArray(new String[list.size()]);
        return args;
    }
}
