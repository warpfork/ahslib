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
import java.util.*;

/**
 * <p>
 * Use this class to implement polymorphism in codecs by enrolling multiple instantiable
 * classes that share a common interface in this mux, along with their encoders and
 * decoders. Upon encoding, the mux shifts the "{@link Eon#MAGICWORD_CLASS}
 * " field to the " {@link Eon#MAGICWORD_HINT}" field, and places the common interface's
 * class in the "{@link Eon#MAGICWORD_CLASS}" field; the process is reversed in decoding.
 * Note that this means that the mux'd instantiable classes enrolled in the mux must not
 * use the "{@link Eon#MAGICWORD_HINT}" data field themselves, but otherwise any existing
 * encoders and decoders should be usable transparently. To use the polymorphism, you use
 * codec instances for encoding and decoding as normal, but must also explicitly call the
 * {@link Codec#encode(Object, Class)} and {@link Codec#decode(Object, Class)} methods
 * with the common interface (<tt>$FACE</tt>) as the target class &mdash; using
 * {@link CodecImpl#encode(Object)} will not work, because it assumes you wish to encode
 * as the most specific type known.
 * </p>
 *
 * <p>
 * Calling the <tt>enroll(*)</tt> functions of this class also calls the
 * <tt>putHook(*)</tt> methods on the parent codec. Encoding and decoding hooks for the
 * <tt>$FACE</tt> type are placed in the parent codec at construction time.
 * </p>
 *
 * <p>
 * Once this mux is configured, all references to it can be safely discarded -- it will be
 * used internally by the parent codec in a transparent fashion.
 * </p>
 *
 * <p>
 * Be aware that the decoding process for a muxed object mutates the given EonObject as
 * opposed to cloning it (specifically, it sets the {@link Eon#MAGICWORD_CLASS} field).
 * </p>
 *
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 *
 */
public class EonDecodingMux<$FACE> {
	public EonDecodingMux(EonCodec $parent, Class<$FACE> $klass) {
		this.$parent = $parent;
		this.$klass = $klass;
		this.$demux = new HashMap<String,Class<? extends $FACE>>();
		initialize();
	}

	private final Class<$FACE>				$klass;
	private final EonCodec					$parent;
	private final Map<String,Class<? extends $FACE>>	$demux;

	private void initialize() {
		$parent.putHook($klass, new Dencoder<EonCodec,EonObject,$FACE>() {
			public EonObject encode(EonCodec $codec, $FACE $x) throws TranslationException {
				EonObject $eo = $parent.encode($x);	// even though $x currently has the type $FACE, later the parent codec will use $x.getClass... which will have more information, and thus keep the encoder from looping back to the $FACE target.
				$eo.put(Eon.MAGICWORD_HINT, $eo.getKlass());
				$eo.putKlass(EonDecodingMux.this.$klass);
				return $eo;
			}

			public $FACE decode(EonCodec $codec, EonObject $eo) throws TranslationException {
				$eo.assertKlass(EonDecodingMux.this.$klass);
				String $hint = $eo.getString(Eon.MAGICWORD_HINT);	// yes, this throws a translation exception if a hint isn't found.
				Class<? extends $FACE> $t = $demux.get($hint);
				if ($t == null) throw new TranslationException("Decoding dispatch hook not found for hint \"" + $hint + "\"");
				$eo.putKlass($hint);
				return $parent.decode($eo, $t);
			}
		});
	}

	public <$T extends $FACE> void enroll(Class<$T> $klass, final Encoder<EonCodec,EonObject,$T> $encoder, final Decoder<EonCodec,EonObject,$T> $decoder) {
		if ($klass == null) throw new NullPointerException();
		$parent.putHook($klass, $encoder);
		$parent.putHook($klass, $decoder);
		$demux.put(Eon.getKlass($klass), $klass);
	}

	/**
	 * Enrolls <tt>$dencoder</tt> as both the Encoder and Decoder for objects of type $klass.  This affects both the state of this EonDecodingMux, and also immediately
	 *
	 * @param $klass
	 * @param $dencoder
	 */
	public <$T extends $FACE> void enroll(Class<$T> $klass, final Dencoder<EonCodec,EonObject,$T> $dencoder) {
		if ($dencoder == null) throw new NullPointerException();
		enroll($klass, $dencoder, $dencoder);
	}
}
