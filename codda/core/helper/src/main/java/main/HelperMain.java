package main;

import javax.swing.JFrame;
import javax.swing.ToolTipManager;

import kr.pe.codda.gui.helper.main.screen.HelperMainWindow;

public class HelperMain {
	public static void main(String[] args) {
		ToolTipManager.sharedInstance().setDismissDelay(1000);
		
		HelperMainWindow mainWindow = new HelperMainWindow();
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWindow.setVisible(true);	
	}
}
