/*
 * Copyright 2010, 2011 Eric Myhre <http://exultant.us>
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
import java.util.*;

/**
 * <p>
 * The EonObject type provides a single interface to the common computer-science concept
 * of a <i>map</i> from keys to values, aiming to provide this in the most general way
 * that is useful in constructing encoding and serialization. This abstract interface can
 * be implemented by any codec scheme with a concept of map; in particular, the AHS
 * library provides implementations for the JSON and EBON schema, giving developers a
 * handy choice between a human-readable schema and a high-efficiency length-delimited
 * binary schema <i>that are completely interchangeable</i>.
 * </p>
 * 
 * <p>
 * This interface provides conveniences specifically regarding features of the Java
 * language and methods to facilitate canonical usage patterns, but the core principles of
 * EonObject remain translatable to any reasonable language.
 * </p>
 * 
 * <p>
 * Support is provided for the following data types: string, int, long, double, boolean,
 * byte array &mdash; as well as {@link EonObject} and {@link EonArray} recursively.
 * Unsigned types are not allowed by EonObject, since this would violate the
 * least-common-denominator approach and can be highly unpleasant to deal with the
 * implications of in many languages.
 * </p>
 * 
 * <p>
 * Note the core philosophy regarding keys as strings. In some codec schemes (i.e. EBON)
 * this can seem a little strange since they're obsessed with binary efficiency, and if
 * people with binary keys have to Base64, well isn't that just horrid! However, in many
 * widely-used codec schemes (i.e. JSON, or XML, or YAML, or...), keys simply cannot be
 * binary. Thus, for a least-common-denominator approach to be effective, strings are the
 * only reasonable option for keys. (Also, it is the author's opinion that while
 * Base64'ing in general can be absolutely ghastly in high-performance systems, when
 * limited to keys that are generally both small and finite in length the overhead is
 * unlikely to be a reasonable target for optimization.)
 * </p>
 * 
 * <p>
 * Some specific implementations may enforce greater strictness on keys &mdash; for
 * example, the subset of strings that are valid keys (some XML schema would certainly do
 * this), or on the length of key (EBON does this at 65536 characters; the author notes
 * with some disapproval that some schemes go so far as to limit this to 255 characters,
 * though the AHSlib is not presently concerned with any of these). Thus, while it is true
 * that EonObject does not forbid any particular key strings, for the sake of portability
 * it generally remains in a developer's best interests to choose keys which are
 * alphanumeric and not of extreme length.
 * </p>
 * 
 * <p>
 * Some specific implementations may choose to perform some operations using forms other
 * than those requested. For example, attempts to put byte arrays in an EonObject
 * implementation for JSON may cause the implementation to choose to store the data in
 * Base64 form (such a change is mostly transparent under normal usage, but would have the
 * interesting side effect of allowing later requests for that field as a string to
 * proceed without producing the error condition one might otherwise expect). Similarly,
 * all of the standard caveats about portability of floating-point number precision apply.
 * </p>
 * 
 * <p>
 * Some specific implementations may choose to throw {@link UnencodableException} in
 * response to a request to put a value to a key to which a value has already been
 * assigned, in addition to the normal roles of that exception type.
 * </p>
 * 
 * <p>
 * Observe that method for retrieving data from an EonObject come in two flavors: "opt"
 * methods either return defaults or null in case of errors; their "get" brethren throw
 * exceptions if the requested value is missing or untranslatable. The cases in which the
 * "get"-flavor methods throw exceptions and the "opt"-flavor methods return nulls or
 * defaults are identical.
 * </p>
 * 
 * @see EonArray for the matching abstraction as applicable for arrays (as opposed to the
 *      maps provided here).
 * 
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 * 
 */
public interface EonObject {
	/**
	 * Asserts that there is a class string encoded in this EonObject (i.e. at the
	 * {@link Eon#MAGICWORD_CLASS} key) that matches the given object's class string
	 * (as defined by {@link Eon#getKlass(Object)}).
	 * 
	 * @param $x the object to compare to
	 * @throws TranslationException if there is not a match.
	 */
	public void    assertKlass(Object $x)   throws TranslationException;
	
