package kr.pe.sinnori.common.config.nativevalueconverter;

import kr.pe.sinnori.common.config.AbstractMinMaxConverter;

import org.apache.commons.collections4.ComparatorUtils;

public class GeneralConverterReturningShortBetweenMinAndMax extends AbstractMinMaxConverter<Short> {	
	public GeneralConverterReturningShortBetweenMinAndMax(Short min, Short max) {
		super(min, max, ComparatorUtils.<Short>naturalComparator(), Short.class);		
	}

	@Override
	protected Short innerValueOf(String itemValue) throws IllegalArgumentException {
		if (null == itemValue) {
			String errorMessage = "parameter itemValue is null";
			throw new IllegalArgumentException(errorMessage);
		}

		if (itemValue.equals("")) {
			String errorMessage = "parameter itemValue is empty";
			throw new IllegalArgumentException(errorMessage);
		}

		Short returnedValue = null;
		try {
			returnedValue = Short.valueOf(itemValue);

		} catch (NumberFormatException e) {
			String errorMessage = new StringBuilder("parameter itemValue[")
					.append(itemValue).append("] is not a number of ")
					.append(getGenericType().getName())
					.toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		return returnedValue;
	}
}
