package kr.pe.codda.common.protocol.thb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Arrays;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import junitlib.AbstractJunitTest;
import kr.pe.codda.common.etc.CommonStaticFinalVars;
import kr.pe.codda.common.io.BinaryInputStreamIF;
import kr.pe.codda.common.io.BinaryOutputStreamIF;
import kr.pe.codda.common.io.FixedSizeInputStream;
import kr.pe.codda.common.io.FixedSizeOutputStream;
import kr.pe.codda.common.type.SelfExn;
import kr.pe.codda.common.type.SingleItemType;

public class THBSingleItemEncoderMatcherTest extends AbstractJunitTest {
	
	@Test
	public void test모든타입별인코딩과디코딩유효성검사() {
		Charset streamCharset = Charset.forName("utf8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();
		
		THBSingleItemEncoderMatcherIF thbSingleItemEncoderMatcher = 
				new THBSingleItemEncoderMatcher(streamCharsetEncoder);
		
		THBSingleItemDecoderMatcherIF thbSingleItemDecoderMatcher = 
				new THBSingleItemDecoderMatcher(streamCharsetDecoder);
		
		ByteBuffer streamByteBuffer = ByteBuffer.allocate(4096);
		
		{
			streamByteBuffer.clear();
			SingleItemType testSingleItemType = SingleItemType.SELFEXN_ERROR_PLACE;
			int itemTypeID = testSingleItemType.getItemTypeID();
			AbstractTHBSingleItemEncoder thbSingleItemEncoder = thbSingleItemEncoderMatcher.get(itemTypeID);
			AbstractTHBSingleItemDecoder thbSingleItemDecoder = thbSingleItemDecoderMatcher.get(itemTypeID);
			
			if (! thbSingleItemEncoder.getSingleItemType().equals(testSingleItemType)) {
				fail("the single item encoder's item type id is not same to the expected item type id");
			}
			
			if (! thbSingleItemDecoder.getSingleItemType().equals(testSingleItemType)) {
				fail("the single item decoder's item type id is not same to the expected item type id");
			}
			
			String itemName = "에러난 장소";
			Object expcectedValue = SelfExn.ErrorPlace.CLIENT;
			int itemSize = -1;
			String nativeItemCharset = null;
			
			
			BinaryOutputStreamIF binaryOutputStream = new FixedSizeOutputStream(streamByteBuffer, streamCharsetEncoder);
			
			try {
				thbSingleItemEncoder.putValue(itemTypeID, itemName, expcectedValue, itemSize, nativeItemCharset, binaryOutputStream);
			} catch (Exception e) {
				log.warn("fail to encode a single item value", e);
				fail("fail to encode a single item value");
			}
			
			streamByteBuffer.flip();			
			BinaryInputStreamIF binaryInputStream = new FixedSizeInputStream(streamByteBuffer, streamCharsetDecoder);
			
			Object actualValue = null;
			try {
				actualValue = thbSingleItemDecoder.getValue(itemTypeID, itemName, itemSize, nativeItemCharset, binaryInputStream);
			} catch (Exception e) {
				log.warn("fail to decode a single item value", e);
				fail("fail to decode a single item value");
			}
			
			if (0 != binaryInputStream.available()) {
				fail("data remaining after decoding");
			}
			
			assertEquals(expcectedValue, actualValue);			
		}
		
		{
			streamByteBuffer.clear();
			SingleItemType testSingleItemType = SingleItemType.SELFEXN_ERROR_TYPE;
			int itemTypeID = testSingleItemType.getItemTypeID();
			AbstractTHBSingleItemEncoder thbSingleItemEncoder = thbSingleItemEncoderMatcher.get(itemTypeID);
			AbstractTHBSingleItemDecoder thbSingleItemDecoder = thbSingleItemDecoderMatcher.get(itemTypeID);
			
			if (! thbSingleItemEncoder.getSingleItemType().equals(testSingleItemType)) {
				fail("the single item encoder's item type id is not same to the expected item type id");
			}
			
			if (! thbSingleItemDecoder.getSingleItemType().equals(testSingleItemType)) {
				fail("the single item decoder's item type id is not same to the expected item type id");
			}
			
			
			String itemName = "에러 타입";
			Object expcectedValue = SelfExn.ErrorType.BodyFormatException;
			int itemSize = -1;
			String nativeItemCharset = null;
			
			
			BinaryOutputStreamIF binaryOutputStream = new FixedSizeOutputStream(streamByteBuffer, streamCharsetEncoder);
			
			try {
				thbSingleItemEncoder.putValue(itemTypeID, itemName, expcectedValue, itemSize, nativeItemCharset, binaryOutputStream);
			} catch (Exception e) {
				log.warn("fail to encode a single item value", e);
				fail("fail to encode a single item value");
			}
			
			streamByteBuffer.flip();			
			BinaryInputStreamIF binaryInputStream = new FixedSizeInputStream(streamByteBuffer, streamCharsetDecoder);
			
			Object actualValue = null;
			try {
				actualValue = thbSingleItemDecoder.getValue(itemTypeID, itemName, itemSize, nativeItemCharset, binaryInputStream);
			} catch (Exception e) {
				log.warn("fail to decode a single item value", e);
				fail("fail to decode a single item value");
			}
			
			if (0 != binaryInputStream.available()) {
				fail("data remaining after decoding");
			}
			
			assertEquals(expcectedValue, actualValue);
		}
		
		
		{
			streamByteBuffer.clear();
			SingleItemType testSingleItemType = SingleItemType.BYTE;
			int itemTypeID = testSingleItemType.getItemTypeID();
			AbstractTHBSingleItemEncoder thbSingleItemEncoder = thbSingleItemEncoderMatcher.get(itemTypeID);
			AbstractTHBSingleItemDecoder thbSingleItemDecoder = thbSingleItemDecoderMatcher.get(itemTypeID);
			
			if (! thbSingleItemEncoder.getSingleItemType().equals(testSingleItemType)) {
				fail("the single item encoder's item type id is not same to the expected item type id");
			}
			
			if (! thbSingleItemDecoder.getSingleItemType().equals(testSingleItemType)) {
				fail("the single item decoder's item type id is not same to the expected item type id");
			}
			
			
			String itemName = "byte 변수";
			Object expcectedValue = (byte)23;
			int itemSize = -1;
			String nativeItemCharset = null;
			
			
			BinaryOutputStreamIF binaryOutputStream = new FixedSizeOutputStream(streamByteBuffer, streamCharsetEncoder);
			
			try {
				thbSingleItemEncoder.putValue(itemTypeID, itemName, expcectedValue, itemSize, nativeItemCharset, binaryOutputStream);
			} catch (Exception e) {
				log.warn("fail to encode a single item value", e);
				fail("fail to encode a single item value");
			}
			
			streamByteBuffer.flip();			
			BinaryInputStreamIF binaryInputStream = new FixedSizeInputStream(streamByteBuffer, streamCharsetDecoder);
			
			Object actualValue = null;
			try {
				actualValue = thbSingleItemDecoder.getValue(itemTypeID, itemName, itemSize, nativeItemCharset, binaryInputStream);
			} catch (Exception e) {
				log.warn("fail to decode a single item value", e);
				fail("fail to decode a single item value");
			}
			
			if (0 != binaryInputStream.available()) {
				fail("data remaining after decoding");
			}
			
			assertEquals(expcectedValue, actualValue);
		}
		
		
		{
			streamByteBuffer.clear();
			SingleItemType testSingleItemType = SingleItemType.UNSIGNED_BYTE;
			int itemTypeID = testSingleItemType.getItemTypeID();
			AbstractTHBSingleItemEncoder thbSingleItemEncoder = thbSingleItemEncoderMatcher.get(itemTypeID);
			AbstractTHBSingleItemDecoder thbSingleItemDecoder = thbSingleItemDecoderMatcher.get(itemTypeID);
			
			if (! thbSingleItemEncoder.getSingleItemType().equals(testSingleItemType)) {
				fail("the single item encoder's item type id is not same to the expected item type id");
			}
			
			if (! thbSingleItemDecoder.getSingleItemType().equals(testSingleItemType)) {
				fail("the single item decoder's item type id is not same to the expected item type id");
			}
			
			
			String itemName = "unsigned byte 변수";
			Object expcectedValue = (short)200;
			int itemSize = -1;
			String nativeItemCharset = null;
			
			
			BinaryOutputStreamIF binaryOutputStream = new FixedSizeOutputStream(streamByteBuffer, streamCharsetEncoder);
			
			try {
				thbSingleItemEncoder.putValue(itemTypeID, itemName, expcectedValue, itemSize, nativeItemCharset, binaryOutputStream);
			} catch (Exception e) {
				log.warn("fail to encode a single item value", e);
				fail("fail to encode a single item value");
			}
			
			streamByteBuffer.flip();			
			BinaryInputStreamIF binaryInputStream = new FixedSizeInputStream(streamByteBuffer, streamCharsetDecoder);
			
			Object actualValue = null;
			try {
				actualValue = thbSingleItemDecoder.getValue(itemTypeID, itemName, itemSize, nativeItemCharset, binaryInputStream);
			} catch (Exception e) {
				log.warn("fail to decode a single item value", e);
				fail("fail to decode a single item value");
			}
			
			if (0 != binaryInputStream.available()) {
				fail("data remaining after decoding");
			}
			
			assertEquals(expcectedValue, actualValue);
		}
		
		
		{
			streamByteBuffer.clear();
			SingleItemType testSingleItemType = SingleItemType.SHORT;
			int itemTypeID = testSingleItemType.getItemTypeID();
			AbstractTHBSingleItemEncoder thbSingleItemEncoder = thbSingleItemEncoderMatcher.get(itemTypeID);
			AbstractTHBSingleItemDecoder thbSingleItemDecoder = thbSingleItemDecoderMatcher.get(itemTypeID);
			
			if (! thbSingleItemEncoder.getSingleItemType().equals(testSingleItemType)) {
				fail("the single item encoder's item type id is not same to the expected item type id");
			}
			
			if (! thbSingleItemDecoder.getSingleItemType().equals(testSingleItemType)) {
				fail("the single item decoder's item type id is not same to the expected item type id");
			}
			
			
			String itemName = "short 변수";
			Object expcectedValue = (short)1000;
			int itemSize = -1;
			String nativeItemCharset = null;
			
			
			BinaryOutputStreamIF binaryOutputStream = new FixedSizeOutputStream(streamByteBuffer, streamCharsetEncoder);
			
			try {
				thbSingleItemEncoder.putValue(itemTypeID, itemName, expcectedValue, itemSize, nativeItemCharset, binaryOutputStream);
			} catch (Exception e) {
				log.warn("fail to encode a single item value", e);
				fail("fail to encode a single item value");
			}
			
			streamByteBuffer.flip();			
			BinaryInputStreamIF binaryInputStream = new FixedSizeInputStream(streamByteBuffer, streamCharsetDecoder);
			
			Object actualValue = null;
			try {
				actualValue = thbSingleItemDecoder.getValue(itemTypeID, itemName, itemSize, nativeItemCharset, binaryInputStream);
			} catch (Exception e) {
				log.warn("fail to decode a single item value", e);
				fail("fail to decode a single item value");
			}
			
			if (0 != binaryInputStream.available()) {
				fail("data remaining after decoding");
			}
			
			assertEquals(expcectedValue, actualValue);
		}
		
		{
			streamByteBuffer.clear();
			SingleItemType testSingleItemType = SingleItemType.UNSIGNED_SHORT;
			int itemTypeID = testSingleItemType.getItemTypeID();
			AbstractTHBSingleItemEncoder thbSingleItemEncoder = thbSingleItemEncoderMatcher.get(itemTypeID);
			AbstractTHBSingleItemDecoder thbSingleItemDecoder = thbSingleItemDecoderMatcher.get(itemTypeID);
			
			if (! thbSingleItemEncoder.getSingleItemType().equals(testSingleItemType)) {
				fail("the single item encoder's item type id is not same to the expected item type id");
			}
			
			if (! thbSingleItemDecoder.getSingleItemType().equals(testSingleItemType)) {
				fail("the single item decoder's item type id is not same to the expected item type id");
			}
			
			
			String itemName = "unsigned short 변수";
			Object expcectedValue = Short.MAX_VALUE+1000;
			int itemSize = -1;
			String nativeItemCharset = null;
			
			
			BinaryOutputStreamIF binaryOutputStream = new FixedSizeOutputStream(streamByteBuffer, streamCharsetEncoder);
			
			try {
				thbSingleItemEncoder.putValue(itemTypeID, itemName, expcectedValue, itemSize, nativeItemCharset, binaryOutputStream);
			} catch (Exception e) {
				log.warn("fail to encode a single item value", e);
				fail("fail to encode a single item value");
			}
			
			streamByteBuffer.flip();			
			BinaryInputStreamIF binaryInputStream = new FixedSizeInputStream(streamByteBuffer, streamCharsetDecoder);
			
			Object actualValue = null;
			try {
				actualValue = thbSingleItemDecoder.getValue(itemTypeID, itemName, itemSize, nativeItemCharset, binaryInputStream);
			} catch (Exception e) {
				log.warn("fail to decode a single item value", e);
				fail("fail to decode a single item value");
			}
			
			if (0 != binaryInputStream.available()) {
				fail("data remaining after decoding");
			}
			
			assertEquals(expcectedValue, actualValue);
		}
		
		{
			streamByteBuffer.clear();
			SingleItemType testSingleItemType = SingleItemType.INTEGER;
			int itemTypeID = testSingleItemType.getItemTypeID();
			AbstractTHBSingleItemEncoder thbSingleItemEncoder = thbSingleItemEncoderMatcher.get(itemTypeID);
			AbstractTHBSingleItemDecoder thbSingleItemDecoder = thbSingleItemDecoderMatcher.get(itemTypeID);
			
			if (! thbSingleItemEncoder.getSingleItemType().equals(testSingleItemType)) {
				fail("the single item encoder's item type id is not same to the expected item type id");
			}
			
			if (! thbSingleItemDecoder.getSingleItemType().equals(testSingleItemType)) {
				fail("the single item decoder's item type id is not same to the expected item type id");
			}
			
			
			String itemName = "integer 변수";
			Object expcectedValue = CommonStaticFinalVars.UNSIGNED_SHORT_MAX+1000;
			int itemSize = -1;
			String nativeItemCharset = null;
			
			
			BinaryOutputStreamIF binaryOutputStream = new FixedSizeOutputStream(streamByteBuffer, streamCharsetEncoder);
			
			try {
				thbSingleItemEncoder.putValue(itemTypeID, itemName, expcectedValue, itemSize, nativeItemCharset, binaryOutputStream);
			} catch (Exception e) {
				log.warn("fail to encode a single item value", e);
				fail("fail to encode a single item value");
			}
			
			streamByteBuffer.flip();			
			BinaryInputStreamIF binaryInputStream = new FixedSizeInputStream(streamByteBuffer, streamCharsetDecoder);
			
			Object actualValue = null;
			try {
				actualValue = thbSingleItemDecoder.getValue(itemTypeID, itemName, itemSize, nativeItemCharset, binaryInputStream);
			} catch (Exception e) {
				log.warn("fail to decode a single item value", e);
				fail("fail to decode a single item value");
			}
			
			if (0 != binaryInputStream.available()) {
				fail("data remaining after decoding");
			}
			
			assertEquals(expcectedValue, actualValue);
		}
		
		{
			streamByteBuffer.clear();
			SingleItemType testSingleItemType = SingleItemType.UNSIGNED_INTEGER;
			int itemTypeID = testSingleItemType.getItemTypeID();
			AbstractTHBSingleItemEncoder thbSingleItemEncoder = thbSingleItemEncoderMatcher.get(itemTypeID);
			AbstractTHBSingleItemDecoder thbSingleItemDecoder = thbSingleItemDecoderMatcher.get(itemTypeID);
			
			if (! thbSingleItemEncoder.getSingleItemType().equals(testSingleItemType)) {
				fail("the single item encoder's item type id is not same to the expected item type id");
			}
			
			if (! thbSingleItemDecoder.getSingleItemType().equals(testSingleItemType)) {
				fail("the single item decoder's item type id is not same to the expected item type id");
			}
			
			
			String itemName = "unsigned integer 변수";
			Object expcectedValue = Integer.MAX_VALUE+1000L;
			int itemSize = -1;
			String nativeItemCharset = null;
			
			
			BinaryOutputStreamIF binaryOutputStream = new FixedSizeOutputStream(streamByteBuffer, streamCharsetEncoder);
			
			try {
				thbSingleItemEncoder.putValue(itemTypeID, itemName, expcectedValue, itemSize, nativeItemCharset, binaryOutputStream);
			} catch (Exception e) {
				log.warn("fail to encode a single item value", e);
				fail("fail to encode a single item value");
			}
			
			streamByteBuffer.flip();			
			BinaryInputStreamIF binaryInputStream = new FixedSizeInputStream(streamByteBuffer, streamCharsetDecoder);
			
			Object actualValue = null;
			try {
				actualValue = thbSingleItemDecoder.getValue(itemTypeID, itemName, itemSize, nativeItemCharset, binaryInputStream);
			} catch (Exception e) {
				log.warn("fail to decode a single item value", e);
				fail("fail to decode a single item value");
			}
			
			// log.info("actualValue={}", actualValue);
			
			if (0 != binaryInputStream.available()) {
				fail("data remaining after decoding");
			}
			
			assertEquals(expcectedValue, actualValue);
		}
		
		{
			streamByteBuffer.clear();
			SingleItemType testSingleItemType = SingleItemType.LONG;
			int itemTypeID = testSingleItemType.getItemTypeID();
			AbstractTHBSingleItemEncoder thbSingleItemEncoder = thbSingleItemEncoderMatcher.get(itemTypeID);
			AbstractTHBSingleItemDecoder thbSingleItemDecoder = thbSingleItemDecoderMatcher.get(itemTypeID);
			
			if (! thbSingleItemEncoder.getSingleItemType().equals(testSingleItemType)) {
				fail("the single item encoder's item type id is not same to the expected item type id");
			}
			
			if (! thbSingleItemDecoder.getSingleItemType().equals(testSingleItemType)) {
				fail("the single item decoder's item type id is not same to the expected item type id");
			}
			
			
			String itemName = "long 변수";
			Object expcectedValue = CommonStaticFinalVars.UNSIGNED_INTEGER_MAX+1000L;
			int itemSize = -1;
			String nativeItemCharset = null;
			
			
			BinaryOutputStreamIF binaryOutputStream = new FixedSizeOutputStream(streamByteBuffer, streamCharsetEncoder);
			
			try {
				thbSingleItemEncoder.putValue(itemTypeID, itemName, expcectedValue, itemSize, nativeItemCharset, binaryOutputStream);
			} catch (Exception e) {
				log.warn("fail to encode a single item value", e);
				fail("fail to encode a single item value");
			}
			
			streamByteBuffer.flip();			
			BinaryInputStreamIF binaryInputStream = new FixedSizeInputStream(streamByteBuffer, streamCharsetDecoder);
			
			Object actualValue = null;
			try {
				actualValue = thbSingleItemDecoder.getValue(itemTypeID, itemName, itemSize, nativeItemCharset, binaryInputStream);
			} catch (Exception e) {
				log.warn("fail to decode a single item value", e);
				fail("fail to decode a single item value");
			}
			
			// log.info("actualValue={}", actualValue);
			
			if (0 != binaryInputStream.available()) {
				fail("data remaining after decoding");
			}
			
			assertEquals(expcectedValue, actualValue);
		}
		
		{
			streamByteBuffer.clear();
			SingleItemType testSingleItemType = SingleItemType.UB_PASCAL_STRING;
			int itemTypeID = testSingleItemType.getItemTypeID();
			AbstractTHBSingleItemEncoder thbSingleItemEncoder = thbSingleItemEncoderMatcher.get(itemTypeID);
			AbstractTHBSingleItemDecoder thbSingleItemDecoder = thbSingleItemDecoderMatcher.get(itemTypeID);
			
			if (! thbSingleItemEncoder.getSingleItemType().equals(testSingleItemType)) {
				fail("the single item encoder's item type id is not same to the expected item type id");
			}
			
			if (! thbSingleItemDecoder.getSingleItemType().equals(testSingleItemType)) {
				fail("the single item decoder's item type id is not same to the expected item type id");
			}
			
			
			String itemName = "ub pascal string 변수";
			Object expcectedValue = "한글테스트1";
			int itemSize = -1;
			String nativeItemCharset = null;
			
			
			BinaryOutputStreamIF binaryOutputStream = new FixedSizeOutputStream(streamByteBuffer, streamCharsetEncoder);
			
			try {
				thbSingleItemEncoder.putValue(itemTypeID, itemName, expcectedValue, itemSize, nativeItemCharset, binaryOutputStream);
			} catch (Exception e) {
				log.warn("fail to encode a single item value", e);
				fail("fail to encode a single item value");
			}
			
			streamByteBuffer.flip();			
			BinaryInputStreamIF binaryInputStream = new FixedSizeInputStream(streamByteBuffer, streamCharsetDecoder);
			// log.info("binaryInputStream.available={}", binaryInputStream.available());
			
			final int typeSize=1;
			final int stringLengthSize=1;
			final int stringSize=16;
			
			if ((typeSize+stringLengthSize+stringSize) != binaryInputStream.available()) {
				fail("not expected size");
			}
			
			
			Object actualValue = null;
			try {
				actualValue = thbSingleItemDecoder.getValue(itemTypeID, itemName, itemSize, nativeItemCharset, binaryInputStream);
			} catch (Exception e) {
				log.warn("fail to decode a single item value", e);
				fail("fail to decode a single item value");
			}
			
			// log.info("actualValue={}", actualValue);
			
			if (0 != binaryInputStream.available()) {
				fail("data remaining after decoding");
			}
			
			assertEquals(expcectedValue, actualValue);
		}
		
		{
			streamByteBuffer.clear();
			SingleItemType testSingleItemType = SingleItemType.US_PASCAL_STRING;
			int itemTypeID = testSingleItemType.getItemTypeID();
			AbstractTHBSingleItemEncoder thbSingleItemEncoder = thbSingleItemEncoderMatcher.get(itemTypeID);
			AbstractTHBSingleItemDecoder thbSingleItemDecoder = thbSingleItemDecoderMatcher.get(itemTypeID);
			
			if (! thbSingleItemEncoder.getSingleItemType().equals(testSingleItemType)) {
				fail("the single item encoder's item type id is not same to the expected item type id");
			}
			
			if (! thbSingleItemDecoder.getSingleItemType().equals(testSingleItemType)) {
				fail("the single item decoder's item type id is not same to the expected item type id");
			}
			
			
			String itemName = "us pascal string 변수";
			Object expcectedValue = "한글테스트2";
			int itemSize = -1;
			String nativeItemCharset = null;
			
			
			BinaryOutputStreamIF binaryOutputStream = new FixedSizeOutputStream(streamByteBuffer, streamCharsetEncoder);
			
			try {
				thbSingleItemEncoder.putValue(itemTypeID, itemName, expcectedValue, itemSize, nativeItemCharset, binaryOutputStream);
			} catch (Exception e) {
				log.warn("fail to encode a single item value", e);
				fail("fail to encode a single item value");
			}
			
			streamByteBuffer.flip();			
			BinaryInputStreamIF binaryInputStream = new FixedSizeInputStream(streamByteBuffer, streamCharsetDecoder);
			final int typeSize=1;
			final int stringLengthSize=2;
			final int stringSize=16;
			
			if ((typeSize+stringLengthSize+stringSize) != binaryInputStream.available()) {
				fail("not expected size");
			}
			
			Object actualValue = null;
			try {
				actualValue = thbSingleItemDecoder.getValue(itemTypeID, itemName, itemSize, nativeItemCharset, binaryInputStream);
			} catch (Exception e) {
				log.warn("fail to decode a single item value", e);
				fail("fail to decode a single item value");
			}
			
			// log.info("actualValue={}", actualValue);
			
			if (0 != binaryInputStream.available()) {
				fail("data remaining after decoding");
			}
			
			assertEquals(expcectedValue, actualValue);
		}
		
		{
			streamByteBuffer.clear();
			SingleItemType testSingleItemType = SingleItemType.SI_PASCAL_STRING;
			int itemTypeID = testSingleItemType.getItemTypeID();
			AbstractTHBSingleItemEncoder thbSingleItemEncoder = thbSingleItemEncoderMatcher.get(itemTypeID);
			AbstractTHBSingleItemDecoder thbSingleItemDecoder = thbSingleItemDecoderMatcher.get(itemTypeID);
			
			if (! thbSingleItemEncoder.getSingleItemType().equals(testSingleItemType)) {
				fail("the single item encoder's item type id is not same to the expected item type id");
			}
			
			if (! thbSingleItemDecoder.getSingleItemType().equals(testSingleItemType)) {
				fail("the single item decoder's item type id is not same to the expected item type id");
			}
			
			
			String itemName = "si pascal string 변수";
			Object expcectedValue = "한글테스트3";
			int itemSize = -1;
			String nativeItemCharset = null;
			
			
			BinaryOutputStreamIF binaryOutputStream = new FixedSizeOutputStream(streamByteBuffer, streamCharsetEncoder);
			
			try {
				thbSingleItemEncoder.putValue(itemTypeID, itemName, expcectedValue, itemSize, nativeItemCharset, binaryOutputStream);
			} catch (Exception e) {
				log.warn("fail to encode a single item value", e);
				fail("fail to encode a single item value");
			}
			
			streamByteBuffer.flip();			
			BinaryInputStreamIF binaryInputStream = new FixedSizeInputStream(streamByteBuffer, streamCharsetDecoder);
			
			final int typeSize=1;
			final int stringLengthSize=4;
			final int stringSize=16;
			
			if ((typeSize+stringLengthSize+stringSize) != binaryInputStream.available()) {
				fail("not expected size");
			}
			
			Object actualValue = null;
			try {
				actualValue = thbSingleItemDecoder.getValue(itemTypeID, itemName, itemSize, nativeItemCharset, binaryInputStream);
			} catch (Exception e) {
				log.warn("fail to decode a single item value", e);
				fail("fail to decode a single item value");
			}
			
			
			//log.info("actualValue={}", actualValue);
			
			if (0 != binaryInputStream.available()) {
				fail("data remaining after decoding");
			}
			
			assertEquals(expcectedValue, actualValue);
		}
		
		{
			streamByteBuffer.clear();
			SingleItemType testSingleItemType = SingleItemType.FIXED_LENGTH_STRING;
			int itemTypeID = testSingleItemType.getItemTypeID();
			AbstractTHBSingleItemEncoder thbSingleItemEncoder = thbSingleItemEncoderMatcher.get(itemTypeID);
			AbstractTHBSingleItemDecoder thbSingleItemDecoder = thbSingleItemDecoderMatcher.get(itemTypeID);
			
			if (! thbSingleItemEncoder.getSingleItemType().equals(testSingleItemType)) {
				fail("the single item encoder's item type id is not same to the expected item type id");
			}
			
			if (! thbSingleItemDecoder.getSingleItemType().equals(testSingleItemType)) {
				fail("the single item decoder's item type id is not same to the expected item type id");
			}
			
			
			String itemName = "fixed length string 변수";
			Object expcectedValue = "한글테스트4";
			int itemSize = 20;
			String nativeItemCharset = null;			
			
			BinaryOutputStreamIF binaryOutputStream = new FixedSizeOutputStream(streamByteBuffer, streamCharsetEncoder);
			
			try {
				thbSingleItemEncoder.putValue(itemTypeID, itemName, expcectedValue, itemSize, nativeItemCharset, binaryOutputStream);
			} catch (Exception e) {
				log.warn("fail to encode a single item value", e);
				fail("fail to encode a single item value");
			}
			
			streamByteBuffer.flip();			
			BinaryInputStreamIF binaryInputStream = new FixedSizeInputStream(streamByteBuffer, streamCharsetDecoder);
			
			Object actualValue = null;
			try {
				actualValue = thbSingleItemDecoder.getValue(itemTypeID, itemName, itemSize, nativeItemCharset, binaryInputStream);
			} catch (Exception e) {
				log.warn("fail to decode a single item value", e);
				fail("fail to decode a single item value");
			}
			
			
			//log.info("actualValue={}", actualValue);
			
			if (0 != binaryInputStream.available()) {
				fail("data remaining after decoding");
			}
			
			assertEquals(expcectedValue, ((String)actualValue).trim());
		}
		
		{
			streamByteBuffer.clear();
			SingleItemType testSingleItemType = SingleItemType.UB_VARIABLE_LENGTH_BYTES;
			int itemTypeID = testSingleItemType.getItemTypeID();
			AbstractTHBSingleItemEncoder thbSingleItemEncoder = thbSingleItemEncoderMatcher.get(itemTypeID);
			AbstractTHBSingleItemDecoder thbSingleItemDecoder = thbSingleItemDecoderMatcher.get(itemTypeID);
			
			if (! thbSingleItemEncoder.getSingleItemType().equals(testSingleItemType)) {
				fail("the single item encoder's item type id is not same to the expected item type id");
			}
			
			if (! thbSingleItemDecoder.getSingleItemType().equals(testSingleItemType)) {
				fail("the single item decoder's item type id is not same to the expected item type id");
			}
			
			
			String itemName = "ub variable length bytes 변수";
			Object expcectedValue = new byte[]{0x11,0x22};
			int itemSize = -1;
			String nativeItemCharset = null;			
			
			BinaryOutputStreamIF binaryOutputStream = new FixedSizeOutputStream(streamByteBuffer, streamCharsetEncoder);
			
			try {
				thbSingleItemEncoder.putValue(itemTypeID, itemName, expcectedValue, itemSize, nativeItemCharset, binaryOutputStream);
			} catch (Exception e) {
				log.warn("fail to encode a single item value", e);
				fail("fail to encode a single item value");
			}
			
			
			
			streamByteBuffer.flip();			
			BinaryInputStreamIF binaryInputStream = new FixedSizeInputStream(streamByteBuffer, streamCharsetDecoder);
			
			final int typeSize=1;
			final int bytesLengthSize=1;
			final int bytesSize=2;
			
			if ((typeSize+bytesLengthSize+bytesSize) != binaryInputStream.available()) {
				fail("not expected size");
			}
			
			Object actualValue = null;
			try {
				actualValue = thbSingleItemDecoder.getValue(itemTypeID, itemName, itemSize, nativeItemCharset, binaryInputStream);
			} catch (Exception e) {
				log.warn("fail to decode a single item value", e);
				fail("fail to decode a single item value");
			}
			
			if (0 != binaryInputStream.available()) {
				fail("data remaining after decoding");
			}
			
			//log.info("actualValue={}", actualValue);
			Assert.assertArrayEquals((byte[])expcectedValue, (byte[])actualValue);
		}
		
		{
			streamByteBuffer.clear();
			SingleItemType testSingleItemType = SingleItemType.US_VARIABLE_LENGTH_BYTES;
			int itemTypeID = testSingleItemType.getItemTypeID();
			AbstractTHBSingleItemEncoder thbSingleItemEncoder = thbSingleItemEncoderMatcher.get(itemTypeID);
			AbstractTHBSingleItemDecoder thbSingleItemDecoder = thbSingleItemDecoderMatcher.get(itemTypeID);
			
			if (! thbSingleItemEncoder.getSingleItemType().equals(testSingleItemType)) {
				fail("the single item encoder's item type id is not same to the expected item type id");
			}
			
			if (! thbSingleItemDecoder.getSingleItemType().equals(testSingleItemType)) {
				fail("the single item decoder's item type id is not same to the expected item type id");
			}
			
			
			String itemName = "us variable length bytes 변수";
			Object expcectedValue = new byte[]{0x11,0x22, 0x23};
			int itemSize = -1;
			String nativeItemCharset = null;			
			
			BinaryOutputStreamIF binaryOutputStream = new FixedSizeOutputStream(streamByteBuffer, streamCharsetEncoder);
			
			try {
				thbSingleItemEncoder.putValue(itemTypeID, itemName, expcectedValue, itemSize, nativeItemCharset, binaryOutputStream);
			} catch (Exception e) {
				log.warn("fail to encode a single item value", e);
				fail("fail to encode a single item value");
			}
			
			
			
			streamByteBuffer.flip();			
			BinaryInputStreamIF binaryInputStream = new FixedSizeInputStream(streamByteBuffer, streamCharsetDecoder);
			
			final int typeSize=1;
			final int bytesLengthSize=2;
			final int bytesSize=3;
			
			if ((typeSize+bytesLengthSize+bytesSize) != binaryInputStream.available()) {
				fail("not expected size");
			}
			
			Object actualValue = null;
			try {
				actualValue = thbSingleItemDecoder.getValue(itemTypeID, itemName, itemSize, nativeItemCharset, binaryInputStream);
			} catch (Exception e) {
				log.warn("fail to decode a single item value", e);
				fail("fail to decode a single item value");
			}
			
			if (0 != binaryInputStream.available()) {
				fail("data remaining after decoding");
			}
			
			//log.info("actualValue={}", actualValue);
			Assert.assertArrayEquals((byte[])expcectedValue, (byte[])actualValue);
		}
		
		{
			streamByteBuffer.clear();
			SingleItemType testSingleItemType = SingleItemType.SI_VARIABLE_LENGTH_BYTES;
			int itemTypeID = testSingleItemType.getItemTypeID();
			AbstractTHBSingleItemEncoder thbSingleItemEncoder = thbSingleItemEncoderMatcher.get(itemTypeID);
			AbstractTHBSingleItemDecoder thbSingleItemDecoder = thbSingleItemDecoderMatcher.get(itemTypeID);
			
			if (! thbSingleItemEncoder.getSingleItemType().equals(testSingleItemType)) {
				fail("the single item encoder's item type id is not same to the expected item type id");
			}
			
			if (! thbSingleItemDecoder.getSingleItemType().equals(testSingleItemType)) {
				fail("the single item decoder's item type id is not same to the expected item type id");
			}
			
			
			String itemName = "si variable length bytes 변수";
			Object expcectedValue = new byte[]{0x11,0x22, 0x23};
			int itemSize = -1;
			String nativeItemCharset = null;			
			
			BinaryOutputStreamIF binaryOutputStream = new FixedSizeOutputStream(streamByteBuffer, streamCharsetEncoder);
			
			try {
				thbSingleItemEncoder.putValue(itemTypeID, itemName, expcectedValue, itemSize, nativeItemCharset, binaryOutputStream);
			} catch (Exception e) {
				log.warn("fail to encode a single item value", e);
				fail("fail to encode a single item value");
			}
			
			
			
			streamByteBuffer.flip();			
			BinaryInputStreamIF binaryInputStream = new FixedSizeInputStream(streamByteBuffer, streamCharsetDecoder);
			
			final int typeSize=1;
			final int bytesLengthSize=4;
			final int bytesSize=3;
			
			if ((typeSize+bytesLengthSize+bytesSize) != binaryInputStream.available()) {
				fail("not expected size");
			}
			
			Object actualValue = null;
			try {
				actualValue = thbSingleItemDecoder.getValue(itemTypeID, itemName, itemSize, nativeItemCharset, binaryInputStream);
			} catch (Exception e) {
				log.warn("fail to decode a single item value", e);
				fail("fail to decode a single item value");
			}
			
			if (0 != binaryInputStream.available()) {
				fail("data remaining after decoding");
			}
			
			//log.info("actualValue={}", actualValue);
			Assert.assertArrayEquals((byte[])expcectedValue, (byte[])actualValue);
		}		
		
		{
			streamByteBuffer.clear();
			SingleItemType testSingleItemType = SingleItemType.FIXED_LENGTH_BYTES;
			int itemTypeID = testSingleItemType.getItemTypeID();
			AbstractTHBSingleItemEncoder thbSingleItemEncoder = thbSingleItemEncoderMatcher.get(itemTypeID);
			AbstractTHBSingleItemDecoder thbSingleItemDecoder = thbSingleItemDecoderMatcher.get(itemTypeID);
			
			if (! thbSingleItemEncoder.getSingleItemType().equals(testSingleItemType)) {
				fail("the single item encoder's item type id is not same to the expected item type id");
			}
			
			if (! thbSingleItemDecoder.getSingleItemType().equals(testSingleItemType)) {
				fail("the single item decoder's item type id is not same to the expected item type id");
			}
			
			
			String itemName = "fixed length bytes 변수";
			byte[] itemValue = new byte[]{0x11,0x22, 0x23};
			int itemSize = 5;
			String nativeItemCharset = null;			
			
			byte[] expcectedValue = Arrays.copyOf(itemValue, itemSize);
			
			BinaryOutputStreamIF binaryOutputStream = new FixedSizeOutputStream(streamByteBuffer, streamCharsetEncoder);
			
			try {
				thbSingleItemEncoder.putValue(itemTypeID, itemName, itemValue, itemSize, nativeItemCharset, binaryOutputStream);
			} catch (Exception e) {
				log.warn("fail to encode a single item value", e);
				fail("fail to encode a single item value");
			}
			
			
			
			streamByteBuffer.flip();			
			BinaryInputStreamIF binaryInputStream = new FixedSizeInputStream(streamByteBuffer, streamCharsetDecoder);
			
			Object actualValue = null;
			try {
				actualValue = thbSingleItemDecoder.getValue(itemTypeID, itemName, itemSize, nativeItemCharset, binaryInputStream);
			} catch (Exception e) {
				log.warn("fail to decode a single item value", e);
				fail("fail to decode a single item value");
			}
			
			if (0 != binaryInputStream.available()) {
				fail("data remaining after decoding");
			}
			
			//log.info("actualValue={}", actualValue);
			Assert.assertArrayEquals("the fixed length is greater than source bytes's length", expcectedValue, (byte[])actualValue);
		}
		
		{
			streamByteBuffer.clear();
			SingleItemType testSingleItemType = SingleItemType.FIXED_LENGTH_BYTES;
			int itemTypeID = testSingleItemType.getItemTypeID();
			AbstractTHBSingleItemEncoder thbSingleItemEncoder = thbSingleItemEncoderMatcher.get(itemTypeID);
			AbstractTHBSingleItemDecoder thbSingleItemDecoder = thbSingleItemDecoderMatcher.get(itemTypeID);
			
			if (! thbSingleItemEncoder.getSingleItemType().equals(testSingleItemType)) {
				fail("the single item encoder's item type id is not same to the expected item type id");
			}
			
			if (! thbSingleItemDecoder.getSingleItemType().equals(testSingleItemType)) {
				fail("the single item decoder's item type id is not same to the expected item type id");
			}
			
			
			String itemName = "fixed length bytes 변수";
			byte[] itemValue = new byte[]{0x11,0x22, 0x23};
			int itemSize = 2;
			String nativeItemCharset = null;			
			
			byte[] expcectedValue = Arrays.copyOf(itemValue, itemSize);
			
			BinaryOutputStreamIF binaryOutputStream = new FixedSizeOutputStream(streamByteBuffer, streamCharsetEncoder);
			
			try {
				thbSingleItemEncoder.putValue(itemTypeID, itemName, itemValue, itemSize, nativeItemCharset, binaryOutputStream);
			} catch (Exception e) {
				log.warn("fail to encode a single item value", e);
				fail("fail to encode a single item value");
			}			
			
			streamByteBuffer.flip();			
			BinaryInputStreamIF binaryInputStream = new FixedSizeInputStream(streamByteBuffer, streamCharsetDecoder);
			
			Object actualValue = null;
			try {
				actualValue = thbSingleItemDecoder.getValue(itemTypeID, itemName, itemSize, nativeItemCharset, binaryInputStream);
			} catch (Exception e) {
				log.warn("fail to decode a single item value", e);
				fail("fail to decode a single item value");
			}
			
			if (0 != binaryInputStream.available()) {
				fail("data remaining after decoding");
			}
			
			//log.info("actualValue={}", actualValue);
			Assert.assertArrayEquals("the fixed length is less than source bytes's length", expcectedValue, (byte[])actualValue);
		}
		
		{
			streamByteBuffer.clear();
			SingleItemType testSingleItemType = SingleItemType.JAVA_SQL_DATE;
			int itemTypeID = testSingleItemType.getItemTypeID();
			AbstractTHBSingleItemEncoder thbSingleItemEncoder = thbSingleItemEncoderMatcher.get(itemTypeID);
			AbstractTHBSingleItemDecoder thbSingleItemDecoder = thbSingleItemDecoderMatcher.get(itemTypeID);
			
			if (! thbSingleItemEncoder.getSingleItemType().equals(testSingleItemType)) {
				fail("the single item encoder's item type id is not same to the expected item type id");
			}
			
			if (! thbSingleItemDecoder.getSingleItemType().equals(testSingleItemType)) {
				fail("the single item decoder's item type id is not same to the expected item type id");
			}
			
			
			String itemName = "sql date 변수";
			Object expcectedValue = new java.sql.Date(new Date().getTime());
			int itemSize = -1;
			String nativeItemCharset = null;
			
			
			BinaryOutputStreamIF binaryOutputStream = new FixedSizeOutputStream(streamByteBuffer, streamCharsetEncoder);
			
			try {
				thbSingleItemEncoder.putValue(itemTypeID, itemName, expcectedValue, itemSize, nativeItemCharset, binaryOutputStream);
			} catch (Exception e) {
				log.warn("fail to encode a single item value", e);
				fail("fail to encode a single item value");
			}
			
			streamByteBuffer.flip();			
			BinaryInputStreamIF binaryInputStream = new FixedSizeInputStream(streamByteBuffer, streamCharsetDecoder);
			
			Object actualValue = null;
			try {
				actualValue = thbSingleItemDecoder.getValue(itemTypeID, itemName, itemSize, nativeItemCharset, binaryInputStream);
			} catch (Exception e) {
				log.warn("fail to decode a single item value", e);
				fail("fail to decode a single item value");
			}
			
			if (0 != binaryInputStream.available()) {
				fail("data remaining after decoding");
			}
			
			assertEquals(expcectedValue, actualValue);
		}
		
		{
			streamByteBuffer.clear();
			SingleItemType testSingleItemType = SingleItemType.JAVA_SQL_TIMESTAMP;
			int itemTypeID = testSingleItemType.getItemTypeID();
			AbstractTHBSingleItemEncoder thbSingleItemEncoder = thbSingleItemEncoderMatcher.get(itemTypeID);
			AbstractTHBSingleItemDecoder thbSingleItemDecoder = thbSingleItemDecoderMatcher.get(itemTypeID);
			
			if (! thbSingleItemEncoder.getSingleItemType().equals(testSingleItemType)) {
				fail("the single item encoder's item type id is not same to the expected item type id");
			}
			
			if (! thbSingleItemDecoder.getSingleItemType().equals(testSingleItemType)) {
				fail("the single item decoder's item type id is not same to the expected item type id");
			}
			
			
			String itemName = "sql timestamp 변수";
			Object expcectedValue = new java.sql.Timestamp(new Date().getTime());
			int itemSize = -1;
			String nativeItemCharset = null;
			
			
			BinaryOutputStreamIF binaryOutputStream = new FixedSizeOutputStream(streamByteBuffer, streamCharsetEncoder);
			
			try {
				thbSingleItemEncoder.putValue(itemTypeID, itemName, expcectedValue, itemSize, nativeItemCharset, binaryOutputStream);
			} catch (Exception e) {
				log.warn("fail to encode a single item value", e);
				fail("fail to encode a single item value");
			}
			
			streamByteBuffer.flip();			
			BinaryInputStreamIF binaryInputStream = new FixedSizeInputStream(streamByteBuffer, streamCharsetDecoder);
			
			Object actualValue = null;
			try {
				actualValue = thbSingleItemDecoder.getValue(itemTypeID, itemName, itemSize, nativeItemCharset, binaryInputStream);
			} catch (Exception e) {
				log.warn("fail to decode a single item value", e);
				fail("fail to decode a single item value");
			}
			
			if (0 != binaryInputStream.available()) {
				fail("data remaining after decoding");
			}
			
			assertEquals(expcectedValue, actualValue);
		}
		
		{
			streamByteBuffer.clear();
			SingleItemType testSingleItemType = SingleItemType.BOOLEAN;
			int itemTypeID = testSingleItemType.getItemTypeID();
			AbstractTHBSingleItemEncoder thbSingleItemEncoder = thbSingleItemEncoderMatcher.get(itemTypeID);
			AbstractTHBSingleItemDecoder thbSingleItemDecoder = thbSingleItemDecoderMatcher.get(itemTypeID);
			
			if (! thbSingleItemEncoder.getSingleItemType().equals(testSingleItemType)) {
				fail("the single item encoder's item type id is not same to the expected item type id");
			}
			
			if (! thbSingleItemDecoder.getSingleItemType().equals(testSingleItemType)) {
				fail("the single item decoder's item type id is not same to the expected item type id");
			}
			
			
			String itemName = "boolean 변수";
			Object expcectedValue = true;
			int itemSize = -1;
			String nativeItemCharset = null;
			
			
			BinaryOutputStreamIF binaryOutputStream = new FixedSizeOutputStream(streamByteBuffer, streamCharsetEncoder);
			
			try {
				thbSingleItemEncoder.putValue(itemTypeID, itemName, expcectedValue, itemSize, nativeItemCharset, binaryOutputStream);
			} catch (Exception e) {
				log.warn("fail to encode a single item value", e);
				fail("fail to encode a single item value");
			}
			
			streamByteBuffer.flip();			
			BinaryInputStreamIF binaryInputStream = new FixedSizeInputStream(streamByteBuffer, streamCharsetDecoder);
			
			Object actualValue = null;
			try {
				actualValue = thbSingleItemDecoder.getValue(itemTypeID, itemName, itemSize, nativeItemCharset, binaryInputStream);
			} catch (Exception e) {
				log.warn("fail to decode a single item value", e);
				fail("fail to decode a single item value");
			}
			
			if (0 != binaryInputStream.available()) {
				fail("data remaining after decoding");
			}
			
			assertEquals(expcectedValue, actualValue);
		}
	}
	
	@Test
	public void test_잘못된크기지정() {
		Charset streamCharset = Charset.forName("utf8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		// CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();
		
		THBSingleItemEncoderMatcherIF thbSingleItemEncoderMatcher = 
				new THBSingleItemEncoderMatcher(streamCharsetEncoder);
		
		SingleItemType[] testSingleItemTypes = {
				SingleItemType.FIXED_LENGTH_STRING,
				SingleItemType.FIXED_LENGTH_BYTES
		};
		
		
		ByteBuffer streamByteBuffer = ByteBuffer.allocate(4096);
		String itemName = "고정 크기 변수";
		Object itemValue = null;
		int itemSize = -1;
		String nativeItemCharset = null;
		
		BinaryOutputStreamIF binaryOutputStream = new FixedSizeOutputStream(streamByteBuffer, streamCharsetEncoder);
		
		
		for (SingleItemType testSingleItemType : testSingleItemTypes) {
			int itemTypeID = testSingleItemType.getItemTypeID();
			AbstractTHBSingleItemEncoder thbSingleItemEncoder = thbSingleItemEncoderMatcher.get(itemTypeID);
			
			try {
				thbSingleItemEncoder.putValue(itemTypeID, itemName, itemValue, itemSize, nativeItemCharset, binaryOutputStream);
			} catch(IllegalArgumentException e) {				
				String errorMessage = e.getMessage();
				if (errorMessage.lastIndexOf("is less than zero") < 0) {
					log.warn("unknown error", e);
					fail("unknown error");
				}
			} catch (Exception e) {
				log.warn("unkwnon error", e);
				fail("unknwon error");
			}
		}
	}
	
	@Test
	public void test_잘못된문자셋() {
		Charset streamCharset = Charset.forName("utf8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		// CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();
		
		THBSingleItemEncoderMatcherIF thbSingleItemEncoderMatcher = 
				new THBSingleItemEncoderMatcher(streamCharsetEncoder);
		
		SingleItemType[] testSingleItemTypes = {
				SingleItemType.UB_PASCAL_STRING,
				SingleItemType.US_PASCAL_STRING,
				SingleItemType.SI_PASCAL_STRING,
				SingleItemType.FIXED_LENGTH_STRING
		};
		
		
		ByteBuffer streamByteBuffer = ByteBuffer.allocate(4096);
		String itemName = "문자열 변수";
		Object itemValue = "한글";
		int itemSize = 20;
		String nativeItemCharset = "utf883";
		
		BinaryOutputStreamIF binaryOutputStream = new FixedSizeOutputStream(streamByteBuffer, streamCharsetEncoder);
		
		
		for (SingleItemType testSingleItemType : testSingleItemTypes) {
			int itemTypeID = testSingleItemType.getItemTypeID();
			AbstractTHBSingleItemEncoder thbSingleItemEncoder = thbSingleItemEncoderMatcher.get(itemTypeID);
			
			try {
				thbSingleItemEncoder.putValue(itemTypeID, itemName, itemValue, itemSize, nativeItemCharset, binaryOutputStream);
			} catch(IllegalArgumentException e) {				
				String errorMessage = e.getMessage();
				if (errorMessage.lastIndexOf("is a bad charset name") < 0) {
					log.warn("unknown error", e);
					fail("unknown error");
				}
			} catch (Exception e) {
				log.warn("unkwnon error", e);
				fail("unknwon error");
			}
		}
	}
	
	@Test
	public void test_사용자지정문자셋() {
		Charset streamCharset = Charset.forName("utf8");
		CharsetEncoder streamCharsetEncoder = streamCharset.newEncoder();
		CharsetDecoder streamCharsetDecoder = streamCharset.newDecoder();
		
		THBSingleItemEncoderMatcherIF thbSingleItemEncoderMatcher = 
				new THBSingleItemEncoderMatcher(streamCharsetEncoder);
		
		THBSingleItemDecoderMatcherIF thbSingleItemDecoderMatcher = 
				new THBSingleItemDecoderMatcher(streamCharsetDecoder);
		
		
		SingleItemType[] testSingleItemTypes = {
				SingleItemType.UB_PASCAL_STRING,
				SingleItemType.US_PASCAL_STRING,
				SingleItemType.SI_PASCAL_STRING,
				SingleItemType.FIXED_LENGTH_STRING
		};
		
		
		ByteBuffer streamByteBuffer = ByteBuffer.allocate(4096);
		String itemName = "문자열 변수";
		Object expcectedValue = "한글";
		int itemSize = 4;
		String nativeItemCharset = "euc-kr";
		
		BinaryOutputStreamIF binaryOutputStream = new FixedSizeOutputStream(streamByteBuffer, streamCharsetEncoder);
		
		final int typeSize=1;
		
		for (SingleItemType testSingleItemType : testSingleItemTypes) {
			streamByteBuffer.clear();
			
			int itemTypeID = testSingleItemType.getItemTypeID();
			AbstractTHBSingleItemEncoder thbSingleItemEncoder = thbSingleItemEncoderMatcher.get(itemTypeID);
			AbstractTHBSingleItemDecoder thbSingleItemDecoder = thbSingleItemDecoderMatcher.get(itemTypeID);
			
			try {
				thbSingleItemEncoder.putValue(itemTypeID, itemName, expcectedValue, itemSize, nativeItemCharset, binaryOutputStream);
			} catch (Exception e) {
				log.warn("unkwnon error", e);
				fail("unknwon error");
			}
			
			streamByteBuffer.flip();
			
			Object actualValue = null;
			BinaryInputStreamIF binaryInputStream = new FixedSizeInputStream(streamByteBuffer, streamCharsetDecoder);
			
			int exepectedSize = -1;
			
			if (testSingleItemType.equals(SingleItemType.UB_PASCAL_STRING)) {
				int stringLengthSize = 1;
				exepectedSize = typeSize +  stringLengthSize + itemSize;
			} else if (testSingleItemType.equals(SingleItemType.US_PASCAL_STRING)) {
				int stringLengthSize = 2;
				exepectedSize = typeSize +  stringLengthSize + itemSize;
			} else if (testSingleItemType.equals(SingleItemType.SI_PASCAL_STRING)) {
				int stringLengthSize = 4;
				exepectedSize = typeSize +  stringLengthSize + itemSize;
			} else {
				exepectedSize = typeSize +  itemSize;
			}
			
			if (exepectedSize != binaryInputStream.available()) {
				log.info("사용자 정의 문자셋을 적용했을 경우 예측한 스트림 크기={}, 실제 크기={}", exepectedSize, binaryInputStream.available());
				fail("사용자 정의 문자셋을 적용했을 경우 예측한 스트림 크기가 다릅니다");
			}

			try {
				actualValue = thbSingleItemDecoder.getValue(itemTypeID, itemName, itemSize, nativeItemCharset, binaryInputStream);
			} catch (Exception e) {
				log.warn("fail to decode a single item value", e);
				fail("fail to decode a single item value");
			}
			
			if (0 != binaryInputStream.available()) {
				fail("data remaining after decoding");
			}
			
			assertEquals(expcectedValue, actualValue);
			
		}
	}
	
}
