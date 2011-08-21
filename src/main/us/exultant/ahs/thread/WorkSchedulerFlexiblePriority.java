package us.exultant.ahs.thread;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;
import java.util.*;

public class WorkSchedulerFlexiblePriority extends ThreadPoolExecutor {
	/**
	 * Sequence number to break scheduling ties, and in turn to guarantee FIFO order
	 * among tied entries.
	 */
	private static final AtomicLong	sequencer					= new AtomicLong(0);
	
	/**
	 * Returns current nanosecond time.
	 */
	final long now() {
		return System.nanoTime();
	}
	
	/**
	 * Creates a new {@code ScheduledThreadPoolExecutor} with the given core pool
	 * size.
	 * 
	 * @param corePoolSize
	 *                the number of threads to keep in the pool, even if they are
	 *                idle, unless {@code allowCoreThreadTimeOut} is set
	 * @throws IllegalArgumentException
	 *                 if {@code corePoolSize < 0}
	 */
	public WorkSchedulerFlexiblePriority(int corePoolSize) {
		super(corePoolSize, Integer.MAX_VALUE, 0, TimeUnit.NANOSECONDS, new DelayedWorkQueue());
	}
	
	/**
	 * Creates a new {@code ScheduledThreadPoolExecutor} with the given initial
	 * parameters.
	 * 
	 * @param corePoolSize
	 *                the number of threads to keep in the pool, even if they are
	 *                idle, unless {@code allowCoreThreadTimeOut} is set
	 * @param threadFactory
	 *                the factory to use when the executor creates a new thread
	 * @throws IllegalArgumentException
	 *                 if {@code corePoolSize < 0}
	 * @throws NullPointerException
	 *                 if {@code threadFactory} is null
	 */
	public WorkSchedulerFlexiblePriority(int corePoolSize, ThreadFactory threadFactory) {
		super(corePoolSize, Integer.MAX_VALUE, 0, TimeUnit.NANOSECONDS, new DelayedWorkQueue(), threadFactory);
	}
	
	/**
	 * Creates a new ScheduledThreadPoolExecutor with the given initial parameters.
	 * 
	 * @param corePoolSize
	 *                the number of threads to keep in the pool, even if they are
	 *                idle, unless {@code allowCoreThreadTimeOut} is set
	 * @param handler
	 *                the handler to use when execution is blocked because the thread
	 *                bounds and queue capacities are reached
	 * @throws IllegalArgumentException
	 *                 if {@code corePoolSize < 0}
	 * @throws NullPointerException
	 *                 if {@code handler} is null
	 */
	public WorkSchedulerFlexiblePriority(int corePoolSize, RejectedExecutionHandler handler) {
		super(corePoolSize, Integer.MAX_VALUE, 0, TimeUnit.NANOSECONDS, new DelayedWorkQueue(), handler);
	}
	
	/**
	 * Creates a new ScheduledThreadPoolExecutor with the given initial parameters.
	 * 
	 * @param corePoolSize
	 *                the number of threads to keep in the pool, even if they are
	 *                idle, unless {@code allowCoreThreadTimeOut} is set
	 * @param threadFactory
	 *                the factory to use when the executor creates a new thread
	 * @param handler
	 *                the handler to use when execution is blocked because the thread
	 *                bounds and queue capacities are reached
	 * @throws IllegalArgumentException
	 *                 if {@code corePoolSize < 0}
	 * @throws NullPointerException
	 *                 if {@code threadFactory} or {@code handler} is null
	 */
	public WorkSchedulerFlexiblePriority(int corePoolSize, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
		super(corePoolSize, Integer.MAX_VALUE, 0, TimeUnit.NANOSECONDS, new DelayedWorkQueue(), threadFactory, handler);
	}
	
	
	
	
	
