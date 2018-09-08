package kr.pe.codda.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.buildsystem.pathsupporter.WebRootBuildSystemPathSupporter;
import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.CoddaConfigurationManager;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.BoardWriteReq.BoardWriteReq;
import kr.pe.codda.impl.message.BoardWriteRes.BoardWriteRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.common.BoardType;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;
import kr.pe.codda.weblib.jdf.AbstractMultipartServlet;

public class BoardWriteProcessSvl extends AbstractMultipartServlet {

	private static final long serialVersionUID = 6954552927880470265L;
	
	private void printBoardProcessFailureCallBackPage(HttpServletRequest req, HttpServletResponse res, String errorMessage) {
		final String goPage = "/jsp/community/BoardProcessFailureCallBack.jsp";
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
		String paramSubject = null;
		String paramContent = null;
		List<BoardWriteReq.NewAttachedFile> newAttachedFileList = new ArrayList<BoardWriteReq.NewAttachedFile>();

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

					/**************** 파라미터 시작 *******************/
					if (formFieldName.equals("boardID")) {
						paramBoardID = formFieldValue;
					} else if (formFieldName.equals("subject")) {
						paramSubject = formFieldValue;
					} else if (formFieldName.equals("content")) {
						paramContent = formFieldValue;
					}
					/**************** 파라미터 종료 *******************/

				} else {
					if (newAttachedFileList.size() == WebCommonStaticFinalVars.WEBSITE_ATTACHED_FILE_MAX_COUNT) {
						String errorMessage = new StringBuilder("첨부 파일은 최대[ ")
								.append(WebCommonStaticFinalVars.WEBSITE_ATTACHED_FILE_MAX_COUNT).append("] 까지만 허용됩니다")
								.toString();

						String debugMessage = new StringBuilder(errorMessage).append(", userID=")
								.append(getLoginedUserIDFromHttpSession(req)).toString();
						log.warn("{}, ip=", debugMessage, req.getRemoteAddr());

						printBoardProcessFailureCallBackPage(req, res, errorMessage);
						return;
					}

					String newAttachedFileName = fileItem.getName();
					String newAttachedFileContentType = fileItem.getContentType();
					long newAtttachedFileSize = fileItem.getSize();

					// FIXME!
					log.info("fileName={}, fileContentType={}, fileSize={}", newAttachedFileName, newAttachedFileContentType, newAtttachedFileSize);

					for (char ch : newAttachedFileName.toCharArray()) {
						if (Character.isWhitespace(ch)) {
							String errorMessage = new StringBuilder("첨부 파일명[")
									.append(newAttachedFileName)
									.append("]에 공백 문자가 존재합니다").toString();

							String debugMessage = new StringBuilder(errorMessage).append(", userID=")
									.append(getLoginedUserIDFromHttpSession(req)).toString();
							log.warn("{}, ip=", debugMessage, req.getRemoteAddr());

							printBoardProcessFailureCallBackPage(req, res, errorMessage);
							return;
						} else {
							for (char forbiddenChar : WebCommonStaticFinalVars.FILENAME_FORBIDDEN_CHARS) {
								if (ch == forbiddenChar) {
									String errorMessage = new StringBuilder("첨부 파일명[")
											.append(newAttachedFileName)
											.append("]에 금지된 문자[")
											.append(forbiddenChar)
											.append("]가 존재합니다").toString();

									String debugMessage = new StringBuilder(errorMessage).append(", userID=")
											.append(getLoginedUserIDFromHttpSession(req)).toString();
									log.warn("{}, ip=", debugMessage, req.getRemoteAddr());

									printBoardProcessFailureCallBackPage(req, res, errorMessage);
									return;
								}
							}
						}
					}

					String lowerCaseFileName = newAttachedFileName.toLowerCase();

					if (!lowerCaseFileName.endsWith(".jpg") && !lowerCaseFileName.endsWith(".gif")
							&& !lowerCaseFileName.endsWith(".png")) {

						String errorMessage = new StringBuilder("첨부 파일[").append(newAttachedFileName)
								.append("]의 확장자는 jpg, gif, png 만 올 수 있습니다.").toString();

						String debugMessage = new StringBuilder(errorMessage).append(", userID=")
								.append(getLoginedUserIDFromHttpSession(req)).toString();
						log.warn("{}, ip=", debugMessage, req.getRemoteAddr());

						printBoardProcessFailureCallBackPage(req, res, errorMessage);
						return;
					}
					
					if (newAtttachedFileSize == 0) {
						String errorMessage = "첨부 파일 크기가 0입니다";

						String debugMessage = new StringBuilder(errorMessage).append(", userID=")
								.append(getLoginedUserIDFromHttpSession(req)).toString();
						log.warn("{}, ip=", debugMessage, req.getRemoteAddr());

						printBoardProcessFailureCallBackPage(req, res, errorMessage);
						return;
					}

					if (null == newAttachedFileContentType) {
						String errorMessage = new StringBuilder("전달 받은 첨부 파일[").append(newAttachedFileName).append("] 종류가 없습니다").toString();
						String debugMessage = new StringBuilder(errorMessage).append(", userID=")
								.append(getLoginedUserIDFromHttpSession(req)).toString();
						log.warn("{}, ip=", debugMessage, req.getRemoteAddr());

						printBoardProcessFailureCallBackPage(req, res, errorMessage);
						return;
					}

					if (! newAttachedFileContentType.equals("image/jpeg") && !newAttachedFileContentType.equals("image/png")
							&& !newAttachedFileContentType.equals("image/gif")) {
						String errorMessage = new StringBuilder("첨부 파일[").append(newAttachedFileName).append("][")
								.append(newAttachedFileContentType).append("]은 이미지 jpg, gif, png 만 올 수 있습니다.").toString();
						String debugMessage = new StringBuilder(errorMessage).append(", userID=")
								.append(getLoginedUserIDFromHttpSession(req)).toString();
						log.warn("{}, ip=", debugMessage, req.getRemoteAddr());

						printBoardProcessFailureCallBackPage(req, res, errorMessage);
						return;
					}
					
					InputStream attachedFileInputStream = fileItem.getInputStream();					
					try {
						String guessedContentType = URLConnection.guessContentTypeFromStream(attachedFileInputStream);
						if (null == guessedContentType) {
							String errorMessage = new StringBuilder("첨부 파일[").append(newAttachedFileName).append("] 데이터 내용으로 파일 종류를 파악하는데 실패하였습니다").toString();
							String debugMessage = new StringBuilder(errorMessage).append(", userID=")
									.append(getLoginedUserIDFromHttpSession(req)).toString();
							log.warn("{}, ip=", debugMessage, req.getRemoteAddr());

							printBoardProcessFailureCallBackPage(req, res, errorMessage);
							return;
						}
						
						
						if (!guessedContentType.equals(newAttachedFileContentType)) {								
							String errorMessage = new StringBuilder("전달 받은 첨부 파일[").append(newAttachedFileName).append("] 종류[")
									.append(newAttachedFileContentType)
									.append("]와 첨부 파일 데이터 내용으로 추정된 파일 종류[")
									.append(guessedContentType).append("]가 다릅니다").toString();
							String debugMessage = new StringBuilder(errorMessage).append(", userID=")
									.append(getLoginedUserIDFromHttpSession(req)).toString();
							log.warn("{}, ip=", debugMessage, req.getRemoteAddr());

							printBoardProcessFailureCallBackPage(req, res, errorMessage);
							return;
						}
					} finally {
						try {
							attachedFileInputStream.close();
						} catch(IOException e) {
						}
					}

					BoardWriteReq.NewAttachedFile newAttachedFile = new BoardWriteReq.NewAttachedFile();
					newAttachedFile.setAttachedFileName(newAttachedFileName);
					newAttachedFileList.add(newAttachedFile);
				}
			}

			if (null == paramBoardID) {
				String errorMessage = "게시판 식별자 값을 넣어 주세요";
				String debugMessage = "the web parameter 'boardID' is null";
				
				log.warn(debugMessage);
				
				printBoardProcessFailureCallBackPage(req, res, errorMessage);
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
				
				printBoardProcessFailureCallBackPage(req, res, errorMessage);
				return;
			}

			try {
				BoardType.valueOf(boardID);
			} catch (IllegalArgumentException e) {
				String errorMessage = "알 수 없는 게시판 식별자 입니다";
				String debugMessage = new StringBuilder("the web parameter 'boardID'[").append(paramBoardID)
						.append("] is not a element of set[").append(BoardType.getSetString()).append("]").toString();
				
				log.warn(debugMessage);
				
				printBoardProcessFailureCallBackPage(req, res, errorMessage);
				return;
			}

			if (null == paramSubject) {
				String errorMessage = "제목 값을 넣어주세요";
				String debugMessage = "the web parameter 'subject' is null";
				
				log.warn(debugMessage);
				
				printBoardProcessFailureCallBackPage(req, res, errorMessage);
				return;
			}

			if (null == paramContent) {
				String errorMessage = "글 내용 값을 넣어주세요";
				String debugMessage = "the web parameter 'content' is null";
				
				log.warn(debugMessage);
				
				printBoardProcessFailureCallBackPage(req, res, errorMessage);
				return;
			}

			if (newAttachedFileList.size() > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
				String errorMessage = "첨부 파일 갯수의 데이터 타입은 unsigned byte 로 최대 255개를 넘을 수 없습니다";
				String debugMessage = "the web parameter 'content' is null";
				
				log.warn(debugMessage);
				
				printBoardProcessFailureCallBackPage(req, res, errorMessage);
				return;
			}

			BoardWriteReq boardWriteReq = new BoardWriteReq();
			boardWriteReq.setRequestUserID(getLoginedUserIDFromHttpSession(req));
			boardWriteReq.setBoardID(boardID);
			boardWriteReq.setSubject(paramSubject);
			boardWriteReq.setContent(paramContent);
			boardWriteReq.setIp(req.getRemoteAddr());
			boardWriteReq.setNewAttachedFileCnt((short) newAttachedFileList.size());
			boardWriteReq.setNewAttachedFileList(newAttachedFileList);

			// FIXME!
			log.info("inObj={}", boardWriteReq.toString());

			System.out.println(boardWriteReq.toString());

			AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance()
					.getMainProjectConnectionPool();
			AbstractMessage outputMessage = mainProjectConnectionPool.sendSyncInputMessage(boardWriteReq);

			if ((outputMessage instanceof MessageResultRes)) {
				MessageResultRes messageResultRes = (MessageResultRes) outputMessage;

				printBoardProcessFailureCallBackPage(req, res, messageResultRes.getResultMessage());
				return;
			} else if (!(outputMessage instanceof BoardWriteRes)) {
				String errorMessage = "게시판 쓰기가 실패했습니다";
				String debugMessage = new StringBuilder("입력 메시지[").append(boardWriteReq.getMessageID())
						.append("]에 대한 비 정상 출력 메시지[").append(outputMessage.toString()).append("] 도착").toString();

				log.error(debugMessage);

				printBoardProcessFailureCallBackPage(req, res, errorMessage);
				return;
			}

			BoardWriteRes boardWriteRes = (BoardWriteRes) outputMessage;
			int fileSequence = 0;
			for (FileItem fileItem : fileItemList) {

				String newAttachedFileFullName = new StringBuilder(WebRootBuildSystemPathSupporter
						.getWebUploadPathString(sinnoriInstalledPathString, mainProjectName)).append(File.separator)
								.append(WebCommonStaticFinalVars.WEBSITE_ATTACHED_FILE_PREFIX).append("_BoardID")
								.append(boardWriteRes.getBoardID()).append("_BoardNo")
								.append(boardWriteRes.getBoardNo()).append("Seq").append(fileSequence)
								.append(WebCommonStaticFinalVars.WEBSITE_ATTACHED_FILE_SUFFIX).toString();

				File newAttachedFile = new File(newAttachedFileFullName);

				if (newAttachedFile.exists()) {
					/** 만약 첨부 파일 정식 이름을 갖는 파일이 존재하면 삭제 */
					try {
						FileUtils.forceDelete(newAttachedFile);

						log.info(
								"the attached file[seq={}, fileName={}, size={}]'s target file[{}] exists and it was deleted",
								fileSequence, fileItem.getName(), fileItem.getSize(), newAttachedFileFullName);
					} catch (IOException e) {
						log.warn("fail to delete the attached file[seq={}, fileName={}, size={}]'s old target file[{}]",
								fileSequence, fileItem.getName(), fileItem.getSize(), newAttachedFileFullName);
					}
				}

				try {
					fileItem.write(newAttachedFile);
				} catch (Exception e) {
					log.warn("fail to copy the attached file[seq={}, fileName={}, size={}] to the target file[{}]",
							fileSequence, fileItem.getName(), fileItem.getSize(), newAttachedFileFullName);
				}

				fileSequence++;
			}

			final String goPage = "/jsp/community/BoardWriteOKCallBack.jsp";
			printJspPage(req, res, goPage);
			return;
		} finally {
			for (FileItem fileItem : fileItemList) {
				if (!fileItem.isFormField()) {
					// FIXME!
					log.info("게시판 개별 첨부 파일[{}] 삭제", fileItem.toString());
					try {
						fileItem.delete();
					} catch (Exception e) {
						log.warn("게시판 개별 첨부 파일[{}] 삭제 실패", fileItem.toString());
					}
				}
			}
		}
	}

}
