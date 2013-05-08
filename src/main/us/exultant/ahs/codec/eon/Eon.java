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

import us.exultant.ahs.codec.*;
import us.exultant.ahs.core.*;
import us.exultant.ahs.util.*;
import java.nio.*;
import java.util.*;

public final class Eon {
	/**
	 * Assumes the entire ByteBuffer is backed by an accessible array and the entire
	 * thing should be used from beginning to end, ignoring position. (If this is not
	 * acceptable in your present situation, consider using the
	 * {@link us.exultant.ahs.util.Arr#makeWrapped(ByteBuffer)} function and have fun
	 * with the extra overhead.)
	 */
	public static class TranslatorFromByteBuffer implements Translator<ByteBuffer,EonObject> {
		public TranslatorFromByteBuffer(EonCodec $co) {
			this.$co = $co;
		}

		private final EonCodec	$co;

		public EonObject translate(ByteBuffer $bb) throws TranslationException {
			return $co.deserialize($bb.array());
		}
	}

	public static class TranslatorToByteBuffer implements Translator<EonObject,ByteBuffer> {
		public TranslatorToByteBuffer(EonCodec $co) {
			this.$co = $co;
		}

		private final EonCodec	$co;

		public ByteBuffer translate(EonObject $eo) throws TranslationException {
			return ByteBuffer.wrap($co.serialize($eo));
		}
	}



	private Eon() {}	// thou shalt not instantiate me // not even for thine lulz // unless thou art full of reflection // in whence case thou art also full of cocks
				// the above comment required me to research hebrew tenses, which just taught me a LOT about the bible.

	/**
	 * This key is canonically used to encode a class string that refers to the
	 * correct type of the data encoded in an {@link EonObject}.
	 *
	 * Enforcing strict rules regarding this is up to individual {@link Decoder}
	 * implementations. In Java, this is usually dealt with through the
	 * {@link EonObject#putKlass(Class)} and {@link EonObject#assertKlass(Class)}
	 * methods for clarity (which in turn refer to {@link #getKlass(Class)}); in other
	 * languages, it is recommended to maintain a similar trend (type names minus any
	 * leading package or namespace declarations).
	 */
	public static final String MAGICWORD_CLASS = "#";
	/**
	 * This key is has no formal canonical usage (aside from any implications made by
	 * the linkage to the {@link EonObject#getName()} and
	 * {@link EonObject#putName(String)} methods).
	 */
	public static final String MAGICWORD_NAME = "$";
	/**
	 * This key is canonically used for the raw data of any object that does not
	 * contain more than one distinct field, but which is still encoded as an
	 * EonObject because it is useful to include a class string or what-have-you. (A
	 * class that does some sort of encapsulation on a byte array might be a primary
	 * example of a situation for this.)
	 */
	public static final String MAGICWORD_DATA = "%";
	/**
	 * This key is canonically used to hold a "type hint" &mdash; this is used
	 * internally by {@link EonDecodingMux} and usage of this key anywhere else is
	 * strongly inadvised (though technically valid, as long as you don't mind
	 * surprising results if ever using the standard provided muxing tools).
	 */
	public static final String MAGICWORD_HINT = "!";

	public static String getKlass(Class<?> $c) {
		return Reflect.getShortClassName($c);
	}
	public static String getKlass(Object $x) {
		return Reflect.getShortClassName($x);
	}

	public static <$TM extends EonObject> $TM fill($TM $holder, Map<String,String> $map) throws UnencodableException {
		for (Map.Entry<String,String> $ent : $map.entrySet())
			$holder.put($ent.getKey(),$ent.getValue());
		return $holder;
	}
}
