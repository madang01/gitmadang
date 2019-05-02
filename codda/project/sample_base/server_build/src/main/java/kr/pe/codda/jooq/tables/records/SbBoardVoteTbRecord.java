/*
 * This file is generated by jOOQ.
*/
package kr.pe.codda.jooq.tables.records;


import java.sql.Timestamp;

import javax.annotation.Generated;

import kr.pe.codda.jooq.tables.SbBoardVoteTb;

import org.jooq.Field;
import org.jooq.Record3;
import org.jooq.Record5;
import org.jooq.Row5;
import org.jooq.impl.UpdatableRecordImpl;
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
public class SbBoardVoteTbRecord extends UpdatableRecordImpl<SbBoardVoteTbRecord> implements Record5<UByte, UInteger, String, String, Timestamp> {

    private static final long serialVersionUID = -1317499601;

    /**
     * Setter for <code>sb_db.sb_board_vote_tb.board_id</code>.
     */
    public void setBoardId(UByte value) {
        set(0, value);
    }

    /**
     * Getter for <code>sb_db.sb_board_vote_tb.board_id</code>.
     */
    public UByte getBoardId() {
        return (UByte) get(0);
    }

    /**
     * Setter for <code>sb_db.sb_board_vote_tb.board_no</code>.
     */
    public void setBoardNo(UInteger value) {
        set(1, value);
    }

    /**
     * Getter for <code>sb_db.sb_board_vote_tb.board_no</code>.
     */
    public UInteger getBoardNo() {
        return (UInteger) get(1);
    }

    /**
     * Setter for <code>sb_db.sb_board_vote_tb.user_id</code>.
     */
    public void setUserId(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>sb_db.sb_board_vote_tb.user_id</code>.
     */
    public String getUserId() {
        return (String) get(2);
    }

    /**
     * Setter for <code>sb_db.sb_board_vote_tb.ip</code>.
     */
    public void setIp(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>sb_db.sb_board_vote_tb.ip</code>.
     */
    public String getIp() {
        return (String) get(3);
    }

    /**
     * Setter for <code>sb_db.sb_board_vote_tb.reg_dt</code>.
     */
    public void setRegDt(Timestamp value) {
        set(4, value);
    }

    /**
     * Getter for <code>sb_db.sb_board_vote_tb.reg_dt</code>.
     */
    public Timestamp getRegDt() {
        return (Timestamp) get(4);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record3<UInteger, String, UByte> key() {
        return (Record3) super.key();
    }

    // -------------------------------------------------------------------------
    // Record5 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row5<UByte, UInteger, String, String, Timestamp> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row5<UByte, UInteger, String, String, Timestamp> valuesRow() {
        return (Row5) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UByte> field1() {
        return SbBoardVoteTb.SB_BOARD_VOTE_TB.BOARD_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UInteger> field2() {
        return SbBoardVoteTb.SB_BOARD_VOTE_TB.BOARD_NO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return SbBoardVoteTb.SB_BOARD_VOTE_TB.USER_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return SbBoardVoteTb.SB_BOARD_VOTE_TB.IP;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field5() {
        return SbBoardVoteTb.SB_BOARD_VOTE_TB.REG_DT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UByte component1() {
        return getBoardId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UInteger component2() {
        return getBoardNo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component3() {
        return getUserId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component4() {
        return getIp();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component5() {
        return getRegDt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UByte value1() {
        return getBoardId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UInteger value2() {
        return getBoardNo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getUserId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getIp();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value5() {
        return getRegDt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbBoardVoteTbRecord value1(UByte value) {
        setBoardId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbBoardVoteTbRecord value2(UInteger value) {
        setBoardNo(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbBoardVoteTbRecord value3(String value) {
        setUserId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbBoardVoteTbRecord value4(String value) {
        setIp(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbBoardVoteTbRecord value5(Timestamp value) {
        setRegDt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbBoardVoteTbRecord values(UByte value1, UInteger value2, String value3, String value4, Timestamp value5) {
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
     * Create a detached SbBoardVoteTbRecord
     */
    public SbBoardVoteTbRecord() {
        super(SbBoardVoteTb.SB_BOARD_VOTE_TB);
    }

    /**
     * Create a detached, initialised SbBoardVoteTbRecord
     */
    public SbBoardVoteTbRecord(UByte boardId, UInteger boardNo, String userId, String ip, Timestamp regDt) {
        super(SbBoardVoteTb.SB_BOARD_VOTE_TB);

        set(0, boardId);
        set(1, boardNo);
        set(2, userId);
        set(3, ip);
        set(4, regDt);
    }
}