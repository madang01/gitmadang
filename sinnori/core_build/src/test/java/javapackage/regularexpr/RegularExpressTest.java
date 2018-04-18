package javapackage.regularexpr;

import static org.junit.Assert.fail;

import org.junit.Test;

import kr.pe.sinnori.common.AbstractJunitTest;

public class RegularExpressTest extends AbstractJunitTest {
	
	
	
	@Test
	public void testRegularExpressDosRootPath() {
		boolean expecedResult = true;
		String pathString = "c:\\";
		boolean result = pathString.matches("^[a-zA-Z]:\\\\$");
		
		if (result != expecedResult) {
			fail(String.format("For the pathString[%s], the expected result[%s] is not same to the result[%s]", pathString, expecedResult, result));
		}
		
		expecedResult = true;
		pathString = "C:\\"; 
		result = pathString.matches("^[a-zA-Z]:\\\\$");
		
		if (result != expecedResult) {
			fail(String.format("For the pathString[%s], the expected result[%s] is not same to the result[%s]", pathString, expecedResult, result));
		}
		
		
		expecedResult = false;
		pathString = "1234";
		result = pathString.matches("^[a-zA-Z]:\\\\$");
		
		if (result != expecedResult) {
			fail(String.format("For the pathString[%s], the expected result[%s] is not same to the result[%s]", pathString, expecedResult, result));
		}
		
		expecedResult = false;
		pathString = "C:\\temp";
		result = pathString.matches("^[a-zA-Z]:\\\\$");
		
		if (result != expecedResult) {
			fail(String.format("For the pathString[%s], the expected result[%s] is not same to the result[%s]", pathString, expecedResult, result));
		}
		
		expecedResult = false;
		pathString = "C:\\\r\nc:\\";
		result = pathString.matches("^[a-zA-Z]:\\\\$");
		
		if (result != expecedResult) {
			fail(String.format("For the pathString[%s], the expected result[%s] is not same to the result[%s]", pathString, expecedResult, result));
		}
	}
	
}
