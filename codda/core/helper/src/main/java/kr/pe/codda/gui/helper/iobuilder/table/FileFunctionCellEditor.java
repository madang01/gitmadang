package kr.pe.codda.gui.helper.iobuilder.table;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;


@SuppressWarnings("serial")
public class FileFunctionCellEditor extends DefaultCellEditor {
	

	public FileFunctionCellEditor(JCheckBox checkBox) {
		super(checkBox);
	}

	public Component getTableCellEditorComponent(JTable table,
			Object value, boolean isSelected, int row, int column) {		
		
		FileFunctionCellValue messageInfoFileCellValue = (FileFunctionCellValue)value;
		
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
