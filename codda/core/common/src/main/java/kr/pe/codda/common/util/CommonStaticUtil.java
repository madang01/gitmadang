package kr.pe.codda.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.FileUtils;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.BuildSystemException;
import kr.pe.codda.common.type.LineSeparatorType;
import kr.pe.codda.common.type.ReadWriteMode;


public abstract class CommonStaticUtil {	
	/**
	 * 주어진 문자열 앞뒤로 공백 문자 여부를 반환한다. 주의점) 주어진 문자열이 빈 문자열일 경우 true 를 던진다.
	 * @param value 앞뒤로 공백 문자 여부를 알고 싶은 문자열
	 * @return 주어진 문자열 앞뒤로 공백 문자 여부
	 * @throws IllegalArgumentException null 주어진 문자열이 null 인 경우 던진다.
	 */
	public static boolean hasLeadingOrTailingWhiteSpace(String value) throws IllegalArgumentException {
		if (null == value) {
			throw new IllegalArgumentException("the paramater value is null");
		}
		
		
		String trimValue = value.trim();		
		boolean returnValue = !trimValue.equals(value);
		
				
		return returnValue;
	}
	
	public static String getFilePathStringFromResourcePathAndRelativePathOfFile(String resourcesPathString, String relativePath) {
		if (null == resourcesPathString) {
			throw new IllegalArgumentException("the paramter resourcesPathString is null");
		}
		
		if (null == relativePath) {
			throw new IllegalArgumentException("the paramter relativePath is null");
		}
		
		String realResourceFilePathString = null;
		
		String headSeparator = null;
		if (relativePath.indexOf("/") == 0) headSeparator = "";
		else headSeparator = File.separator;
		
		String subRealPathString = null;		
		if (File.separator.equals("/")) {
			subRealPathString = relativePath;
		} else {
			subRealPathString = relativePath.replaceAll("/", "\\\\");
		}		
		
		realResourceFilePathString = new StringBuilder(resourcesPathString)
		.append(headSeparator).append(subRealPathString).toString();
		
		return realResourceFilePathString;
	}
	
	/**
	 * 지정한 칼럼수 단위로 지정한 방식에 맞는 구분 문자열을 추가한 문자열을 반환한다.
	 * @param sourceString 변환을 원하는 문자열
	 * @param lineSeparatorType 지정한 칼럼 마다 삽입을 원하는 문자열 구분, BR: <br/>, NEWLINE: newline
	 * @param wantedColumnSize 원하는 문자열 가로 칼럼수
	 * @return 지정한 칼럼수 단위로 지정한 방식에 맞는 구분 문자열을 추가한 문자열
	 */
	public static String splitString(String sourceString, LineSeparatorType lineSeparatorType, int wantedColumnSize) {
		if (null == sourceString) {
			throw new IllegalArgumentException("the paramter sourceString is null");
		}
		
		if (sourceString.equals("")) {
			throw new IllegalArgumentException("the paramter sourceString is a empty string");
		}
		
		if (hasLeadingOrTailingWhiteSpace(sourceString)) {
			throw new IllegalArgumentException("the paramter sourceString has leading or tailing white space");
		}
		
		if (null == lineSeparatorType) {
			throw new IllegalArgumentException("the paramter lineSeparatorGubun is null");
		}		
		
		if (wantedColumnSize <= 0) {
			throw new IllegalArgumentException("the paramter wantedColumnSize is less or equals to zero");
		}		
		
		String lineSeparator = null;
		if (lineSeparatorType == LineSeparatorType.BR) {
			lineSeparator ="<br/>";
		} else {
			lineSeparator = CommonStaticFinalVars.NEWLINE;
		}
		
		int size = sourceString.length();
		StringBuilder resultStringBuilder = new StringBuilder();
		int i=0;
		for (; i+wantedColumnSize < size; i+=wantedColumnSize) {
			resultStringBuilder.append(sourceString.substring(i, i+wantedColumnSize));
			resultStringBuilder.append(lineSeparator);
		}
		resultStringBuilder.append(sourceString.substring(i));
		return resultStringBuilder.toString();
	}
	
	public static String getMultiLineToolTip(String message, int colSize) {
		if (null == message) {
			throw new IllegalArgumentException("the parameter 'message' is null");
		}
		
		String tooltip = new StringBuilder("<html>")
		.append(CommonStaticUtil.splitString(message, LineSeparatorType.BR, colSize))
		.append("</html>").toString();
		return tooltip;
	}
	
