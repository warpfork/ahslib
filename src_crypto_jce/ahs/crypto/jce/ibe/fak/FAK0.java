package ahs.crypto.jce.ibe.fak;

import ahs.crypto.jce.dig.*;
import ahs.io.*;
import ahs.io.codec.eon.*;
import ahs.io.codec.json.*;
import ahs.util.*;

import mcon.base.*;

public class FAK0 implements KeySystemIbeFak {
	public FAK0(EonCodec $codec) {
		this.$codec = $codec;
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
			return $codec.simple("Enc", Base64.encode($ko.getEncoded()), $plaintext).serialize();
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
			if (!Arr.equals($kx.getEncoded(), Base64.decode($jo.getName()))) return null;
			return $jo.getByteData();
		} catch (TranslationException $e) {
			return null;
		}
	}
	
	/** {@inheritDoc} */
	public byte[] sign(byte[] $text, KeyFakPrv $myKey) {
		byte[] $sig = $d.digest(Arr.cat($myKey.getEncoded(), $text));
		try {
			return $codec.simple("Sig", Base64.encode($myKey.getEncoded()), $sig).serialize();
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
		EonObject $jo = $codec.newObj();
		$jo.deserialize($koe);
		return new KeyFakPub(BitVector.DECODER.decode(null, $jo));
	}
	
	/** {@inheritDoc} */
	public byte[] encode(KeyFakPrv $kx) {
		return $kx.getEncoded();
	}
	
	/** {@inheritDoc} */
	public KeyFakPrv decodePrivateKey(byte[] $kxe) throws TranslationException {
		EonObject $jo = $codec.newObj();
		$jo.deserialize($kxe);
		return new KeyFakPrv(BitVector.DECODER.decode(null, $jo));
	}
}
