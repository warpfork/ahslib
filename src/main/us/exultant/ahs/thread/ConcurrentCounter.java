package us.exultant.ahs.thread;

import us.exultant.ahs.core.*;
import us.exultant.ahs.util.*;
import java.util.*;
import java.util.concurrent.atomic.*;

/**
 * <p>
 * Acts as essentially as a Map&lt;Whatever,AtomicInteger&gt;.
 * </p>
 * 
 * <p>
 * ConcurrentCounter can be constructed with either a pre-defined collection of elements
 * or an actual {@link Enum}-based type for greater efficiency. (Arbitrarily-timed online
 * additon of new elements is not allowed as this would require significantly greater
 * synchronization overhead on all operations).
 * </p>
 * 
 * <p>
 * If highly concerned with efficiency and using the Enum-based constructor is not an
 * option, consider combining this class with the {@link Intern} system to reduce the
 * strain of equality checks.
 * </p>
 * 
 * @author hash
 * 
 */
public abstract class ConcurrentCounter<$T> implements Listener<$T> {
	public static <$T> ConcurrentCounter<$T> make(Collection<? extends $T> $elements) {
		return new Flexible<$T>($elements);
	}
	public static <$T extends Enum<$T>> ConcurrentCounter<$T> make(Class<$T> $enumType) {
		return new EnumBased<$T>($enumType);
	}
	
	private static class EnumBased<$E extends Enum<$E>> extends ConcurrentCounter<$E> {
		public EnumBased(Class<$E> $enumType) {
			super(new EnumMap<$E, AtomicInteger>($enumType));
			for ($E $e : $enumType.getEnumConstants())
				$map.put($e, new AtomicInteger());	
		}
	}
	private static class Flexible<$E> extends ConcurrentCounter<$E> {
		public Flexible(Collection<? extends $E> $elements) {
			super(new HashMap<$E, AtomicInteger>());
			for ($E $e : $elements)
				$map.put($e, new AtomicInteger());
		}
	}
	protected ConcurrentCounter(Map<$T,AtomicInteger> $map) {
		this.$map = $map;
	}
	
	protected final Map<$T,AtomicInteger>	$map;	// this would have to be volatile if we didn't have the pre-defined elements criteria to prevent index modification and resizing.
							
	/**
	 * This can be called from any thread, and will internally require no
	 * synchronization on anything but the counter for the given element. By the time
	 * this method returns, the count of the given object will have been incremented.
	 */
	public void hear($T $f) {
		AtomicInteger $i = $map.get($f);
		if ($i != null) $i.incrementAndGet();
	}
	
	/**
	 * This can be called from any thread, and will internally require no
	 * synchronization on anything but the counter for the given element
	 * 
	 * @return the number of times {@link #hear(Object)} has been called for this
	 *         argument, or -1 if the argument is not in this counter.
	 */
	public int getCount($T $f) {
		AtomicInteger $i = $map.get($f);
		return ($i == null) ? -1 : $i.get();
	}
}
