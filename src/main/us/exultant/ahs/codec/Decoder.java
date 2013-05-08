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

package us.exultant.ahs.codec;

import us.exultant.ahs.core.*;

/**
 *
 * <p>
 * Classes that support decoding are recommended to follow a pattern of providing an
 * Decoder implementer as a public static inner class of themselves, then make an instance
 * available as a public static final field. This pattern allows complete access to all
 * private fields of the decodable class without increasing their exposure, keeps code
 * cleaner by limiting amount of visibility the codec running a decode process gains into
 * its client classes, and removes any possibility for ambiguity should it be the case
 * that some classes wish to provide multiple methods for decoding.
 * </p>
 *
 * <p>
 * Unless a specific implementation states otherwise, the decode method is assumed to be
 * reentrant.
 * </p>
 *
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 *
 * @param <$TARG>
 *                The type of object to be produced by the decoding. A single complete
 *                codec system will typically require decoders of one $TARG type for each
 *                type of object to be encodable, each with the same $CODE type.
 * @param <$CODE>
 *                The type of object used to represent the encoded version of the data. A
 *                typical example might be ahs.json.JSONObject.
 */
public interface Decoder<$CODEC extends Codec<$CODEC,$CODE>, $CODE, $TARG> {
	public $TARG decode($CODEC $codec, $CODE $x) throws TranslationException;
}
