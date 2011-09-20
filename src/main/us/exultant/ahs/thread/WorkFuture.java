package us.exultant.ahs.thread;

import us.exultant.ahs.util.*;
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
// You might expect this to have a lot in common with FutureTask.
// You'd be surprised.
// FutureTask is actually built to work under a wider range of conditions than WorkFuture, and so WorkFuture is able to cut a lot of expensive corners.
// WorkFuture is intending for mutation exclusively within a WorkScheduler that already does a great deal of concurrency control,
//  and as such, it's allowed to assume that it will only have one actively mutating thread at any time.
// The contracts of WorkFuture.State are also significantly more relaxed than the state transitions of FutureTask:
//  state is allowed to flip in a wildly unsynchronized fashion *except* for the transitions that are idempotent (one-way).
class WorkFuture<$V> implements Future<$V> {
	public WorkFuture(WorkTarget<$V> $wt, ScheduleParams $schedp) {
		this.$work = $wt;
		this.$schedp = $schedp;
	}
	
	
	
	/** The underlying callable */
        final WorkTarget<$V>		$work;
	
        /** The present state of the task */
	volatile State			$state		= null;
	
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
	
	volatile Thread			$runner;
	
	
	public WorkTarget<$V> getWorkTarget() {
		return $work;
	}
	
	public State getState() {
		return $state;
	}
	
	public ScheduleParams getScheduleParams() {
		return $schedp;
	}
	
	void run() {
		if ($state != State.FINISHED && $cancelPlz) {
			$state = State.CANCELLED;
			return;
		}
		
		if (getState() != State.SCHEDULED) throw new MajorBug();
		
		$runner = Thread.currentThread();

		//TODO
	}
	
	public boolean cancel(boolean $mayInterruptIfRunning) {
		// the core concept here that still lets us get away with zero synchronization is that you can request cancellation whenever you want (and that request has its own idempotency *separate* from the future's state), but no one has to pay attention to your request and update the future's state until the scheduler puts a thread on this guy again.
		
		$cancelPlz = true;
		// from now on, no thread in the scheduler may start running this task.
			// FIXME erm, unless they just checked cancelplz, then yielded to us right above this line.

		// bit rude to cancel something already finished, neh?
		if (getState() == State.FINISHED) return;
		
		// if nobody from the scheduler is looking, we can set it to cancelled in this thread, instead of waiting for the scheduler to get around to it (notice this doesn't necessarily make anything gc'able, though).
		if ($runner == null)
			$state = State.CANCELLED;	//FIRE:finish
		
		// WHAT IF the scheduler's shifting this guy between heaps?  we can't back out from a cancelled state.
		
		//TODO
		return false;
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
		//TODO
		// dunno who you're planning to sync on here.
		return null;
	}
	
	public $V get(long $timeout, TimeUnit $unit) throws InterruptedException, ExecutionException, TimeoutException {
		//TODO
		// dunno who you're planning to sync on here.
		return null;
	}
	
	
	
	void postExecute() {
		
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
}
