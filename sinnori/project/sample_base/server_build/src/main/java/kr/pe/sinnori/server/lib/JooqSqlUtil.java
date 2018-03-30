package kr.pe.sinnori.server.lib;

import java.sql.Timestamp;

import org.jooq.Field;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;

public abstract class JooqSqlUtil {

	public static Field<UInteger> getFieldOfLastInsertID(Class<UInteger> type) {
		return DSL.field("LAST_INSERT_ID()", type);
	}
	
	public static Field<Timestamp> getFieldOfSysDate(Class<Timestamp> type) {
		return DSL.field("SYSDATE()", type);
	}
	
	public static Field<String> getFieldOfMemberGbNm(Field<Byte> memberGbField) {
		return DSL.field("if ({0} = 1, '일반회원', if ({0} = 0, '관리자', '알수없음'))", 
				String.class, memberGbField);
	}
}
