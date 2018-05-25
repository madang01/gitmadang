/*
 * Created by JFormDesigner on Sat Nov 29 13:48:34 KST 2014
 */

package kr.pe.codda.gui.helper.projectmanager.screen;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
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

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.buildsystem.MainProjectBuildSystemState;
import kr.pe.codda.common.buildsystem.ProjectBuilder;
import kr.pe.codda.common.buildsystem.pathsupporter.ProjectBuildSytemPathSupporter;
import kr.pe.codda.common.config.fileorpathstringgetter.AbstractFileOrPathStringGetter;
import kr.pe.codda.common.config.itemidinfo.ItemIDInfo;
import kr.pe.codda.common.config.itemidinfo.ItemIDInfoManger;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.BuildSystemException;
import kr.pe.codda.common.exception.CoddaConfigurationException;
import kr.pe.codda.common.type.LineSeparatorType;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.common.util.SequencedProperties;
import kr.pe.codda.gui.helper.lib.ScreenManagerIF;
import kr.pe.codda.gui.helper.projectmanager.table.configurationpart.ConfigurationPartTableModel;
import kr.pe.codda.gui.helper.projectmanager.table.configurationpart.ItemKeyLabel;
import kr.pe.codda.gui.helper.projectmanager.table.configurationpart.ItemKeyRenderer;
import kr.pe.codda.gui.helper.projectmanager.table.configurationpart.ItemValueEditor;
import kr.pe.codda.gui.helper.projectmanager.table.configurationpart.ItemValuePanel;
import kr.pe.codda.gui.helper.projectmanager.table.configurationpart.ItemValueRenderer;
import kr.pe.codda.gui.util.PathSwingAction;

/**
 * @author Won Jonghoon
 */
@SuppressWarnings("serial")
public class MainProjectEditorPanel extends JPanel {
	private InternalLogger log = InternalLoggerFactory.getInstance(MainProjectEditorPanel.class);
	

	private final String titlesOfPropertiesTableModel[] = { "key", "value" };
	private final Class<?>[] columnTypesOfPropertiesTableModel = new Class[] {
			ItemKeyLabel.class, ItemValuePanel.class };
	private ItemIDInfoManger sinnoriItemIDInfoManger = ItemIDInfoManger
			.getInstance();
	private List<ItemIDInfo<?>> dbcpItemIDInfoList = sinnoriItemIDInfoManger
			.getUnmodifiableDBCPPartItemIDInfoList();
	private int dbcpItemIDInfoListSize = dbcpItemIDInfoList.size();
	private List<ItemIDInfo<?>> subProjectPartItemIDInfoList = sinnoriItemIDInfoManger
			.getUnmodifiableProjectPartItemIDInfoList();
	private int subProjectPartItemIDInfoListSize = subProjectPartItemIDInfoList
			.size();
	// private MessageInfoSAXParser messageInfoSAXParser = null;

	/**
	 * 생성자 초기화에서 초기화
	 */
	private JFileChooser onlyPathChooser = null;
	private JFileChooser onlyFileChooser = null;
	private Frame mainFrame = null;
	private ScreenManagerIF screenManagerIF = null;

	/**
	 * {@link #setProject(MainProjectBuildSystemState)} 에서 초기화
	 */
	private String mainProjectName = null;
	private String sinnoriInstalledPathString = null;
	private List<String> subProjectNameList = new ArrayList<String>();
	private List<String> dbcpNameList = new ArrayList<String>();
	private ConfigurationPartTableModel commonPartTableModel = null;
	private ConfigurationPartTableModel mainProjectPartTableModel = null;
	private HashMap<String, ConfigurationPartTableModel> dbcpName2dbcpPartTableModelHash = new HashMap<String, ConfigurationPartTableModel>();
	private HashMap<String, ConfigurationPartTableModel> subProjectName2subProjectPartTableModelHash = new HashMap<String, ConfigurationPartTableModel>();
	private HashMap<String, ItemValuePanel> itemKey2ItemValuePanelHash = new HashMap<String, ItemValuePanel>();

