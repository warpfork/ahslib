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

package us.exultant.ahs.scratch.crypto.jce.sym;

import us.exultant.ahs.core.*;
import us.exultant.ahs.scratch.crypto.jce.*;
import java.security.*;

/**
 * Implementors are not expected to provide internal synchronization; multiple threads
 * should never access a single KeySystem object without applying some sort of external
 * synchronization first.
 * 
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 * 
 * @param <$KEYSYM>
 */
public interface KeySystemSym<$KEYSYM extends Key> extends KeySystem {
	public $KEYSYM generateKey();
	
	/**
	 * Encrypt using default mode and padding scheme.
	 * 
	 * @param $plaintext
	 * @param $key
	 * @return the ciphertext
	 * @throws InvalidKeyException 
	 */
	public byte[] encrypt(byte[] $plaintext, $KEYSYM $key) throws InvalidKeyException;
	
	/**
	 * Decrypt using default mode and padding scheme. May return null if the
	 * ciphertext is invalid and decryption fails.
	 * 
	 * @param $ciphertext
	 * @param $key
	 * @return the plaintext (or null if decryption fails).
	 * @throws GeneralSecurityException 
	 */
	public byte[] decrypt(byte[] $ciphertext, $KEYSYM $key) throws GeneralSecurityException;
	
	/**
	 * Sign using the symmetric key.
	 * 
	 * @param $text
	 *                the message body to generate a signature over
	 * @param $key
	 *                the MAC key
	 * @return the signature bytes
	 * @throws InvalidKeyException 
	 */
	public byte[] mac(byte[] $text, $KEYSYM $key) throws InvalidKeyException;
	
	/**
	 * Verify a signiture.
	 * 
	 * @param $text
	 *                the message body to check against a signature
	 * @param $sig
	 *                the signature bytes produced by the signer
	 * @param $key
	 *                the MAC key
	 * @return true if the signature using the MAC key matches message text; false otherwise or if
	 *         errors occur during verification.
	 * @throws InvalidKeyException 
	 */
	public boolean verify(byte[] $text, byte[] $sig, $KEYSYM $key) throws InvalidKeyException;

	public byte[] encode($KEYSYM $k);
	
	public $KEYSYM decode(byte[] $ke) throws TranslationException;
}
