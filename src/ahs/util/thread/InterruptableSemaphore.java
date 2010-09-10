package ahs.util.thread;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.util.concurrent.atomic.*;

import sun.misc.*;

/**
 * Allows user interrupt of all acquires; overrides actual interrupts.
 * 
 * Vast majority of code is simply replicated from the original Semaphore in
 * java.util.concurrent by Doug Lea.
 * 
 * @author hash
 * @author Doug Lea
 * 
 */
public class InterruptableSemaphore {
	public void interrupt() {
		sync.$interrupted = true;
	}
	public void resetInterruptStatus() {
		sync.$interrupted = false;
	}
	
	
	
	
	
	
	
	private final Sync	sync;
	
	/**
	 * Synchronization implementation for semaphore. Uses AQS state to represent
	 * permits. Subclassed into fair and nonfair versions.
	 */
	abstract static class Sync {
		private boolean $interrupted = false;
		
		
		
		
		Sync(int permits) {
			state = new AtomicInteger();
			setState(permits);
		}
		
		final int getPermits() {
			return getState();
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
				int p = getState();
				if (compareAndSetState(p, p + releases)) return true;
			}
		}
		
		final void reducePermits(int reductions) {
			for (;;) {
				int current = getState();
				int next = current - reductions;
				if (compareAndSetState(current, next)) return;
			}
		}
		
		final int drainPermits() {
			for (;;) {
				int current = getState();
				if (current == 0 || compareAndSetState(current, 0)) return current;
			}
		}
		
		






		static final class Node {
			/** waitStatus value to indicate thread has cancelled */
			static final int	CANCELLED	= 1;
			/**
			 * waitStatus value to indicate successor's thread needs unparking
			 */
			static final int	SIGNAL		= -1;
			/** waitStatus value to indicate thread is waiting on condition */
			static final int	CONDITION	= -2;
			/** Marker to indicate a node is waiting in shared mode */
			static final Node	SHARED		= new Node();
			/** Marker to indicate a node is waiting in exclusive mode */
			static final Node	EXCLUSIVE	= null;
			
			volatile int		waitStatus;
			
			volatile Node		prev;
			
			volatile Node		next;
			
			volatile Thread		thread;
			
			Node			nextWaiter;
			
			final boolean isShared() {
				return nextWaiter == SHARED;
			}
			
			final Node predecessor() throws NullPointerException {
				Node p = prev;
				if (p == null) throw new NullPointerException();
				else return p;
			}
			
			Node() { // Used to establish initial head or SHARED marker
			}
			
			Node(Thread thread, Node mode) { // Used by addWaiter
				this.nextWaiter = mode;
				this.thread = thread;
			}
			
			Node(Thread thread, int waitStatus) { // Used by Condition
				this.waitStatus = waitStatus;
				this.thread = thread;
			}
		}
		
		private transient volatile Node	head;
		
		private transient volatile Node	tail;
		
		private final AtomicInteger	state;
		
		protected final int getState() {
			return state.get();
		}
		
		protected final void setState(int newState) {
			state.set(newState);
		}
		
		protected final boolean compareAndSetState(int expect, int update) {
			// See below for intrinsics setup to support this
			return state.compareAndSet(expect, update);
		}
		
		// Queuing utilities
		
		static final long	spinForTimeoutThreshold	= 1000L;
		
		private Node enq(final Node node) {
			for (;;) {
				Node t = tail;
				if (t == null) { // Must initialize
					Node h = new Node(); // Dummy header
					h.next = node;
					node.prev = h;
					if (compareAndSetHead(h)) {
						tail = node;
						return h;
					}
				} else {
					node.prev = t;
					if (compareAndSetTail(t, node)) {
						t.next = node;
						return t;
					}
				}
			}
		}
		
		private Node addWaiter(Node mode) {
			Node node = new Node(Thread.currentThread(), mode);
			// Try the fast path of enq; backup to full enq on failure
			Node pred = tail;
			if (pred != null) {
				node.prev = pred;
				if (compareAndSetTail(pred, node)) {
					pred.next = node;
					return node;
				}
			}
			enq(node);
			return node;
		}
		
		private void setHead(Node node) {
			head = node;
			node.thread = null;
			node.prev = null;
		}
		
		private void unparkSuccessor(Node node) {
			compareAndSetWaitStatus(node, Node.SIGNAL, 0);
			Node s = node.next;
			if (s == null || s.waitStatus > 0) {
				s = null;
				for (Node t = tail; t != null && t != node; t = t.prev)
					if (t.waitStatus <= 0) s = t;
			}
			if (s != null) LockSupport.unpark(s.thread);
		}
		
		private void setHeadAndPropagate(Node node, int propagate) {
			setHead(node);
			if (propagate > 0 && node.waitStatus != 0) {
				/*
				 * Don't bother fully figuring out successor.  If it
				 * looks null, call unparkSuccessor anyway to be safe.
				 */
				Node s = node.next;
				if (s == null || s.isShared()) unparkSuccessor(node);
			}
		}
		
		private void cancelAcquire(Node node) {
			if (node != null) { // Ignore if node doesn't exist
				node.thread = null;
				// Can use unconditional write instead of CAS here
				node.waitStatus = Node.CANCELLED;
				unparkSuccessor(node);
			}
		}
		
