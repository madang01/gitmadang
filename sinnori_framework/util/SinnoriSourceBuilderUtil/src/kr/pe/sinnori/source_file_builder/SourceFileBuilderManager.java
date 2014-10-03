package kr.pe.sinnori.source_file_builder;


public class SourceFileBuilderManager {
	private MessageSourceFileBuilder messageSourceFileBuilder = new MessageSourceFileBuilder();
	private DecoderSourceFileBuilder decoderSourceFileBuilder = new DecoderSourceFileBuilder();
	private EncoderSourceFileBuilder encoderSourceFileBuilder = new EncoderSourceFileBuilder();
	private ClientCodecSourceFileBuilder clientCodecSourceFileBuilder = new ClientCodecSourceFileBuilder();
	private ServerCodecSourceFileBuilder serverCodecSourceFileBuilder = new ServerCodecSourceFileBuilder();

	/** 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 클래스 */
	private static final class SourceFileBuilderManagerHolder {
		static final SourceFileBuilderManager singleton = new SourceFileBuilderManager();
	}

	/** 동기화 쓰지 않는 싱글턴 구현 메소드 */
	public static SourceFileBuilderManager getInstance() {
		return SourceFileBuilderManagerHolder.singleton;
	}

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 생성자
	 * @throws NoMoreDataPacketBufferException 
	 */
	private SourceFileBuilderManager() {
		
	}

	public MessageSourceFileBuilder getMessageSourceFileBuilder() {
		return messageSourceFileBuilder;
	}

	public DecoderSourceFileBuilder getDecoderSourceFileBuilder() {
		return decoderSourceFileBuilder;
	}

	public EncoderSourceFileBuilder getEncoderSourceFileBuilder() {
		return encoderSourceFileBuilder;
	}

	public ClientCodecSourceFileBuilder getClientCodecSourceFileBuilder() {
		return clientCodecSourceFileBuilder;
	}

	public ServerCodecSourceFileBuilder getServerCodecSourceFileBuilder() {
		return serverCodecSourceFileBuilder;
	}
}
