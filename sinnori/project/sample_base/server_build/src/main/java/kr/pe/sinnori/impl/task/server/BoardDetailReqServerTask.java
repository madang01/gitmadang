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
import kr.pe.sinnori.server.lib.MemberStateType;
import kr.pe.sinnori.server.lib.ServerCommonStaticFinalVars;
import kr.pe.sinnori.server.task.AbstractServerTask;
import kr.pe.sinnori.server.task.ToLetterCarrier;

public class BoardDetailReqServerTask extends AbstractServerTask {
	@SuppressWarnings("unused")
	private void sendErrorOutputMessageForCommit(String errorMessage,
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
	}
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
	
	private void sendSuccessOutputMessageForCommit(AbstractMessage outputMessage, 
			Connection conn,
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
		// FIXME!
		log.info(inputMessage.toString());
				
		doWork(projectName, toLetterCarrier, (BoardDetailReq)inputMessage);
	}
	
	private void doWork(String projectName,
			ToLetterCarrier toLetterCarrier, BoardDetailReq boardDetailReq)
			throws Exception {
		DataSource dataSource = DBCPManager.getInstance()
				.getBasicDataSource(ServerCommonStaticFinalVars.SB_CONNECTION_POOL_NAME);
		
		try {
			BoardType.valueOf(boardDetailReq.getBoardId());
		} catch(IllegalArgumentException e) {
			log.warn(e.getMessage(), e);
			sendErrorOutputMessage("잘못된 게시판 종류입니다", toLetterCarrier, boardDetailReq);
			return;
		}
		
		
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			
			DSLContext create = DSL.using(conn, SQLDialect.MYSQL);

			/*
		select 
		SB_BOARD_TB.board_no,
		board_id,
		group_no,	
		group_sq,
		parent_no,
		depth,
		subject,
		content,
		writer_id,
		SB_MEMBER_TB.nickname,
		view_cnt,
		(select count(SB_BOARD_VOTE_TB.user_id) from SB_BOARD_VOTE_TB where SB_BOARD_VOTE_TB.board_no = SB_BOARD_TB.board_no) as votes,
		del_fl, 
		ip,
		SB_BOARD_TB.reg_dt,
		SB_BOARD_TB.mod_dt,
		SB_BOARD_TB.attach_id,
		if (SB_MEMBER_TB.member_gb = 1, '일반회원', if (SB_MEMBER_TB.member_gb = 0, '관리자', '알수없음')) as member_gb_nm,
		SB_MEMBER_TB.member_st
	from SB_BOARD_TB, SB_MEMBER_TB
	where SB_BOARD_TB.board_no = #{boardNo} and SB_MEMBER_TB.user_id = SB_BOARD_TB.writer_id*/
			
			Record19<UInteger, UByte, UInteger, UShort, UInteger, UByte, String, String, String, Integer, String, String, Timestamp, Timestamp, UInteger, String, Object, String, Byte>  
			boardRecord = create.select(SB_BOARD_TB.BOARD_NO, SB_BOARD_TB.BOARD_ID, 
					SB_BOARD_TB.GROUP_NO, SB_BOARD_TB.GROUP_SQ,
					SB_BOARD_TB.PARENT_NO,
					SB_BOARD_TB.DEPTH,
					SB_BOARD_TB.SUBJECT,
					SB_BOARD_TB.CONTENT,
					SB_BOARD_TB.WRITER_ID,
					SB_BOARD_TB.VIEW_CNT,
					SB_BOARD_TB.DEL_FL,
					SB_BOARD_TB.IP,
					SB_BOARD_TB.REG_DT,
					SB_BOARD_TB.MOD_DT,
					JooqSqlUtil.getFieldOfAttachID(UInteger.class, SB_BOARD_TB.ATTACH_ID).as(SB_BOARD_TB.ATTACH_ID.getName()),
					SB_MEMBER_TB.NICKNAME,
					create.selectCount()
						.from(SB_BOARD_VOTE_TB)
						.where(SB_BOARD_VOTE_TB.BOARD_NO.eq(SB_BOARD_TB.BOARD_NO))
						.asField("votes"),
					JooqSqlUtil.getFieldOfMemberGbNm(SB_MEMBER_TB.MEMBER_GB).as("member_gb_nm"),
					SB_MEMBER_TB.MEMBER_ST)
			.from(SB_BOARD_TB)
			.join(SB_MEMBER_TB)
			.on(SB_BOARD_TB.WRITER_ID.eq(SB_MEMBER_TB.USER_ID))
			.where(SB_BOARD_TB.BOARD_NO.eq(UInteger.valueOf(boardDetailReq.getBoardNo())))
			.fetchOne();
			
			if (null == boardRecord) {
				String errorMessage = new StringBuilder("해당 게시글[")
						.append(boardDetailReq.getBoardNo())
						.append("이 존재 하지 않습니다").toString();				
				sendErrorOutputMessageForCommit(errorMessage, conn, toLetterCarrier, boardDetailReq);
				return;
			}
			
			byte memberStateTypeValue = boardRecord.get(SB_MEMBER_TB.MEMBER_ST);
			MemberStateType memberStateType =  MemberStateType.valueOf(memberStateTypeValue);
			if (! memberStateType.equals(MemberStateType.OK)) {
				String errorMessage = new StringBuilder("비 정상 회원[")
						.append(memberStateType.getName())
						.append("]은 해당 게시글[")
						.append(boardDetailReq.getBoardNo())
						.append("는 접근 할 수 없습니다").toString();				
				sendErrorOutputMessageForCommit(errorMessage, conn, toLetterCarrier, boardDetailReq);
				return;
			}
			
			BoardDetailRes boardDetailRes = new BoardDetailRes();
			boardDetailRes.setBoardNo(boardRecord.get(SB_BOARD_TB.BOARD_NO).longValue());
			boardDetailRes.setBoardId(boardRecord.get(SB_BOARD_TB.BOARD_ID).longValue());
			boardDetailRes.setGroupNo(boardRecord.get(SB_BOARD_TB.GROUP_NO).longValue());
			boardDetailRes.setGroupSeq(boardRecord.get(SB_BOARD_TB.GROUP_SQ).intValue());
			boardDetailRes.setParentNo(boardRecord.get(SB_BOARD_TB.PARENT_NO).longValue());
			boardDetailRes.setDepth(boardRecord.get(SB_BOARD_TB.DEPTH).shortValue());
			boardDetailRes.setSubject(boardRecord.get(SB_BOARD_TB.SUBJECT));
			boardDetailRes.setContent(boardRecord.get(SB_BOARD_TB.CONTENT));
			boardDetailRes.setWriterId(boardRecord.get(SB_BOARD_TB.WRITER_ID));
			boardDetailRes.setNickname(boardRecord.get(SB_MEMBER_TB.NICKNAME));
			boardDetailRes.setViewCount(boardRecord.get(SB_BOARD_TB.VIEW_CNT));
			boardDetailRes.setVotes((int)boardRecord.get("votes"));
			boardDetailRes.setDeleteFlag(boardRecord.get(SB_BOARD_TB.DEL_FL));
			boardDetailRes.setIp(boardRecord.get(SB_BOARD_TB.IP));
			boardDetailRes.setRegisterDate(boardRecord.get(SB_BOARD_TB.REG_DT));
			boardDetailRes.setModifiedDate(boardRecord.get(SB_BOARD_TB.MOD_DT));
			boardDetailRes.setAttachId(boardRecord.get(SB_BOARD_TB.ATTACH_ID).longValue());
			
			
			
			List<BoardDetailRes.AttachFile> attachFileList = new ArrayList<BoardDetailRes.AttachFile>();
			
			/*select
			attach_sq, attach_fname
		from SB_BOARD_FILELIST_TB where attach_id = #{attachId}*/
			Result<Record> attachFileRecords = create.select()
			.from(SB_BOARD_FILELIST_TB)
			.where(SB_BOARD_FILELIST_TB.ATTACH_ID.eq(UInteger.valueOf(boardDetailRes.getAttachId()))).fetch();
			
			if (null != attachFileRecords) {
				for (Record attachFileRecord : attachFileRecords) {
					BoardDetailRes.AttachFile attachFile = new BoardDetailRes.AttachFile();
					attachFile.setAttachSeq(attachFileRecord.get(SB_BOARD_FILELIST_TB.ATTACH_SQ).shortValue());
					attachFile.setAttachFileName(attachFileRecord.get(SB_BOARD_FILELIST_TB.ATTACH_FNAME));
					attachFileList.add(attachFile);
				}
			}
			
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
				} catch(Exception e) {
					log.warn("fail to close the db connection", e);
				}
			}
		}
		
		
		/*DBCPManager.getInstance().getBasicDataSource(ServerCommonStaticFinalVars.SB_CONNECTION_POOL_NAME);
		
		SqlSessionFactory sqlSessionFactory = MybatisSqlSessionFactoryManger.getInstance()
				.getSqlSessionFactory(ServerCommonStaticFinalVars.SB_CONNECTION_POOL_NAME);

		BoardDetailOutDTO boardDetailOutObj = null;
		
		SqlSession session = sqlSessionFactory.openSession(false);
		try {			
			boardDetailOutObj = session.selectOne("kr.pr.sinnori.testweb.getBoardDetail", boardDetailReq);
			
			if (null == boardDetailOutObj) {				
				session.commit();
				
				String errorMessage = String.format("게시판 상세 조회 얻기 실패, boardDetailInObj=[%s]", boardDetailReq.toString());
				log.warn(errorMessage);
				
				MessageResultRes messageResultOutObj = new MessageResultRes();
				messageResultOutObj.setIsSuccess(false);
				messageResultOutObj.setTaskMessageID(boardDetailReq.getMessageID());
				messageResultOutObj.setResultMessage("게시판 상세 조회 얻기 실패");
				
				
				toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
			} else {
				java.util.List<AttachFile> attachFileList = boardDetailOutObj.getAttachFileList();
				if (null != attachFileList) {
					boardDetailOutObj.setAttachFileCnt(attachFileList.size());
				} else {
					boardDetailOutObj.setAttachFileCnt(0);
				}
				
				if (boardDetailOutObj.getBoardId() != boardDetailReq.getBoardId()) {
					session.commit();
					
					String errorMessage = "게시판 상세 조회 결과로 얻은 게시판 식별자와 입력으로 받은 게시판 식별자가 상이합니다.";
					log.warn("{}, boardDetailInObj=", errorMessage, boardDetailReq.toString());
					
					MessageResultRes messageResultOutObj = new MessageResultRes();
					messageResultOutObj.setIsSuccess(false);
					messageResultOutObj.setTaskMessageID(boardDetailReq.getMessageID());
					messageResultOutObj.setResultMessage(errorMessage);
					
					toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
				} else {
					
					int resultOfUpdate = session.update("kr.pr.sinnori.testweb.updateBoardViewCnt", boardDetailReq);
					if (resultOfUpdate > 0) {						
						session.commit();					
						
						*//** 조회수 증가 성공후 조회수 보정 *//*
						boardDetailOutObj.setViewCount(boardDetailOutObj.getViewCount()+1);
						
						toLetterCarrier.addSyncOutputMessage(boardDetailOutObj);
					} else {
						session.rollback();
						
						String errorMessage = "게시판 상세 조회후 조회수 증가 실패";
						log.warn("{}, boardDetailInObj=", errorMessage, boardDetailReq.toString());
						
						MessageResultRes messageResultOutObj = new MessageResultRes();
						messageResultOutObj.setIsSuccess(false);
						messageResultOutObj.setTaskMessageID(boardDetailReq.getMessageID());
						messageResultOutObj.setResultMessage(errorMessage);
						
						toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
					}
				}				
			}			
		} catch(Exception e) {
			session.rollback();
			log.warn("unknown error", e);
			
			MessageResultRes messageResultOutObj = new MessageResultRes();
			messageResultOutObj.setTaskMessageID(boardDetailReq.getMessageID());
			messageResultOutObj.setIsSuccess(false);
			messageResultOutObj.setResultMessage("알 수 없는 이유로 게시판 조회가 실패하였습니다.");
			toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
			return;
		} finally {
			session.close();
		}		*/
	}
}
