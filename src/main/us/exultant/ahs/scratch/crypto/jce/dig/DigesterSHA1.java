package us.exultant.ahs.scratch.crypto.jce.dig;

import us.exultant.ahs.scratch.crypto.jce.*;
import us.exultant.ahs.util.*;
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
