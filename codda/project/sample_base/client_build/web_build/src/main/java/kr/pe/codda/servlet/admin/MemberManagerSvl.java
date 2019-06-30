package kr.pe.codda.servlet.admin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.pe.codda.client.AnyProjectConnectionPoolIF;
import kr.pe.codda.client.ConnectionPoolManager;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.classloader.ClientMessageCodecManger;
import kr.pe.codda.impl.message.MemberSearchReq.MemberSearchReq;
import kr.pe.codda.impl.message.MemberSearchRes.MemberSearchRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.weblib.common.MemberStateType;
import kr.pe.codda.weblib.common.ValueChecker;
import kr.pe.codda.weblib.common.WebCommonStaticFinalVars;
import kr.pe.codda.weblib.jdf.AbstractAdminLoginServlet;

public class MemberManagerSvl extends AbstractAdminLoginServlet {
	

	private static final long serialVersionUID = 4109429353812223295L;

	@Override
	protected void performTask(HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		/**************** 파라미터 시작 *******************/
		String paramMemberState = req.getParameter("memberState");
		String paramSearchID = req.getParameter("searchID");
		String paramFromDate = req.getParameter("fromDate");
		String paramToDate = req.getParameter("toDate");
		String paramPageNo = req.getParameter("pageNo");
		/**************** 파라미터 종료 *******************/
		
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		
		final MemberStateType memberStateType;
		final String searchID;
		final String fromDateString;
		final String toDateString;
		final int pageNo;
		// final java.util.Date fromDate;
		// final java.util.Date toDate;
		final  Calendar fromCalendar = Calendar.getInstance();
		final  Calendar toCalendar = Calendar.getInstance();
		
		
		if (null == paramMemberState) {
			memberStateType = MemberStateType.BLOCK;
		} else {
			try {
				memberStateType = MemberStateType.valueOf(Byte.parseByte(paramMemberState));
			} catch(IllegalArgumentException e) {
				String errorMessage = "회원 상태값이 잘못되었습니다";
				String debugMessage = null;
				printErrorMessagePage(req, res, errorMessage, debugMessage);
				return;
			}
		}	
		
		if (null == paramSearchID) {
			searchID = "";
		} else {
			char[] searchIDChars = paramSearchID.toCharArray();			
			
			
			for (int i=1; i < searchIDChars.length; i++) {
				char c = searchIDChars[i];
				if (! (c >= 'a' && c <='z') && ! (c >= 'A' && c <='Z') && ! (c >= '0' && c <='9')) {
					String errorMessage = new StringBuilder()
							.append("검색 조건인 아이디[")
							.append(paramSearchID)
							.append("]는 영문과 숫자 조합이어야 합니다").toString();
					String debugMessage = null;
					printErrorMessagePage(req, res, errorMessage, debugMessage);
					return;
				}
				
			}
			
			searchID = paramSearchID;
		}
		
		
		
		if (null == paramFromDate) {
			
			fromCalendar.add(Calendar.DAY_OF_MONTH, -7);
			// fromDate = fromCalendar.getTime();
			
			fromDateString = sdf.format(fromCalendar.getTime());
		} else {
			try {
				java.util.Date fromDate = sdf.parse(paramFromDate);
				fromCalendar.setTime(fromDate);
				
				fromDateString = paramFromDate;
			} catch(ParseException e) {
				String errorMessage = new StringBuilder()
						.append("검색 조건인 시작일[")
						.append(paramFromDate)
						.append("] 값이 잘못되었습니다").toString();
				throw new IllegalArgumentException(errorMessage);
			}
		}
		
		
		
		if (null == paramToDate) {			
			toDateString = sdf.format(fromCalendar.getTime());
		} else {
			try {
				java.util.Date toDate = sdf.parse(paramToDate);
				toCalendar.setTime(toDate);
				
				toDateString = paramToDate;
			} catch(ParseException e) {
				String errorMessage = new StringBuilder()
						.append("검색 조건인 종료일[")
						.append(paramToDate)
						.append("] 값이 잘못되었습니다").toString();
				throw new IllegalArgumentException(errorMessage);
			}
		}
		
		try {
			pageNo = ValueChecker.checkValidPageNoAndPageSize(paramPageNo, WebCommonStaticFinalVars.WEBSITE_BOARD_LIST_SIZE_PER_PAGE);
		} catch(IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			String debugMessage = null;
			printErrorMessagePage(req, res, errorMessage, debugMessage);
			return;
		}
		
		MemberSearchReq memberSearchReq = new MemberSearchReq();
		memberSearchReq.setRequestedUserID(getAccessedUserInformationFromSession(req).getUserID());
		memberSearchReq.setMemberState(memberStateType.getValue());
		memberSearchReq.setSearchID(searchID);
		memberSearchReq.setFromDateString(fromDateString);
		memberSearchReq.setToDateString(toDateString);
		memberSearchReq.setPageNo(pageNo);
		memberSearchReq.setPageSize(WebCommonStaticFinalVars.WEBSITE_BOARD_LIST_SIZE_PER_PAGE);
		
		AnyProjectConnectionPoolIF mainProjectConnectionPool = ConnectionPoolManager.getInstance().getMainProjectConnectionPool();
		AbstractMessage outputMessage = mainProjectConnectionPool.sendSyncInputMessage(ClientMessageCodecManger.getInstance(), memberSearchReq);
		
		if (!(outputMessage instanceof MemberSearchRes)) {
			if (outputMessage instanceof MessageResultRes) {
				MessageResultRes messageResultRes = (MessageResultRes)outputMessage;
				String errorMessage = "회원 검색이 실패하였습니다";
				String debugMessage = messageResultRes.toString();
				printErrorMessagePage(req, res, errorMessage, debugMessage);	
				return;
			} else {
				String errorMessage = "회원 검색이 실패하였습니다";
				String debugMessage = new StringBuilder("입력 메시지[")
						.append(memberSearchReq.getMessageID())
						.append("]에 대한 비 정상 출력 메시지[")
						.append(outputMessage.toString())
						.append("] 도착").toString();
				
				log.error(debugMessage);

				printErrorMessagePage(req, res, errorMessage, debugMessage);
				return;
			}
		} 
		
		MemberSearchRes memberSearchRes = (MemberSearchRes)outputMessage;
		req.setAttribute("fromCalendar", fromCalendar);
		req.setAttribute("toCalendar", toCalendar);
		req.setAttribute("memberSearchRes", memberSearchRes);
		printJspPage(req, res, "/jsp/member/MemberManager.jsp");
		
	}

}
