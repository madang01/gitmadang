package kr.pe.sinnori.common.util;

import org.junit.Before;

public class RegularExpressionUtilTest {
	boolean returnedValue;

	boolean expectedValue;

	String validCaseStringArray[];

	String invalidCaseStringArray[];
	
	@Before
	public void setup() {
		validCaseStringArray = new String[]{ 
				"", 
				"aabb", 
				"multi_lines\na\nabb", 
				"multi_lines_middle_newline\n\nabb",
				"multi_lines_middle_space\na \nabb",
				"a\n\n abb" };
		
		invalidCaseStringArray =  new String[]{ 
					"/tmp.jsp \t",
					"\thead_space", 
					" \thead_space", 
					"\n\nhead_newline", 
					"middle_space_and_tail_space\na \nabc ", 
					"middle_space_and_tail_space\na \n\tabc ",
					"tail_space ",
					"tail_newline\nab\n\n"};
	}

	/*@Test
	public void testIsTrimString_ValidCaseStringArray() {
		expectedValue = false;
		
		for (int i=0; i < validCaseStringArray.length; i++) {
			returnedValue = RegularExpressionUtil.hasLeadingOrTailingWhiteSpace(validCaseStringArray[i]);

			org.junit.Assert
					.assertThat("the index "+ i+" valid string's expected value comparison", returnedValue,
							org.hamcrest.CoreMatchers.equalTo(expectedValue));
		}
		
	}
	
	@Test
	public void testIsTrimString_InvalidCaseStringArray() {
		expectedValue = true;
		
		for (int i=0; i < invalidCaseStringArray.length; i++) {
			returnedValue = RegularExpressionUtil.hasLeadingOrTailingWhiteSpace(invalidCaseStringArray[i]);

			org.junit.Assert
					.assertThat("the index "+ i+" invalid string's expected value comparison", returnedValue,
							org.hamcrest.CoreMatchers.equalTo(expectedValue));
		}
		
	}
	*/
}
