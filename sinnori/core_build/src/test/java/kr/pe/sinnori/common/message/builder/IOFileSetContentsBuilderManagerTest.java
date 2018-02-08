package kr.pe.sinnori.common.message.builder;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.xml.sax.SAXException;

import kr.pe.sinnori.common.AbstractJunitTest;
import kr.pe.sinnori.common.buildsystem.BuildSystemPathSupporter;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.message.builder.info.MessageInfo;
import kr.pe.sinnori.common.message.builder.info.MessageInfoSAXParser;
import kr.pe.sinnori.common.type.MessageTransferDirectionType;
import kr.pe.sinnori.common.util.CommonStaticUtil;

public class IOFileSetContentsBuilderManagerTest extends AbstractJunitTest {
	@Test
	public void test_신놀이설치경로에있는메시지정보파일로부터메시지IO관련파일만들기() {
		String[] targetMessageIDList = {
				"SelfExnRes",
				"AllItemType",
				"Echo",
				"Empty"
		};
		
		final String author = "Won Jonghoon";
		MessageInfoSAXParser messageInfoSAXParser = null;
		try {
			messageInfoSAXParser = new MessageInfoSAXParser();
		} catch (SAXException e) {
			String errorMessage = "error::" + e.toString();
			log.warn(errorMessage, e);
			fail(errorMessage);
		}
		
		String tmpPathString = BuildSystemPathSupporter.getSinnoriTempPathString(sinnoriInstalledPathString);
		
		log.info("the temp directory[{}] where the target messge's io file set will be saved", tmpPathString);
		
		for (String targetMessageID : targetMessageIDList) {
			log.info("the target message[{}]'s io file set creation work start", 
					targetMessageID);
			
			String messageInfoFilePathString = BuildSystemPathSupporter
					.getMessageInfoFilePathString(sinnoriInstalledPathString, targetMessageID);
			File xmlFile = new File(messageInfoFilePathString);
			
			log.info("the message information file[{}]", 
					messageInfoFilePathString);
			
			MessageInfo messageInfo = null;
			try {
				messageInfo = messageInfoSAXParser.parse(xmlFile, true);
			} catch (Exception e) {
				String errorMessage = "error::" + e.toString();
				log.warn(errorMessage, e);
				fail(errorMessage);
			}
			
			IOFileSetContentsBuilderManager ioFileSetContentsBuilderManager = IOFileSetContentsBuilderManager.getInstance();

			{
				String messageSourceFileContent = ioFileSetContentsBuilderManager.getMessageSourceFileContents(author,
						messageInfo);

				File messageSourceFile = new File(new StringBuilder(tmpPathString).append(File.separator)
						.append(targetMessageID).append(".java").toString());

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

				File encoderSourceFile = new File(new StringBuilder(tmpPathString).append(File.separator)
						.append(targetMessageID).append("Encoder.java").toString());

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

				File decoderSourceFile = new File(new StringBuilder(tmpPathString).append(File.separator)
						.append(targetMessageID).append("Decoder.java").toString());

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
						MessageTransferDirectionType.FROM_ALL_TO_ALL, targetMessageID, author);

				File clientCodecSourceFile = new File(new StringBuilder(tmpPathString).append(File.separator)
						.append(targetMessageID).append("ClientCodec.java").toString());

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
						MessageTransferDirectionType.FROM_ALL_TO_ALL, targetMessageID, author);

				File serverCodecSourceFile = new File(new StringBuilder(tmpPathString).append(File.separator)
						.append(targetMessageID).append("ServerCodec.java").toString());

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
}
