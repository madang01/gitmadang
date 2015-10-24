package kr.pe.sinnori.common.config.dependonvalidchecker;

import static org.junit.Assert.fail;

import java.nio.charset.Charset;
import java.util.Properties;

import jdk.nashorn.internal.ir.annotations.Ignore;
import kr.pe.sinnori.common.config.AbstractDependOnValidChecker;
import kr.pe.sinnori.common.config.itemidinfo.ItemIDInfo;
import kr.pe.sinnori.common.config.nativevalueconverter.GeneralConverterReturningCharset;
import kr.pe.sinnori.common.config.nativevalueconverter.GeneralConverterReturningIntegerBetweenMinAndMax;
import kr.pe.sinnori.common.config.nativevalueconverter.GeneralConverterReturningLongBetweenMinAndMax;
import kr.pe.sinnori.common.exception.SinnoriConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MinDependOnMaxValidCheckerTest {
	Logger log = LoggerFactory.getLogger(MinDependOnMaxValidCheckerTest.class);

	private final String prefixOfPart = "project.sample_base.";
	private final Properties sinnoriConfigFileProperties = new Properties();

	private ItemIDInfo<Long> longTypeDependedItemIDInfo = null;
	private ItemIDInfo<Long> longTypeDependentItemIDInfo = null;
	private AbstractDependOnValidChecker longTypeMinDependOnMaxValidChecker = null;

	
	@Before
	public void setup() {
		try {
			String dependentTargetItemID = "server.pool.executor_processor.max_size.value";
			longTypeDependedItemIDInfo = new ItemIDInfo<Long>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT, dependentTargetItemID,
					"서버 비지니스 로직 수행 담당 쓰레드 최대 갯수", "22", true,
					new GeneralConverterReturningLongBetweenMinAndMax(
							2L, Long.MAX_VALUE - 10));

			sinnoriConfigFileProperties.put(prefixOfPart
					+ dependentTargetItemID, "" + (Long.MAX_VALUE-30));

			String dependentSourceItemID = "server.pool.executor_processor.size.value";
			longTypeDependentItemIDInfo = new ItemIDInfo<Long>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT, dependentSourceItemID,
					"서버 비지니스 로직 수행 담당 쓰레드 갯수", "11", true,
					new GeneralConverterReturningLongBetweenMinAndMax(
							1L, Long.MAX_VALUE - 11));

			sinnoriConfigFileProperties.put(prefixOfPart
					+ dependentSourceItemID, "1");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (SinnoriConfigurationException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		try {
			longTypeMinDependOnMaxValidChecker = new MinDependOnMaxValidChecker<Long>(
					longTypeDependentItemIDInfo, longTypeDependedItemIDInfo,
					Long.class);
		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (SinnoriConfigurationException e) {
			fail(e.getMessage());
		}
	}

	@Test(expected=IllegalArgumentException.class)
	public void testConstructor_BadGeneralType_dependentTargetItemIDInfo() throws Exception {
		try {
			String dependentTargetItemID = "server.pool.executor_processor.max_size.value";
			ItemIDInfo<?> dependentTargetItemIDInfo = new ItemIDInfo<Long>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT, dependentTargetItemID,
					"서버 비지니스 로직 수행 담당 쓰레드 최대 갯수", "22", true,
					new GeneralConverterReturningLongBetweenMinAndMax(
							2L, Long.MAX_VALUE - 10));


			String dependentSourceItemID = "server.pool.executor_processor.size.value";
			ItemIDInfo<?> dependentSourceItemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT, dependentSourceItemID,
					"서버 비지니스 로직 수행 담당 쓰레드 갯수", "11", true,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							2, Integer.MAX_VALUE - 10));
			
			@SuppressWarnings({ "unused", "unchecked" })
			AbstractDependOnValidChecker minDependOnMaxValidChecker = new MinDependOnMaxValidChecker<Integer>(
					(ItemIDInfo<Integer>)dependentTargetItemIDInfo,
					(ItemIDInfo<Integer>)dependentSourceItemIDInfo,
					Integer.class);

			fail("bad generic test fail");
		} catch (IllegalArgumentException e) {	
			log.info("this class's generic type T[Integer] is different from dependent target converter type T[Long] test ok", e);
			throw e;
		}

	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructor_BadGeneralType_dependentSourceItemIDInfo() throws Exception {
		try {
			String dependentTargetItemID = "server.pool.executor_processor.max_size.value";
			ItemIDInfo<?> dependentTargetItemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT, dependentTargetItemID,
					"서버 비지니스 로직 수행 담당 쓰레드 최대 갯수", "22", true,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							2, Integer.MAX_VALUE - 10));


			String dependentSourceItemID = "server.pool.executor_processor.size.value";
			ItemIDInfo<?> dependentSourceItemIDInfo = new ItemIDInfo<Long>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT, dependentSourceItemID,
					"서버 비지니스 로직 수행 담당 쓰레드 갯수", "11", true,
					new GeneralConverterReturningLongBetweenMinAndMax(
							2L, Long.MAX_VALUE - 10));
			
			@SuppressWarnings({ "unused", "unchecked" })
			AbstractDependOnValidChecker minDependOnMaxValidChecker = new MinDependOnMaxValidChecker<Integer>(
					(ItemIDInfo<Integer>)dependentTargetItemIDInfo,
					(ItemIDInfo<Integer>)dependentSourceItemIDInfo,
					Integer.class);

			fail("bad generic test fail");
		} catch (IllegalArgumentException e) {	
			log.info("this class's generic type T[Integer] is different from dependent source converter type T[Long] test ok", e);
			throw e;
		}

	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructor_BadInstance_MaxNativeValueConverter() throws Exception {
		ItemIDInfo<?> charsetTypeMaxItemIDInfo = null;
		ItemIDInfo<?> integerTypeMinItemIDInfo = null;
		try {
			String dependentTargetItemID = "server.pool.executor_processor.max_size.value";
			charsetTypeMaxItemIDInfo = new ItemIDInfo<Charset>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT, dependentTargetItemID,
					"서버 비지니스 로직 수행 담당 쓰레드 최대 갯수", "1", false,
					new GeneralConverterReturningCharset());

			String dependentSourceItemID = "server.pool.executor_processor.size.value";
			integerTypeMinItemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					dependentSourceItemID,
					"서버 비지니스 로직 수행 담당 쓰레드 갯수",
					"1",
					false,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							1, Integer.MAX_VALUE));

		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (SinnoriConfigurationException e) {
			fail(e.getMessage());
		}

		try {
			@SuppressWarnings({ "unused", "unchecked" })
			AbstractDependOnValidChecker minDependOnMaxValidChecker = new MinDependOnMaxValidChecker<Integer>(
					(ItemIDInfo<Integer>)integerTypeMinItemIDInfo, (ItemIDInfo<Integer>)charsetTypeMaxItemIDInfo,
					Integer.class);			
		} catch (IllegalArgumentException e) {
			log.info(
					"bad max nativeConverter's instance test ok",
					e);
			throw e;
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructor_BadInstance_MinNativeValueConverter() throws Exception {
		ItemIDInfo<?> integerTypeMaxItemIDInfo = null;
		ItemIDInfo<?> charsetTypeMinItemIDInfo = null;
		try {
			String dependentTargetItemID = "server.pool.executor_processor.max_size.value";
			integerTypeMaxItemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT, dependentTargetItemID,
					"서버 비지니스 로직 수행 담당 쓰레드 최대 갯수", "1", false,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							1, Integer.MAX_VALUE));

			String dependentSourceItemID = "server.pool.executor_processor.size.value";
			charsetTypeMinItemIDInfo = new ItemIDInfo<Charset>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					dependentSourceItemID,
					"서버 비지니스 로직 수행 담당 쓰레드 갯수",
					"1",
					false,
					new GeneralConverterReturningCharset());

		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (SinnoriConfigurationException e) {
			fail(e.getMessage());
		}

		try {
			@SuppressWarnings({ "unused", "unchecked" })
			AbstractDependOnValidChecker minDependOnMaxValidChecker = new MinDependOnMaxValidChecker<Integer>(
					(ItemIDInfo<Integer>)charsetTypeMinItemIDInfo, (ItemIDInfo<Integer>)integerTypeMaxItemIDInfo,
					Integer.class);			
		} catch (IllegalArgumentException e) {
			log.info(
					"bad min nativeConverter's instance test ok, errormessage={}",
					e.getMessage());
			throw e;
		}
	}

	@Test(expected=IllegalArgumentException.class)
	public void testConstructor_GenericTypeDifferent_MaxNativeValueConverter() throws Exception {
		ItemIDInfo<?> longTypeMaxItemIDInfo = null;
		ItemIDInfo<?> integerTypeMinItemIDInfo = null;
		try {
			String dependentTargetItemID = "server.pool.executor_processor.max_size.value";
			longTypeMaxItemIDInfo = new ItemIDInfo<Long>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT, dependentTargetItemID,
					"서버 비지니스 로직 수행 담당 쓰레드 최대 갯수", "1", true,
					new GeneralConverterReturningLongBetweenMinAndMax(
							1L, Long.MAX_VALUE));

			String dependentSourceItemID = "server.pool.executor_processor.size.value";
			integerTypeMinItemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					dependentSourceItemID,
					"서버 비지니스 로직 수행 담당 쓰레드 갯수",
					"1",
					true,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							1, Integer.MAX_VALUE));

		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (SinnoriConfigurationException e) {
			fail(e.getMessage());
		}

		try {
			@SuppressWarnings({ "unused", "unchecked" })
			AbstractDependOnValidChecker minDependOnMaxValidChecker = new MinDependOnMaxValidChecker<Integer>(
					(ItemIDInfo<Integer>)integerTypeMinItemIDInfo, (ItemIDInfo<Integer>)longTypeMaxItemIDInfo,
					Integer.class);
		} catch (IllegalArgumentException e) {
			log.info(
					"bad max nativeConverter's generic type T test ok, errormessage={}",
					e.getMessage());
			throw e;
		}
	}

	@Test(expected=IllegalArgumentException.class)
	public void testConstructor_GenericTypeDifferent_MinNativeValueConverter() throws Exception {
		ItemIDInfo<Integer> integerTypeMaxItemIDInfo = null;
		ItemIDInfo<?> longTypeMinItemIDInfo = null;
		try {
			String dependentTargetItemID = "server.pool.executor_processor.max_size.value";
			integerTypeMaxItemIDInfo = new ItemIDInfo<Integer>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT,
					dependentTargetItemID,
					"서버 비지니스 로직 수행 담당 쓰레드 최대 갯수",
					"1",
					true,
					new GeneralConverterReturningIntegerBetweenMinAndMax(
							1, Integer.MAX_VALUE));

			String dependentSourceItemID = "server.pool.executor_processor.size.value";
			longTypeMinItemIDInfo = new ItemIDInfo<Long>(
					ItemIDInfo.ConfigurationPart.PROJECT,
					ItemIDInfo.ViewType.TEXT, dependentSourceItemID,
					"서버 비지니스 로직 수행 담당 쓰레드 갯수", "1", true,
					new GeneralConverterReturningLongBetweenMinAndMax(
							1L, Long.MAX_VALUE));

		} catch (IllegalArgumentException e) {
			fail(e.getMessage());
		} catch (SinnoriConfigurationException e) {
			fail(e.getMessage());
		}

		try {
			@SuppressWarnings({ "unused", "unchecked" })
			AbstractDependOnValidChecker minDependOnMaxValidChecker = new MinDependOnMaxValidChecker<Integer>(
					(ItemIDInfo<Integer>)longTypeMinItemIDInfo, integerTypeMaxItemIDInfo,
					Integer.class);
		} catch (IllegalArgumentException e) {
			log.info(
					"bad min nativeConverter's generic type T test ok, errormessage={}",
					e.getMessage());
			throw e;
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_NullParameter_dependentSourceItemIDInfo()
			throws Exception {
		try {
			@SuppressWarnings("unused")
			AbstractDependOnValidChecker minDependOnMaxValidChecker = new MinDependOnMaxValidChecker<Long>(
					null, longTypeDependedItemIDInfo, Long.class);
		} catch (IllegalArgumentException e) {
			log.info(
					"'the parameter dependentSourceItemIDInfo is null' test ok, errormessage={}",
					e.getMessage());
			throw e;
		} catch (SinnoriConfigurationException e) {
			fail(e.getMessage());
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_NullParameter_dependentTargetItemIDInfo()
			throws Exception {
		try {
			@SuppressWarnings("unused")
			AbstractDependOnValidChecker minDependOnMaxValidChecker = new MinDependOnMaxValidChecker<Long>(
					longTypeDependentItemIDInfo, null, Long.class);
		} catch (IllegalArgumentException e) {
			log.info(
					"'the parameter dependentTargetItemIDInfo is null' test ok, errormessage={}",
					e.getMessage());
			throw e;
		} catch (SinnoriConfigurationException e) {
			fail(e.getMessage());
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_NullParameter_genericTypeClass()
			throws Exception {
		try {
			@SuppressWarnings("unused")
			AbstractDependOnValidChecker minDependOnMaxValidChecker = new MinDependOnMaxValidChecker<Long>(
					longTypeDependentItemIDInfo, longTypeDependedItemIDInfo,
					null);
		} catch (IllegalArgumentException e) {
			log.info(
					"'the parameter genericTypeClass is null' test ok, errormessage={}",
					e.getMessage());
			throw e;
		} catch (SinnoriConfigurationException e) {
			fail(e.getMessage());
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIsValid_NullParameter_sinnoriConfigFileProperties()
			throws Exception {
		try {
			longTypeMinDependOnMaxValidChecker.isValid(null, prefixOfPart);
		} catch (IllegalArgumentException e) {
			String errorMessage = new StringBuilder(
					"'the parameter sinnoriConfigFileProperties is null' test ok, errormessage=")
					.append(e.getMessage()).toString();
			log.info(errorMessage);
			throw e;
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIsValid_NullParameter_prefixOfPart() throws Exception {
		try {
			longTypeMinDependOnMaxValidChecker.isValid(
					sinnoriConfigFileProperties, null);
		} catch (IllegalArgumentException e) {
			String errorMessage = new StringBuilder(
					"'the parameter prefixOfPart is null' test ok, errormessage=")
					.append(e.getMessage()).toString();
			log.info(errorMessage);
			throw e;
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIsValid_ValidButBadParameter_sourceProperties_NoDependentSourceItemKey()
			throws Exception {
		String dependentSourceItemID = "server.pool.executor_processor.size.value";
		String dependendtSourceItemKey = prefixOfPart + dependentSourceItemID;
		sinnoriConfigFileProperties.remove(dependendtSourceItemKey);

		try {
			longTypeMinDependOnMaxValidChecker.isValid(
					sinnoriConfigFileProperties, prefixOfPart);
		} catch (IllegalArgumentException e) {
			String errorMessage = new StringBuilder(
					"'the variable dependendtSourceItemKey[")
					.append(dependendtSourceItemKey)
					.append("] does not exist at the variable sinnoriConfigFileProperties' test ok, errormessage=")
					.append(e.getMessage()).toString();
			log.info(errorMessage);
			throw e;
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIsValid_ValidButBadParameter_sourceProperties_NoDependentTargetItemKey()
			throws Exception {
		String dependentTargetItemID = "server.pool.executor_processor.max_size.value";
		String dependentTargetItemKey = prefixOfPart + dependentTargetItemID;
		sinnoriConfigFileProperties.remove(dependentTargetItemKey);
		
		//log.info(sinnoriConfigFileProperties.toString());

		try {
			longTypeMinDependOnMaxValidChecker.isValid(
					sinnoriConfigFileProperties, prefixOfPart);
		} catch (IllegalArgumentException e) {
			String errorMessage = new StringBuilder(
					"'the variable dependentTargetItemKey[")
					.append(dependentTargetItemKey)
					.append("] does not exist at the variable sinnoriConfigFileProperties' test ok, errormessage=")
					.append(e.getMessage()).toString();
			log.info(errorMessage);
			throw e;
		}
	}
	
	@Ignore @Test
	public void testIsValid_ValidButBadParameter_prefixOfPart() throws Exception {
		/**
		 * ValidButBadParameter 테스트는 파라미터 값이 유효하지만 잘못된 값을 가지는 경우에 대한 테스트로
		 * isValid 메소드에서는 파라미터 sourceProperties 에 키 값이 존재 하지 않는 경우에 대한 테스트이다.
		 * 키 값이 존재 하지 안흔 경우는 2가지로 나뉘는데,
		 * 첫번째 키가 잘못되었을 경우와
		 * 마지막 두번째 있어야 할 키값이 없는 경우이다.
		 *  
		 * 키는 두개로 구성되는데 참고2 처럼 있어야할 키 값이 없는 경우로 각각 테스를 수행하므로
		 * 파마미터 prefixOfPart 가 잘못된 값을 가져 키값이 잘못되는 경우에 대한 테스트는 생략한다.
		 * 설정 파일의 경우 키 값이 없는 경우가 대부분 이므로 있어야할 키가 없는 테스트인 참고2를 우선하였다.
		 * 
		 * --- 참고1) sourceProperties 의 키 ---
		 * (1) dependentSourceItemKey = 파라미터 prefixOfPart + 생성자 파라미터 dependentSourceItemIDInfo.getItemID();
		 * (2) dependentTargetItemKey = 파라미터 prefixOfPart + 생성자 파라미터 dependentTargetItemIDInfo.getItemID();
		 *
		 * --- 참고2) 파라미터 sourceProperties 에 있어야할 키값이 없는 경우 테스트  ---
		 * (1) testIsValid_ValidButBadParameter_sourceProperties_NoDependentSourceItemKey
		 * (2) testIsValid_ValidButBadParameter_sourceProperties_NoDependentTargetItemKey
		 */
	}
	
	
	@Test
	public void testIsValid_ExpectedValueComparison() {
		boolean expectedValue = true;
		boolean returnedValue = false;
		try {
			returnedValue = longTypeMinDependOnMaxValidChecker.isValid(
					sinnoriConfigFileProperties, prefixOfPart);
		} catch (IllegalArgumentException e) {
			log.info("IllegalArgumentException", e);
			fail(e.getMessage());
		}
		
		
		org.junit.Assert.assertThat("the expected value comparison",
				returnedValue, org.hamcrest.CoreMatchers.equalTo(expectedValue));
	}
	
	
}