	public void updateScreenWithMainProjectBuildSystemState(
			MainProjectBuildSystemState mainProjectBuildSystemState) {
		this.mainProjectName = mainProjectBuildSystemState.getMainProjectName();
		this.sinnoriInstalledPathString = mainProjectBuildSystemState
				.getInstalledPathString();

		this.subProjectNameList.clear();
		this.dbcpNameList.clear();
		this.dbcpName2dbcpPartTableModelHash.clear();
		this.subProjectName2subProjectPartTableModelHash.clear();
		this.itemKey2ItemValuePanelHash.clear();

		boolean isAppClient = mainProjectBuildSystemState.isAppClient();
		boolean isWebClinet = mainProjectBuildSystemState.isWebClient();
		String servletSystemLibrayPathString = mainProjectBuildSystemState
				.getServletSystemLibrayPathString();
		List<String> dbcpNameList = mainProjectBuildSystemState
				.getDBCPNameList();
		List<String> subProjectNameList = mainProjectBuildSystemState
				.getSubProjectNameList();
		SequencedProperties sinnoriConfigSequencedProperties = mainProjectBuildSystemState
				.getConfigurationSequencedPropties();

		ItemIDInfoManger sinnoriItemIDInfoManger = ItemIDInfoManger
				.getInstance();

		int maxRowHeightOfCommonPartItemValuePanel = -1;

		for (String dbcpName : dbcpNameList) {
			this.dbcpNameList.add(dbcpName);
			String prefixOfItemID = new StringBuilder("dbcp.").append(dbcpName)
					.append(".").toString();

			Object[][] valuesOfDBCPPropertiesTableModel = new Object[dbcpItemIDInfoListSize][titlesOfPropertiesTableModel.length];

			for (int i = 0; i < dbcpItemIDInfoListSize; i++) {
				ItemIDInfo<?> dbcpPartItemIDInfo = dbcpItemIDInfoList.get(i);
				String itemID = dbcpPartItemIDInfo.getItemID();
				String itemKey = new StringBuilder(prefixOfItemID).append(itemID).toString();
				String itemValue = sinnoriConfigSequencedProperties
						.getProperty(itemKey);
				
				String itemDescriptionKey = dbcpPartItemIDInfo
						.getItemDescKey(prefixOfItemID);
				String itemDescriptionValue = dbcpPartItemIDInfo.getDescription();

				ItemIDInfo.ViewType itemViewType = dbcpPartItemIDInfo.getViewType();
				Set<String> itemSet = dbcpPartItemIDInfo.getItemSet();

				ItemKeyLabel itemKeyLabel = new ItemKeyLabel(itemKey,
						dbcpPartItemIDInfo.getDescription());
				ItemValuePanel itemValuePanel = new ItemValuePanel(i, 
						itemID, prefixOfItemID, 
						itemKey, itemValue,
						itemDescriptionKey, itemDescriptionValue,					
						itemViewType, itemSet, mainFrame);

				valuesOfDBCPPropertiesTableModel[i][0] = itemKeyLabel;
				valuesOfDBCPPropertiesTableModel[i][1] = itemValuePanel;

				itemKey2ItemValuePanelHash.put(itemKey, itemValuePanel);
			}

			ConfigurationPartTableModel dbcpPartTableModel = new ConfigurationPartTableModel(
					valuesOfDBCPPropertiesTableModel,
					titlesOfPropertiesTableModel,
					columnTypesOfPropertiesTableModel);

			dbcpName2dbcpPartTableModelHash.put(dbcpName, dbcpPartTableModel);
		}

		{
			List<ItemIDInfo<?>> commonPartItemIDInfoList = sinnoriItemIDInfoManger
					.getUnmodifiableCommonPartItemIDInfoList();
			int commonPartConfigItemListSize = commonPartItemIDInfoList.size();

			Object[][] valuesOfCommonPartPropertiesTableModel = new Object[commonPartConfigItemListSize][titlesOfPropertiesTableModel.length];

			String prefixOfItemID = "";
			for (int i = 0; i < commonPartConfigItemListSize; i++) {
				ItemIDInfo<?> commonPartItemIDInfo = commonPartItemIDInfoList.get(i);
				String itemID = commonPartItemIDInfo.getItemID();
				String itemKey = itemID;
				String itemValue = sinnoriConfigSequencedProperties
						.getProperty(itemKey);
				
				String itemDescriptionKey = commonPartItemIDInfo
						.getItemDescKey(prefixOfItemID);
				String itemDescriptionValue = commonPartItemIDInfo.getDescription();

				ItemKeyLabel itemKeyLabel = new ItemKeyLabel(itemKey,
						commonPartItemIDInfo.getDescription());

				ItemIDInfo.ViewType itemViewType = commonPartItemIDInfo.getViewType();
				Set<String> itemSet = commonPartItemIDInfo.getItemSet();

				ItemValuePanel itemValuePanel = new ItemValuePanel(i, 
						itemID, prefixOfItemID, 
						itemKey, 	itemValue,
						itemDescriptionKey, itemDescriptionValue,
						itemViewType, itemSet, mainFrame);

				maxRowHeightOfCommonPartItemValuePanel = Math.max(
						maxRowHeightOfCommonPartItemValuePanel,
						(int) itemValuePanel.getPreferredSize().getHeight());

				valuesOfCommonPartPropertiesTableModel[i][0] = itemKeyLabel;
				valuesOfCommonPartPropertiesTableModel[i][1] = itemValuePanel;

				itemKey2ItemValuePanelHash.put(itemKey, itemValuePanel);
			}

			commonPartTableModel = new ConfigurationPartTableModel(
					valuesOfCommonPartPropertiesTableModel,
					titlesOfPropertiesTableModel,
					columnTypesOfPropertiesTableModel);
		}

		{
			List<ItemIDInfo<?>> mainProjectPartItemIDInfoList = sinnoriItemIDInfoManger
					.getUnmodifiableProjectPartItemIDInfoList();
			int mainProjectPartItemIDInfoListSize = mainProjectPartItemIDInfoList
					.size();

			Object[][] valuesOfMainProjectPartPropertiesTableModel = new Object[mainProjectPartItemIDInfoListSize][titlesOfPropertiesTableModel.length];

			String prefixOfItemID = "mainproject.";
			for (int i = 0; i < mainProjectPartItemIDInfoListSize; i++) {
				ItemIDInfo<?> mainProjectPartItemIDInfo = mainProjectPartItemIDInfoList.get(i);
				String itemID = mainProjectPartItemIDInfo.getItemID();
				String itemKey = new StringBuilder("mainproject.").append(
						itemID).toString();
				String itemValue = sinnoriConfigSequencedProperties
						.getProperty(itemKey);
				
				String itemDescriptionKey = mainProjectPartItemIDInfo
						.getItemDescKey(prefixOfItemID);
				String itemDescriptionValue = mainProjectPartItemIDInfo.getDescription();

				ItemKeyLabel itemKeyLabel = new ItemKeyLabel(itemKey,
						mainProjectPartItemIDInfo.getDescription());

				ItemIDInfo.ViewType itemViewType = mainProjectPartItemIDInfo.getViewType();
				Set<String> itemSet = mainProjectPartItemIDInfo.getItemSet();

				ItemValuePanel itemValuePanel = new ItemValuePanel(i, 
						itemID, prefixOfItemID, itemKey, itemValue, 
						itemDescriptionKey, itemDescriptionValue,
						itemViewType, itemSet, mainFrame);

				valuesOfMainProjectPartPropertiesTableModel[i][0] = itemKeyLabel;
				valuesOfMainProjectPartPropertiesTableModel[i][1] = itemValuePanel;

				itemKey2ItemValuePanelHash.put(itemKey, itemValuePanel);
			}

			this.mainProjectPartTableModel = new ConfigurationPartTableModel(
					valuesOfMainProjectPartPropertiesTableModel,
					titlesOfPropertiesTableModel,
					columnTypesOfPropertiesTableModel);
		}

		for (String subProjectName : subProjectNameList) {
			this.subProjectNameList.add(subProjectName);
			
			String prefixOfItemID = new StringBuilder("subproject.")
			.append(subProjectName).append(".").toString();

			Object[][] valuesOfSubProjectPropertiesTableModel = new Object[subProjectPartItemIDInfoListSize][titlesOfPropertiesTableModel.length];

			for (int i = 0; i < subProjectPartItemIDInfoListSize; i++) {
				ItemIDInfo<?> subProjectPartItemIDInfo = subProjectPartItemIDInfoList.get(i);
				String itemID = subProjectPartItemIDInfo.getItemID();
				String itemKey = new StringBuilder(prefixOfItemID).append(itemID)
						.toString();
				String itemValue = sinnoriConfigSequencedProperties
						.getProperty(itemKey);
				
				String itemDescriptionKey = subProjectPartItemIDInfo
						.getItemDescKey(prefixOfItemID);
				String itemDescriptionValue = subProjectPartItemIDInfo.getDescription();

				ItemIDInfo.ViewType itemViewType = subProjectPartItemIDInfo.getViewType();
				Set<String> itemSet = subProjectPartItemIDInfo.getItemSet();

				ItemKeyLabel itemKeyLabel = new ItemKeyLabel(itemKey,
						subProjectPartItemIDInfo.getDescription());
				ItemValuePanel itemValuePanel = new ItemValuePanel(i, 
						itemID, prefixOfItemID, itemKey, 	itemValue, 
						itemDescriptionKey, itemDescriptionValue,
						itemViewType, itemSet, mainFrame);

				valuesOfSubProjectPropertiesTableModel[i][0] = itemKeyLabel;
				valuesOfSubProjectPropertiesTableModel[i][1] = itemValuePanel;

				itemKey2ItemValuePanelHash.put(itemKey, itemValuePanel);
			}

			ConfigurationPartTableModel subProjectPropertiesTableModel = new ConfigurationPartTableModel(
					valuesOfSubProjectPropertiesTableModel,
					titlesOfPropertiesTableModel,
					columnTypesOfPropertiesTableModel);

			subProjectName2subProjectPartTableModelHash.put(subProjectName,
					subProjectPropertiesTableModel);
		}

		sinnoriInstalledPathValueLabel.setText(sinnoriInstalledPathString);
		mainProjectNameValueLabel.setText(mainProjectName);

		appClientCheckBox.setSelected(isAppClient);
		webClientCheckBox.setSelected(isWebClinet);
		servletSystemLibraryPathTextField.setEditable(isWebClinet);
		servletSystemLibraryPathButton.setEnabled(isWebClinet);
		servletSystemLibraryPathTextField
				.setText(servletSystemLibrayPathString);

		int subProjectNameListSize = subProjectNameList.size();

		String[] subProjectArray = new String[subProjectNameListSize + 1];
		subProjectArray[0] = "- Sub Project Name -";
		for (int i = 0; i < subProjectNameListSize; i++) {
			subProjectArray[i + 1] = subProjectNameList.get(i);
		}

		ComboBoxModel<String> subProjectNameComboBoxModel = new DefaultComboBoxModel<String>(
				subProjectArray);

		subProjectNameListComboBox.setModel(subProjectNameComboBoxModel);

		int dbcpNameListSize = dbcpNameList.size();
		String[] dbcpNameArray = new String[dbcpNameListSize + 1];
		dbcpNameArray[0] = "- DBCP Conn Pool Name -";
		for (int i = 0; i < dbcpNameListSize; i++) {
			dbcpNameArray[i + 1] = dbcpNameList.get(i);
		}

		ComboBoxModel<String> dbcpNameComboBoxModel = new DefaultComboBoxModel<String>(
				dbcpNameArray);

		dbcpNameListComboBox.setModel(dbcpNameComboBoxModel);

		int rowHeightOfCommonPartTable = maxRowHeightOfCommonPartItemValuePanel
				+ commonPartEditorTable.getRowMargin() * 2;

		commonPartEditorTable.setVisible(false);
		commonPartEditorTable.setModel(commonPartTableModel);
		TableColumnModel commonPartTableColumnModel = commonPartEditorTable
				.getColumnModel();
		commonPartTableColumnModel.getColumn(0).setCellRenderer(
				new ItemKeyRenderer());
		commonPartTableColumnModel.getColumn(1).setResizable(false);
		commonPartTableColumnModel.getColumn(1).setPreferredWidth(180);
		commonPartTableColumnModel.getColumn(1).setCellRenderer(
				new ItemValueRenderer());
		commonPartTableColumnModel.getColumn(1).setCellEditor(
				new ItemValueEditor(new JCheckBox()));
		commonPartEditorTable.setRowHeight(rowHeightOfCommonPartTable);
		commonPartEditorScrollPane.repaint();
		commonPartEditorTable.setVisible(true);

		mainProjectPartEditorTable.setVisible(false);
		mainProjectPartEditorTable.setModel(mainProjectPartTableModel);
		TableColumnModel mainProjectPartTableColumnModel = mainProjectPartEditorTable
				.getColumnModel();
		mainProjectPartTableColumnModel.getColumn(0).setCellRenderer(
				new ItemKeyRenderer());
		mainProjectPartTableColumnModel.getColumn(1).setResizable(false);
		mainProjectPartTableColumnModel.getColumn(1).setPreferredWidth(180);
		mainProjectPartTableColumnModel.getColumn(1).setCellRenderer(
				new ItemValueRenderer());
		mainProjectPartTableColumnModel.getColumn(1).setCellEditor(
				new ItemValueEditor(new JCheckBox()));
		mainProjectPartEditorTable.setRowHeight(rowHeightOfCommonPartTable);
		mainProjectPartEditorScrollPane.repaint();
		mainProjectPartEditorTable.setVisible(true);
	}
	
