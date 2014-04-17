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
import kr.pe.sinnori.common.configuration.ClientProjectConfigIF;
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
import kr.pe.sinnori.common.lib.MessageMangerIF;
import kr.pe.sinnori.common.message.InputMessage;
import kr.pe.sinnori.common.message.OutputMessage;
import kr.pe.sinnori.common.sessionkey.ClientSessionKeyManager;
import kr.pe.sinnori.common.sessionkey.ServerSessionKeyManager;
import kr.pe.sinnori.common.sessionkey.SymmetricKey;
import kr.pe.sinnori.common.updownfile.LocalSourceFileResource;
import kr.pe.sinnori.common.updownfile.LocalSourceFileResourceManager;
import kr.pe.sinnori.common.updownfile.LocalTargetFileResource;
import kr.pe.sinnori.common.updownfile.LocalTargetFileResourceManager;
import kr.pe.sinnori.gui.lib.FileUpDown2AsynOutputMessageTask;
import kr.pe.sinnori.gui.lib.MainControllerIF;
import kr.pe.sinnori.gui.screen.ConnectionScreen;
import kr.pe.sinnori.gui.screen.FileTranferProcessDialog;
import kr.pe.sinnori.gui.screen.FileUpDownScreen2;
import kr.pe.sinnori.gui.screen.fileupdownscreen.task.DownloadFileTransferTask2;
import kr.pe.sinnori.gui.screen.fileupdownscreen.task.UploadFileTransferTask2;
import kr.pe.sinnori.util.AbstractClientExecutor;

import org.apache.commons.codec.binary.Base64;

/**
 * 샘플 파일 송수신 클라이언트 버전2
 * @author madang01
 *
 */
public class FileUpDownClientV2CExtor extends AbstractClientExecutor implements MainControllerIF {
	// private final Object monitor = new Object();
	/**
	 * 메시지 교환 종류(MESSAGE_EXCHANGE_TYPE)
	 * 비동기 메시지(ASYNC_MESSAGE) : 입력 메시지를 보내기만 하며 출력 메시지를 기다리지 않는다.
	 * 동기 메시지(SYNC_MESSAGE) : 입력 메시지를 보낸후 출력 메시지를 받아 반환을 한다.
	 */
	private enum MESSAGE_EXCHANGE_TYPE {ASYNC_MESSAGE, SYNC_MESSAGE};
	
	private AbstractConnection conn = null; 
	
	private JFrame mainFrame = null;

	private ConnectionScreen connectionScreen = null;
	private FileUpDownScreen2 fileUpDownScreen = null;
	private FileTranferProcessDialog fileProcessDialog = null;
	

	private MessageMangerIF messageManger = null;
	private ClientProjectIF clientProject = null;
	private ClientProjectConfigIF clientProjectConfig = null;
	private byte[] binaryPublicKeyBytes = null;
	
	private ClientSessionKeyManager clientSessionKeyManager = null;
	
