package kr.pe.sinnori.impl.task.server;

import static kr.pe.sinnori.impl.jooq.tables.SbBoardTb.SB_BOARD_TB;

import java.sql.Connection;
import java.sql.Timestamp;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;

import kr.pe.sinnori.common.etc.DBCPManager;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.impl.jooq.tables.SbBoardTb;
import kr.pe.sinnori.impl.message.BoardReplyReq.BoardReplyReq;
import kr.pe.sinnori.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.sinnori.server.PersonalLoginManagerIF;
import kr.pe.sinnori.server.lib.BoardType;
import kr.pe.sinnori.server.lib.BoardStateType;
import kr.pe.sinnori.server.lib.ServerCommonStaticFinalVars;
import kr.pe.sinnori.server.lib.ValueChecker;
import kr.pe.sinnori.server.task.AbstractServerTask;
import kr.pe.sinnori.server.task.ToLetterCarrier;

public class BoardReplyReqServerTask extends AbstractServerTask {
	@SuppressWarnings("unused")
	private void sendErrorOutputMessageForCommit(String errorMessage, Connection conn, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws InterruptedException {
		try {
			conn.commit();
		} catch (Exception e) {
			log.warn("fail to commit");
		}
		sendErrorOutputMessage(errorMessage, toLetterCarrier, inputMessage);
	}

	private void sendErrorOutputMessageForRollback(String errorMessage, Connection conn,
			ToLetterCarrier toLetterCarrier, AbstractMessage inputMessage) throws InterruptedException {
		if (null != conn) {
			try {
				conn.rollback();
			} catch (Exception e) {
				log.warn("fail to rollback");
			}
		}
		sendErrorOutputMessage(errorMessage, toLetterCarrier, inputMessage);
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

	private void sendSuccessOutputMessageForCommit(AbstractMessage outputMessage, Connection conn,
			ToLetterCarrier toLetterCarrier) throws InterruptedException {
		try {
			conn.commit();
		} catch (Exception e) {
			log.warn("fail to commit");
		}

		toLetterCarrier.addSyncOutputMessage(outputMessage);
	}

	@Override
	public void doTask(String projectName, PersonalLoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {

		doWork(projectName, personalLoginManager, toLetterCarrier, (BoardReplyReq) inputMessage);
	}

	public void doWork(String projectName, PersonalLoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			BoardReplyReq boardReplyReq) throws Exception {
		// FIXME!
		log.info(boardReplyReq.toString());

		try {
			BoardType.valueOf(boardReplyReq.getBoardId());
		} catch (IllegalArgumentException e) {
			log.warn(e.getMessage(), e);
			sendErrorOutputMessage("잘못된 게시판 종류입니다", toLetterCarrier, boardReplyReq);
			return;
		}

		try {
			ValueChecker.checkValidParentBoardNo(boardReplyReq.getParentBoardNo());
		} catch (RuntimeException e) {
			log.warn(e.getMessage(), e);
			sendErrorOutputMessage(e.getMessage(), toLetterCarrier, boardReplyReq);
			return;
		}

		try {
			ValueChecker.checkValidSubject(boardReplyReq.getSubject());
		} catch (IllegalArgumentException e) {
			log.warn(e.getMessage(), e);
			sendErrorOutputMessage(e.getMessage(), toLetterCarrier, boardReplyReq);
			return;
		}

		try {
			ValueChecker.checkValidContent(boardReplyReq.getContent());
		} catch (IllegalArgumentException e) {
			log.warn(e.getMessage(), e);
			sendErrorOutputMessage(e.getMessage(), toLetterCarrier, boardReplyReq);
			return;
		}

		try {
			ValueChecker.checkValidUserId(boardReplyReq.getUserId());
		} catch (IllegalArgumentException e) {
			log.warn(e.getMessage(), e);
			sendErrorOutputMessage(e.getMessage(), toLetterCarrier, boardReplyReq);
			return;
		}

		try {
			ValueChecker.checkValidIP(boardReplyReq.getIp());
		} catch (IllegalArgumentException e) {
			log.warn(e.getMessage(), e);
			sendErrorOutputMessage(e.getMessage(), toLetterCarrier, boardReplyReq);
			return;
		}

		DataSource dataSource = DBCPManager.getInstance()
				.getBasicDataSource(ServerCommonStaticFinalVars.SB_CONNECTION_POOL_NAME);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL);
			
			// UInteger.valueOf(boardReplyReq.getParentBoardNo()
			
			Result<Record> boardResult = 
			create.select()
			.from(SB_BOARD_TB)
			.where(SB_BOARD_TB.BOARD_NO.eq(create.select(SB_BOARD_TB.GROUP_NO)
					.from(SB_BOARD_TB)
					.where(SB_BOARD_TB.BOARD_NO.eq(UInteger.valueOf(boardReplyReq.getParentBoardNo()))).asField()))
			.forUpdate().fetch();
			
			if (null == boardResult) {
				sendErrorOutputMessageForRollback("부모글 혹은 최상위 글이 존재하지 않습니다", conn, toLetterCarrier, boardReplyReq);
				return;
			}
			
			
			/*
			update SB_BOARD_TB a inner join SB_BOARD_TB b on a.group_no = b.group_no 
			set a.group_sq=a.group_sq+1 where b.board_no = 6 and a.group_sq > b.group_sq;
			*/
			
			SbBoardTb a = SB_BOARD_TB.as("a");
			SbBoardTb b = SB_BOARD_TB.as("b");
			
			create.update(a.innerJoin(b).on(a.GROUP_NO.eq(b.GROUP_NO)))
				.set(a.GROUP_SQ, a.GROUP_SQ.add(1))
			.where(b.BOARD_NO.eq(UInteger.valueOf(boardReplyReq.getParentBoardNo())))
			.and(a.GROUP_SQ.gt(b.GROUP_SQ)).execute();
			
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
					, SB_BOARD_TB.VIEW_CNT, SB_BOARD_TB.DEL_FL, 
					SB_BOARD_TB.REG_DT, SB_BOARD_TB.MOD_DT)
			.select(create.select(DSL.val(UInteger.valueOf(0)).as(SB_BOARD_TB.BOARD_NO.getName())
					, SB_BOARD_TB.GROUP_NO
					, SB_BOARD_TB.GROUP_SQ.add(1).as(SB_BOARD_TB.GROUP_SQ.getName())
					, DSL.inline(UInteger.valueOf(boardReplyReq.getParentBoardNo())).as(SB_BOARD_TB.PARENT_NO.getName())
					, SB_BOARD_TB.DEPTH.add(1).as(SB_BOARD_TB.DEPTH.getName())
					, SB_BOARD_TB.BOARD_ID
					, DSL.val(boardReplyReq.getUserId()).as(SB_BOARD_TB.WRITER_ID.getName())
					, DSL.val(boardReplyReq.getSubject()).as(SB_BOARD_TB.SUBJECT.getName())
					, DSL.val(boardReplyReq.getContent()).as(SB_BOARD_TB.CONTENT.getName())
					, DSL.val(0).as(SB_BOARD_TB.VIEW_CNT.getName())
					, DSL.val(BoardStateType.NO.getValue()).as(SB_BOARD_TB.DEL_FL.getName())
					, regetedTimestamp.as(SB_BOARD_TB.REG_DT.getName())
					, regetedTimestamp.as(SB_BOARD_TB.MOD_DT.getName()))
			.from(SB_BOARD_TB)
			.where(SB_BOARD_TB.BOARD_NO.eq(UInteger.valueOf(boardReplyReq.getParentBoardNo()))))
			.execute();	
			
			if (0 == countOfInsert) {				
				sendErrorOutputMessageForRollback("1.댓글 쓰기 실패하였습니다", conn, toLetterCarrier, boardReplyReq);
				return;
			}			
			
			MessageResultRes messageResultRes = new MessageResultRes();
			messageResultRes.setTaskMessageID(boardReplyReq.getMessageID());
			messageResultRes.setIsSuccess(true);
			messageResultRes.setResultMessage("댓글 쓰기 성공하였습니다");
			sendSuccessOutputMessageForCommit(messageResultRes, conn, toLetterCarrier);
			return;
		} catch (Exception e) {
			log.warn("unknown error", e);

			sendErrorOutputMessageForRollback("2.댓글 쓰기 실패하였습니다", conn, toLetterCarrier, boardReplyReq);
			return;

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
