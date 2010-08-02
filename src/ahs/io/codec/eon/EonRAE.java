package ahs.io.codec.eon;

import ahs.io.*;
import ahs.io.codec.*;
import ahs.io.codec.json.*;
import ahs.util.*;
import java.lang.reflect.*;

/**
 * Always respects the value given for classname encoding preferences as given in the
 * Encodable annotation.
 * 
 * @param <$T>
 *                can be <code>java.lang.Object</code> for all I care, though more
 *                precision is generally better.
 */
public class EonRAE<$T> implements Encoder<EonCodec,EonObject,$T> {
	public EonRAE() {
		this(Enc.DEFAULT);
	}
	public EonRAE(String $selector) {
		this.$selector = $selector;
	}
	
	private String $selector;
	
	public JsonObject encode(EonCodec $codec, $T $x) throws TranslationException {
		try {
			JsonObject $jo = new JsonObject();
			String $key;
			
			// pick out and put in the semblance of a class name we want
			// also, check if that class will allow itself to be encoded like this
			Class<?> $class = $x.getClass();
			Encodable $cenc = $class.getAnnotation(Encodable.class);
			if ($cenc == null)
				throw new UnencodableException("Class to be encoded must be annotated with the @Encodable interface.");
			else {
				if (!Arr.contains($cenc.styles(), $selector))
					throw new UnencodableException("Class to be encoded must be annotated to accept the style that this Encoder is configured for (selected=\""+$selector+"\", accept="+Arr.toString($cenc.styles())+").");
				
				$key = $cenc.value();
				if ($key.equals(Encodable.NONE))
					; /* nothing */
				else if ($key.equals(Encodable.DEFAULT))
					$jo.putKlass($x);
				else
					$jo.putKlass($key);
			}
			
			// walk across fields and serialize the non-static ones
			if ($cenc.all_fields()) {	// all of them, regardless of whether that particular field is annotated
				for (Field $f : $class.getDeclaredFields()) {
					$f.setAccessible(true);
					int $mod = $f.getModifiers();
					if (Modifier.isStatic($mod)) continue;
					Enc $anno = $f.getAnnotation(Enc.class);
					if ($anno != null) {
						if ($anno.key().isEmpty())
							$key = $f.getName();
						else $key = $anno.key(); 
						
						encodeField($codec, $jo, $key, $f, $x);
					} else {
						encodeField($codec, $jo, $f.getName(), $f, $x);
					}
				}
			} else {	// only annotated fields matching the selector
				for (Field $f : $class.getDeclaredFields()) {
					$f.setAccessible(true);
					int $mod = $f.getModifiers();
					if (Modifier.isStatic($mod)) continue;
					Enc $anno = $f.getAnnotation(Enc.class);
					if ($anno != null) {
						if (Arr.contains($anno.value(), $selector)) {
							if ($anno.key().isEmpty())
								$key = $f.getName();
							else $key = $anno.key(); 

							encodeField($codec, $jo, $key, $f, $x);
						}
					}
				}
			}
			
			return $jo;
		} catch (IllegalAccessException $e) {
			throw new UnencodableException("reflection problem",$e);
		}
	}
	
	private void encodeField(EonCodec $codec, EonObject $eo, String $key, Field $f, $T $x) throws TranslationException, IllegalAccessException {
		Class<?> $typo = $f.getType();
		// i wish you could do a switch on anything that acts like a pointer
		if ($typo == byte[].class)
			$eo.put($key, (byte[])$f.get($x));
		else if ($typo == double[].class)
			;//$eo.put($key, (SATAN)$value);	//TODO:AHS:CODEC: something with arrays
		else if ($typo == int[].class)
			;//$eo.put($key, (SATAN)$value);	//TODO:AHS:CODEC: something with arrays
		else if ($typo == long[].class)
			;//$eo.put($key, (SATAN)$value);	//TODO:AHS:CODEC: something with arrays
		else if ($typo == boolean.class)
			$eo.put($key, $f.getBoolean($x));
		else if ($typo == double.class)
			$eo.put($key, $f.getDouble($x));
		else if ($typo == int.class)
			$eo.put($key, $f.getInt($x));
		else if ($typo == long.class)
			$eo.put($key, $f.getLong($x));
		else if ($typo == String.class)
			$eo.put($key, (String)$f.get($x));
		// i suppose we could check here if the value is already an EonObject or EonArray, but in practice who would ever do that?
		//	if they really need to do it, someone could just put a no-op encoder in their codec for the requisite types.
		else
			$eo.put($key, $codec.encode($f.get($x)));
	}
}
