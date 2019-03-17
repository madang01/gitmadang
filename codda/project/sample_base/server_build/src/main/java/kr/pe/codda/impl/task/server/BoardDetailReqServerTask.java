package kr.pe.codda.impl.task.server;

import static kr.pe.codda.impl.jooq.tables.SbBoardFilelistTb.SB_BOARD_FILELIST_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardHistoryTb.SB_BOARD_HISTORY_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardInfoTb.SB_BOARD_INFO_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardTb.SB_BOARD_TB;
import static kr.pe.codda.impl.jooq.tables.SbBoardVoteTb.SB_BOARD_VOTE_TB;
import static kr.pe.codda.impl.jooq.tables.SbMemberTb.SB_MEMBER_TB;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record14;
import org.jooq.Record17;
import org.jooq.Record3;
import org.jooq.Record4;
import org.jooq.Record9;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.jooq.types.UShort;

import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.exception.DynamicClassCallException;
import kr.pe.codda.common.exception.ServerServiceException;
import kr.pe.codda.common.message.AbstractMessage;
import kr.pe.codda.impl.jooq.tables.SbBoardHistoryTb;
import kr.pe.codda.impl.jooq.tables.SbBoardTb;
import kr.pe.codda.impl.message.BoardDetailReq.BoardDetailReq;
import kr.pe.codda.impl.message.BoardDetailRes.BoardDetailRes;
import kr.pe.codda.impl.message.MessageResultRes.MessageResultRes;
import kr.pe.codda.server.PersonalLoginManagerIF;
import kr.pe.codda.server.dbcp.DBCPManager;
import kr.pe.codda.server.lib.BoardListType;
import kr.pe.codda.server.lib.BoardStateType;
import kr.pe.codda.server.lib.MemberRoleType;
import kr.pe.codda.server.lib.PermissionType;
import kr.pe.codda.server.lib.ServerCommonStaticFinalVars;
import kr.pe.codda.server.lib.ServerDBUtil;
import kr.pe.codda.server.task.AbstractServerTask;
import kr.pe.codda.server.task.ToLetterCarrier;

public class BoardDetailReqServerTask extends AbstractServerTask {
	public BoardDetailReqServerTask() throws DynamicClassCallException {
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
					(BoardDetailReq) inputMessage);
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

