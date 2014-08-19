package main;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import lib.MainControllerIF;
import lib.SequencedProperties;
import screen.NoConfigFileScreen;

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
public class SourceFileBuilderDesignMain implements MainControllerIF {
	private JFrame mainFrame;
	private NoConfigFileScreen noConfigFileScreen= null;
	private final String SINNORI_CONFIG_FILE_VAR_NAME = "sinnori.configurationFile";
	private final String SINNORI_CONFIG_FILE_CHARSET = "UTF-8";
	private SequencedProperties configFileProperties = new SequencedProperties();

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
		
		String sinnoriConfigurationFileName = System.getProperty(SINNORI_CONFIG_FILE_VAR_NAME);
		if (null == sinnoriConfigurationFileName) {
			noConfigFileScreen = new NoConfigFileScreen(mainFrame, this);
			mainFrame.add(noConfigFileScreen);
			mainFrame.pack();
			noConfigFileScreen.setVisible(true);
		} else {
			if (setSinnoriProperteies(sinnoriConfigurationFileName)) {
				System.exit(1);
			}
		}
	}
	
	
	public boolean setSinnoriProperteies(String sinnoriConfigurationFileName) {
		System.out.printf("sinnoriConfigurationFileName width=%s", sinnoriConfigurationFileName);
		System.out.println();
		
		
		File configFile = new File(sinnoriConfigurationFileName);
		if (!configFile.exists()) {
			String errorMessage = String.format("신놀이 환경 설정 파일 변수명[%s]의 값[%s]에서 지정한 파일이 존재하지 않습니다.", SINNORI_CONFIG_FILE_VAR_NAME, sinnoriConfigurationFileName);
			System.out.println(errorMessage);
			
			JOptionPane.showMessageDialog(mainFrame, errorMessage);
			
			//System.exit(1);
			return false;
		}
		
		if (!configFile.isFile()) {
			String errorMessage = String.format("신놀이 환경 설정 파일 변수명[%s]의 값[%s]에서 지정한 파일이 일반 파일이 아닙니다.", SINNORI_CONFIG_FILE_VAR_NAME, sinnoriConfigurationFileName);
			System.out.println(errorMessage);
			
			JOptionPane.showMessageDialog(mainFrame, errorMessage);
			return false;
		}
		
		if (!configFile.canRead()) {
			String errorMessage = String.format("신놀이 환경 설정 파일 변수명[%s]의 값[%s]에서 지정한 파일을 읽을 수 없습니다.", SINNORI_CONFIG_FILE_VAR_NAME, sinnoriConfigurationFileName);
			// System.out.printf();
			System.out.println(errorMessage);
			
			JOptionPane.showMessageDialog(mainFrame, errorMessage);
			return false;
		}
		
		if (!configFile.canWrite()) {
			String errorMessage = String.format("신놀이 환경 설정 파일 변수명[%s]의 값[%s]에서 지정한 파일을 저장할 수 없습니다.", SINNORI_CONFIG_FILE_VAR_NAME, sinnoriConfigurationFileName);
			System.out.println(errorMessage);
			
			JOptionPane.showMessageDialog(mainFrame, errorMessage);
			return false;
		}
		
		FileInputStream sinnoriConfigFIS = null;
		InputStreamReader sinnoriConfigISR = null;
		try {

			sinnoriConfigFIS = new FileInputStream(configFile);

			sinnoriConfigISR = new InputStreamReader(
					sinnoriConfigFIS, SINNORI_CONFIG_FILE_CHARSET);

			configFileProperties.load(sinnoriConfigISR);

			// System.out.println(this.toString());
			return true;
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			if (null == errorMessage) {
				errorMessage = "설정 파일 읽기 시도시 알 수 없는 에러 발생";
			} else {
				errorMessage = "설정 파일 읽기 시도시 알 수 없는 에러 발생::" + errorMessage;
			}
			System.out.println(errorMessage);
			
			JOptionPane.showMessageDialog(mainFrame, errorMessage);
			return false;
		} finally {
			try {
				if (sinnoriConfigISR != null)
					sinnoriConfigISR.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
	
	private void setAllInVisible() {
		// mainFrame.setVisible(false);
		noConfigFileScreen.setVisible(false);
		// mainFrame.remove(noConfigFileScreen);
	}

	@Override
	public void showSetupScreen(File configFile) {
		System.out.printf("noConfigFileScreen width=%d, height=%d", noConfigFileScreen.getWidth(), noConfigFileScreen.getHeight());
		System.out.println();
		
		// mainFrame.add(noConfigFileScreen);
		// mainFrame.pack();
		noConfigFileScreen.setVisible(true);
		// mainFrame.setVisible(true);
		
		// mainFrame.setSize(noConfigFileScreen.getSize());
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		
	}

}