		private static boolean shouldParkAfterFailedAcquire(Node pred, Node node) {
			int s = pred.waitStatus;
			if (s < 0)
			/*
			 * This node has already set status asking a release
			 * to signal it, so it can safely park
			 */
			return true;
			if (s > 0)
			/*
			 * Predecessor was cancelled. Move up to its predecessor
			 * and indicate retry.
			 */
			node.prev = pred.prev;
			else
			/*
			 * Indicate that we need a signal, but don't park yet. Caller
			 * will need to retry to make sure it cannot acquire before
			 * parking.
			 */
			compareAndSetWaitStatus(pred, 0, Node.SIGNAL);
			return false;
		}
		
		private static void selfInterrupt() {
			Thread.currentThread().interrupt();
		}
		
		private final boolean parkAndCheckInterrupt() {
			LockSupport.park(this);
			return $interrupted;
		}
		
		final boolean acquireQueued(final Node node, int arg) {
			try {
				boolean interrupted = false;
				for (;;) {
					final Node p = node.predecessor();
					if (p == head && tryAcquire(arg)) {
						setHead(node);
						p.next = null; // help GC
						return interrupted;
					}
					if (shouldParkAfterFailedAcquire(p, node) && parkAndCheckInterrupt()) interrupted = true;
				}
			} catch (RuntimeException ex) {
				cancelAcquire(node);
				throw ex;
			}
		}
		
		/**
		 * Acquires in exclusive interruptible mode.
		 * 
		 * @param arg
		 *                the acquire argument
		 */
		private void doAcquireInterruptibly(int arg) throws InterruptedException {
			final Node node = addWaiter(Node.EXCLUSIVE);
			try {
				for (;;) {
					final Node p = node.predecessor();
					if (p == head && tryAcquire(arg)) {
						setHead(node);
						p.next = null; // help GC
						return;
					}
					if (shouldParkAfterFailedAcquire(p, node) && parkAndCheckInterrupt()) break;
				}
			} catch (RuntimeException ex) {
				cancelAcquire(node);
				throw ex;
			}
			// Arrive here only if interrupted
			cancelAcquire(node);
			throw new InterruptedException();
		}
		
		/**
		 * Acquires in exclusive timed mode.
		 * 
		 * @param arg
		 *                the acquire argument
		 * @param nanosTimeout
		 *                max wait time
		 * @return {@code true} if acquired
		 */
		private boolean doAcquireNanos(int arg, long nanosTimeout) throws InterruptedException {
			long lastTime = System.nanoTime();
			final Node node = addWaiter(Node.EXCLUSIVE);
			try {
				for (;;) {
					final Node p = node.predecessor();
					if (p == head && tryAcquire(arg)) {
						setHead(node);
						p.next = null; // help GC
						return true;
					}
					if (nanosTimeout <= 0) {
						cancelAcquire(node);
						return false;
					}
					if (nanosTimeout > spinForTimeoutThreshold && shouldParkAfterFailedAcquire(p, node)) LockSupport.parkNanos(this, nanosTimeout);
					long now = System.nanoTime();
					nanosTimeout -= now - lastTime;
					lastTime = now;
					if ($interrupted) break;
				}
			} catch (RuntimeException ex) {
				cancelAcquire(node);
				throw ex;
			}
			// Arrive here only if interrupted
			cancelAcquire(node);
			throw new InterruptedException();
		}
		
		/**
		 * Acquires in shared uninterruptible mode.
		 * 
		 * @param arg
		 *                the acquire argument
		 */
		private void doAcquireShared(int arg) {
			final Node node = addWaiter(Node.SHARED);
			try {
				boolean interrupted = false;
				for (;;) {
					final Node p = node.predecessor();
					if (p == head) {
						int r = tryAcquireShared(arg);
						if (r >= 0) {
							setHeadAndPropagate(node, r);
							p.next = null; // help GC
							if (interrupted) selfInterrupt();
							return;
						}
					}
					if (shouldParkAfterFailedAcquire(p, node) && parkAndCheckInterrupt()) interrupted = true;
				}
			} catch (RuntimeException ex) {
				cancelAcquire(node);
				throw ex;
			}
		}
		
		/**
		 * Acquires in shared interruptible mode.
		 * 
		 * @param arg
		 *                the acquire argument
		 */
		private void doAcquireSharedInterruptibly(int arg) throws InterruptedException {
			final Node node = addWaiter(Node.SHARED);
			try {
				for (;;) {
					final Node p = node.predecessor();
					if (p == head) {
						int r = tryAcquireShared(arg);
						if (r >= 0) {
							setHeadAndPropagate(node, r);
							p.next = null; // help GC
							return;
						}
					}
					if (shouldParkAfterFailedAcquire(p, node) && parkAndCheckInterrupt()) break;
				}
			} catch (RuntimeException ex) {
				cancelAcquire(node);
				throw ex;
			}
			// Arrive here only if interrupted
			cancelAcquire(node);
			throw new InterruptedException();
		}
		
