package kr.pe.sinnori.gui.helper.iobuilder.table;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;



@SuppressWarnings("serial")
public class BuildFunctionCellEditor extends DefaultCellEditor {
	public BuildFunctionCellEditor(JCheckBox checkBox) {
		super(checkBox);
	}

	public Component getTableCellEditorComponent(JTable table,
			Object value, boolean isSelected, int row, int column) {		
		BuildFunctionCellValue sourceFileCellValue = (BuildFunctionCellValue)value;
		
		if (isSelected) {
			sourceFileCellValue.setForeground(table.getSelectionForeground());
			sourceFileCellValue.setBackground(table.getSelectionBackground());
		} else {
			sourceFileCellValue.setForeground(table.getForeground());
			sourceFileCellValue.setBackground(table.getBackground());
		}
		
		return sourceFileCellValue;
	}
	
	public Object getCellEditorValue() {
	    return null;
	}

}
