package kr.pe.sinnori.server;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.sinnori.common.classloader.IOPartDynamicClassNameUtil;
import kr.pe.sinnori.common.classloader.SimpleClassLoader;
import kr.pe.sinnori.common.etc.ObjectCacheManager;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.protocol.MessageCodecIF;
import kr.pe.sinnori.server.task.AbstractServerTask;

public class ServerObjectCacheManager implements ServerObjectCacheManagerIF {
	private Logger log = LoggerFactory.getLogger(ServerObjectCacheManager.class);
		
	private final Object monitor = new Object();
	
	private String serverAPPINFClassPathString = null;
	
	private final ObjectCacheManager objectCacheManager = ObjectCacheManager
			.getInstance();
	private IOPartDynamicClassNameUtil ioPartDynamicClassNameUtil = null;
	private SimpleClassLoader workBaseClassLoader = null;	
	
	private final HashMap<String, ServerTaskObjectInfo> className2ServerTaskObjectInfoHash = new HashMap<String, ServerTaskObjectInfo>();
	
	public ServerObjectCacheManager(String serverAPPINFClassPathString, String firstPrefixDynamicClassFullName) {
		this.serverAPPINFClassPathString = serverAPPINFClassPathString;
		
		this.ioPartDynamicClassNameUtil = new IOPartDynamicClassNameUtil(firstPrefixDynamicClassFullName);
		this.workBaseClassLoader = createNewServerClassLoader(serverAPPINFClassPathString);
	}
	
	private SimpleClassLoader createNewServerClassLoader(String serverAPPINFClassPathString) {
		return new SimpleClassLoader(serverAPPINFClassPathString, ioPartDynamicClassNameUtil);
	}

	private ServerTaskObjectInfo getServerTaskFromWorkBaseClassload(
			String classFullName) throws DynamicClassCallException {
		Class<?> retClass = null;
		AbstractServerTask serverTask = null;
		String classFileName = null;
		
		
		synchronized (monitor) {
			try {
				retClass = workBaseClassLoader.loadClass(classFullName);
			} catch (ClassNotFoundException e) {
				// String errorMessage =
				// String.format("ServerClassLoader hashCode=[%d], classFullName=[%s]::ClassNotFoundException",
				// this.hashCode(), classFullName);
				// log.warn("ClassNotFoundException", e);
				throw new DynamicClassCallException(e.getMessage());
			}
			
			classFileName = workBaseClassLoader
					.getClassFileName(classFullName);
		}
		
	
		Object retObject = null;
		try {			
			retObject = retClass.getDeclaredConstructor().newInstance();
		} catch (InstantiationException e) {
			String errorMessage = String
					.format("ServerClassLoader hashCode=[%d], classFullName=[%s]::InstantiationException",
							this.hashCode(), classFullName);
			log.warn(errorMessage);
			throw new DynamicClassCallException(errorMessage);
		} catch (IllegalAccessException e) {
			String errorMessage = String
					.format("ServerClassLoader hashCode=[%d], classFullName=[%s]::IllegalAccessException",
							this.hashCode(), classFullName);
			log.warn(errorMessage);
			throw new DynamicClassCallException(errorMessage);
		} catch (IllegalArgumentException e) {
			String errorMessage = String
					.format("ServerClassLoader hashCode=[%d], classFullName=[%s]::IllegalArgumentException",
							this.hashCode(), classFullName);
			log.warn(errorMessage);
			throw new DynamicClassCallException(errorMessage);
		} catch (InvocationTargetException e) {
			String errorMessage = String
					.format("ServerClassLoader hashCode=[%d], classFullName=[%s]::InvocationTargetException",
							this.hashCode(), classFullName);
			log.warn(errorMessage);
			throw new DynamicClassCallException(errorMessage);
		} catch (NoSuchMethodException e) {
			String errorMessage = String
					.format("ServerClassLoader hashCode=[%d], classFullName=[%s]::NoSuchMethodException",
							this.hashCode(), classFullName);
			log.warn(errorMessage);
			throw new DynamicClassCallException(errorMessage);
		} catch (SecurityException e) {
			String errorMessage = String
					.format("ServerClassLoader hashCode=[%d], classFullName=[%s]::SecurityException",
							this.hashCode(), classFullName);
			log.warn(errorMessage);
			throw new DynamicClassCallException(errorMessage);
		}
	
		/*
		 * if (! (retObject instanceof AbstractServerTask)) { // FIXME! 죽은 코드
		 * 이어여함, 발생시 원인 제거 필요함 String errorMessage =
		 * String.format("ServerClassLoader hashCode=[%d], classFullName=[%s]" +
		 * "::클래스명으로 얻은 객체 타입[%s]이 AbstractServerTask 가 아닙니다.", this.hashCode(),
		 * classFullName, retObject.getClass().getCanonicalName());
		 * 
		 * log.warn(errorMessage); throw new
		 * DynamicClassCallException(errorMessage); }
		 */
		serverTask = (AbstractServerTask) retObject;
	
		
	
		// log.info("classFileName={}", classFileName);
	
		File serverTaskClassFile = new File(classFileName);
	
		return new ServerTaskObjectInfo(serverTaskClassFile, serverTask);
	}

