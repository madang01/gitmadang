/*
 * This file is generated by jOOQ.
*/
package kr.pe.codda.jooq.tables;


import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import kr.pe.codda.jooq.Indexes;
import kr.pe.codda.jooq.Keys;
import kr.pe.codda.jooq.SbDb;
import kr.pe.codda.jooq.tables.records.SbMemberActivityHistoryTbRecord;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.6"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class SbMemberActivityHistoryTb extends TableImpl<SbMemberActivityHistoryTbRecord> {

    private static final long serialVersionUID = 768247475;

    /**
     * The reference instance of <code>sb_db.sb_member_activity_history_tb</code>
     */
    public static final SbMemberActivityHistoryTb SB_MEMBER_ACTIVITY_HISTORY_TB = new SbMemberActivityHistoryTb();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<SbMemberActivityHistoryTbRecord> getRecordType() {
        return SbMemberActivityHistoryTbRecord.class;
    }

    /**
     * The column <code>sb_db.sb_member_activity_history_tb.user_id</code>. 사용자 아이디, 활동을 한 회원 아이디
     */
    public final TableField<SbMemberActivityHistoryTbRecord, String> USER_ID = createField("user_id", org.jooq.impl.SQLDataType.VARCHAR(20).nullable(false), this, "사용자 아이디, 활동을 한 회원 아이디");

    /**
     * The column <code>sb_db.sb_member_activity_history_tb.activity_sq</code>. 활동 순번, 0 부터 시작되며 이후 MAX + 1 이 된다
     */
    public final TableField<SbMemberActivityHistoryTbRecord, Long> ACTIVITY_SQ = createField("activity_sq", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "활동 순번, 0 부터 시작되며 이후 MAX + 1 이 된다");

    /**
     * The column <code>sb_db.sb_member_activity_history_tb.board_id</code>. 게시판 식별자,  활동  대상이 되는 게시글의 게시판 식별자
     */
    public final TableField<SbMemberActivityHistoryTbRecord, UByte> BOARD_ID = createField("board_id", org.jooq.impl.SQLDataType.TINYINTUNSIGNED.nullable(false), this, "게시판 식별자,  활동  대상이 되는 게시글의 게시판 식별자");

    /**
     * The column <code>sb_db.sb_member_activity_history_tb.board_no</code>. 게시판 번호, 활동 대상이 되는 게시글의 게시판 번호
     */
    public final TableField<SbMemberActivityHistoryTbRecord, UInteger> BOARD_NO = createField("board_no", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false), this, "게시판 번호, 활동 대상이 되는 게시글의 게시판 번호");

    /**
     * The column <code>sb_db.sb_member_activity_history_tb.activity_type</code>. 사용자 활동 종류, 'W'(=87):게시글 작성, 'R'(=82):게시글 댓글, 'V'(=86):게시글 추천, 'D'(=68):게시글 삭제, ' SELECT char(ascii('W') using ascii);
     */
    public final TableField<SbMemberActivityHistoryTbRecord, Byte> ACTIVITY_TYPE = createField("activity_type", org.jooq.impl.SQLDataType.TINYINT.nullable(false), this, "사용자 활동 종류, 'W'(=87):게시글 작성, 'R'(=82):게시글 댓글, 'V'(=86):게시글 추천, 'D'(=68):게시글 삭제, ' SELECT char(ascii('W') using ascii);");

    /**
     * The column <code>sb_db.sb_member_activity_history_tb.reg_dt</code>.
     */
    public final TableField<SbMemberActivityHistoryTbRecord, Timestamp> REG_DT = createField("reg_dt", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

    /**
     * Create a <code>sb_db.sb_member_activity_history_tb</code> table reference
     */
    public SbMemberActivityHistoryTb() {
        this(DSL.name("sb_member_activity_history_tb"), null);
    }

    /**
     * Create an aliased <code>sb_db.sb_member_activity_history_tb</code> table reference
     */
    public SbMemberActivityHistoryTb(String alias) {
        this(DSL.name(alias), SB_MEMBER_ACTIVITY_HISTORY_TB);
    }

    /**
     * Create an aliased <code>sb_db.sb_member_activity_history_tb</code> table reference
     */
    public SbMemberActivityHistoryTb(Name alias) {
        this(alias, SB_MEMBER_ACTIVITY_HISTORY_TB);
    }

    private SbMemberActivityHistoryTb(Name alias, Table<SbMemberActivityHistoryTbRecord> aliased) {
        this(alias, aliased, null);
    }

    private SbMemberActivityHistoryTb(Name alias, Table<SbMemberActivityHistoryTbRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return SbDb.SB_DB;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.SB_MEMBER_ACTIVITY_HISTORY_TB_BOARD_ID_IDX, Indexes.SB_MEMBER_ACTIVITY_HISTORY_TB_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<SbMemberActivityHistoryTbRecord> getPrimaryKey() {
        return Keys.KEY_SB_MEMBER_ACTIVITY_HISTORY_TB_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<SbMemberActivityHistoryTbRecord>> getKeys() {
        return Arrays.<UniqueKey<SbMemberActivityHistoryTbRecord>>asList(Keys.KEY_SB_MEMBER_ACTIVITY_HISTORY_TB_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<SbMemberActivityHistoryTbRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<SbMemberActivityHistoryTbRecord, ?>>asList(Keys.MEMBER_ACTIVITY_HISTORY_FK1, Keys.MEMBER_ACTIVITY_HISOTRY_FK2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbMemberActivityHistoryTb as(String alias) {
        return new SbMemberActivityHistoryTb(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbMemberActivityHistoryTb as(Name alias) {
        return new SbMemberActivityHistoryTb(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public SbMemberActivityHistoryTb rename(String name) {
        return new SbMemberActivityHistoryTb(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public SbMemberActivityHistoryTb rename(Name name) {
        return new SbMemberActivityHistoryTb(name, null);
    }
}