	private class SinnoriConfigurationSequencedProperties extends
			SequencedProperties {
		/**
		 * 주어진 신놀이 시퀀스 프로퍼티에 지정한 dbcp 이름 목록을 갖는 'dbcp 이름 목록' 항목을 추가한다.
		 * 
		 * @param dbcpNameList
		 *            dbcp 이름 목록
		 */
		public void addDBCPNameListItem(List<String> dbcpNameList) {
			StringBuilder dbcpNameListTokens = new StringBuilder();
			for (String dbcpName : dbcpNameList) {
				if (dbcpNameListTokens.length() > 0) {
					dbcpNameListTokens.append(",");
				}

				dbcpNameListTokens.append(dbcpName);
			}
			this.setProperty(CommonStaticFinalVars.DBCP_NAME_LIST_KEY_STRING,
					dbcpNameListTokens.toString());
		}

		/**
		 * 주어진 신놀이 시퀀스 프로퍼티에 지정한 서브 프로젝트 이름 목록을 갖는 '서브 프로젝트 이름 목록' 항목을 생성한다.
		 * 
		 * @param subProjectNameList
		 *            서브 프로젝트 이름 목록
		 */
		public void addSubProjectNameListItem(List<String> subProjectNameList) {
			StringBuilder subProjectNameListTokens = new StringBuilder();
			for (String subProjectName : subProjectNameList) {
				if (subProjectNameListTokens.length() > 0) {
					subProjectNameListTokens.append(",");
				}

				subProjectNameListTokens.append(subProjectName);
			}
			this.setProperty(
					CommonStaticFinalVars.SUBPROJECT_NAME_LIST_KEY_STRING,
					subProjectNameListTokens.toString());
		}

		/**
		 * 공통 파트 항목들을 지정한 시퀀스 프로퍼티에 추가한다.
		 * 
		 * @return 성공 여부, true : 이상무, false : 공통 파트에서 유효하지 않은 값을 가진 항목이 있는 경우
		 */
		public boolean addCommonPartItems() {
			int rowCount = commonPartTableModel.getRowCount();
			for (int i = 0; i < rowCount; i++) {
				ItemValuePanel itemValuePanel = (ItemValuePanel) commonPartTableModel
						.getValueAt(i, 1);
				itemValuePanel.setSelected(false);
				itemValuePanel.setToolTipText(null);

				String itemID = itemValuePanel.getItemID();
				String prefixOfItemID = itemValuePanel.getPrefixOfItemID();
				String itemKey = itemValuePanel.getItemKey();
				String itemValue = itemValuePanel.getItemValue();
				String itemDescriptionKey = itemValuePanel.getItemDescriptionKey();
				String itemDescriptionValue = itemValuePanel.getItemDescriptionValue();
				
				int indexOfTableModel = itemValuePanel.getIndexOfTableModel();

				this.put(itemDescriptionKey, itemDescriptionValue);
				this.put(itemKey, itemValue);

				boolean isInactive = sinnoriItemIDInfoManger.isDisabled(
						itemID, prefixOfItemID, this);
				if (isInactive)
					continue;

				try {
					sinnoriItemIDInfoManger.getNativeValueAfterValidChecker(
							itemKey, this);
				} catch (IllegalArgumentException
						| CoddaConfigurationException e1) {
					log.warn("fail to get native value", e1);
					showMessageDialog(e1.getMessage());
					itemValuePanel.setSelected(true);
					itemValuePanel.setToolTipText(CommonStaticUtil
							.getMultiLineToolTip(e1.getMessage(), 100));
					commonPartEditorTable.changeSelection(indexOfTableModel, 1,
							false, false);
					commonPartEditorTable.editCellAt(indexOfTableModel, 1);
					return false;
				}
			}

			return true;
		}

		/**
		 * 각 dbcp 별 dbcp 파트 항목들을 지정한 시퀀스 프로퍼티에 추가한다.
		 * 
		 * @return 성공 여부, true : 이상무, false : 각 dbcp 별 dbcp 파트에서 유효하지 않은 값을 가진
		 *         항목이 있는 경우
		 */
		public boolean addAllDBCPPartItems() {
			for (String dbcpName : dbcpNameList) {
				ConfigurationPartTableModel dbcpPartTableModel = dbcpName2dbcpPartTableModelHash
						.get(dbcpName);
				int rowCount = dbcpPartTableModel.getRowCount();
				for (int i = 0; i < rowCount; i++) {
					ItemValuePanel itemValuePanel = (ItemValuePanel) dbcpPartTableModel
							.getValueAt(i, 1);
					itemValuePanel.setSelected(false);
					itemValuePanel.setToolTipText(null);

					String itemID = itemValuePanel.getItemID();
					String prefixOfItemID = itemValuePanel.getPrefixOfItemID();
					String itemKey = itemValuePanel.getItemKey();
					String itemValue = itemValuePanel.getItemValue();
					String itemDescriptionKey = itemValuePanel.getItemDescriptionKey();
					String itemDescriptionValue = itemValuePanel.getItemDescriptionValue();

					this.put(itemDescriptionKey, itemDescriptionValue);
					this.put(itemKey, itemValue);

					boolean isInactive = sinnoriItemIDInfoManger.isDisabled(
							itemID, prefixOfItemID, this);
					if (isInactive)
						continue;

					try {
						sinnoriItemIDInfoManger
								.getNativeValueAfterValidChecker(itemKey, this);
					} catch (IllegalArgumentException
							| CoddaConfigurationException e1) {
						log.warn("fail to get native value", e1);
						showMessageDialog(e1.getMessage());
						itemValuePanel.setSelected(true);
						itemValuePanel.setToolTipText(CommonStaticUtil
								.getMultiLineToolTip(e1.getMessage(), 100));
						openDBCPPartEditor(dbcpName,
								itemValuePanel.getIndexOfTableModel(), itemKey);
						return false;
					}
				}
			}
			return true;
		}

		/**
		 * 메인 프로젝트 파트 항목들을 지정한 시퀀스 프로퍼티에 추가한다.
		 * 
		 * @return 성공 여부, true : 이상무, false : 메인 프로젝트 파트에서 유효하지 않은 값을 가진 항목이 있는
		 *         경우
		 */
		public boolean addMainProjectPartItems() {

			int rowCount = mainProjectPartTableModel.getRowCount();
			for (int i = 0; i < rowCount; i++) {
				ItemValuePanel itemValuePanel = (ItemValuePanel) mainProjectPartTableModel
						.getValueAt(i, 1);
				itemValuePanel.setSelected(false);
				itemValuePanel.setToolTipText(null);

				String itemID = itemValuePanel.getItemID();
				String prefixOfItemID = itemValuePanel.getPrefixOfItemID();
				String itemKey = itemValuePanel.getItemKey();
				String itemValue = itemValuePanel.getItemValue();
				String itemDescriptionKey = itemValuePanel.getItemDescriptionKey();
				String itemDescriptionValue = itemValuePanel.getItemDescriptionValue();
				
				int indexOfTableModel = itemValuePanel.getIndexOfTableModel();				

				this.put(itemDescriptionKey, itemDescriptionValue);				
				this.put(itemKey, itemValue);

				boolean isInactive = sinnoriItemIDInfoManger.isDisabled(
						itemID, prefixOfItemID, this);
				if (isInactive)
					continue;

				try {
					sinnoriItemIDInfoManger.getNativeValueAfterValidChecker(
							itemKey, this);
				} catch (IllegalArgumentException
						| CoddaConfigurationException e1) {
					log.warn("fail to get native value", e1);
					showMessageDialog(e1.getMessage());
					itemValuePanel.setSelected(true);
					itemValuePanel.setToolTipText(CommonStaticUtil
							.getMultiLineToolTip(e1.getMessage(), 100));
					mainProjectPartEditorTable.changeSelection(
							indexOfTableModel, 1, false, false);
					mainProjectPartEditorTable.editCellAt(indexOfTableModel, 1);
					return false;
				}
			}

			return true;
		}

		/**
		 * 각 서브 프로젝트별 서브 프로젝트 파트 항목들을 지정한 시퀀스 프로퍼티에 추가한다.
		 * 
		 * @return 성공 여부, true : 이상무, false : 각 서브 프로젝트별 서브 프로젝트 파트에서 유효하지 않은 값을
		 *         가진 항목이 있는 경우
		 */
		public boolean addAllSubProjectPartItems() {
			for (String subProjectName : subProjectNameList) {
				ConfigurationPartTableModel subProjectPartTableModel = subProjectName2subProjectPartTableModelHash
						.get(subProjectName);
				int rowCount = subProjectPartTableModel.getRowCount();
				for (int i = 0; i < rowCount; i++) {
					ItemValuePanel itemValuePanel = (ItemValuePanel) subProjectPartTableModel
							.getValueAt(i, 1);
					itemValuePanel.setSelected(false);
					itemValuePanel.setToolTipText(null);

					String itemID = itemValuePanel.getItemID();
					String prefixOfItemID = itemValuePanel.getPrefixOfItemID();
					String itemKey = itemValuePanel.getItemKey();
					String itemValue = itemValuePanel.getItemValue();

					this.put(itemKey, itemValue);				

					boolean isInactive = sinnoriItemIDInfoManger.isDisabled(
							itemID, prefixOfItemID, this);
					if (isInactive)
						continue;

					try {
						sinnoriItemIDInfoManger
								.getNativeValueAfterValidChecker(itemKey, this);
					} catch (IllegalArgumentException
							| CoddaConfigurationException e1) {
						log.warn("fail to get native value", e1);
						showMessageDialog(e1.getMessage());
						itemValuePanel.setSelected(true);
						itemValuePanel.setToolTipText(CommonStaticUtil
								.getMultiLineToolTip(e1.getMessage(), 100));
						openSubProjectPopup(subProjectName,
								itemValuePanel.getIndexOfTableModel(), itemKey);
						return false;
					}
				}
			}
			return true;
		}
	}

