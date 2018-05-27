package kr.pe.codda.common.protocol;

public interface ReceivedMessageBlockingQueueIF {
	public void putReceivedMessage(ReadableMiddleObjectWrapper readableMiddleObjectWrapper) throws InterruptedException;
}
