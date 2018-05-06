package kr.pe.codda.common.config.dependoninactivechecker;

import java.io.File;

import kr.pe.codda.common.config.AbstractDisabledItemChecker;
import kr.pe.codda.common.config.AbstractNativeValueConverter;
import kr.pe.codda.common.config.itemidinfo.ItemIDInfo;
import kr.pe.codda.common.config.nativevalueconverter.SetTypeConverterOfSessionKeyRSAKeypairSource;

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

public class RSAKeyFileDisabledItemChecker extends
		AbstractDisabledItemChecker {

	public RSAKeyFileDisabledItemChecker(
			ItemIDInfo<?> disabeldTargetItemIDInfo,
			ItemIDInfo<?> dependentItemIDInfo, String[] disabledConditionStrings)
			throws IllegalArgumentException {
		super(disabeldTargetItemIDInfo, dependentItemIDInfo,
				disabledConditionStrings);

		AbstractNativeValueConverter<?> disabledItemValueConverter = disabeldTargetItemIDInfo
				.getItemValueConverter();

		if (!disabledItemValueConverter.getGenericType().getName().equals(
				File.class.getName())) {
			String errorMessage = new StringBuilder(
					"the parameter disabeldTargetItemIDInfo[")
					.append(disabeldTargetItemIDInfo.getItemID())
					.append("]'s nativeValueConverter generic type[")
					.append(disabledItemValueConverter
							.getGenericType().getName())
					.append("] is not java.io.File").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		AbstractNativeValueConverter<?> dependentItemValueConverter = dependentItemIDInfo
				.getItemValueConverter();
		if (!(dependentItemValueConverter instanceof SetTypeConverterOfSessionKeyRSAKeypairSource)) {
			String errorMessage = new StringBuilder(
					"the parameter dependentItemIDInfo[")
					.append(dependentItemIDInfo.getItemID())
					.append("]'s nativeValueConverter[")
					.append(dependentItemValueConverter.getClass()
							.getName())
					.append("] is not a instance of SetTypeConverterOfSessionKeyRSAKeypairSource")
					.toString();
			throw new IllegalArgumentException(errorMessage);
		}
	}

}
