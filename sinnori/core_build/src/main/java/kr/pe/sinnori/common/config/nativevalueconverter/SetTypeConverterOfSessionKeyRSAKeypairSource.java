package kr.pe.sinnori.common.config.nativevalueconverter;

import kr.pe.sinnori.common.config.AbstractSetTypeNativeValueConverter;
import kr.pe.sinnori.common.etc.CommonType;
import kr.pe.sinnori.common.etc.CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY;

public class SetTypeConverterOfSessionKeyRSAKeypairSource 
extends AbstractSetTypeNativeValueConverter<CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY> {

	public SetTypeConverterOfSessionKeyRSAKeypairSource() {
		super(RSA_KEYPAIR_SOURCE_OF_SESSIONKEY.class);
	}

	@Override
	protected void initItemValueSet() {
		RSA_KEYPAIR_SOURCE_OF_SESSIONKEY[] nativeValues = CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY.values();
		for (int i=0; i < nativeValues.length; i++) {
			itemValueSet.add(nativeValues[i].toString());
		}
		
	}

	@Override
	public String getSetName() {
		return "the rsa keypair source of sessionkey set";
	}

	@Override
	public RSA_KEYPAIR_SOURCE_OF_SESSIONKEY valueOf(
			String itemValue) throws IllegalArgumentException {
		if (null == itemValue) {
			String errorMessage = "parameter itemValue is null";
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (itemValue.equals("")) {
			String errorMessage = "parameter itemValue is empty";
			throw new IllegalArgumentException(errorMessage);
		}		
		
		
		CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY returnValue = null;
		try {
			returnValue = CommonType.RSA_KEYPAIR_SOURCE_OF_SESSIONKEY.valueOf(itemValue);
		} catch(IllegalArgumentException e) {
			String errorMessage = new StringBuilder("parameter itemValue[")
			.append(itemValue)
			.append("] is not a element of the set ")
			.append(getSetName())
			.append(getStringFromSet())
			.toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		return returnValue;
	}	
	
}
