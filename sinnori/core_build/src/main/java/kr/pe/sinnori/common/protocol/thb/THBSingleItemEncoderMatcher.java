package kr.pe.sinnori.common.protocol.thb;

import java.nio.BufferOverflowException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.util.Arrays;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import kr.pe.sinnori.common.etc.CommonStaticFinalVars;
import kr.pe.sinnori.common.exception.NoMoreDataPacketBufferException;
import kr.pe.sinnori.common.exception.SinnoriBufferOverflowException;
import kr.pe.sinnori.common.io.BinaryOutputStreamIF;
import kr.pe.sinnori.common.type.SelfExn;
import kr.pe.sinnori.common.type.SingleItemType;

public class THBSingleItemEncoderMatcher implements THBSingleItemEncoderMatcherIF {
	private InternalLogger log = InternalLoggerFactory.getInstance(THBSingleItemEncoderMatcher.class);
	
	@SuppressWarnings("unused")
	private CharsetEncoder streamCharsetEncoder;
	private CodingErrorAction streamCodingErrorActionOnMalformedInput = null;
	private CodingErrorAction streamCodingErrorActionOnUnmappableCharacter = null;
	
	private final AbstractTHBSingleItemEncoder[] thbSingleItemEncoderList = new AbstractTHBSingleItemEncoder[] { 
			new THBSelfExnErrorPlaceSingleItemEncoder(), new THBSelfExnErrorTypeSingleItemEncoder(),
			new THBByteSingleItemEncoder(), new THBUnsignedByteSingleItemEncoder(), 
			new THBShortSingleItemEncoder(), new THBUnsignedShortSingleItemEncoder(),
			new THBIntSingleItemEncoder(), new THBUnsignedIntSingleItemEncoder(), 
			new THBLongSingleItemEncoder(), new THBUBPascalStringSingleItemEncoder(),
			new THBUSPascalStringSingleItemEncoder(), new THBSIPascalStringSingleItemEncoder(), 
			new THBFixedLengthStringSingleItemEncoder(), new THBUBVariableLengthBytesSingleItemEncoder(), 
			new THBUSVariableLengthBytesSingleItemEncoder(), new THBSIVariableLengthBytesSingleItemEncoder(), 
			new THBFixedLengthBytesSingleItemEncoder(), 
			new THBJavaSqlDateSingleItemEncoder(), new THBJavaSqlTimestampSingleItemEncoder(),
			new THBBooleanSingleItemEncoder()
	};
	
	
	public THBSingleItemEncoderMatcher(CharsetEncoder streamCharsetEncoder) {
		if (null == streamCharsetEncoder) {
			throw new IllegalArgumentException("the parameter streamCharsetEncoder is null");
		}
		this.streamCharsetEncoder = streamCharsetEncoder;
		this.streamCodingErrorActionOnMalformedInput = streamCharsetEncoder.malformedInputAction();
		this.streamCodingErrorActionOnUnmappableCharacter = streamCharsetEncoder.unmappableCharacterAction();
		
		checkValidTHBSingleItemEncoderList();
	}
	
	private void checkValidTHBSingleItemEncoderList() {
		SingleItemType[] singleItemTypes = SingleItemType.values();
		
		if (thbSingleItemEncoderList.length != singleItemTypes.length) {
			log.error("the var thbSingleItemEncoderList.length[{}] is not differnet from the array var singleItemTypes.length[{}]", 
					thbSingleItemEncoderList.length, singleItemTypes.length);
			System.exit(1);
		}
		
		for (int i=0; i < singleItemTypes.length; i++) {
			SingleItemType expectedSingleItemType = singleItemTypes[i];
			SingleItemType actualSingleItemType = thbSingleItemEncoderList[i].getSingleItemType();
			if (! expectedSingleItemType.equals(actualSingleItemType)) {
				log.error("the var thbSingleItemEncoderList[{}]'s SingleItemType[{}] is not the expected SingleItemType[{}]", 
						i, actualSingleItemType.toString(), expectedSingleItemType.toString());
				System.exit(1);
			}
		}
	}