	public static void copyTransferToFile(File sourceFile, File targetFile) throws IOException {
		if (null == sourceFile) {
			throw new IllegalArgumentException("the parameter 'sourceFile' is null");
		}
		
		if (null == targetFile) {
			throw new IllegalArgumentException("the parameter 'targetFile' is null");
		}
		
		
		FileInputStream fis = null;
		FileOutputStream fos = null;

		try {
			fis = new FileInputStream(sourceFile);
			fos = new FileOutputStream(targetFile);

			FileChannel souceFileChannel = fis.getChannel();
			FileChannel targetFileChannel = fos.getChannel();

			souceFileChannel.transferTo(0, souceFileChannel.size(), targetFileChannel);
		} finally {
			try {
				if (null != fis)
					fis.close();
			} catch (Exception e) {
				// log.warn("fail to close source file[" + sourceFile.getAbsolutePath() + "] input stream", e);
			}
			try {
				if (null != fos)
					fos.close();
			} catch (Exception e) {
				InternalLogger log = InternalLoggerFactory.getInstance(CommonStaticUtil.class);
				log.warn("fail to close the file[{}] output stream", targetFile.getAbsolutePath());
			}
		}
	}
	
	public static File getValidPath(String sourcePathString, ReadWriteMode readWriteMode) throws RuntimeException {
		if (null == sourcePathString) {
			throw new IllegalArgumentException("the parameter 'sourcePathString' is null");
		}
		
		File sourcePath = new File(sourcePathString);
		if (!sourcePath.exists()) {
			String errorMessage = String.format("The path[%s] doesn't exist",  sourcePathString);
			throw new RuntimeException(errorMessage);
		}

		if (!sourcePath.isDirectory()) {
			String errorMessage = String.format("The path[%s] is not a directory", 
					sourcePathString);
			throw new RuntimeException(errorMessage);
		}

		if (readWriteMode.equals(ReadWriteMode.ONLY_READ) || readWriteMode.equals(ReadWriteMode.READ_WRITE)) {
			if (!sourcePath.canRead()) {
				String errorMessage = String.format("The path[%s] has a permission to read", 
						sourcePathString);
				throw new RuntimeException(errorMessage);
			}
		}
		
		
		if (readWriteMode.equals(ReadWriteMode.ONLY_WRITE) || readWriteMode.equals(ReadWriteMode.READ_WRITE)) {
			if (!sourcePath.canWrite()) {
				String errorMessage = String.format("The path[%s] has a permission to write", 
						sourcePathString);
				throw new RuntimeException(errorMessage);
			}
		}
		return sourcePath;
	}
	
	public static void createNewFile( File targetFile, String contents, Charset targetCharset) throws FileNotFoundException, IOException {		
		if (null == targetFile) {
			throw new IllegalArgumentException("the parameter 'targetFile' is null");
		}
		if (null == contents) {
			throw new IllegalArgumentException("the parameter 'contents' is null");
		}
		if (null == targetCharset) {
			throw new IllegalArgumentException("the parameter 'targetCharset' is null");
		}
		
		boolean isSuccess = targetFile.createNewFile();
		if (! isSuccess) {
			String errorMessage = String.format("the file[%s] exist", targetFile.getAbsolutePath());
			throw new FileNotFoundException(errorMessage);
		}
		
		if (!targetFile.isFile()) {
			String errorMessage = String.format("the file[%s] is not a regular file",
					targetFile.getAbsolutePath());
			throw new IOException(errorMessage);
		}

		if (!targetFile.canWrite()) {
			String errorMessage = String.format("the file[%s] can not be written", targetFile.getAbsolutePath());
			throw new IOException(errorMessage);
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(targetFile);

			fos.write(contents.getBytes(targetCharset));
		} finally {
			try {
				if (null != fos)
					fos.close();
			} catch (IOException e) {
				// log.warn("fail to close the file[{}][{}] output stream", fileNickname, targetFile.getAbsolutePath());
				// e.printStackTrace();
				InternalLogger log = InternalLoggerFactory.getInstance(CommonStaticUtil.class);
				log.warn("fail to close the file[{}] output stream", targetFile.getAbsolutePath());
			}
		}
	}
	
	
	
	
	public static void overwriteFile( File targetFile, String contents, Charset targetCharset) throws IOException {		
		if (null == targetFile) {
			throw new IllegalArgumentException("the parameter 'targetFile' is null");
		}
		if (null == contents) {
			throw new IllegalArgumentException("the parameter 'contents' is null");
		}
		if (null == targetCharset) {
			throw new IllegalArgumentException("the parameter 'targetCharset' is null");
		}
		
		if (!targetFile.exists()) {
			String errorMessage = String.format("the file[%s] doesn't exist", targetFile.getAbsolutePath());
			throw new IOException(errorMessage);
		}
		
		if (!targetFile.isFile()) {
			String errorMessage = String.format("the file[%s] is not a regular file",
					targetFile.getAbsolutePath());
			throw new IOException(errorMessage);
		}

		if (!targetFile.canWrite()) {
			String errorMessage = String.format("the file[%s] can not be written", targetFile.getAbsolutePath());
			throw new IOException(errorMessage);
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(targetFile);

			fos.write(contents.getBytes(targetCharset));
		} finally {
			try {
				if (null != fos)
					fos.close();
			} catch (IOException e) {
				// log.warn("fail to close the file[{}][{}] output stream", fileNickname, targetFile.getAbsolutePath());
				// e.printStackTrace();
				InternalLogger log = InternalLoggerFactory.getInstance(CommonStaticUtil.class);
				log.warn("fail to close the file[{}] output stream", targetFile.getAbsolutePath());
			}
		}
	}
	
