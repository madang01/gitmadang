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
import kr.pe.sinnori.gui.lib.MainControllerIF;
import kr.pe.sinnori.gui.screen.ConnectionScreen;
import kr.pe.sinnori.gui.screen.FileTranferProcessDialog;
import kr.pe.sinnori.gui.screen.FileUpDownScreen;
import kr.pe.sinnori.gui.screen.fileupdownscreen.task.DownloadFileTransferTask;
import kr.pe.sinnori.gui.screen.fileupdownscreen.task.UploadFileTransferTask;
import kr.pe.sinnori.util.AbstractClientExecutor;

import org.apache.commons.codec.binary.Base64;

/**
 * 샘플 파일 송수신 클라이언트 버전1
 * @author Jonghoon Won
 *
 */
public class FileUpDownClientV1CExtor extends AbstractClientExecutor implements MainControllerIF {
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
	private FileUpDownScreen fileUpDownScreen = null;
	private FileTranferProcessDialog fileProcessDialog = null;
	

	private MessageMangerIF messageManger = null;
	private ClientProjectIF clientProject = null;
	private ClientProjectConfigIF clientProjectConfig = null;
	private byte[] binaryPublicKeyBytes = null;
	
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
		
		fileUpDownScreen = new FileUpDownScreen(mainFrame, this);
		
