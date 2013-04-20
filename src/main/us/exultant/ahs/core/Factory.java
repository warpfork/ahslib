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

package us.exultant.ahs.core;

/**
 * <p>
 * A Factory generalizes the concept of a constructor and is a pattern that frequently
 * appears in software design. If you are not familiar with the pattern, the <a
 * href="https://secure.wikimedia.org/wikipedia/en/wiki/Factory_%28software_concept%29"
 * >wikipedia article</a> provides a decent introduction.
 * </p>
 * 
 * <p>
 * A Factory may have methods for customization (in addition to being inherently
 * customizable by nature of being interfaces).
 * </p>
 * 
 * <p>
 * A Factory instance's {@link #make()} method may be invoked any number of times.
 * Typically, this is expected to return a new object for every invocation, but
 * implementations may also take creative liberties such as pooling objects. (If you have
 * a situation where a customizable construction pathway pattern is appropriate, but you
 * only want to produce one object per Factory instance, please use {@link MonoFactory} to
 * express this.) In general, if a new object is not produced by every invocation of the
 * factory, this should be loudly documented.
 * </p>
 * 
 * <p>
 * A Factory instance's {@link #make()} method often turns out to be idempotent, but the
 * interface does not guarantee this (Factories that deal with object pooling for example
 * will typically not have this property). If your Factory implementation is idempotent or
 * otherwise thread-safe, do your fellow programmers a favor and document it as such.
 * </p>
 * 
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 * 
 * @param <$T>
 */
public interface Factory<$T> {
	public $T make();
}
