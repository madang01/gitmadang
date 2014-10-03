package main;
import java.awt.EventQueue;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import kr.pe.sinnori.common.util.SequencedProperties;
import kr.pe.sinnori.screen.MainControllerIF;
import kr.pe.sinnori.screen.Step1DirectoryConfirmScreen;
import kr.pe.sinnori.screen.Step2SourceBuilderScreen;


public class SinnoriSourceFileBuilderMain implements MainControllerIF {
	private JFrame mainFrame;
	private Step1DirectoryConfirmScreen step1DirectoryConfirmScreen = null;
	private Step2SourceBuilderScreen step2SourceBuilderScreen= null;
	
	private File sinnoriInstallPathFileObj = null;
	private String sinnoriInstallAbsPathName = null;
	/**
	 * 메인 프로젝트는 설정 파일을 갖는 프로젝트이다.
	 */
	private ArrayList<String> mainProjectList = new ArrayList<String>();
	private HashMap<String, SequencedProperties> project2ConfigHash = new HashMap<String, SequencedProperties>();
	
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

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		mainFrame = new JFrame();
		mainFrame.setTitle("메시지 소스 파일 제작기");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		
		step1DirectoryConfirmScreen = new Step1DirectoryConfirmScreen(mainFrame, this);
		
		// step2SourceBuilderScreen = new Step2SourceBuilderScreen(mainFrame);
		// mainFrame.add(step2SourceBuilderScreen);
		mainFrame.add(step1DirectoryConfirmScreen);
		
		mainFrame.pack();
		step1DirectoryConfirmScreen.setVisible(true);
	}
	
	@Override
	public void nextStep2Screen(String sinnoriInstalledPathName) {
		sinnoriInstallPathFileObj = new File(sinnoriInstalledPathName);
		if (!sinnoriInstallPathFileObj.exists()) {
			JOptionPane.showMessageDialog(mainFrame, "신놀이 설치 경로가 존재하지 않습니다.");
			return;
		}
		
		try {
			sinnoriInstallPathFileObj = sinnoriInstallPathFileObj.getCanonicalFile();
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(mainFrame, "신놀이 설치 경로 파일 객체화 하는 과정에서 입출력 에러 발생");
			e1.printStackTrace();
			return;
		}
		
		sinnoriInstallAbsPathName = sinnoriInstallPathFileObj.getAbsolutePath();
		
		StringBuilder projectPathNameBuilder = new  StringBuilder(sinnoriInstallAbsPathName);
		projectPathNameBuilder.append(File.separator);
		projectPathNameBuilder.append("project");
		
		File projectPathFileObj = new File(projectPathNameBuilder.toString());
		
		if (!projectPathFileObj.exists()) {
			JOptionPane.showMessageDialog(mainFrame, "신놀이 설치 경로에 project 디렉토리가 존재하지 않습니다.");
			return;
		}
		
		if (!projectPathFileObj.isDirectory()) {
			JOptionPane.showMessageDialog(mainFrame, "신놀이 설치 경로의 project 는 디렉토리가 아닙니다.");
			return;
		}
		
		File[] projectFileObjList = projectPathFileObj.listFiles();
		
		// ArrayList<String> projectList  = new ArrayList<String>();
		mainProjectList.clear();
		for (int i=0; i < projectFileObjList.length; i ++) {
			File f = projectFileObjList[i];
			if (f.isDirectory()) {
				StringBuilder configFileBuilder = new StringBuilder(f.getAbsolutePath());
				configFileBuilder.append(File.separator);
				configFileBuilder.append("config");
				configFileBuilder.append(File.separator);
				configFileBuilder.append(MainControllerIF.SINNORI_CONFIG_FILE_NAME);
				
				File configFileObjOfProject = new File(configFileBuilder.toString());
				if (configFileObjOfProject.exists() && configFileObjOfProject.isFile()) {
					if (!configFileObjOfProject.canRead()) {
						String errorMessage = String.format("프로젝트 [%s]의 설정파일[project_config.properteis]은 읽기 권한이 없습니다.", f.getName());
						JOptionPane.showMessageDialog(mainFrame, errorMessage);
						return;
					}
					
					if (!configFileObjOfProject.canWrite()) {
						String errorMessage = String.format("프로젝트 [%s]의 설정파일[project_config.properteis]은 쓰기 권한이 없습니다.", f.getName());
						JOptionPane.showMessageDialog(mainFrame, errorMessage);
						return;
					}
					
					String projectName = f.getName();
					
					FileInputStream fis_sinnoriConfig_file = null;
					InputStreamReader isr_sinnoriConfig_file = null;
					try {

						fis_sinnoriConfig_file = new FileInputStream(configFileObjOfProject);

						isr_sinnoriConfig_file = new InputStreamReader(
								fis_sinnoriConfig_file, "UTF-8");

						SequencedProperties configFileProperties = new SequencedProperties();
						
						configFileProperties.load(isr_sinnoriConfig_file);
						
						/**
						 * 메인 프로젝트는 설정 파일을 갖는 프로젝트이다.
						 */
						mainProjectList.add(projectName);
						project2ConfigHash.put(projectName, configFileProperties);

					} catch (Exception e) {
						e.printStackTrace();
						
						String errorMessage = String.format("프로젝트 [%s]의 설정파일[project_config.properteis]을 읽어 Properties 객채 생성하는 과정에서 입출력 에러가 발생하여 실패하였습니다.", projectName);
						JOptionPane.showMessageDialog(mainFrame, errorMessage);
						return;
					} finally {
						try {
							if (isr_sinnoriConfig_file != null)
								isr_sinnoriConfig_file.close();
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						
						try {
							if (fis_sinnoriConfig_file != null)
								fis_sinnoriConfig_file.close();
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					} 
				}
			}
		}
		
		step1DirectoryConfirmScreen.setVisible(false);
		mainFrame.remove(step1DirectoryConfirmScreen);
		
		step2SourceBuilderScreen = new Step2SourceBuilderScreen(mainFrame, this, sinnoriInstallAbsPathName, mainProjectList, project2ConfigHash);
		mainFrame.add(step2SourceBuilderScreen);
		mainFrame.pack();
		step2SourceBuilderScreen.setVisible(true);
	}

	@Override
	public void finish() {
		mainFrame.dispose();
		System.out.println("작업 완료");
	}
}
