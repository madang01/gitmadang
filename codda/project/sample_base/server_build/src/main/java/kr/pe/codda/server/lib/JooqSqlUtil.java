package kr.pe.codda.server.lib;

import java.sql.Timestamp;

import org.jooq.Field;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;

public abstract class JooqSqlUtil {
	
	public static Field<UInteger> getFieldOfAttachID(Field<UInteger> attachIDField) {
		return DSL.field("if ({0} is null, 0, {0})", UInteger.class, attachIDField);
	}

	public static Field<UInteger> getFieldOfLastInsertID(Class<UInteger> type) {
		return DSL.field("LAST_INSERT_ID()", type);
	}
	
	public static Field<Timestamp> getFieldOfSysDate(Class<Timestamp> type) {
		return DSL.field("SYSDATE()", type);
	}
	
	public static Field<String> getFieldOfMemberGbNm(Field<Byte> memberGbField) {
		String sqlString = new StringBuilder("if ({0} = ")
				.append(MembershipLevel.USER.getValue())
				.append(", '")
				.append(MembershipLevel.USER.getName())
				.append("', if ({0} = ")
				.append(MembershipLevel.ADMIN.getValue())
				.append(", '")
				.append(MembershipLevel.ADMIN.getName())
				.append("', '알수없음'))").toString();
		// "if ({0} = 1, '일반회원', if ({0} = 0, '관리자', '알수없음'))"
		return DSL.field(sqlString, 
				String.class, memberGbField);
	}
}
