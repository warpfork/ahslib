package ahs.io;

import ahs.util.*;
import ahs.util.thread.*;

import java.io.*;
import java.util.concurrent.*;

public class Pipe<$T> {
	public Pipe() {
		$queue = new ConcurrentLinkedQueue<$T>();
		$gate = new Semaphore(0, true);	// fair.
		SRC = new Source();
		SINK = new Sink();
	}
	
	public final ReadHead<$T>		SRC;
	public final WriteHead<$T>		SINK;
	
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
	private boolean				$closed;
	
	
	
	private class Source implements ReadHead<$T> {
		private Listener<ReadHead<$T>>		$el;
		private ExceptionHandler<IOException>	$eh;
		
		public Pump getPump() {
			//TODO
			return null;
		}

		public void setExceptionHandler(ExceptionHandler<IOException> $eh) {
			this.$eh = $eh;
		}

		public void setListener(Listener<ReadHead<$T>> $el) {
			this.$el = $el;
		}

		public $T read() {
			try {
				$gate.acquire(); // using an interrupt in this way has the potential to violate the contract of this method laid out in the ReadHead interface, since other threads could issue an interrupt for no reason whatsoever.
			} catch (InterruptedException $e) {
				// closing the stream here would not solve the above issue, since null still shouldn't happen until all buffers are empty
				// oh wait... that actually would be fine, since we shouldn't be blocking if there's nothing in the buffer anyway
				// NOPE still doesn't work, acquire method checks for preexisting interrupts
				// who the fuck wrote this stupid interrupt system anyway.  jesus.
				return null;
				// suppose we could close underlying, kill the pump, and then drain all permits all in one fell swoop before returning... but that's just... nuts.
			}
			$T $v = $queue.remove();
			return $v;
		}
		
		public $T readNow() {
			boolean $one = $gate.tryAcquire();
			if (!$one) return null;
			$T $v = $queue.poll();
			return $v;
		}
		
		public boolean hasNext() {
			return $gate.availablePermits() > 0;
		}
		
		public $T[] readAll() {
			//TODO
			return null;
		}
		
		public $T[] readAllNow() {
			synchronized ($queue) {
				$gate.drainPermits();
				@SuppressWarnings("unchecked")
				$T[] $v = ($T[])$queue.toArray();
				$queue.clear();
				return $v;
			}
		}
		
		public boolean isClosed() {
			return $closed;
		}
		
		public void close() {
			$closed = true;
		}
		
		private void clear() {
			synchronized ($queue) {
				$gate.drainPermits();
				$queue.clear();
			}
		}
	}
	
	private class Sink implements WriteHead<$T> {
		
	}
}
