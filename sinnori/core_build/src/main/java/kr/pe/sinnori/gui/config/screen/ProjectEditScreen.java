/*
 * Created by JFormDesigner on Sat Nov 29 13:48:34 KST 2014
 */

package kr.pe.sinnori.gui.config.screen;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import kr.pe.sinnori.common.config.BuildSystemPathSupporter;
import kr.pe.sinnori.common.config.itemidinfo.ItemIDInfo;
import kr.pe.sinnori.common.config.itemidinfo.SinnoriItemIDInfoManger;
import kr.pe.sinnori.common.exception.ConfigErrorException;
import kr.pe.sinnori.common.util.SequencedProperties;
import kr.pe.sinnori.gui.config.buildsystem.MainProjectInformation;
import kr.pe.sinnori.gui.config.lib.WindowManger;
import kr.pe.sinnori.gui.config.table.ConfigItemKey;
import kr.pe.sinnori.gui.config.table.ConfigItemKeyRenderer;
import kr.pe.sinnori.gui.config.table.ConfigItemTableModel;
import kr.pe.sinnori.gui.config.table.ConfigItemValue;
import kr.pe.sinnori.gui.config.table.ConfigItemValueEditor;
import kr.pe.sinnori.gui.config.table.ConfigItemValueRenderer;
import kr.pe.sinnori.gui.util.PathSwingAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author Jonghoon Won
 */
@SuppressWarnings("serial")
public class ProjectEditScreen extends JPanel {
	private Logger log = LoggerFactory.getLogger(ProjectEditScreen.class);

	private JFrame mainFrame = null;
	private JFileChooser chooser = null;
	private MainProjectInformation mainProjectInformation = null;

	private String titlesOfConfigItemTable[] = { "key", "value" };

	private Class<?>[] columnTypesOfConfigItemTable = new Class[] {
			ConfigItemKey.class, ConfigItemValue.class };

	private ConfigItemTableModel commonConfigItemTableModel = null;
	private HashMap<String, ConfigItemTableModel> dbcpName2ConfigItemTableModelHash = null;
	private HashMap<String, ConfigItemTableModel> projectName2ConfigItemTableModelHash = null;

	
	public ProjectEditScreen() {
		initComponents();
	}
	
	
	public ProjectEditScreen(JFrame mainFrame) {
		this.mainFrame = mainFrame;
		initComponents();
	}

