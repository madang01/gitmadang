package kr.pe.sinnori.common.config.nativevalueconverter;

import kr.pe.sinnori.common.config.AbstractSetTypeNativeValueConverter;

/**
 * jdbc connection url 항목 유효성 검사기  
 * @author "Won Jonghoon"
 *
 */
public class SetTypeConverterReturningInteger extends AbstractSetTypeNativeValueConverter<Integer> {
	
	public SetTypeConverterReturningInteger(String ... parmValueSet) throws IllegalArgumentException {
		super(Integer.class);
		
		if (parmValueSet.length == 0) {
			String errorMessage = "parameter parmValueSet is empty";
			throw new IllegalArgumentException(errorMessage);
		}
		
		
		for (String value : parmValueSet) {			
			try {
				Integer.parseInt(value);
			} catch(NumberFormatException e) {
				String errorMessage = new StringBuilder("the elemment[")
				.append(value)
				.append("] of parmValueSet is not integer type").toString();
				throw new IllegalArgumentException(errorMessage);
			}
			
			itemValueSet.add(value);
		}
	}
	
	@Override
	protected void initItemValueSet() {
		/** 생성자에서 직접 정수 문자열 파라미터들로 받아 정수 문자열 집합을 구성하므로  이곳에서 처리는 필요 없다.  */
	}

	@Override
	public String getSetName() {
		return "the integer set";
	}
	
	
	
	@Override
	public Integer valueOf(String itemValue) throws IllegalArgumentException {
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
		
		try {
			return Integer.valueOf(itemValue);
		} catch(NumberFormatException e) {
			String errorMessage = new StringBuilder("parameter itemValue[")
			.append(itemValue)
			.append("] is not integer type").toString();
			throw new IllegalArgumentException(errorMessage);
		}
	}
	
}
