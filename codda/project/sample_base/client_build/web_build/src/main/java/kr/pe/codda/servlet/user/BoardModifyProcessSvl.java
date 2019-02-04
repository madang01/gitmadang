package kr.pe.codda.servlet.user;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.buildsystem.pathsupporter.WebRootBuildSystemPathSupporter;
import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.CoddaConfigurationManager;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.SymmetricException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.sessionkey.ServerSessionkeyIF;
import kr.pe.codda.common.sessionkey.ServerSessionkeyManager;
import kr.pe.codda.impl.classloader.ClientMessageCodecManger;
import kr.pe.codda.impl.message.BoardModifyReq.BoardModifyReq;
import kr.pe.codda.impl.message.BoardModifyRes.BoardModifyRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.common.BoardType;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;
import kr.pe.codda.weblib.common.WebCommonStaticUtil;
import kr.pe.codda.weblib.exception.WebClinetException;
import kr.pe.codda.weblib.jdf.AbstractMultipartServlet;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * 
 * WARNING! 수정전 조회를 통해 웹 파라미터(nextAttachedFileSeq)는 게시글 수정할때 자신이 첨부한 이미지 파일을 글속에
 * 넣을 수 있도록 한 게시글 수정이라는 일련의 과정에 필요한 변수이기때문에 게시글 수정 처리 결과 메시지(=BoardModifyRes)에서
 * 받는거로 변경하지 말것.
 * 
 * 이미지를 보여주기 위해서는 (1) 게시판 식별자, (2) 게시판 번호, (3) 첨부 파일 시퀀스 라는 3개 정보가 필요하다. 하여 이미지를
 * 본문글에 넣고자 할려면 '첨부 파일 시퀀스' 를 알아야 한다.
 * 
 * '다음 첨부 파일 시퀀스' 는 0부터 시작되며 다음 첨부 파일의 시퀀스 값을 지칭한다. 1개 첨부 파일 추가할때 마다 1씩 증가하며 게시판
 * 조회를 통해서 얻어 올 수 있다.
 * 
 * 이를 정리하면 아래와 같은 공식이 성립한다. '첨부 파일 시퀀스' = 변경전 '다음 첨부 파일 시퀀스' + 신규 첨부 파일 목록의 인덱스
 * 변경후 '다음 첨부 파일 시퀀스' = 변경전 '다음 첨부 파일 시퀀스' + 신규 첨부 파일 목록 갯수
 * 
 * 게시글 신규 작성할때에는 '다음 첨부 파일 시퀀스' 는 0 이지만 수정할때에는 조회를 통해 이를 얻어와야 한다. 이 값은 웹
 * 파라미터(nextAttachedFileSeq) 로 받아 변수 oldNextAttachedFileSeq 에 저장된다.
 * 
 * 첨부 파일명 형식은 '게시판식별자'+'게신판번호'+'첨부파일 시퀀스' 가 되며 첨부 파일 테이블의 키는
 * '게시판식별자'+'게신판번호'+'첨부파일 시퀀스' 이다. 첨부 파일 테이블 레코드와 첨부 파일명이 '게시판식별자'+'게신판번호'+'첨부파일
 * 시퀀스' 로 1:1로 일치 됨을 보장해야 하기위해서 게시글 수정 처리시 게시글 수정시 조회로 부터 끌고온 '다음 첨부 파일 시퀀스' 와
 * DB 의 '다음 첨부 파일 시퀀스' 일치 여부를 검사한다.
 * 
 */
public class BoardModifyProcessSvl extends AbstractMultipartServlet {

	private static final long serialVersionUID = 3391482674902127151L;

	private String installedPathString = null;
	private String mainProjectName = null;
	private String userWebTempPathString = null;
	private File userWebTempPath = null;

	public BoardModifyProcessSvl() {
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
		BoardModifyRes boardModifyRes = null;
		try {
			boardModifyRes = doWork(req, res);
		} catch (WebClinetException e) {
			String errorMessage = e.getErrorMessage();
			String debugMessage = e.getDebugMessage();

			log.warn("{}, userID={}, ip={}",
					(null == debugMessage) ? errorMessage : debugMessage,
					getLoginedUserIDFromHttpSession(req), req.getRemoteAddr());

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}

		req.setAttribute("boardModifyRes", boardModifyRes);

		final String goPage = "/jsp/community/BoardModifyProcess.jsp";
		printJspPage(req, res, goPage);
		return;
	}

