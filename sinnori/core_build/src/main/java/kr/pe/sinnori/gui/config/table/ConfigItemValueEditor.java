package kr.pe.sinnori.gui.config.table;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;



@SuppressWarnings("serial")
public class ConfigItemValueEditor extends DefaultCellEditor {
	public ConfigItemValueEditor(JCheckBox checkBox) {
		super(checkBox);
	}

	public Component getTableCellEditorComponent(JTable table,
			Object value, boolean isSelected, int row, int column) {		
		ConfigItemValue configItemCellValue = (ConfigItemValue)value;
			
		
		if (isSelected) {
			configItemCellValue.setForeground(table.getSelectionForeground());
			configItemCellValue.setBackground(table.getSelectionBackground());
		} else {
			configItemCellValue.setForeground(table.getForeground());
			configItemCellValue.setBackground(table.getBackground());
		}
		
		return configItemCellValue;
	}
	
	public Object getCellEditorValue() {
	    return null;
	}

}
