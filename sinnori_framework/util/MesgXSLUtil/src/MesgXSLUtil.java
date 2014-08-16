import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.lang.StringEscapeUtils;


public class MesgXSLUtil {
	
	public static void toJava(String fileName) {
		File f = new File(fileName);
		BufferedReader br = null;
		FileReader fr = null;
		
		try {
			fr = new FileReader(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		String lineSeparator = System.getProperty("line.separator");
		lineSeparator = lineSeparator.replaceAll("\r", "\\\\r");
		lineSeparator = lineSeparator.replaceAll("\n", "\\\\n");
		
		FileWriter fw = null;
		BufferedWriter bw  = null;
		
		try {
			br = new BufferedReader(fr);
			
			String lineStr = null;
			
			File tmpfile = File.createTempFile("SinnoriMsgXSL", ".tmp");
			fw = new FileWriter(tmpfile);
			bw = new BufferedWriter(fw);
			
			lineStr = "StringBuilder xslStringBuilder = new StringBuilder();";
			System.out.println(lineStr);
			bw.write(lineStr);
			bw.write(System.getProperty("line.separator"));
			
			try {
				do {
					lineStr = br.readLine();
					
					if (null == lineStr) break;
					
					
					System.out.printf("xslStringBuilder.append(\"%s", StringEscapeUtils.escapeJava(lineStr));
					System.out.printf(lineSeparator);
					System.out.printf("\");");
					
					bw.write("xslStringBuilder.append(\"");
					bw.write(StringEscapeUtils.escapeJava(lineStr));
					bw.write(lineSeparator);
					bw.write("\");");
					bw.write(System.getProperty("line.separator"));
					
					System.out.println();
				} while(null != lineStr);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			
			System.out.printf("메시지 구조를 정의한 XSL 내용이 담긴 임시 파일=[%s]", tmpfile.getAbsolutePath());
			System.out.println();
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
			if (null != fr) {
				try {
					fr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if (null != bw) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != fw) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void toXSL() {
	  StringBuilder xslStringBuilder = new StringBuilder();
		xslStringBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n");
		xslStringBuilder.append("<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n");
		xslStringBuilder.append("\n");
		xslStringBuilder.append("\t<!-- \uD56D\uBAA9 \uADF8\uB8F9 -->\n");
		xslStringBuilder.append("\t<xs:group name=\"itemgroup\">\n");
		xslStringBuilder.append("\t\t<xs:choice>\n");
		xslStringBuilder.append("\t\t\t<!-- \uB2E8\uC77C \uD56D\uBAA9 -->\n");
		xslStringBuilder.append("\t\t\t<xs:element name=\"singleitem\">\n");
		xslStringBuilder.append("\t\t\t\t<xs:complexType>\n");
		xslStringBuilder.append("\t\t\t\t\t<xs:sequence>\n");
		xslStringBuilder.append("\t\t\t\t\t\t<xs:element name=\"desc\" type=\"xs:string\" minOccurs=\"0\" maxOccurs=\"1\" />\n");
		xslStringBuilder.append("\t\t\t\t\t</xs:sequence>\n");
		xslStringBuilder.append("\t\t\t\t\t<!-- \uC774\uB984 -->\n");
		xslStringBuilder.append("\t\t\t\t\t<xs:attribute name=\"name\" use=\"required\">\n");
		xslStringBuilder.append("\t\t\t\t\t\t<xs:simpleType>\n");
		xslStringBuilder.append("\t\t\t\t\t\t\t<xs:restriction base=\"xs:string\">\n");
		xslStringBuilder.append("\t\t\t\t\t\t\t\t<xs:minLength value=\"1\" />\n");
		xslStringBuilder.append("\t\t\t\t\t\t\t</xs:restriction>\n");
		xslStringBuilder.append("\t\t\t\t\t\t</xs:simpleType>\n");
		xslStringBuilder.append("\t\t\t\t\t</xs:attribute>\n");
		xslStringBuilder.append("\n");
		xslStringBuilder.append("\t\t\t\t\t<!-- \uD56D\uBAA9 \uD0C0\uC785, \uD56D\uBAA9 \uD0C0\uC785\uC740 \uD06C\uAC8C 2\uAC00\uC9C0\uB85C \uB098\uB258\uB294\uB370 \uC22B\uC790\uD615\uACFC \uBB38\uC790\uD615\uC774 \uC788\uB2E4. \uC22B\uC790\uD615 \uAC19\uC740 \uACBD\uC6B0 \uC815\uC218\uB9CC \uC9C0\uC6D0\uD558\uBA70 \uBD80\uD638 \n");
		xslStringBuilder.append("\t\t\t\t\t\t\uC788\uC74C\uACFC \uBD80\uD638 \uC5C6\uC74C\uC73C\uB85C \uB098\uB258\uBA70 \uBE44 \uBD80\uD638(= \uBD80\uD638 \uC5C6\uC74C)\uB9CC \uC55E\uC5D0 \uD45C\uC2DC\uD55C\uB2E4. \uB2E8 \uD2B9\uC815 \uC5B8\uC5B4\uC758 \uACBD\uC6B0 \uC608\uB97C \uB4E4\uBA74 \uC790\uBC14\uC758 \uACBD\uC6B0 \uBD80\uD638 \uC5C6\uC74C\uC744 \uC9C0\uC6D0\uD558\uC9C0 \n");
		xslStringBuilder.append("\t\t\t\t\t\t\uC54A\uC73C\uBBC0\uB85C \uC774\uB97C \uC18C\uD504\uD2B8\uC6E8\uC5B4\uB85C \uAD6C\uD604\uD55C\uB2E4. \uC18C\uD504\uD2B8\uC6E8\uC5B4 \uAD6C\uD604\uC5D0\uB294 \uD55C\uACC4\uAC00 \uC788\uB2E4 \uC608\uB97C \uB4E4\uBA74 unsigned long \uAC19\uC740 \uACBD\uC6B0 \uC790\uBC14\uB85C \uAD6C\uD604\uD560\uB824\uACE0 \n");
		xslStringBuilder.append("\t\t\t\t\t\t\uD558\uBA74 \uBD88\uAC00\uB2A5\uC5D0 \uAC00\uAE4C\uC6B4 \uB9E4\uC6B0 \uD798\uB4E0 \uC77C\uC774\uB2E4. \uB530\uB77C\uC11C \uBC18\uB4EF\uC774 \uC2E0\uB180\uC774\uB97C \uAD6C\uD604\uD558\uB294 \uC5B8\uC5B4 \uD2B9\uC131\uC73C\uB85C \uAE30\uC778\uD558\uB294 \uD0C0\uC785 \uC81C\uD55C\uC744 \uC219\uC9C0\uD574\uC57C \uD55C\uB2E4. \uD0C0\uC785 \n");
		xslStringBuilder.append("\t\t\t\t\t\t\uC81C\uD55C\uC744 \uADF9\uBCF5 \uD558\uB294 \uBC29\uBC95\uC73C\uB85C \uBB38\uC790\uC5F4 \uADF8 \uC790\uCCB4\uB85C \uBCF4\uB0B4\uACE0 \uD074\uB77C\uC774\uC5B8\uD2B8 \uD639\uC740 \uBE44\uC9C0\uB2C8\uC2A4 \uCE21\uC5D0\uC11C \uC774\uB97C \uC801\uC808\uD558\uAC8C \uBCC0\uD658\uD558\uC5EC \uC0AC\uC6A9\uD558\uB294\uAC83\uC744 \uCD94\uCC9C\uD55C\uB2E4. \n");
		xslStringBuilder.append("\t\t\t\t\t\t\uC2E4\uC218\uD615 \uB370\uC774\uD130\uC758 \uACBD\uC6B0 \uC774\uB807\uAC8C \uD574\uACB0\uD558\uAE30\uB97C \uBC14\uB780\uB2E4. \uC608\uC81C) unsigned byte, \uBC30\uC5F4\uC740 byte \uB9CC \uC9C0\uC6D0\uD55C\uB2E4. \uC608\uC81C) byte[] \n");
		xslStringBuilder.append("\t\t\t\t\t\t\uC22B\uC790\uD615 \uD0C0\uC785 \uBAA9\uB85D : byte, short, integer, long \uBB38\uC790\uD615 \uD0C0\uC785 \uBAA9\uB85D : string -->\n");
		xslStringBuilder.append("\t\t\t\t\t<xs:attribute name=\"type\" use=\"required\">\n");
		xslStringBuilder.append("\t\t\t\t\t\t<xs:simpleType>\n");
		xslStringBuilder.append("\t\t\t\t\t\t\t<xs:restriction base=\"xs:string\">\n");
		xslStringBuilder.append("\t\t\t\t\t\t\t\t<xs:enumeration value=\"byte\" />\n");
		xslStringBuilder.append("\t\t\t\t\t\t\t\t<xs:enumeration value=\"unsigned byte\" />\n");
		xslStringBuilder.append("\t\t\t\t\t\t\t\t<xs:enumeration value=\"short\" />\n");
		xslStringBuilder.append("\t\t\t\t\t\t\t\t<xs:enumeration value=\"unsigned short\" />\n");
		xslStringBuilder.append("\t\t\t\t\t\t\t\t<xs:enumeration value=\"integer\" />\n");
		xslStringBuilder.append("\t\t\t\t\t\t\t\t<xs:enumeration value=\"unsigned integer\" />\n");
		xslStringBuilder.append("\t\t\t\t\t\t\t\t<xs:enumeration value=\"long\" />\n");
		xslStringBuilder.append("\t\t\t\t\t\t\t\t<xs:enumeration value=\"ub pascal string\" />\n");
		xslStringBuilder.append("\t\t\t\t\t\t\t\t<xs:enumeration value=\"us pascal string\" />\n");
		xslStringBuilder.append("\t\t\t\t\t\t\t\t<xs:enumeration value=\"si pascal string\" />\n");
		xslStringBuilder.append("\t\t\t\t\t\t\t\t<xs:enumeration value=\"fixed length string\" />\n");
		xslStringBuilder.append("\t\t\t\t\t\t\t\t<xs:enumeration value=\"ub variable length byte[]\" />\n");
		xslStringBuilder.append("\t\t\t\t\t\t\t\t<xs:enumeration value=\"us variable length byte[]\" />\n");
		xslStringBuilder.append("\t\t\t\t\t\t\t\t<xs:enumeration value=\"si variable length byte[]\" />\n");
		xslStringBuilder.append("\t\t\t\t\t\t\t\t<xs:enumeration value=\"fixed length byte[]\" />\n");
		xslStringBuilder.append("\t\t\t\t\t\t\t</xs:restriction>\n");
		xslStringBuilder.append("\t\t\t\t\t\t</xs:simpleType>\n");
		xslStringBuilder.append("\t\t\t\t\t</xs:attribute>\n");
		xslStringBuilder.append("\n");
		xslStringBuilder.append("\t\t\t\t\t<!-- \uD0C0\uC785 \uBD80\uAC00 \uC815\uBCF4\uC778 \uD06C\uAE30\uB294 2\uAC00\uC9C0 \uD0C0\uC785\uC5D0\uC11C\uB9CC \uC720\uC6A9\uD558\uB2E4. (1) \uACE0\uC815 \uD06C\uAE30 \uBC14\uC774\uD2B8 \uBC30\uC5F4(fixed length byte[]) \n");
		xslStringBuilder.append("\t\t\t\t\t\t(2) \uACE0\uC815 \uD06C\uAE30 \uBB38\uC790\uC5F4(fixed length string) -->\n");
		xslStringBuilder.append("\t\t\t\t\t<xs:attribute name=\"size\" use=\"optional\">\n");
		xslStringBuilder.append("\t\t\t\t\t\t<xs:simpleType>\n");
		xslStringBuilder.append("\t\t\t\t\t\t\t<xs:restriction base=\"xs:string\">\n");
		xslStringBuilder.append("\t\t\t\t\t\t\t\t<xs:minLength value=\"1\" />\n");
		xslStringBuilder.append("\t\t\t\t\t\t\t</xs:restriction>\n");
		xslStringBuilder.append("\t\t\t\t\t\t</xs:simpleType>\n");
		xslStringBuilder.append("\t\t\t\t\t</xs:attribute>\n");
		xslStringBuilder.append("\n");
		xslStringBuilder.append("\t\t\t\t\t<!-- \uD0C0\uC785 \uBD80\uAC00 \uC815\uBCF4\uC778\uC778 \uBB38\uC790\uC14B\uC740 \uC624\uC9C1 \uACE0\uC815 \uD06C\uAE30 \uBB38\uC790\uC5F4(fixed length string)\uC5D0\uC11C\uB9CC \uC720\uD6A8\uD558\uB2E4. -->\n");
		xslStringBuilder.append("\t\t\t\t\t<xs:attribute name=\"charset\" use=\"optional\">\n");
		xslStringBuilder.append("\t\t\t\t\t\t<xs:simpleType>\n");
		xslStringBuilder.append("\t\t\t\t\t\t\t<xs:restriction base=\"xs:string\">\n");
		xslStringBuilder.append("\t\t\t\t\t\t\t\t<xs:minLength value=\"1\" />\n");
		xslStringBuilder.append("\t\t\t\t\t\t\t</xs:restriction>\n");
		xslStringBuilder.append("\t\t\t\t\t\t</xs:simpleType>\n");
		xslStringBuilder.append("\t\t\t\t\t</xs:attribute>\n");
		xslStringBuilder.append("\n");
		xslStringBuilder.append("\t\t\t\t\t<!-- \uAC12 -->\n");
		xslStringBuilder.append("\t\t\t\t\t<xs:attribute name=\"defaultValue\" use=\"optional\">\n");
		xslStringBuilder.append("\t\t\t\t\t\t<xs:simpleType>\n");
		xslStringBuilder.append("\t\t\t\t\t\t\t<xs:restriction base=\"xs:string\">\n");
		xslStringBuilder.append("\t\t\t\t\t\t\t\t<xs:minLength value=\"1\" />\n");
		xslStringBuilder.append("\t\t\t\t\t\t\t</xs:restriction>\n");
		xslStringBuilder.append("\t\t\t\t\t\t</xs:simpleType>\n");
		xslStringBuilder.append("\t\t\t\t\t</xs:attribute>\n");
		xslStringBuilder.append("\t\t\t\t</xs:complexType>\n");
		xslStringBuilder.append("\t\t\t</xs:element>\n");
		xslStringBuilder.append("\n");
		xslStringBuilder.append("\t\t\t<!-- \uBC30\uC5F4 -->\n");
		xslStringBuilder.append("\t\t\t<xs:element name=\"array\">\n");
		xslStringBuilder.append("\t\t\t\t<xs:complexType>\n");
		xslStringBuilder.append("\t\t\t\t\t<!-- \uD56D\uBAA9 \uADF8\uB8F9 -->\n");
		xslStringBuilder.append("\t\t\t\t\t<xs:sequence>\n");
		xslStringBuilder.append("\t\t\t\t\t\t<xs:group minOccurs=\"0\" maxOccurs=\"unbounded\" ref=\"itemgroup\" />\n");
		xslStringBuilder.append("\t\t\t\t\t</xs:sequence>\n");
		xslStringBuilder.append("\n");
		xslStringBuilder.append("\t\t\t\t\t<!-- \uC774\uB984 -->\n");
		xslStringBuilder.append("\t\t\t\t\t<xs:attribute name=\"name\" use=\"required\">\n");
		xslStringBuilder.append("\t\t\t\t\t\t<xs:simpleType>\n");
		xslStringBuilder.append("\t\t\t\t\t\t\t<xs:restriction base=\"xs:string\">\n");
		xslStringBuilder.append("\t\t\t\t\t\t\t\t<xs:minLength value=\"1\" />\n");
		xslStringBuilder.append("\t\t\t\t\t\t\t</xs:restriction>\n");
		xslStringBuilder.append("\t\t\t\t\t\t</xs:simpleType>\n");
		xslStringBuilder.append("\t\t\t\t\t</xs:attribute>\n");
		xslStringBuilder.append("\t\t\t\t\t<!-- \uBC30\uC5F4\uC758 \uBC18\uBCF5 \uD69F\uC218 \uC9C0\uC815 \uBC29\uC2DD(cnttype)\uC740 2\uAC00\uC9C0\uAC00 \uC788\uB2E4. (1) \uC9C1\uC811(direct) : \uACE0\uC815 \uD06C\uAE30 \uC9C0\uC815\uBC29\uC2DD\uC73C\uB85C \n");
		xslStringBuilder.append("\t\t\t\t\t\t\uBC30\uC5F4 \uBC18\uBCF5 \uD69F\uC218\uC5D0\uB294 \uBC30\uC5F4\uC758 \uBC18\uBCF5 \uD69F\uC218 \uAC12\uC774 \uC800\uC7A5\uB418\uBA70, (2) \uCC38\uC870(reference) : \uAC00\uBCC0 \uD06C\uAE30 \uC9C0\uC815\uBC29\uC2DD\uC73C\uB85C \uBC30\uC5F4 \uBC18\uBCF5 \uD69F\uC218\uB294 \n");
		xslStringBuilder.append("\t\t\t\t\t\t\uCC38\uC870\uD558\uB294 \uD56D\uBAA9\uC758 \uAC12\uC774\uB2E4. -->\n");
		xslStringBuilder.append("\t\t\t\t\t<xs:attribute name=\"cnttype\" use=\"required\">\n");
		xslStringBuilder.append("\t\t\t\t\t\t<xs:simpleType>\n");
		xslStringBuilder.append("\t\t\t\t\t\t\t<xs:restriction base=\"xs:string\">\n");
		xslStringBuilder.append("\t\t\t\t\t\t\t\t<xs:enumeration value=\"reference\" />\n");
		xslStringBuilder.append("\t\t\t\t\t\t\t\t<xs:enumeration value=\"direct\" />\n");
		xslStringBuilder.append("\t\t\t\t\t\t\t</xs:restriction>\n");
		xslStringBuilder.append("\t\t\t\t\t\t</xs:simpleType>\n");
		xslStringBuilder.append("\t\t\t\t\t</xs:attribute>\n");
		xslStringBuilder.append("\t\t\t\t\t<!-- \uBC30\uC5F4\uC758 \uBC18\uBCF5 \uD69F\uC218(cntvalue) \"\uBC30\uC5F4\uC758 \uBC18\uBCF5 \uD69F\uC218 \uC9C0\uC815 \uBC29\uC2DD\"\uC774 \uC9C1\uC811(direct) \uC774\uBA74 \uBC30\uC5F4 \uBC18\uBCF5 \uD69F\uC218\uB97C \n");
		xslStringBuilder.append("\t\t\t\t\t\t\uBC18\uD658\uD558\uBA70, \uCC38\uC870(reference)\uC77C \uACBD\uC6B0\uC5D0\uB294 \uCC38\uC870\uD558\uB294 \uD56D\uBAA9 \uC774\uB984\uC744 \uBC18\uD658\uD55C\uB2E4. \uCC38\uC870\uD558\uB294 \uD56D\uBAA9\uC740 \uC22B\uC790\uD615\uC73C\uB85C \uBC30\uC5F4\uACFC \uAC19\uC740 \uB2E8\uACC4\uB85C \uBC18\uB4EF\uC774 \n");
		xslStringBuilder.append("\t\t\t\t\t\t\uC55E\uC5D0 \uB098\uC640\uC57C \uD55C\uB2E4. \uC774\uB807\uAC8C \uC55E\uC5D0 \uB098\uC640\uC57C \uD558\uB294 \uC774\uC720\uB294 \uBC30\uC5F4 \uC815\uBCF4\uB97C \uC77D\uC5B4\uC640\uC11C \uBC30\uC5F4 \uC815\uBCF4\uB97C \uC800\uC7A5\uD558\uAE30 \uC804\uC5D0 \uCC38\uC870 \uBCC0\uC218\uAC00 \uAC19\uC740 \uB808\uBCA8\uC5D0\uC11C \uC874\uC7AC\uD558\uBA70 \n");
		xslStringBuilder.append("\t\t\t\t\t\t\uC22B\uC790\uD615\uC778\uC9C0 \uD310\uB2E8\uC744 \uD558\uAE30 \uC704\uD574\uC11C\uC774\uB2E4. \uBA54\uC2DC\uC9C0 \uC815\uBCF4 \uD30C\uC77C\uC744 \uC21C\uCC28\uC801\uC73C\uB85C \uC77D\uAE30 \uB54C\uBB38\uC5D0 \uBC30\uC5F4 \uB4A4\uC5D0 \uC704\uCE58\uD558\uBA74 \uC54C \uC218\uAC00 \uC5C6\uB2E4. -->\n");
		xslStringBuilder.append("\t\t\t\t\t<xs:attribute name=\"cntvalue\" use=\"required\">\n");
		xslStringBuilder.append("\t\t\t\t\t\t<xs:simpleType>\n");
		xslStringBuilder.append("\t\t\t\t\t\t\t<xs:restriction base=\"xs:string\">\n");
		xslStringBuilder.append("\t\t\t\t\t\t\t\t<xs:minLength value=\"1\" />\n");
		xslStringBuilder.append("\t\t\t\t\t\t\t</xs:restriction>\n");
		xslStringBuilder.append("\t\t\t\t\t\t</xs:simpleType>\n");
		xslStringBuilder.append("\t\t\t\t\t</xs:attribute>\n");
		xslStringBuilder.append("\t\t\t\t</xs:complexType>\n");
		xslStringBuilder.append("\t\t\t</xs:element>\n");
		xslStringBuilder.append("\t\t</xs:choice>\n");
		xslStringBuilder.append("\t</xs:group>\n");
		xslStringBuilder.append("\n");
		xslStringBuilder.append("\t<!-- \uBA54\uC2DC\uC9C0 -->\n");
		xslStringBuilder.append("\t<xs:element name=\"sinnori_message\">\n");
		xslStringBuilder.append("\t\t<xs:complexType>\n");
		xslStringBuilder.append("\t\t\t<xs:sequence>\n");
		xslStringBuilder.append("\t\t\t\t<!-- \uBA54\uC2DC\uC9C0 \uC2DD\uBCC4\uC790 -->\n");
		xslStringBuilder.append("\t\t\t\t<xs:element name=\"messageID\" minOccurs=\"1\" maxOccurs=\"1\">\n");
		xslStringBuilder.append("\t\t\t\t\t<xs:simpleType>\n");
		xslStringBuilder.append("\t\t\t\t\t\t<xs:restriction base=\"xs:string\">\n");
		xslStringBuilder.append("\t\t\t\t\t\t\t<xs:pattern value=\"[a-zA-Z][a-zA-Z1-9]+\" />\n");
		xslStringBuilder.append("\t\t\t\t\t\t</xs:restriction>\n");
		xslStringBuilder.append("\t\t\t\t\t</xs:simpleType>\n");
		xslStringBuilder.append("\t\t\t\t</xs:element>\n");
		xslStringBuilder.append("\t\t\t\t<!-- \uD56D\uBAA9 \uADF8\uB8F9 -->\n");
		xslStringBuilder.append("\t\t\t\t<xs:element name=\"desc\" type=\"xs:string\" minOccurs=\"0\"\n");
		xslStringBuilder.append("\t\t\t\t\tmaxOccurs=\"1\" />\n");
		xslStringBuilder.append("\t\t\t\t<xs:group minOccurs=\"0\" maxOccurs=\"unbounded\" ref=\"itemgroup\" />\n");
		xslStringBuilder.append("\t\t\t</xs:sequence>\n");
		xslStringBuilder.append("\t\t</xs:complexType>\n");
		xslStringBuilder.append("\t</xs:element>\n");
		xslStringBuilder.append("</xs:schema>\n");
		
		

		
		FileWriter fw = null;
		BufferedWriter bw  = null;
		try {
			File f = File.createTempFile("SinnoriMsgXSL", ".tmp");
			
			fw = new FileWriter(f);
			bw = new BufferedWriter(fw);
			
			bw.write(xslStringBuilder.toString());
			
			
			System.out.printf("메시지 구조를 정의한 XSL 내용이 담긴 임시 파일=[%s]", f.getAbsolutePath());
			System.out.println();
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != bw) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != fw) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (1 != args.length) {
			System.out.printf("bad argment, command format) java MesgXSLUtil <message struect define xsl file path>, ex) java MesgXSLUtil ./sinnori_message.xsd");
			System.out.println();
			System.exit(1);
		}
		
		// String fileName = "/home/madang01/sinnori_framework/config/sinnori_message.xsd";
		toJava(args[0]);
		
		toXSL();
	}
}
