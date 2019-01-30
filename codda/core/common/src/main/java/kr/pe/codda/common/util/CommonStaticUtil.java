package kr.pe.codda.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.type.LineSeparatorType;
import kr.pe.codda.common.type.ReadWriteMode;

public abstract class CommonStaticUtil {
	/**
	 * 주어진 문자열 앞뒤로 공백 문자 여부를 반환한다. 주의점) 주어진 문자열이 빈 문자열일 경우 true 를 던진다.
	 * 
	 * @param value
	 *            앞뒤로 공백 문자 여부를 알고 싶은 문자열
	 * @return 주어진 문자열 앞뒤로 공백 문자 여부
	 * @throws IllegalArgumentException
	 *             null 주어진 문자열이 null 인 경우 던진다.
	 */
	public static boolean hasLeadingOrTailingWhiteSpace(String value)
			throws IllegalArgumentException {
		if (null == value) {
			throw new IllegalArgumentException("the paramater value is null");
		}

		String trimValue = value.trim();
		boolean returnValue = !trimValue.equals(value);

		return returnValue;
	}

	public static String getFilePathStringFromResourcePathAndRelativePathOfFile(
			String resourcesPathString, String relativePath) {
		if (null == resourcesPathString) {
			throw new IllegalArgumentException(
					"the paramter resourcesPathString is null");
		}

		if (null == relativePath) {
			throw new IllegalArgumentException(
					"the paramter relativePath is null");
		}

		String realResourceFilePathString = null;

		String headSeparator = null;
		if (relativePath.indexOf("/") == 0)
			headSeparator = "";
		else
			headSeparator = File.separator;

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
	 * 
	 * @param sourceString
	 *            변환을 원하는 문자열
	 * @param lineSeparatorType
	 *            지정한 칼럼 마다 삽입을 원하는 문자열 구분, BR: <br/>
	 *            , NEWLINE: newline
	 * @param wantedColumnSize
	 *            원하는 문자열 가로 칼럼수
	 * @return 지정한 칼럼수 단위로 지정한 방식에 맞는 구분 문자열을 추가한 문자열
	 */
	public static String splitString(String sourceString,
			LineSeparatorType lineSeparatorType, int wantedColumnSize) {
		if (null == sourceString) {
			throw new IllegalArgumentException(
					"the paramter sourceString is null");
		}

		if (sourceString.equals("")) {
			throw new IllegalArgumentException(
					"the paramter sourceString is a empty string");
		}

		if (hasLeadingOrTailingWhiteSpace(sourceString)) {
			throw new IllegalArgumentException(
					"the paramter sourceString has leading or tailing white space");
		}

		if (null == lineSeparatorType) {
			throw new IllegalArgumentException(
					"the paramter lineSeparatorGubun is null");
		}

		if (wantedColumnSize <= 0) {
			throw new IllegalArgumentException(
					"the paramter wantedColumnSize is less or equals to zero");
		}

		String lineSeparator = null;
		if (lineSeparatorType == LineSeparatorType.BR) {
			lineSeparator = "<br/>";
		} else {
			lineSeparator = CommonStaticFinalVars.NEWLINE;
		}

		int size = sourceString.length();
		StringBuilder resultStringBuilder = new StringBuilder();
		int i = 0;
		for (; i + wantedColumnSize < size; i += wantedColumnSize) {
			resultStringBuilder.append(sourceString.substring(i, i
					+ wantedColumnSize));
			resultStringBuilder.append(lineSeparator);
		}
		resultStringBuilder.append(sourceString.substring(i));
		return resultStringBuilder.toString();
	}

	public static String getMultiLineToolTip(String message, int colSize) {
		if (null == message) {
			throw new IllegalArgumentException(
					"the parameter 'message' is null");
		}

		String tooltip = new StringBuilder("<html>")
				.append(CommonStaticUtil.splitString(message,
						LineSeparatorType.BR, colSize)).append("</html>")
				.toString();
		return tooltip;
	}

	public static void copyTransferToFile(File sourceFile, File targetFile)
			throws IOException {
		if (null == sourceFile) {
			throw new IllegalArgumentException(
					"the parameter 'sourceFile' is null");
		}

		if (null == targetFile) {
			throw new IllegalArgumentException(
					"the parameter 'targetFile' is null");
		}

		FileInputStream fis = null;
		FileOutputStream fos = null;

		try {
			fis = new FileInputStream(sourceFile);
			fos = new FileOutputStream(targetFile);

			FileChannel souceFileChannel = fis.getChannel();
			FileChannel targetFileChannel = fos.getChannel();

			souceFileChannel.transferTo(0, souceFileChannel.size(),
					targetFileChannel);
		} finally {
			try {
				if (null != fis) {
					fis.close();
				}
			} catch (Exception e) {
				InternalLogger log = InternalLoggerFactory
						.getInstance(CommonStaticUtil.class);
				log.warn("fail to close the file[{}] input stream",
						targetFile.getAbsolutePath());
			}
			try {
				if (null != fos) {
					fos.close();
				}
			} catch (Exception e) {
				InternalLogger log = InternalLoggerFactory
						.getInstance(CommonStaticUtil.class);
				log.warn("fail to close the file[{}] output stream",
						targetFile.getAbsolutePath());
			}
		}
	}

	public static File getValidPath(String sourcePathString,
			ReadWriteMode readWriteMode) throws RuntimeException {
		if (null == sourcePathString) {
			throw new IllegalArgumentException(
					"the parameter 'sourcePathString' is null");
		}

		File sourcePath = new File(sourcePathString);
		if (!sourcePath.exists()) {
			String errorMessage = String.format("The path[%s] doesn't exist",
					sourcePathString);
			throw new RuntimeException(errorMessage);
		}

		if (!sourcePath.isDirectory()) {
			String errorMessage = String.format(
					"The path[%s] is not a directory", sourcePathString);
			throw new RuntimeException(errorMessage);
		}

		if (readWriteMode.equals(ReadWriteMode.ONLY_READ)
				|| readWriteMode.equals(ReadWriteMode.READ_WRITE)) {
			if (!sourcePath.canRead()) {
				String errorMessage = String.format(
						"The path[%s] has a permission to read",
						sourcePathString);
				throw new RuntimeException(errorMessage);
			}
		}

		if (readWriteMode.equals(ReadWriteMode.ONLY_WRITE)
				|| readWriteMode.equals(ReadWriteMode.READ_WRITE)) {
			if (!sourcePath.canWrite()) {
				String errorMessage = String.format(
						"The path[%s] has a permission to write",
						sourcePathString);
				throw new RuntimeException(errorMessage);
			}
		}
		return sourcePath;
	}

	public static void createNewFile(File targetFile, String contents,
			Charset targetCharset) throws FileNotFoundException, IOException {
		if (null == targetFile) {
			throw new IllegalArgumentException(
					"the parameter 'targetFile' is null");
		}
		if (null == contents) {
			throw new IllegalArgumentException(
					"the parameter 'contents' is null");
		}
		if (null == targetCharset) {
			throw new IllegalArgumentException(
					"the parameter 'targetCharset' is null");
		}

		boolean isSuccess = targetFile.createNewFile();
		if (!isSuccess) {
			String errorMessage = String.format("the file[%s] exist",
					targetFile.getAbsolutePath());
			throw new FileNotFoundException(errorMessage);
		}

		if (!targetFile.isFile()) {
			String errorMessage = String.format(
					"the file[%s] is not a regular file",
					targetFile.getAbsolutePath());
			throw new IOException(errorMessage);
		}

		if (!targetFile.canWrite()) {
			String errorMessage = String.format(
					"the file[%s] can not be written",
					targetFile.getAbsolutePath());
			throw new IOException(errorMessage);
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(targetFile);

			fos.write(contents.getBytes(targetCharset));
		} finally {
			try {
				if (null != fos) {
					fos.close();
				}
			} catch (IOException e) {
				// log.warn("fail to close the file[{}][{}] output stream",
				// fileNickname, targetFile.getAbsolutePath());
				// e.printStackTrace();
				InternalLogger log = InternalLoggerFactory
						.getInstance(CommonStaticUtil.class);
				log.warn("fail to close the file[{}] output stream",
						targetFile.getAbsolutePath());
			}
		}
	}

	public static void overwriteFile(File targetFile, String contents,
			Charset targetCharset) throws IOException {
		if (null == targetFile) {
			throw new IllegalArgumentException(
					"the parameter 'targetFile' is null");
		}
		if (null == contents) {
			throw new IllegalArgumentException(
					"the parameter 'contents' is null");
		}
		if (null == targetCharset) {
			throw new IllegalArgumentException(
					"the parameter 'targetCharset' is null");
		}

		if (!targetFile.exists()) {
			String errorMessage = String.format("the file[%s] doesn't exist",
					targetFile.getAbsolutePath());
			throw new IOException(errorMessage);
		}

		if (!targetFile.isFile()) {
			String errorMessage = String.format(
					"the file[%s] is not a regular file",
					targetFile.getAbsolutePath());
			throw new IOException(errorMessage);
		}

		if (!targetFile.canWrite()) {
			String errorMessage = String.format(
					"the file[%s] can not be written",
					targetFile.getAbsolutePath());
			throw new IOException(errorMessage);
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(targetFile, false);

			fos.write(contents.getBytes(targetCharset));
		} finally {
			try {
				if (null != fos) {
					fos.close();
				}
			} catch (IOException e) {
				// log.warn("fail to close the file[{}][{}] output stream",
				// fileNickname, targetFile.getAbsolutePath());
				// e.printStackTrace();
				InternalLogger log = InternalLoggerFactory
						.getInstance(CommonStaticUtil.class);
				log.warn("fail to close the file[{}] output stream",
						targetFile.getAbsolutePath());
			}
		}
	}

	public static void saveFile(File targetFile, String contents,
			Charset targetCharset) throws IOException {
		if (null == targetFile) {
			throw new IllegalArgumentException(
					"the parameter 'targetFile' is null");
		}
		if (null == contents) {
			throw new IllegalArgumentException(
					"the parameter 'contents' is null");
		}
		if (null == targetCharset) {
			throw new IllegalArgumentException(
					"the parameter 'targetCharset' is null");
		}

		if (targetFile.exists()) {
			overwriteFile(targetFile, contents, targetCharset);
		} else {
			createNewFile(targetFile, contents, targetCharset);
		}
	}

	public static String getPrefixWithTabCharacters(int depth,
			int numberOfAdditionalTabs) {
		if (depth < 0) {
			String errorMessage = String.format(
					"the parameter depth[%d] is less than zero", depth);
			throw new IllegalArgumentException(errorMessage);
		}
		if (numberOfAdditionalTabs < 0) {
			String errorMessage = String
					.format("the parameter numberOfAdditionalTabs[%d] is less than zero",
							numberOfAdditionalTabs);
			throw new IllegalArgumentException(errorMessage);
		}
		StringBuilder stringBuilder = new StringBuilder();

		addPrefixWithTabCharacters(stringBuilder, depth, numberOfAdditionalTabs);

		return stringBuilder.toString();
	}

	public static void addPrefixWithTabCharacters(
			StringBuilder contentsStringBuilder, int depth,
			int numberOfAdditionalTabs) {
		if (depth < 0) {
			String errorMessage = String.format(
					"the parameter depth[%d] is less than zero", depth);
			throw new IllegalArgumentException(errorMessage);
		}
		if (numberOfAdditionalTabs < 0) {
			String errorMessage = String
					.format("the parameter numberOfAdditionalTabs[%d] is less than zero",
							numberOfAdditionalTabs);
			throw new IllegalArgumentException(errorMessage);
		}

		int numberOfTabCharacters = depth + numberOfAdditionalTabs;
		for (int i = 0; i < numberOfTabCharacters; i++) {
			contentsStringBuilder.append("\t");
		}
	}

	/**
	 * @param c
	 * @return 낱글자 한글 포함한 한글 여부를 반환한다
	 */
	public static boolean isHangul(final char c) {
		boolean isHangul = false;
		if (c >= 'ㄱ' && c <= 'ㅎ') {
			isHangul = true;
		} else if (c >= 'ㅏ' && c <= 'ㅣ') {
			isHangul = true;
		} else if (c >= '가' && c <= '힣') {
			isHangul = true;
		}
		return isHangul;
	}

	/**
	 * 
	 * @param c
	 * @return 초성과 중성 혹은 종성까지 조합된 한글 여부를 반환한다
	 */
	public static boolean isFullHangul(final char c) {
		boolean isHangul = false;
		if (c >= '가' && c <= '힣') {
			isHangul = true;
		}
		return isHangul;
	}

	/*
	 * public static boolean isAlphabet(final char c) { boolean isAlphabet =
	 * false; if (c >= 'a' && c <= 'z') { isAlphabet = true; } else if (c >= 'A'
	 * && c <= 'Z') { isAlphabet = true; } return isAlphabet; }
	 * 
	 * public static boolean isDigit(final char c) { boolean isDigit = false; if
	 * (c >= '0' && c <= '9') { isDigit = true; } return isDigit; }
	 */

	public static boolean isPunct(final char c) {
		boolean isPunct = false;

		if (c >= '!' && c <= '/') {
			isPunct = true;
		} else if (c >= ':' && c <= '@') {
			isPunct = true;
		} else if (c >= '[' && c <= '`') {
			isPunct = true;
		} else if (c >= '{' && c <= '~') {
			isPunct = true;
		}
		return isPunct;
	}

	public static boolean isLineSeparator(char c) {
		boolean isWhiteSpace = false;
		if (('\r' == c) || ('\n' == c) || ('\u0085' == c) || ('\u2028' == c)
				|| ('\u2029' == c)) {
			isWhiteSpace = true;
		}

		return isWhiteSpace;
	}

	public static boolean isAlphabetAndDigit(String sourceString) {
		for (char c : sourceString.toCharArray()) {
			if (!Character.isDigit(c) && !Character.isAlphabetic(c)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isAlphabetAndDigitWithRegular(String sourceString) {
		String regex = "[a-zA-Z0-9]+";

		boolean isValid = sourceString.matches(regex);
		return isValid;
	}

	public static byte[] readFileToByteArray(File sourceFile, int maxSize)
			throws IOException {
		if (null == sourceFile) {
			throw new IllegalArgumentException(
					"the parameter sourceFile is null");
		}

		if (!sourceFile.exists()) {
			String errorMessage = new StringBuilder("the parameter sourceFile[")
					.append(sourceFile.getAbsolutePath())
					.append("] doesn't exist").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (!sourceFile.isFile()) {
			String errorMessage = new StringBuilder("the parameter sourceFile[")
					.append(sourceFile.getAbsolutePath())
					.append("] is not a normal file").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (!sourceFile.canRead()) {
			String errorMessage = new StringBuilder("the parameter sourceFile[")
					.append(sourceFile.getAbsolutePath())
					.append("] can't be read").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (sourceFile.length() > maxSize) {
			String errorMessage = new StringBuilder("the parameter sourceFile[")
					.append(sourceFile.getAbsolutePath()).append("]'s size[")
					.append(sourceFile.length())
					.append("] is greater than max[").append(maxSize)
					.append("]").toString();
			throw new IllegalArgumentException(errorMessage);
		}

		byte[] result = new byte[(int) sourceFile.length()];

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(sourceFile);
			fis.read(result);
		} catch (IOException e) {
			throw e;
		} finally {
			if (null != fis) {
				try {
					fis.close();
				} catch (Exception e) {
				}
			}
		}

		return result;
	}

	public static Object getNewObjectFromClassloader(
			ClassLoader targetClassLoader, String classFullName)
			throws DynamicClassCallException {
		Class<?> retClass = null;

		try {
			retClass = targetClassLoader.loadClass(classFullName);
		} catch (ClassNotFoundException e) {
			String errorMessage = new StringBuilder()
					.append("fail to find the class[")
					.append(classFullName)
					.append("], errmsg=")
					.append(e.getMessage()).toString();
			throw new DynamicClassCallException(errorMessage);
		}

		Object retObject = null;
		try {
			retObject = retClass.getDeclaredConstructor().newInstance();
		} catch (InstantiationException e) {
			String errorMessage = new StringBuilder()
					.append("the classloader[")
					.append(targetClassLoader.hashCode())
					.append("] failed to create a instance of ")
					.append(classFullName)
					.append(" class, InstantiationException errmsg=")
					.append(e.getMessage()).toString();

			throw new DynamicClassCallException(errorMessage);
		} catch (IllegalAccessException e) {
			String errorMessage = new StringBuilder()
					.append("the classloader[")
					.append(targetClassLoader.hashCode())
					.append("] failed to create a instance of ")
					.append(classFullName)
					.append(" class, IllegalAccessException errmsg=")
					.append(e.getMessage()).toString();
			
			throw new DynamicClassCallException(errorMessage);
		} catch (IllegalArgumentException e) {
			String errorMessage = new StringBuilder()
					.append("the classloader[")
					.append(targetClassLoader.hashCode())
					.append("] failed to create a instance of ")
					.append(classFullName)
					.append(" class, IllegalArgumentException errmsg=")
					.append(e.getMessage()).toString();
			
			
			throw new DynamicClassCallException(errorMessage);
		} catch (InvocationTargetException e) {
			Throwable targetException = e.getTargetException();
			String errorMessage = new StringBuilder()
					.append("the classloader[")
					.append(targetClassLoader.hashCode())
					.append("] failed to create a instance of ")
					.append(classFullName)
					.append(" class, InvocationTargetException errmsg=")
					.append(targetException.getMessage()).toString();
			
			InternalLogger log = InternalLoggerFactory.getInstance(CommonStaticUtil.class);
			log.warn(errorMessage, targetException);

			throw new DynamicClassCallException(errorMessage);
		} catch (NoSuchMethodException e) {
			String errorMessage = new StringBuilder()
					.append("the classloader[")
					.append(targetClassLoader.hashCode())
					.append("] failed to create a instance of ")
					.append(classFullName)
					.append(" class, NoSuchMethodException errmsg=")
					.append(e.getMessage()).toString();
			
			throw new DynamicClassCallException(errorMessage);
		} catch (SecurityException e) {
			String errorMessage = new StringBuilder()
					.append("the classloader[")
					.append(targetClassLoader.hashCode())
					.append("] failed to create a instance of ")
					.append(classFullName)
					.append(" class, SecurityException errmsg=")
					.append(e.getMessage()).toString();

			throw new DynamicClassCallException(errorMessage);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("the classloader[")
					.append(targetClassLoader.hashCode())
					.append("] failed to create a instance of ")
					.append(classFullName)
					.append(" class, unknwon error errmsg=")
					.append(e.getMessage()).toString();
			
			InternalLogger log = InternalLoggerFactory.getInstance(CommonStaticUtil.class);
			log.warn(errorMessage, e);

			throw new DynamicClassCallException(errorMessage);
		}

		return retObject;
	}
}
