package kr.pe.sinnori.common.mybatis;

import java.io.File;
import java.net.URI;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.buildsystem.BuildSystemPathSupporter;
import kr.pe.sinnori.common.type.MybatisMapperType;
import kr.pe.sinnori.common.util.CommonStaticUtil;

public class MybatisFileTypeMapper {
	private final Logger log = LoggerFactory.getLogger(MybatisFileTypeMapper.class);

	private MybatisMapperType mybatisMapperType = null;
	private String mapperTypeValue = null;
	private File mybatisMapperFile = null;

	public MybatisFileTypeMapper(MybatisMapperType mybatisMapperType, String sinnoriInstalledPathString, String mainProjectName,
			String mapperTypeValue) {
		if (null == mybatisMapperType) {
			throw new IllegalArgumentException("the parameter mybatisMapperType is null");
		}

		if (null == sinnoriInstalledPathString) {
			throw new IllegalArgumentException("the parameter sinnoriInstalledPathString is null");
		}

		if (null == mainProjectName) {
			throw new IllegalArgumentException("the parameter mainProjectName is null");
		}

		if (null == mapperTypeValue) {
			throw new IllegalArgumentException("the parameter mapperTypeValue is null");
		}

		this.mybatisMapperType = mybatisMapperType;
		this.mapperTypeValue = mapperTypeValue;

		switch (mybatisMapperType) {
		case CLASS: {
			throw new IllegalArgumentException("the parameter mybatisMapperType is a unsupported type[CLASS]");
		}
		case URL: {
			/*
			 * if (0 != rightValue.indexOf("file:///")) { throw new
			 * IllegalArgumentException("the parameter rightValue is not a file type url's value["
			 * +rightValue+"]"); }
			 */

			URL mapperURL = null;
			try {
				mapperURL = new URL(mapperTypeValue);
			} catch (Exception e) {
				String errorMessage = new StringBuilder("fail to convert the parameter mapperTypeValue[").append(mapperTypeValue)
						.append("] to URL, errormessage=").append(e.getMessage()).toString();
				log.warn(errorMessage, e);
				throw new IllegalArgumentException(errorMessage);
			}

			String protocol = mapperURL.getProtocol();
			if (!protocol.toLowerCase().equals("file")) {
				String errorMessage = new StringBuilder("the parameter mapperTypeValue is not a file type url's value[")
						.append(mapperTypeValue).append("]").toString();
				throw new IllegalArgumentException(errorMessage);
			}

			URI mapperURI = null;
			try {
				mapperURI = mapperURL.toURI();
			} catch (Exception e) {
				String errorMessage = new StringBuilder("fail to convert the parameter mapperTypeValue[").append(mapperTypeValue)
						.append("] to URI, errormessage=").append(e.getMessage()).toString();

				log.warn(errorMessage, e);
				throw new IllegalArgumentException(errorMessage);
			}

			try {
				mybatisMapperFile = new File(mapperURI);
			} catch (Exception e) {
				String errorMessage = new StringBuilder("fail to convert the parameter mapperTypeValue[").append(mapperTypeValue)
						.append("] to FILE, errormessage=").append(e.getMessage()).toString();

				log.warn(errorMessage, e);
				throw new IllegalArgumentException(errorMessage);
			}
			break;
		}
		case RESOURCE: {

			String mainProjectResorucesPathString = BuildSystemPathSupporter
					.getProjectResourcesPathString(sinnoriInstalledPathString, mainProjectName);

			String mybatisMapperFilePathString = CommonStaticUtil
					.getFilePathStringFromResourcePathAndRelativePathOfFile(mainProjectResorucesPathString, mapperTypeValue);

			mybatisMapperFile = new File(mybatisMapperFilePathString);
			break;
		}
		default: {
			String errorMessage = new StringBuilder("the parameter mybatisMapperType[").append(mybatisMapperType.name())
					.append("] is a unknown type").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		}
	}

	public String getMapperTypeName() {
		return mybatisMapperType.name().toLowerCase();
	}

	public String getMapperTypeValue() {
		return mapperTypeValue;
	}

	public File getMybatisMapperFile() {
		return mybatisMapperFile;
	}
}
