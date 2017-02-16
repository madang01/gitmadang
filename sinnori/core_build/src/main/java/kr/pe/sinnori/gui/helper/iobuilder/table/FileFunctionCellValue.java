package kr.pe.sinnori.gui.helper.iobuilder.table;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import kr.pe.sinnori.common.etc.CommonType;
import kr.pe.sinnori.common.exception.MessageInfoSAXParserException;
import kr.pe.sinnori.common.message.builder.info.MessageInfo;
import kr.pe.sinnori.common.message.builder.info.MessageInfoSAXParser;
import kr.pe.sinnori.common.util.CommonStaticUtil;
import kr.pe.sinnori.gui.helper.iobuilder.screen.FileFunctionManagerIF;

@SuppressWarnings("serial")
public class FileFunctionCellValue extends JPanel {
	private Logger log = LoggerFactory.getLogger(FileFunctionCellValue.class);
	
	private int row;
	// private File messageInfoFile = null;
	private MessageInfo messageInfo = null;
	private FileFunctionManagerIF fileFunctionManager = null;
	private Component parentComponent = null;
	
	private JButton viewButton;
	private JButton retryButton;
	
	private void showMessageDialog(String message) {
		JOptionPane.showMessageDialog(parentComponent, 
				CommonStaticUtil.splitString(message,
						CommonType.LINE_SEPARATOR_GUBUN.NEWLINE, 100));
	}
	
	class ViewButtonAction implements ActionListener {
		
		
		@Override
		public void actionPerformed(ActionEvent e) {
			showMessageDialog(messageInfo.toString());
		}
	}
	
	class RetryButtonAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {			
			MessageInfoSAXParser messageInfoSAXParser = null;
			try {
				messageInfoSAXParser = new MessageInfoSAXParser();
			} catch (MessageInfoSAXParserException e1) {
				showMessageDialog(e1.toString());
				log.warn("fail to create instacne of  MessageInfoSAXParser class", e1);
				return;
			}
			MessageInfo newMessageInfo=null;
			try {
				newMessageInfo = messageInfoSAXParser.parse(messageInfo.getMessageInfoXMLFile(), true);
			} catch (IllegalArgumentException | SAXException | IOException e1) {
				showMessageDialog(e1.toString());
				log.warn("fail to parse the message info file",  e1);
				return;
			}
			if (null == newMessageInfo) {
				showMessageDialog("fail to parse the message["+messageInfo.getMessageID()+"] information xml file");
				return;
			}
			messageInfo = newMessageInfo;
			
			
			
			fileFunctionManager.updateRowOfMessageInfoTableAccordingToNewMessageInfoUpdate(row, newMessageInfo);
		}
	}
	
	public FileFunctionCellValue(int row, MessageInfo messageInfo, FileFunctionManagerIF messageInfoManager, Component parentComponent) {
		this.row = row;
		// this.messageInfoFile = messageInfoFile;
		this.messageInfo = messageInfo;
		this.fileFunctionManager = messageInfoManager;
		this.parentComponent = parentComponent;
		
		viewButton = new JButton("View");
		viewButton.setOpaque(true);
		
		retryButton = new JButton("Reread");
		retryButton.setOpaque(true);
		
		
		viewButton.addActionListener(new ViewButtonAction());
		
		retryButton.addActionListener(new RetryButtonAction());
		
		add(viewButton);
		add(retryButton);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MessageInfoFileCellValue [messageID=");
		builder.append(messageInfo.getMessageID());
		builder.append("]");
		return builder.toString();
	}

}
