package kr.pe.codda.impl.task.server;

import static kr.pe.codda.impl.jooq.tables.SbBoardInfoTb.SB_BOARD_INFO_TB;

import java.sql.Connection;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.BoardInfoAddReq.BoardInfoAddReq;
import kr.pe.codda.impl.message.BoardInfoAddRes.BoardInfoAddRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.BoardListType;
import kr.pe.codda.server.lib.BoardReplyPolicyType;
import kr.pe.codda.server.lib.JooqSqlUtil;
import kr.pe.codda.server.lib.PermissionType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class BoardInfoAddReqServerTask extends AbstractServerTask {

	public BoardInfoAddReqServerTask() throws DynamicClassCallException {
		super();
	}

	private void sendErrorOutputMessage(String errorMessage, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws InterruptedException {
		log.warn("{}, inObj={}", errorMessage, inputMessage.toString());

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
			AbstractMessage outputMessage = doWork(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME,
					(BoardInfoAddReq) inputMessage);
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

			sendErrorOutputMessage("게시판 정보 등록이 실패하였습니다", toLetterCarrier, inputMessage);
			return;
		}
	}
	
	public BoardInfoAddRes doWork(String dbcpName, BoardInfoAddReq boardInfoAddReq) throws Exception {
		// FIXME!
		log.info(boardInfoAddReq.toString());
		
		try {
			ValueChecker.checkValidWriterID(boardInfoAddReq.getRequestedUserID());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}
		
		String boardName = boardInfoAddReq.getBoardName();
		
		if (null == boardName) {
			String errorMessage = "게시판 이름을 넣어 주세요";
			throw new ServerServiceException(errorMessage);
		}
		
		boardName = boardName.trim();
		
		if (0 == boardName.length()) {
			String errorMessage = "게시판 이름을 다시 넣어 주세요";
			throw new ServerServiceException(errorMessage);
		}
		
		if (boardName.length()  < 2) {
			String errorMessage = "게시판 이름을 2 글자 이상 넣어 주세요";
			throw new ServerServiceException(errorMessage);
		}
		
		if (boardName.getBytes(CommonStaticFinalVars.DEFUALT_CHARSET).length > 30) {
			String errorMessage = "게시판 이름의 바이트 배열 크기가 30을 넘습니다";
			throw new ServerServiceException(errorMessage);
		}
		
		BoardListType boardListType = null;
		try {
			boardListType = BoardListType.valueOf(boardInfoAddReq.getBoardListType());
		} catch(IllegalArgumentException e) {
			String errorMessage = "게시판 목록 유형 값이 잘못되었습니다";
			throw new ServerServiceException(errorMessage);
		}
		
		BoardReplyPolicyType boardReplyPolicyType = null;
		try {
			boardReplyPolicyType = BoardReplyPolicyType.valueOf(boardInfoAddReq.getBoardReplyPolicyType());
		} catch(IllegalArgumentException e) {
			String errorMessage = "게시판 댓글 유형 값이 잘못되었습니다";
			throw new ServerServiceException(errorMessage);
		}
		
		PermissionType boardWritePermissionType = null;		
		try {
			boardWritePermissionType = PermissionType.valueOf(boardInfoAddReq.getBoardWritePermissionType());
		} catch(IllegalArgumentException e) {
			String errorMessage = "게시판 본문 쓰기 권한 유형 값이 잘못되었습니다";
			throw new ServerServiceException(errorMessage);
		}
		
		PermissionType boardReplyPermissionType = null;		
		try {
			boardReplyPermissionType = PermissionType.valueOf(boardInfoAddReq.getBoardReplyPermissionType());
		} catch(IllegalArgumentException e) {
			String errorMessage = "게시판 댓글 쓰기 권한 유형 값이 잘못되었습니다";
			throw new ServerServiceException(errorMessage);
		}
		
		short boardID = -1;
		
		DataSource dataSource = DBCPManager.getInstance().getBasicDataSource(dbcpName);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(dbcpName));
			
			ServerDBUtil.checkUserAccessRights(conn, create, log, "게시판 정보 추가 서비스", PermissionType.ADMIN, boardInfoAddReq.getRequestedUserID());
			
			boardID = create.select(JooqSqlUtil.getIfField(SB_BOARD_INFO_TB.BOARD_ID.max(), 0, SB_BOARD_INFO_TB.BOARD_ID.max().add(1)))
			.from(SB_BOARD_INFO_TB)
			.fetchOne(0, Short.class);
			
			if (boardID > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = "새롭게 얻은 게시판 식별자 값이 최대값을 초과하여 더 이상 추가할 수 없습니다";
				throw new ServerServiceException(errorMessage);
			}
			
			int countOfInsert = create.insertInto(SB_BOARD_INFO_TB).set(SB_BOARD_INFO_TB.BOARD_ID, UByte.valueOf(boardID))
			.set(SB_BOARD_INFO_TB.BOARD_NAME, boardName)
			.set(SB_BOARD_INFO_TB.LIST_TYPE, boardListType.getValue())
			.set(SB_BOARD_INFO_TB.REPLY_POLICY_TYPE, boardReplyPolicyType.getValue())
			.set(SB_BOARD_INFO_TB.WRITE_PERMISSION_TYPE, boardWritePermissionType.getValue())
			.set(SB_BOARD_INFO_TB.REPLY_PERMISSION_TYPE, boardReplyPermissionType.getValue())
			.set(SB_BOARD_INFO_TB.CNT, 0)
			.set(SB_BOARD_INFO_TB.TOTAL, 0)
			.set(SB_BOARD_INFO_TB.NEXT_BOARD_NO, UInteger.valueOf(1)).execute();
			
			if (0 == countOfInsert) {
				String errorMessage = new StringBuilder()
						.append("게시판 정보[게시판식별자:")
						.append(boardID)
						.append(", 게시판이름:").append(boardInfoAddReq.getBoardName()).append("] 삽입 실패").toString();
				throw new Exception(errorMessage);
			}
			
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

		BoardInfoAddRes boardInfoAddRes = new BoardInfoAddRes();
		boardInfoAddRes.setBoardID(boardID);

		return boardInfoAddRes;
	}

}
