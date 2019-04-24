package kr.pe.codda.servlet.user;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.CoddaConfigurationManager;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.classloader.ClientMessageCodecManger;
import kr.pe.codda.impl.message.BoardMoveReq.BoardMoveReq;
import kr.pe.codda.impl.message.BoardMoveRes.BoardMoveRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.common.AccessedUserInformation;
import kr.pe.codda.weblib.common.ValueChecker;
import kr.pe.codda.weblib.common.WebCommonStaticUtil;
import kr.pe.codda.weblib.jdf.AbstractUserLoginServlet;

public class BoardMoveProcessSvl extends AbstractUserLoginServlet {
	private static final long serialVersionUID = 1428649367732795305L;
	
	private String installedPathString = null;
	private String mainProjectName = null;
	
	public BoardMoveProcessSvl() {
		super();
		
		CoddaConfiguration runningProjectConfiguration = CoddaConfigurationManager
				.getInstance().getRunningProjectConfiguration();
		mainProjectName = runningProjectConfiguration.getMainProjectName();
		installedPathString = runningProjectConfiguration
				.getInstalledPathString();
	}

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
		/**************** 파라미터 시작 *******************/
		String paramSourceBoardID = req.getParameter("sourceBoardID");
		String paramSourceBoardNo = req.getParameter("sourceBoardNo");
		String paramTargetBoardID = req.getParameter("targetBoardID");
		/**************** 파라미터 종료 *******************/
		
		final short sourceBoardID;
		final long sourceBoardNo;
		final short targetBoardID;
		try {
			sourceBoardID = ValueChecker.checkValidSourceBoardID(paramSourceBoardID);
			sourceBoardNo = ValueChecker.checkValidSourceBoardNo(paramSourceBoardNo);
			targetBoardID = ValueChecker.checkValidTargetBoardID(paramTargetBoardID);
			
			if (targetBoardID == sourceBoardID) {
				throw new IllegalArgumentException("동일한 게시판으로 이동할 수 없습니다");
			}
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String debugMessage = null;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		AccessedUserInformation accessedUserformation = getAccessedUserInformationFromSession(req);
		
		BoardMoveReq boardMoveReq = new BoardMoveReq();
		boardMoveReq.setRequestedUserID(accessedUserformation.getUserID());
		boardMoveReq.setIp(req.getRemoteAddr());
		boardMoveReq.setSourceBoardID(sourceBoardID);
		boardMoveReq.setSourceBoardNo(sourceBoardNo);
		boardMoveReq.setTargetBoardID(targetBoardID);
		
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager
				.getInstance().getMainProjectConnectionPool();
		
		AbstractMessage outputMessage = mainProjectConnectionPool
				.sendSyncInputMessage(ClientMessageCodecManger.getInstance(),
						boardMoveReq);

		if (!(outputMessage instanceof BoardMoveRes)) {
			if ((outputMessage instanceof MessageResultRes)) {
				MessageResultRes messageResultRes = (MessageResultRes) outputMessage;
				String errorMessage = messageResultRes.getResultMessage();
				printErrorMessagePage(req, res, errorMessage, null);
				return;
			}
			
			String errorMessage = "게시글 이동이 실패했습니다";
			String debugMessage = new StringBuilder("입력 메시지[")
					.append(boardMoveReq.getMessageID())
					.append("]에 대한 비 정상 출력 메시지[")
					.append(outputMessage.toString())
					.append("] 도착").toString();
			
			log.error(debugMessage);

			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		BoardMoveRes boardMoveRes = (BoardMoveRes)outputMessage;
		
		for (BoardMoveRes.BoardMoveInfo boardMoveInfo : boardMoveRes.getBoardMoveInfoList()) {
			long fromBoadNo = boardMoveInfo.getFromBoardNo();
			long toBoardNo = boardMoveInfo.getToBoardNo();
			
			for (BoardMoveRes.BoardMoveInfo.AttachedFile attachedFile : boardMoveInfo.getAttachedFileList()) {
				short attachedFileSeq = attachedFile.getAttachedFileSeq();
				
				String fromAttachedFilePathString = WebCommonStaticUtil
						.getAttachedFilePathString(installedPathString,
								mainProjectName, sourceBoardID,
								fromBoadNo, attachedFileSeq);
				
				String toAttachedFilePathString = WebCommonStaticUtil
						.getAttachedFilePathString(installedPathString,
								mainProjectName, targetBoardID,
								toBoardNo, attachedFileSeq);
				
				File srcFile = new File(fromAttachedFilePathString);
				
				if (! srcFile.exists()) {
					String errorMessage = new StringBuilder()
							.append("이동 전 게시글[boardID=")
							.append(sourceBoardID)
							.append(", boardNo=")
							.append(fromBoadNo)
							.append("] 첨부 파일[seq=")
							.append(attachedFileSeq)
							.append("] '")
							.append(fromAttachedFilePathString)
							.append("' 가 존재하지 않습니다").toString();
					log.warn(errorMessage);
					
					continue;
				}
				
				if (! srcFile.isFile()) {
					String errorMessage = new StringBuilder()
							.append("이동 전 게시글[boardID=")
							.append(sourceBoardID)
							.append(", boardNo=")
							.append(fromBoadNo)
							.append("] 첨부 파일[seq=")
							.append(attachedFileSeq)
							.append("] '")
							.append(fromAttachedFilePathString)
							.append("' 는 정규 파일이 아닙니다").toString();
					log.warn(errorMessage);
					
					continue;
				}
				
				File destFile = new File(toAttachedFilePathString);
				if ( destFile.exists()) {
					String errorMessage = new StringBuilder()
							.append("이동 후 게시글[boardID=")
							.append(targetBoardID)
							.append(", boardNo=")
							.append(toBoardNo)
							.append("] 첨부 파일[seq=")
							.append(attachedFileSeq)
							.append("] '")
							.append(fromAttachedFilePathString)
							.append("' 가 존재합니다").toString();
					log.warn(errorMessage);
					
					continue;
				}
				try {
					FileUtils.moveFile(srcFile, destFile);
				} catch(Exception e) {
					String errorMessage = new StringBuilder()
							.append("게시글 첨부 파일 이동 실패, 원본 첨부 파일[")
							.append(fromAttachedFilePathString)
							.append("], 목적지 첨부 파일[")
							.append(toAttachedFilePathString)
							.append("]").toString();
							
					log.warn(errorMessage, e);
				}
			}
		}
		
		// req.setAttribute("boardMoveRes", boardMoveRes);
		printJspPage(req, res, "/jsp/community/BoardMoveProcess.jsp");
		return;
	}
}
