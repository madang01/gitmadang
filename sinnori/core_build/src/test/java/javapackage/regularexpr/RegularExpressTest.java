package javapackage.regularexpr;

import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.sinnori.common.AbstractJunitTest;
import kr.pe.sinnori.common.buildsystem.BuildSystemPathSupporter;

public class RegularExpressTest extends AbstractJunitTest {
	@Test
	public void testRegularExpressMybatisConfigDTDFilePathString() {
		
		if (!File.separator.equals("\\")) {
			fail("this funciton depends on Windows OS");
		}
		
		InternalLogger log = InternalLoggerFactory.getInstance(RegularExpressTest.class);
		
		String expectedFileContents="<!DOCTYPE configuration SYSTEM \"D:\\gitsinnori\\sinnori\\resouces\\mybatis\\mybatis-3-config.dtd\">";
		
		String fileTypeResourceFileContents="<!DOCTYPE configuration SYSTEM \"D:\\gi\\resouces\\mybatis\\mybatis-3-config.dtd\">";
		// String mapperDoctypeString="<!DOCTYPE mapper SYSTEM \"D:\\gitsinnori\\sinnori\\resouces\\mybatis\\mybatis-3-mapper.dtd\">";
		
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		
		String mybatisConfigDTDFilePathString = BuildSystemPathSupporter.getMybatisConfigDTDFilePathString(sinnoriInstalledPathString);
		
		log.info("before mybatisConfigDTDFilePathString=[{}]", mybatisConfigDTDFilePathString);
		
		if (File.separator.equals("\\")) {
			mybatisConfigDTDFilePathString = mybatisConfigDTDFilePathString.replaceAll("\\\\", "\\\\\\\\");
		}
		
		log.info("after mybatisConfigDTDFilePathString=[{}]", mybatisConfigDTDFilePathString);
		
		/*String mybatisMapperDTDFilePathString = BuildSystemPathSupporter.getMybatisMapperDTDFilePathString(sinnoriInstalledPathString);
		
		if (File.separator.equals("\\")) {
			mybatisMapperDTDFilePathString = mybatisMapperDTDFilePathString.replaceAll("\\\\", "\\\\\\\\");
		}*/
		
		String newFileContentsAppliedSinnoriInstalledPath = fileTypeResourceFileContents.replaceAll("SYSTEM\\p{Blank}+\".+\"", 
				new StringBuilder("SYSTEM \"")
						.append(mybatisConfigDTDFilePathString).append("\"").toString());
		
		log.info("fileTypeResourceFileContents=[{}]", fileTypeResourceFileContents);
		log.info("newFileContentsAppliedSinnoriInstalledPath=[{}]", newFileContentsAppliedSinnoriInstalledPath);
		
		if (!expectedFileContents.equals(newFileContentsAppliedSinnoriInstalledPath)) {
			fail("the expected file contents is not same to the new file contents applied Sinnori installed path");
		}
	}
	
	@Test
	public void testRegularExpressMybatisMapperDTDFilePathString() {
		if (!File.separator.equals("\\")) {
			fail("this funciton depends on Windows OS");
		}
		
		InternalLogger log = InternalLoggerFactory.getInstance(RegularExpressTest.class);		
	
		String expectedFileContents = "<!DOCTYPE mapper SYSTEM \"D:\\gitsinnori\\sinnori\\resouces\\mybatis\\mybatis-3-mapper.dtd\">";
		
		String fileTypeResourceFileContents = "<!DOCTYPE mapper SYSTEM \"D:\\ouces\\mybatis\\mybatis-3-mapper.dtd\">";
		
		String sinnoriInstalledPathString = "D:\\gitsinnori\\sinnori";
		
		
		String mybatisMapperDTDFilePathString = BuildSystemPathSupporter.getMybatisMapperDTDFilePathString(sinnoriInstalledPathString);
		
		if (File.separator.equals("\\")) {
			mybatisMapperDTDFilePathString = mybatisMapperDTDFilePathString.replaceAll("\\\\", "\\\\\\\\");
		}
		
		String newFileContentsAppliedSinnoriInstalledPath = fileTypeResourceFileContents.replaceAll("SYSTEM\\p{Blank}+\".+\"", 
				new StringBuilder("SYSTEM \"")
						.append(mybatisMapperDTDFilePathString).append("\"").toString());
		
		log.info("fileTypeResourceFileContents=[{}]", fileTypeResourceFileContents);
		log.info("newFileContentsAppliedSinnoriInstalledPath=[{}]", newFileContentsAppliedSinnoriInstalledPath);
		
		if (!expectedFileContents.equals(newFileContentsAppliedSinnoriInstalledPath)) {
			fail("the expected file contents is not same to the new file contents applied Sinnori installed path");
		}
	}
	@Test
	public void testRegularExpressDosRootPath() {
		boolean expecedResult = true;
		String pathString = "c:\\";
		boolean result = pathString.matches("^[a-zA-Z]:\\\\$");
		
		if (result != expecedResult) {
			fail(String.format("For the pathString[%s], the expected result[%s] is not same to the result[%s]", pathString, expecedResult, result));
		}
		
		expecedResult = true;
		pathString = "C:\\"; 
		result = pathString.matches("^[a-zA-Z]:\\\\$");
		
		if (result != expecedResult) {
			fail(String.format("For the pathString[%s], the expected result[%s] is not same to the result[%s]", pathString, expecedResult, result));
		}
		
		
		expecedResult = false;
		pathString = "1234";
		result = pathString.matches("^[a-zA-Z]:\\\\$");
		
		if (result != expecedResult) {
			fail(String.format("For the pathString[%s], the expected result[%s] is not same to the result[%s]", pathString, expecedResult, result));
		}
		
		expecedResult = false;
		pathString = "C:\\temp";
		result = pathString.matches("^[a-zA-Z]:\\\\$");
		
		if (result != expecedResult) {
			fail(String.format("For the pathString[%s], the expected result[%s] is not same to the result[%s]", pathString, expecedResult, result));
		}
		
		expecedResult = false;
		pathString = "C:\\\r\nc:\\";
		result = pathString.matches("^[a-zA-Z]:\\\\$");
		
		if (result != expecedResult) {
			fail(String.format("For the pathString[%s], the expected result[%s] is not same to the result[%s]", pathString, expecedResult, result));
		}
	}
	
}
