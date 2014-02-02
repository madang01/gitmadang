/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package kr.pe.sinnori.gui.screen;

import static kr.pe.sinnori.common.lib.CommonRootIF.log;

import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import kr.pe.sinnori.common.exception.MessageItemException;
import kr.pe.sinnori.common.message.ArrayData;
import kr.pe.sinnori.common.message.ItemGroupDataIF;
import kr.pe.sinnori.common.message.OutputMessage;
import kr.pe.sinnori.common.util.DirectoryFirstComparator;
import kr.pe.sinnori.common.util.NameFirstComparator;
import kr.pe.sinnori.gui.lib.LocalFileTreeNode;
import kr.pe.sinnori.gui.lib.MainControllerIF;
import kr.pe.sinnori.gui.lib.RemoteFileTreeNode;
import kr.pe.sinnori.gui.lib.TreeCellRenderer;
import kr.pe.sinnori.gui.screen.fileupdownscreen.FileUpDownScreenIF;
import kr.pe.sinnori.gui.screen.fileupdownscreen.action.DownloadSwingAction;
import kr.pe.sinnori.gui.screen.fileupdownscreen.action.LocalDriverChangeAction;
import kr.pe.sinnori.gui.screen.fileupdownscreen.action.LocalParentSwingAction;
import kr.pe.sinnori.gui.screen.fileupdownscreen.action.LocalReloadSwingAction;
import kr.pe.sinnori.gui.screen.fileupdownscreen.action.LocalTreeMouseListener;
import kr.pe.sinnori.gui.screen.fileupdownscreen.action.RemoteDriverChangeAction;
import kr.pe.sinnori.gui.screen.fileupdownscreen.action.RemoteParentSwingAction;
import kr.pe.sinnori.gui.screen.fileupdownscreen.action.RemoteReloadSwingAction;
import kr.pe.sinnori.gui.screen.fileupdownscreen.action.RemoteTreeMouseListener;
import kr.pe.sinnori.gui.screen.fileupdownscreen.action.UploadSwingAction2;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

/**
 * 파일 송수신 버전2 화면
 * @author Jonghoon Won
 *
 */
@SuppressWarnings("serial")
public class FileUpDownScreen2 extends JPanel implements FileUpDownScreenIF {
	private JFrame mainFrame = null;
	private MainControllerIF mainController = null;

	private LocalFileTreeNode localRootNode = null;
	private JTree localTree = null;
	private JComboBox<String> localDriverComboBox = new JComboBox<String>();

	private RemoteFileTreeNode remoteRootNode = null;
	private JTree remoteTree = null;	
	private JComboBox<String> remoteDriverComboBox = new JComboBox<String>();
	private String remotePathSeperator = null;
	
	/**
	 * Create the panel.
	 */
	public FileUpDownScreen2(final JFrame mainFrame, MainControllerIF mainController) {
		this.mainFrame = mainFrame;
		this.mainController = mainController;
		
		try {
			localRootNode = new LocalFileTreeNode(new File(".").getCanonicalFile(), 0L, RemoteFileTreeNode.DIRECTORY);

			/**
			 * <pre> 
			 * 로컬 트리에서 루트 노드를 절대패스로 보여주기위해서
			 * 로컬 루트 로드 생성후 딱 1번 사용자 정의 객체를 절대패스로 다시 설정한다.
			 * 이후에는 루트 로드는 재 성성없이 트리 구조 
			 * 
			 * 참고) 트리 노드는 사용자 정의 객체 toString 으로 얻은 문자열을 보여준다.
			 * </pre> 
			 */
			localRootNode.setUserObject(localRootNode.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		remoteRootNode = new RemoteFileTreeNode("파일 목록을 받는중입니다.", 0L,
				RemoteFileTreeNode.DIRECTORY);
		
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("300px:grow"),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				FormFactory.GROWING_BUTTON_COLSPEC,
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("300px:grow"),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.LINE_GAP_ROWSPEC,
				RowSpec.decode("min:grow"),
				FormFactory.LINE_GAP_ROWSPEC,
				RowSpec.decode("400px:grow"),
				FormFactory.LINE_GAP_ROWSPEC,}));
		
		JPanel localIconPanel = new JPanel();
		add(localIconPanel, "2, 2, fill, fill");
		localIconPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		localDriverComboBox = new JComboBox<String>();
		localDriverComboBox.setModel(new DefaultComboBoxModel<String>(new String[] {"선택"}));
		localIconPanel.add(localDriverComboBox);
		
