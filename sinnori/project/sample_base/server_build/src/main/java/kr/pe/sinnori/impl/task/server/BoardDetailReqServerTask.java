package kr.pe.sinnori.impl.task.server;

import static kr.pe.sinnori.impl.jooq.tables.SbBoardFilelistTb.SB_BOARD_FILELIST_TB;
import static kr.pe.sinnori.impl.jooq.tables.SbBoardTb.SB_BOARD_TB;
import static kr.pe.sinnori.impl.jooq.tables.SbBoardVoteTb.SB_BOARD_VOTE_TB;
import static kr.pe.sinnori.impl.jooq.tables.SbMemberTb.SB_MEMBER_TB;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record19;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.jooq.types.UShort;

import kr.pe.sinnori.common.etc.DBCPManager;
import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.impl.message.BoardDetailReq.BoardDetailReq;
import kr.pe.sinnori.impl.message.BoardDetailRes.BoardDetailRes;
import kr.pe.sinnori.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.sinnori.server.PersonalLoginManagerIF;
import kr.pe.sinnori.server.lib.BoardType;
import kr.pe.sinnori.server.lib.JooqSqlUtil;
import kr.pe.sinnori.server.lib.ServerCommonStaticFinalVars;
import kr.pe.sinnori.server.task.AbstractServerTask;
import kr.pe.sinnori.server.task.ToLetterCarrier;

public class BoardDetailReqServerTask extends AbstractServerTask {

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
		// FIXME!
		log.info(inputMessage.toString());

