package kr.pe.sinnori.common.config;

import java.util.Properties;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.sinnori.common.config.itemidinfo.ItemIDInfo;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;

/**
 * 
 * @author Won Jonghoon
 * 
 */
public abstract class AbstractDependencyValidator {
	protected InternalLogger log = InternalLoggerFactory.getInstance(this.getClass());


	protected ItemIDInfo<?> dependentSourceItemIDInfo = null;
	protected ItemIDInfo<?> dependentTargetItemIDInfo = null;


	/**
	 * 생성자
	 * @param dependentSourceItemIDInfo "의존 주체 항목  식별자 정보"
	 * @param dependentTargetItemIDInfo
	 * @throws IllegalArgumentException
	 */
	public AbstractDependencyValidator(ItemIDInfo<?> dependentSourceItemIDInfo,
			ItemIDInfo<?> dependentTargetItemIDInfo)
			throws IllegalArgumentException {
		if (null == dependentSourceItemIDInfo) {
			String errorMessage = new StringBuilder(
					"parameter dependentSourceItemInfo is null").toString();

			// log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		if (null == dependentTargetItemIDInfo) {
			String errorMessage = new StringBuilder(
					"parameter dependentTargetItemInfo is null").toString();

			// log.warn(errorMessage);
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
		
		this.dependentSourceItemIDInfo = dependentSourceItemIDInfo;
		this.dependentTargetItemIDInfo = dependentTargetItemIDInfo;
		
	}

	
	/**
	 * 외부에 공개되는 메소드로 "환경 설정 파일 내용을 적재한 프로퍼티"와 "항목이 속한 파트의 접두어" 를 입력으로 받아
	 * "의존 주체 항목 키"와 "의존 대상 항목의 키" 를 구하여 내부적으로 사용하는 사용자 정의 메소드
	 * {@link #isValid(Properties, String, String)} 를 호출한다.
	 * 
	 * @param sourceProperties "환경 설정 파일 내용을 적재한 프로퍼티"
	 * @param prefixOfDependentSourceItemID "항목 식별자가 속한 파트의 접두어"
	 * @return 의존관계가 성립하면 true 를 의존 관계가 성립하지 않으면 false 를 반환한다. 단 의존관계가 성립하지 않으면 대부분 예외를 던질수있다. 
	 * @throws IllegalArgumentException 잘못된 파라미티터 값을 넣은 경우 혹은 의존관계가 성립하지 않을때 던지는 예외
	 */
	public boolean isValid(Properties sourceProperties, String prefixOfDependentSourceItemID)
			throws IllegalArgumentException {
		if (null == sourceProperties) {
			String errorMessage = new StringBuilder(
					"parameter sourceProperties is null").toString();
			// log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		if (null == prefixOfDependentSourceItemID) {
			String errorMessage = new StringBuilder(
					"parameter prefixOfItem is null").toString();
			// log.warn(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		String dependentSourceItemKey = new StringBuilder(prefixOfDependentSourceItemID).append(
				getDependentSourceItemID()).toString();
		
		String dependentTargetItemID = getDependentTargetItemID();
		String dependentTargetItemKey = null;		
		if (dependentTargetItemIDInfo.getConfigurationPart().equals(ItemIDInfo.ConfigurationPart.COMMON)) {
			dependentTargetItemKey = dependentTargetItemID;
		} else {
			dependentTargetItemKey = new StringBuilder(prefixOfDependentSourceItemID).append(
					dependentTargetItemID).toString();
		}

		return isValid(sourceProperties, dependentSourceItemKey,
				dependentTargetItemKey);
	}

	/**
	 * {@link #isValid(Properties, String)} 메소드 내부에서 호출되는 사용자 정의 메소드이다.	 * 
	 * Warning! 내부적으로 사용되는 메소드로 호출 금지
	 * 
	 * @param sourceProperties "환경 설정 파일 내용을 적재한 프로퍼티"
	 * @param dependentSourceItemKey  "의존 주체 항목 키"
	 * @param dependentTargetItemKey "의존 대상 항목의 키"
	 * @return 의존관계가 성립하면 true 를 의존 관계가 성립하지 않으면 false 를 반환한다. 단 의존관계가 성립하지 않으면 대부분 예외를 던질수있다.
	 * @throws IllegalArgumentException 잘못된 파라미티터 값을 넣은 경우 혹은 의존관계가 성립하지 않을때 던지는 예외
	 */
	public abstract boolean isValid(Properties sourceProperties,
			String dependentSourceItemKey, String dependentTargetItemKey)
			throws IllegalArgumentException;


	/**
	 * @return "의존 주체 항목 식별자"
	 */
	public String getDependentSourceItemID() {
		return dependentSourceItemIDInfo.getItemID();
	}

	/**
	 * @return "의존 대상 항목 식별자"
	 */
	public String getDependentTargetItemID() {
		return dependentTargetItemIDInfo.getItemID();
	}
}
