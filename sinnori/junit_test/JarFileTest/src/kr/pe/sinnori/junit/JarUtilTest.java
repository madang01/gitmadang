package kr.pe.sinnori.junit;

import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.logging.Logger;

import kr.pe.sinnori.server.classloader.JarClassInfo;
import kr.pe.sinnori.server.classloader.JarUtil;

import org.junit.Test;

public class JarUtilTest {

	@Test
	public void testCurrentDirectory() {
		JarUtil jarUtil = new JarUtil();
		HashMap<String, JarClassInfo> jarClassInfoHash = null;
		try {
			jarClassInfoHash = jarUtil.getJarClassInfoHash(".");		
			
			if (null == jarClassInfoHash) {
				fail("jarClassInfoHash is null");
			}
			
			Logger.getLogger("JarUtilTest").info("jarClassInfoHash size="+jarClassInfoHash.size());
			
		} catch (FileNotFoundException e) {
			fail("현재 작업 경로 입력했음에도 경로 못찾겠다는 에러 발생");
		}		
	}
	
	@Test
	public void testLibOfAPP_INF() {
		JarUtil jarUtil = new JarUtil();
		HashMap<String, JarClassInfo> jarClassInfoHash = null;
		String jarClassPathName = "/home/madang01/gitsinnori/sinnori_framework/core_build/APP-INF/lib";
		try {
			jarClassInfoHash = jarUtil.getJarClassInfoHash(jarClassPathName);		
			
			if (null == jarClassInfoHash) {
				fail("jarClassInfoHash is null");
			}
			
			Logger.getLogger("JarUtilTest").info("jarClassInfoHash size="+jarClassInfoHash.size());
			
		} catch (FileNotFoundException e) {
			fail("not found jarClassPathName="+jarClassPathName);
		}		
	}
}
