package javastudy;

import org.apache.commons.text.StringEscapeUtils;
import org.junit.Test;

import junitlib.AbstractJunitTest;

public class StringEscapeUtilsTest extends AbstractJunitTest {
	

	@Test
	public void testEscapeEcmaScript() {
		String plainText = "한글\"그림하나를 그리다'\r\n조조할인";
		String escpaeText = StringEscapeUtils.escapeEcmaScript(plainText);
		log.info("escpaeText=[{}]", escpaeText);
		
		log.info("unescpaeText=[{}]", StringEscapeUtils.unescapeEcmaScript(escpaeText));
	}
}