	private final class THBSelfExnErrorPlaceSingleItemEncoder extends AbstractTHBSingleItemEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object nativeItemValue, int itemSize,
				String nativeItemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws BufferOverflowException, SinnoriBufferOverflowException, NoMoreDataPacketBufferException {			
			SelfExn.ErrorPlace itemValue = (SelfExn.ErrorPlace) nativeItemValue;

			writeItemID(itemTypeID, binaryOutputStream);
			binaryOutputStream.putByte(itemValue.getErrorPlaceByte());
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.SELFEXN_ERROR_PLACE;
		}
	}
	
	private final class THBSelfExnErrorTypeSingleItemEncoder extends AbstractTHBSingleItemEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object nativeItemValue, int itemSize,
				String nativeItemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws BufferOverflowException, SinnoriBufferOverflowException, NoMoreDataPacketBufferException {			
			SelfExn.ErrorType itemValue = (SelfExn.ErrorType) nativeItemValue;

			writeItemID(itemTypeID, binaryOutputStream);
			binaryOutputStream.putByte(itemValue.getErrorTypeByte());
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.SELFEXN_ERROR_TYPE;
		}
	}

		
	/** THB 프로토콜의 byte 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBByteSingleItemEncoder extends AbstractTHBSingleItemEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object nativeItemValue, int itemSize,
				String nativeItemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws Exception {			
			byte itemValue = (Byte) nativeItemValue;
			writeItemID(itemTypeID, binaryOutputStream);
			binaryOutputStream.putByte(itemValue);
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.BYTE;
		}
	}

	/** THB 프로토콜의 unsigned byte 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBUnsignedByteSingleItemEncoder extends AbstractTHBSingleItemEncoder {

		@Override
		public void putValue(int itemTypeID, String itemName, Object nativeItemValue, int itemSize,
				String nativeItemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws Exception {
			short itemValue = (Short) nativeItemValue;
			
			writeItemID(itemTypeID, binaryOutputStream);
			binaryOutputStream.putUnsignedByte(itemValue);
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.UNSIGNED_BYTE;
		}
	}

	/** THB 프로토콜의 short 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBShortSingleItemEncoder extends AbstractTHBSingleItemEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object nativeItemValue, int itemSize,
				String nativeItemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws Exception {
			short itemValue = (Short) nativeItemValue;
			
			writeItemID(itemTypeID, binaryOutputStream);
			binaryOutputStream.putShort(itemValue);
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.SHORT;
		}
	}

	/** THB 프로토콜의 unsigned short 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBUnsignedShortSingleItemEncoder extends AbstractTHBSingleItemEncoder {
		
		@Override
		public void putValue(int itemTypeID, String itemName, Object nativeItemValue, int itemSize,
				String nativeItemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws Exception {
			int itemValue = (Integer) nativeItemValue;

			writeItemID(itemTypeID, binaryOutputStream);
			binaryOutputStream.putUnsignedShort(itemValue);
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.UNSIGNED_SHORT;
		}
	}

	/** THB 프로토콜의 integer 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBIntSingleItemEncoder extends AbstractTHBSingleItemEncoder {
		
		@Override
		public void putValue(int itemTypeID, String itemName, Object nativeItemValue, int itemSize,
				String nativeItemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws Exception {
			int itemValue = (Integer) nativeItemValue;
			
			
			writeItemID(itemTypeID, binaryOutputStream);
			binaryOutputStream.putInt(itemValue);
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.INTEGER;
		}
	}

	/** THB 프로토콜의 unsigned integer 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBUnsignedIntSingleItemEncoder extends AbstractTHBSingleItemEncoder {
		
		@Override
		public void putValue(int itemTypeID, String itemName, Object nativeItemValue, int itemSize,
				String nativeItemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws Exception {
			long itemValue = (Long) nativeItemValue;
			
			writeItemID(itemTypeID, binaryOutputStream);
			binaryOutputStream.putUnsignedInt(itemValue);
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.UNSIGNED_INTEGER;
		}
	}

	/** THB 프로토콜의 long 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBLongSingleItemEncoder extends AbstractTHBSingleItemEncoder {		
		@Override
		public void putValue(int itemTypeID, String itemName, Object nativeItemValue, int itemSize,
				String nativeItemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws Exception {
			long itemValue = (Long) nativeItemValue;
			
			writeItemID(itemTypeID, binaryOutputStream);
			binaryOutputStream.putLong(itemValue);
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.LONG;
		}
	}

	/** THB 프로토콜의 ub pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBUBPascalStringSingleItemEncoder extends AbstractTHBSingleItemEncoder {		
		@Override
		public void putValue(int itemTypeID, String itemName, Object nativeItemValue, int itemSize,
				String nativeItemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws Exception {
			String itemValue = (String) nativeItemValue;
			
			writeItemID(itemTypeID, binaryOutputStream);
			
			if (null == nativeItemCharset) {
				binaryOutputStream.putUBPascalString(itemValue);
			} else {
				Charset itemCharset = null;
				try {
					itemCharset = Charset.forName(nativeItemCharset);
				} catch(Exception e) {
					String errorMessage = new StringBuffer("the parameter nativeItemCharset[")
							.append(nativeItemCharset).append("] is a bad charset name").toString();
					
					throw new IllegalArgumentException(errorMessage);
				}
				binaryOutputStream.putUBPascalString(itemValue, itemCharset);
			}
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.UB_PASCAL_STRING;
		}
	}

	/** THB 프로토콜의 us pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBUSPascalStringSingleItemEncoder extends AbstractTHBSingleItemEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object nativeItemValue, int itemSize,
				String nativeItemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws Exception {
			String itemValue = (String) nativeItemValue;
			
			
			writeItemID(itemTypeID, binaryOutputStream);
			if (null == nativeItemCharset) {
				binaryOutputStream.putUSPascalString(itemValue);
			} else {
				Charset itemCharset = null;
				try {
					itemCharset = Charset.forName(nativeItemCharset);
				} catch(Exception e) {
					String errorMessage = new StringBuffer("the parameter nativeItemCharset[")
							.append(nativeItemCharset).append("] is a bad charset name").toString();
					
					throw new IllegalArgumentException(errorMessage);
				}
				
				binaryOutputStream.putUSPascalString(itemValue, itemCharset);
			}
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.US_PASCAL_STRING;
		}
	}

	/** THB 프로토콜의 si pascal string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBSIPascalStringSingleItemEncoder extends AbstractTHBSingleItemEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object nativeItemValue, int itemSize,
				String nativeItemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws Exception {
			String itemValue = (String) nativeItemValue;			
			
			writeItemID(itemTypeID, binaryOutputStream);
			if (null == nativeItemCharset) {
				binaryOutputStream.putSIPascalString(itemValue);
			} else {
				Charset itemCharset = null;
				try {
					itemCharset = Charset.forName(nativeItemCharset);
				} catch(Exception e) {
					String errorMessage = new StringBuffer("the parameter nativeItemCharset[")
							.append(nativeItemCharset).append("] is a bad charset name").toString();
					
					throw new IllegalArgumentException(errorMessage);
				}
				binaryOutputStream.putSIPascalString(itemValue, itemCharset);
			}
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.SI_PASCAL_STRING;
		}
	}

	/** THB 프로토콜의 fixed length string 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBFixedLengthStringSingleItemEncoder extends AbstractTHBSingleItemEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object nativeItemValue, int itemSize,
				String nativeItemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws Exception {
			if (itemSize < 0) {
				String errorMesage = new StringBuilder("the parameter itemSize[")
						.append(itemSize)
						.append("] is less than zero").toString();
				throw new IllegalArgumentException(errorMesage);
			}
			
			String itemValue = (String) nativeItemValue;
			
			
			writeItemID(itemTypeID, binaryOutputStream);
			
			if (null == nativeItemCharset) {
				binaryOutputStream.putFixedLengthString(itemSize, itemValue);
			} else {
				Charset itemCharset = null;
				try {
					itemCharset = Charset.forName(nativeItemCharset);
				} catch(Exception e) {
					String errorMessage = new StringBuffer("the parameter nativeItemCharset[")
							.append(nativeItemCharset).append("] is a bad charset name").toString();
					
					throw new IllegalArgumentException(errorMessage);
				}
				
				CharsetEncoder userDefinedCharsetEncoder =  itemCharset.newEncoder();
				userDefinedCharsetEncoder.onMalformedInput(streamCodingErrorActionOnMalformedInput);
				userDefinedCharsetEncoder.onUnmappableCharacter(streamCodingErrorActionOnUnmappableCharacter);
				
				binaryOutputStream.putFixedLengthString(itemSize, itemValue, userDefinedCharsetEncoder);
			}

		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.FIXED_LENGTH_STRING;
		}
	}

	

	/** THB 프로토콜의 ub variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBUBVariableLengthBytesSingleItemEncoder extends AbstractTHBSingleItemEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object nativeItemValue, int itemSize,
				String nativeItemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws Exception {
			writeItemID(itemTypeID, binaryOutputStream);			
			
			byte itemValue[] = (byte[]) nativeItemValue;
			binaryOutputStream.putUnsignedByte(itemValue.length);
			binaryOutputStream.putBytes(itemValue);
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.UB_VARIABLE_LENGTH_BYTES;
		}
	}

	/** THB 프로토콜의 us variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBUSVariableLengthBytesSingleItemEncoder extends AbstractTHBSingleItemEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object nativeItemValue, int itemSize,
				String nativeItemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws Exception {
			writeItemID(itemTypeID, binaryOutputStream);
			
			byte itemValue[] = (byte[]) nativeItemValue;
			binaryOutputStream.putUnsignedShort(itemValue.length);
			binaryOutputStream.putBytes(itemValue);
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.US_VARIABLE_LENGTH_BYTES;
		}
	}
	
	/** THB 프로토콜의 si variable length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBSIVariableLengthBytesSingleItemEncoder extends AbstractTHBSingleItemEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object nativeItemValue, int itemSize,
				String nativeItemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws Exception {
			
			writeItemID(itemTypeID, binaryOutputStream);
			
			byte itemValue[] = (byte[]) nativeItemValue;
			binaryOutputStream.putInt(itemValue.length);
			binaryOutputStream.putBytes(itemValue);
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.SI_VARIABLE_LENGTH_BYTES;
		}
	}
	
	/** THB 프로토콜의 fixed length byte[] 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class THBFixedLengthBytesSingleItemEncoder extends AbstractTHBSingleItemEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object nativeItemValue, int itemSize,
				String nativeItemCharset, BinaryOutputStreamIF binaryOutputStream)
				throws Exception {
			if (itemSize < 0) {
				String errorMesage = new StringBuilder("the parameter itemSize[")
						.append(itemSize)
						.append("] is less than zero").toString();
				throw new IllegalArgumentException(errorMesage);
			}
			
			writeItemID(itemTypeID, binaryOutputStream);
			
			
			byte itemValue[] = (byte[]) nativeItemValue;
			
			byte resultBytes[] = Arrays.copyOf(itemValue, itemSize);
			binaryOutputStream.putBytes(resultBytes);
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.FIXED_LENGTH_BYTES;
		}
	}
	
	/** THB 프로토콜의 java sql date 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class  THBJavaSqlDateSingleItemEncoder extends AbstractTHBSingleItemEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object nativeItemValue,
				int itemSize, String nativeItemCharset,
				BinaryOutputStreamIF binaryOutputStream) throws Exception {
			
			java.sql.Date itemValue = (java.sql.Date)nativeItemValue;
			long resultValue = itemValue.getTime();
			
			writeItemID(itemTypeID, binaryOutputStream);
			binaryOutputStream.putLong(resultValue);
			
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.JAVA_SQL_DATE;
		}
	}
	
	/** THB 프로토콜의 java sql date 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class  THBJavaSqlTimestampSingleItemEncoder extends AbstractTHBSingleItemEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object nativeItemValue,
				int itemSize, String nativeItemCharset,
				BinaryOutputStreamIF binaryOutputStream) throws Exception {			
			java.sql.Timestamp itemValue = (java.sql.Timestamp)nativeItemValue;
			long resultValue = itemValue.getTime();
			
			writeItemID(itemTypeID, binaryOutputStream);
			binaryOutputStream.putLong(resultValue);			
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.JAVA_SQL_TIMESTAMP;
		}
	}
	
	/** THB 프로토콜의 boolean 타입 단일 항목 스트림 변환기 구현 클래스 */
	private final class  THBBooleanSingleItemEncoder extends AbstractTHBSingleItemEncoder {
		@Override
		public void putValue(int itemTypeID, String itemName, Object nativeItemValue,
				int itemSize, String nativeItemCharset,
				BinaryOutputStreamIF binaryOutputStream) throws Exception {			
			boolean itemValue = (Boolean)nativeItemValue;		
			
			byte resultValue = (itemValue) ? 1 : CommonStaticFinalVars.ZERO_BYTE;
						
			writeItemID(itemTypeID, binaryOutputStream);
			binaryOutputStream.putByte(resultValue);				
		}
		
		public SingleItemType getSingleItemType() {
			return SingleItemType.BOOLEAN;
		}
	}
	
	public AbstractTHBSingleItemEncoder get(int itemTypeID) {
		return thbSingleItemEncoderList[itemTypeID];
	}
}
