package ahs.crypto.jce.asm.rsa;

import ahs.crypto.jce.asm.rsa.*;
import ahs.io.*;
import ahs.util.*;

import java.util.*;

import java.security.*;
import java.security.interfaces.*;
import java.security.spec.*;

import javax.crypto.*;
import javax.crypto.interfaces.*;

/**
 * <p>
 * A full wrapper around a 1024-bit RSA key generation, RSA/ECB/PKCS1Padding cipher, and
 * signing and sig verification (using SHA1). Convenience methods for encoding and
 * decoding keys to either bytes or JSON representations are also available.
 * </p>
 * 
 * <p>
 * This should not be used in a production environment because of the various weaknesses
 * of ECB mode; it is included because the JCE is retarded and many JVMs lack
 * out-of-the-box support for other modes that are actually secure.
 * </p>
 * 
 * @author hash
 */
public class RSA1024 implements KeySystemAsymRsa {
	/**
	 * Default constructor; constructs a new SecureRandom and instances of a cipher and
	 * a signer.
	 */
	public RSA1024() {
		this(new SecureRandom());
	}
	
	/**
	 * @param $r
	 *                Source of randomness to be used for both key generation and
	 *                encryption.
	 */
	public RSA1024(SecureRandom $r) {
		$rnd = $r;
		// select and construct a cipher and signer
		try {
			$cipher = Cipher.getInstance(CIPHER_CONFIG);
		        $signer = Signature.getInstance(SIGNER_CONFIG);
			$kf = KeyFactory.getInstance(ALGORITHM);
		} catch (NoSuchAlgorithmException $e) {
			throw new MajorBug(IMPOSSIBLE, $e);	// should never happen in practice
		} catch (NoSuchPaddingException $e) {
			throw new MajorBug(IMPOSSIBLE, $e);	// should never happen in practice
		}
	}
	
	public static final int		BITS		= 1024;
	public static final String	CIPHER_CONFIG	= "RSA/ECB/PKCS1Padding";
	public static final String	SIGNER_CONFIG	= "SHA1WithRSA";
	private SecureRandom		$rnd;
	private Cipher			$cipher;
	private Signature		$signer;
	private KeyFactory		$kf;

	/** {@inheritDoc} */
	public Pair<KeyRsaPub,KeyRsaPrv> generateKeys() {
		try {
			// we could have just made one key pair generator per whole thinger, but i'm assuming RSA keys are generally used much more often than generated.
			KeyPairGenerator $keyGen = KeyPairGenerator.getInstance("RSA");
			$keyGen.initialize(BITS, $rnd);
			KeyPair $kp = $keyGen.genKeyPair();
			return new Pair<KeyRsaPub,KeyRsaPrv>(new KeyRsaPub((RSAPublicKey)$kp.getPublic()), new KeyRsaPrv((RSAPrivateKey)$kp.getPrivate()));
		} catch (NoSuchAlgorithmException $e) {
			throw new MajorBug(IMPOSSIBLE, $e);	// RSA IS REAL DAMNIT
		}
	}
	
	/** {@inheritDoc} */
	public byte[] encrypt(byte[] $plaintext, KeyRsaPub $key) {
		// initialize the cipher
		try {
			$cipher.init(Cipher.ENCRYPT_MODE, $key, $rnd);
		} catch (InvalidKeyException $e) {
			X.cry($e);	// if we get uninitialized keys, we should cry.
		}
		
		// do it faggot
		try {
			return $cipher.doFinal($plaintext);
		} catch (IllegalBlockSizeException $e) {
			throw new MajorBug(IMPOSSIBLE, $e);	// not possible if using padding... which we are.
		} catch (BadPaddingException $e) {
			throw new MajorBug(IMPOSSIBLE, $e);	// not possible in encryption mode
		}
	}
	
	/** {@inheritDoc} */
	public byte[] decrypt(byte[] $ciphertext, KeyRsaPrv $key) {
		// initialize the cipher
		try {
			$cipher.init(Cipher.DECRYPT_MODE, $key);
		} catch (InvalidKeyException $e) {
			X.cry($e);	// if we get uninitialized keys, we should cry.
		}
		
		// do it faggot
		try {
			return $cipher.doFinal($ciphertext);
		} catch (IllegalBlockSizeException $e) {
			return null;	// decryption just fails; ciphertext is invalid.
		} catch (BadPaddingException $e) {
			return null;	// decryption just fails; ciphertext is invalid.
		}
	}
	
	/** {@inheritDoc} */
	public byte[] sign(byte[] $text, KeyRsaPrv $key) {
		try {
			$signer.initSign($key);
		} catch (InvalidKeyException $e) {
			X.cry($e);	// if we get uninitialized keys, we should cry.
		}
		try {
			$signer.update($text);
		        return $signer.sign();
		} catch (SignatureException $e) {
			throw new MajorBug(IMPOSSIBLE, $e);	// only if something isn't init'd... which it is.
		}
	}
	
	/** {@inheritDoc} */
	public boolean verify(byte[] $text, byte[] $sig, KeyRsaPub $signerKey) {
		try {
			$signer.initVerify($signerKey);
		} catch (InvalidKeyException $e) {
			X.cry($e); // if we get uninitialized keys, we should cry.
		}
		try {
			$signer.update($text);
		        return $signer.verify($sig);
		} catch (SignatureException $e) {
			throw new MajorBug(IMPOSSIBLE, $e);	// only if something isn't init'd... which it is.
		}
	}
	
	/** {@inheritDoc} */
	public byte[] encode(KeyRsaPub $ko) {
		return $ko.getEncoded();
	}
	
	/** {@inheritDoc} */
	public KeyRsaPub decodePublicKey(byte[] $koe) throws TranslationException {
		try {
			return new KeyRsaPub((RSAPublicKey)$kf.generatePublic(new X509EncodedKeySpec($koe)));
		} catch (InvalidKeySpecException $e) {
			throw new TranslationException("Decoding KeyRsaPub failed.", $e);
		} catch (ClassCastException $e) {
			throw new TranslationException("Decoding KeyRsaPub failed.", $e);
		}
	}
	
	/** {@inheritDoc} */
	public byte[] encode(KeyRsaPrv $kx) {
		return $kx.getEncoded();
	}
	
	/** {@inheritDoc} */
	public KeyRsaPrv decodePrivateKey(byte[] $kxe) throws TranslationException {
		try {
			return new KeyRsaPrv((RSAPrivateKey)$kf.generatePrivate(new PKCS8EncodedKeySpec($kxe)));
		} catch (InvalidKeySpecException $e) {
			throw new TranslationException("Decoding KeyRsaPrv failed.", $e);
		} catch (ClassCastException $e) {
			throw new TranslationException("Decoding KeyRsaPrv failed.", $e);
		}
	}
}
