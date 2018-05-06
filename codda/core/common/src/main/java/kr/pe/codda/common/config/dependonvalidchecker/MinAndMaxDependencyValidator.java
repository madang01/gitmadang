package kr.pe.codda.common.config.dependonvalidchecker;

import java.util.Comparator;
import java.util.Properties;

import kr.pe.codda.common.config.AbstractDependencyValidator;
import kr.pe.codda.common.config.AbstractMinMaxConverter;
import kr.pe.codda.common.config.AbstractNativeValueConverter;
import kr.pe.codda.common.config.itemidinfo.ItemIDInfo;
import kr.pe.codda.common.exception.CoddaConfigurationException;

public class MinAndMaxDependencyValidator<T extends Number> extends
		AbstractDependencyValidator {

	private Comparator<T> minMaxComparator = null;
	private AbstractMinMaxConverter<T> maxConverter = null;
	private AbstractMinMaxConverter<T> minConverter = null;

	@SuppressWarnings("unchecked")
	public MinAndMaxDependencyValidator(ItemIDInfo<T> dependentSourceConfigItem,
			ItemIDInfo<T> dependentTargetConfigItem, 
			Class<T> genericTypeClass)
			throws IllegalArgumentException, CoddaConfigurationException {
		super(dependentSourceConfigItem, dependentTargetConfigItem);
		
		// Comparator<T> minMaxComparator, 
		// this.minMaxComparator = minMaxComparator;
		
		if (null == genericTypeClass) {
			String errorMessage = new StringBuilder(
					"parameter genericTypeClass is null").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		/*if (!genericTypeClass.equals(Byte.class)
				&& !genericTypeClass.equals(Short.class)
				&& !genericTypeClass.equals(Integer.class)
				&& !genericTypeClass.equals(Long.class)
				&& !genericTypeClass.equals(Float.class)
				&& !genericTypeClass.equals(Double.class)) {

			String errorMessage = new StringBuilder("the MinDependOnMaxBreaker's generic type T[")
					.append(genericTypeClass.getName())
					.append("] is bad, T only can have Byte, Short, Integer, Long, Float and Double")
					.toString();

			throw new ConfigErrorException(errorMessage);
		}*/
		
		AbstractNativeValueConverter<?> dependentSourceItemValueConverter = dependentSourceItemIDInfo.getItemValueConverter();
		AbstractNativeValueConverter<?> dependentTargetItemValueConverter = dependentTargetItemIDInfo.getItemValueConverter();
		
		if (!(dependentSourceItemValueConverter instanceof AbstractMinMaxConverter)) {
			String errorMessage = new StringBuilder("the parameter dependentSourceConfigItem[")
					.append(getDependentSourceItemID())
					.append("]'s dependentSourceItemValueConverter[")
					.append(dependentSourceItemValueConverter.getClass().getName())
					.append("] is not a instance of AbstractMinMaxConverter")
					.toString();
			throw new IllegalArgumentException(errorMessage);
		}

		
		if (!(dependentTargetItemValueConverter instanceof AbstractMinMaxConverter)) {
			String errorMessage = new StringBuilder("the parameter dependentTargetConfigItem[")
			.append(getDependentTargetItemID())
			.append("]'s dependentTargetItemValueConverter[")
			.append(dependentTargetItemValueConverter.getClass().getName())
			.append("] is not a instance of AbstractMinMaxConverter")
			.toString();
			throw new IllegalArgumentException(errorMessage);
		}		

		String genericTypeName = genericTypeClass
				.getName();

		String minConverterTypeTName = dependentSourceItemValueConverter.getGenericType().getName();
		String maxConverterTypeTName = dependentTargetItemValueConverter.getGenericType().getName();

		
		if (!genericTypeName.equals(minConverterTypeTName)) {
			String errorMessage = new StringBuilder("this class's generic type T[")
			.append(genericTypeName)
			.append("] is different from the parameter dependentSourceConfigItem[")
			.append(dependentSourceConfigItem.getItemID())
			.append("]'s generic type T[")
			.append(minConverterTypeTName)			
			.append("]")
			.toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (!genericTypeName.equals(maxConverterTypeTName)) {
			String errorMessage = new StringBuilder("this class's generic type T[")
			.append(genericTypeName)
			.append("] is different from the parameter dependentTargetConfigItem[")
			.append(dependentTargetConfigItem.getItemID())
			.append("]'s generic type T[")
			.append(maxConverterTypeTName)			
			.append("]")
			.toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		

		try {
			minConverter = (AbstractMinMaxConverter<T>) dependentSourceItemValueConverter;

		} catch (ClassCastException e) {
			/** expected dead code */
			String errorMessage = new StringBuilder(
					"fail to cast From the variable dependentSourceItemValueConverter[")
			.append(dependentSourceConfigItem.getItemID())
			.append("]'s type[")
			.append(dependentSourceItemValueConverter.getClass().getName())
			.append("] to GeneralConverterReturningGenericBetweenMinAndMax<T>")
					.append(", errormessage=").append(e.getMessage())
					.toString();
			
			//log.error(errorMessage);
			//System.exit(1);
			throw new CoddaConfigurationException(errorMessage);
		}

		try {
			maxConverter = (AbstractMinMaxConverter<T>) dependentTargetItemValueConverter;
		} catch (ClassCastException e) {
			/** expected dead code */
			String errorMessage = new StringBuilder(
					"fail to cast From the variable dependentTargetItemValueConverter[")
			.append(dependentTargetConfigItem.getItemID())
			.append("]'s type[")
			.append(dependentTargetItemValueConverter.getClass().getName())
			.append("] to GeneralConverterReturningGenericBetweenMinAndMax<T>")
					.append(", errormessage=").append(e.getMessage())
					.toString();
			throw new CoddaConfigurationException(errorMessage);
		}
		
		minMaxComparator = maxConverter.getTypeComparator();
	}

	@Override
	public boolean isValid(Properties sourceProperties,
			String dependentSourceItemKey, String dependentTargetItemKey)
			throws IllegalArgumentException {
		if (null == sourceProperties) {
			String errorMessage = new StringBuilder("parameter sourceProperties is null")
					.toString();
			throw new IllegalArgumentException(errorMessage);
		}
		if (null == dependentSourceItemKey) {
			String errorMessage = new StringBuilder("parameter dependentSourceItemKey is null")
					.toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (null == dependentTargetItemKey) {
			String errorMessage = new StringBuilder("parameter dependentTargetItemKey is null")
					.toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		String dependentSourceItemValue = sourceProperties
				.getProperty(dependentSourceItemKey);
		
		if (null == dependentSourceItemValue) {
			String errorMessage = new StringBuilder(
					"the parameter dependentSourceItemKey[")
					.append(dependentSourceItemKey).append("]) does not exist at the paramerter sourceProperties where the config file was loaded").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		T min = null;
		try {
			
			min = minConverter.valueOf(dependentSourceItemValue);
		} catch (IllegalArgumentException e) {
			String errorMessage = new StringBuilder(
					"the parameter dependentSourceItemKey[")
					.append(dependentSourceItemKey).append("]'s value[")
					.append(dependentSourceItemValue).append("] is bad), errormessage=")
					.append(e.getMessage()).toString();
			throw new IllegalArgumentException(errorMessage);
		}

		String dependentTargetItemValue = sourceProperties
				.getProperty(dependentTargetItemKey);
		if (null == dependentTargetItemValue) {
			String errorMessage = new StringBuilder(
					"the parameter dependentTargetItemKey[")
					.append(dependentTargetItemKey).append("]) does not exist at the paramerter sourceProperties where the config file was loaded").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		T max = null;
		try {
			
			max = maxConverter.valueOf(dependentTargetItemValue);
		} catch (IllegalArgumentException e) {
			String errorMessage = new StringBuilder(
					"the parameter dependentTargetItemKey[")
					.append(dependentTargetItemKey).append("]'s value[")
					.append(dependentTargetItemValue).append("] is bad), errormessage=")
					.append(e.getMessage()).toString();
			throw new IllegalArgumentException(errorMessage);
		} 
		
		if (minMaxComparator.compare(min, max) > 0) {
			String errorMessage = new StringBuilder("the parameter dependentSourceItemKey[")
			.append(dependentSourceItemKey).append("]'s value(=min)[").append(min)
			.append("] is greater than the parameter dependentTargetItemKey[").append(dependentTargetItemKey)
			.append("]'s value(=max)[").append(max).append("]").toString();
			
			throw new IllegalArgumentException(errorMessage);
		}

		/*if (min.doubleValue() > max.doubleValue()) {			
			String errorMessage = new StringBuilder("the parameter dependentSourceItemKey[")
					.append(dependentSourceItemKey).append("]'s value(=min)[").append(min)
					.append("] is greater than the parameter dependentTargetItemKey[").append(dependentTargetItemKey)
					.append("]'s value(=max)[").append(max).append("]").toString();
			//log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}*/

		//log.info("min=[{}], max=[{}]", min.toString(), max.toString());
		
		return true;

	}
}
