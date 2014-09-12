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

package impl.executor.client;

import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.SocketTimeoutException;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import kr.pe.sinnori.client.ClientProjectIF;
import kr.pe.sinnori.client.connection.AbstractConnection;
import kr.pe.sinnori.common.configuration.ClientProjectConfig;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotLoginException;
import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.common.exception.ServerTaskException;
import kr.pe.sinnori.common.exception.SinnoriUnsupportedEncodingException;
import kr.pe.sinnori.common.exception.SymmetricException;
import kr.pe.sinnori.common.exception.UpDownFileException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.sessionkey.ClientSessionKeyManager;
import kr.pe.sinnori.common.sessionkey.ServerSessionKeyManager;
import kr.pe.sinnori.common.sessionkey.SymmetricKey;
import kr.pe.sinnori.common.updownfile.LocalSourceFileResource;
import kr.pe.sinnori.common.updownfile.LocalSourceFileResourceManager;
import kr.pe.sinnori.common.updownfile.LocalTargetFileResource;
import kr.pe.sinnori.common.updownfile.LocalTargetFileResourceManager;
import kr.pe.sinnori.gui.lib.SyncMainControllerIF;
import kr.pe.sinnori.gui.screen.ConnectionScreen;
import kr.pe.sinnori.gui.screen.FileTranferProcessDialog;
import kr.pe.sinnori.gui.screen.syncfileupdownscreen.SyncFileUpDownScreen;
import kr.pe.sinnori.gui.screen.syncfileupdownscreen.task.SyncDownloadFileTransferTask;
import kr.pe.sinnori.gui.screen.syncfileupdownscreen.task.SyncUploadFileTransferTask;
import kr.pe.sinnori.impl.message.BinaryPublicKey.BinaryPublicKey;
import kr.pe.sinnori.impl.message.CancelDownloadFileResult.CancelDownloadFileResult;
import kr.pe.sinnori.impl.message.CancelUploadFileResult.CancelUploadFileResult;
import kr.pe.sinnori.impl.message.DownFileDataResult.DownFileDataResult;
import kr.pe.sinnori.impl.message.DownFileInfoResult.DownFileInfoResult;
import kr.pe.sinnori.impl.message.FileListRequest.FileListRequest;
import kr.pe.sinnori.impl.message.FileListResult.FileListResult;
import kr.pe.sinnori.impl.message.Login.Login;
import kr.pe.sinnori.impl.message.MessageResult.MessageResult;
import kr.pe.sinnori.impl.message.SyncCancelDownloadFile.SyncCancelDownloadFile;
import kr.pe.sinnori.impl.message.SyncCancelUploadFile.SyncCancelUploadFile;
import kr.pe.sinnori.impl.message.SyncDownFileData.SyncDownFileData;
import kr.pe.sinnori.impl.message.SyncDownFileInfo.SyncDownFileInfo;
import kr.pe.sinnori.impl.message.SyncUpFileData.SyncUpFileData;
import kr.pe.sinnori.impl.message.SyncUpFileInfo.SyncUpFileInfo;
import kr.pe.sinnori.impl.message.UpFileDataResult.UpFileDataResult;
import kr.pe.sinnori.impl.message.UpFileInfoResult.UpFileInfoResult;
import kr.pe.sinnori.util.AbstractClientExecutor;

import org.apache.commons.codec.binary.Base64;

/**
 * 샘플 파일 송수신 클라이언트 버전1
 * @author Jonghoon Won
 *
 */
public class SyncFileUpDownClientCExtor extends AbstractClientExecutor implements SyncMainControllerIF {
	// private final Object monitor = new Object();	
	private AbstractConnection conn = null;
	
	private JFrame mainFrame = null;

	private ConnectionScreen connectionScreen = null;
	private SyncFileUpDownScreen fileUpDownScreen = null;
	private FileTranferProcessDialog fileProcessDialog = null;
	
	
	private ClientProjectIF clientProject = null;
	private ClientProjectConfig clientProjectConfig = null;
	
	private ClientSessionKeyManager clientSessionKeyManager = null;
	
	private LocalSourceFileResourceManager localSourceFileResourceManager = LocalSourceFileResourceManager.getInstance();
	private LocalSourceFileResource localSourceFileResource = null;
	
	
	private LocalTargetFileResourceManager  localTargetFileResourceManager = LocalTargetFileResourceManager.getInstance();
	private LocalTargetFileResource localTargetFileResource = null;
	
