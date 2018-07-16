package kr.pe.codda.server.lib;

import java.sql.Timestamp;

import org.jooq.Field;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
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
	
	public static Field<String> getFieldOfMemberGbNm(Field<String> memberTypeField) {
		String sqlString = new StringBuilder("if ({0} = '")
				.append(MemberType.USER.getValue())
				.append("', '")
				.append(MemberType.USER.getName())
				.append("', if ({0} = '")
				.append(MemberType.ADMIN.getValue())
				.append("', '")
				.append(MemberType.ADMIN.getName())
				.append("', '알수없음'))").toString();
		// "if ({0} = 1, '일반회원', if ({0} = 0, '관리자', '알수없음'))"
		return DSL.field(sqlString, 
				String.class, memberTypeField);
	}
	
	/**
	 * <pre>
	 * 파라미터 '신규 메뉴 순서 항목' 의 값을 SB_SITEMENU_TB.ORDER_SQ.max().add(1) 라는 고정 값으로 가져 
	 * 0 부터 시작하는 '신규 메뉴 순서 항목'을 반환한다.
	 * 
	 * WARNING! 파라미터 '신규 메뉴 순서 항목' 의 값은 반듯이 SB_SITEMENU_TB.ORDER_SQ.max().add(1) 라는 고정 값이어야 한다
	 * </pre>
	 * 
	 * @param newOrderSqField 신규 메뉴 순서 항목, 루트 메뉴 혹은 자식 메뉴 추가시 필요한 메뉴 순서를 최종적으로 얻는것을 목표로 하기에 고정 값으로 SB_SITEMENU_TB.ORDER_SQ.max().add(1) 를 갖는다.
	 * @return 파라미터 '신규 메뉴 순서 항목' 값 이 null 이면 0 을 아니면 파라미터 '신규 메뉴 순서 항목' 값을 항목의 값으로 하는 '신규 메뉴 순서 항목' 
	 */
	public static Field<UByte> getFieldOfNewSiteMenuOrderSq(Field<UByte> newOrderSqField) {
		// if ((max(`sb_db`.`sb_sitemenu_tb`.`order_sq`) + 1) is null, 0, (max(`sb_db`.`sb_sitemenu_tb`.`order_sq`) + 1))
		return DSL.field("if ({0} is null, 0, {0})", UByte.class, newOrderSqField);
	}
}
