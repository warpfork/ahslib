package us.exultant.ahs.scratch.crypto.jce.sym.aes;

import us.exultant.ahs.scratch.crypto.jce.sym.*;
import us.exultant.ahs.scratch.crypto.jce.sym.aes.*;
import us.exultant.ahs.core.*;
import us.exultant.ahs.util.*;

import java.security.*;
import java.security.interfaces.*;
import java.security.spec.*;

import javax.crypto.*;
import javax.crypto.spec.*;

public class KeySystemSymAes implements KeySystemSym<KeyAes> {
	/**
	 * Default constructor; constructs a new SecureRandom and instances of a cipher and
	 * a macer.
	 */
	public KeySystemSymAes() {
		this(new SecureRandom());
	}
	
	/**
	 * @param $r
	 *                Source of randomness to be used for both key generation and
	 *                encryption.
	 */
	public KeySystemSymAes(SecureRandom $r) {
		$rnd = $r;
		// select and construct a cipher and macer
		try {
			$kg = KeyGenerator.getInstance(ALGORITHM);
			$cipher = Cipher.getInstance(CIPHER_CONFIG);
		        $macer = Mac.getInstance(MAC_CONFIG);
		} catch (NoSuchAlgorithmException $e) {
			throw new MajorBug(IMPOSSIBLE, $e);	// should never happen in practice
		} catch (NoSuchPaddingException $e) {
			throw new MajorBug(IMPOSSIBLE, $e);	// should never happen in practice
		}
	}
	
	public static final String	ALGORITHM	= "AES";
	public static final String	CIPHER_CONFIG	= "AES/ECB/PKCS5Padding";
	public static final String	MAC_CONFIG	= "HmacSHA1";
	private SecureRandom		$rnd;
	private KeyGenerator		$kg;
	private Cipher			$cipher;
	private Mac			$macer;
	
	/** {@inheritDoc} */
	public KeyAes generateKey() {
		return new KeyAes($kg.generateKey());
	}
	
	/** {@inheritDoc} */
	public byte[] encrypt(byte[] $plaintext, KeyAes $key) throws InvalidKeyException {
		$cipher.init(Cipher.ENCRYPT_MODE, $key, $rnd);
		try {
			return $cipher.doFinal($plaintext);
		} catch (IllegalBlockSizeException $e) {
			throw new MajorBug(IMPOSSIBLE, $e);	// not possible if using padding... which we are.
		} catch (BadPaddingException $e) {
			throw new MajorBug(IMPOSSIBLE, $e);	// not possible in encryption mode
		}
	}
	
	/** {@inheritDoc} */
	public byte[] decrypt(byte[] $ciphertext, KeyAes $key) throws GeneralSecurityException {
		$cipher.init(Cipher.DECRYPT_MODE, $key);
		return $cipher.doFinal($ciphertext);
	}
	
	/** {@inheritDoc} */
	public byte[] mac(byte[] $text, KeyAes $key) throws InvalidKeyException {
		$macer.init($key);
		$macer.update($text);
	        return $macer.doFinal();
	}
	
	/** {@inheritDoc} */
	public boolean verify(byte[] $text, byte[] $sig, KeyAes $key) throws InvalidKeyException {
		byte[] $resig = mac($text, $key);
		return Arr.equals($sig, $resig);
	}
	
	/** {@inheritDoc} */
	public byte[] encode(KeyAes $k) {
		return $k.getEncoded();
	}
	
	/** {@inheritDoc} */
	public KeyAes decode(byte[] $ke) throws TranslationException {
		return new KeyAes(new SecretKeySpec($ke, "AES"));
	}
	
}
