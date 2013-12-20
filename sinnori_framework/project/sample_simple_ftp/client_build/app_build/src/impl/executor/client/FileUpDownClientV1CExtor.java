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
import kr.pe.sinnori.client.io.LetterFromServer;
import kr.pe.sinnori.common.exception.BodyFormatException;
import kr.pe.sinnori.common.exception.DynamicClassCallException;
import kr.pe.sinnori.common.exception.MessageInfoNotFoundException;
import kr.pe.sinnori.common.exception.MessageItemException;
import kr.pe.sinnori.common.exception.NoMatchOutputMessage;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.NotLoginException;
import kr.pe.sinnori.common.exception.NotSupportedException;
import kr.pe.sinnori.common.exception.ServerExcecutorUnknownException;
import kr.pe.sinnori.common.exception.ServerNotReadyException;
import kr.pe.sinnori.common.exception.SinnoriUnsupportedEncodingException;
import kr.pe.sinnori.common.exception.SymmetricException;
import kr.pe.sinnori.common.exception.UpDownFileException;
import kr.pe.sinnori.common.lib.CommonProjectInfo;
import kr.pe.sinnori.common.lib.MessageMangerIF;
import kr.pe.sinnori.common.message.InputMessage;
import kr.pe.sinnori.common.message.OutputMessage;
import kr.pe.sinnori.common.sessionkey.ClientSessionKeyManager;
import kr.pe.sinnori.common.sessionkey.ServerSessionKeyManager;
import kr.pe.sinnori.common.sessionkey.SymmetricKey;
import kr.pe.sinnori.common.updownfile.LocalSourceFileResource;
import kr.pe.sinnori.common.updownfile.LocalSourceFileResourceManager;
import kr.pe.sinnori.gui.lib.FileTransferTaskIF;
import kr.pe.sinnori.gui.lib.MainControllerIF;
import kr.pe.sinnori.gui.lib.UploadFileTransferTask;
import kr.pe.sinnori.gui.screen.ConnectionScreen;
import kr.pe.sinnori.gui.screen.FileTranferProcessDialog;
import kr.pe.sinnori.gui.screen.FileUpDownScreen;
import kr.pe.sinnori.util.AbstractClientExecutor;

import org.apache.commons.codec.binary.Base64;

/**
 * 샘플 파일 송수신 클라이언트 비지니스 모듈
 * @author Jonghoon Won
 *
 */
public class FileUpDownClientV1CExtor extends AbstractClientExecutor implements MainControllerIF {
	private AbstractConnection conn = null;
	
	private JFrame mainFrame = null;

	private ConnectionScreen connectionScreen = null;
	private FileUpDownScreen fileUpDownScreen = null;
	private FileTranferProcessDialog fileProcessDialog = null;
	

	private MessageMangerIF messageManger = null;
	private ClientProjectIF clientProject = null;
	private CommonProjectInfo commonProjectInfo = null;
	private byte[] binaryPublicKeyBytes = null;
	
	private ClientSessionKeyManager clientSessionKeyManager = null;
	
	private LocalSourceFileResourceManager localSourceFileResourceManager = LocalSourceFileResourceManager.getInstance();
	private LocalSourceFileResource localSourceFileResource = null;
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		// log.info("call");
		try {
			conn = clientProject.getConnection();
		} catch (InterruptedException e) {
			log.fatal("InterruptedException", e);
			System.exit(1);
		} catch (NotSupportedException e) {
			log.fatal("NotSupportedException", e);
			System.exit(1);
		}
		
		
		mainFrame = new JFrame();
		// mainFrame.setBounds(100, 100, 450, 300);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setBounds(100, 100, 450, 223);
		
		connectionScreen = new ConnectionScreen(mainFrame, this);
		
		mainFrame.add(connectionScreen);
		mainFrame.pack();
		
