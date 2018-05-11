package kr.pe.codda.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.classloader.IOPartDynamicClassNameUtil;
import kr.pe.codda.common.classloader.SimpleClassLoader;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.server.task.AbstractServerTask;

public class ServerObjectCacheManager implements ServerObjectCacheManagerIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(ServerObjectCacheManager.class);

	// private final Object monitor = new Object();

	private ServerClassLoaderBuilder serverClassLoaderBuilder = null;

	private SimpleClassLoader workBaseClassLoader = null;

	private final ConcurrentHashMap<String, ServerTaskObjectInfo> className2ServerTaskObjectInfoHash = new ConcurrentHashMap<String, ServerTaskObjectInfo>();
	private ReentrantLock lock = new ReentrantLock();

	public ServerObjectCacheManager(ServerClassLoaderBuilder serverClassLoaderBuilder) {
		this.serverClassLoaderBuilder = serverClassLoaderBuilder;

		this.workBaseClassLoader = serverClassLoaderBuilder.build();
	}

	private ServerTaskObjectInfo getNewServerTaskFromWorkBaseClassload(String classFullName)
			throws DynamicClassCallException {
		Class<?> retClass = null;
		AbstractServerTask serverTask = null;
		String classFileName = null;

		try {
			retClass = workBaseClassLoader.loadClass(classFullName);		
		} catch (ClassNotFoundException e) {
			// String errorMessage =
			// String.format("ServerClassLoader hashCode=[%d],
			// classFullName=[%s]::ClassNotFoundException",
			// this.hashCode(), classFullName);
			// log.warn("ClassNotFoundException", e);
			throw new DynamicClassCallException(e.getMessage());
		}

		classFileName = workBaseClassLoader.getClassFileName(classFullName);

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

		/*
		 * if (! (retObject instanceof AbstractServerTask)) { // FIXME! 죽은 코드 이어여함, 발생시
		 * 원인 제거 필요함 String errorMessage =
		 * String.format("ServerClassLoader hashCode=[%d], classFullName=[%s]" +
		 * "::클래스명으로 얻은 객체 타입[%s]이 AbstractServerTask 가 아닙니다.", this.hashCode(),
		 * classFullName, retObject.getClass().getCanonicalName());
		 * 
		 * log.warn(errorMessage); throw new DynamicClassCallException(errorMessage); }
		 */
		serverTask = (AbstractServerTask) retObject;
		
		serverTask.setServerSimpleClassloader(workBaseClassLoader);;

		// log.info("classFileName={}", classFileName);

		File serverTaskClassFile = new File(classFileName);

		return new ServerTaskObjectInfo(serverTaskClassFile, serverTask);
	}
	
	/*public MessageCodecIF getServerMessageCodec(ClassLoader classLoader, String messageID)
			throws DynamicClassCallException {
		String classFullName = ioPartDynamicClassNameUtil.getServerMessageCodecClassFullName(messageID);

		MessageCodecIF messageCodec = null;

		Object valueObj = null;

		try {
			valueObj = objectCacheManager.getCachedObject(classLoader, classFullName);
		} catch (Exception e) {
			String errorMessage = String.format(
					"fail to get cached object::ClassLoader hashCode=[%d], messageID=[%s], classFullName=[%s]",
					classLoader.hashCode(), messageID, classFullName);
			log.warn(errorMessage, e);
			throw new DynamicClassCallException(errorMessage);
		}	

		messageCodec = (MessageCodecIF) valueObj;

		return messageCodec;
	}*/

	private ServerTaskObjectInfo buildNewServerTaskObjectInfo(String messageID) throws DynamicClassCallException {
		String classFullName = IOPartDynamicClassNameUtil.getServerTaskClassFullName(messageID);
		ServerTaskObjectInfo newServerTaskObjectInfo = getNewServerTaskFromWorkBaseClassload(classFullName);

		className2ServerTaskObjectInfoHash.put(messageID, newServerTaskObjectInfo);
		
		return newServerTaskObjectInfo;
	}

	private ServerTaskObjectInfo getServerTaskInfo(String messageID)
			throws DynamicClassCallException, FileNotFoundException {
		ServerTaskObjectInfo serverTaskObjectInfo = className2ServerTaskObjectInfoHash.get(messageID);
		if (null == serverTaskObjectInfo) {
			lock.lock();
			try {
				serverTaskObjectInfo = className2ServerTaskObjectInfoHash.get(messageID);
				if (null == serverTaskObjectInfo) {
					
					serverTaskObjectInfo = buildNewServerTaskObjectInfo(messageID);
				}
			} finally {
				lock.unlock();
			}			
		} else {
			if (serverTaskObjectInfo.isModifed()) {
				lock.lock();
				try {
					if (serverTaskObjectInfo.isModifed()) {
						/** 새로운 서버 클래스 로더로 교체 */
						workBaseClassLoader = serverClassLoaderBuilder.build();
						serverTaskObjectInfo = buildNewServerTaskObjectInfo(messageID);
					}
				} finally {
					lock.unlock();
				}
			}
		}
		return serverTaskObjectInfo;
	}
	
	@Override
	public AbstractServerTask getServerTask(String messageID) throws DynamicClassCallException, FileNotFoundException {
		ServerTaskObjectInfo serverTaskObjectInfo = getServerTaskInfo(messageID);
	
		return serverTaskObjectInfo.getServerTask();
	}
}