		JButton localParentButton = new JButton("..");
		localIconPanel.add(localParentButton);
		
		JButton localReloadButton = new JButton("reload");
		localIconPanel.add(localReloadButton);
		
		JPanel remoteIconPanel = new JPanel();
		add(remoteIconPanel, "6, 2, fill, fill");
		remoteIconPanel.setLayout(new BoxLayout(remoteIconPanel, BoxLayout.X_AXIS));
		
		JPanel panel = new JPanel();
		remoteIconPanel.add(panel);
		
		remoteDriverComboBox = new JComboBox<String>();
		remoteDriverComboBox.setModel(new DefaultComboBoxModel<String>(new String[] {"선택"}));
		panel.add(remoteDriverComboBox);
		
		JButton remoteParentButton = new JButton("..");
		panel.add(remoteParentButton);
		
		JButton remoteReloadButton = new JButton("reload");
		panel.add(remoteReloadButton);
		
		JScrollPane localScrollPane = new JScrollPane();
		add(localScrollPane, "2, 4, fill, fill");
		
		localTree = new JTree(localRootNode);
		
		localScrollPane.setViewportView(localTree);
		
		JPanel centerPanel = new JPanel();
		add(centerPanel, "4, 4, fill, center");
		centerPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.GROWING_BUTTON_COLSPEC,},
			new RowSpec[] {
				FormFactory.GLUE_ROWSPEC,
				FormFactory.MIN_ROWSPEC,
				FormFactory.LINE_GAP_ROWSPEC,
				FormFactory.MIN_ROWSPEC,
				FormFactory.GLUE_ROWSPEC,}));
		
		JButton uploadButton = new JButton("upload");
		centerPanel.add(uploadButton, "1, 2");
		
		JButton downloadButton = new JButton("download");
		centerPanel.add(downloadButton, "1, 4");
		
		JScrollPane remoteScrollPane = new JScrollPane();
		add(remoteScrollPane, "6, 4, fill, fill");
		
		remoteTree = new JTree(remoteRootNode);
		
		remoteScrollPane.setViewportView(remoteTree);
		
		
		
		
		TreeCellRenderer treeCellRenderer = new TreeCellRenderer();
		
		
		/** 로컬 드라이브 */
		localDriverComboBox.setMaximumRowCount(24);
		
		String OSName = System.getProperty("os.name").toLowerCase();
		// FIXME!
		log.info(String.format("OSName=[%s]", OSName));
		
		if (OSName.contains("win")) {
			localDriverComboBox.setVisible(true);

			File[] roots = File.listRoots();
			for (File root : roots) {
				String drive = root.getAbsolutePath();
				// double size = root.getTotalSpace() / Math.pow(1024, 3);
				
				
				localDriverComboBox.addItem(drive);
			}
		} else {
			localDriverComboBox.setVisible(false);
		}
		
		LocalDriverChangeAction localDriverChangeAction = new LocalDriverChangeAction(mainFrame, this, localTree, localRootNode);
		localDriverComboBox.setAction(localDriverChangeAction);
		
		LocalParentSwingAction localParentAction = new LocalParentSwingAction(mainFrame, this, localTree, localRootNode);
		localParentAction.putValue(AbstractAction.NAME, localParentButton.getText());
		localParentButton.setAction(localParentAction);
		
		LocalReloadSwingAction localReloadAction = new LocalReloadSwingAction(this);
		localReloadAction.putValue(AbstractAction.NAME, localReloadButton.getText());
		localReloadButton.setAction(localReloadAction);
		
		
		remoteDriverComboBox.setMaximumRowCount(24);
		RemoteDriverChangeAction remoteDriverChangeAction = new RemoteDriverChangeAction(mainFrame, mainController, this, remoteTree, remoteRootNode, remotePathSeperator);
		remoteDriverComboBox.setAction(remoteDriverChangeAction);
		
		RemoteParentSwingAction remoteParentAction = new RemoteParentSwingAction(mainFrame, mainController, this, remoteTree, remoteRootNode, remotePathSeperator);
		remoteParentAction.putValue(AbstractAction.NAME,
				remoteParentButton.getText());
		remoteParentButton.setAction(remoteParentAction);
		
		RemoteReloadSwingAction remoteReloadAction = new RemoteReloadSwingAction(this);
		remoteReloadAction.putValue(AbstractAction.NAME,
				remoteReloadButton.getText());
		remoteReloadButton.setAction(remoteReloadAction);
		
		localTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		localTree.setShowsRootHandles(true);
		ToolTipManager.sharedInstance().registerComponent(localTree);		
		localTree.setCellRenderer(treeCellRenderer);
		
		LocalTreeMouseListener localTreeMouseListener = new LocalTreeMouseListener(this.mainFrame, this, localTree, localRootNode);
		localTree.addMouseListener(localTreeMouseListener);
		
		remoteTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		remoteTree.setShowsRootHandles(true);
		ToolTipManager.sharedInstance().registerComponent(remoteTree);
		remoteTree.setCellRenderer(treeCellRenderer);
		
		RemoteTreeMouseListener remoteMouseListener = 
				new RemoteTreeMouseListener(mainFrame, mainController, this, remoteTree, remoteRootNode, remotePathSeperator);
		remoteTree.addMouseListener(remoteMouseListener);
		
		UploadSwingAction2 uploadAction = new UploadSwingAction2(this.mainFrame, mainController, this, localTree, localRootNode, remoteTree, remoteRootNode, remotePathSeperator);
		uploadAction.putValue(AbstractAction.NAME, uploadButton.getText());
		uploadButton.setAction(uploadAction);
		
		DownloadSwingAction downloadAction = new DownloadSwingAction(this.mainFrame, mainController, this, localTree, localRootNode, remoteTree, remoteRootNode, remotePathSeperator);
		downloadAction.putValue(AbstractAction.NAME, downloadButton.getText());
		downloadButton.setAction(downloadAction);
		
		long start = System.currentTimeMillis();
		makeLocalTreeNode(localRootNode);
		long end = System.currentTimeMillis();
		
		// FIXME!
		log.info(String.format("소요시간=[%d]", end - start));
		
		repaintTree(localTree);
	}
	
	public void init() {
		remoteRootNode.chageFileName(".");
		reloadRemoteFileList();
	}

	/**
	 * 원격지 경로 구분자를 반환한다. 주의점) 원격지 파일 목록 출력 메시지를 받은후에 설정되는 값이다.
	 * @return 원격지 경로 구분자
	 */
	public String getRemotePathSeperator() {
		return remotePathSeperator;
	}

	public void reloadLocalFileList() {
		localRootNode.removeAllChildren();

		makeLocalTreeNode(localRootNode);
		repaintTree(localTree);
	}
	
	public void reloadRemoteFileList() {
		OutputMessage fileListOutObj = mainController
				.getRemoteFileList(remoteRootNode.getFileName());
		if (null == fileListOutObj) return;
		

		remoteRootNode.removeAllChildren();
		try {
			makeRemoteTreeNode(fileListOutObj, remoteRootNode);
			
			repaintTree(remoteTree);
		} catch (MessageItemException e) {
			log.warn("MessageItemException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return;
		}
	}
	
	public void repaintTree(JTree targetTree) {
		DefaultTreeModel localDefaultTreeModel = (DefaultTreeModel) targetTree
				.getModel();
		localDefaultTreeModel.reload();
		targetTree.repaint();
	}
	

	public void makeLocalTreeNode(LocalFileTreeNode localParentNode) {
		if (localDriverComboBox.isDisplayable()) {
			localDriverComboBox.removeAllItems();
			localDriverComboBox.addItem("선택");
			
			File[] roots = File.listRoots();
			for (File root : roots) {
				String drive = root.getAbsolutePath();
				// double size = root.getTotalSpace() / Math.pow(1024, 3);
				
				
				localDriverComboBox.addItem(drive);
			}
		}
		
		File localParnetFile = localParentNode.getFileObj();
		
		File[] subFiles = localParnetFile.listFiles();

		if (null == subFiles)
			return;
		
		Arrays.sort(subFiles, new NameFirstComparator());
		Arrays.sort(subFiles, new DirectoryFirstComparator());
		
		/*
		int gap = 1;
		for (int i = 0; i < subFiles.length; i++) {
			// log.info(String.format("1. i=[%d] fileName=[%s]", i, subFiles[i].getName()));
			if (subFiles[i].isDirectory()) continue;
			for (int j = i+gap; j < subFiles.length; j++) {
				// log.info(String.format("2. i=[%d], j=[%d] fileName=[%s]", i, j, subFiles[j].getName()));
				if (subFiles[j].isDirectory()) {
					gap = j - i;
					File tmp = subFiles[j];
					int k=j;
					for (; k > i; k--) {
						// log.info(String.format("3. move %d to %d", k-1, k));
						subFiles[k] = subFiles[k-1]; 
					}
					subFiles[i] = tmp;
					// log.info(String.format("4. i=[%d], j=[%d], k=[%d]", i, j, k));
					break;
				}
			}
			// log.info(String.format("5. i=[%d] fileName=[%s]", i, subFiles[i].getName()));
		}
		*/

		for (int i = 0; i < subFiles.length; i++) {
			File workFile = subFiles[i];

			// String fileName = workFile.getName();
			// System.out.printf("In makeLocalDirectoryTreeNode, fileName[%d]=[%s]",
			// i, fileName);
			// System.out.println("");

			LocalFileTreeNode localChildNode = null;
			
			if (workFile.isDirectory()) {
				localChildNode = new LocalFileTreeNode(workFile,
						0L, RemoteFileTreeNode.DIRECTORY);

				/**
				 * <pre>
				 * 2013.09.01
				 * (1) 경로 문자열로 넘기는 경우
				 *     - 메소드 getAbsolutePath 와 getCanonicalPath 속도 비교 결과
				 *       getAbsolutePath : 1035 ms
				 *       getCanonicalPath : 201x ms
				 *       DirectoryTreeNode 를 통한 자체적으로 경로 만들기 : 1098 ms
				 * 
				 * %% 결론적으로 getAbsolutePath 가 속도 좋음.
				 * 
				 * (2)  파일 객체 넘기는 경우
				 *    테스트 결과 985 ms, 1010 ms
				 *    
				 *  %% 결론적으로 파일 객체가 경로 문자열 보다 속도 좋음.
				 * </pre>
				 */
			} else {
				localChildNode = new LocalFileTreeNode(workFile,
						workFile.length(), RemoteFileTreeNode.FILE);
			}
			
			localParentNode.add(localChildNode);
		}
	}
	
	public void makeRemoteTreeNode(OutputMessage fileListOutObj,
			RemoteFileTreeNode remoteParentNode) throws MessageItemException {
		String requestDirectory = (String) fileListOutObj
				.getAttribute("requestDirectory");
		
		if (null == remotePathSeperator) {
			remotePathSeperator = (String) fileListOutObj.getAttribute("pathSeperator");
		}

		// FIXME!
		log.info(String.format("requestDirectory=[%s], pathSeperator=[%s]", requestDirectory, remotePathSeperator));

		int cntOfDriver = (Integer) fileListOutObj.getAttribute("cntOfDriver");
		if (0 == cntOfDriver) {
			remoteDriverComboBox.setVisible(false);
			remoteDriverComboBox.removeAllItems();
			remoteDriverComboBox.addItem("선택");
		} else {
			remoteDriverComboBox.setVisible(true);
			remoteDriverComboBox.removeAllItems();
			remoteDriverComboBox.addItem("선택");
			
			ArrayData driverListOfOutObj = (ArrayData) fileListOutObj.getAttribute("driverList");
			for (int i = 0; i < cntOfDriver; i++) {
				ItemGroupDataIF driverOfOutObj = driverListOfOutObj.get(i);
				String driverName = (String)driverOfOutObj.getAttribute("driverName");
				remoteDriverComboBox.addItem(driverName);
			}
		}
		

		int cntOfFile = (Integer) fileListOutObj.getAttribute("cntOfFile");

		remoteParentNode.chageFileName(requestDirectory);

		ArrayData fileListOfOutObj = (ArrayData) fileListOutObj
				.getAttribute("fileList");

		for (int i = 0; i < cntOfFile; i++) {

			ItemGroupDataIF fileOfOutObj = fileListOfOutObj.get(i);

			String fileName = (String) fileOfOutObj.getAttribute("fileName");
			long fileSize = (Long) fileOfOutObj.getAttribute("fileSize");

			/** 파일 종류, 1:디렉토리, 0:파일 */
			byte fileType = (Byte) fileOfOutObj.getAttribute("fileType");

			RemoteFileTreeNode remoteChildNode = new RemoteFileTreeNode(fileName, fileSize,
					fileType);
			remoteParentNode.add(remoteChildNode);
		}
	}
	
	
}