		connectionScreen.setVisible(true);
		mainFrame.setVisible(true);
	}
		
	
	@Override
	protected void doTask(MessageMangerIF messageManger, ClientProjectIF clientProject)
			throws SocketTimeoutException, ServerNotReadyException,
			DynamicClassCallException, NoMoreDataPacketBufferException,
			BodyFormatException, MessageInfoNotFoundException, InterruptedException {
		// connectionOK = this;
		this.messageManger = messageManger;
		this.clientProject = clientProject;
		this.commonProjectInfo = clientProject.getCommonProjectInfo();
		
		
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
	
	public int getFileBlockSize() {
		return (1024*40);
	}
	
	public void loginOK() {
		fileUpDownScreen = new FileUpDownScreen(mainFrame, this);
		
		connectionScreen.setVisible(false);
		mainFrame.remove(connectionScreen);
		connectionScreen = null;
		// mainFrame.setBounds(100, 100, 450, 420);
		mainFrame.add(fileUpDownScreen);
		
		mainFrame.pack();
		
		
		fileUpDownScreen.setVisible(true);
	}
	
	public byte[] connectServer(String host, int port) {
		commonProjectInfo.serverHost = host;
		commonProjectInfo.serverPort = port;
		
		InputMessage binaryPublicKeyInObj = null;
		
		try {
			binaryPublicKeyInObj = messageManger.createInputMessage("BinaryPublicKey");
		} catch (IllegalArgumentException e) {
			log.warn(String.format("IllegalArgumentException::%s", e.getMessage()));
			return null;
		} catch (MessageInfoNotFoundException e) {
			log.warn(String.format("MessageInfoNotFoundException::%s", e.getMessage()));
			return null;
		}
		
		
		try {
			binaryPublicKeyInObj.setAttribute("publicKeyBytes", ServerSessionKeyManager.getInstance().getPublicKeyBytes());
		} catch (MessageItemException e) {
			log.warn(e.getMessage());
			return null;
		}
			
		LetterFromServer letterFromServer = null;
		try {
			letterFromServer = conn.sendInputMessage(binaryPublicKeyInObj);
			
			if (null == letterFromServer) {
				log.warn(String.format("input message[%s] letterFromServer is null", binaryPublicKeyInObj.getMessageID()));
				return null;
			}
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
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (BodyFormatException e) {
			log.warn("BodyFormatException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (MessageInfoNotFoundException e) {
			log.warn("MessageInfoNotFoundException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		}
		
		
		try {
			OutputMessage binaryPublicKeyOutObj = letterFromServer.getOutputMessage("BinaryPublicKey");
			binaryPublicKeyBytes = (byte[])binaryPublicKeyOutObj.getAttribute("publicKeyBytes");
			
		} catch (IllegalArgumentException e) {
			log.warn("IllegalArgumentException", e);
			JOptionPane.showMessageDialog(mainFrame, e.toString());
			return null;
		} catch (BodyFormatException e) {
			log.warn("BodyFormatException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (DynamicClassCallException e) {
			log.warn("DynamicClassCallException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (NoMoreDataPacketBufferException e) {
			log.warn("NoMoreDataPacketBufferException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (MessageInfoNotFoundException e) {
			log.warn("MessageInfoNotFoundException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (NoMatchOutputMessage e) {
			log.warn("NoMatchOutputMessage", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch(MessageItemException e) {
			log.warn("MessageItemException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (ServerExcecutorUnknownException e) {
			log.warn("ServerExcecutorUnknownException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (NotLoginException e) {
			log.warn("NotLoginException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		}

		
		try {
			clientSessionKeyManager = new ClientSessionKeyManager(binaryPublicKeyBytes);
		} catch (IllegalArgumentException e) {
			log.warn("MessageInfoNotFoundException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (SymmetricException e) {
			log.warn("MessageInfoNotFoundException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		}
		
		return binaryPublicKeyBytes;
	}
	
	
	public boolean login(String id, String pwd) {
		// FIXME!
		log.info(String.format("id=[%s], pwd=[%s]", id, pwd));
		
		
		SymmetricKey symmetricKey = null;
		try {
			symmetricKey = clientSessionKeyManager.getSymmetricKey();
		} catch (SymmetricException e) {
			log.warn("2.SymmetricException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return false;
		}
		
		InputMessage loginInObj = null;
		
		try {
			loginInObj = messageManger.createInputMessage("Login");
		} catch (IllegalArgumentException e) {
			log.warn("IllegalArgumentException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return false;
		} catch (MessageInfoNotFoundException e) {
			log.warn("MessageInfoNotFoundException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return false;
		}
		
		
		String idCipherBase64 = null;		
		String pwdCipherBase64 = null;
		String sessionKeyBase64 = null;
		String ivBase64 = null;
		
		String charsetName = commonProjectInfo.charsetOfProject.name();
		
		try {
			idCipherBase64 = symmetricKey.encryptStringBase64(id, charsetName);
		} catch (IllegalArgumentException e) {
			log.warn("아이디 암호화 시도중 IllegalArgumentException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return false;
		} catch (SinnoriUnsupportedEncodingException e) {
			log.warn("아이디 암호화 시도중 SinnoriUnsupportedEncodingException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return false;
		} catch (SymmetricException e) {
			log.warn("아이디 암호화 시도중 SymmetricException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return false;
		}
		
		
		try {
			pwdCipherBase64 = symmetricKey.encryptStringBase64(pwd, charsetName);
		} catch (IllegalArgumentException e) {
			log.warn("비밀번호 암호화 시도중 IllegalArgumentException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return false;
		} catch (SinnoriUnsupportedEncodingException e) {
			log.warn("비밀번호 암호화 시도중 SinnoriUnsupportedEncodingException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return false;
		} catch (SymmetricException e) {
			log.warn("비밀번호 암호화 시도중 SymmetricException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return false;
		}
		
		byte[] sessionKeyBytes = null;
		try {
			sessionKeyBytes = clientSessionKeyManager.getSessionKey();
		} catch (SymmetricException e) {
			log.warn(e.getMessage());
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return false;
		}
		
		byte[] ivBytes = symmetricKey.getIV();
		
		sessionKeyBase64 = Base64.encodeBase64String(sessionKeyBytes);
		ivBase64 = Base64.encodeBase64String(ivBytes);
		
		
		try {
			loginInObj.setAttribute("idCipherBase64", idCipherBase64);
			
			loginInObj.setAttribute("pwdCipherBase64", pwdCipherBase64);
			loginInObj.setAttribute("sessionKeyBase64", sessionKeyBase64);
			loginInObj.setAttribute("ivBase64", ivBase64);
		} catch (MessageItemException e) {
			log.warn("MessageItemException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return false;
		}
		
			
		LetterFromServer letterFromServer = null;
		try {
			letterFromServer = conn.sendInputMessage(loginInObj);
			
			if (null == letterFromServer) {
				String errorMessage = String.format("input message[%s] letterFromServer is null", loginInObj.getMessageID()); 
				log.warn(errorMessage);
				JOptionPane.showMessageDialog(mainFrame, errorMessage);
				return false;
			}
		} catch (SocketTimeoutException e) {
			log.warn("SocketTimeoutException", e);
			JOptionPane.showMessageDialog(mainFrame, "지정된 연결 시간을 초과하였습니다.");
			return false;
		} catch (ServerNotReadyException e) {
			log.warn("ServerNotReadyException", e);
			JOptionPane.showMessageDialog(mainFrame, "서버가 준비되지 않았습니다. 서버및 네트워크 연결 상태를 점검하세요.");
			return false;
		} catch (NoMoreDataPacketBufferException e) {
			log.warn("NoMoreDataPacketBufferException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return false;
		} catch (BodyFormatException e) {
			log.warn("BodyFormatException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return false;
		} catch (MessageInfoNotFoundException e) {
			log.warn("MessageInfoNotFoundException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return false;
		}

		try {
			OutputMessage messageResultOutObj = letterFromServer.getOutputMessage("MessageResult");
			String taskResult = (String)messageResultOutObj.getAttribute("taskResult");
			String resultMessage = (String)messageResultOutObj.getAttribute("resultMessage");
			
			if (taskResult.equals("N")) {
				JOptionPane.showMessageDialog(mainFrame, resultMessage);
				return false;
			}
		} catch (IllegalArgumentException e) {
			log.warn("IllegalArgumentException", e);
			JOptionPane.showMessageDialog(mainFrame, e.toString());
			return false;
		} catch (BodyFormatException e) {
			log.warn("BodyFormatException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return false;
		} catch (DynamicClassCallException e) {
			log.warn("DynamicClassCallException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return false;
		} catch (NoMoreDataPacketBufferException e) {
			log.warn("NoMoreDataPacketBufferException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return false;
		} catch (MessageInfoNotFoundException e) {
			log.warn("MessageInfoNotFoundException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return false;
		} catch (NoMatchOutputMessage e) {
			log.warn("NoMatchOutputMessage", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return false;
		} catch (MessageItemException e) {
			log.warn("MessageItemException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return false;
		} catch (ServerExcecutorUnknownException e) {
			log.warn("ServerExcecutorUnknownException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return false;
		} catch (NotLoginException e) {
			log.warn("NotLoginException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return false;
		}
		
		return true;
	}
	
	public OutputMessage getRemoteFileList(String requestDirectory) {
		InputMessage fileListInObj = null;
		
		try {
			fileListInObj = messageManger.createInputMessage("FileListRequest");
		} catch (IllegalArgumentException e) {
			log.warn("IllegalArgumentException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (MessageInfoNotFoundException e) {
			log.warn("MessageInfoNotFoundException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		}
		
		try {
			fileListInObj.setAttribute("requestDirectory", requestDirectory);
		} catch (MessageItemException e) {
			log.warn("MessageItemException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		}
		
		LetterFromServer letterFromServer = null;
		try {
			letterFromServer = conn.sendInputMessage(fileListInObj);
			
			if (null == letterFromServer) {
				String errorMessage = String.format("input message[%s] letterFromServer is null", fileListInObj.getMessageID()); 
				log.warn(errorMessage);
				JOptionPane.showMessageDialog(mainFrame, errorMessage);
				return null;
			}
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
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (BodyFormatException e) {
			log.warn("BodyFormatException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (MessageInfoNotFoundException e) {
			log.warn("MessageInfoNotFoundException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		}
		
		OutputMessage fileListResultOutObj = null;
		try {
			fileListResultOutObj = letterFromServer.getOutputMessage("FileListResult");
			String taskResult = (String)fileListResultOutObj.getAttribute("taskResult");
			String resultMessage = (String)fileListResultOutObj.getAttribute("resultMessage");
			
			if (taskResult.equals("N")) {
				JOptionPane.showMessageDialog(mainFrame, resultMessage);
				return null;
			}
			
		} catch (IllegalArgumentException e) {
			log.warn("IllegalArgumentException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (BodyFormatException e) {
			log.warn("BodyFormatException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (DynamicClassCallException e) {
			log.warn("DynamicClassCallException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (NoMoreDataPacketBufferException e) {
			log.warn("NoMoreDataPacketBufferException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (MessageInfoNotFoundException e) {
			log.warn("MessageInfoNotFoundException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (NoMatchOutputMessage e) {
			log.warn("NoMatchOutputMessage", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (MessageItemException e) {
			log.warn("MessageItemException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (ServerExcecutorUnknownException e) {
			log.warn("ServerExcecutorUnknownException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (NotLoginException e) {
			log.warn("NotLoginException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		}
		
		return fileListResultOutObj;
	}
	
	
	public OutputMessage readyUploadFile(String localFilePathName, String localFileName, long localFileSize, 
			String remoteFilePathName, String remoteFileName, int fileBlockSize) {
		try {
			localSourceFileResource = localSourceFileResourceManager.pollLocalSourceFileResource(localFilePathName, localFileName, localFileSize, remoteFilePathName, remoteFileName, fileBlockSize);
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
		
		InputMessage inObj = null;
		
		try {
			inObj = messageManger.createInputMessage("UpFileInfo");
		} catch (IllegalArgumentException e) {
			log.warn("IllegalArgumentException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (MessageInfoNotFoundException e) {
			log.warn("MessageInfoNotFoundException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		}
			
		
		
		try {
			inObj.setAttribute("localFilePathName", localFilePathName);
			inObj.setAttribute("localFileName", localFileName);
			inObj.setAttribute("localFileSize", localFileSize);
			inObj.setAttribute("remoteFilePathName", remoteFilePathName);
			inObj.setAttribute("remoteFileName", remoteFileName);
			inObj.setAttribute("fileBlockSize", fileBlockSize);
		} catch (MessageItemException e) {
			log.warn("MessageItemException", e);
			
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		}
		
		
		LetterFromServer letterFromServer = null;
		
		try {
			letterFromServer = conn.sendInputMessage(inObj);
			
			if (null == letterFromServer) {
				String errorMessage = String.format("input message[%s] letterFromServer is null", inObj.getMessageID()); 
				log.warn(errorMessage);
				JOptionPane.showMessageDialog(mainFrame, errorMessage);
				return null;
			}
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
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (BodyFormatException e) {
			log.warn("BodyFormatException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (MessageInfoNotFoundException e) {
			log.warn("MessageInfoNotFoundException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		}
		
		OutputMessage outObj = null;
		try {
			outObj = letterFromServer.getOutputMessage("UpFileInfoResult");
			
			String taskResult = (String)outObj.getAttribute("taskResult");
			String resultMessage = (String)outObj.getAttribute("resultMessage");
			// int serverTargetFileID = (Integer)outObj.getAttribute("serverTargetFileID");
			
			if (taskResult.equals("N")) {
				JOptionPane.showMessageDialog(mainFrame, resultMessage);
				return null;
			}
		} catch (IllegalArgumentException e) {
			log.warn("IllegalArgumentException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (BodyFormatException e) {
			log.warn("BodyFormatException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (DynamicClassCallException e) {
			log.warn("DynamicClassCallException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (NoMoreDataPacketBufferException e) {
			log.warn("NoMoreDataPacketBufferException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (MessageInfoNotFoundException e) {
			log.warn("MessageInfoNotFoundException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (NoMatchOutputMessage e) {
			log.warn("NoMatchOutputMessage", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (MessageItemException e) {
			log.warn("MessageItemException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (ServerExcecutorUnknownException e) {
			log.warn("ServerExcecutorUnknownException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (NotLoginException e) {
			log.warn("NotLoginException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		}
		
		return outObj;
	}
	
	public OutputMessage doUploadFile(int serverTargetFileID, int fileBlockNo, byte[] fileData) {
		InputMessage inObj = null;
		
		try {
			inObj = messageManger.createInputMessage("UpFileData");
		} catch (IllegalArgumentException e) {
			log.warn("IllegalArgumentException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (MessageInfoNotFoundException e) {
			log.warn("MessageInfoNotFoundException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		}
		
		
		try {
			inObj.setAttribute("serverTargetFileID", serverTargetFileID);
			inObj.setAttribute("fileBlockNo", fileBlockNo);
			inObj.setAttribute("fileData", fileData);
		} catch (MessageItemException e) {
			log.warn("MessageItemException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		}
		
		
		//FIXME!
		// log.warn(inObj.toString());
		
		LetterFromServer letterFromServer = null;
		
		try {
			letterFromServer = conn.sendInputMessage(inObj);
			
			if (null == letterFromServer) {
				String errorMessage = String.format("input message[%s] letterFromServer is null", inObj.getMessageID()); 
				log.warn(errorMessage);
				JOptionPane.showMessageDialog(mainFrame, errorMessage);
				return null;
			}
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
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (BodyFormatException e) {
			log.warn("BodyFormatException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (MessageInfoNotFoundException e) {
			log.warn("MessageInfoNotFoundException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		}
		
		OutputMessage outObj = null;
		try {
			outObj = letterFromServer.getOutputMessage("UpFileDataResult");
			
			String taskResult = (String)outObj.getAttribute("taskResult");
			String resultMessage = (String)outObj.getAttribute("resultMessage");
			// int serverTargetFileID = (Integer)outObj.getAttribute("serverTargetFileID");
			
			if (taskResult.equals("N")) {
				JOptionPane.showMessageDialog(mainFrame, resultMessage);
				return null;
			}
		} catch (IllegalArgumentException e) {
			log.warn("IllegalArgumentException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (BodyFormatException e) {
			log.warn("BodyFormatException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (DynamicClassCallException e) {
			log.warn("DynamicClassCallException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (NoMoreDataPacketBufferException e) {
			log.warn("NoMoreDataPacketBufferException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (MessageInfoNotFoundException e) {
			log.warn("MessageInfoNotFoundException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (NoMatchOutputMessage e) {
			log.warn("NoMatchOutputMessage", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (MessageItemException e) {
			log.warn("MessageItemException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (ServerExcecutorUnknownException e) {
			log.warn("ServerExcecutorUnknownException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (NotLoginException e) {
			log.warn("NotLoginException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		}
		
		return outObj;
	}
	
	public OutputMessage readyDownloadFile(String localFilePathName, String localFileName, 
			String remoteFilePathName, String remoteFileName, long remoteFileSize, int clientTargetFileID, int fileBlockSize) {
		InputMessage inObj = null;
		
		try {
			inObj = messageManger.createInputMessage("DownFileInfo");
		} catch (IllegalArgumentException e) {
			log.warn("IllegalArgumentException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (MessageInfoNotFoundException e) {
			log.warn("MessageInfoNotFoundException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		}
		
		
		try {
			inObj.setAttribute("localFilePathName", localFilePathName);
			inObj.setAttribute("localFileName", localFileName);
			inObj.setAttribute("remoteFilePathName", remoteFilePathName);
			inObj.setAttribute("remoteFileName", remoteFileName);
			inObj.setAttribute("remoteFileSize", remoteFileSize);
			inObj.setAttribute("clientTargetFileID", clientTargetFileID);
			inObj.setAttribute("fileBlockSize", fileBlockSize);
		} catch (MessageItemException e) {
			log.warn("MessageItemException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		}
		
		
		LetterFromServer letterFromServer = null;
		
		try {
			letterFromServer = conn.sendInputMessage(inObj);
			
			if (null == letterFromServer) {
				String errorMessage = String.format("input message[%s] letterFromServer is null", inObj.getMessageID()); 
				log.warn(errorMessage);
				JOptionPane.showMessageDialog(mainFrame, errorMessage);
				return null;
			}
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
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (BodyFormatException e) {
			log.warn("BodyFormatException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (MessageInfoNotFoundException e) {
			log.warn("MessageInfoNotFoundException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		}
		
		OutputMessage outObj = null;
		try {
			outObj = letterFromServer.getOutputMessage("DownFileInfoResult");
			
			String taskResult = (String)outObj.getAttribute("taskResult");
			String resultMessage = (String)outObj.getAttribute("resultMessage");
			// int serverTargetFileID = (Integer)outObj.getAttribute("serverTargetFileID");
			
			if (taskResult.equals("N")) {
				JOptionPane.showMessageDialog(mainFrame, resultMessage);
				return null;
			}
		} catch (IllegalArgumentException e) {
			log.warn("IllegalArgumentException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (BodyFormatException e) {
			log.warn("BodyFormatException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (DynamicClassCallException e) {
			log.warn("DynamicClassCallException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (NoMoreDataPacketBufferException e) {
			log.warn("NoMoreDataPacketBufferException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (MessageInfoNotFoundException e) {
			log.warn("MessageInfoNotFoundException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (NoMatchOutputMessage e) {
			log.warn("NoMatchOutputMessage", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (MessageItemException e) {
			log.warn("MessageItemException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (ServerExcecutorUnknownException e) {
			log.warn("ServerExcecutorUnknownException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (NotLoginException e) {
			log.warn("NotLoginException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		}
		
		return outObj;
	}
	
	public OutputMessage doDownloadFile(int serverSourceFileID, int fileBlockNo) {
		InputMessage inObj = null;
		
		try {
			inObj = messageManger.createInputMessage("DownFileData");
		} catch (IllegalArgumentException e) {
			log.warn("IllegalArgumentException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (MessageInfoNotFoundException e) {
			log.warn("MessageInfoNotFoundException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		}
		
		
		try {
			inObj.setAttribute("serverSourceFileID", serverSourceFileID);
			inObj.setAttribute("fileBlockNo", fileBlockNo);
		} catch (MessageItemException e) {
			log.warn("MessageItemException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		}
		
		
		LetterFromServer letterFromServer = null;
		
		try {
			letterFromServer = conn.sendInputMessage(inObj);
			
			if (null == letterFromServer) {
				String errorMessage = String.format("input message[%s] letterFromServer is null", inObj.getMessageID()); 
				log.warn(errorMessage);
				JOptionPane.showMessageDialog(mainFrame, errorMessage);
				return null;
			}
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
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (BodyFormatException e) {
			log.warn("BodyFormatException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (MessageInfoNotFoundException e) {
			log.warn("MessageInfoNotFoundException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		}
		
		OutputMessage outObj = null;
		try {
			outObj = letterFromServer.getOutputMessage("DownFileDataResult");
			
			String taskResult = (String)outObj.getAttribute("taskResult");
			String resultMessage = (String)outObj.getAttribute("resultMessage");
			// int serverTargetFileID = (Integer)outObj.getAttribute("serverTargetFileID");
			
			if (taskResult.equals("N")) {
				JOptionPane.showMessageDialog(mainFrame, resultMessage);
				return null;
			}
		} catch (IllegalArgumentException e) {
			log.warn("IllegalArgumentException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (BodyFormatException e) {
			log.warn("BodyFormatException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (DynamicClassCallException e) {
			log.warn("DynamicClassCallException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (NoMoreDataPacketBufferException e) {
			log.warn("NoMoreDataPacketBufferException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (MessageInfoNotFoundException e) {
			log.warn("MessageInfoNotFoundException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (NoMatchOutputMessage e) {
			log.warn("NoMatchOutputMessage", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (MessageItemException e) {
			log.warn("MessageItemException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (ServerExcecutorUnknownException e) {
			log.warn("ServerExcecutorUnknownException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (NotLoginException e) {
			log.warn("NotLoginException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		}
		
		return outObj;
	}
	
	public void openFileTransferProcessDialog(String mesg, long fileSize, FileTransferTaskIF fileTransferTask) {
		// fileUpDownScreen.setIsCanceledUpDownFileTransfer(false);
		fileProcessDialog = new FileTranferProcessDialog(mainFrame, mesg, fileSize, fileTransferTask);
		fileProcessDialog.setVisible(true);
		fileProcessDialog.setDefaultCloseOperation(
			    JDialog.DO_NOTHING_ON_CLOSE);
		fileProcessDialog.addWindowListener(new WindowAdapter() {
			    public void windowClosing(WindowEvent we) {
			    	fileProcessDialog.cancelTask();
			    }
			});
	}
	
	public void noticeAddingFileDataToFileTransferProcessDialog(int receivedDataSize) {
		if (null == fileProcessDialog) return;
		
		fileProcessDialog.noticeAddingFileData(receivedDataSize);	
	}
	
	
	public OutputMessage cancelUploadFile(int serverTargetFileID) {
		InputMessage inObj = null;
		
		try {
			inObj = messageManger.createInputMessage("CancelUploadFile");
		} catch (IllegalArgumentException e) {
			log.warn("IllegalArgumentException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (MessageInfoNotFoundException e) {
			log.warn("MessageInfoNotFoundException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		}
		
		try {
			inObj.setAttribute("serverTargetFileID", serverTargetFileID);
		} catch (MessageItemException e) {
			log.warn("MessageItemException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		}
		
		LetterFromServer letterFromServer = null;
		
		try {
			letterFromServer = conn.sendInputMessage(inObj);
			
			if (null == letterFromServer) {
				String errorMessage = String.format("input message[%s] letterFromServer is null", inObj.getMessageID()); 
				log.warn(errorMessage);
				JOptionPane.showMessageDialog(mainFrame, errorMessage);
				return null;
			}
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
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (BodyFormatException e) {
			log.warn("BodyFormatException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (MessageInfoNotFoundException e) {
			log.warn("MessageInfoNotFoundException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		}
		
		OutputMessage outObj = null;
		try {
			outObj = letterFromServer.getOutputMessage("CancelUploadFileResult");
			
			String taskResult = (String)outObj.getAttribute("taskResult");
			String resultMessage = (String)outObj.getAttribute("resultMessage");
			// int serverTargetFileID = (Integer)outObj.getAttribute("serverTargetFileID");
			
			if (taskResult.equals("N")) {
				JOptionPane.showMessageDialog(mainFrame, resultMessage);
				return null;
			}
		} catch (IllegalArgumentException e) {
			log.warn("IllegalArgumentException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (BodyFormatException e) {
			log.warn("BodyFormatException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (DynamicClassCallException e) {
			log.warn("DynamicClassCallException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (NoMoreDataPacketBufferException e) {
			log.warn("NoMoreDataPacketBufferException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (MessageInfoNotFoundException e) {
			log.warn("MessageInfoNotFoundException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (NoMatchOutputMessage e) {
			log.warn("NoMatchOutputMessage", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (MessageItemException e) {
			log.warn("MessageItemException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (ServerExcecutorUnknownException e) {
			log.warn("ServerExcecutorUnknownException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (NotLoginException e) {
			log.warn("NotLoginException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		}
		
		return outObj;
	}
	
	public OutputMessage cancelDownloadFile(int serverSourceFileID) {
		InputMessage inObj = null;
		
		try {
			inObj = messageManger.createInputMessage("CancelDownloadFile");
		} catch (IllegalArgumentException e) {
			log.warn("IllegalArgumentException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (MessageInfoNotFoundException e) {
			log.warn("MessageInfoNotFoundException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		}
		
		try {
			inObj.setAttribute("serverSourceFileID", serverSourceFileID);
		} catch (MessageItemException e) {
			log.warn("MessageItemException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		}
		
		LetterFromServer letterFromServer = null;
		
		try {
			letterFromServer = conn.sendInputMessage(inObj);
			
			if (null == letterFromServer) {
				String errorMessage = String.format("input message[%s] letterFromServer is null", inObj.getMessageID()); 
				log.warn(errorMessage);
				JOptionPane.showMessageDialog(mainFrame, errorMessage);
				return null;
			}
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
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (BodyFormatException e) {
			log.warn("BodyFormatException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (MessageInfoNotFoundException e) {
			log.warn("MessageInfoNotFoundException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		}
		
		OutputMessage outObj = null;
		try {
			outObj = letterFromServer.getOutputMessage("CancelDownloadFileResult");
			
			String taskResult = (String)outObj.getAttribute("taskResult");
			String resultMessage = (String)outObj.getAttribute("resultMessage");
			// int serverTargetFileID = (Integer)outObj.getAttribute("serverTargetFileID");
			
			if (taskResult.equals("N")) {
				JOptionPane.showMessageDialog(mainFrame, resultMessage);
				return null;
			}
		} catch (IllegalArgumentException e) {
			log.warn("IllegalArgumentException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (BodyFormatException e) {
			log.warn("BodyFormatException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (DynamicClassCallException e) {
			log.warn("DynamicClassCallException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (NoMoreDataPacketBufferException e) {
			log.warn("NoMoreDataPacketBufferException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (MessageInfoNotFoundException e) {
			log.warn("MessageInfoNotFoundException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (NoMatchOutputMessage e) {
			log.warn("NoMatchOutputMessage", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (MessageItemException e) {
			log.warn("MessageItemException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (ServerExcecutorUnknownException e) {
			log.warn("ServerExcecutorUnknownException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (NotLoginException e) {
			log.warn("NotLoginException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		}
		
		return outObj;
	}
	
	@Override
	public void doAnonymousServerMessageTask(String projectName, OutputMessage outObj) {
		log.info(outObj.toString());;
	}


	/* (non-Javadoc)
	 * @see kr.pe.sinnori.gui.lib.MainControllerIF#openUploadProcessDialog(int, java.lang.String, long)
	 */
	@Override
	public void openUploadProcessDialog(int serverTargetFileID, String mesg,
			long fileSize) {
		UploadFileTransferTask uploadFileTransferTask = new UploadFileTransferTask(mainFrame, this, serverTargetFileID, localSourceFileResource);
		
		// fileUpDownScreen.setIsCanceledUpDownFileTransfer(false);
		fileProcessDialog = new FileTranferProcessDialog(mainFrame, mesg, fileSize, uploadFileTransferTask);
		fileProcessDialog.setVisible(true);
		fileProcessDialog.setDefaultCloseOperation(
			    JDialog.DO_NOTHING_ON_CLOSE);
		fileProcessDialog.addWindowListener(new WindowAdapter() {
			    public void windowClosing(WindowEvent we) {
			    	fileProcessDialog.cancelTask();
			    }
			});
		
	}


	/* (non-Javadoc)
	 * @see kr.pe.sinnori.gui.lib.MainControllerIF#endUploadTask()
	 */
	@Override
	public void endUploadTask() {
		if (null != localSourceFileResource) {
			localSourceFileResourceManager.putLocalSourceFileResource(localSourceFileResource);
			localSourceFileResource = null;
			fileUpDownScreen.reloadRemoteFileList();
		}
	}
}
