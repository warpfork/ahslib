/*
 * Copyright 2010, 2011 Eric Myhre <http://exultant.us>
 * 
 * This file is part of AHSlib.
 *
 * AHSlib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License, or
 * (at the original copyright holder's option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package us.exultant.ahs.crypto.bc;

import us.exultant.ahs.util.*;
import us.exultant.ahs.crypto.*;
import us.exultant.ahs.crypto.bc.mod.*;
import java.util.*;
import org.bouncycastle.crypto.*;
import org.bouncycastle.crypto.digests.*;
import org.bouncycastle.crypto.macs.*;
import org.bouncycastle.crypto.modes.*;
import org.bouncycastle.crypto.paddings.*;
import org.bouncycastle.crypto.params.*;

/**
 * <p>
 * Implements an Encrypt-Then-Mac symmetric cryptosystem composed of the AES cipher, CTR
 * block mode, and PKCS7 padding, with an HMAC composed from SHA-1. Encryption and
 * decryption modes are each provided by their own nested subclass ({@link Encryptor} and
 * {@link Decryptor}, respectively).
 * </p>
 * 
 * <p>
 * Once initialized, the encryptor and decryptor objects will recall keys they last used
 * and be able to skip some steps of their key schedule initialization if repeated
 * operations are requested on the same keys, which can result in significant performance
 * savings in some applications.
 * </p>
 * 
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 * 
 */
// so really, the reason i did separate subclasses for enc and dec is so that i wouldn't have to deal with the issue of a cipher initialized to the same key but a different mode.  i'm okay with throwing a few bytes of memory at that.
public abstract class AesCtrPkcs7Sha1 {
	public AesCtrPkcs7Sha1() {
		// build the system
		$cipher = new PaddedBufferedBlockCipher(
			new SICBlockCipher(new AESEngineMod()),	// i've decided to frown upon CBC because of the bug i noticed with IVs in that code.
			new PKCS7Padding()
		);
		$hmac = new HMac(new SHA1Digest());
	}
	
	protected final BufferedBlockCipher	$cipher;
	protected final HMac			$hmac;
	protected byte[]			$lastKey;
	protected byte[]			$lastMacKey;
	public static final List<Integer>	VALID_KEY_SIZES = Collections.unmodifiableList(Arr.asList(32,24,16));	// 128,192,256	
	
	
	public List<Integer> getValidKeySizes() {
		return VALID_KEY_SIZES;
		// perhaps some sort of general purpose key-fabrication and/or validation factories should be returned by methods like this in the eventual resolution of a general interface for worker classes like this.
	}
	
	//TODO:AHS:CRYPTO: make a method that figures out what the IV was by the end of making a ciphertext.  we also need this in a more general sense: we need to be able to tell that for previously encrypted CiphertextSymmetric, since there are situations where we intend to "resume" encryption under the same key later.
	
	/**
	 * Implements the {@link AesCtrPkcs7Sha1} system in encryption mode.
	 */
	public static final class Encryptor extends AesCtrPkcs7Sha1 {
		public Encryptor() { super(); }
		
		/**
		 * This method uses a zero-block as an IV -- do NOT encrypt with the same
		 * key twice when using this function or both ciphertexts will be
		 * compromised. (This is not typically a sensible thing to do with a key,
		 * but an example situation in which this is actually perfectly reasonable
		 * usage is when the base key is a one-time use key using in some larger
		 * scheme (typically sent in a message itself encrypted assymetrically or
		 * resulting from an agreement scheme).
		 * 
		 * @param $key
		 *                this key will not be used directly; rather, an
		 *                encryption key and a mac key will be derived from it,
		 *                both of the same size as this key.
		 * @param $cleartext
		 *                nuff said.
		 * @return CiphertextSymmetric
		 */
		public CiphertextSymmetric encrypt(Ks $key, byte[] $cleartext) {
			Ks[] $kss = deriveKeys($key);
			return encrypt(
					$kss[0],
					new Kc(new byte[] { 0 }),
					$kss[1],
					$cleartext
			);
		}
		
