/*
 * Created by JFormDesigner on Sat Feb 18 00:18:04 KST 2017
 */

package kr.pe.sinnori.gui.helper.iobuilder.screen;

import java.awt.Frame;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;

import kr.pe.sinnori.common.etc.CommonType;
import kr.pe.sinnori.common.exception.MessageInfoSAXParserException;
import kr.pe.sinnori.common.message.builder.IOFileSetContentsBuilderManager;
import kr.pe.sinnori.common.message.builder.info.MessageInfo;
import kr.pe.sinnori.common.message.builder.info.MessageInfoSAXParser;
import kr.pe.sinnori.common.util.CommonStaticUtil;
import kr.pe.sinnori.gui.helper.ScreenManagerIF;

/**
 * @author Jonghoon Won
 */
@SuppressWarnings("serial")
public class EachIOFileTypeBuilder extends JPanel {
	private Logger log = LoggerFactory.getLogger(EachIOFileTypeBuilder.class);
	private Frame mainFrame = null;
	private ScreenManagerIF screenManagerIF = null;
	
	private void showMessageDialog(String message) {
		JOptionPane.showMessageDialog(mainFrame, 
				CommonStaticUtil.splitString(message,
						CommonType.LINE_SEPARATOR_GUBUN.NEWLINE, 100));
	}
	
	public EachIOFileTypeBuilder(Frame mainFrame, ScreenManagerIF screenManagerIF) {
		this.mainFrame = mainFrame;
		this.screenManagerIF = screenManagerIF;
		
		initComponents();
	}

	private void eachIOFileTypeBuildButtonActionPerformed(ActionEvent e) {
		String messageInfoText = messageInfoXMLTextArea.getText();
		if (null == messageInfoText) {
			JOptionPane.showMessageDialog(mainFrame, "메시지 정보 파일 내용을 넣어주세요.");
			messageInfoXMLTextArea.requestFocusInWindow();
			return;
		}
		messageInfoText = messageInfoText.trim();
		messageInfoXMLTextArea.setText(messageInfoText);
		if (messageInfoText.equals("")) {
			showMessageDialog("메시지 정보 파일 내용을 넣어주세요.");
			messageInfoXMLTextArea.requestFocusInWindow();
			return;
		}
		
		File tempMessageInfoFile = null;
		FileOutputStream fos = null;
		
		try {
			tempMessageInfoFile = File.createTempFile("sinnoriMessageInfo", null);
			
			fos = new FileOutputStream(tempMessageInfoFile);
			fos.write(messageInfoText.getBytes("UTF-8"));
			
			// logger.log(Level.INFO, String.format("사용자가 입력한 메시지 정보 파일 내용을 저장할 임시 파일=[%s]", messageInfoFile.getAbsolutePath()));
		} catch (FileNotFoundException e1) {
			log.warn("1.fail to create temp file", e1);
			showMessageDialog("1.fail to create temp file::"+e1.toString());
			return;
		} catch (UnsupportedEncodingException e1) {
			log.warn("2.fail to create temp file", e1);
			showMessageDialog("2.fail to create temp file::"+e1.toString());
			return;
		} catch (IOException e1) {
			log.warn("3.fail to create temp file", e1);
			showMessageDialog("3.fail to create temp file::"+e1.toString());
			return;
		} finally {
			try {
				if (null != fos) fos.close();
			} catch(Exception e1) {
				
			}
		}

		IOFileSetContentsBuilderManager sourceFileBuilderManager = IOFileSetContentsBuilderManager.getInstance();
		MessageInfoSAXParser messageInfoSAXParser = null;
		try {
			messageInfoSAXParser = new MessageInfoSAXParser();
		} catch (MessageInfoSAXParserException e1) {
			String errorMessage = e1.toString();
			log.warn(errorMessage, e1);
			showMessageDialog(errorMessage);
			return;
		}
		MessageInfo messageInfo = null;
		try {
			messageInfo = messageInfoSAXParser.parse(tempMessageInfoFile, false);
		} catch (IllegalArgumentException | SAXException | IOException e1) {
			String errorMessage = e1.toString();
			log.warn(errorMessage, e1);
			showMessageDialog(errorMessage);
			return;
		}
	
		// ButtonModel selectedObject = sourceTypeRadioGroup.getSelection();
		// String actionCommand = selectedObject.getActionCommand();
		
		String result = null;
		if (messageRadioButton.isSelected()) {
			result = sourceFileBuilderManager.getMessageSourceFileContents(messageInfo.getMessageID(), "", messageInfo);
		} else if (encoderRadioButton.isSelected()) {
			result = sourceFileBuilderManager.getEncoderSourceFileContents(messageInfo.getMessageID(), "", messageInfo);
		} else if (decoderRadioButton.isSelected()) {
			result = sourceFileBuilderManager.getDecoderSourceFileContents(messageInfo.getMessageID(), "", messageInfo);
		} else if (serverCodecRadioButton.isSelected()) {
			result = sourceFileBuilderManager.getServerCodecSourceFileContents(messageInfo.getDirection(), messageInfo.getMessageID(), "");
		} else if (clientCodecRadioButton.isSelected()) {
			result = sourceFileBuilderManager.getClientCodecSourceFileContents(messageInfo.getDirection(), messageInfo.getMessageID(), "");
		} else {
			showMessageDialog("Warning::unknow IO file type radio button");
			return;
		}
		
		eachIOFileTypeResulTextArea.setText(result);
		
		if (null != tempMessageInfoFile) tempMessageInfoFile.delete();
	}

