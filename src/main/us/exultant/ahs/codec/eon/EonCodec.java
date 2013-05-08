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
import us.exultant.ahs.codec.*;
import java.util.*;

/**
 * <p>
 * EonCodec is a codec that addresses any data model which is composed of maps and arrays
 * (which to be honest is just about everything you're likely to run across on any given
 * day, with the exception of the models used in relational databases). "Maps" are
 * provided by implementors of {@link EonObject}, and "Arrays" are provided by
 * implementors of {@link EonArray}.
 * </p>
 *
 * <p>
 * Any codec scheme with a concept of map can be expressed with the Eon* interfaces. In
 * particular, the AHS library provides implementations for the JSON and EBON schema,
 * giving developers a handy choice between a human-readable schema and a high-efficiency
 * length-delimited binary schema <i>that are completely interchangeable</i>.
 * </p>
 *
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 *
 */
public class EonCodec extends CodecImpl<EonCodec,EonObject> {
	/**
	 * Constructs a new EonCodec that contains no encode or decode hooks and uses the
	 * given factories to produce new empty objects and arrays for storage.
	 */
	public EonCodec(Factory<? extends EonObject> $objProvider, Factory<? extends EonArray> $arrProvider) {
		super();
		this.$objProvider = $objProvider;
		this.$arrProvider = $arrProvider;
	}

	/**
	 * Constructs a new EonCodec that contains no encode or decode hooks and using the
	 * same (pointer-equality!) factories to produce new empty objects and arrays for
	 * storage.
	 */
	public EonCodec(EonCodec $other) {
		super();
		this.$objProvider = $other.$objProvider;
		this.$arrProvider = $other.$arrProvider;
	}

	private final Factory<? extends EonObject>	$objProvider;
	private final Factory<? extends EonArray>	$arrProvider;

	/**
	 * <p>
	 * Serializes all the data in an EonObject (and all of its children recursively)
	 * into a byte array.
	 * </p>
	 *
	 * <p>
	 * (This is actually equivalent to calling <code>$eonObj.serialize();</code> since
	 * the role of the codec class is dealing with the formation of the EonObject and
	 * the disbatching of encoders; once the EonObject tree is fully formed, it is
	 * responsible for its own conversion to bytes.)
	 * </p>
	 *
	 * @throws TranslationException
	 */
	public static byte[] serialize(EonObject $eo) throws TranslationException {
		return $eo.serialize();
	}

	public <$TARG> byte[] serialize($TARG $datclr) throws TranslationException {
		return encode($datclr).serialize();
	}

	public <$TARG> byte[] serialize($TARG $datclr, Class<$TARG> $class) throws TranslationException {
		return encode($datclr, $class).serialize();
	}

	public EonObject deserialize(byte[] $bar) throws TranslationException {
		EonObject $eo = $objProvider.make();
		$eo.deserialize($bar);
		return $eo;
	}

	public <$TARG> $TARG deserialize(byte[] $bar, Class<$TARG> $datclrclass) throws TranslationException {
		return decode(deserialize($bar), $datclrclass);
	}





	public EonArray newArr() {
		return $arrProvider.make();
	}

	/** Creates a new "object" (essentially, a map) for encoding.  This returns the most specific type possible for a  */
	public EonObject newObj() {
		return $objProvider.make();
	}





	/** Helper method that encodes each element in a list as normal, then places it in an EonArray in the same order. */
	public <$TYPE> EonArray encodeList(List<$TYPE> $list) throws TranslationException {
		EonArray $ea = $arrProvider.make();
		int $size = $list.size();
		for (int $i = 0; $i < $size; $i++)
			$ea.put($i, this.encode($list.get($i)));
		return $ea;
	}

	/** Helper method that decodes each element in an EonArray, then returns it as an {@link ArrayList} in the same order. */
	public <$TYPE> List<$TYPE> decodeList(EonArray $ea, Class<$TYPE> $datclrclass) throws TranslationException {
		int $size = $ea.size();
		List<$TYPE> $v = new ArrayList<$TYPE>($size);
		for (int $i = 0; $i < $size; $i++)
			$v.add(this.decode($ea.getObj($i), $datclrclass));
		return $v;
	}





	/** Facade method for making very simple EonObjects in one line calls. */
	public EonObject simple(Object $class, String $name, EonObject $data) {
		EonObject $holder = $objProvider.make();
		if ($class != null) $holder.putKlass($class);
		if ($name != null)  $holder.putName($name);
		if ($data != null)  $holder.putData($data);
		return $holder;
	}
	/** Facade method for making very simple EonObjects in one line calls. */
	public EonObject simple(String $class, String $name, EonObject $data) {
		EonObject $holder = $objProvider.make();
		if ($class != null) $holder.putKlass($class);
		if ($name != null)  $holder.putName($name);
		if ($data != null)  $holder.putData($data);
		return $holder;
	}
	/** Facade method for making very simple EonObjects in one line calls. */
	public EonObject simple(Object $class, String $name, EonArray $data) {
		EonObject $holder = $objProvider.make();
		if ($class != null) $holder.putKlass($class);
		if ($name != null)  $holder.putName($name);
		if ($data != null)  $holder.putData($data);
		return $holder;
	}
	/** Facade method for making very simple EonObjects in one line calls. */
	public EonObject simple(String $class, String $name, EonArray $data) {
		EonObject $holder = $objProvider.make();
		if ($class != null) $holder.putKlass($class);
		if ($name != null)  $holder.putName($name);
		if ($data != null)  $holder.putData($data);
		return $holder;
	}
	/** Facade method for making very simple EonObjects in one line calls. */
	public EonObject simple(Object $class, String $name, String $data) {
		EonObject $holder = $objProvider.make();
		if ($class != null) $holder.putKlass($class);
		if ($name != null)  $holder.putName($name);
		if ($data != null)  $holder.putData($data);
		return $holder;
	}
	/** Facade method for making very simple EonObjects in one line calls. */
	public EonObject simple(String $class, String $name, String $data) {
		EonObject $holder = $objProvider.make();
		if ($class != null) $holder.putKlass($class);
		if ($name != null)  $holder.putName($name);
		if ($data != null)  $holder.putData($data);
		return $holder;
	}
	/** Facade method for making very simple EonObjects in one line calls. */
	public EonObject simple(Object $class, String $name, byte[] $data) {
		EonObject $holder = $objProvider.make();
		if ($class != null) $holder.putKlass($class);
		if ($name != null)  $holder.putName($name);
		if ($data != null)  $holder.putData($data);
		return $holder;
	}
	/** Facade method for making very simple EonObjects in one line calls. */
	public EonObject simple(String $class, String $name, byte[] $data) {
		EonObject $holder = $objProvider.make();
		if ($class != null) $holder.putKlass($class);
		if ($name != null)  $holder.putName($name);
		if ($data != null)  $holder.putData($data);
		return $holder;
	}
}
