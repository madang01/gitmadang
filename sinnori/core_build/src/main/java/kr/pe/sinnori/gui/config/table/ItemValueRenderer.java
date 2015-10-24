package kr.pe.sinnori.gui.config.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;



@SuppressWarnings("serial")
public class ItemValueRenderer extends JPanel implements TableCellRenderer {
	//private Logger log = LoggerFactory.getLogger(ConfigItemValueRenderer.class);
	
	public ItemValueRenderer() {
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
	

}

