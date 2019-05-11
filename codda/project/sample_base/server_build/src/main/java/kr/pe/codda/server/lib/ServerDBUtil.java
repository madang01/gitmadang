package kr.pe.codda.server.lib;

import static kr.pe.codda.jooq.tables.SbBoardInfoTb.SB_BOARD_INFO_TB;
import static kr.pe.codda.jooq.tables.SbBoardTb.SB_BOARD_TB;
import static kr.pe.codda.jooq.tables.SbMemberActivityHistoryTb.SB_MEMBER_ACTIVITY_HISTORY_TB;
import static kr.pe.codda.jooq.tables.SbMemberTb.SB_MEMBER_TB;
import static kr.pe.codda.jooq.tables.SbSeqTb.SB_SEQ_TB;
import static kr.pe.codda.jooq.tables.SbSiteLogTb.SB_SITE_LOG_TB;
import static kr.pe.codda.jooq.tables.SbSitemenuTb.SB_SITEMENU_TB;

import java.io.File;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Arrays;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.conf.MappedSchema;
import org.jooq.conf.RenderMapping;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.jooq.types.UShort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.codda.common.buildsystem.pathsupporter.ProjectBuildSytemPathSupporter;
import kr.pe.codda.common.config.CoddaConfiguration;
import kr.pe.codda.common.config.CoddaConfigurationManager;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.DBCPDataSourceNotFoundException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.util.CommonStaticUtil;
import kr.pe.codda.impl.task.server.MemberRegisterReqServerTask;
import kr.pe.codda.server.dbcp.DBCPManager;

public abstract class ServerDBUtil {

	private static Settings DEFAULT_DBCP_SETTINGS = new Settings().withRenderMapping(new RenderMapping()
			.withSchemata(new MappedSchema().withInput(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME.toLowerCase())
					.withOutput(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME.toLowerCase())));

	private static Settings GENERAL_TEST_DBCP_SETTINGS = new Settings().withRenderMapping(new RenderMapping()
			.withSchemata(new MappedSchema().withInput(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME.toLowerCase())
					.withOutput(ServerCommonStaticFinalVars.GENERAL_TEST_DBCP_NAME.toLowerCase())));

	private static Settings LOAD_TEST_DBCP_SETTINGS = new Settings().withRenderMapping(new RenderMapping()
			.withSchemata(new MappedSchema().withInput(ServerCommonStaticFinalVars.DEFAULT_DBCP_NAME.toLowerCase())
					.withOutput(ServerCommonStaticFinalVars.LOAD_TEST_DBCP_NAME.toLowerCase())));

	private static void initializeSequenceTable(DSLContext create) throws Exception {
		for (SequenceType sequenceTypeValue : SequenceType.values()) {
			boolean exists = create.fetchExists(create.select(SB_SEQ_TB.SQ_ID).from(SB_SEQ_TB)
					.where(SB_SEQ_TB.SQ_ID.eq(sequenceTypeValue.getSequenceID())));

			if (!exists) {
				int countOfInsert = create.insertInto(SB_SEQ_TB).set(SB_SEQ_TB.SQ_ID, sequenceTypeValue.getSequenceID())
						.set(SB_SEQ_TB.SQ_NAME, sequenceTypeValue.getName())
						.set(SB_SEQ_TB.SQ_VALUE, UInteger.valueOf(1)).execute();

				if (0 == countOfInsert) {
					String errorMessage = new StringBuilder().append("fail to insert the sequence(id:")
							.append(sequenceTypeValue.getSequenceID()).append(", name:")
							.append(sequenceTypeValue.getName()).append(")").toString();
					throw new Exception(errorMessage);
				}
			}
		}
	}

