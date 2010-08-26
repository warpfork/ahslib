package ahs.io;

import ahs.util.*;
import ahs.util.thread.*;

import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.concurrent.*;

public class Pipe<$T> {
	public Pipe() {
		$closed = new boolean[] { false };
		$queue = new ConcurrentLinkedQueue<$T>();
		$gate = new InterruptableSemaphore(0, true);	// fair.
		SRC = new Source();
		SINK = new Sink();
	}
	
	// i'm perfectly capable of using just the ReadHead and WriteHead interfaces internally as well... but this lets clients avoid having to always wrap the write calls in a no-op try-catch block.
	public final Source			SRC;
	public final Sink			SINK;
	private volatile Listener<ReadHead<$T>>	$el;
	
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
	private final InterruptableSemaphore	$gate;
	private final boolean[]			$closed;
	
	public final class Source implements ReadHead<$T> {
		private Source() {}	// this should be a singleton per instance of the enclosing class
		
		public Pump getPump() {
			return null;
		}
		
		public void setExceptionHandler(ExceptionHandler<IOException> $eh) {
			// do nothing.  we're not even capable of having exceptions.
		}
		
		public void setListener(Listener<ReadHead<$T>> $el) {
			Pipe.this.$el = $el;
		}
		
		public $T read() {
			try {
				$gate.acquire();
			} catch (InterruptedException $e) {
				return null;
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
		
		public List<$T> readAll() {
			waitForClose();
			return readAllNow();
		}
		
		public List<$T> readAllNow() {
			synchronized ($queue) {
				int $p = $gate.drainPermits();
				List<$T> $v = new ArrayList<$T>($p);
				for (int $i = 0; $i < $p; $i++)
					$v.add($queue.poll());
				return $v;
			}
		}
		
		public boolean isClosed() {
			return $closed[0];
		}
		
		public void close() {
			$closed[0] = true;
			$gate.interrupt();
			X.notifyAll($closed);
		}
		
		private void waitForClose() {
			synchronized ($closed) {
				while (!isClosed())
					X.wait($closed);
			}
		}
	}
	
	public final class Sink implements WriteHead<$T> {
		private Sink() {}	// this should be a singleton per instance of the enclosing class
		
		public void write($T $chunk) {
			synchronized ($queue) {
				$queue.add($chunk);
				$gate.release();
				
				Listener<ReadHead<$T>> $el_dated = Pipe.this.$el;
				if ($el_dated != null) $el_dated.hear(SRC);
			}
		}
		
		public void writeAll(Collection<? extends $T> $chunks) {
			// at first i thought i could implement this with addAll on the queue and a single big release... not actually so.  addAll on the queue can throw exceptions but still have made partial progress.
			synchronized ($queue) {
				for ($T $chunk : $chunks)
					write($chunk);
			}
		}
		
		public boolean hasRoom() {
			return true;	// we don't implement any capacity restrictions, so this isn't really ever in question.
		}
		
		public boolean isClosed() {
			return $closed[0];
		}
		
		public void close() {
			SRC.close();
		}
	}
}
