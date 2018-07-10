/*
 * This file is generated by jOOQ.
*/
package kr.pe.codda.impl.jooq.tables.records;


import javax.annotation.Generated;

import kr.pe.codda.impl.jooq.tables.SbBoardInfoTb;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.UpdatableRecordImpl;
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
public class SbBoardInfoTbRecord extends UpdatableRecordImpl<SbBoardInfoTbRecord> implements Record3<UByte, String, String> {

    private static final long serialVersionUID = 1673220045;

    /**
     * Setter for <code>sb_db.sb_board_info_tb.board_id</code>. 게시판 식별자,
0 : 공지, 1:자유, 2:FAQ
     */
    public void setBoardId(UByte value) {
        set(0, value);
    }

    /**
     * Getter for <code>sb_db.sb_board_info_tb.board_id</code>. 게시판 식별자,
0 : 공지, 1:자유, 2:FAQ
     */
    public UByte getBoardId() {
        return (UByte) get(0);
    }

    /**
     * Setter for <code>sb_db.sb_board_info_tb.board_name</code>. 게시판 이름
     */
    public void setBoardName(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>sb_db.sb_board_info_tb.board_name</code>. 게시판 이름
     */
    public String getBoardName() {
        return (String) get(1);
    }

    /**
     * Setter for <code>sb_db.sb_board_info_tb.board_info</code>. 게시판 설명
     */
    public void setBoardInfo(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>sb_db.sb_board_info_tb.board_info</code>. 게시판 설명
     */
    public String getBoardInfo() {
        return (String) get(2);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<UByte> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row3<UByte, String, String> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row3<UByte, String, String> valuesRow() {
        return (Row3) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UByte> field1() {
        return SbBoardInfoTb.SB_BOARD_INFO_TB.BOARD_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return SbBoardInfoTb.SB_BOARD_INFO_TB.BOARD_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return SbBoardInfoTb.SB_BOARD_INFO_TB.BOARD_INFO;
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
    public String component2() {
        return getBoardName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component3() {
        return getBoardInfo();
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
    public String value2() {
        return getBoardName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getBoardInfo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbBoardInfoTbRecord value1(UByte value) {
        setBoardId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbBoardInfoTbRecord value2(String value) {
        setBoardName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbBoardInfoTbRecord value3(String value) {
        setBoardInfo(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbBoardInfoTbRecord values(UByte value1, String value2, String value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached SbBoardInfoTbRecord
     */
    public SbBoardInfoTbRecord() {
        super(SbBoardInfoTb.SB_BOARD_INFO_TB);
    }

    /**
     * Create a detached, initialised SbBoardInfoTbRecord
     */
    public SbBoardInfoTbRecord(UByte boardId, String boardName, String boardInfo) {
        super(SbBoardInfoTb.SB_BOARD_INFO_TB);

        set(0, boardId);
        set(1, boardName);
        set(2, boardInfo);
    }
}
