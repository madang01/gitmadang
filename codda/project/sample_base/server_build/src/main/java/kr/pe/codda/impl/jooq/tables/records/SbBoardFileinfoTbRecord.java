/*
 * This file is generated by jOOQ.
*/
package kr.pe.codda.impl.jooq.tables.records;


import java.sql.Timestamp;

import javax.annotation.Generated;

import kr.pe.codda.impl.jooq.tables.SbBoardFileinfoTb;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record5;
import org.jooq.Row5;
import org.jooq.impl.UpdatableRecordImpl;
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
public class SbBoardFileinfoTbRecord extends UpdatableRecordImpl<SbBoardFileinfoTbRecord> implements Record5<UInteger, String, String, Timestamp, Timestamp> {

    private static final long serialVersionUID = 1594634714;

    /**
     * Setter for <code>sb_db.sb_board_fileinfo_tb.attach_id</code>. 업로드 식별자, 자동증가
     */
    public void setAttachId(UInteger value) {
        set(0, value);
    }

    /**
     * Getter for <code>sb_db.sb_board_fileinfo_tb.attach_id</code>. 업로드 식별자, 자동증가
     */
    public UInteger getAttachId() {
        return (UInteger) get(0);
    }

    /**
     * Setter for <code>sb_db.sb_board_fileinfo_tb.owner_id</code>. 첨부 파일 등록자 아이디
     */
    public void setOwnerId(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>sb_db.sb_board_fileinfo_tb.owner_id</code>. 첨부 파일 등록자 아이디
     */
    public String getOwnerId() {
        return (String) get(1);
    }

    /**
     * Setter for <code>sb_db.sb_board_fileinfo_tb.ip</code>. 첨부 파일 등록자 IP 주소
     */
    public void setIp(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>sb_db.sb_board_fileinfo_tb.ip</code>. 첨부 파일 등록자 IP 주소
     */
    public String getIp() {
        return (String) get(2);
    }

    /**
     * Setter for <code>sb_db.sb_board_fileinfo_tb.reg_dt</code>. 최초 등록일
     */
    public void setRegDt(Timestamp value) {
        set(3, value);
    }

    /**
     * Getter for <code>sb_db.sb_board_fileinfo_tb.reg_dt</code>. 최초 등록일
     */
    public Timestamp getRegDt() {
        return (Timestamp) get(3);
    }

    /**
     * Setter for <code>sb_db.sb_board_fileinfo_tb.mod_dt</code>. 최근 수정일
     */
    public void setModDt(Timestamp value) {
        set(4, value);
    }

    /**
     * Getter for <code>sb_db.sb_board_fileinfo_tb.mod_dt</code>. 최근 수정일
     */
    public Timestamp getModDt() {
        return (Timestamp) get(4);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<UInteger> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record5 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row5<UInteger, String, String, Timestamp, Timestamp> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row5<UInteger, String, String, Timestamp, Timestamp> valuesRow() {
        return (Row5) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UInteger> field1() {
        return SbBoardFileinfoTb.SB_BOARD_FILEINFO_TB.ATTACH_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return SbBoardFileinfoTb.SB_BOARD_FILEINFO_TB.OWNER_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return SbBoardFileinfoTb.SB_BOARD_FILEINFO_TB.IP;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field4() {
        return SbBoardFileinfoTb.SB_BOARD_FILEINFO_TB.REG_DT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field5() {
        return SbBoardFileinfoTb.SB_BOARD_FILEINFO_TB.MOD_DT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UInteger component1() {
        return getAttachId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component2() {
        return getOwnerId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component3() {
        return getIp();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component4() {
        return getRegDt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component5() {
        return getModDt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UInteger value1() {
        return getAttachId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getOwnerId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getIp();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value4() {
        return getRegDt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value5() {
        return getModDt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbBoardFileinfoTbRecord value1(UInteger value) {
        setAttachId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbBoardFileinfoTbRecord value2(String value) {
        setOwnerId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbBoardFileinfoTbRecord value3(String value) {
        setIp(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbBoardFileinfoTbRecord value4(Timestamp value) {
        setRegDt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbBoardFileinfoTbRecord value5(Timestamp value) {
        setModDt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbBoardFileinfoTbRecord values(UInteger value1, String value2, String value3, Timestamp value4, Timestamp value5) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached SbBoardFileinfoTbRecord
     */
    public SbBoardFileinfoTbRecord() {
        super(SbBoardFileinfoTb.SB_BOARD_FILEINFO_TB);
    }

    /**
     * Create a detached, initialised SbBoardFileinfoTbRecord
     */
    public SbBoardFileinfoTbRecord(UInteger attachId, String ownerId, String ip, Timestamp regDt, Timestamp modDt) {
        super(SbBoardFileinfoTb.SB_BOARD_FILEINFO_TB);

        set(0, attachId);
        set(1, ownerId);
        set(2, ip);
        set(3, regDt);
        set(4, modDt);
    }
}