	private FileUpDown2AsynOutputMessageTask  fileUpDown2AsynOutputMessageTask = null;
	
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
			log.fatal("InterruptedException", e);
			System.exit(1);
		} catch (NotSupportedException e) {
			log.fatal("NotSupportedException", e);
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
		
		fileUpDownScreen = new FileUpDownScreen2(mainFrame, this);
	}
		
	
	@Override
	protected void doTask(ClientProjectConfigIF clientProjectConfig, MessageMangerIF messageManger, ClientProjectIF clientProject)
			throws SocketTimeoutException, ServerNotReadyException,
			DynamicClassCallException, NoMoreDataPacketBufferException,
			BodyFormatException, MessageInfoNotFoundException, InterruptedException {
		// connectionOK = this;
		this.messageManger = messageManger;
		this.clientProject = clientProject;
		this.clientProjectConfig = clientProjectConfig;
		
		this.fileUpDown2AsynOutputMessageTask = new FileUpDown2AsynOutputMessageTask(this);
		clientProject.changeAsynOutputMessageTask(fileUpDown2AsynOutputMessageTask);;
		
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
	
	
	
	
	@Override
	public void loginOK() {
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
	
	private void freeResource() {
		if (null != conn) conn.serverClose();
		if (null != fileProcessDialog) {
			fileProcessDialog.dispose();
		}
		freeLocalSourceFileResource();
		freeLocalTargetFileResource();	
	}
	public void goToFirstScreen() {
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
	public byte[] connectServer(String newServerHost, int newServerPort) {
		clientProjectConfig.changeServerAddress(newServerHost, newServerPort);
		
		
		OutputMessage binaryPublicKeyOutObj = getBinaryPublicKey();
		if (null == binaryPublicKeyOutObj) return null;
		try {
			binaryPublicKeyBytes = (byte[])binaryPublicKeyOutObj.getAttribute("publicKeyBytes");
		} catch (MessageItemException e) {
			log.warn("MessageItemException", e);
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
	
	@Override
	public OutputMessage getBinaryPublicKey() {
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
			letterFromServer = conn.sendSyncInputMessage(binaryPublicKeyInObj);
			
			if (null == letterFromServer) {
				log.warn(String.format("input message[%s] letterFromServer is null", binaryPublicKeyInObj.getMessageID()));
				return null;
			}
		} catch (SocketTimeoutException e) {
			log.warn(String.format("%s", binaryPublicKeyInObj.toString()), e);
			JOptionPane.showMessageDialog(mainFrame, "지정된 연결 시간을 초과하였습니다.");
			
			/** 로그인 전 공개키 요구 */
			return null;
		} catch (ServerNotReadyException e) {
			log.warn("ServerNotReadyException", e);
			JOptionPane.showMessageDialog(mainFrame, "서버가 준비되지 않았습니다. 서버및 네트워크 연결 상태를 점검하세요.");
			/** 로그인 전 공개키 요구 */
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
		
		OutputMessage binaryPublicKeyOutObj = null;
		
		try {
			binaryPublicKeyOutObj = letterFromServer.getOutputMessage("BinaryPublicKey");
			// binaryPublicKeyBytes = (byte[])binaryPublicKeyOutObj.getAttribute("publicKeyBytes");
			
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
			log.fatal("NotLoginException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			System.exit(1);
			return null;
		}
		
		return binaryPublicKeyOutObj;
	}
	
	@Override
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
		
		String charsetName = clientProjectConfig.getCharset().name();
		
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
			letterFromServer = conn.sendSyncInputMessage(loginInObj);
			
			if (null == letterFromServer) {
				String errorMessage = String.format("input message[%s] letterFromServer is null", loginInObj.getMessageID()); 
				log.warn(errorMessage);
				JOptionPane.showMessageDialog(mainFrame, errorMessage);
				return false;
			}
		} catch (SocketTimeoutException e) {
			log.warn("SocketTimeoutException", e);
			JOptionPane.showMessageDialog(mainFrame, "지정된 연결 시간을 초과하였습니다.");
			/** 로그인 요구 */
			return false;
		} catch (ServerNotReadyException e) {
			log.warn("ServerNotReadyException", e);
			JOptionPane.showMessageDialog(mainFrame, "서버가 준비되지 않았습니다. 서버및 네트워크 연결 상태를 점검하세요.");
			/** 로그인 요구 */
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
			log.fatal("NotLoginException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			
			System.exit(1);
			return false;
		}
		
		return true;
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
	
	
	
	private OutputMessage getOutputMessageForLoginServie(InputMessage inObj, String outputMessageID, MESSAGE_EXCHANGE_TYPE messageExchangeType) {
		if (!conn.isConnected()) {
			JOptionPane.showMessageDialog(mainFrame, "서버와의 연결이 끊어 졌습니다.");
			goToFirstScreen();
			return null;
		}
		
		OutputMessage outObj = null;
		
		if (messageExchangeType == MESSAGE_EXCHANGE_TYPE.ASYNC_MESSAGE) {
			try {
				conn.sendAsynInputMessage(inObj);
			} catch (SocketTimeoutException e) {
				log.warn("SocketTimeoutException", e);
				JOptionPane.showMessageDialog(mainFrame, "지정된 연결 시간을 초과하였습니다.");
				
				goToFirstScreen();
				return null;
			} catch (ServerNotReadyException e) {
				log.warn("ServerNotReadyException", e);
				JOptionPane.showMessageDialog(mainFrame, "서버가 준비되지 않았습니다. 서버및 네트워크 연결 상태를 점검하세요.");
				
				goToFirstScreen();
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
			} catch (NotSupportedException e) {
				log.fatal("NotSupportedException", e);
				JOptionPane.showMessageDialog(mainFrame, e.getMessage());
				
				freeResource();
				System.exit(1);
				return null;
			}
			
			try {
				outObj = messageManger.createOutputMessage("MessageResult");
				outObj.setAttribute("taskMessageID", "CancelUploadFile");
				outObj.setAttribute("taskResult", "Y");
				outObj.setAttribute("resultMessage", "가상적으로 성공 처리");
			} catch (IllegalArgumentException e) {
				log.warn("IllegalArgumentException", e);
				JOptionPane.showMessageDialog(mainFrame, e.getMessage());
				return null;
			} catch (MessageInfoNotFoundException e) {
				log.warn("MessageInfoNotFoundException", e);
				JOptionPane.showMessageDialog(mainFrame, e.getMessage());
				return null;
			} catch (MessageItemException e) {
				log.warn("MessageItemException", e);
				JOptionPane.showMessageDialog(mainFrame, e.getMessage());
				return null;
			}		
		} else {
			LetterFromServer letterFromServer = null;
			try {				
				letterFromServer = conn.sendSyncInputMessage(inObj);
				if (null == letterFromServer) {
					String errorMessage = String.format("inObj[%s] letterFromServer is null", inObj.getMessageID()); 
					log.warn(errorMessage);
					JOptionPane.showMessageDialog(mainFrame, errorMessage);
					return null;
				}
			} catch (SocketTimeoutException e) {
				log.warn("SocketTimeoutException", e);
				JOptionPane.showMessageDialog(mainFrame, "지정된 연결 시간을 초과하였습니다.");
				
				goToFirstScreen();
				return null;
			} catch (ServerNotReadyException e) {
				log.warn("ServerNotReadyException", e);
				JOptionPane.showMessageDialog(mainFrame, "서버가 준비되지 않았습니다. 서버및 네트워크 연결 상태를 점검하세요.");
				
				goToFirstScreen();
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
				outObj = letterFromServer.getOutputMessage(outputMessageID);
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
				
				goToFirstScreen();
				return null;
			}
		}		
		
		return outObj;
	}
	
	
	@Override
	public OutputMessage getRemoteFileList(String requestDirectory) {
		if (!conn.isConnected()) {
			JOptionPane.showMessageDialog(mainFrame, "서버와의 연결이 끊어 졌습니다.");
			goToFirstScreen();
			return null;
		}
		
		InputMessage inObj = null;
		
		try {
			inObj = messageManger.createInputMessage("FileListRequest");
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
			inObj.setAttribute("requestDirectory", requestDirectory);
		} catch (MessageItemException e) {
			log.warn("MessageItemException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		}
		
		OutputMessage outObj = getOutputMessageForLoginServie(inObj, "FileListResult", MESSAGE_EXCHANGE_TYPE.SYNC_MESSAGE);
		if (null == outObj) return null;
		try {
			String taskResult = (String)outObj.getAttribute("taskResult");
			String resultMessage = (String)outObj.getAttribute("resultMessage");
			
			if (taskResult.equals("N")) {
				JOptionPane.showMessageDialog(mainFrame, resultMessage);
				return null;
			}
		} catch (MessageItemException e) {
			log.warn("MessageItemException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (Exception e) {
			log.warn("unknown exception", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		}
		
		return outObj;
	}
	
	@Override
	public OutputMessage readyUploadFile(boolean append, 
			String localFilePathName, String localFileName, long localFileSize, 
			String remoteFilePathName, String remoteFileName, long remoteFileSize, int fileBlockSize) {
		
		if (!conn.isConnected()) {
			JOptionPane.showMessageDialog(mainFrame, "서버와의 연결이 끊어 졌습니다.");
			goToFirstScreen();
			return null;
		}
		
		
		try {
			localSourceFileResource = localSourceFileResourceManager.pollLocalSourceFileResource(false, 
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
			inObj.setAttribute("clientSourceFileID", localSourceFileResource.getSourceFileID());
			inObj.setAttribute("localFilePathName", localFilePathName);
			inObj.setAttribute("localFileName", localFileName);
			inObj.setAttribute("localFileSize", localFileSize);
			inObj.setAttribute("remoteFilePathName", remoteFilePathName);
			inObj.setAttribute("remoteFileName", remoteFileName);
			inObj.setAttribute("remoteFileSize", remoteFileSize);
			inObj.setAttribute("fileBlockSize", fileBlockSize);
		} catch (MessageItemException e) {
			log.warn("MessageItemException", e);
			
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		}
		
		OutputMessage outObj = getOutputMessageForLoginServie(inObj, "UpFileInfoResult", MESSAGE_EXCHANGE_TYPE.SYNC_MESSAGE);
		if (null == outObj) return null;
		try {			
			String taskResult = (String)outObj.getAttribute("taskResult");
			String resultMessage = (String)outObj.getAttribute("resultMessage");
			int serverTargetFileID = (Integer)outObj.getAttribute("serverTargetFileID");
			int clientSourceFileID = (Integer)outObj.getAttribute("clientSourceFileID");
			
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
		} catch (MessageItemException e) {
			log.warn("MessageItemException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (Exception e) {
			log.warn("unknown exception", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		}
		
		return outObj;
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
	public void openUploadProcessDialog(int serverTargetFileID, String mesg, long fileSize) {
		localSourceFileResource.setTargetFileID(serverTargetFileID);
		
		
		UploadFileTransferTask2 uploadFileTransferTask = new UploadFileTransferTask2(mainFrame, this, serverTargetFileID, localSourceFileResource);
		
		// fileUpDownScreen.setIsCanceledUpDownFileTransfer(false);
		fileProcessDialog = new FileTranferProcessDialog(this, mainFrame, mesg, fileSize, uploadFileTransferTask);
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
	
	@Override
	public OutputMessage doUploadFile(int serverTargetFileID, int fileBlockNo, byte[] fileData) {
		if (!conn.isConnected()) {
			JOptionPane.showMessageDialog(mainFrame, "서버와의 연결이 끊어 졌습니다.");
			goToFirstScreen();
			return null;
		}
		
		if (null == localSourceFileResource) {
			log.warn("localSourceFileResource is null");
			return null;
		}
		
		InputMessage inObj = null;
		
		try {
			inObj = messageManger.createInputMessage("UpFileData2");
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
			inObj.setAttribute("clientSourceFileID", localSourceFileResource.getSourceFileID());
			inObj.setAttribute("serverTargetFileID", serverTargetFileID);
			inObj.setAttribute("fileBlockNo", fileBlockNo);
			inObj.setAttribute("fileData", fileData);
		} catch (MessageItemException e) {
			log.warn("MessageItemException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		}
		
		//FIXME!
		// log.info(inObj.toString());
		/** UpFileData2 */
		OutputMessage outObj = getOutputMessageForLoginServie(inObj, "UpFileDataResult", MESSAGE_EXCHANGE_TYPE.ASYNC_MESSAGE);
		if (null == outObj) return null;
		return outObj;
	}
	
	@Override
	public OutputMessage readyDownloadFile(boolean append, 
			String localFilePathName, String localFileName, long localFileSize, 
			String remoteFilePathName, String remoteFileName, long remoteFileSize, int fileBlockSize) {
		if (!conn.isConnected()) {
			JOptionPane.showMessageDialog(mainFrame, "서버와의 연결이 끊어 졌습니다.");
			goToFirstScreen();
			return null;
		}
		
		try {
			localTargetFileResource = 
					localTargetFileResourceManager.pollLocalTargetFileResource(false, 
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
		
		int clientTargetFileID = localTargetFileResource.getTargetFileID();
		
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
			if (append) inObj.setAttribute("append", (byte)0x01);
			else inObj.setAttribute("append", (byte)0x00);
			inObj.setAttribute("localFilePathName", localFilePathName);
			inObj.setAttribute("localFileName", localFileName);
			inObj.setAttribute("localFileSize", localFileSize);
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
		
		OutputMessage outObj = getOutputMessageForLoginServie(inObj, "DownFileInfoResult", MESSAGE_EXCHANGE_TYPE.SYNC_MESSAGE);
		if (null == outObj) return null;
		
		// FIXME!
		log.info(outObj.toString());
		
		try {
			String taskResult = (String)outObj.getAttribute("taskResult");
			String resultMessage = (String)outObj.getAttribute("resultMessage");
			int serverSourceFileID = (Integer)outObj.getAttribute("serverSourceFileID");
			
			if (taskResult.equals("N")) {
				JOptionPane.showMessageDialog(mainFrame, resultMessage);
				return null;
			}
			
			localTargetFileResource.setSourceFileID(serverSourceFileID);			
		} catch (MessageItemException e) {
			log.warn("MessageItemException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (Exception e) {
			log.warn("unknown exception", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		}
		
		return outObj;
	}
	
	public boolean truncateLocalTargetFileResource() {
		try {
			localTargetFileResource.truncate();
		} catch (UpDownFileException e) {
			// TODO Auto-generated catch block
			log.warn(e.getMessage(), e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());			 
			OutputMessage outObj = cancelDownloadFileV1();
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
	public void openDownloadProcessDialog(int serverSourceFileID, String mesg, long fileSize) {
		localTargetFileResource.setSourceFileID(serverSourceFileID);
		
		DownloadFileTransferTask2 downloadFileTransferTask = new DownloadFileTransferTask2(this, localTargetFileResource, 5000L);
		
		fileProcessDialog = new FileTranferProcessDialog(this, mainFrame, mesg, fileSize, downloadFileTransferTask);
		fileProcessDialog.setVisible(true);
		fileProcessDialog.setDefaultCloseOperation(
			    JDialog.DO_NOTHING_ON_CLOSE);
		fileProcessDialog.addWindowListener(new WindowAdapter() {
			    public void windowClosing(WindowEvent we) {
			    	// cancelDownloadFile(localTargetFileResource.getSourceFileID());
			    	fileProcessDialog.cancelEvent();
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
	public OutputMessage doDownloadFile(int serverSourceFileID, int fileBlockNo) {
		
		/**
		 * 파일 송수신 버전 1차 전용 기능
		 */
		
		Throwable t = new Throwable();
		log.fatal("파일 송수신 버전 1차 전용 메소드", t);
		freeResource();
		System.exit(1);
		return null;
	}
	
	@Override
	public OutputMessage doDownloadFileAll() {
		if (!conn.isConnected()) {
			JOptionPane.showMessageDialog(mainFrame, "서버와의 연결이 끊어 졌습니다.");
			goToFirstScreen();
			return null;
		}
		
		InputMessage inObj = null;
		
		try {
			inObj = messageManger.createInputMessage("DownFileDataAll");
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
			inObj.setAttribute("serverSourceFileID", localTargetFileResource.getSourceFileID());
			inObj.setAttribute("clientTargetFileID", localTargetFileResource.getTargetFileID());
		} catch (MessageItemException e) {
			log.warn("MessageItemException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		}
		/** DownFileDataAll */
		OutputMessage outObj = getOutputMessageForLoginServie(inObj, "MessageResult", MESSAGE_EXCHANGE_TYPE.ASYNC_MESSAGE);
		if (null == outObj) return null;
		return outObj;
	}
	
	public OutputMessage doLoginEcho() {
		if (!conn.isConnected()) {
			JOptionPane.showMessageDialog(mainFrame, "서버와의 연결이 끊어 졌습니다.");
			goToFirstScreen();
			return null;
		}
		
		InputMessage inObj = null;
		
		try {
			inObj = messageManger.createInputMessage("LoginEcho");
		} catch (IllegalArgumentException e) {
			log.warn("IllegalArgumentException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (MessageInfoNotFoundException e) {
			log.warn("MessageInfoNotFoundException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		}
		
		OutputMessage outObj = getOutputMessageForLoginServie(inObj, "LoginEcho", MESSAGE_EXCHANGE_TYPE.SYNC_MESSAGE);
		return outObj;
	}
		
	@Override
	public OutputMessage cancelUploadFile() {
		if (!conn.isConnected()) {
			/** 서버로 파일 조각을 보내는 파일 업로드 작업중이므로 첫화면으로 가지 않고 이곳에서는 단지 에러 메시지만 보여주면 된다. */
			JOptionPane.showMessageDialog(mainFrame, "서버와의 연결이 끊어 졌습니다.");
			return null;
		}
		
		int serverTargetFileID = localSourceFileResource.getTargetFileID();
		InputMessage inObj = null;
		
		try {
			/**
			 * <pre>
			 * 비동기 메시지 CancelUploadFile2 가 필요한 이유는 
			 * 응답을 기다리지 않고 로컬 원본 파일 조각을 서버로 보내기때문에 서버 목적지 파일의 락을 해제하는 시간이 걸린다.
			 * 따라서 파일 업로드 취소는 비동기 메시지로 처리되어야 한다.
			 * </pre>
			 */
			inObj = messageManger.createInputMessage("CancelUploadFile2");
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
			inObj.setAttribute("clientSourceFileID", localSourceFileResource.getSourceFileID());
			inObj.setAttribute("serverTargetFileID", serverTargetFileID);
		} catch (MessageItemException e) {
			log.warn("MessageItemException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		}
		/** CancelUploadFile2 */
		OutputMessage outObj = getOutputMessageForLoginServie(inObj, "CancelUploadFileResult", MESSAGE_EXCHANGE_TYPE.ASYNC_MESSAGE);
		if (null == outObj) return null;
		
		return outObj;
	}
	
	@Override
	public OutputMessage cancelDownloadFile() {
		if (!conn.isConnected()) {
			JOptionPane.showMessageDialog(mainFrame, "서버와의 연결이 끊어 졌습니다.");
			goToFirstScreen();
			return null;
		}
		
		int serverSourceFileID = localTargetFileResource.getSourceFileID();
		InputMessage inObj = null;
		
		try {
			inObj = messageManger.createInputMessage("CancelDownloadFile2");
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
			inObj.setAttribute("clientTargetFileID", localTargetFileResource.getSourceFileID());
		} catch (MessageItemException e) {
			log.warn("MessageItemException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;			
		}
		
		/** CancelDownloadFile2 */
		OutputMessage outObj = getOutputMessageForLoginServie(inObj, "CancelDownloadFileResult", MESSAGE_EXCHANGE_TYPE.ASYNC_MESSAGE);
		if (null == outObj) return null;
		return outObj;
	}
	
	private OutputMessage cancelDownloadFileV1() {
		if (!conn.isConnected()) {
			JOptionPane.showMessageDialog(mainFrame, "서버와의 연결이 끊어 졌습니다.");
			goToFirstScreen();
			return null;
		}
		
		int serverSourceFileID = localTargetFileResource.getSourceFileID();
		
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
			inObj.setAttribute("clientTargetFileID", localTargetFileResource.getSourceFileID());
		} catch (MessageItemException e) {
			log.warn("MessageItemException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		}
		
		OutputMessage outObj = getOutputMessageForLoginServie(inObj, "CancelDownloadFileResult", MESSAGE_EXCHANGE_TYPE.SYNC_MESSAGE);
		if (null == outObj) return null;
		try {			
			String taskResult = (String)outObj.getAttribute("taskResult");
			String resultMessage = (String)outObj.getAttribute("resultMessage");
			// int serverTargetFileID = (Integer)outObj.getAttribute("serverTargetFileID");
			
			if (taskResult.equals("N")) {
				JOptionPane.showMessageDialog(mainFrame, resultMessage);
				return null;
			}	
		} catch (MessageItemException e) {
			log.warn("MessageItemException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (Exception e) {
			log.warn("unknown exception", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		}
		
		return outObj;
	}
	
	@Override
	public void doAsynOutputMessageTask(OutputMessage outObj) {
		// log.info(String.format("projectName[%s] %s", projectName, outObj.toString()));

		String messageID = outObj.getMessageID();
		try {
			
			if (messageID.equals("UpFileDataResult")) {
				if (null == localSourceFileResource) {
					String errorMessage = String.format("localSourceFileResource is null but outputmessage[UpFileDataResult] sent, %s", outObj.toJSONString());
					log.warn(errorMessage);
					return;
				}
				
				int clientSourceFileID = (Integer)outObj.getAttribute("clientSourceFileID");
				int serverTargetFileID = (Integer)outObj.getAttribute("serverTargetFileID");
				
				if (clientSourceFileID != localSourceFileResource.getSourceFileID()
						|| serverTargetFileID != localSourceFileResource.getTargetFileID()) {
					String errorMessage = String.format("작업중인 파일 업로드 작업[%s]과 관련 없는 출력 메시지[%s]를 폐기합니다.", localSourceFileResource.toString(), outObj.toString());
					log.warn(errorMessage);
					return;
				}
					
				if (!localSourceFileResource.isCanceled()) {
					localSourceFileResource.cancel();
					// String taskResult = (String)outObj.getAttribute("taskResult");
					String resultMessage = (String)outObj.getAttribute("resultMessage");
					JOptionPane.showMessageDialog(mainFrame, resultMessage);
				}
			} else if (messageID.equals("CancelUploadFileResult")) {
				String resultMessage = (String)outObj.getAttribute("resultMessage");
				JOptionPane.showMessageDialog(mainFrame, resultMessage);
				
				log.info(outObj.toString());
			} else if (messageID.equals("DownFileDataResult")) {
				if (null == localTargetFileResource) {
					String errorMessage = String.format("localTargetFileResource is null but outputmessage[DownFileDataResult] sent, %s", outObj.toString());
					log.warn(errorMessage);
					return;
				}
				
				int serverSourceFileID = (Integer) outObj.getAttribute("serverSourceFileID");
				int clientTargetFileID = (Integer) outObj.getAttribute("clientTargetFileID");
				String taskResult = (String) outObj.getAttribute("taskResult");
				String resultMessage = (String) outObj.getAttribute("resultMessage");
				int fileBlockNo = (Integer) outObj.getAttribute("fileBlockNo");
				byte[] fileData = (byte[]) outObj.getAttribute("fileData");
				
				if (serverSourceFileID != localTargetFileResource.getSourceFileID() || 
						clientTargetFileID != localTargetFileResource.getTargetFileID()) {
					String errorMessage = String.format("작업중인 파일 다운로드 작업[%s]과 관련 없는 출력 메시지[%s]를 폐기합니다.", localTargetFileResource.toString(), outObj.toString());
					log.warn(errorMessage);
					return;
				}
				
				try {
					if (taskResult.equals("Y")) {
						boolean isFinished = localTargetFileResource.writeTargetFileData(fileBlockNo, fileData, true);
						fileProcessDialog.noticeAddingFileData(fileData.length);
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
				
			} else if (messageID.equals("CancelDownloadFileResult")) {
				if (null == localTargetFileResource) {
					String errorMessage = String.format("localTargetFileResource is null but outputmessage[DownFileDataResult] sent, %s", outObj.toJSONString());
					log.warn(errorMessage);
					return;
				}
				
				int serverSourceFileID = (Integer) outObj.getAttribute("serverSourceFileID");
				int clientTargetFileID = (Integer) outObj.getAttribute("clientTargetFileID");
				
				if (serverSourceFileID != localTargetFileResource.getSourceFileID() || 
						clientTargetFileID != localTargetFileResource.getTargetFileID()) {
					String errorMessage = String.format("작업중인 파일 다운로드 작업[%s]과 관련 없는 출력 메시지[%s]를 폐기합니다.", localTargetFileResource.toString(), outObj.toString());
					log.warn(errorMessage);
					return;
				}
				
				localTargetFileResource.cancel();
				fileProcessDialog.stopTask();
			} else if (messageID.equals("SelfExn")) {
				log.warn(String.format("projectName[%s] SelfExn, %s", clientProjectConfig.getProjectName(), outObj.toString()));
			} else {
				log.warn(String.format("projectName[%s] unknown output message, %s", clientProjectConfig.getProjectName(), outObj.toString()));
			}
			
		} catch (MessageItemException e) {
			log.warn(String.format("projectName[%s] %s", clientProjectConfig.getProjectName(), e.getMessage()), e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		freeResource();
	}
}
