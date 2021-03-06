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
import kr.pe.codda.jooq.tables.records.SbSiteLogTbRecord;

import org.jooq.Field;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
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
public class SbSiteLogTb extends TableImpl<SbSiteLogTbRecord> {

    private static final long serialVersionUID = 1790598054;

    /**
     * The reference instance of <code>sb_db.sb_site_log_tb</code>
     */
    public static final SbSiteLogTb SB_SITE_LOG_TB = new SbSiteLogTb();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<SbSiteLogTbRecord> getRecordType() {
        return SbSiteLogTbRecord.class;
    }

    /**
     * The column <code>sb_db.sb_site_log_tb.yyyymmdd</code>. 로그 날짜, yyyyMMdd 형태의 8자리 년월일 문자
     */
    public final TableField<SbSiteLogTbRecord, String> YYYYMMDD = createField("yyyymmdd", org.jooq.impl.SQLDataType.CHAR(8).nullable(false), this, "로그 날짜, yyyyMMdd 형태의 8자리 년월일 문자");

    /**
     * The column <code>sb_db.sb_site_log_tb.day_log_sq</code>. 일일 로그 순번
     */
    public final TableField<SbSiteLogTbRecord, UInteger> DAY_LOG_SQ = createField("day_log_sq", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false), this, "일일 로그 순번");

    /**
     * The column <code>sb_db.sb_site_log_tb.user_id</code>. 사용자 아이디
     */
    public final TableField<SbSiteLogTbRecord, String> USER_ID = createField("user_id", org.jooq.impl.SQLDataType.VARCHAR(20), this, "사용자 아이디");

    /**
     * The column <code>sb_db.sb_site_log_tb.log_txt</code>. 로그 내용, 로그로 남기는 내용 (1) 회원 가입, (2) 회원 탈퇴, (3) 사용자 차단, (4) 사용자 차단 해제, (5) 게시글 차단, (6) 게시글 차단 해제,  (7) 관리자 로그인
     */
    public final TableField<SbSiteLogTbRecord, String> LOG_TXT = createField("log_txt", org.jooq.impl.SQLDataType.CLOB, this, "로그 내용, 로그로 남기는 내용 (1) 회원 가입, (2) 회원 탈퇴, (3) 사용자 차단, (4) 사용자 차단 해제, (5) 게시글 차단, (6) 게시글 차단 해제,  (7) 관리자 로그인");

    /**
     * The column <code>sb_db.sb_site_log_tb.reg_dt</code>.
     */
    public final TableField<SbSiteLogTbRecord, Timestamp> REG_DT = createField("reg_dt", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

    /**
     * The column <code>sb_db.sb_site_log_tb.ip</code>.
     */
    public final TableField<SbSiteLogTbRecord, String> IP = createField("ip", org.jooq.impl.SQLDataType.VARCHAR(40), this, "");

    /**
     * Create a <code>sb_db.sb_site_log_tb</code> table reference
     */
    public SbSiteLogTb() {
        this(DSL.name("sb_site_log_tb"), null);
    }

    /**
     * Create an aliased <code>sb_db.sb_site_log_tb</code> table reference
     */
    public SbSiteLogTb(String alias) {
        this(DSL.name(alias), SB_SITE_LOG_TB);
    }

    /**
     * Create an aliased <code>sb_db.sb_site_log_tb</code> table reference
     */
    public SbSiteLogTb(Name alias) {
        this(alias, SB_SITE_LOG_TB);
    }

    private SbSiteLogTb(Name alias, Table<SbSiteLogTbRecord> aliased) {
        this(alias, aliased, null);
    }

    private SbSiteLogTb(Name alias, Table<SbSiteLogTbRecord> aliased, Field<?>[] parameters) {
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
        return Arrays.<Index>asList(Indexes.SB_SITE_LOG_TB_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<SbSiteLogTbRecord> getPrimaryKey() {
        return Keys.KEY_SB_SITE_LOG_TB_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<SbSiteLogTbRecord>> getKeys() {
        return Arrays.<UniqueKey<SbSiteLogTbRecord>>asList(Keys.KEY_SB_SITE_LOG_TB_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbSiteLogTb as(String alias) {
        return new SbSiteLogTb(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbSiteLogTb as(Name alias) {
        return new SbSiteLogTb(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public SbSiteLogTb rename(String name) {
        return new SbSiteLogTb(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public SbSiteLogTb rename(Name name) {
        return new SbSiteLogTb(name, null);
    }
}
