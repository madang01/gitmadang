package kr.pe.codda.client.classloader;

import java.io.File;
import java.util.HashMap;

import kr.pe.codda.client.task.AbstractClientTask;
import kr.pe.codda.common.classloader.IOPartDynamicClassNameUtil;
import kr.pe.codda.common.classloader.SimpleClassLoader;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.util.CommonStaticUtil;

public class ClientDynamicTaskManger implements ClientTaskMangerIF {
	private final Object monitor = new Object();
	
	private ClientClassLoaderFactory clientClassLoaderFactory = null;
	private SimpleClassLoader currentWorkingClientClassLoader = null;

	private final HashMap<String, ClientTaskInfomation> messageID2ClientTaskInformationHash = new HashMap<String, ClientTaskInfomation>();
	
	public ClientDynamicTaskManger(ClientClassLoaderFactory clientClassLoaderFactory) {
		this.clientClassLoaderFactory = clientClassLoaderFactory;

		this.currentWorkingClientClassLoader = clientClassLoaderFactory.createClientClassLoader();
	}
	
	private ClientTaskInfomation getNewClientTaskFromWorkBaseClassload(String messageID)
			throws DynamicClassCallException {
		String clientTaskClassFullName = IOPartDynamicClassNameUtil.getClientTaskClassFullName(messageID);
		
		Object retObject = CommonStaticUtil.getNewObjectFromClassloader(currentWorkingClientClassLoader, clientTaskClassFullName);
		
		if (! (retObject instanceof AbstractClientTask)) {
			String errorMessage = new StringBuilder()
					.append("this instance of ")
					.append(clientTaskClassFullName)
					.append(" class that was created by client dynamic classloader[")
					.append(currentWorkingClientClassLoader.hashCode())			
					.append("] class is not a instance of AbstractClientTask class").toString();
			throw new DynamicClassCallException(errorMessage);
		}
		
		AbstractClientTask clientTask = (AbstractClientTask) retObject;
		String clientTaskClassFilePathString = currentWorkingClientClassLoader.getClassFilePathString(clientTaskClassFullName);

		// serverTask.setServerSimpleClassloader(currentWorkingClassLoader);;
		// log.info("classFileName={}", classFileName);

		File clientTaskClassFile = new File(clientTaskClassFilePathString);

		return new ClientTaskInfomation(clientTaskClassFile, clientTask);
	}
	
	private ClientTaskInfomation getClientTaskInfomation(String messageID)
			throws DynamicClassCallException {
		synchronized (monitor) {
			ClientTaskInfomation clientTaskInfomation = messageID2ClientTaskInformationHash.get(messageID);
			if (null == clientTaskInfomation) {
				// lock.lock();			
				clientTaskInfomation = getNewClientTaskFromWorkBaseClassload(messageID);

				messageID2ClientTaskInformationHash.put(messageID, clientTaskInfomation);
							
			} else if (clientTaskInfomation.isModifed()) {
				/** 새로운 서버 클래스 로더로 교체 */
				currentWorkingClientClassLoader = clientClassLoaderFactory.createClientClassLoader();
				clientTaskInfomation = getNewClientTaskFromWorkBaseClassload(messageID);
				messageID2ClientTaskInformationHash.put(messageID, clientTaskInfomation);
					
			}
			return clientTaskInfomation;
		}		
	}

	@Override
	public AbstractClientTask getClientTask(String messageID) throws DynamicClassCallException {
		ClientTaskInfomation clientTaskInfomation = getClientTaskInfomation(messageID);
		return clientTaskInfomation.getClientTask();
	}

}
