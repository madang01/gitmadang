package kr.pe.codda.common.io;

import java.nio.charset.CharsetDecoder;

import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;

public class ReceivedDataOnlyStreamFactory implements ReceivedDataOnlyStreamFactoryIF {
	private CharsetDecoder streamCharsetDecoder = null;
	private int dataPacketBufferMaxCntPerMessage;
	private DataPacketBufferPoolIF dataPacketBufferPool = null;
	
	public ReceivedDataOnlyStreamFactory(CharsetDecoder streamCharsetDecoder,
			int dataPacketBufferMaxCntPerMessage,
			DataPacketBufferPoolIF dataPacketBufferPool) {
		if (null == streamCharsetDecoder) {
			throw new IllegalArgumentException("the parameter streamCharsetDecoder is null");
		}
		
		if (dataPacketBufferMaxCntPerMessage <= 0) {
			String errorMessage = String.format("the parameter dataPacketBufferMaxCntPerMessage[%d] is less than or equal to zero", dataPacketBufferMaxCntPerMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		if (null == dataPacketBufferPool) {
			throw new IllegalArgumentException("the parameter dataPacketBufferPool is null");
		}
		
		this.streamCharsetDecoder = streamCharsetDecoder;
		this.dataPacketBufferMaxCntPerMessage = dataPacketBufferMaxCntPerMessage;
		this.dataPacketBufferPool = dataPacketBufferPool;
	}

	@Override
	public ReceivedDataOnlyStream createReceivedDataOnlyStream() throws NoMoreDataPacketBufferException {
		ReceivedDataOnlyStream receivedDataOnlyStream = new ReceivedDataOnlyStream(streamCharsetDecoder,
				dataPacketBufferMaxCntPerMessage, dataPacketBufferPool);
		return receivedDataOnlyStream;
	}
}
