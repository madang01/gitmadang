package kr.pe.sinnori.gui.config.table;

import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

@SuppressWarnings("serial")
public class ConfigItemKeyRenderer extends JPanel implements TableCellRenderer {
	//private Logger log = LoggerFactory.getLogger(ConfigItemKeyRenderer.class);
	
	public ConfigItemKeyRenderer() {
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
		
		//log.info("value class name=[{}]", value.getClass().getName());
		
		ConfigItemKey configItemKey = (ConfigItemKey)value;
		
        return configItemKey;
	}
	

}