	public void setProject(MainProjectInformation mainProjectInformation)
			throws ConfigErrorException {
		this.mainProjectInformation = mainProjectInformation;

		
		SequencedProperties sinnoriConfigProperties = mainProjectInformation.getSinnoriConfigSequencedProperties();
		

		sinnoriInstalledPathValueLabel.setText(mainProjectInformation.getSinnoriInstalledPathString());
		mainProjectNameValueLabel.setText(mainProjectInformation.getMainProjectName());
		appClientCheckBox.setSelected(mainProjectInformation.isAppClient());
		webClientCheckBox.setSelected(mainProjectInformation.isWebClient());
		servletSystemLibinaryPathTextField
				.setEditable(mainProjectInformation.isWebClient());
		servletSystemLibinaryPathButton.setEnabled(mainProjectInformation.isWebClient());
		servletSystemLibinaryPathTextField.setText(mainProjectInformation.getServletSystemLibrayPathString());

		List<String> subProjectList = mainProjectInformation.getUnmodifiableSubProjectNameList();
		int subProjectListSize = subProjectList.size();

		
		String[] subProjectArray = new String[subProjectListSize + 1];
		subProjectArray[0] = "- Sub Project Name -";
		for (int i = 0; i < subProjectListSize; i++) {
			subProjectArray[i+1] = subProjectList.get(i);
		}

		ComboBoxModel<String> subProjectNameComboBoxModel = new DefaultComboBoxModel<String>(
				subProjectArray);

		subProjectNameListComboBox.setModel(subProjectNameComboBoxModel);

		final List<String> dbcpNameList = this.mainProjectInformation.getUnmodifiableDBCPNameList();
		int dbcpNameListSize = dbcpNameList.size();
		String[] dbcpNameArray = new String[dbcpNameListSize + 1];
		dbcpNameArray[0] = "- DBCP Conn Pool Name -";
		for (int i = 0; i < dbcpNameListSize; i++) {
			dbcpNameArray[i + 1] = dbcpNameList.get(i);
		}

		ComboBoxModel<String> dbcpNameComboBoxModel = new DefaultComboBoxModel<String>(
				dbcpNameArray);

		dbcpNameListComboBox.setModel(dbcpNameComboBoxModel);

		SinnoriItemIDInfoManger mainProjectItemIDInfo = SinnoriItemIDInfoManger.getInstance();

		{
			List<ItemIDInfo<?>> commonPartConfigItemList = mainProjectItemIDInfo
					.getUnmodifiableCommonPartItemIDInfoList();

			// commonConfigTable
			Object[][] valuesOfCommonConfigItemTable = new Object[commonPartConfigItemList
					.size()][titlesOfConfigItemTable.length];

			for (int i = 0; i < valuesOfCommonConfigItemTable.length; i++) {
				ItemIDInfo<?> configItem = commonPartConfigItemList.get(i);
				String itemID = configItem.getItemID();
				String targetKey = itemID;

				ConfigItemKey configItemKey = new ConfigItemKey(targetKey,
						configItem.getDescription());

				ConfigItemValue configItemCellValue = new ConfigItemValue(
						targetKey,
						sinnoriConfigProperties.getProperty(targetKey),
						mainProjectItemIDInfo, mainFrame);

				valuesOfCommonConfigItemTable[i][0] = configItemKey;
				valuesOfCommonConfigItemTable[i][1] = configItemCellValue;

				// saveSequencedProperties.put(targetKey,
				// configItemCellValue.getValueOfComponent());
			}

			commonConfigItemTableModel = new ConfigItemTableModel(
					valuesOfCommonConfigItemTable, titlesOfConfigItemTable,
					columnTypesOfConfigItemTable);
			commonConfigTable.setModel(commonConfigItemTableModel);

			// commonConfigTable.getColumnModel().getColumn(0).setPreferredWidth(150);
			commonConfigTable.getColumnModel().getColumn(0)
					.setCellRenderer(new ConfigItemKeyRenderer());

			commonConfigTable.getColumnModel().getColumn(1).setResizable(false);
			commonConfigTable.getColumnModel().getColumn(1)
					.setPreferredWidth(180);
			commonConfigTable.getColumnModel().getColumn(1)
					.setCellRenderer(new ConfigItemValueRenderer());
			commonConfigTable.getColumnModel().getColumn(1)
					.setCellEditor(new ConfigItemValueEditor(new JCheckBox()));
			commonConfigTable.setRowHeight(38);
			commonConfigScrollPane.repaint();
		}

		{
			List<ItemIDInfo<?>> dbcpPartConfigItemList = mainProjectItemIDInfo
					.getUnmodifiableDBCPPartItemIDInfoList();

			int dbcpPartConfigItemListSize = dbcpPartConfigItemList.size();

			dbcpName2ConfigItemTableModelHash = new HashMap<String, ConfigItemTableModel>();
			for (String dbcpName : dbcpNameList) {
				Object[][] values = new Object[dbcpPartConfigItemListSize][titlesOfConfigItemTable.length];
				for (int i = 0; i < values.length; i++) {
					ItemIDInfo<?> configItem = dbcpPartConfigItemList.get(i);
					String itemID = configItem.getItemID();

					String targetKey = new StringBuilder("dbcp.")
							.append(dbcpName).append(".")
							.append(itemID).toString();

					log.info("dbcpName={}, targetKey={}",
							dbcpName, targetKey);

					ConfigItemKey configItemKey = new ConfigItemKey(targetKey,
							configItem.getDescription());
					ConfigItemValue configItemCellValue = new ConfigItemValue(
							targetKey,
							sinnoriConfigProperties.getProperty(targetKey),
							mainProjectItemIDInfo, mainFrame);

					values[i][0] = configItemKey;
					values[i][1] = configItemCellValue;
				}

				ConfigItemTableModel dbcpConfigItemTableModel = new ConfigItemTableModel(
						values, titlesOfConfigItemTable,
						columnTypesOfConfigItemTable);

				dbcpName2ConfigItemTableModelHash.put(dbcpName,
						dbcpConfigItemTableModel);
			}
		}

		{
			// projectName2ConfigItemTableModelHash
			List<String> subProjectNameList = mainProjectInformation
					.getUnmodifiableSubProjectNameList();

			List<ItemIDInfo<?>> projectPartConfigItemList = mainProjectItemIDInfo
					.getUnmodifiableProjectPartItemIDInfoList();

			int projectPartConfigItemListSize = projectPartConfigItemList
					.size();

			projectName2ConfigItemTableModelHash = new HashMap<String, ConfigItemTableModel>();

			{
				String mainProjectName = mainProjectInformation.getMainProjectName();
				Object[][] values = new Object[projectPartConfigItemListSize][titlesOfConfigItemTable.length];
				for (int i = 0; i < values.length; i++) {
					ItemIDInfo<?> configItem = projectPartConfigItemList.get(i);
					String itemID = configItem.getItemID();

					String targetKey = new StringBuilder("project.")
							.append(mainProjectName).append(".").append(itemID)
							.toString();

					ConfigItemKey configItemKey = new ConfigItemKey(targetKey,
							configItem.getDescription());
					ConfigItemValue configItemCellValue = new ConfigItemValue(
							targetKey,
							sinnoriConfigProperties.getProperty(targetKey),
							mainProjectItemIDInfo, mainFrame);

					values[i][0] = configItemKey;
					values[i][1] = configItemCellValue;
				}

				ConfigItemTableModel mainProjectPartConfigItemTableModel = new ConfigItemTableModel(
						values, titlesOfConfigItemTable,
						columnTypesOfConfigItemTable);

				projectName2ConfigItemTableModelHash.put(mainProjectName,
						mainProjectPartConfigItemTableModel);
			}

			for (String subProjectName : subProjectNameList) {
				Object[][] values = new Object[projectPartConfigItemListSize][titlesOfConfigItemTable.length];
				for (int i = 0; i < values.length; i++) {
					ItemIDInfo<?> configItem = projectPartConfigItemList.get(i);
					String itemID = configItem.getItemID();

					String targetKey = new StringBuilder("project.")
							.append(subProjectName).append(".").append(itemID)
							.toString();

					ConfigItemKey configItemKey = new ConfigItemKey(targetKey,
							configItem.getDescription());
					ConfigItemValue configItemCellValue = new ConfigItemValue(
							targetKey,
							sinnoriConfigProperties.getProperty(targetKey),
							mainProjectItemIDInfo, mainFrame);

					values[i][0] = configItemKey;
					values[i][1] = configItemCellValue;
				}

				ConfigItemTableModel subProjectPartConfigItemTableModel = new ConfigItemTableModel(
						values, titlesOfConfigItemTable,
						columnTypesOfConfigItemTable);

				projectName2ConfigItemTableModelHash.put(subProjectName,
						subProjectPartConfigItemTableModel);
			}
		}

		{
			String mainProjectName = mainProjectInformation.getMainProjectName();
			mainProjectConfigTable
					.setModel(projectName2ConfigItemTableModelHash
							.get(mainProjectName));

			// commonConfigTable.getColumnModel().getColumn(0).setPreferredWidth(150);
			mainProjectConfigTable.getColumnModel().getColumn(0)
					.setCellRenderer(new ConfigItemKeyRenderer());

			mainProjectConfigTable.getColumnModel().getColumn(1)
					.setResizable(false);
			mainProjectConfigTable.getColumnModel().getColumn(1)
					.setPreferredWidth(180);

			mainProjectConfigTable.getColumnModel().getColumn(1)
					.setCellRenderer(new ConfigItemValueRenderer());
			mainProjectConfigTable.getColumnModel().getColumn(1)
					.setCellEditor(new ConfigItemValueEditor(new JCheckBox()));
			mainProjectConfigTable.setRowHeight(38);
			mainProjectConfigScrollPane.repaint();
		}
	}

