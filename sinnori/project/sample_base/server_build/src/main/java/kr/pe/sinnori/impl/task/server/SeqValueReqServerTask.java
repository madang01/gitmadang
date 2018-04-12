package kr.pe.sinnori.impl.task.server;

import static kr.pe.sinnori.impl.jooq.tables.SbSeqTb.SB_SEQ_TB;

import java.sql.Connection;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;

import kr.pe.sinnori.common.message.AbstractMessage;
import kr.pe.sinnori.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.sinnori.impl.message.SeqValueReq.SeqValueReq;
import kr.pe.sinnori.impl.message.SeqValueRes.SeqValueRes;
import kr.pe.sinnori.server.PersonalLoginManagerIF;
import kr.pe.sinnori.server.dbcp.DBCPManager;
import kr.pe.sinnori.server.lib.SequenceType;
import kr.pe.sinnori.server.lib.ServerCommonStaticFinalVars;
import kr.pe.sinnori.server.task.AbstractServerTask;
import kr.pe.sinnori.server.task.ToLetterCarrier;

public class SeqValueReqServerTask extends AbstractServerTask {
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
	
	private void sendSuccessOutputMessageForCommit(AbstractMessage outputMessage,Connection conn,
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
		doWork(projectName, personalLoginManager, toLetterCarrier, (SeqValueReq)inputMessage);
	}

	
	public void doWork(String projectName, PersonalLoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			SeqValueReq seqValueReq) throws Exception {		
		DataSource dataSource = DBCPManager.getInstance()
				.getBasicDataSource(ServerCommonStaticFinalVars.SB_CONNECTION_POOL_NAME);
		
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			
			DSLContext create = DSL.using(conn, SQLDialect.MYSQL);			
			
			try {
				SequenceType.valueOf(seqValueReq.getSeqTypeId());
			} catch(IllegalArgumentException e) {
				String errorMessage = "요청한 시퀀스 종류 식별자가 잘못되었습니다";
				sendErrorOutputMessage(errorMessage, toLetterCarrier, seqValueReq);
			}
			
			/*
			select 
			sq_value as seqValue, #{wantedSize}
		from SB_SEQ_TB
		where sq_type_id = #{seqTypeId} for update*/
			
			Record resultOfSeqManager = create.select(
					SB_SEQ_TB.SQ_VALUE)
				.from(SB_SEQ_TB)
				.where(SB_SEQ_TB.SQ_ID.eq(UByte.valueOf(seqValueReq.getSeqTypeId())))
				.forUpdate().fetchOne();
			
			if (null == resultOfSeqManager) {
				String errorMessage = new StringBuilder("시퀀스 타입[")
						.append(seqValueReq.getSeqTypeId())
						.append("]의 시퀀스를 가져오는데 실패하였습니다").toString();
				sendErrorOutputMessageForRollback(errorMessage, conn, toLetterCarrier, seqValueReq);
				return;
			}
			
			// update SB_SEQ_TB set sq_value = sq_value + #{wantedSize} where sq_type_id = #{seqTypeId}
			
			int countOfUpdate = create.update(SB_SEQ_TB)
					.set(SB_SEQ_TB.SQ_VALUE, SB_SEQ_TB.SQ_VALUE.add(seqValueReq.getWantedSize()))
					.where(SB_SEQ_TB.SQ_ID.eq(UByte.valueOf(seqValueReq.getSeqTypeId())))
				.execute();
			
			if (0 == countOfUpdate) {
				String errorMessage = new StringBuilder("시퀀스 타입[")
						.append(seqValueReq.getSeqTypeId())
						.append("]의 시퀀스 갱신이 실패하였습니다").toString();
				sendErrorOutputMessageForRollback(errorMessage, conn, toLetterCarrier, seqValueReq);
				return;
			}
			
			long seqValue = resultOfSeqManager.get(SB_SEQ_TB.SQ_VALUE).longValue();
			
			SeqValueRes seqValueRes = new SeqValueRes();
			seqValueRes.setSeqValue(seqValue);
			seqValueRes.setWantedSize(seqValueReq.getWantedSize());
			
			sendSuccessOutputMessageForCommit(seqValueRes, conn, toLetterCarrier);
		} catch (Exception e) {
			log.warn("unknown error", e);
			
			sendErrorOutputMessageForRollback("시퀀스 값을 가져오는데 실패하였습니다", conn, toLetterCarrier, seqValueReq);
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
