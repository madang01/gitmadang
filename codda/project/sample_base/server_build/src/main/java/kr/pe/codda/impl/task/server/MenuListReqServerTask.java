package kr.pe.codda.impl.task.server;

import static kr.pe.codda.impl.jooq.tables.SbSitemenuTb.SB_SITEMENU_TB;

import java.sql.Connection;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record6;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;

import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.MenuListReq.MenuListReq;
import kr.pe.codda.impl.message.MenuListRes.MenuListRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class MenuListReqServerTask extends AbstractServerTask {

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
		doWork(projectName, personalLoginManager, toLetterCarrier, (MenuListReq)inputMessage);
	}

	public void doWork(String projectName, PersonalLoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			MenuListReq menuListReq) throws Exception {
		// FIXME!
		log.info(menuListReq.toString());
		
		DataSource dataSource = DBCPManager.getInstance()
				.getBasicDataSource(ServerCommonStaticFinalVars.SB_CONNECTION_POOL_NAME);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			
			DSLContext create = DSL.using(conn, SQLDialect.MYSQL);
			
			long startParnetNo = 0L;
			java.util.List<MenuListRes.Menu> menuList = new ArrayList<MenuListRes.Menu>();			
			buildMenuListRes(menuList, create, startParnetNo);
			
			MenuListRes menuListRes = new MenuListRes();
			menuListRes.setMenuList(menuList);
			menuListRes.setCnt(menuList.size());
			
			sendSuccessOutputMessageForCommit(menuListRes, conn, toLetterCarrier);
			return;			
		} catch (Exception e) {
			log.warn("unknown error", e);
			sendErrorOutputMessageForRollback("메뉴 목록 조회가 실패하였습니다", conn, toLetterCarrier, menuListReq);
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


	private void buildMenuListRes(java.util.List<MenuListRes.Menu> boardList, DSLContext create, long startParnetNo) {
		Result<Record6<UInteger, UInteger, UByte, UByte, String, String>> menuListResult = create.select(SB_SITEMENU_TB.MENU_NO, 
				SB_SITEMENU_TB.PARENT_NO, 
				SB_SITEMENU_TB.DEPTH, 
				SB_SITEMENU_TB.ORDER_SQ,					
				SB_SITEMENU_TB.MENU_NM,
				SB_SITEMENU_TB.LINK_URL)
		.from(SB_SITEMENU_TB)
		.where(SB_SITEMENU_TB.PARENT_NO.eq(UInteger.valueOf(startParnetNo)))
		.orderBy(SB_SITEMENU_TB.ORDER_SQ)
		.fetch();
		
		for (Record menuListRecord : menuListResult) {
			MenuListRes.Menu menu = new MenuListRes.Menu();
			
			menu.setMenuNo(menuListRecord.getValue(SB_SITEMENU_TB.MENU_NO).longValue());
			menu.setParentNo(menuListRecord.getValue(SB_SITEMENU_TB.PARENT_NO).longValue());
			menu.setDepth(menuListRecord.getValue(SB_SITEMENU_TB.DEPTH).shortValue());
			menu.setOrderSeq(menuListRecord.getValue(SB_SITEMENU_TB.ORDER_SQ).shortValue());
			menu.setMenuName(menuListRecord.getValue(SB_SITEMENU_TB.MENU_NM));
			menu.setLinkURL(menuListRecord.getValue(SB_SITEMENU_TB.LINK_URL));
			
			
			boardList.add(menu);
			buildMenuListRes(boardList, create, menu.getMenuNo());
		}
	}
}
