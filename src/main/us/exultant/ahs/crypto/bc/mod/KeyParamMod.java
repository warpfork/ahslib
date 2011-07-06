package us.exultant.ahs.crypto.bc.mod;

import us.exultant.ahs.util.*;
import org.bouncycastle.crypto.params.KeyParameter;

/**
 * <p>
 * Purpose is simple: override KeyParameter so that copying the byte array of key material
 * on construction of the KeyParameter is not necessary.
 * </p>
 * 
 * <p>
 * The ability to wrap a byte array of key material directly gives the ability to build
 * crypto engines that use pointer equality on the byte array to rapidly detect if the
 * same key material was used in a previous round of the cipher, which means we don't have
 * waste time discarding the old key schedule and generating a new one that's exactly the
 * same. Why BC wasn't smart enough to do this itself I do not know.
 * </p>
 * 
 * @author hash
 */
public class KeyParamMod extends KeyParameter {
	public KeyParamMod(byte[] key) {
		super(Primitives.EMPTY_BYTE);	// oh my GOD this is stupid
		this.key = key;
	}
	
	private byte[]	key;
	
	public byte[] getKey() {
		return key;
	}
}
