package kr.pe.codda.client.connection.sync;

import kr.pe.codda.common.protocol.ReadableMiddleObjectWrapper;
import kr.pe.codda.common.protocol.ReceivedMessageBlockingQueueIF;

public class SyncReceivedMessageBlockingQueue implements ReceivedMessageBlockingQueueIF {
	// private InternalLogger log = InternalLoggerFactory.getInstance(SyncReceivedMessageBlockingQueue.class);
	
	private ReadableMiddleObjectWrapper readableMiddleObjectWrapper = null;
	
	public void reset() {
		readableMiddleObjectWrapper = null;
	}
	
	@Override
	public void putReceivedMessage(ReadableMiddleObjectWrapper readableMiddleObjectWrapper)
			throws InterruptedException {
		// log.info("call::{}", readableMiddleObjectWrapper.toSimpleInformation());
		
		if (null == this.readableMiddleObjectWrapper) {
			this.readableMiddleObjectWrapper = readableMiddleObjectWrapper;
		} else {
			throw new IllegalStateException("the received message has arrived more than once");
		}
	}
	
	public ReadableMiddleObjectWrapper getReadableMiddleObjectWrapper() throws IllegalStateException {
		return readableMiddleObjectWrapper;
	}
	
	public boolean isReceivedMessage() {
		return (null != readableMiddleObjectWrapper);
	}
	
}
