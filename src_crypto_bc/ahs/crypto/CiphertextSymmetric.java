package ahs.crypto;

import ahs.io.codec.*;

@Encodable
public class CiphertextSymmetric {
	/**
	 * This constructor is appropriate for storing the results of a symmetric
	 * Encrypt-Then-Mac process.
	 * 
	 * @param $iv
	 *                the initialization vector used in the encryption process.
	 * @param $msg
	 *                the encrypted message.
	 * @param $mac
	 *                the mac produced over the encrypted message.
	 */
	public static CiphertextSymmetric storeEncMac(Kc $iv, byte[] $msg, byte[] $mac) {
		return new CiphertextSymmetric($iv, $msg, $mac);
	}
	
	/**
	 * This constructor is appropriate for storing the results of a symmetric
	 * Encrypt-Then-Mac process. (It is assumed the recipient already knows or will
	 * generate the IV.)
	 * 
	 * @param $msg
	 *                the encrypted message.
	 * @param $mac
	 *                the mac produced over the encrypted message.
	 */
	public static CiphertextSymmetric storeEncMac(byte[] $msg, byte[] $mac) {
		return new CiphertextSymmetric(null, $msg, $mac);
	}

	/**
	 * This constructor is appropriate for storing the results of a symmetric
	 * Mac-Then-Encrypt process.
	 * 
	 * @param $iv
	 *                the initialization vector used in the encryption process.
	 * @param $msg
	 *                the encrypted message, which (if the encryption scheme includes
	 *                a MAC) contains the MAC.
	 */
	public static CiphertextSymmetric storeMacEnc(Kc $iv, byte[] $msg) {
		return new CiphertextSymmetric($iv, $msg, null);
	}
	
	/**
	 * This constructor is appropriate for storing the results of a symmetric
	 * Mac-Then-Encrypt process. (It is assumed the recipient already knows or will
	 * generate the IV.)
	 * 
	 * @param $msg
	 *                the encrypted message, which (if the encryption scheme includes
	 *                a MAC) contains the MAC.
	 */
	public static CiphertextSymmetric storeMacEnc(byte[] $msg) {
		return new CiphertextSymmetric(null, $msg, null);
	}
	
	private CiphertextSymmetric(Kc $iv, byte[] $msg, byte[] $mac) {
		this.$iv = ($iv == null) ? null : $iv.$bytes;
		this.$msg = $msg;
		this.$mac = $mac;
	}
	
	// algorithm pointer?!  would allow simplified asking of questions like usesMac... but doing something like a class here would be scary if clients don't always have matching versions.
	//  ...no it wouldn't be scary, it would get serialized just like any other class name does in a codec and if you can't find it when you load it you're sad but you know it and life goes on.
	@Enc("v") private final byte[] $iv;
	@Enc("a") private final byte[] $mac;
	@Enc("m") private final byte[] $msg;
	
	/**
	 * @return the initialization vector that was used during encryption of the body,
	 *         or null if the recipient of the message is assumed to already know or
	 *         be able to generate the IV.
	 */
	public Kc getIv() {
		return ($iv == null) ? null : new Kc($iv);
	}
	
	/**
	 * @return the encrypted body of the message. Depending on whether the encryption
	 *         scheme used an Encrypt-Then-Mac process or a Mac-Then-Encrypt process,
	 *         the encrypted body (or included a MAC at all), the MAC may or may not
	 *         be wrapped somewhere within this binary blob.
	 */
	public byte[] getBody() {
		return this.$msg;
	}
	
	/**
	 * @return the MAC of the encrypted body of the message, if applicable (i.e. if
	 *         the encryption scheme used an Encrypt-Then-Mac process).
	 */
	public byte[] getMac() {
		return this.$mac;
	}
}