	private int connectionScreenWidth = -1, connectionScreenHeight = -1;
	private int fileUpDownScreenWidth = -1, fileUpDownScreenHeight = -1;
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		// log.info("call");
		try {
			conn = clientProject.getConnection();
		} catch (InterruptedException e) {
			log.error("InterruptedException", e);
			System.exit(1);
		} catch (NotSupportedException e) {
			log.error("NotSupportedException", e);
			System.exit(1);
		}		
		
		mainFrame = new JFrame();
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosed(java.awt.event.WindowEvent evt){
				freeResource();
			}
		});
		
		// mainFrame.setBounds(100, 100, 450, 223);
		
		connectionScreen = new ConnectionScreen(clientProjectConfig, mainFrame, this);
		
		mainFrame.add(connectionScreen);
		mainFrame.pack();
		
		
		
		connectionScreenWidth = mainFrame.getWidth();
		connectionScreenHeight = mainFrame.getHeight();
		
		
		connectionScreen.setVisible(true);
		mainFrame.setVisible(true);
		
		fileUpDownScreen = new SyncFileUpDownScreen(mainFrame, this);
		
		// FIXME!
		/*log.info(String.format("1.connectionScreen width=[%d], height=[%d]", connectionScreen.getWidth(), connectionScreen.getHeight()));
		log.info(String.format("1.fileUpDownScreen width=[%d], height=[%d]", fileUpDownScreen.getWidth(), fileUpDownScreen.getHeight()));
		log.info(String.format("1.mainFrame width=[%d], height=[%d]", mainFrame.getWidth(), mainFrame.getHeight()));*/
	}
		
	
	@Override
	protected void doTask(ClientProjectConfig clientProjectConfig, ClientProjectIF clientProject)
			throws SocketTimeoutException, ServerNotReadyException, NoMoreDataPacketBufferException, 
			BodyFormatException, DynamicClassCallException, ServerTaskException, NotLoginException {
		// connectionOK = this;
		this.clientProject = clientProject;
		this.clientProjectConfig = clientProjectConfig;
		
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					// mainFrame = new ConnectionScreen(connectionOK);
					initialize();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	@Override
	public int getFileBlockSize() {
		return (1024*63);
	}
	
	
	
	
	private void freeResource() {
		if (null != conn && conn.isConnected()) conn.serverClose();
		freeLocalSourceFileResource();
		freeLocalTargetFileResource();	
	}
	
	private void changeToConnectionScreen() {
		// synchronized (monitor) {
			if (!fileUpDownScreen.isVisible()) return;
			fileUpDownScreen.setVisible(false);

			freeResource();			
			mainFrame.remove(fileUpDownScreen);
			mainFrame.add(connectionScreen);		
			mainFrame.setBounds(mainFrame.getX(), mainFrame.getY(), connectionScreenWidth, connectionScreenHeight);
			connectionScreen.init();
			connectionScreen.setVisible(true);
		// }
	}
	
	@Override
	public byte[] getBinaryPublicKey(String newServerHost, int newServerPort) {
		/** 변경된 호스트와 포트로 접속할 수 있도록 클라이언트 환경 변수에 저장 */
		clientProjectConfig.changeServerAddress(newServerHost, newServerPort);
		/** 새로운 연결 전에 기존 연결 종료 */
		if (conn.isConnected()) conn.serverClose();
		
		BinaryPublicKey binaryPublicKeyInObj = new BinaryPublicKey();
		binaryPublicKeyInObj.setPublicKeyBytes(ServerSessionKeyManager.getInstance().getPublicKeyBytes());
		AbstractMessage outObj = null;
		
		try {
			outObj = conn.sendSyncInputMessage(binaryPublicKeyInObj);
		} catch (SocketTimeoutException e) {
			log.warn("SocketTimeoutException", e);
			JOptionPane.showMessageDialog(mainFrame, "지정된 연결 시간을 초과하였습니다.");
			return null;
		} catch (ServerNotReadyException e) {
			log.warn("ServerNotReadyException", e);
			JOptionPane.showMessageDialog(mainFrame, "서버가 준비되지 않았습니다. 서버및 네트워크 연결 상태를 점검하세요.");
			return null;
		} catch (NoMoreDataPacketBufferException e) {
			log.warn("NoMoreDataPacketBufferException", e);
			JOptionPane.showMessageDialog(mainFrame, "unknown error::"+e.getMessage());
			return null;
		} catch (BodyFormatException e) {
			log.warn("BodyFormatException", e);
			JOptionPane.showMessageDialog(mainFrame, "unknown error::"+e.getMessage());
			return null;
		} catch (DynamicClassCallException e) {
			log.warn("DynamicClassCallException", e);
			JOptionPane.showMessageDialog(mainFrame, "unknown error::"+e.getMessage());
			return null;
		} catch (ServerTaskException e) {
			log.warn("ServerTaskException", e);
			JOptionPane.showMessageDialog(mainFrame, "unknown error::"+e.getMessage());
			return null;
		} catch (NotLoginException e) {
			/** 공개키 요구 서비스는 비 로그인 서비스인데 로그인을 요구하므로 프로그램을 종료한다. */
			log.error("NotLoginException", e);
			JOptionPane.showMessageDialog(mainFrame, "unknown error::"+e.getMessage());
			System.exit(1);
			return null;
		}
		
		BinaryPublicKey binaryPublicKeyOutObj = (BinaryPublicKey)outObj;		
		
		byte[] binaryPublicKeyBytes = binaryPublicKeyOutObj.getPublicKeyBytes();
		
		try {
			clientSessionKeyManager = new ClientSessionKeyManager(binaryPublicKeyBytes);
		} catch (IllegalArgumentException e) {
			log.warn("IllegalArgumentException", e);
			JOptionPane.showMessageDialog(mainFrame, "unknown error::"+e.getMessage());
			return null;
		} catch (SymmetricException e) {
			log.warn("SymmetricException", e);
			JOptionPane.showMessageDialog(mainFrame, "unknown error::"+e.getMessage());
			return null;
		}
		
		return binaryPublicKeyBytes;
	}
	
	
	
	@Override
	public void login(String id, String pwd) {
		// FIXME!
		log.info(String.format("id=[%s], pwd=[%s]", id, pwd));
		
		
		SymmetricKey symmetricKey = null;
		try {
			symmetricKey = clientSessionKeyManager.getSymmetricKey();
		} catch (SymmetricException e) {
			log.warn("2.SymmetricException", e);
			JOptionPane.showMessageDialog(mainFrame, "unknown error::"+e.getMessage());
			return;
		}
		
		String idCipherBase64 = null;		
		String pwdCipherBase64 = null;
		String sessionKeyBase64 = null;
		String ivBase64 = null;
		
		String charsetNameOfProject = clientProjectConfig.getCharset().name();
		
		try {
			idCipherBase64 = symmetricKey.encryptStringBase64(id, charsetNameOfProject);
		} catch (IllegalArgumentException e) {
			log.warn("아이디 암호화 시도중 IllegalArgumentException", e);
			JOptionPane.showMessageDialog(mainFrame, "unknown error::"+e.getMessage());
			return;
		} catch (SinnoriUnsupportedEncodingException e) {
			log.warn("아이디 암호화 시도중 SinnoriUnsupportedEncodingException", e);
			JOptionPane.showMessageDialog(mainFrame, "unknown error::"+e.getMessage());
			return;
		} catch (SymmetricException e) {
			log.warn("아이디 암호화 시도중 SymmetricException", e);
			JOptionPane.showMessageDialog(mainFrame, "unknown error::"+e.getMessage());
			return;
		}
		
		
		try {
			pwdCipherBase64 = symmetricKey.encryptStringBase64(pwd, charsetNameOfProject);
		} catch (IllegalArgumentException e) {
			log.warn("비밀번호 암호화 시도중 IllegalArgumentException", e);
			JOptionPane.showMessageDialog(mainFrame, "unknown error::"+e.getMessage());
			return;
		} catch (SinnoriUnsupportedEncodingException e) {
			log.warn("비밀번호 암호화 시도중 SinnoriUnsupportedEncodingException", e);
			JOptionPane.showMessageDialog(mainFrame, "unknown error::"+e.getMessage());
			return;
		} catch (SymmetricException e) {
			log.warn("비밀번호 암호화 시도중 SymmetricException", e);
			JOptionPane.showMessageDialog(mainFrame, "unknown error::"+e.getMessage());
			return;
		}
		
		byte[] sessionKeyBytes = null;
		try {
			sessionKeyBytes = clientSessionKeyManager.getSessionKey();
		} catch (SymmetricException e) {
			log.warn(e.getMessage());
			JOptionPane.showMessageDialog(mainFrame, "unknown error::"+e.getMessage());
			return;
		}
		
		byte[] ivBytes = symmetricKey.getIV();
		
		sessionKeyBase64 = Base64.encodeBase64String(sessionKeyBytes);
		ivBase64 = Base64.encodeBase64String(ivBytes);
		
		
		Login loginInObj = new Login();
		loginInObj.setIdCipherBase64(idCipherBase64);
		loginInObj.setPwdCipherBase64(pwdCipherBase64);
		loginInObj.setSessionKeyBase64(sessionKeyBase64);
		loginInObj.setIvBase64(ivBase64);
		
		AbstractMessage outObj = null;
		
		try {
			outObj = conn.sendSyncInputMessage(loginInObj);			
		} catch (SocketTimeoutException e) {
			log.warn("SocketTimeoutException", e);
			JOptionPane.showMessageDialog(mainFrame, "지정된 연결 시간을 초과하였습니다.");
			/** 로그인 요구 */
			return;
		} catch (ServerNotReadyException e) {
			log.warn("ServerNotReadyException", e);
			JOptionPane.showMessageDialog(mainFrame, "서버가 준비되지 않았습니다. 서버및 네트워크 연결 상태를 점검하세요.");
			/** 로그인 요구 */
			return;
		} catch (NoMoreDataPacketBufferException e) {
			log.warn("NoMoreDataPacketBufferException", e);
			JOptionPane.showMessageDialog(mainFrame, "unknown error::"+e.getMessage());
			return;
		} catch (BodyFormatException e) {
			log.warn("BodyFormatException", e);
			JOptionPane.showMessageDialog(mainFrame, "unknown error::"+e.getMessage());
			return;
		} catch (DynamicClassCallException e) {
			log.warn("DynamicClassCallException", e);
			JOptionPane.showMessageDialog(mainFrame, "unknown error::"+e.getMessage());
			return;
		} catch (ServerTaskException e) {
			log.warn("ServerTaskException", e);
			JOptionPane.showMessageDialog(mainFrame, "unknown error::"+e.getMessage());
			return;
		} catch (NotLoginException e) {
			log.warn("NotLoginException", e);
			JOptionPane.showMessageDialog(mainFrame, "unknown error::"+e.getMessage());
			return;
		}
		
		// FIXME!
		log.info(outObj.toString());
				
		MessageResult messageResultOutObj = (MessageResult)outObj;
		
		if (messageResultOutObj.getTaskResult().equals("N")) {
			/** 로그인 실패 */
			JOptionPane.showMessageDialog(mainFrame, messageResultOutObj.getResultMessage());
			return;
		}
		
		
		/** 로그인 성공후 "FileUpDown" 화면으로 이동 */
		chanageToFileUpDownScreen();
	}
	
	/**
	 * 로그인 성공후 "FileUpDown" 화면으로 이동
	 */
	private void chanageToFileUpDownScreen() {
		fileUpDownScreen.init();
		
		connectionScreen.setVisible(false);
		mainFrame.remove(connectionScreen);
		// connectionScreen = null;
		// mainFrame.setBounds(100, 100, 450, 420);
		mainFrame.add(fileUpDownScreen);
		
		if (-1 == fileUpDownScreenWidth) {
			mainFrame.pack();
			fileUpDownScreenWidth = mainFrame.getWidth();
			fileUpDownScreenHeight = mainFrame.getHeight();
		} else {
			mainFrame.setBounds(mainFrame.getX(), mainFrame.getY(), fileUpDownScreenWidth, fileUpDownScreenHeight);
		}
		
		// FIXME!
		// log.info(String.format("2. mainFrame width=[%d], height=[%d]", fileUpDownScreenWidth, fileUpDownScreenHeight));
		
		
		fileUpDownScreen.setVisible(true);
	}
	
	/**
	 * <pre>
	 * 입력 메시지를 보내 로그인을 요구하는 입력 메시지 1:1 대응 서버 비지니스 로직으로부터 출력 메시지를 얻는다.
	 * </pre>
	 *   
	 * @param 입력 메시지
	 * @param outputMessageID 출력 메시지 식별자, 주) 입력 메시지 1:1 대응 서버 비지니스 로직이 보내는 출력 메시지 식별자와 일치해야한다.
	 * @return 출력 메시지
	 */
	private AbstractMessage getSyncOutputMessageForLoginServie(AbstractMessage inObj, String outputMessageID) {
		if (!conn.isConnected()) {
			JOptionPane.showMessageDialog(mainFrame, "서버와의 연결이 끊어 졌습니다.");
			changeToConnectionScreen();
			return null;
		}
		
		AbstractMessage outObj = null;
		try {
			outObj = conn.sendSyncInputMessage(inObj);
			if (null == outObj) {
				String errorMessage = String.format("입력 메시지[%s]의 응답 메시지가 없습니다.", inObj.getMessageID()); 
				log.warn(errorMessage);
				JOptionPane.showMessageDialog(mainFrame, errorMessage);
				return null;
			}
		} catch (SocketTimeoutException e) {
			log.warn("SocketTimeoutException", e);
			JOptionPane.showMessageDialog(mainFrame, "지정된 연결 시간을 초과하였습니다.");				
			changeToConnectionScreen();
			return null;
		} catch (ServerNotReadyException e) {
			log.warn("ServerNotReadyException", e);
			JOptionPane.showMessageDialog(mainFrame, "서버가 준비되지 않았습니다. 서버및 네트워크 연결 상태를 점검하세요.");
			
			changeToConnectionScreen();
			return null;
		} catch (NoMoreDataPacketBufferException e) {
			log.warn("NoMoreDataPacketBufferException", e);
			JOptionPane.showMessageDialog(mainFrame, "unknown error::"+e.getMessage());
			return null;
		} catch (BodyFormatException e) {
			log.warn("BodyFormatException", e);
			JOptionPane.showMessageDialog(mainFrame, "unknown error::"+e.getMessage());
			return null;
		} catch (DynamicClassCallException e) {
			log.warn("DynamicClassCallException", e);
			JOptionPane.showMessageDialog(mainFrame, "unknown error::"+e.getMessage());
			return null;
		} catch (ServerTaskException e) {
			log.warn("ServerTaskException", e);
			JOptionPane.showMessageDialog(mainFrame, "unknown error::"+e.getMessage());
			return null;
		} catch (NotLoginException e) {
			log.warn("NotLoginException", e);
			JOptionPane.showMessageDialog(mainFrame, "unknown error::"+e.getMessage());
			return null;
		}
		
		return outObj;
	}
	
	@Override
	public FileListResult getRemoteFileList(String requestDirectory) {
		if (!conn.isConnected()) {
			JOptionPane.showMessageDialog(mainFrame, "서버와의 연결이 끊어 졌습니다.");
			changeToConnectionScreen();
			return null;
		}
		
		FileListRequest inObj = new FileListRequest();
		inObj.setRequestDirectory(requestDirectory);
		
		AbstractMessage outObj = getSyncOutputMessageForLoginServie(inObj, "FileListResult");
		if (null == outObj) return null;
		try {
			FileListResult fileListResultOutObj = (FileListResult)outObj;
			String taskResult = fileListResultOutObj.getTaskResult();
			String resultMessage = fileListResultOutObj.getResultMessage();
			
			if (taskResult.equals("N")) {
				JOptionPane.showMessageDialog(mainFrame, resultMessage);
				return null;
			}
		
			return fileListResultOutObj;
		} catch (Exception e) {
			log.warn("unknown exception", e);
			JOptionPane.showMessageDialog(mainFrame, "unknown error::"+e.getMessage());
			return null;
		}
	}
	
	@Override
	public UpFileInfoResult readyUploadFile(boolean append, 
			String localFilePathName, String localFileName, long localFileSize, 
			String remoteFilePathName, String remoteFileName, long remoteFileSize, int fileBlockSize) {
		if (!conn.isConnected()) {
			JOptionPane.showMessageDialog(mainFrame, "서버와의 연결이 끊어 졌습니다.");
			changeToConnectionScreen();
			return null;
		}
		
		try {
			localSourceFileResource = localSourceFileResourceManager
					.pollLocalSourceFileResource(append,
					localFilePathName, localFileName, localFileSize, 
					remoteFilePathName, remoteFileName, remoteFileSize, fileBlockSize);
		} catch (IllegalArgumentException e1) {
			String errorMessage = e1.toString();
			log.warn(errorMessage, e1);
			JOptionPane.showMessageDialog(mainFrame, errorMessage);
			return null;
		} catch (UpDownFileException e1) {
			String errorMessage = e1.toString();
			log.warn(errorMessage, e1);
			JOptionPane.showMessageDialog(mainFrame, errorMessage);
			return null;
		}
		
		if (null == localSourceFileResource) {
			JOptionPane.showMessageDialog(mainFrame, "큐로부터 원본 파일 자원 할당에 실패하였습니다.");
			return null;
		}
		
		SyncUpFileInfo inObj = new SyncUpFileInfo();
		if (append) inObj.setAppend((byte)1);
		else inObj.setAppend((byte)0);
		inObj.setClientSourceFileID(localSourceFileResource.getSourceFileID());
		inObj.setLocalFilePathName(localFilePathName);
		inObj.setLocalFileName(localFileName);
		inObj.setLocalFileSize(localFileSize);
		inObj.setRemoteFilePathName(remoteFilePathName);
		inObj.setRemoteFileName(remoteFileName);
		inObj.setRemoteFileSize(remoteFileSize);
		inObj.setFileBlockSize(fileBlockSize);		
		
		AbstractMessage outObj = getSyncOutputMessageForLoginServie(inObj, "UpFileInfoResult");
		if (null == outObj) return null;
		try {
			UpFileInfoResult upFileInfoResultOutObj = (UpFileInfoResult) outObj;
			String taskResult = upFileInfoResultOutObj.getTaskResult();
			String resultMessage = upFileInfoResultOutObj.getResultMessage();
			int serverTargetFileID = upFileInfoResultOutObj.getServerTargetFileID();
			int clientSourceFileID = upFileInfoResultOutObj.getClientSourceFileID();
			
			if (taskResult.equals("N")) {				
				JOptionPane.showMessageDialog(mainFrame, resultMessage);
				return null;
			}
			
			int workingClientSourceFileID = localSourceFileResource.getSourceFileID();
			
			if (clientSourceFileID != workingClientSourceFileID) {
				String errorMessage = String.format("서버 clientSourceFileID[%d] 와 클라이언트 clientSourceFileID[%d] 불일치", clientSourceFileID, workingClientSourceFileID);
				log.warn(errorMessage);				
				JOptionPane.showMessageDialog(mainFrame, errorMessage);
				return null;
			}
			
			localSourceFileResource.setTargetFileID(serverTargetFileID);
			
			return upFileInfoResultOutObj;
		} catch (Exception e) {
			log.warn("unknown exception", e);
			JOptionPane.showMessageDialog(mainFrame, "unknown error::"+e.getMessage());
			return null;
		}
	}
	
	@Override
	public void freeLocalSourceFileResource() {
		if (null != localSourceFileResource) {
			localSourceFileResourceManager.putLocalSourceFileResource(localSourceFileResource);
			localSourceFileResource = null;
		}
	}
	
	@Override
	public void openUploadProcessDialog(int serverTargetFileID, String mesg, long fileSize, long totalReceivedDataSize) {
		localSourceFileResource.setTargetFileID(serverTargetFileID);
		
		SyncUploadFileTransferTask uploadFileTransferTask = new SyncUploadFileTransferTask(mainFrame, this, serverTargetFileID, localSourceFileResource);
		
		fileProcessDialog = new FileTranferProcessDialog(this, mainFrame, mesg, fileSize, totalReceivedDataSize, uploadFileTransferTask);
		fileProcessDialog.setVisible(true);
		fileProcessDialog.setDefaultCloseOperation(
			    JDialog.DO_NOTHING_ON_CLOSE);
		fileProcessDialog.addWindowListener(new WindowAdapter() {
			    public void windowClosing(WindowEvent we) {
			    	localSourceFileResource.cancel();
			    }
			});		
	}
	
	@Override
	public void endUploadTask() {
		if (null == localSourceFileResource) {
			log.warn("localSourceFileResource is null");
			return;
		}
		
		/** localSourceFileResource 를 null 만들기 전에 파일 업로드 진행 모달 윈도우를 가장 먼저 닫아야 한다. */
		fileProcessDialog.dispose();
		freeLocalSourceFileResource();
		fileUpDownScreen.reloadRemoteFileList();
	}
	
	@Override
	public UpFileDataResult doUploadFile(int serverTargetFileID, int fileBlockNo, byte[] fileData) {
		if (!conn.isConnected()) {
			JOptionPane.showMessageDialog(mainFrame, "서버와의 연결이 끊어 졌습니다.");
			changeToConnectionScreen();
			return null;
		}
		
		if (null == localSourceFileResource) {
			log.warn("localSourceFileResource is null");
			return null;
		}
		SyncUpFileData inObj = new SyncUpFileData();
		inObj.setClientSourceFileID(localSourceFileResource.getSourceFileID());
		inObj.setServerTargetFileID(serverTargetFileID);
		inObj.setFileBlockNo(fileBlockNo);
		inObj.setFileData(fileData);
		
		
		//FIXME!
		// log.warn(inObj.toString());
		
		AbstractMessage outObj = getSyncOutputMessageForLoginServie(inObj, "UpFileDataResult");
		if (null == outObj) return null;
		try {			
			UpFileDataResult upFileDataResult = (UpFileDataResult) outObj;
			String taskResult = upFileDataResult.getTaskResult();
			String resultMessage = upFileDataResult.getResultMessage();
			
			if (taskResult.equals("N")) {
				JOptionPane.showMessageDialog(mainFrame, resultMessage);
				return null;
			}
			
			return upFileDataResult;
		} catch (Exception e) {
			log.warn("unknown exception", e);
			JOptionPane.showMessageDialog(mainFrame, "unknown error::"+e.getMessage());
			return null;
		}
	}
	
	// FIXME!
	@Override
	public DownFileInfoResult readyDownloadFile(boolean append, 
			String localFilePathName, String localFileName, long localFileSize,
			String remoteFilePathName, String remoteFileName, long remoteFileSize, int fileBlockSize) {
		if (!conn.isConnected()) {
			JOptionPane.showMessageDialog(mainFrame, "서버와의 연결이 끊어 졌습니다.");
			changeToConnectionScreen();
			return null;
		}
		
		try {
			localTargetFileResource = localTargetFileResourceManager
					.pollLocalTargetFileResource(append,
							remoteFilePathName, remoteFileName, remoteFileSize, 
							localFilePathName, localFileName, localFileSize, fileBlockSize);
		} catch (IllegalArgumentException e1) {
			JOptionPane.showMessageDialog(mainFrame, e1.toString());
			return null;
		} catch (UpDownFileException e1) {
			JOptionPane.showMessageDialog(mainFrame, e1.toString());
			return null;
		}
		
		if (null == localTargetFileResource) {
			JOptionPane.showMessageDialog(mainFrame, "큐로부터 목적지 파일 자원 할당에 실패하였습니다.");
			return null;
		}
				
		SyncDownFileInfo inObj = new SyncDownFileInfo();
		if (append) inObj.setAppend((byte)1);
		else inObj.setAppend((byte)0);		
		inObj.setLocalFilePathName(localFilePathName);
		inObj.setLocalFileName(localFileName);
		inObj.setLocalFileSize(localFileSize);
		inObj.setRemoteFilePathName(remoteFilePathName);
		inObj.setRemoteFileName(remoteFileName);
		inObj.setRemoteFileSize(remoteFileSize);
		inObj.setFileBlockSize(fileBlockSize);	
		inObj.setClientTargetFileID(localTargetFileResource.getTargetFileID());
		
		
		AbstractMessage outObj = getSyncOutputMessageForLoginServie(inObj, "DownFileInfoResult");
		if (null == outObj) return null;
		try {
			// FIXME!
			// log.info(outObj.toString());
			DownFileInfoResult downFileInfoResultOutObj = (DownFileInfoResult) outObj;
			String taskResult = downFileInfoResultOutObj.getTaskResult();
			String resultMessage = downFileInfoResultOutObj.getResultMessage();
			int serverSourceFileID = downFileInfoResultOutObj.getServerSourceFileID();
			
			if (taskResult.equals("N")) {
				JOptionPane.showMessageDialog(mainFrame, resultMessage);
				return null;
			}

			localTargetFileResource.setSourceFileID(serverSourceFileID);		
		
			return downFileInfoResultOutObj;
		} catch (Exception e) {
			log.warn("unknown exception", e);
			JOptionPane.showMessageDialog(mainFrame, "unknown error::"+e.getMessage());
			return null;
		}
	}
	
	
	public boolean makeZeroToDownloadFileSize() {
		try {
			localTargetFileResource.makeZeroSizeFile();
		} catch (UpDownFileException e) {
			log.warn(e.getMessage(), e);
			JOptionPane.showMessageDialog(mainFrame, "unknown error::"+e.getMessage());
			CancelDownloadFileResult outObj = cancelDownloadFile();
			if (null == outObj) return false;

			JOptionPane.showMessageDialog(mainFrame, 
					String.format("정상적으로 원격지 원본파일[%s]의 로컬 목적지 파일[%s] 다운 로드가 취소 되었습니다.",
							localTargetFileResource.getSourceFileName(), localTargetFileResource.getTargetFileName()));
			return false;
		}
		
		return true;
	}
	
	@Override
	public void freeLocalTargetFileResource() {
		if (null == localTargetFileResource) {
			log.warn("localTargetFileResource is null");
			return;
		}
		
		localTargetFileResourceManager.putLocalTargetFileResource(localTargetFileResource);
		localTargetFileResource = null;
	}
	
	@Override
	public void openDownloadProcessDialog(int serverSourceFileID, String mesg, long fileSize, long totalReceivedDataSize) {
		localTargetFileResource.setSourceFileID(serverSourceFileID);
		
		
		SyncDownloadFileTransferTask downloadFileTransferTask = new SyncDownloadFileTransferTask(mainFrame, this, serverSourceFileID, localTargetFileResource);
		
		fileProcessDialog = new FileTranferProcessDialog(this, mainFrame, mesg, fileSize, totalReceivedDataSize, downloadFileTransferTask);
		fileProcessDialog.setVisible(true);
		fileProcessDialog.setDefaultCloseOperation(
			    JDialog.DO_NOTHING_ON_CLOSE);
		fileProcessDialog.addWindowListener(new WindowAdapter() {
			    public void windowClosing(WindowEvent we) {
			    	// fileProcessDialog.cancelTask();
			    	localTargetFileResource.cancel();
			    }
			});
		
	}
	
	@Override
	public void endDownloadTask() {
		if (null == localTargetFileResource) {
			log.warn("localTargetFileResource is null");
			return;
		}
		
		/** localTargetFileResource 를 null 만들기 전에 파일 다운로드 진행 모달 윈도우를 가장 먼저 닫아야 한다. */
		fileProcessDialog.dispose();
		freeLocalTargetFileResource();
		fileUpDownScreen.reloadLocalFileList();
	}
	
	@Override
	public DownFileDataResult doDownloadFile(int serverSourceFileID, int fileBlockNo) {
		if (!conn.isConnected()) {
			JOptionPane.showMessageDialog(mainFrame, "서버와의 연결이 끊어 졌습니다.");
			changeToConnectionScreen();
			return null;
		}
		
		if (null == localTargetFileResource) {
			log.warn("localTargetFileResource is null");
			return null;
		}
		SyncDownFileData inObj = new SyncDownFileData();
		inObj.setServerSourceFileID(serverSourceFileID);
		inObj.setClientTargetFileID(localTargetFileResource.getTargetFileID());
		inObj.setFileBlockNo(fileBlockNo);
		
		
		AbstractMessage outObj = getSyncOutputMessageForLoginServie(inObj, "DownFileDataResult");
		if (null == outObj) return null;
		try {
			DownFileDataResult downFileDataResultOutObj = (DownFileDataResult) outObj;
			String taskResult = downFileDataResultOutObj.getTaskResult();
			String resultMessage = downFileDataResultOutObj.getResultMessage();
			
			if (taskResult.equals("N")) {
				JOptionPane.showMessageDialog(mainFrame, resultMessage);
				return null;
			}
			
			return downFileDataResultOutObj;
		} catch (Exception e) {
			log.warn("unknown exception", e);
			JOptionPane.showMessageDialog(mainFrame, "unknown error::"+e.getMessage());
			return null;
		}
	}
	
	
	
	@Override
	public CancelUploadFileResult cancelUploadFile() {
		if (!conn.isConnected()) {
			JOptionPane.showMessageDialog(mainFrame, "서버와의 연결이 끊어 졌습니다.");
			changeToConnectionScreen();			
			return null;
		}
		
		SyncCancelUploadFile inObj = new SyncCancelUploadFile();
		inObj.setClientSourceFileID(localSourceFileResource.getSourceFileID());
		inObj.setServerTargetFileID(localSourceFileResource.getTargetFileID());
		
		AbstractMessage outObj = getSyncOutputMessageForLoginServie(inObj, "CancelUploadFileResult");
		if (null == outObj) return null;
		try {			
			/*String taskResult = (String)outObj.getAttribute("taskResult");
			String resultMessage = (String)outObj.getAttribute("resultMessage");*/
			// int serverTargetFileID = (Integer)outObj.getAttribute("serverTargetFileID");
			CancelUploadFileResult cancelUploadFileResultOutObj = (CancelUploadFileResult) outObj;
			String taskResult = cancelUploadFileResultOutObj.getTaskResult();
			String resultMessage = cancelUploadFileResultOutObj.getResultMessage();
			
			if (taskResult.equals("N")) {
				JOptionPane.showMessageDialog(mainFrame, resultMessage);
				return null;
			}
			
			return cancelUploadFileResultOutObj;
		} catch (Exception e) {
			log.warn("unknown exception", e);
			JOptionPane.showMessageDialog(mainFrame, "unknown error::"+e.getMessage());
			return null;
		}
	}
	
	@Override
	public CancelDownloadFileResult cancelDownloadFile() {
		if (!conn.isConnected()) {
			JOptionPane.showMessageDialog(mainFrame, "서버와의 연결이 끊어 졌습니다.");
			changeToConnectionScreen();
			return null;
		}
		
		SyncCancelDownloadFile inObj = new SyncCancelDownloadFile();
		inObj.setServerSourceFileID(localTargetFileResource.getSourceFileID());
		inObj.setClientTargetFileID(localTargetFileResource.getSourceFileID());
		
		AbstractMessage outObj = getSyncOutputMessageForLoginServie(inObj, "CancelDownloadFileResult");
		if (null == outObj) return null;
		try {			
			CancelDownloadFileResult cancelDownloadFileResult = (CancelDownloadFileResult) outObj;
			String taskResult = cancelDownloadFileResult.getTaskResult();
			String resultMessage = cancelDownloadFileResult.getResultMessage();
			
			if (taskResult.equals("N")) {
				JOptionPane.showMessageDialog(mainFrame, resultMessage);
				return null;
			}
			
			return cancelDownloadFileResult;
		} catch (Exception e) {
			log.warn("unknown exception", e);
			JOptionPane.showMessageDialog(mainFrame, "unknown error::"+e.getMessage());
			return null;
		}
	}
	
	
	
	@Override
	protected void finalize() throws Throwable {
		freeResource();
	}
}
