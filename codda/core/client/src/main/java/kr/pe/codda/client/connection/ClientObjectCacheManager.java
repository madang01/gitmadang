package kr.pe.codda.client.connection;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.client.connection.asyn.executor.AbstractClientTask;
import kr.pe.codda.common.classloader.IOPartDynamicClassNameUtil;
import kr.pe.codda.common.etc.ObjectCacheManager;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.protocol.MessageCodecIF;

public class ClientObjectCacheManager implements ClientObjectCacheManagerIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(ClientObjectCacheManager.class);
	
	
	private final ObjectCacheManager objectCacheManager = ObjectCacheManager
			.getInstance();

	@Override
	public AbstractClientTask getClientTask(String messageID) throws DynamicClassCallException {
		String classFullName = IOPartDynamicClassNameUtil.getClientTaskClassFullName(messageID);

		AbstractClientTask clientTask = null;

		ClassLoader classLoader = ClassLoader.getSystemClassLoader();

		Object valueObj = null;
		try {
			try {
				valueObj = objectCacheManager.getCachedObject(classLoader, classFullName);
			} catch (Exception e) {
				String errorMessage = String.format("ClassLoader hashCode=[%d], messageID=[%s], classFullName=[%s]::%s",
						classLoader.hashCode(), messageID, classFullName, e.toString());
				log.warn(errorMessage, e);
				throw new DynamicClassCallException(errorMessage);
			}
		} catch (IllegalArgumentException e) {
			String errorMessage = String.format(
					"ClassLoader hashCode=[%d], messageID=[%s], classFullName=[%s]::IllegalArgumentException::%s",
					classLoader.hashCode(), messageID, classFullName, e.getMessage());
			log.warn(errorMessage);
			throw new DynamicClassCallException(errorMessage);
		}

		/*
		 * if (null == valueObj) { String errorMessage = String.format(
		 * "ClassLoader hashCode=[%d], messageID=[%s], classFullName=[%s]::valueObj is null"
		 * , classLoader.hashCode(), messageID, classFullName); log.warn(errorMessage);
		 * new DynamicClassCallException(errorMessage); }
		 * 
		 * if (!(valueObj instanceof MessageCodecIF)) { String errorMessage =
		 * String.format(
		 * "ClassLoader hashCode=[%d], messageID=[%s], classFullName=[%s]::valueObj type[%s] is not  MessageCodecIF"
		 * , classLoader.hashCode(), messageID, classFullName,
		 * valueObj.getClass().getCanonicalName()); log.warn(errorMessage); new
		 * DynamicClassCallException(errorMessage); }
		 */

		clientTask = (AbstractClientTask) valueObj;

		return clientTask;
	}

	@Override
	public MessageCodecIF getClientMessageCodec(ClassLoader classLoader, String messageID)
			throws DynamicClassCallException {
		/*
		 * String classFullName = new
		 * StringBuilder(classLoaderClassPackagePrefixName).append("message.")
		 * .append(messageID).append(".").append(messageID)
		 * .append("ClientCodec").toString();
		 */

		String classFullName = IOPartDynamicClassNameUtil.getClientMessageCodecClassFullName(messageID);

		MessageCodecIF messageCodec = null;

		Object valueObj = null;
		try {
			try {
				valueObj = objectCacheManager.getCachedObject(classLoader, classFullName);
			} catch (Exception e) {
				String errorMessage = String.format("ClassLoader hashCode=[%d], messageID=[%s], classFullName=[%s]::%s",
						classLoader.hashCode(), messageID, classFullName, e.toString());
				log.warn(errorMessage, e);
				throw new DynamicClassCallException(errorMessage);
			}
		} catch (IllegalArgumentException e) {
			String errorMessage = String.format(
					"ClassLoader hashCode=[%d], messageID=[%s], classFullName=[%s]::IllegalArgumentException::%s",
					classLoader.hashCode(), messageID, classFullName, e.getMessage());
			log.warn(errorMessage);
			throw new DynamicClassCallException(errorMessage);
		}

		/*
		 * if (null == valueObj) { String errorMessage = String.format(
		 * "ClassLoader hashCode=[%d], messageID=[%s], classFullName=[%s]::valueObj is null"
		 * , classLoader.hashCode(), messageID, classFullName); log.warn(errorMessage);
		 * new DynamicClassCallException(errorMessage); }
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
	
}
