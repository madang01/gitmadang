package kr.pe.sinnori.gui.config.table;

import java.awt.Dimension;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import kr.pe.sinnori.common.config.AbstractNativeValueConverter;
import kr.pe.sinnori.common.config.AbstractSetTypeNativeValueConverter;
import kr.pe.sinnori.common.config.itemidinfo.ItemIDInfo;
import kr.pe.sinnori.common.config.itemidinfo.ItemIDInfo.ViewType;
import kr.pe.sinnori.common.config.itemidinfo.SinnoriItemIDInfoManger;
import kr.pe.sinnori.gui.util.PathSwingAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@SuppressWarnings("serial")
public class ConfigItemValue extends JPanel {
	private Logger log = LoggerFactory.getLogger(ConfigItemValue.class);
	
	private String targetKey;
	private ItemIDInfo.ViewType configItemViewType=null;
	private JFrame mainFrame = null;
	
	private JComboBox<String> valueComboBox = null;
	private JTextField valueTextField = null;
	private JButton pathButton = null;
	
	public ConfigItemValue(String targetKey, 
			String targetItemValue, 
			SinnoriItemIDInfoManger sinnoriConfigInfo, JFrame mainFrame) {		
		this.targetKey = targetKey;
		this.mainFrame = mainFrame;		
		
		ItemIDInfo<?> itemIDInfo = null;
		
		try {
			itemIDInfo = sinnoriConfigInfo.getItemIDInfoFromKey(targetKey, null, null);
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			log.warn(errorMessage, e);
			JOptionPane.showMessageDialog(mainFrame, errorMessage);			
			return;
			
			
		}
		if (null == itemIDInfo) {
			log.error("itemIDInfo is null, targetKey={}", targetKey);
			System.exit(1);
		}
		
		// String itemID = itemIDInfo.getItemID();
		
		
		configItemViewType = itemIDInfo.getViewType();
		String defaultValue = itemIDInfo.getDefaultValue();
		
		if (null == targetItemValue) {
			targetItemValue = defaultValue;
			log.info("targetKey[{}] is not found so change deault value[{}]", targetKey, defaultValue);
		} else {
			/**
			 * 설정 파일의 항목 정보를 구성할때 디폴트 값 검사를 수행하는 항목만 값이 빈 문자열일 경우 디폴트 값으로 설정.
			 * 디폴트 값 검사를 수행한다는 의미는 2가지를 내포한다.
			 * 첫번째 항목 정보를 구성할때의 지정한 디폴트 값이 올바른지 검사를 수행할 수 있다는것이며
			 * 마지막 두번째 의미 있는 값으로 지정하겠다는 의도를 갖는다.
			 * 
			 * 즉 디폴트 값 검사를 수행할 경우에는 의미있는 값 지정하기 바라기 때문에 빈문자열을 허용하지 않는다.
			 * 반면에 디폴트 값 검사를 수행하지 않는 경우 빈문자열은 의도한건지 의도하지 않는건지 불명확하다.
			 */
			if (targetItemValue.equals("") && itemIDInfo.isDefaultValueCheck()) {
				targetItemValue = defaultValue;
				log.info("targetKey[{}]'s value is a empty string and the default validation is true so change deault value[{}]", targetKey, defaultValue);
			}
		}
		
		/*if (targetKey.equals("dbcp.tw_sinnoridb.confige_file.value")) {
			log.info("targetKey={}, configItemViewType={}, targetItemValue={}", 
					targetKey, configItemViewType, targetItemValue);
		}*/
		
		if (configItemViewType == ItemIDInfo.ViewType.FILE) {
			valueTextField = new JTextField();
			valueTextField.setText(targetItemValue);
			valueTextField.setPreferredSize(new Dimension(310,20));
			add(valueTextField);
			
			pathButton = new JButton("파일 선택");
			
			UIManager.put("FileChooser.readOnly", Boolean.TRUE);
			JFileChooser chooser = new JFileChooser();
			chooser.setMultiSelectionEnabled(false);
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			
			PathSwingAction pathAction = new PathSwingAction(this.mainFrame, chooser, valueTextField);
			pathButton.setAction(pathAction);
			add(pathButton);
		} else if (configItemViewType == ItemIDInfo.ViewType.PATH) {
			valueTextField = new JTextField();
			valueTextField.setText(targetItemValue);
			valueTextField.setPreferredSize(new Dimension(310,20));
			add(valueTextField);
			
			pathButton = new JButton("경로 선택");
			
			UIManager.put("FileChooser.readOnly", Boolean.TRUE); 
			JFileChooser chooser = new JFileChooser();
			chooser.setMultiSelectionEnabled(false);
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);			
			
			PathSwingAction pathAction = new PathSwingAction(this.mainFrame, chooser, valueTextField);
			pathButton.setAction(pathAction);
			add(pathButton);
		} else if (configItemViewType == ViewType.SINGLE_SET) {
			AbstractNativeValueConverter<?> itemValueGetter = itemIDInfo.getItemValueConverter();			
			int selectedIndex = -1;
			AbstractSetTypeNativeValueConverter<?> singleSetValueGetter = (AbstractSetTypeNativeValueConverter<?>)itemValueGetter;
			
			Set<String> singleSet = singleSetValueGetter.getItemValueSet();
			
			valueComboBox = new JComboBox<String>();
			
			Iterator<String> iter = singleSet.iterator();
			while (iter.hasNext()) {
				String itemValue = iter.next();
				valueComboBox.addItem(itemValue);
				if (targetItemValue.equals(itemValue)) {
					selectedIndex= valueComboBox.getComponentCount() - 1;
				}				
			}
			
			if (-1 == selectedIndex) {
				selectedIndex=0;
			}
			valueComboBox.setSelectedIndex(selectedIndex);
			add(valueComboBox);
		} else {
			valueTextField = new JTextField();
			valueTextField.setPreferredSize(new Dimension(400,20));
			valueTextField.setText(targetItemValue);
			add(valueTextField);
		}
	}

	public String getTargetKey() {
		return targetKey;
	}
	

	public String getValueOfComponent() {
		if (configItemViewType == ViewType.SINGLE_SET) {
			return valueComboBox.getItemAt(valueComboBox.getSelectedIndex());
		} else {
			return valueTextField.getText();
		}		
	}
}
