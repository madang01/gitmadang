package kr.pe.sinnori.gui.helper.projectmanager.table.messageinfo;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;


@SuppressWarnings("serial")
public class FileFunctionCellEditorForProject extends DefaultCellEditor {
	

	public FileFunctionCellEditorForProject(JCheckBox checkBox) {
		super(checkBox);
	}

	public Component getTableCellEditorComponent(JTable table,
			Object value, boolean isSelected, int row, int column) {		
		
		FileFunctionCellValueForProject messageInfoFileCellValue = (FileFunctionCellValueForProject)value;
		
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
