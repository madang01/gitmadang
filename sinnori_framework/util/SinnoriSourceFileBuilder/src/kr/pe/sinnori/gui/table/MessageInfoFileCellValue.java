package kr.pe.sinnori.gui.table;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import kr.pe.sinnori.message.MessageInfoSAXParser;

@SuppressWarnings("serial")
public class MessageInfoFileCellValue extends JPanel {
	private File messageInfoFile = null;
	private kr.pe.sinnori.message.MessageInfo messageInfo = null;
	private SourceBuilderTableModel sourceBuilderTableModel = null;
	private int row;
	private Component parentComponent = null;
	
	private JButton viewButton;
	private JButton retryButton;
	
	
	class ViewButtonAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("ViewButtonAction::"+e.toString());
			JOptionPane.showMessageDialog(parentComponent, messageInfo.toString());
		}
	}
	
	class RetryButtonAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("RetryButtonAction::"+e.toString());
			
			MessageInfoSAXParser messageInfoSAXParser = new MessageInfoSAXParser(messageInfoFile);
			kr.pe.sinnori.message.MessageInfo oneMessageInfo = messageInfoSAXParser.parse();
			if (null == oneMessageInfo) {
				JOptionPane.showMessageDialog(parentComponent, "메시지 식별자["+messageInfo.getMessageID()+"]의 메시지 정보 파일 다시 읽기 실패");
				return;
			}
			messageInfo = oneMessageInfo;
			sourceBuilderTableModel.setValueAt(oneMessageInfo.getDirection().toString(), row, 1);
			SourceFileCellValue sourceFileCellValue = (SourceFileCellValue)sourceBuilderTableModel.getValueAt(row, 3);
			sourceFileCellValue.setMessageInfo(oneMessageInfo);
			
		}
	}
	
	public MessageInfoFileCellValue(File messageInfoFile, kr.pe.sinnori.message.MessageInfo messageInfo, SourceBuilderTableModel sourceBuilderTableModel, int row, Component parentComponent) {
		this.messageInfoFile = messageInfoFile;
		this.messageInfo = messageInfo;
		this.sourceBuilderTableModel = sourceBuilderTableModel;
		this.row = row;
		this.parentComponent = parentComponent;
		
		viewButton = new JButton("보기");
		viewButton.setOpaque(true);
		
		retryButton = new JButton("다시 읽기");
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
