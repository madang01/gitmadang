/*
 * Created by JFormDesigner on Sat Nov 29 11:20:24 KST 2014
 */

package kr.pe.sinnori.gui.config.screen;

import java.awt.Container;
import java.awt.Frame;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import kr.pe.sinnori.gui.config.buildsystem.MainProjectBuildSystemState;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author Jonghoon Won
 */
@SuppressWarnings("serial")
public class MainWindow extends JFrame 
	implements SinnoriInstalledPathWindowManagerIF, 
	MainProjectManagerWindowManagerIF, MainProjectEditWindowManagerIF {	
		
	
	public MainWindow() {		
		initComponents();
		
		hideAllScreen();
		sinnoriInstalledPathPanel.setVisible(true);
		mainPanel.setVisible(true);
		this.pack();
	}

	private void hideAllScreen() {
		sinnoriInstalledPathPanel.setVisible(false);
		mainProjectManagerPanel.setVisible(false);
		mainProjectEditPanel.setVisible(false);
	}
	
	@Override
	public void goMainProjectManagerScreen(String sinnoriInstalledPathString, List<String> mainProjectNameList) {
		hideAllScreen();
		
		mainProjectManagerPanel.setVisible(true);
		mainProjectManagerPanel.setScreen(sinnoriInstalledPathString, mainProjectNameList);
		this.pack();
	}
	
	@Override
	public void goBackMainProjectManagerScreen() {
		hideAllScreen();
		mainProjectManagerPanel.setVisible(true);
		this.pack();
	}

	@Override
	public void goMainProjectEditScreen(MainProjectBuildSystemState mainProjectBuildSystemState) {
		hideAllScreen();
		
		mainProjectEditPanel.setScreen(mainProjectBuildSystemState);
		mainProjectEditPanel.setVisible(true);
		
		this.pack();
	}

	
	
	@Override
	public Frame getMainWindow() {
		return this;
	}

	public JFrame getThis() {
		return this;
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner non-commercial license
		mainPanel = new JPanel();
		sinnoriInstalledPathPanel = new SinnoriInstalledPathPanel(this, this);
		mainProjectManagerPanel = new MainProjectManagerPanel(this, this);
		mainProjectEditPanel = new MainProjectEditPanel(this, this);

		//======== this ========
		Container contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$rgap, ${growing-button}, $rgap",
			"$nlgap, default:grow, $nlgap"));

		//======== mainPanel ========
		{
			mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
			mainPanel.add(sinnoriInstalledPathPanel);

			//---- mainProjectManagerPanel ----
			mainProjectManagerPanel.setVisible(false);
			mainPanel.add(mainProjectManagerPanel);

			//---- mainProjectEditPanel ----
			mainProjectEditPanel.setVisible(false);
			mainPanel.add(mainProjectEditPanel);
		}
		contentPane.add(mainPanel, CC.xy(2, 2));
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner non-commercial license
	private JPanel mainPanel;
	private SinnoriInstalledPathPanel sinnoriInstalledPathPanel;
	private MainProjectManagerPanel mainProjectManagerPanel;
	private MainProjectEditPanel mainProjectEditPanel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables	
		
}