		doWork(projectName, toLetterCarrier, (BoardDetailReq) inputMessage);
	}

	private void doWork(String projectName, ToLetterCarrier toLetterCarrier, BoardDetailReq boardDetailReq)
			throws Exception {

		try {
			BoardType.valueOf(boardDetailReq.getBoardId());
		} catch (IllegalArgumentException e) {
			log.warn(e.getMessage(), e);
			sendErrorOutputMessage("잘못된 게시판 종류입니다", toLetterCarrier, boardDetailReq);
			return;
		}

		DataSource dataSource = DBCPManager.getInstance()
				.getBasicDataSource(ServerCommonStaticFinalVars.SB_CONNECTION_POOL_NAME);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL);
			Record19<UInteger, UByte, UInteger, UShort, UInteger, UByte, String, String, String, Integer, String, String, Timestamp, Timestamp, UInteger, String, Object, Byte, String> 
			boardRecord = create.select(SB_BOARD_TB.BOARD_NO, SB_BOARD_TB.BOARD_ID, SB_BOARD_TB.GROUP_NO, SB_BOARD_TB.GROUP_SQ,
							SB_BOARD_TB.PARENT_NO, SB_BOARD_TB.DEPTH, SB_BOARD_TB.SUBJECT, SB_BOARD_TB.CONTENT,
							SB_BOARD_TB.WRITER_ID, SB_BOARD_TB.VIEW_CNT, SB_BOARD_TB.BOARD_ST, SB_BOARD_TB.IP,
							SB_BOARD_TB.REG_DT, SB_BOARD_TB.MOD_DT,
							JooqSqlUtil.getFieldOfAttachID(SB_BOARD_TB.ATTACH_ID).as(SB_BOARD_TB.ATTACH_ID.getName()),
							SB_MEMBER_TB.NICKNAME,
							create.selectCount().from(SB_BOARD_VOTE_TB)
									.where(SB_BOARD_VOTE_TB.BOARD_NO.eq(SB_BOARD_TB.BOARD_NO)).asField("votes"),
							SB_MEMBER_TB.LEVEL, SB_MEMBER_TB.MEMBER_ST)
					.from(SB_BOARD_TB).join(SB_MEMBER_TB).on(SB_BOARD_TB.WRITER_ID.eq(SB_MEMBER_TB.USER_ID))
					.where(SB_BOARD_TB.BOARD_NO.eq(UInteger.valueOf(boardDetailReq.getBoardNo()))).forUpdate()
					.fetchOne();

			if (null == boardRecord) {
				String errorMessage = new StringBuilder("해당 게시글[").append(boardDetailReq.getBoardNo())
						.append("이 존재 하지 않습니다").toString();
				sendErrorOutputMessageForCommit(errorMessage, conn, toLetterCarrier, boardDetailReq);
				return;
			}

			/**
			 * page view count increase
			 * 
			 * update SB_BOARD_TB set view_cnt = view_cnt + 1 where board_no=#{boardNo}
			 * 
			 */
			int countOfViewCountUpdate = create.update(SB_BOARD_TB)
					.set(SB_BOARD_TB.VIEW_CNT, SB_BOARD_TB.VIEW_CNT.add(1))
					.where(SB_BOARD_TB.BOARD_NO.eq(UInteger.valueOf(boardDetailReq.getBoardNo()))).execute();

			if (0 == countOfViewCountUpdate) {
				String errorMessage = new StringBuilder("해당 게시글[").append(boardDetailReq.getBoardNo())
						.append("] 읽은 횟수 갱신이 실패하였습니다").toString();
				sendErrorOutputMessageForRollback(errorMessage, conn, toLetterCarrier, boardDetailReq);
				return;
			}

			BoardDetailRes boardDetailRes = new BoardDetailRes();
			boardDetailRes.setBoardNo(boardRecord.get(SB_BOARD_TB.BOARD_NO).longValue());
			boardDetailRes.setBoardId(boardRecord.get(SB_BOARD_TB.BOARD_ID).shortValue());
			boardDetailRes.setGroupNo(boardRecord.get(SB_BOARD_TB.GROUP_NO).longValue());
			boardDetailRes.setGroupSeq(boardRecord.get(SB_BOARD_TB.GROUP_SQ).intValue());
			boardDetailRes.setParentNo(boardRecord.get(SB_BOARD_TB.PARENT_NO).longValue());
			boardDetailRes.setDepth(boardRecord.get(SB_BOARD_TB.DEPTH).shortValue());
			boardDetailRes.setSubject(boardRecord.get(SB_BOARD_TB.SUBJECT));
			boardDetailRes.setContent(boardRecord.get(SB_BOARD_TB.CONTENT));
			boardDetailRes.setWriterId(boardRecord.get(SB_BOARD_TB.WRITER_ID));
			boardDetailRes.setNickname(boardRecord.get(SB_MEMBER_TB.NICKNAME));
			boardDetailRes.setViewCount(boardRecord.get(SB_BOARD_TB.VIEW_CNT));
			boardDetailRes.setVotes(boardRecord.get("votes", Integer.class));
			boardDetailRes.setBoardSate(boardRecord.get(SB_BOARD_TB.BOARD_ST));
			boardDetailRes.setIp(boardRecord.get(SB_BOARD_TB.IP));
			boardDetailRes.setRegisterDate(boardRecord.get(SB_BOARD_TB.REG_DT));
			boardDetailRes.setModifiedDate(boardRecord.get(SB_BOARD_TB.MOD_DT));
			boardDetailRes.setMembershipLevel(boardRecord.get(SB_MEMBER_TB.LEVEL));
			boardDetailRes.setMemberState(boardRecord.get(SB_MEMBER_TB.MEMBER_ST));
			boardDetailRes.setAttachId(boardRecord.get(SB_BOARD_TB.ATTACH_ID).longValue());

			List<BoardDetailRes.AttachFile> attachFileList = new ArrayList<BoardDetailRes.AttachFile>();

			if (0 != boardDetailRes.getAttachId()) {
				Result<Record> attachFileRecords = create.select().from(SB_BOARD_FILELIST_TB)
						.where(SB_BOARD_FILELIST_TB.ATTACH_ID.eq(UInteger.valueOf(boardDetailRes.getAttachId())))
						.fetch();

				if (null != attachFileRecords) {
					for (Record attachFileRecord : attachFileRecords) {
						BoardDetailRes.AttachFile attachFile = new BoardDetailRes.AttachFile();
						attachFile.setAttachSeq(attachFileRecord.get(SB_BOARD_FILELIST_TB.ATTACH_SQ).shortValue());
						attachFile.setAttachFileName(attachFileRecord.get(SB_BOARD_FILELIST_TB.ATTACH_FNAME));
						attachFileList.add(attachFile);
					}
				}
			}

			/*
			 * select attach_sq, attach_fname from SB_BOARD_FILELIST_TB where attach_id =
			 * #{attachId}
			 */

			boardDetailRes.setAttachFileCnt(attachFileList.size());
			boardDetailRes.setAttachFileList(attachFileList);

			sendSuccessOutputMessageForCommit(boardDetailRes, conn, toLetterCarrier);
			return;
		} catch (Exception e) {
			log.warn("unknown error", e);

			sendErrorOutputMessageForRollback("게시글을 가져오는데 실패하였습니다", conn, toLetterCarrier, boardDetailReq);
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
