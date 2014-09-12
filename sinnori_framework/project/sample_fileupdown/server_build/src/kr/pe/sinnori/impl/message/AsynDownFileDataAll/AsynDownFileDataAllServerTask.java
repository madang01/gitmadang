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
package kr.pe.sinnori.impl.message.AsynDownFileDataAll;

import kr.pe.sinnori.common.configuration.ServerProjectConfig;
import kr.pe.sinnori.common.exception.UpDownFileException;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.common.updownfile.LocalSourceFileResource;
import kr.pe.sinnori.common.updownfile.LocalSourceFileResourceManager;
import kr.pe.sinnori.impl.message.CancelDownloadFileResult.CancelDownloadFileResult;
import kr.pe.sinnori.impl.message.DownFileDataResult.DownFileDataResult;
import kr.pe.sinnori.server.ClientResource;
import kr.pe.sinnori.server.LoginManagerIF;
import kr.pe.sinnori.server.executor.AbstractAuthServerExecutor;
import kr.pe.sinnori.server.executor.LetterSender;

/**
 * 비동기 메시지 방식의 파일 다운로드 서버 비지니스 로직 클래스
 * 
 * @author "Jonghoon Won"
 * 
 */
public class AsynDownFileDataAllServerTask extends AbstractAuthServerExecutor {

