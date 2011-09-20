package us.exultant.ahs.thread;

import java.util.concurrent.*;
import java.util.concurrent.locks.*;

/**
 * Produced internally by some WorkScheduler implementations for bookkeeping and return to
 * the function that scheduled a task.
 * 
 * Note that there is (currently) no hardcoded rule that a WorkTarget instance may only be
 * submitted once to a single WorkScheduler and thus have exactly one paired WorkFuture
 * object... but it's the only case the system is designed for, so sane results are not
 * guaranteed if one does otherwise.
 * 
 * @author hash
 * 
 * @param <$V>
 */
class WorkFuture<$V> implements Future<$V> {
	public WorkFuture(WorkTarget<$V> $wt, ScheduleParams $schedp) {
		this.$work = $wt;
		this.$schedp = $schedp;
		this.$sync = new Sync();
	}
	
	
	final Sync			$sync;
	
	/** The underlying callable */
        final WorkTarget<$V>		$work;
	
	/** The parameters with which the work target was scheduled. */
	private final ScheduleParams	$schedp;
	
	/** Set to true when someone calls the cancel method.  Never again becomes false.  If there's currently a thread from the scheduler working on this, it must eventually notice this and deal with it; if there is no thread running this, the cancelling thread may act immediately. */
	volatile boolean		$cancelPlz	= false;
	/** The result to return from get().    Need not be volatile or synchronized since the value is only important when it is idempotent, which is once $state has made its own final idempotent transition. */
	private $V			$result		= null;
	/** The exception to throw from get().  Need not be volatile or synchronized since the value is only important when it is idempotent, which is once $state has made its own final idempotent transition. */
	private Throwable		$exception	= null;
	
	/** Index into delay queue, to support faster updates. */
	int				$heapIndex	= 0;
	
	/** When nulled after set/cancel, this indicates that the results are accessible. */
	volatile Thread			$runner;
	
	
	
	public WorkTarget<$V> getWorkTarget() {
		return $work;
	}
	
	public State getState() {
		return $sync.getWFState();
	}
	
	public ScheduleParams getScheduleParams() {
		return $schedp;
	}
	
	public boolean cancel(boolean $mayInterruptIfRunning) {
		return $sync.innerCancel($mayInterruptIfRunning);
	}
	
	public boolean isCancelled() {
		return getState() == State.CANCELLED;
	}
	
	public boolean isDone() {
		switch (getState()) {
			case FINISHED: return true;
			case CANCELLED: return true;
			default: return false;
		}
	}
	
	public $V get() throws InterruptedException, ExecutionException {
		return $sync.innerGet();
	}
	
	public $V get(long $timeout, TimeUnit $unit) throws InterruptedException, ExecutionException, TimeoutException {
		return $sync.innerGet($unit.toNanos($timeout));
	}
	
	
	
	public static enum State {
		/**
		 * The {@link WorkScheduler} that produced this WorkFuture has put a
		 * thread onto the job and it has stack frames in the execution of the
		 * work.
		 */
		RUNNING,
		/**
		 * The {@link WorkScheduler} has found the WorkTarget of this Future to
		 * be ready, and has queued it for execution. The WorkFuture will be
		 * shifted to {@link #RUNNING} when it reaches the top of the
		 * WorkScheduler's queue of {@link #SCHEDULED} work (unless at that time
		 * {@link WorkTarget#isReady()} is no longer true, in which case this
		 * WorkFuture will be shifted back to {@link #WAITING}).
		 */
		SCHEDULED,
		/**
		 * the work has not identified itself as having things to do immediately,
		 * so it will not be scheduled.
		 * 
		 * Note! This is <b>independent</b> of whether or not
		 * {@link WorkTarget#isReady()} returns true at any given time! The
		 * contract of the {@link WorkTarget#isReady()} method allows it to toggle
		 * at absolutely any time and with no synchronization whatsoever. This
		 * state enum refers only to what the {@link WorkScheduler} has most
		 * recently noticed (typically during invocation of the
		 * {@link WorkScheduler#update(WorkFuture)} method).
		 */
		WAITING,
		/**
		 * the work was running, but made a blocking call which returned thread
		 * power to the scheduler. The thread that was running this WorkTarget
		 * still has stack frames in the work, but the entire thread is paused in
		 * a blocking call under the management of the WorkScheduler (which has
		 * launched the activity of another thread to compensate for this thread's
		 * inactivity, and will wake this thread and return it to RUNNING state as
		 * soon as the task it is currently blocked on completes and any one of
		 * the other then-RUNNING threads completes its task). not currently used.
		 */
		PARKED,
		/**
		 * The {@link WorkTarget#isDone()} method returned true after the last
		 * time a thread acting on behalf of this Future's {@link WorkScheduler}
		 * pushed the WorkTarget; the WorkTarget will no longer be scheduled for
		 * future activation, and the final result of the execution &mdash;
		 * whether it be a return value or an exception &mdash; is now available
		 * for immediate return via the {@link WorkFuture#get()} method. An
		 * exception thrown from the {@link WorkTarget#call()} method will also
		 * result in this Future becoming FINISHED, but the
		 * {@link WorkTarget#isDone()} method may still return false.
		 */
		FINISHED,
		/**
		 * The work was cancelled via the {@link WorkFuture#cancel(boolean)}
		 * method before it could become {@link #FINISHED}. The work may have
		 * previously been {@link #RUNNING}, but will now no longer be scheduled
		 * for future activations. Since the cancellation was the result of an
		 * external operation rather than of the WorkTarget's own volition, the
		 * WorkTarget's {@link WorkTarget#isDone()} method may still return false.
		 */
		CANCELLED
	}
	
	
	