			sendErrorOutputMessage("게시글 가져오는데 실패하였습니다", toLetterCarrier, inputMessage);
			return;
		}
	}

	public BoardDetailRes doWork(String dbcpName, BoardDetailReq boardDetailReq) throws Exception {
		// FIXME!
		log.info(boardDetailReq.toString());

		if (boardDetailReq.getBoardNo() < 0
				|| boardDetailReq.getBoardNo() > CommonStaticFinalVars.UNSIGNED_INTEGER_MAX) {
			String errorMessage = "unsinged integer 를 벗어난 게시판 번호입니다";
			throw new ServerServiceException(errorMessage);
		}

		UByte boardID = UByte.valueOf(boardDetailReq.getBoardID());
		UInteger boardNo = UInteger.valueOf(boardDetailReq.getBoardNo());

		String boardName = null;
		byte boardListTypeValue;
		byte boardReplyPolicyTypeValue;
		byte boardReplyPermssionTypeValue;

		UInteger groupNo = null;
		UShort groupSeqence = null;
		UInteger parentNo = null;
		UByte depth = null;
		int oldViewCount = 0;
		String boardState = null;
		UByte nextAttachedFileSeq = null;
		boolean isBoardPassword = false;
		int votes = 0;
		String firstWriterID = null;
		Timestamp firstRegisteredDate = null;
		String firstWriterNickname = null;
		String subject = null;
		String contents = null;
		String lastModifierID = null;
		String lastModifierNickName = null;
		Timestamp lastModifedDate = null;

		List<BoardDetailRes.AttachedFile> attachedFileList = new ArrayList<BoardDetailRes.AttachedFile>();
		List<BoardDetailRes.ChildNode> childNodeList = new ArrayList<BoardDetailRes.ChildNode>();

		DataSource dataSource = DBCPManager.getInstance().getBasicDataSource(dbcpName);

		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);

			DSLContext create = DSL.using(conn, SQLDialect.MYSQL, ServerDBUtil.getDBCPSettings(dbcpName));

			Record4<String, Byte, Byte, Byte> boardInforRecord = create
					.select(SB_BOARD_INFO_TB.BOARD_NAME, SB_BOARD_INFO_TB.LIST_TYPE, SB_BOARD_INFO_TB.REPLY_POLICY_TYPE,
							SB_BOARD_INFO_TB.REPLY_PERMISSION_TYPE)
					.from(SB_BOARD_INFO_TB).where(SB_BOARD_INFO_TB.BOARD_ID.eq(boardID)).forUpdate().fetchOne();

			if (null == boardInforRecord) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder("입력 받은 게시판 식별자[").append(boardID.shortValue())
						.append("]가 게시판 정보 테이블에 존재하지  않습니다").toString();
				throw new ServerServiceException(errorMessage);
			}

			boardName = boardInforRecord.get(SB_BOARD_INFO_TB.BOARD_NAME);
			boardListTypeValue = boardInforRecord.get(SB_BOARD_INFO_TB.LIST_TYPE);			
			boardReplyPolicyTypeValue = boardInforRecord.get(SB_BOARD_INFO_TB.REPLY_POLICY_TYPE);
			boardReplyPermssionTypeValue = boardInforRecord.get(SB_BOARD_INFO_TB.REPLY_PERMISSION_TYPE);

			BoardListType boardListType = null;
			try {
				boardListType = BoardListType.valueOf(boardListTypeValue);
			} catch (IllegalArgumentException e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}

				String errorMessage = e.getMessage();
				throw new ServerServiceException(errorMessage);
			}
			
			
			MemberRoleType memberRoleTypeOfRequestedUserID = ServerDBUtil.checkUserAccessRights(conn, create, log,
					"게시글 상세 조회 서비스", PermissionType.GUEST, boardDetailReq.getRequestedUserID());

			SbBoardHistoryTb b = SB_BOARD_HISTORY_TB.as("b");
			SbBoardHistoryTb c = SB_BOARD_HISTORY_TB.as("c");

			Record17<UInteger, UShort, UInteger, UByte, Integer, String, UByte, String, Object, String, String, Timestamp, String, Object, Timestamp, String, Object> mainBoardRecord = create
					.select(SB_BOARD_TB.GROUP_NO, SB_BOARD_TB.GROUP_SQ, SB_BOARD_TB.PARENT_NO, SB_BOARD_TB.DEPTH,
							SB_BOARD_TB.VIEW_CNT, SB_BOARD_TB.BOARD_ST, SB_BOARD_TB.NEXT_ATTACHED_FILE_SQ,
							SB_BOARD_TB.PWD_BASE64,
							create.selectCount().from(SB_BOARD_VOTE_TB)
									.where(SB_BOARD_VOTE_TB.BOARD_ID.eq(SB_BOARD_TB.BOARD_ID))
									.and(SB_BOARD_VOTE_TB.BOARD_NO.eq(SB_BOARD_TB.BOARD_NO)).asField("votes"),
							b.SUBJECT, b.CONTENTS, b.REG_DT.as("last_modified_date"),
							b.REGISTRANT_ID.as("last_modifier_id"),
							create.select(SB_MEMBER_TB.NICKNAME).from(SB_MEMBER_TB)
									.where(SB_MEMBER_TB.USER_ID.eq(b.REGISTRANT_ID)).asField("last_modifier_nickname"),
							c.REG_DT.as("first_registered_date"), c.REGISTRANT_ID.as("first_writer_id"),
							create.select(SB_MEMBER_TB.NICKNAME).from(SB_MEMBER_TB)
									.where(SB_MEMBER_TB.USER_ID.eq(c.REGISTRANT_ID)).asField("first_writer_nickname"))
					.from(SB_BOARD_TB).innerJoin(c).on(c.BOARD_ID.eq(SB_BOARD_TB.field(SB_BOARD_TB.BOARD_ID)))
					.and(c.BOARD_NO.eq(SB_BOARD_TB.field(SB_BOARD_TB.BOARD_NO))).and(c.HISTORY_SQ.eq(UByte.valueOf(0)))
					.innerJoin(b).on(b.BOARD_ID.eq(SB_BOARD_TB.field(SB_BOARD_TB.BOARD_ID)))
					.and(b.BOARD_NO.eq(SB_BOARD_TB.field(SB_BOARD_TB.BOARD_NO)))
					.and(b.HISTORY_SQ.eq(create.select(b.HISTORY_SQ.max()).from(b)
							.where(b.BOARD_ID.eq(SB_BOARD_TB.field(SB_BOARD_TB.BOARD_ID)))
							.and(b.BOARD_NO.eq(SB_BOARD_TB.field(SB_BOARD_TB.BOARD_NO)))))
					.where(SB_BOARD_TB.BOARD_ID.eq(boardID)).and(SB_BOARD_TB.BOARD_NO.eq(boardNo))					
					.fetchOne();

			if (null == mainBoardRecord) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder("해당 게시글이 존재 하지 않습니다").toString();
				throw new ServerServiceException(errorMessage);
			}

			groupNo = mainBoardRecord.get(SB_BOARD_TB.GROUP_NO);
			groupSeqence = mainBoardRecord.get(SB_BOARD_TB.GROUP_SQ);
			parentNo = mainBoardRecord.get(SB_BOARD_TB.PARENT_NO);
			depth = mainBoardRecord.get(SB_BOARD_TB.DEPTH);
			oldViewCount = mainBoardRecord.get(SB_BOARD_TB.VIEW_CNT);
			boardState = mainBoardRecord.getValue(SB_BOARD_TB.BOARD_ST);
			nextAttachedFileSeq = mainBoardRecord.getValue(SB_BOARD_TB.NEXT_ATTACHED_FILE_SQ);
			String boardPwdBase64 = mainBoardRecord.get(SB_BOARD_TB.PWD_BASE64);
			votes = mainBoardRecord.get("votes", Integer.class);
			subject = mainBoardRecord.get(SB_BOARD_HISTORY_TB.SUBJECT);
			contents = mainBoardRecord.get(SB_BOARD_HISTORY_TB.CONTENTS);
			lastModifedDate = mainBoardRecord.get("last_modified_date", Timestamp.class);
			lastModifierID = mainBoardRecord.get("last_modifier_id", String.class);
			lastModifierNickName = mainBoardRecord.get("last_modifier_nickname", String.class);
			firstRegisteredDate = mainBoardRecord.get("first_registered_date", Timestamp.class);
			firstWriterID = mainBoardRecord.get("first_writer_id", String.class);
			firstWriterNickname = mainBoardRecord.get("first_writer_nickname", String.class);
			

			BoardStateType boardStateType = null;
			try {
				boardStateType = BoardStateType.valueOf(boardState, false);
			} catch (IllegalArgumentException e) {
				try {
					conn.rollback();
				} catch (Exception e1) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder("게시글의 DB 상태 값[").append(boardState).append("]이 잘못되었습니다")
						.toString();
				throw new ServerServiceException(errorMessage);
			}

			if (!BoardStateType.OK.equals(boardStateType)) {
				/** 게시판 상태가 비 정상인 경우 관리자에게만 허용된다 */
				if (!MemberRoleType.ADMIN.equals(memberRoleTypeOfRequestedUserID)) {
					try {
						conn.rollback();
					} catch (Exception e1) {
						log.warn("fail to rollback");
					}

					String errorMessage = null;

					if (BoardStateType.DELETE.equals(boardStateType)) {
						errorMessage = "해당 게시글은 삭제된 글입니다";
					} else if (BoardStateType.BLOCK.equals(boardStateType)) {
						errorMessage = "해당 게시글은 관리자에 의해 차단된 글입니다";
					} else if (BoardStateType.TREEBLOCK.equals(boardStateType)) {
						errorMessage = "해당 게시글은 관리자에 의해 차단된 글을 루트로 하는 트리에 속한 글입니다";
					} else {
						errorMessage = new StringBuilder().append("해당 게시글 상태[").append(boardStateType.getName())
								.append("]가 정상이 아닙니다").toString();
					}

					throw new ServerServiceException(errorMessage);
				}
			}
			
			/** 게시글 비밀번호 유무 */			
			isBoardPassword = (null != boardPwdBase64);

			if (BoardListType.ONLY_GROUP_ROOT.equals(boardListType)) {
				/** 본문으로만 이루어진 목록의 상세 조회는 본문에 대한 상세 조회만 허용 된다 */
				if (0L != parentNo.longValue()) {
					try {
						conn.rollback();
					} catch (Exception e1) {
						log.warn("fail to rollback");
					}

					String errorMessage = new StringBuilder().append(boardName).append(" 게시판[").append(boardID)
							.append("]은 본문로만 이루어진 목록을 갖는 게시판으로 본문만 상세 조회가 가능합니다. 상세 조회를 요청한 게시글[boardNo=")
							.append(boardNo).append("]은 본문이 아닙니다").toString();
					throw new ServerServiceException(errorMessage);
				}

				SbBoardTb a = SB_BOARD_TB.as("a");
				
				Table<Record3<UByte, UInteger, UShort>> d = create.select(a.BOARD_ID, a.GROUP_NO, a.GROUP_SQ)
						.from(a.forceIndex("sb_board_idx1")).where(a.BOARD_ID.eq(boardID)).and(a.GROUP_NO.eq(groupNo))
						.and(a.BOARD_NO.notEqual(boardNo))
						.asTable("d");

				Table<Record9<UByte, UInteger, UInteger, UShort, UInteger, UByte, Integer, String, UByte>> mainTable = create
						.select(a.BOARD_ID, a.BOARD_NO, a.GROUP_NO, a.GROUP_SQ, a.PARENT_NO, a.DEPTH, a.VIEW_CNT,
								a.BOARD_ST, a.NEXT_ATTACHED_FILE_SQ)
						.from(a).innerJoin(d).on(a.BOARD_ID.eq(d.field(SB_BOARD_TB.BOARD_ID)))
						.and(a.GROUP_NO.eq(d.field(SB_BOARD_TB.GROUP_NO)))
						.and(a.GROUP_SQ.eq(d.field(SB_BOARD_TB.GROUP_SQ))).asTable("a");				

				Result<Record14<UInteger, UShort, UInteger, UByte, String, UByte, Object, String, Timestamp, String, Object, Timestamp, String, Object>> childBoardResult = create
						.select(mainTable.field(SB_BOARD_TB.BOARD_NO), mainTable.field(SB_BOARD_TB.GROUP_SQ),
								mainTable.field(SB_BOARD_TB.PARENT_NO), mainTable.field(SB_BOARD_TB.DEPTH),
								mainTable.field(SB_BOARD_TB.BOARD_ST),
								mainTable.field(SB_BOARD_TB.NEXT_ATTACHED_FILE_SQ),
								create.selectCount().from(SB_BOARD_VOTE_TB)
								.where(SB_BOARD_VOTE_TB.BOARD_ID.eq(mainTable.field(SB_BOARD_TB.BOARD_ID)))
								.and(SB_BOARD_VOTE_TB.BOARD_NO.eq(mainTable.field(SB_BOARD_TB.BOARD_NO)))
								.asField("votes"),
								b.CONTENTS, b.REG_DT.as("last_modified_date"), b.REGISTRANT_ID.as("last_modifier_id"),
								create.select(SB_MEMBER_TB.NICKNAME).from(SB_MEMBER_TB)
										.where(SB_MEMBER_TB.USER_ID.eq(b.REGISTRANT_ID))
										.asField("last_modifier_nickname"),
								c.REG_DT.as("first_registered_date"), c.REGISTRANT_ID.as("first_writer_id"),
								create.select(SB_MEMBER_TB.NICKNAME).from(SB_MEMBER_TB)
										.where(SB_MEMBER_TB.USER_ID.eq(c.REGISTRANT_ID))
										.asField("first_writer_nickname"))
						.from(mainTable).innerJoin(c).on(c.BOARD_ID.eq(mainTable.field(SB_BOARD_TB.BOARD_ID)))
						.and(c.BOARD_NO.eq(mainTable.field(SB_BOARD_TB.BOARD_NO)))
						.and(c.HISTORY_SQ.eq(UByte.valueOf(0))).innerJoin(b)
						.on(b.BOARD_ID.eq(mainTable.field(SB_BOARD_TB.BOARD_ID)))
						.and(b.BOARD_NO.eq(mainTable.field(SB_BOARD_TB.BOARD_NO)))
						.and(b.HISTORY_SQ.eq(create.select(b.HISTORY_SQ.max()).from(b)
								.where(b.BOARD_ID.eq(mainTable.field(SB_BOARD_TB.BOARD_ID)))
								.and(b.BOARD_NO.eq(mainTable.field(SB_BOARD_TB.BOARD_NO)))))
						.orderBy(mainTable.field(SB_BOARD_TB.GROUP_NO).desc(),
								mainTable.field(SB_BOARD_TB.GROUP_SQ).desc())
						.fetch();

				for (Record childBoardRecord : childBoardResult) {
					UInteger childBoardNo = childBoardRecord.get(SB_BOARD_TB.BOARD_NO);
					UShort childGroupSeq = childBoardRecord.get(SB_BOARD_TB.GROUP_SQ);
					UInteger childParentNo = childBoardRecord.get(SB_BOARD_TB.PARENT_NO);
					UByte childDepth = childBoardRecord.get(SB_BOARD_TB.DEPTH);
					String childBoardState = childBoardRecord.getValue(SB_BOARD_TB.BOARD_ST);
					UByte childNextAttachedFileSeq = childBoardRecord.getValue(SB_BOARD_TB.NEXT_ATTACHED_FILE_SQ);
					int childVotes = childBoardRecord.get("votes", Integer.class);
					String childContents = childBoardRecord.get(SB_BOARD_HISTORY_TB.CONTENTS);
					Timestamp childLastModifedDate = childBoardRecord.get("last_modified_date", Timestamp.class);
					String childLastModifierID = childBoardRecord.get("last_modifier_id", String.class);
					String childLastModifierNickName = childBoardRecord.get("last_modifier_nickname", String.class);
					Timestamp childFirstRegisteredDate = childBoardRecord.get("first_registered_date", Timestamp.class);
					String childFirstWriterID = childBoardRecord.get("first_writer_id", String.class);
					String childFirstWriterNickname = childBoardRecord.get("first_writer_nickname", String.class);

					BoardDetailRes.ChildNode childNode = new BoardDetailRes.ChildNode();
					childNode.setBoardNo(childBoardNo.longValue());
					childNode.setGroupSeq(childGroupSeq.intValue());
					childNode.setParentNo(childParentNo.longValue());
					childNode.setDepth(childDepth.shortValue());
					childNode.setContents(childContents);
					childNode.setVotes(childVotes);
					childNode.setBoardSate(childBoardState);
					childNode.setFirstWriterID(childFirstWriterID);
					childNode.setFirstWriterNickname(childFirstWriterNickname);
					childNode.setFirstRegisteredDate(childFirstRegisteredDate);
					childNode.setLastModifierID(childLastModifierID);
					childNode.setLastModifierNickName(childLastModifierNickName);
					childNode.setLastModifiedDate(childLastModifedDate);
					childNode.setNextAttachedFileSeq(childNextAttachedFileSeq.shortValue());

					List<BoardDetailRes.ChildNode.AttachedFile> childAttachedFileList = new ArrayList<BoardDetailRes.ChildNode.AttachedFile>();

					Result<Record> attachFileListRecord = create.select().from(SB_BOARD_FILELIST_TB)
							.where(SB_BOARD_FILELIST_TB.BOARD_ID.eq(boardID))
							.and(SB_BOARD_FILELIST_TB.BOARD_NO.eq(boardNo)).fetch();

					for (Record attachFileRecord : attachFileListRecord) {
						BoardDetailRes.ChildNode.AttachedFile attachedFile = new BoardDetailRes.ChildNode.AttachedFile();
						attachedFile.setAttachedFileSeq(
								attachFileRecord.get(SB_BOARD_FILELIST_TB.ATTACHED_FILE_SQ).shortValue());
						attachedFile.setAttachedFileName(attachFileRecord.get(SB_BOARD_FILELIST_TB.ATTACHED_FNAME));
						attachedFile.setAttachedFileSize(attachFileRecord.get(SB_BOARD_FILELIST_TB.ATTACHED_FSIZE));
						childAttachedFileList.add(attachedFile);
					}

					childNode.setAttachedFileCnt(childAttachedFileList.size());
					childNode.setAttachedFileList(childAttachedFileList);

					childNodeList.add(childNode);
				}
			}

			Result<Record> attachFileListRecord = create.select().from(SB_BOARD_FILELIST_TB)
					.where(SB_BOARD_FILELIST_TB.BOARD_ID.eq(boardID)).and(SB_BOARD_FILELIST_TB.BOARD_NO.eq(boardNo))
					.fetch();

			for (Record attachFileRecord : attachFileListRecord) {
				BoardDetailRes.AttachedFile attachedFile = new BoardDetailRes.AttachedFile();
				attachedFile
						.setAttachedFileSeq(attachFileRecord.get(SB_BOARD_FILELIST_TB.ATTACHED_FILE_SQ).shortValue());
				attachedFile.setAttachedFileName(attachFileRecord.get(SB_BOARD_FILELIST_TB.ATTACHED_FNAME));
				attachedFile.setAttachedFileSize(attachFileRecord.get(SB_BOARD_FILELIST_TB.ATTACHED_FSIZE));
				attachedFileList.add(attachedFile);
			}

			int countOfViewCountUpdate = create.update(SB_BOARD_TB)
					.set(SB_BOARD_TB.VIEW_CNT, SB_BOARD_TB.VIEW_CNT.add(1)).where(SB_BOARD_TB.BOARD_ID.eq(boardID))
					.and(SB_BOARD_TB.BOARD_NO.eq(boardNo)).execute();

			if (0 == countOfViewCountUpdate) {
				try {
					conn.rollback();
				} catch (Exception e) {
					log.warn("fail to rollback");
				}

				String errorMessage = new StringBuilder("해당 게시글 읽은 횟수 갱신이 실패하였습니다").toString();
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

		BoardDetailRes boardDetailRes = new BoardDetailRes();
		boardDetailRes.setBoardID(boardDetailReq.getBoardID());
		boardDetailRes.setBoardName(boardName);
		boardDetailRes.setBoardListType(boardListTypeValue);
		boardDetailRes.setBoardReplyPolicyType(boardReplyPolicyTypeValue);
		boardDetailRes.setBoardReplyPermssionType(boardReplyPermssionTypeValue);
		boardDetailRes.setIsBoardPassword(isBoardPassword);

		boardDetailRes.setBoardNo(boardDetailReq.getBoardNo());
		boardDetailRes.setGroupNo(groupNo.longValue());
		boardDetailRes.setGroupSeq(groupSeqence.intValue());
		boardDetailRes.setParentNo(parentNo.longValue());
		boardDetailRes.setDepth(depth.shortValue());
		boardDetailRes.setViewCount(oldViewCount + 1);
		boardDetailRes.setNextAttachedFileSeq(nextAttachedFileSeq.shortValue());
		boardDetailRes.setBoardSate(boardState);
		boardDetailRes.setVotes(votes);

		boardDetailRes.setSubject(subject);
		boardDetailRes.setContents(contents);

		boardDetailRes.setFirstWriterID(firstWriterID);
		boardDetailRes.setFirstWriterNickname(firstWriterNickname);
		boardDetailRes.setFirstRegisteredDate(firstRegisteredDate);

		boardDetailRes.setLastModifierID(lastModifierID);
		boardDetailRes.setLastModifierNickName(lastModifierNickName);
		boardDetailRes.setLastModifiedDate(lastModifedDate);

		boardDetailRes.setAttachedFileCnt(attachedFileList.size());
		boardDetailRes.setAttachedFileList(attachedFileList);

		boardDetailRes.setChildNodeCnt(childNodeList.size());
		boardDetailRes.setChildNodeList(childNodeList);

		return boardDetailRes;
	}
}
