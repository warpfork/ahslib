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

package us.exultant.ahs.scratch.crypto.jce.ibe.fak;

import us.exultant.ahs.core.*;
import us.exultant.ahs.util.*;
import us.exultant.ahs.scratch.crypto.jce.dig.*;
import us.exultant.ahs.codec.eon.*;
import us.exultant.ahs.codec.eon.pre.*;

public class FAK0 implements KeySystemIbeFak {
	public FAK0(EonCodec $codec) {
		this.$codec = KeySystemIbeFak.HACK;
		$d = new DigesterMD5();
	}
	
	private final EonCodec $codec;
	private final Digester $d;
	
	/** {@inheritDoc} */
	public KeyFakPrv generateKey(KeyFakPub $ko) {
		try {
			return decodePrivateKey(encode($ko));
		} catch (TranslationException $e) {
			X.cry($e);
			return null;
		}
	}
	
	/** {@inheritDoc} */
	public byte[] encrypt(byte[] $plaintext, KeyFakPub $ko) {
		try {
			EonObject $eo = $codec.newObj();
			$eo.putKlass("Enc");
			$eo.put("k", $ko.getEncoded());
			$eo.putData($plaintext);
			return $eo.serialize();
		} catch (TranslationException $e) {
			X.cry($e);
			return null;
		}
	}
	
	/** {@inheritDoc} */
	public byte[] decrypt(byte[] $ciphertext, KeyFakPrv $kx) {
		try {
			EonObject $jo = $codec.newObj();
			$jo.deserialize($ciphertext);
			$jo.assertKlass("Enc");
			if (!Arr.equals($kx.getEncoded(), $jo.getBytes("k"))) return null;
			return $jo.getByteData();
		} catch (TranslationException $e) {
			return null;
		}
	}
	
	/** {@inheritDoc} */
	public byte[] sign(byte[] $text, KeyFakPrv $myKey) {
		byte[] $sig = $d.digest(Arr.cat($myKey.getEncoded(), $text));
		try {
			EonObject $eo = $codec.newObj();
			$eo.putKlass("Sig");
			$eo.put("k", $myKey.getEncoded());
			$eo.putData($sig);
			return $eo.serialize();
		} catch (TranslationException $e) {
			X.cry($e);
			return null;
		}
	}
	
	/** {@inheritDoc} */
	public boolean verify(byte[] $text, byte[] $sig, KeyFakPub $signerKey) {
		try {
			EonObject $jo = $codec.newObj();
			$jo.deserialize($text);
			$jo.assertKlass("Sig");
			if (!Arr.equals($signerKey.getEncoded(), $jo.getBytes("k"))) return false;
			byte[] $resig = $d.digest(Arr.cat($signerKey.getEncoded(), $text));
			return Arr.equals($resig, $jo.getByteData());
		} catch (TranslationException $e) {
			return false;
		}
	}
	
	
	
	/** {@inheritDoc} */
	public byte[] encode(KeyFakPub $ko) {
		return $ko.getEncoded();
	}
	
	/** {@inheritDoc} */
	public KeyFakPub decodePublicKey(byte[] $koe) throws TranslationException {
		EonObject $jo = $codec.newObj();
		$jo.deserialize($koe);
		return new KeyFakPub(BitVectorDencoder.DECODER.decode(null, $jo));
	}
	
	/** {@inheritDoc} */
	public byte[] encode(KeyFakPrv $kx) {
		return $kx.getEncoded();
	}
	
	/** {@inheritDoc} */
	public KeyFakPrv decodePrivateKey(byte[] $kxe) throws TranslationException {
		EonObject $jo = $codec.newObj();
		$jo.deserialize($kxe);
		return new KeyFakPrv(BitVectorDencoder.DECODER.decode(null, $jo));
	}
}
