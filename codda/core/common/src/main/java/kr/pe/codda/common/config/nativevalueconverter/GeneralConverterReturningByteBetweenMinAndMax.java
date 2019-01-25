package kr.pe.codda.common.config.nativevalueconverter;

import kr.pe.codda.common.config.AbstractMinMaxConverter;
import kr.pe.codda.common.util.ComparableComparator;


public class GeneralConverterReturningByteBetweenMinAndMax
	extends AbstractMinMaxConverter<Byte> {	
	public GeneralConverterReturningByteBetweenMinAndMax(Byte min, Byte max) {
		super(min, max, ComparableComparator.<Byte>comparableComparator(), Byte.class);		
	}

	@Override
	protected Byte innerValueOf(String itemValue) throws IllegalArgumentException {
		if (null == itemValue) {
			String errorMessage = "parameter itemValue is null";
			throw new IllegalArgumentException(errorMessage);
		}

		if (itemValue.equals("")) {
			String errorMessage = "parameter itemValue is empty";
			throw new IllegalArgumentException(errorMessage);
		}

		Byte returnedValue = null;
		try {
			returnedValue = Byte.valueOf(itemValue);

		} catch (NumberFormatException e) {
			String errorMessage = new StringBuilder("the parameter itemValue[")
					.append(itemValue).append("] is not a number of ")
					.append(getGenericType().getName())
					.toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		return returnedValue;
	}
}
