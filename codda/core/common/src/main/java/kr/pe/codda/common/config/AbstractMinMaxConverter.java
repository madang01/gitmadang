package kr.pe.codda.common.config;

import java.util.Comparator;

public abstract class AbstractMinMaxConverter<T extends Number> extends AbstractNativeValueConverter<T> {

	protected T min;
	protected T max;
	private Comparator<T> typeComparator = null;

	public AbstractMinMaxConverter(T min, T max, Comparator<T> typeComparator, Class<T> genericType) {
		super(genericType);

		if (null == min) {
			String errorMessage = new StringBuilder("parameter min is null").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (null == max) {
			String errorMessage = new StringBuilder("parameter max is null").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (null == typeComparator) {
			String errorMessage = new StringBuilder("parameter typeComparator is null").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (typeComparator.compare(min, max) > 0) {
			String errorMessage = new StringBuilder("parameter min[").append(min)
					.append("] is greater than parameter max[").append(max).append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		this.min = min;
		this.max = max;
		this.typeComparator = typeComparator;
	}

	/**
	 * {@link #valueOf(String)} 에서 호출되는 내부용 메소드로 {@link #valueOf(String)} 에서 최소 최대값
	 * 제약이라는 공통 부분을 제외한 지정한 타입으로 입력받은 문자열 타입의 항목의 값을 변환하는 기능을 담당한다.
	 * 
	 * @param itemValue
	 *            항목의 값
	 * @return 지정한 타입으로 변환된 입력받은 문자열 타입의 항목의 값
	 * @throws IllegalArgumentException
	 *             항목의 값이 잘못되었을 경우 던지는 예외
	 */
	abstract protected T innerValueOf(String itemValue) throws IllegalArgumentException;

	public T getMin() {
		return min;
	}

	public T getMax() {
		return max;
	}

	public Comparator<T> getTypeComparator() {
		return typeComparator;
	}

	@Override
	public T valueOf(String itemValue) throws IllegalArgumentException {
		T returnedValue = innerValueOf(itemValue);

		if (typeComparator.compare(returnedValue, min) < 0) {
			String errorMessage = new StringBuilder("parameter itemValue[").append(itemValue)
					.append("] is less than min[").append(min).append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (typeComparator.compare(returnedValue, max) > 0) {
			String errorMessage = new StringBuilder("parameter itemValue[").append(itemValue)
					.append("] is greater than max[").append(max).append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		return returnedValue;
	}
}
