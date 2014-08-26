package gui.table;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;



@SuppressWarnings("serial")
public class SourceFileCellValue extends JPanel {	
	private String messageID = null;
	
	private JCheckBox ioCheckBox = null;
	private JCheckBox directionCheckBox = null;
	private JButton sourceFileCreateButton = null;	
	
	public SourceFileCellValue(String messageID, Component parentComponent) {
		this.messageID = messageID;
		// this.parentComponent = parentComponent;
		
		ioCheckBox = new JCheckBox("IO");
		directionCheckBox = new JCheckBox("방향성");
		sourceFileCreateButton = new JButton("생성");
		
		ioCheckBox.setOpaque(true);
		directionCheckBox.setOpaque(true);
		sourceFileCreateButton.setOpaque(true);
		
		class IOCheckboxAction implements ActionListener {
			private String messageID;
			private Component parentComponent = null;
			
			public IOCheckboxAction(String messageID, Component parentComponent) {
				this.messageID = messageID;
				this.parentComponent = parentComponent;
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("IOCheckboxAction::"+e.toString());
				JOptionPane.showMessageDialog(parentComponent, "IOCheckboxAction::call::"+messageID);
			}
			
		}
		
		ioCheckBox.addActionListener(new IOCheckboxAction(messageID, parentComponent));
		
		
		class DirectionCheckboxAction implements ActionListener {
			private String messageID;
			private Component parentComponent = null;
			
			public DirectionCheckboxAction(String messageID, Component parentComponent) {
				this.messageID = messageID;
				this.parentComponent = parentComponent;
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("DirectionCheckboxAction::"+e.toString());
				JOptionPane.showMessageDialog(parentComponent, "DirectionCheckboxAction::call::"+messageID);
			}
			
		}
		
		directionCheckBox.addActionListener(new DirectionCheckboxAction(messageID, parentComponent));
		
		
		class SourceFileCreateButtonAction implements ActionListener {
			private String messageID;
			private Component parentComponent = null;
			
			public SourceFileCreateButtonAction(String messageID, Component parentComponent) {
				this.messageID = messageID;
				this.parentComponent = parentComponent;
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("SourceFileCreateButtonAction::"+e.toString());
				JOptionPane.showMessageDialog(parentComponent, "SourceFileCreateButtonAction::call::"+messageID);
			}
			
		}
		sourceFileCreateButton.addActionListener(new SourceFileCreateButtonAction(messageID, parentComponent));
		
		add(ioCheckBox);
		add(directionCheckBox);
		add(sourceFileCreateButton);
	}
	
	public String getMessageID() {
		return messageID;
	}

	public void setMessageID(String messageID) {
		this.messageID = messageID;
	}

	public boolean isSelectedIO() {
		return ioCheckBox.isSelected();
	}

	public boolean isSelectedDirection() {
		return directionCheckBox.isSelected();
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SourceFileCellValue [messageID=");
		builder.append(messageID);
		builder.append(", isSelectedIO=");
		builder.append(ioCheckBox.isSelected());
		builder.append(", isSelectedDirection=");
		builder.append(directionCheckBox.isSelected());
		builder.append("]");
		return builder.toString();
	}
}
