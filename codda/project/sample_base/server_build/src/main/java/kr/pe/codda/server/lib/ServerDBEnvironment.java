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

public abstract class ServerDBEnvironment {	
	
	private static void rollback(Connection conn) {
		Logger log = LoggerFactory.getLogger(ServerDBEnvironment.class);
		
		if (null != conn) {
			try {
				conn.rollback();
			} catch (Exception e1) {
				log.warn("fail to rollback");
			}
		}
	}

	private static void commit(Connection conn) {
		Logger log = LoggerFactory.getLogger(ServerDBEnvironment.class);
		try {
			conn.commit();
		} catch (Exception e1) {
			log.warn("fail to commit");
		}

	}

	
	
	public static void setup() throws Exception {
		Logger log = LoggerFactory.getLogger(ServerDBEnvironment.class);
		
		DataSource dataSource = DBCPManager.getInstance()
				.getBasicDataSource(ServerCommonStaticFinalVars.SB_CONNECTION_POOL_NAME);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL);
			
			boolean exists;
			
			exists = create.fetchExists(create.select(SB_BOARD_INFO_TB.BOARD_ID)
					.from(SB_BOARD_INFO_TB)
					.where(SB_BOARD_INFO_TB.BOARD_ID
							.eq(UByte.valueOf(BoardType.NOTICE.getBoardID()))));
			
			if (! exists) {
				int countOfInsert = create.insertInto(SB_BOARD_INFO_TB)
				.set(SB_BOARD_INFO_TB.BOARD_ID, UByte.valueOf(BoardType.NOTICE.getBoardID()))
				.set(SB_BOARD_INFO_TB.BOARD_NAME, BoardType.NOTICE.getName())
				.set(SB_BOARD_INFO_TB.BOARD_INFO, "공지 게시판")
				.execute();
				
				if (0 == countOfInsert) {
					commit(conn);
					throw new Exception("공지 게시판 식별자 삽입 실패");
				}
			}
			
			exists = create.fetchExists(create.select(SB_BOARD_INFO_TB.BOARD_ID)
					.from(SB_BOARD_INFO_TB)
					.where(SB_BOARD_INFO_TB.BOARD_ID
							.eq(UByte.valueOf(BoardType.FREE.getBoardID()))));
			
			if (! exists) {
				int countOfInsert = create.insertInto(SB_BOARD_INFO_TB)
				.set(SB_BOARD_INFO_TB.BOARD_ID, UByte.valueOf(BoardType.FREE.getBoardID()))
				.set(SB_BOARD_INFO_TB.BOARD_NAME, BoardType.FREE.getName())
				.set(SB_BOARD_INFO_TB.BOARD_INFO, "자유 게시판")
				.execute();
				
				if (0 == countOfInsert) {
					commit(conn);
					throw new Exception("자유 게시판 식별자 삽입 실패");
				}
			}
						
			exists = create.fetchExists(create.select(SB_BOARD_INFO_TB.BOARD_ID)
					.from(SB_BOARD_INFO_TB)
					.where(SB_BOARD_INFO_TB.BOARD_ID
							.eq(UByte.valueOf(BoardType.FAQ.getBoardID()))));
			
			if (! exists) {
				int countOfInsert = create.insertInto(SB_BOARD_INFO_TB)
				.set(SB_BOARD_INFO_TB.BOARD_ID, UByte.valueOf(BoardType.FAQ.getBoardID()))
				.set(SB_BOARD_INFO_TB.BOARD_NAME, BoardType.FAQ.getName())
				.set(SB_BOARD_INFO_TB.BOARD_INFO, "FAQ 게시판")
				.execute();
				
				if (0 == countOfInsert) {
					commit(conn);
					throw new Exception("FAQ 게시판 식별자 삽입 실패");
				}
			}
			
			for (SequenceType sequenceTypeValue : SequenceType.values()) {
				exists = create.fetchExists(create.select(SB_SEQ_TB.SQ_ID)
						.from(SB_SEQ_TB)
						.where(SB_SEQ_TB.SQ_ID
								.eq(UByte.valueOf(sequenceTypeValue.getSequenceID()))));
				
				if (! exists) {
					int countOfInsert = create.insertInto(SB_SEQ_TB)
					.set(SB_SEQ_TB.SQ_ID, UByte.valueOf(sequenceTypeValue.getSequenceID()))
					.set(SB_SEQ_TB.SQ_NAME, sequenceTypeValue.getName())
					.set(SB_SEQ_TB.SQ_VALUE, UInteger.valueOf(0))
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
			
			commit(conn);
		} catch (Exception e) {
			rollback(conn);
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
}
