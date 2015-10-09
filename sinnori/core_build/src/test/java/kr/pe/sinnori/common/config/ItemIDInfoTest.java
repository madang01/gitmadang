package kr.pe.sinnori.common.config;

import kr.pe.sinnori.common.config.itemidinfo.ItemIDInfo;
import kr.pe.sinnori.common.config.nativevalueconverter.GeneralConverterReturningLongBetweenMinAndMax;

import org.junit.Before;
import org.junit.Test;

public class ItemIDInfoTest {
	@Before
	public void setup() {
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testConstructor_DifferentGenericTypeCastUsingWildcard() throws Exception {
		String itemID = "client.connection.socket_timeout.value";
		ItemIDInfo<?> itemIDInfo = new ItemIDInfo<Long>(
				ItemIDInfo.ConfigurationPart.PROJECT,
				ItemIDInfo.ViewType.TEXT, itemID,
				"소켓 타임아웃, 단위 ms", "5000", true,
				new GeneralConverterReturningLongBetweenMinAndMax(
						1000L, (long) Integer.MAX_VALUE));
		try {
			@SuppressWarnings({ "unchecked", "unused" })
			ItemIDInfo<Float> dupItemIDInfo = (ItemIDInfo<Float>)itemIDInfo;
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
