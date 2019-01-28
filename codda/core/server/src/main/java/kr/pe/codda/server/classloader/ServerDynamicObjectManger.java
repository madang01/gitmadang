package kr.pe.codda.server.classloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;

import kr.pe.codda.common.classloader.IOPartDynamicClassNameUtil;
import kr.pe.codda.common.classloader.SimpleClassLoader;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.server.ServerTaskObjectInfo;
import kr.pe.codda.server.task.AbstractServerTask;

public class ServerDynamicObjectManger implements ServerDynamicObjectMangerIF {
	// private InternalLogger log = InternalLoggerFactory.getInstance(ServerDynamicObjectManger.class);

	// private final Object monitor = new Object();

	private ServerClassLoaderFactory serverClassLoaderFactory = null;

	private SimpleClassLoader currentWorkingServerClassLoader = null;

	private final HashMap<String, ServerTaskObjectInfo> messageID2ServerTaskObjectInfoHash = new HashMap<String, ServerTaskObjectInfo>();
	// private ReentrantLock lock = new ReentrantLock();

	public ServerDynamicObjectManger(ServerClassLoaderFactory serverClassLoaderFactory) {
		this.serverClassLoaderFactory = serverClassLoaderFactory;

		this.currentWorkingServerClassLoader = serverClassLoaderFactory.createServerClassLoader();
	}

	private ServerTaskObjectInfo getNewServerTaskFromWorkBaseClassload(String classFullName)
			throws DynamicClassCallException {
		Object retObject = CommonStaticUtil.getNewObjectFromClassloader(currentWorkingServerClassLoader, classFullName);
		
		if (! (retObject instanceof AbstractServerTask)) {
			String errorMessage = new StringBuilder()
			.append("ServerClassLoader hashCode=[")
			.append(currentWorkingServerClassLoader.hashCode())
			.append("]::this instance of ").append(classFullName)
			.append("] class is not a instance of AbstractServerTask class").toString();

			throw new DynamicClassCallException(errorMessage);
		}
		
		AbstractServerTask serverTask = (AbstractServerTask) retObject;
		String classFileName = currentWorkingServerClassLoader.getClassFileName(classFullName);

		// serverTask.setServerSimpleClassloader(currentWorkingClassLoader);;
		// log.info("classFileName={}", classFileName);

		File serverTaskClassFile = new File(classFileName);

		return new ServerTaskObjectInfo(serverTaskClassFile, serverTask);
	}
	
	
	private ServerTaskObjectInfo getServerTaskInfo(String messageID)
			throws DynamicClassCallException, FileNotFoundException {
		ServerTaskObjectInfo serverTaskObjectInfo = messageID2ServerTaskObjectInfoHash.get(messageID);
		if (null == serverTaskObjectInfo) {
			// lock.lock();
			
					
			String classFullName = IOPartDynamicClassNameUtil.getServerTaskClassFullName(messageID);
			serverTaskObjectInfo = getNewServerTaskFromWorkBaseClassload(classFullName);

			messageID2ServerTaskObjectInfoHash.put(messageID, serverTaskObjectInfo);
						
		} else if (serverTaskObjectInfo.isModifed()) {
			/** 새로운 서버 클래스 로더로 교체 */
			currentWorkingServerClassLoader = serverClassLoaderFactory.createServerClassLoader();
			String classFullName = IOPartDynamicClassNameUtil.getServerTaskClassFullName(messageID);
			serverTaskObjectInfo = getNewServerTaskFromWorkBaseClassload(classFullName);
			messageID2ServerTaskObjectInfoHash.put(messageID, serverTaskObjectInfo);
				
		}
		return serverTaskObjectInfo;
	}
	
	@Override
	public AbstractServerTask getServerTask(String messageID) throws DynamicClassCallException, FileNotFoundException {
		ServerTaskObjectInfo serverTaskObjectInfo = getServerTaskInfo(messageID);
	
		return serverTaskObjectInfo.getServerTask();
	}
}
