/*
 * Copyright 2010 - 2013 Eric Myhre <http://exultant.us>
 *
 * This file is part of AHSlib.
 *
 * AHSlib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License, or
 * (at the original copyright holder's option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package us.exultant.ahs.util;

import java.util.*;

/**
 * <p>
 * A typed n-tuple &mdash; can contain as many objects as you like, and you get them back
 * (type-safely!) by asking for them by type.
 * </p>
 *
 * <p>
 * If you use the nullary constructor, you're allowed to add elements. If you use one of
 * the constructors with arguments, the tuple will be immutable.
 * </p>
 *
 * <p>
 * Note that in some ways this is a little weaker than the kind of type safety you can get
 * using, say, {@link Tup2}... with this, there's no type signature on the tuple itself
 * than can make guarantees for you about the contents. On the other hand, using this is a
 * helluva lot less verbose, and you're not stuck with crappy uninformative method names
 * like "getA()"; instead you have the much more readable "get(ReadableByteChannel.class)"
 * for example.
 * </p>
 *
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 *
 */
public class TupleN {
	public TupleN() {
		$data = new HashMap<Class<?>, Object>();
		$immutable = false;
	}

	public TupleN(Object... $objects) {
		$data = new HashMap<Class<?>, Object>();
		$immutable = true;
		for (Object $o : $objects)
			$data.put($o.getClass(), $o);
	}

	private final Map<Class<?>,Object>	$data;
	private final boolean			$immutable;

	@SuppressWarnings("unchecked") // totally safe at runtime! :D
	public <$T> $T get(Class<$T> $type) {
		return ($T) $data.get($type);
	}

	@SuppressWarnings("unchecked") // totally safe at runtime! :D
	public <$T> $T put(Class<$T> $type, $T $object) {
		if ($immutable) return null;
		return ($T) $data.put($type, $object);
	}



	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.$data == null) ? 0 : this.$data.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		TupleN other = (TupleN) obj;
		if (this.$data == null) {
			if (other.$data != null) return false;
		} else if (!this.$data.equals(other.$data)) return false;
		return true;
	}
}
