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

import java.util.concurrent.*;

/**
 * <p>
 * Creates a WorkTarget that will become ready and runnable after...................... this is suboptimal.  this is a condition you might like to apply *along with any of the others already avaiable* like flowing or the triggerables or callable or runnable.
 * </p>
 * 
 * <p>
 * If constructed in run-once mode, the WorkTarget will run exactly once when
 * scheduled with a {@link WorkScheduler}; otherwise if run-once is false the work
 * will always be ready and will never become done (to stop it, the
 * {@link WorkFuture} must be cancelled or it must throw an exception).
 * </p>
 * 
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 */
public class WorkTargetAdapterFollowup<$V> extends WorkTargetAdapterTriggerable<$V> {
	public WorkTargetAdapterFollowup(Callable<$V> $wrap) { this($wrap,true,true,0); }
	public WorkTargetAdapterFollowup(Callable<$V> $wrap, boolean $startReady, boolean $runOnce) { this($wrap,$startReady,$runOnce,0); }
	public WorkTargetAdapterFollowup(Callable<$V> $wrap, int $priority) { this($wrap,true,true,$priority); }
	public WorkTargetAdapterFollowup(Callable<$V> $wrap, boolean $startReady, boolean $runOnce, int $priority) {
		super($startReady, $runOnce, $priority);
		if ($wrap == null) throw new NullPointerException();
		this.$wrap = $wrap;
	}
	
	private final Callable<$V>	$wrap;
	
	protected $V run() throws Exception {
		$wrap.call();
		return null;
	}
}
