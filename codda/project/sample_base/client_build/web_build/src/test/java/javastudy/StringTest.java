package javastudy;

import org.junit.Test;

import junitlib.AbstractJunitTest;

public class StringTest extends AbstractJunitTest {
	
	@Test
	public void testSplit() {
		String queryString = "ttt=1";
		String pairStrings[] = queryString.split("=");
		
		System.out.printf("pairStrings.length=%d", pairStrings.length);
		System.out.println();
		
		
		// JsonParser jsonParser = new JsonParser();
	}
	
}
