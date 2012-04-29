/*
 * Copyright 2010, 2011 Eric Myhre <http://exultant.us>
 * 
 * This file is part of AHSlib.
 *
 * AHSlib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License, or
 * (at the original copyright holder's option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package us.exultant.ahs.thread;

import us.exultant.ahs.core.*;
import us.exultant.ahs.anno.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * <p>
 * A WorkFuture is a system that allows the user to run an asynchronous task and at their
 * option either wait (blockingly) for it to complete, or register a listener to be
 * notified when the task becomes complete (following a more nonblocking/event-based
 * design pattern).
 * </p>
 * 
 * <p>
 * The most common appearance of a WorkFuture is cooperation with the
 * {@link WorkScheduler}, which produces a WorkFuture when a {@link WorkTarget} is
 * {@link WorkScheduler#schedule(WorkTarget, ScheduleParams) scheduled} with it.
 * WorkFuture can also represent other kinds of delayed system; for example
 * {@link WorkScheduler#stop(boolean) stopping a WorkScheduler} is another action you can
 * either wait for the completion of or request a callback from.
 * </p>
 * 
 * <p>
 * For waiting for or getting notifications of a group of WorkFutures, use
 * {@link AggregateWorkFuture}.
 * </p>
 * 
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 * 
 * @param <$V>
 *                the type of data that will be returned from the {@link #get()} method
 *                when the work this future represents becomes done.
 */
public interface WorkFuture<$V> extends Future<$V> {
	@ThreadSafe
	@Nullipotent
	public State getState();
	
	@ThreadSafe
	@Nullipotent
	public ScheduleParams getScheduleParams();
	
	@ThreadSafe
	@Nullipotent
	public boolean isCancelled();
	
	@ThreadSafe
	@Nullipotent
	public boolean isDone();
	
	@ThreadSafe
	@Nullipotent
	public $V get() throws InterruptedException, ExecutionException, CancellationException;
	
	@ThreadSafe
	@Nullipotent
	public $V get(long $timeout, TimeUnit $unit) throws InterruptedException, ExecutionException, TimeoutException, CancellationException;
	
	/**
	 * <p>
	 * Attempts to cancel execution of this task. This attempt will fail if the task
	 * has already completed, has already been cancelled, or could not be cancelled
	 * for some other reason. If successful, and this task has not started when cancel
	 * is called, this task should never run. If the task has already started, then
	 * the mayInterruptIfRunning parameter determines whether the thread executing
	 * this task should be interrupted in an attempt to stop the task.
	 * </p>
	 * 
	 * <p>
	 * After this method returns, subsequent calls to {@link #isDone()} will always
	 * return true. Subsequent calls to {@link #isCancelled()} will always return true
	 * if this method returned true (they may also return true if this method returned
	 * false; this depends on whether this method returned false because another
	 * thread performed cancellation concurrently, or because the task finished
	 * concurrently).
	 * </p>
	 * 
	 * <p>
	 * Note: the documentation of {@link Future#cancel(boolean)} is potentially
	 * misleading. It states that the method should return "<tt>false</tt> if the task
	 * could not be cancelled, typically because it has already completed normally;
	 * <tt>true</tt> otherwise" &mdash; this use of "normally" would seem to imply
	 * that if another thread called cancel concurrently, both should return true.
	 * This is in fact NOT what the canonical implementation of
	 * {@link FutureTask#cancel(boolean)} does; that implementation acts identially to
	 * this one in that if several threads attempt to cancel a task concurrently, only
	 * one of them should get a true return.
	 * </p>
	 * 
	 * @param $mayInterruptIfRunning
	 *                true if the thread executing this task should be interrupted;
	 *                otherwise, in-progress tasks are allowed to complete
	 * @returns whether or not this thread was responsible for cancelling
	 * 
	 */
	@ThreadSafe
	@Idempotent
	public boolean cancel(boolean $mayInterruptIfRunning);
	
	/**
	 * Functions exactly as calling {@link WorkScheduler#update(WorkFuture)} with this
	 * object on its parent scheduler.
	 */
	@ThreadSafe
	@Idempotent
	public void update();
	
	/**
	 * <p>
	 * Adds a listener to this WorkFuture which will be called as soon as possible
	 * after the task represented by this WorkFuture has become done (regardless if by
	 * normal completion, a deadly exception, or external cancellation). The listener
	 * will be called exactly once (presuming of course that the task ever finishes).
	 * </p>
	 * 
	 * <p>
	 * This method may be called at any time from any thread.
	 * </p>
	 * 
	 * <p>
	 * The listener should only perform very fast operations; any heavy lifting should
	 * be performed in another thread which is triggered by this listener. The
	 * listener may be called by a thread from within a WorkScheduler's pool, or by a
	 * thread that caused cancellation or concurrent completion (i.e. by work
	 * draining) of a task, or by the same thread that attempted to add it (in case
	 * the task was already done).
	 * </p>
	 */
	@ThreadSafe
	public void addCompletionListener(Listener<WorkFuture<?>> $completionListener);
	
	
	
