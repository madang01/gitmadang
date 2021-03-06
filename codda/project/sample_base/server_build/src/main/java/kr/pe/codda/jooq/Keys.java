/*
 * This file is generated by jOOQ.
*/
package kr.pe.codda.jooq;


import javax.annotation.Generated;

import kr.pe.codda.jooq.tables.SbAccountSerarchTb;
import kr.pe.codda.jooq.tables.SbBoardFilelistTb;
import kr.pe.codda.jooq.tables.SbBoardHistoryTb;
import kr.pe.codda.jooq.tables.SbBoardInfoTb;
import kr.pe.codda.jooq.tables.SbBoardTb;
import kr.pe.codda.jooq.tables.SbBoardVoteTb;
import kr.pe.codda.jooq.tables.SbMemberActivityHistoryTb;
import kr.pe.codda.jooq.tables.SbMemberTb;
import kr.pe.codda.jooq.tables.SbSeqTb;
import kr.pe.codda.jooq.tables.SbSiteLogTb;
import kr.pe.codda.jooq.tables.SbSitemenuTb;
import kr.pe.codda.jooq.tables.records.SbAccountSerarchTbRecord;
import kr.pe.codda.jooq.tables.records.SbBoardFilelistTbRecord;
import kr.pe.codda.jooq.tables.records.SbBoardHistoryTbRecord;
import kr.pe.codda.jooq.tables.records.SbBoardInfoTbRecord;
import kr.pe.codda.jooq.tables.records.SbBoardTbRecord;
import kr.pe.codda.jooq.tables.records.SbBoardVoteTbRecord;
import kr.pe.codda.jooq.tables.records.SbMemberActivityHistoryTbRecord;
import kr.pe.codda.jooq.tables.records.SbMemberTbRecord;
import kr.pe.codda.jooq.tables.records.SbSeqTbRecord;
import kr.pe.codda.jooq.tables.records.SbSiteLogTbRecord;
import kr.pe.codda.jooq.tables.records.SbSitemenuTbRecord;

