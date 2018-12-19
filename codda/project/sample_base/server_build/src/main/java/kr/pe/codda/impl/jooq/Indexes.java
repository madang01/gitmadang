/*
 * This file is generated by jOOQ.
*/
package kr.pe.codda.impl.jooq;


import javax.annotation.Generated;

import kr.pe.codda.impl.jooq.tables.SbBoardFilelistTb;
import kr.pe.codda.impl.jooq.tables.SbBoardHistoryTb;
import kr.pe.codda.impl.jooq.tables.SbBoardInfoTb;
import kr.pe.codda.impl.jooq.tables.SbBoardTb;
import kr.pe.codda.impl.jooq.tables.SbBoardVoteTb;
import kr.pe.codda.impl.jooq.tables.SbGroupInfoTb;
import kr.pe.codda.impl.jooq.tables.SbGroupTb;
import kr.pe.codda.impl.jooq.tables.SbMemberTb;
import kr.pe.codda.impl.jooq.tables.SbSeqTb;
import kr.pe.codda.impl.jooq.tables.SbSitemenuTb;
import kr.pe.codda.impl.jooq.tables.SbUserActionHistoryTb;

import org.jooq.Index;
import org.jooq.OrderField;
import org.jooq.impl.Internal;


/**
 * A class modelling indexes of tables of the <code>sb_db</code> schema.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.6"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Indexes {

    // -------------------------------------------------------------------------
    // INDEX definitions
    // -------------------------------------------------------------------------

    public static final Index SB_BOARD_FILELIST_TB_PRIMARY = Indexes0.SB_BOARD_FILELIST_TB_PRIMARY;
    public static final Index SB_BOARD_HISTORY_TB_PRIMARY = Indexes0.SB_BOARD_HISTORY_TB_PRIMARY;
    public static final Index SB_BOARD_HISTORY_TB_SB_BOARD_HISTORY_FK2_IDX = Indexes0.SB_BOARD_HISTORY_TB_SB_BOARD_HISTORY_FK2_IDX;
    public static final Index SB_BOARD_INFO_TB_PRIMARY = Indexes0.SB_BOARD_INFO_TB_PRIMARY;
    public static final Index SB_BOARD_TB_PRIMARY = Indexes0.SB_BOARD_TB_PRIMARY;
    public static final Index SB_BOARD_TB_SB_BOARD_FK1_IDX = Indexes0.SB_BOARD_TB_SB_BOARD_FK1_IDX;
    public static final Index SB_BOARD_TB_SB_BOARD_IDX1 = Indexes0.SB_BOARD_TB_SB_BOARD_IDX1;
    public static final Index SB_BOARD_TB_SB_BOARD_IDX2 = Indexes0.SB_BOARD_TB_SB_BOARD_IDX2;
    public static final Index SB_BOARD_VOTE_TB_PRIMARY = Indexes0.SB_BOARD_VOTE_TB_PRIMARY;
    public static final Index SB_BOARD_VOTE_TB_SB_BOARD_VOTE_FK1 = Indexes0.SB_BOARD_VOTE_TB_SB_BOARD_VOTE_FK1;
    public static final Index SB_BOARD_VOTE_TB_SB_BOARD_VOTE_FK2 = Indexes0.SB_BOARD_VOTE_TB_SB_BOARD_VOTE_FK2;
    public static final Index SB_GROUP_INFO_TB_PRIMARY = Indexes0.SB_GROUP_INFO_TB_PRIMARY;
    public static final Index SB_GROUP_TB_PRIMARY = Indexes0.SB_GROUP_TB_PRIMARY;
    public static final Index SB_GROUP_TB_SB_GROUP_FK2_IDX = Indexes0.SB_GROUP_TB_SB_GROUP_FK2_IDX;
    public static final Index SB_MEMBER_TB_PRIMARY = Indexes0.SB_MEMBER_TB_PRIMARY;
    public static final Index SB_MEMBER_TB_SB_MEMBER_IDX1 = Indexes0.SB_MEMBER_TB_SB_MEMBER_IDX1;
    public static final Index SB_MEMBER_TB_SB_MEMBER_IDX2 = Indexes0.SB_MEMBER_TB_SB_MEMBER_IDX2;
    public static final Index SB_SEQ_TB_PRIMARY = Indexes0.SB_SEQ_TB_PRIMARY;
    public static final Index SB_SITEMENU_TB_PRIMARY = Indexes0.SB_SITEMENU_TB_PRIMARY;
    public static final Index SB_SITEMENU_TB_SB_SITEMENU_IDX1 = Indexes0.SB_SITEMENU_TB_SB_SITEMENU_IDX1;
    public static final Index SB_SITEMENU_TB_SB_SITEMENU_IDX2 = Indexes0.SB_SITEMENU_TB_SB_SITEMENU_IDX2;
    public static final Index SB_USER_ACTION_HISTORY_TB_PRIMARY = Indexes0.SB_USER_ACTION_HISTORY_TB_PRIMARY;

    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Indexes0 {
        public static Index SB_BOARD_FILELIST_TB_PRIMARY = Internal.createIndex("PRIMARY", SbBoardFilelistTb.SB_BOARD_FILELIST_TB, new OrderField[] { SbBoardFilelistTb.SB_BOARD_FILELIST_TB.BOARD_ID, SbBoardFilelistTb.SB_BOARD_FILELIST_TB.BOARD_NO, SbBoardFilelistTb.SB_BOARD_FILELIST_TB.ATTACHED_FILE_SQ }, true);
        public static Index SB_BOARD_HISTORY_TB_PRIMARY = Internal.createIndex("PRIMARY", SbBoardHistoryTb.SB_BOARD_HISTORY_TB, new OrderField[] { SbBoardHistoryTb.SB_BOARD_HISTORY_TB.BOARD_ID, SbBoardHistoryTb.SB_BOARD_HISTORY_TB.BOARD_NO, SbBoardHistoryTb.SB_BOARD_HISTORY_TB.HISTORY_SQ }, true);
        public static Index SB_BOARD_HISTORY_TB_SB_BOARD_HISTORY_FK2_IDX = Internal.createIndex("sb_board_history_fk2_idx", SbBoardHistoryTb.SB_BOARD_HISTORY_TB, new OrderField[] { SbBoardHistoryTb.SB_BOARD_HISTORY_TB.MODIFIER_ID }, false);
        public static Index SB_BOARD_INFO_TB_PRIMARY = Internal.createIndex("PRIMARY", SbBoardInfoTb.SB_BOARD_INFO_TB, new OrderField[] { SbBoardInfoTb.SB_BOARD_INFO_TB.BOARD_ID }, true);
        public static Index SB_BOARD_TB_PRIMARY = Internal.createIndex("PRIMARY", SbBoardTb.SB_BOARD_TB, new OrderField[] { SbBoardTb.SB_BOARD_TB.BOARD_ID, SbBoardTb.SB_BOARD_TB.BOARD_NO }, true);
        public static Index SB_BOARD_TB_SB_BOARD_FK1_IDX = Internal.createIndex("sb_board_fk1_idx", SbBoardTb.SB_BOARD_TB, new OrderField[] { SbBoardTb.SB_BOARD_TB.BOARD_ID }, false);
        public static Index SB_BOARD_TB_SB_BOARD_IDX1 = Internal.createIndex("sb_board_idx1", SbBoardTb.SB_BOARD_TB, new OrderField[] { SbBoardTb.SB_BOARD_TB.BOARD_ID, SbBoardTb.SB_BOARD_TB.GROUP_NO, SbBoardTb.SB_BOARD_TB.GROUP_SQ }, true);
        public static Index SB_BOARD_TB_SB_BOARD_IDX2 = Internal.createIndex("sb_board_idx2", SbBoardTb.SB_BOARD_TB, new OrderField[] { SbBoardTb.SB_BOARD_TB.BOARD_ID, SbBoardTb.SB_BOARD_TB.PARENT_NO, SbBoardTb.SB_BOARD_TB.GROUP_SQ }, false);
        public static Index SB_BOARD_VOTE_TB_PRIMARY = Internal.createIndex("PRIMARY", SbBoardVoteTb.SB_BOARD_VOTE_TB, new OrderField[] { SbBoardVoteTb.SB_BOARD_VOTE_TB.BOARD_NO, SbBoardVoteTb.SB_BOARD_VOTE_TB.USER_ID, SbBoardVoteTb.SB_BOARD_VOTE_TB.BOARD_ID }, true);
        public static Index SB_BOARD_VOTE_TB_SB_BOARD_VOTE_FK1 = Internal.createIndex("sb_board_vote_fk1", SbBoardVoteTb.SB_BOARD_VOTE_TB, new OrderField[] { SbBoardVoteTb.SB_BOARD_VOTE_TB.BOARD_ID, SbBoardVoteTb.SB_BOARD_VOTE_TB.BOARD_NO }, false);
        public static Index SB_BOARD_VOTE_TB_SB_BOARD_VOTE_FK2 = Internal.createIndex("sb_board_vote_fk2", SbBoardVoteTb.SB_BOARD_VOTE_TB, new OrderField[] { SbBoardVoteTb.SB_BOARD_VOTE_TB.USER_ID }, false);
        public static Index SB_GROUP_INFO_TB_PRIMARY = Internal.createIndex("PRIMARY", SbGroupInfoTb.SB_GROUP_INFO_TB, new OrderField[] { SbGroupInfoTb.SB_GROUP_INFO_TB.GROUP_ID }, true);
        public static Index SB_GROUP_TB_PRIMARY = Internal.createIndex("PRIMARY", SbGroupTb.SB_GROUP_TB, new OrderField[] { SbGroupTb.SB_GROUP_TB.GROUP_ID, SbGroupTb.SB_GROUP_TB.USER_ID }, true);
        public static Index SB_GROUP_TB_SB_GROUP_FK2_IDX = Internal.createIndex("sb_group_fk2_idx", SbGroupTb.SB_GROUP_TB, new OrderField[] { SbGroupTb.SB_GROUP_TB.USER_ID }, false);
        public static Index SB_MEMBER_TB_PRIMARY = Internal.createIndex("PRIMARY", SbMemberTb.SB_MEMBER_TB, new OrderField[] { SbMemberTb.SB_MEMBER_TB.USER_ID }, true);
        public static Index SB_MEMBER_TB_SB_MEMBER_IDX1 = Internal.createIndex("sb_member_idx1", SbMemberTb.SB_MEMBER_TB, new OrderField[] { SbMemberTb.SB_MEMBER_TB.NICKNAME }, true);
        public static Index SB_MEMBER_TB_SB_MEMBER_IDX2 = Internal.createIndex("sb_member_idx2", SbMemberTb.SB_MEMBER_TB, new OrderField[] { SbMemberTb.SB_MEMBER_TB.MEMBER_ST }, false);
        public static Index SB_SEQ_TB_PRIMARY = Internal.createIndex("PRIMARY", SbSeqTb.SB_SEQ_TB, new OrderField[] { SbSeqTb.SB_SEQ_TB.SQ_ID }, true);
        public static Index SB_SITEMENU_TB_PRIMARY = Internal.createIndex("PRIMARY", SbSitemenuTb.SB_SITEMENU_TB, new OrderField[] { SbSitemenuTb.SB_SITEMENU_TB.MENU_NO }, true);
        public static Index SB_SITEMENU_TB_SB_SITEMENU_IDX1 = Internal.createIndex("sb_sitemenu_idx1", SbSitemenuTb.SB_SITEMENU_TB, new OrderField[] { SbSitemenuTb.SB_SITEMENU_TB.ORDER_SQ }, false);
        public static Index SB_SITEMENU_TB_SB_SITEMENU_IDX2 = Internal.createIndex("sb_sitemenu_idx2", SbSitemenuTb.SB_SITEMENU_TB, new OrderField[] { SbSitemenuTb.SB_SITEMENU_TB.PARENT_NO, SbSitemenuTb.SB_SITEMENU_TB.ORDER_SQ }, false);
        public static Index SB_USER_ACTION_HISTORY_TB_PRIMARY = Internal.createIndex("PRIMARY", SbUserActionHistoryTb.SB_USER_ACTION_HISTORY_TB, new OrderField[] { SbUserActionHistoryTb.SB_USER_ACTION_HISTORY_TB.NO }, true);
    }
}
