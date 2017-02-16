/*
 * Created by JFormDesigner on Sat Nov 29 13:48:34 KST 2014
 */

package kr.pe.sinnori.gui.helper.projectmanager.screen;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
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
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import kr.pe.sinnori.common.config.BuildSystemPathSupporter;
import kr.pe.sinnori.common.config.buildsystem.BuildSystemSupporter;
import kr.pe.sinnori.common.config.buildsystem.MainProjectBuildSystemState;
import kr.pe.sinnori.common.config.fileorpathstringgetter.AbstractFileOrPathStringGetter;
import kr.pe.sinnori.common.config.itemidinfo.ItemIDInfo;
import kr.pe.sinnori.common.config.itemidinfo.SinnoriItemIDInfoManger;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.etc.CommonType;
import kr.pe.sinnori.common.exception.BuildSystemException;
import kr.pe.sinnori.common.exception.MessageInfoSAXParserException;
import kr.pe.sinnori.common.exception.SinnoriConfigurationException;
import kr.pe.sinnori.common.message.builder.info.MessageInfoSAXParser;
import kr.pe.sinnori.common.util.CommonStaticUtil;
import kr.pe.sinnori.common.util.SequencedProperties;
import kr.pe.sinnori.common.util.SequencedPropertiesUtil;
import kr.pe.sinnori.gui.helper.ScreenManagerIF;
import kr.pe.sinnori.gui.helper.projectmanager.table.configurationpart.ConfigurationPartTableModel;
import kr.pe.sinnori.gui.helper.projectmanager.table.configurationpart.ItemKeyLabel;
import kr.pe.sinnori.gui.helper.projectmanager.table.configurationpart.ItemKeyRenderer;
import kr.pe.sinnori.gui.helper.projectmanager.table.configurationpart.ItemValueEditor;
import kr.pe.sinnori.gui.helper.projectmanager.table.configurationpart.ItemValuePanel;
import kr.pe.sinnori.gui.helper.projectmanager.table.configurationpart.ItemValueRenderer;
import kr.pe.sinnori.gui.util.PathSwingAction;

/**
 * @author Won Jonghoon
 */
@SuppressWarnings("serial")
public class MainProjectEditorPanel extends JPanel {
	private Logger log = LoggerFactory.getLogger(MainProjectEditorPanel.class);
	

	private final String titlesOfPropertiesTableModel[] = { "key", "value" };
	private final Class<?>[] columnTypesOfPropertiesTableModel = new Class[] {
			ItemKeyLabel.class, ItemValuePanel.class };
	private SinnoriItemIDInfoManger sinnoriItemIDInfoManger = SinnoriItemIDInfoManger
			.getInstance();
	private List<ItemIDInfo<?>> dbcpItemIDInfoList = sinnoriItemIDInfoManger
			.getUnmodifiableDBCPPartItemIDInfoList();
	private int dbcpItemIDInfoListSize = dbcpItemIDInfoList.size();
	private List<ItemIDInfo<?>> subProjectPartItemIDInfoList = sinnoriItemIDInfoManger
			.getUnmodifiableProjectPartItemIDInfoList();
	private int subProjectPartItemIDInfoListSize = subProjectPartItemIDInfoList
			.size();
	private MessageInfoSAXParser messageInfoSAXParser = null;

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

	public MainProjectEditorPanel() {
		initComponents();
	}

	public MainProjectEditorPanel(Frame mainFrame,
			ScreenManagerIF screenManagerIF) {
		this.mainFrame = mainFrame;
		this.screenManagerIF = screenManagerIF;
		try {
			messageInfoSAXParser = new MessageInfoSAXParser();
		} catch (MessageInfoSAXParserException e) {
			log.error("fail to create instace of MessageInfoSAXParser class", e);
			System.exit(1);
		}

		initComponents();
	}

