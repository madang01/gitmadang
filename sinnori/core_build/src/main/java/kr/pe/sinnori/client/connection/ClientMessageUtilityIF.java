package kr.pe.sinnori.client.connection;

import java.util.ArrayList;
import java.util.List;

import kr.pe.sinnori.client.connection.asyn.task.AbstractClientTask;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.HeaderFormatException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.io.SocketOutputStream;
import kr.pe.sinnori.common.io.WrapBuffer;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.protocol.WrapReadableMiddleObject;

public interface ClientMessageUtilityIF {
	public AbstractClientTask getClientTask(String messageID) throws DynamicClassCallException;
	
	public AbstractMessage buildOutputMessage(ClassLoader classLoader, WrapReadableMiddleObject wrapReadableMiddleObject)
			throws DynamicClassCallException, BodyFormatException;
	
	public List<WrapBuffer> buildReadableWrapBufferList(ClassLoader classLoader, AbstractMessage inputMessage) 
			throws DynamicClassCallException, NoMoreDataPacketBufferException, BodyFormatException, HeaderFormatException;
	
	public ArrayList<WrapReadableMiddleObject> getWrapReadableMiddleObjectList(SocketOutputStream socketOutputStream) throws HeaderFormatException, NoMoreDataPacketBufferException;
	
	public void releaseWrapBufferList(List<WrapBuffer> warpBufferList);
}
