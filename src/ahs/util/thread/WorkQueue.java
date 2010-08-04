package ahs.util.thread;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

/**
 * <p>
 * This class behaves almost exactly as per ConcurrentLinkedQueue (and is implemented
 * using it), but also provides additional semantics that allow one to determine at least
 * how many entries are in the queue as a constant time operation, and also provides a
 * blocking get functionality. This allows easy use as a work distributor, since one can
 * make get calls that will not return null simply because work is not instantaneously
 * available.
 * </p>
 * 
 * <p>
 * Requests that are blocking in nature are given fair ordering; nonblocking requests
 * disregard this entirely.
 * </p>
 * 
 * <p>
 * It may in many cases be easiest to use this as a sort of "pipe" to connect a
 * <code>ReadHead<$T></code> to a <code>WriteHead<$T></code>.
 * </p>
 * 
 * @author hash
 * 
 */
public class WorkQueue<$T> implements Queue<$T> {
	public WorkQueue() {
		$queue = new ConcurrentLinkedQueue<$T>();
		$gate = new Semaphore(0, true);	// fair.
		$lw = new ReentrantLock();
		$lr = new ReentrantLock();
	}
	
	private final Lock			$lw;
	private final ConcurrentLinkedQueue<$T>	$queue;	// someday i might reimplement this myself since i end up doubling the locks on each end the way i've done it.
	/**
	 * we -always- update this -before- the queue so that it's a -minimal- value. it
	 * may sometimes severely underestimate the available work in order to provide
	 * this service with guaranteeable correctness, since reading the amount of
	 * available work from this semaphore is an operation that is never blocked. (this
	 * comes up when operations that may potentially modify the entire queue are in
	 * progress as opposed to the more common operations that simply effect the head
	 * or tail of the queue).
	 * 
	 * when write is locked, it can't grow (but it can still shrink, even when read is
	 * locked, because the semaphore is used to synchronize and order read requests
	 * even before the read lock is invoked).
	 */
	private final Semaphore			$gate;
	private final Lock			$lr;
	
	private void lockWrite() {
		$lw.lock();
	}
	private void lockRead() {
		$lr.lock();
	}
	private void lockAll() {
		$lw.lock();
		$lr.lock();
	}
	private void unlockWrite() {
		$lw.unlock();
	}
	private void unlockRead() {
		$lr.unlock();
	}
	private void unlockAll() {
		$lr.unlock();
		$lw.unlock();
	}
	
	
	
	
	
	/** {@inheritDoc} */
	public boolean add($T $more) {
		lockWrite();
		$queue.add($more);
		$gate.release();
		unlockWrite();
		return true;	// really
	}
	
	/**
	 * Since this queue does not provide a capacity restriction, this method is the
	 * same as calling <code>add($more)</code>. (Subclasses that do provide capacity
	 * restriction may choose to make this a blocking call.)
	 */
	public boolean offer($T $more) {
		lockWrite();
		$queue.add($more);
		$gate.release();
		unlockWrite();
		return true;	// really
	}
	
	/**
	 * {@inheritDoc}
	 * Use this method with extreme caution in a multithreaded case, since 
	 * consecutive invocations can not be expected to yield the same result.
	 */
	public $T peek() {
		// doesn't need a permit, because it's not guaranteed to return anything
		// in fact, i don't think it even needs to lock, because isn't not modifying anything
		return $queue.peek();
	}
	
	/**
	 * This deviates from the standard contract of the Queue interface. This method
	 * will not throw a NoSuchElementException when the queue is empty; instead it
	 * simply blocks until some data can be returned. Its use is simply not
	 * recommended.
	 */
	public $T element() {
		// this guy needs a permit even though he's not removing since he needs to return something
		$gate.acquireUninterruptibly();	// doing this outside of a lock scares me
		lockRead();
		$T $v = $queue.element();
		$gate.release();	// this must be done inside the lock for voodoo reasons.  (remove(Object))
		unlockRead();
		return $v;
	}
	
	/** {@inheritDoc} */
	public $T poll() {
		boolean $one = $gate.tryAcquire();	// doing this outside of a lock scares me
		if (!$one) return null;
		lockRead();
		$T $v = $queue.poll();
		unlockRead();
		return $v;
	}

	/**
	 * This deviates from the standard contract of the Queue interface. This method
	 * will not throw a NoSuchElementException when the queue is empty; instead it
	 * simply blocks until some data can be returned.
	 */
	public $T remove() {
		$gate.acquireUninterruptibly();	// doing this outside of a lock scares me
		lockRead();
		$T $v = $queue.remove();
		unlockRead();
		return $v;
	}
	
	
	
