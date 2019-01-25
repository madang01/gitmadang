package kr.pe.codda.common.config.nativevalueconverter;

import kr.pe.codda.common.config.AbstractMinMaxConverter;
import kr.pe.codda.common.util.ComparableComparator;

public class GeneralConverterReturningShortBetweenMinAndMax extends AbstractMinMaxConverter<Short> {	
	public GeneralConverterReturningShortBetweenMinAndMax(Short min, Short max) {
		super(min, max, ComparableComparator.<Short>comparableComparator(), Short.class);		
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
