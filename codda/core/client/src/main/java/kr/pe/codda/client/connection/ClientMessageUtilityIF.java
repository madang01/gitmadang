package kr.pe.codda.client.connection;

import java.util.ArrayDeque;

import kr.pe.codda.client.connection.asyn.executor.AbstractClientTask;
import kr.pe.codda.common.exception.BodyFormatException;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.HeaderFormatException;
import kr.pe.codda.common.exception.NoMoreDataPacketBufferException;
import kr.pe.codda.common.io.SocketOutputStream;
import kr.pe.codda.common.io.WrapBuffer;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.protocol.ReceivedMessageBlockingQueueIF;
import kr.pe.codda.common.protocol.ReadableMiddleObjectWrapper;

public interface ClientMessageUtilityIF {
	public AbstractClientTask getClientTask(String messageID) throws DynamicClassCallException;
	
	public AbstractMessage buildOutputMessage(ClassLoader classLoader, ReadableMiddleObjectWrapper readableMiddleObjectWrapper)
			throws DynamicClassCallException, BodyFormatException;
	
	public ArrayDeque<WrapBuffer> buildReadableWrapBufferList(ClassLoader classLoader, AbstractMessage inputMessage) 
			throws DynamicClassCallException, NoMoreDataPacketBufferException, BodyFormatException, HeaderFormatException;
	
	public void S2MList(Object eventHandler, SocketOutputStream socketOutputStream, ReceivedMessageBlockingQueueIF wrapMessageBlockingQueue)
			throws HeaderFormatException, NoMoreDataPacketBufferException, InterruptedException;
	
	public void releaseWrapBuffer(WrapBuffer warpBuffer);
}
