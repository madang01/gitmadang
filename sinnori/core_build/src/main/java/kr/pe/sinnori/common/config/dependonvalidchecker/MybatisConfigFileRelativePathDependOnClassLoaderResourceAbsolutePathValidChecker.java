package kr.pe.sinnori.common.config.dependonvalidchecker;

import java.io.File;
import java.util.Properties;

import kr.pe.sinnori.common.config.AbstractDependOnValidChecker;
import kr.pe.sinnori.common.config.AbstractNativeValueConverter;
import kr.pe.sinnori.common.config.itemidinfo.ItemIDInfo;
import kr.pe.sinnori.common.config.nativevalueconverter.GeneralConverterReturningEmptyOrNoTrimString;
import kr.pe.sinnori.common.config.nativevalueconverter.GeneralConverterReturningPath;
import kr.pe.sinnori.common.exception.ConfigErrorException;
import kr.pe.sinnori.common.util.CommonStaticUtil;

public class MybatisConfigFileRelativePathDependOnClassLoaderResourceAbsolutePathValidChecker extends AbstractDependOnValidChecker {

	GeneralConverterReturningEmptyOrNoTrimString serverMybatisConfigFileRelativePathConverter = null;
	GeneralConverterReturningPath serverClassLoaderAPPINFPathConverter = null;
	
	
	public MybatisConfigFileRelativePathDependOnClassLoaderResourceAbsolutePathValidChecker(
			ItemIDInfo<String> dependentSourceItemIDInfo, ItemIDInfo<File> dependentTargetItemIDInfo)
			throws IllegalArgumentException, ConfigErrorException {
		super(dependentSourceItemIDInfo, dependentTargetItemIDInfo);
		
		AbstractNativeValueConverter<?> dependentSourceItemValueConverter = dependentSourceItemIDInfo.getItemValueConverter();
		AbstractNativeValueConverter<?> dependentTargetItemValueConverter = dependentTargetItemIDInfo.getItemValueConverter();
		
		if (!(dependentSourceItemValueConverter instanceof GeneralConverterReturningEmptyOrNoTrimString)) {
			String errorMessage = new StringBuilder("the parameter dependentSourceItemInfo[")
					.append(getDependentSourceItemID())
					.append("]'s dependentSourceItemValueConverter[")
					.append(dependentSourceItemValueConverter.getClass().getName())
					.append("] is not a instance of GeneralConverterReturningEmptyOrNoTrimString")
					.toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (!(dependentTargetItemValueConverter instanceof GeneralConverterReturningPath)) {
			String errorMessage = new StringBuilder("the parameter dependentTargetItemInfo[")
			.append(getDependentTargetItemID())
			.append("]'s dependentTargetItemValueConverter[")
			.append(dependentTargetItemValueConverter.getClass().getName())
			.append("] is not a instance of GeneralConverterReturningPath")
			.toString();
			throw new IllegalArgumentException(errorMessage);
		}	
		try {
			serverMybatisConfigFileRelativePathConverter = (GeneralConverterReturningEmptyOrNoTrimString)dependentSourceItemIDInfo.getItemValueConverter();
		} catch (ClassCastException e) {
			/** expected dead code */
			String errorMessage = new StringBuilder(
					"fail to cast From the variable dependentSourceItemValueConverter[")
			.append(dependentSourceItemIDInfo.getItemID())
			.append("]'s type[")
			.append(dependentSourceItemValueConverter.getClass().getName())
			.append("] to GeneralConverterReturningEmptyOrNoTrimString")
					.append(", errormessage=").append(e.getMessage())
					.toString();
			throw new ConfigErrorException(errorMessage);
		}
		try {
			serverClassLoaderAPPINFPathConverter = (GeneralConverterReturningPath)dependentTargetItemIDInfo.getItemValueConverter();
		} catch (ClassCastException e) {
			/** expected dead code */
			String errorMessage = new StringBuilder(
					"fail to cast From the variable dependentTargetItemValueConverter[")
			.append(dependentTargetItemIDInfo.getItemID())
			.append("]'s type[")
			.append(dependentTargetItemValueConverter.getClass().getName())
			.append("] to GeneralConverterReturningPath")
					.append(", errormessage=").append(e.getMessage())
					.toString();
			throw new ConfigErrorException(errorMessage);
		}
	}

	@Override
	public boolean isValid(Properties sourceProperties, String dependentSourceItemKey,
			String dependentTargetItemKey) throws IllegalArgumentException {
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

		String serverMybatisConfigFileRelativePath = null;
		String dependentSourceItemValue = sourceProperties.getProperty(dependentSourceItemKey);
		
		try {
			serverMybatisConfigFileRelativePath = serverMybatisConfigFileRelativePathConverter
					.valueOf(dependentSourceItemValue);			
		} catch (IllegalArgumentException e) {
			String errorMessage = new StringBuilder(
					"the parameter dependentSourceItemKey[")
					.append(dependentSourceItemKey).append("]'s value[")
					.append(dependentSourceItemValue).append("] is bad), errormessage=")
					.append(e.getMessage()).toString();
			throw new IllegalArgumentException(errorMessage);
		}

		/**
		 * mybatis 환경설정 파일의 상대 경로가 빈 문자열이면 mybatis 를 사용하지 않겠다는 뜻이므로  의존 관계가 유효하므로 참을 반환한다.
		 */
		if (serverMybatisConfigFileRelativePath.equals("")) {
			return true;
		}

		java.io.File serverClassLoaderAPPINFPath = null;
		String dependentTargetItemValue = sourceProperties
				.getProperty(dependentTargetItemKey);
		
		try {
			serverClassLoaderAPPINFPath = serverClassLoaderAPPINFPathConverter
					.valueOf(dependentTargetItemValue);			
		} catch (IllegalArgumentException e) {
			String errorMessage = new StringBuilder(
					"the parameter dependentTargetItemKey[")
					.append(dependentTargetItemKey).append("]'s value[")
					.append(dependentTargetItemValue).append("] is bad), errormessage=")
					.append(e.getMessage()).toString();
			throw new IllegalArgumentException(errorMessage);
		}			

		String resourcesPathString = new StringBuilder(serverClassLoaderAPPINFPath.getAbsolutePath())
		.append(File.separator).append("resources").toString();
		
		String mybatisConfigFilePathString = CommonStaticUtil.getFilePathStringFromResourcePathAndRelativePathOfFile(resourcesPathString, serverMybatisConfigFileRelativePath);
		
		File mybatisConfigFile = new File(mybatisConfigFilePathString);

		if (!mybatisConfigFile.exists()) {
			String errorMessage = new StringBuilder(
					"the mybatis config file[")
					.append(mybatisConfigFilePathString)
					.append("] dosn't exist. the mybatis config file's relative path is the parameter dependentSourceItemKey[")
					.append(dependentSourceItemKey).append("]'s value[")
					.append(dependentSourceItemValue).append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (!mybatisConfigFile.isFile()) {
			String errorMessage = new StringBuilder(
					"the mybatis config file[")
					.append(mybatisConfigFilePathString)
					.append("] isn't a regular file. the mybatis config file's relative path is the parameter dependentSourceItemKey[")
					.append(dependentSourceItemKey).append("]'s value[")
					.append(dependentSourceItemValue).append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (!mybatisConfigFile.canRead()) {
			String errorMessage = new StringBuilder(
					"the mybatis config file[")
					.append(mybatisConfigFilePathString)
					.append("] doesn't hava permission to read, check file permission. the mybatis config file's relative path is the parameter dependentSourceItemKey[")
					.append(dependentSourceItemKey).append("]'s value[")
					.append(dependentSourceItemValue).append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}
		
		return true;
	}
}