		// FIXME!
		/*log.info(String.format("1.connectionScreen width=[%d], height=[%d]", connectionScreen.getWidth(), connectionScreen.getHeight()));
		log.info(String.format("1.fileUpDownScreen width=[%d], height=[%d]", fileUpDownScreen.getWidth(), fileUpDownScreen.getHeight()));
		log.info(String.format("1.mainFrame width=[%d], height=[%d]", mainFrame.getWidth(), mainFrame.getHeight()));*/
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
		return (1024*30);
	}
	
	@Override
	public void loginOK() {
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
		log.info(String.format("2. mainFrame width=[%d], height=[%d]", fileUpDownScreenWidth, fileUpDownScreenHeight));
		
		
		fileUpDownScreen.setVisible(true);
	}
	
	private void freeResource() {
		if (null != conn) conn.serverClose();
		freeLocalSourceFileResource();
		freeLocalTargetFileResource();	
	}
	
	public void goToFirstScreen() {
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
			log.warn("SocketTimeoutException", e);
			JOptionPane.showMessageDialog(mainFrame, "지정된 연결 시간을 초과하였습니다.");
			/** 로그인전 공개키 요구 */
			return null;
		} catch (ServerNotReadyException e) {
			log.warn("ServerNotReadyException", e);
			JOptionPane.showMessageDialog(mainFrame, "서버가 준비되지 않았습니다. 서버및 네트워크 연결 상태를 점검하세요.");
			/** 로그인전 공개키 요구 */
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
			log.warn("NotLoginException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
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
			log.warn("NotLoginException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
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
				conn.sendAsyncInputMessage(inObj);
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
				System.exit(1);
				return null;
			}
			
			try {
				outObj = messageManger.createOutputMessage("MessageResult");
				outObj.setAttribute("taskMessageID", inObj.getMessageID());
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
		
		} catch (MessageInfoNotFoundException e) {
			log.warn("MessageInfoNotFoundException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		} catch (Exception e) {
			log.warn("unknown exception", e);
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
	public OutputMessage readyUploadFile(String localFilePathName, String localFileName, long localFileSize, 
			String remoteFilePathName, String remoteFileName, int fileBlockSize) {
		if (!conn.isConnected()) {
			JOptionPane.showMessageDialog(mainFrame, "서버와의 연결이 끊어 졌습니다.");
			goToFirstScreen();
			return null;
		}
		
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
			inObj.setAttribute("clientSourceFileID", localSourceFileResource.getSourceFileID());
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
		
		
		OutputMessage outObj = getOutputMessageForLoginServie(inObj, "UpFileInfoResult", MESSAGE_EXCHANGE_TYPE.SYNC_MESSAGE);
		if (null == outObj) return null;
		try {
			String taskResult = (String)outObj.getAttribute("taskResult");
			String resultMessage = (String)outObj.getAttribute("resultMessage");
			int serverTargetFileID = (Integer)outObj.getAttribute("serverTargetFileID");
			int clientSourceFileID = (Integer)outObj.getAttribute("clientSourceFileID");
			
			if (taskResult.equals("N")) {
				// localSourceFileResource
				
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
		if (null != localSourceFileResource) {
			localSourceFileResourceManager.putLocalSourceFileResource(localSourceFileResource);
			localSourceFileResource = null;
		}
	}
	
	@Override
	public void openUploadProcessDialog(int serverTargetFileID, String mesg, long fileSize) {
		localSourceFileResource.setTargetFileID(serverTargetFileID);
		
		UploadFileTransferTask uploadFileTransferTask = new UploadFileTransferTask(mainFrame, this, serverTargetFileID, localSourceFileResource);
		
		// fileUpDownScreen.setIsCanceledUpDownFileTransfer(false);
		fileProcessDialog = new FileTranferProcessDialog(this, mainFrame, mesg, fileSize, uploadFileTransferTask);
		fileProcessDialog.setVisible(true);
		fileProcessDialog.setDefaultCloseOperation(
			    JDialog.DO_NOTHING_ON_CLOSE);
		fileProcessDialog.addWindowListener(new WindowAdapter() {
			    public void windowClosing(WindowEvent we) {
			    	// fileProcessDialog.cancelTask();
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
		// log.warn(inObj.toString());
		
		OutputMessage outObj = getOutputMessageForLoginServie(inObj, "UpFileDataResult", MESSAGE_EXCHANGE_TYPE.SYNC_MESSAGE);
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
	
	// FIXME!
	@Override
	public OutputMessage readyDownloadFile(String localFilePathName, String localFileName, 
			String remoteFilePathName, String remoteFileName, long remoteFileSize, int fileBlockSize) {
		if (!conn.isConnected()) {
			JOptionPane.showMessageDialog(mainFrame, "서버와의 연결이 끊어 졌습니다.");
			goToFirstScreen();
			return null;
		}
		
		try {
			localTargetFileResource = localTargetFileResourceManager.pollLocalTargetFileResource(remoteFilePathName, remoteFileName, remoteFileSize, localFilePathName, localFileName, fileBlockSize);
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
		
		
		OutputMessage outObj = getOutputMessageForLoginServie(inObj, "DownFileInfoResult", MESSAGE_EXCHANGE_TYPE.SYNC_MESSAGE);
		if (null == outObj) return null;
		try {
			// FIXME!
			// log.info(outObj.toString());
			
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
		
		DownloadFileTransferTask downloadFileTransferTask = new DownloadFileTransferTask(mainFrame, this, serverSourceFileID, localTargetFileResource);
		
		fileProcessDialog = new FileTranferProcessDialog(this, mainFrame, mesg, fileSize, downloadFileTransferTask);
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
	public OutputMessage doDownloadFile(int serverSourceFileID, int fileBlockNo) {
		if (!conn.isConnected()) {
			JOptionPane.showMessageDialog(mainFrame, "서버와의 연결이 끊어 졌습니다.");
			goToFirstScreen();
			return null;
		}
		
		if (null == localTargetFileResource) {
			log.warn("localTargetFileResource is null");
			return null;
		}
		
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
			inObj.setAttribute("clientTargetFileID", localTargetFileResource.getTargetFileID());
			inObj.setAttribute("fileBlockNo", fileBlockNo);
		} catch (MessageItemException e) {
			log.warn("MessageItemException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		}
		
		
		OutputMessage outObj = getOutputMessageForLoginServie(inObj, "DownFileDataResult", MESSAGE_EXCHANGE_TYPE.SYNC_MESSAGE);
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
	public OutputMessage cancelUploadFile() {
		if (!conn.isConnected()) {
			JOptionPane.showMessageDialog(mainFrame, "서버와의 연결이 끊어 졌습니다.");
			goToFirstScreen();			
			return null;
		}
		int serverTargetFileID = localSourceFileResource.getTargetFileID();
		
		InputMessage inObj = null;
		
		try {
			/**
			 * <pre>
			 * 서버 목적지 파일의 락을 해제하는 시간이 걸린다. 따라서 주의가 필요하다. 
			 * 파일 송수신 버전2의 경우 파일 업로드 취소는 비동기 메시지 CancelUploadFile2 로 처리되었다.
			 * </pre>
			 */
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
			inObj.setAttribute("clientSourceFileID", localSourceFileResource.getSourceFileID());
			inObj.setAttribute("serverTargetFileID", serverTargetFileID);
		} catch (MessageItemException e) {
			log.warn("MessageItemException", e);
			JOptionPane.showMessageDialog(mainFrame, e.getMessage());
			return null;
		}
		
		OutputMessage outObj = getOutputMessageForLoginServie(inObj, "CancelUploadFileResult", MESSAGE_EXCHANGE_TYPE.SYNC_MESSAGE);
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
	public OutputMessage cancelDownloadFile() {
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
	public void doAnonymousServerMessageTask(OutputMessage outObj) {
		log.info(outObj.toString());;
	}
	
	@Override
	public OutputMessage doDownloadFileAll() {
		/**
		 * 파일 송수신 버전 2차 전용 기능
		 */
		Throwable t = new Throwable();
		log.fatal("파일 송수신 버전 2차 전용 메소드", t);
		System.exit(1);
		return null;
	}
	
	@Override
	public OutputMessage doLoginEcho() {
		/**
		 * 파일 송수신 버전 2차 전용 기능
		 */
		Throwable t = new Throwable();
		log.fatal("파일 송수신 버전 2차 전용 메소드", t);
		System.exit(1);
		return null;
	}
	
	@Override
	protected void finalize() throws Throwable {
		freeResource();
	}
}
