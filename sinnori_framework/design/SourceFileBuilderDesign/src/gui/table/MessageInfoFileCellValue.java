package gui.table;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class MessageInfoFileCellValue extends JPanel {
	private String messageID = null;
	private JButton viewButton;
	private JButton retryButton;
	
	public MessageInfoFileCellValue(String messageID, Component parentComponent) {
		this.messageID = messageID;
		
		viewButton = new JButton("보기");
		viewButton.setOpaque(true);
		
		retryButton = new JButton("다시 읽기");
		retryButton.setOpaque(true);
		
		class ViewButtonAction implements ActionListener {
			private String messageID;
			private Component parentComponent = null;
			
			public ViewButtonAction(String messageID, Component parentComponent) {
				this.messageID = messageID;
				this.parentComponent = parentComponent;
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("ViewButtonAction::"+e.toString());
				JOptionPane.showMessageDialog(parentComponent, "ViewButtonAction::call::"+messageID);
			}
		}
		
		
		viewButton.addActionListener(new ViewButtonAction(messageID, parentComponent));
		
		class RetryButtonAction implements ActionListener {
			private String messageID;
			private Component parentComponent = null;
			
			public RetryButtonAction(String messageID, Component parentComponent) {
				this.messageID = messageID;
				this.parentComponent = parentComponent;
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("RetryButtonAction::"+e.toString());
				JOptionPane.showMessageDialog(parentComponent, "RetryButtonAction::call::"+messageID);
			}
		}
		
		retryButton.addActionListener(new RetryButtonAction(messageID, parentComponent));
		
		add(viewButton);
		add(retryButton);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MessageInfoFileCellValue [messageID=");
		builder.append(messageID);
		builder.append("]");
		return builder.toString();
	}

}
