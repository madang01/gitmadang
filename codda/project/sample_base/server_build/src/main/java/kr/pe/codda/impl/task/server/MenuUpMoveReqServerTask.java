package kr.pe.codda.impl.task.server;

import static kr.pe.codda.impl.jooq.tables.SbSeqTb.SB_SEQ_TB;
import static kr.pe.codda.impl.jooq.tables.SbSitemenuTb.SB_SITEMENU_TB;

import java.sql.Connection;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record5;
import org.jooq.SQLDialect;
import org.jooq.exception.TooManyRowsException;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;

import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.MenuUpMoveReq.MenuUpMoveReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.SequenceType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class MenuUpMoveReqServerTask extends AbstractServerTask {
	private void sendErrorOutputMessage(String errorMessage,			
			ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws InterruptedException {		
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
			AbstractMessage outputMessage = doService((MenuUpMoveReq)inputMessage);
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
			
			
			sendErrorOutputMessage("메뉴 상단 이동이 실패하였습니다", toLetterCarrier, inputMessage);
			return;
		}
	}
	
	public MessageResultRes doService(MenuUpMoveReq menuUpMoveReq) throws Exception {
		// FIXME!
		log.info(menuUpMoveReq.toString());
		
		DataSource dataSource = DBCPManager.getInstance()
				.getBasicDataSource(ServerCommonStaticFinalVars.SB_CONNECTION_POOL_NAME);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			
			DSLContext create = DSL.using(conn, SQLDialect.MYSQL);
			
			/** '메뉴 순서' 를 위한 lock */
			Record menuSeqRecord = create.select(SB_SEQ_TB.SQ_VALUE)
			.from(SB_SEQ_TB)
			.where(SB_SEQ_TB.SQ_ID.eq(UByte.valueOf(SequenceType.MENU.getSequenceID())))
			.forUpdate().fetchOne();
			
			if (null == menuSeqRecord) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("메뉴 시퀀스 식별자[")
						.append(SequenceType.MENU.getSequenceID())
						.append("]의 시퀀스를 가져오는데 실패하였습니다").toString();
				// sendErrorOutputMessageForRollback(errorMessage, conn, toLetterCarrier, menuUpMoveReq);
				
				
				
				throw new ServerServiceException(errorMessage);
			}
			
			
			UInteger sourceMenuNo = UInteger.valueOf(menuUpMoveReq.getMenuNo());
			
			/** 상단으로 이동 요청한 메뉴 레코드 가져오기 */
			Record5<UInteger, UByte, UByte, String, String> sourceMenuRecord = create.select(SB_SITEMENU_TB.PARENT_NO,
					SB_SITEMENU_TB.DEPTH,
					SB_SITEMENU_TB.ORDER_SQ,
					SB_SITEMENU_TB.MENU_NM, SB_SITEMENU_TB.LINK_URL)
			.from(SB_SITEMENU_TB)
			.where(SB_SITEMENU_TB.MENU_NO.eq(sourceMenuNo))
			.fetchOne();
			
			if (null == sourceMenuRecord) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder()
						.append("지정한 메뉴[")
						.append(menuUpMoveReq.getMenuNo())
						.append("]가 존재하지 않습니다").toString();
				
				throw new ServerServiceException(errorMessage);
			}
			
			/** 상단으로 이동 요청한 메뉴가 최상단 즉 메뉴 순서가 0이면 에러 출력 */
			if (0 == sourceMenuRecord.getValue(SB_SITEMENU_TB.ORDER_SQ).shortValue()) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder()
						.append("최상단 메뉴[")
						.append(menuUpMoveReq.getMenuNo())
						.append("]는 상단으로 이동할 수 없습니다").toString();
				
				
				throw new ServerServiceException(errorMessage);
			}
			
			UByte sourceMenuOrderSeq  = sourceMenuRecord.getValue(SB_SITEMENU_TB.ORDER_SQ); 
			UByte upperMenuOrderSeq = UByte.valueOf(sourceMenuOrderSeq.shortValue() - 1);
			
			
			/** 상단으로 이동 요청한 메뉴 보다 한칸 높은 메뉴 레코드 가져오기 */
			Record1<UInteger>  uppperMenuRecord = null;
			try {
				uppperMenuRecord = create.select(
						SB_SITEMENU_TB.MENU_NO)
				.from(SB_SITEMENU_TB)
				.where(SB_SITEMENU_TB.PARENT_NO.eq(sourceMenuRecord.getValue(SB_SITEMENU_TB.PARENT_NO)))
				.and(SB_SITEMENU_TB.ORDER_SQ.eq(upperMenuOrderSeq))
				.fetchOne();
			} catch(TooManyRowsException e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder()
						.append("지정한 메뉴[")
						.append(menuUpMoveReq.getMenuNo())
						.append("]보다 한칸 높은 메뉴가 다수 존재합니다").toString();				
				
				throw new ServerServiceException(errorMessage);
			}
			
			if (null == uppperMenuRecord) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder()
						.append("지정한 메뉴[")
						.append(menuUpMoveReq.getMenuNo())
						.append("]보다 한칸 높은 메뉴가 존재하지 않습니다").toString();
				
				
				throw new ServerServiceException(errorMessage);
			}
			
			UInteger upperMenuNo = uppperMenuRecord.getValue(SB_SITEMENU_TB.MENU_NO);
			
			/**
			 * 요청한 메뉴의 순서와 한칸 높은 메뉴의 순서를 서로 바꾸기
			 */
			
			/** 요청한 메뉴의 순서를 한칸 높은 메뉴의 순서로 바꾸기, 즉 요청한 메뉴의 순서의 한칸 올리기 */
			int sourceMenuUpdateCount = create.update(SB_SITEMENU_TB)
			.set(SB_SITEMENU_TB.ORDER_SQ, upperMenuOrderSeq)
			.where(SB_SITEMENU_TB.MENU_NO.eq(sourceMenuNo))
			.execute();
			
			if (0 == sourceMenuUpdateCount) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder()
						.append("메뉴[")
						.append(menuUpMoveReq.getMenuNo())
						.append("] 순서를 한칸 위로 조정하는데  실패하였습니다").toString();
				
				
				throw new ServerServiceException(errorMessage);
			}
			
			/** 한칸 높은 메뉴의 순서를 요청한 메뉴 순서로 바꾸기, 즉 한칸 높은 메뉴의 순서를 한칸 내리기  */
			int upperMenuUpdateCount = create.update(SB_SITEMENU_TB)
					.set(SB_SITEMENU_TB.ORDER_SQ, sourceMenuOrderSeq)
					.where(SB_SITEMENU_TB.MENU_NO.eq(upperMenuNo))
					.execute();
					
			if (0 == upperMenuUpdateCount) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder()
						.append("메뉴[")
						.append(menuUpMoveReq.getMenuNo())
						.append("]의 한칸 위 메뉴[")
						.append(upperMenuNo)
						.append("] 순서를 조정하는데  실패하였습니다").toString();
				
				
				throw new ServerServiceException(errorMessage);
			}
			
			try {
				conn.commit();
			} catch (Exception e) {
				log.warn("fail to commit");
			}
			
			// sendSuccessOutputMessageForCommit(messageResultRes, conn, toLetterCarrier);
			
			log.info("메뉴[번호:{}, 순서:{}] <--상단 메뉴 이동에 따른 순서 뒤바뀜--> 위치가 뒤 바뀐 메뉴[번호:{}, 순서:{}]",
					menuUpMoveReq.getMenuNo(),
					sourceMenuOrderSeq,
					upperMenuNo,
					upperMenuOrderSeq);
			
			MessageResultRes messageResultRes = new MessageResultRes();
			messageResultRes.setTaskMessageID(menuUpMoveReq.getMessageID());
			messageResultRes.setIsSuccess(true);		
			messageResultRes.setResultMessage(new StringBuilder()
					.append("메뉴[")
					.append(menuUpMoveReq.getMenuNo())
					.append("]의 상단 이동 처리가 완료되었습니다").toString());
			
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
			
			log.warn("unknown error", e);			
			
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
