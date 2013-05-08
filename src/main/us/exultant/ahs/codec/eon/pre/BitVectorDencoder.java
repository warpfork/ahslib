/*
 * Copyright 2010 - 2013 Eric Myhre <http://exultant.us>
 *
 * This file is part of AHSlib.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package us.exultant.ahs.codec.eon.pre;

import us.exultant.ahs.core.*;
import us.exultant.ahs.util.*;
import us.exultant.ahs.codec.*;
import us.exultant.ahs.codec.eon.*;

public class BitVectorDencoder implements Dencoder<EonCodec,EonObject,BitVector> {
	public static final Dencoder<EonCodec,EonObject,BitVector> DENCODER = new BitVectorDencoder();
	public static final  Encoder<EonCodec,EonObject,BitVector> ENCODER = DENCODER;
	public static final  Decoder<EonCodec,EonObject,BitVector> DECODER = DENCODER;

	public EonObject encode(EonCodec $codec, BitVector $x) throws TranslationException {
		EonObject $jo = $codec.simple("BiV", null, $x.toByteArray());
		$jo.put("l", $x.length());
		return $jo;
	}

	public BitVector decode(EonCodec $codec, EonObject $x) throws TranslationException {
		$x.assertKlass("BiV");
		return new BitVector($x.getByteData(),0,$x.getInt("l"));
	}
}