	/**
	 * Asserts that there is a class string encoded in this EonObject (i.e. at the
	 * {@link Eon#MAGICWORD_CLASS} key) that matches the given class (as defined by
	 * {@link Eon#getKlass(Class)}).
	 * 
	 * @param $x the type to compare to
	 * @throws TranslationException if there is not a match.
	 */
	public void    assertKlass(Class<?> $x) throws TranslationException;
	
	/**
	 * Asserts that there is a class string encoded in this EonObject (i.e. at the
	 * {@link Eon#MAGICWORD_CLASS} key) that exactly matches the given string.
	 * 
	 * @param $x the string to compare to
	 * @throws TranslationException if there is not a match.
	 */
	public void    assertKlass(String $x)   throws TranslationException;
	
	/**
	 * Canonical/Convenience method (identical to
	 * <code>put(Eon.MAGICWORD_CLASS,Eon.getKlass($x))</code>).
	 * 
	 * @param $x an object whose type to encode as a class string
	 */
	public void    putKlass   (Object $x);
	
	/**
	 * Canonical/Convenience method (identical to
	 * <code>put(Eon.MAGICWORD_CLASS,Eon.getKlass($x))</code>).
	 * 
	 * @param $x the type to encode as a class string
	 */
	public void    putKlass   (Class<?> $x);
	
	/**
	 * Canonical/Convenience method (identical to
	 * <code>put(Eon.MAGICWORD_CLASS,$x)</code>).
	 * 
	 * @param $x the string to encode as a class string
	 */
	public void    putKlass   (String $x);
	
	/**
	 * Canonical/Convenience method (identical to
	 * <code>put(Eon.MAGICWORD_CLASS,$x)</code>).
	 * 
	 * @return the class string encoded in this EonObject, or null if there is none.
	 */
	public String  getKlass();
	
	
	
	/**
	 * Canonical/Convenience method (identical to
	 * <code>put(Eon.MAGICWORD_NAME,$x)</code>).
	 */
	public void    putName(String $x);
	
	/**
	 * Canonical/Convenience method (identical to
	 * <code>put(Eon.MAGICWORD_NAME,$x)</code>).
	 * 
	 * @return the string encoded as a name in this EonObject, or null if there is none.
	 */
	public String  getName() throws TranslationException;
	
	
	
	/**
	 * Canonical/Convenience method (identical to
	 * <code>put(Eon.MAGICWORD_DATA,$x)</code>).
	 */
	public void    putData(EonObject $x);
	
	/**
	 * Canonical/Convenience method (identical to
	 * <code>put(Eon.MAGICWORD_DATA,$x)</code>).
	 */
	public void    putData(EonArray $x);
	
	/**
	 * Canonical/Convenience method (identical to
	 * <code>put(Eon.MAGICWORD_DATA,$x)</code>).
	 */
	public void    putData(String $x);
	
	/**
	 * Canonical/Convenience method (identical to
	 * <code>put(Eon.MAGICWORD_DATA,$x)</code>).
	 */
	public void    putData(byte[] $x);
	
	/**
	 * Canonical/Convenience method (identical to
	 * <code>getObj(Eon.MAGICWORD_DATA)</code>).
	 * 
	 * @return the encoded data.
	 * @throws TranslationException if the data field does not contain an EonObject.
	 */
	public EonObject  getData      () throws TranslationException;
	
	/**
	 * Canonical/Convenience method (identical to
	 * <code>getArr(Eon.MAGICWORD_DATA)</code>).
	 * 
	 * @return the encoded data.
	 * @throws TranslationException if the data field does not contain an EonArray.
	 */
	public EonArray   getArrayData () throws TranslationException;
	
	/**
	 * Canonical/Convenience method (identical to
	 * <code>getString(Eon.MAGICWORD_DATA)</code>).
	 * 
	 * @return the encoded data.
	 * @throws TranslationException if the data field does not contain a String.
	 */
	public String  getStringData() throws TranslationException;
	
	/**
	 * Canonical/Convenience method (identical to
	 * <code>getBytes(Eon.MAGICWORD_DATA)</code>).
	 * 
	 * @return the encoded data.
	 * @throws TranslationException if the data field does not contain binary data.
	 */
	public byte[]  getByteData  () throws TranslationException;
	
	
	
