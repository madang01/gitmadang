package kr.pe.sinnori.server.classloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.util.JarFileFilter;

public class JarUtil {
	
	public static Hashtable<String, JarClassInfo> getJarClassInfoHash(String jarLibrayPathName) throws FileNotFoundException {
		if (null == jarLibrayPathName) {
			throw new IllegalArgumentException("parameter jarLibrayPathName is null");
		}
		
		String[] jarFileShortNameList = getJarFileList(jarLibrayPathName);
		
		if (null == jarFileShortNameList) {
			String errorMessage = String.format("fail to get a jar file short name list in jarLibrayPathName[%s]", jarLibrayPathName);
			Logger.getLogger("JarUtil").log(Level.WARNING, errorMessage);
			throw new RuntimeException(errorMessage);
		}
		
		Hashtable<String, JarClassInfo> jarClassInfoHash = new Hashtable<String, JarClassInfo>();
		
		for (String jarFileShortName : jarFileShortNameList) {
			String jarFilePathName = jarLibrayPathName+File.separator+jarFileShortName;
			JarFile jarFile = null;
			try {
				jarFile = new JarFile(jarFilePathName);
				Enumeration<JarEntry> jarEntries = jarFile.entries();
				while (jarEntries.hasMoreElements()) {
					JarEntry jarEntry = jarEntries.nextElement();				
					if (jarEntry.isDirectory()) continue;
					long fileSize = jarEntry.getSize();
					String fileName = jarEntry.getName();
					if (fileSize > CommonStaticFinalVars.MAX_FILE_SIZE_IN_JAR_FILE) {
						Logger.getLogger("JarTestMain").log(Level.WARNING, 
								String.format("In JarFile[%s], the size[%d] of class file[%s] in jar file[%s] is larger than max size=[%d]", 
										jarFileShortName, fileSize, fileName, jarFileShortName, CommonStaticFinalVars.MAX_FILE_SIZE_IN_JAR_FILE));
						continue;
					}
					
					if (fileName.endsWith(".class")) {
						/** class 파일만 */					
						int inx = fileName.lastIndexOf(".class");
						
						String className = fileName.substring(0, inx).replace('/', '.');
						
						/*Logger.getLogger("JarUtil").log(Level.INFO, 
								String.format("in JarFIle[%s], fileName=[%s], fileSize=[%d], className=[%s]", 
										jarFileShortName, fileName, fileSize, className));*/
						
						int classFileBufferSize = (int)fileSize;
						byte[] classFileBuffer = new byte[classFileBufferSize];
						int offset = 0;
						int len = classFileBufferSize;
						
						InputStream is = null;
						try {							
							is = jarFile.getInputStream(jarEntry);	
							
							int firstAvailable = is.available();
							if (firstAvailable != classFileBufferSize) {
								Logger.getLogger("JarUtil").log(Level.WARNING, 
	String.format("In JarFile[%s], the size[%d] of the fileName=[%s] is not same to the total number[%d] of bytes read into the buffer", 
			jarFileShortName, fileSize, fileName, firstAvailable));
								continue;
							}
							
							do {
								int readBytes = is.read(classFileBuffer, offset, len);
								offset += readBytes;
								len -= readBytes;
								if (0 > len) {
									Logger.getLogger("JarUtil").log(Level.WARNING, 
										String.format("In JarFile[%s], the size[%d] of the fileName=[%s] is not same to the total number[%d] of bytes read into the buffer", 
												jarFileShortName, fileSize, fileName, readBytes));
								}
							} while (is.available() > 0);
							
						} finally  {
							if (null != is) {
								try {
									is.close();
								} catch(Exception e) {
									e.printStackTrace();
								}
							}
						}
						JarClassInfo jarClassInfo = new JarClassInfo(jarFileShortName, className, classFileBuffer);
						jarClassInfoHash.put(className, jarClassInfo);
					}
				}
			} catch (IOException e) {
				String errorMessage = String.format("fail to make a hash map in jarFilePathName[%s]", jarFilePathName);
				Logger.getLogger("JarUtil").log(Level.WARNING, errorMessage, e);
				continue;
			} catch (Exception e) {
				String errorMessage = String.format("unknown error in jarFilePathName[%s]", jarFilePathName);
				Logger.getLogger("JarUtil").log(Level.WARNING, errorMessage, e);
				continue;
			} finally {
				if (null != jarFile) {
					try {
						jarFile.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return jarClassInfoHash;
	}
	
	

	private static String[] getJarFileList(String jarLibrayPathName) throws FileNotFoundException {		
		File jarLibrayPath = new File(jarLibrayPathName);
		if (!jarLibrayPath.exists()) {
			String errorMessage = String.format("jarLibrayPathName[%s] not exist", jarLibrayPathName);
			throw new FileNotFoundException(errorMessage);
		}
		
		if (!jarLibrayPath.isDirectory()) {
			String errorMessage = String.format("jarLibrayPathName[%s] is not a directory", jarLibrayPathName);
			throw new FileNotFoundException(errorMessage);
		}
		
		if (!jarLibrayPath.canRead()) {
			String errorMessage = String.format("jarLibrayPathName[%s] can't read", jarLibrayPathName);
			throw new FileNotFoundException(errorMessage);
		}
		
		
		return jarLibrayPath.list(new JarFileFilter());
	}
}
