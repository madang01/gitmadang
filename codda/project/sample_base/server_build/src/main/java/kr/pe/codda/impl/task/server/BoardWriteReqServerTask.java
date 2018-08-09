package kr.pe.codda.impl.task.server;

import static kr.pe.codda.impl.jooq.tables.SbBoardFilelistTb.SB_BOARD_FILELIST_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardHistoryTb.SB_BOARD_HISTORY_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardTb.SB_BOARD_TB;
import static kr.pe.codda.impl.jooq.tables.SbSeqTb.SB_SEQ_TB;

import java.sql.Connection;
import java.sql.Timestamp;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.jooq.types.UShort;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.BoardWriteReq.BoardWriteReq;
import kr.pe.codda.impl.message.BoardWriteRes.BoardWriteRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.BoardStateType;
import kr.pe.codda.server.lib.BoardType;
import kr.pe.codda.server.lib.JooqSqlUtil;
import kr.pe.codda.server.lib.SequenceType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class BoardWriteReqServerTask extends AbstractServerTask {	
	
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
	
	@Override
	public void doTask(String projectName, 
			PersonalLoginManagerIF personalLoginManager, 
			ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {
		try {
			AbstractMessage outputMessage = doService((BoardWriteReq) inputMessage);
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

			sendErrorOutputMessage("게시판 최상의 글 등록이 실패하였습니다", toLetterCarrier, inputMessage);
			return;
		}
	}
	
	
	
	public BoardWriteRes doService(BoardWriteReq boardWriteReq) throws Exception {
		// FIXME!
		log.info(boardWriteReq.toString());	
		
		BoardType boardType = null;
		SequenceType boardSequenceType = null;
		
		try {
			boardType = BoardType.valueOf(boardWriteReq.getBoardID());
		} catch(IllegalArgumentException e) {			
			String errorMessage = "잘못된 게시판 식별자입니다";
			throw new ServerServiceException(errorMessage);
		}
		
		if (BoardType.NOTICE.equals(boardType)) {
			boardSequenceType = SequenceType.NOTICE_BOARD;
		} else if (BoardType.FREE.equals(boardType)) {
			boardSequenceType = SequenceType.FREE_BOARD;
		} else if (BoardType.FAQ.equals(boardType)) {
			boardSequenceType = SequenceType.FAQ_BOARD;
		} else {
			String errorMessage = "알 수 없는 게시판 타입입니다";
			throw new ServerServiceException(errorMessage);
		}
		
		try {
			ValueChecker.checkValidSubject(boardWriteReq.getSubject());
		} catch(IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}
		
		try {
			ValueChecker.checkValidContent(boardWriteReq.getContent());
		} catch(IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}
		
		try {
			ValueChecker.checkValidWriterID(boardWriteReq.getWriterID());
		} catch(IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}
		
		
		try {
			ValueChecker.checkValidIP(boardWriteReq.getIp());
		} catch(IllegalArgumentException e) {			
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}				
		
		if (boardWriteReq.getNewAttachedFileCnt() > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
			String errorMessage = new StringBuilder().append("첨부 파일 등록 갯수[")
					.append(boardWriteReq.getNewAttachedFileCnt())
					.append("]가 최대 첨부 파일 등록 갯수[")
					.append(CommonStaticFinalVars.UNSIGNED_BYTE_MAX)
					.append("]를 초과하였습니다").toString();
			throw new ServerServiceException(errorMessage);
		}
		
		UByte boardSequenceID =  UByte.valueOf(boardSequenceType.getSequenceID());
		UByte boardID = UByte.valueOf(boardWriteReq.getBoardID());
		
		
		DataSource dataSource = DBCPManager.getInstance()
				.getBasicDataSource(ServerCommonStaticFinalVars.SB_CONNECTION_POOL_NAME);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			
			DSLContext create = DSL.using(conn, SQLDialect.MYSQL);
			
			ValueChecker.checkValidMemberStateForUserID(conn, create, log, boardWriteReq.getWriterID());	
			
			
			Record boardSequenceRecord = create.select(SB_SEQ_TB.SQ_VALUE)
			.from(SB_SEQ_TB)
			.where(SB_SEQ_TB.SQ_ID.eq(boardSequenceID))
			.forUpdate().fetchOne();
			
			if (null == boardSequenceRecord) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("게시판 번호로 사용할 게시판[")
						.append(boardType.getName())
						.append("] 시퀀스 식별자[")
						.append(boardSequenceID)
						.append("]의 시퀀스를 가져오는데 실패하였습니다").toString();				
				throw new ServerServiceException(errorMessage);
			}
			
			UInteger boardNo = boardSequenceRecord.get(SB_SEQ_TB.SQ_VALUE);			
			
			int countOfUpdate = create.update(SB_SEQ_TB)
					.set(SB_SEQ_TB.SQ_VALUE, SB_SEQ_TB.SQ_VALUE.add(1))
					.where(SB_SEQ_TB.SQ_ID.eq(boardSequenceID))
				.execute();
			
			if (0 == countOfUpdate) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("게시판 시퀀스 식별자[")
						.append(boardSequenceID)
						.append("]의 시퀀스 갱신이 실패하였습니다").toString();
				throw new ServerServiceException(errorMessage);
			}
			
			int boardInsertCount = 
					create.insertInto(SB_BOARD_TB)
			.set(SB_BOARD_TB.BOARD_ID, boardID)
			.set(SB_BOARD_TB.BOARD_NO, boardNo)
			.set(SB_BOARD_TB.GROUP_NO,  boardNo)
			.set(SB_BOARD_TB.GROUP_SQ, UShort.valueOf(0))
			.set(SB_BOARD_TB.PARENT_NO, UInteger.valueOf(0L))
			.set(SB_BOARD_TB.DEPTH, UByte.valueOf(0))
			.set(SB_BOARD_TB.VIEW_CNT, Integer.valueOf(0))
			.set(SB_BOARD_TB.BOARD_ST, BoardStateType.OK.getValue()).execute();
			
			if (0 == boardInsertCount) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				String errorMessage = "게시판 최상의 글 등록이 실패하였습니다";
				throw new ServerServiceException(errorMessage);
			}
			
			int boardHistoryInsertCount = create.insertInto(SB_BOARD_HISTORY_TB)
			.set(SB_BOARD_HISTORY_TB.BOARD_ID, boardID)
			.set(SB_BOARD_HISTORY_TB.BOARD_NO, boardNo)
			.set(SB_BOARD_HISTORY_TB.HISTORY_SQ, UByte.valueOf(0))
			.set(SB_BOARD_HISTORY_TB.SUBJECT, boardWriteReq.getSubject())
			.set(SB_BOARD_HISTORY_TB.CONTENT, boardWriteReq.getContent())
			.set(SB_BOARD_HISTORY_TB.MODIFIER_ID, boardWriteReq.getWriterID())
			.set(SB_BOARD_HISTORY_TB.IP, boardWriteReq.getIp())
			.set(SB_BOARD_HISTORY_TB.REG_DT, JooqSqlUtil.getFieldOfSysDate(Timestamp.class))			
			.execute();			
			
			if (0 == boardHistoryInsertCount) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				String errorMessage = "게시판 최상의 글 내용을 저장하는데 실패하였습니다";
				throw new ServerServiceException(errorMessage);
			}
			
			int attachedFileListIndex = 0;
			for (BoardWriteReq.NewAttachedFile attachedFileForRequest : boardWriteReq.getNewAttachedFileList()) {				
				int boardFileListInsertCount = create.insertInto(SB_BOARD_FILELIST_TB)
				.set(SB_BOARD_FILELIST_TB.BOARD_ID, boardID)
				.set(SB_BOARD_FILELIST_TB.BOARD_NO, boardNo)
				.set(SB_BOARD_FILELIST_TB.ATTACHED_FILE_SQ, UByte.valueOf(attachedFileListIndex))
				.set(SB_BOARD_FILELIST_TB.ATTACHED_FNAME, attachedFileForRequest.getAttachedFileName())
				.execute();		
				
				if (0 == boardFileListInsertCount) {
					try {
						conn.rollback();
					} catch (Exception e) {
						log.warn("fail to rollback");
					}
					String errorMessage = "게시판 첨부 파일을  저장하는데 실패하였습니다";
					log.warn("게시판 첨부 파일 목록내 인덱스[{}]의 첨부 파일 이름을 저장하는데 실패하였습니다", 
							attachedFileListIndex);
					
					throw new ServerServiceException(errorMessage);
				}
				
				attachedFileListIndex++;
			}
			
			conn.commit();

			// log.info("게시판 최상의 글[boardID={}, boardNo={}, subject={}] 등록 성공", boardID, boardNo, boardWriteReq.getSubject());
			
			BoardWriteRes boardWriteRes = new BoardWriteRes();
			boardWriteRes.setBoardID(boardID.shortValue());
			boardWriteRes.setBoardNo(boardNo.longValue());			
			
			return boardWriteRes;
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
				} catch(Exception e) {
					log.warn("fail to close the db connection", e);
				}
			}
		}
	}
}
