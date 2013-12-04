import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;


public class RSAKeyPairFIleCreatorMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		KeyPairGenerator rsaKeyPairGenerator = null;		
		try {
			rsaKeyPairGenerator = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}	
		
		int keySize = 1024;
		rsaKeyPairGenerator.initialize(keySize);
		KeyPair keyPair = rsaKeyPairGenerator.generateKeyPair();
		RSAPublicKey rsaPublicKey =  (RSAPublicKey)keyPair.getPublic();
		RSAPrivateKey rsaPrivateKey =  (RSAPrivateKey)keyPair.getPrivate();
			
		System.out.printf("rsaPublicKey[%s]\n", rsaPublicKey.toString());
		System.out.printf("rsaPrivateKey[%s]\n", rsaPrivateKey.toString());
		
		System.out.printf("******************* RSA staret ********************\n");
		String rsaAlgorithm = rsaPublicKey.getAlgorithm();
		System.out.printf("rsaAlgorithm[%s], keysize[%d]\n", rsaAlgorithm, keySize);
		System.out.printf("******************* RSA end ********************\n");
		
		
		System.out.printf("******************* Pulbic Key staret ********************\n");
		String rsaPublicKeyFormat = rsaPublicKey.getFormat();
		BigInteger rsaPublicKeyModulus = rsaPublicKey.getModulus();
		BigInteger rsaPublicKeyExponent = rsaPublicKey.getPublicExponent();		
		System.out.printf("rsaPublicKeyFormat[%s], rsaPublicKeyExponent(hex)[%s], rsaPublicKeyModulus(hex)[%s]\n", rsaPublicKeyFormat, rsaPublicKeyExponent.toString(16), rsaPublicKeyModulus.toString(16));
		System.out.printf("******************* Pulbic Key end ********************\n");
		
		
		System.out.printf("******************* Private Key staret ********************\n");		
		String rsaPrivateKeyFormat = rsaPrivateKey.getFormat();
		BigInteger rsaPrivateKeyModulus = rsaPrivateKey.getModulus();
		BigInteger rsaPrivateKeyExponent = rsaPrivateKey.getPrivateExponent();		
		System.out.printf("rsaPrivateKeyFormat[%s], rsaPrivateKeyExponent(hex)[%s], rsaPrivateKeyModulus(hex)[%s]\n", rsaPrivateKeyFormat, rsaPrivateKeyExponent.toString(16), rsaPrivateKeyModulus.toString(16));
		System.out.printf("******************* Private Key end ********************\n");
		
		
		FileOutputStream publicKeyFOS = null;		
		try {
			publicKeyFOS = new FileOutputStream("sinnori.publickey");
			
			publicKeyFOS.write(rsaPublicKey.getEncoded());
			publicKeyFOS.flush();			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		} finally {
			try {
				if (publicKeyFOS != null) publicKeyFOS.close();
			} catch(Exception e1) {
			}
		}
		
		FileOutputStream privateKeyFOS = null;		
		try {
			privateKeyFOS = new FileOutputStream("sinnori.privatekey");
			
			privateKeyFOS.write(rsaPrivateKey.getEncoded());
			privateKeyFOS.flush();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		} finally {
			try {
				if (privateKeyFOS != null) privateKeyFOS.close();
			} catch(Exception e1) {
			}
		}
	}

}
