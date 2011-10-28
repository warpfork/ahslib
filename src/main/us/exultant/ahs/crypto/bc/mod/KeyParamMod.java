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