	/**
	 * Returns true if this EonObject contains a mapping for the specified key.
	 * 
	 * @param $key key whose presence in this EonObject is to be tested 
	 * @return true if this map contains a field matching the specified key 
	 */
	public boolean has(String $key);
	
	/**
	 * Returns the number of fields encoded in this EonObject.
	 * 
	 * @return the number of fields encoded in this EonObject.
	 */
	public int     size();
	
	/**
	 * Allows access of this EonObject's keys and fields as a Set of Entry.
	 * 
	 * Whether or not the returned set is backed by the EonObject directly is not
	 * defined, so the sake of portability it is inadvisable to attempt to modify the
	 * set directly or to modify the EonObject while iterating over the set.
	 * 
	 * @return an entry set for this EonObject.
	 */
	public Set<Map.Entry<String,Object>> entrySet();
	
	
	
	
	/**
	 * Sets the field at the given key to the given value.
	 * @param $key key
	 * @param $val value
	 */
	public void    put(String $key, byte[] $val);
	
	/**
	 * Gets the value from the field at the given key.
	 * @param $key key
	 * @return the value
	 * @throws TranslationException if there was no appropriate data
	 */
	public byte[]  getBytes(String $key) throws TranslationException;
	
	/**
	 * Gets the value from the field at the given key if possible.
	 * @param $key key
	 * @return the value, or null if there was no appropriate data.
	 */
	public byte[]  optBytes(String $key);
	
	/**
	 * Gets the value from the field at the given key if possible.
	 * @param $key key
	 * @return the value, or the provided default value if there was no appropriate data.
	 */
	public byte[]  optBytes(String $key, byte[] $default);
	
	/**
	 * Sets the field at the given key to the given value.
	 * @param $key key
	 * @param $val value
	 */
	public void    put(String $key, boolean $val);
	
	/**
	 * Gets the value from the field at the given key.
	 * @param $key key
	 * @return the value
	 * @throws TranslationException if there was no appropriate data
	 */
	public boolean getBoolean(String $key) throws TranslationException;
	
	/**
	 * Gets the value from the field at the given key if possible.
	 * @param $key key
	 * @return the value, or the provided default value if there was no appropriate data.
	 */
	public boolean optBoolean(String $key, boolean $default);
	
	/**
	 * Sets the field at the given key to the given value.
	 * @param $key key
	 * @param $val value
	 * @throws UnencodableException if the value cannot be represented in this implementation of EonObject.
	 */
	public void    put(String $key, double $val) throws UnencodableException;
	
	/**
	 * Gets the value from the field at the given key.
	 * @param $key key
	 * @return the value
	 * @throws TranslationException if there was no appropriate data
	 */
	public double  getDouble(String $key)        throws TranslationException;
	
	/**
	 * Gets the value from the field at the given key if possible.
	 * @param $key key
	 * @return the value, or the provided default value if there was no appropriate data.
	 */
	public double  optDouble(String $key, double $default);
	
	/**
	 * Sets the field at the given key to the given value.
	 * @param $key key
	 * @param $val value
	 * @throws UnencodableException if the value cannot be represented in this implementation of EonObject.
	 */
	public void    put(String $key, int $val) throws UnencodableException;
	
	/**
	 * Gets the value from the field at the given key.
	 * @param $key key
	 * @return the value
	 * @throws TranslationException if there was no appropriate data
	 */
	public int     getInt(String $key)        throws TranslationException;
	
	/**
	 * Gets the value from the field at the given key if possible.
	 * @param $key key
	 * @return the value, or the provided default value if there was no appropriate data.
	 */
	public int     optInt(String $key, int $default);
	
	/**
	 * Sets the field at the given key to the given value.
	 * @param $key key
	 * @param $val value
	 * @throws UnencodableException if the value cannot be represented in this implementation of EonObject.
	 */
	public void    put(String $key, long $val) throws UnencodableException;
	
	/**
	 * Gets the value from the field at the given key.
	 * @param $key key
	 * @return the value
	 * @throws TranslationException if there was no appropriate data
	 */
	public long    getLong(String $key)        throws TranslationException;
	
