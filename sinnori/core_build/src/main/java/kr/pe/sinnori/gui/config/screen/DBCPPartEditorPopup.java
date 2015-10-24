/*
 * Created by JFormDesigner on Thu Dec 25 17:41:56 KST 2014
 */

package kr.pe.sinnori.gui.config.screen;

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

import kr.pe.sinnori.common.config.itemidinfo.SinnoriItemIDInfoManger;
import kr.pe.sinnori.common.etc.CommonType;
import kr.pe.sinnori.common.exception.SinnoriConfigurationException;
import kr.pe.sinnori.common.util.CommonStaticUtil;
import kr.pe.sinnori.common.util.SequencedProperties;
import kr.pe.sinnori.gui.config.table.ConfigurationPartTableModel;
import kr.pe.sinnori.gui.config.table.ItemKeyRenderer;
import kr.pe.sinnori.gui.config.table.ItemValueEditor;
import kr.pe.sinnori.gui.config.table.ItemValuePanel;
import kr.pe.sinnori.gui.config.table.ItemValueRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author Jonghoon Won
 */
@SuppressWarnings("serial")
public class DBCPPartEditorPopup extends JDialog {
	private Logger log = LoggerFactory.getLogger(DBCPPartEditorPopup.class);
	
	private String mainProjectName;
	private String selectedDBCPName;
	private ConfigurationPartTableModel dbcpPartTableModel;
	private PopupManagerIF popupManager = null;
	
	public DBCPPartEditorPopup(Frame owner,
			String mainProjectName, 
			String selectedDBCPName,
			ConfigurationPartTableModel dbcpPartTableModel,
			int tableModelIndexOfItemHavingBadValue,
			String itemKeyHavingBadValue, PopupManagerIF popupManager) {
		super(owner);
		
		this.popupManager = popupManager;
		
		int maxRow = dbcpPartTableModel.getRowCount();
		if (tableModelIndexOfItemHavingBadValue >= maxRow) {
			String errorMessage = new StringBuilder("the parameter tableModelIndexOfItemHavingBadValue[")
			.append(tableModelIndexOfItemHavingBadValue).append("] is greater than or equals to max row[")
			.append(maxRow).append(" of the paramter dbcpPartTableModel").toString();
			showMessageDialog(errorMessage);	
			this.dispose();
			return;
		}
		
		initComponents();
		
		this.mainProjectName = mainProjectName;
		this.selectedDBCPName = selectedDBCPName;
		this.dbcpPartTableModel = dbcpPartTableModel;
		
		
		mainProjectNameValueLabel.setText(this.mainProjectName);
		dbcpNameValueLabel.setText(this.selectedDBCPName);
		dbcpPartTable.setModel(this.dbcpPartTableModel);
		
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
				CommonStaticUtil.convertToNewLineSplitString(message, 
						CommonType.SPLIT_STRING_GUBUN.NEWLINE, 100));
	}

	private void okButtonActionPerformed(ActionEvent e) {
		SinnoriItemIDInfoManger sinnoriItemIDInfoManger = SinnoriItemIDInfoManger.getInstance();

		SequencedProperties dbcpPartSequencedProperties = popupManager.getNewSequencedPropertiesHavingCommonPartIems();
		if (null == dbcpPartSequencedProperties) {
			showMessageDialog("공통 항목에서 값 오류가 발생하여 더 이상 서브 프로젝트 항목을 수정할 수 없습니다. 자동으로 창이 닫히게 됩니다.");
			this.dispose();
			return;
		}
		
		int maxRow = dbcpPartTableModel.getRowCount();
		log.info("selectedDBCPName={}, maxRow={}", selectedDBCPName, maxRow);
						
		for (int i=0; i < maxRow; i++) {
			Object tableModelSecondColValue = dbcpPartTableModel.getValueAt(i, 1);
			
			if (!(tableModelSecondColValue instanceof ItemValuePanel)) {
				log.error("selectedDBCPName[{}] ItemValuePanel[{}][{}]'s value is not instanc of ItemValuePanel class",
						selectedDBCPName, i, 1);
				System.exit(1);
			}
			 
			ItemValuePanel configItemCellValue = (ItemValuePanel)tableModelSecondColValue;
			String itemKey = configItemCellValue.getItemKey();
			String itemValue = configItemCellValue.getItemValue();
			
			/*log.info("selectedDBCPName={}, row index={}, itemKey={}, itemValue={}", 
					selectedDBCPName, i, itemKey, itemValue);*/
			
			dbcpPartSequencedProperties.put(itemKey, itemValue);
			
			boolean isInactive = true;
			try {
				isInactive = sinnoriItemIDInfoManger.isInactive(itemKey, dbcpPartSequencedProperties);
				
				if (isInactive) {
					sinnoriItemIDInfoManger.getNativeValueAfterValidChecker(itemKey, dbcpPartSequencedProperties);
				}
				
			} catch (IllegalArgumentException | SinnoriConfigurationException e1) {
				log.warn("fail to check validation of item value", e1);
				String errorMessage = e1.getMessage();				
				showMessageDialog(errorMessage);				
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
