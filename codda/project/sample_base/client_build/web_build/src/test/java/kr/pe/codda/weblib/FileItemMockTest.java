package kr.pe.codda.weblib;

import static org.junit.Assert.*;

import java.io.File;

import junitlib.AbstractJunitTest;

import kr.pe.codda.common.buildsystem.pathsupporter.WebRootBuildSystemPathSupporter;

import org.apache.commons.fileupload.FileItem;
import org.junit.Test;

public class FileItemMockTest extends AbstractJunitTest {

	@Test
	public void testOK() {
		String selectedUploadFilePathString = new StringBuilder()
		.append(WebRootBuildSystemPathSupporter.getUserWebRootPathString(installedPath.getAbsolutePath(), mainProjectName))
		.append(File.separator)
		.append("images")
		.append(File.separator).append("sinnori_server_framework01.png").toString();
		
		File selectedUploadFile = new File(selectedUploadFilePathString);
		
		if (! selectedUploadFile.exists()) {
			log.warn("업로드할 대상 파일[{}]이 존재하지 않습니다", selectedUploadFilePathString);
			fail("업로드할 대상 파일이 존재하지 않습니다");
		}
		
		if (! selectedUploadFile.isFile()) {
			log.warn("업로드할 대상 파일[{}]이 일반 파일이 아닙니다", selectedUploadFilePathString);
			fail("업로드할 대상 파일이 존재하지 않습니다");
		}
		
		FileItem fileItem = null;			
		try {
			fileItem = new FileItemMock(selectedUploadFile, "newAttachedFile");
		} catch(Exception e) {
			log.warn("fail to create a instance of FileItemMock class", e);
			fail("fail to create a instance of FileItemMock class");
		}
		
		
		
		log.info(fileItem.toString());
	}

}
