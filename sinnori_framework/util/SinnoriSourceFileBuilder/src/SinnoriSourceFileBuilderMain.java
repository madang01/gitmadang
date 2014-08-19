import java.io.File;

import message.MessageInfoSAXParser;
import source_file_builder.DecoderSourceFileBuilder;

public class SinnoriSourceFileBuilderMain {
	private String messageInfoBasePath = "D:\\gitsinnori\\sinnori_framework\\project\\sample_fileupdown\\impl\\message\\info";
	// private Charset charsetOfProject = Charset.defaultCharset();
	/*private String dynamicClassBasePackageName = "kr.pe.sinnori.impl.message.";
	private String dynamicClassSourceBasePath = "D:\\gitsinnori\\sinnori_framework\\project\\sample_fileupdown\\server_build\\src";
	private String lineSeparator = System.getProperty("line.separator");*/


	// sample_fileupdown.common.dynamic_class_base_package_name.value

	public message.MessageInfo getMessageInfo(String messageID) {
		File xmlFile = new File(new StringBuilder(messageInfoBasePath)
				.append(File.separator).append(messageID).append(".xml")
				.toString());
		MessageInfoSAXParser messageInfoSAXParser = new MessageInfoSAXParser(xmlFile);
		message.MessageInfo messageInfo = messageInfoSAXParser.parse();
		return messageInfo;
	}

	/*public void toFile(String messageID, String suffix) {
		String classFullName = new StringBuilder(dynamicClassBasePackageName).append(messageID).append(".").append(messageID).append(suffix).toString();
		String sourceFileName = new StringBuilder(dynamicClassSourceBasePath).append(File.separator).append(classFullName.replace(".", File.separator)).append(".java").toString();
		
		File f = new File(sourceFileName);
		BufferedReader br = null;
		FileReader fr = null;

		try {
			fr = new FileReader(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}		
		
		StringBuilder fileBuilder = new StringBuilder();

		try {
			br = new BufferedReader(fr);

			String lineStr = null;
			do {
				lineStr = br.readLine();
				fileBuilder.append(StringEscapeUtils.escapeJava(lineStr));
				fileBuilder.append(lineSeparator);
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
			if (null != fr) {
				try {
					fr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		System.out.println(fileBuilder.toString());
	}*/

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SinnoriSourceFileBuilderMain sinnoriMessageUtilMain = new SinnoriSourceFileBuilderMain();

		String messageID = "SelfExn";
		
		message.MessageInfo messageInfo = sinnoriMessageUtilMain
				.getMessageInfo(messageID);

		// System.out.println(messageInfo.toString());
		
		
		/**
		 * ClinetCodec
		 * ServerCodec
		 * Encoder
		 * Decoder
		 * ServerTask
		 */
		// System.out.println(new MessageSourceFileBuilder().toString(messageID, "Jonghoon Won", messageInfo));
		// System.out.println(new EncoderSourceFileBuilder().toString(messageID, "Jonghoon Won", messageInfo));
		System.out.println(new DecoderSourceFileBuilder().toString(messageID, "Jonghoon Won", messageInfo));
		// System.out.println(new ClientCodecSourceFileBuilder().toString(CommonType.CONNECTION_DIRECTION_MODE.FROM_ALL_TO_ALL, messageID, "Jonghoon Won"));
		// System.out.println(new ServerCodecSourceFileBuilder().toString(CommonType.CONNECTION_DIRECTION_MODE.FROM_ALL_TO_ALL, messageID, "Jonghoon Won"));
		
	}

}
