package ahs.crypto.jce.dig;

import ahs.crypto.jce.*;
import ahs.crypto.jce.dig.*;
import ahs.util.*;

import java.security.*;

/**
 * @author hash
 *
 */
public class DigesterMD5 implements Digester {
	public DigesterMD5() {
		try {
			$md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException $e) {
			throw new MajorBug(KeySystem.IMPOSSIBLE, $e);
		}
	}

	private MessageDigest $md;

	public byte[] digest(byte[] $x) {
		$md.update($x);
		return $md.digest();
	}
	
	public byte[] digest(byte[]... $xs) {
		for (byte[] $x : $xs)
			$md.update($x);
		return $md.digest();
	}
	
	public static final int OUTPUT_SIZE_BYTES = 16;
	public static final int OUTPUT_SIZE_BITS = 128;
	
	public int getOutputSize() {
		return OUTPUT_SIZE_BYTES;
	}
}
