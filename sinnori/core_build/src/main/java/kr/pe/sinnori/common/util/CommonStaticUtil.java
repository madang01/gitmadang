package kr.pe.sinnori.common.util;

import java.io.File;


public abstract class CommonStaticUtil {	
	/**
	 * 주어진 문자열 앞뒤로 공백 문자 여부를 반환한다. 주의점) 주어진 문자열이 빈 문자열일 경우 true 를 던진다.
	 * @param value 앞뒤로 공백 문자 여부를 알고 싶은 문자열
	 * @return 주어진 문자열 앞뒤로 공백 문자 여부
	 * @throws IllegalArgumentException null 주어진 문자열이 null 인 경우 던진다.
	 */
	public static boolean hasLeadingOrTailingWhiteSpace(String value) throws IllegalArgumentException {
		if (null == value) {
			throw new IllegalArgumentException("the paramater value is null");
		}
		
		
		String trimValue = value.trim();		
		boolean returnValue = !trimValue.equals(value);
		
				
		return returnValue;
	}
	
	public static String getFilePathStringFromResourcePathAndRelativePathOfFile(String resourcesPathString, String relativePath) {
		String realResourceFilePathString = null;
		
		String headSeparator = null;
		if (relativePath.indexOf("/") == 0) headSeparator = "";
		else headSeparator = File.separator;
		
		String subRealPathString = null;		
		if (File.separator.equals("/")) {
			subRealPathString = relativePath;
		} else {
			subRealPathString = relativePath.replaceAll("/", "\\\\");
		}		
		
		realResourceFilePathString = new StringBuilder(resourcesPathString)
		.append(headSeparator).append(subRealPathString).toString();
		
		return realResourceFilePathString;
	}
}
