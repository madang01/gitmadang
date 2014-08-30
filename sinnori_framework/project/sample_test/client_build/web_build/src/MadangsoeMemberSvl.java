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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import kr.pe.sinnori.client.ClientProject;
import kr.pe.sinnori.client.ClientProjectManager;
import kr.pe.sinnori.client.io.LetterFromServer;
import kr.pe.sinnori.common.exception.MessageInfoNotFoundException;
import kr.pe.sinnori.common.exception.SymmetricException;
import kr.pe.sinnori.common.lib.CommonType;
import kr.pe.sinnori.common.message.InputMessage;
import kr.pe.sinnori.common.message.OutputMessage;
import kr.pe.sinnori.common.servlet.AbstractServlet;
import kr.pe.sinnori.common.sessionkey.ClientSessionKeyManager;
import kr.pe.sinnori.common.sessionkey.ServerSessionKeyManager;
import kr.pe.sinnori.common.sessionkey.SymmetricKey;

import org.apache.commons.codec.binary.Base64;

/**
 * 회원 가입<br/>
 * 회원 가입은 총 2개 페이지로 구성된다.<br/> 
 * 첫번째페이지는 입력 화면 페이지, 마지막 두번째 페이지는 회원 가입 결과 페이지이다.
 * @author Jonghoon Won
 *
 */

@SuppressWarnings("serial")
public class MadangsoeMemberSvl extends AbstractServlet {
	final String arryPageURL[] = { "/madangsoe/MadangsoeMember01.jsp",
			"/madangsoe/MadangsoeMember02.jsp" };
	
	

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		// TODO Auto-generated method stub
		String pageGubun = req.getParameter("pagegubun");
		log.info(String.format("pageGubun[%s]", pageGubun));

		String goPage = null;

