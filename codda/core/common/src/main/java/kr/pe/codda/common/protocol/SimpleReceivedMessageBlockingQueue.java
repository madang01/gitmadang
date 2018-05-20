package kr.pe.codda.common.protocol;

import java.util.concurrent.ArrayBlockingQueue;

public class SimpleReceivedMessageBlockingQueue implements ReceivedMessageBlockingQueueIF {
	ArrayBlockingQueue<WrapReadableMiddleObject> wrapReadableMiddleObjectQueue = null;
	
	public SimpleReceivedMessageBlockingQueue(ArrayBlockingQueue<WrapReadableMiddleObject> wrapReadableMiddleObjectQueue) {
		this.wrapReadableMiddleObjectQueue = wrapReadableMiddleObjectQueue;
	}			

	@Override
	public void putReceivedMessage(WrapReadableMiddleObject wrapReadableMiddleObject)
			throws InterruptedException {
		wrapReadableMiddleObjectQueue.put(wrapReadableMiddleObject);
	}
}
