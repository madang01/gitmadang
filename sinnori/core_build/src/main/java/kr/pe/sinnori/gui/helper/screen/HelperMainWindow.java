/*
 * Created by JFormDesigner on Thu Feb 02 11:07:52 KST 2017
 */

package kr.pe.sinnori.gui.helper.screen;

import java.awt.Container;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import kr.pe.sinnori.common.config.buildsystem.MainProjectBuildSystemState;
import kr.pe.sinnori.gui.helper.screen.projectmanager.AllMainProjectManagerPanel;
import kr.pe.sinnori.gui.helper.screen.projectmanager.MainProjectEditorPanel;
import kr.pe.sinnori.gui.helper.screen.projectmanager.SinnoriInstalledPathPanel;

/**
 * @author Jonghoon Won
 */
@SuppressWarnings("serial")
public class HelperMainWindow extends JFrame implements ScreenManagerIF {
	
	
	public HelperMainWindow() {
		initComponents();
	}

	private void createUIComponents() {
		// TODO: add custom component creation code here
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner non-commercial license
		createUIComponents();

		sinnoriInstalledPathPanel = new SinnoriInstalledPathPanel(this, this);
		allMainProjectManagerPanel = new AllMainProjectManagerPanel(this, this);
		mainProjectMangerPanel = new MainProjectEditorPanel(this, this);

		//======== this ========
		Container contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$rgap, ${growing-button}, $rgap",
			"$nlgap, default:grow, $nlgap"));

		//======== mainPanel ========
		{
			mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
			mainPanel.add(sinnoriInstalledPathPanel);

			//---- allMainProjectManagerPanel ----
			allMainProjectManagerPanel.setVisible(false);
			mainPanel.add(allMainProjectManagerPanel);

			//---- mainProjectMangerPanel ----
			mainProjectMangerPanel.setVisible(false);
			mainPanel.add(mainProjectMangerPanel);
		}
		contentPane.add(mainPanel, CC.xy(2, 2));
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner non-commercial license
	private JPanel mainPanel;
	private SinnoriInstalledPathPanel sinnoriInstalledPathPanel;
	private AllMainProjectManagerPanel allMainProjectManagerPanel;
	private MainProjectEditorPanel mainProjectMangerPanel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
	
	
	@Override
	public void moveToFirstScreen() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void moveToSinnoriInstalledPathScreen() {
		// TODO Auto-generated method stub
		
	}	

	@Override
	public void moveToAllMainProjectManagerScreen(String sinnoriInstalledPathString) {
		hideAllScreen();
		
		allMainProjectManagerPanel.setEnabled(true);
		allMainProjectManagerPanel.setVisible(true);
		allMainProjectManagerPanel.setScreen(sinnoriInstalledPathString);
		this.pack();
	}

	@Override
	public void moveToMainProjectEditScreen(MainProjectBuildSystemState mainProjectBuildSystemState) {
		hideAllScreen();
		
		mainProjectMangerPanel.updateScreenWithMainProjectBuildSystemState(mainProjectBuildSystemState);
		mainProjectMangerPanel.setEnabled(true);
		mainProjectMangerPanel.setVisible(true);
		
		this.pack();
		
	}

	
	@Override
	public void moveToIOManagerScreen() {
		// TODO Auto-generated method stub
		
	}
	
	private void hideAllScreen() {
		sinnoriInstalledPathPanel.setEnabled(false);
		sinnoriInstalledPathPanel.setVisible(false);
		
		allMainProjectManagerPanel.setEnabled(false);
		allMainProjectManagerPanel.setVisible(false);
		
		mainProjectMangerPanel.setEnabled(false);
		mainProjectMangerPanel.setVisible(false);
	}



	
}
