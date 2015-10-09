package kr.pe.sinnori.common.config;

import kr.pe.sinnori.common.config.AbstractNativeValueConverter;

/**
 * {@link AbstractNativeValueConverter} class test interface
 * @author Won Jonghoon
 *
 */
public interface NativeValueConverterTestIF {
	/**
	 * {@link AbstractNativeValueConverter#valueOf(String)} method test
	 * return value compare the expected value 
	 */
	public void setup();
	/**
	 * Constructor method test
	 */
	public void testConstructor() throws Exception;
	/**
	 * {@link AbstractNativeValueConverter#valueOf(String)} method's
	 * the expected value comparison test
	 */
	public void testToNativeValue_ExpectedValueComparison();
	/**
	 * {@link AbstractNativeValueConverter#valueOf(String)} method's
	 * the null parameter test
	 */
	public void testToNativeValue_NullParameter() throws Exception;
	/**
	 * {@link AbstractNativeValueConverter#valueOf(String)} method's
	 * the empty string parameter test
	 */
	public void testToNativeValue_EmptyStringParameter() throws Exception;
	/**
	 * {@link AbstractNativeValueConverter#valueOf(String)} method's
	 * the valid but bad parameter test
	 * info) the valid parameter is not null and not a empty string.
	 * info) the bad parameter is 
	 */
	public void testToNativeValue_ValidButBadParameter() throws Exception;
}
