package kr.pe.sinnori.weblib.htmlstring;


public class StringReplacementActorUtil {
	public enum STRING_REPLACEMENT_ACTOR_TYPE {
		LINE2BR(0), ESCAPEHTML4(1), ESCAPEECMASCRIPT(2);
		
		private int indexOfList;
		private STRING_REPLACEMENT_ACTOR_TYPE(int indexOfList) {
			this.indexOfList = indexOfList;
		}
		
		private static AbstractStringReplacementActor[] actorList = {
				new Line2BrStringReplacementActor(),
				new EscapeHtml4StringReplacementActor(),
				new EscapeEcmaScriptStringReplacementActor()
		};
		
		public AbstractStringReplacementActor getStringReplacementActor() {
			return actorList[indexOfList];
		}
	};
	
	
	public static String replace(String sourceString, 
			STRING_REPLACEMENT_ACTOR_TYPE ... stringReplacementActorTypeList) {
		
		String resultString = sourceString;
		for (STRING_REPLACEMENT_ACTOR_TYPE stringReplacementActorType : stringReplacementActorTypeList) {
						
			resultString = stringReplacementActorType.getStringReplacementActor().replace(resultString);
		}
		
		return resultString;
	}
	
	

}