		private boolean doAcquireSharedNanos(int arg, long nanosTimeout) throws InterruptedException {
			
			long lastTime = System.nanoTime();
			final Node node = addWaiter(Node.SHARED);
			try {
				for (;;) {
					final Node p = node.predecessor();
					if (p == head) {
						int r = tryAcquireShared(arg);
						if (r >= 0) {
							setHeadAndPropagate(node, r);
							p.next = null; // help GC
							return true;
						}
					}
					if (nanosTimeout <= 0) {
						cancelAcquire(node);
						return false;
					}
					if (nanosTimeout > spinForTimeoutThreshold && shouldParkAfterFailedAcquire(p, node)) LockSupport.parkNanos(this, nanosTimeout);
					long now = System.nanoTime();
					nanosTimeout -= now - lastTime;
					lastTime = now;
					if ($interrupted) break;
				}
			} catch (RuntimeException ex) {
				cancelAcquire(node);
				throw ex;
			}
			// Arrive here only if interrupted
			cancelAcquire(node);
			throw new InterruptedException();
		}
		
		protected boolean tryAcquire(int arg) {
			throw new UnsupportedOperationException();
		}
		
		protected boolean tryRelease(int arg) {
			throw new UnsupportedOperationException();
		}
		
		protected int tryAcquireShared(int arg) {
			throw new UnsupportedOperationException();
		}
		
		protected boolean isHeldExclusively() {
			throw new UnsupportedOperationException();
		}

		public final void acquire(int arg) {
			if (!tryAcquire(arg) && acquireQueued(addWaiter(Node.EXCLUSIVE), arg)) selfInterrupt();
		}

		public final void acquireInterruptibly(int arg) throws InterruptedException {
			if ($interrupted) throw new InterruptedException();
			if (!tryAcquire(arg)) doAcquireInterruptibly(arg);
		}
		
		public final boolean tryAcquireNanos(int arg, long nanosTimeout) throws InterruptedException {
			if ($interrupted) throw new InterruptedException();
			return tryAcquire(arg) || doAcquireNanos(arg, nanosTimeout);
		}
		
		public final boolean release(int arg) {
			if (tryRelease(arg)) {
				Node h = head;
				if (h != null && h.waitStatus != 0) unparkSuccessor(h);
				return true;
			}
			return false;
		}
		
		public final void acquireShared(int arg) {
			if (tryAcquireShared(arg) < 0) doAcquireShared(arg);
		}
		
		public final void acquireSharedInterruptibly(int arg) throws InterruptedException {
			if ($interrupted) throw new InterruptedException();
			if (tryAcquireShared(arg) < 0) doAcquireSharedInterruptibly(arg);
		}
		
		
		public final boolean tryAcquireSharedNanos(int arg, long nanosTimeout) throws InterruptedException {
			if ($interrupted) throw new InterruptedException();
			return tryAcquireShared(arg) >= 0 || doAcquireSharedNanos(arg, nanosTimeout);
		}
		
		
		public final boolean releaseShared(int arg) {
			if (tryReleaseShared(arg)) {
				Node h = head;
				if (h != null && h.waitStatus != 0) unparkSuccessor(h);
				return true;
			}
			return false;
		}
		
		public final boolean hasQueuedThreads() {
			return head != tail;
		}
		
		
		public final boolean hasContended() {
			return head != null;
		}
		
		
		public final Thread getFirstQueuedThread() {
			// handle only fast path, else relay
			return (head == tail) ? null : fullGetFirstQueuedThread();
		}
		
		private Thread fullGetFirstQueuedThread() {
			Node h, s;
			Thread st;
			if (((h = head) != null && (s = h.next) != null && s.prev == head && (st = s.thread) != null) || ((h = head) != null && (s = h.next) != null && s.prev == head && (st = s.thread) != null)) return st;
			
			Node t = tail;
			Thread firstThread = null;
			while (t != null && t != head) {
				Thread tt = t.thread;
				if (tt != null) firstThread = tt;
				t = t.prev;
			}
			return firstThread;
		}
		
		/**
		 * Returns true if the given thread is currently queued.
		 * 
		 * <p>
		 * This implementation traverses the queue to determine presence of the
		 * given thread.
		 * 
		 * @param thread
		 *                the thread
		 * @return {@code true} if the given thread is on the queue
		 * @throws NullPointerException
		 *                 if the thread is null
		 */
		public final boolean isQueued(Thread thread) {
			if (thread == null) throw new NullPointerException();
			for (Node p = tail; p != null; p = p.prev)
				if (p.thread == thread) return true;
			return false;
		}
		
		/**
		 * Return {@code true} if the apparent first queued thread, if one exists,
		 * is not waiting in exclusive mode. Used only as a heuristic in
		 * ReentrantReadWriteLock.
		 */
		final boolean apparentlyFirstQueuedIsExclusive() {
			Node h, s;
			return ((h = head) != null && (s = h.next) != null && s.nextWaiter != Node.SHARED);
		}
		
		/**
		 * Return {@code true} if the queue is empty or if the given thread is at
		 * the head of the queue. This is reliable only if <tt>current</tt> is
		 * actually Thread.currentThread() of caller.
		 */
		final boolean isFirst(Thread current) {
			Node h, s;
			return ((h = head) == null || ((s = h.next) != null && s.thread == current) || fullIsFirst(current));
		}
		
		final boolean fullIsFirst(Thread current) {
			// same idea as fullGetFirstQueuedThread
			Node h, s;
			Thread firstThread = null;
			if (((h = head) != null && (s = h.next) != null && s.prev == head && (firstThread = s.thread) != null)) return firstThread == current;
			Node t = tail;
			while (t != null && t != head) {
				Thread tt = t.thread;
				if (tt != null) firstThread = tt;
				t = t.prev;
			}
			return firstThread == current || firstThread == null;
		}
		
		
		// Instrumentation and monitoring methods
		
