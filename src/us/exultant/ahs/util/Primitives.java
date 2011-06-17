package us.exultant.ahs.util;

import java.nio.*;

public class Primitives {
	/**
	 * ($field | $mask)
	 */
	public static int addMask(int $field, int $mask) {
		return ($field | $mask);
	}
	
	/**
	 * ($field & ~$mask)
	 */
	public static int removeMask(int $field, int $mask) {
		return ($field & ~$mask);
	}
	
	/**
	 * (($field & $mask) == $mask)
	 * @param $field
	 * @param $mask
	 * @return true if at all of the bits set in $mask are set in $field (even if more
	 *         bits are set in $field than in $mask), false otherwise.
	 */
	public static boolean containsFullMask(int $field, int $mask) {
		return (($field & $mask) == $mask);
	}
	
	/**
	 * (($field & $mask) != 0)
	 * @param $field
	 * @param $mask
	 * @return true if at least some of the bits set in $mask are set in $field, false
	 *         otherwise.  Also false if $mask is 0.
	 */
	public static boolean containsPartialMask(int $field, int $mask) {
		return (($field & $mask) != 0);
	}
	
	
	
	
	// things named EMPTY_[primitive] refer to arrays; everything else means the "empty" form of that object, whatever that means (usually a wrapper of an empty primitive).
	public static final byte[]		EMPTY_BYTE		= new byte[0];
	public static final String[]		EMPTY_STRING		= new String[0];
	public static final ByteBuffer		EMPTY_BYTEBUFFER	= ByteBuffer.wrap(EMPTY_BYTE);
	public static final ByteVector		EMPTY_BYTEVECTOR	= new ByteVector(EMPTY_BYTE);
	// consider the potential for replacing this with a SyncFreeProvider and some kind of map?  would be nice to remove references like these from core and yet maintain the non-need of repetition in client code.
	
	
	
	public static byte[] byteArrayFromInt(int $i) {
		byte[] $eax = new byte[4];
		$eax[0] = (byte) ($i >> 24);
		$eax[1] = (byte) (($i << 8) >> 24);
		$eax[2] = (byte) (($i << 16) >> 24);
		$eax[3] = (byte) (($i << 24) >> 24);
		return $eax;
	}
	
	public static int intFromByteArray(byte[] $preint, int $offset) {
		return (($preint[$offset] & 0xFF) << 24) | (($preint[$offset + 1] & 0xFF) << 16) | (($preint[$offset + 2] & 0xFF) << 8) | $preint[$offset + 3] & 0xFF;
	}
	
	public static int intFromByteArray(byte[] $preint) {
		return (($preint[0] & 0xFF) << 24) | (($preint[1] & 0xFF) << 16) | (($preint[2] & 0xFF) << 8) | $preint[3] & 0xFF;
	}
	
	
	
	public static int intFromUnsignedByte(byte $b) {
		return ($b & 0xFF);
	}
	


	@Deprecated
	public static byte[] fromB64String(String $b64) {
		return Base64.decode($b64);
	}
	
	@Deprecated
	public static String toB64String(byte[] $bat) {
		return Base64.encode($bat);
	}
}
