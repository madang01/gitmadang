/*
 * Created by JFormDesigner on Thu Feb 02 11:07:52 KST 2017
 */

package kr.pe.sinnori.gui.helper;

import java.awt.Container;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import kr.pe.sinnori.common.config.buildsystem.MainProjectBuildSystemState;
import kr.pe.sinnori.gui.helper.iobuilder.screen.IOBuilderPanel;
import kr.pe.sinnori.gui.helper.projectmanager.screen.AllMainProjectManagerPanel;
import kr.pe.sinnori.gui.helper.projectmanager.screen.MainProjectEditorPanel;
import kr.pe.sinnori.gui.helper.projectmanager.screen.SinnoriInstalledPathPanel;
import kr.pe.sinnori.gui.helper.screen.FirstPanel;

/**
 * @author Jonghoon Won
 */
@SuppressWarnings("serial")
public class HelperMainWindow extends JFrame implements ScreenManagerIF {
	
	private FirstPanel firstPanel = new FirstPanel(this,  this);
	private IOBuilderPanel ioBuilderPanel = new IOBuilderPanel(this, this);	
	private SinnoriInstalledPathPanel sinnoriInstalledPathPanel = new SinnoriInstalledPathPanel(this, this);
	private AllMainProjectManagerPanel allMainProjectManagerPanel = new AllMainProjectManagerPanel(this, this);
	private MainProjectEditorPanel mainProjectEditorPanel = new MainProjectEditorPanel(this, this);
	
	private void postInitComponets() {		
		hideAllScreen();
		
		mainPanel.add(firstPanel);
		mainPanel.add(ioBuilderPanel);
		mainPanel.add(sinnoriInstalledPathPanel);
		mainPanel.add(allMainProjectManagerPanel);
		mainPanel.add(mainProjectEditorPanel);
		
		firstPanel.setEnabled(true);
		firstPanel.setVisible(true);
		//this.pack();
	}
	
	public HelperMainWindow() {
		initComponents();
	}


	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner non-commercial license
		mainPanel = new JPanel();

		//======== this ========
		Container contentPane = getContentPane();
		contentPane.setLayout(new FormLayout(
			"$rgap, ${growing-button}, $rgap",
			"$lgap, default:grow, $nlgap, $ugap"));

		//======== mainPanel ========
		{
			mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
			/** Post-init Code Start */
			postInitComponets();
			/** Post-init Code End */
		}
		contentPane.add(mainPanel, CC.xy(2, 2));
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner non-commercial license
	private JPanel mainPanel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
	
	
	@Override
	public void moveToFirstScreen() {
		hideAllScreen();
		
		firstPanel.setEnabled(true);
		firstPanel.setVisible(true);
		this.pack();
	}
	
	@Override
	public void moveToSinnoriInstalledPathScreen() {
		hideAllScreen();
		
		sinnoriInstalledPathPanel.setEnabled(true);
		sinnoriInstalledPathPanel.setVisible(true);
		this.pack();
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
		
		mainProjectEditorPanel.updateScreenWithMainProjectBuildSystemState(mainProjectBuildSystemState);
		mainProjectEditorPanel.setEnabled(true);
		mainProjectEditorPanel.setVisible(true);
		
		this.pack();
		
	}

	
	@Override
	public void moveToIOManagerScreen() {
		hideAllScreen();
		
		ioBuilderPanel.setEnabled(true);
		ioBuilderPanel.setVisible(true);
		this.pack();
		
	}
	
	private void hideAllScreen() {
		firstPanel.setEnabled(false);
		ioBuilderPanel.setEnabled(false);
		sinnoriInstalledPathPanel.setEnabled(false);
		allMainProjectManagerPanel.setEnabled(false);
		mainProjectEditorPanel.setEnabled(false);
		
		firstPanel.setVisible(false);
		ioBuilderPanel.setVisible(false);
		sinnoriInstalledPathPanel.setVisible(false);
		allMainProjectManagerPanel.setVisible(false);
		mainProjectEditorPanel.setVisible(false);
	}



	
}
