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
package kr.pe.sinnori.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.sinnori.client.ClientProject;
import kr.pe.sinnori.client.ClientProjectManager;
import kr.pe.sinnori.common.exception.SymmetricException;
import kr.pe.sinnori.common.lib.CommonStaticFinalVars;
import kr.pe.sinnori.common.lib.CommonType;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.servlet.AbstractServlet;
import kr.pe.sinnori.common.servlet.WebCommonStaticFinalVars;
import kr.pe.sinnori.common.sessionkey.ClientSessionKeyManager;
import kr.pe.sinnori.common.sessionkey.ServerSessionKeyManager;
import kr.pe.sinnori.common.sessionkey.SymmetricKey;
import kr.pe.sinnori.impl.message.BinaryPublicKey.BinaryPublicKey;
import kr.pe.sinnori.impl.message.MemberSessionKey.MemberSessionKey;
import kr.pe.sinnori.impl.message.MessageResult.MessageResult;

import org.apache.commons.codec.binary.Base64;

/**
 * 회원 가입<br/>
 * 회원 가입은 총 2개 페이지로 구성된다.<br/> 
 * 첫번째페이지는 입력 화면 페이지, 마지막 두번째 페이지는 회원 가입 결과 페이지이다.
 * @author Jonghoon Won
 *
 */

@SuppressWarnings("serial")
public class MemberSvl extends AbstractServlet {
	final String arryPageURL[] = { "/member/Member01.jsp",
			"/member/Member02.jsp" };
	
	

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
		// TODO Auto-generated method stub
		String pageGubun = req.getParameter("pagegubun");
		log.info(String.format("pageGubun[%s]", pageGubun));

		String goPage = null;

