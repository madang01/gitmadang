package kr.pe.sinnori.server.classloader;

public class JarClassEntryContents {
	private String ownerJarFilePathString;
	private String classFullName;
	private byte[] classFileContents;	
	
	public JarClassEntryContents(String ownerJarFilePathString, String classFullName, byte[] classFileContents) {
		this.ownerJarFilePathString = ownerJarFilePathString;
		this.classFullName = classFullName;
		this.classFileContents = classFileContents;
	}
	
	public String getOwnerJarFilePathString() {
		return ownerJarFilePathString;
	}
	public String getClassFullName() {
		return classFullName;
	}
	public byte[] getClassFileContents() {
		return classFileContents;
	}	
}
