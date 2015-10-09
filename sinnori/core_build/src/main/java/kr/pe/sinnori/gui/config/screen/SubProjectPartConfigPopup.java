/*
 * Created by JFormDesigner on Sun Nov 30 01:28:48 KST 2014
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
import javax.swing.table.DefaultTableModel;

import kr.pe.sinnori.common.config.itemidinfo.SinnoriItemIDInfoManger;
import kr.pe.sinnori.common.exception.ConfigErrorException;
import kr.pe.sinnori.common.util.SequencedProperties;
import kr.pe.sinnori.gui.config.table.ConfigItemKeyRenderer;
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
public class SubProjectPartConfigPopup extends JDialog {
	private Logger log = LoggerFactory.getLogger(SubProjectPartConfigPopup.class);
	
	private String mainProjectName;
	private String selectedSubProjectName;	
	private ConfigItemTableModel subProjectPartTableModel;
	
	public SubProjectPartConfigPopup(Frame owner,
			String mainProjectName, 
			String selectedSubProjectName,
			ConfigItemTableModel subProjectPartTableModel, int rowHavingBadValue, String targetKeyHavingBadValue) {
		super(owner, true);
		
		int maxRow = subProjectPartTableModel.getRowCount();
		if (rowHavingBadValue >= maxRow) {			
			log.warn("parameter rowHavingBadValue[{}] is greater than maxRow[{}]", rowHavingBadValue, maxRow);
			JOptionPane.showMessageDialog(this, "parameter focusRow is greater than maxRow");	
			this.dispose();
		}
		
		initComponents();
		
		this.mainProjectName = mainProjectName;
		this.selectedSubProjectName = selectedSubProjectName;
		this.subProjectPartTableModel = subProjectPartTableModel;
		
		/** Post-Creation Code Start */
		mainProjectNameValueLabel.setText(this.mainProjectName);
		subProjectNameValueLabel.setText(this.selectedSubProjectName);
		subProjectPartTable.setModel(this.subProjectPartTableModel);
				
		// commonConfigTable.setModel(commonConfigItemTableModel);
		subProjectPartTable.getColumnModel().getColumn(0).setCellRenderer(new ConfigItemKeyRenderer());
		
		subProjectPartTable.getColumnModel().getColumn(1).setResizable(false);
		subProjectPartTable.getColumnModel().getColumn(1).setPreferredWidth(250);
				
		subProjectPartTable.getColumnModel().getColumn(1).setCellRenderer(new ConfigItemValueRenderer());
		subProjectPartTable.getColumnModel().getColumn(1).setCellEditor(new ConfigItemValueEditor(new JCheckBox()));
		subProjectPartTable.setRowHeight(38);
		subProjectPartScrollPane.repaint();
		
		if (rowHavingBadValue >= 0) {			
			subProjectPartTable.changeSelection(rowHavingBadValue, 1, false, false);
			subProjectPartTable.editCellAt(rowHavingBadValue, 1);
			JOptionPane.showMessageDialog(this, new StringBuilder("Please check the value of key[")
			.append(targetKeyHavingBadValue).append("]").toString());
		}
		/** Post-Creation Code End */
	}


	private void okButtonActionPerformed(ActionEvent e) {
		SinnoriItemIDInfoManger sinnoriItemIDInfoManger = SinnoriItemIDInfoManger.getInstance();
		
		SequencedProperties newSinnoriConfig = new SequencedProperties();
		int maxRow = subProjectPartTableModel.getRowCount();
		log.info("selectedSubProjectName={}, maxRow={}", selectedSubProjectName, maxRow);
						
		for (int i=0; i < maxRow; i++) {
			Object tableModelValue = subProjectPartTableModel.getValueAt(i, 1);
			
			if (!(tableModelValue instanceof ConfigItemValue)) {
				log.error("subProjectName[{}] ConfigItemTableModel[{}][{}]'s value is not instanc of ConfigItemValue class",
						selectedSubProjectName, i, 1);
				System.exit(1);
			}
			 
			ConfigItemValue configItemCellValue = (ConfigItemValue)tableModelValue;
			String targetKey = configItemCellValue.getTargetKey();
			String targetValue = configItemCellValue.getValueOfComponent();
			
			log.info("selectedSubProjectName={}, row index={}, targetKey={}, targetValue={}", 
					selectedSubProjectName, i, targetKey, targetValue);
			
			newSinnoriConfig.put(targetKey, targetValue);
			
			boolean isValidation = true;
			try {
				isValidation = sinnoriItemIDInfoManger.isInactive(targetKey, newSinnoriConfig);
				
				if (isValidation) {
					sinnoriItemIDInfoManger.getNativeValueAfterValidChecker(targetKey, newSinnoriConfig);
				}
				
			} catch (ConfigErrorException e1) {
				String errorMessage = e1.getMessage();
				log.warn("targetKey={}, errormessage={}", targetKey, errorMessage);
				
				subProjectPartTable.changeSelection(i, 1, false, false);
				subProjectPartTable.editCellAt(i, 1);
				
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
		mainProjectNameLinePanel = new JPanel();
		mainProjectNameTitleLabel = new JLabel();
		mainProjectNameValueLabel = new JLabel();
		subProjectNameLinePanel = new JPanel();
		subProjectNameTitleLabel = new JLabel();
		subProjectNameValueLabel = new JLabel();
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
					"2*(default, $lgap), 90dlu:grow"));

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
				contentPanel.add(subProjectPartScrollPane, CC.xy(1, 5));
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
	private JScrollPane subProjectPartScrollPane;
	private JTable subProjectPartTable;
	private JPanel buttonBar;
	private JButton okButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