	/**
	 * WARNING! 일반 사용자 '사이트 메뉴 정보 테이블' 초기화는 '시퀀스 테이블' 초기화 이후 호출되어야 한다
	 * 
	 * @param create
	 * @throws Exception
	 */
	private static void initializeUserMenuInfoTable(DSLContext create) throws Exception {
		CoddaConfiguration mainProjectConfiguration = CoddaConfigurationManager.getInstance()
				.getRunningProjectConfiguration();
		String installedPathString = mainProjectConfiguration.getInstalledPathString();
		String mainProjectName = mainProjectConfiguration.getMainProjectName();

		String projectDBInitializationDirecotryPathString = ProjectBuildSytemPathSupporter
				.getDBInitializationDirecotryPathString(installedPathString, mainProjectName);

		File projectBoardInfoJsonFile = new File(new StringBuilder(projectDBInitializationDirecotryPathString)
				.append(File.separator).append("sample_base_usermenu.json.txt").toString());

		byte[] buffer = CommonStaticUtil.readFileToByteArray(projectBoardInfoJsonFile, 10 * 1024 * 1024L);

		String userMenuInfoJsonString = new String(buffer, CommonStaticFinalVars.SOURCE_FILE_CHARSET);

		JsonParser jsonParser = new JsonParser();
		JsonElement userMenuInfoJsonElement = jsonParser.parse(userMenuInfoJsonString);

		if (!userMenuInfoJsonElement.isJsonObject()) {
			throw new Exception("the var userMenuInfoJsonElement is not a JsonObject");
		}

		JsonObject jsonObject = userMenuInfoJsonElement.getAsJsonObject();
		JsonElement siteMenuListJsonElement = jsonObject.get("siteMenuList");

		if (null == siteMenuListJsonElement) {
			String errorMessage = "the var siteMenuListJsonElement is null";
			throw new Exception(errorMessage);
		}

		if (!siteMenuListJsonElement.isJsonArray()) {
			String errorMessage = "the var siteMenuListJsonElement is not a json array";
			throw new Exception(errorMessage);
		}

		JsonArray siteMenuListJsonArray = siteMenuListJsonElement.getAsJsonArray();

		int size = siteMenuListJsonArray.size();
		for (int i = 0; i < size; i++) {
			JsonElement siteMenuJsonElement = siteMenuListJsonArray.get(i);
			if (!siteMenuJsonElement.isJsonObject()) {
				String errorMessage = "the var siteMenuJsonElement is not a json object";
				throw new Exception(errorMessage);
			}

			JsonObject siteMenuJsonObject = siteMenuJsonElement.getAsJsonObject();

			JsonElement menuNoJsonElement = siteMenuJsonObject.get("menu_no");
			JsonElement parentNoJsonElement = siteMenuJsonObject.get("parent_no");
			JsonElement depthJsonElement = siteMenuJsonObject.get("depth");
			JsonElement orderSequenceJsonElement = siteMenuJsonObject.get("order_sq");
			JsonElement menuNameJsonElement = siteMenuJsonObject.get("menu_nm");
			JsonElement linkURLJsonElement = siteMenuJsonObject.get("link_url");

			if (null == menuNoJsonElement) {
				String errorMessage = "the var menuNoJsonElement is is null";
				throw new Exception(errorMessage);
			}

			if (null == parentNoJsonElement) {
				String errorMessage = "the var parentNoJsonElement is is null";
				throw new Exception(errorMessage);
			}

			if (null == depthJsonElement) {
				String errorMessage = "the var depthJsonElement is is null";
				throw new Exception(errorMessage);
			}

			if (null == orderSequenceJsonElement) {
				String errorMessage = "the var orderSequenceJsonElement is is null";
				throw new Exception(errorMessage);
			}

			if (null == menuNameJsonElement) {
				String errorMessage = "the var menuNameJsonElement is is null";
				throw new Exception(errorMessage);
			}

			if (null == linkURLJsonElement) {
				String errorMessage = "the var linkURLJsonElement is is null";
				throw new Exception(errorMessage);
			}

			UInteger menuNo = UInteger.valueOf(menuNoJsonElement.getAsLong());
			UInteger parentNo = UInteger.valueOf(parentNoJsonElement.getAsLong());
			UByte depth = UByte.valueOf(depthJsonElement.getAsShort());
			UByte orderSequence = UByte.valueOf(orderSequenceJsonElement.getAsShort());
			String menuName = menuNameJsonElement.getAsString();
			String linkURL = linkURLJsonElement.getAsString();

			boolean isMenu = create.fetchExists(create.select(SB_SITEMENU_TB.MENU_NO).from(SB_SITEMENU_TB)
					.where(SB_SITEMENU_TB.MENU_NO.eq(menuNo)));

			if (!isMenu) {
				int countOfInsert = create.insertInto(SB_SITEMENU_TB).set(SB_SITEMENU_TB.MENU_NO, menuNo)
						.set(SB_SITEMENU_TB.PARENT_NO, parentNo).set(SB_SITEMENU_TB.DEPTH, depth)
						.set(SB_SITEMENU_TB.ORDER_SQ, orderSequence).set(SB_SITEMENU_TB.MENU_NM, menuName)
						.set(SB_SITEMENU_TB.LINK_URL, linkURL).execute();

				if (0 == countOfInsert) {
					String errorMessage = new StringBuilder().append("사용자 사이트 메뉴[번호:").append(menuNo).append(", 메뉴명:")
							.append(menuName).append("] 삽입 실패").toString();
					throw new Exception(errorMessage);
				}
			}
		}

		create.update(SB_SEQ_TB).set(SB_SEQ_TB.SQ_VALUE,
				create.select(DSL.field("if ({0} is null, {1}, {2})", UInteger.class, SB_SITEMENU_TB.MENU_NO.max(),
						UInteger.valueOf(1), SB_SITEMENU_TB.MENU_NO.max().add(1))).from(SB_SITEMENU_TB))
				.where(SB_SEQ_TB.SQ_ID.eq(SequenceType.MENU.getSequenceID())).execute();
	}

