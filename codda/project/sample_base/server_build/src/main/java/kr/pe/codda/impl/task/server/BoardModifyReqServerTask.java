package kr.pe.codda.impl.task.server;

import static kr.pe.codda.impl.jooq.tables.SbBoardFilelistTb.SB_BOARD_FILELIST_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardHistoryTb.SB_BOARD_HISTORY_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardTb.SB_BOARD_TB;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;

import javax.sql.DataSource;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.BoardDetailRes.BoardDetailRes;
import kr.pe.codda.impl.message.BoardModifyReq.BoardModifyReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.BoardStateType;
import kr.pe.codda.server.lib.BoardType;
import kr.pe.codda.server.lib.JooqSqlUtil;
import kr.pe.codda.server.lib.MemberType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;

public class BoardModifyReqServerTask extends AbstractServerTask {	
	private void sendErrorOutputMessage(String errorMessage,			
			ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws InterruptedException {
		log.warn("{}, inObj=", errorMessage, inputMessage.toString());
		
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
			AbstractMessage outputMessage = doWork(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, (BoardModifyReq)inputMessage);
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
						
			sendErrorOutputMessage("게시글 수정하는데 실패하였습니다", toLetterCarrier, inputMessage);
			return;
		}
	}
	
	public MessageResultRes doWork(String dbcpName, BoardModifyReq boardModifyReq) throws Exception {
		// FIXME!
		log.info(boardModifyReq.toString());
		
		try {
			BoardType.valueOf(boardModifyReq.getBoardID());
		} catch(IllegalArgumentException e) {			
			String errorMessage = "잘못된 게시판 식별자입니다";
			throw new ServerServiceException(errorMessage);
		}
		
		try {
			ValueChecker.checkValidSubject(boardModifyReq.getSubject());
		} catch(RuntimeException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}
		
		try {
			ValueChecker.checkValidContent(boardModifyReq.getContent());
		} catch(RuntimeException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}
		
		try {
			ValueChecker.checkValidWriterID(boardModifyReq.getRequestUserID());
		} catch(RuntimeException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}
		
		
		try {
			ValueChecker.checkValidIP(boardModifyReq.getIp());
		} catch(RuntimeException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}
				
		if ((boardModifyReq.getOldAttachedFileSeqCnt() + boardModifyReq.getNewAttachedFileCnt()) > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
			String errorMessage = new StringBuilder().append("첨부 파일 등록 갯수[기존갯수=")
					.append(boardModifyReq.getOldAttachedFileSeqCnt())
					.append(",신규추가갯수=")
					.append(boardModifyReq.getNewAttachedFileCnt())
					.append("]가 최대 첨부 파일 등록 갯수[")						
					.append(CommonStaticFinalVars.UNSIGNED_BYTE_MAX)
					.append("]를 초과하였습니다").toString();
			throw new ServerServiceException(errorMessage);
		}
		
		if (boardModifyReq.getNewAttachedFileCnt() > 0) {			
			int newAttachedFileCnt = boardModifyReq.getNewAttachedFileCnt();
			List<BoardModifyReq.NewAttachedFile> newAttachedFileList = boardModifyReq.getNewAttachedFileList();

			for (int i=0; i < newAttachedFileCnt; i++) {
				BoardModifyReq.NewAttachedFile attachedFileForRequest = newAttachedFileList.get(i);
				try {
					ValueChecker.checkValidFileName(attachedFileForRequest.getAttachedFileName());
				} catch (IllegalArgumentException e) {
					String errorMessage = new StringBuilder()
							.append(i)
							.append("번째 파일 이름 유효성 검사 에러 메시지::")
							.append(e.getMessage()).toString();
					throw new ServerServiceException(errorMessage);
				}			
			}
		}
		
		UByte boardID = UByte.valueOf(boardModifyReq.getBoardID());
		UInteger boardNo = UInteger.valueOf(boardModifyReq.getBoardNo());
		
		HashSet<Short> remainingOldAttachedFileSequeceSet = new HashSet<Short>();
		for (BoardModifyReq.OldAttachedFileSeq oldAttachedFile: boardModifyReq.getOldAttachedFileSeqList()) {
			remainingOldAttachedFileSequeceSet.add(oldAttachedFile.getAttachedFileSeq());
		}
		
		if (remainingOldAttachedFileSequeceSet.size() != boardModifyReq.getOldAttachedFileSeqCnt()) {
			String errorMessage = "증복된 첨부 파일 시퀀스를 갖는 잘못된 구 첨부 파일 목록입니다";
			throw new ServerServiceException(errorMessage);
		}
		
		DataSource dataSource = DBCPManager.getInstance()
				.getBasicDataSource(dbcpName);
		
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			
			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(dbcpName));			
			
