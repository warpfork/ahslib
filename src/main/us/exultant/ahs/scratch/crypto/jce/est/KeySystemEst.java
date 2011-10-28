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

package us.exultant.ahs.scratch.crypto.jce.est;

import us.exultant.ahs.core.*;
import us.exultant.ahs.util.*;
import us.exultant.ahs.scratch.crypto.jce.*;
import us.exultant.ahs.scratch.crypto.jce.sym.*;

public interface KeySystemEst<$KEYPUB extends KeyEstPub, $KEYPRV extends KeyEstPrv, $KEYEST extends KeySym> extends KeySystem  {
	public Tup2<$KEYPUB,$KEYPRV> generateKeys();
	
	public $KEYEST reachSecret($KEYPRV $mykx, $KEYPUB $theirko);
	
	
	
	public byte[] encode($KEYPUB $ko);
	
	public $KEYPUB decodePublicKey(byte[] $koe) throws TranslationException;
	
	public byte[] encode($KEYPRV $kx);
	
	public $KEYPRV decodePrivateKey(byte[] $kxe) throws TranslationException;
}
