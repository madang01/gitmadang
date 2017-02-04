/*
 * Created by JFormDesigner on Fri Feb 03 23:13:21 KST 2017
 */

package kr.pe.sinnori.gui.helper.screen;

import java.awt.Frame;
import java.awt.event.*;
import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;

import kr.pe.sinnori.gui.helper.screen.projectmanager.SinnoriInstalledPathPanel;

/**
 * @author Jonghoon Won
 */
@SuppressWarnings("serial")
public class FirstPanel extends JPanel {
	private Logger log = LoggerFactory.getLogger(SinnoriInstalledPathPanel.class);
	private Frame mainFrame = null;
	private ScreenManagerIF screenManagerIF = null;
	
	
	public FirstPanel(Frame mainFrame, ScreenManagerIF screenManagerIF) {
		this.mainFrame = mainFrame;
		this.screenManagerIF = screenManagerIF;
		
		initComponents();
	}

	private void AllMainProjectManagerScreenButtonActionPerformed(ActionEvent e) {
		screenManagerIF.moveToSinnoriInstalledPathScreen();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner non-commercial license
		TitleLabel = new JLabel();
		AllMainProjectManagerScreenButton = new JButton();
		IOBuilderScreenButton = new JButton();

		//======== this ========
		setLayout(new FormLayout(
			"$ugap, ${growing-button}, $ugap",
			"8dlu, default, 5dlu, 2*(default, 8dlu)"));

		//---- TitleLabel ----
		TitleLabel.setText("Menu List Screen");
		add(TitleLabel, CC.xy(2, 2, CC.CENTER, CC.DEFAULT));

		//---- AllMainProjectManagerScreenButton ----
		AllMainProjectManagerScreenButton.setText("All Main Project Manager");
		AllMainProjectManagerScreenButton.addActionListener(e -> AllMainProjectManagerScreenButtonActionPerformed(e));
		add(AllMainProjectManagerScreenButton, CC.xy(2, 4));

		//---- IOBuilderScreenButton ----
		IOBuilderScreenButton.setText("IO Builder");
		add(IOBuilderScreenButton, CC.xy(2, 6));
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner non-commercial license
	private JLabel TitleLabel;
	private JButton AllMainProjectManagerScreenButton;
	private JButton IOBuilderScreenButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