			String nativeRequestUserIDMemberType = ValueChecker.checkValidMemberStateForUserID(conn, create, log, boardModifyReq.getRequestUserID());
			MemberType  modifierIDMemberType = null;
			try {
				modifierIDMemberType = MemberType.valueOf(nativeRequestUserIDMemberType, false);
			} catch(IllegalArgumentException e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("해당 게시글 수정 요청자[").append(boardModifyReq.getRequestUserID())
						.append("]의 멤버 타입[")
						.append(nativeRequestUserIDMemberType)
						.append("]이 잘못되어있습니다").toString();
				throw new ServerServiceException(errorMessage);
			}
			
			if (! MemberType.ADMIN.equals(modifierIDMemberType)) {
				Record1<String> 
				firstWriterBoardRecord = create.select(SB_BOARD_HISTORY_TB.MODIFIER_ID)
				.from(SB_BOARD_HISTORY_TB)
				.where(SB_BOARD_HISTORY_TB.BOARD_ID.eq(boardID))
				.and(SB_BOARD_HISTORY_TB.BOARD_NO.eq(boardNo))
				.and(SB_BOARD_HISTORY_TB.HISTORY_SQ.eq(UByte.valueOf(0)))
				.fetchOne();
				
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
				
				if (! boardModifyReq.getRequestUserID().equals(firstWriterID)) {
					try {
						conn.rollback();
					} catch (Exception e) {
						log.warn("fail to rollback");
					}
					
					String errorMessage = new StringBuilder("타인[")
							.append(firstWriterID)							
							.append("] 게시글은 수정 할 수 없습니다").toString();
					throw new ServerServiceException(errorMessage);
				}
			}
			
