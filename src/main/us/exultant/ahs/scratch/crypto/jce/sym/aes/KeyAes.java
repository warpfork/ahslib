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

package us.exultant.ahs.scratch.crypto.jce.sym.aes;

import us.exultant.ahs.scratch.crypto.jce.sym.*;

import javax.crypto.*;

public class KeyAes extends KeySym.KeyWrapper<SecretKey> {
	public KeyAes(SecretKey $k) {
		super($k);
		if (!$k.getAlgorithm().equals("AES")) throw new IllegalArgumentException("Must rap a SecretKey for the AES algorithm.");
	}
	
	public String getAlgorithm() {
		return "AES";
	}
}