	private static void initializeBoardInfoTable(DSLContext create) throws Exception {
		CoddaConfiguration mainProjectConfiguration = CoddaConfigurationManager.getInstance()
				.getRunningProjectConfiguration();
		String installedPathString = mainProjectConfiguration.getInstalledPathString();
		String mainProjectName = mainProjectConfiguration.getMainProjectName();

		String projectDBInitializationDirecotryPathString = ProjectBuildSytemPathSupporter
				.getDBInitializationDirecotryPathString(installedPathString, mainProjectName);

		File projectBoardInfoJsonFile = new File(new StringBuilder(projectDBInitializationDirecotryPathString)
				.append(File.separator).append("sample_base_board_info.json.txt").toString());

		byte[] buffer = CommonStaticUtil.readFileToByteArray(projectBoardInfoJsonFile, 10 * 1024 * 1024L);

		String boardInfoJsonString = new String(buffer, CommonStaticFinalVars.SOURCE_FILE_CHARSET);

		JsonParser jsonParser = new JsonParser();
		JsonElement boardInfoJsonElement = jsonParser.parse(boardInfoJsonString);

		if (!boardInfoJsonElement.isJsonObject()) {
			throw new Exception("the var boardInfoJsonElement is not a JsonObject");
		}

		JsonObject jsonObject = boardInfoJsonElement.getAsJsonObject();
		JsonElement boardInfomationListJsonElement = jsonObject.get("boardInfomationList");

		if (null == boardInfomationListJsonElement) {
			String errorMessage = "the var boardInfomationListJsonElement is null";
			throw new Exception(errorMessage);
		}

		if (!boardInfomationListJsonElement.isJsonArray()) {
			String errorMessage = "the var boardInfomationListJsonElement is not a json array";
			throw new Exception(errorMessage);
		}

		JsonArray boardInfomationListJsonArray = boardInfomationListJsonElement.getAsJsonArray();

		int size = boardInfomationListJsonArray.size();
		for (int i = 0; i < size; i++) {
			JsonElement boardInfomationJsonElement = boardInfomationListJsonArray.get(i);
			if (!boardInfomationJsonElement.isJsonObject()) {
				String errorMessage = "the var boardInfomationJsonElement is not a json object";
				throw new Exception(errorMessage);
			}

			JsonObject boardInfomationJsonObject = boardInfomationJsonElement.getAsJsonObject();

			JsonElement boardIDJsonElement = boardInfomationJsonObject.get("boardID");
			JsonElement boardNameJsonElement = boardInfomationJsonObject.get("boardName");
			JsonElement boardListTypeJsonElement = boardInfomationJsonObject.get("listType");
			JsonElement boardReplyPolicyTypeJsonElement = boardInfomationJsonObject.get("replyPolicyType");
			JsonElement boardWritePermissionTypeJsonElement = boardInfomationJsonObject.get("writePermissionType");
			JsonElement boardReplyPermissionTypeJsonElement = boardInfomationJsonObject.get("replyPermissionType");

			if (null == boardIDJsonElement) {
				String errorMessage = "the var boardIDJsonElement is is null";
				throw new Exception(errorMessage);
			}

			if (null == boardNameJsonElement) {
				String errorMessage = "the var boardNameJsonElement is is null";
				throw new Exception(errorMessage);
			}

			if (null == boardListTypeJsonElement) {
				String errorMessage = "the var boardListTypeJsonElement is is null";
				throw new Exception(errorMessage);
			}

			if (null == boardReplyPolicyTypeJsonElement) {
				String errorMessage = "the var boardReplyPolicyTypeJsonElement is is null";
				throw new Exception(errorMessage);
			}

			if (null == boardWritePermissionTypeJsonElement) {
				String errorMessage = "the var boardWritePermissionTypeJsonElement is is null";
				throw new Exception(errorMessage);
			}

			if (null == boardReplyPermissionTypeJsonElement) {
				String errorMessage = "the var boardReplyPermissionTypeJsonElement is is null";
				throw new Exception(errorMessage);
			}

			UByte boardID = UByte.valueOf(boardIDJsonElement.getAsShort());
			String boardName = boardNameJsonElement.getAsString();
			byte boardListType = boardListTypeJsonElement.getAsByte();
			byte boardReplyPolictyType = boardReplyPolicyTypeJsonElement.getAsByte();
			byte boardWritePermissionType = boardWritePermissionTypeJsonElement.getAsByte();
			byte boardReplyPermissionType = boardReplyPermissionTypeJsonElement.getAsByte();

			boolean isBoardTypeID = create.fetchExists(create.select(SB_BOARD_INFO_TB.BOARD_ID).from(SB_BOARD_INFO_TB)
					.where(SB_BOARD_INFO_TB.BOARD_ID.eq(boardID)));

			if (!isBoardTypeID) {
				int countOfInsert = create.insertInto(SB_BOARD_INFO_TB).set(SB_BOARD_INFO_TB.BOARD_ID, boardID)
						.set(SB_BOARD_INFO_TB.BOARD_NAME, boardName).set(SB_BOARD_INFO_TB.LIST_TYPE, boardListType)
						.set(SB_BOARD_INFO_TB.REPLY_POLICY_TYPE, boardReplyPolictyType)
						.set(SB_BOARD_INFO_TB.WRITE_PERMISSION_TYPE, boardWritePermissionType)
						.set(SB_BOARD_INFO_TB.REPLY_PERMISSION_TYPE, boardReplyPermissionType)
						.set(SB_BOARD_INFO_TB.CNT, 0L)
						.set(SB_BOARD_INFO_TB.TOTAL, 0L)
						.set(SB_BOARD_INFO_TB.NEXT_BOARD_NO, UInteger.valueOf(1)).execute();

				if (0 == countOfInsert) {
					String errorMessage = new StringBuilder().append("게시판 정보[게시판식별자:").append(boardID)
							.append(", 게시판이름:").append(boardName).append("] 삽입 실패").toString();
					throw new Exception(errorMessage);
				}
			}
		}
	}

	public static void initializeDBEnvoroment(String dbcpName) throws Exception {
		Logger log = LoggerFactory.getLogger(ServerDBUtil.class);

		DataSource dataSource = null;

		try {
			dataSource = DBCPManager.getInstance().getBasicDataSource(dbcpName);
		} catch (IllegalArgumentException | DBCPDataSourceNotFoundException e) {
			String errorMessage = e.getMessage();

			log.warn(errorMessage, e);

			/** 지정한 이름의 dbcp 를 받지 못할 경우 아무 처리도 하지 않는다 */
			return;
		}

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(true);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(dbcpName));

			initializeSequenceTable(create);

			initializeBoardInfoTable(create);

