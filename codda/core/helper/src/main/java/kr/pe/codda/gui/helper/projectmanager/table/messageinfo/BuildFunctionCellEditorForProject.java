package kr.pe.codda.gui.helper.projectmanager.table.messageinfo;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;



@SuppressWarnings("serial")
public class BuildFunctionCellEditorForProject extends DefaultCellEditor {
	public BuildFunctionCellEditorForProject(JCheckBox checkBox) {
		super(checkBox);
	}

	public Component getTableCellEditorComponent(JTable table,
			Object value, boolean isSelected, int row, int column) {		
		BuildFunctionCellValueForProject sourceFileCellValue = (BuildFunctionCellValueForProject)value;
		
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
