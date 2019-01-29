package kr.pe.codda.client.classloader;

import java.util.HashMap;

import kr.pe.codda.client.task.AbstractClientTask;
import kr.pe.codda.common.classloader.IOPartDynamicClassNameUtil;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.util.CommonStaticUtil;

public class ClientStaticTaskManger implements ClientTaskMangerIF {
	private final Object monitor = new Object();
	private final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
	
	private final HashMap<String, AbstractClientTask> messageID2ClientTaskHash = new HashMap<String, AbstractClientTask>();	

	@Override
	public AbstractClientTask getClientTask(String messageID) throws DynamicClassCallException {
		synchronized (monitor) {
			AbstractClientTask clientTask = messageID2ClientTaskHash.get(messageID);
			
			if (null == clientTask) {
				String clientTaskClassFullName = IOPartDynamicClassNameUtil.getClientTaskClassFullName(messageID);
				
				Object retObject = CommonStaticUtil.getNewObjectFromClassloader(systemClassLoader, clientTaskClassFullName);
				
				if (! (retObject instanceof AbstractClientTask)) {
					String errorMessage = new StringBuilder()
					.append("this instance of ").append(clientTaskClassFullName)
					.append(" class that was created by system classloader is not a instance of AbstractClientTask class").toString();

					throw new DynamicClassCallException(errorMessage);
				}
				
				clientTask = (AbstractClientTask) retObject;
			}		
			
			return clientTask;
		}
	}
}
