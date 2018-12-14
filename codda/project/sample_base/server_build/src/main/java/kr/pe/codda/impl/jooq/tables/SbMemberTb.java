/*
 * This file is generated by jOOQ.
*/
package kr.pe.codda.impl.jooq.tables;


import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import kr.pe.codda.impl.jooq.Indexes;
import kr.pe.codda.impl.jooq.Keys;
import kr.pe.codda.impl.jooq.SbDb;
import kr.pe.codda.impl.jooq.tables.records.SbMemberTbRecord;

import org.jooq.Field;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import org.jooq.types.UByte;


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
public class SbMemberTb extends TableImpl<SbMemberTbRecord> {

    private static final long serialVersionUID = 2080323558;

    /**
     * The reference instance of <code>sb_db.sb_member_tb</code>
     */
    public static final SbMemberTb SB_MEMBER_TB = new SbMemberTb();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<SbMemberTbRecord> getRecordType() {
        return SbMemberTbRecord.class;
    }

    /**
     * The column <code>sb_db.sb_member_tb.user_id</code>. 사용자 아이디
     */
    public final TableField<SbMemberTbRecord, String> USER_ID = createField("user_id", org.jooq.impl.SQLDataType.VARCHAR(20).nullable(false), this, "사용자 아이디");

    /**
     * The column <code>sb_db.sb_member_tb.nickname</code>. 별명
     */
    public final TableField<SbMemberTbRecord, String> NICKNAME = createField("nickname", org.jooq.impl.SQLDataType.VARCHAR(45).nullable(false), this, "별명");

    /**
     * The column <code>sb_db.sb_member_tb.pwd_base64</code>. 비밀번호, 비밀번호는 해쉬 값으로 변환되어 base64 형태로 저장된다.
     */
    public final TableField<SbMemberTbRecord, String> PWD_BASE64 = createField("pwd_base64", org.jooq.impl.SQLDataType.VARCHAR(88).defaultValue(org.jooq.impl.DSL.inline("NULL", org.jooq.impl.SQLDataType.VARCHAR)), this, "비밀번호, 비밀번호는 해쉬 값으로 변환되어 base64 형태로 저장된다.");

    /**
     * The column <code>sb_db.sb_member_tb.pwd_salt_base64</code>. 비밀번호를 해쉬로 바꿀때 역 추적 방해를 목적으로 함께 사용하는 랜덤 값
     */
    public final TableField<SbMemberTbRecord, String> PWD_SALT_BASE64 = createField("pwd_salt_base64", org.jooq.impl.SQLDataType.VARCHAR(12).defaultValue(org.jooq.impl.DSL.inline("NULL", org.jooq.impl.SQLDataType.VARCHAR)), this, "비밀번호를 해쉬로 바꿀때 역 추적 방해를 목적으로 함께 사용하는 랜덤 값");

    /**
     * The column <code>sb_db.sb_member_tb.member_type</code>. 회원 구분, A:관리자, M:일반회원
     */
    public final TableField<SbMemberTbRecord, String> MEMBER_TYPE = createField("member_type", org.jooq.impl.SQLDataType.CHAR(1).nullable(false), this, "회원 구분, A:관리자, M:일반회원");

    /**
     * The column <code>sb_db.sb_member_tb.member_st</code>. 회원 상태, Y : 정상, B:블락, W:탈퇴
     */
    public final TableField<SbMemberTbRecord, String> MEMBER_ST = createField("member_st", org.jooq.impl.SQLDataType.CHAR(1).defaultValue(org.jooq.impl.DSL.inline("NULL", org.jooq.impl.SQLDataType.CHAR)), this, "회원 상태, Y : 정상, B:블락, W:탈퇴");

    /**
     * The column <code>sb_db.sb_member_tb.pwd_hint</code>. 비밀번호 힌트, 비밀번호 분실시 답변 유도용 사용자한테 보여주는 힌트
     */
    public final TableField<SbMemberTbRecord, String> PWD_HINT = createField("pwd_hint", org.jooq.impl.SQLDataType.CLOB.defaultValue(org.jooq.impl.DSL.inline("NULL", org.jooq.impl.SQLDataType.CLOB)), this, "비밀번호 힌트, 비밀번호 분실시 답변 유도용 사용자한테 보여주는 힌트");