	public BoardModifyRes doWork(HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		String paramSessionKeyBase64 = null;
		String paramIVBase64 = null;
		String paramBoardID = null;
		String paramBoardNo = null;
		String paramSubject = null;
		String paramContent = null;
		String paramNextAttachedFileSeq = null;
		List<BoardModifyReq.OldAttachedFile> oldAttachedFileList = new ArrayList<BoardModifyReq.OldAttachedFile>();
		List<BoardModifyReq.NewAttachedFile> newAttachedFileList = new ArrayList<BoardModifyReq.NewAttachedFile>();

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
				} else if (formFieldName.equals("boardNo")) {
					paramBoardNo = formFieldValue;
				} else if (formFieldName.equals("subject")) {
					paramSubject = formFieldValue;
				} else if (formFieldName.equals("content")) {
					paramContent = formFieldValue;
				} else if (formFieldName.equals("nextAttachedFileSeq")) {
					paramNextAttachedFileSeq = formFieldValue;
				} else if (formFieldName.equals("oldAttachedFileSeq")) {
					String paramOldAttachedFileSeq = formFieldValue;

					if ((newAttachedFileList.size() + oldAttachedFileList
							.size()) == WebCommonStaticFinalVars.WEBSITE_ATTACHED_FILE_MAX_COUNT) {
						String errorMessage = new StringBuilder(
								"총 첨부 파일 갯수[보존 원하는 구 첨부 파일 갯수=")
								.append(oldAttachedFileList.size())
								.append(", 신규첨부파일갯수=")
								.append(newAttachedFileList.size())
								.append("]가 최대 갯수[")
								.append(WebCommonStaticFinalVars.WEBSITE_ATTACHED_FILE_MAX_COUNT)
								.append("]를 초과하여 더 이상 보존을 원하는 구 첨부 파일[")
								.append(paramOldAttachedFileSeq)
								.append("을 추가할 수 없습니다").toString();

						String debugMessage = null;
						throw new WebClinetException(errorMessage, debugMessage);
					}

					BoardModifyReq.OldAttachedFile oldAttachedFile = new BoardModifyReq.OldAttachedFile();
					try {
						oldAttachedFile.setAttachedFileSeq(Short
								.parseShort(paramOldAttachedFileSeq));
					} catch (NumberFormatException e) {
						String errorMessage = new StringBuilder(
								"자바 short 타입 변수인 파라미터  '첨부 파일 순번'(oldAttachedFileSeq)의 값[")
								.append(paramOldAttachedFileSeq)
								.append("]이 잘못되었습니다").toString();
						String debugMessage = null;
						throw new WebClinetException(errorMessage, debugMessage);
					}

					if (oldAttachedFile.getAttachedFileSeq() > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
						String errorMessage = new StringBuilder(
								"파라미터  '첨부 파일 순번'(oldAttachedFileSeq)의 값[")
								.append(paramOldAttachedFileSeq)
								.append("]이 unsigned byte 범위 최대값(=255)를 넘었습니다")
								.toString();
						String debugMessage = null;
						throw new WebClinetException(errorMessage, debugMessage);
					}

					if (oldAttachedFileList.contains(oldAttachedFile)) {
						String errorMessage = new StringBuilder(
								"파라미터  '첨부 파일 순번'(oldAttachedFileSeq)의 값[")
								.append(paramOldAttachedFileSeq)
								.append("]이 중복되었습니다").toString();
						String debugMessage = null;
						throw new WebClinetException(errorMessage, debugMessage);
					}

					oldAttachedFileList.add(oldAttachedFile);
				} else {
					log.warn("필요 없은 웹 파라미터 '{}' 전달 받음", formFieldName);
				}

				/**************** 파라미터 종료 *******************/

