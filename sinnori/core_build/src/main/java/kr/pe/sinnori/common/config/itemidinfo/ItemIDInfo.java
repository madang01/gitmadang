package kr.pe.sinnori.common.config.itemidinfo;


import java.util.Set;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.sinnori.common.config.AbstractMinMaxConverter;
import kr.pe.sinnori.common.config.AbstractNativeValueConverter;
import kr.pe.sinnori.common.config.AbstractSetTypeNativeValueConverter;
import kr.pe.sinnori.common.exception.SinnoriConfigurationException;

/**
 * 항목 식별자  환경 설정 정보 클래스
 * 
 * @author "Won Jonghoon"
 * 
 */
public class ItemIDInfo<T> {
	protected InternalLogger log = InternalLoggerFactory.getInstance(ItemIDInfo.class);
	
	public enum ConfigurationPart {
		DBCP, COMMON, PROJECT
	};

	public enum ViewType {
		TEXT, FILE, PATH, SINGLE_SET
	};

	private ConfigurationPart configurationPart;
	private ViewType viewType;
	private String itemID;
	private String description;

	private String defaultValue;
	private boolean isDefaultValueCheck;
	private AbstractNativeValueConverter<T> itemValueConverter;

	/**
	 * 환경 변수 값을 검사하기 위한 정보 클래스 생성자
	 * 
	 * @param configPart
	 *            환경 설정 항목들이 속한 파트
	 * @param itemViewType
	 *            환경 설정 도구에서 항목 값 표현 방식
	 * @param itemID
	 *            항목 식별자
	 * @param description
	 *            환경 변수 설명
	 * @param defaultValue
	 *            디폴트 값
	 * @param isDefaultValueCheck
	 *            객체 생성시 디폴트 값 검사 수행 여부, 잘못된 값을 가져도 당장 검사하지 않고 사용자가 후에 환경설정 도구에서 이를 인지하여 고치도록 할때 false 를 넣는다.
	 *            예를 들면 파일 관련 항목은 파일이 반듯이 존재해야 하는데 환경 설정 항목 정보 구성시 
	 * @param nativeValueConverter
	 *            문자열인 환경 설정 파일의 값을 언어 종속적 값으로 바꾸어 주는 변환기
	 * @throws IllegalArgumentException
	 *             잘못된 파라미터 입력시 던지는 예외
	 * @throws SinnoriConfigurationException
	 *             디폴트 값 검사 수행시 디폴트 값이 잘못된 경우 던지는 예외
	 */
	public ItemIDInfo(ConfigurationPart configPart,
			ViewType itemViewType, String itemID,
			String description, String defaultValue,
			boolean isDefaultValueCheck,
			AbstractNativeValueConverter<T> nativeValueConverter)
			throws IllegalArgumentException, SinnoriConfigurationException {
		if (null == configPart) {
			String errorMessage = "the parameter configPart is null";
			throw new IllegalArgumentException(errorMessage);
		}
		if (null == itemViewType) {
			String errorMessage = "the parameter itemViewType is null";
			throw new IllegalArgumentException(errorMessage);
		}

		if (null == itemID) {
			String errorMessage = "the parameter itemID is null";
			throw new IllegalArgumentException(errorMessage);
		}

		if (itemID.equals("")) {
			String errorMessage = "the parameter itemID is a empty string";
			throw new IllegalArgumentException(errorMessage);
		}
		
		int firstIndex = itemID.indexOf(".value");
		if (firstIndex < 0) {
			String errorMessage = new StringBuilder("parameter itemID[")
			.append(itemID)
			.append("] must end with '.value'").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (0 == firstIndex) {
			String errorMessage = new StringBuilder("parameter itemID[")
			.append(itemID)
			.append("] must not start with '.value'").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (itemID.length() != (firstIndex+".value".length())) {
			String errorMessage = new StringBuilder("the parameter itemID[")
			.append(itemID)
			.append("] has '.value' string in the middle").toString();
			throw new IllegalArgumentException(errorMessage);
		}	

		if (null == description) {
			String errorMessage = "the parameter description is null";
			throw new IllegalArgumentException(errorMessage);
		}

		if (description.equals("")) {
			String errorMessage = "the parameter description is a empty string";
			throw new IllegalArgumentException(errorMessage);
		}

		if (null == defaultValue) {
			String errorMessage = "the parameter defaultValue is null";
			throw new IllegalArgumentException(errorMessage);
		}

		if (null == nativeValueConverter) {
			String errorMessage = "the parameter itemValueConverter is null";
			throw new IllegalArgumentException(errorMessage);
		}

		if (isDefaultValueCheck) {
			try {
				nativeValueConverter.valueOf(defaultValue);
			} catch (IllegalArgumentException e) {
				String errorMessage = new StringBuilder(
						"the parameter defaultValue[").append(defaultValue)
						.append("] test failed").toString();
				
				log.info(errorMessage, e);
				
				
				throw new IllegalArgumentException(new StringBuilder(errorMessage)
				.append(", errormessage=").append(e.getMessage()).toString());
			}
		}

		if (itemViewType == ViewType.SINGLE_SET) {
			if (!(nativeValueConverter instanceof AbstractSetTypeNativeValueConverter)) {
				String errorMessage = "parameter configItemViewType is a signle view type  "
						+ "but parameter itemValidator object is not a instance of  a SingleSetValueGetterIF type";
				throw new IllegalArgumentException(errorMessage);
			}
		}	

		this.configurationPart = configPart;
		this.viewType = itemViewType;
		this.itemID = itemID;
		this.description = description;
		this.defaultValue = defaultValue;
		this.isDefaultValueCheck = isDefaultValueCheck;
		this.itemValueConverter = nativeValueConverter;
	}
	
	public String getItemDescKey(String prefixOfItemID) {
		if (null == prefixOfItemID) {
			throw new IllegalArgumentException("the paramter prefixOfItemID is null");
		}
		String postfix = ".value";
		int lastIndexOfPostfix = itemID.lastIndexOf(postfix);			
		String itemDescKey = new StringBuilder(prefixOfItemID)
		.append(itemID.substring(0, lastIndexOfPostfix)).append(".desc").toString();
		
		return itemDescKey;
		
	}

	public ViewType getViewType() {
		return viewType;
	}

	public ConfigurationPart getConfigurationPart() {
		return configurationPart;
	}

	public String getItemID() {
		return itemID;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public AbstractNativeValueConverter<T> getItemValueConverter() {
		return itemValueConverter;
	}

	public boolean isDefaultValueCheck() {
		return isDefaultValueCheck;
	}
	
	/**
	 * 항목의 값으로 가질수있는 집합을 반환한다. 
	 * 단 집합이 없으면 즉 집합형 항목이 아니면 널을 반환한다.
	 * 
	 * @return
	 */
	public Set<String> getItemSet() {
		Set<String> itemSet = null;
		if (viewType.equals(ItemIDInfo.ViewType.SINGLE_SET)) {
			AbstractSetTypeNativeValueConverter<?> setTypeNativeConvter = 
					(AbstractSetTypeNativeValueConverter<?>)itemValueConverter;
			itemSet = setTypeNativeConvter.getItemValueSet();
		}
		return itemSet;
	}

	public String getDescription() {
		StringBuilder descriptBuilder = new StringBuilder(description);

		descriptBuilder.append(", default value[");
		descriptBuilder.append(defaultValue);
		descriptBuilder.append("]");

		if (itemValueConverter instanceof AbstractSetTypeNativeValueConverter) {
			AbstractSetTypeNativeValueConverter<?> copyitemValueGetter = (AbstractSetTypeNativeValueConverter<?>) itemValueConverter;
			descriptBuilder.append(", ");
			descriptBuilder.append(copyitemValueGetter.getSetName());
			descriptBuilder.append(copyitemValueGetter.getItemValueSet().toString());
		} else if (itemValueConverter instanceof AbstractMinMaxConverter) {
			AbstractMinMaxConverter<?> minMaxConverter = (AbstractMinMaxConverter<?>) itemValueConverter;
			descriptBuilder.append(", min[");
			descriptBuilder.append(minMaxConverter.getMin());
			descriptBuilder.append("], max[");
			descriptBuilder.append(minMaxConverter.getMax());			
			descriptBuilder.append("]");
		}

		return descriptBuilder.toString();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ConfigItem [configPart=");
		builder.append(configurationPart);
		builder.append(", viewType=");
		builder.append(viewType);
		builder.append(", itemID=");
		builder.append(itemID);
		builder.append(", description=");
		builder.append(description);
		builder.append(", defaultValue=");
		builder.append(defaultValue);
		builder.append(", isDefaultValueCheck=");
		builder.append(isDefaultValueCheck);
		builder.append(", itemValueGetter=");
		builder.append(itemValueConverter);
		builder.append("]");
		return builder.toString();
	}
}