			Record1<String> boardRecord = create.select(SB_BOARD_TB.BOARD_ST)
			.from(SB_BOARD_TB)			
			.where(SB_BOARD_TB.BOARD_ID.eq(boardID))
			.and(SB_BOARD_TB.BOARD_NO.eq(boardNo))
			.forUpdate().fetchOne();
			
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
			} catch(IllegalArgumentException e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("게시글의 상태 값[")
						.append(nativeBoardStateType)
						.append("]이 잘못되었습니다").toString();
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
			
			Result<Record> attachFileListRecord = create.select().from(SB_BOARD_FILELIST_TB)
					.where(SB_BOARD_FILELIST_TB.BOARD_ID.eq(boardID))
					.and(SB_BOARD_FILELIST_TB.BOARD_NO.eq(boardNo))
					.fetch();			
			
			short attachedFileMaxSeq = -1;
			
			if (null == attachFileListRecord) {
				/**
				 * 첨부 파일이 실제 DB 에 미 존재하는데 1개 이상의 원소를 갖는 구 첨부 파일 목록을 파라미터 받았을 경우 에러 처리
				 */
				if (! remainingOldAttachedFileSequeceSet.isEmpty()) {
					try {
						conn.rollback();
					} catch (Exception e) {
						log.warn("fail to rollback");
					}
					
					String errorMessage = new StringBuilder("구 첨부 파일들이 존재 하지 않는데 보존을 원하는 구 첨부 파일 목록을 요청하셨습니다").toString();
					throw new ServerServiceException(errorMessage);
				}
			} else {
				HashSet<Short> actualOldAttachedFileSequeceSet = new HashSet<Short>();
				
				for (Record attachFileRecord : attachFileListRecord) {
					BoardDetailRes.AttachedFile attachedFile = new BoardDetailRes.AttachedFile();
					attachedFile.setAttachedFileSeq(attachFileRecord.get(SB_BOARD_FILELIST_TB.ATTACHED_FILE_SQ).shortValue());
					attachedFile.setAttachedFileName(attachFileRecord.get(SB_BOARD_FILELIST_TB.ATTACHED_FNAME));
					short attachedFileSeq = attachFileRecord.get(SB_BOARD_FILELIST_TB.ATTACHED_FILE_SQ).shortValue();
					
					if (attachedFileMaxSeq < attachedFileSeq) {
						attachedFileMaxSeq = attachedFileSeq;
					}
					actualOldAttachedFileSequeceSet.add(attachedFileSeq);
				}
				
				if (! actualOldAttachedFileSequeceSet.containsAll(remainingOldAttachedFileSequeceSet)) {
					try {
						conn.rollback();
					} catch (Exception e) {
						log.warn("fail to rollback");
					}
					
					String errorMessage = new StringBuilder("보존을 원하는 구 첨부 파일 목록중 실제 구 첨부 파일 목록에 존재하지 않는 첨부 파일이 존재합니다, 실제 구 첨부 파일 목록=")
							.append(actualOldAttachedFileSequeceSet.toString()).toString();
					throw new ServerServiceException(errorMessage);
				}
				
				actualOldAttachedFileSequeceSet.removeAll(remainingOldAttachedFileSequeceSet);
				
				if (actualOldAttachedFileSequeceSet.size() > 0) {
					create.delete(SB_BOARD_FILELIST_TB)
					.where(SB_BOARD_HISTORY_TB.BOARD_ID.eq(boardID))
					.and(SB_BOARD_HISTORY_TB.BOARD_NO.eq(boardNo))
					.and(SB_BOARD_HISTORY_TB.HISTORY_SQ.in(actualOldAttachedFileSequeceSet)).execute();
				}
			}					
						
			Record1<UByte> boardHistoryMaxSequenceRecord = create.select(SB_BOARD_HISTORY_TB.HISTORY_SQ.max().add(1))
					.from(SB_BOARD_HISTORY_TB)
					.where(SB_BOARD_HISTORY_TB.BOARD_ID.eq(boardID))
					.and(SB_BOARD_HISTORY_TB.BOARD_NO.eq(boardNo)).fetchOne();
			
			if (null == boardHistoryMaxSequenceRecord) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder().append("해당 게시판의 글[boardID=")
						.append(boardID)
						.append(", boardNo=")
						.append(boardNo)
						.append("]은  제목과 내용이 저장된 테이블(SB_BOARD_HISTORY_TB)에 미 존재합니다").toString();				
				throw new ServerServiceException(errorMessage);
			}
			
			UByte boardHistorySequence = boardHistoryMaxSequenceRecord.value1();
			
			int boardHistoryInsertCount = create.insertInto(SB_BOARD_HISTORY_TB)
					.set(SB_BOARD_HISTORY_TB.BOARD_ID, boardID)
					.set(SB_BOARD_HISTORY_TB.BOARD_NO, boardNo)
					.set(SB_BOARD_HISTORY_TB.HISTORY_SQ, boardHistorySequence)
					.set(SB_BOARD_HISTORY_TB.SUBJECT, boardModifyReq.getSubject())
					.set(SB_BOARD_HISTORY_TB.CONTENT, boardModifyReq.getContent())
					.set(SB_BOARD_HISTORY_TB.MODIFIER_ID, boardModifyReq.getRequestUserID())
					.set(SB_BOARD_HISTORY_TB.IP, boardModifyReq.getIp())
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
			
			if (boardModifyReq.getNewAttachedFileCnt() > 0) {
				int newAttachedFileListIndex = 0;
				for (BoardModifyReq.NewAttachedFile newAttachedFileForRequest : boardModifyReq.getNewAttachedFileList()) {
					
					if (attachedFileMaxSeq == CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
						try {
							conn.rollback();
						} catch (Exception e) {
							log.warn("fail to rollback");
						}
						
						String errorMessage = "게시글당 첨부 파일 최대 등록 횟수(=256)를 초가하였습니다";
						throw new ServerServiceException(errorMessage);
					}
					
					int boardFileListInsertCount = create.insertInto(SB_BOARD_FILELIST_TB)
					.set(SB_BOARD_FILELIST_TB.BOARD_ID, boardID)
					.set(SB_BOARD_FILELIST_TB.BOARD_NO, boardNo)
					.set(SB_BOARD_FILELIST_TB.ATTACHED_FILE_SQ, UByte.valueOf(attachedFileMaxSeq+1))
					.set(SB_BOARD_FILELIST_TB.ATTACHED_FNAME, newAttachedFileForRequest.getAttachedFileName())
					.execute();		
					
					attachedFileMaxSeq++;
					
					if (0 == boardFileListInsertCount) {
						try {
							conn.rollback();
						} catch (Exception e) {
							log.warn("fail to rollback");
						}
						String errorMessage = "게시판 첨부 파일을  저장하는데 실패하였습니다";
						log.warn("게시판 첨부 파일 목록내 인덱스[{}]의 첨부 파일 이름을 저장하는데 실패하였습니다", 
								newAttachedFileListIndex);
						
						throw new ServerServiceException(errorMessage);
					}
					
					newAttachedFileListIndex++;
				}
			}										
			
			conn.commit();			
			
			MessageResultRes messageResultRes = new MessageResultRes();
			messageResultRes.setTaskMessageID(boardModifyReq.getMessageID());
			messageResultRes.setIsSuccess(true);		
			messageResultRes.setResultMessage("게시글 수정이 성공하였습니다");			
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
