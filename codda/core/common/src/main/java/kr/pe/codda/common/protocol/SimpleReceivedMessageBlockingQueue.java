package kr.pe.codda.common.protocol;

import java.util.concurrent.ArrayBlockingQueue;

public class SimpleReceivedMessageBlockingQueue implements ReceivedMessageBlockingQueueIF {
	ArrayBlockingQueue<ReadableMiddleObjectWrapper> readableMiddleObjectWrapperQueue = null;
	
	public SimpleReceivedMessageBlockingQueue(ArrayBlockingQueue<ReadableMiddleObjectWrapper> readableMiddleObjectWrapperQueue) {
		this.readableMiddleObjectWrapperQueue = readableMiddleObjectWrapperQueue;
	}

	@Override
	public void putReceivedMessage(ReadableMiddleObjectWrapper readableMiddleObjectWrapper)
			throws InterruptedException {
		readableMiddleObjectWrapperQueue.put(readableMiddleObjectWrapper);
	}
}
