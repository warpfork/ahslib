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

package us.exultant.ahs.scratch.crypto.jce.est.dh;

import us.exultant.ahs.core.*;
import us.exultant.ahs.util.*;
import us.exultant.ahs.scratch.crypto.jce.sym.aes.*;
import java.math.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.interfaces.*;
import javax.crypto.spec.*;

/**
 * @author hash
 *
 */
public class DH1024 implements KeySystemEstDh<KeyAes> {
	public DH1024() {
		this(new SecureRandom());
	}
	
	public DH1024(SecureRandom $r) {
		$rnd = $r;
		try {
			$keyGen = KeyPairGenerator.getInstance(ALGORITHM);
			$keyGen.initialize(DHPARAM_SkipParamSpec, $rnd);
			$kagre = KeyAgreement.getInstance(ALGORITHM);
			$kf = KeyFactory.getInstance(ALGORITHM);
		} catch (NoSuchAlgorithmException $e) {
			throw new MajorBug(IMPOSSIBLE, $e);
		} catch (InvalidAlgorithmParameterException $e) {
			throw new MajorBug(IMPOSSIBLE, $e);
		}
	}
	
	public static final String	ALGORITHM	= "DH";
	public static final int		BITS		= 1024;
	private SecureRandom		$rnd;
	private KeyPairGenerator	$keyGen;
	private KeyAgreement		$kagre;
	private KeyFactory		$kf;
	
	/** {@inheritDoc} */
	public Tup2<KeyDhPub,KeyDhPrv> generateKeys() {
		KeyPair $kp = $keyGen.genKeyPair();
		return new Tup2<KeyDhPub,KeyDhPrv>(new KeyDhPub((DHPublicKey)$kp.getPublic()), new KeyDhPrv((DHPrivateKey)$kp.getPrivate()));
	}
	
	/** {@inheritDoc} */
	public KeyAes reachSecret(KeyDhPrv $mykx, KeyDhPub $theirko) {
		return new KeyAes(new SecretKeySpec(reachByteSecret($mykx, $theirko), 0, 16, "AES"));	// enforce shittier key size to avoid getting raped by the JCE policy limits
	}
	
	private byte[] reachByteSecret(KeyDhPrv $mykx, KeyDhPub $theirko) {
		try {
			$kagre.init($mykx, $rnd);
			$kagre.doPhase($theirko, true);
		} catch (InvalidKeyException $e) {
			$e.printStackTrace();
			return null;				// crypto fail
		} catch (IllegalStateException $e) {
			throw new MajorBug(IMPOSSIBLE, $e);	// seriously, we initialized it in an earlier part of this same try block.
		}
		return $kagre.generateSecret();
	}
	
	
	
	/** {@inheritDoc} */
	public byte[] encode(KeyDhPub $ko) {
		return $ko.getEncoded();
	}
	
	/** {@inheritDoc} */
	public KeyDhPub decodePublicKey(byte[] $koe) throws TranslationException {
		try {
			return new KeyDhPub((DHPublicKey)$kf.generatePublic(new X509EncodedKeySpec($koe)));
		} catch (InvalidKeySpecException $e) {
			throw new TranslationException("Decoding KeyDhPub failed.", $e);
		} catch (ClassCastException $e) {
			throw new TranslationException("Decoding KeyDhPub failed.", $e);
		}
	}
	
	/** {@inheritDoc} */
	public byte[] encode(KeyDhPrv $kx) {
		return $kx.getEncoded();
	}
	
	/** {@inheritDoc} */
	public KeyDhPrv decodePrivateKey(byte[] $kxe) throws TranslationException {
		try {
			return new KeyDhPrv((DHPrivateKey)$kf.generatePrivate(new PKCS8EncodedKeySpec($kxe)));
		} catch (InvalidKeySpecException $e) {
			throw new TranslationException("Decoding KeyDhPrv failed.", $e);
		} catch (ClassCastException $e) {
			throw new TranslationException("Decoding KeyDhPrv failed.", $e);
		}
	}
	
	
	
	
	
	
	
