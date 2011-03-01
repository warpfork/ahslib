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
	
	// boy, i should would like to be able to make some optimizations by caching here, but it turns out you can't set a new IV in BC without also re-doing the key initialization!  Ain't that just a dandy.
	//    AH!  i -CAN- do it!  override the init function in AESFastEngine to do the check.  it has to do it walking the whole array instead of doing pointer-eq on the array since i can't save it from the copyfuck in KeyParameter without a fairly high degree of evil, but that's okay.  and also i could do an extend hack on KP.  or save KP at this level.  yeah, the AESEngine should def just do a peq on the byte array.   
//	private byte[]				$lastKey;
//	private byte[]				$lastMacKey;
	
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
		
	}
	
	
	public CiphertextSymmetric encrypt(Ks $key, Kc $iv, Ks $mackey, byte[] $cleartext) {
		// init the bitch
		$cipher.init(true, new ParametersWithIV(new KeyParameter($key.getBytes()), $iv.getBytes()));
		$hmac.init(new KeyParameter($mackey.getBytes()));
		
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
	
	// engine class must remember the last key used
	// can we make one single cleartext type with all appropiate methods?
	//	no, because authentication is a complex question and has key type issues up the wazoo.
	//	for just sym enc and sym mac it would probs be doable though.
	// delayed computation of mac and such until asked for it: doesn't jive well with concept of a cleartext class.  jives better with just asking the engine (?).
	
	
	
}