		/**
		 * Returns an estimate of the number of threads waiting to acquire. The
		 * value is only an estimate because the number of threads may change
		 * dynamically while this method traverses internal data structures. This
		 * method is designed for use in monitoring system state, not for
		 * synchronization control.
		 * 
		 * @return the estimated number of threads waiting to acquire
		 */
		public final int getQueueLength() {
			int n = 0;
			for (Node p = tail; p != null; p = p.prev) {
				if (p.thread != null) ++n;
			}
			return n;
		}
		
		/**
		 * Returns a collection containing threads that may be waiting to acquire.
		 * Because the actual set of threads may change dynamically while
		 * constructing this result, the returned collection is only a best-effort
		 * estimate. The elements of the returned collection are in no particular
		 * order. This method is designed to facilitate construction of subclasses
		 * that provide more extensive monitoring facilities.
		 * 
		 * @return the collection of threads
		 */
		public final Collection<Thread> getQueuedThreads() {
			ArrayList<Thread> list = new ArrayList<Thread>();
			for (Node p = tail; p != null; p = p.prev) {
				Thread t = p.thread;
				if (t != null) list.add(t);
			}
			return list;
		}
		
		/**
		 * Returns a collection containing threads that may be waiting to acquire
		 * in exclusive mode. This has the same properties as
		 * {@link #getQueuedThreads} except that it only returns those threads
		 * waiting due to an exclusive acquire.
		 * 
		 * @return the collection of threads
		 */
		public final Collection<Thread> getExclusiveQueuedThreads() {
			ArrayList<Thread> list = new ArrayList<Thread>();
			for (Node p = tail; p != null; p = p.prev) {
				if (!p.isShared()) {
					Thread t = p.thread;
					if (t != null) list.add(t);
				}
			}
			return list;
		}
		
		/**
		 * Returns a collection containing threads that may be waiting to acquire
		 * in shared mode. This has the same properties as
		 * {@link #getQueuedThreads} except that it only returns those threads
		 * waiting due to a shared acquire.
		 * 
		 * @return the collection of threads
		 */
		public final Collection<Thread> getSharedQueuedThreads() {
			ArrayList<Thread> list = new ArrayList<Thread>();
			for (Node p = tail; p != null; p = p.prev) {
				if (p.isShared()) {
					Thread t = p.thread;
					if (t != null) list.add(t);
				}
			}
			return list;
		}
		
		/**
		 * Returns a string identifying this synchronizer, as well as its state.
		 * The state, in brackets, includes the String {@code "State ="} followed
		 * by the current value of {@link #getState}, and either
		 * {@code "nonempty"} or {@code "empty"} depending on whether the queue is
		 * empty.
		 * 
		 * @return a string identifying this synchronizer, as well as its state
		 */
		public String toString() {
			int s = getState();
			String q = hasQueuedThreads() ? "non" : "";
			return super.toString() + "[State = " + s + ", " + q + "empty queue]";
		}
		
		
		// Internal support methods for Conditions
		
		/**
		 * Returns true if a node, always one that was initially placed on a
		 * condition queue, is now waiting to reacquire on sync queue.
		 * 
		 * @param node
		 *                the node
		 * @return true if is reacquiring
		 */
		final boolean isOnSyncQueue(Node node) {
			if (node.waitStatus == Node.CONDITION || node.prev == null) return false;
			if (node.next != null) // If has successor, it must be on queue
			return true;
			/*
			 * node.prev can be non-null, but not yet on queue because
			 * the CAS to place it on queue can fail. So we have to
			 * traverse from tail to make sure it actually made it.  It
			 * will always be near the tail in calls to this method, and
			 * unless the CAS failed (which is unlikely), it will be
			 * there, so we hardly ever traverse much.
			 */
			return findNodeFromTail(node);
		}
		
		/**
		 * Returns true if node is on sync queue by searching backwards from tail.
		 * Called only when needed by isOnSyncQueue.
		 * 
		 * @return true if present
		 */
		private boolean findNodeFromTail(Node node) {
			Node t = tail;
			for (;;) {
				if (t == node) return true;
				if (t == null) return false;
				t = t.prev;
			}
		}
		
		/**
		 * Transfers a node from a condition queue onto sync queue. Returns true
		 * if successful.
		 * 
		 * @param node
		 *                the node
		 * @return true if successfully transferred (else the node was cancelled
		 *         before signal).
		 */
		final boolean transferForSignal(Node node) {
			/*
			 * If cannot change waitStatus, the node has been cancelled.
			 */
			if (!compareAndSetWaitStatus(node, Node.CONDITION, 0)) return false;
			
			/*
			 * Splice onto queue and try to set waitStatus of predecessor to
			 * indicate that thread is (probably) waiting. If cancelled or
			 * attempt to set waitStatus fails, wake up to resync (in which
			 * case the waitStatus can be transiently and harmlessly wrong).
			 */
			Node p = enq(node);
			int c = p.waitStatus;
			if (c > 0 || !compareAndSetWaitStatus(p, c, Node.SIGNAL)) LockSupport.unpark(node.thread);
			return true;
		}
		
