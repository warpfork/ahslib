package us.exultant.ahs.io.codec.eon;

import us.exultant.ahs.io.*;
import us.exultant.ahs.io.codec.*;
import us.exultant.ahs.io.codec.json.*;

import java.nio.*;
import java.util.*;

public final class Eon {
	/**
	 * Assumes the entire ByteBuffer is backed by an accessible array and the entire
	 * thing should be used from beginning to end, ignoring position. (If this is not
	 * acceptable in your present situation, consider using the
	 * {@link ahs.util.Arr#makeWrapped(ByteBuffer)} function and have fun with the
	 * extra overhead.)
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
	
	public static final String MAGICWORD_CLASS = "#";
	public static final String MAGICWORD_NAME = "$";
	public static final String MAGICWORD_DATA = "%";
	public static final String MAGICWORD_HINT = "!";
	
	public static String getKlass(Class<?> $c) {
		String[] $arrg = $c.getCanonicalName().split("\\Q.\\E");
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
