package kr.pe.codda.servlet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.buildsystem.pathsupporter.WebRootBuildSystemPathSupporter;
import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.CoddaConfigurationManager;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.BoardModifyReq.BoardModifyReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.common.BoardType;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;
import kr.pe.codda.weblib.jdf.AbstractMultipartServlet;

public class BoardModifyProcessSvl extends AbstractMultipartServlet {

	private static final long serialVersionUID = 3391482674902127151L;

	private void printBoardErrorCallBackPage(HttpServletRequest req, HttpServletResponse res, String errorMessage) {
		final String goPage = "/jsp/community/BoardErrorCallBack.jsp";
		req.setAttribute("errorMessage", errorMessage);
		printJspPage(req, res, goPage);
	}

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {

		// Create a factory for disk-based file items
		DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();

		// Set factory constraints
		diskFileItemFactory.setSizeThreshold(WebCommonStaticFinalVars.APACHE_FILEUPLOAD_MAX_MEMORY_SIZE);

		CoddaConfiguration runningProjectConfiguration = CoddaConfigurationManager.getInstance()
				.getRunningProjectConfiguration();
		String mainProjectName = runningProjectConfiguration.getMainProjectName();
		String sinnoriInstalledPathString = runningProjectConfiguration.getInstalledPathString();
		String webTempPathString = WebRootBuildSystemPathSupporter.getWebTempPathString(sinnoriInstalledPathString,
				mainProjectName);

		diskFileItemFactory.setRepository(new File(webTempPathString));

		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(diskFileItemFactory);

		// upload.setHeaderEncoding("UTF-8");

		// FIXME!
		log.info("upload.getHeaderEncoding={}", upload.getHeaderEncoding());

		// Set overall request size constraint
		upload.setSizeMax(WebCommonStaticFinalVars.ATTACHED_FILE_MAX_SIZE);

		String paramBoardID = null;
		String paramBoardNo = null;
		String paramSubject = null;
		String paramContent = null;
		List<BoardModifyReq.OldAttachedFileSeq> oldAttachedFileSeqList = new ArrayList<BoardModifyReq.OldAttachedFileSeq>();
		List<BoardModifyReq.NewAttachedFile> newAttachedFileList = new ArrayList<BoardModifyReq.NewAttachedFile>();

		// Parse the request
		List<FileItem> fileItemList = upload.parseRequest(req);
		try {

			for (FileItem fileItem : fileItemList) {
				// FIXME!
				log.debug("fileItem={}, userId={}, ip={}", fileItem.toString(), getLoginedUserIDFromHttpSession(req),
						req.getRemoteAddr());

				if (fileItem.isFormField()) {
					String formFieldName = fileItem.getFieldName();
					String formFieldValue = fileItem.getString();
					// FIXME!
					log.info("form field's name={} and value={}", formFieldName, formFieldValue);

					if (formFieldName.equals("boardID")) {
						paramBoardID = formFieldValue;
					} else if (formFieldName.equals("boardNo")) {
						paramBoardNo = formFieldValue;
					} else if (formFieldName.equals("subject")) {
						paramSubject = formFieldValue;
					} else if (formFieldName.equals("content")) {
						paramContent = formFieldValue;
					} else if (formFieldName.equals("oldAttachedFileSeq")) {
						String paramOldAttachedFileSeq = formFieldValue;
						
						BoardModifyReq.OldAttachedFileSeq oldAttachedFileSeq = new BoardModifyReq.OldAttachedFileSeq();						
						try {
							oldAttachedFileSeq.setAttachedFileSeq(Short.parseShort(paramOldAttachedFileSeq));
						} catch(NumberFormatException e) {
							String errorMessage = new StringBuilder("자바 short 타입 변수인 파라미터  '첨부 파일 순번'(oldAttachedFileSeq)의 값[")
							.append(paramOldAttachedFileSeq).append("]이 잘못되었습니다").toString();
							log.warn("{}, userID={}, ip={}", errorMessage, getLoginedUserIDFromHttpSession(req), req.getRemoteAddr());
							
							printBoardErrorCallBackPage(req, res, errorMessage);
							return;
						}						
						
						if (oldAttachedFileSeq.getAttachedFileSeq() > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
							String errorMessage = new StringBuilder("파라미터  '첨부 파일 순번'(oldAttachedFileSeq)의 값[")
									.append(paramOldAttachedFileSeq).append("]이 unsigned byte 범위 최대값(=255)를 넘었습니다").toString();
							log.warn("{}, userID={}, ip={}", errorMessage, getLoginedUserIDFromHttpSession(req), req.getRemoteAddr());

							printBoardErrorCallBackPage(req, res, errorMessage);
							return;
						}
						
						
						oldAttachedFileSeqList.add(oldAttachedFileSeq);
					}
					
					// oldAttachedFileSeqList

				} else {
					if (newAttachedFileList.size() == WebCommonStaticFinalVars.WEBSITE_ATTACHED_FILE_MAX_COUNT) {
						String errorMessage = new StringBuilder("업로드 파일은 최대[ ")
								.append(WebCommonStaticFinalVars.WEBSITE_ATTACHED_FILE_MAX_COUNT).append("] 까지만 허용됩니다")
								.toString();

						String debugMessage = new StringBuilder(errorMessage).append(", userID=")
								.append(getLoginedUserIDFromHttpSession(req)).toString();
						log.warn("{}, ip=", debugMessage, req.getRemoteAddr());

						printBoardErrorCallBackPage(req, res, errorMessage);
						return;
					}

					String fileName = fileItem.getName();
					String fileContentType = fileItem.getContentType();
					long fileSize = fileItem.getSize();

					// FIXME!
					log.info("fileName={}, fileContentType={}, fileSize={}", fileName, fileContentType, fileSize);

					if (fileSize == 0) {
						// FIXME!, 파일 크기 0 인 경우 디버깅용으로 남김
						log.info("file size is zero, fileItem={}, userId={}, ip={}", fileItem.toString(),
								getLoginedUserIDFromHttpSession(req), req.getRemoteAddr());
						continue;
					}

					String lowerCaseFileName = fileName.toLowerCase();

					if (!lowerCaseFileName.endsWith(".jpg") && !lowerCaseFileName.endsWith(".gif")
							&& !lowerCaseFileName.endsWith(".png")) {

						String errorMessage = new StringBuilder("업로드 파일[").append(fileName)
								.append("]의 확장자는 jpg, gif, png 만 올 수 있습니다.").toString();

						String debugMessage = new StringBuilder(errorMessage).append(", userID=")
								.append(getLoginedUserIDFromHttpSession(req)).toString();
						log.warn("{}, ip=", debugMessage, req.getRemoteAddr());

						printBoardErrorCallBackPage(req, res, errorMessage);
						return;
					}

					if (!fileContentType.equals("image/jpeg") && !fileContentType.equals("image/png")
							&& !fileContentType.equals("image/gif")) {
						String errorMessage = new StringBuilder("업로드 파일[").append(fileName).append("][")
								.append(fileContentType).append("]는 이미지 jpg, gif, png 만 올 수 있습니다.").toString();
						String debugMessage = new StringBuilder(errorMessage).append(", userID=")
								.append(getLoginedUserIDFromHttpSession(req)).toString();
						log.warn("{}, ip=", debugMessage, req.getRemoteAddr());

						printBoardErrorCallBackPage(req, res, errorMessage);
						return;
					}

					BoardModifyReq.NewAttachedFile newAttachedFile = new BoardModifyReq.NewAttachedFile();
					newAttachedFile.setAttachedFileName(fileName);
					newAttachedFileList.add(newAttachedFile);
				}
			}
			
			if (null == paramBoardID) {
				String errorMessage = "게시판 식별자 값을 넣어 주세요.";
				String debugMessage = "the web parameter 'boardID' is null";
				log.warn(debugMessage);
				printBoardErrorCallBackPage(req, res, errorMessage);
				return;
			}

			short boardID = 0;
			try {
				boardID = Short.parseShort(paramBoardID);
			} catch (NumberFormatException nfe) {
				String errorMessage = "잘못된 게시판 식별자 입니다";
				String debugMessage = new StringBuilder("the web parameter 'boardID'[").append(paramBoardID)
						.append("] is not a short").toString();
				log.warn(debugMessage);
				printBoardErrorCallBackPage(req, res, errorMessage);
				return;
			}

			try {
				BoardType.valueOf(boardID);
			} catch (IllegalArgumentException e) {
				String errorMessage = "알 수 없는 게시판 식별자 입니다";
				String debugMessage = new StringBuilder("the web parameter 'boardID'[").append(paramBoardID)
						.append("] is not a element of set[").append(BoardType.getSetString()).append("]").toString();
				log.warn(debugMessage);
				printBoardErrorCallBackPage(req, res, errorMessage);
				return;
			}


			if (null == paramBoardNo) {
				String errorMessage = "게시판 번호 값을 넣어 주세요";
				String debugMessage = "the web parameter 'boardNo' is null";
				log.warn(debugMessage);
				printBoardErrorCallBackPage(req, res, errorMessage);
				return;
			}

			long boardNo = 0L;

			try {
				boardNo = Long.parseLong(paramBoardNo);
			} catch (NumberFormatException nfe) {
				String errorMessage = "잘못된 게시판 번호입니다";
				String debugMessage = new StringBuilder("the web parameter \"boardNo\"'s value[").append(paramBoardNo)
						.append("] is not a Long").toString();
				log.warn(debugMessage);
				printBoardErrorCallBackPage(req, res, errorMessage);
				return;
			}

			if (boardNo <= 0) {
				String errorMessage = "게시판 번호는 0 보다 커야 합니다";
				String debugMessage = new StringBuilder("the web parameter \"boardNo\"'s value[").append(paramBoardNo)
						.append("] is less than or equal to zero").toString();
				log.warn(debugMessage);
				printBoardErrorCallBackPage(req, res, errorMessage);
				return;
			}

			if (null == paramSubject) {
				String errorMessage = "제목 값을 넣어주세요.";
				String debugMessage = "the web parameter 'subject' is null";
				log.warn(debugMessage);
				printBoardErrorCallBackPage(req, res, errorMessage);
				return;
			}

			
			if (null == paramContent) {
				String errorMessage = "글 내용 값을 넣어주세요.";
				String debugMessage = "the web parameter 'content' is null";
				log.warn(debugMessage);
				printBoardErrorCallBackPage(req, res, errorMessage);
				return;
			}

			BoardModifyReq boardModifyReq = new BoardModifyReq();
			boardModifyReq.setBoardID(boardID);
			boardModifyReq.setBoardNo(boardNo);
			boardModifyReq.setSubject(paramSubject);
			boardModifyReq.setContent(paramContent);
			boardModifyReq.setModifierID(getLoginedUserIDFromHttpSession(req));
			boardModifyReq.setIp(req.getRemoteAddr());
			boardModifyReq.setOldAttachedFileSeqCnt(oldAttachedFileSeqList.size());
			boardModifyReq.setOldAttachedFileSeqList(oldAttachedFileSeqList);
			boardModifyReq.setNewAttachedFileCnt(newAttachedFileList.size());
			boardModifyReq.setNewAttachedFileList(newAttachedFileList);			

			// FIXME!
			log.debug("boardModifyReq={}", boardModifyReq.toString());

			AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance()
					.getMainProjectConnectionPool();
			AbstractMessage outputMessage = mainProjectConnectionPool.sendSyncInputMessage(boardModifyReq);
			if (! (outputMessage instanceof MessageResultRes)) {				
				String errorMessage = "게시판 수정이 실패했습니다";
				String debugMessage = new StringBuilder("입력 메시지[").append(boardModifyReq.getMessageID())
						.append("]에 대한 비 정상 출력 메시지[").append(outputMessage.toString()).append("] 도착").toString();

				log.error(debugMessage);

				printBoardErrorCallBackPage(req, res, errorMessage);
				return;
			}
			
			MessageResultRes messageResultRes = (MessageResultRes) outputMessage;

			if (messageResultRes.getIsSuccess()) {
				final String goPage = "/jsp/community/ BoardModifyOKCallBack.jsp";				
				printJspPage(req, res, goPage);
				return;
			} else {
				String errorMessage = messageResultRes.getResultMessage();
				printBoardErrorCallBackPage(req, res, errorMessage);
				return;				
			}
			
		} finally {
			for (FileItem fileItem : fileItemList) {
				if (!fileItem.isFormField()) {
					// FIXME!
					log.info("게시판 개별 업로드 파일[{}] 삭제", fileItem.toString());
					try {
						fileItem.delete();
					} catch (Exception e) {
						log.warn("게시판 개별 업로드 파일[{}] 삭제 실패", fileItem.toString());
					}
				}
			}
		}
	}

}
