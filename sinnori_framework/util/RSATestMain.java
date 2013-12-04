import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSATestMain {
	private final static int MAXKeyFileSIZE = 4096;

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
	 * @throws NoSuchAlgorithmException
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		File rsaPrivateKeyFile = new File("sinnori.privatekey");

		if (!rsaPrivateKeyFile.exists()) {
			System.out.printf(
					"sinnori.privatekey file not exist. fullpath=[%s]\n",
					rsaPrivateKeyFile.getAbsolutePath());
			System.exit(1);
		}

		if (!rsaPrivateKeyFile.canRead()) {
			System.out.printf(
					"can not read sinnori.privatekey file. fullpath=[%s]\n",
					rsaPrivateKeyFile.getAbsolutePath());
			System.exit(1);
		}

		FileInputStream rsaPrivateKeyFIS = null;
		byte privateKeyBytes[] = null;
		try {
			rsaPrivateKeyFIS = new FileInputStream(rsaPrivateKeyFile);

			int size = (int) rsaPrivateKeyFile.length();
			privateKeyBytes = new byte[size];
			rsaPrivateKeyFIS.read(privateKeyBytes);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(2);
		} finally {
			try {
				if (null != rsaPrivateKeyFIS)
					rsaPrivateKeyFIS.close();
			} catch (Exception e1) {
			}
		}

		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
				privateKeyBytes);
		KeyFactory rsaKeyFactory = null;
		try {
			rsaKeyFactory = KeyFactory.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(3);
		}

		PrivateKey privateKey = null;
		try {
			privateKey = rsaKeyFactory.generatePrivate(privateKeySpec);
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(4);
		}

		// String privateKeyHex = "f6a57f499658558cc9cbf5101534bcad";

		File rsaPublicKeyFile = new File("sinnori.publickey");
		String errorMessage = null;

		FileInputStream rsaPublicKeyFIS = null;
		byte publicKeyBytes[] = null;
		try {
			rsaPublicKeyFIS = new FileInputStream(rsaPublicKeyFile);

			long size = rsaPublicKeyFile.length();
			if (size > MAXKeyFileSIZE || size > Integer.MAX_VALUE) {
				errorMessage = String.format(
						"check rsa public key file size[%d]", size);
				System.out.println(errorMessage);
				return;
			}
			publicKeyBytes = new byte[(int) size];
			rsaPublicKeyFIS.read(publicKeyBytes);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			errorMessage = String
					.format("RSA Public Key FileNotFoundException");
			System.out.println(errorMessage);
			return;
		} catch (IOException e) {
			e.printStackTrace();
			errorMessage = String.format("RSA Public Key IOException");
			System.out.println(errorMessage);
			return;
		} finally {
			try {
				if (null != rsaPublicKeyFIS)
					rsaPublicKeyFIS.close();
			} catch (Exception e1) {
			}
		}

		PublicKey publicKey = null;
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
				publicKeyBytes);

		try {
			publicKey = rsaKeyFactory.generatePublic(publicKeySpec);
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
			errorMessage = String
					.format("RSA Public Key InvalidKeySpecException");
			System.out.println(errorMessage);
			return;
		}

		Cipher rsaCipher = null;

		try {
			rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(5);
		}

		String plainTextHexStr = "cf5553bdbe3a6240a0a89fdd9be4e64c";
		byte plainTextBytes[] = RSATestMain.hexToByteArray(plainTextHexStr);
		byte encryptedBytes[] = null;
		try {
			rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);
			encryptedBytes = rsaCipher.doFinal(plainTextBytes);

			System.out.printf("encryptedHex[%s]",
					RSATestMain.byteArrayAllToHex(encryptedBytes));
			System.out.println("");
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(6);
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(6);
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(6);
		}

		// String encryptedHexStr =
		// "87f5a0c0e7112f343c53201189012c8a6658f26d574bd63aa73526105d227bcfaba193c14342bbe1f009fccc6fb273ffb026151b5f59a9bacf5c0ae5d4284b7457b1701d91418d709020829b9033c02745d4150fc4eb01d39b05c453eb623630c387f9c0af635c6c9e7d8326064a1e6b8b81614d2ccf5355897b30c2736e1b5c";
		// byte encryptedBytes[] = RSATestMain.hexToByteArray(encryptedHexStr);

		try {
			rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] originalBytes = rsaCipher.doFinal(encryptedBytes);

			System.out.printf("원문=[%s], 복문[%s] originalBytes.length=[%s]",
					plainTextHexStr,
					RSATestMain.byteArrayAllToHex(originalBytes),
					originalBytes.length);
			System.out.println("");
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(6);
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(6);
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(6);
		}

	}
}
