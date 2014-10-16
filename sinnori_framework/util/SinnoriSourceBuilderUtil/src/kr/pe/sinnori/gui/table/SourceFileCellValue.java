package kr.pe.sinnori.gui.table;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import kr.pe.sinnori.screen.SourceManagerIF;



@SuppressWarnings("serial")
public class SourceFileCellValue extends JPanel {	
	private kr.pe.sinnori.common.message.MessageInfo messageInfo = null;
	private SourceManagerIF sourceManager = null;;
	private Component parentComponent = null;
	
	private JCheckBox ioCheckBox = null;
	private JCheckBox directionCheckBox = null;
	private JButton sourceCreateButton = null;	
	
	/*class IOCheckboxAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("IOCheckboxAction::"+e.toString());
			JOptionPane.showMessageDialog(parentComponent, "IOCheckboxAction::call::"+messageInfo.getMessageID());
		}
		
	}
	
	class DirectionCheckboxAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("DirectionCheckboxAction::"+e.toString());
			JOptionPane.showMessageDialog(parentComponent, "DirectionCheckboxAction::call::"+messageInfo.getMessageID());
		}
		
	}*/
	
	class SourceFileCreateButtonAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("SourceFileCreateButtonAction::"+e.toString()+"::"+messageInfo.getMessageID());
			
			boolean isSuccess = sourceManager.createSourceFile(ioCheckBox.isSelected(), directionCheckBox.isSelected(), messageInfo);
			if (isSuccess) {
				StringBuilder messageBuilder = new StringBuilder("메시지[");
				messageBuilder.append(messageInfo.getMessageID());
				messageBuilder.append("] ");
				if (ioCheckBox.isSelected()) {
					messageBuilder.append("IO 소스 ");
				}
				if (directionCheckBox.isSelected()) {
					messageBuilder.append("방향성 소스 ");
				}
				
				messageBuilder.append("생성이 완료 되었습니다.");
				
				JOptionPane.showMessageDialog(parentComponent, messageBuilder.toString());
			}			
		}
	}
	
	public SourceFileCellValue(kr.pe.sinnori.common.message.MessageInfo messageInfo, 
			SourceManagerIF sourceManager, Component parentComponent) {
		this.messageInfo = messageInfo;
		this.sourceManager = sourceManager;
		this.parentComponent = parentComponent;
		
		ioCheckBox = new JCheckBox("IO", true);
		directionCheckBox = new JCheckBox("방향성", true);
		sourceCreateButton = new JButton("생성");
		
		ioCheckBox.setOpaque(true);
		directionCheckBox.setOpaque(true);
		sourceCreateButton.setOpaque(true);
		
		/*ioCheckBox.addActionListener(new IOCheckboxAction());
		directionCheckBox.addActionListener(new DirectionCheckboxAction());*/
		sourceCreateButton.addActionListener(new SourceFileCreateButtonAction());
		
		add(ioCheckBox);
		add(directionCheckBox);
		add(sourceCreateButton);
	}
	
	public String getMessageID() {
		return messageInfo.getMessageID();
	}

	/*public void setMessageID(String messageID) {
		this.messageID = messageID;
	}*/

	public boolean isSelectedIO() {
		return ioCheckBox.isSelected();
	}

	public boolean isSelectedDirection() {
		return directionCheckBox.isSelected();
	}

	public void setMessageInfo(kr.pe.sinnori.common.message.MessageInfo messageInfo) {
		this.messageInfo = messageInfo;
	}
	
	public void createSourceFile() {
		sourceManager.createSourceFile(ioCheckBox.isSelected(), directionCheckBox.isSelected(), messageInfo);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SourceFileCellValue [messageID=");
		builder.append(messageInfo.getMessageID());
		builder.append(", isSelectedIO=");
		builder.append(ioCheckBox.isSelected());
		builder.append(", isSelectedDirection=");
		builder.append(directionCheckBox.isSelected());
		builder.append("]");
		return builder.toString();
	}
}
