package kr.pe.sinnori.impl.task.server;

import static kr.pe.sinnori.impl.jooq.tables.SbBoardTb.SB_BOARD_TB;

import java.sql.Connection;
import java.sql.Timestamp;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.jooq.types.UShort;

import kr.pe.sinnori.common.etc.DBCPManager;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.impl.jooq.tables.records.SbBoardTbRecord;
import kr.pe.sinnori.impl.message.BoardWriteReq.BoardWriteReq;
import kr.pe.sinnori.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.sinnori.server.PersonalLoginManagerIF;
import kr.pe.sinnori.server.lib.BoardType;
import kr.pe.sinnori.server.lib.BoardStateType;
import kr.pe.sinnori.server.lib.JooqSqlUtil;
import kr.pe.sinnori.server.lib.ServerCommonStaticFinalVars;
import kr.pe.sinnori.server.lib.ValueChecker;
import kr.pe.sinnori.server.task.AbstractServerTask;
import kr.pe.sinnori.server.task.ToLetterCarrier;

public class BoardWriteReqServerTask extends AbstractServerTask {	
	
	@SuppressWarnings("unused")
	private void sendErrorOutputtMessageForCommit(String errorMessage,
			Connection conn,			
			ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws InterruptedException {
		try {
			conn.commit();
		} catch (Exception e) {
			log.warn("fail to commit");
		}
		sendErrorOutputMessage(errorMessage, toLetterCarrier, inputMessage);
	}
	
	private void sendErrorOutputMessageForRollback(String errorMessage,
			Connection conn,			
			ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws InterruptedException {
		log.warn("{}, inObj={}", errorMessage, inputMessage.toString());
		if (null != conn) {
			try {
				conn.rollback();
			} catch (Exception e) {
				log.warn("fail to rollback");
			}
		}		
		sendErrorOutputMessage(errorMessage, toLetterCarrier, inputMessage);
	}
	
	private void sendErrorOutputMessage(String errorMessage,			
			ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws InterruptedException {
		log.warn("{}, inObj={}", errorMessage, inputMessage.toString());
		
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
	public void doTask(String projectName, 
			PersonalLoginManagerIF personalLoginManager, 
			ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {
		doWork(projectName, personalLoginManager, toLetterCarrier, (BoardWriteReq)inputMessage);
	}
	
	public void doWork(String projectName, 
			PersonalLoginManagerIF personalLoginManager, 
			ToLetterCarrier toLetterCarrier,
			BoardWriteReq boardWriteReq) throws Exception {
		// FIXME!
		log.info(boardWriteReq.toString());	
		
		// boardWriteReq.getBoardId()
		try {
			BoardType.valueOf(boardWriteReq.getBoardId());
		} catch(IllegalArgumentException e) {
			log.warn(e.getMessage(), e);
			sendErrorOutputMessage("잘못된 게시판 종류입니다", toLetterCarrier, boardWriteReq);
			return;
		}
		
		try {
			ValueChecker.checkValidSubject(boardWriteReq.getSubject());
		} catch(IllegalArgumentException e) {
			log.warn(e.getMessage(), e);
			sendErrorOutputMessage(e.getMessage(), toLetterCarrier, boardWriteReq);
			return;
		}
		
		try {
			ValueChecker.checkValidContent(boardWriteReq.getContent());
		} catch(IllegalArgumentException e) {
			log.warn(e.getMessage(), e);
			sendErrorOutputMessage(e.getMessage(), toLetterCarrier, boardWriteReq);
			return;
		}
		
		try {
			ValueChecker.checkValidWriterId(boardWriteReq.getUserId());
		} catch(IllegalArgumentException e) {
			log.warn(e.getMessage(), e);
			sendErrorOutputMessage(e.getMessage(), toLetterCarrier, boardWriteReq);
			return;
		}
		
		
		try {
			ValueChecker.checkValidIP(boardWriteReq.getIp());
		} catch(IllegalArgumentException e) {
			log.warn(e.getMessage(), e);
			sendErrorOutputMessage(e.getMessage(), toLetterCarrier, boardWriteReq);
			return;
		}
		
		
		
		DataSource dataSource = DBCPManager.getInstance()
				.getBasicDataSource(ServerCommonStaticFinalVars.SB_CONNECTION_POOL_NAME);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			
			DSLContext create = DSL.using(conn, SQLDialect.MYSQL);
			
			/*
			INSERT INTO `SINNORIDB`.`SB_BOARD_TB`
			(`board_no`, `group_no`, `group_sq`, `parent_no`, `depth`, `board_id`, `writer_id`,
			`subject`, `content`, `attach_id`, `view_cnt`, `del_fl`, `ip`, `reg_dt`, `mod_dt`)
			VALUES
			(0, 0, 1, 0, 0, #{boardId}, #{userId}, #{subject}, #{content},
			if (#{attachId} = 0, null, #{attachId}), 0, 'N', #{ip}, sysdate(), reg_dt);
			
			update SB_BOARD_TB set group_no=board_no where board_no=LAST_INSERT_ID()
			*/			
			
			InsertSetMoreStep<SbBoardTbRecord> boardInsertSetStep = 
					create.insertInto(SB_BOARD_TB)
			.set(SB_BOARD_TB.BOARD_NO, UInteger.valueOf(0L))
			.set(SB_BOARD_TB.GROUP_NO,  UInteger.valueOf(0L))
			.set(SB_BOARD_TB.GROUP_SQ, UShort.valueOf(1))
			.set(SB_BOARD_TB.PARENT_NO, UInteger.valueOf(0L))
			.set(SB_BOARD_TB.DEPTH, UByte.valueOf(0))
			.set(SB_BOARD_TB.BOARD_ID, UByte.valueOf(boardWriteReq.getBoardId()))
			.set(SB_BOARD_TB.WRITER_ID, boardWriteReq.getUserId())
			.set(SB_BOARD_TB.SUBJECT, boardWriteReq.getSubject())
			.set(SB_BOARD_TB.CONTENT, boardWriteReq.getContent())
			.set(SB_BOARD_TB.VIEW_CNT, Integer.valueOf(0))
			.set(SB_BOARD_TB.DEL_FL, BoardStateType.NO.getValue())
			.set(SB_BOARD_TB.IP, boardWriteReq.getIp())
			.set(SB_BOARD_TB.REG_DT, JooqSqlUtil.getFieldOfSysDate(Timestamp.class))
			.set(SB_BOARD_TB.MOD_DT, SB_BOARD_TB.REG_DT);
			
			int resultOfInsert;
			
			if (0 == boardWriteReq.getAttachId()) {
				resultOfInsert = boardInsertSetStep.set(SB_BOARD_TB.ATTACH_ID, (UInteger)null).execute();
			} else {
				resultOfInsert = boardInsertSetStep.set(SB_BOARD_TB.ATTACH_ID, UInteger.valueOf(boardWriteReq.getAttachId())).execute();
			}
			
			if (0 == resultOfInsert) {
				conn.rollback();				
				sendErrorOutputMessageForRollback("1.게시판 최상의 글 등록이 실패하였습니다.", conn, toLetterCarrier, boardWriteReq);
				return;
			}

			int countOfUpdate = create.update(SB_BOARD_TB)
				.set(SB_BOARD_TB.GROUP_NO, SB_BOARD_TB.BOARD_NO)
			.where(SB_BOARD_TB.BOARD_NO.eq(DSL.field("LAST_INSERT_ID()", UInteger.class))).execute();
			
			if (0 == countOfUpdate) {				
				sendErrorOutputMessageForRollback("2.게시판 최상의 글 등록이 실패하였습니다.", conn, toLetterCarrier, boardWriteReq);
				return;
				
				
			}
			
			// log.info("입력 메시지[{}] 게시판 최상의 글 등록 성공여부[{}]", boardWriteReq.toString(), messageResultRes.getIsSuccess());
			
			MessageResultRes messageResultRes = new MessageResultRes();
			messageResultRes.setTaskMessageID(boardWriteReq.getMessageID());
			messageResultRes.setIsSuccess(true);
			messageResultRes.setResultMessage("게시판 최상의 글 등록이 성공하였습니다.");
			sendSuccessOutputMessageForCommit(messageResultRes, conn, toLetterCarrier);
			return;			
		} catch (Exception e) {
			log.warn("unknown error", e);
			sendErrorOutputMessageForRollback("3.게시판 최상의 글 등록이 실패하였습니다.", conn, toLetterCarrier, boardWriteReq);
			return;

		} finally {
			if (null != conn) {
				try {
					conn.close();
				} catch(Exception e) {
					log.warn("fail to close the db connection", e);
				}
			}
		}
	}
}
