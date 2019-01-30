package kr.pe.codda.impl.task.server;

import static kr.pe.codda.impl.jooq.tables.SbBoardFilelistTb.SB_BOARD_FILELIST_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardHistoryTb.SB_BOARD_HISTORY_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardTb.SB_BOARD_TB;

import java.sql.Connection;

import javax.sql.DataSource;

import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.BoardDownloadFileReq.BoardDownloadFileReq;
import kr.pe.codda.impl.message.BoardDownloadFileRes.BoardDownloadFileRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.BoardStateType;
import kr.pe.codda.server.lib.MemberRoleType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;

public class BoardDownloadFileReqServerTask extends AbstractServerTask {

	public BoardDownloadFileReqServerTask() throws DynamicClassCallException {
		super();
	}

	private void sendErrorOutputMessage(String errorMessage, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws InterruptedException {
		log.warn("{}, inObj=", errorMessage, inputMessage.toString());

		MessageResultRes messageResultRes = new MessageResultRes();
		messageResultRes.setTaskMessageID(inputMessage.getMessageID());
		messageResultRes.setIsSuccess(false);
		messageResultRes.setResultMessage(errorMessage);
		toLetterCarrier.addSyncOutputMessage(messageResultRes);
	}

	@Override
	public void doTask(String projectName, PersonalLoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {
		try {
			AbstractMessage outputMessage = doWork(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, (BoardDownloadFileReq) inputMessage);
			toLetterCarrier.addSyncOutputMessage(outputMessage);
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();
			log.warn("errmsg=={}, inObj={}", errorMessage, inputMessage.toString());

			sendErrorOutputMessage(errorMessage, toLetterCarrier, inputMessage);
			return;
		} catch (Exception e) {
			String errorMessage = new StringBuilder().append("unknwon errmsg=").append(e.getMessage())
					.append(", inObj=").append(inputMessage.toString()).toString();

			log.warn(errorMessage, e);

			sendErrorOutputMessage("다운로드가 실패하였습니다", toLetterCarrier, inputMessage);
			return;
		}
	}

	public BoardDownloadFileRes doWork(String dbcpName, BoardDownloadFileReq boardDownloadFileReq) throws Exception {
		// FIXME!
		log.info(boardDownloadFileReq.toString());

		UByte boardID = UByte.valueOf(boardDownloadFileReq.getBoardID());
		UInteger boardNo = UInteger.valueOf(boardDownloadFileReq.getBoardNo());
		UByte attachedFileSeq = UByte.valueOf(boardDownloadFileReq.getAttachedFileSeq());
		String attachedFileName = null;

		DataSource dataSource = DBCPManager.getInstance()
				.getBasicDataSource(dbcpName);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(dbcpName));

			String memberRoleOfRequestedUserID = ValueChecker.checkValidRequestedUserState(conn, create, log,
					boardDownloadFileReq.getRequestedUserID());
			MemberRoleType memberRoleTypeOfRequestedUserID = null;
			try {
				memberRoleTypeOfRequestedUserID = MemberRoleType.valueOf(memberRoleOfRequestedUserID, false);
			} catch (IllegalArgumentException e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder("해당 게시글 상세 보기 요청자의 멤버 타입[")
						.append(memberRoleOfRequestedUserID).append("]이 잘못되어있습니다").toString();
				throw new ServerServiceException(errorMessage);
			}

			Record1<String> boardRecord = create.select(SB_BOARD_TB.BOARD_ST).from(SB_BOARD_TB)
					.where(SB_BOARD_TB.BOARD_ID.eq(boardID)).and(SB_BOARD_TB.BOARD_NO.eq(boardNo)).forUpdate()
					.fetchOne();

			if (null == boardRecord) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder("해당 게시글이 존재 하지 않습니다").toString();
				throw new ServerServiceException(errorMessage);
			}

			String nativeBoardStateType = boardRecord.getValue(SB_BOARD_TB.BOARD_ST);

			BoardStateType boardStateType = null;
			try {
				boardStateType = BoardStateType.valueOf(nativeBoardStateType, false);
			} catch (IllegalArgumentException e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder("게시글의 상태 값[").append(nativeBoardStateType).append("]이 잘못되었습니다")
						.toString();
				throw new ServerServiceException(errorMessage);
			}

			if (BoardStateType.DELETE.equals(boardStateType)) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder("해당 게시글은 삭제된 글입니다").toString();
				throw new ServerServiceException(errorMessage);
			} else if (BoardStateType.BLOCK.equals(boardStateType)) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder("해당 게시글은 관리자에 의해 블락된 글입니다").toString();
				throw new ServerServiceException(errorMessage);
			}

			if (! MemberRoleType.ADMIN.equals(memberRoleTypeOfRequestedUserID)) {

				Record1<String> firstWriterBoardRecord = create.select(SB_BOARD_HISTORY_TB.MODIFIER_ID)
						.from(SB_BOARD_HISTORY_TB).where(SB_BOARD_HISTORY_TB.BOARD_ID.eq(boardID))
						.and(SB_BOARD_HISTORY_TB.BOARD_NO.eq(boardNo))
						.and(SB_BOARD_HISTORY_TB.HISTORY_SQ.eq(UByte.valueOf(0))).fetchOne();

				if (null == firstWriterBoardRecord) {
					try {
						conn.rollback();
					} catch (Exception e) {
						log.warn("fail to rollback");
					}

					String errorMessage = new StringBuilder("해당 게시글의 최초 작성자 정보가 존재 하지 않습니다").toString();
					throw new ServerServiceException(errorMessage);
				}

				String firstWriterID = firstWriterBoardRecord.getValue(SB_BOARD_HISTORY_TB.MODIFIER_ID);

				if (!boardDownloadFileReq.getRequestedUserID().equals(firstWriterID)) {
					try {
						conn.rollback();
					} catch (Exception e) {
						log.warn("fail to rollback");
					}

					String errorMessage = new StringBuilder("타인[").append(firstWriterID).append("] 게시글은 수정 할 수 없습니다")
							.toString();
					throw new ServerServiceException(errorMessage);
				}
			}
			
			Record1<String>  fileListRecord = create.select(SB_BOARD_FILELIST_TB.ATTACHED_FNAME)
					.from(SB_BOARD_FILELIST_TB)
			.where(SB_BOARD_FILELIST_TB.BOARD_ID.eq(boardID))
			.and(SB_BOARD_FILELIST_TB.BOARD_NO.eq(boardNo))
			.and(SB_BOARD_FILELIST_TB.ATTACHED_FILE_SQ.eq(attachedFileSeq)).fetchOne();
			
			if (null == fileListRecord) {
				String errorMessage = "지정한 첨부 파일 정보가 존재하지 않습니다";
				throw new ServerServiceException(errorMessage);
			}
			
			
			attachedFileName = fileListRecord.getValue(SB_BOARD_FILELIST_TB.ATTACHED_FNAME);

			conn.commit();

			
		} catch (ServerServiceException e) {
			throw e;
		} catch (Exception e) {
			if (null != conn) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
			}
			throw e;
		} finally {
			if (null != conn) {
				try {
					conn.close();
				} catch (Exception e) {
					log.warn("fail to close the db connection", e);
				}
			}
		}
		
		BoardDownloadFileRes boardDownloadFileRes = new BoardDownloadFileRes();
		boardDownloadFileRes.setBoardID(boardDownloadFileReq.getBoardID());
		boardDownloadFileRes.setBoardNo(boardDownloadFileReq.getBoardNo());
		boardDownloadFileRes.setAttachedFileSeq(boardDownloadFileReq.getAttachedFileSeq());
		boardDownloadFileRes.setAttachedFileName(attachedFileName);			

		return boardDownloadFileRes;
	}
}
