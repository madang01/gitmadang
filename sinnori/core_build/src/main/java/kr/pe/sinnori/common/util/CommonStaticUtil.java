package kr.pe.sinnori.common.util;

import java.io.File;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.CommonType;


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
	
	/**
	 * 지정한 칼럼수 단위로 개행문자를 추가한 문자열 즉 지정한 칼럼수를 갖는 문자열을 반환한다.
	 * @param sourceString 변환을 원하는 문자열
	 * @param wantedColumnSize 지정한 칼럼수
	 * @return 지정한 칼럼수 단위로 개행문자를 추가한 문자열, 즉 지정한 칼럼수를 갖는 문자열
	 */
	public static String convertToNewLineSplitString(String sourceString, CommonType.SPLIT_STRING_GUBUN sliptStringGubun, int wantedColumnSize) {
		if (null == sourceString) {
			throw new IllegalArgumentException("the paramter sourceString is null");
		}
		
		if (sourceString.equals("")) {
			throw new IllegalArgumentException("the paramter sourceString is a empty string");
		}
		
		if (hasLeadingOrTailingWhiteSpace(sourceString)) {
			throw new IllegalArgumentException("the paramter sourceString has leading or tailing white space");
		}
		
		if (null == sliptStringGubun) {
			throw new IllegalArgumentException("the paramter sliptStringGubun is null");
		}
		
		
		if (wantedColumnSize <= 0) {
			throw new IllegalArgumentException("the paramter wantedColumnSize is less or equals to zero");
		}		
		
		String sliptString = null;
		if (sliptStringGubun == CommonType.SPLIT_STRING_GUBUN.BR) {
			sliptString ="<br/>";
		} else {
			sliptString = CommonStaticFinalVars.NEWLINE;
		}
		
		int size = sourceString.length();
		StringBuilder result = new StringBuilder();
		int i=0;
		for (; i+wantedColumnSize < size; i+=wantedColumnSize) {
			result.append(sourceString.substring(i, i+wantedColumnSize));
			result.append(sliptString);
		}
		result.append(sourceString.substring(i));
		return result.toString();
	}
	
	public static String getMultiLineToolTip(String message, int colSize) {
		String tooltip = new StringBuilder("<html>")
		.append(CommonStaticUtil.convertToNewLineSplitString(message, CommonType.SPLIT_STRING_GUBUN.BR, colSize))
		.append("</html>").toString();
		return tooltip;
	}
}
