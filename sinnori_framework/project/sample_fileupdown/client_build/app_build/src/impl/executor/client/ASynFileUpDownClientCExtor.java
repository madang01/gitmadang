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
import kr.pe.sinnori.gui.lib.AsynMainControllerIF;
import kr.pe.sinnori.gui.screen.ConnectionScreen;
import kr.pe.sinnori.gui.screen.FileTranferProcessDialog;
import kr.pe.sinnori.gui.screen.asynfileupdownscreen.AsynFileUpDownScreen;
import kr.pe.sinnori.gui.screen.asynfileupdownscreen.task.AsynDownloadFileTransferTask;
import kr.pe.sinnori.gui.screen.asynfileupdownscreen.task.AsynFileUpDownOutputMessageTask;
import kr.pe.sinnori.gui.screen.asynfileupdownscreen.task.AsynUploadFileTransferTask;
import kr.pe.sinnori.impl.message.AsynCancelDownloadFile.AsynCancelDownloadFile;
import kr.pe.sinnori.impl.message.AsynCancelUploadFile.AsynCancelUploadFile;
import kr.pe.sinnori.impl.message.AsynDownFileDataAll.AsynDownFileDataAll;
import kr.pe.sinnori.impl.message.AsynUpFileData.AsynUpFileData;
import kr.pe.sinnori.impl.message.BinaryPublicKey.BinaryPublicKey;
import kr.pe.sinnori.impl.message.CancelDownloadFileResult.CancelDownloadFileResult;
import kr.pe.sinnori.impl.message.CancelUploadFileResult.CancelUploadFileResult;
import kr.pe.sinnori.impl.message.DownFileDataResult.DownFileDataResult;
import kr.pe.sinnori.impl.message.DownFileInfoResult.DownFileInfoResult;
import kr.pe.sinnori.impl.message.FileListRequest.FileListRequest;
import kr.pe.sinnori.impl.message.FileListResult.FileListResult;
import kr.pe.sinnori.impl.message.Login.Login;
import kr.pe.sinnori.impl.message.LoginEcho.LoginEcho;
import kr.pe.sinnori.impl.message.MessageResult.MessageResult;
import kr.pe.sinnori.impl.message.SelfExn.SelfExn;
import kr.pe.sinnori.impl.message.SyncDownFileInfo.SyncDownFileInfo;
import kr.pe.sinnori.impl.message.SyncUpFileInfo.SyncUpFileInfo;
import kr.pe.sinnori.impl.message.UpFileDataResult.UpFileDataResult;
import kr.pe.sinnori.impl.message.UpFileInfoResult.UpFileInfoResult;
import kr.pe.sinnori.util.AbstractClientExecutor;

import org.apache.commons.codec.binary.Base64;

/**
 * 샘플 파일 송수신 클라이언트 버전2
 * @author madang01
 *
 */
public class ASynFileUpDownClientCExtor extends AbstractClientExecutor implements AsynMainControllerIF {
	// private final Object monitor = new Object();	
	private AbstractConnection conn = null; 
	
	private JFrame mainFrame = null;

	private ConnectionScreen connectionScreen = null;
	private AsynFileUpDownScreen fileUpDownScreen = null;
	private FileTranferProcessDialog fileProcessDialog = null;
	

	private ClientProjectIF clientProject = null;
	private ClientProjectConfig clientProjectConfig = null;
	
	private ClientSessionKeyManager clientSessionKeyManager = null;
	
	private AsynFileUpDownOutputMessageTask  fileUpDown2AsynOutputMessageTask = null;
	
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
		
