package kr.pe.sinnori.gui.table;

import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class SourceBuilderTableModel extends DefaultTableModel {
	private Object values[][] = null;
	private String titles[] = null;	
	private Class<?>[] columnTypes = null;
	
	public SourceBuilderTableModel(Object values[][], String titles[], Class<?>[] columnTypes) {
		super(values, titles);
		this.values = values;
		this.titles = titles;
		this.columnTypes = columnTypes;
		// this.setDataVector(values, titles);
	}
	
	public int getRowCount() {
		if (null == values) return 0;
		return values.length; 
	}
	
	public int getColumnCount() {
		return titles.length;
	}
	public Class<?> getColumnClass(int columnIndex) {
		return columnTypes[columnIndex];
	}
	public String getColumnName(int columnIndex) {
		return titles[columnIndex];
	}
	public Object getValueAt(int row, int column) {
		
		return values[row][column];
	}
	
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		System.out.printf("SourceBuilderTableModel::columnIndex=[%d], rowIndex=[%d]", columnIndex, rowIndex);
		System.out.println();
		
		if (columnIndex >= 2) return true;
		else return false; 
	}
	
	public void setValueAt(Object aValue, int row, int col) {
		if (null == aValue) return;
		
		values[row][col] = aValue;
        fireTableCellUpdated(row, col);
	}
}
