package kr.pe.codda.common.classloader;

import java.util.HashSet;

public class ServerSystemClassLoaderClassManager implements ServerSystemClassLoaderClassManagerIF{
	
	
	private HashSet<String> systemClassLoaderTargetClassFullNameSet = new HashSet<String>();
	
	public ServerSystemClassLoaderClassManager() {
		
		String[] noTaskSystemClassLoaderTargetMessageIDList = { "SelfExnRes"};

		for (String systemClassLoaderTargetMessageID : noTaskSystemClassLoaderTargetMessageIDList) {
			systemClassLoaderTargetClassFullNameSet
					.add(IOPartDynamicClassNameUtil.getMessageClassFullName(systemClassLoaderTargetMessageID));
			systemClassLoaderTargetClassFullNameSet.add(
					IOPartDynamicClassNameUtil.getClientMessageCodecClassFullName(systemClassLoaderTargetMessageID));
			systemClassLoaderTargetClassFullNameSet
					.add(IOPartDynamicClassNameUtil.getMessageDecoderClassFullName(systemClassLoaderTargetMessageID));
			systemClassLoaderTargetClassFullNameSet
					.add(IOPartDynamicClassNameUtil.getMessageEncoderClassFullName(systemClassLoaderTargetMessageID));
			systemClassLoaderTargetClassFullNameSet.add(
					IOPartDynamicClassNameUtil.getServerMessageCodecClassFullName(systemClassLoaderTargetMessageID));
		}

		String[] taskSystemClassLoaderTargetMessageIDList = { "Empty" };

		for (String systemClassLoaderTargetMessageID : taskSystemClassLoaderTargetMessageIDList) {
			systemClassLoaderTargetClassFullNameSet
					.add(IOPartDynamicClassNameUtil.getMessageClassFullName(systemClassLoaderTargetMessageID));
			systemClassLoaderTargetClassFullNameSet.add(
					IOPartDynamicClassNameUtil.getClientMessageCodecClassFullName(systemClassLoaderTargetMessageID));
			systemClassLoaderTargetClassFullNameSet
					.add(IOPartDynamicClassNameUtil.getMessageDecoderClassFullName(systemClassLoaderTargetMessageID));
			systemClassLoaderTargetClassFullNameSet
					.add(IOPartDynamicClassNameUtil.getMessageEncoderClassFullName(systemClassLoaderTargetMessageID));
			systemClassLoaderTargetClassFullNameSet.add(
					IOPartDynamicClassNameUtil.getServerMessageCodecClassFullName(systemClassLoaderTargetMessageID));

			systemClassLoaderTargetClassFullNameSet
					.add(IOPartDynamicClassNameUtil.getClientTaskClassFullName(systemClassLoaderTargetMessageID));

			systemClassLoaderTargetClassFullNameSet
					.add(IOPartDynamicClassNameUtil.getServerTaskClassFullName(systemClassLoaderTargetMessageID));
		}
	}

	@Override
	public boolean isSystemClassLoader(String classFullName) {
		return systemClassLoaderTargetClassFullNameSet.contains(classFullName);
	}

	
	
	public String getServerMessageCodecClassFullName(String messageID) {
		return IOPartDynamicClassNameUtil.getServerMessageCodecClassFullName(messageID);
	}

}
