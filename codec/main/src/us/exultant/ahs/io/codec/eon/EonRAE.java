package us.exultant.ahs.io.codec.eon;

import us.exultant.ahs.io.*;
import us.exultant.ahs.io.codec.*;
import us.exultant.ahs.util.*;

import java.lang.reflect.*;

/**
 * Always respects the value given for classname encoding preferences as given in the
 * Encodable annotation.
 * 
 * @param <$T>
 */
public class EonRAE<$T> implements Encoder<EonCodec,EonObject,$T> {
	public EonRAE(Class<$T> $class) throws UnencodableException {
		this($class, Encodable.DEFAULT);
	}
	public EonRAE(Class<$T> $class, String $selector) throws UnencodableException {
		this.$class = $class;
		this.$selector = $selector;
		
		// pick out and put in the semblance of a class name we want
		// also, check if that class will allow itself to be encoded like this
		Encodable $cenc = $class.getAnnotation(Encodable.class);
		if ($cenc == null)
			throw new UnencodableException("Class to be encoded must be annotated with the @Encodable interface.");
		else {
			if (!Arr.contains($cenc.styles(), $selector))
				throw new UnencodableException("Class to be encoded must be annotated to accept the style that this Encoder is configured for (selected=\""+$selector+"\", accept="+Arr.toString($cenc.styles())+").");
			
			String $key = $cenc.value();
			if ($key.equals(Encodable.NONE))
				$classname = "";
			else if ($key.equals(Encodable.DEFAULT))
				$classname = null;
			else
				$classname = $key;
		}

		// and finally check if we're just supposed to use the "all fields" shortcut
		$allFields = $cenc.all_fields();
	}
	
	private final Class<$T>	$class;
	private final String	$selector;
	private final String	$classname;
	private final boolean	$allFields;
	
	public EonObject encode(EonCodec $codec, $T $x) throws TranslationException {
		try {
			EonObject $jo = $codec.newObj();
			String $key;
			
			// put in the appropriate class name tag
			if ($classname == null) {
				$jo.putKlass($x);
			} else if ($classname == "") {
				; /* nothing */
			} else {
				$jo.putKlass($classname);
			}
			
			// walk across fields and serialize the non-static ones
			if ($allFields) {	// all of them, regardless of whether that particular field is annotated
				for (Field $f : $class.getDeclaredFields()) {
					$f.setAccessible(true);
					int $mod = $f.getModifiers();
					if (Modifier.isStatic($mod)) continue;
					Enc $anno = $f.getAnnotation(Enc.class);
					if ($anno != null) {
						if ($anno.value().isEmpty())
							$key = $f.getName();
						else $key = $anno.value(); 
						
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
						if (Arr.contains($anno.selected(), $selector)) {
							if ($anno.value().isEmpty())
								$key = $f.getName();
							else $key = $anno.value(); 

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
			throw new ImBored();//$eo.put($key, (SATAN)$value);	//TODO:AHS:CODEC: something with arrays
		else if ($typo == int[].class)
			throw new ImBored();//$eo.put($key, (SATAN)$value);	//TODO:AHS:CODEC: something with arrays
		else if ($typo == long[].class)
			throw new ImBored();//$eo.put($key, (SATAN)$value);	//TODO:AHS:CODEC: something with arrays
		//NOTE: Arrays.deepToString is an example of how to treat arbitrarily typed primitive arrays properly
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
