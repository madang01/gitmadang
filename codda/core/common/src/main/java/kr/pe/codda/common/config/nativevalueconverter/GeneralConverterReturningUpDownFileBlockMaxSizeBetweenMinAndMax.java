package kr.pe.codda.common.config.nativevalueconverter;

import kr.pe.codda.common.config.AbstractNativeValueConverter;

/**
 * 파일 송수신 파일 블락 최대 크기 항목의 값 유효성 검사기
 * 
 * @author "Won Jonghoon"
 * 
 */
public class GeneralConverterReturningUpDownFileBlockMaxSizeBetweenMinAndMax extends
		AbstractNativeValueConverter<Integer> {
	private int min;
	private int max;

	/**
	 * 파일 송수신 파일 블락 최대 크기 항목의 값 유효성 검사기 생성자
	 * 
	 * @param min
	 *            최소값, 주) 1024 배수 검사 없음
	 * @param max
	 *            최대값, 주) 1024 배수 검사 없음
	 * @throws IllegalArgumentException
	 *             최소값이 최대값 보다 클때 던지는 예외
	 */
	public GeneralConverterReturningUpDownFileBlockMaxSizeBetweenMinAndMax(int min, int max)
			throws IllegalArgumentException {
		super(Integer.class);
		if (min < 0) {
			String errorMessage = new StringBuilder("parameter min[")
					.append(min).append("] is less than zero").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		/*if (max < 0) {
			String errorMessage = new StringBuilder("parameter max[")
					.append(max).append("] is less than zero").toString();
			throw new IllegalArgumentException(errorMessage);
		}*/
		
		if (max < 1024) {
			String errorMessage = new StringBuilder("parameter max[")
					.append(max).append("] is less than 1024").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (min > max) {
			String errorMessage = new StringBuilder("parameter min[")
					.append(min).append("] is greater than parameter max[")
					.append(max).append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		/*
		 * if (min % 1024 != 0) { String errorMessage = new
		 * StringBuilder("parameter min[") .append(min)
		 * .append("] is not a multiple of 1024").toString(); throw new
		 * ConfigException(errorMessage); }
		 * 
		 * if (max % 1024 != 0) { String errorMessage = new
		 * StringBuilder("parameter max[") .append(max)
		 * .append("] is not a multiple of 1024").toString(); throw new
		 * ConfigException(errorMessage); }
		 */

		this.min = min;
		this.max = max;
	}

	public int getMin() {
		return min;
	}
	
	public int getMax() {
		return max;
	}
	
	@Override
	public Integer valueOf(String itemValue)
			throws IllegalArgumentException {
		if (null == itemValue) {
			String errorMessage = "parameter itemValue is null";
			throw new IllegalArgumentException(errorMessage);
		}

		if (itemValue.equals("")) {
			String errorMessage = "parameter itemValue is empty";
			throw new IllegalArgumentException(errorMessage);
		}

		int returnedValue;
		try {
			returnedValue = Integer.parseInt(itemValue);
		} catch (NumberFormatException e) {
			String errorMessage = new StringBuilder("parameter itemValue[")
					.append(itemValue).append("] is not integer type")
					.toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (returnedValue < min) {
			String errorMessage = new StringBuilder("parameter itemValue[")
					.append(itemValue).append("] is less than min[")
					.append(min).append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (returnedValue > max) {
			String errorMessage = new StringBuilder("parameter itemValue[")
					.append(itemValue).append("] is greater than max[")
					.append(max).append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (returnedValue % 1024 != 0) {
			String errorMessage = new StringBuilder("parameter itemValue[")
					.append(itemValue).append("] is not a multiple of 1024")
					.toString();
			throw new IllegalArgumentException(errorMessage);
		}

		return returnedValue;
	}
}
