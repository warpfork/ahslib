package us.exultant.ahs.thread;

import java.util.concurrent.*;

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
	public WorkFuture(WorkTarget<$V> $wt) {
		this.$work = $wt;
	}
	
	public final WorkTarget<$V>	$work;
	
	/** Index into delay queue, to support faster updates. */
	int				$heapIndex;
	
	private State			$state;
	
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
		 * the work is ready, but the relevant
		 * {@link WorkScheduler#update(WorkTarget)} invocation has not been made
		 * in order to shift the WorkTarget from the waiting pile into the
		 * scheduled heap.
		 */	// i'm highly unsure this is necessary, since it would otherwise simply equate to WAITING && isReady().  
		READY,
		/**
		 * the work has not identified itself as having things to do immediately,
		 * so it will not be scheduled.
		 * 
		 * Note! This is <b>independant</b> of whether or not
		 * {@link WorkTarget#isReady()} returns true at any given time! The
		 * contract of the {@link WorkTarget#isReady()} method allows it to toggle
		 * at absolutely any time and with no synchronization whatsoever. This
		 * state enum refers only to what the {@link WorkScheduler} has most
		 * recently noticed (typically during invocation of the
		 * {@link WorkScheduler#update(WorkTarget)} method).
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
		 * for future activitions. Since the cancellation was the result of an
		 * external operation rather than of the WorkTarget's own volition, the
		 * WorkTarget's {@link WorkTarget#isDone()} method may still return false, even though it 
		 */
		CANCELLED
	}
	
	public State getState() {
		return $state;
	}
	
	
	
	
	
	public boolean cancel(boolean $mayInterruptIfRunning) {
		//TODO
		return false;
	}
	
	public boolean isCancelled() {
		//TODO
		return false;
	}
	
	public boolean isDone() {
		//TODO
		return false;
	}
	
	public $V get() throws InterruptedException, ExecutionException {
		//TODO
		return null;
	}
	
	public $V get(long $timeout, TimeUnit $unit) throws InterruptedException, ExecutionException, TimeoutException {
		//TODO
		return null;
	}
}
