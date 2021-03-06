package kr.pe.codda.common.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.etc.CommonStaticFinalVars;

public class HexUtilTest extends AbstractJunitTest {
	
	@Test
	public void test() {
		String tmp1 = "allItemType[byteVar1=127, byteVar2=-128, byteVar3=96, unsignedByteVar1=0, unsignedByteVar2=255, unsignedByteVar3=101, shortVar1=32767, shortVar2=-32768, shortVar3=31, unsignedShortVar1=0, unsignedShortVar2=65535, unsignedShortVar3=32, intVar1=2147483647, intVar2=-2147483648, intVar3=1800946891, unsignedIntVar1=0, unsignedIntVar2=2147483647, unsignedIntVar3=2147484647, longVar1=9223372036854775807, longVar2=-9223372036854775808, longVar3=7305730154326879330, strVar1=testHH, strVar2=1234, strVar3=uiop, bytesVar1=778880817e7fff, bytesVar2=00000000000000, sqldate=2018-03-25, sqltimestamp=2018-03-25 09:44:28.211, cnt=2, memberList=[member[0]=Member[memberID=test01ID, memberName=test01Name, cnt=1, itemList=[item[0]=Item[itemID=1, itemName=최강의검, itemCnt=1, subItemList=[subItem[0]=SubItem[subItemID=sub01, subItemName=칼집, itemCnt=1]]]]], member[1]=Member[memberID=test01ID, memberName=test01Name, cnt=2, itemList=[item[0]=Item[itemID=2, itemName=살살검, itemCnt=5, subItemList=[subItem[0]=SubItem[subItemID=sub02, subItemName=신발, itemCnt=1], subItem[1]=SubItem[subItemID=sub03, subItemName=모자, itemCnt=2], subItem[2]=SubItem[subItemID=sub04, subItemName=장갑, itemCnt=1], subItem[3]=SubItem[subItemID=sub05, subItemName=배낭, itemCnt=1], subItem[4]=SubItem[subItemID=sub06, subItemName=배낭, itemCnt=1]]], item[1]=Item[itemID=3, itemName=안좋은검, itemCnt=0, subItemList=empty]]]], longVar4=0]";

		String tmp2 = "allItemType[byteVar1=127, byteVar2=-128, byteVar3=96, unsignedByteVar1=0, unsignedByteVar2=255, unsignedByteVar3=101, shortVar1=32767, shortVar2=-32768, shortVar3=31, unsignedShortVar1=0, unsignedShortVar2=65535, unsignedShortVar3=32, intVar1=2147483647, intVar2=-2147483648, intVar3=1800946891, unsignedIntVar1=0, unsignedIntVar2=2147483647, unsignedIntVar3=2147484647, longVar1=9223372036854775807, longVar2=-9223372036854775808, longVar3=7305730154326879330, strVar1=testHH, strVar2=1234, strVar3=uiop, bytesVar1=778880817e7fff, bytesVar2=00000000000000, sqldate=2018-03-25, sqltimestamp=2018-03-25 09:44:28.211, cnt=2, memberList=[member[0]=Member[memberID=test01ID, memberName=test01Name, cnt=1, itemList=[item[0]=Item[itemID=1, itemName=최강의검, itemCnt=1, subItemList=[subItem[0]=SubItem[subItemID=sub01, subItemName=칼집, itemCnt=1]]]]], member[1]=Member[memberID=test01ID, memberName=test01Name, cnt=2, itemList=[item[0]=Item[itemID=2, itemName=살살검, itemCnt=5, subItemList=[subItem[0]=SubItem[subItemID=sub02, subItemName=신발, itemCnt=1], subItem[1]=SubItem[subItemID=sub03, subItemName=모자, itemCnt=2], subItem[2]=SubItem[subItemID=sub04, subItemName=장갑, itemCnt=1], subItem[3]=SubItem[subItemID=sub05, subItemName=배낭, itemCnt=1], subItem[4]=SubItem[subItemID=sub06, subItemName=배낭, itemCnt=1]]], item[1]=Item[itemID=3, itemName=안좋은검, itemCnt=0, subItemList=empty]]]], longVar4=0]";

		assertEquals(tmp1, tmp2);
	}

	@Test
	public void testGetByteArrayFromHexString() {
		byte[] srcByteArray = { 
				0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f,
				0x10, 0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18, 0x19, 0x1a, 0x1b, 0x1c, 0x1d, 0x1e, 0x1f,
				0x20, 0x21, 0x22, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29, 0x2a, 0x2b, 0x2c, 0x2d, 0x2e, 0x2f
				};
		byte[] dstByteArray = HexUtil.getByteArrayFromHexString(
				"000102030405060708090a0b0c0d0e0f" +
				"101112131415161718191a1b1c1d1e1f" + 
				"202122232425262728292a2b2c2d2e2f" );
		
		//assertArrayEquals(srcByteArray, dstByteArray);
		boolean isSame = Arrays.equals(srcByteArray, dstByteArray);
		if (! isSame) {			
			fail("변환된 핵사 문자열과 원본 핵사 문자열 틀림");
		}
	}
	