	public static void saveFile( File targetFile, String contents, Charset targetCharset) throws IOException {		
		if (null == targetFile) {
			throw new IllegalArgumentException("the parameter 'targetFile' is null");
		}
		if (null == contents) {
			throw new IllegalArgumentException("the parameter 'contents' is null");
		}
		if (null == targetCharset) {
			throw new IllegalArgumentException("the parameter 'targetCharset' is null");
		}
		
		if (targetFile.exists()) {
			overwriteFile(targetFile, contents, targetCharset);
		} else {
			createNewFile(targetFile, contents, targetCharset);
		}
	}
	
	public static void createChildDirectoriesOfBasePath(String basePathStrig,
			List<String> childRelativeDirectoryList) throws BuildSystemException {
		InternalLogger log = InternalLoggerFactory.getInstance(CommonStaticUtil.class);
		
		for (String childRelativedirectory : childRelativeDirectoryList) {
			// String relativeDir = childDirectories[i];

			// log.info("relativeDir[{}]=[{}]", i, relativeDir);

			String childRealPathString = null;
			
			
			if (File.separator.equals("/")) {
				childRealPathString = new StringBuilder(basePathStrig)
						.append(File.separator).append(childRelativedirectory).toString();
			} else {
				childRealPathString = new StringBuilder(basePathStrig)
						.append(File.separator).append(childRelativedirectory.replaceAll("/", "\\\\")).toString();
			}
			

			File childRealPath = new File(childRealPathString);
			if (!childRealPath.exists()) {
				try {
					FileUtils.forceMkdir(childRealPath);
				} catch (IOException e) {
					String errorMessage = String.format(
							"fail to create a new path[%s][%s]", basePathStrig, childRelativedirectory);
					log.info(errorMessage, e);
					throw new BuildSystemException(errorMessage);
				}

				log.info("the new child relative direcotry[{}][{}] was created successfully",
						basePathStrig, childRelativedirectory);
			} else {
				log.info("the child relative direcotry[{}][{}] exist, so nothing", basePathStrig, childRelativedirectory);
			}

			if (!childRealPath.isDirectory()) {
				String errorMessage = String.format(
						"the child relative direcotry[%s][%s] is not a real directory", basePathStrig, childRelativedirectory);
				// log.warn(errorMessage);
				throw new BuildSystemException(errorMessage);
			}

			if (!childRealPath.canRead()) {
				String errorMessage = String.format(
						"the child relative direcotry[%s][%s] doesn't hava permission to read",
						basePathStrig, childRelativedirectory);
				// log.warn(errorMessage);
				throw new BuildSystemException(errorMessage);
			}

			if (!childRealPath.canWrite()) {
				String errorMessage = String.format(
						"the child relative direcotry[%s][%s] doesn't hava permission to write",
						basePathStrig, childRelativedirectory);
				// log.warn(errorMessage);
				throw new BuildSystemException(errorMessage);
			}

		}
	}
	
	public static String getPrefixWithTabCharacters(int depth, int numberOfAdditionalTabs) {
		if (depth < 0) {
			String errorMessage = String.format("the parameter depth[%d] is less than zero", depth);
			throw new IllegalArgumentException(errorMessage);
		}
		if (numberOfAdditionalTabs < 0) {
			String errorMessage = String.format("the parameter numberOfAdditionalTabs[%d] is less than zero", numberOfAdditionalTabs);
			throw new IllegalArgumentException(errorMessage);
		}
		StringBuilder stringBuilder = new StringBuilder();
		
		addPrefixWithTabCharacters(stringBuilder, depth, numberOfAdditionalTabs);
		
		return stringBuilder.toString();
	}
	
	public static void addPrefixWithTabCharacters(StringBuilder contentsStringBuilder, int depth, int numberOfAdditionalTabs) {
		if (depth < 0) {
			String errorMessage = String.format("the parameter depth[%d] is less than zero", depth);
			throw new IllegalArgumentException(errorMessage);
		}
		if (numberOfAdditionalTabs < 0) {
			String errorMessage = String.format("the parameter numberOfAdditionalTabs[%d] is less than zero", numberOfAdditionalTabs);
			throw new IllegalArgumentException(errorMessage);
		}
		
		
		int numberOfTabCharacters = depth + numberOfAdditionalTabs;
		for (int i=0; i < numberOfTabCharacters; i++) {
			contentsStringBuilder.append("\t");
		}
	}
}
