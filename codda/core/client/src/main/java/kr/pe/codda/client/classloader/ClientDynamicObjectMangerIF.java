package kr.pe.codda.client.classloader;

import kr.pe.codda.client.connection.asyn.executor.AbstractClientTask;
import kr.pe.codda.common.exception.DynamicClassCallException;

public interface ClientDynamicObjectMangerIF {
	public AbstractClientTask getClientTask(String messageID) throws DynamicClassCallException;
	
	public Object getDynamicObject(String classFullName);
}