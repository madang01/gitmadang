package kr.pe.codda.server.lib;

import static kr.pe.codda.impl.jooq.tables.SbBoardInfoTb.SB_BOARD_INFO_TB;
import static kr.pe.codda.impl.jooq.tables.SbSeqTb.SB_SEQ_TB;

import java.sql.Connection;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.pe.codda.server.dbcp.DBCPManager;

public abstract class ServerDBUtil {

	public static void initializeDBEnvoroment() throws Exception {
		Logger log = LoggerFactory.getLogger(ServerDBUtil.class);
		
		DataSource dataSource = DBCPManager.getInstance()
				.getBasicDataSource(ServerCommonStaticFinalVars.SB_CONNECTION_POOL_NAME);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(true);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL);
			
			
			
			insertAllBoardIDIfNotExist(create);
			
			insertAllSeqIDIfNotExist(create);			
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (Exception e1) {
				log.warn("fail to rollback");
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
	}

	private static void insertAllSeqIDIfNotExist(DSLContext create) throws Exception {
		for (SequenceType sequenceTypeValue : SequenceType.values()) {
			boolean exists = create.fetchExists(create.select(SB_SEQ_TB.SQ_ID)
					.from(SB_SEQ_TB)
					.where(SB_SEQ_TB.SQ_ID
							.eq(UByte.valueOf(sequenceTypeValue.getSequenceID()))));
			
			if (! exists) {
				int countOfInsert = create.insertInto(SB_SEQ_TB)
				.set(SB_SEQ_TB.SQ_ID, UByte.valueOf(sequenceTypeValue.getSequenceID()))
				.set(SB_SEQ_TB.SQ_NAME, sequenceTypeValue.getName())
				.set(SB_SEQ_TB.SQ_VALUE, UInteger.valueOf(1))
				.execute();
				
				if (0 == countOfInsert) {
					String errorMessage = new StringBuilder()
							.append("fail to insert the sequence(id:")
							.append(sequenceTypeValue.getSequenceID())
							.append(", name:")
							.append(sequenceTypeValue.getName())
							.append(")").toString();
					throw new Exception(errorMessage);
				}
			}	
		}
	}

	private static void insertAllBoardIDIfNotExist(DSLContext create) throws Exception {
		for (BoardType boardType : BoardType.values()) {
			UByte boardTypeID = UByte.valueOf(boardType.getBoardID());
			
			boolean exists = create.fetchExists(create.select(SB_BOARD_INFO_TB.BOARD_ID)
					.from(SB_BOARD_INFO_TB)
					.where(SB_BOARD_INFO_TB.BOARD_ID
							.eq(boardTypeID)));

			if (! exists) {
				String boardInfo = new StringBuilder(boardType.getName())
				.append(" 게시판").toString();
				
				int countOfInsert = create.insertInto(SB_BOARD_INFO_TB)
				.set(SB_BOARD_INFO_TB.BOARD_ID, boardTypeID)
				.set(SB_BOARD_INFO_TB.BOARD_NAME, boardType.getName())
				.set(SB_BOARD_INFO_TB.BOARD_INFO, boardInfo)
				.execute();
				
				if (0 == countOfInsert) {
					String errorMessage = new StringBuilder().append(boardInfo)
							.append(" 식별자 삽입 실패").toString();
					throw new Exception(errorMessage);
				}				
			}
		}
	}
}
