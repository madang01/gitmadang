package kr.pe.sinnori.server.classloader;

public class IOPartDynamicClassNameUtil {
	private String classLoaderClassPackageFirstPrefixName = null;
	private final String messageSecondPrefix = "message.";
	private final String serverTaskSecondPrefix = "task.";
	
	public IOPartDynamicClassNameUtil(String classLoaderClassPackagePrefixName) {
		this.classLoaderClassPackageFirstPrefixName = classLoaderClassPackagePrefixName;	
	}
	
	public String getClientMessageCodecClassShortName(String messageID) {
		String classFullName = new StringBuilder(messageID)				
				.append("ClientCodec").toString();
		return classFullName;
	}
	
	public String getClientMessageCodecClassFullName(String messageID) {
		String classFullName = new StringBuilder(classLoaderClassPackageFirstPrefixName)
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
		String classFullName = new StringBuilder(classLoaderClassPackageFirstPrefixName)
				.append(messageSecondPrefix)
				.append(messageID)
				.append(".")
				.append(getServerMessageCodecClassShortName(messageID))
				.toString();
		return classFullName;
	}
	
	public String getServerTaskClassShortName(String messageID) {
		String classFullName = new StringBuilder(messageID)
				.append("ServerCodec").toString();
		return classFullName;
	}
	
	public String getServerTaskClassFullName(String messageID) {
		String classFullName = new StringBuilder(classLoaderClassPackageFirstPrefixName)
				.append(serverTaskSecondPrefix)
				.append(messageID)
				.append(".")
				.append(getServerTaskClassShortName(messageID)).toString();
		return classFullName;
	}
	
	
	public String getMessageDecoderClassShortName(String messageID) {
		String classFullName = new StringBuilder(messageID)
				.append("Decoder").toString();
		return classFullName;
	}
	
	public String getMessageDecoderClassFullName(String messageID) {
		String classFullName = new StringBuilder(classLoaderClassPackageFirstPrefixName)
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
		String classFullName = new StringBuilder(classLoaderClassPackageFirstPrefixName)
				.append(messageSecondPrefix)
				.append(messageID)
				.append(".")
				.append(getMessageEncoderClassShortName(messageID)).toString();
		return classFullName;
	}
}