	@Override
	public void doTask(ServerProjectConfig serverProjectConfig,
			LoginManagerIF loginManager,
			LetterSender letterSender, AbstractMessage messageFromClient)
			throws Exception {
		// FIXME!
		log.info(messageFromClient.toString());

		AsynDownFileDataAll inObj = (AsynDownFileDataAll) messageFromClient;
		int serverSourceFileID = inObj.getServerSourceFileID();
		int clientTargetFileID = inObj.getClientTargetFileID();
		/*
		 * int serverSourceFileID =
		 * (Integer)inObj.getAttribute("serverSourceFileID"); int
		 * clientTargetFileID =
		 * (Integer)inObj.getAttribute("clientTargetFileID");
		 */

		ClientResource clientResource = letterSender.getClientResource();
		LocalSourceFileResourceManager localTargetFileResourceManager = LocalSourceFileResourceManager
				.getInstance();

		LocalSourceFileResource localSourceFileResource = localTargetFileResourceManager
				.getLocalSourceFileResource(serverSourceFileID);

		if (null == localSourceFileResource) {
			log.warn(String.format(
					"serverSourceFileID[%d] 다운로드 파일을 받을 자원이 준비되지 않았습니다.",
					serverSourceFileID));

			/*
			 * OutputMessage outObj =
			 * messageManger.createOutputMessage("DownFileDataResult"); //
			 * outObj.messageHeaderInfo.mailboxID =
			 * CommonStaticFinal.SERVER_MAILBOX_ID; //
			 * outObj.messageHeaderInfo.mailID =
			 * clientResource.getServerMailID();
			 * outObj.setAttribute("taskResult", "N");
			 * outObj.setAttribute("resultMessage",
			 * "서버에서 다운로드 파일을 받을 자원이 준비되지 않았습니다.");
			 * outObj.setAttribute("serverSourceFileID", serverSourceFileID);
			 * outObj.setAttribute("fileBlockNo", 0);
			 * outObj.setAttribute("fileData", new byte[0]);
			 * 
			 * // letterToClientList.addLetterToClient(fromSC, outObj);
			 * letterSender.sendAsyn(outObj);
			 */
			DownFileDataResult outObj = new DownFileDataResult();
			outObj.setTaskResult("N");
			outObj.setResultMessage("서버에서 다운로드 파일을 받을 자원이 준비되지 않았습니다.");
			outObj.setServerSourceFileID(serverSourceFileID);
			outObj.setClientTargetFileID(clientTargetFileID);
			outObj.setFileBlockNo(0);
			outObj.setFileData(new byte[0]);
			letterSender.directSendAsynMessage(outObj);
			return;
		}

		// boolean isCompletedReadingFile = false;
		int startFileBlockNo = localSourceFileResource.getStartFileBlockNo();
		int endFileBlockNo = localSourceFileResource.getEndFileBlockNo();
		int i = startFileBlockNo;

		for (; i <= endFileBlockNo; i++) {
			if (!clientResource.isLogin()) {
				log.info("not login so loop break");
				break;
			}
			if (localSourceFileResource.isCanceled()) {
				log.info(String
						.format("serverSourceFileID[%s] to clientTargetFileID[%d] 파일 다운로드 취소",
								serverSourceFileID,
								localSourceFileResource.getTargetFileID()));

				CancelDownloadFileResult outObj = new CancelDownloadFileResult();
				outObj.setTaskResult("Y");
				outObj.setResultMessage(String.format(
						"서버 다운로드용 원본 파일[%d] 자원을 성공적으로 해제하였습니다.",
						serverSourceFileID));
				outObj.setServerSourceFileID(serverSourceFileID);
				outObj.setClientTargetFileID(clientTargetFileID);
				letterSender.directSendAsynMessage(outObj);
				
				clientResource.removeLocalSourceFileID(serverSourceFileID);
				return;
			}

			byte[] fileData = null;
			try {
				fileData = localSourceFileResource.getByteArrayOfFileBlockNo(i);
			} catch (OutOfMemoryError e) {
				DownFileDataResult outObj = new DownFileDataResult();
				outObj.setTaskResult("N");
				outObj.setResultMessage(String.format(
						"파일 블락 번호[%d]에 해당하는 파일 블락 메모리 확보 실패", i));
				outObj.setServerSourceFileID(serverSourceFileID);
				outObj.setClientTargetFileID(clientTargetFileID);
				outObj.setFileBlockNo(i);
				outObj.setFileData(new byte[0]);
				letterSender.directSendAsynMessage(outObj);
				return;
			}
			try {
				localSourceFileResource.readSourceFileData(i, fileData, true);
			} catch (IllegalArgumentException e) {
				log.warn(String.format("serverSourceFileID[%d] lock free::%s",
						serverSourceFileID, e.getMessage()), e);
				DownFileDataResult outObj = new DownFileDataResult();
				outObj.setTaskResult("N");
				outObj.setResultMessage(new StringBuilder("서버 에러 메시지\n")
						.append(e.getMessage()).toString());
				outObj.setServerSourceFileID(serverSourceFileID);
				outObj.setClientTargetFileID(clientTargetFileID);
				outObj.setFileBlockNo(i);
				outObj.setFileData(new byte[0]);
				letterSender.directSendAsynMessage(outObj);
				return;
			} catch (UpDownFileException e) {
				log.warn(String.format("serverSourceFileID[%d] lock free::%s",
						serverSourceFileID, e.getMessage()), e);
				DownFileDataResult outObj = new DownFileDataResult();
				outObj.setTaskResult("N");
				outObj.setResultMessage(new StringBuilder("서버 에러 메시지\n")
						.append(e.getMessage()).toString());
				outObj.setServerSourceFileID(serverSourceFileID);
				outObj.setClientTargetFileID(clientTargetFileID);
				outObj.setFileBlockNo(i);
				outObj.setFileData(new byte[0]);
				letterSender.directSendAsynMessage(outObj);
				return;
			}

			/*
			 * OutputMessage outObj =
			 * messageManger.createOutputMessage("DownFileDataResult"); //
			 * outObj.messageHeaderInfo.mailboxID =
			 * CommonStaticFinal.SERVER_MAILBOX_ID; //
			 * outObj.messageHeaderInfo.mailID =
			 * clientResource.getServerMailID();
			 * outObj.setAttribute("taskResult", "Y");
			 * 
			 * outObj.setAttribute("resultMessage",
			 * "서버에서 요청한 파일 조각을 성공적으로 읽었습니다.");
			 * outObj.setAttribute("serverSourceFileID", serverSourceFileID);
			 * outObj.setAttribute("clientTargetFileID", clientTargetFileID);
			 * outObj.setAttribute("fileBlockNo", i);
			 * outObj.setAttribute("fileData", fileData);
			 */
			// letterSender.sendAsyn(outObj);
			DownFileDataResult outObj = new DownFileDataResult();
			outObj.setTaskResult("Y");
			outObj.setResultMessage("서버에서 요청한 파일 조각을 성공적으로 읽었습니다.");
			outObj.setServerSourceFileID(serverSourceFileID);
			outObj.setClientTargetFileID(clientTargetFileID);
			outObj.setFileBlockNo(i);
			outObj.setFileData(fileData);
			letterSender.directSendAsynMessage(outObj);
		}

		// FIXME!
		log.info(String
				.format("serverSourceFileID[%s] to clientTargetFileID[%d] 파일 다운로드 전체 완료",
						serverSourceFileID,
						localSourceFileResource.getTargetFileID()));

		clientResource.removeLocalSourceFileID(serverSourceFileID);

	}
}
