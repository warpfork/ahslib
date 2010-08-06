package ahs.util.thread;

import java.util.*;
import java.util.concurrent.*;

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
	}
	
	private final ConcurrentLinkedQueue<$T>	$queue;
	/**
	 * we -always- update this -before- the queue so that it's a -minimal- value. it
	 * may sometimes severely underestimate the available work in order to provide
	 * this service with guaranteeable correctness, since reading the amount of
	 * available work from this semaphore is an operation that is never blocked. (this
	 * comes up when operations that may potentially modify the entire queue are in
	 * progress. the more common operations that simply effect the head or tail of the
	 * queue will not lead to drastic varation in the number of permits available to
	 * the semphore).
	 * 
	 * when write is locked, it can't grow (but it can still shrink, even when read is
	 * locked, because the semaphore is used to synchronize and order read requests
	 * even before the read lock is invoked). thus, if write is locked and all permits
	 * are then drained, read is effectly completely locked as well.
	 */
	private final Semaphore			$gate;
	
	public int interrupt() {
		return $gate.getQueueLength();
		//TODO:AHS: how do i do this without timed cycles on the acquisition (or using actual interruption)?
	}
	
	
	
	/** {@inheritDoc} */
	public boolean add($T $more) {
		synchronized ($queue) {
			$queue.add($more);
			$gate.release();
		}
		return true;	// really
	}
	
	/**
	 * Since this queue does not provide a capacity restriction, this method is the
	 * same as calling <code>add($more)</code>. (Subclasses that do provide capacity
	 * restriction may choose to make this a blocking call.)
	 */
	public boolean offer($T $more) {
		synchronized ($queue) {
			$queue.add($more);
			$gate.release();
		}
		return true;	// really
	}
	
	/**
	 * {@inheritDoc}
	 * Use this method with extreme caution in a multithreaded case, since 
	 * consecutive invocations can not be expected to yield the same result.
	 */
	public $T peek() {
		// doesn't need a permit, because it's not guaranteed to return anything
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
		$gate.acquireUninterruptibly();
		$T $v = $queue.element();
		$gate.release();
		return $v;
	}
	
	/** {@inheritDoc} */
	public $T poll() {
		boolean $one = $gate.tryAcquire();
		if (!$one) return null;
		$T $v = $queue.poll();
		return $v;
	}

	/**
	 * This deviates from the standard contract of the Queue interface. This method
	 * will not throw a NoSuchElementException when the queue is empty; instead it
	 * simply blocks until some data can be returned.
	 */
	public $T remove() {
		$gate.acquireUninterruptibly();
		$T $v = $queue.remove();
		return $v;
	}
	
	
	
	/** {@inheritDoc} */
	public boolean addAll(Collection<? extends $T> $more) {
		synchronized ($queue) {
			boolean $victory = $queue.addAll($more);
			$gate.release($more.size());	// i think this is safe with this particular queue, since it never rejects elements without throwing exceptions.  alternative is to recount queue size like we had to do in the removeAll method.
			return $victory;
		}
	}

	/** {@inheritDoc} */
	public void clear() {
		synchronized ($queue) {
			$gate.drainPermits();
			$queue.clear();
		}
	}
	
	/** {@inheritDoc} */
	public boolean contains(Object $o) {
		return $queue.contains($o);	// since these don't mutate anything, queue's internal locking is sufficient
	}
	
	public boolean containsAll(Collection<?> $c) {
		return $queue.containsAll($c);	// since these don't mutate anything, queue's internal locking is sufficient
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
		synchronized ($queue) {
			boolean $one = $gate.tryAcquire();
			// nobody else can be modifying the list right now (add or remove)
			// which means that while the gate is readable, it is NOT writable
			// and thus the gate's estimate of how many things is in the queue is not just a minimum anymore; it's exact. 
			// so if we didn't get a permit, we can just skip the search because the list is certainly empty.
			if (!$one) {
				return false;
			} else {
				boolean $victory = $queue.remove($o);
				if (!$victory) $gate.release();
				// we don't release the permit from the tryAcquire if we did remove something... obviously
				return $victory;
			}
		}
	}
	
	/** {@inheritDoc} */
	public boolean removeAll(Collection<?> $c) {
		synchronized ($queue) {
			$gate.drainPermits();
			boolean $victory = $queue.removeAll($c);
			$gate.release($queue.size());
			return $victory;
		}
	}
	
	/** {@inheritDoc} */
	public boolean retainAll(Collection<?> $c) {
		synchronized ($queue) {
			$gate.drainPermits();
			boolean $victory = $queue.retainAll($c);
			$gate.release($queue.size());
			return $victory;
		}
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
	@SuppressWarnings("unchecked")
	public $T[] toArray() {
		return ($T[])$queue.toArray();	// since these don't mutate anything, queue's internal locking is sufficient
	}
	
	/** {@inheritDoc} 
	 * <p>
	 * Note that this does not remove any of the gathered objects from the queue.
	 * </p>
	 */
	public <$A> $A[] toArray($A[] $a) {
		return $queue.toArray($a);	// since these don't mutate anything, queue's internal locking is sufficient
	}
}