				// oldAttachedFileSeqList

			} else {
				String formFieldName = fileItem.getFieldName();
				String newAttachedFileName = fileItem.getName();
				String newAttachedFileContentType = fileItem.getContentType();
				long newAttachedFileSize = fileItem.getSize();

				if (!formFieldName.equals("newAttachedFile")) {
					String errorMessage = new StringBuilder()
							.append("'newAttachedFile' 로 정해진 신규 첨부 파일의 웹 파라미터 이름[")
							.append(formFieldName).append("]이 잘못되었습니다")
							.toString();
					String debugMessage = null;
					throw new WebClinetException(errorMessage, debugMessage);
				}

				if ((newAttachedFileList.size() + oldAttachedFileList.size()) == WebCommonStaticFinalVars.WEBSITE_ATTACHED_FILE_MAX_COUNT) {
					String errorMessage = new StringBuilder(
							"총 첨부 파일 갯수[보존 원하는 구 첨부 파일 갯수=")
							.append(oldAttachedFileList.size())
							.append(", 신규첨부파일갯수=")
							.append(newAttachedFileList.size())
							.append("]가 최대 갯수[")
							.append(WebCommonStaticFinalVars.WEBSITE_ATTACHED_FILE_MAX_COUNT)
							.append("]를 초과하여 더 이상 신규 첨부 파일[")
							.append(newAttachedFileName)
							.append("] 을 추가할 수 없습니다").toString();

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
					String errorMessage = "첨부 파일 크기가 0입니다";

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

				BoardModifyReq.NewAttachedFile newAttachedFile = new BoardModifyReq.NewAttachedFile();
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
		
		Base64.Decoder base64Decoder = Base64.getDecoder();
		

		byte[] sessionkeyBytes = null;
		try {
			sessionkeyBytes = base64Decoder.decode(paramSessionKeyBase64);
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
			ivBytes = base64Decoder.decode(paramIVBase64);
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
			String errorMessage = "게시판 식별자 값을 넣어 주세요.";
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

		if (null == paramBoardNo) {
			String errorMessage = "게시판 번호 값을 넣어 주세요";
			String debugMessage = "the web parameter 'boardNo' is null";
			throw new WebClinetException(errorMessage, debugMessage);
		}

		long boardNo = 0L;

		try {
			boardNo = Long.parseLong(paramBoardNo);
		} catch (NumberFormatException nfe) {
			String errorMessage = "잘못된 게시판 번호입니다";
			String debugMessage = new StringBuilder(
					"the web parameter 'boardNo'[").append(paramBoardNo)
					.append("] is not a Long").toString();
			throw new WebClinetException(errorMessage, debugMessage);
		}

		if (boardNo <= 0) {
			String errorMessage = "게시판 번호는 0 보다 커야 합니다";
			String debugMessage = new StringBuilder(
					"the web parameter 'boardNo'[").append(paramBoardNo)
					.append("] is less than or equal to zero").toString();
			throw new WebClinetException(errorMessage, debugMessage);
		}

		if (null == paramSubject) {
			String errorMessage = "제목 값을 넣어주세요";
			String debugMessage = "the web parameter 'subject' is null";
			throw new WebClinetException(errorMessage, debugMessage);
		}

		if (null == paramContent) {
			String errorMessage = "글 내용 값을 넣어주세요";
			String debugMessage = "the web parameter 'content' is null";
			throw new WebClinetException(errorMessage, debugMessage);
		}

		if (null == paramNextAttachedFileSeq) {
			String errorMessage = "다음 첨부 파일 시퀀스 번호를 넣어주세요";
			String debugMessage = "the web parameter 'content' is null";
			throw new WebClinetException(errorMessage, debugMessage);
		}

		short nextAttachedFileSeq = 0;
		try {
			nextAttachedFileSeq = Short.parseShort(paramNextAttachedFileSeq);
		} catch (NumberFormatException e) {
			String errorMessage = "잘못된 다음 첨부 파일 시퀀스 번호입니다";
			String debugMessage = new StringBuilder(
					"the web parameter 'nextAttachedFileSeq'[")
					.append(paramNextAttachedFileSeq)
					.append("] is not a Short").toString();
			throw new WebClinetException(errorMessage, debugMessage);
		}

		if (nextAttachedFileSeq > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
			String errorMessage = "잘못된 '다음 첨부 파일 시퀀스 번호'입니다";
			String debugMessage = new StringBuilder(
					"the web parameter 'nextAttachedFileSeq'[")
					.append(paramNextAttachedFileSeq)
					.append("] is greater than a maximum value(=255) tha an usniged byte can have")
					.toString();
			throw new WebClinetException(errorMessage, debugMessage);
		}

		BoardModifyReq boardModifyReq = new BoardModifyReq();
		boardModifyReq.setRequestedUserID(getLoginedUserIDFromHttpSession(req));
		boardModifyReq.setBoardID(boardID);
		boardModifyReq.setBoardNo(boardNo);
		boardModifyReq.setSubject(paramSubject);
		boardModifyReq.setContent(paramContent);
		boardModifyReq.setIp(req.getRemoteAddr());
		boardModifyReq.setNextAttachedFileSeq(nextAttachedFileSeq);
		boardModifyReq.setOldAttachedFileCnt(oldAttachedFileList.size());
		boardModifyReq.setOldAttachedFileList(oldAttachedFileList);
		boardModifyReq.setNewAttachedFileCnt(newAttachedFileList.size());
		boardModifyReq.setNewAttachedFileList(newAttachedFileList);

		// log.debug("boardModifyReq={}", boardModifyReq.toString());

		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager
				.getInstance().getMainProjectConnectionPool();
		AbstractMessage outputMessage = mainProjectConnectionPool
				.sendSyncInputMessage(ClientMessageCodecManger.getInstance(), boardModifyReq);

		if (!(outputMessage instanceof BoardModifyRes)) {
			if (!(outputMessage instanceof MessageResultRes)) {
				String errorMessage = "게시판 수정이 실패했습니다";
				String debugMessage = new StringBuilder("입력 메시지[")
						.append(boardModifyReq.getMessageID())
						.append("]에 대한 비 정상 출력 메시지[")
						.append(outputMessage.toString()).append("] 도착")
						.toString();

				throw new WebClinetException(errorMessage, debugMessage);
			}

			MessageResultRes messageResultRes = (MessageResultRes) outputMessage;
			String errorMessage = messageResultRes.getResultMessage();
			String debugMessage = null;
			throw new WebClinetException(errorMessage, debugMessage);
		}

		BoardModifyRes boardModifyRes = (BoardModifyRes) outputMessage;

		List<BoardModifyRes.DeletedAttachedFile> deletedAttachedFileSeqList = boardModifyRes
				.getDeletedAttachedFileList();
		for (BoardModifyRes.DeletedAttachedFile deletedAttachedFile : deletedAttachedFileSeqList) {
			short deletedAttachedFileSequence = deletedAttachedFile
					.getAttachedFileSeq();

			String shortFileNameOfDelectedAttachedFile = WebCommonStaticUtil
					.getShortFileNameOfAttachedFile(boardID, boardNo,
							deletedAttachedFileSequence);

			String delectedAttachedFilePathString = WebCommonStaticUtil
					.getAttachedFilePathString(installedPathString,
							mainProjectName, boardID, boardNo,
							deletedAttachedFileSequence);

			File delectedAttachedFile = new File(delectedAttachedFilePathString);

			String tempPathString = WebRootBuildSystemPathSupporter
					.getUserWebTempPathString(installedPathString,
							mainProjectName);

			File tempPath = new File(tempPathString);

			String targetFilePathString = new StringBuilder(tempPathString)
					.append(File.separator)
					.append(shortFileNameOfDelectedAttachedFile).toString();

			File targetFile = new File(targetFilePathString);

			if (!delectedAttachedFile.exists()) {
				log.warn("게시글의 삭제된 첨부 파일[{}] 이 존재하지 않습니다",
						delectedAttachedFilePathString);
				continue;
			}

			boolean result = false;

			if (targetFile.exists()) {
				result = targetFile.delete();

				if (result) {
					log.warn(
							"게시글의 삭제된 첨부 파일[{}] 이 이동할 임시 경로[{}]에 존재하여 임시 경로에 있는 파일 삭제하였습니다",
							delectedAttachedFilePathString, tempPathString);
				} else {
					log.warn(
							"게시글의 삭제된 첨부 파일[{}] 이 이동할 임시 경로[{}]에 존재하여 임시 경로에 있는 파일 삭제 시도했지만 실패하였습니다",
							delectedAttachedFilePathString, tempPathString);

					continue;
				}
			}

			try {
				result = delectedAttachedFile.renameTo(tempPath);
			} catch (Exception e) {
				log.warn(
						"에러가 발생하여 게시글의 삭제된 첨부 파일[{}] 임시 디렉토리[{}]로 이동하는데 실패하였습니다, errmsg={}",
						delectedAttachedFilePathString, tempPathString,
						e.getMessage());
				continue;
			}

			if (!result) {
				log.warn(
						"알 수 없는 이유로 게시글의 삭제된 첨부 파일[{}] 임시 디렉토리[{}]로 이동하는데 실패하였습니다",
						delectedAttachedFilePathString, tempPathString);
				continue;
			}
		}

		// log.info("게시글[{}] 수정이 완료되었습니다", boardModifyRes.toString());
		int indexOfNewAttachedFileList = 0;
		short newAttachedFileSeq = nextAttachedFileSeq;
		for (FileItem fileItem : fileItemList) {
			if (!fileItem.isFormField()) {
				String newAttachedFilePathString = WebCommonStaticUtil
						.getAttachedFilePathString(installedPathString,
								mainProjectName, boardID, boardNo,
								newAttachedFileSeq);

				File newAttachedFile = new File(newAttachedFilePathString);
				try {
					fileItem.write(newAttachedFile);
				} catch (Exception e) {
					String errorMessage = new StringBuilder().append("게시글[")
							.append(boardModifyRes.toString())
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

		return boardModifyRes;
	}

}