	@Test
	public void testGetHexString_TheParamterValue_byteType_fulltest() {
		/** the parameter value's type is byte */
		byte byteValue=Byte.MIN_VALUE;
		
		for (int i=0; i <= CommonStaticFinalVars.UNSIGNED_BYTE_MAX; i++, byteValue++) {
			String expectedValue = String.format("%02x", byteValue);
			String acutualValue = HexUtil.getHexString(byteValue);
			
			log.info("byteValue={}, expectedValue={}, acutualValue={}", byteValue, expectedValue, acutualValue);
			// if (i > 10) break;
			
			assertEquals(expectedValue, acutualValue);
		}
	}
	
	
	@Test
	public void testGetHexString_TheParamterValue_shortType_fulltest() {
		/** the parameter value's type is short */
		short shortValue=Short.MIN_VALUE;
		
		for (int i=0; i <= CommonStaticFinalVars.UNSIGNED_SHORT_MAX; i++, shortValue++) {
			String expectedValue = String.format("%04x", shortValue);
			String acutualValue = HexUtil.getHexString(shortValue);
			
			// log.info("shortValue={}, expectedValue={}, acutualValue={}", shortValue, expectedValue, acutualValue);
			
			// if (i > 10) break;
			
			assertEquals(expectedValue, acutualValue);
		}
	}
	
	
	
	@Test
	public void testGetHexString_TheParamterValue_integerType_minMaxMiddleTest() {
		/** the parameter value's type is integer */
		int integerValue=Integer.MIN_VALUE;
		
		{
			integerValue=Integer.MIN_VALUE;
			String expectedValue = String.format("%08x", integerValue);
			String acutualValue = HexUtil.getHexString(integerValue);
			
			assertEquals(expectedValue, acutualValue);
		}
		
		{
			integerValue=Integer.MIN_VALUE-Integer.MIN_VALUE/2;
			String expectedValue = String.format("%08x", integerValue);
			String acutualValue = HexUtil.getHexString(integerValue);
			
			assertEquals(expectedValue, acutualValue);
		}
		
		{
			integerValue=-1;
			String expectedValue = String.format("%08x", integerValue);
			String acutualValue = HexUtil.getHexString(integerValue);
			
			assertEquals(expectedValue, acutualValue);
		}
		
		{
			integerValue=0;
			String expectedValue = String.format("%08x", integerValue);
			String acutualValue = HexUtil.getHexString(integerValue);
			
			assertEquals(expectedValue, acutualValue);
		}
		
		{
			integerValue=1;
			String expectedValue = String.format("%08x", integerValue);
			String acutualValue = HexUtil.getHexString(integerValue);
			
			assertEquals(expectedValue, acutualValue);
		}
		
		{
			integerValue=1;
			String expectedValue = String.format("%08x", integerValue);
			String acutualValue = HexUtil.getHexString(integerValue);
			
			assertEquals(expectedValue, acutualValue);
		}
		
		{
			integerValue=Integer.MAX_VALUE - Integer.MAX_VALUE/2;
			String expectedValue = String.format("%08x", integerValue);
			String acutualValue = HexUtil.getHexString(integerValue);
			
			assertEquals(expectedValue, acutualValue);
		}
		
		{
			integerValue=Integer.MAX_VALUE;
			String expectedValue = String.format("%08x", integerValue);
			String acutualValue = HexUtil.getHexString(integerValue);
			
			assertEquals(expectedValue, acutualValue);
		}
	}
	
	
	@Test
	public void testGetHexString_TheParamterValue_longType_minMaxMiddleTest() {
		/** the parameter value's type is long */
		long longValue=Long.MIN_VALUE;
		
		{
			longValue=Long.MIN_VALUE;
			String expectedValue = String.format("%016x", longValue);
			String acutualValue = HexUtil.getHexString(longValue);
			
			assertEquals(expectedValue, acutualValue);
		}
		
		{
			longValue=Long.MIN_VALUE-Long.MIN_VALUE/2;
			String expectedValue = String.format("%016x", longValue);
			String acutualValue = HexUtil.getHexString(longValue);
			
			assertEquals(expectedValue, acutualValue);
		}
		
		{
			longValue=-1;
			String expectedValue = String.format("%016x", longValue);
			String acutualValue = HexUtil.getHexString(longValue);
			
			assertEquals(expectedValue, acutualValue);
		}
		
		{
			longValue=0;
			String expectedValue = String.format("%016x", longValue);
			String acutualValue = HexUtil.getHexString(longValue);
			
			assertEquals(expectedValue, acutualValue);
		}
		
		{
			longValue=1;
			String expectedValue = String.format("%016x", longValue);
			String acutualValue = HexUtil.getHexString(longValue);
			
			assertEquals(expectedValue, acutualValue);
		}
		
		{
			longValue=1;
			String expectedValue = String.format("%016x", longValue);
			String acutualValue = HexUtil.getHexString(longValue);
			
			assertEquals(expectedValue, acutualValue);
		}
		
		{
			longValue=Long.MAX_VALUE - Long.MAX_VALUE/2;
			String expectedValue = String.format("%016x", longValue);
			String acutualValue = HexUtil.getHexString(longValue);
			
			assertEquals(expectedValue, acutualValue);
		}
		
		{
			longValue=Long.MAX_VALUE;
			String expectedValue = String.format("%016x", longValue);
			String acutualValue = HexUtil.getHexString(longValue);
			
			assertEquals(expectedValue, acutualValue);
		}
	}
	
	
	