		/**
		 * Transfers node, if necessary, to sync queue after a cancelled wait.
		 * Returns true if thread was cancelled before being signalled.
		 * 
		 * @param node
		 *                its node
		 * @return true if cancelled before the node was signalled.
		 */
		final boolean transferAfterCancelledWait(Node node) {
			if (compareAndSetWaitStatus(node, Node.CONDITION, 0)) {
				enq(node);
				return true;
			}
			while (!isOnSyncQueue(node))
				Thread.yield();
			return false;
		}
		
		final int fullyRelease(Node node) {
			try {
				int savedState = getState();
				if (release(savedState)) return savedState;
			} catch (RuntimeException ex) {
				node.waitStatus = Node.CANCELLED;
				throw ex;
			}
			// reach here if release fails
			node.waitStatus = Node.CANCELLED;
			throw new IllegalMonitorStateException();
		}
		
		// Instrumentation methods for conditions
		
		/**
		 * Queries whether the given ConditionObject uses this synchronizer as its
		 * lock.
		 * 
		 * @param condition
		 *                the condition
		 * @return <tt>true</tt> if owned
		 * @throws NullPointerException
		 *                 if the condition is null
		 */
		public final boolean owns(ConditionObject condition) {
			if (condition == null) throw new NullPointerException();
			return condition.isOwnedBy(this);
		}
		
		/**
		 * Queries whether any threads are waiting on the given condition
		 * associated with this synchronizer. Note that because timeouts and
		 * interrupts may occur at any time, a <tt>true</tt> return does not
		 * guarantee that a future <tt>signal</tt> will awaken any threads. This
		 * method is designed primarily for use in monitoring of the system state.
		 * 
		 * @param condition
		 *                the condition
		 * @return <tt>true</tt> if there are any waiting threads
		 * @throws IllegalMonitorStateException
		 *                 if exclusive synchronization is not held
		 * @throws IllegalArgumentException
		 *                 if the given condition is not associated with this
		 *                 synchronizer
		 * @throws NullPointerException
		 *                 if the condition is null
		 */
		public final boolean hasWaiters(ConditionObject condition) {
			if (!owns(condition)) throw new IllegalArgumentException("Not owner");
			return condition.hasWaiters();
		}
		
		/**
		 * Returns an estimate of the number of threads waiting on the given
		 * condition associated with this synchronizer. Note that because timeouts
		 * and interrupts may occur at any time, the estimate serves only as an
		 * upper bound on the actual number of waiters. This method is designed
		 * for use in monitoring of the system state, not for synchronization
		 * control.
		 * 
		 * @param condition
		 *                the condition
		 * @return the estimated number of waiting threads
		 * @throws IllegalMonitorStateException
		 *                 if exclusive synchronization is not held
		 * @throws IllegalArgumentException
		 *                 if the given condition is not associated with this
		 *                 synchronizer
		 * @throws NullPointerException
		 *                 if the condition is null
		 */
		public final int getWaitQueueLength(ConditionObject condition) {
			if (!owns(condition)) throw new IllegalArgumentException("Not owner");
			return condition.getWaitQueueLength();
		}
		
		/**
		 * Returns a collection containing those threads that may be waiting on
		 * the given condition associated with this synchronizer. Because the
		 * actual set of threads may change dynamically while constructing this
		 * result, the returned collection is only a best-effort estimate. The
		 * elements of the returned collection are in no particular order.
		 * 
		 * @param condition
		 *                the condition
		 * @return the collection of threads
		 * @throws IllegalMonitorStateException
		 *                 if exclusive synchronization is not held
		 * @throws IllegalArgumentException
		 *                 if the given condition is not associated with this
		 *                 synchronizer
		 * @throws NullPointerException
		 *                 if the condition is null
		 */
		public final Collection<Thread> getWaitingThreads(ConditionObject condition) {
			if (!owns(condition)) throw new IllegalArgumentException("Not owner");
			return condition.getWaitingThreads();
		}
		
		/**
		 * Condition implementation for a {@link AbstractQueuedSynchronizer}
		 * serving as the basis of a Lock implementation.
		 * 
		 * <p>
		 * Method documentation for this class describes mechanics, not behavioral
		 * specifications from the point of view of Lock and Condition users.
		 * Exported versions of this class will in general need to be accompanied
		 * by documentation describing condition semantics that rely on those of
		 * the associated <tt>AbstractQueuedSynchronizer</tt>.
		 * 
		 * <p>
		 * This class is Serializable, but all fields are transient, so
		 * deserialized conditions have no waiters.
		 */
		public class ConditionObject implements Condition, java.io.Serializable {
			private static final long	serialVersionUID	= 1173984872572414699L;
			/** First node of condition queue. */
			private transient Node		firstWaiter;
			/** Last node of condition queue. */
			private transient Node		lastWaiter;
			
			/**
			 * Creates a new <tt>ConditionObject</tt> instance.
			 */
			public ConditionObject() {
			}
			
			// Internal methods
			
			/**
			 * Adds a new waiter to wait queue.
			 * 
			 * @return its new wait node
			 */
			private Node addConditionWaiter() {
				Node node = new Node(Thread.currentThread(), Node.CONDITION);
				Node t = lastWaiter;
				if (t == null) firstWaiter = node;
				else t.nextWaiter = node;
				lastWaiter = node;
				return node;
			}
			
