/*
 * This file is generated by jOOQ.
*/
package kr.pe.codda.impl.jooq.tables.records;


import javax.annotation.Generated;

import kr.pe.codda.impl.jooq.tables.SbBoardTb;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Record8;
import org.jooq.Row8;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.jooq.types.UShort;


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
public class SbBoardTbRecord extends UpdatableRecordImpl<SbBoardTbRecord> implements Record8<UByte, UInteger, UInteger, UShort, UInteger, UByte, Integer, String> {

    private static final long serialVersionUID = 904831454;

    /**
     * Setter for <code>sb_db.sb_board_tb.board_id</code>. 게시판 종류 식별자, 어떤 게시판인지 설명하는 게시판 정보(board_info) 테이블을 바라본다.
     */
    public void setBoardId(UByte value) {
        set(0, value);
    }

    /**
     * Getter for <code>sb_db.sb_board_tb.board_id</code>. 게시판 종류 식별자, 어떤 게시판인지 설명하는 게시판 정보(board_info) 테이블을 바라본다.
     */
    public UByte getBoardId() {
        return (UByte) get(0);
    }

    /**
     * Setter for <code>sb_db.sb_board_tb.board_no</code>. 게시판 번호,  1부터 시작한다. 1 로 초기화 되는 시퀀스 테이블(SB_SEQ_TB) 로 부터 게시판 타입별로 게시판 번호를 얻어옴
     */
    public void setBoardNo(UInteger value) {
        set(1, value);
    }

    /**
     * Getter for <code>sb_db.sb_board_tb.board_no</code>. 게시판 번호,  1부터 시작한다. 1 로 초기화 되는 시퀀스 테이블(SB_SEQ_TB) 로 부터 게시판 타입별로 게시판 번호를 얻어옴
     */
    public UInteger getBoardNo() {
        return (UInteger) get(1);
    }

    /**
     * Setter for <code>sb_db.sb_board_tb.group_no</code>. 그룹 번호
     */
    public void setGroupNo(UInteger value) {
        set(2, value);
    }

    /**
     * Getter for <code>sb_db.sb_board_tb.group_no</code>. 그룹 번호
     */
    public UInteger getGroupNo() {
        return (UInteger) get(2);
    }

    /**
     * Setter for <code>sb_db.sb_board_tb.group_sq</code>. 그룹 즉 동일한 그룹 번호(=group_no)  에서 0 부터 시작되는 순번
     */
    public void setGroupSq(UShort value) {
        set(3, value);
    }

    /**
     * Getter for <code>sb_db.sb_board_tb.group_sq</code>. 그룹 즉 동일한 그룹 번호(=group_no)  에서 0 부터 시작되는 순번
     */
    public UShort getGroupSq() {
        return (UShort) get(3);
    }

    /**
     * Setter for <code>sb_db.sb_board_tb.parent_no</code>. 부모 게시판 번호,  게시판 번호는 1부터 시작하며 부모가 없는 경우 부모 게시판 번호는 0 값을 갖는다.
     */
    public void setParentNo(UInteger value) {
        set(4, value);
    }

    /**
     * Getter for <code>sb_db.sb_board_tb.parent_no</code>. 부모 게시판 번호,  게시판 번호는 1부터 시작하며 부모가 없는 경우 부모 게시판 번호는 0 값을 갖는다.
     */
    public UInteger getParentNo() {
        return (UInteger) get(4);
    }

    /**
     * Setter for <code>sb_db.sb_board_tb.depth</code>. 트리 깊이,  0 부터 시작하며 트리 깊이가 0 일 경우 최상위 글로써 최상위 글을 기준으로 이후 댓글이 달린다. 자식 글의 댓글 깊이는 부모 글의 댓글 깊이보다 1 이 크다.
     */
    public void setDepth(UByte value) {
        set(5, value);
    }

    /**
     * Getter for <code>sb_db.sb_board_tb.depth</code>. 트리 깊이,  0 부터 시작하며 트리 깊이가 0 일 경우 최상위 글로써 최상위 글을 기준으로 이후 댓글이 달린다. 자식 글의 댓글 깊이는 부모 글의 댓글 깊이보다 1 이 크다.
     */
    public UByte getDepth() {
        return (UByte) get(5);
    }

