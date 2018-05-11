package kr.pe.codda.common.classloader;

public interface ServerSystemClassLoaderClassManagerIF {
	public boolean isSystemClassLoader(String classFullName);
	public String getServerMessageCodecClassFullName(String messageID);
}
