package kr.pe.sinnori.common.config.nativevalueconverter;

import kr.pe.sinnori.common.config.AbstractNativeValueConverter;
import kr.pe.sinnori.common.util.CommonStaticUtil;

/**
 * jdbc connection url 항목 유효성 검사기  
 * @author "Won Jonghoon"
 *
 */
public class GeneralConverterReturningNoTrimString extends AbstractNativeValueConverter<String> {	

	public GeneralConverterReturningNoTrimString() {
		super(String.class);
	}

	@Override
	public String valueOf(String itemValue) throws IllegalArgumentException {
		if (null == itemValue) {
			String errorMessage = "parameter itemValue is null";
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (itemValue.equals("")) {
			String errorMessage = "parameter itemValue is empty";
			throw new IllegalArgumentException(errorMessage);
		}
		
		// log.info("itemValue[{}] trim 문자열 여부[{}]", itemValue, itemValue.matches("^\\s+"));
		
		if (CommonStaticUtil.hasLeadingOrTailingWhiteSpace(itemValue)) {
			String errorMessage = new StringBuilder()
			.append("parameter itemValue[")
			.append(itemValue)
			.append("] has leading or tailing white space").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		
		
		/*String trimValue = itemValue.trim();
		if (!trimValue.equals(itemValue)) {
			String errorMessage = "parameter itemValue have a trim string";
			throw new IllegalArgumentException(errorMessage);
		}*/

		return itemValue;
	}
}
