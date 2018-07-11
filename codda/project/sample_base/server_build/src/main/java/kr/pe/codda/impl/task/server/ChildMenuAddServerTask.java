package kr.pe.codda.impl.task.server;

import static kr.pe.codda.impl.jooq.tables.SbSeqTb.SB_SEQ_TB;
import static kr.pe.codda.impl.jooq.tables.SbSitemenuTb.SB_SITEMENU_TB;

import java.sql.Connection;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.ChildMenuAddReq.ChildMenuAddReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.SequenceType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class ChildMenuAddServerTask extends AbstractServerTask {
	@SuppressWarnings("unused")
	private void sendErrorOutputtMessageForCommit(String errorMessage,
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
		log.warn("{}, inObj={}", errorMessage, inputMessage.toString());
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
		log.warn("{}, inObj={}", errorMessage, inputMessage.toString());
		
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
		doWork(projectName, personalLoginManager, toLetterCarrier, (ChildMenuAddReq)inputMessage);
		
	}
	public void doWork(String projectName, PersonalLoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			ChildMenuAddReq childMenuAddReq) throws Exception {
		
		// FIXME!
		log.info(childMenuAddReq.toString());
		
		DataSource dataSource = DBCPManager.getInstance()
				.getBasicDataSource(ServerCommonStaticFinalVars.SB_CONNECTION_POOL_NAME);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			
			DSLContext create = DSL.using(conn, SQLDialect.MYSQL);
			
			Record menuSeqRecord = create.select(SB_SEQ_TB.SQ_VALUE)
			.from(SB_SEQ_TB)
			.where(SB_SEQ_TB.SQ_ID.eq(UByte.valueOf(SequenceType.MENU.getSequenceID())))
			.forUpdate().fetchOne();
			
			if (null == menuSeqRecord) {
				String errorMessage = new StringBuilder("메뉴 시퀀스 식별자[")
						.append(SequenceType.MENU.getSequenceID())
						.append("]의 시퀀스를 가져오는데 실패하였습니다").toString();
				sendErrorOutputMessageForRollback(errorMessage, conn, toLetterCarrier, childMenuAddReq);
				return;
			}
			
			UInteger childMenuNo = menuSeqRecord.getValue(SB_SEQ_TB.SQ_VALUE);			
			UInteger parentMenuNo = UInteger.valueOf(childMenuAddReq.getParentNo());
			
			Record1<UByte> parentMenuRecord = create.select(SB_SITEMENU_TB.DEPTH.add(1))
			.from(SB_SITEMENU_TB)
			.where(SB_SITEMENU_TB.MENU_NO.eq(parentMenuNo))
			.fetchOne();
			
			if (null == parentMenuRecord) {
				String errorMessage = new StringBuilder()
						.append("부모 메뉴[")
						.append(childMenuAddReq.getParentNo())
						.append("]가 존재하지 않습니다")
						.toString();
				sendErrorOutputMessageForRollback(errorMessage, conn, toLetterCarrier, childMenuAddReq);
				return;
			}
			
			UByte childMenuDepth = parentMenuRecord.getValue(SB_SITEMENU_TB.DEPTH);
			
			if (childMenuDepth.shortValue() > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
				String errorMessage = new StringBuilder()
						.append("자식 메뉴 깊이가 최대치에 도달하여 더 이상 추가할 수 없습니다")
						.toString();
				sendErrorOutputMessageForRollback(errorMessage, conn, toLetterCarrier, childMenuAddReq);
				return;
			}
			
			short orderSeq = create.selectCount()
			.from(SB_SITEMENU_TB)
			.where(SB_SITEMENU_TB.PARENT_NO.eq(UInteger.valueOf(childMenuAddReq.getParentNo())))
			.fetchOne(0, short.class);
			
			if (orderSeq > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
				String errorMessage = new StringBuilder()
						.append("자식 메뉴 갯수가 최대치에 도달하여 더 이상 추가할 수 없습니다")
						.toString();
				sendErrorOutputMessageForRollback(errorMessage, conn, toLetterCarrier, childMenuAddReq);
				return;
			}
			
			
			int childMenuInsertCount = create.insertInto(SB_SITEMENU_TB)
			.set(SB_SITEMENU_TB.MENU_NO, childMenuNo)
			.set(SB_SITEMENU_TB.PARENT_NO, parentMenuNo)
			.set(SB_SITEMENU_TB.DEPTH, childMenuDepth)
			.set(SB_SITEMENU_TB.ORDER_SQ, UByte.valueOf(orderSeq))
			.set(SB_SITEMENU_TB.MENU_NM, childMenuAddReq.getMenuName())
			.set(SB_SITEMENU_TB.LINK_URL, childMenuAddReq.getLinkURL())
			.execute();
			
			if (0 == childMenuInsertCount) {
				String errorMessage = new StringBuilder()
						.append("자식 메뉴 추가하는데 실패하였습니다")
						.toString();
				sendErrorOutputMessageForRollback(errorMessage, conn, toLetterCarrier, childMenuAddReq);
				return;
			}
			
			MessageResultRes messageResultRes = new MessageResultRes();
			messageResultRes.setTaskMessageID(childMenuAddReq.getMessageID());
			messageResultRes.setIsSuccess(true);		
			messageResultRes.setResultMessage(new StringBuilder()
					.append("보모 메뉴[")
					.append(childMenuAddReq.getParentNo())
					.append("]의 자식 메뉴[")
					.append(childMenuNo)
					.append("] 추가가 완료되었습니다").toString());
			
			sendSuccessOutputMessageForCommit(messageResultRes, conn, toLetterCarrier);
			
			log.info("자식 메뉴[부모 메뉴번호:{}, 번호:{}, 메뉴명:{}, URL:{}] 추가 완료",
					childMenuAddReq.getParentNo(),
					childMenuNo,
					childMenuAddReq.getMenuName(),
					childMenuAddReq.getLinkURL());
			return;			
		} catch (Exception e) {
			log.warn("unknown error", e);
			sendErrorOutputMessageForRollback("자식 메뉴 추가하는데 실패하였습니다", conn, toLetterCarrier, childMenuAddReq);
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
		
	}

}