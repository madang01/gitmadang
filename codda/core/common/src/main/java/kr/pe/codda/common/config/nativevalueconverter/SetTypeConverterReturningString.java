package kr.pe.codda.common.config.nativevalueconverter;

import kr.pe.codda.common.config.AbstractSetTypeNativeValueConverter;
import kr.pe.codda.common.util.CommonStaticUtil;

/**
 * jdbc connection url 항목 유효성 검사기  
 * @author "Won Jonghoon"
 *
 */
public class SetTypeConverterReturningString extends AbstractSetTypeNativeValueConverter<String> {
	
	public SetTypeConverterReturningString(String ... parmValueSet) throws IllegalArgumentException {
		super(String.class);
		
		if (parmValueSet.length == 0) {
			String errorMessage = "parameter parmValueSet is empty";
			throw new IllegalArgumentException(errorMessage);
		}
		
		for (int i=0; i < parmValueSet.length; i++) {
			String itemValue = parmValueSet[i];
			if (CommonStaticUtil.hasLeadingOrTailingWhiteSpace(itemValue)) {
				String errorMessage = new StringBuilder()
				.append("variable parameter parmValueSet[")
				.append(i)
				.append("] has leading or tailing white space").toString();
				throw new IllegalArgumentException(errorMessage);
			}
			itemValueSet.add(itemValue);
		}
	}
	
	@Override
	protected void initItemValueSet() {
		/** 생성자에서 직접 문자열 파라미터들로 받아 문자열 집합을 구성하므로  이곳에서 처리는 필요 없다.  */
		
	}

	@Override
	public String getSetName() {
		return "the string set";
	}
	
	@Override
	public String valueOf(String itemValue) throws IllegalArgumentException {
		if (null == itemValue) {
			String errorMessage = "parameter itemValue is null";
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (itemValue.equals("")) {
			String errorMessage = "parameter itemValue is empty";
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (! itemValueSet.contains(itemValue)) {
			String errorMessage = new StringBuilder("parameter itemValue[")
			.append(itemValue)
			.append("] is not a element of ")
			.append(getSetName())
			.append(getStringFromSet())
			.append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}	
		
		return itemValue;
	}

	

}
