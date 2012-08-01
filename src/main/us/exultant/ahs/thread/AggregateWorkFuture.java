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
import us.exultant.ahs.util.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * <p>
 * A {@link WorkFuture} that represents a set of WorkFutures, making it possible to
 * coordinate them easily as a group. An AggregateWorkFuture can wait for all of the
 * collected WorkFutures to complete, use the completion listener to fire once when all
 * the tasks of a set are done, issue cancellations to all of the collected tasks, and
 * trigger scheduler updates in bulk.
 * </p>
 * 
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 * 
 */
public class AggregateWorkFuture<$T> extends WorkFutureAdapter<Void> {
	public AggregateWorkFuture(Collection<WorkFuture<$T>> $futures) {
		this.$pip = new FuturePipe<$T>();
		this.$state = WorkFuture.State.WAITING;
		this.$completionListeners = new ArrayList<Listener<WorkFuture<?>>>(1);
		this.$pip.sink().writeAll($futures);
		this.$pip.sink().close();
		this.$pip.source().setListener(new Listener<ReadHead<WorkFuture<$T>>>() {
			public void hear(ReadHead<WorkFuture<$T>> $rh) {
				synchronized ($pip) {
					$pip.source().readAllNow();
					if (!$pip.source().isExhausted()) return;
					switch ($state) {
						case WAITING: $state = State.FINISHED; break;
						case CANCELLING: $state = State.CANCELLED; break;
						case FINISHED: case CANCELLED: return;
						case RUNNING: case SCHEDULED: throw new MajorBug();
					}
					X.notifyAll($pip);
				}
				hearDone();
			}
		});
	}
	
	private FuturePipe<$T> $pip;
	/**
	 * Generally speaking, if you're going to change this, you should synchronize on
	 * {@link #$pip} first. However, if you're only reading, and you're checking for
	 * {@link WorkFuture.State#CANCELLED} or {@link WorkFuture.State#FINISHED}, you
	 * can do that without any synchronization since this field is volatile and those
	 * transitions are permanent.
	 */
	private volatile WorkFuture.State $state;
	/**
	 * A list of {@link Listener} to be called as soon as possible after this task
	 * becomes done. This is synchronized on before adding new elements, and before
	 * transitioning to done (we're not worried about efficiency because this
	 * operation should be quite rare (i.e. never ever ever ever in a loop) and
	 * contention not really an issue).
	 */
	private final List<Listener<WorkFuture<?>>>	$completionListeners;
	
	public WorkFuture.State getState() {
		return $state;
	}
	
	public ScheduleParams getScheduleParams() {
		return ScheduleParams.NOW;
	}
	
	/**
	 * Returns when all of the aggregated WorkFutures have either finished or been
	 * cancelled (or if this wait itself is interrupted).
	 * 
	 * @throws InterruptedException
	 *                 if this wait is interrupted (note, NOT if any of the aggregated
	 *                 futures were interrupted)
	 * @throws CancellationException
	 *                 if this AggregateWorkFuture object was cancelled (note, NOT
	 *                 merely if any of the aggregated futures were cancelled; also,
	 *                 while cancellation of this AggregateWorkFuture means that the
	 *                 cancellation event was passed on to the aggregated futures, it
	 *                 may not have taken effect on all of them because they may have
	 *                 already finished.)
	 */
	public Void get() throws InterruptedException, CancellationException {
		synchronized ($pip) {
			while (!isDone())
				$pip.wait();
		}
		if (isCancelled()) throw new CancellationException();
		return null;
	}

	/**
	 * Returns when all of the aggregated WorkFutures have either finished or been
	 * cancelled; or if this wait itself is interrupted or times out.
	 * 
	 * @throws InterruptedException
	 *                 if this wait is interrupted (note, NOT if any of the aggregated
	 *                 futures were interrupted)
	 * @throws CancellationException
	 *                 if this AggregateWorkFuture object was cancelled (note, NOT
	 *                 merely if any of the aggregated futures were cancelled; also,
	 *                 while cancellation of this AggregateWorkFuture means that the
	 *                 cancellation event was passed on to the aggregated futures, it
	 *                 may not have taken effect on all of them because they may have
	 *                 already finished.)
	 * @throws TimeoutException
	 *                 if the wait timed out
	 */
	public Void get(long $timeout, TimeUnit $unit) throws InterruptedException, TimeoutException, CancellationException {
		$timeout = $unit.toMillis($timeout);
		final long $target = X.time() + $timeout;
		synchronized ($pip) {
			while (!isDone() && $timeout > 0) {
				$pip.wait($timeout);
				$timeout = $target-X.time();
			}
		}
		if (isCancelled()) throw new CancellationException();
		if (isDone()) return null;
		throw new TimeoutException();
		// about returning lists: yeah, really cool idea... except for the whole exception thing and how you couldn't represent that.  well, unless you made a new struct for that.  which is an option, and kind of a cool one i suppose.  though once you crossed that line, it would seem almost strange for workfuture.get to itself not return such a struct instead of throwing execution exceptions.  and... that... yeah that's a big do not want i think.  i dunno, i suppose the heterogenous option wouldn't be too bad; after all, how often do you really want to *return* an exception instead of throwing one?  that would be just darn weird of you to do, and i think i'd be alright just documenting that as a "don't do it if you want AWF.get() to make sense".
	}
	
