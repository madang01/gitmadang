package kr.pe.sinnori.gui.table;

import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;



@SuppressWarnings("serial")
public class SourceFileCellRenderer extends JPanel implements TableCellRenderer {	
	public SourceFileCellRenderer() {
		setOpaque(true);
		init();
	}

	private void init() {
		setLayout(new FlowLayout(FlowLayout.CENTER));
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		/*System.out.printf("SourceFileCellRenderer::row=[%d], column=[%d], isSelected=[%s]", row, column, isSelected);
		System.out.println();
		
		if (null != value) {
			System.out.printf("SourceFileCellRenderer::value=[%s]", value.toString());
			System.out.println();
		} else {
			System.out.println("SourceFileCellRenderer::value is null");
		}*/
		
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
	

}

