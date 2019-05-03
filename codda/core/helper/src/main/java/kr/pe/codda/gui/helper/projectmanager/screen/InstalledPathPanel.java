/*
 * Created by JFormDesigner on Sat Oct 17 17:48:18 KST 2015
 */

package kr.pe.codda.gui.helper.projectmanager.screen;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.type.LineSeparatorType;
import kr.pe.codda.common.type.ReadWriteMode;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.gui.helper.lib.ScreenManagerIF;
import kr.pe.codda.gui.util.PathSwingAction;

/**
 * @author Jonghoon Won
 */
@SuppressWarnings("serial")
public class InstalledPathPanel extends JPanel {
	private InternalLogger log = InternalLoggerFactory.getInstance(InstalledPathPanel.class);
	private Frame mainFrame = null;
	private ScreenManagerIF screenManagerIF = null;
	private JFileChooser installedPathChooser = null;
	
	private void showMessageDialog(String message) {
		JOptionPane.showMessageDialog(mainFrame, 
				CommonStaticUtil.splitString(message,
						LineSeparatorType.NEWLINE, 100));
	}
	

	/**
	 * @param sourcePathTextField TextField whose value is path  
	 * @return the writable and readable path. but if parameter sourceTextField's value is not a valid path then return null.
	 */
	private File getValidPathFromTextField(JTextField sourcePathTextField, ReadWriteMode	readWriteMode) throws RuntimeException {
		String sourcePathString = sourcePathTextField.getText();
		if (null == sourcePathString) {
			String errorMessage = String.format("parameter sourcePathTextField[%s]'s value is nul",
					sourcePathTextField.getName());
			throw new RuntimeException(errorMessage);
		}
		sourcePathString = sourcePathString.trim();
		sourcePathTextField.setText(sourcePathString);

		File sourcePath = null;
		try {
			sourcePath =CommonStaticUtil.getValidPath(sourcePathString, readWriteMode);
		} catch(RuntimeException e) {
			String errorMessage = e.toString();
			throw new RuntimeException(String.format("parameter sourcePathTextField[%s]'s value is not a valid path::%s", sourcePathTextField.getName(), errorMessage));
		}

		return sourcePath;
	}
	
	private void postInitComponents() {
		UIManager.put("FileChooser.readOnly", Boolean.TRUE); 
		installedPathChooser = new JFileChooser();
		installedPathChooser.setMultiSelectionEnabled(false);
		installedPathChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		PathSwingAction pathAction = new PathSwingAction(mainFrame, installedPathChooser, installedPathButton.getText(), installedPathTextField);		
		installedPathButton.setAction(pathAction);
		nextStepButton.setToolTipText(
			"<html>update all project'config and overwrite all project'shells based on installed path</html>");
		
		File installedPath = new File(".");
		
		String installedPathString= null;
		try {
			installedPathString = installedPath.getCanonicalPath();
			
			installedPathTextField.setText(installedPathString);
		} catch (IOException e) {
			String errorMessage = String.format("fail to get the canonical pathname of the installed path[%s]", installedPathString);
			log.warn(errorMessage, e);
		}
	}
	
	public InstalledPathPanel(Frame mainFrame, ScreenManagerIF screenManagerIF) {
		this.mainFrame = mainFrame;
		this.screenManagerIF = screenManagerIF;
		initComponents();
		
		postInitComponents();
	}	
	
	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner non-commercial license
		installPathLinePanel = new JPanel();
		installedPathLabel = new JLabel();
		installedPathTextField = new JTextField();
		installedPathButton = new JButton();
		nextStepLinePanel = new JPanel();
		prevStepButton = new JButton();
		nextStepButton = new JButton();

		//======== this ========
		setLayout(new FormLayout(
			"$ugap, [600px,pref]:grow, $ugap",
			"2*($lgap, default), $lgap"));

		//======== installPathLinePanel ========
		{
			installPathLinePanel.setLayout(new FormLayout(
				"78dlu, $lcgap, [156dlu,pref]:grow, $lcgap, default",
				"default"));

			//---- installedPathLabel ----
			installedPathLabel.setText("Codda installed path :");
			installPathLinePanel.add(installedPathLabel, CC.xy(1, 1));
			installPathLinePanel.add(installedPathTextField, CC.xy(3, 1));

			//---- installedPathButton ----
			installedPathButton.setText("path");
			installPathLinePanel.add(installedPathButton, CC.xy(5, 1));
		}
		add(installPathLinePanel, CC.xy(2, 2));

		//======== nextStepLinePanel ========
		{
			nextStepLinePanel.setLayout(new FormLayout(
				"default, $ugap, default",
				"default:grow"));

			//---- prevStepButton ----
			prevStepButton.setText("Prev");
			prevStepButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					prevStepButtonActionPerformed(e);
				}
			});
			nextStepLinePanel.add(prevStepButton, CC.xy(1, 1));

			//---- nextStepButton ----
			nextStepButton.setText("Next");
			nextStepButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					nextStepButtonActionPerformed(e);
				}
			});
			nextStepLinePanel.add(nextStepButton, CC.xy(3, 1));
		}
		add(nextStepLinePanel, CC.xy(2, 4, CC.CENTER, CC.DEFAULT));
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner non-commercial license
	private JPanel installPathLinePanel;
	private JLabel installedPathLabel;
	private JTextField installedPathTextField;
	private JButton installedPathButton;
	private JPanel nextStepLinePanel;
	private JButton prevStepButton;
	private JButton nextStepButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
	
	private void nextStepButtonActionPerformed(ActionEvent e) {
		File installedPath = null;
		try {
			installedPath = getValidPathFromTextField(installedPathTextField, ReadWriteMode.READ_WRITE);
		} catch(RuntimeException e1) {
			String errorMessage = String.format("fail to get the valid installed path::%s", e1.getMessage());
			log.warn(errorMessage);
			showMessageDialog(errorMessage);
			return;
		}
		
		String installedPathString= null;
		try {
			installedPathString = installedPath.getCanonicalPath();
		} catch (IOException e1) {
			String errorMessage = String.format("fail to get the canonical pathname of the installed path[%s]", installedPathString);
			log.warn(errorMessage);
			
			showMessageDialog(errorMessage);
			installedPathTextField.requestFocusInWindow();
			return;
		}
		
		installedPathTextField.setText(installedPathString);
		
		screenManagerIF.moveToAllMainProjectManagerScreen(installedPathString);
	}

	private void prevStepButtonActionPerformed(ActionEvent e) {
		screenManagerIF.moveToFirstScreen();
	}
}
