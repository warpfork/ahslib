package ahs.crypto.jce.ibe.fak;

import ahs.crypto.jce.dig.*;
import ahs.io.*;
import ahs.io.codec.eon.*;
import ahs.io.codec.json.*;
import ahs.util.*;

import mcon.base.*;

public class FAK0 implements KeySystemIbeFak {
	public FAK0() {
		
	}
	
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
			return MCON.CODECPROV.get().simple("Enc", Base64.encode($ko.getEncoded()), $plaintext).serialize();
		} catch (TranslationException $e) {
			X.cry($e);
			return null;
		}
	}
	
	/** {@inheritDoc} */
	public byte[] decrypt(byte[] $ciphertext, KeyFakPrv $kx) {
		try {
			EonObject $jo = MCON.CODECPROV.get().newObj();
			$jo.deserialize($ciphertext);
			$jo.assertKlass("Enc");
			if (!Arr.equals($kx.getEncoded(), Base64.decode($jo.getName()))) return null;
			return $jo.getByteData();
		} catch (TranslationException $e) {
			return null;
		}
	}
	
	/** {@inheritDoc} */
	public byte[] sign(byte[] $text, KeyFakPrv $myKey) {
		Digester $d = new DigesterMD5();
		byte[] $sig = $d.digest(Arr.cat($myKey.getEncoded(), $text));
		try {
			return MCON.CODECPROV.get().simple("Sig", Base64.encode($myKey.getEncoded()), $sig).serialize();
		} catch (TranslationException $e) {
			X.cry($e);
			return null;
		}
	}
	
	/** {@inheritDoc} */
	public boolean verify(byte[] $text, byte[] $sig, KeyFakPub $signerKey) {
		Digester $d = new DigesterMD5();
		try {
			EonObject $jo = MCON.CODECPROV.get().newObj();
			$jo.deserialize($text);
			$jo.assertKlass("Sig");
			if (!Arr.equals($signerKey.getEncoded(), Base64.decode($jo.getName()))) return false;
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
		EonObject $jo = MCON.CODECPROV.get().newObj();
		$jo.deserialize($koe);
		return new KeyFakPub(BitVector.DECODER.decode(null, $jo));
	}
	
	/** {@inheritDoc} */
	public byte[] encode(KeyFakPrv $kx) {
		return $kx.getEncoded();
	}
	
	/** {@inheritDoc} */
	public KeyFakPrv decodePrivateKey(byte[] $kxe) throws TranslationException {
		EonObject $jo = MCON.CODECPROV.get().newObj();
		$jo.deserialize($kxe);
		return new KeyFakPrv(BitVector.DECODER.decode(null, $jo));
	}
}
