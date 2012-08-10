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
import java.util.*;
import java.util.concurrent.*;

/**
 * <p>
 * Creates a WorkTarget that will become ready after a set of other {@link WorkFuture}s complete.
 * </p>
 * 
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 */
//XXX:AHS:THREAD: this is suboptimal.  We'd really like to be able to apply followup conditions along with any of the other adapters already available; especially like flowing but also the other the triggerables like callable and runnable.
public abstract class WorkTargetAdapterFollowup<$V> extends WorkTargetAdapterTriggerable<$V> {
	public WorkTargetAdapterFollowup(Collection<WorkFuture<?>> $eventsToFollow, int $priority) {
		super(false, true, $priority);
		final CountDownLatch $latch = new CountDownLatch( $eventsToFollow.size() );
		final Listener<WorkFuture<?>> $downcounter = new Listener<WorkFuture<?>>() {
			public void hear(WorkFuture<?> $wf) {
				$latch.countDown();
				if ($latch.getCount() == 0)
					trigger();
					//XXX:AHS:THREAD: this doesn't emit any events that can be used to update our own WorkFuture at the scheduler now, which isn't cool.  The workaround is to add those updators to everyone in $eventsToFollow after creating and scheduling this WorkTarget, but that's definitely clunky.  (Or as usual, just forget about event-based updating and let that be a clocked thing.)
			}
		};
		for (WorkFuture<?> $wf : $eventsToFollow)
			$wf.addCompletionListener($downcounter);
	}
}
