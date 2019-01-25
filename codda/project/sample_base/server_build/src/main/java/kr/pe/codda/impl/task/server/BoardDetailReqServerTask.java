package kr.pe.codda.impl.task.server;

import static kr.pe.codda.impl.jooq.tables.SbBoardFilelistTb.SB_BOARD_FILELIST_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardHistoryTb.SB_BOARD_HISTORY_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardTb.SB_BOARD_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardVoteTb.SB_BOARD_VOTE_TB;
import static kr.pe.codda.impl.jooq.tables.SbMemberTb.SB_MEMBER_TB;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.BoardDetailReq.BoardDetailReq;
import kr.pe.codda.impl.message.BoardDetailRes.BoardDetailRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.BoardStateType;
import kr.pe.codda.server.lib.BoardType;
import kr.pe.codda.server.lib.MemberRoleType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record4;
import org.jooq.Record6;
import org.jooq.Record8;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.jooq.types.UShort;

public class BoardDetailReqServerTask extends AbstractServerTask {
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
			AbstractMessage outputMessage = doWork(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, (BoardDetailReq)inputMessage);
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
						
			sendErrorOutputMessage("게시글 가져오는데 실패하였습니다", toLetterCarrier, inputMessage);
			return;
		}
	}

	public BoardDetailRes doWork(String dbcpName, BoardDetailReq boardDetailReq)
			throws Exception {
		// FIXME!
		log.info(boardDetailReq.toString());

		try {
			BoardType.valueOf(boardDetailReq.getBoardID());
		} catch (IllegalArgumentException e) {			
			String errorMessage = "잘못된 게시판 식별자입니다";
			throw new ServerServiceException(errorMessage);
		}
		
		if (boardDetailReq.getBoardNo() < 0 || boardDetailReq.getBoardNo() > CommonStaticFinalVars.UNSIGNED_INTEGER_MAX) {
			String errorMessage = "unsinged integer 를 벗어난 게시판 번호입니다";
			throw new ServerServiceException(errorMessage);
		}
		
		UByte boardID = UByte.valueOf(boardDetailReq.getBoardID());
		UInteger boardNo = UInteger.valueOf(boardDetailReq.getBoardNo());
		
		UInteger groupNo = null;
		UShort groupSeqence = null;
		UInteger parentNo = null;
		UByte depth = null;
		int oldViewCount = 0;
		String boardState = null;
		UByte nextAttachedFileSeq = null;
		int votes = 0;
		String firstWriterID = null;
		String firstWriterIP = null;
		Timestamp firstRegisteredDate = null;
		String firstWriterNickname = null;
		String lastlModifiedSubject = null;
		String lastModifiedContent = null;
		String lastModifierID =  null;
		String lastModifierNickName = null;
		String lastModifierIP = null;
		Timestamp lastModifedDate = null;
		
		List<BoardDetailRes.AttachedFile> attachedFileList = new ArrayList<BoardDetailRes.AttachedFile>();

		DataSource dataSource = DBCPManager.getInstance()
				.getBasicDataSource(dbcpName);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			
			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(dbcpName));
			
			String memberRoleOfRequestedUserID = ValueChecker.checkValidRequestedUserState(conn, create, log, boardDetailReq.getRequestedUserID());	
			MemberRoleType  memberRoleTypeOfRequestedUserID = null;
			try {
				memberRoleTypeOfRequestedUserID = MemberRoleType.valueOf(memberRoleOfRequestedUserID, false);
			} catch(IllegalArgumentException e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("해당 게시글 상세 보기 요청자의 멤버 타입[")
						.append(memberRoleOfRequestedUserID)
						.append("]이 잘못되어있습니다").toString();
				throw new ServerServiceException(errorMessage);
			}			
			
			Record8<UInteger, UShort, UInteger, UByte, Integer, String, UByte, Object> 
			boardRecord = create.select(SB_BOARD_TB.GROUP_NO, 
						SB_BOARD_TB.GROUP_SQ,
						SB_BOARD_TB.PARENT_NO, 
						SB_BOARD_TB.DEPTH,						 
						SB_BOARD_TB.VIEW_CNT, 
						SB_BOARD_TB.BOARD_ST,
						SB_BOARD_TB.NEXT_ATTACHED_FILE_SQ,
						create.selectCount().from(SB_BOARD_VOTE_TB)
							.where(SB_BOARD_VOTE_TB.BOARD_ID.eq(SB_BOARD_TB.BOARD_ID))
							.and(SB_BOARD_VOTE_TB.BOARD_NO.eq(SB_BOARD_TB.BOARD_NO)).asField("votes"))
					.from(SB_BOARD_TB)					
					.where(SB_BOARD_TB.BOARD_ID.eq(boardID))
					.and(SB_BOARD_TB.BOARD_NO.eq(boardNo)).forUpdate()
					.fetchOne();

			if (null == boardRecord) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("해당 게시글이 존재 하지 않습니다").toString();
				throw new ServerServiceException(errorMessage);
			}
			
			
			groupNo = boardRecord.get(SB_BOARD_TB.GROUP_NO);
			groupSeqence = boardRecord.get(SB_BOARD_TB.GROUP_SQ);
			parentNo = boardRecord.get(SB_BOARD_TB.PARENT_NO);
			depth = boardRecord.get(SB_BOARD_TB.DEPTH);
			oldViewCount = boardRecord.get(SB_BOARD_TB.VIEW_CNT);
			boardState = boardRecord.getValue(SB_BOARD_TB.BOARD_ST);
			nextAttachedFileSeq = boardRecord.getValue(SB_BOARD_TB.NEXT_ATTACHED_FILE_SQ);
			votes = boardRecord.get("votes", Integer.class);			
			
			BoardStateType boardStateType = null;
			try {
				boardStateType = BoardStateType.valueOf(boardState, false);
			} catch(IllegalArgumentException e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("게시글의 DB 상태 값[")
					.append(boardState).append("]이 잘못되었습니다")
					.toString();
				throw new ServerServiceException(errorMessage);
			}	
			
			Record4<String, String, Timestamp, String> 
			firstWriterBoardRecord = create.select(SB_BOARD_HISTORY_TB.MODIFIER_ID, 
					SB_BOARD_HISTORY_TB.IP, 
					SB_BOARD_HISTORY_TB.REG_DT,
					SB_MEMBER_TB.NICKNAME)
			.from(SB_BOARD_HISTORY_TB)
			.join(SB_MEMBER_TB)
			.on(SB_MEMBER_TB.USER_ID.eq(SB_BOARD_HISTORY_TB.MODIFIER_ID))
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
				
				String errorMessage = "해당 게시글의 최초 작성자 정보가 존재 하지 않습니다";
				throw new ServerServiceException(errorMessage);
			}
			
			firstWriterID = firstWriterBoardRecord.getValue(SB_BOARD_HISTORY_TB.MODIFIER_ID);
			firstWriterIP = firstWriterBoardRecord.getValue(SB_BOARD_HISTORY_TB.IP);
			firstRegisteredDate = firstWriterBoardRecord.getValue(SB_BOARD_HISTORY_TB.REG_DT);
			firstWriterNickname = firstWriterBoardRecord.getValue(SB_MEMBER_TB.NICKNAME);			
			
			if (! MemberRoleType.ADMIN.equals(memberRoleTypeOfRequestedUserID)) {
				if (! BoardStateType.OK.equals(boardStateType)) {
					try {
						conn.rollback();
					} catch (Exception e1) {
						log.warn("fail to rollback");
					}

					String errorMessage = null;
					
					if (BoardStateType.DELETE.equals(boardStateType)) {
						errorMessage = "해당 게시글은 삭제된 글입니다";
					} else if (BoardStateType.BLOCK.equals(boardStateType)) {
						errorMessage = "해당 게시글은 관리자에 의해 차단된 글입니다";
					} else if (BoardStateType.TREEBLOCK.equals(boardStateType)) {
						errorMessage = "해당 게시글은 관리자에 의해 차단된 글을 루트로 하는 트리에 속한 글입니다";
					} else {
						errorMessage = new StringBuilder()
						.append("해당 게시글 상태[")
						.append(boardStateType.getName())
						.append("]가 정상이 아닙니다").toString();
					}
					
					throw new ServerServiceException(errorMessage);
				}
			}			
				
			Record6<String, String, String, String, Timestamp, String> lastlModifiedBoardHistoryRecord = create.select(SB_BOARD_HISTORY_TB.SUBJECT, 
					SB_BOARD_HISTORY_TB.CONTENT,
					SB_BOARD_HISTORY_TB.MODIFIER_ID,
					SB_MEMBER_TB.NICKNAME,
					SB_BOARD_HISTORY_TB.REG_DT,					
					SB_BOARD_HISTORY_TB.IP)
			.from(SB_BOARD_HISTORY_TB)
			.join(SB_MEMBER_TB)
			.on(SB_MEMBER_TB.USER_ID.eq(SB_BOARD_HISTORY_TB.MODIFIER_ID))
			.where(SB_BOARD_HISTORY_TB.BOARD_ID.eq(boardID))
			.and(SB_BOARD_HISTORY_TB.BOARD_NO.eq(boardNo))
			.and(SB_BOARD_HISTORY_TB.HISTORY_SQ.eq(create.select(SB_BOARD_HISTORY_TB.HISTORY_SQ.max())
					.from(SB_BOARD_HISTORY_TB)
					.where(SB_BOARD_HISTORY_TB.BOARD_ID.eq(boardID))
					.and(SB_BOARD_HISTORY_TB.BOARD_NO.eq(boardNo)))).fetchOne();	
			
			lastlModifiedSubject = lastlModifiedBoardHistoryRecord.getValue(SB_BOARD_HISTORY_TB.SUBJECT);
			lastModifiedContent = lastlModifiedBoardHistoryRecord.getValue(SB_BOARD_HISTORY_TB.CONTENT);
			lastModifierID =  lastlModifiedBoardHistoryRecord.getValue(SB_BOARD_HISTORY_TB.MODIFIER_ID);
			lastModifierNickName = lastlModifiedBoardHistoryRecord.getValue(SB_MEMBER_TB.NICKNAME);
			lastModifierIP = lastlModifiedBoardHistoryRecord.getValue(SB_BOARD_HISTORY_TB.IP);
			lastModifedDate = lastlModifiedBoardHistoryRecord.getValue(SB_BOARD_HISTORY_TB.REG_DT);
			
			Result<Record> attachFileListRecord = create.select().from(SB_BOARD_FILELIST_TB)
					.where(SB_BOARD_FILELIST_TB.BOARD_ID.eq(boardID))
					.and(SB_BOARD_FILELIST_TB.BOARD_NO.eq(boardNo))
					.fetch();

			if (null != attachFileListRecord) {
				for (Record attachFileRecord : attachFileListRecord) {
					BoardDetailRes.AttachedFile attachedFile = new BoardDetailRes.AttachedFile();
					attachedFile.setAttachedFileSeq(attachFileRecord.get(SB_BOARD_FILELIST_TB.ATTACHED_FILE_SQ).shortValue());
					attachedFile.setAttachedFileName(attachFileRecord.get(SB_BOARD_FILELIST_TB.ATTACHED_FNAME));
					attachedFile.setAttachedFileSize(attachFileRecord.get(SB_BOARD_FILELIST_TB.ATTACHED_FSIZE));
					attachedFileList.add(attachedFile);
				}
			}
			
			int countOfViewCountUpdate = create.update(SB_BOARD_TB)
					.set(SB_BOARD_TB.VIEW_CNT, SB_BOARD_TB.VIEW_CNT.add(1))
					.where(SB_BOARD_TB.BOARD_ID.eq(boardID))
					.and(SB_BOARD_TB.BOARD_NO.eq(boardNo)).execute();

			if (0 == countOfViewCountUpdate) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("해당 게시글 읽은 횟수 갱신이 실패하였습니다").toString();				
				throw new ServerServiceException(errorMessage);
			}			
			
			conn.commit();			
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
				} catch (Exception e) {
					log.warn("fail to close the db connection", e);
				}
			}
		}
		
		BoardDetailRes boardDetailRes = new BoardDetailRes();
		boardDetailRes.setBoardID(boardDetailReq.getBoardID());
		boardDetailRes.setBoardNo(boardDetailReq.getBoardNo());			
		boardDetailRes.setGroupNo(groupNo.longValue());
		boardDetailRes.setGroupSeq(groupSeqence.intValue());
		boardDetailRes.setParentNo(parentNo.longValue());
		boardDetailRes.setDepth(depth.shortValue());
		boardDetailRes.setViewCount(oldViewCount + 1);
		boardDetailRes.setNextAttachedFileSeq(nextAttachedFileSeq.shortValue());
		boardDetailRes.setBoardSate(boardState);			
		boardDetailRes.setVotes(votes);			
		
		boardDetailRes.setWriterID(firstWriterID );
		boardDetailRes.setWriterIP(firstWriterIP);
		boardDetailRes.setRegisteredDate(firstRegisteredDate);
		boardDetailRes.setNickname(firstWriterNickname);			
		boardDetailRes.setSubject(lastlModifiedSubject);
		boardDetailRes.setContent(lastModifiedContent);
		
		boardDetailRes.setLastModifierID(lastModifierID);
		boardDetailRes.setLastModifierNickName(lastModifierNickName);
		boardDetailRes.setLastModifierIP(lastModifierIP);
		boardDetailRes.setLastModifiedDate(lastModifedDate);						
		
		boardDetailRes.setAttachedFileCnt(attachedFileList.size());
		boardDetailRes.setAttachedFileList(attachedFileList);

		return boardDetailRes;
	}
}
