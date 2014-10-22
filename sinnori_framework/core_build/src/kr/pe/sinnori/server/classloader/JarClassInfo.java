package kr.pe.sinnori.server.classloader;

public class JarClassInfo {
	private String jarFileShortName;
	private String className;
	private byte[] classFileBuffer;	
	
	public JarClassInfo(String jarFileShortName, String className, byte[] classFileBuffer) {
		this.jarFileShortName = jarFileShortName;
		this.className = className;
		this.classFileBuffer = classFileBuffer;
	}
	
	public String getJarFileShortName() {
		return jarFileShortName;
	}
	public String getClassName() {
		return className;
	}
	public byte[] getClassFileBuffer() {
		return classFileBuffer;
	}	
}
