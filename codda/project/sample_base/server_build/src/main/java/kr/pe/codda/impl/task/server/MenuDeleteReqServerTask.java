package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbSeqTb.SB_SEQ_TB;
import static kr.pe.codda.jooq.tables.SbSitemenuTb.SB_SITEMENU_TB;

import java.sql.Connection;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;

import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.MenuDeleteReq.MenuDeleteReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.PermissionType;
import kr.pe.codda.server.lib.SequenceType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class MenuDeleteReqServerTask extends AbstractServerTask {	
	
	public MenuDeleteReqServerTask() throws DynamicClassCallException {
		super();
	}

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
			AbstractMessage outputMessage = doWork(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, (MenuDeleteReq)inputMessage);
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

			sendErrorOutputMessage("메뉴 삭제하는데 실패하였습니다", toLetterCarrier, inputMessage);
			return;
		}
	}
	
	public MessageResultRes doWork(String dbcpName, MenuDeleteReq menuDeleteReq) throws Exception {
		// FIXME!
		log.info(menuDeleteReq.toString());
		
		final UByte menuSequenceID = SequenceType.MENU.getSequenceID();
		DataSource dataSource = DBCPManager.getInstance()
				.getBasicDataSource(dbcpName);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			
			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(dbcpName));
			
			ServerDBUtil.checkUserAccessRights(conn, create, log, "메뉴 삭제 서비스", PermissionType.ADMIN, menuDeleteReq.getRequestedUserID());
			
			/** 삭제에 따른 '메뉴 순서' 조정을  위한 lock */
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
			
			
			Record1<UByte> deleteMenuRecord =  create.select(SB_SITEMENU_TB.ORDER_SQ)
			.from(SB_SITEMENU_TB)
			.where(SB_SITEMENU_TB.MENU_NO.eq(UInteger.valueOf(menuDeleteReq.getMenuNo()))).fetchOne();
			
			if (null == deleteMenuRecord) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder()
						.append("삭제할 메뉴[")
						.append(menuDeleteReq.getMenuNo())
						.append("]가 존재하지 않습니다").toString();
				throw new ServerServiceException(errorMessage);
			}
			
			boolean isParentMenuRecord = create.fetchExists(create.selectOne()
					.from(SB_SITEMENU_TB)
					.where(SB_SITEMENU_TB.PARENT_NO.eq(UInteger.valueOf(menuDeleteReq.getMenuNo()))));
			
			if (isParentMenuRecord) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder()
						.append("자식이 있는 메뉴[")
						.append(menuDeleteReq.getMenuNo())
						.append("]는 삭제 할 수 없습니다").toString();
				throw new ServerServiceException(errorMessage);
			}
			
			
			int menuDeleteCount = create.delete(SB_SITEMENU_TB)
			.where(SB_SITEMENU_TB.MENU_NO.eq(UInteger.valueOf(menuDeleteReq.getMenuNo())))
			.execute();
			
			if (0 == menuDeleteCount) {				
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder()
						.append("메뉴[")
						.append(menuDeleteReq.getMenuNo())
						.append("] 삭제가 실패하였습니다").toString();
				throw new ServerServiceException(errorMessage);
			}
			
			/** 삭제할 메뉴 이후의 순서 보정 */
			create.update(SB_SITEMENU_TB)
			.set(SB_SITEMENU_TB.ORDER_SQ, SB_SITEMENU_TB.ORDER_SQ.sub(1))
			.where(SB_SITEMENU_TB.ORDER_SQ.gt(deleteMenuRecord.getValue(SB_SITEMENU_TB.ORDER_SQ)))
			.execute();
						
			
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
				} catch(Exception e) {
					log.warn("fail to close the db connection", e);
				}
			}
		}
		
		log.info("메뉴[{}] 삭제 처리가 완료되었습니다", menuDeleteReq.getMenuNo());
		
		MessageResultRes messageResultRes = new MessageResultRes();
		messageResultRes.setTaskMessageID(menuDeleteReq.getMessageID());
		messageResultRes.setIsSuccess(true);		
		messageResultRes.setResultMessage(new StringBuilder()
				.append("메뉴[")
				.append(menuDeleteReq.getMenuNo())
				.append("] 삭제 처리가 완료되었습니다").toString());
		
		return messageResultRes;
	}
}
