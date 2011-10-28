/*
 * Copyright 2010, 2011 Eric Myhre <http://exultant.us>
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
 * Maintains a pool of "canonical" objects; similar to a generalized version of
 * {@link String#intern()}.
 * </p>
 * 
 * <p>
 * This implementation differs markedly from the pattern of {@link String#intern()} in
 * that it is not necessary to intern a piece of data merely to check if it is already
 * interned &mdash; this has the highly useful implication that it is possible to use this
 * on arbitrary input data, since the size of permanently used memory will not grow unless
 * you explicitly ask it to.
 * </p>
 * 
 * <p>
 * One of the most <i>common</i> use cases for this functionality is checking if some
 * input data is of in a set of things known to be interesting. That is, the set of
 * interesting things is first {@link #intern(Object)}'d, and then all future input is
 * checked against {@link #optIntern(Object)}. Data that does not match is discarded, and
 * data that does match can thereafter be handled with the efficiency bonus of only ever
 * needing pointer-equality for comparison in the future (or even further, the cannonical
 * types might have additional fields that were not part of the
 * {@link Object#equals(Object)} that provide more handy presets, like say an enum that
 * can be switch'd on).
 * </p>
 * 
 * <p>
 * The <i>general</i> use case for this is almost exactly the same as
 * {@link String#intern()} &mdash; it gives you the power to use extremely fast pointer
 * equality checks to compare objects that have been intern'd instead of using
 * {@link Object#equals(Object)}, which may be an extremely nontrivial performance
 * degradation for sufficiently heavy objects (as an (extremely) general rule, the cost of
 * {@link Object#equals(Object)} tends to be more or less linear in the memory-size of the
 * objects). Of course, as noted earlier, the ability to choose whether a piece of data
 * should be interned (and grow the size of permanent memory) or whether merely checking
 * for cannonicalizability is sufficient makes the potential range of uses much less
 * restricted than {@link String#intern()}.
 * </p>
 * 
 * <h3>A fully worked example:</h3>
 * 
 * <p>
 * The specifics of exactly how useful this will be in some situations can be more complex
 * than readily visible at first glance. For example, suppose you have a HashMap that maps
 * a String representing some sort of "event type" to a "listener". If all of the
 * locations which refer to this "event type" String are specified explicitly in the
 * source (i.e., are string literals), then those have already been interned by the JVM,
 * and the equals method employed internally by the HashMap will always turn out to
 * short-circut to pointer equality when finding a matching string... which is better than
 * you might have expected, and pretty great if your HashMap actually turns out to be
 * underloaded and balanced enough that you've usually got one or zero entries per bucket.
 * </p>
 * 
 * <p>
 * However, in the case of multiple entries in a bucket of the HashMap, or a request for a
 * string that isn't even in the map but still maps to a nonempty bucket, the automatic
 * interning of the strings by the JVM doesn't turn out to save you... a full O(n)
 * equality check is still required on any strings in the target hash bucket that aren't
 * equal to the requested string.
 * </p>
 * 
 * <p>
 * This example demonstrates the somewhat bothersome fact that members of Java Collections
 * Framework tend not to be able to make as much use of the functionality that can be
 * established with Intern unless the {@link Object#equals(Object)} method is nothing but
 * a pointer equality check. There are two ways around this to effect the most complete
 * possible speed-up of the HashMap:
 * <ol>
 * <li>use some placeholder or decorator around the key which implements a strawman
 * {@link Object#equals(Object)} method that returns nothing but the results of a pointer
 * equality check.
 * <li>change the {@link Object#equals(Object)} of the type to be nothing but pointer
 * equals, then use an Intern instance produced with a Comparator that implements the more
 * complete concept of equality. This option will have better performance than the
 * previous and save you from the inconvenince of fiddling with wrapper objects in all
 * your calls, but is only possible when {@link Object#equals(Object)} checks nothing but
 * pointer equality.
 * </ol>
 * </p>
 * 
 * <h3>A note on threading and multiple instances of Intern:</h3>
 * 
 * <p>
 * Intern instances are NOT thread-safe in any way. If you want thread-safe interning, it
 * can none the less be done, and even without synchronization(!). As long as you set up
 * one instance of Intern per thread and do all the same interning (with the same objects)
 * before putting those threads to work (and the "work" only includes optInterning),
 * everything works out fine: you get the same concept of interned objects across all the
 * threads with zero synchronization overhead.
 * </p>
 * 
 * @author hash
 * 
 * @param <$T>
 */
public class Intern<$T> {
	/**
	 * Produces a new, empty Intern. The {@link Object#equals(Object)} method will be
	 * used to determine the matching of all interned objects.
	 */
	public Intern() {
		this((Comparator<$T>)null);
	}
	
	/**
	 * Beware that when using this form, a LOT of highly detailed assumptions are made
	 * about the nature of equality check invocations and orderings in the
	 * {@link HashMap} class. It is not inconcievable that a JVM using a standard
	 * library other than Sun's will trip.
	 * 
	 * @param $comp
	 */
	public Intern(Comparator<$T> $comp) {
		$map = new HashMap<Object,$T>();
		this.$comp = $comp;
	}
	
