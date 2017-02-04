/*
 * Created by JFormDesigner on Sun Nov 30 01:28:48 KST 2014
 */

package kr.pe.sinnori.gui.helper.screen.projectmanager;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import kr.pe.sinnori.common.config.itemidinfo.SinnoriItemIDInfoManger;
import kr.pe.sinnori.common.etc.CommonType;
import kr.pe.sinnori.common.exception.SinnoriConfigurationException;
import kr.pe.sinnori.common.util.CommonStaticUtil;
import kr.pe.sinnori.common.util.SequencedProperties;
import kr.pe.sinnori.gui.helper.screen.projectmanager.table.ConfigurationPartTableModel;
import kr.pe.sinnori.gui.helper.screen.projectmanager.table.ItemKeyRenderer;
import kr.pe.sinnori.gui.helper.screen.projectmanager.table.ItemValueEditor;
import kr.pe.sinnori.gui.helper.screen.projectmanager.table.ItemValuePanel;
import kr.pe.sinnori.gui.helper.screen.projectmanager.table.ItemValueRenderer;

/**
 * @author Jonghoon Won
 */
@SuppressWarnings("serial")
public class SubProjectPartEditorPopup extends JDialog {
	private Logger log = LoggerFactory.getLogger(SubProjectPartEditorPopup.class);
	
	private String mainProjectName;
	private String selectedSubProjectName;	
	private ConfigurationPartTableModel subProjectPartTableModel;
	private SequencedProperties commonPartSequencedProperties = null;
	
	public SubProjectPartEditorPopup(Frame owner,
			String mainProjectName, 
			String selectedSubProjectName,
			ConfigurationPartTableModel subProjectPartTableModel, 
			int tableModelIndexOfItemHavingBadValue, String itemKeyHavingBadValue,
			SequencedProperties commonPartSequencedProperties) {
		super(owner, true);
		
		if (null == mainProjectName) {
			throw new IllegalArgumentException("the paramter mainProjectName is null");
		}
		if (null == selectedSubProjectName) {
			throw new IllegalArgumentException("the paramter selectedSubProjectName is null");
		}
		if (null == subProjectPartTableModel) {
			throw new IllegalArgumentException("the paramter subProjectPartTableModel is null");
		}		
		
		if (tableModelIndexOfItemHavingBadValue >= 0) {
			int maxRow = subProjectPartTableModel.getRowCount();
			if (tableModelIndexOfItemHavingBadValue >= maxRow) {
				String errorMessage = new StringBuilder("the parameter tableModelIndexOfItemHavingBadValue[")
				.append(tableModelIndexOfItemHavingBadValue).append("] is greater than or equals to max row[")
				.append(maxRow).append(" of the variabe subProjectPartConfigTableModel[")
				.append(selectedSubProjectName).append("]").toString();
				throw new IllegalArgumentException(errorMessage);
			}
			
			if (null == itemKeyHavingBadValue) {
				throw new IllegalArgumentException(
		"Any sub project part item value is not valid but the paramter itemKeyHavingBadValue is null");
			}
		}
		
		if (null == commonPartSequencedProperties) {
			throw new IllegalArgumentException("the paramter commonPartSequencedProperties is null");
		}
		
		this.mainProjectName = mainProjectName;
		this.selectedSubProjectName = selectedSubProjectName;
		this.subProjectPartTableModel = subProjectPartTableModel;
		this.commonPartSequencedProperties = commonPartSequencedProperties;
		
		initComponents();
		
		mainProjectNameValueLabel.setText(mainProjectName);
		subProjectNameValueLabel.setText(selectedSubProjectName);
		subProjectPartTable.setModel(subProjectPartTableModel);
				
		subProjectPartTable.getColumnModel().getColumn(0).setCellRenderer(new ItemKeyRenderer());
		
		subProjectPartTable.getColumnModel().getColumn(1).setResizable(false);
		subProjectPartTable.getColumnModel().getColumn(1).setPreferredWidth(250);
				
		subProjectPartTable.getColumnModel().getColumn(1).setCellRenderer(new ItemValueRenderer());
		subProjectPartTable.getColumnModel().getColumn(1).setCellEditor(new ItemValueEditor(new JCheckBox()));
		subProjectPartTable.setRowHeight(38);
		subProjectPartScrollPane.repaint();
		
		if (tableModelIndexOfItemHavingBadValue >= 0) {			
			subProjectPartTable.changeSelection(tableModelIndexOfItemHavingBadValue, 1, false, false);
			subProjectPartTable.editCellAt(tableModelIndexOfItemHavingBadValue, 1);
		}
	}


