package ahs.crypto.jce.ibe.fak;

import ahs.crypto.jce.dig.*;
import ahs.io.*;
import ahs.json.*;
import ahs.util.*;

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
		return new JSONObject("Enc", Base64.encode($ko.getEncoded()), $plaintext).toString().getBytes(Strings.UTF_8);
	}
	
	/** {@inheritDoc} */
	public byte[] decrypt(byte[] $ciphertext, KeyFakPrv $kx) {
		try {
			JSONObject $jo = new JSONObject(new String($ciphertext, Strings.UTF_8));
			$jo.assertKlass("Enc");
			if (!Arr.equals($kx.getEncoded(), Base64.decode($jo.getName()))) return null;
			return $jo.getByteData();
		} catch (JSONException $e) {
			return null;
		}
	}
	
	/** {@inheritDoc} */
	public byte[] sign(byte[] $text, KeyFakPrv $myKey) {
		Digester $d = new DigesterMD5();
		byte[] $sig = $d.digest(Arr.cat($myKey.getEncoded(), $text));
		return new JSONObject("Sig", Base64.encode($myKey.getEncoded()), $sig).toString().getBytes(Strings.UTF_8);
	}
	
	/** {@inheritDoc} */
	public boolean verify(byte[] $text, byte[] $sig, KeyFakPub $signerKey) {
		Digester $d = new DigesterMD5();
		try {
			JSONObject $jo = new JSONObject(new String($text, Strings.UTF_8));
			$jo.assertKlass("Sig");
			if (!Arr.equals($signerKey.getEncoded(), Base64.decode($jo.getName()))) return false;
			byte[] $resig = $d.digest(Arr.cat($signerKey.getEncoded(), $text));
			return Arr.equals($resig, $jo.getByteData());
		} catch (JSONException $e) {
			return false;
		}
	}
	
	
	
	/** {@inheritDoc} */
	public byte[] encode(KeyFakPub $ko) {
		return $ko.getEncoded();
	}
	
	/** {@inheritDoc} */
	public KeyFakPub decodePublicKey(byte[] $koe) throws TranslationException {
		return new KeyFakPub(BitVector.DECODER_JSON.decode(null, new JSONObject(new String($koe, Strings.UTF_8))));
	}
	
	/** {@inheritDoc} */
	public byte[] encode(KeyFakPrv $kx) {
		return $kx.getEncoded();
	}
	
	/** {@inheritDoc} */
	public KeyFakPrv decodePrivateKey(byte[] $kxe) throws TranslationException {
		return new KeyFakPrv(BitVector.DECODER_JSON.decode(null, new JSONObject(new String($kxe, Strings.UTF_8))));
	}
}
