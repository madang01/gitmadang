package kr.pe.codda.impl.task.server;

import static kr.pe.codda.impl.jooq.tables.SbSeqTb.SB_SEQ_TB;
import static kr.pe.codda.impl.jooq.tables.SbSitemenuTb.SB_SITEMENU_TB;

import java.sql.Connection;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.impl.message.RootMenuAddReq.RootMenuAddReq;
import kr.pe.codda.impl.message.RootMenuAddRes.RootMenuAddRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.JooqSqlUtil;
import kr.pe.codda.server.lib.PermissionType;
import kr.pe.codda.server.lib.SequenceType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class RootMenuAddReqServerTask extends AbstractServerTask {

	public RootMenuAddReqServerTask() throws DynamicClassCallException {
		super();
	}

	private void sendErrorOutputMessage(String errorMessage, ToLetterCarrier toLetterCarrier,
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
			AbstractMessage outputMessage = doWork(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME,
					(RootMenuAddReq) inputMessage);
			toLetterCarrier.addSyncOutputMessage(outputMessage);
		} catch (ServerServiceException e) {
			String errorMessage = e.getMessage();
			log.warn("errmsg=={}, inObj={}", errorMessage, inputMessage.toString());

			sendErrorOutputMessage(errorMessage, toLetterCarrier, inputMessage);
			return;
		} catch (Exception e) {
			String errorMessage = new StringBuilder().append("unknwon errmsg=").append(e.getMessage())
					.append(", inObj=").append(inputMessage.toString()).toString();

			log.warn(errorMessage, e);

			sendErrorOutputMessage("루트 메뉴 추가하는데 실패하였습니다", toLetterCarrier, inputMessage);
			return;
		}

	}

	public RootMenuAddRes doWork(String dbcpName, RootMenuAddReq rootMenuAddReq) throws Exception {
		// FIXME!
		log.info(rootMenuAddReq.toString());

		final UByte menuSequenceID = SequenceType.MENU.getSequenceID();
		UInteger rootMenuNo = null;
		short newOrderSeq = 0;

		DataSource dataSource = DBCPManager.getInstance().getBasicDataSource(dbcpName);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(dbcpName));

			ServerDBUtil.checkUserAccessRights(conn, create, log, "루트 메뉴 추가 서비스", PermissionType.ADMIN,
					rootMenuAddReq.getRequestedUserID());

			/** '메뉴 순서' 를 위한 lock */
			Record menuSeqRecord = create.select(SB_SEQ_TB.SQ_VALUE).from(SB_SEQ_TB)
					.where(SB_SEQ_TB.SQ_ID.eq(menuSequenceID)).forUpdate().fetchOne();

			if (null == menuSeqRecord) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder("메뉴 시퀀스 식별자[").append(menuSequenceID)
						.append("]의 시퀀스를 가져오는데 실패하였습니다").toString();
				throw new ServerServiceException(errorMessage);
			}

			rootMenuNo = menuSeqRecord.getValue(SB_SEQ_TB.SQ_VALUE);

			if (rootMenuNo.longValue() == UInteger.MAX_VALUE) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder("메뉴 시퀀스 식별자[").append(menuSequenceID)
						.append("]의 시퀀스가 최대치에 도달하였습니다").toString();
				throw new ServerServiceException(errorMessage);
			}

			int seqUpdateCnt = create.update(SB_SEQ_TB).set(SB_SEQ_TB.SQ_VALUE, SB_SEQ_TB.SQ_VALUE.add(1))
					.where(SB_SEQ_TB.SQ_ID.eq(menuSequenceID)).execute();

			if (0 == seqUpdateCnt) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder("메뉴 시퀀스 식별자[").append(menuSequenceID)
						.append("]의 시퀀스 갱신하는데 실패하였습니다").toString();
				throw new ServerServiceException(errorMessage);
			}

			newOrderSeq = create.select(
					JooqSqlUtil.getIfField(SB_SITEMENU_TB.ORDER_SQ.max(), 0, SB_SITEMENU_TB.ORDER_SQ.max().add(1)))
					.from(SB_SITEMENU_TB).fetchOne(0, Short.class);

			if (newOrderSeq >= CommonStaticFinalVars.UNSIGNED_BYTE_MAX) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}

				String errorMessage = "메뉴 갯수가 최대치(=255)에 도달하여 더 이상 추가할 수 없습니다";
				throw new ServerServiceException(errorMessage);
			}

			int rootMenuInsertCount = create.insertInto(SB_SITEMENU_TB).set(SB_SITEMENU_TB.MENU_NO, rootMenuNo)
					.set(SB_SITEMENU_TB.PARENT_NO, UInteger.valueOf(0)).set(SB_SITEMENU_TB.DEPTH, UByte.valueOf(0))
					.set(SB_SITEMENU_TB.ORDER_SQ, UByte.valueOf(newOrderSeq))
					.set(SB_SITEMENU_TB.MENU_NM, rootMenuAddReq.getMenuName())
					.set(SB_SITEMENU_TB.LINK_URL, rootMenuAddReq.getLinkURL()).execute();

			if (0 == rootMenuInsertCount) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder().append("루트 메뉴 추가하는데 실패하였습니다").toString();
				throw new ServerServiceException(errorMessage);
			}

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
				} catch (Exception e) {
					log.warn("fail to close the db connection", e);
				}
			}
		}

		log.info("루트 메뉴[번호:{}, 메뉴명:{}, URL:{}] 추가 완료", rootMenuNo, rootMenuAddReq.getMenuName(),
				rootMenuAddReq.getLinkURL());

		RootMenuAddRes rootMenuAddRes = new RootMenuAddRes();
		rootMenuAddRes.setMenuNo(rootMenuNo.longValue());
		rootMenuAddRes.setOrderSeq(newOrderSeq);

		return rootMenuAddRes;

	}

}
