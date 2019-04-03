package kr.pe.codda.impl.task.server;


import static kr.pe.codda.impl.jooq.tables.SbBoardHistoryTb.SB_BOARD_HISTORY_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardInfoTb.SB_BOARD_INFO_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardTb.SB_BOARD_TB;
import static kr.pe.codda.impl.jooq.tables.SbMemberActivityHistoryTb.SB_MEMBER_ACTIVITY_HISTORY_TB;
import static kr.pe.codda.impl.jooq.tables.SbMemberTb.SB_MEMBER_TB;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record11;
import org.jooq.Record3;
import org.jooq.Record9;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;

import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.jooq.tables.SbBoardHistoryTb;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.impl.message.PersonalActivityHistoryReq.PersonalActivityHistoryReq;
import kr.pe.codda.impl.message.PersonalActivityHistoryRes.PersonalActivityHistoryRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.BoardListType;
import kr.pe.codda.server.lib.MemberRoleType;
import kr.pe.codda.server.lib.MemberStateType;
import kr.pe.codda.server.lib.PermissionType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class PersonalActivityHistoryReqServerTask extends AbstractServerTask {
	public PersonalActivityHistoryReqServerTask() throws DynamicClassCallException {
		super();
	}


	private void sendErrorOutputMessage(String errorMessage, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws InterruptedException {
		log.warn("{}, inObj=", errorMessage, inputMessage.toString());

		MessageResultRes messageResultRes = new MessageResultRes();
		messageResultRes.setTaskMessageID(inputMessage.getMessageID());
		messageResultRes.setIsSuccess(false);
		messageResultRes.setResultMessage(errorMessage);
		toLetterCarrier.addSyncOutputMessage(messageResultRes);
	}

	
	@Override
	public void doTask(String projectName, 
			PersonalLoginManagerIF personalLoginManager, 
			ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {
		
		try {
			AbstractMessage outputMessage = doWork(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, (PersonalActivityHistoryReq)inputMessage);
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
						
			sendErrorOutputMessage("게시글을 추천하는데  실패하였습니다", toLetterCarrier, inputMessage);
			return;
		}
		
	}
	
	public PersonalActivityHistoryRes doWork(String dbcpName, PersonalActivityHistoryReq personalActivityHistoryReq) throws Exception {
		// FIXME!
		log.info(personalActivityHistoryReq.toString());
		
		try {
			ValueChecker.checkValidUserID(personalActivityHistoryReq.getRequestedUserID());
		} catch(RuntimeException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}

		try {
			ValueChecker.checkValidActivtyTargetUserID(personalActivityHistoryReq.getTargetUserID());
		} catch(RuntimeException e) {			
			String errorMessage = e.getMessage();
			log.warn(errorMessage, e);
			
			throw new ServerServiceException(errorMessage);
		}
		
		try {
			ValueChecker.checkValidPageNoAndPageSize(personalActivityHistoryReq.getPageNo(), personalActivityHistoryReq.getPageSize());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}
		
		int pageNo = personalActivityHistoryReq.getPageNo();
		int pageSize = personalActivityHistoryReq.getPageSize();
		
		long total = 0;
		String targetUserNickname = null;		
		List<PersonalActivityHistoryRes.PersonalActivity> personalActivityList = new
				ArrayList<PersonalActivityHistoryRes.PersonalActivity>();
		
		DataSource dataSource = DBCPManager.getInstance()
				.getBasicDataSource(dbcpName);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(dbcpName));
			
			MemberRoleType memberRoleTypeOfRequestedUserID = ServerDBUtil.checkUserAccessRights(conn, create, log, "개인 활동 내역 조회 서비스", PermissionType.GUEST, personalActivityHistoryReq.getRequestedUserID());
			
			Record3<String, Byte, Byte> targetUserMemberRecord = create.select(
					SB_MEMBER_TB.NICKNAME,
					SB_MEMBER_TB.ROLE,
					SB_MEMBER_TB.STATE)
				.from(SB_MEMBER_TB)
				.where(SB_MEMBER_TB.USER_ID.eq(personalActivityHistoryReq.getTargetUserID())).fetchOne();
			
			targetUserNickname = targetUserMemberRecord.get(SB_MEMBER_TB.NICKNAME);
			byte targetUserMemberRole = targetUserMemberRecord.get(SB_MEMBER_TB.ROLE);
			byte targetUserMemberState = targetUserMemberRecord.get(SB_MEMBER_TB.STATE);
			
			MemberRoleType targetUserMemberRoleType = null;
			try {
				targetUserMemberRoleType = MemberRoleType.valueOf(targetUserMemberRole);
			} catch(IllegalArgumentException e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("개인 활동 내역 대상 사용자[")
						.append(personalActivityHistoryReq.getTargetUserID())
						.append("]의 멤버 구분[")
						.append(targetUserMemberRole)
						.append("]이 잘못되었습니다").toString();
				
				throw new ServerServiceException(errorMessage);
			}
			
			if (MemberRoleType.GUEST.equals(targetUserMemberRoleType)) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = "개인 활동 내역 조회 서비스의 대상은 회원만 가능합니다";
				
				throw new ServerServiceException(errorMessage);
			}
			
			MemberStateType targetUserMemberStateType = null;
			try {
				targetUserMemberStateType = MemberStateType.valueOf(targetUserMemberState);
			} catch(IllegalArgumentException e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("개인 활동 내역 대상 사용자[")
						.append(personalActivityHistoryReq.getTargetUserID())
						.append("]의 멤버 상태[")
						.append(targetUserMemberState)
						.append("]가 잘못되었습니다").toString();
				throw new ServerServiceException(errorMessage);
			}
			
			
			if (! MemberStateType.OK.equals(targetUserMemberStateType) &&
					! MemberRoleType.ADMIN.equals(memberRoleTypeOfRequestedUserID)) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}
				
				String errorMessage = new StringBuilder("개인 활동 내역 대상 사용자[")
						.append(personalActivityHistoryReq.getTargetUserID())
						.append("] 상태[")
						.append(targetUserMemberStateType.getName())
						.append("]가 정상이 아닙니다").toString();
				throw new ServerServiceException(errorMessage);
			}
			
			
			Record1<Long> maxActivitySeqRecord =  create.select(SB_MEMBER_ACTIVITY_HISTORY_TB.ACTIVITY_SQ.max())
			.from(SB_MEMBER_ACTIVITY_HISTORY_TB)
			.where(SB_MEMBER_ACTIVITY_HISTORY_TB.USER_ID.eq(personalActivityHistoryReq.getTargetUserID()))
			.fetchOne();
			
			if (null == maxActivitySeqRecord) {
				total = 0;
			} else {
				total = maxActivitySeqRecord.value1() + 1;				
			}
			
			if (total > 0) {
				long startActivitySeq = ((long)pageNo - 1) * (long)pageSize;
				long endActivitySeq = startActivitySeq + pageSize;
				
				Table<Record9<Long, Byte, UByte, UInteger, Timestamp, Byte, String, Byte, UInteger>> mainTable = 
						create.select( SB_MEMBER_ACTIVITY_HISTORY_TB.ACTIVITY_SQ, SB_MEMBER_ACTIVITY_HISTORY_TB.ACTIVITY_TYPE, SB_MEMBER_ACTIVITY_HISTORY_TB.BOARD_ID, 
					SB_MEMBER_ACTIVITY_HISTORY_TB.BOARD_NO,	SB_MEMBER_ACTIVITY_HISTORY_TB.REG_DT,
					create.select(SB_BOARD_INFO_TB.LIST_TYPE).from(SB_BOARD_INFO_TB).where(SB_BOARD_INFO_TB.BOARD_ID.eq(SB_MEMBER_ACTIVITY_HISTORY_TB.BOARD_ID)).<Byte>asField(SB_BOARD_INFO_TB.LIST_TYPE.getName()),
					create.select(SB_BOARD_INFO_TB.BOARD_NAME).from(SB_BOARD_INFO_TB).where(SB_BOARD_INFO_TB.BOARD_ID.eq(SB_MEMBER_ACTIVITY_HISTORY_TB.BOARD_ID)).<String>asField(SB_BOARD_INFO_TB.BOARD_NAME.getName()),
					SB_BOARD_TB.BOARD_ST,
					SB_BOARD_TB.GROUP_NO
				).from(SB_MEMBER_ACTIVITY_HISTORY_TB)
				.innerJoin(SB_BOARD_TB)
					.on(SB_BOARD_TB.BOARD_ID.eq(SB_MEMBER_ACTIVITY_HISTORY_TB.BOARD_ID))
					.and(SB_BOARD_TB.BOARD_NO.eq(SB_MEMBER_ACTIVITY_HISTORY_TB.BOARD_NO))
				.where(SB_MEMBER_ACTIVITY_HISTORY_TB.USER_ID.eq(personalActivityHistoryReq.getTargetUserID()))
				.and(SB_MEMBER_ACTIVITY_HISTORY_TB.ACTIVITY_SQ.ge(startActivitySeq))
				.and(SB_MEMBER_ACTIVITY_HISTORY_TB.ACTIVITY_SQ.lt(endActivitySeq)).asTable("a");
				
				
				SbBoardHistoryTb b = SB_BOARD_HISTORY_TB.as("b");
				SbBoardHistoryTb c = SB_BOARD_HISTORY_TB.as("c");
				SbBoardHistoryTb d = SB_BOARD_HISTORY_TB.as("d");
				
				Result<Record11<Byte, UByte, UInteger, Timestamp, Byte, String, Byte, UInteger, String, String, String>> memberActivityHisotryResult = create.select(mainTable.field(SB_MEMBER_ACTIVITY_HISTORY_TB.ACTIVITY_TYPE),
						mainTable.field(SB_MEMBER_ACTIVITY_HISTORY_TB.BOARD_ID),
						mainTable.field(SB_MEMBER_ACTIVITY_HISTORY_TB.BOARD_NO),
						mainTable.field(SB_MEMBER_ACTIVITY_HISTORY_TB.REG_DT),
						mainTable.field(SB_BOARD_INFO_TB.LIST_TYPE),
						mainTable.field(SB_BOARD_INFO_TB.BOARD_NAME),
						mainTable.field(SB_BOARD_TB.BOARD_ST),
						mainTable.field(SB_BOARD_TB.GROUP_NO),						
						DSL.choose(mainTable.field(SB_BOARD_INFO_TB.LIST_TYPE)).when(BoardListType.TREE.getValue(), d.SUBJECT).otherwise(b.SUBJECT).as("sourceSubject"),
						DSL.choose(mainTable.field(SB_BOARD_INFO_TB.LIST_TYPE)).when(BoardListType.TREE.getValue(), personalActivityHistoryReq.getTargetUserID()).otherwise(c.REGISTRANT_ID).as("sourceWriterID"),
						DSL.choose(mainTable.field(SB_BOARD_INFO_TB.LIST_TYPE)).when(BoardListType.TREE.getValue(), targetUserNickname)
							.otherwise(create.select(SB_MEMBER_TB.NICKNAME).from(SB_MEMBER_TB).where(SB_MEMBER_TB.USER_ID.eq(c.REGISTRANT_ID)).<String>asField()).as("sourceWriterNickname")
				)
				.from(mainTable)
				.innerJoin(b)
					.on(b.BOARD_ID.eq(mainTable.field(SB_MEMBER_ACTIVITY_HISTORY_TB.BOARD_ID)))
					.and(b.BOARD_NO.eq(mainTable.field(SB_BOARD_TB.GROUP_NO)))
					.and(b.field(SB_BOARD_HISTORY_TB.HISTORY_SQ).eq(create.select(SB_BOARD_HISTORY_TB.HISTORY_SQ.max())
							.from(SB_BOARD_HISTORY_TB)
							.where(SB_BOARD_HISTORY_TB.BOARD_ID.eq(mainTable.field(SB_MEMBER_ACTIVITY_HISTORY_TB.BOARD_ID)))
							.and(SB_BOARD_HISTORY_TB.BOARD_NO.eq(mainTable.field(SB_BOARD_TB.GROUP_NO)))))
				.innerJoin(c)
					.on(c.BOARD_ID.eq(mainTable.field(SB_MEMBER_ACTIVITY_HISTORY_TB.BOARD_ID)))
					.and(c.BOARD_NO.eq(mainTable.field(SB_BOARD_TB.GROUP_NO)))
					.and(c.field(SB_BOARD_HISTORY_TB.HISTORY_SQ).eq(UByte.valueOf(0)))
				.innerJoin(d)
					.on(d.BOARD_ID.eq(mainTable.field(SB_MEMBER_ACTIVITY_HISTORY_TB.BOARD_ID)))
					.and(d.BOARD_NO.eq(mainTable.field(SB_MEMBER_ACTIVITY_HISTORY_TB.BOARD_NO)))
					.and(d.field(SB_BOARD_HISTORY_TB.HISTORY_SQ).eq(create.select(SB_BOARD_HISTORY_TB.HISTORY_SQ.max())
							.from(SB_BOARD_HISTORY_TB)
							.where(SB_BOARD_HISTORY_TB.BOARD_ID.eq(mainTable.field(SB_MEMBER_ACTIVITY_HISTORY_TB.BOARD_ID)))
							.and(SB_BOARD_HISTORY_TB.BOARD_NO.eq(mainTable.field(SB_MEMBER_ACTIVITY_HISTORY_TB.BOARD_NO)))))
				.orderBy(mainTable.field(SB_MEMBER_ACTIVITY_HISTORY_TB.ACTIVITY_SQ).desc())
				.fetch();
				
				
				
				for (Record11<Byte, UByte, UInteger, Timestamp, Byte, String, Byte, UInteger, String, String, String> memberActivityHisotryRecord : memberActivityHisotryResult) {
					byte memberActivityTypeValue = memberActivityHisotryRecord.get(SB_MEMBER_ACTIVITY_HISTORY_TB.ACTIVITY_TYPE);
					UByte boardIDOfActivityHistory = memberActivityHisotryRecord.get(SB_MEMBER_ACTIVITY_HISTORY_TB.BOARD_ID);
					UInteger boardNoOfActivityHistory = memberActivityHisotryRecord.get(SB_MEMBER_ACTIVITY_HISTORY_TB.BOARD_NO);
					Timestamp registeredDate = memberActivityHisotryRecord.get(SB_MEMBER_ACTIVITY_HISTORY_TB.REG_DT);
					byte boardListTypeValue = memberActivityHisotryRecord.get(SB_BOARD_INFO_TB.LIST_TYPE);
					// FIXME!
					String boardName = memberActivityHisotryRecord.get(SB_BOARD_INFO_TB.BOARD_NAME);
					byte boardState = memberActivityHisotryRecord.get(SB_BOARD_TB.BOARD_ST);
					UInteger groupNo = memberActivityHisotryRecord.get(SB_BOARD_TB.GROUP_NO);
					String sourceSubject = memberActivityHisotryRecord.get("sourceSubject", String.class);
					String sourceWriterID = memberActivityHisotryRecord.get("sourceWriterID", String.class);
					String sourceWriterNickname = memberActivityHisotryRecord.get("sourceWriterNickname", String.class);
					
					
					
					// BoardListType boardListTypeOfActivityHistory = BoardListType.valueOf(boardListTypeValueOfActivityHistory);
					
					/*
					 * if (BoardListType.TREE.equals(boardListTypeOfActivityHistory)) {
					 * Table<SbBoardHistoryTbRecord> b = SB_BOARD_HISTORY_TB.asTable("b");
					 * 
					 * Record3<UInteger, Byte, String> boardRecord =
					 * create.select(SB_BOARD_TB.GROUP_NO, SB_BOARD_TB.BOARD_ST,
					 * b.field(SB_BOARD_HISTORY_TB.SUBJECT)) .from(SB_BOARD_TB) .innerJoin(b)
					 * .on(b.field(SB_BOARD_HISTORY_TB.BOARD_ID).eq(SB_BOARD_TB.BOARD_ID))
					 * .and(b.field(SB_BOARD_HISTORY_TB.BOARD_NO).eq(SB_BOARD_TB.BOARD_NO))
					 * .and(b.field(SB_BOARD_HISTORY_TB.HISTORY_SQ).eq(create.select(
					 * SB_BOARD_HISTORY_TB.HISTORY_SQ.max()) .from(SB_BOARD_HISTORY_TB)
					 * .where(SB_BOARD_HISTORY_TB.BOARD_ID.eq(SB_BOARD_TB.BOARD_ID))
					 * .and(SB_BOARD_HISTORY_TB.BOARD_NO.eq(SB_BOARD_TB.BOARD_NO))))
					 * .where(SB_BOARD_TB.BOARD_ID.eq(boardIDOfActivityHistory))
					 * .and(SB_BOARD_TB.BOARD_NO.eq(boardNoOfActivityHistory)) .fetchOne();
					 * 
					 * groupNo = boardRecord.get(SB_BOARD_TB.GROUP_NO); boardState =
					 * boardRecord.get(SB_BOARD_TB.BOARD_ST); sourceSubject =
					 * boardRecord.get(SB_BOARD_HISTORY_TB.SUBJECT); sourceWriterID =
					 * personalActivityHistoryReq.getTargetUserID(); sourceWriterNickname =
					 * targetUserNickname; } else {
					 * 
					 * Table<SbBoardHistoryTbRecord> b = SB_BOARD_HISTORY_TB.asTable("b");
					 * Table<SbBoardHistoryTbRecord> c = SB_BOARD_HISTORY_TB.asTable("c");
					 * 
					 * Record5<UInteger, Byte, String, String, Object> boardRecord =
					 * create.select(SB_BOARD_TB.GROUP_NO, SB_BOARD_TB.BOARD_ST,
					 * b.field(SB_BOARD_HISTORY_TB.SUBJECT),
					 * c.field(SB_BOARD_HISTORY_TB.REGISTRANT_ID).as("sourceWriterID"),
					 * (create.select(SB_MEMBER_TB.NICKNAME).from(SB_MEMBER_TB).where(SB_MEMBER_TB.
					 * USER_ID.eq(c.field(SB_BOARD_HISTORY_TB.REGISTRANT_ID)))).asField(
					 * "sourceWriterNickname")) .from(SB_BOARD_TB) .innerJoin(b)
					 * .on(b.field(SB_BOARD_HISTORY_TB.BOARD_ID).eq(SB_BOARD_TB.BOARD_ID))
					 * .and(b.field(SB_BOARD_HISTORY_TB.BOARD_NO).eq(SB_BOARD_TB.GROUP_NO))
					 * .and(b.field(SB_BOARD_HISTORY_TB.HISTORY_SQ).eq(create.select(
					 * SB_BOARD_HISTORY_TB.HISTORY_SQ.max()) .from(SB_BOARD_HISTORY_TB)
					 * .where(SB_BOARD_HISTORY_TB.BOARD_ID.eq(SB_BOARD_TB.BOARD_ID))
					 * .and(SB_BOARD_HISTORY_TB.BOARD_NO.eq(SB_BOARD_TB.GROUP_NO)))) .innerJoin(c)
					 * .on(c.field(SB_BOARD_HISTORY_TB.BOARD_ID).eq(SB_BOARD_TB.BOARD_ID))
					 * .and(c.field(SB_BOARD_HISTORY_TB.BOARD_NO).eq(SB_BOARD_TB.GROUP_NO))
					 * .and(c.field(SB_BOARD_HISTORY_TB.HISTORY_SQ).eq(UByte.valueOf(0)))
					 * .where(SB_BOARD_TB.BOARD_ID.eq(boardIDOfActivityHistory))
					 * .and(SB_BOARD_TB.BOARD_NO.eq(boardNoOfActivityHistory)) .fetchOne();
					 * 
					 * groupNo = boardRecord.get(SB_BOARD_TB.GROUP_NO); boardState =
					 * boardRecord.get(SB_BOARD_TB.BOARD_ST); sourceSubject =
					 * boardRecord.get(SB_BOARD_HISTORY_TB.SUBJECT); sourceWriterID =
					 * boardRecord.get("sourceWriterID", String.class); sourceWriterNickname =
					 * boardRecord.get("sourceWriterNickname", String.class); }
					 */
					
					PersonalActivityHistoryRes.PersonalActivity personalActivity = new PersonalActivityHistoryRes.PersonalActivity();
					personalActivity.setMemberActivityType(memberActivityTypeValue);					
					personalActivity.setBoardListType(boardListTypeValue);
					personalActivity.setBoardName(boardName);
					personalActivity.setBoardID(boardIDOfActivityHistory.shortValue());
					personalActivity.setBoardNo(boardNoOfActivityHistory.longValue());					
					personalActivity.setGroupNo(groupNo.longValue());
					personalActivity.setBoardSate(boardState);
					personalActivity.setRegisteredDate(registeredDate);
					personalActivity.setSourceSubject(sourceSubject);
					personalActivity.setSourceWriterID(sourceWriterID);
					personalActivity.setSourceWriterNickname(sourceWriterNickname);
					
					personalActivityList.add(personalActivity);
				}
				
				
			}
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
		
		
		PersonalActivityHistoryRes personalActivityHistoryRes = new PersonalActivityHistoryRes();
		personalActivityHistoryRes.setTargetUserID(personalActivityHistoryReq.getTargetUserID());
		personalActivityHistoryRes.setTargetUserNickname(targetUserNickname);
		personalActivityHistoryRes.setTotal(total);
		personalActivityHistoryRes.setPageNo(pageNo);
		personalActivityHistoryRes.setPageSize(pageSize);
		personalActivityHistoryRes.setCnt(personalActivityList.size());
		personalActivityHistoryRes.setPersonalActivityList(personalActivityList);
		
		
		return personalActivityHistoryRes;
	}
	
}
