package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbMemberTb.SB_MEMBER_TB;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jooq.Record8;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.types.UByte;

import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.message.MemberSearchReq.MemberSearchReq;
import kr.pe.codda.impl.message.MemberSearchRes.MemberSearchRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.lib.MemberRoleType;
import kr.pe.codda.server.lib.MemberStateType;
import kr.pe.codda.server.lib.PermissionType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class MemberSearchReqServerTask extends AbstractServerTask {
	

	public MemberSearchReqServerTask() throws DynamicClassCallException {
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
	public void doTask(String projectName, PersonalLoginManagerIF personalLoginManager, ToLetterCarrier toLetterCarrier,
			AbstractMessage inputMessage) throws Exception {
		try {
			AbstractMessage outputMessage = doWork(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME, (MemberSearchReq)inputMessage);
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
						
			sendErrorOutputMessage("사용자 관리자 서비스가 실패하였습니다", toLetterCarrier, inputMessage);
			return;
		}
	}
	
	public MemberSearchRes doWork(String dbcpName, MemberSearchReq memberSearchReq)
			throws Exception {
		
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS");
		final MemberStateType memberStateType;
		final Date fromDate;
		final Date toDate;
		
		String searchID = memberSearchReq.getSearchID();
		byte memberStateValue = memberSearchReq.getMemberState();
		String fromDateString = memberSearchReq.getFromDateString();
		String toDateString = memberSearchReq.getToDateString();
		
		final int pageNo = memberSearchReq.getPageNo();
		final int pageSize = memberSearchReq.getPageSize();
		
		
		MemberSearchRes memberSearchRes = new MemberSearchRes();		
		
		try {
			ValueChecker.checkValidRequestedUserID(memberSearchReq.getRequestedUserID());		
			
			try {
				memberStateType = MemberStateType.valueOf(memberStateValue);
			} catch(IllegalArgumentException e) {
				String errorMessage = new StringBuilder().append("검색 조건인 회원 상태값[")
						.append(memberSearchReq.getMemberState())
						.append("]이 잘못되었습니다").toString();
				throw new IllegalArgumentException(errorMessage);
			}
			
			if (! searchID.isEmpty()) {
				char[] searchIDChars = searchID.toCharArray();			
				
				
				for (int i=1; i < searchIDChars.length; i++) {
					char c = searchIDChars[i];
					if (! Character.isDigit(c) && ! Character.isAlphabetic(c)) {
						String errorMessage = new StringBuilder()
								.append("검색 조건인 아이디[")
								.append(searchID)
								.append("]는 영문과 숫자 조합이어야 합니다").toString();
						throw new IllegalArgumentException(errorMessage);
					}
				}
			}
			
			//if (! fromDateString.isEmpty()) {
				try {
					fromDate = sdf.parse(new StringBuilder().append(fromDateString)
							.append(" 00:00:00.000").toString());
				} catch(ParseException e) {
					String errorMessage = new StringBuilder()
							.append("검색 조건인 시작일[")
							.append(fromDateString)
							.append("] 값이 잘못되었습니다").toString();
					throw new IllegalArgumentException(errorMessage);
				}
			//}
			
			//if (! toDateString.isEmpty()) {
				try {
					toDate = sdf.parse(new StringBuilder().append(toDateString)
							.append(" 23:59:59.999").toString());
				} catch(ParseException e) {
					String errorMessage = new StringBuilder()
							.append("검색 조건인 종료일[")
							.append(toDateString)
							.append("] 값이 잘못되었습니다").toString();
					throw new IllegalArgumentException(errorMessage);
				}
			//}
			
			ValueChecker.checkValidPageNoAndPageSize(pageNo, pageSize);
			
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}
		
		final int offset = (pageNo - 1) * pageSize;
		
		List<MemberSearchRes.Member> memberList = new ArrayList<MemberSearchRes.Member>();
		
		ServerDBUtil.execute(dbcpName, (conn, create) -> {
			
			ServerDBUtil.checkUserAccessRights(conn, create, log, "회원 조회 서비스", PermissionType.ADMIN, 
					memberSearchReq.getRequestedUserID());
			
			
			SelectConditionStep<Record8<String, String, UByte, Timestamp, Timestamp, Timestamp, Timestamp, Timestamp>> memberSelectConditionStep = create.select(SB_MEMBER_TB.USER_ID, 
					SB_MEMBER_TB.NICKNAME,
					SB_MEMBER_TB.PWD_FAIL_CNT,
					SB_MEMBER_TB.REG_DT,
					SB_MEMBER_TB.LAST_NICKNAME_MOD_DT,
					SB_MEMBER_TB.LAST_EMAIL_MOD_DT,
					SB_MEMBER_TB.LAST_PWD_MOD_DT,
					SB_MEMBER_TB.LAST_STATE_MOD_DT)
			.from(SB_MEMBER_TB)
			.where(SB_MEMBER_TB.STATE.eq(memberStateType.getValue()))
			.and(SB_MEMBER_TB.LAST_STATE_MOD_DT.between(new java.sql.Timestamp(fromDate.getTime()), new java.sql.Timestamp(toDate.getTime())))
			.and(SB_MEMBER_TB.ROLE.eq(MemberRoleType.MEMBER.getValue()));			
			
			
			if (! searchID.isEmpty()) {
				memberSelectConditionStep = memberSelectConditionStep.and(SB_MEMBER_TB.USER_ID.containsIgnoreCase(searchID));
			}
			
			
			Result<Record8<String, String, UByte, Timestamp, Timestamp, Timestamp, Timestamp, Timestamp>> 
			memberResult = memberSelectConditionStep.orderBy(SB_MEMBER_TB.LAST_STATE_MOD_DT.desc()).fetch();
			
			
			int inx = 0;
			memberSearchRes.setIsNextPage(false);
			
			for (Record8<String, String, UByte, Timestamp, Timestamp, Timestamp, Timestamp, Timestamp> memberRcord : memberResult) {
				
				if (inx < offset) {
					inx++;
					continue;
				} else if (inx == offset+pageSize) {
					memberSearchRes.setIsNextPage(true);
					break;
				}
				
				String userID = memberRcord.get(SB_MEMBER_TB.USER_ID);
				String nickname = memberRcord.get(SB_MEMBER_TB.NICKNAME);
				UByte passwordFailCount = memberRcord.get(SB_MEMBER_TB.PWD_FAIL_CNT);
				Timestamp registeredDate = memberRcord.get(SB_MEMBER_TB.REG_DT);
				Timestamp lastNicknameModifiedDate = memberRcord.get(SB_MEMBER_TB.LAST_NICKNAME_MOD_DT);
				Timestamp lastEmailModifiedDate = memberRcord.get(SB_MEMBER_TB.LAST_EMAIL_MOD_DT);
				Timestamp lastPasswordModifiedDate = memberRcord.get(SB_MEMBER_TB.LAST_PWD_MOD_DT);
				Timestamp lastStateModifiedDate = memberRcord.get(SB_MEMBER_TB.LAST_STATE_MOD_DT);
				
				MemberSearchRes.Member member = new MemberSearchRes.Member();
				member.setUserID(userID);
				member.setNickname(nickname);
				member.setPasswordFailCount(passwordFailCount.shortValue());
				member.setRegisteredDate(registeredDate);
				member.setLastNicknameModifiedDate(lastNicknameModifiedDate);
				member.setLastEmailModifiedDate(lastEmailModifiedDate);
				member.setLastPasswordModifiedDate(lastPasswordModifiedDate);
				member.setLastStateModifiedDate(lastStateModifiedDate);
				
				
				memberList.add(member);	
				
				inx++;
			}
			
			memberSearchRes.setCnt(memberList.size());
			memberSearchRes.setMemberList(memberList);
			
			conn.commit();
		});	
		
		memberSearchRes.setMemberState(memberStateValue);
		memberSearchRes.setSearchID(searchID);
		memberSearchRes.setFromDateString(fromDateString);
		memberSearchRes.setToDateString(toDateString);
		memberSearchRes.setPageNo(pageNo);
		memberSearchRes.setPageSize(pageSize);
		
		
		return memberSearchRes;
	}

}