		if (null == pageGubun || pageGubun.equals("step1")) {
			goPage = arryPageURL[0];

			ServerSessionKeyManager sessionKeyServerManger = ServerSessionKeyManager
					.getInstance();
			String modulusHex = sessionKeyServerManger.getModulusHexStrForWeb();
			req.setAttribute("modulusHex", modulusHex);

		} else {
			goPage = arryPageURL[1];
			

			String parmSessionKey = req.getParameter("sessionkey");
			String parmIV = req.getParameter("iv");

			String parmID = req.getParameter("id");
			String parmPWD = req.getParameter("pwd");
			String parmNickname = req.getParameter("nickname");
			String parmQuestion = req.getParameter("question");
			String parmAnswer = req.getParameter("answer");

			// FIXME!
			// parmSessionKey
			// ="54f5499e2f6c40ec9a35361c4348d798acc3c31310ae949ae2f4e468ab9275aa34fe2452b194c0994d351d6124dd00eb6fdab614add4e661603e56dcccedf8704328f8ae907d4e8529d6457a628402bee0eb641860b10a0c31ceb6c535aeb86c7afa8534d2b486fb383f448394a8630b3620c8938c6b69d410e1d8f7f3ed1d62";
			String parmPrivateKey = req.getParameter("privateKey");
			// String parmSessionKeyHex = req.getParameter("sessionKeyHex");
			// byte[] tmpBytes =
			// sessionKeyManger.encryptUsingPublicKey(HexUtil.hexToByteArray(parmPrivateKey));
			// parmSessionKey = Base64.encodeBytes(tmpBytes);
			log.info(String.format("parm privateKey=[%s]", parmPrivateKey));
			// log.info("parm sessionKeyHex=[%s]",parmSessionKeyHex);
			// log.info("enc sessionKeyHex=[%s]",
			// HexUtil.byteArrayAllToHex(tmpBytes));

			log.info(String.format("parm SessionKey=[%s]", parmSessionKey));
			log.info(String.format("parm IV=[%s]", parmIV));

			log.info(String.format("parm ID=[%s]", parmID));
			log.info(String.format("parm PWD=[%s]", parmPWD));
			log.info(String.format("parm Nickname=[%s]", parmNickname));
			log.info(String.format("parm Question=[%s]", parmQuestion));
			log.info(String.format("parm Answer=[%s]", parmAnswer));

			SymmetricKey  webUserSymmetricKey = null;
			ServerSessionKeyManager sessionKeyServerManger = ServerSessionKeyManager.getInstance();
			try {
				
				webUserSymmetricKey = sessionKeyServerManger.getSymmetricKey("AES", CommonType.SymmetricKeyEncoding.BASE64, parmSessionKey, parmIV);
			} catch(IllegalArgumentException e) {
				String errorMessage = e.getMessage();
				log.warn(errorMessage);
				
				printMessagePage(req, res, errorMessage, errorMessage);
				return;
			} catch(SymmetricException e) {
				String errorMessage = e.getMessage();
				log.warn(errorMessage);
				printMessagePage(req, res, errorMessage, errorMessage);
				return;
			}

			String errorMessage = "";
			
			String id = webUserSymmetricKey.decryptStringBase64(parmID);
			String pwd = webUserSymmetricKey.decryptStringBase64(parmPWD);
			String nickname = webUserSymmetricKey
					.decryptStringBase64(parmNickname);
			String question = webUserSymmetricKey
					.decryptStringBase64(parmQuestion);
			String answer = webUserSymmetricKey.decryptStringBase64(parmAnswer);

			log.info(String.format("id=[%s]", id));

			HttpSession session = req.getSession();

			// ClientSessionKeyManager sessionKeyClientManager =
			// ClientSessionKeyManager.getInstance();
			ClientSessionKeyManager sessionKeyClientManager = null;

			String projectName = System.getenv("SINNORI_PROJECT_NAME");
			ClientProject clientProject = ClientProjectManager.getInstance()
					.getClientProject(projectName);

			// String defaultServerName =
			// (String)conf.getResource("default_server_name");
			// getConnectionPool
			// ServerResource defaultServerResource =
			// SinnoriClientManager.getInstance().getServerResource(defaultServerName);

			synchronized (session) {
				sessionKeyClientManager = (ClientSessionKeyManager) session
						.getAttribute("sessionKeyClientManager");

				// error 처리를 위한 loop

				if (null == sessionKeyClientManager) {
					InputMessage binaryPublicKeyInObj = null;
					try {
						binaryPublicKeyInObj = clientProject.createInputMessage("BinaryPublicKey");
					} catch (MessageInfoNotFoundException e1) {
						e1.printStackTrace();
						System.exit(1);
					}
					binaryPublicKeyInObj.setAttribute("publicKeyBytes", sessionKeyServerManger.getPublicKeyBytes());

					LetterFromServer letterFromServer = clientProject.sendSyncInputMessage(binaryPublicKeyInObj);

					
					if (null == letterFromServer) {
						errorMessage = String.format(
								"input message[%s] letterList is null",
								binaryPublicKeyInObj.getMessageID());
						log.warn(errorMessage);
					} else {
						OutputMessage binaryPublicKeyOutObj = letterFromServer.getOutputMessage("BinaryPublicKey");
						byte[] binaryPublicKeyBytes = (byte[])binaryPublicKeyOutObj.getAttribute("publicKeyBytes");
						ClientSessionKeyManager clientSessionKeyManager = new ClientSessionKeyManager(binaryPublicKeyBytes);
						
						session.setAttribute("sessionKeyClientManager", clientSessionKeyManager);
					}
				}

				// error 처리르 위한 loop문 종료
				log.info(String.format("9. errorMessage=[%s]", errorMessage));

				if (errorMessage.equals("")) {
					byte sessionKeyBytes[] = sessionKeyClientManager
							.getSessionKey();

					SymmetricKey servletSymmetricKey = sessionKeyClientManager
							.getSymmetricKey();
					byte ivBytes[] = servletSymmetricKey.getIV();

					InputMessage memberSessionKeyInObj = null;
					try {
						memberSessionKeyInObj = clientProject
								.createInputMessage("MemberSessionKey");
					} catch (MessageInfoNotFoundException e1) {
						e1.printStackTrace();
						System.exit(1);
					}

					memberSessionKeyInObj.setAttribute("idCipherBase64",
							servletSymmetricKey.encryptStringBase64(id));
					memberSessionKeyInObj.setAttribute("pwdCipherBase64",
							servletSymmetricKey.encryptStringBase64(pwd));
					memberSessionKeyInObj.setAttribute("nicknameCipherBase64",
							servletSymmetricKey.encryptStringBase64(nickname));
					memberSessionKeyInObj.setAttribute("questionCipherBase64",
							servletSymmetricKey.encryptStringBase64(question));
					memberSessionKeyInObj.setAttribute("answerCipherBase64",
							servletSymmetricKey.encryptStringBase64(answer));
					memberSessionKeyInObj.setAttribute("sessionKeyBase64",
							Base64.encodeBase64String(sessionKeyBytes));
					memberSessionKeyInObj.setAttribute("ivBase64",
							Base64.encodeBase64String(ivBytes));

					LetterFromServer letterFromServer = clientProject
							.sendSyncInputMessage(memberSessionKeyInObj);

					if (null == letterFromServer) {
						errorMessage = String.format(
								"input message[%s] letterList is null",
								memberSessionKeyInObj.getMessageID()); 
						log.warn(errorMessage);
					} else {
						OutputMessage messagerResultOutputMessage = letterFromServer.getOutputMessage("MessageResult");

						req.setAttribute("resultOutputMessage", messagerResultOutputMessage);
					}					
				}
			}
			
			req.setAttribute("webUserSymmetricKey", webUserSymmetricKey);
			req.setAttribute("errorMessage", errorMessage);
			req.setAttribute("pageIV", parmIV);
		}

		log.info(String.format("goPage[%s]", goPage));

		printJspPage(req, res, goPage);
	}
}
