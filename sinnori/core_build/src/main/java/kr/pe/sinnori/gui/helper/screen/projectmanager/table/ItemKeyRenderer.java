package kr.pe.sinnori.gui.helper.screen.projectmanager.table;

import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

@SuppressWarnings("serial")
public class ItemKeyRenderer extends JPanel implements TableCellRenderer {
	//private Logger log = LoggerFactory.getLogger(ConfigItemKeyRenderer.class);
	
	public ItemKeyRenderer() {
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
		
		ItemKeyLabel configItemKey = (ItemKeyLabel)value;
		
        return configItemKey;
	}
	

}
