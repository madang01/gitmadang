package kr.pe.codda.impl.task.server;

import static kr.pe.codda.impl.jooq.tables.SbBoardFileinfoTb.SB_BOARD_FILEINFO_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardFilelistTb.SB_BOARD_FILELIST_TB;

import java.sql.Connection;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.Record3;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;

import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.BoardDownloadFileReq.BoardDownloadFileReq;
import kr.pe.codda.impl.message.BoardDownloadFileRes.BoardDownloadFileRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class BoardDownloadFileReqServerTask extends AbstractServerTask {
	/*private void sendErrorOutputMessageForCommit(String errorMessage,
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
		if (null != conn) {
			try {
				conn.rollback();
			} catch (Exception e) {
				log.warn("fail to rollback");
			}
		}		
		sendErrorOutputMessage(errorMessage, toLetterCarrier, inputMessage);
	}*/
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
	
	/*private void sendSuccessOutputMessageForCommit(AbstractMessage outputMessage, Connection conn,
			ToLetterCarrier toLetterCarrier) throws InterruptedException {		
		try {
			conn.commit();
		} catch (Exception e) {
			log.warn("fail to commit");
		}
		
		toLetterCarrier.addSyncOutputMessage(outputMessage);
	}*/
	

	@Override
	public void doTask(String projectName, 
			PersonalLoginManagerIF personalLoginManager, 
			ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {
		try {
			AbstractMessage outputMessage = doService((BoardDownloadFileReq)inputMessage);
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
						
			sendErrorOutputMessage("다운로드가 실패하였습니다", toLetterCarrier, inputMessage);
			return;
		}
	}
	
	public BoardDownloadFileRes doService(BoardDownloadFileReq boardDownloadFileReq)
			throws Exception {
		// FIXME!
		log.info(boardDownloadFileReq.toString());
		
		DataSource dataSource = DBCPManager.getInstance()
				.getBasicDataSource(ServerCommonStaticFinalVars.SB_CONNECTION_POOL_NAME);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			
			DSLContext create = DSL.using(conn, SQLDialect.MYSQL);
			
			Record3<String, String, String> attachedFileListRecord = create.select(SB_BOARD_FILEINFO_TB.OWNER_ID
					, SB_BOARD_FILELIST_TB.ATTACH_FNAME
					, SB_BOARD_FILELIST_TB.SYS_FNAME).from(SB_BOARD_FILELIST_TB)
			.join(SB_BOARD_FILEINFO_TB).on(SB_BOARD_FILEINFO_TB.ATTACH_ID.eq(SB_BOARD_FILELIST_TB.ATTACH_ID))
			.where(SB_BOARD_FILELIST_TB.ATTACH_ID.eq(UInteger.valueOf(boardDownloadFileReq.getAttachId())))
			.and(SB_BOARD_FILELIST_TB.ATTACH_SQ.eq(UInteger.valueOf(boardDownloadFileReq.getAttachSeq())))
			.fetchOne();
			
			if (null == attachedFileListRecord) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = "다운 로드 파일 정보가 존재하지 않습니다";
				/*sendErrorOutputMessageForCommit(errorMessage, conn, toLetterCarrier, boardDownloadFileReq);
				return;*/
				throw new ServerServiceException(errorMessage);
			}
			
			conn.commit();
			
			BoardDownloadFileRes boardDownloadFileRes = new BoardDownloadFileRes();
			boardDownloadFileRes.setOwnerId(attachedFileListRecord.getValue(SB_BOARD_FILEINFO_TB.OWNER_ID));
			boardDownloadFileRes.setAttachId(boardDownloadFileReq.getAttachId());
			boardDownloadFileRes.setAttachSeq(boardDownloadFileReq.getAttachSeq());
			boardDownloadFileRes.setAttachFiledName(attachedFileListRecord.getValue(SB_BOARD_FILELIST_TB.ATTACH_FNAME));
			boardDownloadFileRes.setSystemFileName(attachedFileListRecord.getValue(SB_BOARD_FILELIST_TB.SYS_FNAME));
			
			// sendSuccessOutputMessageForCommit(boardDownloadFileRes, conn, toLetterCarrier);
			return boardDownloadFileRes;
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
			
			/*String errorMessage = new StringBuilder("unknown error, inObj=")
					.append(boardDownloadFileReq.toString()).toString();
			log.warn(errorMessage, e);	*/				
			
			/*sendErrorOutputMessageForRollback("다운 로드 파일 정보를 얻는데 실패하였습니다", conn, toLetterCarrier, boardDownloadFileReq);
			return;*/
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
