/*
 * This file is generated by jOOQ.
*/
package kr.pe.codda.impl.jooq.tables;


import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import kr.pe.codda.impl.jooq.Indexes;
import kr.pe.codda.impl.jooq.Keys;
import kr.pe.codda.impl.jooq.SbDb;
import kr.pe.codda.impl.jooq.tables.records.SbBoardTbRecord;

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
public class SbBoardTb extends TableImpl<SbBoardTbRecord> {

    private static final long serialVersionUID = 58481052;

    /**
     * The reference instance of <code>sb_db.sb_board_tb</code>
     */
    public static final SbBoardTb SB_BOARD_TB = new SbBoardTb();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<SbBoardTbRecord> getRecordType() {
        return SbBoardTbRecord.class;
    }

    /**
     * The column <code>sb_db.sb_board_tb.board_id</code>. 게시판 종류 식별자, 어떤 게시판인지 설명하는 게시판 정보(board_info) 테이블을 바라본다.
     */
    public final TableField<SbBoardTbRecord, UByte> BOARD_ID = createField("board_id", org.jooq.impl.SQLDataType.TINYINTUNSIGNED.nullable(false), this, "게시판 종류 식별자, 어떤 게시판인지 설명하는 게시판 정보(board_info) 테이블을 바라본다.");

    /**
     * The column <code>sb_db.sb_board_tb.board_no</code>. 게시판 번호,  1부터 시작한다. 1 로 초기화 되는 시퀀스 테이블(SB_SEQ_TB) 로 부터 게시판 타입별로 게시판 번호를 얻어옴
     */
    public final TableField<SbBoardTbRecord, UInteger> BOARD_NO = createField("board_no", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false), this, "게시판 번호,  1부터 시작한다. 1 로 초기화 되는 시퀀스 테이블(SB_SEQ_TB) 로 부터 게시판 타입별로 게시판 번호를 얻어옴");

    /**
     * The column <code>sb_db.sb_board_tb.group_no</code>. 그룹 번호
     */
    public final TableField<SbBoardTbRecord, UInteger> GROUP_NO = createField("group_no", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false), this, "그룹 번호");

    /**
     * The column <code>sb_db.sb_board_tb.group_sq</code>. 그룹 즉 동일한 그룹 번호(=group_no)  에서 0 부터 시작되는 순번
     */
    public final TableField<SbBoardTbRecord, UShort> GROUP_SQ = createField("group_sq", org.jooq.impl.SQLDataType.SMALLINTUNSIGNED.nullable(false), this, "그룹 즉 동일한 그룹 번호(=group_no)  에서 0 부터 시작되는 순번");

    /**
     * The column <code>sb_db.sb_board_tb.parent_no</code>. 부모 게시판 번호,  게시판 번호는 1부터 시작하며 부모가 없는 경우 부모 게시판 번호는 0 값을 갖는다.
     */
    public final TableField<SbBoardTbRecord, UInteger> PARENT_NO = createField("parent_no", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.defaultValue(org.jooq.impl.DSL.inline("NULL", org.jooq.impl.SQLDataType.INTEGERUNSIGNED)), this, "부모 게시판 번호,  게시판 번호는 1부터 시작하며 부모가 없는 경우 부모 게시판 번호는 0 값을 갖는다.");

    /**
     * The column <code>sb_db.sb_board_tb.depth</code>. 트리 깊이,  0 부터 시작하며 트리 깊이가 0 일 경우 최상위 글로써 최상위 글을 기준으로 이후 댓글이 달린다. 자식 글의 댓글 깊이는 부모 글의 댓글 깊이보다 1 이 크다.
     */
    public final TableField<SbBoardTbRecord, UByte> DEPTH = createField("depth", org.jooq.impl.SQLDataType.TINYINTUNSIGNED.defaultValue(org.jooq.impl.DSL.inline("NULL", org.jooq.impl.SQLDataType.TINYINTUNSIGNED)), this, "트리 깊이,  0 부터 시작하며 트리 깊이가 0 일 경우 최상위 글로써 최상위 글을 기준으로 이후 댓글이 달린다. 자식 글의 댓글 깊이는 부모 글의 댓글 깊이보다 1 이 크다.");

    /**
     * The column <code>sb_db.sb_board_tb.view_cnt</code>. 조회수
     */
    public final TableField<SbBoardTbRecord, Integer> VIEW_CNT = createField("view_cnt", org.jooq.impl.SQLDataType.INTEGER.defaultValue(org.jooq.impl.DSL.inline("NULL", org.jooq.impl.SQLDataType.INTEGER)), this, "조회수");

    /**
     * The column <code>sb_db.sb_board_tb.board_st</code>. 게시글 상태, B : 블락, D : 삭제된 게시글, Y : 정상 게시글
     */
    public final TableField<SbBoardTbRecord, String> BOARD_ST = createField("board_st", org.jooq.impl.SQLDataType.CHAR(1).nullable(false), this, "게시글 상태, B : 블락, D : 삭제된 게시글, Y : 정상 게시글");

    /**
     * The column <code>sb_db.sb_board_tb.next_attached_file_sq</code>. 다음 첨부 파일 시퀀스, 처음 0부터 시작
     */
    public final TableField<SbBoardTbRecord, UByte> NEXT_ATTACHED_FILE_SQ = createField("next_attached_file_sq", org.jooq.impl.SQLDataType.TINYINTUNSIGNED.defaultValue(org.jooq.impl.DSL.inline("NULL", org.jooq.impl.SQLDataType.TINYINTUNSIGNED)), this, "다음 첨부 파일 시퀀스, 처음 0부터 시작");

    /**
     * Create a <code>sb_db.sb_board_tb</code> table reference
     */
    public SbBoardTb() {
        this(DSL.name("sb_board_tb"), null);
    }

    /**
     * Create an aliased <code>sb_db.sb_board_tb</code> table reference
     */
    public SbBoardTb(String alias) {
        this(DSL.name(alias), SB_BOARD_TB);
    }

    /**
     * Create an aliased <code>sb_db.sb_board_tb</code> table reference
     */
    public SbBoardTb(Name alias) {
        this(alias, SB_BOARD_TB);
    }

    private SbBoardTb(Name alias, Table<SbBoardTbRecord> aliased) {
        this(alias, aliased, null);
    }

    private SbBoardTb(Name alias, Table<SbBoardTbRecord> aliased, Field<?>[] parameters) {
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
        return Arrays.<Index>asList(Indexes.SB_BOARD_TB_PRIMARY, Indexes.SB_BOARD_TB_SB_BOARD_FK1_IDX, Indexes.SB_BOARD_TB_SB_BOARD_IDX1, Indexes.SB_BOARD_TB_SB_BOARD_IDX2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<SbBoardTbRecord> getPrimaryKey() {
        return Keys.KEY_SB_BOARD_TB_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<SbBoardTbRecord>> getKeys() {
        return Arrays.<UniqueKey<SbBoardTbRecord>>asList(Keys.KEY_SB_BOARD_TB_PRIMARY, Keys.KEY_SB_BOARD_TB_SB_BOARD_IDX1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<SbBoardTbRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<SbBoardTbRecord, ?>>asList(Keys.SB_BOARD_FK1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbBoardTb as(String alias) {
        return new SbBoardTb(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbBoardTb as(Name alias) {
        return new SbBoardTb(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public SbBoardTb rename(String name) {
        return new SbBoardTb(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public SbBoardTb rename(Name name) {
        return new SbBoardTb(name, null);
    }
}