    /**
     * Setter for <code>sb_db.sb_board_tb.view_cnt</code>. 조회수
     */
    public void setViewCnt(Integer value) {
        set(6, value);
    }

    /**
     * Getter for <code>sb_db.sb_board_tb.view_cnt</code>. 조회수
     */
    public Integer getViewCnt() {
        return (Integer) get(6);
    }

    /**
     * Setter for <code>sb_db.sb_board_tb.board_st</code>. 게시판 상태, B : 블락, D : 삭제된 게시글, Y : 정상 게시글
     */
    public void setBoardSt(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>sb_db.sb_board_tb.board_st</code>. 게시판 상태, B : 블락, D : 삭제된 게시글, Y : 정상 게시글
     */
    public String getBoardSt() {
        return (String) get(7);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record2<UByte, UInteger> key() {
        return (Record2) super.key();
    }

    // -------------------------------------------------------------------------
    // Record8 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row8<UByte, UInteger, UInteger, UShort, UInteger, UByte, Integer, String> fieldsRow() {
        return (Row8) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row8<UByte, UInteger, UInteger, UShort, UInteger, UByte, Integer, String> valuesRow() {
        return (Row8) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UByte> field1() {
        return SbBoardTb.SB_BOARD_TB.BOARD_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UInteger> field2() {
        return SbBoardTb.SB_BOARD_TB.BOARD_NO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UInteger> field3() {
        return SbBoardTb.SB_BOARD_TB.GROUP_NO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UShort> field4() {
        return SbBoardTb.SB_BOARD_TB.GROUP_SQ;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UInteger> field5() {
        return SbBoardTb.SB_BOARD_TB.PARENT_NO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UByte> field6() {
        return SbBoardTb.SB_BOARD_TB.DEPTH;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field7() {
        return SbBoardTb.SB_BOARD_TB.VIEW_CNT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field8() {
        return SbBoardTb.SB_BOARD_TB.BOARD_ST;
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
    public UInteger component3() {
        return getGroupNo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UShort component4() {
        return getGroupSq();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UInteger component5() {
        return getParentNo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UByte component6() {
        return getDepth();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component7() {
        return getViewCnt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component8() {
        return getBoardSt();
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
    public UInteger value3() {
        return getGroupNo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UShort value4() {
        return getGroupSq();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UInteger value5() {
        return getParentNo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UByte value6() {
        return getDepth();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value7() {
        return getViewCnt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value8() {
        return getBoardSt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbBoardTbRecord value1(UByte value) {
        setBoardId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbBoardTbRecord value2(UInteger value) {
        setBoardNo(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbBoardTbRecord value3(UInteger value) {
        setGroupNo(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbBoardTbRecord value4(UShort value) {
        setGroupSq(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbBoardTbRecord value5(UInteger value) {
        setParentNo(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbBoardTbRecord value6(UByte value) {
        setDepth(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbBoardTbRecord value7(Integer value) {
        setViewCnt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbBoardTbRecord value8(String value) {
        setBoardSt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbBoardTbRecord values(UByte value1, UInteger value2, UInteger value3, UShort value4, UInteger value5, UByte value6, Integer value7, String value8) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached SbBoardTbRecord
     */
    public SbBoardTbRecord() {
        super(SbBoardTb.SB_BOARD_TB);
    }

    /**
     * Create a detached, initialised SbBoardTbRecord
     */
    public SbBoardTbRecord(UByte boardId, UInteger boardNo, UInteger groupNo, UShort groupSq, UInteger parentNo, UByte depth, Integer viewCnt, String boardSt) {
        super(SbBoardTb.SB_BOARD_TB);

        set(0, boardId);
        set(1, boardNo);
        set(2, groupNo);
        set(3, groupSq);
        set(4, parentNo);
        set(5, depth);
        set(6, viewCnt);
        set(7, boardSt);
    }
}
