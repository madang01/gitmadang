/*
 * Created by JFormDesigner on Thu Dec 25 10:39:38 KST 2014
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
import kr.pe.sinnori.common.exception.ConfigErrorException;
import kr.pe.sinnori.common.util.SequencedProperties;
import kr.pe.sinnori.gui.config.table.ConfigItemTableModel;
import kr.pe.sinnori.gui.config.table.ConfigItemValue;
import kr.pe.sinnori.gui.config.table.ConfigItemValueEditor;
import kr.pe.sinnori.gui.config.table.ConfigItemValueRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author Jonghoon Won
 */
@SuppressWarnings("serial")
public class DBCPNamePopup extends JDialog {
	private Logger log = LoggerFactory.getLogger(DBCPNamePopup.class);
	
	private String mainProjectName;
	private String selectedDBCPName;
	private ConfigItemTableModel dbcpPartConfigTableModel;
	
	/*private String titlesOfConfigItemTable[] = {
			"key", "value"
		};
	
	private Class<?>[] columnTypesOfConfigItemTable = new Class[] {
		String.class, ConfigItemCellValue.class
	};*/
	
	public DBCPNamePopup(Frame owner, 
			String mainProjectName, 
			String selectedDBCPName,
			ConfigItemTableModel dbcpPartConfigTableModel, int rowHavingBadValue, String targetKeyHavingBadValue) {
		super(owner);
		
		this.mainProjectName = mainProjectName;		
		this.selectedDBCPName = selectedDBCPName;
		this.dbcpPartConfigTableModel = dbcpPartConfigTableModel;
		
		int maxRow = dbcpPartConfigTableModel.getRowCount();
		if (rowHavingBadValue >= maxRow) {			
			log.warn("parameter rowHavingBadValue[{}] is greater than maxRow[{}]", rowHavingBadValue, maxRow);
			JOptionPane.showMessageDialog(this, "parameter focusRow is greater than maxRow");	
			this.dispose();
		}
		
		initComponents();
		
		/** Post-Creation Code Start */
		mainProjectNameValueLabel.setText(this.mainProjectName);
		dbcpNameValueLabel.setText(this.selectedDBCPName);
		dbcpNamePartTable.setModel(this.dbcpPartConfigTableModel);
				
		// commonConfigTable.setModel(commonConfigItemTableModel);
		dbcpNamePartTable.getColumnModel().getColumn(1).setResizable(false);
		dbcpNamePartTable.getColumnModel().getColumn(1).setPreferredWidth(250);
				
		dbcpNamePartTable.getColumnModel().getColumn(1).setCellRenderer(new ConfigItemValueRenderer());
		dbcpNamePartTable.getColumnModel().getColumn(1).setCellEditor(new ConfigItemValueEditor(new JCheckBox()));
		dbcpNamePartTable.setRowHeight(38);
		dbcpNamePartScrollPane.repaint();
		if (rowHavingBadValue >= 0) {			
			dbcpNamePartTable.changeSelection(rowHavingBadValue, 1, false, false);
			dbcpNamePartTable.editCellAt(rowHavingBadValue, 1);
			JOptionPane.showMessageDialog(this, new StringBuilder("Please check the value of key[")
			.append(targetKeyHavingBadValue).append("]").toString());
		}		
		/** Post-Creation Code End */
	}

	private void okButtonActionPerformed(ActionEvent e) {
		SinnoriItemIDInfoManger sinnoriItemIDInfoManger = SinnoriItemIDInfoManger.getInstance();
		
		SequencedProperties newSinnoriConfig = new SequencedProperties();
		int maxRow = dbcpPartConfigTableModel.getRowCount();
		log.info("dbcpName={}, maxRow={}", selectedDBCPName, maxRow);
						
		for (int i=0; i < maxRow; i++) {
			Object tableModelValue = dbcpPartConfigTableModel.getValueAt(i, 1);
			
			if (!(tableModelValue instanceof ConfigItemValue)) {
				log.error("dbcpName[{}] ConfigItemTableModel[{}][{}]'s value is not instanc of ConfigItemValue class",
						selectedDBCPName, i, 1);
				System.exit(1);
			}
			 
			ConfigItemValue configItemCellValue = (ConfigItemValue)tableModelValue;
			String targetKey = configItemCellValue.getTargetKey();
			String targetValue = configItemCellValue.getValueOfComponent();
			
			log.info("dbcpName={}, row index={}, targetKey={}, targetValue={}", 
					selectedDBCPName, i, targetKey, targetValue);
			
			newSinnoriConfig.put(targetKey, targetValue);
			
			boolean isSkip = true;
			try {
				isSkip = sinnoriItemIDInfoManger.isInactive(targetKey, newSinnoriConfig);
				
				if (!isSkip) {
					sinnoriItemIDInfoManger.getNativeValueAfterValidChecker(targetKey, newSinnoriConfig);
				}
				
			} catch (ConfigErrorException e1) {
				String errorMessage = e1.getMessage();
				log.warn("targetKey={}, errormessage={}", targetKey, errorMessage);
				
				dbcpNamePartTable.changeSelection(i, 1, false, false);
				dbcpNamePartTable.editCellAt(i, 1);
				
				JOptionPane.showMessageDialog(this, 
						new StringBuilder("Please check the value of key[")
				.append(targetKey).append("]").toString());
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
		/** Post-Creation Code Start */
		dbcpNameValueLabel.setText(selectedDBCPName);
		/** Post-Creation Code End */
		dbcpPartIntroductionLable = new JLabel();
		dbcpNamePartScrollPane = new JScrollPane();
		dbcpNamePartTable = new JTable();
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
					"394dlu:grow",
					"3*(default, $lgap), default"));

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
					dbcpNameTitleLabel.setText("DBCP Connection Pool Name :");
					dbcpNameLinePanel.add(dbcpNameTitleLabel, CC.xy(1, 1));
					dbcpNameLinePanel.add(dbcpNameValueLabel, CC.xy(3, 1));
				}
				contentPanel.add(dbcpNameLinePanel, CC.xy(1, 3));

				//---- dbcpPartIntroductionLable ----
				dbcpPartIntroductionLable.setText("DBCP Part Config");
				contentPanel.add(dbcpPartIntroductionLable, CC.xy(1, 5));

				//======== dbcpNamePartScrollPane ========
				{

					//---- dbcpNamePartTable ----
					dbcpNamePartTable.setModel(new DefaultTableModel(
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
						TableColumnModel cm = dbcpNamePartTable.getColumnModel();
						cm.getColumn(1).setMinWidth(150);
					}
					dbcpNamePartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					dbcpNamePartTable.setAutoCreateColumnsFromModel(false);
					dbcpNamePartScrollPane.setViewportView(dbcpNamePartTable);
				}
				contentPanel.add(dbcpNamePartScrollPane, CC.xy(1, 7));
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
	private JLabel dbcpPartIntroductionLable;
	private JScrollPane dbcpNamePartScrollPane;
	private JTable dbcpNamePartTable;
	private JPanel buttonBar;
	private JButton okButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
