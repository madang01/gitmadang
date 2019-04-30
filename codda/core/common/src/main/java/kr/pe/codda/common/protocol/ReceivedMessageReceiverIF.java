package kr.pe.codda.common.protocol;

public interface ReceivedMessageReceiverIF {
	// public void putReceivedMessage(ReadableMiddleObjectWrapper readableMiddleObjectWrapper) throws InterruptedException;
	public void putReceivedMessage(int mailboxID, int mailID, String messageID, Object readableMiddleObject) throws InterruptedException;
}
