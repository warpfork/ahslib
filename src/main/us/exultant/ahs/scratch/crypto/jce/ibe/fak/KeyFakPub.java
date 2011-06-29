package us.exultant.ahs.scratch.crypto.jce.ibe.fak;

import us.exultant.ahs.core.*;
import us.exultant.ahs.util.*;
import us.exultant.ahs.scratch.crypto.jce.ibe.*;
import us.exultant.ahs.codec.eon.pre.*;

public class KeyFakPub implements KeyIbePub {
	public KeyFakPub(BitVector $bat) {
		$x = $bat;
		try {
			$serial = BitVectorDencoder.ENCODER.encode(KeySystemIbeFak.HACK, $x).serialize();
		} catch (TranslationException $e) {
			throw new MajorBug("what the hell.");
		}
	}
	
	private final BitVector $x;
	private final byte[] $serial;
	
	public String getAlgorithm() {
		return KeySystemIbeFak.ALGORITHM;
	}
	
	/** DNMR OMFG DNMR */
	public byte[] getEncoded() {
		return $serial;
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
		KeyFakPub other = (KeyFakPub) obj;
		if (this.$x == null) {
			if (other.$x != null) return false;
		} else if (!this.$x.equals(other.$x)) return false;
		return true;
	}
}
