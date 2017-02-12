/*
 * Created by JFormDesigner on Sat Oct 17 17:48:18 KST 2015
 */

package kr.pe.sinnori.gui.helper.projectmanager.screen;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import kr.pe.sinnori.common.etc.CommonType;
import kr.pe.sinnori.common.util.CommonStaticUtil;
import kr.pe.sinnori.gui.helper.ScreenManagerIF;
import kr.pe.sinnori.gui.util.PathSwingAction;

/**
 * @author Jonghoon Won
 */
@SuppressWarnings("serial")
public class SinnoriInstalledPathPanel extends JPanel {
	private Logger log = LoggerFactory.getLogger(SinnoriInstalledPathPanel.class);
	private Frame mainFrame = null;
	private ScreenManagerIF screenManagerIF = null;
	private JFileChooser sinnoriInstalledPathChooser = null;
	
	public SinnoriInstalledPathPanel() {
		initComponents();		
	}
	
	public SinnoriInstalledPathPanel(Frame mainFrame, ScreenManagerIF screenManagerIF) {
		this.mainFrame = mainFrame;
		this.screenManagerIF = screenManagerIF;
		initComponents();
		
		// FIXME!
		//sinnoriInstalledPathTextField.setText("/home/madang01/gitsinnori/sinnori");
		
	}
	
	private void showMessageDialog(String message) {
		JOptionPane.showMessageDialog(mainFrame, 
				CommonStaticUtil.splitString(message,
						CommonType.SPLIT_STRING_GUBUN.NEWLINE, 100));
	}
	
	private void nextStepButtonActionPerformed(ActionEvent e) {
		String sinnoriInstalledPathString = sinnoriInstalledPathTextField.getText();
		if ( null == sinnoriInstalledPathString) {
			showMessageDialog("Please insert Sinnori installed path string");
			sinnoriInstalledPathTextField.requestFocusInWindow();
			return;
		}
		sinnoriInstalledPathString = sinnoriInstalledPathString.trim();
		sinnoriInstalledPathTextField.setText(sinnoriInstalledPathString);
		
		if (sinnoriInstalledPathString.equals("")) {
			showMessageDialog("Please insert Sinnori installed path string");
			sinnoriInstalledPathTextField.requestFocusInWindow();
			return;
		}
		
		File sinnoriInstalledPath = new File(sinnoriInstalledPathString);
		if (!sinnoriInstalledPath.exists()) {
			String errorMessage = String.format("The path[%s] that Sinnori installed doesn't exist", sinnoriInstalledPathString);
			log.warn(errorMessage);
			
			showMessageDialog(errorMessage);
			sinnoriInstalledPathTextField.requestFocusInWindow();
			return;
		}
		
		if (!sinnoriInstalledPath.isDirectory()) {
			String errorMessage = String.format("The path[%s] that Sinnori installed is not a directory", sinnoriInstalledPathString);
			log.warn(errorMessage);
			
			showMessageDialog(errorMessage);
			sinnoriInstalledPathTextField.requestFocusInWindow();
			return;
		}
		
		if (!sinnoriInstalledPath.canRead()) {
			String errorMessage = String.format("The path[%s] that Sinnori installed has a permission to read", sinnoriInstalledPathString);
			log.warn(errorMessage);
			
			showMessageDialog(errorMessage);
			sinnoriInstalledPathTextField.requestFocusInWindow();
			return;
		}
		
		if (!sinnoriInstalledPath.canWrite()) {
			String errorMessage = String.format("The path[%s] that Sinnori installed has a permission to write", sinnoriInstalledPathString);
			log.warn(errorMessage);
			
			showMessageDialog(errorMessage);
			sinnoriInstalledPathTextField.requestFocusInWindow();
			return;
		}
		
		try {
			sinnoriInstalledPathString = sinnoriInstalledPath.getCanonicalPath();
		} catch (IOException e1) {
			String errorMessage = String.format("fail to get the canonical pathname of the path[%s] that Sinnori installed", sinnoriInstalledPathString);
			log.warn(errorMessage);
			
			showMessageDialog(errorMessage);
			sinnoriInstalledPathTextField.requestFocusInWindow();
			return;
		}
		
		/*String projectBasePathString = BuildSystemPathSupporter.getProjectBasePathString(sinnoriInstalledPathString);
		
		File projectBasePath = new File(projectBasePathString);
		if (! projectBasePath.exists()) {
			String errorMessage = String.format("the sinnori installed path(=parameter sinnoriInstalledPathString[%s])'s the project base path[%s] doesn't exist", 
					sinnoriInstalledPathString, projectBasePathString);
			
			log.warn(errorMessage);
			
			showMessageDialog(errorMessage);
			sinnoriInstalledPathTextField.requestFocusInWindow();
			return;
		}
		
		if (!projectBasePath.isDirectory()) {
			String errorMessage = String.format("the sinnori installed path(=parameter sinnoriInstalledPathString[%s])'s the project base path[%s] is not a direcotry", 
					sinnoriInstalledPathString, projectBasePathString);
			log.warn(errorMessage);
			
			showMessageDialog(errorMessage);
			sinnoriInstalledPathTextField.requestFocusInWindow();
			return;
		}
		
		if (!projectBasePath.canRead()) {
			String errorMessage = String.format("the sinnori installed path(=parameter sinnoriInstalledPathString[%s])'s the project base path[%s] doesn't hava permission to read", 
					sinnoriInstalledPathString, projectBasePathString);
			log.warn(errorMessage);
			
			showMessageDialog(errorMessage);
			sinnoriInstalledPathTextField.requestFocusInWindow();
			return;
		}
		
		List<String> mainProjectNameList = new ArrayList<String>();
		
		for (File fileOfList : projectBasePath.listFiles()) {
			if (fileOfList.isDirectory()) {
				if (!fileOfList.canRead()) {
					String errorMessage = String.format("the sinnori project base path[%s] doesn't hava permission to read", fileOfList.getAbsolutePath());
					log.warn(errorMessage);
					
					showMessageDialog(errorMessage);
					sinnoriInstalledPathTextField.requestFocusInWindow();
					return;
				}
				
				if (!fileOfList.canWrite()) {
					String errorMessage = String.format("the sinnori project base path[%s] doesn't hava permission to write", fileOfList.getAbsolutePath());
					log.warn(errorMessage);
					
					showMessageDialog(errorMessage);
					sinnoriInstalledPathTextField.requestFocusInWindow();
					return;
				}
				
				mainProjectNameList.add(fileOfList.getName());
			}
		}
		*/
		sinnoriInstalledPathTextField.setText(sinnoriInstalledPathString);
		
		screenManagerIF.moveToAllMainProjectManagerScreen(sinnoriInstalledPathString);
	}

