package us.exultant.ahs.thread;

import java.util.concurrent.locks.*;

public class ClosableSemaphore {
	public final void flip(boolean $flip) {
		sync.flip($flip);
	}
	
	public final boolean isFlipped() {
		return sync.isFlipped();
	}
	
	
	
	private final Sync	sync;
	
	/**
	 * 
	 * @param $status
	 *                the number "currently" held by CAS
	 * @param $delta
	 *                number of permits to release or acquire (positive and negative,
	 *                respectively)
	 * @return the number to push into CAS, or {@link Integer#MAX_VALUE} if the
	 *         transition would be illegal (i.e. trying to acquire more permits than
	 *         are available).
	 * @throws Error
	 *                 if the numbers involved were so big we hit an integer overflow
	 *                 situation
	 */
	private final int shift(int $status, int $delta) {
		final int $real = ($status == Integer.MIN_VALUE) ? 0 : Math.abs($status);
		final int $next = $real + $delta;
		if ($next < 0)
			if ($delta < 0)
				return Integer.MAX_VALUE;		// if acquiring (delta is negative), reject if insufficient permits available (aka real + delta < 0)
			else
				throw new Error("integer overflow");	// if releasing (delta is positive) and we somehow got a negative by increasing a positive?  scream.
		// if $next == Integer.MAX_VALUE here that could arguably be considered an overflow in context since returning it unmolested is supposed to signal a completely different situation... but eh. 
		return ($status >= 0) ? $real : ($next == 0) ? Integer.MIN_VALUE : -$real;
	}
	
	
	
	/**
	 * State herein is mostly how you would expect semaphore permits to be described,
	 * except:
	 * <ul>
	 * <li>All negative numbers are the equivalent in permit count to their absolute
	 * value, but also signify that we're in a "flipped" state. (Zero is not a
	 * negative number.)
	 * <li>{@link Integer#MIN_VALUE} means we are flipped, and there are no permits
	 * available.
	 * </ul>
	 * 
	 * @author hash
	 * 
	 */
	abstract static class Sync extends AbstractQueuedSynchronizer {
		//Sync() { setState(0); }
		
		final int getPermits() {
			return getState();
		}
		
		final boolean isFlipped() {
			return (getState() < 0);
		}
		
		final void flip(boolean $flip) {
			if ($flip) {
				for (;;) {
					final int state = getState();
					if (state < 0 || compareAndSetState(state, (state == 0) ? Integer.MIN_VALUE : -state)) return;
				}
			} else {
				for (;;) {
					final int state = getState();
					if (state >= 0 || compareAndSetState(state, (state == Integer.MIN_VALUE) ? 0 : -state)) return;
				}
			}
		}
		
		final int nonfairTryAcquireShared(int acquires) {
			for (;;) {
				int available = getState();
				int remaining = available - acquires;
				if (remaining < 0 || compareAndSetState(available, remaining)) return remaining;
			}
		}
		
		protected final boolean tryReleaseShared(int releases) {
			for (;;) {
				int current = getState();
				int next = current + releases;
				if (next < current) // overflow
				throw new Error("Maximum permit count exceeded");
				if (compareAndSetState(current, next)) return true;
			}
		}
		
		final void reducePermits(int reductions) {
			for (;;) {
				int current = getState();
				int next = current - reductions;
				if (next > current) // underflow
				throw new Error("Permit count underflow");
				if (compareAndSetState(current, next)) return;
			}
		}
		
		final int drainPermits() {
			for (;;) {
				int current = getState();
				if (current == 0 || compareAndSetState(current, 0)) return current;
			}
		}
	}
	static final class NonfairSync extends Sync {
		protected int tryAcquireShared(int acquires) {
			return nonfairTryAcquireShared(acquires);
		}
	}
	static final class FairSync extends Sync {
		protected int tryAcquireShared(int acquires) {
			for (;;) {
				if (hasQueuedPredecessors()) return -1;
				int available = getState();
				int remaining = available - acquires;
				if (remaining < 0 || compareAndSetState(available, remaining)) return remaining;
			}
		}
	}
}
