package ahs.crypto.bc;

import ahs.crypto.*;
import ahs.crypto.bc.mod.*;
import ahs.io.*;
import ahs.io.codec.*;
import ahs.io.codec.eon.*;
import ahs.util.*;

import java.util.*;

import org.bouncycastle.crypto.*;
import org.bouncycastle.crypto.digests.*;
import org.bouncycastle.crypto.engines.*;
import org.bouncycastle.crypto.macs.*;
import org.bouncycastle.crypto.modes.*;
import org.bouncycastle.crypto.paddings.*;
import org.bouncycastle.crypto.params.*;

public class AesCtrPkcs7Sha1 {
	public AesCtrPkcs7Sha1() {
		// build the system
		$cipher = new PaddedBufferedBlockCipher(
			new SICBlockCipher(new AESEngineMod()),	// i've decided to frown upon CBC because of the bug i noticed with IVs in that code.
			new PKCS7Padding()
		);
		$hmac = new HMac(new SHA1Digest());
	}
	
	private final BufferedBlockCipher	$cipher;
	private final HMac			$hmac;
	private byte[]				$lastKey;
	private byte[]				$lastMacKey;
	public static final List<Integer>	VALID_KEY_SIZES = Collections.unmodifiableList(Arr.asList(32,24,16));	// 128,192,256	
	
	
	public List<Integer> getValidKeySizes() {
		return VALID_KEY_SIZES;
		// perhaps some sort of general purpose key-fabrication and/or validation factories should be returned by methods like this in the eventual resolution of a general interface for worker classes like this.
	}
	
	//TODO:AHS:CRYPTO: make a method that figures out what the IV was by the end of making a ciphertext.
	
	/**
	 * This method uses a zero-block as an IV -- do NOT encrypt with the same key
	 * twice when using this function or both ciphertexts will be compromised. (This
	 * is not typically a sensible thing to do with a key, but an example situation in
	 * which this is actually perfectly reasonable usage is when the base key is a
	 * one-time use key using in some larger scheme (typically sent in a message
	 * itself encrypted assymetrically or resulting from an agreement scheme).
	 * 
	 * @param $key
	 *                this key will not be used directly; rather, an encryption key
	 *                and a mac key will be derived from it, both of the same size as
	 *                this key.
	 * @param $cleartext
	 * @return CiphertextSymmetric
	 */
	public CiphertextSymmetric encrypt(Ks $key, byte[] $cleartext) {
		Ks[] $kss = BcUtil.deriveKeys($key, 345, 2);
		return encrypt($kss[0], new Kc(new byte[] { 0 }), $kss[1], $cleartext);
	}
	
	/**
	 * @param $key The key for symmetric encryption.
	 * @param $iv The Initialization Vector to use in encryption.
	 * @param $mackey The key to use to construct MAC for authenticity.
	 * @param $cleartext
	 * @return CiphertextSymmetric
	 */
	public CiphertextSymmetric encrypt(Ks $key, Kc $iv, Ks $mackey, byte[] $cleartext) {
		// init the bitch
		if ($lastKey != $key.getBytes()) {
			$lastKey = $key.getBytes();
			$cipher.init(true, new ParametersWithIV(new KeyParamMod($key.getBytes()), $iv.getBytes()));
			// damnit, BC... i want different exceptions for an invalid IV and an invalid key, or at the very least i'd like it if you threw them from different functions so i could tell them apart by careful calling and multiple try blocks.  but noooooo.
		}
		
		if ($lastMacKey != $mackey.getBytes()) {
			$lastMacKey = $mackey.getBytes();
			$hmac.init(new KeyParamMod($mackey.getBytes()));
		}
		// we did make the assumptions above that if we didn't need to init then reset would already have been done.
		// since any entrance to this function that doesn't result in an init definitely had the last state of those systems being a doFinal which in turn did a reset... yeah, we're good.
		
		// crunch the numbers
		byte[] $ciphertext = null;
		try {
			$ciphertext = BcUtil.invokeCipher($cipher, $cleartext);
		} catch (InvalidCipherTextException $e) {
			throw new MajorBug("This doesn't even make sense for encryption mode.", $e);
		}
		byte[] $mac = new byte[$hmac.getMacSize()];
		$hmac.update($ciphertext, 0, $ciphertext.length);
		$hmac.doFinal($mac, 0);
		
		// victory
		return CiphertextSymmetric.storeEncMac($iv, $ciphertext, $mac);
	}
}