	private void firstScreenButtonActionPerformed(ActionEvent e) {
		screenManagerIF.moveToFirstScreen();
	}

	private void ioFileSetBuilderScreenButtonActionPerformed(ActionEvent e) {
		screenManagerIF.moveToIOFileSetBuilderScreen();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner non-commercial license
		eachIOFileTypeBuildPanel = new JPanel();
		panel1 = new JPanel();
		firstScreenButton = new JButton();
		ioFileSetBuilderScreenButton = new JButton();
		ioFileTypBuildMenuPanel = new JPanel();
		messageRadioButton = new JRadioButton();
		encoderRadioButton = new JRadioButton();
		decoderRadioButton = new JRadioButton();
		serverCodecRadioButton = new JRadioButton();
		clientCodecRadioButton = new JRadioButton();
		eachIOFileTypeBuildButton = new JButton();
		messageInfoXMLInputTitleLabel = new JLabel();
		messageInfoXMLScrollPane = new JScrollPane();
		messageInfoXMLTextArea = new JTextArea();
		eachIOFileTypeResultLabel = new JLabel();
		eachIOFileTypeResultScrollPane = new JScrollPane();
		eachIOFileTypeResulTextArea = new JTextArea();

		//======== this ========
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		//======== eachIOFileTypeBuildPanel ========
		{
			eachIOFileTypeBuildPanel.setLayout(new FormLayout(
				"$ugap, 308dlu:grow, $ugap",
				"3*($lgap, default), $lgap, fill:100dlu:grow, $lgap, default, $lgap, fill:120dlu:grow, $ugap"));

			//======== panel1 ========
			{
				panel1.setLayout(new FormLayout(
					"default, $ugap, default",
					"default"));

				//---- firstScreenButton ----
				firstScreenButton.setText("go back to 'first sreen'");
				firstScreenButton.addActionListener(e -> firstScreenButtonActionPerformed(e));
				panel1.add(firstScreenButton, CC.xy(1, 1));

				//---- ioFileSetBuilderScreenButton ----
				ioFileSetBuilderScreenButton.setText("go to 'IO file set builder screen'");
				ioFileSetBuilderScreenButton.addActionListener(e -> ioFileSetBuilderScreenButtonActionPerformed(e));
				panel1.add(ioFileSetBuilderScreenButton, CC.xy(3, 1));
			}
			eachIOFileTypeBuildPanel.add(panel1, CC.xy(2, 2));

			//======== ioFileTypBuildMenuPanel ========
			{
				ioFileTypBuildMenuPanel.setLayout(new BoxLayout(ioFileTypBuildMenuPanel, BoxLayout.X_AXIS));

				//---- messageRadioButton ----
				messageRadioButton.setText("Message");
				messageRadioButton.setSelected(true);
				ioFileTypBuildMenuPanel.add(messageRadioButton);

				//---- encoderRadioButton ----
				encoderRadioButton.setText("Encoder");
				ioFileTypBuildMenuPanel.add(encoderRadioButton);

				//---- decoderRadioButton ----
				decoderRadioButton.setText("Decoder");
				ioFileTypBuildMenuPanel.add(decoderRadioButton);

				//---- serverCodecRadioButton ----
				serverCodecRadioButton.setText("ServerCodec");
				ioFileTypBuildMenuPanel.add(serverCodecRadioButton);

				//---- clientCodecRadioButton ----
				clientCodecRadioButton.setText("ClientCodec");
				ioFileTypBuildMenuPanel.add(clientCodecRadioButton);

				//---- eachIOFileTypeBuildButton ----
				eachIOFileTypeBuildButton.setText("Build");
				eachIOFileTypeBuildButton.addActionListener(e -> eachIOFileTypeBuildButtonActionPerformed(e));
				ioFileTypBuildMenuPanel.add(eachIOFileTypeBuildButton);
			}
			eachIOFileTypeBuildPanel.add(ioFileTypBuildMenuPanel, CC.xy(2, 4));

			//---- messageInfoXMLInputTitleLabel ----
			messageInfoXMLInputTitleLabel.setText(">> Message Infomation XML Input");
			eachIOFileTypeBuildPanel.add(messageInfoXMLInputTitleLabel, CC.xy(2, 6));

			//======== messageInfoXMLScrollPane ========
			{
				messageInfoXMLScrollPane.setViewportView(messageInfoXMLTextArea);
			}
			eachIOFileTypeBuildPanel.add(messageInfoXMLScrollPane, CC.xy(2, 8));

			//---- eachIOFileTypeResultLabel ----
			eachIOFileTypeResultLabel.setText(">> Result");
			eachIOFileTypeBuildPanel.add(eachIOFileTypeResultLabel, CC.xy(2, 10));

			//======== eachIOFileTypeResultScrollPane ========
			{

				//---- eachIOFileTypeResulTextArea ----
				eachIOFileTypeResulTextArea.setEditable(false);
				eachIOFileTypeResultScrollPane.setViewportView(eachIOFileTypeResulTextArea);
			}
			eachIOFileTypeBuildPanel.add(eachIOFileTypeResultScrollPane, CC.xy(2, 12));
		}
		add(eachIOFileTypeBuildPanel);

		//---- ioFileTypeButtonGroup ----
		ButtonGroup ioFileTypeButtonGroup = new ButtonGroup();
		ioFileTypeButtonGroup.add(messageRadioButton);
		ioFileTypeButtonGroup.add(encoderRadioButton);
		ioFileTypeButtonGroup.add(decoderRadioButton);
		ioFileTypeButtonGroup.add(serverCodecRadioButton);
		ioFileTypeButtonGroup.add(clientCodecRadioButton);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner non-commercial license
	private JPanel eachIOFileTypeBuildPanel;
	private JPanel panel1;
	private JButton firstScreenButton;
	private JButton ioFileSetBuilderScreenButton;
	private JPanel ioFileTypBuildMenuPanel;
	private JRadioButton messageRadioButton;
	private JRadioButton encoderRadioButton;
	private JRadioButton decoderRadioButton;
	private JRadioButton serverCodecRadioButton;
	private JRadioButton clientCodecRadioButton;
	private JButton eachIOFileTypeBuildButton;
	private JLabel messageInfoXMLInputTitleLabel;
	private JScrollPane messageInfoXMLScrollPane;
	private JTextArea messageInfoXMLTextArea;
	private JLabel eachIOFileTypeResultLabel;
	private JScrollPane eachIOFileTypeResultScrollPane;
	private JTextArea eachIOFileTypeResulTextArea;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
