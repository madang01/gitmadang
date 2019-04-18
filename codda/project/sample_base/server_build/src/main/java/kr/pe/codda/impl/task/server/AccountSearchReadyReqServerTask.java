package kr.pe.codda.impl.task.server;

import static kr.pe.codda.jooq.tables.SbMemberTb.SB_MEMBER_TB;
import static kr.pe.codda.jooq.tables.SbAccountSerarchReqTb.SB_ACCOUNT_SERARCH_REQ_TB;

import java.security.SecureRandom;
import java.sql.Timestamp;

import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.types.UByte;

import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.impl.message.AccountSearchReadyReq.AccountSearchReadyReq;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.lib.AccountSearchType;
import kr.pe.codda.server.lib.EmilUtil;
import kr.pe.codda.server.lib.MemberRoleType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.lib.ValueChecker;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class AccountSearchReadyReqServerTask extends AbstractServerTask {

	public AccountSearchReadyReqServerTask() throws DynamicClassCallException {
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
			AbstractMessage outputMessage = doWork(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME,
					(AccountSearchReadyReq) inputMessage);
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

			sendErrorOutputMessage("게시글 상세 조회가 실패하였습니다", toLetterCarrier, inputMessage);
			return;
		}
	}

	public MessageResultRes doWork(final String dbcpName, final AccountSearchReadyReq accountSearchReadyReq)
			throws Exception {
		// FIXME!
		log.info(accountSearchReadyReq.toString());
		
		final AccountSearchType accountSearchType;
		try {
			accountSearchType = AccountSearchType.valueOf(accountSearchReadyReq.getAccountSearchType());
			
			ValueChecker.checkValidEmail(accountSearchReadyReq.getEmail());
			ValueChecker.checkValidIP(accountSearchReadyReq.getIp());
		} catch (IllegalArgumentException e) {
			String errorMessage = e.getMessage();
			throw new ServerServiceException(errorMessage);
		}

		final MessageResultRes messageResultRes = new MessageResultRes();

		/**
		 * '아이디 혹은 비밀번호 찾기' 서비스는 오직 일반 회원에 한에서만 서비스가 제공됩니다. 회원 테이블에는 손님, 관리자 그리고 일반회원
		 * 이렇게 3종류가 존재하는데 이중 손님과 관리자 2개는 '아이디 혹은 비밀번호 찾기'서비스 대상자에서 제외합니다. 손님은 내부용 처리를
		 * 위해 존재할뿐 회원이 아니기때문에 제외하며 관리자는 보안상 허용해서는 안되기때문에 제외합니다.
		 */

		ServerDBUtil.execute(dbcpName, (conn, create) -> {
			Record3<String, String, String> memberRecord = create
					.select(SB_MEMBER_TB.USER_ID, SB_MEMBER_TB.NICKNAME, SB_MEMBER_TB.EMAIL).from(SB_MEMBER_TB)
					.where(SB_MEMBER_TB.EMAIL.eq(accountSearchReadyReq.getEmail()))
					.and(SB_MEMBER_TB.ROLE.eq(MemberRoleType.MEMBER.getValue())).forUpdate().fetchOne();

			if (null == memberRecord) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}

				String errorMessage = "입력한 이메일에 해당하는 일반 회원이 없습니다";

				throw new ServerServiceException(errorMessage);
			}

			String userID = memberRecord.get(SB_MEMBER_TB.USER_ID);
			String nickname = memberRecord.get(SB_MEMBER_TB.NICKNAME);
			String email = memberRecord.get(SB_MEMBER_TB.EMAIL);

			Record2<UByte, UByte> passwordSearchRequestRecord = create
					.select(SB_ACCOUNT_SERARCH_REQ_TB.FAIL_CNT, SB_ACCOUNT_SERARCH_REQ_TB.RETRY_CNT).from(SB_ACCOUNT_SERARCH_REQ_TB)
					.where(SB_ACCOUNT_SERARCH_REQ_TB.USER_ID.eq(userID)).fetchOne();

			if (null == passwordSearchRequestRecord) {
				byte[] secretAuthenticationValueBytes = new byte[8];
				SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
				random.nextBytes(secretAuthenticationValueBytes);

				String secretAuthenticationValue = CommonStaticUtil.Base64Encoder
						.encodeToString(secretAuthenticationValueBytes);

				Timestamp registeredDate = new java.sql.Timestamp(System.currentTimeMillis());

				create.insertInto(SB_ACCOUNT_SERARCH_REQ_TB).set(SB_ACCOUNT_SERARCH_REQ_TB.USER_ID, userID)
						.set(SB_ACCOUNT_SERARCH_REQ_TB.FAIL_CNT, UByte.valueOf(0))
						.set(SB_ACCOUNT_SERARCH_REQ_TB.RETRY_CNT, UByte.valueOf(1))
						.set(SB_ACCOUNT_SERARCH_REQ_TB.LAST_SECRET_AUTH_VALUE, secretAuthenticationValue)
						.set(SB_ACCOUNT_SERARCH_REQ_TB.LAST_REQ_DT, registeredDate)
						.set(SB_ACCOUNT_SERARCH_REQ_TB.IS_FINISHED, "N").execute();

				EmilUtil.sendPasswordSearchEmail(accountSearchType, nickname, email, secretAuthenticationValue);

				conn.commit();
				
				String siteLogText = new StringBuilder().append(accountSearchType.getName())
						.append(" 찾기 신청").toString();

				ServerDBUtil.insertSiteLog(conn, create, log, userID, 
						siteLogText, registeredDate,
						accountSearchReadyReq.getIp());

				conn.commit();
			} else {
				UByte failCount = passwordSearchRequestRecord.get(SB_ACCOUNT_SERARCH_REQ_TB.FAIL_CNT);
				UByte retryCount = passwordSearchRequestRecord.get(SB_ACCOUNT_SERARCH_REQ_TB.RETRY_CNT);				

				if (ServerCommonStaticFinalVars.MAX_RETRY_COUNT_OF_PASSWORD_SEARCH_SERVICE == retryCount.shortValue()) {
					try {
						conn.rollback();
					} catch (Exception e1) {
						log.warn("fail to rollback");
					}

					String errorMessage = new StringBuilder().append("아이디 혹은 비밀번호 찾기 신청 횟수가 최대 횟수 ")
							.append(ServerCommonStaticFinalVars.MAX_RETRY_COUNT_OF_PASSWORD_SEARCH_SERVICE)
							.append("회에 도달하여 더 이상 진행할 수 없습니다, 관리자에게 문의하여 주시기 바랍니다").toString();

					throw new ServerServiceException(errorMessage);
				}
				
				if (ServerCommonStaticFinalVars.MAX_WRONG_PASSWORD_COUNT_OF_PASSWORD_SEARCH_SERVICE == failCount
						.shortValue()) {
					try {
						conn.rollback();
					} catch (Exception e1) {
						log.warn("fail to rollback");
					}

					String errorMessage = new StringBuilder().append("아이디 혹은 비밀번호 찾기로 비밀값 틀린 횟수가  최대 횟수 ")
							.append(ServerCommonStaticFinalVars.MAX_WRONG_PASSWORD_COUNT_OF_PASSWORD_SEARCH_SERVICE)
							.append("회에 도달하여 더 이상 진행할 수 없습니다, 관리자에게 문의하여 주시기 바랍니다").toString();

					throw new ServerServiceException(errorMessage);
				}

				byte[] newSecretAuthenticationValueBytes = new byte[8];
				SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
				random.nextBytes(newSecretAuthenticationValueBytes);

				String secretAuthenticationValue = CommonStaticUtil.Base64Encoder
						.encodeToString(newSecretAuthenticationValueBytes);
				Timestamp registeredDate = new java.sql.Timestamp(System.currentTimeMillis());

				create.update(SB_ACCOUNT_SERARCH_REQ_TB)
						.set(SB_ACCOUNT_SERARCH_REQ_TB.RETRY_CNT, SB_ACCOUNT_SERARCH_REQ_TB.RETRY_CNT.add(1))
						.set(SB_ACCOUNT_SERARCH_REQ_TB.LAST_SECRET_AUTH_VALUE, secretAuthenticationValue)
						.set(SB_ACCOUNT_SERARCH_REQ_TB.LAST_REQ_DT, registeredDate)
						.where(SB_ACCOUNT_SERARCH_REQ_TB.USER_ID.eq(userID)).execute();

				EmilUtil.sendPasswordSearchEmail(accountSearchType, nickname, email, secretAuthenticationValue);

				conn.commit();

				String siteLogText = new StringBuilder()
						.append(retryCount.shortValue() + 1)
						.append("회 아이디 혹은 비밀번호 찾기 신청[찾기 대상:")
						.append(accountSearchType.getName()).append("]").toString();

				ServerDBUtil.insertSiteLog(conn, create, log, userID, siteLogText, registeredDate,
						accountSearchReadyReq.getIp());

				conn.commit();
			}

			messageResultRes.setTaskMessageID(accountSearchReadyReq.getMessageID());
			messageResultRes.setIsSuccess(true);
			messageResultRes.setResultMessage(new StringBuilder()
					.append(accountSearchType.getName())
					.append(" 찾기 준비 단계 처리가 완료되었습니다").toString());
		});

		return messageResultRes;
	}

}
