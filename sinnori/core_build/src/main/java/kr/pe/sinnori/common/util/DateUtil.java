package kr.pe.sinnori.common.util;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	
	/*public Date getFirstDayOfWeekend(Date targetDate) {
		Calendar calendar = Calendar.getInstance();
		
		
	}*/
	
	public Date getFirstDayOfWeekFromYYYYMMDD(String yyyyMMdd) {
		if (null == yyyyMMdd) {
			throw new IllegalArgumentException("the paramter yyyyMMdd is null");
		}
		
		if (6 != yyyyMMdd.length()) {
			String errorMessage = String.format("the paramter yyyyMMdd[%s]'s format is not yyyyMMDD", yyyyMMdd);
			throw new IllegalArgumentException(errorMessage);
		}
		
		Calendar calendar = Calendar.getInstance();
		Calendar calendarClone = (Calendar)calendar.clone();
		
		try {
			int year = Integer.parseInt(yyyyMMdd.substring(0, 4));
			int month = Integer.parseInt(yyyyMMdd.substring(4, 6));
			int date = Integer.parseInt(yyyyMMdd.substring(6));
			
			calendarClone.set(year, month-1, date);
		} catch (Exception e) {
			String errorMessage = String.format("the paramter yyyyMMdd[%s]'s format is not yyyyMMDD", yyyyMMdd);
			throw new IllegalArgumentException(errorMessage);
		}
		
		
		
		
		
		return null;
	}

}
