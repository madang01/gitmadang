package kr.pe.codda.common.classloader;

import kr.pe.codda.common.etc.CommonStaticFinalVars;

public abstract class IOPartDynamicClassNameUtil {
	
	
	public static String getMessageClassShortName(String messageID) {
		return messageID;
	}
	
	public static String getMessageClassFullName(String messageID) {
		String classFullName = new StringBuilder(CommonStaticFinalVars.BASE_MESSAGE_CLASS_FULL_NAME)
				.append(".")
				.append(messageID)
				.append(".")
				.append(getMessageClassShortName(messageID))
				.toString();
		return classFullName;
	}
	
	public static String getClientMessageCodecClassShortName(String messageID) {
		String classFullName = new StringBuilder(messageID)				
				.append("ClientCodec").toString();
		return classFullName;
	}
	
	public static String getClientMessageCodecClassFullName(String messageID) {
		String classFullName = new StringBuilder(CommonStaticFinalVars.BASE_MESSAGE_CLASS_FULL_NAME)
				.append(".")
				.append(messageID)
				.append(".")
				.append(getClientMessageCodecClassShortName(messageID))
				.toString();
		return classFullName;
	}
	
	public static String getServerMessageCodecClassShortName(String messageID) {
		String classFullName = new StringBuilder(messageID)
				.append("ServerCodec").toString();
		return classFullName;
	}
	
	public static String getServerMessageCodecClassFullName(String messageID) {
		String classFullName = new StringBuilder(CommonStaticFinalVars.BASE_MESSAGE_CLASS_FULL_NAME)
				.append(".")
				.append(messageID)
				.append(".")
				.append(getServerMessageCodecClassShortName(messageID))
				.toString();
		return classFullName;
	}
	
	public static String getServerTaskClassShortName(String messageID) {
		String classFullName = new StringBuilder(messageID)
				.append("ServerTask").toString();
		return classFullName;
	}
	
	public static String getServerTaskClassFullName(String messageID) {
		String classFullName = new StringBuilder(CommonStaticFinalVars.BASE_SERVER_TASK_CLASS_FULL_NAME)
				.append(".")
				.append(getServerTaskClassShortName(messageID)).toString();
		return classFullName;
	}
	
	public static String getClientTaskClassShortName(String messageID) {
		String classFullName = new StringBuilder(messageID)
				.append("ClientTask").toString();
		return classFullName;
	}
	
	public static String getClientTaskClassFullName(String messageID) {
		String classFullName = new StringBuilder(CommonStaticFinalVars.BASE_CLIENT_TASK_CLASS_FULL_NAME)
				.append(".")
				.append(getClientTaskClassShortName(messageID)).toString();
		return classFullName;
	}
	
	public static String getMessageDecoderClassShortName(String messageID) {
		String classFullName = new StringBuilder(messageID)
				.append("Decoder").toString();
		return classFullName;
	}
	
	public static String getMessageDecoderClassFullName(String messageID) {
		String classFullName = new StringBuilder(CommonStaticFinalVars.BASE_MESSAGE_CLASS_FULL_NAME)
				.append(".")
				.append(messageID)
				.append(".")
				.append(getMessageDecoderClassShortName(messageID)).toString();
		return classFullName;
	}
	
	public static String getMessageEncoderClassShortName(String messageID) {
		String classFullName = new StringBuilder(messageID)
				.append("Encoder").toString();
		return classFullName;
	}
	
	public static String getMessageEncoderClassFullName(String messageID) {
		String classFullName = new StringBuilder(CommonStaticFinalVars.BASE_MESSAGE_CLASS_FULL_NAME)
				.append(".")
				.append(messageID)
				.append(".")
				.append(getMessageEncoderClassShortName(messageID)).toString();
		return classFullName;
	}
}
