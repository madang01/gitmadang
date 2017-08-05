/*
 * Created by JFormDesigner on Tue Jul 04 02:55:08 KST 2017
 */

package kr.pe.sinnori.gui.syncfileupdown.screen;

import java.awt.Container;

import javax.swing.BoxLayout;
import javax.swing.JFrame;

import kr.pe.sinnori.gui.syncfileupdown.lib.ScreenManagerIF;

/**
 * @author Jonghoon Won
 */
@SuppressWarnings("serial")
public class SyncFileUpDownMainWindow extends JFrame implements ScreenManagerIF {
	
	private LoginPanel loginPanel = new LoginPanel(this,  this);
	private SyncFileUpDownPanel syncFileUpDownPanel = new SyncFileUpDownPanel(this, this);
	
	private void postInitComponets() {		
		hideAllScreen();
		
		this.add(loginPanel);
		this.add(syncFileUpDownPanel);
		
		this.setTitle("Login Screen");
		loginPanel.setEnabled(true);
		loginPanel.setVisible(true);
		this.pack();
	}
	
	private void hideAllScreen() {
		loginPanel.setEnabled(false);		
		loginPanel.setVisible(false);
		
		
		syncFileUpDownPanel.setEnabled(false);		
		syncFileUpDownPanel.setVisible(false);
	}	
		
	public SyncFileUpDownMainWindow() {
		initComponents();
		postInitComponets();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner non-commercial license

		//======== this ========
		Container contentPane = getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner non-commercial license
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	public void goToLoginScreen() {
		hideAllScreen();
		
		loginPanel.setEnabled(true);
		loginPanel.setVisible(true);
		this.pack();
	}
	public void goToFileUpDownScreen() {
		hideAllScreen();
		
		syncFileUpDownPanel.setEnabled(true);
		syncFileUpDownPanel.setVisible(true);
		this.pack();
	}
}
