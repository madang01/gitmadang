package kr.pe.sinnori.common.config.nativevalueconverter;

import kr.pe.sinnori.common.config.AbstractSetTypeNativeValueConverter;
import kr.pe.sinnori.common.etc.CommonType;

public class SetTypeConverterReturningSessionkeyPrivateKeyEncoding 
extends AbstractSetTypeNativeValueConverter<CommonType.SYMMETRIC_KEY_ENCODING> {
	public SetTypeConverterReturningSessionkeyPrivateKeyEncoding() {
		super(CommonType.SYMMETRIC_KEY_ENCODING.class);
	}

	@Override
	protected void initItemValueSet() {		
		CommonType.SYMMETRIC_KEY_ENCODING[] nativeValues = CommonType.SYMMETRIC_KEY_ENCODING.values();
		for (int i=0; i < nativeValues.length; i++) {
			itemValueSet.add(nativeValues[i].toString());
		}
	}
	
	@Override
	public String getSetName() {
		return "the symmetric key encoding set";
	}
		
	@Override
	public CommonType.SYMMETRIC_KEY_ENCODING valueOf(String itemValue) throws IllegalArgumentException {
		if (null == itemValue) {
			String errorMessage = "parameter itemValue is null";
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (itemValue.equals("")) {
			String errorMessage = "parameter itemValue is empty";
			throw new IllegalArgumentException(errorMessage);
		}
		
		CommonType.SYMMETRIC_KEY_ENCODING returnValue = null;
		try {
			returnValue = CommonType.SYMMETRIC_KEY_ENCODING.valueOf(itemValue);
		} catch(IllegalArgumentException e) {
			String errorMessage = new StringBuilder("parameter itemValue[")
			.append(itemValue)
			.append("] is not a element of ")
			.append(getSetName())
			.append(getStringFromSet())
			.toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		return returnValue;
	}
}
