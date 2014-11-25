package kr.pe.sinnori.gui.table;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;



@SuppressWarnings("serial")
public class SourceFileCellEditor extends DefaultCellEditor {
	public SourceFileCellEditor(JCheckBox checkBox) {
		super(checkBox);
	}

	public Component getTableCellEditorComponent(JTable table,
			Object value, boolean isSelected, int row, int column) {
		System.out.println();
		System.out.printf("SourceFileCellEditor::row=[%d], column=[%d], isSelected=[%s]", row, column, isSelected);
		System.out.println();
		
		if (null != value) {
			System.out.printf("SourceFileCellEditor::value=[%s]", value.toString());
			System.out.println();
		} else {
			System.out.println("SourceFileCellEditor::value is null");
		}
		
		SourceFileCellValue sourceFileCellValue = (SourceFileCellValue)value;
		
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
		System.out.println("call getCellEditorValue in SourceFileCellEditor");
	    return null;
	}

}
