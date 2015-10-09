package kr.pe.sinnori.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegularExpressionUtil {
	public static boolean hasLeadingOrTailingWhiteSpace(String value) {
		Logger log = LoggerFactory.getLogger(RegularExpressionUtil.class);

		boolean expectedValue;		
		String trimValue = value.trim();		
		expectedValue = !trimValue.equals(value);
		
		boolean returnValue = value.matches("^\\s+[^.$]*");
		
		log.info("1. value=[{}] returnValue={}", value, returnValue);
		
		if (!returnValue) {
			
			returnValue = value.matches("[^.$]*\\s+$");
			
			log.info("2. value=[{}] returnValue={}", value, returnValue);
		}
		
		
		
		if (expectedValue != returnValue) {
			log.warn("parameter value=[{}], trimValue=[{}], expectedValue={}, returnValue={}", 
					value, trimValue, expectedValue, returnValue);
		}
				
		return returnValue;
	}
	
	public static boolean isValidMessageID(String value) {
		return value.matches("[a-zA-Z][a-zA-Z0-9]+");
	}
	
}
