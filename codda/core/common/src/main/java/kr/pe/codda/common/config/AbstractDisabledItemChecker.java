package kr.pe.codda.common.config;

import java.util.Properties;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.config.itemidinfo.ItemIDInfo;
import kr.pe.codda.common.etc.CommonStaticFinalVars;

public abstract class AbstractDisabledItemChecker {
	protected InternalLogger log = InternalLoggerFactory.getInstance(AbstractDisabledItemChecker.class);
	
	protected ItemIDInfo<?> disabeldTargetItemIDInfo  = null;
	protected ItemIDInfo<?> dependentItemIDInfo = null;
	protected String disabledConditionStrings[] = null;
	
	public AbstractDisabledItemChecker(ItemIDInfo<?> disabeldTargetItemIDInfo, ItemIDInfo<?> dependentItemIDInfo, String[] disabledConditionStrings) throws IllegalArgumentException {
		if (null == disabeldTargetItemIDInfo) {
			String errorMessage = new StringBuilder("the parameter disabeldTargetItemIDInfo is null").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == dependentItemIDInfo) {
			String errorMessage = new StringBuilder("the parameter dependentItemIDInfo is null").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == disabledConditionStrings) {
			String errorMessage = new StringBuilder("the parameter disabledConditionStrings is null").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (0 == disabledConditionStrings.length) {			
			String errorMessage = new StringBuilder("the parameter disabledConditionStrings is a zero size string array").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		ItemIDInfo.ConfigurationPart configurationPartOfDependentSourceItemID = disabeldTargetItemIDInfo.getConfigurationPart();
		ItemIDInfo.ConfigurationPart configurationPartOfDependentTargetItemID = dependentItemIDInfo.getConfigurationPart();
		
		if (!configurationPartOfDependentTargetItemID.equals(ItemIDInfo.ConfigurationPart.COMMON)) {
			if (!configurationPartOfDependentTargetItemID.equals(configurationPartOfDependentSourceItemID)) {
				String errorMessage = new StringBuilder(
						"the dependent target item id[")
				.append(dependentItemIDInfo.getItemID())
				.append("]'s configuration part[")
				.append(configurationPartOfDependentTargetItemID)
				.append("] must be one of common part")
				.append(CommonStaticFinalVars.NEWLINE)
				.append(" or equal to the dependent source item id[")
				.append(disabeldTargetItemIDInfo.getItemID())
				.append("]'s configuration part[")
				.append(configurationPartOfDependentSourceItemID)
				.append("]").toString();
				throw new IllegalArgumentException(errorMessage);
			}
		}
		
		
		AbstractNativeValueConverter<?> dependentTargetItemValueConverter = dependentItemIDInfo.getItemValueConverter();
		
		if (!(dependentTargetItemValueConverter instanceof AbstractSetTypeNativeValueConverter)) {
			String errorMessage = new StringBuilder(
					"parameter dependentTargetItemIDInfo[")
			.append(dependentItemIDInfo.getItemID()).append("]'s nativeValueConverter[")
					.append(dependentTargetItemValueConverter.getClass().getName())
					.append("] is not inherited by AbstractSetTypeNativeValueConverter")
					.toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		for (int i=0; i < disabledConditionStrings.length; i++) {
			try {
				dependentTargetItemValueConverter.valueOf(disabledConditionStrings[i]);
			} catch(IllegalArgumentException e) {
				String errorMessage = new StringBuilder("the parameter inactiveStrings[")
				.append(i)
				.append("]'s value[")
				.append(disabledConditionStrings[i])
				.append("] is bad, errrmessage=")
				.append(e.getMessage()).toString();
				throw new IllegalArgumentException(errorMessage);
			}
		}
		
		
		this.disabeldTargetItemIDInfo  = disabeldTargetItemIDInfo;
		this.dependentItemIDInfo = dependentItemIDInfo;		
		this.disabledConditionStrings = disabledConditionStrings;
	}
	
	public boolean isDisabled(Properties sourceProperties, String prefixOfItemID) throws IllegalArgumentException {
		
		if (null == sourceProperties) {
			String errorMessage = new StringBuilder("parameter sourceProperties is null").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == prefixOfItemID) {
			String errorMessage = new StringBuilder("parameter prefixOfItem is null").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		String dependentSoruceItemKey = new StringBuilder(prefixOfItemID).append(getDisabledItemID()).toString();
		String dependentSoruceItemValue = 
				sourceProperties.getProperty(dependentSoruceItemKey);
		if (null == dependentSoruceItemValue) {
			String errorMessage = new StringBuilder("the parameter sourceProperties's key(=the variable dependentSoruceItemKey[")
			.append(dependentSoruceItemKey)
			.append("]) consisting of the parameter prefixOfItem and the variable dependentSourceItemId does not exist.").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		String dependentTargetItemID = getDependentItemID();
		String dependentTargetItemKey = null;		
		if (dependentItemIDInfo.getConfigurationPart().equals(ItemIDInfo.ConfigurationPart.COMMON)) {
			dependentTargetItemKey = dependentTargetItemID;
		} else {
			dependentTargetItemKey = new StringBuilder(prefixOfItemID).append(
					dependentTargetItemID).toString();
		}
		String dependentTargetItemValue = sourceProperties.getProperty(dependentTargetItemKey);
		if (null == dependentTargetItemValue) {
			String errorMessage = new StringBuilder("the parameter sourceProperties's key(=the variable dependentTargetItemKey[")
			.append(dependentTargetItemKey)
			.append("]) consisting of the parameter prefixOfItem and the variable dependentTargetItemIDInfo's itemID does not exist.").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		for (int i=0; i < disabledConditionStrings.length; i++) {
			if (disabledConditionStrings[i].equals(dependentTargetItemValue)) {
				return true;
			}
		}
		
		
		return false;
	}
	
	
	public final String getDisabledItemID() {
		return disabeldTargetItemIDInfo.getItemID();
	}

	public final String getDependentItemID() {
		return dependentItemIDInfo.getItemID();
	}

	public final String[] getDisabledConditionStrings() {
		return disabledConditionStrings;
	}
}