		fileUpDownScreen = new AsynFileUpDownScreen(mainFrame, this);
	}
		
	
	@Override
	protected void doTask(ClientProjectConfig clientProjectConfig, ClientProjectIF clientProject)
			throws SocketTimeoutException, ServerNotReadyException, NoMoreDataPacketBufferException, 
			BodyFormatException, DynamicClassCallException, ServerTaskException, NotLoginException {
		// connectionOK = this;
		this.clientProject = clientProject;
		this.clientProjectConfig = clientProjectConfig;
		
		this.fileUpDown2AsynOutputMessageTask = new AsynFileUpDownOutputMessageTask(this);
		clientProject.changeAsynOutputMessageTask(fileUpDown2AsynOutputMessageTask);
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					initialize();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	@Override
	public int getFileBlockSize() {
		return (1024*23);
	}
	
	private void freeResource() {
		if (null != conn && conn.isConnected()) conn.serverClose();
		if (null != fileProcessDialog) {
			fileProcessDialog.dispose();
		}
		freeLocalSourceFileResource();
		freeLocalTargetFileResource();	
	}
	
	/**
	 * 연결 화면으로 전환
	 */
	private void changeToConnectionScreen() {
		//synchronized (monitor) {
			if (!fileUpDownScreen.isVisible()) return;
			fileUpDownScreen.setVisible(false);
			freeResource();			
			mainFrame.remove(fileUpDownScreen);
			mainFrame.add(connectionScreen);
			mainFrame.setBounds(mainFrame.getX(), mainFrame.getY(), connectionScreenWidth, connectionScreenHeight);
			connectionScreen.init();
			connectionScreen.setVisible(true);
		//}
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
	
	/** 로그인 성공후 "FileUpDown" 화면으로 이동 */
	private void chanageToFileUpDownScreen() {
		fileUpDownScreen.init();		
		
		connectionScreen.setVisible(false);
		mainFrame.remove(connectionScreen);
		//connectionScreen = null;
		// mainFrame.setBounds(100, 100, 450, 420);
		mainFrame.add(fileUpDownScreen);
		
		if (-1 == fileUpDownScreenWidth) {
			mainFrame.pack();
			fileUpDownScreenWidth = mainFrame.getWidth();
			fileUpDownScreenHeight = mainFrame.getHeight();
		} else {
			mainFrame.setBounds(mainFrame.getX(), mainFrame.getY(), fileUpDownScreenWidth, fileUpDownScreenHeight);
		}
		
		
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
	
	private boolean getAsynOutputMessageForLoginServie(AbstractMessage inObj, String outputMessageID) {
		if (!conn.isConnected()) {
			JOptionPane.showMessageDialog(mainFrame, "서버와의 연결이 끊어 졌습니다.");
			changeToConnectionScreen();
			return false;
		}
		
		try {
			conn.sendAsynInputMessage(inObj);
		} catch (SocketTimeoutException e) {
			log.warn("SocketTimeoutException", e);
			JOptionPane.showMessageDialog(mainFrame, "지정된 연결 시간을 초과하였습니다.");
			
			changeToConnectionScreen();
			return false;
		} catch (ServerNotReadyException e) {
			log.warn("ServerNotReadyException", e);
			JOptionPane.showMessageDialog(mainFrame, "서버가 준비되지 않았습니다. 서버및 네트워크 연결 상태를 점검하세요.");
			
			changeToConnectionScreen();
			return false;
		} catch (NoMoreDataPacketBufferException e) {
			log.warn("NoMoreDataPacketBufferException", e);
			JOptionPane.showMessageDialog(mainFrame, "unknown error::"+e.getMessage());
			return false;
		} catch (BodyFormatException e) {
			log.warn("BodyFormatException", e);
			JOptionPane.showMessageDialog(mainFrame, "unknown error::"+e.getMessage());
			return false;
		} catch (DynamicClassCallException e) {
			log.warn("DynamicClassCallException", e);
			JOptionPane.showMessageDialog(mainFrame, "unknown error::"+e.getMessage());
			return false;
		} catch (NotSupportedException e) {
			log.error("NotSupportedException", e);
			JOptionPane.showMessageDialog(mainFrame, "unknown error::"+e.getMessage());
			
			freeResource();
			System.exit(1);
			return false;
		} 
		
		return true;
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
			localSourceFileResource = localSourceFileResourceManager.pollLocalSourceFileResource(append, 
					localFilePathName, localFileName, localFileSize, 
					remoteFilePathName, remoteFileName, remoteFileSize, 
					fileBlockSize);
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
		// FIXME!
//		log.info("call");
				
		if (null != localSourceFileResource) {
			localSourceFileResourceManager.putLocalSourceFileResource(localSourceFileResource);
			localSourceFileResource = null;
		}
	}
	
	@Override
	public void openUploadProcessDialog(int serverTargetFileID, String mesg, long fileSize, long totalReceivedDataSize) {
		localSourceFileResource.setTargetFileID(serverTargetFileID);
		
		
		AsynUploadFileTransferTask uploadFileTransferTask = new AsynUploadFileTransferTask(mainFrame, this, serverTargetFileID, localSourceFileResource);
		
		// fileUpDownScreen.setIsCanceledUpDownFileTransfer(false);
		fileProcessDialog = new FileTranferProcessDialog(this, mainFrame, mesg, fileSize, totalReceivedDataSize, uploadFileTransferTask);
		fileProcessDialog.setVisible(true);
		fileProcessDialog.setDefaultCloseOperation(
			    JDialog.DO_NOTHING_ON_CLOSE);
		fileProcessDialog.addWindowListener(new WindowAdapter() {
			    public void windowClosing(WindowEvent we) {
			    	localSourceFileResource.cancel();
			    	fileProcessDialog.cancelEvent();
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
	
	// FIXME!
	@Override
	public boolean doUploadFile(int serverTargetFileID, int fileBlockNo, byte[] fileData) {
		if (!conn.isConnected()) {
			JOptionPane.showMessageDialog(mainFrame, "서버와의 연결이 끊어 졌습니다.");
			changeToConnectionScreen();
			return false;
		}
		
		if (null == localSourceFileResource) {
			log.warn("localSourceFileResource is null");
			return false;
		}

		AsynUpFileData  inObj = new AsynUpFileData();
		inObj.setClientSourceFileID(localSourceFileResource.getSourceFileID());
		inObj.setServerTargetFileID(serverTargetFileID);
		inObj.setFileBlockNo(fileBlockNo);
		inObj.setFileData(fileData);
		
		//FIXME!
		// log.info(inObj.toString());
		/** UpFileData2 */
		boolean result = getAsynOutputMessageForLoginServie(inObj, "UpFileDataResult");
		return result;
	}
	
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
			localTargetFileResource = 
					localTargetFileResourceManager.pollLocalTargetFileResource(append, 
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
		
		// FIXME!
		log.info("SyncDownFileInfo={}", inObj.toString());
		
		AbstractMessage outObj = getSyncOutputMessageForLoginServie(inObj, "DownFileInfoResult");
		if (null == outObj) return null;
		
		
		
		try {
			DownFileInfoResult downFileInfoResultOutObj = (DownFileInfoResult) outObj;
			// FIXME!
			log.info("DownFileInfoResult={}", downFileInfoResultOutObj.toString());
			
			
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

			/*JOptionPane.showMessageDialog(mainFrame, 
					String.format("정상적으로 원격지 원본파일[%s]의 로컬 목적지 파일[%s] 다운 로드가 취소 되었습니다.",
							localTargetFileResource.getSourceFileName(), localTargetFileResource.getTargetFileName()));*/
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
		// FIXME!
		// log.info("call openDownloadProcessDialog");
		
		localTargetFileResource.setSourceFileID(serverSourceFileID);
		
		AsynDownloadFileTransferTask downloadFileTransferTask = new AsynDownloadFileTransferTask(this, localTargetFileResource, 5000L);
		
		fileProcessDialog = new FileTranferProcessDialog(this, mainFrame, mesg, fileSize, totalReceivedDataSize, downloadFileTransferTask);
		fileProcessDialog.setDefaultCloseOperation(
			    JDialog.DO_NOTHING_ON_CLOSE);
		fileProcessDialog.addWindowListener(new WindowAdapter() {
			    public void windowClosing(WindowEvent we) {
			    	// cancelDownloadFile(localTargetFileResource.getSourceFileID());
			    	fileProcessDialog.cancelEvent();
			    }
			});
		fileProcessDialog.setVisible(true);
		
		
	}
	
	@Override
	public void endDownloadTask() {
		if (null == localTargetFileResource) {
			log.warn("localTargetFileResource is null");
			return;
		}
		
		log.info("call endDownloadTask");
		
		/** localTargetFileResource 를 null 만들기 전에 파일 다운로드 진행 모달 윈도우를 가장 먼저 닫아야 한다. */
		fileProcessDialog.dispose();
		freeLocalTargetFileResource();
		fileUpDownScreen.reloadLocalFileList();
	}
	
	
	@Override
	public MessageResult doDownloadFileAll() {
		// FIXME!
		log.info("call doDownloadFileAll");
		
		if (!conn.isConnected()) {
			JOptionPane.showMessageDialog(mainFrame, "서버와의 연결이 끊어 졌습니다.");
			changeToConnectionScreen();
			return null;
		}

		AsynDownFileDataAll inObj = new AsynDownFileDataAll();		
		inObj.setServerSourceFileID(localTargetFileResource.getSourceFileID());
		inObj.setClientTargetFileID(localTargetFileResource.getTargetFileID());
		
		// FIXME!
		log.info("AsynDownFileDataAll={}", inObj.toString());
		
		
		/** DownFileDataAll */
		boolean result = getAsynOutputMessageForLoginServie(inObj, "MessageResult");
		if (!result) return null;
		MessageResult messageResultOutObj = new MessageResult();
		messageResultOutObj.setTaskMessageID("DownFileDataAll");
		messageResultOutObj.setTaskResult("Y");
		messageResultOutObj.setResultMessage("가상적으로 성공 처리");
		
		return messageResultOutObj;
	}
	
	
		
	@Override
	public CancelUploadFileResult cancelUploadFile() {
		if (!conn.isConnected()) {
			/** 서버로 파일 조각을 보내는 파일 업로드 작업중이므로 첫화면으로 가지 않고 이곳에서는 단지 에러 메시지만 보여주면 된다. */
			JOptionPane.showMessageDialog(mainFrame, "서버와의 연결이 끊어 졌습니다.");
			return null;
		}
		
		AsynCancelUploadFile inObj = new AsynCancelUploadFile();
		inObj.setClientSourceFileID(localSourceFileResource.getSourceFileID());
		inObj.setServerTargetFileID(localSourceFileResource.getTargetFileID());		
		
		boolean result = getAsynOutputMessageForLoginServie(inObj, "CancelUploadFileResult");
		if (!result) return null;
		CancelUploadFileResult cancelUploadFileResultOutObj = new CancelUploadFileResult();
		cancelUploadFileResultOutObj.setTaskResult("Y");
		cancelUploadFileResultOutObj.setResultMessage("가상적으로 성공 처리");
		
		return cancelUploadFileResultOutObj;
	}
	
	@Override
	public CancelDownloadFileResult cancelDownloadFile() {
		/** FIMME!*/ 
		log.info("call cancelDownloadFile");
		
		if (!conn.isConnected()) {
			JOptionPane.showMessageDialog(mainFrame, "서버와의 연결이 끊어 졌습니다.");
			changeToConnectionScreen();
			return null;
		}
		
		AsynCancelDownloadFile inObj = new AsynCancelDownloadFile();
		inObj.setServerSourceFileID(localTargetFileResource.getSourceFileID());
		inObj.setClientTargetFileID(localTargetFileResource.getSourceFileID());
		
		boolean result = getAsynOutputMessageForLoginServie(inObj, "CancelDownloadFileResult");
		if (!result) return null;

		CancelDownloadFileResult cancelDownloadFileResult = new CancelDownloadFileResult();
		cancelDownloadFileResult.setTaskResult("Y");
		cancelDownloadFileResult.setResultMessage("가상적으로 성공 처리");
		return cancelDownloadFileResult;
	}
	
	@Override
	public void doAsynOutputMessageTask(AbstractMessage outObj) {
		// FIXME!
		//log.info(String.format("projectName[%s] %s", clientProjectConfig.getProjectName(), outObj.toString()));

		// String messageID = outObj.getMessageID();
		
		try {			
			if (outObj instanceof UpFileDataResult) {
				if (null == localSourceFileResource) {
					String errorMessage = String.format("localSourceFileResource is null but outputmessage[UpFileDataResult] sent, %s", outObj.toString());
					log.warn(errorMessage);
					return;
				}
				
				UpFileDataResult upFileDataResultOutObj = (UpFileDataResult) outObj;
				int clientSourceFileID = upFileDataResultOutObj.getClientSourceFileID();
				int serverTargetFileID = upFileDataResultOutObj.getServerTargetFileID();
				int fileBlockNo = upFileDataResultOutObj.getFileBlockNo();
				
				
				if (clientSourceFileID != localSourceFileResource.getSourceFileID()
						|| serverTargetFileID != localSourceFileResource.getTargetFileID()) {
					String errorMessage = String.format("작업중인 파일 업로드 작업[%s]과 관련 없는 출력 메시지[%s]를 폐기합니다.", localSourceFileResource.toString(), outObj.toString());
					log.warn(errorMessage);
					return;
				}
				
				String taskResult = upFileDataResultOutObj.getTaskResult();
				String resultMessage = upFileDataResultOutObj.getResultMessage();
				if (taskResult.equals("Y")) {
					fileProcessDialog.noticeAddingFileData(localSourceFileResource.getByteArrayOfFileBlockNo(fileBlockNo).length);
				} else {
					if (!localSourceFileResource.isCanceled()) {
						localSourceFileResource.cancel();						
						JOptionPane.showMessageDialog(mainFrame, resultMessage);
					}
				}
			} else if (outObj instanceof CancelUploadFileResult) {				
				CancelUploadFileResult cancelUploadFileResultOutObj = (CancelUploadFileResult) outObj;
				String resultMessage = cancelUploadFileResultOutObj.getResultMessage();
						
				JOptionPane.showMessageDialog(mainFrame, resultMessage);
				
				log.info(outObj.toString());
			} else if (outObj instanceof DownFileDataResult) {
				if (null == localTargetFileResource) {
					String errorMessage = String.format("localTargetFileResource is null but outputmessage[DownFileDataResult] sent, %s", outObj.toString());
					log.warn(errorMessage);
					return;
				}				
				
				DownFileDataResult downFileDataResultOutObj = (DownFileDataResult) outObj;
				int serverSourceFileID = downFileDataResultOutObj.getServerSourceFileID();
				int clientTargetFileID = downFileDataResultOutObj.getClientTargetFileID();
				String taskResult = downFileDataResultOutObj.getTaskResult();
				String resultMessage = downFileDataResultOutObj.getResultMessage();
				int fileBlockNo = downFileDataResultOutObj.getFileBlockNo();
				byte[] fileData = downFileDataResultOutObj.getFileData();
				
				if (serverSourceFileID != localTargetFileResource.getSourceFileID() || 
						clientTargetFileID != localTargetFileResource.getTargetFileID()) {
					String errorMessage = String.format("작업중인 파일 다운로드 작업[%s]과 관련 없는 출력 메시지[%s]를 폐기합니다.", localTargetFileResource.toString(), outObj.toString());
					log.warn(errorMessage);
					return;
				}
				
				try {
					if (taskResult.equals("Y")) {
						fileProcessDialog.noticeAddingFileData(fileData.length);
						
						boolean isFinished = localTargetFileResource.writeTargetFileData(fileBlockNo, fileData, true);						
						if (isFinished) {
							log.info(String.format("file download finished::%s", localTargetFileResource.toString()));
							
							fileProcessDialog.stopTask();
						}
					} else {
						if (!localTargetFileResource.isCanceled()) {
							localTargetFileResource.cancel();
							JOptionPane.showMessageDialog(mainFrame, resultMessage);
						}	
					}
				} catch (IllegalArgumentException e) {
					log.warn("IllegalArgumentException", e);
					JOptionPane.showMessageDialog(mainFrame, e.toString());
				} catch (UpDownFileException e) {
					log.warn("UpDownFileException", e);
					JOptionPane.showMessageDialog(mainFrame, e.toString());
				}
			} else if (outObj instanceof CancelDownloadFileResult) {
				if (null == localTargetFileResource) {
					String errorMessage = String.format("localTargetFileResource is null but outputmessage[DownFileDataResult] sent, %s", outObj.toString());
					log.warn(errorMessage);
					return;
				}
				
				CancelDownloadFileResult cancelDownloadFileResultObj = (CancelDownloadFileResult)outObj;
				int serverSourceFileID = cancelDownloadFileResultObj.getServerSourceFileID();
				int clientTargetFileID = cancelDownloadFileResultObj.getClientTargetFileID();
				String resultMessage = cancelDownloadFileResultObj.getResultMessage();
				
				if (serverSourceFileID != localTargetFileResource.getSourceFileID() || 
						clientTargetFileID != localTargetFileResource.getTargetFileID()) {
					String errorMessage = String.format("작업중인 파일 다운로드 작업[%s]과 관련 없는 출력 메시지[%s]를 폐기합니다.", localTargetFileResource.toString(), outObj.toString());
					log.warn(errorMessage);
					return;
				}
				
				localTargetFileResource.cancel();
				fileProcessDialog.stopTask();
				
				
				JOptionPane.showMessageDialog(mainFrame, resultMessage);
			} else if (outObj instanceof SelfExn) {
				log.warn(String.format("projectName[%s] SelfExn, %s", clientProjectConfig.getProjectName(), outObj.toString()));
			} else {
				log.warn(String.format("projectName[%s] unknown output message, %s", clientProjectConfig.getProjectName(), outObj.toString()));
			}
			
		} catch (Exception e) {
			log.warn(String.format("projectName[%s] %s", clientProjectConfig.getProjectName(), e.getMessage()), e);
			JOptionPane.showMessageDialog(mainFrame, "unknown error::"+e.getMessage());
		}
	}
	
	// FIXME!
	public LoginEcho doLoginEcho() {
		if (!conn.isConnected()) {
			JOptionPane.showMessageDialog(mainFrame, "서버와의 연결이 끊어 졌습니다.");
			changeToConnectionScreen();
			return null;
		}

		LoginEcho inObj = new LoginEcho();
		
		/** DownFileDataAll */
		boolean result = getAsynOutputMessageForLoginServie(inObj, "LoginEcho");
		if (!result) return null;
		LoginEcho loginEchoOutObj = new LoginEcho();
		
		return loginEchoOutObj;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		freeResource();
	}
}
