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
 * A {@link WorkFuture} that represents a set of WorkFutures. This makes it possible to
 * wait for all of the collected WorkFutures to complete, or use the completion listener
 * to fire once when all the set of tasks is done, for example.
 * </p>
 * 
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 * 
 */
public class AggregateWorkFuture<$T> implements WorkFuture<Void> {
	public AggregateWorkFuture(Collection<WorkFuture<$T>> $futures) {
		this.$pip = new FuturePipe<$T>();
		this.$completionListeners = new ArrayList<Listener<WorkFuture<?>>>(1);
		this.$pip.source().setListener(new Listener<ReadHead<WorkFuture<$T>>>() {
			public void hear(ReadHead<WorkFuture<$T>> $rh) {
				synchronized ($pip) {
					$pip.source().readAllNow();
					if (!$pip.source().isExhausted()) return;
					if ($state != State.WAITING) return;
					$state = State.FINISHED;
					X.notifyAll($pip);
				}
				hearDone();
			}
		});
		this.$pip.sink().writeAll($futures);
	}
	
	private Flow<WorkFuture<$T>> $pip;
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
		synchronized ($pip) {
			return $state;
		}
	}
	
	public ScheduleParams getScheduleParams() {
		return ScheduleParams.NOW;
	}
	
	public boolean isCancelled() {
		synchronized ($pip) {
			return getState() == State.CANCELLED;
		}
	}
	
	public boolean isDone() {
		synchronized ($pip) {
			switch (getState()) {
				case FINISHED: return true;
				case CANCELLED: return true;
				default: return false;
			}
		}
	}
	
	public Void get() throws InterruptedException, CancellationException {
		synchronized ($pip) {
			while (!isDone())
				$pip.wait();
		}
		if (isCancelled()) throw new CancellationException();
		return null;
	}
	
	public Void get(long $timeout, TimeUnit $unit) throws InterruptedException, TimeoutException, CancellationException {
		final long $target = $unit.toMillis($timeout);
		long $left = $target-X.time();
		synchronized ($pip) {
			while (!isDone() && $left > 0) {
				$pip.wait($left);
				$left = $target-X.time();
			}
		}
		if (isCancelled()) throw new CancellationException();
		if (isDone()) return null;
		throw new TimeoutException();
	}
	
	public boolean cancel(boolean $notApplicable) {
		synchronized ($pip) {
			if ($state != State.WAITING) return false; 
			$state = State.CANCELLED;
		}
		hearDone();
		//XXX:AHS:THREAD: there's no way to punt the work targets out of the pipe to be gc'd without dropping the whole AWF.
		return true;
	}
	
	public void update() {
		/* no-op */
	}
	
	public void addCompletionListener(Listener<WorkFuture<?>> $completionListener) {
		synchronized ($completionListeners) {
			if (isDone()) $completionListener.hear(this);
			else $completionListeners.add($completionListener);
		}
	}
	
	/** Called exactly once.  Called AFTER the CAS to completion has already been completed. */
	private void hearDone() {
		synchronized ($completionListeners) {
			for (Listener<WorkFuture<?>> $x : $completionListeners)
				$x.hear(AggregateWorkFuture.this);
			$completionListeners.clear();	// let that crap be gc'd even if this future is forced to hang around for a while
		}
	}
}