			/**
			 * Removes and transfers nodes until hit non-cancelled one or
			 * null. Split out from signal in part to encourage compilers to
			 * inline the case of no waiters.
			 * 
			 * @param first
			 *                (non-null) the first node on condition queue
			 */
			private void doSignal(Node first) {
				do {
					if ((firstWaiter = first.nextWaiter) == null) lastWaiter = null;
					first.nextWaiter = null;
				} while (!transferForSignal(first) && (first = firstWaiter) != null);
			}
			
			/**
			 * Removes and transfers all nodes.
			 * 
			 * @param first
			 *                (non-null) the first node on condition queue
			 */
			private void doSignalAll(Node first) {
				lastWaiter = firstWaiter = null;
				do {
					Node next = first.nextWaiter;
					first.nextWaiter = null;
					transferForSignal(first);
					first = next;
				} while (first != null);
			}
			
			/**
			 * Returns true if given node is on this condition queue. Call
			 * only when holding lock.
			 */
			private boolean isOnConditionQueue(Node node) {
				return node.next != null || node == lastWaiter;
			}
			
			/**
			 * Unlinks a cancelled waiter node from condition queue. This is
			 * called when cancellation occurred during condition wait, not
			 * lock wait, and is called only after lock has been re-acquired
			 * by a cancelled waiter and the node is not known to already have
			 * been dequeued. It is needed to avoid garbage retention in the
			 * absence of signals. So even though it may require a full
			 * traversal, it comes into play only when timeouts or
			 * cancellations occur in the absence of signals.
			 */
			private void unlinkCancelledWaiter(Node node) {
				Node t = firstWaiter;
				Node trail = null;
				while (t != null) {
					if (t == node) {
						Node next = t.nextWaiter;
						if (trail == null) firstWaiter = next;
						else trail.nextWaiter = next;
						if (lastWaiter == node) lastWaiter = trail;
						break;
					}
					trail = t;
					t = t.nextWaiter;
				}
			}
			
			// public methods
			
			/**
			 * Moves the longest-waiting thread, if one exists, from the wait
			 * queue for this condition to the wait queue for the owning lock.
			 * 
			 * @throws IllegalMonitorStateException
			 *                 if {@link #isHeldExclusively} returns
			 *                 {@code false}
			 */
			public final void signal() {
				if (!isHeldExclusively()) throw new IllegalMonitorStateException();
				Node first = firstWaiter;
				if (first != null) doSignal(first);
			}
			
			/**
			 * Moves all threads from the wait queue for this condition to the
			 * wait queue for the owning lock.
			 * 
			 * @throws IllegalMonitorStateException
			 *                 if {@link #isHeldExclusively} returns
			 *                 {@code false}
			 */
			public final void signalAll() {
				if (!isHeldExclusively()) throw new IllegalMonitorStateException();
				Node first = firstWaiter;
				if (first != null) doSignalAll(first);
			}
			
			/**
			 * Implements uninterruptible condition wait.
			 * <ol>
			 * <li>Save lock state returned by {@link #getState}
			 * <li>Invoke {@link #release} with saved state as argument,
			 * throwing IllegalMonitorStateException if it fails.
			 * <li>Block until signalled
			 * <li>Reacquire by invoking specialized version of
			 * {@link #acquire} with saved state as argument.
			 * </ol>
			 */
			public final void awaitUninterruptibly() {
				Node node = addConditionWaiter();
				int savedState = fullyRelease(node);
				boolean interrupted = false;
				while (!isOnSyncQueue(node)) {
					LockSupport.park(this);
					if ($interrupted) interrupted = true;
				}
				if (acquireQueued(node, savedState) || interrupted) selfInterrupt();
			}
			
			/*
			 * For interruptible waits, we need to track whether to throw
			 * InterruptedException, if interrupted while blocked on
			 * condition, versus reinterrupt current thread, if
			 * interrupted while blocked waiting to re-acquire.
			 */

			/** Mode meaning to reinterrupt on exit from wait */
			private static final int	REINTERRUPT	= 1;
			/** Mode meaning to throw InterruptedException on exit from wait */
			private static final int	THROW_IE	= -1;
			
			/**
			 * Checks for interrupt, returning THROW_IE if interrupted before
			 * signalled, REINTERRUPT if after signalled, or 0 if not
			 * interrupted.
			 */
			private int checkInterruptWhileWaiting(Node node) {
				return ($interrupted) ? ((transferAfterCancelledWait(node)) ? THROW_IE : REINTERRUPT) : 0;
			}
			
			/**
			 * Throws InterruptedException, reinterrupts current thread, or
			 * does nothing, depending on mode.
			 */
			private void reportInterruptAfterWait(int interruptMode) throws InterruptedException {
				if (interruptMode == THROW_IE) throw new InterruptedException();
				else if (interruptMode == REINTERRUPT) selfInterrupt();
			}
			
