/*
 * Created by JFormDesigner on Sat Nov 29 11:20:24 KST 2014
 */

package kr.pe.sinnori.gui.config.screen;

import java.awt.Container;

import javax.swing.JFrame;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author Jonghoon Won
 */
@SuppressWarnings("serial")
public class MainWindow extends JFrame {	
	public MainWindow() {
		initComponents();
	}

	private void createUIComponents() {
		// TODO: add custom component creation code here
		firstScreenPanel = new FirstScreen(this);
		projectEditScreenPanel = new ProjectEditScreen(this);
	}


	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner non-commercial license
		createUIComponents();


		//======== this ========
		Container contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$ugap, default:grow, $ugap",
			"$lgap, 178dlu, $lgap"));
		contentPane.add(firstScreenPanel, CC.xy(2, 2));

		//---- projectEditScreenPanel ----
		projectEditScreenPanel.setVisible(false);
		contentPane.add(projectEditScreenPanel, CC.xy(2, 2));
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner non-commercial license
	private FirstScreen firstScreenPanel;
	private ProjectEditScreen projectEditScreenPanel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables	
	public FirstScreen getFirstScreenPanel() {
		return firstScreenPanel;
	}

	public ProjectEditScreen getProjectEditScreenPanel() {
		return projectEditScreenPanel;
	}
	
	
}
