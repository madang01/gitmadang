package kr.pe.sinnori.gui.table;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import kr.pe.sinnori.screen.SourceManagerIF;



@SuppressWarnings("serial")
public class SourceFileCellValue extends JPanel {	
	private kr.pe.sinnori.message.MessageInfo messageInfo = null;
	private SourceManagerIF sourceManager = null;;
	// private Component parentComponent = null;
	
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
			
			sourceManager.createSourceFile(ioCheckBox.isSelected(), directionCheckBox.isSelected(), messageInfo);
		}
		
	}
	
	public SourceFileCellValue(kr.pe.sinnori.message.MessageInfo messageInfo, 
			SourceManagerIF sourceManager) {
		this.messageInfo = messageInfo;
		this.sourceManager = sourceManager;
		// this.parentComponent = parentComponent;
		
		ioCheckBox = new JCheckBox("IO");
		directionCheckBox = new JCheckBox("방향성");
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

	public void setMessageInfo(kr.pe.sinnori.message.MessageInfo messageInfo) {
		this.messageInfo = messageInfo;
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
