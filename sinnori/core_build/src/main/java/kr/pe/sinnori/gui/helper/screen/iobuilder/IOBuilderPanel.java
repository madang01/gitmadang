/*
 * Created by JFormDesigner on Sat Feb 04 13:29:06 KST 2017
 */

package kr.pe.sinnori.gui.helper.screen.iobuilder;

import javax.swing.*;
import javax.swing.table.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;

/**
 * @author Jonghoon Won
 */
public class IOBuilderPanel extends JPanel {
	public IOBuilderPanel() {
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner non-commercial license
		sourceBuilderTabbedPane = new JTabbedPane();
		ioFileSetPanel = new JPanel();
		messageInfoFilePathPanel = new JPanel();
		messageInfoFilePathLabel = new JLabel();
		messageInfoFilePathTextField = new JTextField();
		messageInfoFilePathButton = new JButton();
		firstPathSavingIOFileSetPanel = new JPanel();
		firstPathSavingIOFileSetTitleLabel = new JLabel();
		firstPathSavingIOFileSetCheckBox = new JCheckBox();
		firstPathSavingIOFileSetTextField = new JTextField();
		firstPathSavingIOFileSetButton = new JButton();
		secoundPathSavingIOFileSetPanel = new JPanel();
		secoundPathSavingIOFileSetLabel = new JLabel();
		secoundPathSavingIOFileSetCheckBox = new JCheckBox();
		secoundPathSavingIOFileSetTextField = new JTextField();
		secoundPathSavingIOFileSetButton = new JButton();
		thirdIPathSavingIOFileSetPanel = new JPanel();
		thirdPathSavingIOFileSetLabel = new JLabel();
		thirdPathSavingIOFileSetCheckBox = new JCheckBox();
		thirdPathSavingIOFileSetTextField = new JTextField();
		thirdPathSavingIOFileSetButton = new JButton();
		writerPanel = new JPanel();
		writerLabel = new JLabel();
		writerTextField = new JTextField();
		menuPanel = new JPanel();
		rereadingAllMessageInfoFileButton = new JButton();
		buildingIOSourceFileSetButton = new JButton();
		resultLabel = new JLabel();
		messageIDSearchPanel = new JPanel();
		messageIDSearchLabel = new JLabel();
		messageIDSearchTextField = new JTextField();
		messageIDSearchButton = new JButton();
		messageInfoScrollPane = new JScrollPane();
		messageInfoTable = new JTable();
		eachIOFileTypePanel = new JPanel();

		//======== this ========
		setLayout(new FormLayout(
			"$ugap, 432dlu, $ugap",
			"8dlu, 242dlu, 8dlu"));

		//======== sourceBuilderTabbedPane ========
		{

			//======== ioFileSetPanel ========
			{
				ioFileSetPanel.setLayout(new FormLayout(
					"$ugap, ${growing-button}, $ugap",
					"8*($lgap, default), $lgap, 50dlu, $lgap"));

				//======== messageInfoFilePathPanel ========
				{
					messageInfoFilePathPanel.setLayout(new FormLayout(
						"132dlu, $ugap, [236dlu,pref]:grow, $ugap, default",
						"default"));

					//---- messageInfoFilePathLabel ----
					messageInfoFilePathLabel.setText("Message info file path");
					messageInfoFilePathPanel.add(messageInfoFilePathLabel, CC.xy(1, 1));
					messageInfoFilePathPanel.add(messageInfoFilePathTextField, CC.xy(3, 1));

					//---- messageInfoFilePathButton ----
					messageInfoFilePathButton.setText("Path");
					messageInfoFilePathPanel.add(messageInfoFilePathButton, CC.xy(5, 1));
				}
				ioFileSetPanel.add(messageInfoFilePathPanel, CC.xy(2, 2));

				//======== firstPathSavingIOFileSetPanel ========
				{
					firstPathSavingIOFileSetPanel.setLayout(new FormLayout(
						"98dlu, $ugap, default, $ugap, ${growing-button}, $ugap, default",
						"default"));

					//---- firstPathSavingIOFileSetTitleLabel ----
					firstPathSavingIOFileSetTitleLabel.setText("First path saving IO file set");
					firstPathSavingIOFileSetPanel.add(firstPathSavingIOFileSetTitleLabel, CC.xy(1, 1));

					//---- firstPathSavingIOFileSetCheckBox ----
					firstPathSavingIOFileSetCheckBox.setText("text");
					firstPathSavingIOFileSetPanel.add(firstPathSavingIOFileSetCheckBox, CC.xy(3, 1));
					firstPathSavingIOFileSetPanel.add(firstPathSavingIOFileSetTextField, CC.xy(5, 1));

					//---- firstPathSavingIOFileSetButton ----
					firstPathSavingIOFileSetButton.setText("Path");
					firstPathSavingIOFileSetPanel.add(firstPathSavingIOFileSetButton, CC.xy(7, 1));
				}
				ioFileSetPanel.add(firstPathSavingIOFileSetPanel, CC.xy(2, 4));

				//======== secoundPathSavingIOFileSetPanel ========
				{
					secoundPathSavingIOFileSetPanel.setLayout(new FormLayout(
						"2*(default, $ugap), ${growing-button}, $ugap, default",
						"default"));

					//---- secoundPathSavingIOFileSetLabel ----
					secoundPathSavingIOFileSetLabel.setText("Secound path saving IO file set");
					secoundPathSavingIOFileSetPanel.add(secoundPathSavingIOFileSetLabel, CC.xy(1, 1));

					//---- secoundPathSavingIOFileSetCheckBox ----
					secoundPathSavingIOFileSetCheckBox.setText("text");
					secoundPathSavingIOFileSetPanel.add(secoundPathSavingIOFileSetCheckBox, CC.xy(3, 1));
					secoundPathSavingIOFileSetPanel.add(secoundPathSavingIOFileSetTextField, CC.xy(5, 1));

					//---- secoundPathSavingIOFileSetButton ----
					secoundPathSavingIOFileSetButton.setText("Path");
					secoundPathSavingIOFileSetPanel.add(secoundPathSavingIOFileSetButton, CC.xy(7, 1));
				}
				ioFileSetPanel.add(secoundPathSavingIOFileSetPanel, CC.xy(2, 6));

				//======== thirdIPathSavingIOFileSetPanel ========
				{
					thirdIPathSavingIOFileSetPanel.setLayout(new FormLayout(
						"98dlu, $ugap, default, $ugap, ${growing-button}, $ugap, default",
						"default"));

					//---- thirdPathSavingIOFileSetLabel ----
					thirdPathSavingIOFileSetLabel.setText("Third path saving IO file set");
					thirdIPathSavingIOFileSetPanel.add(thirdPathSavingIOFileSetLabel, CC.xy(1, 1));

					//---- thirdPathSavingIOFileSetCheckBox ----
					thirdPathSavingIOFileSetCheckBox.setText("text");
					thirdIPathSavingIOFileSetPanel.add(thirdPathSavingIOFileSetCheckBox, CC.xy(3, 1));
					thirdIPathSavingIOFileSetPanel.add(thirdPathSavingIOFileSetTextField, CC.xy(5, 1));

					//---- thirdPathSavingIOFileSetButton ----
					thirdPathSavingIOFileSetButton.setText("Path");
					thirdIPathSavingIOFileSetPanel.add(thirdPathSavingIOFileSetButton, CC.xy(7, 1));
				}
				ioFileSetPanel.add(thirdIPathSavingIOFileSetPanel, CC.xy(2, 8));

				//======== writerPanel ========
				{
					writerPanel.setLayout(new FormLayout(
						"default, $ugap, ${growing-button}",
						"default"));

					//---- writerLabel ----
					writerLabel.setText("Writer");
					writerPanel.add(writerLabel, CC.xy(1, 1));
					writerPanel.add(writerTextField, CC.xy(3, 1));
				}
				ioFileSetPanel.add(writerPanel, CC.xy(2, 10));

				//======== menuPanel ========
				{
					menuPanel.setLayout(new FormLayout(
						"default, $ugap, default",
						"default"));

					//---- rereadingAllMessageInfoFileButton ----
					rereadingAllMessageInfoFileButton.setText("Reread all message info file");
					menuPanel.add(rereadingAllMessageInfoFileButton, CC.xy(1, 1));

					//---- buildingIOSourceFileSetButton ----
					buildingIOSourceFileSetButton.setText("Build IO source file set");
					menuPanel.add(buildingIOSourceFileSetButton, CC.xy(3, 1));
				}
				ioFileSetPanel.add(menuPanel, CC.xy(2, 12));

				//---- resultLabel ----
				resultLabel.setText(">> result rereading all message info file");
				ioFileSetPanel.add(resultLabel, CC.xy(2, 14));

				//======== messageIDSearchPanel ========
				{
					messageIDSearchPanel.setLayout(new FormLayout(
						"default, $ugap, ${growing-button}, $ugap, default",
						"default"));

					//---- messageIDSearchLabel ----
					messageIDSearchLabel.setText("message ID");
					messageIDSearchPanel.add(messageIDSearchLabel, CC.xy(1, 1));
					messageIDSearchPanel.add(messageIDSearchTextField, CC.xy(3, 1));

					//---- messageIDSearchButton ----
					messageIDSearchButton.setText("text");
					messageIDSearchPanel.add(messageIDSearchButton, CC.xy(5, 1));
				}
				ioFileSetPanel.add(messageIDSearchPanel, CC.xy(2, 16));

				//======== messageInfoScrollPane ========
				{

					//---- messageInfoTable ----
					messageInfoTable.setModel(new DefaultTableModel(
						new Object[][] {
							{null, null, "", null, null},
							{null, null, null, null, null},
						},
						new String[] {
							"message id", "recently modified date", "direction", "file function", "build function"
						}
					) {
						Class<?>[] columnTypes = new Class<?>[] {
							String.class, String.class, String.class, Object.class, Object.class
						};
						@Override
						public Class<?> getColumnClass(int columnIndex) {
							return columnTypes[columnIndex];
						}
					});
					messageInfoScrollPane.setViewportView(messageInfoTable);
				}
				ioFileSetPanel.add(messageInfoScrollPane, CC.xy(2, 18));
			}
			sourceBuilderTabbedPane.addTab("source builder for IO file set", ioFileSetPanel);

			//======== eachIOFileTypePanel ========
			{
				eachIOFileTypePanel.setLayout(new FormLayout(
					"3*(default)",
					"6*(default)"));
			}
			sourceBuilderTabbedPane.addTab("source builder for each IO file type", eachIOFileTypePanel);
		}
		add(sourceBuilderTabbedPane, CC.xywh(2, 2, 2, 2));
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner non-commercial license
	private JTabbedPane sourceBuilderTabbedPane;
	private JPanel ioFileSetPanel;
	private JPanel messageInfoFilePathPanel;
	private JLabel messageInfoFilePathLabel;
	private JTextField messageInfoFilePathTextField;
	private JButton messageInfoFilePathButton;
	private JPanel firstPathSavingIOFileSetPanel;
	private JLabel firstPathSavingIOFileSetTitleLabel;
	private JCheckBox firstPathSavingIOFileSetCheckBox;
	private JTextField firstPathSavingIOFileSetTextField;
	private JButton firstPathSavingIOFileSetButton;
	private JPanel secoundPathSavingIOFileSetPanel;
	private JLabel secoundPathSavingIOFileSetLabel;
	private JCheckBox secoundPathSavingIOFileSetCheckBox;
	private JTextField secoundPathSavingIOFileSetTextField;
	private JButton secoundPathSavingIOFileSetButton;
	private JPanel thirdIPathSavingIOFileSetPanel;
	private JLabel thirdPathSavingIOFileSetLabel;
	private JCheckBox thirdPathSavingIOFileSetCheckBox;
	private JTextField thirdPathSavingIOFileSetTextField;
	private JButton thirdPathSavingIOFileSetButton;
	private JPanel writerPanel;
	private JLabel writerLabel;
	private JTextField writerTextField;
	private JPanel menuPanel;
	private JButton rereadingAllMessageInfoFileButton;
	private JButton buildingIOSourceFileSetButton;
	private JLabel resultLabel;
	private JPanel messageIDSearchPanel;
	private JLabel messageIDSearchLabel;
	private JTextField messageIDSearchTextField;
	private JButton messageIDSearchButton;
	private JScrollPane messageInfoScrollPane;
	private JTable messageInfoTable;
	private JPanel eachIOFileTypePanel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
