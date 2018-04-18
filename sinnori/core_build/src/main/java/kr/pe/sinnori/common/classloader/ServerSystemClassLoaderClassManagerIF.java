package kr.pe.sinnori.common.classloader;

public interface ServerSystemClassLoaderClassManagerIF {
	public boolean isSystemClassLoader(String classFullName);
	public String getFirstPrefixDynamicClassFullName();
	public String getServerMessageCodecClassFullName(String messageID);
}
