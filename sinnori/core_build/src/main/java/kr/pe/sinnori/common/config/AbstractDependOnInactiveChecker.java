package kr.pe.sinnori.common.config;

import java.util.Properties;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.sinnori.common.config.itemidinfo.ItemIDInfo;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;

public abstract class AbstractDependOnInactiveChecker {
	protected InternalLogger log = InternalLoggerFactory.getInstance(AbstractDependOnInactiveChecker.class);
	
	protected ItemIDInfo<?> dependentSourceItemIDInfo  = null;
	protected ItemIDInfo<?> dependentTargetItemIDInfo = null;
	protected String inactiveStrings[] = null;
	
	public AbstractDependOnInactiveChecker(ItemIDInfo<?> dependentSourceItemIDInfo, ItemIDInfo<?> dependentTargetItemIDInfo, String[] inactiveStrings) throws IllegalArgumentException {
		if (null == dependentSourceItemIDInfo) {
			String errorMessage = new StringBuilder("the parameter dependentSourceItemIDInfo is null").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == dependentTargetItemIDInfo) {
			String errorMessage = new StringBuilder("the parameter dependentTargetItemIDInfo is null").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == inactiveStrings) {
			String errorMessage = new StringBuilder("the parameter inactiveStrings is null").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (0 == inactiveStrings.length) {			
			String errorMessage = new StringBuilder("the parameter inactiveStrings is a zero size string array").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		ItemIDInfo.ConfigurationPart configurationPartOfDependentSourceItemID = dependentSourceItemIDInfo.getConfigurationPart();
		ItemIDInfo.ConfigurationPart configurationPartOfDependentTargetItemID = dependentTargetItemIDInfo.getConfigurationPart();
		
		if (!configurationPartOfDependentTargetItemID.equals(ItemIDInfo.ConfigurationPart.COMMON)) {
			if (!configurationPartOfDependentTargetItemID.equals(configurationPartOfDependentSourceItemID)) {
				String errorMessage = new StringBuilder(
						"the dependent target item id[")
				.append(dependentTargetItemIDInfo.getItemID())
				.append("]'s configuration part[")
				.append(configurationPartOfDependentTargetItemID)
				.append("] must be one of common part")
				.append(CommonStaticFinalVars.NEWLINE)
				.append(" or equal to the dependent source item id[")
				.append(dependentSourceItemIDInfo.getItemID())
				.append("]'s configuration part[")
				.append(configurationPartOfDependentSourceItemID)
				.append("]").toString();
				throw new IllegalArgumentException(errorMessage);
			}
		}
		
		
		AbstractNativeValueConverter<?> dependentTargetItemValueConverter = dependentTargetItemIDInfo.getItemValueConverter();
		
		if (!(dependentTargetItemValueConverter instanceof AbstractSetTypeNativeValueConverter)) {
			String errorMessage = new StringBuilder(
					"parameter dependentTargetItemIDInfo[")
			.append(dependentTargetItemIDInfo.getItemID()).append("]'s nativeValueConverter[")
					.append(dependentTargetItemValueConverter.getClass().getName())
					.append("] is not inherited by AbstractSetTypeNativeValueConverter")
					.toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		for (int i=0; i < inactiveStrings.length; i++) {
			try {
				dependentTargetItemValueConverter.valueOf(inactiveStrings[i]);
			} catch(IllegalArgumentException e) {
				String errorMessage = new StringBuilder("the parameter inactiveStrings[")
				.append(i)
				.append("]'s value[")
				.append(inactiveStrings[i])
				.append("] is bad, errrmessage=")
				.append(e.getMessage()).toString();
				throw new IllegalArgumentException(errorMessage);
			}
		}
		
		
		this.dependentSourceItemIDInfo  = dependentSourceItemIDInfo;
		this.dependentTargetItemIDInfo = dependentTargetItemIDInfo;		
		this.inactiveStrings = inactiveStrings;
	}
	
	public boolean isInactive(Properties sourceProperties, String prefixOfItemID) throws IllegalArgumentException {
		
		if (null == sourceProperties) {
			String errorMessage = new StringBuilder("parameter sourceProperties is null").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == prefixOfItemID) {
			String errorMessage = new StringBuilder("parameter prefixOfItem is null").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		String dependentSoruceItemKey = new StringBuilder(prefixOfItemID).append(getDependentSourceItemID()).toString();
		String dependentSoruceItemValue = 
				sourceProperties.getProperty(dependentSoruceItemKey);
		if (null == dependentSoruceItemValue) {
			String errorMessage = new StringBuilder("the parameter sourceProperties's key(=the variable dependentSoruceItemKey[")
			.append(dependentSoruceItemKey)
			.append("]) consisting of the parameter prefixOfItem and the variable dependentSourceItemId does not exist.").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		String dependentTargetItemID = getDependentTargetItemID();
		String dependentTargetItemKey = null;		
		if (dependentTargetItemIDInfo.getConfigurationPart().equals(ItemIDInfo.ConfigurationPart.COMMON)) {
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
		
		for (int i=0; i < inactiveStrings.length; i++) {
			if (inactiveStrings[i].equals(dependentTargetItemValue)) {
				return true;
			}
		}
		
		
		return false;
	}
	
	
	public final String getDependentSourceItemID() {
		return dependentSourceItemIDInfo.getItemID();
	}

	public final String getDependentTargetItemID() {
		return dependentTargetItemIDInfo.getItemID();
	}

	public final String[] getInactiveStrings() {
		return inactiveStrings;
	}
}
