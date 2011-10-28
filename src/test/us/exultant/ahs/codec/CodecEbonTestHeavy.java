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

package us.exultant.ahs.codec;

import us.exultant.ahs.core.*;
import us.exultant.ahs.codec.ebon.*;

public class CodecEbonTestHeavy extends CodecEonTest {
	public CodecEbonTestHeavy() {
		super(new EbonCodec());
	}
	
	public void testHard() throws TranslationException {
		for (int $i = 0; $i < 1000000; $i++) {
			testListDenseSerial();
			testCompositeD3NMuxSerial();
		}
	}
}
