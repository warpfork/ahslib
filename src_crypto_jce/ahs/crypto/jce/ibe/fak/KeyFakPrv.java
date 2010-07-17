package ahs.crypto.jce.ibe.fak;

import ahs.crypto.jce.ibe.*;
import ahs.io.*;
import ahs.util.*;

public class KeyFakPrv implements KeyIbePrv {
	public KeyFakPrv(BitVector $bat) {
		$x = $bat;
	}
	
	private BitVector $x;
	
	public String getAlgorithm() {
		return KeySystemIbeFak.ALGORITHM;
	}

	public byte[] getEncoded() {
		try {
			return BitVector.ENCODER_JSON.encode(null, $x).toString().getBytes(Strings.UTF_8);
		} catch (TranslationException $e) {
			X.cry($e);
			return null;
		}
	}

	public String getFormat() {
		return KeySystemIbeFak.ALGORITHM;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.$x == null) ? 0 : this.$x.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		KeyFakPrv other = (KeyFakPrv) obj;
		if (this.$x == null) {
			if (other.$x != null) return false;
		} else if (!this.$x.equals(other.$x)) return false;
		return true;
	}
}
