package kr.pe.codda.common.config.nativevalueconverter;

import kr.pe.codda.common.config.AbstractSetTypeNativeValueConverter;
import kr.pe.codda.common.type.SessionKey;

public class SetTypeConverterOfSessionKeyRSAKeypairSource 
extends AbstractSetTypeNativeValueConverter<SessionKey.RSAKeypairSourceType> {

	public SetTypeConverterOfSessionKeyRSAKeypairSource() {
		super(SessionKey.RSAKeypairSourceType.class);
	}

	@Override
	protected void initItemValueSet() {
		SessionKey.RSAKeypairSourceType[] nativeValues = SessionKey.RSAKeypairSourceType.values();
		for (int i=0; i < nativeValues.length; i++) {
			itemValueSet.add(nativeValues[i].toString());
		}
		
	}

	@Override
	public String getSetName() {
		return "the rsa keypair source of sessionkey set";
	}

	@Override
	public SessionKey.RSAKeypairSourceType valueOf(
			String itemValue) throws IllegalArgumentException {
		if (null == itemValue) {
			String errorMessage = "parameter itemValue is null";
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (itemValue.equals("")) {
			String errorMessage = "parameter itemValue is empty";
			throw new IllegalArgumentException(errorMessage);
		}		
		
		
		SessionKey.RSAKeypairSourceType returnValue = null;
		try {
			returnValue = SessionKey.RSAKeypairSourceType.valueOf(itemValue);
		} catch(IllegalArgumentException e) {
			String errorMessage = new StringBuilder("parameter itemValue[")
			.append(itemValue)
			.append("] is not an element of the set ")
			.append(getSetName())
			.append(getStringFromSet())
			.toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		return returnValue;
	}	
	
}