	// The 1024 bit Diffie-Hellman modulus values used by SKIP
	private static final byte DHPARAM_Skip1024ModulusBytes[] = {
		(byte)0xF4, (byte)0x88, (byte)0xFD, (byte)0x58,
		(byte)0x4E, (byte)0x49, (byte)0xDB, (byte)0xCD,
		(byte)0x20, (byte)0xB4, (byte)0x9D, (byte)0xE4,
		(byte)0x91, (byte)0x07, (byte)0x36, (byte)0x6B,
		(byte)0x33, (byte)0x6C, (byte)0x38, (byte)0x0D,
		(byte)0x45, (byte)0x1D, (byte)0x0F, (byte)0x7C,
		(byte)0x88, (byte)0xB3, (byte)0x1C, (byte)0x7C,
		(byte)0x5B, (byte)0x2D, (byte)0x8E, (byte)0xF6,
		(byte)0xF3, (byte)0xC9, (byte)0x23, (byte)0xC0,
		(byte)0x43, (byte)0xF0, (byte)0xA5, (byte)0x5B,
		(byte)0x18, (byte)0x8D, (byte)0x8E, (byte)0xBB,
		(byte)0x55, (byte)0x8C, (byte)0xB8, (byte)0x5D,
		(byte)0x38, (byte)0xD3, (byte)0x34, (byte)0xFD,
		(byte)0x7C, (byte)0x17, (byte)0x57, (byte)0x43,
		(byte)0xA3, (byte)0x1D, (byte)0x18, (byte)0x6C,
		(byte)0xDE, (byte)0x33, (byte)0x21, (byte)0x2C,
		(byte)0xB5, (byte)0x2A, (byte)0xFF, (byte)0x3C,
		(byte)0xE1, (byte)0xB1, (byte)0x29, (byte)0x40,
		(byte)0x18, (byte)0x11, (byte)0x8D, (byte)0x7C,
		(byte)0x84, (byte)0xA7, (byte)0x0A, (byte)0x72,
		(byte)0xD6, (byte)0x86, (byte)0xC4, (byte)0x03,
		(byte)0x19, (byte)0xC8, (byte)0x07, (byte)0x29,
		(byte)0x7A, (byte)0xCA, (byte)0x95, (byte)0x0C,
		(byte)0xD9, (byte)0x96, (byte)0x9F, (byte)0xAB,
		(byte)0xD0, (byte)0x0A, (byte)0x50, (byte)0x9B,
		(byte)0x02, (byte)0x46, (byte)0xD3, (byte)0x08,
		(byte)0x3D, (byte)0x66, (byte)0xA4, (byte)0x5D,
		(byte)0x41, (byte)0x9F, (byte)0x9C, (byte)0x7C,
		(byte)0xBD, (byte)0x89, (byte)0x4B, (byte)0x22,
		(byte)0x19, (byte)0x26, (byte)0xBA, (byte)0xAB,
		(byte)0xA2, (byte)0x5E, (byte)0xC3, (byte)0x55,
		(byte)0xE9, (byte)0x2F, (byte)0x78, (byte)0xC7
	};
	
	// The SKIP 1024 bit modulus
	private static final BigInteger		DHPARAM_Skip1024Modulus		= new BigInteger(1, DHPARAM_Skip1024ModulusBytes);
	
	// The base used with the SKIP 1024 bit modulus
	private static final BigInteger		DHPARAM_Skip1024Base		= BigInteger.valueOf(2);
	
	// Easy victory
	public static final DHParameterSpec	DHPARAM_SkipParamSpec		= new DHParameterSpec(DHPARAM_Skip1024Modulus, DHPARAM_Skip1024Base);
	
	
	
	public KeyDhPub makeMagic(byte[] $wtf) {
		try {
			return new KeyDhPub((DHPublicKey)$kf.generatePublic(new DHPublicKeySpec(new BigInteger($wtf), DH1024.DHPARAM_SkipParamSpec.getP(), DH1024.DHPARAM_SkipParamSpec.getG())));
		} catch (InvalidKeySpecException $e) { throw new MajorBug(IMPOSSIBLE, $e); }
	}
}
