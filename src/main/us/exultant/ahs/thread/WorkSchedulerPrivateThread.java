package us.exultant.ahs.thread;

import us.exultant.ahs.util.*;
import java.util.*;

public class WorkSchedulerPrivateThread implements WorkScheduler {
	public WorkSchedulerPrivateThread() {
		$thread = new WorkerThread();
	}
	
	private final WorkerThread	$thread;
	private volatile boolean	$updatePlz;
	private WorkTarget<?>		$workTarget;
	private WorkFuture<?>		$workFuture;
	
	public WorkScheduler start() {
		$thread.start();
		return this;
	}
	
	
	public <$V> WorkFuture<$V> schedule(WorkTarget<$V> $work, ScheduleParams $when) {
		synchronized ($thread) {
			if ($workTarget != null) throw new IllegalStateException("this scheduler can only accept one work target");
			//hmm.  i suppose we can deal with delays, sure.  todo.
			WorkFuture<$V> $v = new WorkFuture<$V>(this,$work,$when);
			$workFuture = $v;
			X.notifyAll($thread);
			return $v;
		}
	}
	
	/**
	 * @param $fut
	 *                I don't even care what this is, actually; since this class only
	 *                deals with one piece of work and one thread, update is... yeah,
	 *                there's no meaning to parameterizing an update request when
	 *                there's only one thing to update.
	 */
	public <$V> void update(WorkFuture<$V> $fut) {
		X.notifyAll($thread);
	}
	
	/**
	 * @param $futs
	 *                I don't even care what this is, actually; since this class only
	 *                deals with one piece of work and one thread, update is... yeah,
	 *                there's no meaning to parameterizing an update request when
	 *                there's only one thing to update.
	 */
	public <$V> void update(Collection<WorkFuture<$V>> $futs) {
		X.notifyAll($thread);
	}
	
	private class WorkerThread extends Thread {
		public void run() {
			// wait until we get anything to work with
			while (true) {
				synchronized ($thread) {
					if ($workTarget != null) break;
					try {
						$thread.wait();
					} catch (InterruptedException $e) {
						/* um... ?  What, do you want me to die and not accept work?  Why did you even make me then? */
					}
				}
			}
			
			// work it
			while (true) {
				// if it's done, do the finishing work and let us rest
				if ($workFuture.isDone()) {
					$workFuture.$sync.tryFinish(false, null, null);
					break;	// i almost wanna let the thread object itself be gc'd after this even if someone's holding on to the scheduler.  can look into that someday; it just involves needing to change the object we're syncing on everywhere.
				}
				
				// try to shift it into schedulability or wait until we can (well, or let us rest if it was cancelled or finished concurrently).
				if (!$workFuture.$sync.scheduler_shift()) {
					if ($workFuture.getState() != WorkFuture.State.WAITING) break;
					synchronized ($thread) {
						try {
							$thread.wait();
						} catch (InterruptedException $e) {
							$workFuture.cancel(true);
							break;
						}
					}
					continue;
				}
				
				// shift it into running and run it
				$workFuture.$sync.scheduler_power();
			}
		}
	}
}
