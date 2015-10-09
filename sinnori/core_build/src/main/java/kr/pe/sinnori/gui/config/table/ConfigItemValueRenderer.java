package kr.pe.sinnori.gui.config.table;

import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;



@SuppressWarnings("serial")
public class ConfigItemValueRenderer extends JPanel implements TableCellRenderer {
	//private Logger log = LoggerFactory.getLogger(ConfigItemValueRenderer.class);
	
	public ConfigItemValueRenderer() {
		setOpaque(true);
		init();
	}

	private void init() {
		//LayoutManager layoutManager = this.getLayout();
		//log.info("current layout manger class name={}", layoutManager.getClass().getName());
		
		setLayout(new FlowLayout(FlowLayout.LEFT));
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		/*LayoutManager layoutManager = this.getLayout();
		log.info("current layout manger class name={}", layoutManager.getClass().getName());*/
		
		
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
	

}

