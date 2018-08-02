package kr.pe.codda.impl.task.server;

import static kr.pe.codda.impl.jooq.tables.SbBoardTb.SB_BOARD_TB;
import static kr.pe.codda.impl.jooq.tables.SbSeqTb.SB_SEQ_TB;

import java.sql.Connection;
import java.sql.Timestamp;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.jooq.types.UShort;

import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.BoardReplyReq.BoardReplyReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.BoardStateType;
import kr.pe.codda.server.lib.BoardType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class BoardReplyReqServerTask extends AbstractServerTask {
	
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
			AbstractMessage outputMessage = doService((BoardReplyReq)inputMessage);
			toLetterCarrier.addSyncOutputMessage(outputMessage);
		} catch(ServerServiceException e) {
			String errorMessage = e.getMessage();
			log.warn("errmsg=={}, inObj={}", errorMessage, inputMessage.toString());
			
			sendErrorOutputMessage(errorMessage, toLetterCarrier, inputMessage);
			return;
		} catch(Exception e) {
			String errorMessage = new StringBuilder().append("unknwon errmsg=")
					.append(e.getMessage())
					.append(", inObj=")
					.append(inputMessage.toString()).toString();
			
			log.warn(errorMessage, e);
						
			sendErrorOutputMessage("게시글 댓글 쓰기가 실패하였습니다", toLetterCarrier, inputMessage);
			return;
		}
	}

	public MessageResultRes doService(BoardReplyReq boardReplyReq) throws Exception {
		// FIXME!
		log.info(boardReplyReq.toString());

		BoardType boardType = null;
		try {
			boardType = BoardType.valueOf(boardReplyReq.getBoardId());
		} catch (IllegalArgumentException e) {
			// log.warn(e.getMessage(), e);
			/*sendErrorOutputMessage("잘못된 게시판 종류입니다", toLetterCarrier, boardReplyReq);
			return;*/
			String errorMessage = "잘못된 게시판 식별자입니다";
			throw new ServerServiceException(errorMessage);
		}

		try {
			ValueChecker.checkValidParentBoardNo(boardReplyReq.getParentBoardNo());
		} catch (IllegalArgumentException e) {
			/*log.warn(e.getMessage(), e);
			sendErrorOutputMessage(e.getMessage(), toLetterCarrier, boardReplyReq);
			return;*/
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
			
		}

		try {
			ValueChecker.checkValidSubject(boardReplyReq.getSubject());
		} catch (IllegalArgumentException e) {
			/*log.warn(e.getMessage(), e);
			sendErrorOutputMessage(e.getMessage(), toLetterCarrier, boardReplyReq);
			return;*/
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}

		try {
			ValueChecker.checkValidContent(boardReplyReq.getContent());
		} catch (IllegalArgumentException e) {
			/*log.warn(e.getMessage(), e);
			sendErrorOutputMessage(e.getMessage(), toLetterCarrier, boardReplyReq);
			return;*/
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}

		try {
			ValueChecker.checkValidUserId(boardReplyReq.getUserId());
		} catch (IllegalArgumentException e) {
			/*log.warn(e.getMessage(), e);
			sendErrorOutputMessage(e.getMessage(), toLetterCarrier, boardReplyReq);
			return;*/
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}

		try {
			ValueChecker.checkValidIP(boardReplyReq.getIp());
		} catch (IllegalArgumentException e) {
			/*log.warn(e.getMessage(), e);
			sendErrorOutputMessage(e.getMessage(), toLetterCarrier, boardReplyReq);
			return;*/
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}
		
		final int sequenceID = boardType.getBoardID()+1;

		DataSource dataSource = DBCPManager.getInstance()
				.getBasicDataSource(ServerCommonStaticFinalVars.SB_CONNECTION_POOL_NAME);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL);
			
			Record resultOfSeqManager = create.select(SB_SEQ_TB.SQ_VALUE)
			.from(SB_SEQ_TB)
			.where(SB_SEQ_TB.SQ_ID.eq(UByte.valueOf(sequenceID)))
			.forUpdate().fetchOne();
			
			if (null == resultOfSeqManager) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("시퀀스 식별자[")
						.append(sequenceID)
						.append("]의 시퀀스를 가져오는데 실패하였습니다").toString();
				/*sendErrorOutputMessageForRollback(errorMessage, conn, toLetterCarrier, boardReplyReq);
				return;*/
				throw new ServerServiceException(errorMessage);
			}
			
			
			int countOfUpdate = create.update(SB_SEQ_TB)
					.set(SB_SEQ_TB.SQ_VALUE, SB_SEQ_TB.SQ_VALUE.add(1))
					.where(SB_SEQ_TB.SQ_ID.eq(UByte.valueOf(sequenceID)))
				.execute();
			
			if (0 == countOfUpdate) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("시퀀스 식별자[")
						.append(sequenceID)
						.append("]의 시퀀스 갱신이 실패하였습니다").toString();
				/*sendErrorOutputMessageForRollback(errorMessage, conn, toLetterCarrier, boardReplyReq);
				return;*/
				throw new ServerServiceException(errorMessage);
				
			}
			
			UByte boardID = UByte.valueOf(boardReplyReq.getBoardId());
			UInteger parentBoardNo = UInteger.valueOf(boardReplyReq.getParentBoardNo());
			long seqValue = resultOfSeqManager.get(SB_SEQ_TB.SQ_VALUE).longValue();
			
			// UInteger.valueOf(boardReplyReq.getParentBoardNo()
			
			Record2<UInteger, UShort> parentBoardRecord = create
			.select(SB_BOARD_TB.GROUP_NO, SB_BOARD_TB.GROUP_SQ)
			.from(SB_BOARD_TB)
			.where(SB_BOARD_TB.BOARD_ID.eq(boardID))
			.and(SB_BOARD_TB.BOARD_NO.eq(parentBoardNo)).fetchOne();
			
			if (null == parentBoardRecord) {
				String errorMessage = new StringBuilder().append("부모글[")
						.append(boardReplyReq.getParentBoardNo())
						.append("]이 존재하지 않습니다").toString();
				throw new ServerServiceException(errorMessage);
			}
			
			
			UInteger groupNoOfParentBoard = parentBoardRecord.getValue(SB_BOARD_TB.GROUP_NO);
			UShort groupSeqOfParentBoard = parentBoardRecord.getValue(SB_BOARD_TB.GROUP_SQ);
			
			
			
			Result<Record> boardResult = 
			create.select()
			.from(SB_BOARD_TB)
			.where(SB_BOARD_TB.BOARD_ID.eq(boardID))
			.and(SB_BOARD_TB.BOARD_NO.eq(groupNoOfParentBoard))
			.forUpdate().fetch();
			
			if (null == boardResult) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				/*sendErrorOutputMessageForRollback("부모글 혹은 최상위 글이 존재하지 않습니다", conn, toLetterCarrier, boardReplyReq);
				return;*/
				String errorMessage = new StringBuilder().append("그룹 최상위 글[")
						.append(groupNoOfParentBoard.longValue())
						.append("] 이 존재하지 않습니다").toString();
				throw new ServerServiceException(errorMessage);
			}
			
			create.update(SB_BOARD_TB)
				.set(SB_BOARD_TB.GROUP_SQ, SB_BOARD_TB.GROUP_SQ.add(1))
			.where(SB_BOARD_TB.BOARD_ID.eq(boardID))
			.and(SB_BOARD_TB.GROUP_NO.eq(groupNoOfParentBoard))
			.and(SB_BOARD_TB.GROUP_SQ.gt(groupSeqOfParentBoard)).execute();
			
			/*
			INSERT INTO `SINNORIDB`.`SB_BOARD_TB`
			(`board_no`, `group_no`, `group_sq`,
			`parent_no`, `depth`, `board_id`,
			`writer_id`, `subject`, `content`,
			`attach_id`, `view_cnt`,  `del_fl`,
			`ip`, `reg_dt`, `mod_dt`)
			VALUES
			(0,
			#{groupNo},
			#{groupSeq},
			#{parentBoardNo},
			#{depth},
			#{boardId},
			#{writerId},
			#{subject},
			#{content},
			if (#{attachId} = 0, null, #{attachId}),
			0,
			'N',
			#{ip},
			sysdate(),
			reg_dt)
			*/
			/*
			select 0 as `board_no`, group_no, group_sq+1, 6 as `parent_no`, 
			depth+1, board_id, 'test01' as writer_id,  '주제334' as subject, '내용333' as content, 
			0 as view_cnt, 'N' as del_fl, sysdate() as reg_dt, sysdate() as mod_dt 
			from SB_BOARD_TB t where board_no = 6;
			*/
			
			Field<Timestamp> regetedTimestamp = DSL.currentTimestamp();
			
			// isert into SB_BOARD_TB (
			int countOfInsert = create.insertInto(SB_BOARD_TB, SB_BOARD_TB.BOARD_NO , SB_BOARD_TB.GROUP_NO , SB_BOARD_TB.GROUP_SQ 
					, SB_BOARD_TB.PARENT_NO , SB_BOARD_TB.DEPTH , SB_BOARD_TB.BOARD_ID
					, SB_BOARD_TB.WRITER_ID, SB_BOARD_TB.SUBJECT, SB_BOARD_TB.CONTENT
					, SB_BOARD_TB.VIEW_CNT, SB_BOARD_TB.BOARD_ST
					, SB_BOARD_TB.IP
					, SB_BOARD_TB.REG_DT, SB_BOARD_TB.MOD_DT)
			.select(create.select(DSL.val(UInteger.valueOf(seqValue)).as(SB_BOARD_TB.BOARD_NO.getName())
					, SB_BOARD_TB.GROUP_NO
					, SB_BOARD_TB.GROUP_SQ.add(1).as(SB_BOARD_TB.GROUP_SQ.getName())
					, DSL.inline(UInteger.valueOf(boardReplyReq.getParentBoardNo())).as(SB_BOARD_TB.PARENT_NO.getName())
					, SB_BOARD_TB.DEPTH.add(1).as(SB_BOARD_TB.DEPTH.getName())
					, SB_BOARD_TB.BOARD_ID
					, DSL.val(boardReplyReq.getUserId()).as(SB_BOARD_TB.WRITER_ID.getName())
					, DSL.val(boardReplyReq.getSubject()).as(SB_BOARD_TB.SUBJECT.getName())
					, DSL.val(boardReplyReq.getContent()).as(SB_BOARD_TB.CONTENT.getName())
					, DSL.val(0).as(SB_BOARD_TB.VIEW_CNT.getName())
					, DSL.val(BoardStateType.OK.getValue()).as(SB_BOARD_TB.BOARD_ST.getName())
					, DSL.val(boardReplyReq.getIp())
					, regetedTimestamp.as(SB_BOARD_TB.REG_DT.getName())
					, regetedTimestamp.as(SB_BOARD_TB.MOD_DT.getName()))
			.from(SB_BOARD_TB)
			.where(SB_BOARD_TB.BOARD_NO.eq(UInteger.valueOf(boardReplyReq.getParentBoardNo()))))
			.execute();	
			
			if (0 == countOfInsert) {	
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				/*sendErrorOutputMessageForRollback("1.댓글 쓰기 실패하였습니다", conn, toLetterCarrier, boardReplyReq);
				return;*/
				String errorMessage = "댓글 정보를 DB 에 넣는데 실패하였습니다";
				throw new ServerServiceException(errorMessage);
			}			
			
			conn.commit();
			
			MessageResultRes messageResultRes = new MessageResultRes();
			messageResultRes.setTaskMessageID(boardReplyReq.getMessageID());
			messageResultRes.setIsSuccess(true);
			messageResultRes.setResultMessage("댓글 쓰기 성공하였습니다");
			/*sendSuccessOutputMessageForCommit(messageResultRes, conn, toLetterCarrier);
			return;*/
			return messageResultRes;
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
			
			/*log.warn("unknown error", e);

			sendErrorOutputMessageForRollback("2.댓글 쓰기 실패하였습니다", conn, toLetterCarrier, boardReplyReq);
			return;*/
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
	}
}
