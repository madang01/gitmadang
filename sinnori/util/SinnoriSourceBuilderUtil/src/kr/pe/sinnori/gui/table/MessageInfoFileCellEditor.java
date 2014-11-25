package kr.pe.sinnori.gui.table;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;


@SuppressWarnings("serial")
public class MessageInfoFileCellEditor extends DefaultCellEditor {
	

	public MessageInfoFileCellEditor(JCheckBox checkBox) {
		super(checkBox);
	}

	public Component getTableCellEditorComponent(JTable table,
			Object value, boolean isSelected, int row, int column) {		
		
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
	
	public Object getCellEditorValue() {
	    return null;
	}
}
