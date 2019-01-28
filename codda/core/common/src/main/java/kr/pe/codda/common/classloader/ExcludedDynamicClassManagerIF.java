package kr.pe.codda.common.classloader;

public interface ExcludedDynamicClassManagerIF {
	public boolean isExcludedDynamicClass(String classFullName);
	// public String getMessageCodecClassFullName(String messageID);
}
