package kr.pe.sinnori.common.updownfile;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class FileUpDownNormalTest {

	@Test
	public void test_copy_sourceFileSize_lessThan_fileblock() {
		File sourfile = null;
		try {
			sourfile = File.createTempFile("", "");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("fail to get a source temp file");
		}
		
		// FileUtils.contentEquals(file1, file2);
		try {
			FileUtils.writeStringToFile(sourfile, "ab", "UTF-8", false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("fail to create the source temp file");
		}
	}
}
