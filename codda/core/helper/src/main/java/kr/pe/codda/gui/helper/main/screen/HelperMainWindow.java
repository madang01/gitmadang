/*
 * Created by JFormDesigner on Thu Feb 02 11:07:52 KST 2017
 */

package kr.pe.codda.gui.helper.main.screen;

import java.awt.Container;

import javax.swing.BoxLayout;
import javax.swing.JFrame;

import kr.pe.codda.common.buildsystem.MainProjectBuildSystemState;
import kr.pe.codda.gui.helper.iobuilder.screen.EachIOFileTypeBuilder;
import kr.pe.codda.gui.helper.iobuilder.screen.IOFIleSetBuilderPanel;
import kr.pe.codda.gui.helper.lib.ScreenManagerIF;
import kr.pe.codda.gui.helper.projectmanager.screen.AllMainProjectManagerPanel;
import kr.pe.codda.gui.helper.projectmanager.screen.MainProjectEditorPanel;
import kr.pe.codda.gui.helper.projectmanager.screen.SinnoriInstalledPathPanel;

/**
 * @author Jonghoon Won
 */
@SuppressWarnings("serial")
public class HelperMainWindow extends JFrame implements ScreenManagerIF {
	
	private FirstPanel firstPanel = new FirstPanel(this,  this);
	
	// FIXME!
	private IOFIleSetBuilderPanel ioFileSetBuilderPanel  = new IOFIleSetBuilderPanel(this, this);
	private EachIOFileTypeBuilder eachIOFileTypeBuilder  = new EachIOFileTypeBuilder(this, this);
	
	private SinnoriInstalledPathPanel sinnoriInstalledPathPanel = new SinnoriInstalledPathPanel(this, this);
	private AllMainProjectManagerPanel allMainProjectManagerPanel = new AllMainProjectManagerPanel(this, this);
	private MainProjectEditorPanel mainProjectEditorPanel = new MainProjectEditorPanel(this, this);
	
	private void postInitComponets() {		
		hideAllScreen();
		
		this.add(firstPanel);
		this.add(ioFileSetBuilderPanel);
		this.add(eachIOFileTypeBuilder);
		this.add(sinnoriInstalledPathPanel);
		this.add(allMainProjectManagerPanel);
		this.add(mainProjectEditorPanel);
		
		this.setTitle("Introduction");
		firstPanel.setEnabled(true);
		firstPanel.setVisible(true);
		this.pack();
	}
	
	public HelperMainWindow() {
		initComponents();
		postInitComponets();
	}


	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner non-commercial license

		//======== this ========
		Container contentPane = getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner non-commercial license
	// JFormDesigner - End of variables declaration  //GEN-END:variables
	
	
	@Override
	public void moveToFirstScreen() {
		this.setTitle("Introduction");
		
		hideAllScreen();
		
		firstPanel.setEnabled(true);
		firstPanel.setVisible(true);
		this.pack();
	}
	
	@Override
	public void moveToSinnoriInstalledPathScreen() {
		this.setTitle("Sinnori Installed Path");
		
		hideAllScreen();
		
		sinnoriInstalledPathPanel.setEnabled(true);
		sinnoriInstalledPathPanel.setVisible(true);
		this.pack();
	}	

	@Override
	public void moveToAllMainProjectManagerScreen(String sinnoriInstalledPathString) {
		this.setTitle("All Main Project Manager");
		
		hideAllScreen();
		
		allMainProjectManagerPanel.setEnabled(true);
		allMainProjectManagerPanel.setVisible(true);
		allMainProjectManagerPanel.setScreen(sinnoriInstalledPathString);
		this.pack();
	}

	@Override
	public void moveToMainProjectEditScreen(MainProjectBuildSystemState mainProjectBuildSystemState) {
		this.setTitle("Main Project Edit");
		
		hideAllScreen();
		
		mainProjectEditorPanel.updateScreenWithMainProjectBuildSystemState(mainProjectBuildSystemState);
		mainProjectEditorPanel.setEnabled(true);
		mainProjectEditorPanel.setVisible(true);
		
		this.pack();
		
	}

	
	@Override
	public void moveToIOFileSetBuilderScreen() {
		this.setTitle("IO File Set Builder");
		
		hideAllScreen();
		
		ioFileSetBuilderPanel.setEnabled(true);
		ioFileSetBuilderPanel.setVisible(true);
		this.pack();
		
	}
	
	@Override
	public void moveToEachIOFileTypeBuilderScreen() {
		this.setTitle("Each IO File Type Builder");
		
		hideAllScreen();
		
		eachIOFileTypeBuilder.setEnabled(true);
		eachIOFileTypeBuilder.setVisible(true);
		this.pack();
		
	}
	
	private void hideAllScreen() {
		firstPanel.setEnabled(false);
		ioFileSetBuilderPanel.setEnabled(false);
		eachIOFileTypeBuilder.setEnabled(false);
		sinnoriInstalledPathPanel.setEnabled(false);
		allMainProjectManagerPanel.setEnabled(false);
		mainProjectEditorPanel.setEnabled(false);
		
		firstPanel.setVisible(false);
		ioFileSetBuilderPanel.setVisible(false);
		eachIOFileTypeBuilder.setVisible(false);
		sinnoriInstalledPathPanel.setVisible(false);
		allMainProjectManagerPanel.setVisible(false);
		mainProjectEditorPanel.setVisible(false);
	}	
}
