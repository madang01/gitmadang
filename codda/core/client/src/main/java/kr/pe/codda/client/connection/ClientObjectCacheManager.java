package kr.pe.codda.client.connection;

import org.apache.commons.collections4.map.LRUMap;
import org.apache.commons.collections4.map.MultiKeyMap;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.client.task.AbstractClientTask;
import kr.pe.codda.common.classloader.IOPartDynamicClassNameUtil;
import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.CoddaConfigurationManager;
import kr.pe.codda.common.config.subset.CommonPartConfiguration;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.protocol.MessageCodecIF;

public class ClientObjectCacheManager implements ClientObjectCacheManagerIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(ClientObjectCacheManager.class);
	
	private final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
	private final int systemClassLoaderHashCode = systemClassLoader.hashCode();
	
	@SuppressWarnings("rawtypes")
	private MultiKeyMap objectMultiKeyMap = null;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ClientObjectCacheManager() {
		CoddaConfiguration runningProjectConfiguration = 
				CoddaConfigurationManager.getInstance()
				.getRunningProjectConfiguration();
		
		CommonPartConfiguration commonPart = runningProjectConfiguration.getCommonPartConfiguration();
		int cachedObjectMaxSize = commonPart.getCachedObjectMaxSize();
		
		
		objectMultiKeyMap = MultiKeyMap.multiKeyMap(new LRUMap(cachedObjectMaxSize));
	}	
	

	@SuppressWarnings("unchecked")
	@Override
	public AbstractClientTask getClientTask(String messageID) throws DynamicClassCallException {
		String classFullName = IOPartDynamicClassNameUtil.getClientTaskClassFullName(messageID);		
		
		AbstractClientTask clientTask = null;		

		Object valueObj = objectMultiKeyMap.get(systemClassLoaderHashCode, classFullName);
		
		if (null == valueObj) {
			try {
				/** classLoader 미 등재 */
				Class<?> cachedObjClass = systemClassLoader.loadClass(classFullName);
				valueObj = cachedObjClass.getDeclaredConstructor().newInstance();
				objectMultiKeyMap.put(systemClassLoaderHashCode, classFullName, valueObj);
			
			} catch (Exception e) {
				String errorMessage = String.format("ClassLoader hashCode=[%d], messageID=[%s], classFullName=[%s]::%s",
						systemClassLoader.hashCode(), messageID, classFullName, e.toString());
				log.warn(errorMessage, e);
				throw new DynamicClassCallException(errorMessage);
			}
		}
		
		clientTask = (AbstractClientTask) valueObj;
		
		// clientTask.setTaskClassLoader(systemClassLoader);

		return clientTask;
	}

	@SuppressWarnings("unchecked")
	@Override
	public MessageCodecIF getClientMessageCodec(ClassLoader currentWorkingClassLoader, String messageID)
			throws DynamicClassCallException {		
		int currentWorkingClassLoaderHashCode = currentWorkingClassLoader.hashCode();

		String classFullName = IOPartDynamicClassNameUtil.getClientMessageCodecClassFullName(messageID);

		MessageCodecIF messageCodec = null;

		Object valueObj = objectMultiKeyMap.get(currentWorkingClassLoaderHashCode, classFullName);
		if (null == valueObj) {
			try {
				/** classLoader 미 등재 */
				Class<?> cachedObjClass = currentWorkingClassLoader.loadClass(classFullName);
				valueObj = cachedObjClass.getDeclaredConstructor().newInstance();
				objectMultiKeyMap.put(currentWorkingClassLoaderHashCode, classFullName, valueObj);
			} catch (Exception e) {
				String errorMessage = String.format("ClassLoader hashCode=[%d], messageID=[%s], classFullName=[%s]::%s",
						currentWorkingClassLoader.hashCode(), messageID, classFullName, e.toString());
				log.warn(errorMessage, e);
				throw new DynamicClassCallException(errorMessage);
			}
		}	

		messageCodec = (MessageCodecIF) valueObj;

		return messageCodec;
	}
	
}