	// FIXME!
	private void projectWorkSaveButtonActionPerformed(ActionEvent e) {
		List<String> dbcpNameList = mainProjectInformation.getUnmodifiableDBCPNameList();
		List<String> subProjectNameList = mainProjectInformation.getUnmodifiableSubProjectNameList();
		String mainProjectName = mainProjectInformation.getMainProjectName();		
		
		for (String dbcpName : dbcpNameList) {
			ConfigItemTableModel configItemTableModel = dbcpName2ConfigItemTableModelHash
					.get(dbcpName);
			int maxRow = configItemTableModel.getRowCount();
			log.info("dbcpName={}, maxRow={}", dbcpName,
					maxRow);

			for (int i = 0; i < maxRow; i++) {
				Object tableModelValue = configItemTableModel.getValueAt(i,
						1);

				if (!(tableModelValue instanceof ConfigItemValue)) {
					log.error(
							"dbcpName[{}] ConfigItemTableModel[{}][{}]'s value is not instanc of ConfigItemValue class",
							dbcpName, i, 1);
					System.exit(1);
				}

				ConfigItemValue configItemCellValue = (ConfigItemValue) tableModelValue;
				String targetKey = configItemCellValue.getTargetKey();
				String targetValue = configItemCellValue
						.getValueOfComponent();

				log.info(
						"dbcpName={}, row index={}, targetKey={}, targetValue={}",
						dbcpName, i, targetKey, targetValue);

				try {
					mainProjectInformation.setSinnoriConfigProperties(targetKey, targetValue);

				} catch (ConfigErrorException e1) {
					String errorMessage = e1.getMessage();
					log.warn("targetKey={}, errormessage={}", targetKey,
							errorMessage);

					DBCPNamePopup popup = new DBCPNamePopup(
							mainFrame, mainProjectName,
							dbcpName, configItemTableModel, i,
							targetKey);
					popup.setTitle("DBCP Connection Pool Name Conifg");
					popup.setSize(740, 220);
					popup.setVisible(true);
					return;
				}
			}

		}
		
		{
			int maxRow = commonConfigItemTableModel.getRowCount();
			log.info("commonConfigItemTableModel row={}", maxRow);

			for (int i = 0; i < maxRow; i++) {
				Object tableModelValue = commonConfigItemTableModel.getValueAt(
						i, 1);

				if (!(tableModelValue instanceof ConfigItemValue)) {
					log.error(
							"commonConfigItemTableModel[{}][{}]'s value is not a ConfigItemValue class object",
							i, 1);
					System.exit(1);
				}

				ConfigItemValue configItemCellValue = (ConfigItemValue) tableModelValue;
				String targetKey = configItemCellValue.getTargetKey();
				String targetValue = configItemCellValue.getValueOfComponent();

				log.info(
						"commonConfigItemTableModel row index={}, targetKey={}, targetValue={}",
						i, targetKey, targetValue);

				
				try {
					mainProjectInformation.setSinnoriConfigProperties(targetKey, targetValue);

				} catch (ConfigErrorException e1) {
					String errorMessage = e1.getMessage();
					log.warn("targetKey={}, errormessage={}", targetKey,
							errorMessage);

					commonConfigTable.changeSelection(i, 1, false, false);
					commonConfigTable.editCellAt(i, 1);

					JOptionPane.showMessageDialog(this, new StringBuilder(
							"Please check the value of key[").append(targetKey)
							.append("]").toString());
					return;
				}
			}
		}

	

		{
			ConfigItemTableModel mainProjectConfigItemTableModel = projectName2ConfigItemTableModelHash
					.get(mainProjectName);

			int row = mainProjectConfigItemTableModel.getRowCount();
			log.info("mainProjectName={}, row={}", mainProjectName, row);

			for (int i = 0; i < row; i++) {
				Object tableModelValue = mainProjectConfigItemTableModel
						.getValueAt(i, 1);

				if (!(tableModelValue instanceof ConfigItemValue)) {
					log.error(
							"mainProjectName[{}] ConfigItemTableModel[{}][{}]'s value is not instanc of ConfigItemValue class",
							mainProjectName, i, 1);
					System.exit(1);
				}

				ConfigItemValue configItemCellValue = (ConfigItemValue) tableModelValue;
				String targetKey = configItemCellValue.getTargetKey();
				String targetValue = configItemCellValue.getValueOfComponent();

				log.info(
						"mainProjectName={}, row index={}, targetKey={}, targetValue={}",
						mainProjectName, i, targetKey, targetValue);			
				try {
					mainProjectInformation.setSinnoriConfigProperties(targetKey, targetValue);

				} catch (ConfigErrorException e1) {
					String errorMessage = e1.getMessage();
					log.warn("targetKey={}, errormessage={}", targetKey,
							errorMessage);

					mainProjectConfigTable.changeSelection(i, 1, false, false);
					mainProjectConfigTable.editCellAt(i, 1);

					JOptionPane.showMessageDialog(this, new StringBuilder(
							"Please check the value of key[").append(targetKey)
							.append("]").toString());
					return;
				}
			}

		}

		for (String subPorjectName : subProjectNameList) {
			ConfigItemTableModel configItemTableModel = projectName2ConfigItemTableModelHash
					.get(subPorjectName);
			int row = configItemTableModel.getRowCount();
			log.info("subPorjectName={}, row={}", subPorjectName, row);

			for (int i = 0; i < row; i++) {
				Object tableModelValue = configItemTableModel.getValueAt(i, 1);

				if (!(tableModelValue instanceof ConfigItemValue)) {
					log.error(
							"subPorjectName[{}] ConfigItemTableModel[{}][{}]'s value is not instanc of ConfigItemValue class",
							subPorjectName, i, 1);
					System.exit(1);
				}

				ConfigItemValue configItemCellValue = (ConfigItemValue) tableModelValue;
				String targetKey = configItemCellValue.getTargetKey();
				String targetValue = configItemCellValue.getValueOfComponent();

				log.info(
						"subPorjectName={}, row index={}, targetKey={}, targetValue={}",
						subPorjectName, i, targetKey, targetValue);

				try {
					mainProjectInformation.setSinnoriConfigProperties(targetKey, targetValue);

				} catch (ConfigErrorException e1) {
					// FIXME!
					String errorMessage = e1.getMessage();
					log.warn("targetKey={}, errormessage={}", targetKey,
							errorMessage);

					SubProjectPartConfigPopup popup = new SubProjectPartConfigPopup(
							mainFrame, mainProjectName, subPorjectName,
							configItemTableModel, i, targetKey);
					popup.setTitle("Sub Project Part Conifg");
					popup.setSize(740, 380);
					popup.setVisible(true);
					return;
				}
			}
		}

		try {
			mainProjectInformation.save();
		} catch (ConfigErrorException e1) {
			String errorMessage = String.format(
					"fail to apply main project state, errormessage=%s",
					e1.getMessage());
			log.warn(errorMessage);

			webClientCheckBox.requestFocusInWindow();
			JOptionPane.showMessageDialog(mainFrame, errorMessage);
			return;
		}
		

		JOptionPane.showMessageDialog(mainFrame, "저장 성공");
	}

