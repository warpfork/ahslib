package ahs.crypto.jce.asm;

import ahs.crypto.jce.*;
import ahs.io.*;
import ahs.util.*;

import java.security.*;
import java.security.interfaces.*;

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
	public Pair<$KEYPUB,$KEYPRV> generateKeys();
	
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
