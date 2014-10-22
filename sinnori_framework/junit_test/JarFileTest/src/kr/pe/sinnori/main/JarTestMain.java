package kr.pe.sinnori.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import kr.pe.sinnori.server.classloader.JarClassInfo;
import kr.pe.sinnori.server.classloader.JarUtil;

public class JarTestMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String jarLibrayPathName = "/home/madang01/gitsinnori/sinnori_framework/core_build/lib/ex";
		String jarFileShortName = "mybatis-3.3.0-SNAPSHOT.jar";
		String jarFilePathName = jarLibrayPathName+File.separator+jarFileShortName;
		// long MAX_FILE_SIZE_IN_JAR_FILE = 1024 * 1024;
		JarFile jarFile = null;
		try {
			jarFile = new JarFile(jarFilePathName);
			Enumeration<JarEntry> jarEntries = jarFile.entries();
			while (jarEntries.hasMoreElements()) {
				JarEntry jarEntry = jarEntries.nextElement();				
				if (jarEntry.isDirectory()) continue;
				long fileSize = jarEntry.getSize();
				String fileName = jarEntry.getName();
				/*if (fileSize > MAX_FILE_SIZE_IN_JAR_FILE) {
					Logger.getLogger("JarTestMain").log(Level.WARNING, 
							String.format("the size[%d] of class file[%s] in jar file[%s] is larger than max size=[%d]", 
									fileSize, fileName, jarSortFileName, MAX_FILE_SIZE_IN_JAR_FILE));
					return;
				}*/
				
				if (fileName.endsWith(".class")) {
					/** class 파일만 */
					// Logger.getLogger("JarTestMain").log(Level.INFO, String.format("fileName=[%s], fileSize=[%d]", fileName, fileSize));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != jarFile) {
				try {
					jarFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		JarUtil jarUtil = new JarUtil();
		try {
			String[] fileNameList = jarUtil.getJarFileList(jarLibrayPathName);
			for (String fileName : fileNameList) {
				Logger.getLogger("JarTestMain").log(Level.INFO, String.format("fileName=[%s]", fileName));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			HashMap<String, JarClassInfo> jarClassInfoHash = jarUtil.getJarClassInfoHash(jarLibrayPathName);
			// Logger.getLogger("JarTestMain").log(Level.INFO, jarClassInfoHash.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}		
		

	}

}
