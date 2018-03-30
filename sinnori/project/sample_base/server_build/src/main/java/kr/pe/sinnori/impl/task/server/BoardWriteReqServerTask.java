package kr.pe.sinnori.impl.task.server;

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
import static kr.pe.sinnori.impl.jooq.tables.SbBoardTb.SB_BOARD_TB;
import kr.pe.sinnori.impl.jooq.tables.records.SbBoardTbRecord;
import kr.pe.sinnori.impl.message.BoardWriteReq.BoardWriteReq;
import kr.pe.sinnori.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.sinnori.server.PersonalLoginManagerIF;
import kr.pe.sinnori.server.lib.DeleteFlag;
import kr.pe.sinnori.server.lib.JooqSqlUtil;
import kr.pe.sinnori.server.lib.ServerCommonStaticFinalVars;
import kr.pe.sinnori.server.lib.ValueChecker;
import kr.pe.sinnori.server.task.AbstractServerTask;
import kr.pe.sinnori.server.task.ToLetterCarrier;

public class BoardWriteReqServerTask extends AbstractServerTask {	
	@Override
	public void doTask(String projectName, 
			PersonalLoginManagerIF personalLoginManager, 
			ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {
		// FIXME!
		log.info(inputMessage.toString());	
		
		BoardWriteReq boardWriteReq = (BoardWriteReq) inputMessage;
		
		MessageResultRes messageResultRes = new MessageResultRes();
		messageResultRes.setIsSuccess(false);
		messageResultRes.setTaskMessageID(boardWriteReq.getMessageID());
		
		try {
			ValueChecker.checkValidSubject(boardWriteReq.getSubject());
		} catch(RuntimeException e) {
			log.warn(e.getMessage(), e);
			messageResultRes.setResultMessage(e.getMessage());
			toLetterCarrier.addSyncOutputMessage(messageResultRes);
			return;
		}
		
		try {
			ValueChecker.checkValidContent(boardWriteReq.getContent());
		} catch(RuntimeException e) {
			log.warn(e.getMessage(), e);
			messageResultRes.setResultMessage(e.getMessage());
			toLetterCarrier.addSyncOutputMessage(messageResultRes);
			return;
		}
		
		try {
			ValueChecker.checkValidWriterId(boardWriteReq.getUserId());
		} catch(RuntimeException e) {
			log.warn(e.getMessage(), e);
			messageResultRes.setResultMessage(e.getMessage());
			toLetterCarrier.addSyncOutputMessage(messageResultRes);
			return;
		}
		
		
		try {
			ValueChecker.checkValidIP(boardWriteReq.getIp());
		} catch(RuntimeException e) {
			log.warn(e.getMessage(), e);
			messageResultRes.setResultMessage(e.getMessage());
			toLetterCarrier.addSyncOutputMessage(messageResultRes);
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
			.set(SB_BOARD_TB.DEL_FL, DeleteFlag.NO.getValue())
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
				messageResultRes.setResultMessage("1.게시판 최상의 글 등록이 실패하였습니다.");
				toLetterCarrier.addSyncOutputMessage(messageResultRes);
				return;
			}

			int countOfUpdate = create.update(SB_BOARD_TB)
				.set(SB_BOARD_TB.GROUP_NO, SB_BOARD_TB.BOARD_NO)
			.where(SB_BOARD_TB.BOARD_NO.eq(DSL.field("LAST_INSERT_ID()", UInteger.class))).execute();
			
			if (0 == countOfUpdate) {
				conn.rollback();
				messageResultRes.setResultMessage("1.게시판 최상의 글 등록이 실패하였습니다.");
				toLetterCarrier.addSyncOutputMessage(messageResultRes);
				return;
				
				
			}
			
			// log.info("입력 메시지[{}] 게시판 최상의 글 등록 성공여부[{}]", boardWriteReq.toString(), messageResultRes.getIsSuccess());
			conn.commit();
			messageResultRes.setIsSuccess(true);
			messageResultRes.setResultMessage("게시판 최상의 글 등록이 성공하였습니다.");
			toLetterCarrier.addSyncOutputMessage(messageResultRes);
			return;			
		} catch (Exception e) {
			log.warn("unknown error", e);

			if (null != conn) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback", e1);
				}
			}
			messageResultRes.setResultMessage("알 수 없는 이유로 게시판 조회가 실패하였습니다.");
			toLetterCarrier.addSyncOutputMessage(messageResultRes);
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
