package kr.pe.sinnori.common.message.builder;

import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.message.builder.info.MessageInfo;
import kr.pe.sinnori.common.type.MessageTransferDirectionType;


public class IOFileSetContentsBuilderManager {
	private MessageFileContensBuilder messageFileContensBuilder = null;
	private DecoderFileContensBuilder decoderFileContensBuilder = null;
	private EncoderFileContensBuilder encoderFileContensBuilder = null;
	private ClientCodecFileContensBuilder clientCodecFileContensBuilder = null;
	private ServerCodecFileContensBuilder serverCodecFileContensBuilder = null;

	/** 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 클래스 */
	private static final class MessageProcessFileContentsManagerHolder {
		static final IOFileSetContentsBuilderManager singleton = new IOFileSetContentsBuilderManager();
	}

	/** 동기화 쓰지 않는 싱글턴 구현 메소드 */
	public static IOFileSetContentsBuilderManager getInstance() {
		return MessageProcessFileContentsManagerHolder.singleton;
	}

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 생성자
	 * @throws NoMoreDataPacketBufferException 
	 */
	private IOFileSetContentsBuilderManager() {
		messageFileContensBuilder = new MessageFileContensBuilder();
		decoderFileContensBuilder = new DecoderFileContensBuilder();
		encoderFileContensBuilder = new EncoderFileContensBuilder();
		clientCodecFileContensBuilder = new ClientCodecFileContensBuilder();
		serverCodecFileContensBuilder = new ServerCodecFileContensBuilder();
	}

	public String getMessageSourceFileContents(String author, MessageInfo messageInfo) {
		return messageFileContensBuilder.buildStringOfFileContents(messageInfo.getMessageID(), author, messageInfo);
	}
	
	public String getDecoderSourceFileContents(String author,
			MessageInfo messageInfo) {		
		return decoderFileContensBuilder.buildStringOfFileContents(author, messageInfo);
	}

	public String getEncoderSourceFileContents(String author,
			MessageInfo messageInfo) {
		return encoderFileContensBuilder.buildStringOfFileContents(author, messageInfo);
	}
	
	public String getClientCodecSourceFileContents(
			MessageTransferDirectionType connectionDirectionMode,
			String messageID, String author) {
		return clientCodecFileContensBuilder.buildStringOfFileContents(connectionDirectionMode, messageID, author);
	}

	public String getServerCodecSourceFileContents(MessageTransferDirectionType connectionDirectionMode, String messageID, String author) {
		return serverCodecFileContensBuilder.buildStringOfFileContents(connectionDirectionMode, messageID, author);
	}
}