	private void prevButtonActionPerformed(ActionEvent e) {
		WindowManger.getInstance().changeProjectEditScreenToFirstScreen();
	}

	private void subProjectNameAddButtonActionPerformed(ActionEvent e) {
		String mainProjectName = mainProjectInformation.getMainProjectName();

		String newSubProjectName = subProjectNameTextField.getText();
		if (newSubProjectName.equals(mainProjectName)) {
			String errorMessage = String.format(
					"입력한 서브 프포르젝트 이름[%s]은 메인 프로젝트 이름과 동일합니다. 다른 이름을 넣어주세요.",
					newSubProjectName);
			subProjectNameTextField.requestFocusInWindow();
			JOptionPane.showMessageDialog(mainFrame, errorMessage);
			return;
		}
		List<String> subProjectNameList = mainProjectInformation.getUnmodifiableSubProjectNameList();
		if (subProjectNameList.contains(newSubProjectName)) {
			String errorMessage = String
					.format("입력한 서브 프포르젝트 이름[%s]은 기존에 입력한 서브 프로젝트 이름입니다. 다른 이름을 넣어주세요.",
							newSubProjectName);
			subProjectNameTextField.requestFocusInWindow();
			JOptionPane.showMessageDialog(mainFrame, errorMessage);
			return;
		}
		
		ConfigItemTableModel subProjectPartConfigItemTableModel = null;
		
		{
			SinnoriItemIDInfoManger sinnoriItemIDInfoManger = SinnoriItemIDInfoManger.getInstance();
			List<ItemIDInfo<?>> projectPartItemIDInfoList = sinnoriItemIDInfoManger
					.getUnmodifiableProjectPartItemIDInfoList();

			int projectPartItemIDInfoListSize = projectPartItemIDInfoList.size();
			Object[][] values = new Object[projectPartItemIDInfoListSize][titlesOfConfigItemTable.length];
			for (int i = 0; i < values.length; i++) {
				ItemIDInfo<?> configItem = projectPartItemIDInfoList.get(i);
				String itemID = configItem.getItemID();
				String defaultValue = configItem.getDefaultValue();

				String targetKey = new StringBuilder("project.")
						.append(newSubProjectName).append(".").append(itemID)
						.toString();

				ConfigItemKey configItemKey = new ConfigItemKey(targetKey,
						configItem.getDescription());
				ConfigItemValue configItemCellValue = new ConfigItemValue(
						targetKey, defaultValue, sinnoriItemIDInfoManger, mainFrame);

				values[i][0] = configItemKey;
				values[i][1] = configItemCellValue;
			}

			subProjectPartConfigItemTableModel = new ConfigItemTableModel(
					values, titlesOfConfigItemTable, columnTypesOfConfigItemTable);
		}

		

		mainProjectInformation.addNewSubProjectName(newSubProjectName);
		projectName2ConfigItemTableModelHash.put(newSubProjectName,
				subProjectPartConfigItemTableModel);
		subProjectNameListComboBox.addItem(newSubProjectName);

		JOptionPane.showMessageDialog(mainFrame, String.format(
				"Adding a new sub project name[%s] is success",
				newSubProjectName));
	}