	@Override
	public MessageCodecIF getServerMessageCodec(ClassLoader classLoader,
			String messageID) throws DynamicClassCallException {		
		String classFullName = ioPartDynamicClassNameUtil.getServerMessageCodecClassFullName(messageID);

		MessageCodecIF messageCodec = null;

		Object valueObj = null;
		
		try {
			valueObj = objectCacheManager.getCachedObject(classLoader,
					classFullName);
		} catch (Exception e) {
			String errorMessage = String
					.format("fail to get cached object::ClassLoader hashCode=[%d], messageID=[%s], classFullName=[%s]",
							classLoader.hashCode(), messageID,
							classFullName);
			log.warn(errorMessage, e);
			throw new DynamicClassCallException(errorMessage);
		}
		

		/*
		 * if (null == valueObj) { String errorMessage = String.format(
		 * "ClassLoader hashCode=[%d], messageID=[%s], classFullName=[%s]::valueObj is null"
		 * , classLoader.hashCode(), messageID, classFullName);
		 * log.warn(errorMessage); new DynamicClassCallException(errorMessage);
		 * }
		 * 
		 * if (!(valueObj instanceof MessageCodecIF)) { String errorMessage =
		 * String.format(
		 * "ClassLoader hashCode=[%d], messageID=[%s], classFullName=[%s]::valueObj type[%s] is not  MessageCodecIF"
		 * , classLoader.hashCode(), messageID, classFullName,
		 * valueObj.getClass().getCanonicalName()); log.warn(errorMessage); new
		 * DynamicClassCallException(errorMessage); }
		 */

		messageCodec = (MessageCodecIF) valueObj;

		return messageCodec;
	}

	@Override
	public AbstractServerTask getServerTask(String messageID)
			throws DynamicClassCallException {

		/*String classFullName = new StringBuilder(
				projectPartConfiguration.getClassLoaderClassPackagePrefixName()).append("servertask.")
				.append(messageID).append("ServerTask").toString();*/
		
		String classFullName = ioPartDynamicClassNameUtil.getServerTaskClassFullName(messageID);
		
		
		ServerTaskObjectInfo serverTaskObjectInfo = null;
		synchronized (monitor) {
			serverTaskObjectInfo = className2ServerTaskObjectInfoHash
					.get(classFullName);
			if (null == serverTaskObjectInfo) {
				serverTaskObjectInfo = getServerTaskFromWorkBaseClassload(classFullName);

				className2ServerTaskObjectInfoHash.put(classFullName,
						serverTaskObjectInfo);
			} else {
				if (serverTaskObjectInfo.isModifed()) {
					/** 새로운 서버 클래스 로더로 교체 */
					workBaseClassLoader = createNewServerClassLoader(serverAPPINFClassPathString);
					serverTaskObjectInfo = getServerTaskFromWorkBaseClassload(classFullName);
					className2ServerTaskObjectInfoHash.put(classFullName,
							serverTaskObjectInfo);
				}
			}
		}
		return serverTaskObjectInfo.getServerTask();
	}
}