	/**
	 * 공통 파트 테이블 모델, 메인 프로젝트 테이블 모델, dbcp 별 테이블 모델, 서브 프로젝트별 테이블 모델로 부터 신놀이 설정
	 * 순서 프로퍼티를 만들어서 반환한다.
	 * 
	 * @return 모든 파트 테이블 모델로부터 만들어진 신놀이 설정 순서 프로퍼티
	 */
	private SequencedProperties getModifiedSinnoriConfigSequencedPropertiesFromAllPartTableModel() {
		SinnoriConfigurationSequencedProperties sinnoriConfigurationSequencedProperties = new SinnoriConfigurationSequencedProperties();

		boolean result = sinnoriConfigurationSequencedProperties
				.addCommonPartItems();
		if (!result) {
			return null;
		}

		sinnoriConfigurationSequencedProperties
				.addDBCPNameListItem(dbcpNameList);

		result = sinnoriConfigurationSequencedProperties.addAllDBCPPartItems();
		if (!result) {
			return null;
		}

		result = sinnoriConfigurationSequencedProperties
				.addMainProjectPartItems();
		if (!result) {
			return null;
		}

		sinnoriConfigurationSequencedProperties
				.addSubProjectNameListItem(subProjectNameList);

		result = sinnoriConfigurationSequencedProperties
				.addAllSubProjectPartItems();
		if (!result) {
			return null;
		}

		return sinnoriConfigurationSequencedProperties;
	}	

