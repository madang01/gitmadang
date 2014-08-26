package main;

import java.awt.EventQueue;

import javax.swing.JFrame;

import kr.pe.sinnori.screen.NoConfigFileScreen;


/**
 * <pre>
 * 이 프로그램은 이클립스 플러그인 SWT Designer "WindowBuilder" 를 사용하여 작성되었습니다.
 * 이 프로그램은 서버와 클라이언트 사이에 주고 받는 메시지의 내용이 담긴 정보 파일로 부터 
 * 신놀이 운영에 필요한 소스들(메시지, 인코더, 디코더, 서버 코덱, 클라이언트 코덱)을 
 * 자동 생성해 주는 작성기의 화면 디자인 겸용 스토리 보드입니다. 
 * </pre>
 * @author "Jonghoon Won"
 *
 */
public class SourceFileBuilderDesignMain {
	private JFrame mainFrame;
	private NoConfigFileScreen noConfigFileScreen= null;
	// private final String SINNORI_CONFIG_FILE_VAR_NAME = "sinnori.configurationFile";
	// private final String SINNORI_CONFIG_FILE_CHARSET = "UTF-8";
	// private SequencedProperties configFileProperties = new SequencedProperties();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SourceFileBuilderDesignMain window = new SourceFileBuilderDesignMain();
					window.mainFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public SourceFileBuilderDesignMain() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		mainFrame = new JFrame();
		mainFrame.setTitle("메시지 소스 파일 제작기");
		// mainFrame.setBounds(100, 100, 450, 300);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// String sinnoriConfigurationFileName = System.getProperty(SINNORI_CONFIG_FILE_VAR_NAME);
		
		noConfigFileScreen = new NoConfigFileScreen(mainFrame);
		mainFrame.add(noConfigFileScreen);
		mainFrame.pack();
		noConfigFileScreen.setVisible(true);
	}

}
