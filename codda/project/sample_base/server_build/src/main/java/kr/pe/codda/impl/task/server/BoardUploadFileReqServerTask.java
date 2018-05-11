package kr.pe.codda.impl.task.server;


import static kr.pe.codda.impl.jooq.tables.SbBoardFileinfoTb.SB_BOARD_FILEINFO_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardFilelistTb.SB_BOARD_FILELIST_TB;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;

import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.BoardUploadFileReq.BoardUploadFileReq;
import kr.pe.codda.impl.message.BoardUploadFileRes.BoardUploadFileRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.JooqSqlUtil;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class BoardUploadFileReqServerTask extends AbstractServerTask {
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
	public void doTask(String projectName, 
			PersonalLoginManagerIF personalLoginManager, 
			ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {
		doWork(projectName, personalLoginManager, toLetterCarrier, (BoardUploadFileReq)inputMessage);
	}
	
	public void doWork(String projectName, 
			PersonalLoginManagerIF personalLoginManager, 
			ToLetterCarrier toLetterCarrier,
			BoardUploadFileReq boardUploadFileReq) throws Exception {
		// FIXME!
		log.info(boardUploadFileReq.toString());
		
		try {
			ValueChecker.checkValidUserId(boardUploadFileReq.getUserId());
		} catch(IllegalArgumentException e) {
			sendErrorOutputMessage(e.getMessage(), toLetterCarrier, boardUploadFileReq);
			return;
		}		
		
		try {
			ValueChecker.checkValidIP(boardUploadFileReq.getIp());
		} catch(IllegalArgumentException e) {
			sendErrorOutputMessage(e.getMessage(), toLetterCarrier, boardUploadFileReq);
			return;
		}
		
		
		int selectedOldAttachedFileCnt = boardUploadFileReq.getOldAttachedFileCnt();
		int newAttachedFileCnt = boardUploadFileReq.getNewAttachedFileCnt();
		
		if (0 > selectedOldAttachedFileCnt) {
			String errorMessage = new StringBuilder("업로드했던 파일들에 대한 사용자 선택 갯수[")
			.append(selectedOldAttachedFileCnt).append("]가 0보다 작습니다.").toString();			
			sendErrorOutputMessage(errorMessage, toLetterCarrier, boardUploadFileReq);
			return;
		}
		
		if (0 == boardUploadFileReq.getAttachId()) {
			/** 신규 */
			if (0 != selectedOldAttachedFileCnt) {
				String errorMessage = new StringBuilder("업로드했던 파일들에 대한 사용자 선택 갯수[")
				.append(selectedOldAttachedFileCnt).append("]가 0보다 작습니다.").toString();			
				sendErrorOutputMessage(errorMessage, toLetterCarrier, boardUploadFileReq);
				return;
			}
			
			if (0 >= newAttachedFileCnt) {
				String errorMessage = new StringBuilder("신규 업로드 파일 갯수[")
				.append(newAttachedFileCnt).append("]는 0 보다 커야 합니다").toString();			
				sendErrorOutputMessage(errorMessage, toLetterCarrier, boardUploadFileReq);
				return;
			}
		} else {
			/** 수정 */
			if (0 == selectedOldAttachedFileCnt && 0 >= newAttachedFileCnt) {				
				String errorMessage = new StringBuilder("업로드 했던 파일들 모두 선택 취소후에는 신규 파일 갯수[")
				.append(newAttachedFileCnt).append("]는 0 보다 커야 합니다").toString();	
				sendErrorOutputMessage(errorMessage, toLetterCarrier, boardUploadFileReq);
				return;
			}
		}
		
		Set<Long> oldAttachSeqSet = new HashSet<Long>();
		for (BoardUploadFileReq.OldAttachedFile selectedOldAttachedFile : boardUploadFileReq.getOldAttachedFileList()) {
			oldAttachSeqSet.add(selectedOldAttachedFile.getAttachSeq());
		}
		
		if (oldAttachSeqSet.size() != boardUploadFileReq.getOldAttachedFileList().size()) {
			String errorMessage = "업로드 했던 파일들중 선택한 목록에서 중복된 원소가 있습니다";	
			sendErrorOutputMessage(errorMessage, toLetterCarrier, boardUploadFileReq);
			return;
		}		
		
		for (BoardUploadFileReq.NewAttachedFile newAttachedFile : boardUploadFileReq.getNewAttachedFileList()) {
			
			try {
				ValueChecker.checkNoTrimString(newAttachedFile.getAttachedFileName());
			} catch(IllegalArgumentException e) {
				String errorMessage = new StringBuilder("유효하지 않은 첨부 파일명입니다. errmsg=")
						.append(e.getMessage()).toString();
				sendErrorOutputMessage(errorMessage, toLetterCarrier, boardUploadFileReq);
				return;
			}
			
			try {
				ValueChecker.checkNoTrimString(newAttachedFile.getSystemFileName());
			} catch(IllegalArgumentException e) {
				String errorMessage = new StringBuilder("유효하지 않은 시스템 파일명입니다. errmsg=")
						.append(e.getMessage()).toString();
				sendErrorOutputMessage(errorMessage, toLetterCarrier, boardUploadFileReq);
				return;
			}			
		}
		
		DataSource dataSource = DBCPManager.getInstance()
				.getBasicDataSource(ServerCommonStaticFinalVars.SB_CONNECTION_POOL_NAME);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL);
			
			if (0 == boardUploadFileReq.getAttachId()) {
				/** 신규 */
				/**
				insert into `SINNORIDB`.`SB_BOARD_FILEINFO_TB`  (`attach_id`, `owner_id`, `ip`, `reg_dt`, `mod_dt`) VALUES  (0, #{userId}, #{ip}, sysdate(), reg_dt)
				select last_insert_id() as attachId
				insert into `SINNORIDB`.`SB_BOARD_FILELIST_TB` (`attach_id`,`attach_sq`,`attach_fname`,`sys_fname`) VALUES  (#{attachId}, #{attachSeq}, #{attachFileName}, #{systemFileName})
				*/
				
				int countOfFileInfoInsert = create.insertInto(SB_BOARD_FILEINFO_TB)
				.set(SB_BOARD_FILEINFO_TB.ATTACH_ID, UInteger.valueOf(0))
				.set(SB_BOARD_FILEINFO_TB.OWNER_ID, boardUploadFileReq.getUserId())
				.set(SB_BOARD_FILEINFO_TB.IP, boardUploadFileReq.getIp())
				.set(SB_BOARD_FILEINFO_TB.REG_DT, JooqSqlUtil.getFieldOfSysDate(Timestamp.class))
				.set(SB_BOARD_FILEINFO_TB.MOD_DT, SB_BOARD_FILEINFO_TB.REG_DT)
				.execute();
				
				if (0 == countOfFileInfoInsert) {
					String errorMessage = "업로드 파일 정보를 생성하는데 실패 하였습니다";
					sendErrorOutputMessageForRollback(errorMessage, conn, toLetterCarrier, boardUploadFileReq);
					return;
				}				
				 
				UInteger newAttachID = create.select(JooqSqlUtil.getFieldOfLastInsertID(UInteger.class)
								.as(SB_BOARD_FILEINFO_TB.ATTACH_ID.getName())).fetchOne(0, UInteger.class);
				
				BoardUploadFileRes boardUploadFileRes = new BoardUploadFileRes();
				boardUploadFileRes.setAttachId(newAttachID.longValue());
				boardUploadFileRes.setOwnerId(boardUploadFileReq.getUserId());
				boardUploadFileRes.setIp(boardUploadFileReq.getIp());
				boardUploadFileRes.setAttachedFileCnt(boardUploadFileReq.getNewAttachedFileList().size());
				List<BoardUploadFileRes.AttachedFile> attachedFileList = new ArrayList<BoardUploadFileRes.AttachedFile>();
				boardUploadFileRes.setAttachedFileList(attachedFileList);
				
				long newAttachSeq = 0;
				for (BoardUploadFileReq.NewAttachedFile newAttachedFile : boardUploadFileReq.getNewAttachedFileList()) {

					int countOfFileListInsert = create.insertInto(SB_BOARD_FILELIST_TB)
					.set(SB_BOARD_FILELIST_TB.ATTACH_ID, newAttachID)
					.set(SB_BOARD_FILELIST_TB.ATTACH_SQ, UInteger.valueOf(newAttachSeq))
					.set(SB_BOARD_FILELIST_TB.ATTACH_FNAME, newAttachedFile.getAttachedFileName())
					.set(SB_BOARD_FILELIST_TB.SYS_FNAME, newAttachedFile.getSystemFileName())
					.execute();
					
					if (0 == countOfFileListInsert) {
						String errorMessage = new StringBuilder("신규로 첨부된 파일[")
								.append(newAttachedFile.toString())
								.append("]을 파일 목록[")
								.append(newAttachSeq)
								.append("]에 추가하는데 실패하였습니다").toString();
						sendErrorOutputMessageForRollback(errorMessage, conn, toLetterCarrier, boardUploadFileReq);
						return;
					}
					
					BoardUploadFileRes.AttachedFile attachedFile = new BoardUploadFileRes.AttachedFile();					
					attachedFile.setAttachSeq(newAttachSeq);
					attachedFile.setAttachedFileName(newAttachedFile.getAttachedFileName());
					attachedFile.setSystemFileName(newAttachedFile.getSystemFileName());
					
					newAttachSeq++;
				}								
				
				sendSuccessOutputMessageForCommit(boardUploadFileRes, conn, toLetterCarrier);
				return;
			} else {
				/** 수정 */
				
				/**
				select * from SB_BOARD_FILEINFO_TB where attach_id = #{attachId} for update 
				update SB_BOARD_FILEINFO_TB set ip=#{ip}, mod_dt=sysdate() where attach_id = #{attachId}				
				delete from SB_BOARD_FILELIST_TB where attach_id = 1;				
				insert into `SINNORIDB`.`SB_BOARD_FILELIST_TB` (`attach_id`,`attach_sq`,`attach_fname`,`sys_fname`) VALUES  (#{attachId}, #{newAttachSeq}, #{attachFileName}, #{systemFileName})
				 */
				
				UInteger targetAttachId = UInteger.valueOf(boardUploadFileReq.getAttachId());
				
				Record1<String> 
				fileInfoRecord = create.select(SB_BOARD_FILEINFO_TB.OWNER_ID)
				.from(SB_BOARD_FILEINFO_TB)
				.where(SB_BOARD_FILEINFO_TB.ATTACH_ID
						.eq(targetAttachId))
				.forUpdate().fetchOne();
				
				if (null == fileInfoRecord) {
					String errorMessage = new StringBuilder("파일 식별자[")
							.append(boardUploadFileReq.getAttachId())
							.append("]와 일치하는 업로드 파일 정보가 없습니다")
							.toString();
					sendErrorOutputMessageForRollback(errorMessage, conn, toLetterCarrier, boardUploadFileReq);
					return;
				}
				
				String ownerID = fileInfoRecord.get(SB_BOARD_FILEINFO_TB.OWNER_ID);
				
				if (! ownerID.equals(boardUploadFileReq.getUserId())) {
					String errorMessage = new StringBuilder("업로드 파일 수정은 소유자만 가능합니다, errmsg=파일 식별자[")
							.append(boardUploadFileReq.getAttachId())
							.append("]의 소유자[")
							.append(ownerID)
							.append("]와 업로드 파일 수정 요청자[")
							.append(boardUploadFileReq.getUserId())
							.append("]가 다릅니다")
							.toString();
					sendErrorOutputMessageForCommit(errorMessage, conn, toLetterCarrier, boardUploadFileReq);
					return;
				}
				
				
				int countOfFileInfoUpdate = create.update(SB_BOARD_FILEINFO_TB)
				.set(SB_BOARD_FILEINFO_TB.IP, boardUploadFileReq.getIp())
				.set(SB_BOARD_FILEINFO_TB.MOD_DT, JooqSqlUtil.getFieldOfSysDate(Timestamp.class))
				.where(SB_BOARD_FILEINFO_TB.ATTACH_ID
						.eq(targetAttachId))
				.execute();
				
				if (0 == countOfFileInfoUpdate) {
					String errorMessage = new StringBuilder("업로드 파일 정보[")
							.append(boardUploadFileReq.getAttachId())
							.append("]를 갱신하는데 실패하였습니다")
							.toString();
					sendErrorOutputMessageForRollback(errorMessage, conn, toLetterCarrier, boardUploadFileReq);
					return;
				}				
				
				create.delete(SB_BOARD_FILELIST_TB)
				.where(SB_BOARD_FILELIST_TB.ATTACH_ID.eq(targetAttachId))
				.and(SB_BOARD_FILELIST_TB.ATTACH_ID.notIn(oldAttachSeqSet))
				.execute();
				
				for (BoardUploadFileReq.NewAttachedFile newAttachedFile : boardUploadFileReq.getNewAttachedFileList()) {
					int countOfFileListInsert = create.insertInto(SB_BOARD_FILELIST_TB)
							.set(SB_BOARD_FILELIST_TB.ATTACH_ID, targetAttachId)
							.set(SB_BOARD_FILELIST_TB.ATTACH_SQ, UInteger.valueOf(0))
							.set(SB_BOARD_FILELIST_TB.ATTACH_FNAME, newAttachedFile.getAttachedFileName())
							.set(SB_BOARD_FILELIST_TB.SYS_FNAME, newAttachedFile.getSystemFileName())
							.execute();
							
							if (0 == countOfFileListInsert) {
								String errorMessage = new StringBuilder("업로드 파일 목록에 신규 업로드 파일 레코드[")
										.append(newAttachedFile.toString())
										.append("]를 추가하는데 실패하였습니다")
										.toString();
								sendErrorOutputMessageForRollback(errorMessage, conn, toLetterCarrier, boardUploadFileReq);
								return;
							}
				}
			}
		} catch (Exception e) {
			log.warn("unknown error", e);

			sendErrorOutputMessageForRollback("2.게시글에 대한 추천이 실패하였습니다", conn, toLetterCarrier, boardUploadFileReq);
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
		
		
		/*SqlSessionFactory sqlSessionFactory = MybatisSqlSessionFactoryManger.getInstance()
				.getSqlSessionFactory(ServerCommonStaticFinalVars.SB_CONNECTION_POOL_NAME);
		if (0 == attachId) {
			*//** 업로드 신규 등록 *//*
			if (0 != selectedOldAttachFileCnt) {
				String errorMessage = "업로드 신규 등록:업로드했던 파일들에 대한 사용자 선택 목록이 존재합니다.";
				log.warn("{}, inObj=", errorMessage, inObj.toString());
				
				MessageResultRes messageResultOutObj = new MessageResultRes();
				messageResultOutObj.setIsSuccess(false);
				messageResultOutObj.setTaskMessageID(inObj.getMessageID());
				messageResultOutObj.setResultMessage(errorMessage);
				toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
				return;
			}
			
			if (0 == newAttachFileCnt) {
				String errorMessage = "업로드 신규 등록:업로드 파일이 존재하지 않습니다.";
				log.warn("{}, inObj=", errorMessage, inObj.toString());
				
				MessageResultRes messageResultOutObj = new MessageResultRes();
				messageResultOutObj.setIsSuccess(false);
				messageResultOutObj.setTaskMessageID(inObj.getMessageID());
				messageResultOutObj.setResultMessage(errorMessage);
				toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
				return;
			}
			
			if (newAttachFileCnt > ServerCommonStaticFinalVars.WEBSITE_FILEUPLOAD_MAX_COUNT) {
				String errorMessage = new StringBuilder("업로드 신규 등록:신규 업로드 파일 갯수[")
				.append(newAttachFileCnt)
				.append("]가 최대 업로드 파일 갯수[")
				.append(ServerCommonStaticFinalVars.WEBSITE_FILEUPLOAD_MAX_COUNT)
				.append("]를 초과 하였습니다.").toString();
				log.warn("{}, inObj={}", errorMessage, inObj.toString());
				
				MessageResultRes messageResultOutObj = new MessageResultRes();
				messageResultOutObj.setIsSuccess(false);
				messageResultOutObj.setTaskMessageID(inObj.getMessageID());
				messageResultOutObj.setResultMessage(errorMessage);
				toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
				return;
			}
			
			
			java.util.List<NewAttachFile> newAttachFileList = inObj.getNewAttachFileList();
			for (NewAttachFile newAttachFile : newAttachFileList) {
				String attachFileName = newAttachFile.getAttachFileName();
				
				if (attachFileName.equals("")) {
					String errorMessage = "업로드 신규 등록:업로드 파일명을 넣어주세요.";
					log.warn("{}, inObj=", errorMessage, inObj.toString());
					
					MessageResultRes messageResultOutObj = new MessageResultRes();
					messageResultOutObj.setIsSuccess(false);
					messageResultOutObj.setTaskMessageID(inObj.getMessageID());
					messageResultOutObj.setResultMessage(errorMessage);
					toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
					return;
				}
				
				String trimAttachFileName = attachFileName.trim();
				if (!trimAttachFileName.equals(attachFileName)) {
					String errorMessage = "업로드 신규 등록:업로드 파일명을 다시 넣어주세요.";
					log.warn("{}, inObj=", errorMessage, inObj.toString());
					
					MessageResultRes messageResultOutObj = new MessageResultRes();
					messageResultOutObj.setIsSuccess(false);
					messageResultOutObj.setTaskMessageID(inObj.getMessageID());
					messageResultOutObj.setResultMessage(errorMessage);
					toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
					return;
				}
				
				String systemFileName = newAttachFile.getSystemFileName();		
				
				if (systemFileName.equals("")) {
					String errorMessage = "업로드 신규 등록:업로드 시스템 절대 경로 파일명을 넣어주세요.";
					log.warn("{}, inObj=", errorMessage, inObj.toString());
					
					MessageResultRes messageResultOutObj = new MessageResultRes();
					messageResultOutObj.setIsSuccess(false);
					messageResultOutObj.setTaskMessageID(inObj.getMessageID());
					messageResultOutObj.setResultMessage(errorMessage);
					toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
					return;
				}
				
				String trimSystemFileName = systemFileName.trim();
				if (!trimSystemFileName.equals(systemFileName)) {
					String errorMessage = "업로드 신규 등록:업로드 시스템 절대 경로 파일명을 다시 넣어주세요.";
					log.warn("{}, inObj=", errorMessage, inObj.toString());
					
					MessageResultRes messageResultOutObj = new MessageResultRes();
					messageResultOutObj.setIsSuccess(false);
					messageResultOutObj.setTaskMessageID(inObj.getMessageID());
					messageResultOutObj.setResultMessage(errorMessage);
					toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
					return;
				}				
			}
			
			BoardUploadFileRes outObj = null;
			
			
			
			SqlSession session = sqlSessionFactory.openSession(false);
			try {			
				int cntOfInsertBoardFileInfo= session.insert("insertBoardFileInfo", inObj);
				
				if (0 == cntOfInsertBoardFileInfo) {
					session.rollback();
					
					String errorMessage = "업로드 신규 등록:게시판 파일 정보 등록 처리가 실패하였습니다.";
					log.warn("{}, inObj={}", errorMessage, inObj.toString());
					
					MessageResultRes messageResultOutObj = new MessageResultRes();
					messageResultOutObj.setIsSuccess(false);
					messageResultOutObj.setTaskMessageID(inObj.getMessageID());
					messageResultOutObj.setResultMessage(errorMessage);
					toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
					return;
				}
				
				Long retAttachId = session.selectOne("getAttachId");
				
				if (null == retAttachId) {				
					session.rollback();
					
					String errorMessage = "업로드 신규 등록:업로드 식별자 조회가 실패하였습니다.";
					log.warn("{}, inObj={}", errorMessage, inObj.toString());
					
					MessageResultRes messageResultOutObj = new MessageResultRes();
					messageResultOutObj.setIsSuccess(false);
					messageResultOutObj.setTaskMessageID(inObj.getMessageID());
					messageResultOutObj.setResultMessage(errorMessage);
					
					toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
					return;
				} else {					
					// java.util.List<BoardFileDTO>  attachFileDTOList = new java.util.ArrayList<BoardFileDTO>();
					
					short attachSeq = 0;
					for (NewAttachFile newAttachFile : newAttachFileList) {						
						BoardFileDTO newBoardFileDTO = new BoardFileDTO();
						newBoardFileDTO.setAttachId(retAttachId);
						newBoardFileDTO.setAttachSeq(attachSeq++);
						newBoardFileDTO.setAttachFileName(newAttachFile.getAttachFileName());
						newBoardFileDTO.setSystemFileName(newAttachFile.getSystemFileName());
						
						int cntOfInsertBoardFile = session.insert("insertBoardFile", newBoardFileDTO);
						
						if (cntOfInsertBoardFile == 0) {
							session.rollback();
							
							String errorMessage = "업로드 신규 등록:개별 업로드 파일 추가 처리가 실패하였습니다.";
							log.warn("{}, 실패한 개별 업로드 파일={}, inObj={}", errorMessage, newBoardFileDTO.toString(), inObj.toString());
							
							MessageResultRes messageResultOutObj = new MessageResultRes();
							messageResultOutObj.setIsSuccess(false);
							messageResultOutObj.setTaskMessageID(inObj.getMessageID());
							messageResultOutObj.setResultMessage(errorMessage);
							toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
							return;
						}
						
						log.debug("attachFileDTOList add newAttachFile={}", newBoardFileDTO.toString());
					}
					
					// session.commit();
					
					outObj = session.selectOne("getBoardUploadFileOutDTO", retAttachId);
					if (null == outObj) {
						session.rollback();
						
						String errorMessage = "업로드 신규 등록:업로드 신규 등록 처리 결과 조회가 실패하였습니다.";
						log.warn("{}, inObj={}", errorMessage, inObj.toString());
						
						MessageResultRes messageResultOutObj = new MessageResultRes();
						messageResultOutObj.setIsSuccess(false);
						messageResultOutObj.setTaskMessageID(inObj.getMessageID());
						messageResultOutObj.setResultMessage(errorMessage);
						toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
						return;
					}
					
					outObj.setAttachFileCnt(outObj.getAttachFileList().size());
					
					// FIXME!
					log.debug("outObj={}", outObj.toString());
					
					session.commit();
					
					toLetterCarrier.addSyncOutputMessage(outObj);
					return;
				}			
			} catch(Exception e) {
				session.rollback();
				
				String errorMessage = new StringBuilder("업로드 신규 등록:알수 없는 이유로 업로드 신규 등록 처리가 실패하였습니다. inObj=")
				.append(inObj.toString().toString()).toString();				
				log.warn(errorMessage, e);
				
				MessageResultRes messageResultOutObj = new MessageResultRes();
				messageResultOutObj.setTaskMessageID(inObj.getMessageID());
				messageResultOutObj.setIsSuccess(false);
				messageResultOutObj.setResultMessage(errorMessage);
				toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
				return;
			} finally {
				session.close();
			}
			
		} else {
			*//** 업로드 수정 *//*
			
			int totalAttachFile = newAttachFileCnt + selectedOldAttachFileCnt;
			
			if (0 == totalAttachFile) {
				String errorMessage = new StringBuilder("업로드 수정:업로드 파일이 존재하지 않습니다.").toString();
				log.warn("{}, inObj={}", errorMessage, inObj.toString());
				
				MessageResult messageResultOutObj = new MessageResult();
				messageResultOutObj.setTaskMessageID(inObj.getMessageID());
				messageResultOutObj.setIsSuccess(false);
				messageResultOutObj.setResultMessage(errorMessage);
				toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
				return;
			}	
			
			if (ServerCommonStaticFinalVars.WEBSITE_FILEUPLOAD_MAX_COUNT < totalAttachFile) {
				String errorMessage = new StringBuilder("업로드 수정:기존 업로드 파일등중 사용자가 선택한 갯수[")
				.append(selectedOldAttachFileCnt)
				.append("]와 신규 추가한 업로드 파일 개수[")
				.append(newAttachFileCnt)
				.append("]의 합이 최대 업로드 파일 개수[")
				.append(ServerCommonStaticFinalVars.WEBSITE_FILEUPLOAD_MAX_COUNT)
				.append("]를 초과 하였습니다.").toString();
				log.warn("{}, inObj={}", errorMessage, inObj.toString());
				
				MessageResultRes messageResultOutObj = new MessageResultRes();
				messageResultOutObj.setTaskMessageID(inObj.getMessageID());
				messageResultOutObj.setIsSuccess(false);
				messageResultOutObj.setResultMessage(errorMessage);
				toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
				return;
			}			
					
			java.util.List<NewAttachFile> newAttachFileList = inObj.getNewAttachFileList();
			
			for (NewAttachFile newAttachFile : newAttachFileList) {
				String attachFileName = newAttachFile.getAttachFileName();
				
				if (attachFileName.equals("")) {
					String errorMessage = "업로드 수정:업로드 파일명을 넣어주세요.";
					log.warn("{}, inObj=", errorMessage, inObj.toString());
					
					MessageResultRes messageResultOutObj = new MessageResultRes();
					messageResultOutObj.setIsSuccess(false);
					messageResultOutObj.setTaskMessageID(inObj.getMessageID());
					messageResultOutObj.setResultMessage(errorMessage);
					toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
					return;
				}
				
				String trimAttachFileName = attachFileName.trim();
				if (!trimAttachFileName.equals(attachFileName)) {
					String errorMessage = "업로드 수정:업로드 파일명을 다시 넣어주세요.";
					log.warn("{}, inObj=", errorMessage, inObj.toString());
					
					MessageResultRes messageResultOutObj = new MessageResultRes();
					messageResultOutObj.setIsSuccess(false);
					messageResultOutObj.setTaskMessageID(inObj.getMessageID());
					messageResultOutObj.setResultMessage(errorMessage);
					toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
					return;
				}
				
				String systemFileName = newAttachFile.getSystemFileName();		
				
				if (systemFileName.equals("")) {
					String errorMessage = "업로드 수정:업로드 시스템 절대 경로 파일명을 넣어주세요.";
					log.warn("{}, inObj=", errorMessage, inObj.toString());
					
					MessageResultRes messageResultOutObj = new MessageResultRes();
					messageResultOutObj.setIsSuccess(false);
					messageResultOutObj.setTaskMessageID(inObj.getMessageID());
					messageResultOutObj.setResultMessage(errorMessage);
					toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
					return;
				}
				
				String trimSystemFileName = systemFileName.trim();
				if (!trimSystemFileName.equals(systemFileName)) {
					String errorMessage = "업로드 수정:업로드 시스템 절대 경로 파일명을 다시 넣어주세요.";
					log.warn("{}, inObj=", errorMessage, inObj.toString());
					
					MessageResultRes messageResultOutObj = new MessageResultRes();
					messageResultOutObj.setIsSuccess(false);
					messageResultOutObj.setTaskMessageID(inObj.getMessageID());
					messageResultOutObj.setResultMessage(errorMessage);
					toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
					return;
				}				
			}		
			
			java.util.List<SelectedOldAttachFile> selectedOldAttachFileList = inObj.getSelectedOldAttachFileList();			
			java.util.List<BoardFileDTO> attachFileDTOList = new ArrayList<BoardFileDTO>();			
			BoardUploadFileRes outObj = null;			
			
			SqlSession session = sqlSessionFactory.openSession(false);
			try {
				BoardFileInfoDTO boardFileInfoDTO = session.selectOne("getBoardFileInfoDTOInLock", inObj);
				if (null == boardFileInfoDTO) {
					session.rollback();
					
					String errorMessage = "업로드 수정:업로드 파일 정보가 존재하지 않습니다.";
					log.warn("{}, inObj=", errorMessage, inObj.toString());
					
					MessageResultRes messageResultOutObj = new MessageResultRes();
					messageResultOutObj.setIsSuccess(false);
					messageResultOutObj.setTaskMessageID(inObj.getMessageID());
					messageResultOutObj.setResultMessage(errorMessage);
					toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
					return;
				}
				
				// FIXME!
				log.info("업로드 수정:변경전 업로드 파일 정보={}, inObj={}", boardFileInfoDTO.toString(), inObj.toString());
				
				*//** 소유자 검사 *//*
				if (! boardFileInfoDTO.getOwnerId().equals(inObj.getUserId())) {
					session.rollback();
					
					String errorMessage = new StringBuilder("업로드 수정:업로드 수정 요청자[")
					.append(inObj.getUserId())
					.append("] 와 업로드 파일 소유자[")
					.append(boardFileInfoDTO.getOwnerId())
					.append("] 가 다릅니다.").toString();
					log.warn("{}, inObj=", errorMessage, inObj.toString());
					
					MessageResultRes messageResultOutObj = new MessageResultRes();
					messageResultOutObj.setIsSuccess(false);
					messageResultOutObj.setTaskMessageID(inObj.getMessageID());
					messageResultOutObj.setResultMessage(errorMessage);
					toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
					return;
				}
				
				
				
				int cntOfUpdateUploadFileInfo = session.update("updateUploadFileInfo", inObj);
				if (0 == cntOfUpdateUploadFileInfo) {
					session.rollback();
					
					String errorMessage = "업로드 수정:업로드 파일 정보 수정 처리가 실패하였습니다.";
					log.warn("{}, inObj=", errorMessage, inObj.toString());
					
					MessageResultRes messageResultOutObj = new MessageResultRes();
					messageResultOutObj.setIsSuccess(false);
					messageResultOutObj.setTaskMessageID(inObj.getMessageID());
					messageResultOutObj.setResultMessage(errorMessage);
					toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
					return;
				}
				
				
				for (SelectedOldAttachFile selectedOldAttachFile : selectedOldAttachFileList) {
					BoardFileDTO userSelectedBoardFileDTO = new BoardFileDTO();
					userSelectedBoardFileDTO.setAttachId(attachId);
					userSelectedBoardFileDTO.setAttachSeq(selectedOldAttachFile.getAttachSeq());
					
					BoardFileDTO  oldBoardFileDTO = session.selectOne("getBoardFileDTO", userSelectedBoardFileDTO);
					if (null == oldBoardFileDTO) {
						session.rollback();
						
						String errorMessage = "업로드 수정:사용자가 선택한 업로드 파일이 존재하지 않습니다.";
						log.warn("{}, 사용자가 선택한 업로드 파일={}, inObj=", errorMessage, selectedOldAttachFile.toString(), inObj.toString());
						
						MessageResultRes messageResultOutObj = new MessageResultRes();
						messageResultOutObj.setIsSuccess(false);
						messageResultOutObj.setTaskMessageID(inObj.getMessageID());
						messageResultOutObj.setResultMessage(errorMessage);
						toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
						return;
					}
					
					oldBoardFileDTO.setAttachSeq((short)attachFileDTOList.size());
					
					
					log.debug("attachFileDTOList add oldAttachFile={}", oldBoardFileDTO.toString());
					
					attachFileDTOList.add(oldBoardFileDTO);					
				}
				
				
				for (NewAttachFile newAttachFile : newAttachFileList) {
					BoardFileDTO newBoardFileDTO = new BoardFileDTO();
					newBoardFileDTO.setAttachId(attachId);
					newBoardFileDTO.setAttachSeq((short)attachFileDTOList.size());
					newBoardFileDTO.setAttachFileName(newAttachFile.getAttachFileName());
					newBoardFileDTO.setSystemFileName(newAttachFile.getSystemFileName());
					
					log.debug("attachFileDTOList add newAttachFile={}", newBoardFileDTO.toString());
					
					attachFileDTOList.add(newBoardFileDTO);	
				}
							
				
				// FIXME!
				// log.info("attachFileDTOList size={}", attachFileDTOList.size());
				
				session.delete("deleteAllUploadFiles", inObj);
				
				for (BoardFileDTO boardFileDTO : attachFileDTOList) {
					int cntOfInsertBoardFile = session.insert("insertBoardFile", boardFileDTO);
					if (0 == cntOfInsertBoardFile) {
						session.rollback();
						
						String errorMessage = "업로드 수정:개별 업로드 파일 추가 처리가 실패하였습니다.";
						log.warn("{}, 개별 업로드 파일={}, inObj=", errorMessage, boardFileDTO.toString(), inObj.toString());
						
						MessageResultRes messageResultOutObj = new MessageResultRes();
						messageResultOutObj.setIsSuccess(false);
						messageResultOutObj.setTaskMessageID(inObj.getMessageID());
						messageResultOutObj.setResultMessage(errorMessage);
						toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
						return;
					}
					
					// FIXME!
					log.info("insertBoardFile, boardFileDTO={}", boardFileDTO.toString());
				}
				
				
				
				outObj = session.selectOne("getBoardUploadFileOutDTO", inObj.getAttachId());
				if (null == outObj) {
					session.rollback();
					
					String errorMessage = "업로드 수정:업로드 수정 처리 결과 조회가 실패하였습니다.";
					log.warn("{}, inObj={}", errorMessage, inObj.toString());
					
					MessageResultRes messageResultOutObj = new MessageResultRes();
					messageResultOutObj.setIsSuccess(false);
					messageResultOutObj.setTaskMessageID(inObj.getMessageID());
					messageResultOutObj.setResultMessage(errorMessage);
					toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
					return;
				}
				
				outObj.setAttachFileCnt(outObj.getAttachFileList().size());
				
				// FIXME!
				log.debug("outObj={}", outObj.toString());
				
				session.commit();
				
				toLetterCarrier.addSyncOutputMessage(outObj);
				return;
			} catch(Exception e) {
				session.rollback();
				
				String errorMessage = new StringBuilder("업로드 수정:알수 없는 이유로 업로드 수정 처리가 실패하였습니다. inObj=")
				.append(inObj.toString().toString()).toString();				
				log.warn(errorMessage, e);
				
				MessageResultRes messageResultOutObj = new MessageResultRes();
				messageResultOutObj.setTaskMessageID(inObj.getMessageID());
				messageResultOutObj.setIsSuccess(false);
				messageResultOutObj.setResultMessage(errorMessage);
				toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
				return;
			} finally {
				session.close();
			}
		}*/
	}
}
