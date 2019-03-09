package kr.pe.codda.impl.task.server;

import static kr.pe.codda.impl.jooq.tables.SbBoardInfoTb.SB_BOARD_INFO_TB;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.Record10;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;

import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.BoardInfoListReq.BoardInfoListReq;
import kr.pe.codda.impl.message.BoardInfoListRes.BoardInfoListRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.MemberRoleType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class BoardInfoListReqServerTask extends AbstractServerTask {

	public BoardInfoListReqServerTask() throws DynamicClassCallException {
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
					(BoardInfoListReq) inputMessage);
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
	public BoardInfoListRes doWork(String dbcpName, BoardInfoListReq boardInfoListReq) throws Exception {
		// FIXME!
		log.info(boardInfoListReq.toString());
		
		try {
			ValueChecker.checkValidWriterID(boardInfoListReq.getRequestedUserID());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}
		
		List<BoardInfoListRes.BoardInfo> boardInfoList = new ArrayList<BoardInfoListRes.BoardInfo>();
		
		DataSource dataSource = DBCPManager.getInstance().getBasicDataSource(dbcpName);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(dbcpName));
			
			String memberRoleOfRequestedUserID = ValueChecker.checkValidRequestedUserState(conn, create, log,
					boardInfoListReq.getRequestedUserID());			
			MemberRoleType  memberRoleTypeOfRequestedUserID = null;
			try {
				memberRoleTypeOfRequestedUserID = MemberRoleType.valueOf(memberRoleOfRequestedUserID, false);
			} catch(IllegalArgumentException e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("알 수 없는 회원[")
					.append(boardInfoListReq.getRequestedUserID())
					.append("]의 역활[")
					.append(memberRoleOfRequestedUserID)
					.append("] 값입니다").toString();
				throw new ServerServiceException(errorMessage);
			}	
			
			if (! MemberRoleType.ADMIN.equals(memberRoleTypeOfRequestedUserID)) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = "게시판 정보 목록 조회 서비스는 관리자 전용 서비스입니다";
				throw new ServerServiceException(errorMessage);
			}
			
			
			Result<Record10<UByte, String, String, Byte, Byte, Byte, Byte, Integer, Integer, UInteger>>  
			boardInfoListResult = create.select(SB_BOARD_INFO_TB.BOARD_ID, SB_BOARD_INFO_TB.BOARD_NAME, 
					SB_BOARD_INFO_TB.BOARD_INFO,
					SB_BOARD_INFO_TB.LIST_TYPE, SB_BOARD_INFO_TB.REPLY_POLICY_TYPE,
					SB_BOARD_INFO_TB.WRITE_PERMISSION_TYPE,
					SB_BOARD_INFO_TB.REPLY_PERMISSION_TYPE,
					SB_BOARD_INFO_TB.CNT, SB_BOARD_INFO_TB.TOTAL,
					SB_BOARD_INFO_TB.NEXT_BOARD_NO)
			.from(SB_BOARD_INFO_TB).fetch();
			
			for (Record10<UByte, String, String, Byte, Byte, Byte, Byte, Integer, Integer, UInteger> boardInfoRecord : boardInfoListResult) {
				UByte boardID = boardInfoRecord.get(SB_BOARD_INFO_TB.BOARD_ID);
				String boardName = boardInfoRecord.get(SB_BOARD_INFO_TB.BOARD_NAME);
				String boardInformation = boardInfoRecord.get(SB_BOARD_INFO_TB.BOARD_INFO);
				byte boardListTypeValue = boardInfoRecord.get(SB_BOARD_INFO_TB.LIST_TYPE);
				byte boardReplyPolicyTypeValue = boardInfoRecord.get(SB_BOARD_INFO_TB.REPLY_POLICY_TYPE);
				byte boardWritePermissionTypeValue = boardInfoRecord.get(SB_BOARD_INFO_TB.WRITE_PERMISSION_TYPE);
				byte boardReplyPermissionTypeValue = boardInfoRecord.get(SB_BOARD_INFO_TB.REPLY_PERMISSION_TYPE);
				int cnt = boardInfoRecord.get(SB_BOARD_INFO_TB.CNT);
				int total = boardInfoRecord.get(SB_BOARD_INFO_TB.TOTAL);
				UInteger nextBoardNo = boardInfoRecord.get(SB_BOARD_INFO_TB.NEXT_BOARD_NO);
				
				BoardInfoListRes.BoardInfo boardInfo = new BoardInfoListRes.BoardInfo();
				boardInfo.setBoardID(boardID.shortValue());
				boardInfo.setBoardName(boardName);
				boardInfo.setBoardInformation(boardInformation);
				boardInfo.setBoardListType(boardListTypeValue);
				boardInfo.setBoardReplyPolicyType(boardReplyPolicyTypeValue);
				boardInfo.setBoardWritePermissionType(boardWritePermissionTypeValue);
				boardInfo.setBoardReplyPermissionType(boardReplyPermissionTypeValue);
				boardInfo.setCnt(cnt);
				boardInfo.setTotal(total);
				boardInfo.setNextBoardNo(nextBoardNo.longValue());
				
				boardInfoList.add(boardInfo);
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

		BoardInfoListRes boardInfoListRes = new BoardInfoListRes();
		boardInfoListRes.setCnt(boardInfoList.size());
		boardInfoListRes.setBoardInfoList(boardInfoList);

		return boardInfoListRes;
	}
}
