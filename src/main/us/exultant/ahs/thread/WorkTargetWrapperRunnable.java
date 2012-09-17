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

/**
 * <p>
 * Bridges the gap between {@link Runnable} and WorkTarget. The work is ready any time
 * that it's not done.
 * </p>
 * 
 * <p>
 * If constructed in run-once mode, the WorkTarget will run exactly once when scheduled
 * with a {@link WorkScheduler}; otherwise if run-once is false the work will always be
 * ready and will never become done (to stop it, the {@link WorkFuture} must be cancelled
 * or it must throw an exception).
 * </p>
 * 
 * <p>
 * If you find yourself wanting to construct one of these with a null {@link Runnable} and
 * then override the {@link #run()} method, perhaps you should just extend
 * {@link WorkTargetAdapterTriggerable}.
 * </p>
 * 
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 */
public class WorkTargetWrapperRunnable extends WorkTargetAdapterTriggerable<Void> {
	public WorkTargetWrapperRunnable(Runnable $wrap) { this($wrap,true,true,0); }
	public WorkTargetWrapperRunnable(Runnable $wrap, boolean $startReady, boolean $runOnce) { this($wrap,$startReady,$runOnce,0); }
	public WorkTargetWrapperRunnable(Runnable $wrap, int $priority) { this($wrap,true,true,$priority); }
	public WorkTargetWrapperRunnable(Runnable $wrap, boolean $startReady, boolean $runOnce, int $priority) {
		super($startReady, $runOnce, $priority);
		if ($wrap == null) throw new NullPointerException();
		this.$wrap = $wrap;
	}
	
	private final Runnable	$wrap;
	
	protected Void run() {
		$wrap.run();
		return null;
	}
}
