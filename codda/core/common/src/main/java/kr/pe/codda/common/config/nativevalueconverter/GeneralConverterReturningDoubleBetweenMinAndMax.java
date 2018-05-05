package kr.pe.codda.common.config.nativevalueconverter;

import org.apache.commons.collections4.ComparatorUtils;

import kr.pe.codda.common.config.AbstractMinMaxConverter;

public class GeneralConverterReturningDoubleBetweenMinAndMax extends AbstractMinMaxConverter<Double> {	
	public GeneralConverterReturningDoubleBetweenMinAndMax(Double min, Double max) {
		super(min, max, ComparatorUtils.<Double>naturalComparator(), Double.class);		
	}

	@Override
	protected Double innerValueOf(String itemValue) throws IllegalArgumentException {
		if (null == itemValue) {
			String errorMessage = "parameter itemValue is null";
			throw new IllegalArgumentException(errorMessage);
		}

		if (itemValue.equals("")) {
			String errorMessage = "parameter itemValue is empty";
			throw new IllegalArgumentException(errorMessage);
		}

		Double returnedValue = null;
		try {
			returnedValue = Double.valueOf(itemValue);

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