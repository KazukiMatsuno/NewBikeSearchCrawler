/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.co.bughouse.batch;

import java.util.Calendar;
import static jp.co.bughouse.batch.common.MyStringUtils.*;
import jp.co.bughouse.batch.exception.YearConvertException;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author user
 */
public class MyStringUtilsTest {
	
	public MyStringUtilsTest() {
	}
	
	/**
	 * Test of unEscapeHtml method, of class MyStringUtils.
	 */
	@Test
	public void testUnEscapeHtml() {
//		fail("The test case is a prototype.");
		assertThat(unEscapeHtml(String.valueOf((char)160)), is(" "));
		assertThat(unEscapeHtml(String.valueOf((char)60)), is("<"));
		assertThat(unEscapeHtml(String.valueOf((char)62)), is(">"));
		assertThat(unEscapeHtml(String.valueOf((char)38)), is("&"));
		assertThat(unEscapeHtml(String.valueOf((char)34)), is("\""));
		assertThat(unEscapeHtml(String.valueOf((char)39)), is("'"));
		assertThat(unEscapeHtml(String.valueOf((char)8722)), is("-"));
		
		String message =	String.valueOf((char)160)	+
							String.valueOf((char)60)	+
							String.valueOf((char)62)	+
							String.valueOf((char)38)	+
							String.valueOf((char)34)	+
							String.valueOf((char)39)	+
							String.valueOf((char)8722);
		assertThat(unEscapeHtml(message), is(" <>&\"'-"));
}

	/**
	 * Test of zenkakuToHankaku method, of class MyStringUtils.
	 */
	@Test
	public void testZenkakuToHankaku() {
//		fail("The test case is a prototype.");
		assertThat(zenkakuToHankaku("１２３４５６７８９０"), is("1234567890"));
		assertThat(zenkakuToHankaku("ＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺ"), is("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
		assertThat(zenkakuToHankaku("私はＡＧＥ３０のお兄さんです。"), is("私はAGE30のお兄さんです。"));
	}

	/**
	 * Test of isExpiredInspection method, of class MyStringUtils.
	 */
	@Test
	public void testIsExpiredInspection() {
//		fail("The test case is a prototype.");
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
		assertFalse(isExpiredInspection(oneYearAgoCalendar));
		// 一ヶ月前
		assertFalse(isExpiredInspection(oneMonthAgoCalendar));
		// 当月
		assertTrue(isExpiredInspection(nowCalendar));
		// 一ヵ月後
		assertTrue(isExpiredInspection(oneMonthLaterCalendar));
		// 一年後
		assertTrue(isExpiredInspection(oneYearLaterCalendar));
	}
	
	/**
	 * Test of convertYearHEISEI method, of class MyStringUtils.
	 */
	@Test
	public void testHeiseiConvertAD() {
//		fail("The test case is a prototype.");
            assertThat(heiseiConvertAD(15), is(2003));
            assertThat(heiseiConvertAD(29), is(2017));
            assertThat(heiseiConvertAD(0), is(1988));
            try{
                heiseiConvertAD(-1);
                // Exceptionが出なければテスト失敗
                fail();
            }catch(YearConvertException e){
            }catch(Error e){
                // ConvertException以外のエラーが出た場合はテスト失敗
                fail();
            }
	}

	/**
	 * Test of convertYearSHOWA method, of class MyStringUtils.
	 */
	@Test
	public void testShowaConvertAD() {
//		fail("The test case is a prototype.");
            assertThat(showaConvertAD(62), is(1987));
            assertThat(showaConvertAD(50), is(1975));
            assertThat(showaConvertAD(0), is(1925));
            try{
                showaConvertAD(-1);
                // Exceptionが出なければテスト失敗
                fail();
            }catch(YearConvertException e){
            }catch(Error e){
                // ConvertException以外のエラーが出た場合はテスト失敗
                fail();
            }
	}

	/**
	 * Test of deleteBlank method, of class MyStringUtils.
	 */
	@Test
	public void testDeleteBlank() {
//		fail("The test case is a prototype.");
            assertThat(deleteBlank("　"), is(""));
            assertThat(deleteBlank(" "), is(""));
            assertThat(deleteBlank("1　2 3　"), is("123"));
            assertThat(deleteBlank("matsuno"), is("matsuno"));
		
	}	
}
