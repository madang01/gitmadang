/*
 * This file is generated by jOOQ.
*/
package kr.pe.sinnori.impl.jooq.tables.records;


import javax.annotation.Generated;

import kr.pe.sinnori.impl.jooq.tables.SbGroupInfoTb;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.UpdatableRecordImpl;


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
public class SbGroupInfoTbRecord extends UpdatableRecordImpl<SbGroupInfoTbRecord> implements Record3<Byte, String, String> {

    private static final long serialVersionUID = -1557058984;

    /**
     * Setter for <code>SB_DB.SB_GROUP_INFO_TB.group_id</code>. 그룹 식별자, 0:admin, 1:joho
     */
    public void setGroupId(Byte value) {
        set(0, value);
    }

    /**
     * Getter for <code>SB_DB.SB_GROUP_INFO_TB.group_id</code>. 그룹 식별자, 0:admin, 1:joho
     */
    public Byte getGroupId() {
        return (Byte) get(0);
    }

    /**
     * Setter for <code>SB_DB.SB_GROUP_INFO_TB.group_name</code>.
     */
    public void setGroupName(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>SB_DB.SB_GROUP_INFO_TB.group_name</code>.
     */
    public String getGroupName() {
        return (String) get(1);
    }

    /**
     * Setter for <code>SB_DB.SB_GROUP_INFO_TB.group_info</code>.
     */
    public void setGroupInfo(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>SB_DB.SB_GROUP_INFO_TB.group_info</code>.
     */
    public String getGroupInfo() {
        return (String) get(2);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<Byte> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row3<Byte, String, String> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row3<Byte, String, String> valuesRow() {
        return (Row3) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field1() {
        return SbGroupInfoTb.SB_GROUP_INFO_TB.GROUP_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return SbGroupInfoTb.SB_GROUP_INFO_TB.GROUP_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return SbGroupInfoTb.SB_GROUP_INFO_TB.GROUP_INFO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component1() {
        return getGroupId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component2() {
        return getGroupName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component3() {
        return getGroupInfo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value1() {
        return getGroupId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getGroupName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getGroupInfo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbGroupInfoTbRecord value1(Byte value) {
        setGroupId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbGroupInfoTbRecord value2(String value) {
        setGroupName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbGroupInfoTbRecord value3(String value) {
        setGroupInfo(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SbGroupInfoTbRecord values(Byte value1, String value2, String value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached SbGroupInfoTbRecord
     */
    public SbGroupInfoTbRecord() {
        super(SbGroupInfoTb.SB_GROUP_INFO_TB);
    }

    /**
     * Create a detached, initialised SbGroupInfoTbRecord
     */
    public SbGroupInfoTbRecord(Byte groupId, String groupName, String groupInfo) {
        super(SbGroupInfoTb.SB_GROUP_INFO_TB);

        set(0, groupId);
        set(1, groupName);
        set(2, groupInfo);
    }
}
