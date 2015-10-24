package kr.pe.sinnori.gui.config.table;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;



@SuppressWarnings("serial")
public class ItemValueEditor extends DefaultCellEditor {
	public ItemValueEditor(JCheckBox checkBox) {
		super(checkBox);
	}

	public Component getTableCellEditorComponent(JTable table,
			Object value, boolean isSelected, int row, int column) {		
		ItemValuePanel itemValuePanel = (ItemValuePanel)value;
			
		if (itemValuePanel.isSelected()) {
			itemValuePanel.setForeground(table.getForeground());
			itemValuePanel.setBackground(Color.RED);
		} else {
			if (isSelected) {
				itemValuePanel.setForeground(table.getSelectionForeground());
				itemValuePanel.setBackground(table.getSelectionBackground());
			} else {
				itemValuePanel.setForeground(table.getForeground());
				itemValuePanel.setBackground(table.getBackground());
			}
		}	
		
		
		return itemValuePanel;
	}
	
	public Object getCellEditorValue() {
	    return null;
	}

}
