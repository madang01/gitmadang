package kr.pe.sinnori.weblib.htmlstring;


public class StringReplacementActorUtil {
	public enum STRING_REPLACEMENT_ACTOR_TYPE {
		LINE2BR, ESCAPEHTML4, ESCAPEECMASCRIPT
	};
	
	
	private static AbstractStringReplacementActor[] actorList = {
			new Line2BrStringReplacementActor(),
			new EscapeHtml4StringReplacementActor(),
			new EscapeEcmaScriptStringReplacementActor()
	};
	
	public static String replace(String sourceString, 
			STRING_REPLACEMENT_ACTOR_TYPE ... stringReplacementActorTypeList) {
		
		String resultString = sourceString;
		for (STRING_REPLACEMENT_ACTOR_TYPE stringReplacementActorType : stringReplacementActorTypeList) {
						
			resultString = actorList[stringReplacementActorType.ordinal()].replace(resultString);
		}
		
		return resultString;
	}
	
	

}
