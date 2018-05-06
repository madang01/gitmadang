package kr.pe.sinnori.impl.task.server;

import static kr.pe.sinnori.impl.jooq.tables.SbBoardTb.SB_BOARD_TB;
import static kr.pe.sinnori.impl.jooq.tables.SbMemberTb.SB_MEMBER_TB;

import java.sql.Connection;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.Record3;
import org.jooq.SQLDialect;
import org.jooq.UpdateSetMoreStep;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;

import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.impl.jooq.tables.records.SbBoardTbRecord;
import kr.pe.sinnori.impl.message.BoardModifyReq.BoardModifyReq;
import kr.pe.sinnori.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.sinnori.server.PersonalLoginManagerIF;
import kr.pe.sinnori.server.dbcp.DBCPManager;
import kr.pe.sinnori.server.lib.BoardType;
import kr.pe.sinnori.server.lib.MemberStateType;
import kr.pe.sinnori.server.lib.ServerCommonStaticFinalVars;
import kr.pe.sinnori.server.lib.ValueChecker;
import kr.pe.sinnori.server.task.AbstractServerTask;
import kr.pe.sinnori.server.task.ToLetterCarrier;

public class BoardModifyReqServerTask extends AbstractServerTask {
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
		
		doWork(projectName, personalLoginManager, toLetterCarrier, (BoardModifyReq)inputMessage);
	}
	
	public void doWork(String projectName, 
			PersonalLoginManagerIF personalLoginManager, 
			ToLetterCarrier toLetterCarrier,
			BoardModifyReq boardModifyReq) throws Exception {
		// FIXME!
		log.info(boardModifyReq.toString());
		
		try {
			BoardType.valueOf(boardModifyReq.getBoardId());
		} catch(IllegalArgumentException e) {
			log.warn(e.getMessage(), e);
			sendErrorOutputMessage("잘못된 게시판 종류입니다", toLetterCarrier, boardModifyReq);
			return;
		}
		
		try {
			ValueChecker.checkValidSubject(boardModifyReq.getSubject());
		} catch(RuntimeException e) {
			log.warn(e.getMessage(), e);
			sendErrorOutputMessage(e.getMessage(), toLetterCarrier, boardModifyReq);
			return;
		}
		
		try {
			ValueChecker.checkValidContent(boardModifyReq.getContent());
		} catch(RuntimeException e) {
			log.warn(e.getMessage(), e);
			sendErrorOutputMessage(e.getMessage(), toLetterCarrier, boardModifyReq);
			return;
		}
		
		try {
			ValueChecker.checkValidWriterId(boardModifyReq.getUserId());
		} catch(RuntimeException e) {
			log.warn(e.getMessage(), e);
			sendErrorOutputMessage(e.getMessage(), toLetterCarrier, boardModifyReq);
			return;
		}
		
		
		try {
			ValueChecker.checkValidIP(boardModifyReq.getIp());
		} catch(RuntimeException e) {
			log.warn(e.getMessage(), e);
			sendErrorOutputMessage(e.getMessage(), toLetterCarrier, boardModifyReq);
			return;
		}
		
		DataSource dataSource = DBCPManager.getInstance()
				.getBasicDataSource(ServerCommonStaticFinalVars.SB_CONNECTION_POOL_NAME);
		
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			
			DSLContext create = DSL.using(conn, SQLDialect.MYSQL);
			
			/*select 
			writer_id as writerId, board_id as boardId, attach_id as attachId
		from SB_BOARD_TB 
		where board_no=#{boardNo} for update*/			
			
			Record3<UByte, String, String> boardRecord = create.select(SB_BOARD_TB.BOARD_ID
					, SB_BOARD_TB.WRITER_ID					
					, SB_MEMBER_TB.MEMBER_ST)
			.from(SB_BOARD_TB)
			.join(SB_MEMBER_TB)
			.on(SB_BOARD_TB.WRITER_ID.eq(SB_MEMBER_TB.USER_ID))
			.where(SB_BOARD_TB.BOARD_NO.eq(UInteger.valueOf(boardModifyReq.getBoardNo())))
			.forUpdate().fetchOne();
			
			if (null == boardRecord) {
				String errorMessage = new StringBuilder("해당 게시글[")
						.append(boardModifyReq.getBoardNo())
						.append("이 존재 하지 않습니다").toString();
				sendErrorOutputMessageForCommit(errorMessage, conn, toLetterCarrier, boardModifyReq);
				return;
			}
			
			short boardTypeValue = boardRecord.get(SB_BOARD_TB.BOARD_ID).shortValue();
			
			try {
				@SuppressWarnings("unused")
				BoardType boardType = BoardType.valueOf(boardTypeValue);
			} catch(IllegalArgumentException e) {
				String errorMessage = new StringBuilder("해당 게시글[")
						.append(boardModifyReq.getBoardNo())
						.append("의 게시판 식별자[")
						.append(boardTypeValue)
						.append("]가 잘못되어 있습니다").toString();
				sendErrorOutputMessageForCommit(errorMessage, conn, toLetterCarrier, boardModifyReq);
				return;
			}
			
			if (boardModifyReq.getBoardId() != boardTypeValue) {
				String errorMessage = new StringBuilder("해당 게시글[")
						.append(boardModifyReq.getBoardNo())
						.append("의 게시판 식별자[")
						.append(boardTypeValue)
						.append("]와 입력 메시지상의 게시판 식별자[")
						.append(boardModifyReq.getBoardId())
						.append("]가 상이합니다").toString();
				sendErrorOutputMessageForCommit(errorMessage, conn, toLetterCarrier, boardModifyReq);
				return;
			}
			
			
			String writerID = boardRecord.get(SB_BOARD_TB.WRITER_ID);
			
			if (! boardModifyReq.getUserId().equals(writerID)) {
				String errorMessage = new StringBuilder("해당 게시글[")
						.append(boardModifyReq.getBoardNo())
						.append("]의 작성자[")
						.append(boardModifyReq.getUserId())
						.append("]만이 수정 할 수 있습니다").toString();
				sendErrorOutputMessageForCommit(errorMessage, conn, toLetterCarrier, boardModifyReq);
				return;
			}
			
			String nativeMemberStateType = boardRecord.get(SB_MEMBER_TB.MEMBER_ST);
			MemberStateType memberStateType =  MemberStateType.valueOf(nativeMemberStateType, false);
			if (! memberStateType.equals(MemberStateType.OK)) {
				String errorMessage = new StringBuilder("비 정상 회원[")
						.append(memberStateType.getName())
						.append("]은 해당 게시글[")
						.append(boardModifyReq.getBoardNo())
						.append("는 접근 할 수 없습니다").toString();				
				sendErrorOutputMessageForCommit(errorMessage, conn, toLetterCarrier, boardModifyReq);
				return;
			}
			
			/*
			 update SB_BOARD_TB set subject=#{subject}, content=#{content}, attach_id=#{attachId}, ip=#{ip}, 
			 mod_dt=sysdate() where board_no=#{boardNo}
			 */
			
			UpdateSetMoreStep<SbBoardTbRecord> boardUpdateSetMoreStep = create.update(SB_BOARD_TB)
					.set(SB_BOARD_TB.SUBJECT, boardModifyReq.getSubject())
					.set(SB_BOARD_TB.CONTENT, boardModifyReq.getContent())
					.set(SB_BOARD_TB.IP, boardModifyReq.getIp());			
			
			int countOfUpdate;
			
			if (0 == boardModifyReq.getAttachId()) {
				countOfUpdate = boardUpdateSetMoreStep.set(SB_BOARD_TB.ATTACH_ID, (UInteger)null)
						.where(SB_BOARD_TB.BOARD_NO.eq(UInteger.valueOf(boardModifyReq.getBoardNo())))
						.execute();
			} else {
				countOfUpdate = boardUpdateSetMoreStep.set(SB_BOARD_TB.ATTACH_ID, UInteger.valueOf(boardModifyReq.getAttachId()))
						.where(SB_BOARD_TB.BOARD_NO.eq(UInteger.valueOf(boardModifyReq.getBoardNo())))
						.execute();
			}
			
			if (0 == countOfUpdate) {
				String errorMessage = "1.게시글 수정이 실패하였습니다";
				sendErrorOutputMessageForRollback(errorMessage, conn, toLetterCarrier, boardModifyReq);
				return;
			}
			
			
			MessageResultRes messageResultRes = new MessageResultRes();
			messageResultRes.setTaskMessageID(boardModifyReq.getMessageID());
			messageResultRes.setIsSuccess(true);		
			messageResultRes.setResultMessage("게시글 수정이 성공하였습니다");
			
			sendSuccessOutputMessageForCommit(messageResultRes, conn, toLetterCarrier);
			
		} catch (Exception e) {
			log.warn("unknown error", e);
			
			sendErrorOutputMessageForRollback("2.게시글 수정이 실패하였습니다", conn, toLetterCarrier, boardModifyReq);
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
		
		/*SqlSessionFactory sqlSessionFactory = MybatisSqlSessionFactoryManger.getInstance()
				.getSqlSessionFactory(ServerCommonStaticFinalVars.SB_CONNECTION_POOL_NAME);
		
		SqlSession session = sqlSessionFactory.openSession(false);
		
		try {		
			HashMap<String, Object> boardModifyHash = session.selectOne("getBoardModifyInLock", boardModifyReq);
			if (null == boardModifyHash) {
				session.rollback();
				String errorMessage = String.format("게시판[%d]에서 글[%d]이 존재하지 않습니다.", 
						boardModifyReq.getBoardId(), boardModifyReq.getBoardNo());
				messageResultOutObj.setResultMessage(errorMessage);
				
				log.warn("{}, userId={}, ip={}", errorMessage, boardModifyReq.getUserId(), boardModifyReq.getUserId());
			} else {
				// FIXME!
				log.info("boardModifyHash={}", boardModifyHash.toString());
				
				if (boardModifyReq.getBoardId() != (Long)boardModifyHash.get("boardId")) {
					session.commit();
					String errorMessage = String.format("게시판 식별자[%d]와 파라미터로 넘어온 게시판 식별자[%d]가 상이합니다.", 
							(Long)boardModifyHash.get("boardId"), boardModifyReq.getBoardId());
					messageResultOutObj.setResultMessage(errorMessage);
					
					log.warn("{}, userId={}, ip={}", errorMessage, boardModifyReq.getUserId(), boardModifyReq.getIp());
				} else {
						
					if (boardModifyReq.getUserId().equals((String)boardModifyHash.get("writerId"))) {
						Long attachId = (Long)boardModifyHash.get("attachId");
						if (null != attachId && attachId != boardModifyReq.getAttachId()) {
							session.commit();
							String errorMessage = String.format("기 등록된 게시판 업로드 식별자[%d]와 파라미터 게시판 업로드 식별자가[%s]가 상이합니다.", 
									attachId, boardModifyReq.getAttachId());
							log.warn("{}, userId={}, ip={}", errorMessage, boardModifyReq.getUserId(), boardModifyReq.getIp());
							
							messageResultOutObj.setResultMessage(errorMessage);
						} else {
							int resultOfUpdate = session.update("updateBoard", boardModifyReq);						
							if (resultOfUpdate > 0) {
								session.commit();
								messageResultOutObj.setIsSuccess(true);
								messageResultOutObj.setResultMessage("게시판 글 수정이 성공하였습니다.");
							} else {
								session.rollback();
								messageResultOutObj.setResultMessage("1.게시판 글 수정이 실패하였습니다.");
							}
						}					
						
					} else {
						session.commit();
						
						String errorMessage = String.format("게시판 작성자[%s]와 로그인 아이디[%s]가 상이합니다.", 
								(String)boardModifyHash.get("writerId"), boardModifyReq.getUserId());
						log.warn("{}, ip={}", errorMessage, boardModifyReq.getIp());
						
						messageResultOutObj.setResultMessage(errorMessage);					
					}
				}			
			}			
			
		} catch(Exception e) {
			session.rollback();
			log.warn("unknown error", e);			
			
			messageResultOutObj.setResultMessage("2.게시판 글 수정이 실패하였습니다.");
			toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
			return;
		} finally {
			session.close();
		}
		
		toLetterCarrier.addSyncOutputMessage(messageResultOutObj);
		return;*/
		
	}
}