			/**
			 * Implements interruptible condition wait.
			 * <ol>
			 * <li>If current thread is interrupted, throw
			 * InterruptedException
			 * <li>Save lock state returned by {@link #getState}
			 * <li>Invoke {@link #release} with saved state as argument,
			 * throwing IllegalMonitorStateException if it fails.
			 * <li>Block until signalled or interrupted
			 * <li>Reacquire by invoking specialized version of
			 * {@link #acquire} with saved state as argument.
			 * <li>If interrupted while blocked in step 4, throw exception
			 * </ol>
			 */
			public final void await() throws InterruptedException {
				if ($interrupted) throw new InterruptedException();
				Node node = addConditionWaiter();
				int savedState = fullyRelease(node);
				int interruptMode = 0;
				while (!isOnSyncQueue(node)) {
					LockSupport.park(this);
					if ((interruptMode = checkInterruptWhileWaiting(node)) != 0) break;
				}
				if (acquireQueued(node, savedState) && interruptMode != THROW_IE) interruptMode = REINTERRUPT;
				if (isOnConditionQueue(node)) unlinkCancelledWaiter(node);
				if (interruptMode != 0) reportInterruptAfterWait(interruptMode);
			}
			
			/**
			 * Implements timed condition wait.
			 * <ol>
			 * <li>If current thread is interrupted, throw
			 * InterruptedException
			 * <li>Save lock state returned by {@link #getState}
			 * <li>Invoke {@link #release} with saved state as argument,
			 * throwing IllegalMonitorStateException if it fails.
			 * <li>Block until signalled, interrupted, or timed out
			 * <li>Reacquire by invoking specialized version of
			 * {@link #acquire} with saved state as argument.
			 * <li>If interrupted while blocked in step 4, throw
			 * InterruptedException
			 * </ol>
			 */
			public final long awaitNanos(long nanosTimeout) throws InterruptedException {
				if ($interrupted) throw new InterruptedException();
				Node node = addConditionWaiter();
				int savedState = fullyRelease(node);
				long lastTime = System.nanoTime();
				int interruptMode = 0;
				while (!isOnSyncQueue(node)) {
					if (nanosTimeout <= 0L) {
						transferAfterCancelledWait(node);
						break;
					}
					LockSupport.parkNanos(this, nanosTimeout);
					if ((interruptMode = checkInterruptWhileWaiting(node)) != 0) break;
					
					long now = System.nanoTime();
					nanosTimeout -= now - lastTime;
					lastTime = now;
				}
				if (acquireQueued(node, savedState) && interruptMode != THROW_IE) interruptMode = REINTERRUPT;
				if (isOnConditionQueue(node)) unlinkCancelledWaiter(node);
				if (interruptMode != 0) reportInterruptAfterWait(interruptMode);
				return nanosTimeout - (System.nanoTime() - lastTime);
			}
			
			public final boolean awaitUntil(Date deadline) throws InterruptedException {
				if (deadline == null) throw new NullPointerException();
				long abstime = deadline.getTime();
				if ($interrupted) throw new InterruptedException();
				Node node = addConditionWaiter();
				int savedState = fullyRelease(node);
				boolean timedout = false;
				int interruptMode = 0;
				while (!isOnSyncQueue(node)) {
					if (System.currentTimeMillis() > abstime) {
						timedout = transferAfterCancelledWait(node);
						break;
					}
					LockSupport.parkUntil(this, abstime);
					if ((interruptMode = checkInterruptWhileWaiting(node)) != 0) break;
				}
				if (acquireQueued(node, savedState) && interruptMode != THROW_IE) interruptMode = REINTERRUPT;
				if (isOnConditionQueue(node)) unlinkCancelledWaiter(node);
				if (interruptMode != 0) reportInterruptAfterWait(interruptMode);
				return !timedout;
			}
			
			public final boolean await(long time, TimeUnit unit) throws InterruptedException {
				if (unit == null) throw new NullPointerException();
				long nanosTimeout = unit.toNanos(time);
				if ($interrupted) throw new InterruptedException();
				Node node = addConditionWaiter();
				int savedState = fullyRelease(node);
				long lastTime = System.nanoTime();
				boolean timedout = false;
				int interruptMode = 0;
				while (!isOnSyncQueue(node)) {
					if (nanosTimeout <= 0L) {
						timedout = transferAfterCancelledWait(node);
						break;
					}
					LockSupport.parkNanos(this, nanosTimeout);
					if ((interruptMode = checkInterruptWhileWaiting(node)) != 0) break;
					long now = System.nanoTime();
					nanosTimeout -= now - lastTime;
					lastTime = now;
				}
				if (acquireQueued(node, savedState) && interruptMode != THROW_IE) interruptMode = REINTERRUPT;
				if (isOnConditionQueue(node)) unlinkCancelledWaiter(node);
				if (interruptMode != 0) reportInterruptAfterWait(interruptMode);
				return !timedout;
			}
			
			final boolean isOwnedBy(Sync sync) {
				return sync == Sync.this;
			}
			
			protected final boolean hasWaiters() {
				if (!isHeldExclusively()) throw new IllegalMonitorStateException();
				for (Node w = firstWaiter; w != null; w = w.nextWaiter) {
					if (w.waitStatus == Node.CONDITION) return true;
				}
				return false;
			}
			
			protected final int getWaitQueueLength() {
				if (!isHeldExclusively()) throw new IllegalMonitorStateException();
				int n = 0;
				for (Node w = firstWaiter; w != null; w = w.nextWaiter) {
					if (w.waitStatus == Node.CONDITION) ++n;
				}
				return n;
			}
			
			protected final Collection<Thread> getWaitingThreads() {
				if (!isHeldExclusively()) throw new IllegalMonitorStateException();
				ArrayList<Thread> list = new ArrayList<Thread>();
				for (Node w = firstWaiter; w != null; w = w.nextWaiter) {
					if (w.waitStatus == Node.CONDITION) {
						Thread t = w.thread;
						if (t != null) list.add(t);
					}
				}
				return list;
			}
		}
		
