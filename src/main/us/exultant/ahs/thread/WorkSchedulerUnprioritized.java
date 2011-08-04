package us.exultant.ahs.thread;

import java.util.*;
import java.util.concurrent.*;

public class WorkSchedulerUnprioritized implements WorkScheduler {
	public WorkSchedulerUnprioritized() {
		this.$waiting = new HashSet<WorkTarget>();
		this.$fuck = new HashMap<Object,WorkTarget>();
		this.$seshat = new Seshat();
	}
	
	private final Set<WorkTarget>			$waiting;
	private final Map<Object,WorkTarget>		$fuck;
	private final ScheduledThreadPoolExecutor	$seshat;
	
	public void schedule(WorkTarget $wt) {
		if ($wt.isDone()) return;
		synchronized ($seshat) {
			$waiting.add($wt);
			update($wt);
		}
	}
	
	public void schedule(WorkTarget $wt, long $delay, TimeUnit $unit) {
		if ($wt.isDone()) return;
		$seshat.schedule($wt, $delay, $unit);
	}
	
	public void scheduleAtFixedRate(WorkTarget $wt, long $initialDelay, long $delay, TimeUnit $unit) {
		if ($wt.isDone()) return;
		$seshat.scheduleAtFixedRate($wt, $initialDelay, $delay, $unit);
	}
	
	public void scheduleWithFixedDelay(WorkTarget $wt, long $initialDelay, long $delay, TimeUnit $unit) {
		if ($wt.isDone()) return;
		$seshat.scheduleWithFixedDelay($wt, $initialDelay, $delay, $unit);
	}
	
	public void update(WorkTarget $wt) {
		synchronized ($seshat) {
			// if the WorkTarget isn't ready, we don't care.
			//	even if we'd previously put it in the priority queue but it's not ready now, we leave it there because that datastructure isn't great at arbitrary removes, and we're fine waiting for it to bubble out and be dismissed eventually.
			if (!$wt.isReady()) return;
			
			if ($waiting.remove($wt)) $fuck.put($seshat.submit($wt),$wt);
		}
	}
	
	
	
	private class Seshat extends ScheduledThreadPoolExecutor {
		public Seshat() {
			super(Math.min(4, Runtime.getRuntime().availableProcessors()));
		}
		public Seshat(int $corePoolSize) {
			super($corePoolSize);
		}
		
		protected void afterExecute(Runnable r, Throwable t) {
			super.afterExecute(r, t);
			// this Runnable r turns out to be ScheduledThreadPoolExecutor.ScheduledFutureTask<V> extends FutureTask<V> implements RunnableScheduledFuture<V>... which is too much of a dick to give us any exposure back to what we submitted, making it a serious pain in the ass to implement anything useful in this method without resorting to an utterly ridiculous hashmap.
			synchronized ($seshat) {
				WorkTarget $wt = $fuck.get(r);
				WorkSchedulerUnprioritized.this.schedule($wt);
			}
		}
	}
}