	/**
	 * <p>
	 * Attempts to cancel execution of all of the individual tasks aggregated by this
	 * object that are not yet completed. This WorkFuture immediately transitions to
	 * the {@link WorkFuture.State#CANCELLING} state; it will transition to the
	 * {@link WorkFuture.State#CANCELLED} state when all of its aggregated WorkFutures
	 * are themselves either {@link WorkFuture.State#CANCELLED} or
	 * {@link WorkFuture.State#FINISHED}; in this way the invarient that the
	 * <tt>get()</tt> method here shouldn't return until the <tt>get()</tt> method can
	 * return immediately on every member is unbroken.
	 * </p>
	 * 
	 * <p>
	 * This method returns after issuing all cancels to aggregated tasks, but does not
	 * wait for all tasks to acknowledge the cancel or otherwise become complete. This
	 * means that by the time this method returns, all aggregated tasks are no longer
	 * schedulable by a WorkScheduler; however, if they were running at the time the
	 * cancellation was issued it's quite possible that they have not yet returned. To
	 * wait for all tasks to be completed and to have no threads with stack frames in
	 * their {@link WorkTarget#call()} method, simply use
	 * {@link AggregateWorkFuture#get()} or
	 * {@link AggregateWorkFuture#addCompletionListener(Listener)} in the usual ways.
	 * </p>
	 * 
	 * <p>
	 * Calling this method after this AggregateWorkFuture has become
	 * <tt>CANCELLED</tt> or <tt>FINISHED</tt> is ignored and returns false. Calling
	 * it repeatedly when it has already become <tt>CANCELLING</tt> has no effect on
	 * the state, but will still relay cancel events on to all incomplete futures.
	 * (This in turn is typically useless, with the exception that it does allow you
	 * to send thread interrupts in a second call after declining to do so in a
	 * previous call.)
	 * </p>
	 */
	public boolean cancel(boolean $mayInterruptIfRunning) {
		synchronized ($pip) {
			switch ($state) {
				case WAITING: $state = State.CANCELLING; break;
				case CANCELLING: break;
				case FINISHED: case CANCELLED: return false;
				case RUNNING: case SCHEDULED: throw new MajorBug();
			}
		}
		Set<WorkFuture<$T>> $helds;
		synchronized ($pip.$held) {
			$helds = new HashSet<WorkFuture<$T>>($pip.$held);
		}
		for (WorkFuture<$T> $held : $helds)
			$held.cancel($mayInterruptIfRunning);
		return true;
	}
	

	/**
	 * <p>
	 * Invokes update for all of the individual tasks aggregated by this object that
	 * are not yet completed.
	 * </p>
	 * 
	 * <p>
	 * Note that if you have kept a the full set of WorkFuture that were aggregated
	 * around elsewhere and you know that they all came from the same
	 * {@link WorkScheduler}, it may be slightly more efficient to use that
	 * WorkScheduler's {@link WorkScheduler#update(Collection) mass update} method.
	 * </p>
	 */
	public void update() {
		Set<WorkFuture<$T>> $helds;
		synchronized ($pip.$held) {
			$helds = new HashSet<WorkFuture<$T>>($pip.$held);
		}
		//XXX:AHS:THREAD: i'm really not sure how i feel about this copy.  avoiding holding that sync?  great.  but wasting all that memory in a possibly tight loop is bad.  and really... a monitor in a tight loop?  that's shiver enough.  that monitor was originally intended to be grabbed only when something was finishing and thus not be a bottleneck.  perhaps we should just make a single copy of the whole collection and keep that readonly?  the beef I have with that though is that it holds on to the WorkFutures until the entire aggregate is done, and that's something I didn't really want to do for GC purposes.
		for (WorkFuture<$T> $held : $helds)
			$held.update();
	}
	
	public void addCompletionListener(Listener<WorkFuture<?>> $completionListener) {
		synchronized ($completionListeners) {
			if (isDone()) $completionListener.hear(this);
			else $completionListeners.add($completionListener);
		}
	}
	
	/** Called exactly once.  Called AFTER the transition to completion has already been completed. */
	private void hearDone() {
		synchronized ($completionListeners) {
			for (Listener<WorkFuture<?>> $x : $completionListeners)
				$x.hear(AggregateWorkFuture.this);
			$completionListeners.clear();	// let that crap be gc'd even if this future is forced to hang around for a while
		}
	}
}
