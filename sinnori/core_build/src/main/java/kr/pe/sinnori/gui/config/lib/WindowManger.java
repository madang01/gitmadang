package kr.pe.sinnori.gui.config.lib;

import javax.swing.JFrame;

import kr.pe.sinnori.common.exception.ConfigErrorException;
import kr.pe.sinnori.gui.config.buildsystem.MainProjectInformation;
import kr.pe.sinnori.gui.config.screen.FirstScreen;
import kr.pe.sinnori.gui.config.screen.MainWindow;
import kr.pe.sinnori.gui.config.screen.ProjectEditScreen;



public class WindowManger {
	private MainWindow mainWindow =  null;
	private FirstScreen firstScreenPanel;
	private ProjectEditScreen projectEditScreenPanel;
	

	/**
	 * 동기화 쓰지 않고 싱글턴 구현을 위한 비공개 생성자.
	 */
	private WindowManger() {
		mainWindow =  new MainWindow();
		firstScreenPanel = mainWindow.getFirstScreenPanel();
		projectEditScreenPanel = mainWindow.getProjectEditScreenPanel();
	}	
	
	/**
	 * 동기화 안쓰고 싱글턴 구현을 위한 내부 클래스
	 */
	private static final class WindowMangerHolder {
		static final WindowManger singleton = new WindowManger();
	}

	/**
	 * 동기화 쓰지 않는 싱글턴 메소드
	 * 
	 * @return 싱글턴 객체
	 */
	public static WindowManger getInstance() {
		return WindowMangerHolder.singleton;
	}
	
	public void startMainWindow() {
		try {
			mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);			
			mainWindow.setVisible(true);			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void changeFirstScreenToProjectEditScreen(MainProjectInformation mainProjectInformation) throws ConfigErrorException {
		firstScreenPanel.setVisible(false);
		try {
			projectEditScreenPanel.setProject(mainProjectInformation);
			projectEditScreenPanel.setVisible(true);
			mainWindow.pack();
		} catch (ConfigErrorException e) {
			firstScreenPanel.setVisible(true);
			throw e;
		}
		
	}
	
	public void changeProjectEditScreenToFirstScreen() {
		firstScreenPanel.setVisible(true);
		projectEditScreenPanel.setVisible(false);
		mainWindow.pack();
	}
	
	public JFrame getMainWindow() {
		return mainWindow;
	}
}
