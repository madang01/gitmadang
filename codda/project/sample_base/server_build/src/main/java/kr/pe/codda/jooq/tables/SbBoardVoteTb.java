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
import kr.pe.codda.jooq.tables.records.SbBoardVoteTbRecord;

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
public class SbBoardVoteTb extends TableImpl<SbBoardVoteTbRecord> {

    private static final long serialVersionUID = -653769379;

    /**
     * The reference instance of <code>sb_db.sb_board_vote_tb</code>
     */
    public static final SbBoardVoteTb SB_BOARD_VOTE_TB = new SbBoardVoteTb();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<SbBoardVoteTbRecord> getRecordType() {
        return SbBoardVoteTbRecord.class;
    }

    /**
     * The column <code>sb_db.sb_board_vote_tb.board_id</code>.
     */
    public final TableField<SbBoardVoteTbRecord, UByte> BOARD_ID = createField("board_id", org.jooq.impl.SQLDataType.TINYINTUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>sb_db.sb_board_vote_tb.board_no</code>.
     */
    public final TableField<SbBoardVoteTbRecord, UInteger> BOARD_NO = createField("board_no", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>sb_db.sb_board_vote_tb.user_id</code>.
     */
    public final TableField<SbBoardVoteTbRecord, String> USER_ID = createField("user_id", org.jooq.impl.SQLDataType.VARCHAR(20).nullable(false), this, "");

    /**
     * The column <code>sb_db.sb_board_vote_tb.ip</code>.
     */
    public final TableField<SbBoardVoteTbRecord, String> IP = createField("ip", org.jooq.impl.SQLDataType.VARCHAR(40), this, "");

    /**
     * The column <code>sb_db.sb_board_vote_tb.reg_dt</code>.
     */
    public final TableField<SbBoardVoteTbRecord, Timestamp> REG_DT = createField("reg_dt", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

    /**
     * Create a <code>sb_db.sb_board_vote_tb</code> table reference
     */
    public SbBoardVoteTb() {
        this(DSL.name("sb_board_vote_tb"), null);
    }

    /**
     * Create an aliased <code>sb_db.sb_board_vote_tb</code> table reference
     */
    public SbBoardVoteTb(String alias) {
        this(DSL.name(alias), SB_BOARD_VOTE_TB);
    }

    /**
     * Create an aliased <code>sb_db.sb_board_vote_tb</code> table reference
     */
    public SbBoardVoteTb(Name alias) {
        this(alias, SB_BOARD_VOTE_TB);
    }

    private SbBoardVoteTb(Name alias, Table<SbBoardVoteTbRecord> aliased) {
        this(alias, aliased, null);
    }

    private SbBoardVoteTb(Name alias, Table<SbBoardVoteTbRecord> aliased, Field<?>[] parameters) {
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
        return Arrays.<Index>asList(Indexes.SB_BOARD_VOTE_TB_PRIMARY, Indexes.SB_BOARD_VOTE_TB_SB_BOARD_VOTE_FK1, Indexes.SB_BOARD_VOTE_TB_SB_BOARD_VOTE_FK2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<SbBoardVoteTbRecord> getPrimaryKey() {
        return Keys.KEY_SB_BOARD_VOTE_TB_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<SbBoardVoteTbRecord>> getKeys() {
        return Arrays.<UniqueKey<SbBoardVoteTbRecord>>asList(Keys.KEY_SB_BOARD_VOTE_TB_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<SbBoardVoteTbRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<SbBoardVoteTbRecord, ?>>asList(Keys.SB_BOARD_VOTE_FK1, Keys.SB_BOARD_VOTE_FK2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbBoardVoteTb as(String alias) {
        return new SbBoardVoteTb(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbBoardVoteTb as(Name alias) {
        return new SbBoardVoteTb(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public SbBoardVoteTb rename(String name) {
        return new SbBoardVoteTb(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public SbBoardVoteTb rename(Name name) {
        return new SbBoardVoteTb(name, null);
    }
}