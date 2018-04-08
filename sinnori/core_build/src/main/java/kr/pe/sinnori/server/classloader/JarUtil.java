package kr.pe.sinnori.server.classloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.util.JarFileFilter;

public class JarUtil {
	
	public static ConcurrentHashMap<String, JarClassEntryContents> getJarClassEntryContensHash(String jarLibrayPathString) throws FileNotFoundException {
		if (null == jarLibrayPathString) {
			throw new IllegalArgumentException("parameter jarLibrayPathName is null");
		}
		
		// 		Jar extension file
		File[] jarExtensionFileList = getJarExtensionFileList(jarLibrayPathString);
		
		if (null == jarExtensionFileList) {
			String errorMessage = String.format("fail to get a jar file short name list in jarLibrayPathName[%s]", jarLibrayPathString);
			Logger.getLogger("JarUtil").log(Level.WARNING, errorMessage);
			throw new RuntimeException(errorMessage);
		}
		
		ConcurrentHashMap<String, JarClassEntryContents> jarClassEntryContentsHash = new ConcurrentHashMap<String, JarClassEntryContents>();
		
		for (File jarExtensionFile : jarExtensionFileList) {
			JarFile jarFile = null;
			try {
				jarFile = new JarFile(jarExtensionFile);
			} catch (IOException e1) {
				String errorMessage = String.format("fail to create JarFile.class instance using the jar extension file[%s]",						
						 jarExtensionFile.getAbsolutePath());
				
				Logger.getLogger("JarTestMain").log(Level.WARNING, errorMessage);
				continue;
			}
			try {
				
				Enumeration<JarEntry> jarEntries = jarFile.entries();
				while (jarEntries.hasMoreElements()) {
					JarEntry jarEntry = jarEntries.nextElement();				
					
					if (jarEntry.isDirectory()) continue;
					
					long jarEntrySize = jarEntry.getSize();
					String jarEntryName = jarEntry.getName();
					if (jarEntrySize > CommonStaticFinalVars.MAX_FILE_SIZE_IN_JAR_FILE) {
						String errorMessage = String.format("the size[%d] of class file[%s] in jar file[%s] is larger than max size=[%d]", 
								 jarEntrySize, jarEntryName, 
								 jarExtensionFile.getAbsolutePath(), CommonStaticFinalVars.MAX_FILE_SIZE_IN_JAR_FILE);
						
						Logger.getLogger("JarTestMain").log(Level.WARNING, errorMessage);
						continue;
					}
					
					if (jarEntryName.endsWith(".class")) {
						/** class 파일만 */					
						int inx = jarEntryName.lastIndexOf(".class");
						
						String classFullName = jarEntryName.substring(0, inx).replace('/', '.');
						
						/*Logger.getLogger("JarUtil").log(Level.INFO, 
								String.format("in JarFIle[%s], fileName=[%s], fileSize=[%d], className=[%s]", 
										jarFileShortName, fileName, fileSize, className));*/
						
						int classFileBufferSize = (int)jarEntrySize;
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
			jarExtensionFile.getAbsolutePath(), jarEntrySize, jarEntryName, firstAvailable));
								continue;
							}
							
							do {
								int readBytes = is.read(classFileBuffer, offset, len);
								offset += readBytes;
								len -= readBytes;
								if (0 > len) {
									Logger.getLogger("JarUtil").log(Level.WARNING, 
										String.format("In JarFile[%s], the size[%d] of the fileName=[%s] is not same to the total number[%d] of bytes read into the buffer", 
												jarExtensionFile.getAbsolutePath(), jarEntrySize, jarEntryName, readBytes));
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
						JarClassEntryContents jarClassEntryContents = new JarClassEntryContents(jarExtensionFile.getAbsolutePath(), classFullName, classFileBuffer);
						jarClassEntryContentsHash.put(classFullName, jarClassEntryContents);
					}
				}
			} catch (IOException e) {
				String errorMessage = String.format("fail to make a hash map in jarFilePathName[%s]", jarExtensionFile.getAbsolutePath());
				Logger.getLogger("JarUtil").log(Level.WARNING, errorMessage, e);
				continue;
			} catch (Exception e) {
				String errorMessage = String.format("unknown error in jarFilePathName[%s]", jarExtensionFile.getAbsolutePath());
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
		
		return jarClassEntryContentsHash;
	}
	
	

	private static File[] getJarExtensionFileList(String jarLibrayPathString) throws FileNotFoundException {		
		File jarLibrayPath = new File(jarLibrayPathString);
		if (!jarLibrayPath.exists()) {
			String errorMessage = String.format("jarLibrayPathName[%s] not exist", jarLibrayPathString);
			throw new FileNotFoundException(errorMessage);
		}
		
		if (!jarLibrayPath.isDirectory()) {
			String errorMessage = String.format("jarLibrayPathName[%s] is not a directory", jarLibrayPathString);
			throw new FileNotFoundException(errorMessage);
		}
		
		if (!jarLibrayPath.canRead()) {
			String errorMessage = String.format("jarLibrayPathName[%s] can't read", jarLibrayPathString);
			throw new FileNotFoundException(errorMessage);
		}
		
		
		
		return jarLibrayPath.listFiles(new JarFileFilter());
	}
}
