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
import us.exultant.ahs.util.*;
import us.exultant.ahs.codec.*;
import java.lang.reflect.*;

/**
 * <p>
 * Note that it's impossible for this class to provide any sort of validation that
 * the object it returns obeys any sort of invariants, since it operates entirely
 * through mindless reflection instead of any controlled pattern of constructors
 * or factory methods. In particular, it's trivially possible for the encoded form
 * to have been modified to, for example, exclude fields (which will likely result
 * in the decoded object having unexpected null values), or modify values to
 * invalid combinations.
 * </p>
 * 
 * <p>
 * It's also impossible to use one instance of this class a decoder for multiple
 * classes, unfortunately -- note the constructor.
 * </p>
 */
public class EonRAD<$T> implements Decoder<EonCodec,EonObject,$T> {
	/**
	 * <p>
	 * This constructor is awkward and somewhat redundant-sounding, but
	 * unfortunately there's no other way to get a reference to the Class<?>
	 * object for the generic type, and that reference is needed at runtime
	 * for critical reflection operations.
	 * </p>
	 * 
	 * @throws UnencodableException
	 *                 if the class is not annotated with the Encodable
	 *                 interface or otherwise not a suitable decode target.
	 */
	public EonRAD(Class<$T> $class, String $selector) throws UnencodableException {
		this.$class = $class;
		
		// check if the class will allow itself to be dencoded like this
		if ($class.isAnnotation() || $class.isInterface() || $class.isAnonymousClass())
			throw new UnencodableException("Interfaces, anonymous classes, and annotations can not be a decode target -- such magic is impossible.");
		Encodable $cenc = $class.getAnnotation(Encodable.class);
		if ($cenc == null)
			throw new UnencodableException("Class to be decoded must be annotated with the @Encodable interface.");
		
		this.$selector = $selector;
	}
	public EonRAD(Class<$T> $class) throws UnencodableException {
		this($class, Encodable.DEFAULT);
	}
	
	private Class<$T> $class;
	private String $selector;
	
	public $T decode(EonCodec $codec, EonObject $jo) throws TranslationException {
		String $key;
		
		// also, check if that class should have a name including in its encoding
		// make assertions for sanity if it does
		Encodable $cenc = $class.getAnnotation(Encodable.class);
		$key = $cenc.value();
		if ($key.equals(Encodable.NONE))
			; /* no checks */
		else if ($key.equals(Encodable.DEFAULT))
			$jo.assertKlass($class);
		else
			$jo.assertKlass($key);

		try {
			// create a new blank instance of the object to be returned
			$T $x;
			try {
				Constructor<$T> $con = $class.getDeclaredConstructor(Encodable.class);
				$con.setAccessible(true);
				$x = $con.newInstance((Encodable)null);
			} catch (NoSuchMethodException $e) {
				throw new UnencodableException("reflection problem: a (no-op) constructor accepting a single Encodable argument must be available.",$e);
			} catch (InvocationTargetException $e) {
				throw new UnencodableException("reflection problem: a constructor threw exception.",$e);
			}
			
			// walk across fields and deserialize the non-static ones
			if ($cenc.all_fields()) {	// all of them, regardless of whether that particular field is annotated
				for (Field $f : $class.getDeclaredFields()) {
					$f.setAccessible(true);
					int $mod = $f.getModifiers();
					if (Modifier.isStatic($mod)) continue;
					Enc $anno = $f.getAnnotation(Enc.class);
					if ($anno != null) {
						if ($anno.value().isEmpty())
							$key = $f.getName();
						else $key = $anno.value(); 
						
						decodeField($codec, $jo, $key, $f, $x);
					} else {
						decodeField($codec, $jo, $f.getName(), $f, $x);
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

							decodeField($codec, $jo, $key, $f, $x);
						}
					}
				}
			}
			
			return $x;
		} catch (InstantiationException $e) {
			throw new UnencodableException("reflection problem",$e);
		} catch (IllegalAccessException $e) {
			throw new UnencodableException("reflection problem",$e);
		}
	}
	
	private void decodeField(EonCodec $codec, EonObject $eo, String $key, Field $f, $T $x) throws IllegalAccessException, TranslationException {
		Class<?> $typo = $f.getType();
		// i wish you could do a switch on anything that acts like a pointer
		if ($typo == byte[].class)
			$f.set($x, $eo.getBytes($key));
		else if ($typo == double[].class)
			throw new NotYetImplementedException();//$eo.put($key, (SATAN)$value);	//TODO:AHS:CODEC: something with arrays
		else if ($typo == int[].class)
			throw new NotYetImplementedException();//$eo.put($key, (SATAN)$value);	//TODO:AHS:CODEC: something with arrays
		else if ($typo == long[].class)
			throw new NotYetImplementedException();//$eo.put($key, (SATAN)$value);	//TODO:AHS:CODEC: something with arrays
		else if ($typo == boolean.class)
			$f.set($x, $eo.getBoolean($key));
		else if ($typo == double.class)
			$f.set($x, $eo.getDouble($key));
		else if ($typo == int.class)
			$f.set($x, $eo.getInt($key));
		else if ($typo == long.class)
			$f.setLong($x, $eo.getLong($key));
		else if ($typo == String.class)
			$f.set($x, $eo.getString($key));
		// i suppose we could check here if the value is already an EonObject or EonArray, but in practice who would ever do that?
		//	if they really need to do it, someone could just put a no-op encoder in their codec for the requisite types.
		else
			$f.set($x, $codec.decode($eo.getObj($key), $typo));
	}
}
