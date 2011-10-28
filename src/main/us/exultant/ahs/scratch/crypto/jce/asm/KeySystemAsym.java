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

package us.exultant.ahs.scratch.crypto.jce.asm;

import us.exultant.ahs.scratch.crypto.jce.*;
import us.exultant.ahs.core.*;
import us.exultant.ahs.util.*;

/**
 * Implementors are not expected to provide internal synchronization; multiple threads
 * should never access a single KeySystem object without applying some sort of external
 * synchronization first.
 * 
 * @author hash
 * 
 * @param <$KEYPUB>
 * @param <$KEYPRV>
 */
public interface KeySystemAsym<$KEYPUB extends KeyAsymPub, $KEYPRV extends KeyAsymPrv> extends KeySystem {
	public Tup2<$KEYPUB,$KEYPRV> generateKeys();
	
	/**
	 * Encrypt using default mode and padding scheme.
	 * 
	 * @param $plaintext
	 * @param $ko
	 * @return the ciphertext
	 */
	public byte[] encrypt(byte[] $plaintext, $KEYPUB $ko);
	
	/**
	 * Decrypt using default mode and padding scheme. May return null if the
	 * ciphertext is invalid and decryption fails.
	 * 
	 * @param $ciphertext
	 * @param $kx
	 * @return the plaintext (or null if decryption fails).
	 */
	public byte[] decrypt(byte[] $ciphertext, $KEYPRV $kx);
	
	/**
	 * Sign using the private key.
	 * 
	 * @param $text
	 *                the message body to generate a signature over
	 * @param $myKey
	 *                the private key of the signer
	 * @return the signature bytes
	 */
	public byte[] sign(byte[] $text, $KEYPRV $myKey);
	
	/**
	 * Verify a signiture.
	 * 
	 * @param $text
	 *                the message body to check against a signature
	 * @param $sig
	 *                the signature bytes produced by the signer
	 * @param $signerKey
	 *                the public key of the signer
	 * @return true if the signer signed the given message text; false otherwise or if
	 *         errors occur during verification.
	 */
	public boolean verify(byte[] $text, byte[] $sig, $KEYPUB $signerKey);
	
	
	
	public byte[] encode($KEYPUB $ko);
	
	public $KEYPUB decodePublicKey(byte[] $koe) throws TranslationException;
	
	public byte[] encode($KEYPRV $kx);
	
	public $KEYPRV decodePrivateKey(byte[] $kxe) throws TranslationException;
}
