package kr.pe.codda.impl.task.server;

import static kr.pe.codda.impl.jooq.tables.SbSeqTb.SB_SEQ_TB;
import static kr.pe.codda.impl.jooq.tables.SbSitemenuTb.SB_SITEMENU_TB;

import java.sql.Connection;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.ChildMenuAddReq.ChildMenuAddReq;
import kr.pe.codda.impl.message.ChildMenuAddRes.ChildMenuAddRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.SequenceType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class ChildMenuAddReqServerTask extends AbstractServerTask {
	
	
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
			AbstractMessage outputMessage = doWork(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, (ChildMenuAddReq)inputMessage);
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
			
			sendErrorOutputMessage("자식 메뉴 추가하는데 실패하였습니다", toLetterCarrier, inputMessage);
			return;
		}
		
	}
	public ChildMenuAddRes doWork(String dbcpName, ChildMenuAddReq childMenuAddReq) throws Exception {		
		// FIXME!
		log.info(childMenuAddReq.toString());
		
		UByte menuSequenceID = UByte.valueOf(SequenceType.MENU.getSequenceID());
		UInteger parentMenuNo = UInteger.valueOf(childMenuAddReq.getParentNo());
		
		DataSource dataSource = DBCPManager.getInstance()
				.getBasicDataSource(dbcpName);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			
			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(dbcpName));
			
			/** 자식 메뉴 추가에 따른 '메뉴 순서' 보장을 위한 lock */
			Record menuSeqRecord = create.select(SB_SEQ_TB.SQ_VALUE)
			.from(SB_SEQ_TB)
			.where(SB_SEQ_TB.SQ_ID.eq(menuSequenceID))
			.forUpdate().fetchOne();
			
			if (null == menuSeqRecord) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("메뉴 시퀀스 식별자[")
						.append(menuSequenceID)
						.append("]의 시퀀스를 가져오는데 실패하였습니다").toString();
				throw new ServerServiceException(errorMessage);
			}
			
			UInteger childMenuNo = menuSeqRecord.getValue(SB_SEQ_TB.SQ_VALUE);	
			
			if (childMenuNo.longValue() == UInteger.MAX_VALUE) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("메뉴 시퀀스 식별자[")
						.append(menuSequenceID)
						.append("]의 시퀀스가 최대치에 도달하였습니다").toString();
				throw new ServerServiceException(errorMessage);
			}
			
			int seqUpdateCnt = create.update(SB_SEQ_TB)
					.set(SB_SEQ_TB.SQ_VALUE, SB_SEQ_TB.SQ_VALUE.add(1))
					.where(SB_SEQ_TB.SQ_ID.eq(menuSequenceID))
				.execute();
			
			if (0 == seqUpdateCnt) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("메뉴 시퀀스 식별자[")
						.append(menuSequenceID)
						.append("]의 시퀀스 갱신하는데 실패하였습니다").toString();
				throw new ServerServiceException(errorMessage);
			}
			
			int numberOfMenu = create.selectCount().from(SB_SITEMENU_TB).fetchOne().value1();
			
			if (numberOfMenu >= CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = "메뉴 갯수가 최대치(=255)에 도달하여 더 이상 추가할 수 없습니다";
				throw new ServerServiceException(errorMessage);
			}
			
			
			Record3<UInteger, UByte, UByte> parentMenuRecord = create.select(SB_SITEMENU_TB.PARENT_NO, 
					SB_SITEMENU_TB.ORDER_SQ, 
					SB_SITEMENU_TB.DEPTH)
			.from(SB_SITEMENU_TB)
			.where(SB_SITEMENU_TB.MENU_NO.eq(parentMenuNo))
			.fetchOne();
			
			if (null == parentMenuRecord) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder()
						.append("부모 메뉴[")
						.append(parentMenuNo)
						.append("]가 존재하지 않습니다")
						.toString();
				throw new ServerServiceException(errorMessage);
			}
			
			UInteger parentParnetNo = parentMenuRecord.getValue(SB_SITEMENU_TB.PARENT_NO);
			UByte parentOrderSeq = parentMenuRecord.getValue(SB_SITEMENU_TB.ORDER_SQ);
			UByte parentDepth = parentMenuRecord.getValue(SB_SITEMENU_TB.DEPTH);
			
			UByte newOrderSeq = null;
			
			Record1<UByte> firstYoungerBrotherMenuReccordOfParentMenu = create.select(SB_SITEMENU_TB.ORDER_SQ.min().as(SB_SITEMENU_TB.ORDER_SQ)).from(SB_SITEMENU_TB)
			.where(SB_SITEMENU_TB.PARENT_NO.eq(parentParnetNo))
			.and(SB_SITEMENU_TB.DEPTH.eq(parentDepth))
			.and(SB_SITEMENU_TB.ORDER_SQ.gt(parentOrderSeq))
			.fetchOne();
			
			if (null == firstYoungerBrotherMenuReccordOfParentMenu || null == firstYoungerBrotherMenuReccordOfParentMenu.getValue(SB_SITEMENU_TB.ORDER_SQ)) {
				Record1<UByte>  lastOrderSeqMenuRecord = create.select(
						SB_SITEMENU_TB.ORDER_SQ.max().as(SB_SITEMENU_TB.ORDER_SQ))
				.from(SB_SITEMENU_TB.forceIndex("sb_sitemenu_idx"))				
				.fetchOne();
				
				newOrderSeq = UByte.valueOf(lastOrderSeqMenuRecord.value1().shortValue()+1);
			} else {
				newOrderSeq = firstYoungerBrotherMenuReccordOfParentMenu.getValue(SB_SITEMENU_TB.ORDER_SQ);
			}
			
			
			if (parentDepth.shortValue() >= CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder()
						.append("자식 메뉴 깊이가 최대치(=255)에 도달하여 더 이상 추가할 수 없습니다")
						.toString();
				throw new ServerServiceException(errorMessage);
			}
			
			
			create.update(SB_SITEMENU_TB)
			.set(SB_SITEMENU_TB.ORDER_SQ, SB_SITEMENU_TB.ORDER_SQ.add(1))
			.where(SB_SITEMENU_TB.ORDER_SQ.greaterOrEqual(newOrderSeq))
			.execute();
					
			
			int childMenuInsertCount = create.insertInto(SB_SITEMENU_TB)
			.set(SB_SITEMENU_TB.MENU_NO, childMenuNo)
			.set(SB_SITEMENU_TB.PARENT_NO, parentMenuNo)
			.set(SB_SITEMENU_TB.DEPTH, UByte.valueOf(parentDepth.shortValue() + 1))
			.set(SB_SITEMENU_TB.ORDER_SQ, newOrderSeq)
			.set(SB_SITEMENU_TB.MENU_NM, childMenuAddReq.getMenuName())
			.set(SB_SITEMENU_TB.LINK_URL, childMenuAddReq.getLinkURL())
			.execute();
			
			if (0 == childMenuInsertCount) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				
				String errorMessage = new StringBuilder()
						.append("자식 메뉴 추가하는데 실패하였습니다")
						.toString();
				throw new ServerServiceException(errorMessage);
			}
			
			try {
				conn.commit();
			} catch (Exception e) {
				log.warn("fail to commit");
			}
			
			log.info("자식 메뉴[부모 메뉴번호:{}, 번호:{}, 메뉴명:{}, URL:{}] 추가 완료",
					childMenuAddReq.getParentNo(),
					childMenuNo,
					childMenuAddReq.getMenuName(),
					childMenuAddReq.getLinkURL());
			
			ChildMenuAddRes childMenuAddRes = new ChildMenuAddRes();
			childMenuAddRes.setMenuNo(childMenuNo.longValue());
			childMenuAddRes.setOrderSeq(newOrderSeq.shortValue());
			
			return childMenuAddRes;
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