import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.commons.lang.StringEscapeUtils;

public class SinnoriTxtFileToJavaStringMain {
	private String lineSeparator = System.getProperty("line.separator");

	public String getFileName(String messageID, String suffix) {
		String dynamicClassBasePackageName = "kr.pe.sinnori.impl.message.";
		String dynamicClassSourceBasePath = "D:\\gitsinnori\\sinnori_framework\\project\\sample_fileupdown\\server_build\\src";
		
		String classFullName = new StringBuilder(dynamicClassBasePackageName).append(messageID).append(".").append(messageID).append(suffix).toString();
		String sourceFileName = new StringBuilder(dynamicClassSourceBasePath).append(File.separator).append(classFullName.replace(".", File.separator)).append(".java").toString();

		return sourceFileName;
	}
	
	
	public String toJavaString(String sourceFileName, String charsetName) {
		File f = new File(sourceFileName);
		BufferedReader br = null;
		InputStream is = null;
		InputStreamReader isr = null;

		try {
			is = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
		try {
			isr = new InputStreamReader(is, charsetName);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.exit(1);
		}

		StringBuilder fileBuilder = new StringBuilder();
		
		fileBuilder.append("StringBuilder stringBuilder = new StringBuilder();");

		// int lineNum = 0;
		try {
			br = new BufferedReader(isr);

			String lineStr = null;
			do {
				lineStr = br.readLine();
				
				String escapeString = StringEscapeUtils.escapeJava(lineStr);
				
				if (null != escapeString) {
					fileBuilder.append(lineSeparator);
					fileBuilder.append("stringBuilder.append(\"");
					fileBuilder.append(escapeString);
					fileBuilder.append("\");");
					fileBuilder.append(lineSeparator);
					fileBuilder.append("stringBuilder.append(");
					fileBuilder.append("System.getProperty(\"line.separator\")");
					fileBuilder.append(");");
				}
				// lineNum++;
			} while (null != lineStr);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != br) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != isr) {
				try {
					isr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		fileBuilder.append(lineSeparator);
		fileBuilder.append("stringBuilder.toString();");
		
		return fileBuilder.toString();
	}
	
	public String getResultString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("#Project[sample_fileupdown]'s Config File");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("#Mon Aug 18 00:07:34 KST 2014");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("servlet_jsp.jdf_error_message_page.desc=JDF framework\uC5D0\uC11C \uC5D0\uB7EC \uBC1C\uC0DD\uC2DC \uC5D0\uB7EC \uB0B4\uC6A9\uC744 \uBCF4\uC5EC\uC8FC\uB294 \uC0AC\uC6A9\uC790 \uCE5C\uD654\uC801\uC778 \uD654\uBA74\uC744 \uC804\uB2F4\uD560 jsp, \uBBF8 \uC9C0\uC815\uC2DC /errorMessagePage.jsp.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("servlet_jsp.jdf_error_message_page.value=/errorMessagePage.jsp");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("servlet_jsp.jdf_login_page.desc=\uB85C\uADF8\uC778 \uC804\uC6A9 \uCC98\uB9AC jsp, \uBBF8 \uC9C0\uC815\uC2DC /login.jsp.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("servlet_jsp.jdf_login_page.value=/login.jsp");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("servlet_jsp.jdf_servlet_trace.desc=JDF framework\uC5D0\uC11C \uC11C\uBE14\uB9BF \uACBD\uACFC\uC2DC\uAC04 \uCD94\uC801 \uC5EC\uBD80, \uBBF8 \uC9C0\uC815\uC2DC true.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("servlet_jsp.jdf_servlet_trace.set=true, false");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("servlet_jsp.jdf_servlet_trace.value=true");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("servlet_jsp.web_layout_control_page.desc=\uC2E0\uB180\uC774 \uC6F9\uC758 \uB808\uC774\uC544\uC6C3 \uCEE8\uD2B8\uB864\uB7EC jsp, /PageJump.jsp.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("servlet_jsp.web_layout_control_page.value=/PageJump.jsp");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("sessionkey.rsa_keypair_source.desc=\uC138\uC158\uD0A4\uC5D0 \uC0AC\uC6A9\uB418\uB294 \uACF5\uAC1C\uD0A4 \uD0A4\uC30D \uC0DD\uC131 \uBC29\uBC95(\\=\uC6D0\uCC9C)\uB85C\uC368 2\uAC00\uC9C0\uAC00 \uC788\uB2E4. \uBBF8\uC9C0\uC815\uC2DC API, (1) API \\: \uC790\uCCB4 \uC554\uD638 lib \uC774\uC6A9\uD558\uC5EC RSA \uD0A4\uC30D \uC0DD\uC131, (2) File \\: \uC678\uBD80 \uD30C\uC77C\uB97C \uC77D\uC5B4\uC640\uC11C RSA  \uD0A4\uC30D\uC744 \uC0DD\uC131");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("sessionkey.rsa_keypair_source.set=API, File");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("sessionkey.rsa_keypair_source.value=API");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("sessionkey.rsa_keypair_path.desc=\uC138\uC158\uD0A4\uC5D0 \uC0AC\uC6A9\uB418\uB294 \uACF5\uAC1C\uD0A4 \uD0A4\uC30D \uD30C\uC77C \uACBD\uB85C,  \uC138\uC158\uD0A4\uC5D0 \uC0AC\uC6A9\uB418\uB294 \uACF5\uAC1C\uD0A4 \uD0A4\uC30D \uC0DD\uC131 \uBC29\uBC95\uC774 File \uC77C \uACBD\uC6B0\uC5D0\uB294 \uD544\uC218 \uD56D\uBAA9.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("sessionkey.rsa_keypair_path.value=./");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("sessionkey.rsa_keysize.desc=\uC138\uC158\uD0A4\uC5D0 \uC0AC\uC6A9\uD558\uB294 \uACF5\uAC1C\uD0A4 \uD06C\uAE30, \uB2E8\uC704 byte. \uB514\uD3F4\uD2B8 1024");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("sessionkey.rsa_keysize.value=1024");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("sessionkey.symmetric_key_algorithm.desc=\uC138\uC158\uD0A4\uC5D0 \uC0AC\uC6A9\uB418\uB294 \uB300\uCE6D\uD0A4 \uC54C\uACE0\uB9AC\uC998, \uBBF8 \uC9C0\uC815\uC2DC AES.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("sessionkey.symmetric_key_algorithm.set=ASE, DESede, DES");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("sessionkey.symmetric_key_algorithm.value=AES");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("sessionkey.symmetric_key_size.desc=\uC138\uC158\uD0A4\uC5D0 \uC0AC\uC6A9\uB418\uB294 \uB300\uCE6D\uD0A4 \uD06C\uAE30, \uB2E8\uC704 byte, \uC554\uD638 \uAC15\uB3C4 \uB54C\uBB38\uC5D0 \uCD5C\uC18C 8 byte \uC774\uC0C1 \uC694\uAD6C, \uBBF8 \uC9C0\uC815\uC2DC 16.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("sessionkey.symmetric_key_size.value=16");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("sessionkey.iv_size.desc=\uC138\uC158\uD0A4\uC5D0 \uC0AC\uC6A9\uB418\uB294 \uB300\uCE6D\uD0A4\uC640 \uAC19\uC774 \uC0AC\uC6A9\uB418\uB294 IV \uD06C\uAE30, \uB2E8\uC704 byte, \uCD5C\uC18C 8 byte \uC774\uC0C1 \uAC16\uB3C4\uB85D \uD568. \uBBF8 \uC9C0\uC815\uC2DC 16.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("sessionkey.iv_size.value=16");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("sessionkey.private_key.encoding.desc=\uAC1C\uC778\uD0A4\uB97C \uC778\uCF54\uB529 \uBC29\uBC95, \uBBF8 \uC9C0\uC815\uC2DC NONE. \uC6F9\uC758 \uACBD\uC6B0 \uC774\uC9C4\uB370\uC774\uD130\uB294 \uD3FC \uC804\uC1A1\uC774 \uBD88\uAC00\uD558\uBBC0\uB85C base64 \uC778\uCF54\uB529\uD558\uC5EC \uC804\uC1A1\uD55C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("sessionkey.private_key.encoding.set=NONE, BASE64");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("sessionkey.private_key.encoding.value=BASE64");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("sinnori_worker.running_mode.desc=\uC2E0\uB180\uC774 \uC791\uC5C5\uC790 \uB3D9\uC791 \uBAA8\uB4DC");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("sinnori_worker.running_mode.set=client, server, all");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("sinnori_worker.running_mode.value=client");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("sinnori_worker.client.executor.prefix.value=impl.executor.client.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("sinnori_worker.client.executor.suffix.value=CExtor");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("common.updownfile.local_source_file_resource_cnt.desc=\uB85C\uCEEC \uC6D0\uBCF8 \uD30C\uC77C \uC790\uC6D0 \uAC2F\uC218, \uBBF8 \uC9C0\uC815\uC2DC 10, \uCD5C\uC18C 5");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("common.updownfile.local_source_file_resource_cnt.value=10");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("common.updownfile.local_target_file_resource_cnt.desc=\uB85C\uCEEC \uBAA9\uC801\uC9C0 \uD30C\uC77C \uC790\uC6D0 \uAC2F\uC218, \uBBF8 \uC9C0\uC815\uC2DC 10, \uCD5C\uC18C 5");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("common.updownfile.local_target_file_resource_cnt.value=10");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("common.updownfile.file_block_max_size.desc=\uD30C\uC77C \uC1A1\uC218\uC2E0 \uD30C\uC77C \uBE14\uB77D \uCD5C\uB300 \uD06C\uAE30, \uCD5C\uC18C\uAC12 1024, 1024\uC758 \uBC30\uC218, \uAE30\uBCF8\uAC12 1 Mbytes \\= 1024*1024(\\=1048576), \uB2E8\uC704 byte.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("common.updownfile.file_block_max_size.value=1048576");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("common.cached_object.max_size.desc=\uC2F1\uAE00\uD134 \uD074\uB798\uC2A4 \uAC1D\uCCB4 \uCE90\uC26C \uAD00\uB9AC\uC790(LoaderAndName2ObjectManager) \uC5D0\uC11C \uCE90\uC26C\uB85C \uAD00\uB9AC\uD560 \uAC1D\uCCB4\uC758 \uCD5C\uB300 \uAC2F\uC218. \uC8FC\uB85C \uCE90\uC26C\uB418\uB294 \uB300\uC0C1 \uAC1D\uCCB4\uB294 xxxServerCodec, xxxClientCodec \uC774\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("common.cached_object.max_size.value=1");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("common.cached_object.max_update_seq_interval.desc=\uC2DC\uAC04 \uAC1C\uB150\uC758 \uAC1D\uCCB4 \uC0DD\uC131 \uC21C\uC11C\uB97C \uAC31\uC2E0\uD558\uB294 \uCD5C\uC18C \uAC04\uACA9, \uB2E8\uC704 ms.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("common.cached_object.max_update_seq_interval.value=5000");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("common.projectlist.desc=\uD504\uB85C\uC81D\uD2B8\uC640 \uD504\uB85C\uC81D\uD2B8 \uAD6C\uBD84\uC740 \uACF5\uBC31 \uC5C6\uC774 \uCF64\uB9C8\uB85C \uD55C\uB2E4.");
		stringBuilder.append(System.getProperty("line.separator"));
		stringBuilder.append("common.projectlist.value=");
		stringBuilder.append(System.getProperty("line.separator"));
		return stringBuilder.toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SinnoriTxtFileToJavaStringMain sinnoriTxtFileToJavaStringMain = new SinnoriTxtFileToJavaStringMain();

		String retStr = null;
		
		retStr = sinnoriTxtFileToJavaStringMain.toJavaString("D:\\gitsinnori\\sinnori_framework\\util\\SinnoriTxtFileToJavaString\\sinnori.properties", "UTF-8");
		
		// retStr = sinnoriTxtFileToJavaStringMain.getResultString();
		
		
		System.out.println(retStr);
	}

}