	/** Uses AQS sync state to represent run status. */
	private final class Sync extends AbstractQueuedSynchronizer {
		Sync() {}
		
		State getWFState() {
			return State.values()[getState()];
		}
		
		/** Implements AQS base acquire to succeed if finished or cancelled */
		protected int tryAcquireShared(int $ignore) {
			return innerIsDone() ? 1 : -1;
		}
		
		/** Implements AQS base release to always signal after setting final done status by nulling runner thread. */
		protected boolean tryReleaseShared(int $ignore) {
			$runner = null;
			return true;
		}
		
		boolean innerIsCancelled() {
			return getState() == State.CANCELLED.ordinal();
		}
		
		boolean innerIsDone() {
			int $s = getState();
			return ($s == State.CANCELLED.ordinal() || $s == State.FINISHED.ordinal()) && $runner == null;
		}
		
		$V innerGet() throws InterruptedException, ExecutionException {
			acquireSharedInterruptibly(0);
			return innerGetHelper();
		}
		
		$V innerGet(long $nanosTimeout) throws InterruptedException, ExecutionException, TimeoutException {
			if (!tryAcquireSharedNanos(0, $nanosTimeout)) throw new TimeoutException();
			return innerGetHelper();
		}
		
		private final $V innerGetHelper() throws ExecutionException {
			if (getState() == State.CANCELLED.ordinal()) throw new CancellationException();
			if ($exception != null) throw new ExecutionException($exception);
			return $result;
		}
		
		void innerSet($V v) {
			for (;;) {
				int s = getState();
				if (s == State.FINISHED.ordinal()) return;
				if (s == State.CANCELLED.ordinal()) {
					// aggressively release to set runner to null, in case we are racing with a cancel request that will try to interrupt runner
					releaseShared(0);
					return;
				}
				if (compareAndSetState(s, State.FINISHED.ordinal())) {
					$result = v;
					releaseShared(0);
					//TODO:AHS:THREAD: call the hearDone() hook right here
					return;
				}
			}
		}
		
		void innerSetException(Throwable t) {
			for (;;) {
				int s = getState();
				if (s == State.FINISHED.ordinal()) return;
				if (s == State.CANCELLED.ordinal()) {
					// aggressively release to set runner to null, in case we are racing with a cancel request that will try to interrupt runner
					releaseShared(0);
					return;
				}
				if (compareAndSetState(s, State.FINISHED.ordinal())) {
					$exception = t;
					releaseShared(0);
					//TODO:AHS:THREAD: call the hearDone() hook right here
					return;
				}
			}
		}
		
		boolean innerCancel(boolean mayInterruptIfRunning) {
			for (;;) {
				int s = getState();
				if (s == State.FINISHED.ordinal()) return false;
				if (s == State.CANCELLED.ordinal()) return false;
				if (compareAndSetState(s, State.CANCELLED.ordinal())) break;
			}
			if (mayInterruptIfRunning) {
				Thread r = $runner;
				if (r != null) r.interrupt();
			}
			releaseShared(0);
			//TODO:AHS:THREAD: call the hearDone() hook right here
			return true;
		}
		
		void innerRun() {
			if (!compareAndSetState(State.SCHEDULED.ordinal(), State.RUNNING.ordinal())) return;
			
			$runner = Thread.currentThread();
			if (getState() == State.RUNNING.ordinal()) { // recheck after setting thread
				$V result;
				try {
					result = $work.call();
				} catch (Throwable ex) {
					innerSetException(ex);
					return;
				}
				innerSet(result);
			} else {
				releaseShared(0); // cancel
			}
		}
		
		boolean innerRunAndReset() {
			if (!compareAndSetState(State.SCHEDULED.ordinal(), State.RUNNING.ordinal())) return false;
			try {
				$runner = Thread.currentThread();
				if (getState() == State.RUNNING.ordinal()) $work.call(); // don't set result
				$runner = null;
				return compareAndSetState(State.RUNNING.ordinal(), State.WAITING.ordinal());	// note the waiting state set here.  this is a divergence from DL's library; the workscheduler must do something about this later.
			} catch (Throwable ex) {
				innerSetException(ex);
				return false;
			}
		}
	}
}