	public void updateScreenWithMainProjectBuildSystemState(
			MainProjectBuildSystemState mainProjectBuildSystemState) {
		this.mainProjectName = mainProjectBuildSystemState.getMainProjectName();
		this.sinnoriInstalledPathString = mainProjectBuildSystemState
				.getSinnoriInstalledPathString();

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
				.getSinnoriConfigurationSequencedPropties();

		SinnoriItemIDInfoManger sinnoriItemIDInfoManger = SinnoriItemIDInfoManger
				.getInstance();

		int maxRowHeightOfCommonPartItemValuePanel = -1;

		for (String dbcpName : dbcpNameList) {
			this.dbcpNameList.add(dbcpName);
			String prefixOfItemID = new StringBuilder("dbcp.").append(dbcpName)
					.append(".").toString();

			Object[][] valuesOfDBCPPropertiesTableModel = new Object[dbcpItemIDInfoListSize][titlesOfPropertiesTableModel.length];

			for (int i = 0; i < dbcpItemIDInfoListSize; i++) {
				ItemIDInfo<?> itemIDInfo = dbcpItemIDInfoList.get(i);
				String itemID = itemIDInfo.getItemID();
				String itemKey = new StringBuilder(prefixOfItemID).append(itemID).toString();
				String itemValue = sinnoriConfigSequencedProperties
						.getProperty(itemKey);

				ItemIDInfo.ViewType itemViewType = itemIDInfo.getViewType();
				Set<String> itemSet = itemIDInfo.getItemSet();

				ItemKeyLabel itemKeyLabel = new ItemKeyLabel(itemKey,
						itemIDInfo.getDescription());
				ItemValuePanel itemValuePanel = new ItemValuePanel(i, 
						itemID, prefixOfItemID, itemKey,
						itemValue, itemViewType, itemSet, mainFrame);

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

			for (int i = 0; i < commonPartConfigItemListSize; i++) {
				ItemIDInfo<?> itemIDInfo = commonPartItemIDInfoList.get(i);
				String itemID = itemIDInfo.getItemID();
				String itemKey = itemID;
				String itemValue = sinnoriConfigSequencedProperties
						.getProperty(itemKey);

				ItemKeyLabel itemKeyLabel = new ItemKeyLabel(itemKey,
						itemIDInfo.getDescription());

				ItemIDInfo.ViewType itemViewType = itemIDInfo.getViewType();
				Set<String> itemSet = itemIDInfo.getItemSet();

				ItemValuePanel itemValuePanel = new ItemValuePanel(i, 
						itemID, "", itemKey,
						itemValue, itemViewType, itemSet, mainFrame);

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

			for (int i = 0; i < mainProjectPartItemIDInfoListSize; i++) {
				ItemIDInfo<?> itemIDInfo = mainProjectPartItemIDInfoList.get(i);
				String itemID = itemIDInfo.getItemID();
				String itemKey = new StringBuilder("mainproject.").append(
						itemID).toString();
				String itemValue = sinnoriConfigSequencedProperties
						.getProperty(itemKey);

				ItemKeyLabel itemKeyLabel = new ItemKeyLabel(itemKey,
						itemIDInfo.getDescription());

				ItemIDInfo.ViewType itemViewType = itemIDInfo.getViewType();
				Set<String> itemSet = itemIDInfo.getItemSet();

				ItemValuePanel itemValuePanel = new ItemValuePanel(i, 
						itemID, "mainproject.", itemKey,
						itemValue, itemViewType, itemSet, mainFrame);

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
				ItemIDInfo<?> itemIDInfo = subProjectPartItemIDInfoList.get(i);
				String itemID = itemIDInfo.getItemID();
				String itemKey = new StringBuilder(prefixOfItemID).append(itemID)
						.toString();
				String itemValue = sinnoriConfigSequencedProperties
						.getProperty(itemKey);

				ItemIDInfo.ViewType itemViewType = itemIDInfo.getViewType();
				Set<String> itemSet = itemIDInfo.getItemSet();

				ItemKeyLabel itemKeyLabel = new ItemKeyLabel(itemKey,
						itemIDInfo.getDescription());
				ItemValuePanel itemValuePanel = new ItemValuePanel(i, 
						itemID, prefixOfItemID, itemKey,
						itemValue, itemViewType, itemSet, mainFrame);

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
				int indexOfTableModel = itemValuePanel.getIndexOfTableModel();

				this.put(itemKey, itemValue);

				boolean isInactive = sinnoriItemIDInfoManger.isInactive(
						itemID, prefixOfItemID, this);
				if (isInactive)
					continue;

				try {
					sinnoriItemIDInfoManger.getNativeValueAfterValidChecker(
							itemKey, this);
				} catch (IllegalArgumentException
						| SinnoriConfigurationException e1) {
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

					this.put(itemKey, itemValue);

					boolean isInactive = sinnoriItemIDInfoManger.isInactive(
							itemID, prefixOfItemID, this);
					if (isInactive)
						continue;

					try {
						sinnoriItemIDInfoManger
								.getNativeValueAfterValidChecker(itemKey, this);
					} catch (IllegalArgumentException
							| SinnoriConfigurationException e1) {
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
				int indexOfTableModel = itemValuePanel.getIndexOfTableModel();

				this.put(itemKey, itemValue);

				boolean isInactive = sinnoriItemIDInfoManger.isInactive(
						itemID, prefixOfItemID, this);
				if (isInactive)
					continue;

				try {
					sinnoriItemIDInfoManger.getNativeValueAfterValidChecker(
							itemKey, this);
				} catch (IllegalArgumentException
						| SinnoriConfigurationException e1) {
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

					boolean isInactive = sinnoriItemIDInfoManger.isInactive(
							itemID, prefixOfItemID, this);
					if (isInactive)
						continue;

					try {
						sinnoriItemIDInfoManger
								.getNativeValueAfterValidChecker(itemKey, this);
					} catch (IllegalArgumentException
							| SinnoriConfigurationException e1) {
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
	private SequencedProperties makeConfigurationSequencedPropertiesFromAllPartTableModel() {
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

	private void applyAppClientStatus(boolean isAppClient) {
		String appClientBuildPathString = BuildSystemPathSupporter
				.getAppClientBuildPathString(mainProjectName,
						sinnoriInstalledPathString);
		File appClientBuildPath = new File(appClientBuildPathString);

		if (isAppClient) {
			if (appClientBuildPath.exists()) {
				showMessageDialog("app client exist, so skip creation of app client build system");
			} else {
				try {
					
					BuildSystemSupporter.createAppClientBuildSystem(
							mainProjectName, sinnoriInstalledPathString,
							CommonStaticFinalVars.JVM_OPTIONS_OF_APP_CLIENT, messageInfoSAXParser);
				} catch (BuildSystemException e1) {
					log.warn("fail to create app client build system", e1);
					showMessageDialog("app client exist, so skip creation of app client build system");
					return;
				}
			}
		} else {
			if (!appClientBuildPath.exists()) {
				showMessageDialog("app client doesn't exist, so skip deletion of app client build system");
			} else {
				try {
					FileUtils.forceDelete(appClientBuildPath);
				} catch (IOException e1) {
					log.warn("fail to delete app client build system", e1);

					showMessageDialog("fail to delete app client build system");
					return;
				}
			}
		}
	}

	public void applyWebClientStatus(boolean isWebClient) {
		String webClientBuildPathString = BuildSystemPathSupporter
				.getWebClientBuildPathString(mainProjectName,
						sinnoriInstalledPathString);
		File webClientBuildPath = new File(webClientBuildPathString);

		String webRootPathString = BuildSystemPathSupporter
				.getWebRootPathString(mainProjectName,
						sinnoriInstalledPathString);
		File webRootPath = new File(webRootPathString);

		if (isWebClient) {
			if (webClientBuildPath.exists()) {
				showMessageDialog("web client exists, so skip creation of web client build system");
			} else {
				try {
					BuildSystemSupporter.createWebClientBuildSystem(
							mainProjectName, sinnoriInstalledPathString, messageInfoSAXParser);
				} catch (BuildSystemException e1) {
					String errorMessage = "fail to create web client build system";
					log.warn(errorMessage, e1);
					showMessageDialog(new StringBuilder(errorMessage)
							.append(", errormessage=").append(e1.getMessage())
							.toString());
					return;
				}

				log.info(
						"main project[{}] web client build system creation success",
						mainProjectName);
			}

			if (webRootPath.exists()) {
				showMessageDialog("web root exists, so skip creation of web root system");
			} else {
				try {
					BuildSystemSupporter.createWebRootEnvironment(
							mainProjectName, sinnoriInstalledPathString);
				} catch (BuildSystemException e1) {
					String errorMessage = "fail to delete web root";
					log.warn(errorMessage, e1);
					showMessageDialog(new StringBuilder(errorMessage)
							.append(", errormessage=").append(e1.getMessage())
							.toString());
					return;
				}

				log.info("main project[{}] web root creation success",
						mainProjectName);
			}

		} else {
			if (!webClientBuildPath.exists()) {
				showMessageDialog("web client doesn't exist, so skip deletion of web client build system");
			} else {
				try {
					FileUtils.forceDelete(webClientBuildPath);
				} catch (IOException e1) {
					String errorMessage = "fail to delete web client";
					log.warn(errorMessage, e1);
					showMessageDialog(new StringBuilder(errorMessage)
							.append(", errormessage=").append(e1.toString())
							.toString());
					return;
				}
				log.info("main project[{}] web client deletion success",
						mainProjectName);
			}

			if (!webRootPath.exists()) {
				showMessageDialog("web root doesn't exist, so skip deletion of web root");
			} else {
				try {
					FileUtils.forceDelete(webRootPath);
				} catch (IOException e1) {
					String errorMessage = "fail to delete web root";
					log.warn(errorMessage, e1);
					showMessageDialog(new StringBuilder(errorMessage)
							.append(", errormessage=").append(e1.toString())
							.toString());
					return;
				}
				log.info("main project[{}] web root deletion success",
						mainProjectName);
			}
		}
	}

	private void saveMainProjectState(ActionEvent e) {
		String message = new StringBuilder(
				"Do you wnat to save the main project[")
				.append(mainProjectName).append("] state?").toString();
		String title = "main project state storage choice";
		int answer = JOptionPane.showConfirmDialog(mainFrame, message, title,
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

		if (answer == JOptionPane.CANCEL_OPTION) {
			return;
		}

		boolean isAppClient = appClientCheckBox.isSelected();
		boolean isWebClient = webClientCheckBox.isSelected();
		String servletSystemLibraryPathString = servletSystemLibraryPathTextField
				.getText();

		applyAppClientStatus(isAppClient);
		applyWebClientStatus(isWebClient);

		try {
			BuildSystemSupporter.saveAntBuiltInProperties(mainProjectName,
					sinnoriInstalledPathString, servletSystemLibraryPathString);
		} catch (BuildSystemException e1) {
			log.warn("fail to sava ant built-in properties", e1);
			showMessageDialog(e1.getMessage());
			return;
		}

		SequencedProperties configurationSequencedProperties = makeConfigurationSequencedPropertiesFromAllPartTableModel();
		if (null == configurationSequencedProperties)
			return;

		String sinnoriConfigFilePathString = BuildSystemPathSupporter
				.getSinnoriConfigFilePathString(mainProjectName,
						sinnoriInstalledPathString);
		try {
			SequencedPropertiesUtil.saveSequencedPropertiesToFile(
					configurationSequencedProperties, BuildSystemSupporter
							.getSinnoriConfigPropertiesTitle(mainProjectName),
					sinnoriConfigFilePathString,
					CommonStaticFinalVars.SINNORI_SOURCE_FILE_CHARSET);
		} catch (IOException e1) {
			String errorMessage = new StringBuilder(
					"fail to save the main project[").append(mainProjectName)
					.append("]'s sinnori configuration file").toString();

			log.warn(errorMessage, e1);
			showMessageDialog(new StringBuilder(errorMessage)
					.append(", errormessage=").append(e.toString()).toString());
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
			ItemIDInfo<?> itemIDInfo = subProjectPartItemIDInfoList.get(i);
			String itemID = itemIDInfo.getItemID();
			String itemKey = new StringBuilder("subproject.")
					.append(newSubProjectName).append(".").append(itemID)
					.toString();
			String itemValue = itemIDInfo.getDefaultValue();
			AbstractFileOrPathStringGetter fileOrPathStringGetter = sinnoriItemIDInfoManger
					.getFileOrPathStringGetter(itemID);
			if (null != fileOrPathStringGetter) {
				fileOrPathStringGetter
						.getFileOrPathStringDependingOnSinnoriInstalledPath(
								mainProjectName, sinnoriInstalledPathString);
			}
			ItemIDInfo.ViewType itemViewType = itemIDInfo.getViewType();
			Set<String> itemSet = itemIDInfo.getItemSet();

			ItemKeyLabel itemKeyLabel = new ItemKeyLabel(itemKey,
					itemIDInfo.getDescription());
			ItemValuePanel itemValuePanel = new ItemValuePanel(i, 
					itemID, prefixOfItemID, itemKey,
					itemValue, itemViewType, itemSet, mainFrame);

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
			ItemIDInfo<?> itemIDInfo = dbcpItemIDInfoList.get(i);
			String itemID = itemIDInfo.getItemID();
			String itemKey = new StringBuilder("dbcp.").append(newDBCPName)
					.append(".").append(itemID).toString();
			String itemValue = itemIDInfo.getDefaultValue();
			AbstractFileOrPathStringGetter fileOrPathStringGetter = sinnoriItemIDInfoManger
					.getFileOrPathStringGetter(itemID);
			if (null != fileOrPathStringGetter) {
				fileOrPathStringGetter
						.getFileOrPathStringDependingOnSinnoriInstalledPath(
								mainProjectName, sinnoriInstalledPathString,
								newDBCPName);
			}
			ItemIDInfo.ViewType itemViewType = itemIDInfo.getViewType();
			Set<String> itemSet = itemIDInfo.getItemSet();

			ItemKeyLabel itemKeyLabel = new ItemKeyLabel(itemKey,
					itemIDInfo.getDescription());
			ItemValuePanel itemValuePanel = new ItemValuePanel(i, 
					itemID, prefixOfItemID, itemKey,
					itemValue, itemViewType, itemSet, mainFrame);

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
		servletSystemLibraryPathTextField.setEditable(isWebClient);
		servletSystemLibraryPathButton.setEnabled(isWebClient);
	}

	private void showMessageDialog(String message) {
		JOptionPane.showMessageDialog(mainFrame, CommonStaticUtil
				.splitString(message,
						CommonType.LINE_SEPARATOR_GUBUN.NEWLINE, 100));
	}

	private void popupProjectIOManagerScreenActionPerformed(ActionEvent e) {		
		String projectBasePathString = BuildSystemPathSupporter.getProjectBasePathString(sinnoriInstalledPathString);
		
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

	private void postInitComponents() {
		UIManager.put("FileChooser.readOnly", Boolean.TRUE);
		onlyPathChooser = new JFileChooser();
		onlyPathChooser.setMultiSelectionEnabled(true);
		onlyPathChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);		
		onlyFileChooser = new JFileChooser();
		onlyFileChooser.setMultiSelectionEnabled(true);
		onlyFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		PathSwingAction pathAction = new PathSwingAction(mainFrame, onlyPathChooser, servletSystemLibraryPathTextField);
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
			"[451dlu,pref]:grow",
			"11*(default, $lgap), 104dlu, $lgap, default, $lgap, 104dlu, $lgap, default"));
		/** Post-initialization Code start */
		postInitComponents();
		/** Post-initialization Code end */

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
			mainProjectStateSaveButton.addActionListener(e -> saveMainProjectState(e));
			functionPanel.add(mainProjectStateSaveButton, CC.xy(3, 1));

			//---- popupProjectIOManagerScreenButton ----
			popupProjectIOManagerScreenButton.setText("popup 'project IO manager screen'");
			popupProjectIOManagerScreenButton.addActionListener(e -> popupProjectIOManagerScreenActionPerformed(e));
			functionPanel.add(popupProjectIOManagerScreenButton, CC.xy(5, 1));

			//---- prevButton ----
			prevButton.setText("go back to 'all project manager screen'");
			prevButton.addActionListener(e -> goBack(e));
			functionPanel.add(prevButton, CC.xy(7, 1));
		}
		add(functionPanel, CC.xy(1, 1));

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
		add(sinnoriInstalledPathLinePanel, CC.xy(1, 3));

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
		add(mainProjectNameLinePanel, CC.xy(1, 5));

		//======== projectTypeChoiceLinePanel ========
		{
			projectTypeChoiceLinePanel.setLayout(new FormLayout(
				"default, $lcgap, [364dlu,pref]:grow",
				"default"));

			//---- projectTypeChoiceLabel ----
			projectTypeChoiceLabel.setText("project type :");
			projectTypeChoiceLinePanel.add(projectTypeChoiceLabel, CC.xy(1, 1));

			//======== projectTypeChoicePanel ========
			{
				projectTypeChoicePanel.setLayout(new BoxLayout(projectTypeChoicePanel, BoxLayout.X_AXIS));

				//---- serverCheckBox ----
				serverCheckBox.setText("server");
				serverCheckBox.setSelected(true);
				serverCheckBox.setEnabled(false);
				projectTypeChoicePanel.add(serverCheckBox);

				//---- appClientCheckBox ----
				appClientCheckBox.setText("application client");
				appClientCheckBox.setSelected(true);
				projectTypeChoicePanel.add(appClientCheckBox);

				//---- webClientCheckBox ----
				webClientCheckBox.setText("web client");
				webClientCheckBox.setSelected(true);
				webClientCheckBox.addChangeListener(e -> webClientCheckBoxStateChanged(e));
				projectTypeChoicePanel.add(webClientCheckBox);
			}
			projectTypeChoiceLinePanel.add(projectTypeChoicePanel, CC.xy(3, 1));
		}
		add(projectTypeChoiceLinePanel, CC.xy(1, 7));

		//======== servletEnginLibinaryPathLinePanel ========
		{
			servletEnginLibinaryPathLinePanel.setLayout(new FormLayout(
				"default, $lcgap, ${growing-button}, $lcgap, default",
				"default"));

			//---- servletSystemLibinaryPathLabel ----
			servletSystemLibinaryPathLabel.setText("servlet system library :");
			servletEnginLibinaryPathLinePanel.add(servletSystemLibinaryPathLabel, CC.xy(1, 1));
			servletEnginLibinaryPathLinePanel.add(servletSystemLibraryPathTextField, CC.xy(3, 1));

			//---- servletSystemLibraryPathButton ----
			servletSystemLibraryPathButton.setText("\uacbd\ub85c \uc120\ud0dd");
			servletEnginLibinaryPathLinePanel.add(servletSystemLibraryPathButton, CC.xy(5, 1));
		}
		add(servletEnginLibinaryPathLinePanel, CC.xy(1, 9));

		//---- hSpacer1 ----
		hSpacer1.setBorder(LineBorder.createBlackLineBorder());
		add(hSpacer1, CC.xy(1, 11));

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
			newSubProjectAddButton.addActionListener(e -> newSubProjectAddButtonActionPerformed(e));
			subProjectNameInputLinePanel.add(newSubProjectAddButton, CC.xy(5, 1));
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

				//---- subProjectEditButton ----
				subProjectEditButton.setText("edit");
				subProjectEditButton.addActionListener(e -> subProjectNameEditButtonActionPerformed(e));
				subProjectNameListFuncPanel.add(subProjectEditButton);

				//---- subProjectNameDeleteButton ----
				subProjectNameDeleteButton.setText("remove");
				subProjectNameDeleteButton.addActionListener(e -> subProjectNameDeleteButtonActionPerformed(e));
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

			//---- newDBCPNameInputLabel ----
			newDBCPNameInputLabel.setText("New DBCP Name :");
			dbcpNameInputLinePanel.add(newDBCPNameInputLabel, CC.xy(1, 1));
			dbcpNameInputLinePanel.add(newDBCPNameTextField, CC.xy(3, 1));

			//---- newDBCPAddButton ----
			newDBCPAddButton.setText("add");
			newDBCPAddButton.addActionListener(e -> addNewDBCP(e));
			dbcpNameInputLinePanel.add(newDBCPAddButton, CC.xy(5, 1));
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
				dbcpNameEditButton.addActionListener(e -> editDBCP(e));
				dbcpNameListFuncPanel.add(dbcpNameEditButton);

				//---- dbcpNameDeleteButton ----
				dbcpNameDeleteButton.setText("remove");
				dbcpNameDeleteButton.addActionListener(e -> deleteDBCP(e));
				dbcpNameListFuncPanel.add(dbcpNameDeleteButton);
			}
			dbcpNameListLinePanel.add(dbcpNameListFuncPanel, CC.xy(5, 1));
		}
		add(dbcpNameListLinePanel, CC.xy(1, 19));

		//---- commonPartTilteLabel ----
		commonPartTilteLabel.setText("Common Part Editor");
		add(commonPartTilteLabel, CC.xy(1, 21));

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
		add(commonPartEditorScrollPane, CC.xy(1, 23));

		//---- mainProjectPartTitleLabel ----
		mainProjectPartTitleLabel.setText("Main Project Editor");
		add(mainProjectPartTitleLabel, CC.xy(1, 25));

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
		add(mainProjectPartEditorScrollPane, CC.xy(1, 27));
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
