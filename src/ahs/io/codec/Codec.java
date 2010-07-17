package ahs.io.codec;

import ahs.io.*;
import ahs.json.*;
import java.util.*;

/**
 * <p>
 * Many data-storage classes implement some form of serialization or encoding in their own
 * methods. This class and its contained interfaces allow classes such as these as well as
 * other heterogenous classes which may not provide their own internal encoding to all be
 * wrapped smoothly into one entry point for encoding and decoding into a single generic
 * format.
 * </p>
 * 
 * <p>
 * There are advantages to using other factory-like classes to perform encoding and
 * decoding rather than simply having classes always be self-encoding and decoding. It
 * allows multiple encoders to be chosen from for a single class, for example, depending
 * on context. It might allow one encoder class to perform the encoding operation for
 * several similar data classes, and thus allow optomizations reducing code size as well
 * as preparing often used variables (and making life easier for HotSpot). Overall, it's
 * just a better example of MVC practice -- a codec should be a C for any of your M.
 * </p>
 * 
 * @author hash
 * 
 * @param <$CODE>
 *                The type of object used to represent the encoded version of the data. A
 *                typical example might be ahs.json.JSONObject.
 */
public interface Codec<$CODE> {
	public <$TARG, $SPEC extends $TARG> void putHook(Class<$SPEC> $datclrclass, Encoder<$CODE, $TARG> $encoder);

	public <$TARG, $SPEC extends $TARG> void putHook(Class<$SPEC> $datclrclass, Decoder<$CODE, $TARG> $decoder);
	
	public <$TARG> $CODE encode($TARG $datclr) throws TranslationException;
	
	public <$TARG> $TARG decode($CODE $datenc, Class<$TARG> $datclrclass) throws TranslationException;
	
	


	/**
	 * <p>
	 * This is a utility class for use in building the internals of codec
	 * implementations. The putHook(*) and encode/decode methods of the Codec
	 * interface align nicely with these dispatcher implementations.
	 * </p>
	 * 
	 * @author hash
	 * 
	 * @param <$C>
	 *                The type of object used to represent the encoded version of the
	 *                data. In practice, this should presumably match the $CODE
	 *                parameter of the Codec interface.
	 */
	public static class EncoderDispatch<$C> {
        	public <$T, $S extends $T> void putHook(Class<$S> $c, Encoder<$C,$T> $e) {
        		$hooks.put($c, $e);
        	}
        	
		private Map<Class<?>,Encoder<$C,?>>	$hooks	= new HashMap<Class<?>,Encoder<$C,?>>();
		
		@SuppressWarnings("unchecked")	// yes, the following method is technically unsafe.  at runtime, it should be absolutely reliable.
		public <$T> $C encode(Codec<$C> $codec, $T $x) throws TranslationException {
			Encoder<$C,$T> $hook = (Encoder<$C,$T>)$hooks.get($x.getClass());
			if ($hook == null) throw new TranslationException("Encoding dispatch hook not found for " + $x.getClass().getName()); 
			return encode($codec, $x, $hook);
		}
		
		// probably a bad idea to have to specify an encoder like this.  you're going to want to remember to put in a matching decoder anyway.
		public <$T> $C encode(Codec<$C> $codec, $T $x, Encoder<$C,$T> $h) throws TranslationException {
			return $h.encode($codec, $x);
		}
	}
	
	


	/**
	 * <p>
	 * This is a utility class for use in building the internals of codec
	 * implementations. The putHook(*) and encode/decode methods of the Codec
	 * interface align nicely with these dispatcher implementations.
	 * </p>
	 * 
	 * @author hash
	 * 
	 * @param <$C>
	 *                The type of object used to represent the encoded version of the
	 *                data. In practice, this should presumably match the $CODE
	 *                parameter of the Codec interface.
	 */
	public static class DecoderDispatch<$C> {
        	public <$T, $S extends $T> void putHook(Class<$S> $c, Decoder<$C,$T> $d) {
        		$hooks.put($c, $d);
        	}
        	
		private Map<Class<?>,Decoder<$C,?>>	$hooks	= new HashMap<Class<?>,Decoder<$C,?>>();
		
		@SuppressWarnings("unchecked")	// yes, the following method is technically unsafe.  at runtime, it should be absolutely reliable.
		public <$T> $T decode(Codec<$C> $codec, $C $x, Class<$T> $c) throws TranslationException {
			Decoder<$C,$T> $hook = (Decoder<$C,$T>)$hooks.get($c);
			if ($hook == null) throw new TranslationException("Decoding dispatch hook not found for class " + $c.getCanonicalName()); 
			return $hook.decode($codec, $x);
		}
	}
}
