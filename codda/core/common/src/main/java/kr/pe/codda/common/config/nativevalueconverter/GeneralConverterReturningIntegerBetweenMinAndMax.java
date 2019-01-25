package kr.pe.codda.common.config.nativevalueconverter;

import kr.pe.codda.common.config.AbstractMinMaxConverter;
import kr.pe.codda.common.util.ComparableComparator;

public class GeneralConverterReturningIntegerBetweenMinAndMax extends AbstractMinMaxConverter<Integer> {	
	public GeneralConverterReturningIntegerBetweenMinAndMax(Integer min, Integer max) {
		super(min, max, ComparableComparator.<Integer>comparableComparator(), Integer.class);		
	}

	@Override
	protected Integer innerValueOf(String itemValue) throws IllegalArgumentException {
		if (null == itemValue) {
			String errorMessage = "parameter itemValue is null";
			throw new IllegalArgumentException(errorMessage);
		}

		if (itemValue.equals("")) {
			String errorMessage = "parameter itemValue is empty";
			throw new IllegalArgumentException(errorMessage);
		}

		Integer returnedValue = null;
		try {
			returnedValue = Integer.valueOf(itemValue);

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
