package main;
import java.awt.EventQueue;

import javax.swing.JFrame;

import kr.pe.sinnori.screen.NoConfigFileScreen;


public class SinnoriSourceFileBuilderMain {
	private JFrame mainFrame;
	private NoConfigFileScreen noConfigFileScreen= null;
	
	// private String messageInfoBasePath = "D:\\gitsinnori\\sinnori_framework\\project\\sample_fileupdown\\impl\\message\\info";
	

	/*public kr.pe.sinnori.message.MessageInfo getMessageInfo(String messageID) {
		File xmlFile = new File(new StringBuilder(messageInfoBasePath)
				.append(File.separator).append(messageID).append(".xml")
				.toString());
		MessageInfoSAXParser messageInfoSAXParser = new MessageInfoSAXParser(xmlFile);
		kr.pe.sinnori.message.MessageInfo messageInfo = messageInfoSAXParser.parse();
		return messageInfo;
	}*/

	/**
	 * Create the application.
	 */
	public SinnoriSourceFileBuilderMain() {
		initialize();
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		mainFrame = new JFrame();
		mainFrame.setTitle("메시지 소스 파일 제작기");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		
		noConfigFileScreen = new NoConfigFileScreen(mainFrame);
		mainFrame.add(noConfigFileScreen);
		mainFrame.pack();
		noConfigFileScreen.setVisible(true);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// SinnoriSourceFileBuilderMain sinnoriMessageUtilMain = new SinnoriSourceFileBuilderMain();

		// String messageID = "SelfExn";
		
		/*kr.pe.sinnori.message.MessageInfo messageInfo = sinnoriMessageUtilMain.getMessageInfo(messageID);

		// System.out.println(messageInfo.toString());
		
		
		*//**
		 * ClinetCodec
		 * ServerCodec
		 * Encoder
		 * Decoder
		 * ServerTask
		 *//*
		// System.out.println(new MessageSourceFileBuilder().toString(messageID, "Jonghoon Won", messageInfo));
		// System.out.println(new EncoderSourceFileBuilder().toString(messageID, "Jonghoon Won", messageInfo));
		System.out.println(new DecoderSourceFileBuilder().toString(messageID, "Jonghoon Won", messageInfo));
		// System.out.println(new ClientCodecSourceFileBuilder().toString(CommonType.CONNECTION_DIRECTION_MODE.FROM_ALL_TO_ALL, messageID, "Jonghoon Won"));
		// System.out.println(new ServerCodecSourceFileBuilder().toString(CommonType.CONNECTION_DIRECTION_MODE.FROM_ALL_TO_ALL, messageID, "Jonghoon Won"));
*/		
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SinnoriSourceFileBuilderMain window = new SinnoriSourceFileBuilderMain();
					window.mainFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
