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
		String[] $arrg = Primitives.PATTERN_DOT.split($c.getCanonicalName());
		return $arrg[$arrg.length-1];
	}
	public static String getKlass(Object $x) {
		return getKlass($x.getClass());
	}
	
	public static <$TM extends EonObject> $TM fill($TM $holder, Map<String,String> $map) throws UnencodableException {
		for (Map.Entry<String,String> $ent : $map.entrySet())
			$holder.put($ent.getKey(),$ent.getValue());
		return $holder;
	}
}