	/**
	 * @throws RejectedExecutionException
	 *                 {@inheritDoc}
	 * @throws NullPointerException
	 *                 {@inheritDoc}
	 */
	public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
		if (command == null || unit == null) throw new NullPointerException();
		WaveGuide<Void> sft = new WaveGuide<Void>(command, null, triggerTime(delay, unit));
		delayedExecute(sft);
		return sft;
	}
	
	/**
	 * @throws RejectedExecutionException
	 *                 {@inheritDoc}
	 * @throws NullPointerException
	 *                 {@inheritDoc}
	 */
	public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
		if (callable == null || unit == null) throw new NullPointerException();
		WaveGuide<V> sft = new WaveGuide<V>(callable, triggerTime(delay, unit));
		delayedExecute(sft);
		return sft;
	}
	
	/**
	 * @throws RejectedExecutionException
	 *                 {@inheritDoc}
	 * @throws NullPointerException
	 *                 {@inheritDoc}
	 * @throws IllegalArgumentException
	 *                 {@inheritDoc}
	 */
	public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
		if (command == null || unit == null) throw new NullPointerException();
		if (period <= 0) throw new IllegalArgumentException();
		WaveGuide<Void> sft = new WaveGuide<Void>(command, null, triggerTime(initialDelay, unit), unit.toNanos(period));
		delayedExecute(sft);
		return sft;
	}
	
	/**
	 * @throws RejectedExecutionException
	 *                 {@inheritDoc}
	 * @throws NullPointerException
	 *                 {@inheritDoc}
	 * @throws IllegalArgumentException
	 *                 {@inheritDoc}
	 */
	public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
		if (command == null || unit == null) throw new NullPointerException();
		if (delay <= 0) throw new IllegalArgumentException();
		WaveGuide<Void> sft = new WaveGuide<Void>(command, null, triggerTime(initialDelay, unit), unit.toNanos(-delay));
		delayedExecute(sft);
		return sft;
	}
	
	/**
	 * Executes {@code command} with zero required delay. This has effect equivalent
	 * to {@link #schedule(Runnable,long,TimeUnit) schedule(command, 0, anyUnit)}.
	 * Note that inspections of the queue and of the list returned by
	 * {@code shutdownNow} will access the zero-delayed {@link ScheduledFuture}, not
	 * the {@code command} itself.
	 * 
	 * <p>
	 * A consequence of the use of {@code ScheduledFuture} objects is that
	 * {@link ThreadPoolExecutor#afterExecute afterExecute} is always called with a
	 * null second {@code Throwable} argument, even if the {@code command} terminated
	 * abruptly. Instead, the {@code Throwable} thrown by such a task can be obtained
	 * via {@link Future#get}.
	 * 
	 * @throws RejectedExecutionException
	 *                 at discretion of {@code RejectedExecutionHandler}, if the task
	 *                 cannot be accepted for execution because the executor has been
	 *                 shut down
	 * @throws NullPointerException
	 *                 {@inheritDoc}
	 */
	public void execute(Runnable command) {
		schedule(command, 0, TimeUnit.NANOSECONDS);
	}
	
	// Override AbstractExecutorService methods
	
	/**
	 * @throws RejectedExecutionException
	 *                 {@inheritDoc}
	 * @throws NullPointerException
	 *                 {@inheritDoc}
	 */
	public Future<?> submit(Runnable task) {
		return schedule(task, 0, TimeUnit.NANOSECONDS);
	}
	
	/**
	 * @throws RejectedExecutionException
	 *                 {@inheritDoc}
	 * @throws NullPointerException
	 *                 {@inheritDoc}
	 */
	public <T> Future<T> submit(Runnable task, T result) {
		return schedule(Executors.callable(task, result), 0, TimeUnit.NANOSECONDS);
	}
	
	/**
	 * @throws RejectedExecutionException
	 *                 {@inheritDoc}
	 * @throws NullPointerException
	 *                 {@inheritDoc}
	 */
	public <T> Future<T> submit(Callable<T> task) {
		return schedule(task, 0, TimeUnit.NANOSECONDS);
	}
	
	
	
	
	
	/**
	 * Main execution method for delayed or periodic tasks. If pool is shut down,
	 * rejects the task. Otherwise adds task to queue and starts a thread, if
	 * necessary, to run it. (We cannot prestart the thread to run the task because
	 * the task (probably) shouldn't be run yet,) If the pool is shut down while the
	 * task is being added, cancel and remove it if required by state and
	 * run-after-shutdown parameters.
	 * 
	 * @param task
	 *                the task
	 */
	private void delayedExecute(RunnableScheduledFuture<?> task) {
		if (isShutdown()) ;	//reject(task);		// i don't think i care.
		else {
			super.getQueue().add(task);
			if (isShutdown() && remove(task)) task.cancel(false);
			else prestartCoreThread();
		}
	}
	
	/**
	 * Requeues a periodic task unless current run state precludes it. Same idea as
	 * delayedExecute except drops task rather than rejecting.
	 * 
	 * @param task
	 *                the task
	 */
	private void reExecutePeriodic(RunnableScheduledFuture<?> task) {
		if (!isShutdown()) {
			super.getQueue().add(task);
			if (isShutdown() && remove(task)) task.cancel(false);
			else prestartCoreThread();
		}
	}
	
	/**
	 * Returns the trigger time of a delayed action.
	 */
	private long triggerTime(long delay, TimeUnit unit) {
		return triggerTime(unit.toNanos((delay < 0) ? 0 : delay));
	}
	
	/**
	 * Returns the trigger time of a delayed action.
	 */
	private long triggerTime(long delay) {
		return now() + ((delay < (Long.MAX_VALUE >> 1)) ? delay : overflowFree(delay));
	}
	
	/**
	 * Constrains the values of all delays in the queue to be within Long.MAX_VALUE of
	 * each other, to avoid overflow in compareTo. This may occur if a task is
	 * eligible to be dequeued, but has not yet been, while some other task is added
	 * with a delay of Long.MAX_VALUE.
	 */
	private long overflowFree(long delay) {
		Delayed head = (Delayed) super.getQueue().peek();
		if (head != null) {
			long headDelay = head.getDelay(TimeUnit.NANOSECONDS);
			if (headDelay < 0 && (delay - headDelay < 0)) delay = Long.MAX_VALUE + headDelay;
		}
		return delay;
	}
	
	
	
	
	
	////////////////////////////////////////////////////////////////
	//
	//	THE TASK WRAPPER FOR ORDERING
	//
	////////////////////////////////////////////////////////////////
	private class WaveGuide<V> extends FutureTask<V> implements RunnableScheduledFuture<V> {
		
		/** Sequence number to break ties FIFO */
		private final long		sequenceNumber;
		
		/** The time the task is enabled to execute in nanoTime units */
		private long			time;
		
		/**
		 * Period in nanoseconds for repeating tasks. A positive value indicates
		 * fixed-rate execution. A negative value indicates fixed-delay execution.
		 * A value of 0 indicates a non-repeating task.
		 */
		private final long		period;
		
		/**
		 * Index into delay queue, to support faster cancellation.
		 */
		int				heapIndex;
		
		/**
		 * Creates a one-shot action with given nanoTime-based trigger time.
		 */
		WaveGuide(Runnable r, V result, long ns) {
			super(r, result);
			this.time = ns;
			this.period = 0;
			this.sequenceNumber = sequencer.getAndIncrement();
		}
		
		/**
		 * Creates a periodic action with given nano time and period.
		 */
		WaveGuide(Runnable r, V result, long ns, long period) {
			super(r, result);
			this.time = ns;
			this.period = period;
			this.sequenceNumber = sequencer.getAndIncrement();
		}
		
		/**
		 * Creates a one-shot action with given nanoTime-based trigger.
		 */
		WaveGuide(Callable<V> callable, long ns) {
			super(callable);
			this.time = ns;
			this.period = 0;
			this.sequenceNumber = sequencer.getAndIncrement();
		}
		
		public long getDelay(TimeUnit unit) {
			return unit.convert(time - now(), TimeUnit.NANOSECONDS);
		}
		
		public int compareTo(Delayed other) {
			if (other == this) // compare zero ONLY if same object
			return 0;
			if (other instanceof WaveGuide) {
				WaveGuide<?> x = (WaveGuide<?>) other;
				long diff = time - x.time;
				if (diff < 0) return -1;
				else if (diff > 0) return 1;
				else if (sequenceNumber < x.sequenceNumber) return -1;
				else return 1;
			}
			long d = (getDelay(TimeUnit.NANOSECONDS) - other.getDelay(TimeUnit.NANOSECONDS));
			return (d == 0) ? 0 : ((d < 0) ? -1 : 1);
		}
		
		/**
		 * Returns true if this is a periodic (not a one-shot) action.
		 * 
		 * @return true if periodic
		 */
		public final boolean isPeriodic() {
			return period != 0;
		}
		
		/**
		 * Sets the next time to run for a periodic task.
		 */
		private void setNextRunTime() {
			long p = period;
			if (p > 0) time += p;
			else time = triggerTime(-p);
		}
		
		public boolean cancel(boolean mayInterruptIfRunning) {
			boolean cancelled = super.cancel(mayInterruptIfRunning);
			//if (cancelled && removeOnCancel && heapIndex >= 0) remove(this);	// i never care about removing.
			return cancelled;
		}
		
		/**
		 * Overrides FutureTask version so as to reset/requeue if periodic.
		 */
		public void run() {
			if (isShutdown()) cancel(false);
			else if (!isPeriodic()) WaveGuide.super.run();
			//TODO:AHS:THREAD:DOUG: this is more or less where one would put a recurrent work target back in the waiting pool
			else if (WaveGuide.super.runAndReset()) {
				setNextRunTime();
				reExecutePeriodic(this);
			}
		}
	}
	
	
	
	
	
	////////////////////////////////////////////////////////////////
	//
	//	THE QUEUE
	//
	////////////////////////////////////////////////////////////////
	/**
	 * The most difficult case is a high-priority but delayed task.
	 * 
	 * - We can't have the return of a task from this queue blocking on that guy, so
	 * even if he's the highest priority he can't be the head.
	 * 
	 * - We can't sort on time first because then the priorities turn to crap.
	 * 
	 * So, don't have delayed tasks enter this queue until they're ready (and then
	 * they have to soar to the top immediately).
	 * 
	 * Problem is then in taking notice of clock tasks that are now ready. We'll have
	 * to poll them every time a thread asks for something from this queue... so,
	 * essentially, clock tasks they require their own separately sorted storage.
	 * 
	 * (On checking the readiness of clock tasks: we'll only ever need to check the
	 * top, so having them do their own time call is fine for effic. We can have any
	 * non-zero time sort to the top of this queue. When we check clock tasks, we have
	 * to keep pulling from the top and pushing it into this until we can't anymore,
	 * because this one is still needed to sort the clock tasks by relative priority
	 * amongst themselves (making what I said just a moment ago about time calls
	 * potentially a slight lie). The clock task heap need only be sorted by time. I
	 * can have positive index ints point into this guy and negs into the clock; min
	 * and max be special values for running and nonclock-waiting. In fact, overall I
	 * think it might turn out to be the same queue impl but with different
	 * comparators and a signnum.)
	 */
	static class DelayedWorkQueue extends AbstractQueue<Runnable> implements BlockingQueue<Runnable> {
		
		/*
		 * A DelayedWorkQueue is based on a heap-based data structure
		 * like those in DelayQueue and PriorityQueue, except that
		 * every ScheduledFutureTask also records its index into the
		 * heap array. This eliminates the need to find a task upon
		 * cancellation, greatly speeding up removal (down from O(n)
		 * to O(log n)), and reducing garbage retention that would
		 * otherwise occur by waiting for the element to rise to top
		 * before clearing. But because the queue may also hold
		 * RunnableScheduledFutures that are not ScheduledFutureTasks,
		 * we are not guaranteed to have such indices available, in
		 * which case we fall back to linear search. (We expect that
		 * most tasks will not be decorated, and that the faster cases
		 * will be much more common.)
		 *
		 * All heap operations must record index changes -- mainly
		 * within siftUp and siftDown. Upon removal, a task's
		 * heapIndex is set to -1. Note that ScheduledFutureTasks can
		 * appear at most once in the queue (this need not be true for
		 * other kinds of tasks or work queues), so are uniquely
		 * identified by heapIndex.
		 */

		private static final int		INITIAL_CAPACITY	= 32;
		private RunnableScheduledFuture[]	queue			= new RunnableScheduledFuture[INITIAL_CAPACITY];
		private final ReentrantLock		lock			= new ReentrantLock();
		private int				size			= 0;
		
		/**
		 * Thread designated to wait for the task at the head of the queue. This
		 * variant of the Leader-Follower pattern
		 * (http://www.cs.wustl.edu/~schmidt/POSA/POSA2/) serves to minimize
		 * unnecessary timed waiting. When a thread becomes the leader, it waits
		 * only for the next delay to elapse, but other threads await
		 * indefinitely. The leader thread must signal some other thread before
		 * returning from take() or poll(...), unless some other thread becomes
		 * leader in the interim. Whenever the head of the queue is replaced with
		 * a task with an earlier expiration time, the leader field is invalidated
		 * by being reset to null, and some waiting thread, but not necessarily
		 * the current leader, is signalled. So waiting threads must be prepared
		 * to acquire and lose leadership while waiting.
		 */
		private Thread				leader			= null;
		
		/**
		 * Condition signalled when a newer task becomes available at the head of
		 * the queue or a new thread may need to become leader.
		 */
		private final Condition			available		= lock.newCondition();
		
		/**
		 * Set f's heapIndex if it is a ScheduledFutureTask.
		 */
		private void setIndex(RunnableScheduledFuture f, int idx) {
			if (f instanceof WaveGuide) ((WaveGuide) f).heapIndex = idx;	//FIXME:AHS:THREAD:DOUG: heap index touching here shall be updated
		}
		
		/**
		 * Sift element added at bottom up to its heap-ordered spot. Call only
		 * when holding lock.
		 */
		private void siftUp(int k, RunnableScheduledFuture key) {
			while (k > 0) {
				int parent = (k - 1) >>> 1;
				RunnableScheduledFuture e = queue[parent];
				if (key.compareTo(e) >= 0) break;	//FIXME:AHS:THREAD:DOUG: this is where comparators are(n't) invoked.
				queue[k] = e;
				setIndex(e, k);
				k = parent;
			}
			queue[k] = key;
			setIndex(key, k);
		}
		
		/**
		 * Sift element added at top down to its heap-ordered spot. Call only when
		 * holding lock.
		 */
		private void siftDown(int k, RunnableScheduledFuture key) {
			int half = size >>> 1;
			while (k < half) {
				int child = (k << 1) + 1;
				RunnableScheduledFuture c = queue[child];
				int right = child + 1;
				if (right < size && c.compareTo(queue[right]) > 0) c = queue[child = right];	//FIXME:AHS:THREAD:DOUG: this is where comparators are(n't) invoked.
				if (key.compareTo(c) <= 0) break;						//FIXME:AHS:THREAD:DOUG: this is where comparators are(n't) invoked.
				queue[k] = c;
				setIndex(c, k);
				k = child;
			}
			queue[k] = key;
			setIndex(key, k);
		}
		
		/**
		 * Resize the heap array. Call only when holding lock.
		 */
		private void grow() {
			int oldCapacity = queue.length;
			int newCapacity = oldCapacity + (oldCapacity >> 1); // grow 50%
			if (newCapacity < 0) // overflow
			newCapacity = Integer.MAX_VALUE;
			queue = Arrays.copyOf(queue, newCapacity);
		}
		
		/**
		 * Find index of given object, or -1 if absent
		 */
		private int indexOf(Object x) {
			if (x != null) {
				if (x instanceof WaveGuide) {
					int i = ((WaveGuide) x).heapIndex;	//FIXME:AHS:THREAD:DOUG: heap index touching here shall be updated
					// Sanity check; x could conceivably be a ScheduledFutureTask from some other pool.
					//XXX:AHS:THREAD:DOUG: there are so many more reasonable ways i can see us limiting the origin pool of this stuff
					if (i >= 0 && i < size && queue[i] == x) return i;
				} else {	//XXX:AHS:THREAD:DOUG: to be perfectly honest, i'm not sure why we retain this code.  we have enough control over the calling options for this method that it's really not necessary.
					for (int i = 0; i < size; i++)
						if (x.equals(queue[i])) return i;
				}
			}
			return -1;
		}
		
		public boolean contains(Object x) {
			final ReentrantLock lock = this.lock;
			lock.lock();
			try {
				return indexOf(x) != -1;
			} finally {
				lock.unlock();
			}
		}
		
		public boolean remove(Object x) {
			final ReentrantLock lock = this.lock;
			lock.lock();
			try {
				int i = indexOf(x);
				if (i < 0) return false;
				
				setIndex(queue[i], -1);
				int s = --size;
				RunnableScheduledFuture replacement = queue[s];
				queue[s] = null;
				if (s != i) {
					siftDown(i, replacement);
					if (queue[i] == replacement) siftUp(i, replacement);
				}
				return true;
			} finally {
				lock.unlock();
			}
		}
		
		public int size() {
			final ReentrantLock lock = this.lock;
			lock.lock();
			try {
				return size;
			} finally {
				lock.unlock();
			}
		}
		
		public boolean isEmpty() {
			return size() == 0;
		}
		
		public int remainingCapacity() {
			return Integer.MAX_VALUE;
		}
		
		public RunnableScheduledFuture peek() {
			final ReentrantLock lock = this.lock;
			lock.lock();
			try {
				return queue[0];
			} finally {
				lock.unlock();
			}
		}
		
		public boolean offer(Runnable x) {
			if (x == null) throw new NullPointerException();
			RunnableScheduledFuture e = (RunnableScheduledFuture) x;
			final ReentrantLock lock = this.lock;
			lock.lock();
			try {
				int i = size;
				if (i >= queue.length) grow();
				size = i + 1;
				if (i == 0) {
					queue[0] = e;
					setIndex(e, 0);
				} else {
					siftUp(i, e);
				}
				if (queue[0] == e) {
					leader = null;
					available.signal();
				}
			} finally {
				lock.unlock();
			}
			return true;
		}
		
		public void put(Runnable e) {
			offer(e);
		}
		
		public boolean add(Runnable e) {
			return offer(e);
		}
		
		public boolean offer(Runnable e, long timeout, TimeUnit unit) {
			return offer(e);
		}
		
		/**
		 * Performs common bookkeeping for poll and take: Replaces first element
		 * with last and sifts it down. Call only when holding lock.
		 * 
		 * @param f
		 *                the task to remove and return
		 */
		private RunnableScheduledFuture finishPoll(RunnableScheduledFuture f) {
			int s = --size;
			RunnableScheduledFuture x = queue[s];
			queue[s] = null;
			if (s != 0) siftDown(0, x);
			setIndex(f, -1);
			return f;
		}
		
		public RunnableScheduledFuture poll() {
			final ReentrantLock lock = this.lock;
			lock.lock();
			try {
				RunnableScheduledFuture first = queue[0];
				if (first == null || first.getDelay(TimeUnit.NANOSECONDS) > 0) return null;	//FIXME:AHS:THREAD:DOUG: this should be looking at readiness (and the delay thing should be determining readiness for clock-based tasks).
				else return finishPoll(first);
			} finally {
				lock.unlock();
			}
		}
		
		public RunnableScheduledFuture take() throws InterruptedException {
			final ReentrantLock lock = this.lock;
			lock.lockInterruptibly();
			try {
				for (;;) {
					RunnableScheduledFuture first = queue[0];
					if (first == null) available.await();
					else {
						long delay = first.getDelay(TimeUnit.NANOSECONDS);
						if (delay <= 0) return finishPoll(first);	//FIXME:AHS:THREAD:DOUG: this should be looking at readiness (and the delay thing should be determining readiness for clock-based tasks).
						else if (leader != null) available.await();
						else {
							Thread thisThread = Thread.currentThread();
							leader = thisThread;
							try {
								available.awaitNanos(delay);
							} finally {
								if (leader == thisThread) leader = null;
							}
						}
					}
				}
			} finally {
				if (leader == null && queue[0] != null) available.signal();
				lock.unlock();
			}
		}
		
		public RunnableScheduledFuture poll(long timeout, TimeUnit unit) throws InterruptedException {
			long nanos = unit.toNanos(timeout);
			final ReentrantLock lock = this.lock;
			lock.lockInterruptibly();
			try {
				for (;;) {
					RunnableScheduledFuture first = queue[0];
					if (first == null) {
						if (nanos <= 0) return null;
						else nanos = available.awaitNanos(nanos);
					} else {
						long delay = first.getDelay(TimeUnit.NANOSECONDS);
						if (delay <= 0) return finishPoll(first);
						if (nanos <= 0) return null;
						if (nanos < delay || leader != null) nanos = available.awaitNanos(nanos);
						else {
							Thread thisThread = Thread.currentThread();
							leader = thisThread;
							try {
								long timeLeft = available.awaitNanos(delay);
								nanos -= delay - timeLeft;
							} finally {
								if (leader == thisThread) leader = null;
							}
						}
					}
				}
			} finally {
				if (leader == null && queue[0] != null) available.signal();
				lock.unlock();
			}
		}
		
		public void clear() {
			final ReentrantLock lock = this.lock;
			lock.lock();
			try {
				for (int i = 0; i < size; i++) {
					RunnableScheduledFuture t = queue[i];
					if (t != null) {
						queue[i] = null;
						setIndex(t, -1);
					}
				}
				size = 0;
			} finally {
				lock.unlock();
			}
		}
		
		/**
		 * Return and remove first element only if it is expired. Used only by
		 * drainTo. Call only when holding lock.
		 */
		private RunnableScheduledFuture pollExpired() {
			RunnableScheduledFuture first = queue[0];
			if (first == null || first.getDelay(TimeUnit.NANOSECONDS) > 0) return null;
			return finishPoll(first);
		}
		
		public int drainTo(Collection<? super Runnable> c) {
			if (c == null) throw new NullPointerException();
			if (c == this) throw new IllegalArgumentException();
			final ReentrantLock lock = this.lock;
			lock.lock();
			try {
				RunnableScheduledFuture first;
				int n = 0;
				while ((first = pollExpired()) != null) {
					c.add(first);
					++n;
				}
				return n;
			} finally {
				lock.unlock();
			}
		}
		
		public int drainTo(Collection<? super Runnable> c, int maxElements) {
			if (c == null) throw new NullPointerException();
			if (c == this) throw new IllegalArgumentException();
			if (maxElements <= 0) return 0;
			final ReentrantLock lock = this.lock;
			lock.lock();
			try {
				RunnableScheduledFuture first;
				int n = 0;
				while (n < maxElements && (first = pollExpired()) != null) {
					c.add(first);
					++n;
				}
				return n;
			} finally {
				lock.unlock();
			}
		}
		
		public Object[] toArray() {
			final ReentrantLock lock = this.lock;
			lock.lock();
			try {
				return Arrays.copyOf(queue, size, Object[].class);
			} finally {
				lock.unlock();
			}
		}
		
		@SuppressWarnings("unchecked")
		public <T> T[] toArray(T[] a) {
			final ReentrantLock lock = this.lock;
			lock.lock();
			try {
				if (a.length < size) return (T[]) Arrays.copyOf(queue, size, a.getClass());
				System.arraycopy(queue, 0, a, 0, size);
				if (a.length > size) a[size] = null;
				return a;
			} finally {
				lock.unlock();
			}
		}
		
		public Iterator<Runnable> iterator() {
			return new Itr(Arrays.copyOf(queue, size));
		}
		
		

		/**
		 * Snapshot iterator that works off copy of underlying q array.
		 */
		private class Itr implements Iterator<Runnable> {
			final RunnableScheduledFuture[]	array;
			int				cursor	= 0;	// index of next element to return
			int				lastRet	= -1;	// index of last element, or -1 if no such
									
			Itr(RunnableScheduledFuture[] array) {
				this.array = array;
			}
			
			public boolean hasNext() {
				return cursor < array.length;
			}
			
			public Runnable next() {
				if (cursor >= array.length) throw new NoSuchElementException();
				lastRet = cursor;
				return array[cursor++];
			}
			
			public void remove() {
				if (lastRet < 0) throw new IllegalStateException();
				DelayedWorkQueue.this.remove(array[lastRet]);
				lastRet = -1;
			}
		}
	}
}
