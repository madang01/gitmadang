package kr.pe.sinnori.common.message.builder;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import kr.pe.sinnori.common.buildsystem.BuildSystemPathSupporter;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.CommonType;
import kr.pe.sinnori.common.etc.CommonType.LOG_TYPE;
import kr.pe.sinnori.common.message.builder.info.MessageInfo;
import kr.pe.sinnori.common.message.builder.info.MessageInfoSAXParser;
import kr.pe.sinnori.common.util.CommonStaticUtil;

public class IOFileSetContentsBuilderManagerTest {
	Logger log = null;

	final String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
	final String mainProjectName = "sample_base";

	@Before
	public void setup() {

		LOG_TYPE logType = LOG_TYPE.SERVER;
		String logbackConfigFilePathString = BuildSystemPathSupporter
				.getLogbackConfigFilePathString(sinnoriInstalledPathString, mainProjectName);
		String sinnoriLogPathString = BuildSystemPathSupporter.getLogPathString(sinnoriInstalledPathString,
				mainProjectName, logType);

		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_INSTALLED_PATH,
				sinnoriInstalledPathString);
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_RUNNING_PROJECT_NAME,
				mainProjectName);

		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_SINNORI_LOG_PATH, sinnoriLogPathString);
		System.setProperty(CommonStaticFinalVars.JAVA_SYSTEM_PROPERTIES_KEY_LOGBACK_CONFIG_FILE,
				logbackConfigFilePathString);

		log = LoggerFactory.getLogger(IOFileSetContentsBuilderManagerTest.class);
	}

	@Test
	public void test_SelfExn메시지IO관련파일만들기() {
		final String messageID = "SelfExn";

		String messageInfoFilesPathString = BuildSystemPathSupporter
				.getMessageInfoFilesPathString(sinnoriInstalledPathString);

		String messageInfoFilePathString = BuildSystemPathSupporter
				.getMessageInfoFilePathString(sinnoriInstalledPathString, messageID);

		File xmlFile = new File(messageInfoFilePathString);
		MessageInfoSAXParser messageInfoSAXParser = null;
		try {
			messageInfoSAXParser = new MessageInfoSAXParser();
		} catch (SAXException e) {
			String errorMessage = "error::" + e.toString();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}

		MessageInfo messageInfo = null;
		try {
			messageInfo = messageInfoSAXParser.parse(xmlFile, true);
		} catch (Exception e) {
			String errorMessage = "error::" + e.toString();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}

		// log.info(messageInfo.toString());

		String author = "Won Jonghoon";

		IOFileSetContentsBuilderManager ioFileSetContentsBuilderManager = IOFileSetContentsBuilderManager.getInstance();

		{
			String messageSourceFileContent = ioFileSetContentsBuilderManager.getMessageSourceFileContents(author,
					messageInfo);

			File messageSourceFile = new File(new StringBuilder(messageInfoFilesPathString).append(File.separator)
					.append(messageID).append(".java").toString());

			try {
				CommonStaticUtil.saveFile(messageSourceFile, messageSourceFileContent,
						CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
			} catch (IOException e) {
				String errorMessage = String.format("fail to save file[%s][%s]::%s", "the message source file",
						messageSourceFile.getAbsolutePath(), e.getMessage());
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
		}

		{
			String encoderSourceFileContents = ioFileSetContentsBuilderManager.getEncoderSourceFileContents(author,
					messageInfo);

			File encoderSourceFile = new File(new StringBuilder(messageInfoFilesPathString).append(File.separator)
					.append(messageID).append("Encoder.java").toString());

			try {
				CommonStaticUtil.saveFile(encoderSourceFile, encoderSourceFileContents,
						CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
			} catch (IOException e) {
				String errorMessage = String.format("fail to save file[%s][%s]::%s", "the message encoder source file",
						encoderSourceFile.getAbsolutePath(), e.getMessage());
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
		}

		{
			String decoderSourceFileContents = ioFileSetContentsBuilderManager
					.getDecoderSourceFileContents(author, messageInfo);

			File decoderSourceFile = new File(new StringBuilder(messageInfoFilesPathString).append(File.separator)
					.append(messageID).append("Decoder.java").toString());

			try {
				CommonStaticUtil.saveFile(decoderSourceFile, decoderSourceFileContents,
						CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
			} catch (IOException e) {
				String errorMessage = String.format("fail to save file[%s][%s]::%s", "the message decoder source file",
						decoderSourceFile.getAbsolutePath(), e.getMessage());
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
		}

		{
			String clientCodecSourceFileContents = ioFileSetContentsBuilderManager.getClientCodecSourceFileContents(
					CommonType.MESSAGE_TRANSFER_DIRECTION.FROM_ALL_TO_ALL, messageID, author);

			File clientCodecSourceFile = new File(new StringBuilder(messageInfoFilesPathString).append(File.separator)
					.append(messageID).append("ClientCodec.java").toString());

			try {
				CommonStaticUtil.saveFile(clientCodecSourceFile, clientCodecSourceFileContents,
						CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
			} catch (IOException e) {
				String errorMessage = String.format("fail to save file[%s][%s]::%s",
						"the message client codec source file", clientCodecSourceFile.getAbsolutePath(),
						e.getMessage());
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
		}

		{
			String serverCodecSourceFileContents = ioFileSetContentsBuilderManager.getServerCodecSourceFileContents(
					CommonType.MESSAGE_TRANSFER_DIRECTION.FROM_ALL_TO_ALL, messageID, author);

			File serverCodecSourceFile = new File(new StringBuilder(messageInfoFilesPathString).append(File.separator)
					.append(messageID).append("ServerCodec.java").toString());

			try {
				CommonStaticUtil.saveFile(serverCodecSourceFile, serverCodecSourceFileContents,
						CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
			} catch (IOException e) {
				String errorMessage = String.format("fail to save file[%s][%s]::%s",
						"the message server codec source file", serverCodecSourceFile.getAbsolutePath(),
						e.getMessage());
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
		}
	}

	// FIXME!
	@Test
	public void test_AllItemTypeReq_메시지IO관련파일만들기() {
		final String messageID = "AllItemTypeReq";

		String messageInfoFilesPathString = BuildSystemPathSupporter
				.getMessageInfoFilesPathString(sinnoriInstalledPathString);

		String messageInfoFilePathString = BuildSystemPathSupporter
				.getMessageInfoFilePathString(sinnoriInstalledPathString, messageID);

		File xmlFile = new File(messageInfoFilePathString);
		MessageInfoSAXParser messageInfoSAXParser = null;
		try {
			messageInfoSAXParser = new MessageInfoSAXParser();
		} catch (SAXException e) {
			String errorMessage = "error::" + e.toString();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}

		MessageInfo messageInfo = null;
		try {
			messageInfo = messageInfoSAXParser.parse(xmlFile, true);
		} catch (Exception e) {
			String errorMessage = "error::" + e.toString();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}

		// log.info(messageInfo.toString());

		String author = "Won Jonghoon";

		IOFileSetContentsBuilderManager ioFileSetContentsBuilderManager = IOFileSetContentsBuilderManager.getInstance();

		{
			String messageSourceFileContent = ioFileSetContentsBuilderManager.getMessageSourceFileContents(author, messageInfo);

			File messageSourceFile = new File(new StringBuilder(messageInfoFilesPathString).append(File.separator)
					.append(messageID).append(".java").toString());

			try {
				CommonStaticUtil.saveFile(messageSourceFile, messageSourceFileContent,
						CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
			} catch (IOException e) {
				String errorMessage = String.format("fail to save file[%s][%s]::%s", "the message source file",
						messageSourceFile.getAbsolutePath(), e.getMessage());
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
		}

		{
			String encoderSourceFileContents = ioFileSetContentsBuilderManager.getEncoderSourceFileContents(author, messageInfo);

			File encoderSourceFile = new File(new StringBuilder(messageInfoFilesPathString).append(File.separator)
					.append(messageID).append("Encoder.java").toString());
			
			// FIXME! TDD 위해서 core 소스 디렉토리에 바로 저장
			/*File encoderSourceFile = new File(new StringBuilder("D:\\gitsinnori\\sinnori\\core_build\\src\\main\\java\\kr\\pe\\sinnori\\impl\\message\\AllItemTypeReq").append(File.separator)
					.append(messageID).append("Encoder.java").toString());*/

			try {
				CommonStaticUtil.saveFile(encoderSourceFile, encoderSourceFileContents,
						CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
			} catch (IOException e) {
				String errorMessage = String.format("fail to save file[%s][%s]::%s", "the message encoder source file",
						encoderSourceFile.getAbsolutePath(), e.getMessage());
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
		}

		{
			String decoderSourceFileContents = ioFileSetContentsBuilderManager.getDecoderSourceFileContents(author, messageInfo);

			File decoderSourceFile = new File(new StringBuilder(messageInfoFilesPathString).append(File.separator)
					.append(messageID).append("Decoder.java").toString());
			
			// FIXME! TDD 위해서 core 소스 디렉토리에 바로 저장 
			/*File decoderSourceFile = new File(new StringBuilder("D:\\gitsinnori\\sinnori\\core_build\\src\\main\\java\\kr\\pe\\sinnori\\impl\\message\\AllItemTypeReq").append(File.separator)
					.append(messageID).append("Decoder.java").toString());*/

			try {
				CommonStaticUtil.saveFile(decoderSourceFile, decoderSourceFileContents,
						CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
			} catch (IOException e) {
				String errorMessage = String.format("fail to save file[%s][%s]::%s", "the message decoder source file",
						decoderSourceFile.getAbsolutePath(), e.getMessage());
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
		}

		{
			String clientCodecSourceFileContents = ioFileSetContentsBuilderManager.getClientCodecSourceFileContents(
					CommonType.MESSAGE_TRANSFER_DIRECTION.FROM_ALL_TO_ALL, messageID, author);

			File clientCodecSourceFile = new File(new StringBuilder(messageInfoFilesPathString).append(File.separator)
					.append(messageID).append("ClientCodec.java").toString());

			try {
				CommonStaticUtil.saveFile(clientCodecSourceFile, clientCodecSourceFileContents,
						CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
			} catch (IOException e) {
				String errorMessage = String.format("fail to save file[%s][%s]::%s",
						"the message client codec source file", clientCodecSourceFile.getAbsolutePath(),
						e.getMessage());
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
		}

		{
			String serverCodecSourceFileContents = ioFileSetContentsBuilderManager.getServerCodecSourceFileContents(
					CommonType.MESSAGE_TRANSFER_DIRECTION.FROM_ALL_TO_ALL, messageID, author);

			File serverCodecSourceFile = new File(new StringBuilder(messageInfoFilesPathString).append(File.separator)
					.append(messageID).append("ServerCodec.java").toString());

			try {
				CommonStaticUtil.saveFile(serverCodecSourceFile, serverCodecSourceFileContents,
						CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
			} catch (IOException e) {
				String errorMessage = String.format("fail to save file[%s][%s]::%s",
						"the message server codec source file", serverCodecSourceFile.getAbsolutePath(),
						e.getMessage());
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
		}
	}
}
