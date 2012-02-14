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

import org.bouncycastle.crypto.*;
import org.bouncycastle.crypto.engines.*;
import org.bouncycastle.crypto.params.*;

/**
 * <p>
 * Purpose is simple: override the initization method on AESFastEngine so that if the same
 * key material was used last time, we don't waste time discarding the old key schedule
 * and generating a new one that's exactly the same. Why BC wasn't smart enough to do this
 * itself I do not know.
 * </p>
 * 
 * <p>
 * This same-key check is implemented as a pointer-equality check on the byte array in the
 * KeyParameter (which we presume is what CipherParameters is; if not, we let the BC AES
 * engine scream)... which means in order to gain the efficiency here, the calling code
 * must either be caching that KeyParameter object, or use the hacked KeyParameter
 * subclass I made ({@link KeyParamMod}) to allow avoidance of that ridiculous array copy in the
 * constructor of the original KeyParameter. However, this also means that if you ever
 * mutate that byte array, you're going to get yourself in a world of trouble, because now
 * the engine won't notice, and it's not going to update its key schedule even though you
 * probably think you gave it a new key, and boy are you going to be in for a sorry
 * surprise when you figure out what an idiot you were about pointers.
 * </p>
 * 
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 */
public class AESEngineMod extends AESFastEngine {
	// note incredibly lazy initialization here it?  we even check these before setting them!  it's fine, though: the worst that can happen if either one of these is unset or wrong is an unnecessary init of the cipher... and that's exactly what should be done if either of these are unset.
	private byte[]	$lastKey;
	private boolean	$lastEdMode;	// and can you believe they don't even bother to have a function for asking if it's in encrypt mode or not?!  jesus.
					
	public void init(boolean $forEncryption, CipherParameters $params) {
		tryToHelp: {
			if ($lastEdMode != $forEncryption)
				break tryToHelp;
			if (!($params instanceof KeyParameter))	// this is just going to throw anyway, but i'll let them do it rather than replicate the exception here.
				break tryToHelp;
			if ($lastKey != ((KeyParameter)$params).getKey())
				break tryToHelp;
			return;
		}
		super.init($forEncryption, $params);
		$lastEdMode = $forEncryption;
		$lastKey = ((KeyParameter)$params).getKey();	// again, the assumption that AESFastEngine is going to have throw up already if this isn't a KeyParameter.
	}
}
