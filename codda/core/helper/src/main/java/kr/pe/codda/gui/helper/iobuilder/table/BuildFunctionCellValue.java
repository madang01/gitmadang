package kr.pe.codda.gui.helper.iobuilder.table;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import kr.pe.codda.common.message.builder.info.MessageInfo;
import kr.pe.codda.gui.helper.iobuilder.screen.BuildFunctionManagerIF;;



@SuppressWarnings("serial")
public class BuildFunctionCellValue extends JPanel {	
	private MessageInfo messageInfo = null;
	private BuildFunctionManagerIF buildFunctionManager = null;;
	private Component parentComponent = null;
	
	private JCheckBox ioCoreCheckBox = null;
	private JCheckBox directionCheckBox = null;
	private JButton sourceCreateButton = null;	
	
	
	class SourceFileCreateButtonAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {			
			boolean isSuccess = buildFunctionManager.saveIOFileSetOfSelectedMessageInfo(ioCoreCheckBox.isSelected(), directionCheckBox.isSelected(), messageInfo);
			if (isSuccess) {
				StringBuilder messageBuilder = new StringBuilder("In the message id");
				messageBuilder.append(messageInfo.getMessageID());
				messageBuilder.append("]'");
				boolean whetherAndStringIsAppended = false;
				if (ioCoreCheckBox.isSelected()) {
					whetherAndStringIsAppended = true;
					messageBuilder.append("io core file set build success");
				}
				if (directionCheckBox.isSelected()) {
					if (whetherAndStringIsAppended) {
						messageBuilder.append(" and ");
					}
					messageBuilder.append("direction file set build success");
				}
				
				JOptionPane.showMessageDialog(parentComponent, messageBuilder.toString());
			}			
		}
	}
	
	public BuildFunctionCellValue(MessageInfo messageInfo, 
			BuildFunctionManagerIF sourceManager, Component parentComponent) {
		this.messageInfo = messageInfo;
		this.buildFunctionManager = sourceManager;
		this.parentComponent = parentComponent;
		
		ioCoreCheckBox = new JCheckBox("IO Core", true);
		directionCheckBox = new JCheckBox("Direction", true);
		sourceCreateButton = new JButton("Build");
		
		ioCoreCheckBox.setOpaque(true);
		directionCheckBox.setOpaque(true);
		sourceCreateButton.setOpaque(true);
		
		/*ioCheckBox.addActionListener(new IOCheckboxAction());
		directionCheckBox.addActionListener(new DirectionCheckboxAction());*/
		sourceCreateButton.addActionListener(new SourceFileCreateButtonAction());
		
		add(ioCoreCheckBox);
		add(directionCheckBox);
		add(sourceCreateButton);
	}
	
	public String getMessageID() {
		return messageInfo.getMessageID();
	}

	public boolean isSelectedIO() {
		return ioCoreCheckBox.isSelected();
	}

	public boolean isSelectedDirection() {
		return directionCheckBox.isSelected();
	}

	public void setMessageInfo(MessageInfo messageInfo) {
		this.messageInfo = messageInfo;
	}
	
	public MessageInfo getMessageInfo() {
		return messageInfo;
	}
	

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SourceFileCellValue [messageID=");
		builder.append(messageInfo.getMessageID());
		builder.append(", isSelectedIO=");
		builder.append(ioCoreCheckBox.isSelected());
		builder.append(", isSelectedDirection=");
		builder.append(directionCheckBox.isSelected());
		builder.append("]");
		return builder.toString();
	}
}
