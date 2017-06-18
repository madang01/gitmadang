package javapackage.java.uti.Calendar;

import java.util.Calendar;
import java.util.TimeZone;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalendarTest {
	private Logger log = LoggerFactory.getLogger(CalendarTest.class);

	@Test
	public void testWeek() {
		
		
		log.info("Calendar.SUNDAY={}", Calendar.SUNDAY);
		log.info("Calendar.MONDAY={}", Calendar.MONDAY);
		log.info("Calendar.TUESDAY={}", Calendar.TUESDAY);
		log.info("Calendar.WEDNESDAY={}", Calendar.WEDNESDAY);
		log.info("Calendar.THURSDAY={}", Calendar.THURSDAY);
		log.info("Calendar.FRIDAY={}", Calendar.FRIDAY);
		log.info("Calendar.SATURDAY={}", Calendar.SATURDAY);
	}
	
	@Test
	public void test1() {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		
		log.info("year={}, month={}, day={}", year, month, day);
		
	}
	
	@Test
	public void test2() {
		Calendar calendar = Calendar.getInstance();
		int minOfDayOfMonth = calendar.getMinimum(Calendar.DAY_OF_MONTH);
		int maxOfDayOfMonth  = calendar.getMaximum(Calendar.DAY_OF_MONTH);
		
		
		log.info("minOfDayOfMonth={}, maxOfDayOfMonth={}", minOfDayOfMonth, maxOfDayOfMonth);
		
	}
	
	@Test
	public void test3() {
		String weekStrings[] = {"", "일", "월", "화", "수", "목", "금", "토"};

		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
		
		
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.set(2017, Calendar.APRIL, 9);
		
		int actualMaxOfDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		
		
		
		int weekYear = calendar.getWeekYear();
		int weeksInWeekYear = calendar.getWeeksInWeekYear();
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		
		log.info("0. actualMaxOfDayOfMonth={}, weekYear={}, weeksInWeekYear={}, dayOfWeek={}::{}", 
				actualMaxOfDayOfMonth, weekYear, weeksInWeekYear, dayOfWeek, weekStrings[dayOfWeek]);
		
		
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		
		log.info("1. year={}, month={}, day={}", year, month+1, day);
		
		calendar.add(Calendar.MONTH, 1);
		
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);
		
		log.info("2. year={}, month={}, day={}", year, month+1, day);
		
		calendar.set(year, month, 1);
		
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);
		
		log.info("3. year={}, month={}, day={}", year, month+1, day);
		
		
		calendar.add(Calendar.DATE, -1);
		
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);
		
		log.info("4. year={}, month={}, day={}", year, month+1, day);
		
		// log.info("minOfDayOfMonth={}, maxOfDayOfMonth={}", minOfDayOfMonth, maxOfDayOfMonth);
		
	}
}
