package kr.pe.sinnori.common.config.dependoninactivechecker;

import java.io.File;

import kr.pe.sinnori.common.config.AbstractDependOnInactiveChecker;
import kr.pe.sinnori.common.config.AbstractNativeValueConverter;
import kr.pe.sinnori.common.config.itemidinfo.ItemIDInfo;
import kr.pe.sinnori.common.config.nativevalueconverter.SetTypeConverterOfSessionKeyRSAKeypairSource;

/**
 * 이 클래스는 환경 변수 "RSA 키 쌍 소스에 의존하는 "RSA 키 쌍이 있는
 * 경로"(="sessionkey.rsa_keysize.value") 항목의 활성화 여부를 결정 하기위한 빈 글래스입니다. 실질적인 처리는
 * 상속 받은 AbstractDependOnSkiper 에서 모두 처리를 합니다. 빈 클래스이지만 코드 가독성 증진을 목적으로 어떤 환경
 * 변수를 처리하는지 클래스 이름을 표시하기 위한 클래스입니다.
 * 
 * "RSA 키 쌍이 있는 경로" 항목은 "RSA 키 쌍 소스" 항목에 의존하여 만약 "RSA 키 쌍 소스" 값이 "API" 이면 비활성
 * 그렇지 않고 "RSA 키 쌍 소스" 값이 "File" 이면 활성화 됩니다.
 * 
 * @author Won Jonghoon
 * 
 */
public class RSAKeypairPathDependOnSourceInActiveChecker extends
		AbstractDependOnInactiveChecker {

	public RSAKeypairPathDependOnSourceInActiveChecker(
			ItemIDInfo<?> dependentSourceItemIDInfo,
			ItemIDInfo<?> dependentTargetItemIDInfo, String[] inactiveStrings)
			throws IllegalArgumentException {
		super(dependentSourceItemIDInfo, dependentTargetItemIDInfo,
				inactiveStrings);

		AbstractNativeValueConverter<?> dependentSourceItemValueConverter = dependentSourceItemIDInfo
				.getItemValueConverter();

		if (!dependentSourceItemValueConverter.getGenericType().getName().equals(
				File.class.getName())) {
			String errorMessage = new StringBuilder(
					"the parameter dependentSourceItemIDInfo[")
					.append(dependentTargetItemIDInfo.getItemID())
					.append("]'s nativeValueConverter generic type[")
					.append(dependentSourceItemValueConverter
							.getGenericType().getName())
					.append("] is not java.io.File").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		AbstractNativeValueConverter<?> dependentTargetItemValueConverter = dependentTargetItemIDInfo
				.getItemValueConverter();
		if (!(dependentTargetItemValueConverter instanceof SetTypeConverterOfSessionKeyRSAKeypairSource)) {
			String errorMessage = new StringBuilder(
					"the parameter dependentTargetItemIDInfo[")
					.append(dependentTargetItemIDInfo.getItemID())
					.append("]'s nativeValueConverter[")
					.append(dependentTargetItemValueConverter.getClass()
							.getName())
					.append("] is not a instance of SetTypeConverterOfSessionKeyRSAKeypairSource")
					.toString();
			throw new IllegalArgumentException(errorMessage);
		}
	}

}
