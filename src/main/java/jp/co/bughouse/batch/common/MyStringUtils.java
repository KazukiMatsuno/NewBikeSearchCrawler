/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.co.bughouse.batch.common;

import java.util.Calendar;
import jp.co.bughouse.batch.exception.YearConvertException;

/**
 *
 * @author user
 */
public class MyStringUtils {
	public final static Calendar todayDate	= Calendar.getInstance();
	public final static Calendar expiryDate	= Calendar.getInstance();

	static{
		todayDate.set(Calendar.DATE, 1);
		expiryDate.add(Calendar.YEAR, 3);
	}
	
	public static String unEscapeHtml(String str){
		return str
				.replace((char)160, ' ')
				.replace((char)60, '<')
				.replace((char)62, '>')
				.replace((char)38, '&')
				.replace((char)34, '"')
				.replace((char)39, '\'')
				.replace((char)8722, '-');
	}
	//全角英数字を半角にする
	synchronized public static String zenkakuToHankaku(String value) {
		StringBuilder sb = new StringBuilder(value);
		for (int i = 0; i < sb.length(); i++) {
			int c = (int) sb.charAt(i);
			if ((c >= 0xFF10 && c <= 0xFF19) || (c >= 0xFF21 && c <= 0xFF3A) || (c >= 0xFF41 && c <= 0xFF5A)) {
				sb.setCharAt(i, (char) (c - 0xFEE0));
			}
		}
		value = sb.toString();
		return value;
	}

	//きちんと車検があるのかどうかをチェックする。
	public static boolean isExpiredInspection(Calendar inspectionDate){
		try{
			//today以上であり、expiryと同じかそれ以下である場合
			if(todayDate.compareTo(inspectionDate) <= 0 && 0 <= expiryDate.compareTo(inspectionDate)){
				return true;
			}else{
				return false;
			}
		}catch(NullPointerException e){
			e.printStackTrace();
			return false;
		}

	}

//	//和暦と西暦を判断して変換する（2桁の整数値が引数)
//	public static Integer convertYear(int year, int limitYear){
//		if(String.valueOf(year).length() == 4)	return year;
//		if(2000 + year <= limitYear)			return 2000 + year;	//西暦表記でした。
//		else if(1988 + year <= limitYear && (limitYear - 1988) >= year)	return 1988 + year;	//和暦の平成表記でした。
//		else if(1925 + year <= limitYear && (1988 - 1924) >= year)						return 1925 + year; //和暦の昭和表記でした。
//		else if(1900 + year <= limitYear)	return 1900 + year;	//西暦表記(19xx年)でした。
//		else								return 0;			//よくわからないです。
//	}

	//2桁の和暦(平成)を4桁の西暦に変換する
	public static Integer heiseiConvertAD(int year) throws YearConvertException{
		if(year < 0){
			throw new YearConvertException("year:" + year);
        }else{
			return year + 1988;
        }
	}

	//2桁の和暦(昭和)を4桁の西暦に変換する
	public static Integer showaConvertAD(int year) throws YearConvertException{
        // 0年未満 or 64年以上の場合
        if(0 > year || year > 64){
			throw new YearConvertException("year:" + year);
        }else{
			return year + 1925;
        }
	}
	
	public static String deleteBlank(String str){
		//全角スペースと半角スペースを正規表現で削除して戻す
		return str.replaceAll("[ 　]", "");
	}
}
