package kr.pe.codda.common.config.nativevalueconverter;

import org.apache.commons.collections4.ComparatorUtils;

import kr.pe.codda.common.config.AbstractMinMaxConverter;

public class GeneralConverterReturningLongBetweenMinAndMax extends AbstractMinMaxConverter<Long> {	
	public GeneralConverterReturningLongBetweenMinAndMax(Long min, Long max) {
		super(min, max, ComparatorUtils.<Long>naturalComparator(), Long.class);		
	}

	@Override
	protected Long innerValueOf(String itemValue) throws IllegalArgumentException {
		if (null == itemValue) {
			String errorMessage = "parameter itemValue is null";
			throw new IllegalArgumentException(errorMessage);
		}

		if (itemValue.equals("")) {
			String errorMessage = "parameter itemValue is empty";
			throw new IllegalArgumentException(errorMessage);
		}

		Long returnedValue = null;
		try {
			returnedValue = Long.valueOf(itemValue);

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