	private void showMessageDialog(String message) {
		JOptionPane.showMessageDialog(this, 
				CommonStaticUtil.splitString(message, 
						CommonType.SPLIT_STRING_GUBUN.NEWLINE, 100));
	}
	private void okButtonActionPerformed(ActionEvent e) {
		SinnoriItemIDInfoManger sinnoriItemIDInfoManger = SinnoriItemIDInfoManger.getInstance();
		
		SequencedProperties subProjectPartSequencedProperties = new SequencedProperties();
		subProjectPartSequencedProperties.putAll(commonPartSequencedProperties);
		
		int maxRow = subProjectPartTableModel.getRowCount();
		//log.info("selectedSubProjectName={}, maxRow={}", selectedSubProjectName, maxRow);
						
		for (int i=0; i < maxRow; i++) {
			Object tableModelValue = subProjectPartTableModel.getValueAt(i, 1);
			
			if (!(tableModelValue instanceof ItemValuePanel)) {
				log.error("subProjectName[{}] ConfigItemTableModel[{}][{}]'s value is not instanc of ConfigItemValue class",
						selectedSubProjectName, i, 1);
				System.exit(1);
			}
			 
			ItemValuePanel itemValuePanel = (ItemValuePanel)tableModelValue;
			itemValuePanel.setSelected(false);
			itemValuePanel.setToolTipText(null);
			
			String itemID = itemValuePanel.getItemID();
			String prefixOfItemID = itemValuePanel.getPrefixOfItemID();
			String itemKey = itemValuePanel.getItemKey();
			String itemValue = itemValuePanel.getItemValue();
			
			/*log.info("selectedSubProjectName={}, row index={}, itemKey={}, itemValue={}", 
					selectedSubProjectName, i, itemKey, itemValue);
			*/
			subProjectPartSequencedProperties.put(itemKey, itemValue);
			
			boolean isInactive = true;
			try {
				isInactive = sinnoriItemIDInfoManger.isInactive(itemID, prefixOfItemID, subProjectPartSequencedProperties);
				
				if (isInactive) {
					sinnoriItemIDInfoManger.getNativeValueAfterValidChecker(itemKey, subProjectPartSequencedProperties);
				}
				
			} catch (IllegalArgumentException | SinnoriConfigurationException e1) {
				String errorMessage = new StringBuilder("fail to check validation of item[")
				.append(itemKey)
				.append("] value in main project[")
				.append(mainProjectName).append("]'s sub project[")
				.append(selectedSubProjectName)
				.append("] part").toString();
				
				log.warn(errorMessage, e1);
				
				errorMessage = new StringBuilder(errorMessage)
				.append(", errormessage=").append(e1.getMessage()).toString();
				
				showMessageDialog(errorMessage);
				
				itemValuePanel.setSelected(true);
				itemValuePanel.setToolTipText(errorMessage);
				subProjectPartTable.changeSelection(i, 1, false, false);
				subProjectPartTable.editCellAt(i, 1);
				return;
			}
		}
		
		this.dispose();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner non-commercial license
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		mainProjectNameLinePanel = new JPanel();
		mainProjectNameTitleLabel = new JLabel();
		mainProjectNameValueLabel = new JLabel();
		subProjectNameLinePanel = new JPanel();
		subProjectNameTitleLabel = new JLabel();
		subProjectNameValueLabel = new JLabel();
		subprojectPartTitleLable = new JLabel();
		subProjectPartScrollPane = new JScrollPane();
		subProjectPartTable = new JTable();
		buttonBar = new JPanel();
		okButton = new JButton();

		//======== this ========
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPane ========
		{
			dialogPane.setBorder(Borders.createEmptyBorder("7dlu, 7dlu, 7dlu, 7dlu"));
			dialogPane.setLayout(new BorderLayout());

			//======== contentPanel ========
			{
				contentPanel.setLayout(new FormLayout(
					"${growing-button}",
					"12dlu, $lgap, [12dlu,default], $lgap, default, $lgap, 140dlu:grow"));

				//======== mainProjectNameLinePanel ========
				{
					mainProjectNameLinePanel.setLayout(new FormLayout(
						"default, $lcgap, default:grow",
						"default"));

					//---- mainProjectNameTitleLabel ----
					mainProjectNameTitleLabel.setText("\uc8fc \ud504\ub85c\uc81d\ud2b8 \uc774\ub984 :");
					mainProjectNameLinePanel.add(mainProjectNameTitleLabel, CC.xy(1, 1));

					//---- mainProjectNameValueLabel ----
					mainProjectNameValueLabel.setText("sample_test");
					mainProjectNameLinePanel.add(mainProjectNameValueLabel, CC.xy(3, 1));
				}
				contentPanel.add(mainProjectNameLinePanel, CC.xy(1, 1));

				//======== subProjectNameLinePanel ========
				{
					subProjectNameLinePanel.setLayout(new FormLayout(
						"default, $lcgap, default:grow",
						"default"));

					//---- subProjectNameTitleLabel ----
					subProjectNameTitleLabel.setText("\uc11c\ube0c \ud504\ub85c\uc81d\ud2b8 \uc774\ub984 :");
					subProjectNameLinePanel.add(subProjectNameTitleLabel, CC.xy(1, 1));

					//---- subProjectNameValueLabel ----
					subProjectNameValueLabel.setText("sample_test_sub1");
					subProjectNameLinePanel.add(subProjectNameValueLabel, CC.xy(3, 1));
				}
				contentPanel.add(subProjectNameLinePanel, CC.xy(1, 3));

				//---- subprojectPartTitleLable ----
				subprojectPartTitleLable.setText("Subproject Part Editor");
				contentPanel.add(subprojectPartTitleLable, CC.xy(1, 5));

				//======== subProjectPartScrollPane ========
				{

					//---- subProjectPartTable ----
					subProjectPartTable.setModel(new DefaultTableModel(
						new Object[][] {
							{null, null},
							{null, null},
						},
						new String[] {
							"\ud0a4", "\uac12"
						}
					) {
						Class<?>[] columnTypes = new Class<?>[] {
							String.class, Object.class
						};
						@Override
						public Class<?> getColumnClass(int columnIndex) {
							return columnTypes[columnIndex];
						}
					});
					subProjectPartScrollPane.setViewportView(subProjectPartTable);
				}
				contentPanel.add(subProjectPartScrollPane, CC.xy(1, 7));
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);

			//======== buttonBar ========
			{
				buttonBar.setBorder(Borders.createEmptyBorder("5dlu, 0dlu, 0dlu, 0dlu"));
				buttonBar.setLayout(new FormLayout(
					"$glue, $button",
					"pref"));

				//---- okButton ----
				okButton.setText("Close");
				okButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						okButtonActionPerformed(e);
					}
				});
				buttonBar.add(okButton, CC.xy(2, 1));
			}
			dialogPane.add(buttonBar, BorderLayout.SOUTH);
		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner non-commercial license
	private JPanel dialogPane;
	private JPanel contentPanel;
	private JPanel mainProjectNameLinePanel;
	private JLabel mainProjectNameTitleLabel;
	private JLabel mainProjectNameValueLabel;
	private JPanel subProjectNameLinePanel;
	private JLabel subProjectNameTitleLabel;
	private JLabel subProjectNameValueLabel;
	private JLabel subprojectPartTitleLable;
	private JScrollPane subProjectPartScrollPane;
	private JTable subProjectPartTable;
	private JPanel buttonBar;
	private JButton okButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