	/**
	 * <p>
	 * Enumerates all possible states a piece of work may be in.
	 * </p>
	 * 
	 * <p>
	 * Note! While the documentation for each of these states may be defined in terms
	 * of some function of a {@link WorkTarget}, it is critical to understand that
	 * when a {@link WorkFuture} reports its <tt>State</tt>, it is only reporting the
	 * last observed state. This is typically set by a {@link WorkScheduler} and thus
	 * matches the scheduler's understanding of where the work is at.
	 * </p>
	 */
	public static enum State {
		/**
		 * <p>
		 * A piece of work that is <tt>WAITING</tt> has not identified itself as
		 * having things to do immediately, will not be scheduled by a
		 * {@link WorkScheduler}. It may transition to {@link #SCHEDULED},
		 * {@link #CANCELLED}, or {@link #FINISHED}.
		 * </p>
		 * 
		 * <p>
		 * Note! This is <b>independent</b> of whether or not
		 * {@link WorkTarget#isReady()} returns true at any given time! The
		 * contract of the {@link WorkTarget#isReady()} method allows it to toggle
		 * at absolutely any time and with no synchronization whatsoever. This
		 * state enum refers only to what the {@link WorkScheduler} has most
		 * recently noticed (typically during invocation of the
		 * {@link WorkScheduler#update(WorkFuture)} method).
		 * </p>
		 * 
		 * <p>
		 * For some kinds of {@link WorkFuture} that do not directly represent a
		 * {@link WorkTarget} scheduled with a {@link WorkScheduler} (such as an
		 * {@link AggregateWorkFuture} for example), this is the default state:
		 * {@link #SCHEDULED} and {@link #RUNNING} may never occur.
		 * </p>
		 */
		WAITING,	// this actually has to be ordinal zero due to the silliness in AQS
		/**
		 * <p>
		 * A piece of work that is <tt>SCHEDULED</tt> has been found by its
		 * {@link WorkScheduler} to be ready, and the scheduler has queued it for
		 * execution. It may transition to {@link #WAITING}, {@link #RUNNING},
		 * {@link #CANCELLED}, or {@link #FINISHED} &mdash; the WorkFuture will be
		 * shifted to {@link #RUNNING} when it reaches the top of the
		 * WorkScheduler's queue of {@link #SCHEDULED} work (unless at that time
		 * {@link WorkTarget#isReady()} is no longer true, in which case this
		 * WorkFuture will be shifted back to {@link #WAITING}).
		 * </p>
		 */
		SCHEDULED,
		/**
		 * <p>
		 * A piece of work that is <tt>RUNNING</tt> now has a {@link Thread}
		 * assigned to it, that thread has stack frames in the execution of the
		 * work. It may transition to {@link #WAITING}, {@link #CANCELLING}, or
		 * {@link #FINISHED} &mdash; note that a direct transition to
		 * {@link #CANCELLED} is not allowed here.
		 * </p>
		 */
		RUNNING,
		///**
		// * the work was running, but made a blocking call which returned thread
		// * power to the scheduler. The thread that was running this WorkTarget
		// * still has stack frames in the work, but the entire thread is paused in
		// * a blocking call under the management of the WorkScheduler (which has
		// * launched the activity of another thread to compensate for this thread's
		// * inactivity, and will wake this thread and return it to RUNNING state as
		// * soon as the task it is currently blocked on completes and any one of
		// * the other then-RUNNING threads completes its task). not currently used.
		// */
		//PARKED,
		/**
		 * <p>
		 * A piece of work that is <tt>FINISHED</tt> will no longer be scheduled
		 * for execution, and the final result of the execution &mdash; whether it
		 * be a return value or an exception &mdash; is now available for prompt
		 * return via the {@link WorkFuture#get()} method. <tt>FINISHED</tt> is a
		 * final and {@link Idempotent} transition &mdash; once it happens, it is
		 * permanent. A <tt>FINISHED</tt> also guarantees that there is no thread
		 * active within the work.
		 * </p>
		 * 
		 * <p>
		 * A piece of work that is <tt>FINISHED</tt> became so in one of two ways:
		 * it either became finished "normally" via the
		 * {@link WorkTarget#isDone()} method returning true when the
		 * {@link WorkScheduler} examined it (which may be either when a run was
		 * finished, or when when the Scheduler responded to a request to
		 * {@link WorkScheduler#update(WorkFuture) update} the work), or it became
		 * finished when a run caused an exception to be thrown from the
		 * {@link WorkTarget#call()} method.
		 * </p>
		 * 
		 * <p>
		 * A clarification of how promptly is "prompt" in the above paragraphs. It
		 * is of course a relative term; the exact order of operations is as
		 * follows:
		 * <ol>
		 * <li>the return value is set for the final time
		 * <li>the transition of State occurs
		 * <li>the system that blocks {@link WorkFuture#get()} is released
		 * <li>completion listeners are called
		 * </ol>
		 * Thus, the sync call in {@link WorkFuture#get()} might still actually
		 * block for a tiny bit in the moments after it becomes <tt>FINISHED</tt>
		 * &mdash; but we're talking about a handful of machine operations after
		 * the state transition and before releasing the locks for the last time,
		 * and there are no blocking nor error prone operations in that range.
		 * </p>
		 */
		FINISHED,
		/**
		 * <p>
		 * A piece of work that is <tt>CANCELLING</tt> was previously in the
		 * {@link #RUNNING} state, but was {@link WorkFuture#cancel(boolean)
		 * cancelled} from another thread. The thread that was assigned to running
		 * the work still has stack frames in the execution of the work; that
		 * thread is expected to notice this status as soon as possible and is
		 * responsible for transitioning to {@link #CANCELLED} (no other
		 * transitions are valid).
		 * </p>
		 * 
		 * <p>
		 * In theory, the period of time between a running work becoming
		 * CANCELLING and then becoming CANCELLED is hoped to be infinitesimal. In
		 * reality of course things are never so simple. It is the responsibility
		 * of the running thread to check for CANCELLING status and return
		 * promptly, but of course it is not reasonable to insert such a check
		 * between every single line of code within the work, nor is there any way
		 * to automatically do so. Typically, it is considered good enough if the
		 * work is defined in such a way that if the interrupt status of the
		 * thread is set (as it is by calling {@link WorkFuture#cancel(boolean)}
		 * with an argument of <tt>true</tt>) then the work will abort any
		 * blocking or waiting operations and return immediately. It is
		 * unforunately possible for a working thread to fail entirely to respond
		 * to <tt>CANCELLING</tt>, if for example it is caught in an infinite
		 * loop; such disastrous circumstances cannot be averted by any amount of
		 * library design and are the responsibility of the work's programmer to
		 * avoid.
		 * </p>
		 * 
		 * <p>
		 * For some kinds of {@link WorkFuture} that do not directly represent a
		 * {@link WorkTarget} scheduled with a {@link WorkScheduler} (such as an
		 * {@link AggregateWorkFuture} for example), this may be a valid
		 * transition directly from {@link #WAITING} state, it may be a somewhat
		 * more prolonged state, and it may have different semantics regarding
		 * where a thread is. See the documentation of
		 * {@link AggregateWorkFuture#cancel(boolean)} for an example of this; the
		 * semantics are slightly different, but should be essentially
		 * unsurprising.
		 * </p>
		 */
		CANCELLING,
		/**
		 * <p>
		 * A piece of work that is <tt>CANCELLED</tt> was
		 * {@link WorkFuture#cancel(boolean) cancelled} at some point before it
		 * could become {@link #FINISHED}. Cancelling is considered a very serious
		 * operation and as such <tt>CANCELLED</tt> is a valid transition from
		 * almost any point in the lifecycle except {@link #FINISHED} (
		 * {@link #RUNNING} though running also has special rules); the
		 * <tt>CANCELLED</tt> state itself is a final and {@link Idempotent}
		 * transition &mdash; once it happens, it is permanent. Like work that is
		 * {@link #FINISHED}, work that is <tt>CANCELLED</tt> will never again be
		 * scheduled for execution and it is guaranteed that there is no thread
		 * active within the work.
		 * </p>
		 * 
		 * <p>
		 * Note that since the cancellation was the result of an external
		 * operation rather than of the WorkTarget's own volition, the
		 * WorkTarget's {@link WorkTarget#isDone()} method may still return
		 * <tt>false</tt>, even though the matching WorkFuture's
		 * {@link WorkTarget#isDone()} method will now return <tt>true</tt>.
		 * </p>
		 */
		CANCELLED;
		
		final static State[] values = State.values();
	}
	
	
	
	public static class DelayComparator implements Comparator<WorkFuture<?>> {
		public static final DelayComparator INSTANCE = new DelayComparator();

		/** @return positive if the first arg should be run sooner than the second */
		public int compare(WorkFuture<?> $o1, WorkFuture<?> $o2) {
			final long $diff = $o1.getScheduleParams().getNextRunTime() - $o2.getScheduleParams().getNextRunTime();
			if ($diff > 0) return -1;
			if ($diff < 0) return 1;
			return 0;
		}
	}
}
