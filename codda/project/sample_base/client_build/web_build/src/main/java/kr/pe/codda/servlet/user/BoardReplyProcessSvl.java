package kr.pe.codda.servlet.user;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.buildsystem.pathsupporter.WebRootBuildSystemPathSupporter;
import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.CoddaConfigurationManager;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.sessionkey.ServerSessionkeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyManager;
import kr.pe.codda.impl.message.BoardReplyReq.BoardReplyReq;
import kr.pe.codda.impl.message.BoardReplyRes.BoardReplyRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.common.BoardType;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;
import kr.pe.codda.weblib.common.WebCommonStaticUtil;
import kr.pe.codda.weblib.exception.WebClinetException;
import kr.pe.codda.weblib.jdf.AbstractMultipartServlet;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class BoardReplyProcessSvl extends AbstractMultipartServlet {

	private static final long serialVersionUID = 6956101754008552227L;

	private String installedPathString = null;
	private String mainProjectName = null;
	private String userWebTempPathString = null;
	private File userWebTempPath = null;

	public BoardReplyProcessSvl() {
		super();

		CoddaConfiguration runningProjectConfiguration = CoddaConfigurationManager
				.getInstance().getRunningProjectConfiguration();
		mainProjectName = runningProjectConfiguration.getMainProjectName();
		installedPathString = runningProjectConfiguration
				.getInstalledPathString();
		userWebTempPathString = WebRootBuildSystemPathSupporter
				.getUserWebTempPathString(installedPathString, mainProjectName);

		userWebTempPath = new File(userWebTempPathString);
	}

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res)
			throws Exception {

		BoardReplyRes boardReplyRes = null;

		try {
			boardReplyRes = doWork(req, res);
		} catch (WebClinetException e) {
			String errorMessage = e.getErrorMessage();
			String debugMessage = e.getDebugMessage();

			log.warn("{}, userID={}, ip={}",
					(null == debugMessage) ? errorMessage : debugMessage,
					getLoginedUserIDFromHttpSession(req), req.getRemoteAddr());

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}

		req.setAttribute("boardReplyRes", boardReplyRes);

		final String goPage = "/jsp/community/BoardReplyProcess.jsp";
		printJspPage(req, res, goPage);
		return;
	}

	public BoardReplyRes doWork(HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		String paramSessionKeyBase64 = null;
		String paramIVBase64 = null;
		String paramBoardID = null;
		String paramParentBoardNo = null;
		String paramSubject = null;
		String paramContent = null;
		List<BoardReplyReq.NewAttachedFile> newAttachedFileList = new ArrayList<BoardReplyReq.NewAttachedFile>();

		// Create a factory for disk-based file items
		DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();

		// Set factory constraints
		diskFileItemFactory
				.setSizeThreshold(WebCommonStaticFinalVars.APACHE_FILEUPLOAD_MAX_MEMORY_SIZE);

		diskFileItemFactory.setRepository(userWebTempPath);

		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(diskFileItemFactory);

		// upload.setHeaderEncoding("UTF-8");
		// log.info("upload.getHeaderEncoding={}", upload.getHeaderEncoding());

		// Set overall request size constraint
		upload.setSizeMax(WebCommonStaticFinalVars.TOTAL_ATTACHED_FILE_MAX_SIZE);
		upload.setFileSizeMax(WebCommonStaticFinalVars.ATTACHED_FILE_MAX_SIZE);

		// Parse the request
		List<FileItem> fileItemList = upload.parseRequest(req);

		for (FileItem fileItem : fileItemList) {
			/*
			 * log.debug("fileItem={}, userId={}, ip={}", fileItem.toString(),
			 * getLoginedUserIDFromHttpSession(req), req.getRemoteAddr());
			 */

			if (fileItem.isFormField()) {
				String formFieldName = fileItem.getFieldName();
				String formFieldValue = fileItem.getString("UTF-8");

				// log.info("form field's name={} and value={}", formFieldName,
				// formFieldValue);

				/**************** 파라미터 시작 *******************/
				if (formFieldName
						.equals(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY)) {
					paramSessionKeyBase64 = formFieldValue;
				} else if (formFieldName
						.equals(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV)) {
					paramIVBase64 = formFieldValue;
				} else if (formFieldName.equals("boardID")) {
					paramBoardID = formFieldValue;
				} else if (formFieldName.equals("parentBoardNo")) {
					paramParentBoardNo = formFieldValue;
				} else if (formFieldName.equals("subject")) {
					paramSubject = formFieldValue;
				} else if (formFieldName.equals("content")) {
					paramContent = formFieldValue;
				} else {
					log.warn("필요 없은 웹 파라미터 '{}' 전달 받음", formFieldName);
				}
				/**************** 파라미터 종료 *******************/

			} else {
				String formFieldName = fileItem.getFieldName();
				String newAttachedFileName = fileItem.getName();
				String newAttachedFileContentType = fileItem.getContentType();
				long newAttachedFileSize = fileItem.getSize();

				if (!formFieldName.equals("newAttachedFile")) {
					String errorMessage = new StringBuilder()
							.append("'newAttachedFile' 로 정해진 첨부 파일의 웹 파라미터 이름[")
							.append(formFieldName).append("]이 잘못되었습니다")
							.toString();
					String debugMessage = null;
					throw new WebClinetException(errorMessage, debugMessage);
				}

				if (newAttachedFileList.size() == WebCommonStaticFinalVars.WEBSITE_ATTACHED_FILE_MAX_COUNT) {
					String errorMessage = new StringBuilder("첨부 파일은 최대[ ")
							.append(WebCommonStaticFinalVars.WEBSITE_ATTACHED_FILE_MAX_COUNT)
							.append("] 까지만 허용됩니다").toString();

					String debugMessage = null;
					throw new WebClinetException(errorMessage, debugMessage);
				}

				/*
				 * log.info("fileName={}, fileContentType={}, fileSize={}",
				 * newAttachedFileName, newAttachedFileContentType,
				 * newAtttachedFileSize);
				 */

				for (char ch : newAttachedFileName.toCharArray()) {
					if (Character.isWhitespace(ch)) {
						String errorMessage = new StringBuilder("첨부 파일명[")
								.append(newAttachedFileName)
								.append("]에 공백 문자가 존재합니다").toString();

						String debugMessage = null;

						throw new WebClinetException(errorMessage, debugMessage);
					} else {
						for (char forbiddenChar : WebCommonStaticFinalVars.FILENAME_FORBIDDEN_CHARS) {
							if (ch == forbiddenChar) {
								String errorMessage = new StringBuilder(
										"첨부 파일명[").append(newAttachedFileName)
										.append("]에 금지된 문자[")
										.append(forbiddenChar)
										.append("]가 존재합니다").toString();

								String debugMessage = null;

								throw new WebClinetException(errorMessage,
										debugMessage);
							}
						}
					}
				}

				String lowerCaseFileName = newAttachedFileName.toLowerCase();

				if (!lowerCaseFileName.endsWith(".jpg")
						&& !lowerCaseFileName.endsWith(".gif")
						&& !lowerCaseFileName.endsWith(".png")) {

					String errorMessage = new StringBuilder("첨부 파일[")
							.append(newAttachedFileName)
							.append("]의 확장자는 jpg, gif, png 만 올 수 있습니다.")
							.toString();

					String debugMessage = null;

					throw new WebClinetException(errorMessage, debugMessage);
				}

				if (newAttachedFileSize == 0) {
					String errorMessage = new StringBuilder("첨부 파일[")
							.append(newAttachedFileName).append("]의 크기가 0 입니다")
							.toString();

					String debugMessage = null;

					throw new WebClinetException(errorMessage, debugMessage);
				}

				if (null == newAttachedFileContentType) {
					String errorMessage = new StringBuilder(
							"알 수 없는 내용이 담긴 첨부 파일[").append(newAttachedFileName)
							.append("] 입니다").toString();
					String debugMessage = null;
					throw new WebClinetException(errorMessage, debugMessage);
				}

				if (!newAttachedFileContentType.equals("image/jpeg")
						&& !newAttachedFileContentType.equals("image/png")
						&& !newAttachedFileContentType.equals("image/gif")) {
					String errorMessage = new StringBuilder("첨부 파일[")
							.append(newAttachedFileName).append("][")
							.append(newAttachedFileContentType)
							.append("]은 이미지 jpg, gif, png 만 올 수 있습니다")
							.toString();
					String debugMessage = null;
					throw new WebClinetException(errorMessage, debugMessage);
				}

				InputStream attachedFileInputStream = fileItem.getInputStream();
				try {
					String guessedContentType = URLConnection
							.guessContentTypeFromStream(attachedFileInputStream);
					if (null == guessedContentType) {
						String errorMessage = new StringBuilder("첨부 파일[")
								.append(newAttachedFileName)
								.append("] 데이터 내용으로 파일 종류를 파악하는데 실패하였습니다")
								.toString();
						String debugMessage = null;

						throw new WebClinetException(errorMessage, debugMessage);
					}

					if (!guessedContentType.equals(newAttachedFileContentType)) {
						String errorMessage = new StringBuilder("전달 받은 첨부 파일[")
								.append(newAttachedFileName).append("] 종류[")
								.append(newAttachedFileContentType)
								.append("]와 첨부 파일 데이터 내용으로 추정된 파일 종류[")
								.append(guessedContentType).append("]가 다릅니다")
								.toString();
						String debugMessage = null;
						throw new WebClinetException(errorMessage, debugMessage);
					}
				} finally {
					try {
						attachedFileInputStream.close();
					} catch (IOException e) {
					}
				}

				BoardReplyReq.NewAttachedFile newAttachedFile = new BoardReplyReq.NewAttachedFile();
				newAttachedFile.setAttachedFileName(newAttachedFileName);
				newAttachedFile.setAttachedFileSize(newAttachedFileSize);
				newAttachedFileList.add(newAttachedFile);
			}
		}

		if (null == paramSessionKeyBase64) {
			String errorMessage = "세션키를 넣어 주세요";
			String debugMessage = new StringBuilder("the web parameter '")
					.append(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY)
					.append("' is null").toString();

			throw new WebClinetException(errorMessage, debugMessage);
		}

		if (null == paramIVBase64) {
			String errorMessage = "iv 값을 넣어 주세요";
			String debugMessage = new StringBuilder("the web parameter '")
					.append(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV)
					.append("' is null").toString();

			throw new WebClinetException(errorMessage, debugMessage);
		}

		ServerSessionkeyIF webServerSessionkey = null;
		try {
			ServerSessionkeyManager serverSessionkeyManager = ServerSessionkeyManager
					.getInstance();
			webServerSessionkey = serverSessionkeyManager
					.getMainProjectServerSessionkey();
		} catch (SymmetricException e) {
			log.warn("SymmetricException", e);

			String errorMessage = "fail to initialize ServerSessionkeyManger instance";
			String debugMessage = new StringBuilder()
					.append("fail to initialize ServerSessionkeyManger instance, errmsg=")
					.append(e.getMessage()).toString();
			throw new WebClinetException(errorMessage, debugMessage);
		}

		byte[] sessionkeyBytes = null;
		try {
			sessionkeyBytes = org.apache.commons.codec.binary.Base64
					.decodeBase64(paramSessionKeyBase64);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("the web parameter '")
					.append(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY)
					.append("' is not a base64 string").toString();

			String debugMessage = new StringBuilder()
					.append("the web parameter '")
					.append(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY)
					.append("'[").append(paramSessionKeyBase64)
					.append("] is not a base64 string, errmsg=")
					.append(e.getMessage()).toString();

			throw new WebClinetException(errorMessage, debugMessage);
		}
		byte[] ivBytes = null;
		try {
			ivBytes = org.apache.commons.codec.binary.Base64
					.decodeBase64(paramIVBase64);
		} catch (Exception e) {
			String errorMessage = new StringBuilder()
					.append("the web parameter '")
					.append(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV)
					.append("' is not a base64 string").toString();

			String debugMessage = new StringBuilder()
					.append("the web parameter '")
					.append(WebCommonStaticFinalVars.PARAMETER_KEY_NAME_OF_SESSION_KEY_IV)
					.append("'[").append(paramIVBase64)
					.append("] is not a base64 string, errmsg=")
					.append(e.getMessage()).toString();

			throw new WebClinetException(errorMessage, debugMessage);
		}

		// log.info("sessionkeyBytes=[{}]",
		// HexUtil.getHexStringFromByteArray(sessionkeyBytes));
		// log.info("ivBytes=[{}]", HexUtil.getHexStringFromByteArray(ivBytes));

		req.setAttribute(
				WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_MODULUS_HEX_STRING,
				webServerSessionkey.getModulusHexStrForWeb());
		req.setAttribute(
				WebCommonStaticFinalVars.REQUEST_KEY_NAME_OF_WEB_SERVER_SYMMETRIC_KEY,
				webServerSessionkey.getNewInstanceOfServerSymmetricKey(true,
						sessionkeyBytes, ivBytes));

		if (null == paramBoardID) {
			String errorMessage = "게시판 식별자 값을 넣어 주세요";
			String debugMessage = "the web parameter 'boardID' is null";
			throw new WebClinetException(errorMessage, debugMessage);
		}

		short boardID = 0;
		try {
			boardID = Short.parseShort(paramBoardID);
		} catch (NumberFormatException nfe) {
			String errorMessage = "잘못된 게시판 식별자 입니다";
			String debugMessage = new StringBuilder(
					"the web parameter 'boardID'[").append(paramBoardID)
					.append("] is not a short").toString();
			throw new WebClinetException(errorMessage, debugMessage);
		}

		try {
			BoardType.valueOf(boardID);
		} catch (IllegalArgumentException e) {
			String errorMessage = "알 수 없는 게시판 식별자 입니다";
			String debugMessage = new StringBuilder(
					"the web parameter 'boardID'[").append(paramBoardID)
					.append("] is not a element of set[")
					.append(BoardType.getSetString()).append("]").toString();
			throw new WebClinetException(errorMessage, debugMessage);
		}

		if (null == paramParentBoardNo) {
			String errorMessage = "부모 게시판 번호 값을 넣어 주세요";
			String debugMessage = "the web parameter 'parentBoardNo' is null";
			throw new WebClinetException(errorMessage, debugMessage);
		}

		long parentBoardNo = 0L;

		try {
			parentBoardNo = Long.parseLong(paramParentBoardNo);
		} catch (NumberFormatException nfe) {
			String errorMessage = "잘못된 부모 게시판 번호입니다";
			String debugMessage = new StringBuilder(
					"the web parameter 'parentBoardNo'[")
					.append(paramParentBoardNo).append("] is not a Long")
					.toString();

			throw new WebClinetException(errorMessage, debugMessage);
		}

		if (parentBoardNo <= 0) {
			String errorMessage = "부모 게시판 번호는 0 보다 커야 합니다";
			String debugMessage = new StringBuilder(
					"the web parameter 'parentBoardNo'[").append(parentBoardNo)
					.append("] is less than or equal to zero").toString();

			throw new WebClinetException(errorMessage, debugMessage);
		}

		if (null == paramSubject) {
			String errorMessage = "제목을 넣어 주세요";
			String debugMessage = "the web parameter 'subject' is null";

			throw new WebClinetException(errorMessage, debugMessage);
		}

		if (null == paramContent) {
			String errorMessage = "내용을 넣어 주세요";
			String debugMessage = "the web parameter 'content' is null";

			throw new WebClinetException(errorMessage, debugMessage);
		}

		BoardReplyReq boardReplyReq = new BoardReplyReq();
		boardReplyReq.setRequestedUserID(getLoginedUserIDFromHttpSession(req));
		boardReplyReq.setBoardID(boardID);
		boardReplyReq.setParentBoardNo(parentBoardNo);
		boardReplyReq.setSubject(paramSubject);
		boardReplyReq.setContent(paramContent);
		boardReplyReq.setIp(req.getRemoteAddr());
		boardReplyReq.setNewAttachedFileCnt((short) newAttachedFileList.size());
		boardReplyReq.setNewAttachedFileList(newAttachedFileList);

		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager
				.getInstance().getMainProjectConnectionPool();
		AbstractMessage outputMessage = mainProjectConnectionPool
				.sendSyncInputMessage(boardReplyReq);

		if ((outputMessage instanceof MessageResultRes)) {
			MessageResultRes messageResultRes = (MessageResultRes) outputMessage;
			String errorMessage = messageResultRes.getResultMessage();
			String debugMessage = null;

			throw new WebClinetException(errorMessage, debugMessage);
		} else if (!(outputMessage instanceof BoardReplyRes)) {
			String errorMessage = "게시판 쓰기가 실패했습니다";
			String debugMessage = new StringBuilder("입력 메시지[")
					.append(boardReplyReq.getMessageID())
					.append("]에 대한 비 정상 출력 메시지[")
					.append(outputMessage.toString()).append("] 도착").toString();

			throw new WebClinetException(errorMessage, debugMessage);
		}

		BoardReplyRes boardReplyRes = (BoardReplyRes) outputMessage;

		// log.info("댓글[{}] 등록이 완료되었습니다", boardReplyRes.toString());

		int indexOfNewAttachedFileList = 0;
		short newAttachedFileSeq = 0;
		for (FileItem fileItem : fileItemList) {
			if (!fileItem.isFormField()) {
				String newAttachedFilePathString = WebCommonStaticUtil
						.getAttachedFilePathString(installedPathString,
								mainProjectName, boardID,
								boardReplyRes.getBoardNo(), newAttachedFileSeq);

				File newAttachedFile = new File(newAttachedFilePathString);
				try {
					fileItem.write(newAttachedFile);
				} catch (Exception e) {
					String errorMessage = new StringBuilder().append("댓글[")
							.append(boardReplyRes.toString())
							.append("]의 임시로 저장된 신규 첨부 파일[index=")
							.append(indexOfNewAttachedFileList)
							.append(", fileName=").append(fileItem.getName())
							.append(", size=").append(fileItem.getSize())
							.append("]의 이름을 게시판 첨부 파일 이름 규칙에 맞도록 개명[")
							.append(newAttachedFilePathString)
							.append("] 하는데 실패하였습니다").toString();

					log.warn(errorMessage, e);
				}
				newAttachedFileSeq++;
				indexOfNewAttachedFileList++;
			}
		}

		return boardReplyRes;
	}
}
