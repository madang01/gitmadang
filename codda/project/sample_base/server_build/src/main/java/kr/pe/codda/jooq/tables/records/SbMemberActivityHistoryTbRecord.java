/*
 * This file is generated by jOOQ.
*/
package kr.pe.codda.jooq.tables.records;


import java.sql.Timestamp;

import javax.annotation.Generated;

import kr.pe.codda.jooq.tables.SbMemberActivityHistoryTb;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Record6;
import org.jooq.Row6;
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
public class SbMemberActivityHistoryTbRecord extends UpdatableRecordImpl<SbMemberActivityHistoryTbRecord> implements Record6<String, Long, UByte, UInteger, Byte, Timestamp> {

    private static final long serialVersionUID = -747732189;

    /**
     * Setter for <code>sb_db.sb_member_activity_history_tb.user_id</code>. 사용자 아이디, 활동을 한 회원 아이디
     */
    public void setUserId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>sb_db.sb_member_activity_history_tb.user_id</code>. 사용자 아이디, 활동을 한 회원 아이디
     */
    public String getUserId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>sb_db.sb_member_activity_history_tb.activity_sq</code>. 활동 순번, 0 부터 시작되며 이후 MAX + 1 이 된다
     */
    public void setActivitySq(Long value) {
        set(1, value);
    }

    /**
     * Getter for <code>sb_db.sb_member_activity_history_tb.activity_sq</code>. 활동 순번, 0 부터 시작되며 이후 MAX + 1 이 된다
     */
    public Long getActivitySq() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>sb_db.sb_member_activity_history_tb.board_id</code>. 게시판 식별자,  활동  대상이 되는 게시글의 게시판 식별자
     */
    public void setBoardId(UByte value) {
        set(2, value);
    }

    /**
     * Getter for <code>sb_db.sb_member_activity_history_tb.board_id</code>. 게시판 식별자,  활동  대상이 되는 게시글의 게시판 식별자
     */
    public UByte getBoardId() {
        return (UByte) get(2);
    }

    /**
     * Setter for <code>sb_db.sb_member_activity_history_tb.board_no</code>. 게시판 번호, 활동 대상이 되는 게시글의 게시판 번호
     */
    public void setBoardNo(UInteger value) {
        set(3, value);
    }

    /**
     * Getter for <code>sb_db.sb_member_activity_history_tb.board_no</code>. 게시판 번호, 활동 대상이 되는 게시글의 게시판 번호
     */
    public UInteger getBoardNo() {
        return (UInteger) get(3);
    }

    /**
     * Setter for <code>sb_db.sb_member_activity_history_tb.activity_type</code>. 사용자 활동 종류, 'W'(=87):게시글 작성, 'R'(=82):게시글 댓글, 'V'(=86):게시글 추천, 'D'(=68):게시글 삭제, ' SELECT char(ascii('W') using ascii);
     */
    public void setActivityType(Byte value) {
        set(4, value);
    }

    /**
     * Getter for <code>sb_db.sb_member_activity_history_tb.activity_type</code>. 사용자 활동 종류, 'W'(=87):게시글 작성, 'R'(=82):게시글 댓글, 'V'(=86):게시글 추천, 'D'(=68):게시글 삭제, ' SELECT char(ascii('W') using ascii);
     */
    public Byte getActivityType() {
        return (Byte) get(4);
    }

    /**
     * Setter for <code>sb_db.sb_member_activity_history_tb.reg_dt</code>.
     */
    public void setRegDt(Timestamp value) {
        set(5, value);
    }

    /**
     * Getter for <code>sb_db.sb_member_activity_history_tb.reg_dt</code>.
     */
    public Timestamp getRegDt() {
        return (Timestamp) get(5);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record2<String, Long> key() {
        return (Record2) super.key();
    }

    // -------------------------------------------------------------------------
    // Record6 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row6<String, Long, UByte, UInteger, Byte, Timestamp> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row6<String, Long, UByte, UInteger, Byte, Timestamp> valuesRow() {
        return (Row6) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field1() {
        return SbMemberActivityHistoryTb.SB_MEMBER_ACTIVITY_HISTORY_TB.USER_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field2() {
        return SbMemberActivityHistoryTb.SB_MEMBER_ACTIVITY_HISTORY_TB.ACTIVITY_SQ;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UByte> field3() {
        return SbMemberActivityHistoryTb.SB_MEMBER_ACTIVITY_HISTORY_TB.BOARD_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UInteger> field4() {
        return SbMemberActivityHistoryTb.SB_MEMBER_ACTIVITY_HISTORY_TB.BOARD_NO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field5() {
        return SbMemberActivityHistoryTb.SB_MEMBER_ACTIVITY_HISTORY_TB.ACTIVITY_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field6() {
        return SbMemberActivityHistoryTb.SB_MEMBER_ACTIVITY_HISTORY_TB.REG_DT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component1() {
        return getUserId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long component2() {
        return getActivitySq();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UByte component3() {
        return getBoardId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UInteger component4() {
        return getBoardNo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component5() {
        return getActivityType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component6() {
        return getRegDt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value1() {
        return getUserId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value2() {
        return getActivitySq();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UByte value3() {
        return getBoardId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UInteger value4() {
        return getBoardNo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value5() {
        return getActivityType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value6() {
        return getRegDt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbMemberActivityHistoryTbRecord value1(String value) {
        setUserId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbMemberActivityHistoryTbRecord value2(Long value) {
        setActivitySq(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbMemberActivityHistoryTbRecord value3(UByte value) {
        setBoardId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbMemberActivityHistoryTbRecord value4(UInteger value) {
        setBoardNo(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbMemberActivityHistoryTbRecord value5(Byte value) {
        setActivityType(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbMemberActivityHistoryTbRecord value6(Timestamp value) {
        setRegDt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbMemberActivityHistoryTbRecord values(String value1, Long value2, UByte value3, UInteger value4, Byte value5, Timestamp value6) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached SbMemberActivityHistoryTbRecord
     */
    public SbMemberActivityHistoryTbRecord() {
        super(SbMemberActivityHistoryTb.SB_MEMBER_ACTIVITY_HISTORY_TB);
    }

    /**
     * Create a detached, initialised SbMemberActivityHistoryTbRecord
     */
    public SbMemberActivityHistoryTbRecord(String userId, Long activitySq, UByte boardId, UInteger boardNo, Byte activityType, Timestamp regDt) {
        super(SbMemberActivityHistoryTb.SB_MEMBER_ACTIVITY_HISTORY_TB);

        set(0, userId);
        set(1, activitySq);
        set(2, boardId);
        set(3, boardNo);
        set(4, activityType);
        set(5, regDt);
    }
}