	@Test
	public void testGetHexStringFromByteBuffer() {
		ByteBuffer srcByteBuffer = ByteBuffer.allocate(4);
		srcByteBuffer.put((byte)0x10);
		srcByteBuffer.put((byte)0xaa);
		srcByteBuffer.put((byte)0xfa);
		srcByteBuffer.put((byte)0x0e);	
		
		@SuppressWarnings("unused")
		String retStr = null;
		try {
			retStr = HexUtil.getHexStringFromByteBuffer(srcByteBuffer,0, 4);
		} catch(IllegalArgumentException e) {
			fail(e.getMessage());
		}
		
		try {
			retStr = HexUtil.getHexStringFromByteBuffer(srcByteBuffer,1, 3);
		} catch(IllegalArgumentException e) {
			fail(e.getMessage());
		}
		
		try {
			retStr = HexUtil.getHexStringFromByteArray(null,1, 3);
			
			fail("parm buffer is null but no fail");
		} catch(IllegalArgumentException e) {
			log.info(e.getMessage());
		}
		
		try {
			retStr = HexUtil.getHexStringFromByteBuffer(srcByteBuffer,-1, 4);
			
			fail("parm offset[-1] less than zero but no fail");
		} catch(IllegalArgumentException e) {
			log.info(e.getMessage());
		}
		
		try {
			retStr = HexUtil.getHexStringFromByteBuffer(srcByteBuffer,1, -2);
			
			fail("parm length[-1] less than zero but no fail");
		} catch(IllegalArgumentException e) {
			log.info(e.getMessage());
		}
		
		try {
			retStr = HexUtil.getHexStringFromByteBuffer(srcByteBuffer, 1, 5);
			
			fail("sum of parm offset[1] and parm length[5] over than parm buffer'capacity[4] but no fail");
		} catch(IllegalArgumentException e) {
			log.info(e.getMessage());
		}
	}
	
	@Test
	public void testGetHexStringFromByteArray() {		
		byte[] srcBytes = {(byte)0x10, (byte)0xaa, (byte)0xfa, (byte)0x0e};		
		
		@SuppressWarnings("unused")
		String retStr = null;
		try {
			retStr = HexUtil.getHexStringFromByteArray(srcBytes,0, 4);
		} catch(IllegalArgumentException e) {
			fail(e.getMessage());
		}
		
		try {
			retStr = HexUtil.getHexStringFromByteArray(srcBytes,1, 3);
		} catch(IllegalArgumentException e) {
			fail(e.getMessage());
		}
		
		try {
			retStr = HexUtil.getHexStringFromByteArray(null,1, 3);
			
			fail("parm buffer is null but no fail");
		} catch(IllegalArgumentException e) {
			log.info(e.getMessage());
		}
		
		try {
			retStr = HexUtil.getHexStringFromByteArray(srcBytes,-1, 4);
			
			fail("parm offset[-1] less than zero but no fail");
		} catch(IllegalArgumentException e) {
			log.info(e.getMessage());
		}
		
		try {
			retStr = HexUtil.getHexStringFromByteArray(srcBytes,1, -2);
			
			fail("parm length[-1] less than zero but no fail");
		} catch(IllegalArgumentException e) {
			log.info(e.getMessage());
		}
		
		try {
			retStr = HexUtil.getHexStringFromByteArray(srcBytes, 1, 5);
			
			fail("sum of parm offset[1] and parm length[5] over than parm buffer'capacity[4] but no fail");
		} catch(IllegalArgumentException e) {
			log.info(e.getMessage());
		}
	}
}
