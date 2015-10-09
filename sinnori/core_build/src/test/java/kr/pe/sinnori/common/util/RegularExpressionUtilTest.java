package kr.pe.sinnori.common.util;

import org.junit.Before;
import org.junit.Test;

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

	@Test
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
	

	/*@Test
	public void testIsTrimString_NoTrim_MultiLines_MiddleSpace() {
		expectedValue = false;
		returnedValue = RegularExpressionUtil.isTrimString("aabb\ncd \n\teee");

		org.junit.Assert
				.assertThat("the expected value comparison", returnedValue,
						org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}

	@Test
	public void testIsTrimString_NoTrim_MultiLines_HeadEmpty() {
		expectedValue = true;
		returnedValue = RegularExpressionUtil.isTrimString("\n\ncd \n\teee");

		org.junit.Assert
				.assertThat("the expected value comparison", returnedValue,
						org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}

	@Test
	public void testIsTrimString_Trim_MultiLines_HeadSpace() {
		expectedValue = true;
		returnedValue = RegularExpressionUtil.isTrimString(" aabb\ncd\neee");

		org.junit.Assert
				.assertThat("the expected value comparison", returnedValue,
						org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}

	@Test
	public void testIsTrimString_Trim_MultiLines_HeadNewLine() {
		expectedValue = true;
		returnedValue = RegularExpressionUtil.isTrimString("\n\ncd\neee");

		org.junit.Assert
				.assertThat("the expected value comparison", returnedValue,
						org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}

	@Test
	public void testIsTrimString_Trim_MultiLines_TailSpace() {
		expectedValue = true;
		returnedValue = RegularExpressionUtil.isTrimString("aabb\ncd\neee ");

		org.junit.Assert
				.assertThat("the expected value comparison", returnedValue,
						org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}

	@Test
	public void testIsTrimString_NoTrim_NotEmptyString() {
		expectedValue = false;
		returnedValue = RegularExpressionUtil.isTrimString("a ab");

		org.junit.Assert
				.assertThat("the expected value comparison", returnedValue,
						org.hamcrest.CoreMatchers.equalTo(expectedValue));

		expectedValue = false;
		returnedValue = RegularExpressionUtil.isTrimString("a \tab");

		org.junit.Assert
				.assertThat("the expected value comparison", returnedValue,
						org.hamcrest.CoreMatchers.equalTo(expectedValue));

		expectedValue = false;
		returnedValue = RegularExpressionUtil.isTrimString("aab");

		org.junit.Assert
				.assertThat("the expected value comparison", returnedValue,
						org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}

	@Test
	public void testIsTrimString_Trim_HeadAndTailSpaces() {
		org.junit.Assert
				.assertThat("the expected value comparison", returnedValue,
						org.hamcrest.CoreMatchers.equalTo(expectedValue));

		expectedValue = true;
		returnedValue = RegularExpressionUtil.isTrimString(" a\t");

		org.junit.Assert
				.assertThat("the expected value comparison", returnedValue,
						org.hamcrest.CoreMatchers.equalTo(expectedValue));

		expectedValue = true;
		returnedValue = RegularExpressionUtil.isTrimString(" a b\t");

		org.junit.Assert
				.assertThat("the expected value comparison", returnedValue,
						org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}

	@Test
	public void testIsTrimString_Trim_HeadSpaces() {
		expectedValue = true;
		returnedValue = RegularExpressionUtil.isTrimString(" a");

		org.junit.Assert
				.assertThat("the expected value comparison", returnedValue,
						org.hamcrest.CoreMatchers.equalTo(expectedValue));

		expectedValue = true;
		returnedValue = RegularExpressionUtil.isTrimString("  aa b");

		org.junit.Assert
				.assertThat("the expected value comparison", returnedValue,
						org.hamcrest.CoreMatchers.equalTo(expectedValue));

		expectedValue = true;
		returnedValue = RegularExpressionUtil.isTrimString("\taab");

		org.junit.Assert
				.assertThat("the expected value comparison", returnedValue,
						org.hamcrest.CoreMatchers.equalTo(expectedValue));

		expectedValue = true;
		returnedValue = RegularExpressionUtil.isTrimString("\t aab");

		org.junit.Assert
				.assertThat("the expected value comparison", returnedValue,
						org.hamcrest.CoreMatchers.equalTo(expectedValue));

		expectedValue = false;
		returnedValue = RegularExpressionUtil.isTrimString("aab");

		org.junit.Assert
				.assertThat("the expected value comparison", returnedValue,
						org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}

	@Test
	public void testIsTrimString_Trim_TailSpaces() {
		expectedValue = true;
		returnedValue = RegularExpressionUtil.isTrimString("a ");

		org.junit.Assert
				.assertThat("the expected value comparison", returnedValue,
						org.hamcrest.CoreMatchers.equalTo(expectedValue));

		expectedValue = true;
		returnedValue = RegularExpressionUtil.isTrimString("aab  ");

		org.junit.Assert
				.assertThat("the expected value comparison", returnedValue,
						org.hamcrest.CoreMatchers.equalTo(expectedValue));

		expectedValue = true;
		returnedValue = RegularExpressionUtil.isTrimString("aab\t");

		org.junit.Assert
				.assertThat("the expected value comparison", returnedValue,
						org.hamcrest.CoreMatchers.equalTo(expectedValue));

		expectedValue = true;
		returnedValue = RegularExpressionUtil.isTrimString("aab \t");

		org.junit.Assert
				.assertThat("the expected value comparison", returnedValue,
						org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}*/

	@Test
	public void testIsValidMessageID_Valid() {
		expectedValue = true;
		returnedValue = RegularExpressionUtil.isValidMessageID("abc");

		org.junit.Assert
				.assertThat("the expected value comparison", returnedValue,
						org.hamcrest.CoreMatchers.equalTo(expectedValue));

		expectedValue = true;
		returnedValue = RegularExpressionUtil.isValidMessageID("abc1");

		org.junit.Assert
				.assertThat("the expected value comparison", returnedValue,
						org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}

	@Test
	public void testIsValidMessageID_NotValid_NotAlphabet() {
		expectedValue = false;
		returnedValue = RegularExpressionUtil.isValidMessageID("한a");

		org.junit.Assert
				.assertThat("the expected value comparison", returnedValue,
						org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}

	@Test
	public void testIsValidMessageID_NotValid_HeadSpaces() {
		expectedValue = false;
		returnedValue = RegularExpressionUtil.isValidMessageID(" abc");

		org.junit.Assert
				.assertThat("the expected value comparison", returnedValue,
						org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}

	@Test
	public void testIsValidMessageID_NotValid_HeadDigit() {
		expectedValue = false;
		returnedValue = RegularExpressionUtil.isValidMessageID("1a");

		org.junit.Assert
				.assertThat("the expected value comparison", returnedValue,
						org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}
}
