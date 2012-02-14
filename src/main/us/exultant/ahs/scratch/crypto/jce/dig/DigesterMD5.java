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

package us.exultant.ahs.scratch.crypto.jce.dig;

import us.exultant.ahs.scratch.crypto.jce.*;
import us.exultant.ahs.util.*;
import java.security.*;

/**
 * @author Eric Myhre <tt>hash@exultant.us</tt>
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
