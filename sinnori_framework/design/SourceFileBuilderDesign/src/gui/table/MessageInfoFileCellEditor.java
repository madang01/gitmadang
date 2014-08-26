package gui.table;

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
		System.out.println();
		System.out.printf("MessageInfoFileCellEditor::row=[%d], column=[%d], isSelected=[%s]", row, column, isSelected);
		System.out.println();
		
		if (null != value) {
			System.out.printf("MessageInfoFileCellEditor::value=[%s]", value.toString());
			System.out.println();
		} else {
			System.out.println("MessageInfoFileCellEditor::value is null");
		}
		
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
		System.out.println("call getCellEditorValue in MessageInfoFileCellEditor");
	    return null;
	}
}