			initializeUserMenuInfoTable(create);

		} catch (Exception e) {
			log.warn("error", e);

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

		/*
		 * String nickname = "테스트어드민"; String pwdHint = "비밀번호힌트::그것이 알고싶다"; String
		 * pwdAnswer = "비밀번호답변::"; // 비밀번호는 영문으로 시작해서 영문/숫자/특수문자가 최소 1자이상 조합되어야 한다
		 * byte[] passwordBytes = {(byte)'t', (byte)'e', (byte)'s', (byte)'t',
		 * (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'$'};
		 * 
		 * try { registerMember(MemberType.ADMIN, adminID, nickname, pwdHint, pwdAnswer,
		 * passwordBytes); } catch (Exception e) { log.warn("unknown error", e); throw
		 * e; }
		 */

	}

	
	/**
	 * 회원 종류에 따른 회원 등록을 수행한다. 어드민과 일반 회원 등록 관리를 일원화 시킬 목적으로 '회원 등록 서버 서버
	 * 타스크'(={@link MemberRegisterReqServerTask})가 아닌 이곳 서버 라이브러리에서 관리한다.
	 * 
	 * @param dbcpName dbcp 이름(=db schema)
	 * @param memberRoleType 회원 역활
	 * @param userID 회원 아이디
	 * @param nickname 별명
	 * @param email 이메일 주소
	 * @param passwordBytes 패스워드
	 * @param ip 아이피 주소
	 * @param registeredDate 등록일
	 * @throws Exception
	 */
	public static void registerMember(String dbcpName, MemberRoleType memberRoleType, String userID, String nickname,
			String email, byte[] passwordBytes, Timestamp registeredDate, String ip) throws Exception {		
		InternalLogger log = InternalLoggerFactory.getInstance(ServerDBUtil.class);
		

		if (null == memberRoleType) {
			String errorMessage = "the parameter memberRoleType is null";
			throw new ServerServiceException(errorMessage);
		}

		try {
			ValueChecker.checkValidUserID(userID);
			ValueChecker.checkValidNickname(nickname);
			ValueChecker.checkValidEmail(email);		
			ValueChecker.checkValidMemberReigsterPwd(passwordBytes);		
			ValueChecker.checkValidIP(ip);
		} catch (IllegalArgumentException e) {
			throw new ServerServiceException(e.getMessage());
		}		

		SecureRandom random = null;
		try {
			random = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			/** dead code */
			log.error("NoSuchAlgorithmException", e);
			System.exit(1);
		}
		byte[] pwdSaltBytes = new byte[8];
		random.nextBytes(pwdSaltBytes);

		PasswordPairOfMemberTable passwordPairOfMemberTable = toPasswordPairOfMemberTable(passwordBytes, pwdSaltBytes);

		ServerDBUtil.execute(dbcpName, (conn, create) -> {
			
			boolean isSameIDMember = create.fetchExists(
					create.select(SB_MEMBER_TB.USER_ID).from(SB_MEMBER_TB).where(SB_MEMBER_TB.USER_ID.eq(userID)));

			if (isSameIDMember) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder("기존 회원과 중복되는 아이디[").append(userID).append("] 입니다").toString();
				throw new ServerServiceException(errorMessage);
			}

			boolean isSameNicknameMember = create.fetchExists(
					create.select(SB_MEMBER_TB.NICKNAME).from(SB_MEMBER_TB).where(SB_MEMBER_TB.NICKNAME.eq(nickname)));

			if (isSameNicknameMember) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder("기존 회원과 중복되는 별명[").append(nickname).append("] 입니다").toString();
				throw new ServerServiceException(errorMessage);
			}

			int resultOfInsert = create.insertInto(SB_MEMBER_TB).set(SB_MEMBER_TB.USER_ID, userID)
					.set(SB_MEMBER_TB.NICKNAME, nickname)
					.set(SB_MEMBER_TB.PWD_BASE64, passwordPairOfMemberTable.getPasswordBase64())
					.set(SB_MEMBER_TB.PWD_SALT_BASE64, passwordPairOfMemberTable.getPasswordSaltBase64())
					.set(SB_MEMBER_TB.ROLE, memberRoleType.getValue())
					.set(SB_MEMBER_TB.STATE, MemberStateType.OK.getValue())
					.set(SB_MEMBER_TB.EMAIL, email)					
					.set(SB_MEMBER_TB.PWD_FAIL_CNT, UByte.valueOf(0))
					.set(SB_MEMBER_TB.REG_DT, registeredDate)
					.set(SB_MEMBER_TB.LAST_NICKNAME_MOD_DT, registeredDate)
					.set(SB_MEMBER_TB.LAST_EMAIL_MOD_DT, registeredDate)
					.set(SB_MEMBER_TB.LAST_PWD_MOD_DT, registeredDate)
					.set(SB_MEMBER_TB.LAST_STATE_MOD_DT, registeredDate)
					.execute();

			if (0 == resultOfInsert) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}

				String errorMessage = "회원 등록하는데 실패하였습니다";
				throw new ServerServiceException(errorMessage);
			}			

			conn.commit();
			
			String logText = new StringBuilder().append("회원 가입 신청 아이디[").append(userID).append("], 회원 종류[")
					.append(memberRoleType.getName()).append("]").toString();
			
			insertSiteLog(conn, create, log, userID, logText, registeredDate, ip);
			
			conn.commit();
			
		});
	}
	

	/**
	 * <pre>
	 * 회원 테이블 '비밀번호' 필드와 '비밀번호 소금' 필드 값 묶음을 반환한다. 
	 * 
	 * 회원 테이블 '비밀번호' 필드 값은 base64로 인코딩한 비밀번호 해쉬 값이다.
	 * 회원 테이블  '비밀번호 소금' 필드 값은 base64로 인코딩한 소금값으로 비밀번호 역추적을 어렵게 하기 위해 목적을 갖는다. 
	 * 
	 * WARNING! 파라미터 유효성 검사를 수행하지 않기때문에 사용에 주의가 필요합니다.
	 * WARNING! 보안상 비밀번호 만드는 방법은 노출되어서는 안됩니다. 하여 만약 실제로 운영한다면 반듯이 노출하지 않도록 조취가 필요합니다.
	 * </pre>
	 * 
	 * @param passwordBytes 사용자가 입력한 비밀번호
	 * @param pwdSaltBytes  비밀번호 해쉬에 사용할 소금
	 * @return 회원 테이블 '비밀번호' 필드와 '비밀번호 소금' 필드 값 묶음
	 * @throws NoSuchAlgorithmException
	 */
	public static PasswordPairOfMemberTable toPasswordPairOfMemberTable(byte[] passwordBytes, byte[] pwdSaltBytes)
			throws NoSuchAlgorithmException {
		
		MessageDigest md = MessageDigest.getInstance(CommonStaticFinalVars.PASSWORD_ALGORITHM_NAME);

		int limit = pwdSaltBytes.length + passwordBytes.length;
		
		ByteBuffer passwordByteBuffer = ByteBuffer.allocate(limit);
		passwordByteBuffer.put(pwdSaltBytes);
		passwordByteBuffer.put(passwordBytes);

		md.update(passwordByteBuffer.array());

		byte passwordMDBytes[] = md.digest();

		/** 복호환 비밀번호 초기화 */
		Arrays.fill(passwordBytes, CommonStaticFinalVars.ZERO_BYTE);

		return new PasswordPairOfMemberTable(CommonStaticUtil.Base64Encoder.encodeToString(passwordMDBytes),
				CommonStaticUtil.Base64Encoder.encodeToString(pwdSaltBytes));
	}

	public static Settings getDBCPSettings(String dbcpName) {
		if (dbcpName.equals(ServerCommonStaticFinalVars.GENERAL_TEST_DBCP_NAME)) {
			return GENERAL_TEST_DBCP_SETTINGS;
		} else if (dbcpName.equals(ServerCommonStaticFinalVars.LOAD_TEST_DBCP_NAME)) {
			return LOAD_TEST_DBCP_SETTINGS;
		}

		return DEFAULT_DBCP_SETTINGS;
	}

	public static UByte getToOrderSeqOfRelativeRootMenu(DSLContext create, UByte orderSeq, UByte depth) {
		UByte toOrderSeq = orderSeq;

		Result<Record2<UByte, UByte>> childMenuResult = create.select(SB_SITEMENU_TB.ORDER_SQ, SB_SITEMENU_TB.DEPTH)
				.from(SB_SITEMENU_TB.forceIndex("sb_sitemenu_idx1")).where(SB_SITEMENU_TB.ORDER_SQ.gt(orderSeq))
				.orderBy(SB_SITEMENU_TB.ORDER_SQ.asc()).fetch();

		for (Record2<UByte, UByte> childMenuRecord : childMenuResult) {
			UByte childOrderSeq = childMenuRecord.get(SB_SITEMENU_TB.ORDER_SQ);
			UByte childDepth = childMenuRecord.get(SB_SITEMENU_TB.DEPTH);

			if (childDepth.shortValue() <= depth.shortValue()) {
				break;
			}

			toOrderSeq = childOrderSeq;
		}

		return toOrderSeq;
	}

	public static UByte getToOrderSeqOfRelativeRootMenu(DSLContext create, UByte orderSeq, UInteger directParentNo)
			throws ServerServiceException {
		while (true) {
			Record1<UByte> toOrderSeqRecord = create.select(SB_SITEMENU_TB.ORDER_SQ.min().sub(1).as("toOrderSeq"))
					.from(SB_SITEMENU_TB.forceIndex("sb_sitemenu_idx2"))
					.where(SB_SITEMENU_TB.PARENT_NO.eq(directParentNo)).and(SB_SITEMENU_TB.ORDER_SQ.gt(orderSeq))
					.fetchOne();

			if (null == toOrderSeqRecord.getValue("toOrderSeq")) {
				if (0 == directParentNo.longValue()) {
					Record1<UByte> maxOrderSqRecord = create.select(SB_SITEMENU_TB.ORDER_SQ.max())
							.from(SB_SITEMENU_TB.forceIndex("sb_sitemenu_idx1")).fetchOne();

					return maxOrderSqRecord.value1();
				}

				Record1<UInteger> parentMenuRecord = create.select(SB_SITEMENU_TB.PARENT_NO).from(SB_SITEMENU_TB)
						.where(SB_SITEMENU_TB.MENU_NO.eq(directParentNo)).fetchOne();

				if (null == parentMenuRecord) {
					String errorMessage = new StringBuilder().append("직계 조상 메뉴[menuNo=").append(directParentNo)
							.append("]가 없습니다").toString();
					throw new ServerServiceException(errorMessage);
				}

				directParentNo = parentMenuRecord.getValue(SB_SITEMENU_TB.PARENT_NO);
				continue;
			}

			UByte toOrderSeq = toOrderSeqRecord.getValue("toOrderSeq", UByte.class);
			return toOrderSeq;
		}
	}

	public static UShort getToGroupSeqOfRelativeRootBoard(DSLContext create, UByte boardID, UInteger groupNo,
			UShort groupSeq, UByte depth) {

		Result<Record2<UShort, UByte>> childBoardResult = create.select(SB_BOARD_TB.GROUP_SQ, SB_BOARD_TB.DEPTH)
				.from(SB_BOARD_TB).where(SB_BOARD_TB.BOARD_ID.eq(boardID)).and(SB_BOARD_TB.GROUP_NO.eq(groupNo))
				.and(SB_BOARD_TB.GROUP_SQ.lt(groupSeq)).orderBy(SB_BOARD_TB.GROUP_SQ.desc()).fetch();

		UShort toGroupSeq = groupSeq;

		for (Record2<UShort, UByte> childBoardRecord : childBoardResult) {
			UShort childGroupSeq = childBoardRecord.getValue(SB_BOARD_TB.GROUP_SQ);
			UByte childDepth = childBoardRecord.getValue(SB_BOARD_TB.DEPTH);

			if (childDepth.shortValue() <= depth.shortValue()) {
				break;
			}

			toGroupSeq = childGroupSeq;
		}

		return toGroupSeq;
	}

	/*
	 * public static MemberRoleType getValidMemberRoleType(Connection conn,
	 * DSLContext create, InternalLogger log, String requestedUserID) throws
	 * ServerServiceException { if (null == requestedUserID) { try {
	 * conn.rollback(); } catch (Exception e) { log.warn("fail to rollback"); }
	 * 
	 * String errorMessage = "서비스 요청자를 입력해 주세요"; throw new
	 * ServerServiceException(errorMessage); }
	 * 
	 * MemberRoleType memberRoleTypeOfRequestedUserID = null;
	 * 
	 * Record2<Byte, Byte> memberRecord = create.select(SB_MEMBER_TB.STATE,
	 * SB_MEMBER_TB.ROLE).from(SB_MEMBER_TB)
	 * .where(SB_MEMBER_TB.USER_ID.eq(requestedUserID)).fetchOne();
	 * 
	 * if (null == memberRecord) { try { conn.rollback(); } catch (Exception e) {
	 * log.warn("fail to rollback"); }
	 * 
	 * String errorMessage = new StringBuilder("서비스 요청자[").append(requestedUserID).
	 * append("]가 회원 테이블에 존재하지 않습니다") .toString(); throw new
	 * ServerServiceException(errorMessage); }
	 * 
	 * byte memeberStateOfRequestedUserID =
	 * memberRecord.getValue(SB_MEMBER_TB.STATE); MemberStateType
	 * memberStateTypeOfRequestedUserID = null; try {
	 * memberStateTypeOfRequestedUserID =
	 * MemberStateType.valueOf(memeberStateOfRequestedUserID); } catch
	 * (IllegalArgumentException e) { try { conn.rollback(); } catch (Exception e1)
	 * { log.warn("fail to rollback"); }
	 * 
	 * String errorMessage = new
	 * StringBuilder("서비스 요청자[").append(requestedUserID).append("]의 회원 상태[")
	 * .append(memeberStateOfRequestedUserID).append("] 값이 잘못되었습니다").toString();
	 * 
	 * throw new ServerServiceException(errorMessage); }
	 * 
	 * if (! MemberStateType.OK.equals(memberStateTypeOfRequestedUserID)) { try {
	 * conn.rollback(); } catch (Exception e1) { log.warn("fail to rollback"); }
	 * 
	 * String errorMessage = new
	 * StringBuilder("서비스 요청자[").append(requestedUserID).append("]의 회원 상태[")
	 * .append(memberStateTypeOfRequestedUserID.getName()).append("]가 정상이 아닙니다").
	 * toString(); throw new ServerServiceException(errorMessage); }
	 * 
	 * byte memberRoleTypeValueOfRequestedUserID =
	 * memberRecord.getValue(SB_MEMBER_TB.ROLE);
	 * 
	 * try { memberRoleTypeOfRequestedUserID =
	 * MemberRoleType.valueOf(memberRoleTypeValueOfRequestedUserID); } catch
	 * (IllegalArgumentException e) { try { conn.rollback(); } catch (Exception e1)
	 * { log.warn("fail to rollback"); }
	 * 
	 * String errorMessage = new
	 * StringBuilder("서비스 요청자[").append(requestedUserID).append("]의 멤버 역활 유형[")
	 * .append(memberRoleTypeValueOfRequestedUserID).append("]이 잘못되어있습니다").toString(
	 * ); throw new ServerServiceException(errorMessage); }
	 * 
	 * return memberRoleTypeOfRequestedUserID; }
	 */

	/**
	 * <pre>
	 * 서비스 요청자가 지정한 서비스를 이용 가능한지 검사를 수행하며 
	 * 요청자의 회원 역활 유형을 반환한다, 만약 이용 권한이 없다면 예외를 던진다
	 * 서비스 이용 권한 유형으로는 (1) 관리자, (2) 일반회원, (3) 손님 이 있으며 
	 * 이 값에 따라 아래와 같이 회원 역활 유형이 정해진다
	 * - 서비스 이용 권한 유형에 따른 이용 가능한 회원 역활 유형 정리표 -
	 * ----------------------------------------------
	 * 서비스 이용 권한 유형	::	회원 역활 유형
	 * ----------------------------------------------
	 * 관리자			::	관리자
	 * 일반회원			::	관리자, 일반회원
	 * 손님			::	관리자, 일반회원, 손님
	 * ----------------------------------------------
	 * </pre>
	 * 
	 * @param conn                    연결 객체
	 * @param create                  jooq DLSContext 객체
	 * @param log                     로그
	 * @param serviceName              서비스 이름
	 * @param servicePermissionType 서비스 이용 권한 유형
	 * @param requestedUserID         서비스 요청자
	 * @return 서비스 요청자의 회원 역활 유형
	 * @throws ServerServiceException 서비스 이용 권한이 없거나 기타 에러 발생시 던지는 예외
	 */
	public static MemberRoleType checkUserAccessRights(Connection conn, DSLContext create, InternalLogger log,
			String serviceName, PermissionType servicePermissionType, String requestedUserID)
			throws ServerServiceException {

		if (null == requestedUserID) {
			try {
				conn.rollback();
			} catch (Exception e) {
				log.warn("fail to rollback");
			}

			String errorMessage = "서비스 요청자를 입력해 주세요";
			throw new ServerServiceException(errorMessage);
		}

		

		Record2<Byte, Byte> memberRecord = create.select(SB_MEMBER_TB.STATE, SB_MEMBER_TB.ROLE).from(SB_MEMBER_TB)
				.where(SB_MEMBER_TB.USER_ID.eq(requestedUserID)).fetchOne();

		if (null == memberRecord) {
			try {
				conn.rollback();
			} catch (Exception e) {
				log.warn("fail to rollback");
			}

			String errorMessage = new StringBuilder("서비스 요청자[").append(requestedUserID).append("]가 회원 테이블에 존재하지 않습니다")
					.toString();
			throw new ServerServiceException(errorMessage);
		}

		byte memeberStateOfRequestedUserID = memberRecord.getValue(SB_MEMBER_TB.STATE);
		MemberStateType memberStateTypeOfRequestedUserID = null;
		try {
			memberStateTypeOfRequestedUserID = MemberStateType.valueOf(memeberStateOfRequestedUserID);
		} catch (IllegalArgumentException e) {
			try {
				conn.rollback();
			} catch (Exception e1) {
				log.warn("fail to rollback");
			}

			String errorMessage = new StringBuilder("서비스 요청자[").append(requestedUserID).append("]의 회원 상태[")
					.append(memberStateTypeOfRequestedUserID.getName()).append("] 값이 잘못되었습니다").toString();

			throw new ServerServiceException(errorMessage);
		}

		if (! MemberStateType.OK.equals(memberStateTypeOfRequestedUserID)) {
			try {
				conn.rollback();
			} catch (Exception e1) {
				log.warn("fail to rollback");
			}

			String errorMessage = new StringBuilder("서비스 요청자[").append(requestedUserID).append("]의 회원 상태[")
					.append(memberStateTypeOfRequestedUserID.getName()).append("]가 정상이 아닙니다").toString();
			throw new ServerServiceException(errorMessage);
		}

		
		byte memberRoleTypeValueOfRequestedUserID = memberRecord.getValue(SB_MEMBER_TB.ROLE);

		MemberRoleType memberRoleTypeOfRequestedUserID = null;
		try {
			memberRoleTypeOfRequestedUserID = MemberRoleType.valueOf(memberRoleTypeValueOfRequestedUserID);
		} catch (IllegalArgumentException e) {
			try {
				conn.rollback();
			} catch (Exception e1) {
				log.warn("fail to rollback");
			}

			String errorMessage = new StringBuilder("서비스 요청자[").append(requestedUserID).append("]의 멤버 역활 유형[")
					.append(memberRoleTypeValueOfRequestedUserID).append("]이 잘못되어있습니다").toString();
			throw new ServerServiceException(errorMessage);
		}

		
		if (PermissionType.ADMIN.equals(servicePermissionType)) {
			if (!MemberRoleType.ADMIN.equals(memberRoleTypeOfRequestedUserID)) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder().append(serviceName).append("는 관리자 전용 서비스입니다").toString();
				throw new ServerServiceException(errorMessage);
			}
		} else if (PermissionType.MEMBER.equals(servicePermissionType)) {
			if (MemberRoleType.GUEST.equals(memberRoleTypeOfRequestedUserID)) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder().append(serviceName).append("는 로그인 해야만 이용할 수 있습니다").toString();
				throw new ServerServiceException(errorMessage);
			}
		}

		return memberRoleTypeOfRequestedUserID;
	}

	public static void insertSiteLog(Connection conn, DSLContext create, InternalLogger log, String userID, 
			String logText, Timestamp registeredDate, String ip) throws Exception {
		
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		// String yyyyMMdd = sdf.format(registeredDate);
		
		Field<String> yyyyMMdd = DSL.field("date_format({0}, {1})", String.class, registeredDate, DSL.inline("%Y%m%d"));
		
		
		
		/** '일별 로그 순번' 동기화를 위해 락을 건다 */
		create.select(SB_SEQ_TB.SQ_ID).from(SB_SEQ_TB)
		.where(SB_SEQ_TB.SQ_ID.eq(SequenceType.SITE_LOG_LOCK.getSequenceID()))
		.forUpdate().fetchOne();
		
		UInteger dayLogSeq = create.select(DSL.field("if ({0} is null, {1}, {2})", 
				UInteger.class, SB_SITE_LOG_TB.DAY_LOG_SQ.max(), UInteger.valueOf(0), 
				SB_SITE_LOG_TB.DAY_LOG_SQ.max().add(1)))
		.from(SB_SITE_LOG_TB)
		.where(SB_SITE_LOG_TB.YYYYMMDD.eq(yyyyMMdd)).fetchOne().value1();
		
		create.insertInto(SB_SITE_LOG_TB)
		.set(SB_SITE_LOG_TB.YYYYMMDD, yyyyMMdd)
		.set(SB_SITE_LOG_TB.DAY_LOG_SQ, dayLogSeq)
		.set(SB_SITE_LOG_TB.USER_ID, userID)		
		.set(SB_SITE_LOG_TB.LOG_TXT, logText)		
		.set(SB_SITE_LOG_TB.REG_DT, registeredDate)
		.set(SB_SITE_LOG_TB.IP, ip).execute();
	}
	
	
	public static void insertMemberActivityHistory(Connection conn, DSLContext create, InternalLogger log,
			String userID, 
			MemberRoleType memberRoleType, MemberActivityType memberActivityType, UByte boardID, UInteger boardNo,
			Timestamp registeredDate) throws Exception {
		
		if (MemberRoleType.GUEST.equals(memberRoleType)) {
			/** 손님은 활동 이력 저장을 하지 않는다 */
			return;
		}
		
		/** '회원 이력 순번' 동기화를 위해 락을 건다 */
		create.select(SB_MEMBER_TB.USER_ID).from(SB_MEMBER_TB)
		.where(SB_MEMBER_TB.USER_ID.eq(userID))
		.forUpdate().fetchOne();
		
		Long activitySeq = create.select(DSL.field("if ({0} is null, {1}, {2})", 
				Long.class, SB_MEMBER_ACTIVITY_HISTORY_TB.ACTIVITY_SQ.max(), 
				0, SB_MEMBER_ACTIVITY_HISTORY_TB.ACTIVITY_SQ.max().add(1))).from(SB_MEMBER_ACTIVITY_HISTORY_TB)
				.where(SB_MEMBER_ACTIVITY_HISTORY_TB.USER_ID.eq(userID)).fetchOne().value1();
		
		create.insertInto(SB_MEMBER_ACTIVITY_HISTORY_TB)
		.set(SB_MEMBER_ACTIVITY_HISTORY_TB.USER_ID, userID)
		.set(SB_MEMBER_ACTIVITY_HISTORY_TB.ACTIVITY_SQ, activitySeq)
		.set(SB_MEMBER_ACTIVITY_HISTORY_TB.BOARD_ID, boardID)
		.set(SB_MEMBER_ACTIVITY_HISTORY_TB.BOARD_NO, boardNo)
		.set(SB_MEMBER_ACTIVITY_HISTORY_TB.ACTIVITY_TYPE, memberActivityType.getValue())
		.set(SB_MEMBER_ACTIVITY_HISTORY_TB.REG_DT, registeredDate).execute();
	}

	
	/**
	 * 지정한 게시글에 속한 그룹의 루트 노드에 해당하는 레코드에 락을 건후 그룹 번호를 반환한다
	 * @param conn JDBC 연결
	 * @param create DSLContext
	 * @param log 로거
	 * @param boardID 게시판 식별자
	 * @param boardNo 게시글 번호
	 * @return 지정한 게시글에 속한 그룹 번호
	 * @throws Exception 에러
	 */
	public static UInteger lockGroupOfGivenBoard(Connection conn, DSLContext create, InternalLogger log,
			UByte boardID, UInteger boardNo) throws Exception {
		/** 그룹 락 시작 */
		Record1<UInteger> groupRecord = create.select(SB_BOARD_TB.GROUP_NO)
		.from(SB_BOARD_TB).where(SB_BOARD_TB.BOARD_ID.eq(boardID)).and(SB_BOARD_TB.BOARD_NO.eq(boardNo))
		.fetchOne();
		
		if (null == groupRecord) {
			try {
				conn.rollback();
			} catch (Exception e) {
				log.warn("fail to rollback");
			}

			String errorMessage = new StringBuilder()
					.append("해당 게시글[boardID=")
					.append(boardID)
					.append(", boardNo=")
					.append(boardNo)
					.append("]이 존재 하지 않습니다").toString();
			throw new ServerServiceException(errorMessage);
		}
		
		UInteger groupNo = groupRecord.get(SB_BOARD_TB.GROUP_NO);
		
		Record1<UInteger> groupLockRecord = create
		.select(SB_BOARD_TB.BOARD_NO)
		.from(SB_BOARD_TB).where(SB_BOARD_TB.BOARD_ID.eq(boardID)).and(SB_BOARD_TB.BOARD_NO.eq(groupNo))
		.forUpdate().fetchOne();
		
		if (null == groupLockRecord) {
			try {
				conn.rollback();
			} catch (Exception e) {
				log.warn("fail to rollback");
			}

			String errorMessage = new StringBuilder()
					.append("그룹 루트 게시글[boardID=")
					.append(boardID)
					.append(", groupNo=")
					.append(groupNo)
					.append("]이 존재 하지 않습니다").toString(); 
					
					new StringBuilder("그룹 루트 게시글이 존재 하지 않습니다").toString();
			throw new ServerServiceException(errorMessage);
		}
		/** 그룹 락 종료 */
		
		return groupNo;
		
	}
	
	
	
	public static void execute(final String dbcpName, 
			final DBExecutorIF dbExecutor) throws Exception {
		DataSource dataSource = DBCPManager.getInstance().getBasicDataSource(dbcpName);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			
			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(dbcpName));
			
			dbExecutor.execute(conn, create); 
		} catch (ServerServiceException e) {
			throw e;
		} catch (Exception e) {
			if (null != conn) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					InternalLogger log = InternalLoggerFactory.getInstance(ServerDBUtil.class);
					log.warn("fail to rollback", e1);
				}
			}
			
			throw e;
		} finally {
			if (null != conn) {
				try {
					conn.close();
				} catch (Exception e) {
					InternalLogger log = InternalLoggerFactory.getInstance(ServerDBUtil.class);
					log.warn("fail to close the db connection", e);
				}
			}
		}
	}	
}