		/**
		 * @param $key
		 *                The key for symmetric encryption.
		 * @param $iv
		 *                The Initialization Vector to use in encryption.
		 * @param $mackey
		 *                The key to use to construct MAC for authenticity.
		 * @param $cleartext
		 *                nuff said.
		 * @return CiphertextSymmetric
		 */
		public CiphertextSymmetric encrypt(Ks $key, Kc $iv, Ks $mackey, byte[] $cleartext) {
			warmup($key, $iv, $mackey, true);
			
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
	
	/**
	 * Implements the {@link AesCtrPkcs7Sha1} system in decryption mode.
	 */
	public static final class Decryptor extends AesCtrPkcs7Sha1 {
		public Decryptor() { super(); }
		
		/**
		 * This method uses a zero-block as an IV. It is the inverse of
		 * {@link Encryptor#encrypt(Ks, byte[])}.
		 * 
		 * @param $key
		 *                this key will not be used directly; rather, an
		 *                encryption key and a mac key will be derived from it,
		 *                both of the same size as this key.
		 * @param $ciphertext
		 *                nuff said.
		 * @return the cleartext byte array.
		 * @throws InvalidCipherTextException
		 *                 if the decryption failed for any reason: invalid MAC,
		 *                 invalid padding, etc.
		 */
		public byte[] encrypt(Ks $key, CiphertextSymmetric $ciphertext) throws InvalidCipherTextException {
			Ks[] $kss = deriveKeys($key);
			return decrypt(
					$kss[0],
					new Kc(new byte[] { 0 }),
					$kss[1],
					$ciphertext
			);
		}
		
		/**
		 * @param $key
		 *                The key for symmetric encryption.
		 * @param $iv
		 *                The Initialization Vector to use in encryption.
		 * @param $mackey
		 *                The key to use to construct MAC for authenticity.
		 * @param $ciphertext
		 *                nuff said.
		 * @return the cleartext byte array.
		 * @throws InvalidCipherTextException
		 *                 if the decryption failed for any reason: invalid MAC,
		 *                 invalid padding, etc.
		 */
		public byte[] decrypt(Ks $key, Kc $iv, Ks $mackey, CiphertextSymmetric $ciphertext) throws InvalidCipherTextException {
			warmup($key, $iv, $mackey, false);
			
			// crunch the numbers
			byte[] $mac = new byte[$hmac.getMacSize()];
			$hmac.update($ciphertext.getBody(), 0, $ciphertext.getBody().length);
			$hmac.doFinal($mac, 0);
			if (!Arr.equals($mac, $ciphertext.getMac()))
				throw new InvalidCipherTextException("Invalid MAC");	// this is one way to notice bad encryption.
			return BcUtil.invokeCipher($cipher, $ciphertext.getBody());	// this is another; it will also throw InvalidCipherTextException if the padding doesn't match.
		}
	}
	
	/** Get two keys from one. */
	private static Ks[] deriveKeys(Ks $key) {
		return BcUtil.deriveKeys($key, 345, 2);
	}
	
	/**
	 * Check if the keys given match existing keys and skip like mad if so; initialize
	 * the cipher and the hmac as necessary.
	 */
	protected final void warmup(Ks $key, Kc $iv, Ks $mackey, boolean $encryptMode) {
		// init the bitch
		if ($lastKey != $key.getBytes()) {
			$lastKey = $key.getBytes();
			$cipher.init($encryptMode, new ParametersWithIV(new KeyParamMod($key.getBytes()), $iv.getBytes()));
			// damnit, BC... i want different exceptions for an invalid IV and an invalid key, or at the very least i'd like it if you threw them from different functions so i could tell them apart by careful calling and multiple try blocks.  but noooooo.
		}
		
		if ($lastMacKey != $mackey.getBytes()) {
			$lastMacKey = $mackey.getBytes();
			$hmac.init(new KeyParamMod($mackey.getBytes()));
		}
		// we did make the assumptions above that if we didn't need to init then reset would already have been done.
		// since any entrance to this function that doesn't result in an init definitely had the last state of those systems being a doFinal which in turn did a reset... yeah, we're good.
	}
}
