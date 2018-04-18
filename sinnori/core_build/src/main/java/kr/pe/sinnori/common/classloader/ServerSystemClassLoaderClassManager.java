package kr.pe.sinnori.common.classloader;

import java.util.HashSet;

public class ServerSystemClassLoaderClassManager implements ServerSystemClassLoaderClassManagerIF{
	private IOPartDynamicClassNameUtil ioPartDynamicClassNameUtil = null;
	
	private HashSet<String> systemClassLoaderTargetClassFullNameSet = new HashSet<String>();
	
	public ServerSystemClassLoaderClassManager(IOPartDynamicClassNameUtil ioPartDynamicClassNameUtil) {
		this.ioPartDynamicClassNameUtil = ioPartDynamicClassNameUtil;
		
		String[] noTaskSystemClassLoaderTargetMessageIDList = { "SelfExnRes" };

		for (String systemClassLoaderTargetMessageID : noTaskSystemClassLoaderTargetMessageIDList) {
			systemClassLoaderTargetClassFullNameSet
					.add(ioPartDynamicClassNameUtil.getMessageClassFullName(systemClassLoaderTargetMessageID));
			systemClassLoaderTargetClassFullNameSet.add(
					ioPartDynamicClassNameUtil.getClientMessageCodecClassFullName(systemClassLoaderTargetMessageID));
			systemClassLoaderTargetClassFullNameSet
					.add(ioPartDynamicClassNameUtil.getMessageDecoderClassFullName(systemClassLoaderTargetMessageID));
			systemClassLoaderTargetClassFullNameSet
					.add(ioPartDynamicClassNameUtil.getMessageEncoderClassFullName(systemClassLoaderTargetMessageID));
			systemClassLoaderTargetClassFullNameSet.add(
					ioPartDynamicClassNameUtil.getServerMessageCodecClassFullName(systemClassLoaderTargetMessageID));
		}

		String[] taskSystemClassLoaderTargetMessageIDList = { "Empty" };

		for (String systemClassLoaderTargetMessageID : taskSystemClassLoaderTargetMessageIDList) {
			systemClassLoaderTargetClassFullNameSet
					.add(ioPartDynamicClassNameUtil.getMessageClassFullName(systemClassLoaderTargetMessageID));
			systemClassLoaderTargetClassFullNameSet.add(
					ioPartDynamicClassNameUtil.getClientMessageCodecClassFullName(systemClassLoaderTargetMessageID));
			systemClassLoaderTargetClassFullNameSet
					.add(ioPartDynamicClassNameUtil.getMessageDecoderClassFullName(systemClassLoaderTargetMessageID));
			systemClassLoaderTargetClassFullNameSet
					.add(ioPartDynamicClassNameUtil.getMessageEncoderClassFullName(systemClassLoaderTargetMessageID));
			systemClassLoaderTargetClassFullNameSet.add(
					ioPartDynamicClassNameUtil.getServerMessageCodecClassFullName(systemClassLoaderTargetMessageID));

			systemClassLoaderTargetClassFullNameSet
					.add(ioPartDynamicClassNameUtil.getClientTaskClassFullName(systemClassLoaderTargetMessageID));

			systemClassLoaderTargetClassFullNameSet
					.add(ioPartDynamicClassNameUtil.getServerTaskClassFullName(systemClassLoaderTargetMessageID));
		}
	}

	@Override
	public boolean isSystemClassLoader(String classFullName) {
		return systemClassLoaderTargetClassFullNameSet.contains(classFullName);
	}

	
	
	public String getFirstPrefixDynamicClassFullName() {
		return ioPartDynamicClassNameUtil.getFirstPrefixDynamicClassFullName();
	}
	
	public String getServerMessageCodecClassFullName(String messageID) {
		return ioPartDynamicClassNameUtil.getServerMessageCodecClassFullName(messageID);
	}

}
