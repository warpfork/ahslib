package ahs.crypto.bc;

import ahs.crypto.*;
import ahs.io.*;
import ahs.io.codec.*;
import ahs.io.codec.eon.*;
import ahs.util.*;

import org.bouncycastle.crypto.*;
import org.bouncycastle.crypto.digests.*;
import org.bouncycastle.crypto.engines.*;
import org.bouncycastle.crypto.macs.*;
import org.bouncycastle.crypto.modes.*;
import org.bouncycastle.crypto.paddings.*;
import org.bouncycastle.crypto.params.*;

public class AesCtrPkcs7Sha1 {
	// this naming scheme... make sure it's looking forward to discrimination between enc-then-mac and mac-then-enc.  (i don't expect i'll ever do the "and" pattern because the stupid in that is just obvious.)
	// SymEncMac_AesSha1 isn't really sufficient discription.  it's more like SymEncMac_AesCtrPkcs7Sha1.  but then the first half is a little redundant too, i suppose.
	
	public AesCtrPkcs7Sha1() {
		// build the system
		$cipher = new PaddedBufferedBlockCipher(
			new SICBlockCipher(new AESFastEngine()),	// i've decided to frown upon CBC because of the bug i noticed with IVs in that code.
			new PKCS7Padding()
		);
		$hmac = new HMac(new SHA1Digest());
	}
	
	private final BufferedBlockCipher	$cipher;
	private final HMac			$hmac;
	private KeyParameter			$lastKey;
	private KeyParameter			$lastMacKey;
	
	/**
	 * This method uses a zero-block as an IV -- do NOT encrypt with the same key
	 * twice when using this function or both ciphertexts will be compromised.
	 * 
	 * @param $key
	 *                this key will not be used directly; rather, an encryption and a
	 *                mac key will be derived from it.  Both will be 256 bits.
	 * @param $cleartext
	 * @return CiphertextSymmetric
	 */
	public CiphertextSymmetric encrypt(Ks $key, byte[] $cleartext) {
		return null;	//TODO:AHS:CRYPTO: key derivation.  probably ought to be doable somewhere fairly central.
		// hmm.  if i keep this peq-for-effic-cipher-reuse requirement up then this derivation thing won't like it much.  but then i suppose if you're accessing the system through this interface you probably don't expect to be reusing these keys anyway.
	}
	
	public CiphertextSymmetric encrypt(Ks $key, Kc $iv, Ks $mackey, byte[] $cleartext) {
		// init the bitch
		if ($key.getBytes() != $lastKey.getKey()) {
			$lastKey = new KeyParameter($key.getBytes());	// this still copies the data into a new array goddamnit.  need to extend KP.
			$cipher.init(true, new ParametersWithIV($lastKey, $iv.getBytes()));
		}
		
		if ($mackey.getBytes() != $lastMacKey.getKey()) {
			$lastMacKey = new KeyParameter($mackey.getBytes());	// this still copies the data into a new array goddamnit.  need to extend KP.
			$hmac.init($lastMacKey);
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
