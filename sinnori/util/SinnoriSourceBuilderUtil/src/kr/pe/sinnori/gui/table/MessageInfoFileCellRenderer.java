package kr.pe.sinnori.gui.table;

import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;


@SuppressWarnings("serial")
public class MessageInfoFileCellRenderer extends JPanel implements TableCellRenderer {

	public MessageInfoFileCellRenderer() {
		setOpaque(true);
		init();
	}

	private void init() {
		setLayout(new FlowLayout(FlowLayout.CENTER));
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {		
		MessageInfoFileCellValue messageInfoFileCellValue = (MessageInfoFileCellValue)value;
		
		if (isSelected) {
			messageInfoFileCellValue.setForeground(table.getSelectionForeground());
			messageInfoFileCellValue.setBackground(table.getSelectionBackground());
		} else {
			messageInfoFileCellValue.setForeground(table.getForeground());
			messageInfoFileCellValue.setBackground(table.getBackground());
		}
		
		return messageInfoFileCellValue;
	}
}
