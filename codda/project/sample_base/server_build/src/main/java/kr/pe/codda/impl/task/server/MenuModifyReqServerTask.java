package kr.pe.codda.impl.task.server;

import static kr.pe.codda.impl.jooq.tables.SbSitemenuTb.SB_SITEMENU_TB;

import java.sql.Connection;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;

import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.MenuModifyReq.MenuModifyReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class MenuModifyReqServerTask extends AbstractServerTask {	
	
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
			AbstractMessage outputMessage = doService((MenuModifyReq)inputMessage);
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
						
			sendErrorOutputMessage("메뉴 수정이 실패하였습니다", toLetterCarrier, inputMessage);
			return;
		}
	}
		
	public MessageResultRes doService(MenuModifyReq menuModifyReq) throws Exception {
		// FIXME!
		log.info(menuModifyReq.toString());
		
		DataSource dataSource = DBCPManager.getInstance()
				.getBasicDataSource(ServerCommonStaticFinalVars.SB_CONNECTION_POOL_NAME);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			
			DSLContext create = DSL.using(conn, SQLDialect.MYSQL);
			
			Record2<String, String> menuRecord = create.select(SB_SITEMENU_TB.MENU_NM, SB_SITEMENU_TB.LINK_URL)
			.from(SB_SITEMENU_TB)
			.where(SB_SITEMENU_TB.MENU_NO.eq(UInteger.valueOf(menuModifyReq.getMenuNo())))
			.fetchOne();
			
			if (null == menuRecord) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder()
						.append("수정할 메뉴[")
						.append(menuModifyReq.getMenuNo())
						.append("]가 존재하지 않습니다").toString();
				throw new ServerServiceException(errorMessage);
			}
			
			
			int menuUpdateCount = create.update(SB_SITEMENU_TB)
			.set(SB_SITEMENU_TB.MENU_NM, menuModifyReq.getMenuName())
			.set(SB_SITEMENU_TB.LINK_URL, menuModifyReq.getLinkURL())
			.where(SB_SITEMENU_TB.MENU_NO.eq(UInteger.valueOf(menuModifyReq.getMenuNo())))
			.execute();
			
			if (0 == menuUpdateCount) {
				
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder()
						.append("메뉴[")
						.append(menuModifyReq.getMenuNo())
						.append("] 수정이 실패하였습니다").toString();
				throw new ServerServiceException(errorMessage);
			}
			
			try {
				conn.commit();
			} catch (Exception e) {
				log.warn("fail to commit");
			}
			
			
			log.info("메뉴[{}] 수정전 {메뉴명[{}], URL[{}]}, 수정후 {메뉴명[{}], URL[{}]}", 
					menuRecord.getValue(SB_SITEMENU_TB.MENU_NM),
					menuRecord.getValue(SB_SITEMENU_TB.LINK_URL),
					menuModifyReq.getMenuName(),
					menuModifyReq.getLinkURL());
			
			MessageResultRes messageResultRes = new MessageResultRes();
			messageResultRes.setTaskMessageID(menuModifyReq.getMessageID());
			messageResultRes.setIsSuccess(true);		
			messageResultRes.setResultMessage(new StringBuilder()
					.append("메뉴[")
					.append(menuModifyReq.getMenuNo())
					.append("] 수정 처리가 완료되었습니다").toString());
			
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
