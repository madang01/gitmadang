package kr.pe.codda.weblib.common;

import java.io.File;

import org.apache.commons.lang3.time.FastDateFormat;

import kr.pe.codda.common.buildsystem.pathsupporter.WebRootBuildSystemPathSupporter;

public abstract class WebCommonStaticUtil {
	
	public static String getShortFileNameOfAttachedFile(short boardID, long boardNo, short attachedFileSeq) {
		return new StringBuilder(WebCommonStaticFinalVars.WEBSITE_ATTACHED_FILE_PREFIX).append("_BoardID")
				.append(boardID).append("_BoardNo")
				.append(boardNo).append("_Seq").append(attachedFileSeq)
				.append(WebCommonStaticFinalVars.WEBSITE_ATTACHED_FILE_SUFFIX).toString();
	}
	
	public static String getAttachedFilePathString(String installedPathString, 
			String mainProjectName, short boardID, long boardNo, short attachedFileSeq) {
		return new StringBuilder(WebRootBuildSystemPathSupporter.getUserWebUploadPathString(installedPathString, mainProjectName)).append(File.separator)
				.append(getShortFileNameOfAttachedFile(boardID, boardNo, attachedFileSeq)).toString();
	}
	
	public static FastDateFormat FULL_DATE_FORMAT = FastDateFormat.getInstance( "yyyy년 MM월 dd일 HH시 mm분 ss초");
	public static FastDateFormat SIMPLE_DATE_FORMAT = FastDateFormat.getInstance( "yyyy.MM.dd HHmmss");
	
}
