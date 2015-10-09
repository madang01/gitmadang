package kr.pe.sinnori.common.config.nativevalueconverter;

import java.nio.ByteOrder;

import kr.pe.sinnori.common.config.AbstractSetTypeNativeValueConverter;

public class SetTypeConverterReturningByteOrder extends
		AbstractSetTypeNativeValueConverter<ByteOrder> {
	public SetTypeConverterReturningByteOrder() {
		super(ByteOrder.class);
	}

	@Override
	protected void initItemValueSet() {
		itemValueSet.add(ByteOrder.LITTLE_ENDIAN.toString());
		itemValueSet.add(ByteOrder.BIG_ENDIAN.toString());
	}

	@Override
	public String getSetName() {
		return "the byteorder set";
	}

	@Override
	public ByteOrder valueOf(String itemValue)
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
					.append(itemValue).append("] is not a element of ")
					.append(getSetName()).append(getStringFromSet())
					.append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		ByteOrder ret = null;
		if (itemValue.equals(ByteOrder.LITTLE_ENDIAN.toString())) {
			ret = ByteOrder.LITTLE_ENDIAN;
		} else {
			ret = ByteOrder.BIG_ENDIAN;
		}

		return ret;
	}
}
