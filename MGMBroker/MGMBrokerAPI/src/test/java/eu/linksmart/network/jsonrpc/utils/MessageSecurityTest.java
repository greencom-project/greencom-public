package eu.linksmart.network.jsonrpc.utils;

import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;
import org.junit.Test;

import eu.linksmart.network.Message;
import eu.linksmart.network.VirtualAddress;
import eu.linksmart.security.communication.SecurityProtocol;

public class MessageSecurityTest {

	private static final Logger LOG = Logger.getLogger(MessageSecurityTest.class);
	
	private static final String DEFAULT_ENCODING ="UTF-8";

	@Test
	public void securityProtocolCheck() throws Exception{//NOSONAR squid:S00112 - JPU: Generic  exception is o.k. for test method
		String text = "The quick brown fox jumps over the lazy dog, The quick brown fox jumps over the lazy dog, The quick brown fox jumps over the lazy dog, ";
		VirtualAddress sender = new VirtualAddress("201398021808213");
		VirtualAddress receiver = new VirtualAddress("261398021808213");

		SecurityProtocol protocol = new MGMBrokerSecurityProtocol(sender, receiver,"password");

		Message m = new Message("test", sender, receiver, text.getBytes(DEFAULT_ENCODING));
		// 
		try {
			m = protocol.protectMessage(m);
			m = protocol.unprotectMessage(m);
			LOG.debug("Message to protect:	'" + text + "'");
			LOG.debug("Unprotected message:	'" + new String(m.getData(),DEFAULT_ENCODING) + "'");
		} catch (Exception e) {
			LOG.error("An error occurred in processsing message: " + e);
		}

	}

	@Test
	public void encryptionTest() throws Exception{//NOSONAR squid:S00112 - JPU: Generic  exception is o.k. for test method 
		String key = "password";

		String text = "The quick brown fox jumps over the lazy dogg";
		byte[] salt = new byte[] { -94, 25, 62, -45, -74, 96, 31, 47, 3, 47, -23, 95, 113, 9, 118, -105, -9, 57, 122,
				-90, 123, 120, 120, -75, -22, 37, 62, -19, 13, -71, -110, 6 };

		LOG.debug("Salt: [" + bytesToHex(salt) + "] ");

		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(new SecretKeySpec(key.getBytes(DEFAULT_ENCODING), "HmacSHA256"));
		byte[] macResult = mac.doFinal(text.getBytes(DEFAULT_ENCODING));

		LOG.debug("HMAC Result:[" + bytesToHex(macResult) + "] size:" + macResult.length * 8);

		SecretKey secret = getSecretKey(key, salt);

		Cipher encCipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
		encCipher.init(Cipher.ENCRYPT_MODE, secret);

		byte[] encResult = encCipher.doFinal(text.getBytes(DEFAULT_ENCODING));
		byte[] iv = encCipher.getParameters().getParameterSpec(IvParameterSpec.class).getIV();

		LOG.debug("ENC Result:[" + bytesToHex(encResult) + "] size:" + encResult.length * 8);
		LOG.debug("ENC IV:[" + bytesToHex(iv) + "] size:" + iv.length * 8);
		LOG.debug("AES KEY: [" + bytesToHex(secret.getEncoded()) + "] size:" + secret.getEncoded().length * 8);

		byte[] message = new byte[macResult.length + iv.length + encResult.length];

		LOG.debug(message.length);

		System.arraycopy(macResult, 0, message, 0, macResult.length);
		System.arraycopy(iv, 0, message, macResult.length, iv.length);
		System.arraycopy(encResult, 0, message, macResult.length + iv.length, encResult.length);

		byte[] hmacVerify = new byte[32];
		byte[] ivVerify = new byte[16];
		byte[] encVerify = new byte[message.length - 48];

		for (int i = 0; i < 32; i++) {
			hmacVerify[i] = message[i];
		}

		for (int i = 32; i < 48; i++) {
			ivVerify[i - 32] = message[i];
		}

		for (int j = 48; j < message.length; j++) {
			encVerify[j - 48] = message[j];
		}

		Cipher decCipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
		SecretKey verifySecret = getSecretKey(key, salt);
		decCipher.init(Cipher.DECRYPT_MODE, verifySecret, new IvParameterSpec(ivVerify));

		byte[] decResult = decCipher.doFinal(encVerify);

		LOG.debug("Decoded message: " + new String(decResult,DEFAULT_ENCODING));

		LOG.debug("Message verified? " + Arrays.equals(mac.doFinal(decResult), hmacVerify));

		byte[] testBytes = ByteBuffer.allocate(4).putInt(127).array();

		LOG.debug(ByteBuffer.wrap(testBytes).getInt());
	}

	private static SecretKey getSecretKey(String key, byte[] salt) throws Exception {//NOSONAR squid:S00112 - JPU: Generic  exception is o.k. for test method
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		KeySpec spec = new PBEKeySpec(key.toCharArray(), salt, 1000, 256);
		SecretKey tmp = factory.generateSecret(spec);
		SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
		return secret;
	}

	private static String bytesToHex(byte[] bytes) {
		final char[] hexArray = "0123456789ABCDEF".toCharArray();
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

}
