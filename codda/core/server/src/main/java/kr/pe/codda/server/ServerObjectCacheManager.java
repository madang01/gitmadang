package kr.pe.codda.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.classloader.IOPartDynamicClassNameUtil;
import kr.pe.codda.common.classloader.SimpleClassLoader;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.server.task.AbstractServerTask;

public class ServerObjectCacheManager implements ServerObjectCacheManagerIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(ServerObjectCacheManager.class);

	// private final Object monitor = new Object();

	private ServerClassLoaderFactory serverClassLoaderFactory = null;

	private SimpleClassLoader currentWorkingClassLoader = null;

	private final HashMap<String, ServerTaskObjectInfo> messageID2ServerTaskObjectInfoHash = new HashMap<String, ServerTaskObjectInfo>();
	// private ReentrantLock lock = new ReentrantLock();

	public ServerObjectCacheManager(ServerClassLoaderFactory serverClassLoaderFactory) {
		this.serverClassLoaderFactory = serverClassLoaderFactory;

		this.currentWorkingClassLoader = serverClassLoaderFactory.createServerClassLoader();
	}

	private ServerTaskObjectInfo getNewServerTaskFromWorkBaseClassload(String classFullName)
			throws DynamicClassCallException {
		Class<?> retClass = null;
		AbstractServerTask serverTask = null;
		String classFileName = null;

		try {
			retClass = currentWorkingClassLoader.loadClass(classFullName);		
		} catch (ClassNotFoundException e) {
			// String errorMessage =
			// String.format("ServerClassLoader hashCode=[%d],
			// classFullName=[%s]::ClassNotFoundException",
			// this.hashCode(), classFullName);
			// log.warn("ClassNotFoundException", e);
			throw new DynamicClassCallException(e.getMessage());
		}

		classFileName = currentWorkingClassLoader.getClassFileName(classFullName);

		Object retObject = null;
		try {
			retObject = retClass.getDeclaredConstructor().newInstance();
		} catch (InstantiationException e) {
			String errorMessage = String.format(
					"ServerClassLoader hashCode=[%d], classFullName=[%s]::InstantiationException", this.hashCode(),
					classFullName);
			log.warn(errorMessage);
			throw new DynamicClassCallException(errorMessage);
		} catch (IllegalAccessException e) {
			String errorMessage = String.format(
					"ServerClassLoader hashCode=[%d], classFullName=[%s]::IllegalAccessException", this.hashCode(),
					classFullName);
			log.warn(errorMessage);
			throw new DynamicClassCallException(errorMessage);
		} catch (IllegalArgumentException e) {
			String errorMessage = String.format(
					"ServerClassLoader hashCode=[%d], classFullName=[%s]::IllegalArgumentException", this.hashCode(),
					classFullName);
			log.warn(errorMessage);
			throw new DynamicClassCallException(errorMessage);
		} catch (InvocationTargetException e) {
			String errorMessage = String.format(
					"ServerClassLoader hashCode=[%d], classFullName=[%s]::InvocationTargetException", this.hashCode(),
					classFullName);
			log.warn(errorMessage);
			throw new DynamicClassCallException(errorMessage);
		} catch (NoSuchMethodException e) {
			String errorMessage = String.format(
					"ServerClassLoader hashCode=[%d], classFullName=[%s]::NoSuchMethodException", this.hashCode(),
					classFullName);
			log.warn(errorMessage);
			throw new DynamicClassCallException(errorMessage);
		} catch (SecurityException e) {
			String errorMessage = String.format(
					"ServerClassLoader hashCode=[%d], classFullName=[%s]::SecurityException", this.hashCode(),
					classFullName);
			log.warn(errorMessage);
			throw new DynamicClassCallException(errorMessage);
		}
		
		serverTask = (AbstractServerTask) retObject;
		
		serverTask.setServerSimpleClassloader(currentWorkingClassLoader);;

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
			currentWorkingClassLoader = serverClassLoaderFactory.createServerClassLoader();
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
