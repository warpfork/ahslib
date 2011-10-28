/*
 * Copyright 2010, 2011 Eric Myhre <http://exultant.us>
 * 
 * This file is part of AHSlib.
 *
 * AHSlib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License, or
 * (at the original copyright holder's option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package us.exultant.ahs.util;

import java.nio.*;
import java.util.*;
import java.util.regex.*;

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
	//public static final List<Object>	EMPTY_LIST		= Collections.EMPTY_LIST; 
	public static final List<Object>	LIST_NULL;
	static {
		List<Object> tmp_LIST_NULL = new ArrayList<Object>(1);
		tmp_LIST_NULL.add(null);
		LIST_NULL = Collections.unmodifiableList(tmp_LIST_NULL);
	}
	// consider the potential for replacing this with a SyncFreeProvider and some kind of map?  would be nice to remove references like these from core and yet maintain the non-need of repetition in client code.
	
	
	public static final Pattern		PATTERN_DOT		= Pattern.compile(".", Pattern.LITERAL);
	
	
	
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
