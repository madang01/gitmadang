package kr.pe.codda.impl.task.server;

import static kr.pe.codda.impl.jooq.tables.SbSeqTb.SB_SEQ_TB;

import java.sql.Connection;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.impl.message.SeqValueReq.SeqValueReq;
import kr.pe.codda.impl.message.SeqValueRes.SeqValueRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.SequenceType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class SeqValueReqServerTask extends AbstractServerTask {

	
	private void sendErrorOutputMessage(String errorMessage,			
			ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws InterruptedException {
		// log.warn("{}, inObj=", errorMessage, inputMessage.toString());
		
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
			AbstractMessage outputMessage = doService((SeqValueReq)inputMessage);
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
						
			sendErrorOutputMessage("시퀀스 값을 가져오는데 실패하였습니다", toLetterCarrier, inputMessage);
			return;
		}
	}

	
	public SeqValueRes doService(SeqValueReq seqValueReq) throws Exception {		
		try {
			SequenceType.valueOf(seqValueReq.getSeqTypeId());
		} catch(IllegalArgumentException e) {
			String errorMessage = "요청한 시퀀스 종류 식별자가 잘못되었습니다";
			//sendErrorOutputMessage(errorMessage, toLetterCarrier, seqValueReq);
			throw new ServerServiceException(errorMessage);
		}
		
		short wantedSize = seqValueReq.getWantedSize();
		if (wantedSize < 0 || wantedSize > CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
			String errorMessage = "원하는 시퀀스 크기는 usinged byte 형 범위를 가져야 합니다";
			throw new ServerServiceException(errorMessage);
		}
		
		DataSource dataSource = DBCPManager.getInstance()
				.getBasicDataSource(ServerCommonStaticFinalVars.SB_CONNECTION_POOL_NAME);
		
		
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			
			DSLContext create = DSL.using(conn, SQLDialect.MYSQL);			
			
			Record resultOfSeqManager = create.select(
					SB_SEQ_TB.SQ_VALUE)
				.from(SB_SEQ_TB)
				.where(SB_SEQ_TB.SQ_ID.eq(UByte.valueOf(seqValueReq.getSeqTypeId())))
				.forUpdate().fetchOne();
			
			if (null == resultOfSeqManager) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("시퀀스 타입[")
						.append(seqValueReq.getSeqTypeId())
						.append("]의 시퀀스를 가져오는데 실패하였습니다").toString();
				/*sendErrorOutputMessageForRollback(errorMessage, conn, toLetterCarrier, seqValueReq);
				return;*/
				throw new ServerServiceException(errorMessage);
			}
			
			// update SB_SEQ_TB set sq_value = sq_value + #{wantedSize} where sq_type_id = #{seqTypeId}
			
			int countOfUpdate = create.update(SB_SEQ_TB)
					.set(SB_SEQ_TB.SQ_VALUE, SB_SEQ_TB.SQ_VALUE.add(seqValueReq.getWantedSize()))
					.where(SB_SEQ_TB.SQ_ID.eq(UByte.valueOf(seqValueReq.getSeqTypeId())))
				.execute();
			
			if (0 == countOfUpdate) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("시퀀스 타입[")
						.append(seqValueReq.getSeqTypeId())
						.append("]의 시퀀스 갱신이 실패하였습니다").toString();
				/*sendErrorOutputMessageForRollback(errorMessage, conn, toLetterCarrier, seqValueReq);
				return;*/
				throw new ServerServiceException(errorMessage);
			}
			
			conn.commit();
			
			long seqValue = resultOfSeqManager.get(SB_SEQ_TB.SQ_VALUE).longValue();
			
			SeqValueRes seqValueRes = new SeqValueRes();
			seqValueRes.setSeqValue(seqValue);
			seqValueRes.setWantedSize(seqValueReq.getWantedSize());
			
			//sendSuccessOutputMessageForCommit(seqValueRes, conn, toLetterCarrier);
			return seqValueRes;
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
			
			sendErrorOutputMessageForRollback("시퀀스 값을 가져오는데 실패하였습니다", conn, toLetterCarrier, seqValueReq);
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
	}
}