	private void subProjectEditButtonActionPerformed(ActionEvent e) {
		int selectedInx = subProjectNameListComboBox.getSelectedIndex();
		if (selectedInx <= 0) {
			String errorMessage = "Please, choose Sub Project Name";
			subProjectNameListComboBox.requestFocusInWindow();
			JOptionPane.showMessageDialog(mainFrame, errorMessage);
		} else {
			String mainProjectName = mainProjectInformation.getMainProjectName();
			String selectedSubProjectName = subProjectNameListComboBox
					.getItemAt(selectedInx);
			ConfigItemTableModel subProjectPartConfigTableModel = projectName2ConfigItemTableModelHash
					.get(selectedSubProjectName);

			SubProjectPartConfigPopup popup = new SubProjectPartConfigPopup(
					mainFrame, mainProjectName, selectedSubProjectName,
					subProjectPartConfigTableModel, -1, null);
			popup.setTitle("Sub Project Part Conifg");
			popup.setSize(740, 380);
			popup.setVisible(true);
		}
	}

	private void subProjectNameDeleteButtonActionPerformed(ActionEvent e) {
		int selectedInx = subProjectNameListComboBox.getSelectedIndex();
		if (selectedInx <= 0) {
			String errorMessage = "Please, choose Sub Project Name";
			subProjectNameListComboBox.requestFocusInWindow();
			JOptionPane.showMessageDialog(mainFrame, errorMessage);
		} else {
			String selectedSubProjectName = subProjectNameListComboBox
					.getItemAt(selectedInx);
			mainProjectInformation.removeSubProjectName(selectedSubProjectName);
			projectName2ConfigItemTableModelHash.remove(selectedSubProjectName);
			subProjectNameListComboBox.removeItemAt(selectedInx);
		}
	}

	private void dbcpNameEditButtonActionPerformed(ActionEvent e) {
		int selectedInx = dbcpNameListComboBox.getSelectedIndex();
		if (selectedInx <= 0) {
			String errorMessage = "Please, choose DBCP Connection Pool Name";
			dbcpNameListComboBox.requestFocusInWindow();
			JOptionPane.showMessageDialog(mainFrame, errorMessage);
		} else {
			String mainProjectName = mainProjectInformation.getMainProjectName();
			String selectedDBCPName = dbcpNameListComboBox
					.getItemAt(selectedInx);
			ConfigItemTableModel dbcpConfigItemTableModel = dbcpName2ConfigItemTableModelHash
					.get(selectedDBCPName);

			DBCPNamePopup popup = new DBCPNamePopup(mainFrame,
					mainProjectName,
					selectedDBCPName, dbcpConfigItemTableModel, -1,
					null);
			popup.setTitle("DBCP Connection Pool Name Conifg");
			popup.setSize(740, 220);
			popup.setVisible(true);
			return;
		}
	}

	private void dbcpNameAddButtonActionPerformed(ActionEvent e) {
		String newDBCPName = dbcpNameTextField.getText();

		List<String> dbcpNameList = this.mainProjectInformation.getUnmodifiableDBCPNameList();

		if (dbcpNameList.contains(newDBCPName)) {
			String errorMessage = String.format(
					"중복된 이름[%s]을 가진 DBCP 연결 폴 이름이 있습니다.", newDBCPName);
			dbcpNameTextField.requestFocusInWindow();
			JOptionPane.showMessageDialog(mainFrame, errorMessage);
			return;
		}

		SinnoriItemIDInfoManger sinnoriItemIDInfoManger = SinnoriItemIDInfoManger.getInstance();

		List<ItemIDInfo<?>> dbcpPartConfigItemList = sinnoriItemIDInfoManger
				.getUnmodifiableDBCPPartItemIDInfoList();

		int dbcpPartConfigItemListSize = dbcpPartConfigItemList.size();

		Object[][] values = new Object[dbcpPartConfigItemListSize][titlesOfConfigItemTable.length];
		for (int i = 0; i < values.length; i++) {
			ItemIDInfo<?> configItem = dbcpPartConfigItemList.get(i);
			String itemID = configItem.getItemID();
			String defaultValue = configItem.getDefaultValue();

			String targetKey = new StringBuilder("dbcp.")
					.append(newDBCPName).append(".").append(itemID)
					.toString();

			if (itemID.equals("confige_file.value")) {
				defaultValue = BuildSystemPathSupporter
						.getDBCPConfigFilePathString(
								mainProjectInformation
										.getMainProjectName(),
										mainProjectInformation
										.getSinnoriInstalledPathString(),
										newDBCPName);
			}

			log.info("newDBCPName={}, targetKey={}", newDBCPName,
					targetKey);

			ConfigItemValue configItemCellValue = new ConfigItemValue(
					targetKey, defaultValue, sinnoriItemIDInfoManger,
					mainFrame);

			values[i][0] = targetKey;
			values[i][1] = configItemCellValue;
		}

		ConfigItemTableModel dbcpConfigItemTableModel = new ConfigItemTableModel(
				values, titlesOfConfigItemTable, columnTypesOfConfigItemTable);

		mainProjectInformation.addNewDBCPName(newDBCPName);
		dbcpName2ConfigItemTableModelHash.put(newDBCPName,
				dbcpConfigItemTableModel);
		dbcpNameListComboBox.addItem(newDBCPName);

		JOptionPane.showMessageDialog(mainFrame, String.format(
				"Adding a new DBCP Connection Pool Name[%s] is success",
				newDBCPName));
	}

	private void dbcpNameDeleteButtonActionPerformed(ActionEvent e) {
		int selectedInx = dbcpNameListComboBox.getSelectedIndex();
		if (selectedInx <= 0) {
			String errorMessage = "Please, choose DBCP Connection Pool Name";
			dbcpNameListComboBox.requestFocusInWindow();
			JOptionPane.showMessageDialog(mainFrame, errorMessage);
		} else {
			String selectedDBCPName = dbcpNameListComboBox
					.getItemAt(selectedInx);

			mainProjectInformation.removeDBCPName(selectedDBCPName);
			dbcpName2ConfigItemTableModelHash
					.remove(selectedDBCPName);
			dbcpNameListComboBox.removeItemAt(selectedInx);
		}
	}

