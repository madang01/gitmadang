/*
 * Created by JFormDesigner on Thu Dec 25 17:41:56 KST 2014
 */

package kr.pe.sinnori.gui.config.screen;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import kr.pe.sinnori.gui.config.table.ConfigItemKeyRenderer;
import kr.pe.sinnori.gui.config.table.ConfigItemTableModel;
import kr.pe.sinnori.gui.config.table.ConfigItemValueEditor;
import kr.pe.sinnori.gui.config.table.ConfigItemValueRenderer;

import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Jonghoon Won
 */
@SuppressWarnings("serial")
public class DBCPPartConfigPopup extends JDialog {
	private String mainProjectName;
	private String selectedDBCPName;
	private ConfigItemTableModel dbcpPartTableModel;
	
	public DBCPPartConfigPopup(Frame owner,
			String mainProjectName, 
			String selectedDBCPName,
			ConfigItemTableModel dbcpPartTableModel) {
		super(owner);
		initComponents();
		
		this.mainProjectName = mainProjectName;
		this.selectedDBCPName = selectedDBCPName;
		this.dbcpPartTableModel = dbcpPartTableModel;
		
		
		mainProjectNameValueLabel.setText(this.mainProjectName);
		dbcpNameValueLabel.setText(this.selectedDBCPName);
		dbcpPartTable.setModel(this.dbcpPartTableModel);
		
		dbcpPartTable.getColumnModel().getColumn(0).setCellRenderer(new ConfigItemKeyRenderer());
		
		dbcpPartTable.getColumnModel().getColumn(1).setResizable(false);
		dbcpPartTable.getColumnModel().getColumn(1).setPreferredWidth(250);
				
		dbcpPartTable.getColumnModel().getColumn(1).setCellRenderer(new ConfigItemValueRenderer());
		dbcpPartTable.getColumnModel().getColumn(1).setCellEditor(new ConfigItemValueEditor(new JCheckBox()));
		dbcpPartTable.setRowHeight(38);
		dbcpPartScrollPane.repaint();
	}


	private void okButtonActionPerformed(ActionEvent e) {
		// TODO add your code here
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
		dbcpPartIntroductionLable = new JLabel();
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
					dbcpNameTitleLabel.setText("DBCP Name :");
					dbcpNameLinePanel.add(dbcpNameTitleLabel, CC.xy(1, 1));
					dbcpNameLinePanel.add(dbcpNameValueLabel, CC.xy(3, 1));
				}
				contentPanel.add(dbcpNameLinePanel, CC.xy(1, 3));

				//---- dbcpPartIntroductionLable ----
				dbcpPartIntroductionLable.setText("DBCP Part Configuration");
				contentPanel.add(dbcpPartIntroductionLable, CC.xy(1, 5));

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
	private JLabel dbcpPartIntroductionLable;
	private JScrollPane dbcpPartScrollPane;
	private JTable dbcpPartTable;
	private JPanel buttonBar;
	private JButton okButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
