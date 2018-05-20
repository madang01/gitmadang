package kr.pe.codda.common.protocol;

public interface ReceivedMessageBlockingQueueIF {
	public void putReceivedMessage(WrapReadableMiddleObject wrapReadableMiddleObject) throws InterruptedException;
}
