package us.exultant.ahs.io.codec;

import us.exultant.ahs.io.*;
import us.exultant.ahs.io.codec.json.*;
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
 *                typical example might be ahs.io.codec.json.JsonObject or
 *                ahs.io.codec.ebon.EbonObject.
 */
public interface Codec<$CODEC extends Codec<$CODEC, $CODE>, $CODE> {
	public <$TARG, $SPEC extends $TARG> void putHook(Class<$SPEC> $datclrclass, Encoder<$CODEC, $CODE, $TARG> $encoder);
	
	public <$TARG, $SPEC extends $TARG> void putHook(Class<$SPEC> $datclrclass, Decoder<$CODEC, $CODE, $TARG> $decoder);
	
	public <$TARG> $CODE encode($TARG $datclr, Class<$TARG> $datclrclass) throws TranslationException;
	
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
	public static class EncoderDispatch<$CO extends Codec<$CO,$C>, $C> {
        	public <$T, $S extends $T> void putHook(Class<$S> $c, Encoder<$CO,$C,$T> $e) {
        		$hooks.put($c, $e);
        	}
        	
		private Map<Class<?>,Encoder<$CO,$C,?>>	$hooks	= new HashMap<Class<?>,Encoder<$CO,$C,?>>();

		@SuppressWarnings("unchecked")	// ...seriously?
		public <$T> $C encode($CO $codec, $T $x) throws TranslationException { 
			return encode($codec, $x, (Class<$T>)$x.getClass());
		}
		
		@SuppressWarnings("unchecked")	// yes, the following method is technically unsafe.  at runtime, it should be absolutely reliable.
		public <$T> $C encode($CO $codec, $T $x, Class<$T> $c) throws TranslationException {
			Encoder<$CO,$C,$T> $hook = (Encoder<$CO,$C,$T>)$hooks.get($c);
			if ($hook == null) throw new TranslationException("Encoding dispatch hook not found for " + $x.getClass().getName()); 
			return $hook.encode($codec, $x);
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
	public static class DecoderDispatch<$CO extends Codec<$CO,$C>, $C> {
        	public <$T, $S extends $T> void putHook(Class<$S> $c, Decoder<$CO,$C,$T> $d) {
        		$hooks.put($c, $d);
        	}
        	
		private Map<Class<?>,Decoder<$CO,$C,?>>	$hooks	= new HashMap<Class<?>,Decoder<$CO,$C,?>>();
		
		@SuppressWarnings("unchecked")	// yes, the following method is technically unsafe.  at runtime, it should be absolutely reliable.
		public <$T> $T decode($CO $codec, $C $x, Class<$T> $c) throws TranslationException {
			Decoder<$CO,$C,$T> $hook = (Decoder<$CO,$C,$T>)$hooks.get($c);
			if ($hook == null) throw new TranslationException("Decoding dispatch hook not found for class " + $c.getCanonicalName()); 
			return $hook.decode($codec, $x);
		}
	}
}
