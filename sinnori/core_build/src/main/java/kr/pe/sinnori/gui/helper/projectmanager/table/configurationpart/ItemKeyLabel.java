package kr.pe.sinnori.gui.helper.projectmanager.table.configurationpart;

import javax.swing.JLabel;

@SuppressWarnings("serial")
public class ItemKeyLabel extends JLabel {
	public ItemKeyLabel(String text, String tooltipText) {
		this.setText(text);
		this.setToolTipText("<html>"+tooltipText.replaceAll("\n", "<br/>")+"</html>");
	}
}
