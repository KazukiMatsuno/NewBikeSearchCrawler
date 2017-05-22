/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.co.bughouse.batch.site;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import static org.hamcrest.CoreMatchers.is;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author user
 */
public class WeBikeTest extends WeBike{
	public WeBikeTest() {
		super("", 1000);
	}
	
	private static WeBikeTest weBikeTest;
	
	@BeforeClass
	public static void BeforeClass(){
		weBikeTest = new WeBikeTest();
	}
	
	@Test
	//getPrice();
	public void 価格取得テスト(){
		//fail("The test case is a prototype.");
		assertThat(weBikeTest.getPrice("1万"), is(10000));
		assertThat(weBikeTest.getPrice("10.5万"), is(105000));
		assertThat(weBikeTest.getPrice("001.5万"), is(15000));
		assertThat(weBikeTest.getPrice("10千"), is(10000));
		assertThat(weBikeTest.getPrice("10.5千"), is(10500));
		assertThat(weBikeTest.getPrice("001.5千"), is(1500));
		assertNull(weBikeTest.getPrice("-"));
		assertNull(weBikeTest.getPrice("不明"));
	}
	
	@Test
	//getDistance();
	public void 走行距離取得テスト(){
		//fail("The test case is a prototype.");
		assertThat(weBikeTest.getDistance("100km"), is(100));
		assertThat(weBikeTest.getDistance("2525km"), is(2525));
		assertThat(weBikeTest.getDistance("-"), is(0));
		assertNull(weBikeTest.getDistance("100メートル"));
		assertNull(weBikeTest.getDistance("１００ｋｍ"));
	}
	
	@Test
	//getYear();
	public void 登録年数取得テスト(){
		//fail("The test case is a prototype.");
		assertThat(weBikeTest.getYear("1999年"), is(1999));
		assertThat(weBikeTest.getYear("2015年"), is(2015));
		assertNull(weBikeTest.getYear("不明"));
		assertNull(weBikeTest.getYear("-"));
	}
	
	@Test
	//getInspection();
	public void 車検取得テスト(){
		//fail("The test case is a prototype.");
		Locale locale = new Locale("ja", "JP", "JP");
		SimpleDateFormat sdf1 = new SimpleDateFormat("検y.MMM", locale);
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMM");
		//一年前
		Calendar oneYearAgoCalendar = Calendar.getInstance();
		oneYearAgoCalendar.add(Calendar.YEAR, -1);
		// 一ヶ月前
		Calendar oneMonthAgoCalendar = Calendar.getInstance();
		oneMonthAgoCalendar.add(Calendar.MONTH, -1);
		// 当月
		Calendar nowCalendar = Calendar.getInstance();
		// 一ヵ月後
		Calendar oneMonthLaterCalendar = Calendar.getInstance();
		oneMonthLaterCalendar.add(Calendar.MONTH, +1);
		//一年後
		Calendar oneYearLaterCalendar = Calendar.getInstance();
		oneYearLaterCalendar.add(Calendar.YEAR, +1);
		
		// 一年前
		final String ONE_YEAR_AGO_STR = sdf1.format(oneYearAgoCalendar.getTime());
		assertNull(weBikeTest.getInspection(ONE_YEAR_AGO_STR));

		// 一ヶ月前
		final String ONE_MONTH_AGO_STR = sdf1.format(oneMonthAgoCalendar.getTime());
		assertNull(weBikeTest.getInspection(ONE_MONTH_AGO_STR));
		
		// 当月
		final String NOW_STR = sdf1.format(nowCalendar.getTime());
		final Integer NOW_INT = new Integer(sdf2.format(nowCalendar.getTime()));
		assertThat(weBikeTest.getInspection(NOW_STR), is(NOW_INT));

		// 一ヵ月後
		final String ONE_MONTH_LATER_STR = sdf1.format(oneMonthLaterCalendar.getTime());
		final Integer ONE_MONTH_LATER_INT = new Integer(sdf2.format(oneMonthLaterCalendar.getTime()));
		assertThat(weBikeTest.getInspection(ONE_MONTH_LATER_STR), is(ONE_MONTH_LATER_INT));
		
		// 一年後
		final String ONE_YEAR_LATER_STR = sdf1.format(oneYearLaterCalendar.getTime());
		final Integer ONE_YEAR_LATER_INT = new Integer(sdf2.format(oneYearLaterCalendar.getTime()));
		assertThat(weBikeTest.getInspection(ONE_YEAR_LATER_STR), is(ONE_YEAR_LATER_INT));
	}
	
}
