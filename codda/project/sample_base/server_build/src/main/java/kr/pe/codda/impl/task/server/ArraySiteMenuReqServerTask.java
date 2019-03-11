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

import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.ArraySiteMenuReq.ArraySiteMenuReq;
import kr.pe.codda.impl.message.ArraySiteMenuRes.ArraySiteMenuRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.PermissionType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class ArraySiteMenuReqServerTask extends AbstractServerTask {
	// final UInteger rootParnetNo = UInteger.valueOf(0);
	
	public ArraySiteMenuReqServerTask() throws DynamicClassCallException {
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
			AbstractMessage outputMessage = doWork(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, (ArraySiteMenuReq)inputMessage);
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
						
			sendErrorOutputMessage("메뉴 목록 조회가 실패하였습니다", toLetterCarrier, inputMessage);
			return;
		}
	}

	public ArraySiteMenuRes doWork(String dbcpName, ArraySiteMenuReq arraySiteMenuReq) throws Exception {
		// FIXME!
		log.info(arraySiteMenuReq.toString());
		
		java.util.List<ArraySiteMenuRes.Menu> menuList = new ArrayList<ArraySiteMenuRes.Menu>();
		
		DataSource dataSource = DBCPManager.getInstance()
				.getBasicDataSource(dbcpName);

		
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			
			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(dbcpName));
			
			ServerDBUtil.checkUserAccessRights(conn, create, log, 
					"배열형 메뉴 조회 서비스", PermissionType.ADMIN, arraySiteMenuReq.getRequestedUserID());
						
			
			Result<Record6<UInteger, UInteger, UByte, UByte, String, String>> menuListResult = create.select(SB_SITEMENU_TB.MENU_NO, 
					SB_SITEMENU_TB.PARENT_NO, 
					SB_SITEMENU_TB.DEPTH, 
					SB_SITEMENU_TB.ORDER_SQ,					
					SB_SITEMENU_TB.MENU_NM,
					SB_SITEMENU_TB.LINK_URL)
			.from(SB_SITEMENU_TB.forceIndex("sb_sitemenu_idx1"))
			.orderBy(SB_SITEMENU_TB.ORDER_SQ.asc())
			.fetch();			
						
			// buildMenuListRes(menuList, create, rootParnetNo);		
			for (Record menuListRecord : menuListResult) {
				ArraySiteMenuRes.Menu menu = new ArraySiteMenuRes.Menu();
				
				UInteger menuNo = menuListRecord.getValue(SB_SITEMENU_TB.MENU_NO);
				
				menu.setMenuNo(menuNo.longValue());
				menu.setParentNo(menuListRecord.getValue(SB_SITEMENU_TB.PARENT_NO).longValue());
				menu.setDepth(menuListRecord.getValue(SB_SITEMENU_TB.DEPTH).shortValue());
				menu.setOrderSeq(menuListRecord.getValue(SB_SITEMENU_TB.ORDER_SQ).shortValue());
				menu.setMenuName(menuListRecord.getValue(SB_SITEMENU_TB.MENU_NM));
				menu.setLinkURL(menuListRecord.getValue(SB_SITEMENU_TB.LINK_URL));
				menuList.add(menu);
			}			
			
			conn.commit();			
			
				
		} catch (Exception e) {
			if (null != conn) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
			}
			
			// log.warn("unknown error", e);			
			
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
		
		ArraySiteMenuRes menuListRes = new ArraySiteMenuRes();
		menuListRes.setMenuList(menuList);
		menuListRes.setCnt(menuList.size());
		
		return menuListRes;	
	}
}
