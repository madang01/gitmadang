package kr.pe.sinnori.weblib.htmlstring;

import kr.pe.sinnori.common.type.LineSeparatorType;
import kr.pe.sinnori.common.util.CommonStaticUtil;
import kr.pe.sinnori.weblib.htmlstring.StringReplacementActorUtil.STRING_REPLACEMENT_ACTOR_TYPE;

public class HtmlStringUtil {
	
	/**
	 * Step1 EscapeHtml4
	 * @param sourceString source string
	 * @return Step1 EscapeHtml4
	 */
	public static String toHtml4String(String sourceString) {
		return StringReplacementActorUtil.replace(sourceString, 
				STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4);
	}
	

	/**
	 * Step1 EscapeHtml4 -> Step2 Line2BR
	 * @param sourceString source string
	 * @return Step1 EscapeHtml4 -> Step2 Line2BR
	 */
	public static String toHtml4BRString(String sourceString) {
		return StringReplacementActorUtil.replace(sourceString, 
				STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4,
				STRING_REPLACEMENT_ACTOR_TYPE.LINE2BR);
	}
	
	/**
	 * Step1 split wanted column size string -> Step2 EscapeHtml4 -> Step3 Line2BR
	 * @param sourceString source string
	 * @param wantedColumnSize wanted column size
	 * @return Step1 split wanted column size string -> Step2 EscapeHtml4 -> Step3 Line2BR
	 */
	public static String toHtml4BRString(String sourceString, int wantedColumnSize) {
		sourceString = CommonStaticUtil.splitString(sourceString, 
				LineSeparatorType.BR, wantedColumnSize);
		
		return StringReplacementActorUtil.replace(sourceString, 
				STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEHTML4,
				STRING_REPLACEMENT_ACTOR_TYPE.LINE2BR);
	}
	
	/**
	 * Step1 EscapeScript
	 * @param sourceString
	 * @return Step1 EscapeScript
	 */
	public static String toScriptString(String sourceString) {
		return StringReplacementActorUtil.replace(sourceString, 
				STRING_REPLACEMENT_ACTOR_TYPE.ESCAPEECMASCRIPT);
	}

}
