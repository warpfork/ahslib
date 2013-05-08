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

package us.exultant.ahs.codec.eon;

import us.exultant.ahs.core.*;
import us.exultant.ahs.codec.*;

/**
 * Adapts a {@link Codec} to perform as a {@link Translator} between a serializable
 * {@link EonObject} form and some full Object specified by the generic type. (Note that
 * this may even implicitly be a muxed type, since the codec will handle this
 * transparently if already properly configured with a {@link EonDecodingMux}.)
 *
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 *
 * @param <$FACE>
 *                the type of Object this Translator should wield the Codec to decode
 *                EonObject into.
 */
public class TranslatorEonToObject<$FACE> implements Translator<EonObject,$FACE> {
	public TranslatorEonToObject(Class<$FACE> $klass, EonCodec $codec) {
		this.$klass = $klass;
		this.$codec = $codec;
	}

	private final Class<$FACE>	$klass;
	private final EonCodec		$codec;

	/**
	 * As long as the {@link Codec} this Translator was constructed with contains only
	 * re-entrant {@link Decoder}s, this translation method also reentrant.
	 */
	public $FACE translate(EonObject $x) throws TranslationException {
		return $codec.decode($x, $klass);
	}
}