import org.jooq.ForeignKey;
import org.jooq.UniqueKey;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables of 
 * the <code>sb_db</code> schema.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.6"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // IDENTITY definitions
    // -------------------------------------------------------------------------


    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<SbAccountSerarchTbRecord> KEY_SB_ACCOUNT_SERARCH_TB_PRIMARY = UniqueKeys0.KEY_SB_ACCOUNT_SERARCH_TB_PRIMARY;
    public static final UniqueKey<SbBoardFilelistTbRecord> KEY_SB_BOARD_FILELIST_TB_PRIMARY = UniqueKeys0.KEY_SB_BOARD_FILELIST_TB_PRIMARY;
    public static final UniqueKey<SbBoardHistoryTbRecord> KEY_SB_BOARD_HISTORY_TB_PRIMARY = UniqueKeys0.KEY_SB_BOARD_HISTORY_TB_PRIMARY;
    public static final UniqueKey<SbBoardInfoTbRecord> KEY_SB_BOARD_INFO_TB_PRIMARY = UniqueKeys0.KEY_SB_BOARD_INFO_TB_PRIMARY;
    public static final UniqueKey<SbBoardTbRecord> KEY_SB_BOARD_TB_PRIMARY = UniqueKeys0.KEY_SB_BOARD_TB_PRIMARY;
    public static final UniqueKey<SbBoardVoteTbRecord> KEY_SB_BOARD_VOTE_TB_PRIMARY = UniqueKeys0.KEY_SB_BOARD_VOTE_TB_PRIMARY;
    public static final UniqueKey<SbMemberActivityHistoryTbRecord> KEY_SB_MEMBER_ACTIVITY_HISTORY_TB_PRIMARY = UniqueKeys0.KEY_SB_MEMBER_ACTIVITY_HISTORY_TB_PRIMARY;
    public static final UniqueKey<SbMemberTbRecord> KEY_SB_MEMBER_TB_PRIMARY = UniqueKeys0.KEY_SB_MEMBER_TB_PRIMARY;
    public static final UniqueKey<SbMemberTbRecord> KEY_SB_MEMBER_TB_SB_MEMBER_IDX1 = UniqueKeys0.KEY_SB_MEMBER_TB_SB_MEMBER_IDX1;
    public static final UniqueKey<SbMemberTbRecord> KEY_SB_MEMBER_TB_EMAIL_UNIQUE = UniqueKeys0.KEY_SB_MEMBER_TB_EMAIL_UNIQUE;
    public static final UniqueKey<SbSeqTbRecord> KEY_SB_SEQ_TB_PRIMARY = UniqueKeys0.KEY_SB_SEQ_TB_PRIMARY;
    public static final UniqueKey<SbSitemenuTbRecord> KEY_SB_SITEMENU_TB_PRIMARY = UniqueKeys0.KEY_SB_SITEMENU_TB_PRIMARY;
    public static final UniqueKey<SbSiteLogTbRecord> KEY_SB_SITE_LOG_TB_PRIMARY = UniqueKeys0.KEY_SB_SITE_LOG_TB_PRIMARY;

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------

    public static final ForeignKey<SbAccountSerarchTbRecord, SbMemberTbRecord> PWD_SEARCH_FK1 = ForeignKeys0.PWD_SEARCH_FK1;
    public static final ForeignKey<SbBoardFilelistTbRecord, SbBoardTbRecord> SB_BOARD_FILELIST_FK1 = ForeignKeys0.SB_BOARD_FILELIST_FK1;
    public static final ForeignKey<SbBoardHistoryTbRecord, SbBoardTbRecord> SB_BOARD_HISTORY_FK1 = ForeignKeys0.SB_BOARD_HISTORY_FK1;
    public static final ForeignKey<SbBoardHistoryTbRecord, SbMemberTbRecord> SB_BOARD_HISTORY_FK2 = ForeignKeys0.SB_BOARD_HISTORY_FK2;
    public static final ForeignKey<SbBoardTbRecord, SbBoardInfoTbRecord> SB_BOARD_FK1 = ForeignKeys0.SB_BOARD_FK1;
    public static final ForeignKey<SbBoardVoteTbRecord, SbBoardTbRecord> SB_BOARD_VOTE_FK1 = ForeignKeys0.SB_BOARD_VOTE_FK1;
    public static final ForeignKey<SbBoardVoteTbRecord, SbMemberTbRecord> SB_BOARD_VOTE_FK2 = ForeignKeys0.SB_BOARD_VOTE_FK2;
    public static final ForeignKey<SbMemberActivityHistoryTbRecord, SbMemberTbRecord> MEMBER_ACTIVITY_HISTORY_FK1 = ForeignKeys0.MEMBER_ACTIVITY_HISTORY_FK1;
    public static final ForeignKey<SbMemberActivityHistoryTbRecord, SbBoardTbRecord> MEMBER_ACTIVITY_HISOTRY_FK2 = ForeignKeys0.MEMBER_ACTIVITY_HISOTRY_FK2;

    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class UniqueKeys0 {
        public static final UniqueKey<SbAccountSerarchTbRecord> KEY_SB_ACCOUNT_SERARCH_TB_PRIMARY = Internal.createUniqueKey(SbAccountSerarchTb.SB_ACCOUNT_SERARCH_TB, "KEY_sb_account_serarch_tb_PRIMARY", SbAccountSerarchTb.SB_ACCOUNT_SERARCH_TB.USER_ID);
        public static final UniqueKey<SbBoardFilelistTbRecord> KEY_SB_BOARD_FILELIST_TB_PRIMARY = Internal.createUniqueKey(SbBoardFilelistTb.SB_BOARD_FILELIST_TB, "KEY_sb_board_filelist_tb_PRIMARY", SbBoardFilelistTb.SB_BOARD_FILELIST_TB.BOARD_ID, SbBoardFilelistTb.SB_BOARD_FILELIST_TB.BOARD_NO, SbBoardFilelistTb.SB_BOARD_FILELIST_TB.ATTACHED_FILE_SQ);
        public static final UniqueKey<SbBoardHistoryTbRecord> KEY_SB_BOARD_HISTORY_TB_PRIMARY = Internal.createUniqueKey(SbBoardHistoryTb.SB_BOARD_HISTORY_TB, "KEY_sb_board_history_tb_PRIMARY", SbBoardHistoryTb.SB_BOARD_HISTORY_TB.BOARD_ID, SbBoardHistoryTb.SB_BOARD_HISTORY_TB.BOARD_NO, SbBoardHistoryTb.SB_BOARD_HISTORY_TB.HISTORY_SQ);
        public static final UniqueKey<SbBoardInfoTbRecord> KEY_SB_BOARD_INFO_TB_PRIMARY = Internal.createUniqueKey(SbBoardInfoTb.SB_BOARD_INFO_TB, "KEY_sb_board_info_tb_PRIMARY", SbBoardInfoTb.SB_BOARD_INFO_TB.BOARD_ID);
        public static final UniqueKey<SbBoardTbRecord> KEY_SB_BOARD_TB_PRIMARY = Internal.createUniqueKey(SbBoardTb.SB_BOARD_TB, "KEY_sb_board_tb_PRIMARY", SbBoardTb.SB_BOARD_TB.BOARD_ID, SbBoardTb.SB_BOARD_TB.BOARD_NO);
        public static final UniqueKey<SbBoardVoteTbRecord> KEY_SB_BOARD_VOTE_TB_PRIMARY = Internal.createUniqueKey(SbBoardVoteTb.SB_BOARD_VOTE_TB, "KEY_sb_board_vote_tb_PRIMARY", SbBoardVoteTb.SB_BOARD_VOTE_TB.BOARD_NO, SbBoardVoteTb.SB_BOARD_VOTE_TB.USER_ID, SbBoardVoteTb.SB_BOARD_VOTE_TB.BOARD_ID);
        public static final UniqueKey<SbMemberActivityHistoryTbRecord> KEY_SB_MEMBER_ACTIVITY_HISTORY_TB_PRIMARY = Internal.createUniqueKey(SbMemberActivityHistoryTb.SB_MEMBER_ACTIVITY_HISTORY_TB, "KEY_sb_member_activity_history_tb_PRIMARY", SbMemberActivityHistoryTb.SB_MEMBER_ACTIVITY_HISTORY_TB.USER_ID, SbMemberActivityHistoryTb.SB_MEMBER_ACTIVITY_HISTORY_TB.ACTIVITY_SQ);
        public static final UniqueKey<SbMemberTbRecord> KEY_SB_MEMBER_TB_PRIMARY = Internal.createUniqueKey(SbMemberTb.SB_MEMBER_TB, "KEY_sb_member_tb_PRIMARY", SbMemberTb.SB_MEMBER_TB.USER_ID);
        public static final UniqueKey<SbMemberTbRecord> KEY_SB_MEMBER_TB_SB_MEMBER_IDX1 = Internal.createUniqueKey(SbMemberTb.SB_MEMBER_TB, "KEY_sb_member_tb_sb_member_idx1", SbMemberTb.SB_MEMBER_TB.NICKNAME);
        public static final UniqueKey<SbMemberTbRecord> KEY_SB_MEMBER_TB_EMAIL_UNIQUE = Internal.createUniqueKey(SbMemberTb.SB_MEMBER_TB, "KEY_sb_member_tb_email_UNIQUE", SbMemberTb.SB_MEMBER_TB.EMAIL);
        public static final UniqueKey<SbSeqTbRecord> KEY_SB_SEQ_TB_PRIMARY = Internal.createUniqueKey(SbSeqTb.SB_SEQ_TB, "KEY_sb_seq_tb_PRIMARY", SbSeqTb.SB_SEQ_TB.SQ_ID);
        public static final UniqueKey<SbSitemenuTbRecord> KEY_SB_SITEMENU_TB_PRIMARY = Internal.createUniqueKey(SbSitemenuTb.SB_SITEMENU_TB, "KEY_sb_sitemenu_tb_PRIMARY", SbSitemenuTb.SB_SITEMENU_TB.MENU_NO);
        public static final UniqueKey<SbSiteLogTbRecord> KEY_SB_SITE_LOG_TB_PRIMARY = Internal.createUniqueKey(SbSiteLogTb.SB_SITE_LOG_TB, "KEY_sb_site_log_tb_PRIMARY", SbSiteLogTb.SB_SITE_LOG_TB.YYYYMMDD, SbSiteLogTb.SB_SITE_LOG_TB.DAY_LOG_SQ);
    }

    private static class ForeignKeys0 {
        public static final ForeignKey<SbAccountSerarchTbRecord, SbMemberTbRecord> PWD_SEARCH_FK1 = Internal.createForeignKey(kr.pe.codda.jooq.Keys.KEY_SB_MEMBER_TB_PRIMARY, SbAccountSerarchTb.SB_ACCOUNT_SERARCH_TB, "pwd_search_fk1", SbAccountSerarchTb.SB_ACCOUNT_SERARCH_TB.USER_ID);
        public static final ForeignKey<SbBoardFilelistTbRecord, SbBoardTbRecord> SB_BOARD_FILELIST_FK1 = Internal.createForeignKey(kr.pe.codda.jooq.Keys.KEY_SB_BOARD_TB_PRIMARY, SbBoardFilelistTb.SB_BOARD_FILELIST_TB, "sb_board_filelist_fk1", SbBoardFilelistTb.SB_BOARD_FILELIST_TB.BOARD_ID, SbBoardFilelistTb.SB_BOARD_FILELIST_TB.BOARD_NO);
        public static final ForeignKey<SbBoardHistoryTbRecord, SbBoardTbRecord> SB_BOARD_HISTORY_FK1 = Internal.createForeignKey(kr.pe.codda.jooq.Keys.KEY_SB_BOARD_TB_PRIMARY, SbBoardHistoryTb.SB_BOARD_HISTORY_TB, "sb_board_history_fk1", SbBoardHistoryTb.SB_BOARD_HISTORY_TB.BOARD_ID, SbBoardHistoryTb.SB_BOARD_HISTORY_TB.BOARD_NO);
        public static final ForeignKey<SbBoardHistoryTbRecord, SbMemberTbRecord> SB_BOARD_HISTORY_FK2 = Internal.createForeignKey(kr.pe.codda.jooq.Keys.KEY_SB_MEMBER_TB_PRIMARY, SbBoardHistoryTb.SB_BOARD_HISTORY_TB, "sb_board_history_fk2", SbBoardHistoryTb.SB_BOARD_HISTORY_TB.REGISTRANT_ID);
        public static final ForeignKey<SbBoardTbRecord, SbBoardInfoTbRecord> SB_BOARD_FK1 = Internal.createForeignKey(kr.pe.codda.jooq.Keys.KEY_SB_BOARD_INFO_TB_PRIMARY, SbBoardTb.SB_BOARD_TB, "sb_board_fk1", SbBoardTb.SB_BOARD_TB.BOARD_ID);
        public static final ForeignKey<SbBoardVoteTbRecord, SbBoardTbRecord> SB_BOARD_VOTE_FK1 = Internal.createForeignKey(kr.pe.codda.jooq.Keys.KEY_SB_BOARD_TB_PRIMARY, SbBoardVoteTb.SB_BOARD_VOTE_TB, "sb_board_vote_fk1", SbBoardVoteTb.SB_BOARD_VOTE_TB.BOARD_ID, SbBoardVoteTb.SB_BOARD_VOTE_TB.BOARD_NO);
        public static final ForeignKey<SbBoardVoteTbRecord, SbMemberTbRecord> SB_BOARD_VOTE_FK2 = Internal.createForeignKey(kr.pe.codda.jooq.Keys.KEY_SB_MEMBER_TB_PRIMARY, SbBoardVoteTb.SB_BOARD_VOTE_TB, "sb_board_vote_fk2", SbBoardVoteTb.SB_BOARD_VOTE_TB.USER_ID);
        public static final ForeignKey<SbMemberActivityHistoryTbRecord, SbMemberTbRecord> MEMBER_ACTIVITY_HISTORY_FK1 = Internal.createForeignKey(kr.pe.codda.jooq.Keys.KEY_SB_MEMBER_TB_PRIMARY, SbMemberActivityHistoryTb.SB_MEMBER_ACTIVITY_HISTORY_TB, "member_activity_history_fk1", SbMemberActivityHistoryTb.SB_MEMBER_ACTIVITY_HISTORY_TB.USER_ID);
        public static final ForeignKey<SbMemberActivityHistoryTbRecord, SbBoardTbRecord> MEMBER_ACTIVITY_HISOTRY_FK2 = Internal.createForeignKey(kr.pe.codda.jooq.Keys.KEY_SB_BOARD_TB_PRIMARY, SbMemberActivityHistoryTb.SB_MEMBER_ACTIVITY_HISTORY_TB, "member_activity_hisotry_fk2", SbMemberActivityHistoryTb.SB_MEMBER_ACTIVITY_HISTORY_TB.BOARD_ID, SbMemberActivityHistoryTb.SB_MEMBER_ACTIVITY_HISTORY_TB.BOARD_NO);
    }
}
