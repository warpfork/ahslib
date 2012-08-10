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

import us.exultant.ahs.anno.*;
import java.util.concurrent.*;

/**
 * <p>
 * Implements most of the guts for the readiness and doneness functions for common tasks.
 * The task may start as unready, and become ready when a triggering function is called,
 * at which point it is ready until it is done. The task may optionally be defined as
 * run-once. The priority is fixed at construction time.
 * </p>
 * 
 * <p>
 * Tasks that would otherwise be expressed as {@link Callable} or {@link Runnable} are
 * likely to be easily expressed using this adapater (in particular note that
 * {@link WorkTargetWrapperCallable} and {@link WorkTargetWrapperRunnable} are already
 * implemented that way); tasks that deal with streams of events are probably better
 * expressed using a {@link WorkTargetAdapterFlowing}.
 * </p>
 * 
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 * 
 */
public abstract class WorkTargetAdapterTriggerable<$V> implements WorkTarget<$V> {
	public WorkTargetAdapterTriggerable(boolean $startReady, boolean $runOnce, int $priority) {
		$once = $runOnce;
		$ready = $startReady;
		$prio = $priority;
		$done = false;
	}
	
	private final boolean		$once;
	private final int		$prio;
	private volatile boolean	$ready;
	private volatile boolean	$done;
	
	@Idempotent
	@ThreadSafe
	@ChainableInvocation
	public WorkTargetAdapterTriggerable<$V> trigger() {
		$ready = true;
		return this;
	}
	
	/**
	 * Call this to cause the work target to become done &mdash; after calling
	 * this, the {@link WorkScheduler} will attempt to transition the state of
	 * the associated {@link WorkFuture} to {@link WorkFuture.State#FINISHED}.
	 * Resultingly, the {@link #run()} method will never be called again.
	 */
	@Idempotent
	protected final void done() { $done = true; }
	
	/**
	 * This method does the readiness and run-once checks, then passes control
	 * to the {@link #run()} method which you must define.
	 */
	public final $V call() throws Exception {
		if ($done) throw new IllegalStateException("This task is already done!");
		if (!$ready) return null;
		$V $v = run();
		if ($once) done();
		return $v;
	}
	protected abstract $V run() throws Exception;
	
	/** returns true any time {@link #trigger()} has been called and {@link #isDone()} is still false. */
	public final boolean isReady() { return !isDone() && $ready; }
	/** returns true when either {@link #done()} has been called or the task was run-once and has been run. */
	public final boolean isDone() { return $done; }
	/** @inheritDocs */
	public final int getPriority() { return $prio; }
}
