package kr.pe.sinnori.common.config.nativevalueconverter;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import kr.pe.sinnori.common.config.NativeValueConverterTestIF;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeneralConverterReturningRegularFileTest implements
		NativeValueConverterTestIF {
	Logger log = LoggerFactory
			.getLogger(GeneralConverterReturningRegularFileTest.class);

	private GeneralConverterReturningRegularFile nativeValueConverter = null;
	private File returnedValue = null;

	// private String canonicalPathStringOfExpectedValue = null;
	private File expectedValue = null;

	@Override
	@Before
	public void setup() {

		boolean isWritePermissionChecking = true;

		try {
			expectedValue = File.createTempFile("test", "");
			expectedValue.deleteOnExit();
		} catch (IOException e1) {
			fail("fail to create a temp file, errormessage=" + e1.getMessage());
		}

		log.info("temp file=[{}]", expectedValue.getAbsolutePath());

		boolean isSuccess = expectedValue
				.setWritable(isWritePermissionChecking);
		if (!isSuccess) {
			fail("fail to set writable[" + isWritePermissionChecking + "]");
		}

		/*
		 * try { canonicalPathStringOfExpectedValue =
		 * expectedValue.getCanonicalPath(); } catch (IOException e) {
		 * fail("fail to get a canonical path of a temp file, errormessage="
		 * +e.getMessage()); }
		 */

		nativeValueConverter = new GeneralConverterReturningRegularFile(
				isWritePermissionChecking);
	}
	
	@Override
	public void testConstructor() throws Exception {
		/** ignore */
	}

	@Override
	@Test
	public void testToNativeValue_ExpectedValueComparison() {
		try {
			returnedValue = nativeValueConverter.valueOf(expectedValue
					.getAbsolutePath());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		/*
		 * String canonicalPathStringOfReturnedValue = null; try {
		 * canonicalPathStringOfReturnedValue =
		 * returnedValue.getCanonicalPath(); } catch (IOException e) {
		 * fail("fail to get a canonical path of a temp file, errormessage="
		 * +e.getMessage()); }
		 */

		// org.junit.Assert.assertEquals("the expected value comparison",
		// canonicalPathStringOfReturnedValue,
		// canonicalPathStringOfReturnedValue);
		/**
		 * Warning! File.equals() method return false although the file to be
		 * compared has same path.
		 */
		try {
			org.junit.Assert.assertThat("the expected value comparison",
					returnedValue.getCanonicalPath(), org.hamcrest.CoreMatchers
							.equalTo(expectedValue.getCanonicalPath()));
		} catch (IOException e) {
			fail(e.getMessage());
		}

		// assert("", canonicalPathStringOfReturnedValue,
		// canonicalPathStringOfReturnedValue);

		/*
		 * if (!canonicalPathStringOfReturnedValue.equals(
		 * canonicalPathStringOfExpectedValue)) { fail(String.format(
		 * "the temp file[%s] is different from the file[%s] getting from a getNativeValueWithValidation method"
		 * , canonicalPathStringOfExpectedValue,
		 * canonicalPathStringOfReturnedValue)); }
		 */

	}

	@Override
	@Test(expected = IllegalArgumentException.class)
	public void testToNativeValue_NullParameter() throws Exception {
		try {
			returnedValue = nativeValueConverter.valueOf(null);
		} catch (IllegalArgumentException e) {
			log.info(
					"'the parameter itemvalue is null' test ok, errormessage={}",
					e.getMessage());
			throw e;
		}
	}

	@Override
	@Test(expected = IllegalArgumentException.class)
	public void testToNativeValue_EmptyStringParameter() throws Exception {
		try {
			returnedValue = nativeValueConverter.valueOf("");
		} catch (IllegalArgumentException e) {
			log.info(
					"'the parameter itemvalue is a empty string' test ok, errormessage={}",
					e.getMessage());
			throw e;
		}

	}

	@Override
	public void testToNativeValue_ValidButBadParameter() throws Exception {
		try {
			testToNativeValue_ValidButBadParameter_NotRegularFile();
		} catch (IllegalArgumentException e) {
		}
		try {
			testToNativeValue_ValidButBadParameter_CannotRead();
		} catch (IllegalArgumentException e) {
		}
		try {
			testToNativeValue_ValidButBadParameter_CannotWrite();
		} catch (IllegalArgumentException e) {
		}
		try {
			testToNativeValue_ValidButBadParameter_BadFileName();
		} catch (IllegalArgumentException e) {
		}
	}

	@Ignore
	@Test(expected = IllegalArgumentException.class)
	public void testToNativeValue_ValidButBadParameter_CannotRead()
			throws Exception {
		/**
		 * Warning! 파일 읽기 권한 검사 테스트는 파일 읽기 권한 비활성이 필요한데 이 기능은 운영체제 종속된 기능이므로 생략함.
		 * "Window7 32bit Home Premium K" 에서는 읽기 권한이 강제적으로 활성화 되어 있어 이를 비활성화
		 * 시킬수없다. 비활성 시킬 수 없지만 "읽기 거부 권한"이 있어 이를 활성화 시키면 읽기를 막을 수 있다. 다만 이렇게 할
		 * 경우 File.canRead 로는 알 수 없고 실제적인 파일 읽기 작업중에 어렴품이 알 수 있다. 옐르 들면
		 * FileInputStream 를 새로 생성할때 "액세스가 거부되었습니다" 라는 사유로 FileNotFoundException
		 * 이 발생한다. 이것에 대한 테스틑 java.io.FileTest#
		 * testCanRead_Win7_UserWhoIsNotAdmin_ReadingYesAndReadingDenyYesFile
		 * 참조할것.
		 */
	}

	@Test(expected = IllegalArgumentException.class)
	public void testToNativeValue_ValidButBadParameter_NotRegularFile()
			throws Exception {
		try {
			returnedValue = nativeValueConverter.valueOf(".");
		} catch (IllegalArgumentException e) {
			log.info(
					"'the parameter itemvalue is a bad string' test ok, errormessage={}",
					e.getMessage());
			throw e;
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testToNativeValue_ValidButBadParameter_BadFileName()
			throws Exception {
		try {
			returnedValue = nativeValueConverter.valueOf("aabb$sd$s");
		} catch (IllegalArgumentException e) {
			log.info(
					"'the parameter itemvalue is a bad string' test ok, errormessage={}",
					e.getMessage());
			throw e;
		}
	}

	/**
	 * testToNativeValue_ValidButBadParameter_CannotRead() 메소드는 윈도7에서 파일의 읽기 가능
	 * 권한 수정이 불가능하여 기능 제거로 인한 주석 처리함.
	 */
	/*
	 * @Test(expected = IllegalArgumentException.class) public void
	 * testToNativeValue_ValidButBadParameter_CannotRead() { boolean isSuccess =
	 * expectedValue.setReadable(false); if (!isSuccess) { String errorMessage =
	 * String.format( "fail to set readable[false], file=%s",
	 * expectedValue.getAbsolutePath()); log.warn(errorMessage);
	 * fail(errorMessage); }
	 * 
	 * try { returnedValue = nativeValueConverter.toNativeValue(expectedValue
	 * .getAbsolutePath()); } catch (IllegalArgumentException e) { log.info(
	 * "'the parameter itemvalue is a bad string' test ok, errormessage={}",
	 * e.getMessage()); throw e; } }
	 */

	@Test(expected = IllegalArgumentException.class)
	public void testToNativeValue_ValidButBadParameter_CannotWrite()
			throws Exception {

		boolean isSuccess = expectedValue.setWritable(false);
		if (!isSuccess) {
			String errorMessage = "fail to set writable[false]";
			log.warn(errorMessage);
			fail(errorMessage);
		}

		try {
			returnedValue = nativeValueConverter.valueOf(expectedValue
					.getAbsolutePath());
		} catch (IllegalArgumentException e) {
			log.info(
					"'the parameter itemvalue is a bad string' test ok, errormessage={}",
					e.getMessage());
			throw e;
		}
	}
}
