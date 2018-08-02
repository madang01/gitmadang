package kr.pe.codda.impl.task.server;

import static kr.pe.codda.impl.jooq.tables.SbBoardTb.SB_BOARD_TB;
import static kr.pe.codda.impl.jooq.tables.SbMemberTb.SB_MEMBER_TB;

import java.sql.Connection;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.Record3;
import org.jooq.SQLDialect;
import org.jooq.UpdateSetMoreStep;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;

import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.jooq.tables.records.SbBoardTbRecord;
import kr.pe.codda.impl.message.BoardModifyReq.BoardModifyReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.BoardType;
import kr.pe.codda.server.lib.MemberStateType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class BoardModifyReqServerTask extends AbstractServerTask {
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
	
	/*private void sendSuccessOutputMessageForCommit(AbstractMessage outputMessage, 
			Connection conn,
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
			AbstractMessage outputMessage = doService((BoardModifyReq)inputMessage);
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
	
	public MessageResultRes doService(BoardModifyReq boardModifyReq) throws Exception {
		// FIXME!
		log.info(boardModifyReq.toString());
		
		try {
			BoardType.valueOf(boardModifyReq.getBoardId());
		} catch(IllegalArgumentException e) {
			/*log.warn(e.getMessage(), e);
			sendErrorOutputMessage("잘못된 게시판 종류입니다", toLetterCarrier, boardModifyReq);
			return;*/
			String errorMessage = "잘못된 게시판 식별자입니다";
			throw new ServerServiceException(errorMessage);
		}
		
		try {
			ValueChecker.checkValidSubject(boardModifyReq.getSubject());
		} catch(RuntimeException e) {
			/*log.warn(e.getMessage(), e);
			sendErrorOutputMessage(e.getMessage(), toLetterCarrier, boardModifyReq);
			return;*/
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}
		
		try {
			ValueChecker.checkValidContent(boardModifyReq.getContent());
		} catch(RuntimeException e) {
			/*log.warn(e.getMessage(), e);
			sendErrorOutputMessage(e.getMessage(), toLetterCarrier, boardModifyReq);
			return;*/
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}
		
		try {
			ValueChecker.checkValidWriterId(boardModifyReq.getUserId());
		} catch(RuntimeException e) {
			/*log.warn(e.getMessage(), e);
			sendErrorOutputMessage(e.getMessage(), toLetterCarrier, boardModifyReq);
			return;*/
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}
		
		
		try {
			ValueChecker.checkValidIP(boardModifyReq.getIp());
		} catch(RuntimeException e) {
			/*log.warn(e.getMessage(), e);
			sendErrorOutputMessage(e.getMessage(), toLetterCarrier, boardModifyReq);
			return;*/
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
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
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("해당 게시글[")
						.append(boardModifyReq.getBoardNo())
						.append("이 존재 하지 않습니다").toString();
				/*sendErrorOutputMessageForCommit(errorMessage, conn, toLetterCarrier, boardModifyReq);
				return;*/
				throw new ServerServiceException(errorMessage);
			}
			
			short boardTypeValue = boardRecord.get(SB_BOARD_TB.BOARD_ID).shortValue();
			
			try {
				@SuppressWarnings("unused")
				BoardType boardType = BoardType.valueOf(boardTypeValue);
			} catch(IllegalArgumentException e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("해당 게시글[")
						.append(boardModifyReq.getBoardNo())
						.append("의 게시판 식별자[")
						.append(boardTypeValue)
						.append("]가 잘못되어 있습니다").toString();
				/*sendErrorOutputMessageForCommit(errorMessage, conn, toLetterCarrier, boardModifyReq);
				return;*/
				throw new ServerServiceException(errorMessage);
			}
			
			if (boardModifyReq.getBoardId() != boardTypeValue) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("해당 게시글[")
						.append(boardModifyReq.getBoardNo())
						.append("의 게시판 식별자[")
						.append(boardTypeValue)
						.append("]와 입력 메시지상의 게시판 식별자[")
						.append(boardModifyReq.getBoardId())
						.append("]가 상이합니다").toString();
				/*sendErrorOutputMessageForCommit(errorMessage, conn, toLetterCarrier, boardModifyReq);
				return;*/
				throw new ServerServiceException(errorMessage);
			}
			
			
			String writerID = boardRecord.get(SB_BOARD_TB.WRITER_ID);
			
			if (! boardModifyReq.getUserId().equals(writerID)) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("해당 게시글[")
						.append(boardModifyReq.getBoardNo())
						.append("]의 작성자[")
						.append(boardModifyReq.getUserId())
						.append("]만이 수정 할 수 있습니다").toString();
				/*sendErrorOutputMessageForCommit(errorMessage, conn, toLetterCarrier, boardModifyReq);
				return;*/
				throw new ServerServiceException(errorMessage);
			}
			
			String nativeMemberStateType = boardRecord.get(SB_MEMBER_TB.MEMBER_ST);
			MemberStateType memberStateType =  MemberStateType.valueOf(nativeMemberStateType, false);
			if (! memberStateType.equals(MemberStateType.OK)) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("비 정상 회원[")
						.append(memberStateType.getName())
						.append("]은 해당 게시글[")
						.append(boardModifyReq.getBoardNo())
						.append("는 접근 할 수 없습니다").toString();				
				/*sendErrorOutputMessageForCommit(errorMessage, conn, toLetterCarrier, boardModifyReq);
				return;*/
				
				throw new ServerServiceException(errorMessage);
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
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = "게시글 DB 수정하는데 실패하였습니다";
				/*sendErrorOutputMessageForRollback(errorMessage, conn, toLetterCarrier, boardModifyReq);
				return;*/
				throw new ServerServiceException(errorMessage);
			}
			
			conn.commit();
			
			
			MessageResultRes messageResultRes = new MessageResultRes();
			messageResultRes.setTaskMessageID(boardModifyReq.getMessageID());
			messageResultRes.setIsSuccess(true);		
			messageResultRes.setResultMessage("게시글 수정이 성공하였습니다");
			
			//sendSuccessOutputMessageForCommit(messageResultRes, conn, toLetterCarrier);
			
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
			
			/*log.warn("unknown error", e);
			
			sendErrorOutputMessageForRollback("2.게시글 수정이 실패하였습니다", conn, toLetterCarrier, boardModifyReq);
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
