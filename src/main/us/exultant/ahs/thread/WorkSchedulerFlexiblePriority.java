package us.exultant.ahs.thread;

import us.exultant.ahs.util.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class WorkSchedulerFlexiblePriority implements WorkScheduler {
	public WorkSchedulerFlexiblePriority(int $threadCount) {
		Thread[] $threads = ThreadUtil.wrapAll(new Runnable() {
			public void run() {
				for (;;) {
					try {
						worker_cycle();
					} catch (InterruptedException $e) { $e.printStackTrace(); }
				}
			}
		}, $threadCount);
		
		ThreadUtil.startAll($threads);
	}
	
	public <$V> WorkFuture<$V> schedule(WorkTarget<$V> $work, ScheduleParams $when) {
		WorkFuture<$V> $wf = new WorkFuture<$V>($work, $when);
		if ($wf.$sync.scheduler_shift())
			$scheduled.add($wf);
		else
			if ($when.isUnclocked())
				$unready.add($wf);
			else
				$delayed.add($wf);
		return $wf;
	}
	
	public <$V> void update(WorkFuture<$V> $fut) {
		// check doneness; try to transition immediate to FINISHED if is done.
		if ($fut.$work.isDone()) $fut.$sync.tryFinish(false);
		
		// just push this into the set of requested updates.
		$updatereq.add($fut);
	}
	
	
	
	private final PriorityHeap		$delayed	= new PriorityHeap(WorkFuture.DelayComparator.INSTANCE);
	private final PriorityHeap		$scheduled	= new PriorityHeap(WorkFuture.PriorityComparator.INSTANCE);
	private final Set<WorkFuture<?>>	$unready	= new HashSet<WorkFuture<?>>();
	private final Set<WorkFuture<?>>	$updatereq	= new ConcurrentSkipListSet<WorkFuture<?>>();	// as long as we run updates strictly after removing something from this, our synchronization demands are quite low.
	
	private final ReentrantLock		$lock		= new ReentrantLock();
	private Thread				$leader		= null;
	private final Condition			$available	= $lock.newCondition();
	
	
	private void worker_cycle() throws InterruptedException {
		$lock.lockInterruptibly();
		try { retry: for (;;) {		// we repeat this loop until we get someone who actually makes it into the 'power' call. 
			WorkFuture<?> $wf = worker_acquireWork();	// this may block.
			switch ($wf.getState()) {
				case FINISHED: continue retry;	// and drop it
				case CANCELLED: continue retry;	// and drop it
				case SCHEDULED:
					if ($wf.$sync.scheduler_power()) {
						// the work finished into a WAITING state; check it for immediate readiness and put it in the appropriate heap.
						if ($wf.$sync.scheduler_shift()) {
							$scheduled.add($wf);
						} else {
							if ($wf.getScheduleParams().isUnclocked())
								$unready.add($wf);
							else
								$delayed.add($wf);
						}
					} else {
						// the work is completed (either as FINISHED or CANCELLED); we must now drop it.
						/* no-op */
					}
					return;
				default:
					throw new MajorBug();
			}
		}} finally {
			$lock.unlock();
		}
	}
	
	/**
	 * Try to get WorkFuture from the $scheduled heap, and don't give up. Also always
	 * try to pull clocked work from delayed to scheduled every time around.
	 * 
	 * WorkFuture returned from this method may be either SCHEDULED, CANCELLED, or
	 * FINISHED (they may not be WAITING or RUNNING since those mutations are only
	 * carried out under this scheduler's lock). The WorkTarget of the returned
	 * WorkFuture may return either true or false to both {@link WorkTarget#isDone()}
	 * and {@link WorkTarget#isReady()}, regardless of the WorkFuture's state; the
	 * caller of this function should be aware of that, and either finish and drop the
	 * task or immediately return it to waiting as necessary.
	 * 
	 * The lock much be acquired during this entire function, in order to make
	 * possible the correctly atomic shifts to RUNNING or WAITING that may be carried
	 * out immediately following this function.
	 */	// actually, not sure about that lock.  it doesn't cause problems if we keep it listed as scheduled even if its not in that heap for a moment, i think.  and either of the following transitions would be followed by our relinquishment anyway, i think.  though if we hit FINISHED, CANCELLED, or make it directly into WAITING, then we'll have to sink right back into this, so we might as well not leave ourselves open to contention.
	private WorkFuture<?> worker_acquireWork() throws InterruptedException {
		try { for (;;) {
			// shift any clock-based tasks that need no further delay into the scheduled heap.  note the time until the next of those clocked tasks will be delay-free.
			long $delay = worker_pollDelayed();		
			
			// get work now, if we have any.
			WorkFuture<?> $first = $scheduled.peek();
			if ($first != null) return $scheduled.poll();
			
			// if we don't have any ready work, wait for signal of new work submission or until what we were told would be the next delay expiry; then we just retry.
			if ($leader != null) {
				$available.await();
			} else {
				$leader = Thread.currentThread();
				try {
					$available.awaitNanos($delay);	// note that if we had zero delayed work, this is just an obscenely long timeout and no special logic is needed.
				} finally {
					if ($leader == Thread.currentThread()) $leader = null;
				}
			}
		}} finally {
			if ($leader == null && $scheduled.peek() != null) $available.signal();
		}
	}
	
	/**
	 * Pulls clocked work that requires no further delay off of the delayed heap,
	 * pushes it into the scheduled heap, and sifts the shifted tasks as necessary by
	 * priority.
	 * 
	 * Hold the friggin' lock when calling, of course. Since cause WorkFuture's to
	 * change their state in here, we must enforce that this aligns with changing the
	 * heap they're in so the rest of the scheduler doesn't go insane.
	 * 
	 * @return the delay (in nanosec) until the next known clocked task will be ready
	 *         (or Long.MAX_VALUE if there are no more clocked tasks present).
	 */
	private long worker_pollDelayed() {
		WorkFuture<?> $key;
		for (;;) {
			$key = $delayed.peek();
			if ($key == null) return Long.MAX_VALUE;	// the caller should just wait indefinitely for notification of new tasks entering the system, because we're empty.
			if (!$key.$sync.scheduler_shift()) return $key.getScheduleParams().getDelay();
			
			$delayed.poll();	// get it outta there
			$scheduled.add($key);	
		}
	}
	
	
	
	
	
	
	private class PriorityHeap {
		public PriorityHeap(Comparator<WorkFuture<?>> $comparator) {
			this.$comparator = $comparator;
		}
		
		private static final int		INITIAL_CAPACITY	= 32;
		private WorkFuture<?>[]			$queue			= new WorkFuture<?>[INITIAL_CAPACITY];
		private int				$size			= 0;
		private final Comparator<WorkFuture<?>>	$comparator;
		
		public WorkFuture<?> peek() {
			return $queue[0];
		}
		
		/** Returns the first element, replacing the first element with the last and sifting it down. Call only when holding lock. */
		private WorkFuture<?> poll() {
			int $s = --$size;
			WorkFuture<?> f = $queue[0];
			WorkFuture<?> x = $queue[$s];
			$queue[$s] = null;
			if ($s != 0) siftDown(0, x);
			f.$heapIndex = -1;
			return f;
		}

		/** Add a new element and immediately sift it to its heap-ordered spot. Call only when holding lock. */
		public boolean add(WorkFuture<?> $newb) {
			if ($newb == null) throw new NullPointerException();
			$lock.lock();
			try {
				int $s = $size;
				if ($s >= $queue.length) grow();
				$size = $s + 1;
				if ($s == 0) {
					$queue[0] = $newb;
					$newb.$heapIndex = 0;
				} else {
					siftUp($s, $newb);
				}
				if ($queue[0] == $newb) {
					$leader = null;
					$available.signal();
				}
			} finally {
				$lock.unlock();
			}
			return true;
		}
		
		/** Sift element added at bottom up to its heap-ordered spot. Call only when holding lock. */
		private void siftUp(int $k, WorkFuture<?> $x) {
			while ($k > 0) {
				int $parent = ($k - 1) >>> 1;
				WorkFuture<?> $e = $queue[$parent];
				if ($comparator.compare($x, $e) >= 0) break;
				$queue[$k] = $e;
				$e.$heapIndex = $k;
				$k = $parent;
			}
			$queue[$k] = $x;
			$x.$heapIndex = $k;
		}
		
		/** Sift element added at top down to its heap-ordered spot. Call only when holding lock. */
		private void siftDown(int $k, WorkFuture<?> $x) {
			int $half = $size >>> 1;
			while ($k < $half) {
				int $child = ($k << 1) + 1;
				WorkFuture<?> $c = $queue[$child];
				int right = $child + 1;
				if (right < $size && $comparator.compare($c, $queue[right]) > 0) $c = $queue[$child = right];
				if ($comparator.compare($x, $c) <= 0) break;
				$queue[$k] = $c;
				$c.$heapIndex = $k;
				$k = $child;
			}
			$queue[$k] = $x;
			$x.$heapIndex = $k;
		}
		
		/** Resize the heap array. Call only when holding lock. */
		private void grow() {
			int $oldCapacity = $queue.length;
			int $newCapacity = $oldCapacity + ($oldCapacity >> 1); // grow 50%
			if ($newCapacity < 0) // overflow
			$newCapacity = Integer.MAX_VALUE;
			$queue = Arrays.copyOf($queue, $newCapacity);
		}
	}
}