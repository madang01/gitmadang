package kr.pe.sinnori.common.classloader;

public class IOPartDynamicClassNameUtil {
	private String firstPrefixDynamicClassFullName = null;
	private final String messageSecondPrefix = "message.";
	private final String serverTaskSecondPrefix = "task.server.";
	private final String clientTaskSecondPrefix = "task.client.";
	
	public IOPartDynamicClassNameUtil(String firstPrefixDynamicClassFullName) {
		if (null == firstPrefixDynamicClassFullName) {
			throw new IllegalArgumentException("the parameter classLoaderClassPackagePrefixName is null");
		}
		
		this.firstPrefixDynamicClassFullName = firstPrefixDynamicClassFullName;	
	}
	
	public String getMessageClassShortName(String messageID) {
		return messageID;
	}
	public String getMessageClassFullName(String messageID) {
		String classFullName = new StringBuilder(firstPrefixDynamicClassFullName)
				.append(messageSecondPrefix)
				.append(messageID)
				.append(".")
				.append(getMessageClassShortName(messageID))
				.toString();
		return classFullName;
	}
	
	public String getClientMessageCodecClassShortName(String messageID) {
		String classFullName = new StringBuilder(messageID)				
				.append("ClientCodec").toString();
		return classFullName;
	}
	
	public String getClientMessageCodecClassFullName(String messageID) {
		String classFullName = new StringBuilder(firstPrefixDynamicClassFullName)
				.append(messageSecondPrefix)
				.append(messageID)
				.append(".")
				.append(getClientMessageCodecClassShortName(messageID))
				.toString();
		return classFullName;
	}
	
	public String getServerMessageCodecClassShortName(String messageID) {
		String classFullName = new StringBuilder(messageID)
				.append("ServerCodec").toString();
		return classFullName;
	}
	
	public String getServerMessageCodecClassFullName(String messageID) {
		String classFullName = new StringBuilder(firstPrefixDynamicClassFullName)
				.append(messageSecondPrefix)
				.append(messageID)
				.append(".")
				.append(getServerMessageCodecClassShortName(messageID))
				.toString();
		return classFullName;
	}
	
	public String getServerTaskClassShortName(String messageID) {
		String classFullName = new StringBuilder(messageID)
				.append("ServerTask").toString();
		return classFullName;
	}
	
	public String getServerTaskClassFullName(String messageID) {
		String classFullName = new StringBuilder(firstPrefixDynamicClassFullName)
				.append(serverTaskSecondPrefix)
				.append(getServerTaskClassShortName(messageID)).toString();
		return classFullName;
	}
	
	public String getClientTaskClassShortName(String messageID) {
		String classFullName = new StringBuilder(messageID)
				.append("ClientTask").toString();
		return classFullName;
	}
	
	public String getClientTaskClassFullName(String messageID) {
		String classFullName = new StringBuilder(firstPrefixDynamicClassFullName)
				.append(clientTaskSecondPrefix)
				.append(getClientTaskClassShortName(messageID)).toString();
		return classFullName;
	}
	
	public String getMessageDecoderClassShortName(String messageID) {
		String classFullName = new StringBuilder(messageID)
				.append("Decoder").toString();
		return classFullName;
	}
	
	public String getMessageDecoderClassFullName(String messageID) {
		String classFullName = new StringBuilder(firstPrefixDynamicClassFullName)
				.append(messageSecondPrefix)
				.append(messageID)
				.append(".")
				.append(getMessageDecoderClassShortName(messageID)).toString();
		return classFullName;
	}
	
	public String getMessageEncoderClassShortName(String messageID) {
		String classFullName = new StringBuilder(messageID)
				.append("Encoder").toString();
		return classFullName;
	}
	
	public String getMessageEncoderClassFullName(String messageID) {
		String classFullName = new StringBuilder(firstPrefixDynamicClassFullName)
				.append(messageSecondPrefix)
				.append(messageID)
				.append(".")
				.append(getMessageEncoderClassShortName(messageID)).toString();
		return classFullName;
	}

	public String getFirstPrefixDynamicClassFullName() {
		return firstPrefixDynamicClassFullName;
	}
}
