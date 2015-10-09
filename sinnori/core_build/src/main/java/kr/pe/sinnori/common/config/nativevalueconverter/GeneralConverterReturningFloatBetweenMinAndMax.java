package kr.pe.sinnori.common.config.nativevalueconverter;

import kr.pe.sinnori.common.config.AbstractMinMaxConverter;

import org.apache.commons.collections4.ComparatorUtils;

public class GeneralConverterReturningFloatBetweenMinAndMax extends AbstractMinMaxConverter<Float> {	
	public GeneralConverterReturningFloatBetweenMinAndMax(Float min, Float max) {
		super(min, max, ComparatorUtils.<Float>naturalComparator(), Float.class);		
	}

	@Override
	protected Float innerValueOf(String itemValue) throws IllegalArgumentException {
		if (null == itemValue) {
			String errorMessage = "parameter itemValue is null";
			throw new IllegalArgumentException(errorMessage);
		}

		if (itemValue.equals("")) {
			String errorMessage = "parameter itemValue is empty";
			throw new IllegalArgumentException(errorMessage);
		}

		Float returnedValue = null;
		try {
			returnedValue = Float.valueOf(itemValue);

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
