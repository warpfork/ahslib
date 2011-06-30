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
 * equal to the requested string. In this example situation, in order to effect the most
 * complete possible speed-up of the HashMap, you would have to use some placeholder or
 * decorator around the key which implements a strawman {@link Object#equals(Object)}
 * method that returns nothing but the results of a pointer equality check.
 * </p>
 * 
 * @author hash
 * 
 * @param <$T>
 */

//* <p>
//* (This example demonstrates the somewhat bothersome fact that members of Java
//* Collections Framework tend not to be able to make as much use of the functionality that
//* can be established with Intern unless the {@link Object#equals(Object)} method is
//* nothing but a pointer equality check, which can be inconvenient if the key type isn't
//* one within your own code base.)
//* </p>
// once you finish this, also don't forget to add to the other doc about and say that decorators not the only way.
public class Intern<$T> {
	public Intern() {
		$map = new HashMap<Object,$T>();
	}
//	public Intern(Comparator<$T> $comp) {
//		$map = new HashMap<$T,$T>();
//		this.$comp = $comp;
//	}
	
	/**
	 * Does it seem somewhat oddly inefficient to store a pointer to a thing indexed
	 * by itself? Well, perhaps at first glance. But this is exactly the same kind of
	 * abstraction used in the core library's {@link HashSet} implementation as well.
	 */
	protected final HashMap<Object,$T> $map;
	
//	private final Comparator<$T> $comp;
	// so, this is a complicated idea
	// we can wrap an incoming $x in something with a snazzy equals method based on this and use that on our internal map
	//  which would have the radical improvement of letting $T have a peq equals method for use everywhere else and get that effic bonus real smooth even in std Collections stuff
	// this really gets into the guts of map implementations though.  we're basically relying on situations where x.equals(y) not necessarily iff y.equals(x).
	
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
		$T $v = $map.get($x);
		if ($v == null) {
			// in a perfect world, i'd rather have a putIfEmpty function that only traversed the hashmap once,
			//  but then again since the puts are supposed to be rare i guess it's pretty meh.
			$map.put($x,$x);
			return $x;
		}
		return $v;
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
		return $map.get($x);
	}
	
	// i cannot think of a situation where you could actually reasonably use this without massive peril of driving yourself insane.
//	public $T unintern($T $x) {
//		return $map.remove($x);
//	}
	
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
 