	private void webClientCheckBoxStateChanged(ChangeEvent e) {
		boolean isWebClient = webClientCheckBox.isSelected();
		servletSystemLibinaryPathTextField.setEditable(isWebClient);
		servletSystemLibinaryPathButton.setEnabled(isWebClient);
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		// Generated using JFormDesigner non-commercial license
		sinnoriInstalledPathLinePanel = new JPanel();
		sinnoriInstalledPathTitleLabel = new JLabel();
		sinnoriInstalledPathValueLabel = new JLabel();
		mainProjectNameLinePanel = new JPanel();
		mainProjectNameTitleLabel = new JLabel();
		mainProjectNameValueLabel = new JLabel();
		projectStructLinePanel = new JPanel();
		projectStructLabel = new JLabel();
		projectStructFuncPanel = new JPanel();
		serverCheckBox = new JCheckBox();
		appClientCheckBox = new JCheckBox();
		webClientCheckBox = new JCheckBox();
		servletEnginLibinaryPathLinePanel = new JPanel();
		servletSystemLibinaryPathLabel = new JLabel();
		servletSystemLibinaryPathTextField = new JTextField();
		servletSystemLibinaryPathButton = new JButton();
		hSpacer1 = new JPanel(null);
		projectWorkSaveLinePanel = new JPanel();
		projectWorkSaveButton = new JButton();
		prevButton = new JButton();
		subProjectNameInputLinePanel = new JPanel();
		subProjectNameInputLabel = new JLabel();
		subProjectNameTextField = new JTextField();
		subProjectNameAddButton = new JButton();
		subProjectListLinePanel = new JPanel();
		subProjectNameListLabel = new JLabel();
		subProjectNameListComboBox = new JComboBox<>();
		subProjectNameListFuncPanel = new JPanel();
		subProjectNameEditButton = new JButton();
		subProjectNameDeleteButton = new JButton();
		dbcpNameInputLinePanel = new JPanel();
		dbcpNameInputLabel = new JLabel();
		dbcpNameTextField = new JTextField();
		dbcpNameAddButton = new JButton();
		dbcpNameListLinePanel = new JPanel();
		dbcpNameListLabel = new JLabel();
		dbcpNameListComboBox = new JComboBox<>();
		dbcpNameListFuncPanel = new JPanel();
		dbcpNameEditButton = new JButton();
		dbcpNameDeleteButton = new JButton();
		commonConfigLabel = new JLabel();
		commonConfigScrollPane = new JScrollPane();
		commonConfigTable = new JTable();
		mainProjectConfigLabel = new JLabel();
		mainProjectConfigScrollPane = new JScrollPane();
		mainProjectConfigTable = new JTable();

		//======== this ========
		setLayout(new FormLayout(
			"[443dlu,pref]:grow",
			"11*(default, $lgap), 104dlu, $lgap, default, $lgap, 104dlu, $lgap, default"));
		/** Post-initialization Code start */
		UIManager.put("FileChooser.readOnly", Boolean.TRUE); 
		chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(true);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		PathSwingAction pathAction = new PathSwingAction(mainFrame, chooser, servletSystemLibinaryPathTextField);
		servletSystemLibinaryPathButton.setAction(pathAction);
		/** Post-initialization Code end */

		//======== sinnoriInstalledPathLinePanel ========
		{
			sinnoriInstalledPathLinePanel.setLayout(new FormLayout(
				"default, $lcgap, default:grow",
				"default"));

			//---- sinnoriInstalledPathTitleLabel ----
			sinnoriInstalledPathTitleLabel.setText("\uc2e0\ub180\uc774 \uc124\uce58 \uacbd\ub85c :");
			sinnoriInstalledPathLinePanel.add(sinnoriInstalledPathTitleLabel, CC.xy(1, 1));

			//---- sinnoriInstalledPathValueLabel ----
			sinnoriInstalledPathValueLabel.setText("d:\\gitsinnori\\sinnori");
			sinnoriInstalledPathLinePanel.add(sinnoriInstalledPathValueLabel, CC.xy(3, 1));
		}
		add(sinnoriInstalledPathLinePanel, CC.xy(1, 1));

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
		add(mainProjectNameLinePanel, CC.xy(1, 3));

		//======== projectStructLinePanel ========
		{
			projectStructLinePanel.setLayout(new FormLayout(
				"default, $lcgap, [364dlu,pref]:grow",
				"default"));

			//---- projectStructLabel ----
			projectStructLabel.setText("\ud504\ub85c\uc81d\ud2b8 \uad6c\uc131 :");
			projectStructLinePanel.add(projectStructLabel, CC.xy(1, 1));

			//======== projectStructFuncPanel ========
			{
				projectStructFuncPanel.setLayout(new BoxLayout(projectStructFuncPanel, BoxLayout.X_AXIS));

				//---- serverCheckBox ----
				serverCheckBox.setText("\uc11c\ubc84");
				serverCheckBox.setSelected(true);
				serverCheckBox.setEnabled(false);
				projectStructFuncPanel.add(serverCheckBox);

				//---- appClientCheckBox ----
				appClientCheckBox.setText("\uc751\uc6a9 \ud074\ub77c\uc774\uc5b8\ud2b8");
				appClientCheckBox.setSelected(true);
				projectStructFuncPanel.add(appClientCheckBox);

				//---- webClientCheckBox ----
				webClientCheckBox.setText("\uc6f9 \ud074\ub77c\uc774\uc5b8\ud2b8");
				webClientCheckBox.setSelected(true);
				webClientCheckBox.addChangeListener(new ChangeListener() {
					@Override
					public void stateChanged(ChangeEvent e) {
						webClientCheckBoxStateChanged(e);
					}
				});
				projectStructFuncPanel.add(webClientCheckBox);
			}
			projectStructLinePanel.add(projectStructFuncPanel, CC.xy(3, 1));
		}
		add(projectStructLinePanel, CC.xy(1, 5));

		//======== servletEnginLibinaryPathLinePanel ========
		{
			servletEnginLibinaryPathLinePanel.setLayout(new FormLayout(
				"default, $lcgap, ${growing-button}, $lcgap, default",
				"default"));

			//---- servletSystemLibinaryPathLabel ----
			servletSystemLibinaryPathLabel.setText("\uc11c\ube14\ub9bf \uc5d4\uc9c4 \ub77c\uc774\ube0c\ub7ec\ub9ac \uacbd\ub85c :");
			servletEnginLibinaryPathLinePanel.add(servletSystemLibinaryPathLabel, CC.xy(1, 1));
			servletEnginLibinaryPathLinePanel.add(servletSystemLibinaryPathTextField, CC.xy(3, 1));

			//---- servletSystemLibinaryPathButton ----
			servletSystemLibinaryPathButton.setText("\uacbd\ub85c \uc120\ud0dd");
			servletEnginLibinaryPathLinePanel.add(servletSystemLibinaryPathButton, CC.xy(5, 1));
		}
		add(servletEnginLibinaryPathLinePanel, CC.xy(1, 7));

		//---- hSpacer1 ----
		hSpacer1.setBorder(LineBorder.createBlackLineBorder());
		add(hSpacer1, CC.xy(1, 9));

		//======== projectWorkSaveLinePanel ========
		{
			projectWorkSaveLinePanel.setAlignmentX(1.0F);
			projectWorkSaveLinePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 2));

