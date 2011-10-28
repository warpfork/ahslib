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

import us.exultant.ahs.crypto.*;
import us.exultant.ahs.util.*;
import org.bouncycastle.crypto.*;
import org.bouncycastle.crypto.digests.*;

public class BcUtil {
	/**
	 * @param $cipher must already be initialized.  May be in either encrypt or decrypt mode.
	 * @param $bletch bytes to work on.  will not be modified.  (can be either the ciphertext or the cleartext depending on what mode the cipher is in.)
	 * @return the processed bytes (in new memory).
	 * @throws DataLengthException if the input is not block size aligned and should be.
	 * @throws IllegalStateException if the underlying cipher is not initialized.
	 * @throws InvalidCipherTextException if padding is expected and not found.
	 */
	static byte[] invokeCipher(BufferedBlockCipher $cipher, byte[] $bletch) throws DataLengthException, IllegalStateException, InvalidCipherTextException {
	        return invokeCipher($cipher, $bletch.length, $bletch);
	}

	/**
	 * @param $cipher must already be initialized.  May be in either encrypt or decrypt mode.
	 * @param $bletchley an array of byte arrays to work on; they act as if cat'd together as pushed into the cipher.  will not be modified.  (can be either the ciphertext or the cleartext depending on what mode the cipher is in.)
	 * @return the processed bytes (in new memory).
	 * @throws DataLengthException if the input is not block size aligned and should be.
	 * @throws IllegalStateException if the underlying cipher is not initialized.
	 * @throws InvalidCipherTextException if padding is expected and not found.
	 */
	static byte[] invokeCipher(BufferedBlockCipher $cipher, byte[]... $bletchley) throws DataLengthException, IllegalStateException, InvalidCipherTextException {
		// this code is and invokeCipher with a single byte array instead of a bunch of them are written separately to avoid wasting time cat'ing all of $bletchley together, since loading it into the cipher object involves an array copy of its own.
		int $size = 0;
		for (byte[] $bletch : $bletchley)
			$size += $bletch.length;
		return invokeCipher($cipher, $size, $bletchley);
	}
	
	private static byte[] invokeCipher(BufferedBlockCipher $cipher, int $size, byte[]... $bletchley) throws DataLengthException, IllegalStateException, InvalidCipherTextException {
		$size = $cipher.getOutputSize($size);
		byte[] $park = new byte[$size];
		int $olen = 0;
		for (byte[] $bletch : $bletchley)
			$olen += $cipher.processBytes($bletch, 0, $bletch.length, $park, $olen);	// we're quite confident there's no DataLengthException from here, but there is a possibility on the next line.
		$olen += $cipher.doFinal($park, $olen);							// there's no super easy way to tell in this context whether or not input must be block aligned.
		
		if ($olen < $size)	// $cipher.getOutputSize(*) lied to us!  now we have to make a new smaller array so we aren't returning evil nulls :(
			Arr.copyFromBeginning($park, $olen);
		
		return $park;
	}
	
	/**
	 * Derives symmetric keys by hashing a given key along with a predictable nonce to
	 * produce more keys of the same length. The derived keys are random unless the
	 * base key and the nonce are known.
	 * 
	 * @param $baseKey
	 *                a symmetric key to derive more keys from.
	 * @param $baseModified
	 *                will be converted to bytes and prepended to the base key for
	 *                hashing (this will be incremented before each derivation in the
	 *                case of $keyCount > 1).
	 * @param $keyCount
	 *                how many new keys to derive.
	 * @return an array of size $keyCount containing new symmetric keys.
	 */
	static Ks[] deriveKeys(Ks $baseKey, int $baseModified, int $keyCount) {
		Ks[] $v = new Ks[$keyCount];
		Digest $dig = new SHA1Digest();
		final int $rounds = $baseKey.getBytes().length / $dig.getDigestSize() +1;
		byte[] $fwee = new byte[$rounds * $dig.getDigestSize()];
		for (int $i = 0; $i < $keyCount; $i++) {
			for (int $round = 0; $round < $rounds; $round++) {
				$dig.update(Primitives.byteArrayFromInt($baseModified+$i), 0, 4);	// make each key different
				$dig.update(Primitives.byteArrayFromInt($round), 0, 4);			// make each chunk of a key different if it takes more than one digest to get enough material
				$dig.update($baseKey.getBytes(), 0, $baseKey.getBytes().length);
				$dig.doFinal($fwee, $rounds * $dig.getDigestSize());
			}
			$v[$i] = new Ks.Basic(Arr.copyFromBeginning($fwee, $baseKey.getBytes().length));
		}
		return $v;
	}
}
