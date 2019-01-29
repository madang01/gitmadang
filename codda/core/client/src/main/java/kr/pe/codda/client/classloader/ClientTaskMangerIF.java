package kr.pe.codda.client.classloader;

import kr.pe.codda.client.task.AbstractClientTask;
import kr.pe.codda.common.exception.DynamicClassCallException;

public interface ClientTaskMangerIF {
	public AbstractClientTask getClientTask(String messageID) throws DynamicClassCallException;
}