	/**
	 * Copy constructor. All interned objects are copied, as is the comparator if any
	 * (which implies that comparators are expected to be reentrant).
	 * 
	 * @param $toCopy
	 */
	public Intern(Intern<$T> $toCopy) {
		this.$map = new HashMap<Object,$T>($toCopy.$map);
		this.$comp = $toCopy.$comp;
	}
	
	/**
	 * Does it seem somewhat oddly inefficient to store a pointer to a thing indexed
	 * by itself? Well, perhaps at first glance. But this is exactly the same kind of
	 * abstraction used in the core library's {@link HashSet} implementation as well.
	 */
	protected final HashMap<Object,$T> $map;
	
	/**
	 * If set, this causes a complicated idea to come into swing. We wrap an incoming
	 * $x in something with a snazzy equals method based on this and use that on our
	 * internal map. This lets us make the radical improvement of letting $T have a
	 * pointer-equality-only equals method for use everywhere else and get that effic
	 * bonus real smooth even in std Collections stuff, while still using arbitrarily
	 * complex concepts of equality when decided what constitutes a match to canonical
	 * objects. On the other hand, we end up really doing terrifying stuff with the
	 * guts of maps to make it work internally.
	 */
	private final Comparator<$T> $comp;
	
	/**
	 * If something {@link Object#equals(Object)} to <tt>$x</tt> is not yet interned
	 * here, then <tt>$x</tt> is interned and returned; else the already interned
	 * object is returned.
	 * 
	 * @param $x
	 *                an object to return a cannonical reference to.
	 * @return an object that is equal to the given object (and possibly the
	 *         same/pointer-equal object), guaranteed to be the same/pointer-equal
	 *         object returned from {@link #intern(Object)} and
	 *         {@link #optIntern(Object)} in future invocations.
	 */
	public $T intern($T $x) {
		if ($comp == null) {
			$T $v = $map.get($x);
			if ($v == null) {
				$map.put($x,$x);
				return $x;
			}
			return $v;
		} else {
			$T $v = $map.get($gw.wrap($x));
			if ($v == null) {
				$map.put(new PutWrapper($x),$x);
				return $x;
			}
			return $v;
		}
	}
	
	/**
	 * If something {@link Object#equals(Object)} to <tt>$x</tt> is already interned
	 * here, then that already interned object is returned; else null is returned.
	 * 
	 * @param $x
	 *                an object to return a cannonical reference to.
	 * @return an object that is equal to the given object (and possibly the
	 *         same/pointer-equal object), guaranteed to be the same/pointer-equal
	 *         object returned from {@link #intern(Object)} and
	 *         {@link #optIntern(Object)} in future invocations.
	 */
	public $T optIntern($T $x) {
		if ($comp == null)
			return $map.get($x);
		else
			return $map.get($gw.wrap($x));
	}
	
	// i cannot think of a situation where you could actually reasonably use this without massive peril of driving yourself insane.
//	public $T unintern($T $x) {
//		return $map.remove($x);
//	}
	
	private class PutWrapper {
		public PutWrapper($T $x) { if ($x == null) throw new NullPointerException(); this.$x = $x; }
		private final $T $x;
		public int hashCode() { return $x.hashCode(); }
		@SuppressWarnings("unchecked")	// this is checked properly at runtime (as long as no one makes a subclass that fucks with the invarients of the protected fields).  it's just hard for the compiler to see it because of the generic erasure.
		public boolean equals(Object $o) {
			$T $t = ((PutWrapper)$o).$x;
			return ($comp.compare($x, $t) == 0);
		}
	}
	private final GetWrapper $gw = new GetWrapper();	// i do NOT want to have to gc one of these for every getting operation.
	private class GetWrapper {
		public GetWrapper wrap($T $x) { if ($x == null) throw new NullPointerException(); this.$x = $x; return this; }	// not thread safe.  neither is the hashmap.
		private $T $x;
		public int hashCode() { return $x.hashCode(); }
		@SuppressWarnings("unchecked")	// this is checked properly at runtime (as long as no one makes a subclass that fucks with the invarients of the protected fields).  it's just hard for the compiler to see it because of the generic erasure.
		public boolean equals(Object $o) {
			$T $t = ((PutWrapper)$o).$x;
			return ($comp.compare($x, $t) == 0);
		}
	}
	
	public static class Strings extends Intern<String> {
		/**
		 * If something {@link Object#equals(Object)} to <tt>$x</tt> is not yet
		 * interned here, then the result of interning <tt>$x</tt> in the global
		 * string pool (i.e. via {@link String#intern()}) is interned locally and
		 * returned; else the already interned object is returned.
		 * 
		 * @param $x
		 *                an object to return a cannonical reference to.
		 * @return an object that is equal to the given object (and possibly the
		 *         same/pointer-equal object), guaranteed to be the
		 *         same/pointer-equal object returned from {@link #intern(Object)}
		 *         and {@link #optIntern(Object)} in future invocations and also
		 *         the same/pointer-equal string as that is interned in the global
		 *         string pool.
		 */
		public String intern(String $x) {
			String $v = $map.get($x);
			if ($v == null) {
				$x = $x.intern();
				$map.put($x,$x);
				return $x;
			}
			return $v;
		}
	}
}
 