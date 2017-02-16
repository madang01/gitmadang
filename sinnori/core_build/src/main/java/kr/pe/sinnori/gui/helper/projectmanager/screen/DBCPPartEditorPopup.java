/*
 * Created by JFormDesigner on Thu Dec 25 17:41:56 KST 2014
 */

package kr.pe.sinnori.gui.helper.projectmanager.screen;

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
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

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
import kr.pe.sinnori.gui.helper.projectmanager.table.configurationpart.ConfigurationPartTableModel;
import kr.pe.sinnori.gui.helper.projectmanager.table.configurationpart.ItemKeyRenderer;
import kr.pe.sinnori.gui.helper.projectmanager.table.configurationpart.ItemValueEditor;
import kr.pe.sinnori.gui.helper.projectmanager.table.configurationpart.ItemValuePanel;
import kr.pe.sinnori.gui.helper.projectmanager.table.configurationpart.ItemValueRenderer;

/**
 * @author Jonghoon Won
 */
@SuppressWarnings("serial")
public class DBCPPartEditorPopup extends JDialog {
	private Logger log = LoggerFactory.getLogger(DBCPPartEditorPopup.class);
	
	private String mainProjectName;
	private String selectedDBCPName;
	private ConfigurationPartTableModel dbcpPartTableModel;
	private SequencedProperties commonPartSequencedProperties = null;
	
	public DBCPPartEditorPopup(Frame owner,
			String mainProjectName, 
			String selectedDBCPName,
			ConfigurationPartTableModel dbcpPartTableModel,
			int tableModelIndexOfItemHavingBadValue,
			String itemKeyHavingBadValue, 
			SequencedProperties commonPartSequencedProperties) {
		super(owner);
		
		if (null == mainProjectName) {
			throw new IllegalArgumentException("the paramter mainProjectName is null");
		}
		if (null == selectedDBCPName) {
			throw new IllegalArgumentException("the paramter selectedDBCPName is null");
		}
		if (null == dbcpPartTableModel) {
			throw new IllegalArgumentException("the paramter dbcpPartTableModel is null");
		}
		
		if (tableModelIndexOfItemHavingBadValue >= 0) {
			int maxRow = dbcpPartTableModel.getRowCount();
			if (tableModelIndexOfItemHavingBadValue >= maxRow) {
				String errorMessage = new StringBuilder("the parameter tableModelIndexOfItemHavingBadValue[")
				.append(tableModelIndexOfItemHavingBadValue).append("] is greater than or equals to max row[")
				.append(maxRow).append(" of the variabe dbcpPartTableModel[")
				.append(selectedDBCPName).append("]").toString();
				throw new IllegalArgumentException(errorMessage);
			}
			
			if (null == itemKeyHavingBadValue) {
				throw new IllegalArgumentException(
		"Any dbcp part item value is not valid but the paramter itemKeyHavingBadValue is null");
			}
		}
		
		if (null == commonPartSequencedProperties) {
			throw new IllegalArgumentException("the paramter commonPartSequencedProperties is null");
		}
		
		this.mainProjectName = mainProjectName;
		this.selectedDBCPName = selectedDBCPName;
		this.dbcpPartTableModel = dbcpPartTableModel;
		this.commonPartSequencedProperties = commonPartSequencedProperties;
		
		initComponents();		
		
		mainProjectNameValueLabel.setText(mainProjectName);
		dbcpNameValueLabel.setText(selectedDBCPName);
		dbcpPartTable.setModel(dbcpPartTableModel);
		
		dbcpPartTable.getColumnModel().getColumn(0).setCellRenderer(new ItemKeyRenderer());
		
		dbcpPartTable.getColumnModel().getColumn(1).setResizable(false);
		dbcpPartTable.getColumnModel().getColumn(1).setPreferredWidth(250);
				
		dbcpPartTable.getColumnModel().getColumn(1).setCellRenderer(new ItemValueRenderer());
		dbcpPartTable.getColumnModel().getColumn(1).setCellEditor(new ItemValueEditor(new JCheckBox()));
		dbcpPartTable.setRowHeight(38);
		dbcpPartScrollPane.repaint();
		
		if (tableModelIndexOfItemHavingBadValue >= 0) {			
			dbcpPartTable.changeSelection(tableModelIndexOfItemHavingBadValue, 1, false, false);
			dbcpPartTable.editCellAt(tableModelIndexOfItemHavingBadValue, 1);
		}
	}

	private void showMessageDialog(String message) {
		JOptionPane.showMessageDialog(this, 
				CommonStaticUtil.splitString(message, 
						CommonType.LINE_SEPARATOR_GUBUN.NEWLINE, 100));
	}

