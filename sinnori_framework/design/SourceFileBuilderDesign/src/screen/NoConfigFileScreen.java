package screen;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import lib.MainControllerIF;
import lib.PathSwingAction;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

@SuppressWarnings("serial")
public class NoConfigFileScreen extends JPanel {
	private JFrame mainFrame = null;
	private MainControllerIF mainController = null;
	private JTextField configFileTextField;
	private JFileChooser chooser = null;
	private final Action okAction = new OKButtonAction();

	/**
	 * Create the panel.
	 */
	public NoConfigFileScreen(JFrame mainFrame, MainControllerIF mainController) {
		this.mainFrame = mainFrame;
		this.mainController = mainController;
		
		chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(true);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		// chooser.setCurrentDirectory(new File(""));
		
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				FormFactory.GROWING_BUTTON_COLSPEC,},
			new RowSpec[] {
				RowSpec.decode("pref:grow"),
				RowSpec.decode("min:grow"),}));
		
		JPanel bodyPanel = new JPanel();
		add(bodyPanel, "2, 1, fill, center");
		bodyPanel.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("max(30dlu;min):grow"),
				FormFactory.GROWING_BUTTON_COLSPEC,
				FormFactory.UNRELATED_GAP_COLSPEC,},
			new RowSpec[] {
				RowSpec.decode("pref:grow"),}));
		
		JPanel labelPanel = new JPanel();
		bodyPanel.add(labelPanel, "1, 1, fill, center");
		
		JLabel configFileLabel = new JLabel("메시지 정보 파일들 위치");
		labelPanel.add(configFileLabel);
		
		JPanel itemPanel = new JPanel();
		bodyPanel.add(itemPanel, "2, 1, fill, fill");
		
		configFileTextField = new JTextField();
		itemPanel.add(configFileTextField);
		configFileTextField.setColumns(30);
		
		PathSwingAction configFileAction = new PathSwingAction(this.mainFrame, chooser, configFileTextField);
		JButton configFileButton = new JButton("파일 선택");
		configFileButton.setAction(configFileAction);
		itemPanel.add(configFileButton);
		
		JPanel bottomPanel = new JPanel();
		add(bottomPanel, "2, 2, fill, center");
		
		JButton okButton = new JButton("다음");
		okButton.setAction(okAction);
		bottomPanel.add(okButton);
	}

	private class OKButtonAction extends AbstractAction {
		public OKButtonAction() {
			putValue(NAME, "Next");
			putValue(SHORT_DESCRIPTION, "확인 버튼 액션");
		}
		public void actionPerformed(ActionEvent e) {
			if (!mainController.setSinnoriProperteies(configFileTextField.getText())) {
				return;
			}
		}
	}
}
