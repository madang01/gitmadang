package main;

import javax.swing.JFrame;
import javax.swing.ToolTipManager;

import kr.pe.sinnori.gui.helper.main.screen.HelperMainWindow;


public class SinnoriHelperMain {
	public static void main(String[] args) {
		ToolTipManager.sharedInstance().setDismissDelay(1000);
		
		HelperMainWindow mainWindow = new HelperMainWindow();
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWindow.setVisible(true);	
	}
}
