package kr.pe.codda.common.classloader;

import java.util.HashSet;

public class ExcludedDynamicClassManager implements ExcludedDynamicClassManagerIF {

	private HashSet<String> serverExcludedDynamicClassFullNameSet = new HashSet<String>();
	
	public ExcludedDynamicClassManager() {
		
		String[] messageIDListOfServerExcludedDynamicClass = { "SelfExnRes" };

		for (String messageIDOfServerExcludedDynamicClass : messageIDListOfServerExcludedDynamicClass) {
			serverExcludedDynamicClassFullNameSet
					.add(IOPartDynamicClassNameUtil.getMessageClassFullName(messageIDOfServerExcludedDynamicClass));
			serverExcludedDynamicClassFullNameSet.add(
					IOPartDynamicClassNameUtil.getClientMessageCodecClassFullName(messageIDOfServerExcludedDynamicClass));
			serverExcludedDynamicClassFullNameSet
					.add(IOPartDynamicClassNameUtil.getMessageDecoderClassFullName(messageIDOfServerExcludedDynamicClass));
			serverExcludedDynamicClassFullNameSet
					.add(IOPartDynamicClassNameUtil.getMessageEncoderClassFullName(messageIDOfServerExcludedDynamicClass));
			serverExcludedDynamicClassFullNameSet.add(
					IOPartDynamicClassNameUtil.getServerMessageCodecClassFullName(messageIDOfServerExcludedDynamicClass));
		}

		String[] taskSystemClassLoaderTargetMessageIDList = { "Empty" };

		for (String systemClassLoaderTargetMessageID : taskSystemClassLoaderTargetMessageIDList) {
			serverExcludedDynamicClassFullNameSet
					.add(IOPartDynamicClassNameUtil.getMessageClassFullName(systemClassLoaderTargetMessageID));
			serverExcludedDynamicClassFullNameSet.add(
					IOPartDynamicClassNameUtil.getClientMessageCodecClassFullName(systemClassLoaderTargetMessageID));
			serverExcludedDynamicClassFullNameSet
					.add(IOPartDynamicClassNameUtil.getMessageDecoderClassFullName(systemClassLoaderTargetMessageID));
			serverExcludedDynamicClassFullNameSet
					.add(IOPartDynamicClassNameUtil.getMessageEncoderClassFullName(systemClassLoaderTargetMessageID));
			serverExcludedDynamicClassFullNameSet.add(
					IOPartDynamicClassNameUtil.getServerMessageCodecClassFullName(systemClassLoaderTargetMessageID));

			serverExcludedDynamicClassFullNameSet
					.add(IOPartDynamicClassNameUtil.getClientTaskClassFullName(systemClassLoaderTargetMessageID));

			serverExcludedDynamicClassFullNameSet
					.add(IOPartDynamicClassNameUtil.getServerTaskClassFullName(systemClassLoaderTargetMessageID));
		}
	}

	@Override
	public boolean isExcludedDynamicClass(String classFullName) {
		return serverExcludedDynamicClassFullNameSet.contains(classFullName);
	}

	
	public String getMessageCodecClassFullName(String messageID) {
		return IOPartDynamicClassNameUtil.getServerMessageCodecClassFullName(messageID);
	}

}
