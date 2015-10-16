package kr.pe.sinnori.common.config.nativevalueconverter;

import kr.pe.sinnori.common.config.AbstractSetTypeNativeValueConverter;

public class SetTypeConverterReturningBoolean extends
		AbstractSetTypeNativeValueConverter<Boolean> {
	public SetTypeConverterReturningBoolean() {
		super(Boolean.class);
	}

	@Override
	protected void initItemValueSet() {
		itemValueSet.add(Boolean.FALSE.toString());
		itemValueSet.add(Boolean.TRUE.toString());
	}

	@Override
	public String getSetName() {
		return "the boolean set";
	}

	@Override
	public Boolean valueOf(String itemValue)
			throws IllegalArgumentException {
		if (null == itemValue) {
			String errorMessage = "parameter itemValue is null";
			throw new IllegalArgumentException(errorMessage);
		}

		if (itemValue.equals("")) {
			String errorMessage = "parameter itemValue is empty";
			throw new IllegalArgumentException(errorMessage);
		}

		if (!itemValueSet.contains(itemValue)) {
			String errorMessage = new StringBuilder("parameter itemValue[")
					.append(itemValue).append("] is not an element of ")
					.append(getSetName()).append(getStringFromSet())
					.append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		Boolean returnedValue = null;

		/**
		 * Warning) Boolean.valueOf("king") return Boolean.FALSE; if you want to
		 * use Boolean.valueOf method then you must check the parameter is the
		 * element of the boolean set;
		 */
		/*
		 * if (itemValue.equals(Boolean.FALSE.toString())) { returnedValue =
		 * Boolean.FALSE; } else if (itemValue.equals(Boolean.TRUE.toString()))
		 * { returnedValue = Boolean.TRUE; } else { String errorMessage = new
		 * StringBuilder("parameter itemValue[") .append(itemValue)
		 * .append("] is not a element of ") .append(getSetName())
		 * .append(getStringFromSet()) .append("]").toString(); throw new
		 * IllegalArgumentException(errorMessage); }
		 */

		returnedValue = Boolean.valueOf(itemValue);

		return returnedValue;
	}

}