	/**
	 * Gets the value from the field at the given key if possible.
	 * @param $key key
	 * @return the value, or the provided default value if there was no appropriate data.
	 */
	public long    optLong(String $key, long $default);
	
	/**
	 * Sets the field at the given key to the given value.
	 * @param $key key
	 * @param $val value
	 */
	public void    put(String $key, String $val);
	
	/**
	 * Gets the value from the field at the given key.
	 * @param $key key
	 * @return the value
	 * @throws TranslationException if there was no appropriate data
	 */
	public String  getString(String $key) throws TranslationException;
	
	/**
	 * Gets the value from the field at the given key if possible.
	 * @param $key key
	 * @return the value, or null if there was no appropriate data.
	 */
	public String  optString(String $key);
	
	/**
	 * Gets the value from the field at the given key if possible.
	 * @param $key key
	 * @return the value, or the provided default value if there was no appropriate data.
	 */
	public String  optString(String $key, String $default);
	
	/**
	 * Sets the field at the given key to the given value.
	 * @param $key key
	 * @param $val value
	 */
	public void    put(String $key, EonObject $val);
	
	/**
	 * Gets the value from the field at the given key.
	 * @param $key key
	 * @return the value
	 * @throws TranslationException if there was no appropriate data
	 */
	public EonObject   getObj(String $key) throws TranslationException;
	
	/**
	 * Gets the value from the field at the given key if possible.
	 * @param $key key
	 * @return the value, or null if there was no appropriate data.
	 */
	public EonObject   optObj(String $key);
	
	/**
	 * Sets the field at the given key to the given value.
	 * @param $key key
	 * @param $val value
	 */
	public void    put(String $key, EonArray $val);
	
	/**
	 * Gets the value from the field at the given key.
	 * @param $key key
	 * @return the value
	 * @throws TranslationException if there was no appropriate data
	 */
	public EonArray   getArr(String $key) throws TranslationException;
	
	/**
	 * Gets the value from the field at the given key if possible.
	 * @param $key key
	 * @return the value, or null if there was no appropriate data.
	 */
	public EonArray   optArr(String $key);
	
	
	
	/**
	 * <p>
	 * Produces a byte array which contains the serialized representation of all the
	 * data in an EonObject.
	 * </p>
	 * 
	 * <p>
	 * Note that in implementations that are not binary, this tends to be a UTF-8
	 * string.
	 * </p>
	 * 
	 * @return a byte array which contains the serialized representation of an
	 *         EonObject
	 * @throws TranslationException
	 *                 in case of unserializable data or other extreme surprises.
	 *                 Generally, this should not happen, but some implementations may
	 *                 accept a wider range of input when deserializing than they will
	 *                 output when serializing, which leaves a space where the object
	 *                 may contain data which is more or less valid but nonetheless
	 *                 not serializable.
	 */
	public byte[] serialize() throws TranslationException;
	
	/**
	 * <p>
	 * Fills an empty EonObject with the data produced by deserializing the given byte
	 * array.
	 * </p>
	 * 
	 * <p>
	 * If used on an EonObject that already contains some data, the result is not
	 * defined and exceptions may be thrown.
	 * </p>
	 * 
	 * @param $bats
	 *                a byte array which contains the serialized representation of an
	 *                EonObject. Note that in implementations that are not binary,
	 *                this tends to be a UTF-8 string.
	 * @throws TranslationException
	 *                 if any part of decoding process encounters data that is not
	 *                 valid for the schema.
	 */
	public void deserialize(byte[] $bats) throws TranslationException;
	
	
	
	
	
	/**
	 * This adapter provides wrappers around most of the basic methods that provide
	 * "opt"-like functionality, but still leaves the basic data-oriented methods to
	 * be implemented by concrete subclasses.
	 * 
	 * @author Eric Myhre <tt>hash@exultant.us</tt>
	 */
	public abstract static class Adapter {
		//TODO:AHS:CODEC: omg really.  this would make so much json stuff SO much more clear, to say nothing of consistent across other platforms to come.
	}
}