		/**
		 * Setup to support compareAndSet. We need to natively implement this
		 * here: For the sake of permitting future enhancements, we cannot
		 * explicitly subclass AtomicInteger, which would be efficient and useful
		 * otherwise. So, as the lesser of evils, we natively implement using
		 * hotspot intrinsics API. And while we are at it, we do the same for
		 * other CASable fields (which could otherwise be done with atomic field
		 * updaters).
		 */
		static {
			try {
			        Field field = Unsafe.class.getDeclaredField("theUnsafe");
			        field.setAccessible(true);
			        unsafe = (Unsafe)field.get(null);
			    } catch (Exception ex) {
			        throw new Error("can't get Unsafe instance", ex);
			    }
		}
		private static final Unsafe	unsafe;
		private static final long	headOffset;
		private static final long	tailOffset;
		
		static {
			try {
				headOffset = unsafe.objectFieldOffset(AbstractQueuedSynchronizer.class.getDeclaredField("head"));
				tailOffset = unsafe.objectFieldOffset(AbstractQueuedSynchronizer.class.getDeclaredField("tail"));
			} catch (Exception ex) {
				throw new Error(ex);
			}
		}
		
		/**
		 * CAS head field. Used only by enq
		 */
		private final boolean compareAndSetHead(Node update) {
			return unsafe.compareAndSwapObject(this, headOffset, null, update);
		}
		
		/**
		 * CAS tail field. Used only by enq
		 */
		private final boolean compareAndSetTail(Node expect, Node update) {
			//AtomicReferenceFieldUpdater
			return unsafe.compareAndSwapObject(this, tailOffset, expect, update);
		}
		
		/**
		 * CAS waitStatus field of a node.
		 */
		private final static boolean compareAndSetWaitStatus(Node node, int expect, int update) {
			synchronized (node) {
				if (node.waitStatus == expect) {
					node.waitStatus = update;
					return true;
				} return false;
			}
		}
	}
	
	/**
	 * NonFair version
	 */
	final static class NonfairSync extends Sync {
		NonfairSync(int permits) {
			super(permits);
		}
		
		protected int tryAcquireShared(int acquires) {
			return nonfairTryAcquireShared(acquires);
		}
	}
	
	/**
	 * Fair version
	 */
	final static class FairSync extends Sync {
		FairSync(int permits) {
			super(permits);
		}
		
		protected int tryAcquireShared(int acquires) {
			Thread current = Thread.currentThread();
			for (;;) {
				Thread first = getFirstQueuedThread();
				if (first != null && first != current) return -1;
				int available = getState();
				int remaining = available - acquires;
				if (remaining < 0 || compareAndSetState(available, remaining)) return remaining;
			}
		}
	}
	
	
	
	
	
	public InterruptableSemaphore(int permits) {
		sync = new NonfairSync(permits);
	}
	
	public InterruptableSemaphore(int permits, boolean fair) {
		sync = (fair) ? new FairSync(permits) : new NonfairSync(permits);
	}
	
	public void acquire() throws InterruptedException {
		sync.acquireSharedInterruptibly(1);
	}
	
	public void acquireUninterruptibly() {
		sync.acquireShared(1);
	}
	
	public boolean tryAcquire() {
		return sync.nonfairTryAcquireShared(1) >= 0;
	}
	
	public boolean tryAcquire(long timeout, TimeUnit unit) throws InterruptedException {
		return sync.tryAcquireSharedNanos(1, unit.toNanos(timeout));
	}
	
	public void release() {
		sync.releaseShared(1);
	}
	
	public void acquire(int permits) throws InterruptedException {
		if (permits < 0) throw new IllegalArgumentException();
		sync.acquireSharedInterruptibly(permits);
	}
	
	public void acquireUninterruptibly(int permits) {
		if (permits < 0) throw new IllegalArgumentException();
		sync.acquireShared(permits);
	}
	
	public boolean tryAcquire(int permits) {
		if (permits < 0) throw new IllegalArgumentException();
		return sync.nonfairTryAcquireShared(permits) >= 0;
	}
	
	public boolean tryAcquire(int permits, long timeout, TimeUnit unit) throws InterruptedException {
		if (permits < 0) throw new IllegalArgumentException();
		return sync.tryAcquireSharedNanos(permits, unit.toNanos(timeout));
	}
	
	public void release(int permits) {
		if (permits < 0) throw new IllegalArgumentException();
		sync.releaseShared(permits);
	}
	
	public int availablePermits() {
		return sync.getPermits();
	}
	
	public int drainPermits() {
		return sync.drainPermits();
	}
	
	protected void reducePermits(int reduction) {
		if (reduction < 0) throw new IllegalArgumentException();
		sync.reducePermits(reduction);
	}
	
	public boolean isFair() {
		return sync instanceof FairSync;
	}
	
	public final boolean hasQueuedThreads() {
		return sync.hasQueuedThreads();
	}
	
	public final int getQueueLength() {
		return sync.getQueueLength();
	}
	
	protected Collection<Thread> getQueuedThreads() {
		return sync.getQueuedThreads();
	}
	
	public String toString() {
		return super.toString() + "[Permits = " + sync.getPermits() + "]";
	}
}
