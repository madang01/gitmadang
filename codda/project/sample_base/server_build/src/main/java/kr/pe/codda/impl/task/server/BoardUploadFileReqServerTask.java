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

import kr.pe.codda.common.exception.ServerServiceException;
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
			AbstractMessage outputMessage = doService((BoardUploadFileReq) inputMessage);
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

			sendErrorOutputMessage("업로드가 실패하였습니다", toLetterCarrier, inputMessage);
			return;
		}
	}

	public BoardUploadFileRes doService(BoardUploadFileReq boardUploadFileReq) throws Exception {
		// FIXME!
		log.info(boardUploadFileReq.toString());

		try {
			ValueChecker.checkValidUserId(boardUploadFileReq.getUserId());
		} catch (IllegalArgumentException e) {
			/*
			 * sendErrorOutputMessage(e.getMessage(), toLetterCarrier, boardUploadFileReq);
			 * return;
			 */
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}

		try {
			ValueChecker.checkValidIP(boardUploadFileReq.getIp());
		} catch (IllegalArgumentException e) {
			/*
			 * sendErrorOutputMessage(e.getMessage(), toLetterCarrier, boardUploadFileReq);
			 * return;
			 */
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}

		int selectedOldAttachedFileCnt = boardUploadFileReq.getOldAttachedFileCnt();
		int newAttachedFileCnt = boardUploadFileReq.getNewAttachedFileCnt();

		if (0 > selectedOldAttachedFileCnt) {
			String errorMessage = new StringBuilder("업로드했던 파일들에 대한 사용자 선택 갯수[").append(selectedOldAttachedFileCnt)
					.append("]가 0보다 작습니다.").toString();
			/*
			 * sendErrorOutputMessage(errorMessage, toLetterCarrier, boardUploadFileReq);
			 * return;
			 */
			throw new ServerServiceException(errorMessage);
		}

		if (0 == boardUploadFileReq.getAttachId()) {
			/** 신규 */
			if (0 != selectedOldAttachedFileCnt) {
				String errorMessage = new StringBuilder("업로드했던 파일들에 대한 사용자 선택 갯수[").append(selectedOldAttachedFileCnt)
						.append("]가 0보다 작습니다.").toString();
				/*
				 * sendErrorOutputMessage(errorMessage, toLetterCarrier, boardUploadFileReq);
				 * return;
				 */
				throw new ServerServiceException(errorMessage);
			}

			if (0 >= newAttachedFileCnt) {
				String errorMessage = new StringBuilder("신규 업로드 파일 갯수[").append(newAttachedFileCnt)
						.append("]는 0 보다 커야 합니다").toString();
				/*
				 * sendErrorOutputMessage(errorMessage, toLetterCarrier, boardUploadFileReq);
				 * return;
				 */
				throw new ServerServiceException(errorMessage);
			}
		} else {
			/** 수정 */
			if (0 == selectedOldAttachedFileCnt && 0 >= newAttachedFileCnt) {
				String errorMessage = new StringBuilder("업로드 했던 파일들 모두 선택 취소후에는 신규 파일 갯수[").append(newAttachedFileCnt)
						.append("]는 0 보다 커야 합니다").toString();
				/*
				 * sendErrorOutputMessage(errorMessage, toLetterCarrier, boardUploadFileReq);
				 * return;
				 */
				throw new ServerServiceException(errorMessage);
			}
		}

		Set<Long> oldAttachSeqSet = new HashSet<Long>();
		for (BoardUploadFileReq.OldAttachedFile selectedOldAttachedFile : boardUploadFileReq.getOldAttachedFileList()) {
			oldAttachSeqSet.add(selectedOldAttachedFile.getAttachSeq());
		}

		if (oldAttachSeqSet.size() != boardUploadFileReq.getOldAttachedFileList().size()) {
			String errorMessage = "업로드 했던 파일들중 선택한 목록에서 중복된 원소가 있습니다";
			/*
			 * sendErrorOutputMessage(errorMessage, toLetterCarrier, boardUploadFileReq);
			 * return;
			 */
			throw new ServerServiceException(errorMessage);
		}

		for (BoardUploadFileReq.NewAttachedFile newAttachedFile : boardUploadFileReq.getNewAttachedFileList()) {

			try {
				ValueChecker.checkValidFileName(newAttachedFile.getAttachedFileName());
			} catch (IllegalArgumentException e) {
				String errorMessage = new StringBuilder("유효하지 않은 첨부 파일명입니다. errmsg=").append(e.getMessage()).toString();
				/*
				 * sendErrorOutputMessage(errorMessage, toLetterCarrier, boardUploadFileReq);
				 * return;
				 */
				throw new ServerServiceException(errorMessage);
			}

			try {
				ValueChecker.checkValidFileName(newAttachedFile.getSystemFileName());
			} catch (IllegalArgumentException e) {
				String errorMessage = new StringBuilder("유효하지 않은 시스템 파일명입니다. errmsg=").append(e.getMessage())
						.toString();
				/*
				 * sendErrorOutputMessage(errorMessage, toLetterCarrier, boardUploadFileReq);
				 * return;
				 */
				throw new ServerServiceException(errorMessage);
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
				 * insert into `SINNORIDB`.`SB_BOARD_FILEINFO_TB` (`attach_id`, `owner_id`,
				 * `ip`, `reg_dt`, `mod_dt`) VALUES (0, #{userId}, #{ip}, sysdate(), reg_dt)
				 * select last_insert_id() as attachId insert into
				 * `SINNORIDB`.`SB_BOARD_FILELIST_TB`
				 * (`attach_id`,`attach_sq`,`attach_fname`,`sys_fname`) VALUES (#{attachId},
				 * #{attachSeq}, #{attachFileName}, #{systemFileName})
				 */

				int countOfFileInfoInsert = create.insertInto(SB_BOARD_FILEINFO_TB)
						.set(SB_BOARD_FILEINFO_TB.ATTACH_ID, UInteger.valueOf(0))
						.set(SB_BOARD_FILEINFO_TB.OWNER_ID, boardUploadFileReq.getUserId())
						.set(SB_BOARD_FILEINFO_TB.IP, boardUploadFileReq.getIp())
						.set(SB_BOARD_FILEINFO_TB.REG_DT, JooqSqlUtil.getFieldOfSysDate(Timestamp.class))
						.set(SB_BOARD_FILEINFO_TB.MOD_DT, SB_BOARD_FILEINFO_TB.REG_DT).execute();

				if (0 == countOfFileInfoInsert) {
					try {
						conn.rollback();
					} catch (Exception e) {
						log.warn("fail to rollback");
					}

					String errorMessage = "업로드 파일 정보를 생성하는데 실패 하였습니다";
					/*
					 * sendErrorOutputMessageForRollback(errorMessage, conn, toLetterCarrier,
					 * boardUploadFileReq); return;
					 */
					throw new ServerServiceException(errorMessage);
				}

				UInteger newAttachID = create.select(
						JooqSqlUtil.getFieldOfLastInsertID(UInteger.class).as(SB_BOARD_FILEINFO_TB.ATTACH_ID.getName()))
						.fetchOne(0, UInteger.class);

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
							.set(SB_BOARD_FILELIST_TB.SYS_FNAME, newAttachedFile.getSystemFileName()).execute();

					if (0 == countOfFileListInsert) {
						try {
							conn.rollback();
						} catch (Exception e) {
							log.warn("fail to rollback");
						}

						String errorMessage = new StringBuilder("신규로 첨부된 파일[").append(newAttachedFile.toString())
								.append("]을 파일 목록[").append(newAttachSeq).append("]에 추가하는데 실패하였습니다").toString();
						/*
						 * sendErrorOutputMessageForRollback(errorMessage, conn, toLetterCarrier,
						 * boardUploadFileReq); return;
						 */
						throw new ServerServiceException(errorMessage);
					}

					BoardUploadFileRes.AttachedFile attachedFile = new BoardUploadFileRes.AttachedFile();
					attachedFile.setAttachSeq(newAttachSeq);
					attachedFile.setAttachedFileName(newAttachedFile.getAttachedFileName());
					attachedFile.setSystemFileName(newAttachedFile.getSystemFileName());

					newAttachSeq++;
				}

				// sendSuccessOutputMessageForCommit(boardUploadFileRes, conn, toLetterCarrier);
				return boardUploadFileRes;
			} else {
				/** 수정 */

				/**
				 * select * from SB_BOARD_FILEINFO_TB where attach_id = #{attachId} for update
				 * update SB_BOARD_FILEINFO_TB set ip=#{ip}, mod_dt=sysdate() where attach_id =
				 * #{attachId} delete from SB_BOARD_FILELIST_TB where attach_id = 1; insert into
				 * `SINNORIDB`.`SB_BOARD_FILELIST_TB`
				 * (`attach_id`,`attach_sq`,`attach_fname`,`sys_fname`) VALUES (#{attachId},
				 * #{newAttachSeq}, #{attachFileName}, #{systemFileName})
				 */

				UInteger targetAttachId = UInteger.valueOf(boardUploadFileReq.getAttachId());

				Record1<String> fileInfoRecord = create.select(SB_BOARD_FILEINFO_TB.OWNER_ID).from(SB_BOARD_FILEINFO_TB)
						.where(SB_BOARD_FILEINFO_TB.ATTACH_ID.eq(targetAttachId)).forUpdate().fetchOne();

				if (null == fileInfoRecord) {
					try {
						conn.rollback();
					} catch (Exception e) {
						log.warn("fail to rollback");
					}

					String errorMessage = new StringBuilder("파일 식별자[").append(boardUploadFileReq.getAttachId())
							.append("]와 일치하는 업로드 파일 정보가 없습니다").toString();
					/*
					 * sendErrorOutputMessageForRollback(errorMessage, conn, toLetterCarrier,
					 * boardUploadFileReq); return;
					 */
					throw new ServerServiceException(errorMessage);
				}

				String ownerID = fileInfoRecord.get(SB_BOARD_FILEINFO_TB.OWNER_ID);

				if (!ownerID.equals(boardUploadFileReq.getUserId())) {
					try {
						conn.rollback();
					} catch (Exception e) {
						log.warn("fail to rollback");
					}

					String errorMessage = new StringBuilder("업로드 파일 수정은 소유자만 가능합니다, errmsg=파일 식별자[")
							.append(boardUploadFileReq.getAttachId()).append("]의 소유자[").append(ownerID)
							.append("]와 업로드 파일 수정 요청자[").append(boardUploadFileReq.getUserId()).append("]가 다릅니다")
							.toString();
					/*
					 * sendErrorOutputMessageForCommit(errorMessage, conn, toLetterCarrier,
					 * boardUploadFileReq); return;
					 */
					throw new ServerServiceException(errorMessage);
				}

				int countOfFileInfoUpdate = create.update(SB_BOARD_FILEINFO_TB)
						.set(SB_BOARD_FILEINFO_TB.IP, boardUploadFileReq.getIp())
						.set(SB_BOARD_FILEINFO_TB.MOD_DT, JooqSqlUtil.getFieldOfSysDate(Timestamp.class))
						.where(SB_BOARD_FILEINFO_TB.ATTACH_ID.eq(targetAttachId)).execute();

				if (0 == countOfFileInfoUpdate) {
					try {
						conn.rollback();
					} catch (Exception e) {
						log.warn("fail to rollback");
					}

					String errorMessage = new StringBuilder("업로드 파일 정보[").append(boardUploadFileReq.getAttachId())
							.append("]를 갱신하는데 실패하였습니다").toString();
					/*
					 * sendErrorOutputMessageForRollback(errorMessage, conn, toLetterCarrier,
					 * boardUploadFileReq); return;
					 */
					throw new ServerServiceException(errorMessage);
				}

				create.delete(SB_BOARD_FILELIST_TB).where(SB_BOARD_FILELIST_TB.ATTACH_ID.eq(targetAttachId))
						.and(SB_BOARD_FILELIST_TB.ATTACH_ID.notIn(oldAttachSeqSet)).execute();
				
				List<BoardUploadFileRes.AttachedFile> attachedFileList = new ArrayList<BoardUploadFileRes.AttachedFile>();

				long newAttachSeq = 0;
				for (BoardUploadFileReq.NewAttachedFile newAttachedFile : boardUploadFileReq.getNewAttachedFileList()) {
					int countOfFileListInsert = create.insertInto(SB_BOARD_FILELIST_TB)
							.set(SB_BOARD_FILELIST_TB.ATTACH_ID, targetAttachId)
							.set(SB_BOARD_FILELIST_TB.ATTACH_SQ, UInteger.valueOf(newAttachSeq))
							.set(SB_BOARD_FILELIST_TB.ATTACH_FNAME, newAttachedFile.getAttachedFileName())
							.set(SB_BOARD_FILELIST_TB.SYS_FNAME, newAttachedFile.getSystemFileName()).execute();

					if (0 == countOfFileListInsert) {
						try {
							conn.rollback();
						} catch (Exception e) {
							log.warn("fail to rollback");
						}

						String errorMessage = new StringBuilder("업로드 파일 목록에 신규 업로드 파일 레코드[")
								.append(newAttachedFile.toString()).append("]를 추가하는데 실패하였습니다").toString();
						/*
						 * sendErrorOutputMessageForRollback(errorMessage, conn, toLetterCarrier,
						 * boardUploadFileReq); return;
						 */
						throw new ServerServiceException(errorMessage);
					}
					
					BoardUploadFileRes.AttachedFile attachedFile = new BoardUploadFileRes.AttachedFile(); 
					attachedFile.setAttachSeq(newAttachSeq);
					attachedFile.setAttachedFileName(newAttachedFile.getAttachedFileName());
					attachedFile.setSystemFileName(newAttachedFile.getSystemFileName());
					attachedFileList.add(attachedFile);
					
					newAttachSeq++;
				}
				
				conn.commit();
				
				BoardUploadFileRes boardUploadFileRes = new BoardUploadFileRes();
				boardUploadFileRes.setAttachId(boardUploadFileReq.getAttachId());
				boardUploadFileRes.setOwnerId(boardUploadFileReq.getUserId());
				boardUploadFileRes.setIp(boardUploadFileReq.getIp());
				boardUploadFileRes.setAttachedFileCnt(boardUploadFileReq.getNewAttachedFileList().size());
				boardUploadFileRes.setAttachedFileList(attachedFileList);
				
				return boardUploadFileRes;

			}
		} catch (ServerServiceException e) {
			throw e;
		} catch (Exception e) {
			/*
			 * log.warn("unknown error", e);
			 * 
			 * sendErrorOutputMessageForRollback("2.게시글에 대한 추천이 실패하였습니다", conn,
			 * toLetterCarrier, boardUploadFileReq); return;
			 */
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
	}
}