		if (null == pageGubun || !pageGubun.equals("step2")) {
			goPage = arryPageURL[0];

			ServerSessionKeyManager sessionKeyServerManger = ServerSessionKeyManager
					.getInstance();
			String modulusHex = sessionKeyServerManger.getModulusHexStrForWeb();
			req.setAttribute("modulusHex", modulusHex);

		} else {
			goPage = arryPageURL[1];
			

			String parmSessionKeyBase64 = req.getParameter("sessionkeyBase64");
			String parmIVBase64 = req.getParameter("ivBase64");

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

			log.info(String.format("parm sessionkeyBase64=[%s]", parmSessionKeyBase64));
			log.info(String.format("parm ivBase64=[%s]", parmIVBase64));

			log.info(String.format("parm id=[%s]", parmID));
			log.info(String.format("parm pwd=[%s]", parmPWD));
			log.info(String.format("parm nickname=[%s]", parmNickname));
			log.info(String.format("parm question=[%s]", parmQuestion));
			log.info(String.format("parm answer=[%s]", parmAnswer));
			
			MessageResult messageResultOutObj = new MessageResult();
			messageResultOutObj.setTaskMessageID("");
			messageResultOutObj.setTaskResult("N");
			messageResultOutObj.setResultMessage("회원 가입이 실패하였습니다.");

			SymmetricKey  webUserSymmetricKey = null;
			ServerSessionKeyManager sessionKeyServerManger = ServerSessionKeyManager.getInstance();
			try {
				
				webUserSymmetricKey = sessionKeyServerManger.getSymmetricKey(WebCommonStaticFinalVars.WEBSITE_JAVA_SYMMETRIC_KEY_ALGORITHM_NAME, CommonType.SymmetricKeyEncoding.BASE64, parmSessionKeyBase64, parmIVBase64);
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

			// String errorMessage = "";
			
			String id = webUserSymmetricKey.decryptStringBase64(parmID);
			String pwd = webUserSymmetricKey.decryptStringBase64(parmPWD);
			String nickname = webUserSymmetricKey
					.decryptStringBase64(parmNickname);
			String question = webUserSymmetricKey
					.decryptStringBase64(parmQuestion);
			String answer = webUserSymmetricKey.decryptStringBase64(parmAnswer);

			log.info(String.format("id=[%s]", id));

			// HttpSession session = req.getSession();

			// ClientSessionKeyManager sessionKeyClientManager =
			// ClientSessionKeyManager.getInstance();
			

			

			// String defaultServerName =
			// (String)conf.getResource("default_server_name");
			// getConnectionPool
			// ServerResource defaultServerResource =
			// SinnoriClientManager.getInstance().getServerResource(defaultServerName);

			String projectName = System.getProperty(CommonStaticFinalVars.SINNORI_PROJECT_NAME_JAVA_SYSTEM_VAR_NAME);		
			ClientProject clientProject = ClientProjectManager.getInstance().getClientProject(projectName);
			
			
			BinaryPublicKey binaryPublicKeyInObj = new BinaryPublicKey();			
			binaryPublicKeyInObj.setPublicKeyBytes(sessionKeyServerManger.getPublicKeyBytes());
			
			AbstractMessage messageFromServer = clientProject.sendSyncInputMessage(binaryPublicKeyInObj);					
			if (messageFromServer instanceof BinaryPublicKey) {
				BinaryPublicKey binaryPublicKeyOutObj = (BinaryPublicKey) messageFromServer;
				byte[] binaryPublicKeyBytes = binaryPublicKeyOutObj.getPublicKeyBytes();
				ClientSessionKeyManager clientSessionKeyManage = new ClientSessionKeyManager(binaryPublicKeyBytes);
				
				byte sessionKeyBytes[] = clientSessionKeyManage.getSessionKey();
				SymmetricKey serverSymmetricKey = clientSessionKeyManage.getSymmetricKey();				
				byte ivBytes[] = serverSymmetricKey.getIV();

				MemberSessionKey memberSessionKeyInObj = new MemberSessionKey();
				
				memberSessionKeyInObj.setIdCipherBase64(serverSymmetricKey.encryptStringBase64(id));
				memberSessionKeyInObj.setPwdCipherBase64(serverSymmetricKey.encryptStringBase64(pwd));
				memberSessionKeyInObj.setNicknameCipherBase64(serverSymmetricKey.encryptStringBase64(nickname));
				memberSessionKeyInObj.setQuestionCipherBase64(serverSymmetricKey.encryptStringBase64(question));
				memberSessionKeyInObj.setAnswerCipherBase64(serverSymmetricKey.encryptStringBase64(answer));
				memberSessionKeyInObj.setSessionKeyBase64(Base64.encodeBase64String(sessionKeyBytes));
				memberSessionKeyInObj.setIvBase64(Base64.encodeBase64String(ivBytes));				

				messageFromServer = clientProject.sendSyncInputMessage(memberSessionKeyInObj);					
				if (messageFromServer instanceof MessageResult) {
					messageResultOutObj = (MessageResult)messageFromServer;
					/*if (outObj.getTaskResult().equals("N")) {
						errorMessage = outObj.getResultMessage();
						log.warn(errorMessage);
					}*/			
					
				} else {
					// errorMessage = messageFromServer.toString();
					// FIXME!
					log.warn(messageFromServer.toString());
					
					messageResultOutObj.setResultMessage("서버에서 회원 가입 처리가 실패하였습니다.");
				}	
			} else {
				// FIXME!
				log.warn(messageFromServer.toString());
				
				messageResultOutObj.setResultMessage("서버로 부터 공개키 바이트 배열을 얻는데 실패하였습니다.");
				
			}

			// error 처리르 위한 loop문 종료
			// log.info(String.format("9. errorMessage=[%s]", errorMessage));
			
			// FIXME!
			log.warn(messageResultOutObj.toString());

			req.setAttribute("messageResultOutObj", messageResultOutObj);
			req.setAttribute("webUserSymmetricKey", webUserSymmetricKey);
			// req.setAttribute("errorMessage", errorMessage);
			req.setAttribute("ivBase64", parmIVBase64);
		}

		log.info(String.format("goPage[%s]", goPage));

		printJspPage(req, res, goPage);
	}
}
