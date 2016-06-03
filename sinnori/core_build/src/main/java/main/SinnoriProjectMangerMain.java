package main;

import javax.swing.JFrame;
import javax.swing.ToolTipManager;

import kr.pe.sinnori.gui.config.screen.MainWindow;


public class SinnoriProjectMangerMain {
	public static void main(String[] args) {
		ToolTipManager.sharedInstance().setDismissDelay(10000);
		
		MainWindow mainWindow = new MainWindow();
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWindow.setVisible(true);
	}
}
