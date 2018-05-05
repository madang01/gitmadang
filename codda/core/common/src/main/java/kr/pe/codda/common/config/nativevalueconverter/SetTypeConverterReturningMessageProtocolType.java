package kr.pe.codda.common.config.nativevalueconverter;

import kr.pe.codda.common.config.AbstractSetTypeNativeValueConverter;
import kr.pe.codda.common.type.MessageProtocolType;

/**
 * 메시지 프로토콜 항목 값 유효성 검사기, DHB:교차 md5 헤더+바디, DJSON:길이+존슨문자열, THB:길이+바디
 * 
 * @author "Won Jonghoon"
 * 
 */
public class SetTypeConverterReturningMessageProtocolType extends
		AbstractSetTypeNativeValueConverter<MessageProtocolType> {

	public SetTypeConverterReturningMessageProtocolType() {
		super(MessageProtocolType.class);
	}

	@Override
	protected void initItemValueSet() {
		MessageProtocolType[] nativeValues = MessageProtocolType
				.values();
		for (int i = 0; i < nativeValues.length; i++) {
			itemValueSet.add(nativeValues[i].toString());
		}
	}

	@Override
	public String getSetName() {
		return "the message protocol set";
	}

	@Override
	public MessageProtocolType valueOf(String itemValue)
			throws IllegalArgumentException {
		if (null == itemValue) {
			String errorMessage = "parameter itemValue is null";
			throw new IllegalArgumentException(errorMessage);
		}

		if (itemValue.equals("")) {
			String errorMessage = "parameter itemValue is empty";
			throw new IllegalArgumentException(errorMessage);
		}

		MessageProtocolType returnValue = null;
		try {
			returnValue = MessageProtocolType.valueOf(itemValue);
		} catch (IllegalArgumentException e) {
			String errorMessage = new StringBuilder("parameter itemValue[")
					.append(itemValue).append("] is not an element of ")
					.append(getSetName()).append(getStringFromSet()).toString();
			throw new IllegalArgumentException(errorMessage);
		}

		return returnValue;
	}
}
