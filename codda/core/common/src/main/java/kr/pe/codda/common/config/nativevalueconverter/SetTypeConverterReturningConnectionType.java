package kr.pe.codda.common.config.nativevalueconverter;

import kr.pe.codda.common.config.AbstractSetTypeNativeValueConverter;
import kr.pe.codda.common.type.ConnectionType;

/**
 * 소캣 랩퍼 클래스인 연결 종류 항목 값 유효성 검사기, NoShareAsyn:비공유+비동기, ShareAsyn:공유+비동기,
 * NoShareSync:비공유+동기.
 * 
 * @author "Won Jonghoon"
 * 
 */
public class SetTypeConverterReturningConnectionType extends
		AbstractSetTypeNativeValueConverter<ConnectionType> {

	public SetTypeConverterReturningConnectionType() {
		super(ConnectionType.class);
	}

	@Override
	protected void initItemValueSet() {
		ConnectionType[] nativeValues = ConnectionType.values();
		for (int i = 0; i < nativeValues.length; i++) {
			itemValueSet.add(nativeValues[i].toString());
		}
	}

	@Override
	public String getSetName() {
		return "the connection type set";
	}

	@Override
	public ConnectionType valueOf(String itemValue)
			throws IllegalArgumentException {
		if (null == itemValue) {
			String errorMessage = "parameter itemValue is null";
			throw new IllegalArgumentException(errorMessage);
		}

		if (itemValue.equals("")) {
			String errorMessage = "parameter itemValue is empty";
			throw new IllegalArgumentException(errorMessage);
		}

		ConnectionType returnValue = null;
		try {
			returnValue = ConnectionType.valueOf(itemValue);
		} catch (IllegalArgumentException e) {
			String errorMessage = new StringBuilder("parameter itemValue[")
					.append(itemValue).append("] is not an element of ")
					.append(getSetName()).append(getStringFromSet()).toString();
			throw new IllegalArgumentException(errorMessage);
		}

		return returnValue;
	}
}
