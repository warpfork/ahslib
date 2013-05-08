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

/**
 * <p>
 * The EonArray type provides a single interface to the common computer-science concept of
 * a <i>array</i> of zero-indexed values, aiming to provide this in the most general way
 * that is useful in constructing encoding and serialization. This abstract interface can
 * be implemented by any codec scheme with a concept of arrays; in particular, the AHS
 * library provides implementations for the JSON and EBON schema, giving developers a
 * handy choice between a human-readable schema and a high-efficiency length-delimited
 * binary schema <i>that are completely interchangeable</i>.
 * </p>
 *
 * <p>
 * This interface provides conveniences specifically regarding features of the Java
 * language and methods to facilitate canonical usage patterns, but the core principles of
 * EonArray remain translatable to any reasonable language.
 * </p>
 *
 * <p>
 * Support is provided for the following data types: string, int, long, double, boolean,
 * byte array &mdash; as well as {@link EonObject} and {@link EonArray} recursively.
 * Unsigned types are not allowed by EonArray, since this would violate the
 * least-common-denominator approach and can be highly unpleasant to deal with the
 * implications of in many languages.
 * </p>
 *
 * <p>
 * Some specific implementations may choose to perform some operations using forms other
 * than those requested. For example, attempts to put byte arrays in an EonArray
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
 * Observe that method for retrieving data from an EonArray come in two flavors: "opt"
 * methods either return defaults or null in case of errors; their "get" brethren throw
 * exceptions if the requested value is missing or untranslatable. The cases in which the
 * "get"-flavor methods throw exceptions and the "opt"-flavor methods return nulls or
 * defaults are identical.
 * </p>
 *
 * @see EonObject for the matching abstraction as applicable for maps (as opposed to the
 *      arrays provided here).
 */
public interface EonArray {
	/**
	 * Returns the number of fields encoded in this EonArray.
	 *
	 * @return the number of fields encoded in this EonArray.
	 */
	public int     size();



	/**
	 * Sets the field at the given index to the given value.
	 * @param $index key
	 * @param $val value
	 */
	public void    put(int $index, byte[] $val);

	/**
	 * Gets the value from the field at the given index.
	 * @param $index index
	 * @return the value
	 * @throws TranslationException if there was no appropriate data
	 */
	public byte[]  getBytes(int $index) throws TranslationException;

	/**
	 * Gets the value from the field at the given index if possible.
	 * @param $index index
	 * @return the value, or null if there was no appropriate data.
	 */
	public byte[]  optBytes(int $index);

	/**
	 * Gets the value from the field at the given index if possible.
	 * @param $index index
	 * @return the value, or the provided default value if there was no appropriate data.
	 */
	public byte[]  optBytes(int $index, byte[] $default);

	/**
	 * Sets the field at the given index to the given value.
	 * @param $index key
	 * @param $val value
	 */
	public void    put(int $index, boolean $val);

	/**
	 * Gets the value from the field at the given index.
	 * @param $index index
	 * @return the value
	 * @throws TranslationException if there was no appropriate data
	 */
	public boolean getBoolean(int $index) throws TranslationException;

	/**
	 * Gets the value from the field at the given index if possible.
	 * @param $index index
	 * @return the value, or the provided default value if there was no appropriate data.
	 */
	public boolean optBoolean(int $index, boolean $default);

	/**
	 * Sets the field at the given index to the given value.
	 * @param $index key
	 * @param $val value
	 * @throws UnencodableException if the value cannot be represented in this implementation of EonArray.
	 */
	public void    put(int $index, double $val) throws UnencodableException;

	/**
	 * Gets the value from the field at the given index.
	 * @param $index index
	 * @return the value
	 * @throws TranslationException if there was no appropriate data
	 */
	public double  getDouble(int $index)        throws TranslationException;

	/**
	 * Gets the value from the field at the given index if possible.
	 * @param $index index
	 * @return the value, or the provided default value if there was no appropriate data.
	 */
	public double  optDouble(int $index, double $default);

	/**
	 * Sets the field at the given index to the given value.
	 * @param $index key
	 * @param $val value
	 * @throws UnencodableException if the value cannot be represented in this implementation of EonArray.
	 */
	public void    put(int $index, int $val) throws UnencodableException;

	/**
	 * Gets the value from the field at the given index.
	 * @param $index index
	 * @return the value
	 * @throws TranslationException if there was no appropriate data
	 */
	public int     getInt(int $index)        throws TranslationException;

	/**
	 * Gets the value from the field at the given index if possible.
	 * @param $index index
	 * @return the value, or the provided default value if there was no appropriate data.
	 */
	public int     optInt(int $index, int $default);

	/**
	 * Sets the field at the given index to the given value.
	 * @param $index key
	 * @param $val value
	 * @throws UnencodableException if the value cannot be represented in this implementation of EonArray.
	 */
	public void    put(int $index, long $val) throws UnencodableException;

	/**
	 * Gets the value from the field at the given index.
	 * @param $index index
	 * @return the value
	 * @throws TranslationException if there was no appropriate data
	 */
	public long    getLong(int $index)        throws TranslationException;

	/**
	 * Gets the value from the field at the given index if possible.
	 * @param $index index
	 * @return the value, or the provided default value if there was no appropriate data.
	 */
	public long    optLong(int $index, long $default);

	/**
	 * Sets the field at the given index to the given value.
	 * @param $index key
	 * @param $val value
	 */
	public void    put(int $index, String $val);

	/**
	 * Gets the value from the field at the given index.
	 * @param $index index
	 * @return the value
	 * @throws TranslationException if there was no appropriate data
	 */
	public String  getString(int $index) throws TranslationException;

	/**
	 * Gets the value from the field at the given index if possible.
	 * @param $index index
	 * @return the value, or null if there was no appropriate data.
	 */
	public String  optString(int $index);

	/**
	 * Gets the value from the field at the given index if possible.
	 * @param $index index
	 * @return the value, or the provided default value if there was no appropriate data.
	 */
	public String  optString(int $index, String $default);

	/**
	 * Sets the field at the given index to the given value.
	 * @param $index key
	 * @param $val value
	 */
	public void    put(int $index, EonObject $val);

	/**
	 * Gets the value from the field at the given index.
	 * @param $index index
	 * @return the value
	 * @throws TranslationException if there was no appropriate data
	 */
	public EonObject   getObj(int $index) throws TranslationException;

	/**
	 * Gets the value from the field at the given index if possible.
	 * @param $index index
	 * @return the value, or null if there was no appropriate data.
	 */
	public EonObject   optObj(int $index);

	/**
	 * Sets the field at the given index to the given value.
	 * @param $index key
	 * @param $val value
	 */
	public void    put(int $index, EonArray $val);

	/**
	 * Gets the value from the field at the given index.
	 * @param $index index
	 * @return the value
	 * @throws TranslationException if there was no appropriate data
	 */
	public EonArray   getArr(int $index) throws TranslationException;

	/**
	 * Gets the value from the field at the given index if possible.
	 * @param $index index
	 * @return the value, or null if there was no appropriate data.
	 */
	public EonArray   optArr(int $index);



	// this does indeed neglect to mention serialize and deserialize methods -- you're always expected to use an object at the topmost level, because really, if you needed nothing but arrays then what are you using such a powerful suite of tools for?
}
