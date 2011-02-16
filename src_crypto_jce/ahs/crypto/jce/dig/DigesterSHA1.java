package ahs.crypto.jce.dig;

import ahs.crypto.jce.*;
import ahs.crypto.jce.dig.*;
import ahs.util.*;

import java.security.*;

/**
 * @author hash
 *
 */
public class DigesterSHA1 implements Digester {
	public DigesterSHA1() {
		try {
			$md = MessageDigest.getInstance("SHA-1");	// 256, 384, and 512 also exist.
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
	
	public static final int OUTPUT_SIZE_BYTES = 20;
	public static final int OUTPUT_SIZE_BITS = 160;
	
	public int getOutputSize() {
		return OUTPUT_SIZE_BYTES;
	}
}