	private void okButtonActionPerformed(ActionEvent e) {
		SinnoriItemIDInfoManger sinnoriItemIDInfoManger = SinnoriItemIDInfoManger.getInstance();

		SequencedProperties dbcpPartSequencedProperties = new SequencedProperties();
		dbcpPartSequencedProperties.putAll(commonPartSequencedProperties);
		
		int maxRow = dbcpPartTableModel.getRowCount();
		//log.info("selectedDBCPName={}, maxRow={}", selectedDBCPName, maxRow);
						
		for (int i=0; i < maxRow; i++) {
			Object tableModelSecondColValue = dbcpPartTableModel.getValueAt(i, 1);
			
			if (!(tableModelSecondColValue instanceof ItemValuePanel)) {
				log.error("selectedDBCPName[{}] ItemValuePanel[{}][{}]'s value is not instanc of ItemValuePanel class",
						selectedDBCPName, i, 1);
				System.exit(1);
			}
			 
			ItemValuePanel itemValuePanel = (ItemValuePanel)tableModelSecondColValue;
			itemValuePanel.setSelected(false);
			itemValuePanel.setToolTipText(null);
			
			String itemID = itemValuePanel.getItemID();
			String prefixOfItemID = itemValuePanel.getPrefixOfItemID();
			String itemKey = itemValuePanel.getItemKey();
			String itemValue = itemValuePanel.getItemValue();
			
			/*log.info("selectedDBCPName={}, row index={}, itemKey={}, itemValue={}", 
					selectedDBCPName, i, itemKey, itemValue);*/
			
			dbcpPartSequencedProperties.put(itemKey, itemValue);
			
			boolean isInactive = true;
			try {
				isInactive = sinnoriItemIDInfoManger.isInactive(itemID, prefixOfItemID, dbcpPartSequencedProperties);
				
				if (isInactive) {
					sinnoriItemIDInfoManger.getNativeValueAfterValidChecker(itemKey, dbcpPartSequencedProperties);
				}
				
			} catch (IllegalArgumentException | SinnoriConfigurationException e1) {
				String errorMessage = new StringBuilder("fail to check validation of item[")
				.append(itemKey)
				.append("] value in main project[")
				.append(mainProjectName).append("]'s dbcp[")
				.append(selectedDBCPName)
				.append("] part").toString();
				
				log.warn(errorMessage, e1);
				
				errorMessage = new StringBuilder(errorMessage)
				.append(", errormessage=").append(e1.getMessage()).toString();
				
				showMessageDialog(errorMessage);
				
				itemValuePanel.setSelected(true);
				itemValuePanel.setToolTipText(errorMessage);
				
				dbcpPartTable.changeSelection(i, 1, false, false);
				dbcpPartTable.editCellAt(i, 1);
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
		mainProjectLinePanel = new JPanel();
		mainProjectNameTitleLabel = new JLabel();
		mainProjectNameValueLabel = new JLabel();
		dbcpNameLinePanel = new JPanel();
		dbcpNameTitleLabel = new JLabel();
		dbcpNameValueLabel = new JLabel();
		dbcpPartTitleLable = new JLabel();
		dbcpPartScrollPane = new JScrollPane();
		dbcpPartTable = new JTable();
		buttonBar = new JPanel();
		okButton = new JButton();

		//======== this ========
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		/** Post Initialization code start */
		dbcpNameValueLabel.setText(selectedDBCPName);
		/** Post Initialization code end */

		//======== dialogPane ========
		{
			dialogPane.setBorder(Borders.createEmptyBorder("7dlu, 7dlu, 7dlu, 7dlu"));
			dialogPane.setLayout(new BorderLayout());

			//======== contentPanel ========
			{
				contentPanel.setLayout(new FormLayout(
					"394dlu:grow",
					"3*(default, $lgap), 36dlu"));

				//======== mainProjectLinePanel ========
				{
					mainProjectLinePanel.setLayout(new FormLayout(
						"110dlu, $lcgap, 168dlu",
						"default"));

					//---- mainProjectNameTitleLabel ----
					mainProjectNameTitleLabel.setText("Main Project Name :");
					mainProjectLinePanel.add(mainProjectNameTitleLabel, CC.xy(1, 1));
					mainProjectLinePanel.add(mainProjectNameValueLabel, CC.xy(3, 1));
				}
				contentPanel.add(mainProjectLinePanel, CC.xy(1, 1));

				//======== dbcpNameLinePanel ========
				{
					dbcpNameLinePanel.setLayout(new FormLayout(
						"110dlu, $lcgap, 200dlu",
						"default"));

					//---- dbcpNameTitleLabel ----
					dbcpNameTitleLabel.setText("DBCP Name :");
					dbcpNameLinePanel.add(dbcpNameTitleLabel, CC.xy(1, 1));
					dbcpNameLinePanel.add(dbcpNameValueLabel, CC.xy(3, 1));
				}
				contentPanel.add(dbcpNameLinePanel, CC.xy(1, 3));

				//---- dbcpPartTitleLable ----
				dbcpPartTitleLable.setText("DBCP Part Editor");
				contentPanel.add(dbcpPartTitleLable, CC.xy(1, 5));

				//======== dbcpPartScrollPane ========
				{

					//---- dbcpPartTable ----
					dbcpPartTable.setModel(new DefaultTableModel(
						new Object[][] {
							{null, null},
						},
						new String[] {
							"key", "value"
						}
					) {
						Class<?>[] columnTypes = new Class<?>[] {
							String.class, Object.class
						};
						boolean[] columnEditable = new boolean[] {
							false, false
						};
						@Override
						public Class<?> getColumnClass(int columnIndex) {
							return columnTypes[columnIndex];
						}
						@Override
						public boolean isCellEditable(int rowIndex, int columnIndex) {
							return columnEditable[columnIndex];
						}
					});
					{
						TableColumnModel cm = dbcpPartTable.getColumnModel();
						cm.getColumn(1).setMinWidth(150);
					}
					dbcpPartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					dbcpPartTable.setAutoCreateColumnsFromModel(false);
					dbcpPartScrollPane.setViewportView(dbcpPartTable);
				}
				contentPanel.add(dbcpPartScrollPane, CC.xy(1, 7));
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
	private JPanel mainProjectLinePanel;
	private JLabel mainProjectNameTitleLabel;
	private JLabel mainProjectNameValueLabel;
	private JPanel dbcpNameLinePanel;
	private JLabel dbcpNameTitleLabel;
	private JLabel dbcpNameValueLabel;
	private JLabel dbcpPartTitleLable;
	private JScrollPane dbcpPartScrollPane;
	private JTable dbcpPartTable;
	private JPanel buttonBar;
	private JButton okButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