	private void prevStepButtonActionPerformed(ActionEvent e) {
		screenManagerIF.moveToFirstScreen();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner non-commercial license
		sinnoriInstallPathLinePanel = new JPanel();
		sinnoriInstalledPathLabel = new JLabel();
		sinnoriInstalledPathTextField = new JTextField();
		sinnoriInstalledPathButton = new JButton();
		nextStepLinePanel = new JPanel();
		prevStepButton = new JButton();
		nextStepButton = new JButton();

		//======== this ========
		setLayout(new FormLayout(
			"$ugap, [600px,pref]:grow, $ugap",
			"2*($lgap, default), $lgap"));
		/** Post-initialization Code start */
		UIManager.put("FileChooser.readOnly", Boolean.TRUE); 
		sinnoriInstalledPathChooser = new JFileChooser();
		sinnoriInstalledPathChooser.setMultiSelectionEnabled(true);
		sinnoriInstalledPathChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		PathSwingAction pathAction = new PathSwingAction(mainFrame, sinnoriInstalledPathChooser, sinnoriInstalledPathTextField);
		sinnoriInstalledPathButton.setAction(pathAction);
		nextStepButton.setToolTipText(
			"<html>update all project'config and overwrite all project'shells based on installed path</html>");
		/** Post-initialization Code end */

		//======== sinnoriInstallPathLinePanel ========
		{
			sinnoriInstallPathLinePanel.setLayout(new FormLayout(
				"78dlu, $lcgap, [156dlu,pref]:grow, $lcgap, default",
				"default"));

			//---- sinnoriInstalledPathLabel ----
			sinnoriInstalledPathLabel.setText("Sinnori installed path :");
			sinnoriInstallPathLinePanel.add(sinnoriInstalledPathLabel, CC.xy(1, 1));
			sinnoriInstallPathLinePanel.add(sinnoriInstalledPathTextField, CC.xy(3, 1));

			//---- sinnoriInstalledPathButton ----
			sinnoriInstalledPathButton.setText("path");
			sinnoriInstallPathLinePanel.add(sinnoriInstalledPathButton, CC.xy(5, 1));
		}
		add(sinnoriInstallPathLinePanel, CC.xy(2, 2));

		//======== nextStepLinePanel ========
		{
			nextStepLinePanel.setLayout(new FormLayout(
				"default, $ugap, default",
				"default:grow"));

			//---- prevStepButton ----
			prevStepButton.setText("Prev");
			prevStepButton.addActionListener(e -> prevStepButtonActionPerformed(e));
			nextStepLinePanel.add(prevStepButton, CC.xy(1, 1));

			//---- nextStepButton ----
			nextStepButton.setText("Next");
			nextStepButton.addActionListener(e -> nextStepButtonActionPerformed(e));
			nextStepLinePanel.add(nextStepButton, CC.xy(3, 1));
		}
		add(nextStepLinePanel, CC.xy(2, 4, CC.CENTER, CC.DEFAULT));
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner non-commercial license
	private JPanel sinnoriInstallPathLinePanel;
	private JLabel sinnoriInstalledPathLabel;
	private JTextField sinnoriInstalledPathTextField;
	private JButton sinnoriInstalledPathButton;
	private JPanel nextStepLinePanel;
	private JButton prevStepButton;
	private JButton nextStepButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