	private void saveMainProjectState(ActionEvent e) {
		String isSaveQuestionMessage = new StringBuilder(
				"Do you wnat to save the main project[")
				.append(mainProjectName).append("] state?").toString();
		String title = "Main Project Save Dialog";
		int answer = JOptionPane.showConfirmDialog(mainFrame, isSaveQuestionMessage, title,
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

		if (answer == JOptionPane.CANCEL_OPTION) {
			/** 'cancel' choose so nothing */
			return;
		}
	
		boolean isServer = serverCheckBox.isSelected();
		boolean isAppClient = appClientCheckBox.isSelected();
		boolean isWebClient = webClientCheckBox.isSelected();
		
		String servletSystemLibraryPathString = servletSystemLibraryPathTextField
				.getText();
		if (isWebClient) {
			File servletSystemLibraryPath = new File(servletSystemLibraryPathString);
			if (!servletSystemLibraryPath.exists()) {
				String errorMessage = "Sevlet system library path doesn't exist";
				showMessageDialog(errorMessage);
				servletSystemLibraryPathTextField.requestFocusInWindow();				
				return;
			}
			
			if (!servletSystemLibraryPath.isDirectory()) {
				String errorMessage = "Sevlet system library path isn't a directory";
				showMessageDialog(errorMessage);
				servletSystemLibraryPathTextField.requestFocusInWindow();				
				return;
			}
		}
		
		
		SequencedProperties modifiedSinnoriConfigSequencedProperties = getModifiedSinnoriConfigSequencedPropertiesFromAllPartTableModel();
		if (null == modifiedSinnoriConfigSequencedProperties)
			return;
		
		try {
			ProjectBuilder projectBuilder = new ProjectBuilder(	sinnoriInstalledPathString, mainProjectName);
			projectBuilder.changeProjectState(isServer, isAppClient, isWebClient, servletSystemLibraryPathString, modifiedSinnoriConfigSequencedProperties);

		} catch (BuildSystemException e1) {
			log.warn(e1.getMessage(), e1);
			showMessageDialog(e1.getMessage());
			return;
		}

		JOptionPane.showMessageDialog(mainFrame, "save successfully");
	}

	private void goBack(ActionEvent e) {
		screenManagerIF.moveToAllMainProjectManagerScreen(sinnoriInstalledPathString);
	}

	private void newSubProjectAddButtonActionPerformed(ActionEvent e) {
		String newSubProjectName = newSubProjectNameTextField.getText();
		if (subProjectNameList.contains(newSubProjectName)) {
			String errorMesssage = new StringBuilder(
					"the new sub proejct name[").append(newSubProjectName)
					.append("] exists in the sub project name list").toString();
			showMessageDialog(errorMesssage);
			newSubProjectNameTextField.requestFocus();
			return;
		}
		subProjectNameList.add(newSubProjectName);
		
		
		String prefixOfItemID = new StringBuilder("subproject.")
		.append(newSubProjectName).append(".").toString();
		
		Object[][] valuesOfSubProjectPropertiesTableModel = new Object[subProjectPartItemIDInfoListSize][titlesOfPropertiesTableModel.length];
		for (int i = 0; i < subProjectPartItemIDInfoListSize; i++) {
			ItemIDInfo<?> subProjectPartItemIDInfo = subProjectPartItemIDInfoList.get(i);
			String itemID = subProjectPartItemIDInfo.getItemID();
			String itemKey = new StringBuilder("subproject.")
					.append(newSubProjectName).append(".").append(itemID)
					.toString();
			String itemValue = subProjectPartItemIDInfo.getDefaultValue();
			
			String itemDescriptionKey = subProjectPartItemIDInfo
					.getItemDescKey(prefixOfItemID);
			String itemDescriptionValue = subProjectPartItemIDInfo.getDescription();
			
			AbstractFileOrPathStringGetter fileOrPathStringGetter = sinnoriItemIDInfoManger
					.getFileOrPathStringGetter(itemID);
			if (null != fileOrPathStringGetter) {
				fileOrPathStringGetter
						.getFileOrPathStringDependingOnInstalledPath(
								sinnoriInstalledPathString, mainProjectName);
			}
			ItemIDInfo.ViewType itemViewType = subProjectPartItemIDInfo.getViewType();
			Set<String> itemSet = subProjectPartItemIDInfo.getItemSet();

			ItemKeyLabel itemKeyLabel = new ItemKeyLabel(itemKey,
					subProjectPartItemIDInfo.getDescription());
			ItemValuePanel itemValuePanel = new ItemValuePanel(i, 
					itemID, prefixOfItemID, itemKey, itemValue, 
					itemDescriptionKey, itemDescriptionValue,
					itemViewType, itemSet, mainFrame);

			valuesOfSubProjectPropertiesTableModel[i][0] = itemKeyLabel;
			valuesOfSubProjectPropertiesTableModel[i][1] = itemValuePanel;

			itemKey2ItemValuePanelHash.put(itemKey, itemValuePanel);
		}

		ConfigurationPartTableModel subProjectPropertiesTableModel = new ConfigurationPartTableModel(
				valuesOfSubProjectPropertiesTableModel,
				titlesOfPropertiesTableModel, columnTypesOfPropertiesTableModel);

		subProjectName2subProjectPartTableModelHash.put(newSubProjectName,
				subProjectPropertiesTableModel);

		JOptionPane.showMessageDialog(mainFrame, String.format(
				"Adding a new sub project name[%s] is success",
				newSubProjectName));
	}

	private void subProjectNameEditButtonActionPerformed(ActionEvent e) {
		int selectedInx = subProjectNameListComboBox.getSelectedIndex();
		if (selectedInx <= 0) {
			String errorMessage = "Please, choose Sub Project Name";
			subProjectNameListComboBox.requestFocusInWindow();
			JOptionPane.showMessageDialog(mainFrame, errorMessage);
		} else {
			String selectedSubProjectName = subProjectNameListComboBox
					.getItemAt(selectedInx);
			openSubProjectPopup(selectedSubProjectName, -1, null);
		}
	}

	
	private void openSubProjectPopup(String subProjectName,
			int tableModelIndexOfItemHavingBadValue,
			String itemKeyHavingBadValue) {
		if (null == subProjectName) {
			throw new IllegalArgumentException(
					"the paramter subProjectName is null");
		}
		ConfigurationPartTableModel subProjectPartTableModel = subProjectName2subProjectPartTableModelHash
				.get(subProjectName);

		if (null == subProjectPartTableModel) {
			String errorMessage = new StringBuilder(
					"the paramter subProjectName[").append(subProjectName)
					.append("]'s ConfigurationPartTableModel doesn't exist")
					.toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (tableModelIndexOfItemHavingBadValue >= 0) {
			int maxRow = subProjectPartTableModel.getRowCount();
			if (tableModelIndexOfItemHavingBadValue >= maxRow) {
				String errorMessage = new StringBuilder(
						"the parameter tableModelIndexOfItemHavingBadValue[")
						.append(tableModelIndexOfItemHavingBadValue)
						.append("] is greater than or equals to max row[")
						.append(maxRow)
						.append(" of the variabe subProjectPartConfigTableModel[")
						.append(subProjectName).append("]").toString();
				throw new IllegalArgumentException(errorMessage);
			}

			if (null == itemKeyHavingBadValue) {
				throw new IllegalArgumentException(
						"Any sub project part item value is not valid but the paramter itemKeyHavingBadValue is null");
			}
		}

		/**
		 * 공통 파트 항목들은 모든 서브 파트 항목들이 의존하므로 반듯이 먼저 유효성 검사를 수행해야 한다.
		 */
		SinnoriConfigurationSequencedProperties commonPartSequencedProperties = new SinnoriConfigurationSequencedProperties();
		boolean result = commonPartSequencedProperties.addCommonPartItems();
		if (!result) {
			return;
		}

		SubProjectPartEditorPopup popup = new SubProjectPartEditorPopup(
				mainFrame, mainProjectName, subProjectName,
				subProjectPartTableModel, tableModelIndexOfItemHavingBadValue,
				itemKeyHavingBadValue, commonPartSequencedProperties);
		popup.setTitle("Sub Project Part Editor");
		popup.setSize(740, 380);
		popup.setVisible(true);
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

			if (!subProjectNameList.contains(selectedSubProjectName)) {
				String errorMessage = new StringBuilder(
						"the parameter selectedSubProjectName[")
						.append(selectedSubProjectName)
						.append("] doesn't exist in the sub project name list")
						.toString();
				JOptionPane.showMessageDialog(mainFrame, errorMessage);
				return;
			}

			String message = new StringBuilder(
					"Do you wnat to delete the sub project[")
					.append(selectedSubProjectName).append("]?").toString();
			String title = "sub project name deletion choice";
			int answer = JOptionPane.showConfirmDialog(mainFrame, message,
					title, JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE);

			if (answer == JOptionPane.OK_OPTION) {
				subProjectNameList.remove(selectedSubProjectName);
				subProjectName2subProjectPartTableModelHash
						.remove(selectedSubProjectName);

				subProjectNameListComboBox.removeItemAt(selectedInx);
			}
		}
	}

	private void addNewDBCP(ActionEvent e) {
		String newDBCPName = newDBCPNameTextField.getText();

		if (dbcpNameList.contains(newDBCPName)) {
			String errorMesssage = new StringBuilder("the new dbcp name[")
					.append(newDBCPName)
					.append("] exist in the dbcp name list").toString();
			showMessageDialog(errorMesssage);
			newDBCPNameTextField.requestFocusInWindow();
			return;
		}

		dbcpNameList.add(newDBCPName);
		String prefixOfItemID = new StringBuilder("dbcp.")
		.append(newDBCPName).append(".").toString();

		Object[][] valuesOfDBCPPropertiesTableModel = new Object[dbcpItemIDInfoListSize][titlesOfPropertiesTableModel.length];

		for (int i = 0; i < dbcpItemIDInfoListSize; i++) {
			ItemIDInfo<?> dbcpPartItemIDInfo = dbcpItemIDInfoList.get(i);
			String itemID = dbcpPartItemIDInfo.getItemID();
			String itemKey = new StringBuilder("dbcp.").append(newDBCPName)
					.append(".").append(itemID).toString();
			String itemValue = dbcpPartItemIDInfo.getDefaultValue();
			
			String itemDescriptionKey = dbcpPartItemIDInfo
					.getItemDescKey(prefixOfItemID);
			String itemDescriptionValue = dbcpPartItemIDInfo.getDescription();
			
			AbstractFileOrPathStringGetter fileOrPathStringGetter = sinnoriItemIDInfoManger
					.getFileOrPathStringGetter(itemID);
			if (null != fileOrPathStringGetter) {
				fileOrPathStringGetter
						.getFileOrPathStringDependingOnInstalledPath(
								sinnoriInstalledPathString, mainProjectName, 
								newDBCPName);
			}
			ItemIDInfo.ViewType itemViewType = dbcpPartItemIDInfo.getViewType();
			Set<String> itemSet = dbcpPartItemIDInfo.getItemSet();

			ItemKeyLabel itemKeyLabel = new ItemKeyLabel(itemKey,
					dbcpPartItemIDInfo.getDescription());
			ItemValuePanel itemValuePanel = new ItemValuePanel(i, 
					itemID, prefixOfItemID, itemKey, itemValue, 
					itemDescriptionKey, itemDescriptionValue,
					itemViewType, itemSet, mainFrame);

			valuesOfDBCPPropertiesTableModel[i][0] = itemKeyLabel;
			valuesOfDBCPPropertiesTableModel[i][1] = itemValuePanel;

			itemKey2ItemValuePanelHash.put(itemKey, itemValuePanel);
		}

		ConfigurationPartTableModel dbcpPartTableModel = new ConfigurationPartTableModel(
				valuesOfDBCPPropertiesTableModel, titlesOfPropertiesTableModel,
				columnTypesOfPropertiesTableModel);

		dbcpName2dbcpPartTableModelHash.put(newDBCPName, dbcpPartTableModel);

		JOptionPane.showMessageDialog(mainFrame, String.format(
				"Adding a new dbcp name[%s] is success", newDBCPName));
	}

	private void editDBCP(ActionEvent e) {
		int selectedInx = dbcpNameListComboBox.getSelectedIndex();
		if (selectedInx <= 0) {
			String errorMessage = "Please, choose DBCP Connection Pool Name";
			dbcpNameListComboBox.requestFocusInWindow();
			JOptionPane.showMessageDialog(mainFrame, errorMessage);
		} else {

			String selectedDBCPName = dbcpNameListComboBox
					.getItemAt(selectedInx);

			openDBCPPartEditor(selectedDBCPName, -1, null);
			return;
		}
	}

	private void openDBCPPartEditor(String dbcpName,
			int tableModelIndexOfItemHavingBadValue,
			String itemKeyHavingBadValue) {
		if (null == dbcpName) {
			throw new IllegalArgumentException("the paramter dbcpName is null");
		}

		ConfigurationPartTableModel dbcpPartTableModel = dbcpName2dbcpPartTableModelHash
				.get(dbcpName);

		if (null == dbcpPartTableModel) {
			String errorMessage = new StringBuilder("the paramter dbcpName[")
					.append(dbcpName)
					.append("]'s ConfigurationPartTableModel doesn't exist")
					.toString();
			throw new IllegalArgumentException(errorMessage);
		}

		if (tableModelIndexOfItemHavingBadValue >= 0) {
			int maxRow = dbcpPartTableModel.getRowCount();
			if (tableModelIndexOfItemHavingBadValue >= maxRow) {
				String errorMessage = new StringBuilder(
						"the parameter tableModelIndexOfItemHavingBadValue[")
						.append(tableModelIndexOfItemHavingBadValue)
						.append("] is greater than or equals to max row[")
						.append(maxRow)
						.append(" of the variabe dbcpPartTableModel[")
						.append(dbcpName).append("]").toString();
				throw new IllegalArgumentException(errorMessage);
			}

			if (null == itemKeyHavingBadValue) {
				throw new IllegalArgumentException(
						"Any dbcp part item value is not valid but the paramter itemKeyHavingBadValue is null");
			}
		}

		/**
		 * 공통 파트 항목들은 모든 서브 파트 항목들이 의존하므로 반듯이 먼저 유효성 검사를 수행해야 한다.
		 */
		SinnoriConfigurationSequencedProperties commonPartSequencedProperties = new SinnoriConfigurationSequencedProperties();
		boolean result = commonPartSequencedProperties.addCommonPartItems();
		if (!result) {
			return;
		}

		DBCPPartEditorPopup popup = new DBCPPartEditorPopup(mainFrame,
				mainProjectName, dbcpName, dbcpPartTableModel,
				tableModelIndexOfItemHavingBadValue, itemKeyHavingBadValue,
				commonPartSequencedProperties);
		popup.setTitle("DBCP Part Editor");
		popup.setSize(740, 220);
		popup.setVisible(true);
	}

	private void deleteDBCP(ActionEvent e) {
		int selectedInx = dbcpNameListComboBox.getSelectedIndex();
		if (selectedInx <= 0) {
			String errorMessage = "Please, choose DBCP Connection Pool Name";
			dbcpNameListComboBox.requestFocusInWindow();
			JOptionPane.showMessageDialog(mainFrame, errorMessage);
			return;
		} else {
			String selectedDBCPName = dbcpNameListComboBox
					.getItemAt(selectedInx);

			if (!dbcpNameList.contains(selectedDBCPName)) {
				String errorMessage = new StringBuilder(
						"the selected dbcp name[").append(selectedDBCPName)
						.append("] doesn't exist in the dbcp name list")
						.toString();
				showMessageDialog(errorMessage);
				return;
			}

			String message = new StringBuilder(
					"Do you wnat to delete the dbcp[").append(selectedDBCPName)
					.append("]?").toString();
			String title = "dbcp deletion choice";
			int answer = JOptionPane.showConfirmDialog(mainFrame, message,
					title, JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE);

			if (answer == JOptionPane.OK_OPTION) {
				dbcpNameList.remove(selectedDBCPName);
				dbcpName2dbcpPartTableModelHash.remove(selectedDBCPName);
				dbcpNameListComboBox.removeItemAt(selectedInx);
			}
		}
	}

	private void webClientCheckBoxStateChanged(ChangeEvent e) {
		boolean isWebClient = webClientCheckBox.isSelected();		
		servletSystemLibraryPathTextField.setEnabled(isWebClient);
		servletSystemLibraryPathButton.setEnabled(isWebClient);
	}

	private void showMessageDialog(String message) {
		JOptionPane.showMessageDialog(mainFrame, CommonStaticUtil
				.splitString(message,
						LineSeparatorType.NEWLINE, 100));
	}

	private void popupProjectIOManagerScreenActionPerformed(ActionEvent e) {		
		String projectBasePathString = ProjectBuildSytemPathSupporter.getProjectBasePathString(sinnoriInstalledPathString);
		
		assert(null == projectBasePathString);
		
		File projectBasePath = new File(projectBasePathString);
		if (! projectBasePath.exists()) {
			String errorMessage = String.format("the sinnori installed path(=parameter sinnoriInstalledPathString[%s])'s the project base path[%s] doesn't exist", 
					sinnoriInstalledPathString, projectBasePathString);
			
			log.warn(errorMessage);
			
			showMessageDialog(errorMessage);
			return;
		}
		
		if (!projectBasePath.isDirectory()) {
			String errorMessage = String.format("the sinnori installed path(=parameter sinnoriInstalledPathString[%s])'s the project base path[%s] is not a direcotry", 
					sinnoriInstalledPathString, projectBasePathString);
			log.warn(errorMessage);
			
			showMessageDialog(errorMessage);
			return;
		}
		
		if (!projectBasePath.canRead()) {
			String errorMessage = String.format("the sinnori installed path(=parameter sinnoriInstalledPathString[%s])'s the project base path[%s] doesn't hava permission to read", 
					sinnoriInstalledPathString, projectBasePathString);
			log.warn(errorMessage);
			
			showMessageDialog(errorMessage);
			return;
		}
		
		ArrayList<String> otherProjectNameList = new ArrayList<String>();
		
		for (File fileOfList : projectBasePath.listFiles()) {
			if (fileOfList.isDirectory()) {
				if (!fileOfList.canRead()) {
					String errorMessage = String.format("the sinnori project base path[%s] doesn't hava permission to read", fileOfList.getAbsolutePath());
					log.warn(errorMessage);
					
					showMessageDialog(errorMessage);
					return;
				}
				
				if (!fileOfList.canWrite()) {
					String errorMessage = String.format("the sinnori project base path[%s] doesn't hava permission to write", fileOfList.getAbsolutePath());
					log.warn(errorMessage);
					
					showMessageDialog(errorMessage);
					return;
				}
				String theProjectName = fileOfList.getName();
				if (!theProjectName.equals(mainProjectName)) {
					otherProjectNameList.add(theProjectName);
				}
				
			}
		}		
		
		ProjectIOFileSetBuilderPopup popup = new ProjectIOFileSetBuilderPopup(
				mainFrame, sinnoriInstalledPathString, mainProjectName, otherProjectNameList);
		popup.setTitle("Sub Project Part Editor");
		popup.setSize(740, 380);
		popup.setVisible(true);
	}
	
	public MainProjectEditorPanel(Frame mainFrame,
			ScreenManagerIF screenManagerIF) {
		this.mainFrame = mainFrame;
		this.screenManagerIF = screenManagerIF;
		/*try {
			messageInfoSAXParser = new MessageInfoSAXParser();
		} catch (MessageInfoSAXParserException e) {
			log.error("fail to create instace of MessageInfoSAXParser class", e);
			System.exit(1);
		}*/

		initComponents();
		
		postInitComponents();
	}
	

	private void postInitComponents() {
		UIManager.put("FileChooser.readOnly", Boolean.TRUE);
		onlyPathChooser = new JFileChooser();
		onlyPathChooser.setMultiSelectionEnabled(true);
		onlyPathChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);		
		onlyFileChooser = new JFileChooser();
		onlyFileChooser.setMultiSelectionEnabled(true);
		onlyFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		PathSwingAction pathAction = new PathSwingAction(mainFrame, onlyPathChooser, servletSystemLibraryPathButton.getText(), servletSystemLibraryPathTextField);
		servletSystemLibraryPathButton.setAction(pathAction);
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		// Generated using JFormDesigner non-commercial license
		functionPanel = new JPanel();
		functionLabel = new JLabel();
		mainProjectStateSaveButton = new JButton();
		popupProjectIOManagerScreenButton = new JButton();
		prevButton = new JButton();
		sinnoriInstalledPathLinePanel = new JPanel();
		sinnoriInstalledPathTitleLabel = new JLabel();
		sinnoriInstalledPathValueLabel = new JLabel();
		mainProjectNameLinePanel = new JPanel();
		mainProjectNameTitleLabel = new JLabel();
		mainProjectNameValueLabel = new JLabel();
		projectTypeChoiceLinePanel = new JPanel();
		projectTypeChoiceLabel = new JLabel();
		projectTypeChoicePanel = new JPanel();
		serverCheckBox = new JCheckBox();
		appClientCheckBox = new JCheckBox();
		webClientCheckBox = new JCheckBox();
		servletEnginLibinaryPathLinePanel = new JPanel();
		servletSystemLibinaryPathLabel = new JLabel();
		servletSystemLibraryPathTextField = new JTextField();
		servletSystemLibraryPathButton = new JButton();
		hSpacer1 = new JPanel(null);
		subProjectNameInputLinePanel = new JPanel();
		newSubProjectNameInputLabel = new JLabel();
		newSubProjectNameTextField = new JTextField();
		newSubProjectAddButton = new JButton();
		subProjectListLinePanel = new JPanel();
		subProjectNameListLabel = new JLabel();
		subProjectNameListComboBox = new JComboBox<>();
		subProjectNameListFuncPanel = new JPanel();
		subProjectEditButton = new JButton();
		subProjectNameDeleteButton = new JButton();
		dbcpNameInputLinePanel = new JPanel();
		newDBCPNameInputLabel = new JLabel();
		newDBCPNameTextField = new JTextField();
		newDBCPAddButton = new JButton();
		dbcpNameListLinePanel = new JPanel();
		dbcpNameListLabel = new JLabel();
		dbcpNameListComboBox = new JComboBox<>();
		dbcpNameListFuncPanel = new JPanel();
		dbcpNameEditButton = new JButton();
		dbcpNameDeleteButton = new JButton();
		commonPartTilteLabel = new JLabel();
		commonPartEditorScrollPane = new JScrollPane();
		commonPartEditorTable = new JTable();
		mainProjectPartTitleLabel = new JLabel();
		mainProjectPartEditorScrollPane = new JScrollPane();
		mainProjectPartEditorTable = new JTable();

		//======== this ========
		setLayout(new FormLayout(
			"$ugap, [451dlu,pref]:grow, $ugap",
			"$ugap, 11*(default, $lgap), 104dlu:grow, $lgap, default, $lgap, 104dlu:grow, $lgap, default"));

		//======== functionPanel ========
		{
			functionPanel.setLayout(new FormLayout(
				"2*(default, $rgap), 2*(default, $lcgap), default",
				"default:grow"));

			//---- functionLabel ----
			functionLabel.setText("Function : ");
			functionPanel.add(functionLabel, CC.xy(1, 1));

			//---- mainProjectStateSaveButton ----
			mainProjectStateSaveButton.setText("save");
			mainProjectStateSaveButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					saveMainProjectState(e);
				}
			});
			functionPanel.add(mainProjectStateSaveButton, CC.xy(3, 1));

			//---- popupProjectIOManagerScreenButton ----
			popupProjectIOManagerScreenButton.setText("popup 'project IO manager screen'");
			popupProjectIOManagerScreenButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					popupProjectIOManagerScreenActionPerformed(e);
				}
			});
			functionPanel.add(popupProjectIOManagerScreenButton, CC.xy(5, 1));

			//---- prevButton ----
			prevButton.setText("go back to 'all project manager screen'");
			prevButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					goBack(e);
				}
			});
			functionPanel.add(prevButton, CC.xy(7, 1));
		}
		add(functionPanel, CC.xy(2, 2));

		//======== sinnoriInstalledPathLinePanel ========
		{
			sinnoriInstalledPathLinePanel.setLayout(new FormLayout(
				"default, $lcgap, default:grow",
				"default"));

			//---- sinnoriInstalledPathTitleLabel ----
			sinnoriInstalledPathTitleLabel.setText("Sinnori installed path :");
			sinnoriInstalledPathLinePanel.add(sinnoriInstalledPathTitleLabel, CC.xy(1, 1));

			//---- sinnoriInstalledPathValueLabel ----
			sinnoriInstalledPathValueLabel.setText("d:\\gitsinnori\\sinnori");
			sinnoriInstalledPathLinePanel.add(sinnoriInstalledPathValueLabel, CC.xy(3, 1));
		}
		add(sinnoriInstalledPathLinePanel, CC.xy(2, 4));

		//======== mainProjectNameLinePanel ========
		{
			mainProjectNameLinePanel.setLayout(new FormLayout(
				"default, $lcgap, default:grow",
				"default"));

			//---- mainProjectNameTitleLabel ----
			mainProjectNameTitleLabel.setText("main project name :");
			mainProjectNameLinePanel.add(mainProjectNameTitleLabel, CC.xy(1, 1));

			//---- mainProjectNameValueLabel ----
			mainProjectNameValueLabel.setText("sample_test");
			mainProjectNameLinePanel.add(mainProjectNameValueLabel, CC.xy(3, 1));
		}
		add(mainProjectNameLinePanel, CC.xy(2, 6));

		//======== projectTypeChoiceLinePanel ========
		{
			projectTypeChoiceLinePanel.setLayout(new FormLayout(
				"default, $lcgap, [364dlu,pref]:grow",
				"default"));

			//---- projectTypeChoiceLabel ----
			projectTypeChoiceLabel.setText("Project build type :");
			projectTypeChoiceLinePanel.add(projectTypeChoiceLabel, CC.xy(1, 1));

			//======== projectTypeChoicePanel ========
			{
				projectTypeChoicePanel.setLayout(new BoxLayout(projectTypeChoicePanel, BoxLayout.X_AXIS));

				//---- serverCheckBox ----
				serverCheckBox.setText("server");
				serverCheckBox.setSelected(true);
				projectTypeChoicePanel.add(serverCheckBox);

				//---- appClientCheckBox ----
				appClientCheckBox.setText("application client");
				appClientCheckBox.setSelected(true);
				projectTypeChoicePanel.add(appClientCheckBox);

				//---- webClientCheckBox ----
				webClientCheckBox.setText("web client");
				webClientCheckBox.setSelected(true);
				webClientCheckBox.addChangeListener(new ChangeListener() {
					@Override
					public void stateChanged(ChangeEvent e) {
						webClientCheckBoxStateChanged(e);
					}
				});
				projectTypeChoicePanel.add(webClientCheckBox);
			}
			projectTypeChoiceLinePanel.add(projectTypeChoicePanel, CC.xy(3, 1));
		}
		add(projectTypeChoiceLinePanel, CC.xy(2, 8));

		//======== servletEnginLibinaryPathLinePanel ========
		{
			servletEnginLibinaryPathLinePanel.setEnabled(false);
			servletEnginLibinaryPathLinePanel.setLayout(new FormLayout(
				"default, $lcgap, ${growing-button}, $lcgap, default",
				"default"));

			//---- servletSystemLibinaryPathLabel ----
			servletSystemLibinaryPathLabel.setText("Servlet system library :");
			servletEnginLibinaryPathLinePanel.add(servletSystemLibinaryPathLabel, CC.xy(1, 1));
			servletEnginLibinaryPathLinePanel.add(servletSystemLibraryPathTextField, CC.xy(3, 1));

			//---- servletSystemLibraryPathButton ----
			servletSystemLibraryPathButton.setText("\uacbd\ub85c \uc120\ud0dd");
			servletSystemLibraryPathButton.setEnabled(false);
			servletEnginLibinaryPathLinePanel.add(servletSystemLibraryPathButton, CC.xy(5, 1));
		}
		add(servletEnginLibinaryPathLinePanel, CC.xy(2, 10));

		//---- hSpacer1 ----
		hSpacer1.setBorder(LineBorder.createBlackLineBorder());
		add(hSpacer1, CC.xy(2, 12));

		//======== subProjectNameInputLinePanel ========
		{
			subProjectNameInputLinePanel.setLayout(new FormLayout(
				"default, $lcgap, ${growing-button}, $lcgap, 37dlu",
				"default"));

			//---- newSubProjectNameInputLabel ----
			newSubProjectNameInputLabel.setText("New Sub Project Name :");
			subProjectNameInputLinePanel.add(newSubProjectNameInputLabel, CC.xy(1, 1));
			subProjectNameInputLinePanel.add(newSubProjectNameTextField, CC.xy(3, 1));

			//---- newSubProjectAddButton ----
			newSubProjectAddButton.setText("add");
			newSubProjectAddButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					newSubProjectAddButtonActionPerformed(e);
				}
			});
			subProjectNameInputLinePanel.add(newSubProjectAddButton, CC.xy(5, 1));
		}
		add(subProjectNameInputLinePanel, CC.xy(2, 14));

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

				//---- subProjectEditButton ----
				subProjectEditButton.setText("edit");
				subProjectEditButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						subProjectNameEditButtonActionPerformed(e);
					}
				});
				subProjectNameListFuncPanel.add(subProjectEditButton);

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
		add(subProjectListLinePanel, CC.xy(2, 16));

		//======== dbcpNameInputLinePanel ========
		{
			dbcpNameInputLinePanel.setLayout(new FormLayout(
				"default, $lcgap, ${growing-button}, $lcgap, 37dlu",
				"default"));

			//---- newDBCPNameInputLabel ----
			newDBCPNameInputLabel.setText("New DBCP Name :");
			dbcpNameInputLinePanel.add(newDBCPNameInputLabel, CC.xy(1, 1));
			dbcpNameInputLinePanel.add(newDBCPNameTextField, CC.xy(3, 1));

			//---- newDBCPAddButton ----
			newDBCPAddButton.setText("add");
			newDBCPAddButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					addNewDBCP(e);
				}
			});
			dbcpNameInputLinePanel.add(newDBCPAddButton, CC.xy(5, 1));
		}
		add(dbcpNameInputLinePanel, CC.xy(2, 18));

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
						editDBCP(e);
					}
				});
				dbcpNameListFuncPanel.add(dbcpNameEditButton);

				//---- dbcpNameDeleteButton ----
				dbcpNameDeleteButton.setText("remove");
				dbcpNameDeleteButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						deleteDBCP(e);
					}
				});
				dbcpNameListFuncPanel.add(dbcpNameDeleteButton);
			}
			dbcpNameListLinePanel.add(dbcpNameListFuncPanel, CC.xy(5, 1));
		}
		add(dbcpNameListLinePanel, CC.xy(2, 20));

		//---- commonPartTilteLabel ----
		commonPartTilteLabel.setText("Common Part Editor");
		add(commonPartTilteLabel, CC.xy(2, 22));

		//======== commonPartEditorScrollPane ========
		{

			//---- commonPartEditorTable ----
			commonPartEditorTable.setModel(new DefaultTableModel(
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
				TableColumnModel cm = commonPartEditorTable.getColumnModel();
				cm.getColumn(1).setMinWidth(150);
			}
			commonPartEditorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			commonPartEditorTable.setAutoCreateColumnsFromModel(false);
			commonPartEditorScrollPane.setViewportView(commonPartEditorTable);
		}
		add(commonPartEditorScrollPane, CC.xy(2, 24));

		//---- mainProjectPartTitleLabel ----
		mainProjectPartTitleLabel.setText("Main Project Editor");
		add(mainProjectPartTitleLabel, CC.xy(2, 26));

		//======== mainProjectPartEditorScrollPane ========
		{

			//---- mainProjectPartEditorTable ----
			mainProjectPartEditorTable.setModel(new DefaultTableModel(
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
			mainProjectPartEditorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			mainProjectPartEditorScrollPane.setViewportView(mainProjectPartEditorTable);
		}
		add(mainProjectPartEditorScrollPane, CC.xy(2, 28));
		// //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY
	// //GEN-BEGIN:variables
	// Generated using JFormDesigner non-commercial license
	private JPanel functionPanel;
	private JLabel functionLabel;
	private JButton mainProjectStateSaveButton;
	private JButton popupProjectIOManagerScreenButton;
	private JButton prevButton;
	private JPanel sinnoriInstalledPathLinePanel;
	private JLabel sinnoriInstalledPathTitleLabel;
	private JLabel sinnoriInstalledPathValueLabel;
	private JPanel mainProjectNameLinePanel;
	private JLabel mainProjectNameTitleLabel;
	private JLabel mainProjectNameValueLabel;
	private JPanel projectTypeChoiceLinePanel;
	private JLabel projectTypeChoiceLabel;
	private JPanel projectTypeChoicePanel;
	private JCheckBox serverCheckBox;
	private JCheckBox appClientCheckBox;
	private JCheckBox webClientCheckBox;
	private JPanel servletEnginLibinaryPathLinePanel;
	private JLabel servletSystemLibinaryPathLabel;
	private JTextField servletSystemLibraryPathTextField;
	private JButton servletSystemLibraryPathButton;
	private JPanel hSpacer1;
	private JPanel subProjectNameInputLinePanel;
	private JLabel newSubProjectNameInputLabel;
	private JTextField newSubProjectNameTextField;
	private JButton newSubProjectAddButton;
	private JPanel subProjectListLinePanel;
	private JLabel subProjectNameListLabel;
	private JComboBox<String> subProjectNameListComboBox;
	private JPanel subProjectNameListFuncPanel;
	private JButton subProjectEditButton;
	private JButton subProjectNameDeleteButton;
	private JPanel dbcpNameInputLinePanel;
	private JLabel newDBCPNameInputLabel;
	private JTextField newDBCPNameTextField;
	private JButton newDBCPAddButton;
	private JPanel dbcpNameListLinePanel;
	private JLabel dbcpNameListLabel;
	private JComboBox<String> dbcpNameListComboBox;
	private JPanel dbcpNameListFuncPanel;
	private JButton dbcpNameEditButton;
	private JButton dbcpNameDeleteButton;
	private JLabel commonPartTilteLabel;
	private JScrollPane commonPartEditorScrollPane;
	private JTable commonPartEditorTable;
	private JLabel mainProjectPartTitleLabel;
	private JScrollPane mainProjectPartEditorScrollPane;
	private JTable mainProjectPartEditorTable;
	// JFormDesigner - End of variables declaration //GEN-END:variables
}
