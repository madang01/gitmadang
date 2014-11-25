import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoTestMain {

	static final String[] hexTable = { "00", "01", "02", "03", "04", "05",
			"06", "07", "08", "09", "0a", "0b", "0c", "0d", "0e", "0f", "10",
			"11", "12", "13", "14", "15", "16", "17", "18", "19", "1a", "1b",
			"1c", "1d", "1e", "1f", "20", "21", "22", "23", "24", "25", "26",
			"27", "28", "29", "2a", "2b", "2c", "2d", "2e", "2f", "30", "31",
			"32", "33", "34", "35", "36", "37", "38", "39", "3a", "3b", "3c",
			"3d", "3e", "3f", "40", "41", "42", "43", "44", "45", "46", "47",
			"48", "49", "4a", "4b", "4c", "4d", "4e", "4f", "50", "51", "52",
			"53", "54", "55", "56", "57", "58", "59", "5a", "5b", "5c", "5d",
			"5e", "5f", "60", "61", "62", "63", "64", "65", "66", "67", "68",
			"69", "6a", "6b", "6c", "6d", "6e", "6f", "70", "71", "72", "73",
			"74", "75", "76", "77", "78", "79", "7a", "7b", "7c", "7d", "7e",
			"7f", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89",
			"8a", "8b", "8c", "8d", "8e", "8f", "90", "91", "92", "93", "94",
			"95", "96", "97", "98", "99", "9a", "9b", "9c", "9d", "9e", "9f",
			"a0", "a1", "a2", "a3", "a4", "a5", "a6", "a7", "a8", "a9", "aa",
			"ab", "ac", "ad", "ae", "af", "b0", "b1", "b2", "b3", "b4", "b5",
			"b6", "b7", "b8", "b9", "ba", "bb", "bc", "bd", "be", "bf", "c0",
			"c1", "c2", "c3", "c4", "c5", "c6", "c7", "c8", "c9", "ca", "cb",
			"cc", "cd", "ce", "cf", "d0", "d1", "d2", "d3", "d4", "d5", "d6",
			"d7", "d8", "d9", "da", "db", "dc", "dd", "de", "df", "e0", "e1",
			"e2", "e3", "e4", "e5", "e6", "e7", "e8", "e9", "ea", "eb", "ec",
			"ed", "ee", "ef", "f0", "f1", "f2", "f3", "f4", "f5", "f6", "f7",
			"f8", "f9", "fa", "fb", "fc", "fd", "fe", "ff" };

	public static byte[] hexToByteArray(String hexStr)
			throws NumberFormatException {
		if (hexStr == null || hexStr.length() == 0) {
			return null;
		}

		byte[] retBytes = new byte[hexStr.length() / 2];
		for (int i = 0; i < retBytes.length; i++) {
			retBytes[i] = (byte) Integer.parseInt(
					hexStr.substring(2 * i, 2 * i + 2), 16);
		}
		return retBytes;
	}

	public static String byteBufferAllToHex(ByteBuffer buffer) {
		int capacity = buffer.capacity();
		return byteBufferToHex(buffer, 0, capacity);
	}

	public static String byteBufferAvailableToHex(ByteBuffer buffer) {
		int position = buffer.position();
		int limit = buffer.limit();
		return byteBufferToHex(buffer, position, limit);
	}

	private static String byteBufferToHex(ByteBuffer buffer, int offset,
			int length) {
		if (offset < 0) {
			throw new RuntimeException("offset less than zero");
		}

		if (length < 0) {
			throw new RuntimeException("length less than zero");
		}

		int capacity = buffer.capacity();

		if (offset >= capacity) {
			throw new RuntimeException("offset more than capacity");
		}

		int size = offset + length;

		if (size > capacity) {
			throw new RuntimeException("size(offset+length) more than capacity");
		}

		ByteBuffer dupBuffer = buffer.duplicate();
		dupBuffer.clear();

		StringBuffer strbuff = new StringBuffer();

		for (int j = offset; j < size; j++) {
			byte one_byte = dupBuffer.get(j);
			int inx = 0xff & one_byte;
			strbuff.append(hexTable[inx]);
		}
		return strbuff.toString();
	}

	public static String byteArrayAllToHex(byte[] buffer) {
		return byteArrayToHex(buffer, 0, buffer.length);
	}

	public static String byteArrayToHex(byte[] buffer, int offset, int length) {
		if (offset < 0) {
			throw new RuntimeException("offset less than zero");
		}

		if (length < 0) {
			throw new RuntimeException("length less than zero");
		}

		int capacity = buffer.length;

		if (offset > capacity) {
			throw new RuntimeException("offset more than capacity");
		}

		int size = offset + length;

		if (size > capacity) {
			throw new RuntimeException("size(offset+length) more than capacity");
		}

		StringBuffer strbuff = new StringBuffer();
		for (int j = offset; j < size; j++) {
			byte one_byte = buffer[j];
			int inx = 0xff & one_byte;
			strbuff.append(hexTable[inx]);
		}
		return strbuff.toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String algorithm = "AES";
		String ivHex = "b4dcb9e8c5285c9969f250b51a51c793";
		String plainText="한글 테스트";
		String encryptedHex = "83b7840269eaaf5ff6f3addc1f9fc964b648158dd761b10e908c2bec79ae25e5";
		String privateKeyHex = "831b6c43a852ee314a24f2aa1b61c5a5";

		Hashtable<String, String> symmetricKeyTransformationHash = null;
		symmetricKeyTransformationHash = new Hashtable<String, String>();
		symmetricKeyTransformationHash.put("AES", "AES/CBC/PKCS5Padding");
		symmetricKeyTransformationHash.put("DES", "DES/CBC/PKCS5Padding");
		symmetricKeyTransformationHash.put("DESede", "DESede/CBC/PKCS5Padding");

		String transformation = symmetricKeyTransformationHash.get(algorithm);

		byte[] privateKeyBytes = CryptoTestMain.hexToByteArray(privateKeyHex);
		byte[] ivBytes = CryptoTestMain.hexToByteArray(ivHex);
		byte[] encryptedBytes = CryptoTestMain.hexToByteArray(encryptedHex);

		System.out.printf("1.key size[%d]", privateKeyBytes.length);
		System.out.println("");

		Cipher symmetricKeyCipher = null;
		try {
			symmetricKeyCipher = Cipher.getInstance(transformation);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new RuntimeException("NoSuchAlgorithmException");
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			throw new RuntimeException("NoSuchPaddingException");
		}

		SecretKeySpec symmetricKey = new SecretKeySpec(privateKeyBytes,
				algorithm);

		IvParameterSpec iv = new IvParameterSpec(ivBytes);
		try {
			symmetricKeyCipher.init(Cipher.DECRYPT_MODE, symmetricKey, iv);
		} catch (InvalidKeyException e1) {
			e1.printStackTrace();
			throw new RuntimeException("InvalidKeyException");
		} catch (InvalidAlgorithmParameterException e1) {
			e1.printStackTrace();
			throw new RuntimeException("InvalidAlgorithmParameterException");
		}

		System.out.println("Cipher.init with IV");

		byte[] decryptedBytes;
		try {
			decryptedBytes = symmetricKeyCipher.doFinal(encryptedBytes);
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
			throw new RuntimeException("IllegalBlockSizeException");
		} catch (BadPaddingException e) {
			e.printStackTrace();
			throw new RuntimeException("BadPaddingException");
		}

		String decryptedBytesHex = CryptoTestMain
				.byteArrayAllToHex(decryptedBytes);
		System.out.printf("decryptedBytes[%s]", decryptedBytesHex);
		System.out.println("");
		
		try {
			/** FIXME!, javascript crypto-js 라이브러리는 문자셋 UTF-8 이라 추정하기때문에 최종적으로 UTF-8이 맞는지 반듯이 확인이 필요하다. */
			System.out.printf("원문[%s], 복문[%s]", plainText, new String(decryptedBytes, "UTF-8"));
			System.out.println("");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
	}

}
