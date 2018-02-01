package kr.pe.sinnori.gui.helper.projectmanager.table.configurationpart;

import java.awt.Dimension;
import java.awt.Frame;
import java.util.Iterator;
import java.util.Set;

import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import kr.pe.sinnori.common.config.itemidinfo.ItemIDInfo;
import kr.pe.sinnori.common.config.itemidinfo.ItemIDInfo.ViewType;
import kr.pe.sinnori.common.type.LineSeparatorType;
import kr.pe.sinnori.common.util.CommonStaticUtil;
import kr.pe.sinnori.gui.util.PathSwingAction;



@SuppressWarnings("serial")
public class ItemValuePanel extends JPanel {
	// private Logger log = LoggerFactory.getLogger(ItemValuePanel.class);
	private int indexOfTableModel;
	private String itemID;
	private String prefixOfItemID;
	private String itemKey;
	
	private String itemDescriptionKey = null;
	private String itemDescriptionValue = null;
	
	private ItemIDInfo.ViewType itemViewType = null;
	private Set<String> itemSet = null;
	private Frame mainFrame = null;
	
	private JComboBox<String> valueComboBox = null;
	private JTextField valueTextField = null;
	
	private JButton pathButton = null;
	private boolean isSelected = false;
	
	public ItemValuePanel(int indexOfTableModel, 
			String itemID, String prefixOfItemID,
			String itemKey, 	String itemValue, 
			String itemDescriptionKey, String itemDescriptionValue,
			ItemIDInfo.ViewType itemViewType, 
			Set<String> itemSet, Frame mainFrame) {
		this.indexOfTableModel = indexOfTableModel;
		this.itemID = itemID;
		this.prefixOfItemID = prefixOfItemID;
		this.itemKey = itemKey;
		this.itemDescriptionKey = itemDescriptionKey;
		this.itemDescriptionValue = itemDescriptionValue;
		this.itemSet = itemSet;
		this.mainFrame = mainFrame;
		this.itemViewType = itemViewType;
						
		if (itemViewType == ItemIDInfo.ViewType.FILE) {
			valueTextField = new JTextField();
			valueTextField.setText(itemValue);
			valueTextField.setPreferredSize(new Dimension(310,20));
			add(valueTextField);
			
			pathButton = new JButton("File");
			
			UIManager.put("FileChooser.readOnly", Boolean.TRUE);
			JFileChooser chooser = new JFileChooser();
			chooser.setMultiSelectionEnabled(false);
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			
			PathSwingAction pathAction = new PathSwingAction(this.mainFrame, chooser, pathButton.getText(), valueTextField);
			pathButton.setAction(pathAction);
			add(pathButton);
		} else if (itemViewType == ItemIDInfo.ViewType.PATH) {
			valueTextField = new JTextField();
			valueTextField.setText(itemValue);
			valueTextField.setPreferredSize(new Dimension(310,20));
			add(valueTextField);
			
			pathButton = new JButton("Path");
			
			UIManager.put("FileChooser.readOnly", Boolean.TRUE); 
			JFileChooser chooser = new JFileChooser();
			chooser.setMultiSelectionEnabled(false);
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);			
			
			PathSwingAction pathAction = new PathSwingAction(this.mainFrame, chooser, pathButton.getText(), valueTextField);
			pathButton.setAction(pathAction);
			add(pathButton);		
		} else if (itemViewType == ViewType.SINGLE_SET) {
			int selectedIndex = -1;
			
			valueComboBox = new JComboBox<String>();
			
			Iterator<String> iter = itemSet.iterator();
			while (iter.hasNext()) {
				String itemValueOfSet = iter.next();
				valueComboBox.addItem(itemValueOfSet);
				if (itemValueOfSet.equals(itemValue)) {
					selectedIndex= valueComboBox.getComponentCount() - 1;
				}				
			}
			valueComboBox.setSelectedIndex(0);
			add(valueComboBox);			
			
			if (-1 != selectedIndex) {
				valueComboBox.setSelectedIndex(selectedIndex);
			} else {
				showMessageDialog(new StringBuilder("the paramter itemValue[")
				.append(itemKey)
				.append("][")
				.append(itemValue)
				.append("] is not element of set")
				.append(itemSet.toString()).toString());
				
				valueComboBox.setSelectedIndex(0);
			}
			
		} else {
			valueTextField = new JTextField();
			valueTextField.setPreferredSize(new Dimension(400,20));
			valueTextField.setText(itemValue);
			add(valueTextField);
		}
		
	}
	
	private void showMessageDialog(String message) {
		JOptionPane.showMessageDialog(mainFrame, 
				CommonStaticUtil.splitString(message, 
						LineSeparatorType.NEWLINE, 100));
	}
	
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	
	public boolean isSelected() {
		return isSelected;
	}
	
	public void setItemValue(String itemValue) {
		if (itemViewType == ItemIDInfo.ViewType.FILE) {
			valueTextField.setText(itemValue);
		} else if (itemViewType == ItemIDInfo.ViewType.PATH) {
			valueTextField.setText(itemValue);		
		} else if (itemViewType == ViewType.SINGLE_SET) {
			int selectedIndex = -1;
			
			ComboBoxModel<String> comboBoxModel = valueComboBox.getModel();
			int size = comboBoxModel.getSize();
			for (int i=0; i < size; i++) {
				String one = comboBoxModel.getElementAt(i);
				if (one.equals(itemValue)) {
					selectedIndex =  i;
					break;
				}
			}
			
			if (-1 != selectedIndex) {
				valueComboBox.setSelectedIndex(selectedIndex);
			} else {
				showMessageDialog(new StringBuilder("the paramter itemValue[")
				.append(itemKey)
				.append("][")
				.append(itemValue)
				.append("] is not element of set")
				.append(itemSet.toString()).toString());
				
				valueComboBox.setSelectedIndex(0);
			}
			
		} else {
			valueTextField.setText(itemValue);	
		}
	}

	public int getIndexOfTableModel() {
		return indexOfTableModel;
	}
	public String getItemKey() {
		return itemKey;
	}
	

	public String getItemID() {
		return itemID;
	}

	public String getPrefixOfItemID() {
		return prefixOfItemID;
	}

	public String getItemValue() {
		if (itemViewType == ViewType.SINGLE_SET) {
			return valueComboBox.getItemAt(valueComboBox.getSelectedIndex());
		} else {
			return valueTextField.getText();
		}		
	}

	public String getItemDescriptionKey() {
		return itemDescriptionKey;
	}

	public String getItemDescriptionValue() {
		return itemDescriptionValue;
	}
}