			//---- projectWorkSaveButton ----
			projectWorkSaveButton.setText("\ubcc0\uacbd \ub0b4\uc5ed \uc800\uc7a5");
			projectWorkSaveButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					projectWorkSaveButtonActionPerformed(e);
				}
			});
			projectWorkSaveLinePanel.add(projectWorkSaveButton);

			//---- prevButton ----
			prevButton.setText("\uba54\uc778 \ud654\uba74\uc73c\ub85c \ub3cc\uc544\uac00\uae30");
			prevButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					prevButtonActionPerformed(e);
				}
			});
			projectWorkSaveLinePanel.add(prevButton);
		}
		add(projectWorkSaveLinePanel, CC.xy(1, 11));

		//======== subProjectNameInputLinePanel ========
		{
			subProjectNameInputLinePanel.setLayout(new FormLayout(
				"default, $lcgap, ${growing-button}, $lcgap, 37dlu",
				"default"));

			//---- subProjectNameInputLabel ----
			subProjectNameInputLabel.setText("Sub Project Name :");
			subProjectNameInputLinePanel.add(subProjectNameInputLabel, CC.xy(1, 1));
			subProjectNameInputLinePanel.add(subProjectNameTextField, CC.xy(3, 1));

			//---- subProjectNameAddButton ----
			subProjectNameAddButton.setText("add");
			subProjectNameAddButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					subProjectNameAddButtonActionPerformed(e);
				}
			});
			subProjectNameInputLinePanel.add(subProjectNameAddButton, CC.xy(5, 1));
		}
		add(subProjectNameInputLinePanel, CC.xy(1, 13));

		//======== subProjectListLinePanel ========
		{
			subProjectListLinePanel.setLayout(new FormLayout(
				"2*(default, $lcgap), default",
				"default"));

			//---- subProjectNameListLabel ----
			subProjectNameListLabel.setText("Sub Project Name Choose");
			subProjectListLinePanel.add(subProjectNameListLabel, CC.xy(1, 1));

			//---- subProjectNameListComboBox ----
			subProjectNameListComboBox.setModel(new DefaultComboBoxModel<>(new String[] {
				"- Sub Project Name -",
				"sample_test_sub1",
				"sample_test_sub2"
			}));
			subProjectListLinePanel.add(subProjectNameListComboBox, CC.xy(3, 1));

			//======== subProjectNameListFuncPanel ========
			{
				subProjectNameListFuncPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 2));

				//---- subProjectNameEditButton ----
				subProjectNameEditButton.setText("edit");
				subProjectNameEditButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						subProjectEditButtonActionPerformed(e);
					}
				});
				subProjectNameListFuncPanel.add(subProjectNameEditButton);

				//---- subProjectNameDeleteButton ----
				subProjectNameDeleteButton.setText("remove");
				subProjectNameDeleteButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						subProjectNameDeleteButtonActionPerformed(e);
					}
				});
				subProjectNameListFuncPanel.add(subProjectNameDeleteButton);
			}
			subProjectListLinePanel.add(subProjectNameListFuncPanel, CC.xy(5, 1));
		}
		add(subProjectListLinePanel, CC.xy(1, 15));

		//======== dbcpNameInputLinePanel ========
		{
			dbcpNameInputLinePanel.setLayout(new FormLayout(
				"default, $lcgap, ${growing-button}, $lcgap, 37dlu",
				"default"));

			//---- dbcpNameInputLabel ----
			dbcpNameInputLabel.setText("DBCP Name :");
			dbcpNameInputLinePanel.add(dbcpNameInputLabel, CC.xy(1, 1));
			dbcpNameInputLinePanel.add(dbcpNameTextField, CC.xy(3, 1));

			//---- dbcpNameAddButton ----
			dbcpNameAddButton.setText("add");
			dbcpNameAddButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dbcpNameAddButtonActionPerformed(e);
				}
			});
			dbcpNameInputLinePanel.add(dbcpNameAddButton, CC.xy(5, 1));
		}
		add(dbcpNameInputLinePanel, CC.xy(1, 17));

		//======== dbcpNameListLinePanel ========
		{
			dbcpNameListLinePanel.setLayout(new FormLayout(
				"2*(default, $lcgap), default",
				"default"));

			//---- dbcpNameListLabel ----
			dbcpNameListLabel.setText("DBCP Name Choose");
			dbcpNameListLinePanel.add(dbcpNameListLabel, CC.xy(1, 1));

			//---- dbcpNameListComboBox ----
			dbcpNameListComboBox.setModel(new DefaultComboBoxModel<>(new String[] {
				"- DBCP Name -",
				"tw_sinnoridb"
			}));
			dbcpNameListLinePanel.add(dbcpNameListComboBox, CC.xy(3, 1));

			//======== dbcpNameListFuncPanel ========
			{
				dbcpNameListFuncPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 2));

				//---- dbcpNameEditButton ----
				dbcpNameEditButton.setText("edit");
				dbcpNameEditButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						dbcpNameEditButtonActionPerformed(e);
					}
				});
				dbcpNameListFuncPanel.add(dbcpNameEditButton);

				//---- dbcpNameDeleteButton ----
				dbcpNameDeleteButton.setText("remove");
				dbcpNameDeleteButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						dbcpNameDeleteButtonActionPerformed(e);
					}
				});
				dbcpNameListFuncPanel.add(dbcpNameDeleteButton);
			}
			dbcpNameListLinePanel.add(dbcpNameListFuncPanel, CC.xy(5, 1));
		}
		add(dbcpNameListLinePanel, CC.xy(1, 19));

		//---- commonConfigLabel ----
		commonConfigLabel.setText("Common Part Config");
		add(commonConfigLabel, CC.xy(1, 21));

		//======== commonConfigScrollPane ========
		{

			//---- commonConfigTable ----
			commonConfigTable.setModel(new DefaultTableModel(
				new Object[][] {
					{null, null},
					{null, null},
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
				TableColumnModel cm = commonConfigTable.getColumnModel();
				cm.getColumn(1).setMinWidth(150);
			}
			commonConfigTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			commonConfigTable.setAutoCreateColumnsFromModel(false);
			commonConfigScrollPane.setViewportView(commonConfigTable);
		}
		add(commonConfigScrollPane, CC.xy(1, 23));

		//---- mainProjectConfigLabel ----
		mainProjectConfigLabel.setText("Main Project Part Config");
		add(mainProjectConfigLabel, CC.xy(1, 25));

		//======== mainProjectConfigScrollPane ========
		{

			//---- mainProjectConfigTable ----
			mainProjectConfigTable.setModel(new DefaultTableModel(
				new Object[][] {
					{null, null},
					{null, null},
				},
				new String[] {
					"key", "value"
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
			mainProjectConfigTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			mainProjectConfigScrollPane.setViewportView(mainProjectConfigTable);
		}
		add(mainProjectConfigScrollPane, CC.xy(1, 27));
		// //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY
	// //GEN-BEGIN:variables
	// Generated using JFormDesigner non-commercial license
	private JPanel sinnoriInstalledPathLinePanel;
	private JLabel sinnoriInstalledPathTitleLabel;
	private JLabel sinnoriInstalledPathValueLabel;
	private JPanel mainProjectNameLinePanel;
	private JLabel mainProjectNameTitleLabel;
	private JLabel mainProjectNameValueLabel;
	private JPanel projectStructLinePanel;
	private JLabel projectStructLabel;
	private JPanel projectStructFuncPanel;
	private JCheckBox serverCheckBox;
	private JCheckBox appClientCheckBox;
	private JCheckBox webClientCheckBox;
	private JPanel servletEnginLibinaryPathLinePanel;
	private JLabel servletSystemLibinaryPathLabel;
	private JTextField servletSystemLibinaryPathTextField;
	private JButton servletSystemLibinaryPathButton;
	private JPanel hSpacer1;
	private JPanel projectWorkSaveLinePanel;
	private JButton projectWorkSaveButton;
	private JButton prevButton;
	private JPanel subProjectNameInputLinePanel;
	private JLabel subProjectNameInputLabel;
	private JTextField subProjectNameTextField;
	private JButton subProjectNameAddButton;
	private JPanel subProjectListLinePanel;
	private JLabel subProjectNameListLabel;
	private JComboBox<String> subProjectNameListComboBox;
	private JPanel subProjectNameListFuncPanel;
	private JButton subProjectNameEditButton;
	private JButton subProjectNameDeleteButton;
	private JPanel dbcpNameInputLinePanel;
	private JLabel dbcpNameInputLabel;
	private JTextField dbcpNameTextField;
	private JButton dbcpNameAddButton;
	private JPanel dbcpNameListLinePanel;
	private JLabel dbcpNameListLabel;
	private JComboBox<String> dbcpNameListComboBox;
	private JPanel dbcpNameListFuncPanel;
	private JButton dbcpNameEditButton;
	private JButton dbcpNameDeleteButton;
	private JLabel commonConfigLabel;
	private JScrollPane commonConfigScrollPane;
	private JTable commonConfigTable;
	private JLabel mainProjectConfigLabel;
	private JScrollPane mainProjectConfigScrollPane;
	private JTable mainProjectConfigTable;
	// JFormDesigner - End of variables declaration //GEN-END:variables
}
