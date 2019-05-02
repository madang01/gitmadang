/*
 * Created by JFormDesigner on Fri Feb 03 23:13:21 KST 2017
 */

package kr.pe.codda.gui.helper.main.screen;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.gui.helper.lib.ScreenManagerIF;
import kr.pe.codda.gui.helper.projectmanager.screen.InstalledPathPanel;

/**
 * @author Jonghoon Won
 */
@SuppressWarnings("serial")
public class FirstPanel extends JPanel {
	@SuppressWarnings("unused")
	private InternalLogger log = InternalLoggerFactory.getInstance(InstalledPathPanel.class);
	@SuppressWarnings("unused")
	private Frame mainFrame = null;
	private ScreenManagerIF screenManagerIF = null;
	
	
	public FirstPanel(Frame mainFrame, ScreenManagerIF screenManagerIF) {
		this.mainFrame = mainFrame;
		this.screenManagerIF = screenManagerIF;
		
		mainFrame.setTitle("First Screen");
		initComponents();
	}

	private void AllMainProjectManagerScreenButtonActionPerformed(ActionEvent e) {
		if (null != screenManagerIF) 	screenManagerIF.moveToInstalledPathScreen();
	}

	private void IOBuilderScreenButtonActionPerformed(ActionEvent e) {
		if (null != screenManagerIF) screenManagerIF.moveToIOFileSetBuilderScreen();
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
		AllMainProjectManagerScreenButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AllMainProjectManagerScreenButtonActionPerformed(e);
				AllMainProjectManagerScreenButtonActionPerformed(e);
			}
		});
		add(AllMainProjectManagerScreenButton, CC.xy(2, 4));

		//---- IOBuilderScreenButton ----
		IOBuilderScreenButton.setText("IO Builder");
		IOBuilderScreenButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				IOBuilderScreenButtonActionPerformed(e);
			}
		});
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
