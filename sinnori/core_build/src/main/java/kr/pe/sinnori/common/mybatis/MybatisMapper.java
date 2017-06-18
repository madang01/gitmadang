package kr.pe.sinnori.common.mybatis;

import java.io.File;

import kr.pe.sinnori.common.buildsystem.BuildSystemPathSupporter;
import kr.pe.sinnori.common.util.CommonStaticUtil;

public class MybatisMapper {
	private String mybatisMapperFileRelativePathString = null;
	private File mybatisMapperFile = null;
	
	public MybatisMapper(String sinnoriInstalledPathString, String mainProjectName, String mybatisMapperFileRelativePathString) {
		this.mybatisMapperFileRelativePathString = mybatisMapperFileRelativePathString;
		
		String mainProjectResorucesPathString = BuildSystemPathSupporter
				.getProjectResourcesPathString(sinnoriInstalledPathString, mainProjectName);		
		
		String mybatisMapperFilePathString = CommonStaticUtil
				.getFilePathStringFromResourcePathAndRelativePathOfFile(
						mainProjectResorucesPathString,
						mybatisMapperFileRelativePathString);
		
		mybatisMapperFile = new File(mybatisMapperFilePathString);	
	}

	public String getMybatisMapperFileRelativePathString() {
		return mybatisMapperFileRelativePathString;
	}

	public File getMybatisMapperFile() {
		return mybatisMapperFile;
	}
}
