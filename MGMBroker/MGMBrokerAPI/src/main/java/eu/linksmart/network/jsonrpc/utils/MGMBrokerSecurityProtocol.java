package eu.linksmart.network.jsonrpc.utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;

import eu.linksmart.network.Message;
import eu.linksmart.network.VirtualAddress;
import eu.linksmart.security.communication.CryptoException;
import eu.linksmart.security.communication.SecurityProtocol;
import eu.linksmart.security.communication.VerificationFailureException;

/**
 * Security protocol implementation, providing confidentiality, integrity,
 * authenticity and replay attacks protection through a shared secret, using
 * AES256-CBC and HMAC-SHA256 algorithms and counters for replay attacks.
 * 
 * @author Ivan Grimaldi (grimaldi@ismb.it)
 *
 */
public class MGMBrokerSecurityProtocol implements SecurityProtocol {

	private final static Logger LOG = Logger.getLogger(MGMBrokerSecurityProtocol.class);

	// HMAC_TYPE "HmacSHA256"

	// salt
	byte[] salt = new byte[] { -94, 25, 62, -45, -74, 96, 31, 47, 3, 47, -23, 95, 113, 9, 118, -105, -9, 57, 122, -90,
			123, 120, 120, -75, -22, 37, 62, -19, 13, -71, -110, 6 };

	/**
	 * Increasing counter which protects order integrity of messages sent.
	 */
	private int localCounter = 0;
	/**
	 * Increasing counter which protects order integrity of messages received.
	 */
	private int remoteCounter = 0;

	private boolean isInitialized = false;
	
	private String password;

	private VirtualAddress sender;

	private VirtualAddress receiver;

	public MGMBrokerSecurityProtocol(VirtualAddress sender, VirtualAddress receiver, String password) {
		this.localCounter = 0;
		this.remoteCounter = 0;
		this.isInitialized = true;
		this.sender = sender;
		this.receiver = receiver;
		this.password = password;
	}

	public VirtualAddress getSender() {
		return sender;
	}

	public VirtualAddress getReceiver() {
		return receiver;
	}

	@Override
	public boolean canBroadcast() {
		return false;
	}

	@Override
	public boolean isInitialized() {
		return isInitialized;
	}

	@Override
	public Message processMessage(Message message) throws CryptoException, VerificationFailureException, IOException {
		// No handshake required, return null
		return null;
	}

	@Override
	public Message protectMessage(Message message) throws Exception {

		// attach counter to data
		byte[] dataAndCounter = new byte[message.getData().length + 4];
		byte[] counterBytes = ByteBuffer.allocate(4).putInt(localCounter).array();

		// reset the counter if the limit was reached
		localCounter++;
		if (localCounter == Integer.MAX_VALUE) {
			localCounter = 0;
		}

		System.arraycopy(message.getData(), 0, dataAndCounter, 0, message.getData().length);
		System.arraycopy(counterBytes, 0, dataAndCounter, message.getData().length, counterBytes.length);

		// calculate HMAC
		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(new SecretKeySpec(password.getBytes(), "HmacSHA256"));// NOSONAR
																							// findbugs:DM_DEFAULT_ENCODING
		byte[] macResult = mac.doFinal(dataAndCounter);

		// encrypt data
		SecretKey secret = getSecretKey(password, salt);
		Cipher encCipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
		encCipher.init(Cipher.ENCRYPT_MODE, secret);
		byte[] encResult = encCipher.doFinal(dataAndCounter);
		byte[] iv = encCipher.getParameters().getParameterSpec(IvParameterSpec.class).getIV();
		byte[] encryptedMessage = new byte[macResult.length + iv.length + encResult.length];

		// putting things together: HMAC | IV | AES-256(MESSAGE | COUNTER)
		System.arraycopy(macResult, 0, encryptedMessage, 0, macResult.length);
		System.arraycopy(iv, 0, encryptedMessage, macResult.length, iv.length);
		System.arraycopy(encResult, 0, encryptedMessage, macResult.length + iv.length, encResult.length);

		// modify the message
		message.setData(encryptedMessage);
		LOG.debug("Length of message+hmac+iv to be sent:" + encryptedMessage.length);
		return message;
	}

	@Override
	public Message unprotectMessage(Message message) throws Exception {

		LOG.debug("Length of message+hmac+iv received:" + message.getData().length);
		// Split message parts
		byte[] data = message.getData();

		byte[] hmacVerify = new byte[32];
		byte[] ivVerify = new byte[16];
		byte[] encVerify = new byte[data.length - 48];

		for (int i = 0; i < 32; i++) {
			hmacVerify[i] = data[i];
		}

		for (int i = 32; i < 48; i++) {
			ivVerify[i - 32] = data[i];
		}

		for (int i = 48; i < data.length; i++) {
			encVerify[i - 48] = data[i];
		}
		// decrypt content
		Cipher decCipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
		SecretKey verifySecret = getSecretKey(password, salt);
		decCipher.init(Cipher.DECRYPT_MODE, verifySecret, new IvParameterSpec(ivVerify));
		byte[] decResult = null;
		try {
			decResult = decCipher.doFinal(encVerify);
		} catch (Exception e) {
			LOG.error("Error decrypting message", e);

		}

		// calculate HMAC of decrypted message
		Mac mac = Mac.getInstance("HmacSHA256");
		mac.init(new SecretKeySpec(password.getBytes(), "HmacSHA256"));// NOSONAR
																							// findbugs:DM_DEFAULT_ENCODING
		byte[] macResult = mac.doFinal(decResult);

		if (!Arrays.equals(macResult, hmacVerify)) {
			throw new VerificationFailureException("Unable to verify message");
		}

		// extract the data and the counter
		byte[] clearData = new byte[decResult.length - 4];
		byte[] clearCounter = new byte[4];

		System.arraycopy(decResult, 0, clearData, 0, clearData.length);
		System.arraycopy(decResult, clearData.length, clearCounter, 0, 4);

		int receivedCounter = ByteBuffer.wrap(clearCounter).getInt();

		// verify the counter, re-set to 0 if the last value was greatest
		// possible integer
		if (remoteCounter == (Integer.MAX_VALUE - 1)) {
			remoteCounter = 0;
		}

		if (receivedCounter < remoteCounter) {
			throw new VerificationFailureException("Message is an older message and is not accepted");
		}

		remoteCounter = receivedCounter;

		message.setData(clearData);
		return message;
	}

	@Override
	public Message startProtocol() throws CryptoException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Message protectBroadcastMessage(Message message) {
		throw new UnsupportedOperationException("Broadcasting not supported by security protocol!");
	}

	@Override
	public Message unprotectBroadcastMessage(Message message) {
		throw new UnsupportedOperationException("Broadcasting not supported by security protocol!");
	}

	private static SecretKey getSecretKey(String key, byte[] salt) throws NoSuchAlgorithmException,
			InvalidKeySpecException {
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		KeySpec spec = new PBEKeySpec(key.toCharArray(), salt, 1000, 256);
		SecretKey tmp = factory.generateSecret(spec);
		SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
		return secret;
	}
}