	/** {@inheritDoc} */
	public boolean addAll(Collection<? extends $T> $more) {
		lockWrite();
		boolean $victory = $queue.addAll($more);
		$gate.release($more.size());	// i think this is safe with this particular queue, since it never rejects elements without throwing exceptions.
		unlockWrite();
		return $victory;
	}

	/** {@inheritDoc} */
	public void clear() {
		lockAll();
		$gate.drainPermits();
		$queue.clear();
		unlockAll();
	}
	
	/** {@inheritDoc} */
	public boolean contains(Object $o) {
		lockAll();
		boolean $victory = $queue.contains($o);
		unlockAll();
		return $victory;
	}
	
	public boolean containsAll(Collection<?> $c) {
		lockAll();
		boolean $victory = $queue.containsAll($c);
		unlockAll();
		return $victory;
	}
	
	/** {@inheritDoc}
	 * See the <code>size()</code> method for more -- this method uses that internally.
	 */
	public boolean isEmpty() {
		return (size() < 1);
	}
	
	/** Not supported. */
	public Iterator<$T> iterator() {
		throw new UnsupportedOperationException();
	}
	
	/** {@inheritDoc} */
	public boolean remove(Object $o) {
		// I wanted to drain one permit so no one overworks, then check/remove, then restore a permit if no remove.  turns out i can't without a different semaphore implementation since that might involve an overdraw itself.  soooo we lock.
		lockAll();
		boolean $one = $gate.tryAcquire();
		// nobody else can be modifying the list right now (add or remove)
		// which means that while the gate is readable, it is NOT writable
		// and thus the gate's estimate of how many things is in the queue is not just a minimum anymore; it's exact. 
		// so if we didn't get a permit, we can just skip the search because the list is certainly empty.
		if (!$one) {
			unlockAll();
			return false;
		} else {
			boolean $victory = $queue.remove($o);
			if (!$victory) $gate.release();
			// we don't release the permit from the tryAcquire if we did remove something... obviously
			unlockAll();
			return $victory;
		}
	}
	
	/** {@inheritDoc} */
	public boolean removeAll(Collection<?> $c) {
		lockAll();
		$gate.drainPermits();
		boolean $victory = $queue.removeAll($c);
		$gate.release($queue.size());
		unlockAll();
		return $victory;
	}
	
	/** {@inheritDoc} */
	public boolean retainAll(Collection<?> $c) {
		lockAll();
		$gate.drainPermits();
		boolean $victory = $queue.retainAll($c);
		$gate.release($queue.size());
		unlockAll();
		return $victory;
	}
	
	/**
	 * <p>
	 * This method immediately (without blocking) returns the minimal amount of data
	 * available. It may sometimes severely underestimate the available data in order
	 * to provide this service with guaranteeable correctness (this comes up when
	 * operations that may potentially modify the entire queue are in progress since
	 * such operations might possibly leave the queue with no elements; the more
	 * common operations that simply effect the head or tail of the queue do not have
	 * such erratic effects on the minimal available data).
	 * </p>
	 * 
	 * <p>
	 * In order to get the exact size of the underlying queue, use
	 * <code>qsize()</code> -- that method performs a linear walk of the underlying
	 * queue in order to determine exact size.
	 * </p>
	 */
	public int size() {
		return $gate.availablePermits();
	}
	
	/**
	 * <p>
	 * Returns the exact size of the underlying queue.
	 * </p>
	 * 
	 * <p>
	 * Beware that this method is <i>NOT</i> a constant-time operation. Because of the
	 * asynchronous nature of these queues, determining the current number of elements
	 * requires an O(n) traversal.
	 * </p>
	 */
	public int qsize() {
		return $queue.size();
	}
	
	/** {@inheritDoc} 
	 * <p>
	 * Note that this does not remove any of the gathered objects from the queue.
	 * </p>
	 */
	public $T[] toArray() {
		lockAll();
		@SuppressWarnings("unchecked")
		$T[] $v = ($T[]) $queue.toArray();
		unlockAll();
		return $v;
	}
	
	/** {@inheritDoc} 
	 * <p>
	 * Note that this does not remove any of the gathered objects from the queue.
	 * </p>
	 */
	public <$A> $A[] toArray($A[] $a) {
		lockAll();
		$A[] $v = $queue.toArray($a);
		unlockAll();
		return $v;
	}
}