    /**
     * The column <code>sb_db.sb_member_tb.pwd_answer</code>. 비밀번호 답변, 비밀번호 분실시 맞춘다면 비밀번호 재 설정 혹은 비밀번호 초기화를 진행한다.
     */
    public final TableField<SbMemberTbRecord, String> PWD_ANSWER = createField("pwd_answer", org.jooq.impl.SQLDataType.CLOB.defaultValue(org.jooq.impl.DSL.inline("NULL", org.jooq.impl.SQLDataType.CLOB)), this, "비밀번호 답변, 비밀번호 분실시 맞춘다면 비밀번호 재 설정 혹은 비밀번호 초기화를 진행한다.");

    /**
     * The column <code>sb_db.sb_member_tb.pwd_fail_cnt</code>. 비밀번호 틀린 횟수, 로그인시 비밀번호 틀릴 경우 1 씩 증가하며 최대 n 번까지 시도 가능하다.  비밀번호를 맞쳤을 경우 0 으로 초기화 된다.
     */
    public final TableField<SbMemberTbRecord, UByte> PWD_FAIL_CNT = createField("pwd_fail_cnt", org.jooq.impl.SQLDataType.TINYINTUNSIGNED.defaultValue(org.jooq.impl.DSL.inline("NULL", org.jooq.impl.SQLDataType.TINYINTUNSIGNED)), this, "비밀번호 틀린 횟수, 로그인시 비밀번호 틀릴 경우 1 씩 증가하며 최대 n 번까지 시도 가능하다.  비밀번호를 맞쳤을 경우 0 으로 초기화 된다.");

    /**
     * The column <code>sb_db.sb_member_tb.reg_dt</code>. 회원 가입일
     */
    public final TableField<SbMemberTbRecord, Timestamp> REG_DT = createField("reg_dt", org.jooq.impl.SQLDataType.TIMESTAMP.defaultValue(org.jooq.impl.DSL.inline("NULL", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "회원 가입일");

    /**
     * The column <code>sb_db.sb_member_tb.mod_dt</code>. 회원 정보 수정일
     */
    public final TableField<SbMemberTbRecord, Timestamp> MOD_DT = createField("mod_dt", org.jooq.impl.SQLDataType.TIMESTAMP.defaultValue(org.jooq.impl.DSL.inline("NULL", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "회원 정보 수정일");

    /**
     * The column <code>sb_db.sb_member_tb.ip</code>.
     */
    public final TableField<SbMemberTbRecord, String> IP = createField("ip", org.jooq.impl.SQLDataType.VARCHAR(40).defaultValue(org.jooq.impl.DSL.inline("NULL", org.jooq.impl.SQLDataType.VARCHAR)), this, "");

    /**
     * Create a <code>sb_db.sb_member_tb</code> table reference
     */
    public SbMemberTb() {
        this(DSL.name("sb_member_tb"), null);
    }

    /**
     * Create an aliased <code>sb_db.sb_member_tb</code> table reference
     */
    public SbMemberTb(String alias) {
        this(DSL.name(alias), SB_MEMBER_TB);
    }

    /**
     * Create an aliased <code>sb_db.sb_member_tb</code> table reference
     */
    public SbMemberTb(Name alias) {
        this(alias, SB_MEMBER_TB);
    }

    private SbMemberTb(Name alias, Table<SbMemberTbRecord> aliased) {
        this(alias, aliased, null);
    }

    private SbMemberTb(Name alias, Table<SbMemberTbRecord> aliased, Field<?>[] parameters) {
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
        return Arrays.<Index>asList(Indexes.SB_MEMBER_TB_PRIMARY, Indexes.SB_MEMBER_TB_SB_MEMBER_IDX1, Indexes.SB_MEMBER_TB_SB_MEMBER_IDX2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<SbMemberTbRecord> getPrimaryKey() {
        return Keys.KEY_SB_MEMBER_TB_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<SbMemberTbRecord>> getKeys() {
        return Arrays.<UniqueKey<SbMemberTbRecord>>asList(Keys.KEY_SB_MEMBER_TB_PRIMARY, Keys.KEY_SB_MEMBER_TB_SB_MEMBER_IDX1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbMemberTb as(String alias) {
        return new SbMemberTb(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbMemberTb as(Name alias) {
        return new SbMemberTb(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public SbMemberTb rename(String name) {
        return new SbMemberTb(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public SbMemberTb rename(Name name) {
        return new SbMemberTb(name, null);
    }
}
