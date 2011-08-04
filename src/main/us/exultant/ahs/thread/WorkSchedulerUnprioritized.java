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
	
	public Future<?> schedule(WorkTarget $wt) {
		if ($wt.isDone()) return null;
		synchronized ($seshat) {
			$waiting.add($wt);
			return  doUpdate($wt);
		}
	}
	
	public Future<?> schedule(WorkTarget $wt, long $delay, TimeUnit $unit) {
		if ($wt.isDone()) return null;
		return $seshat.schedule($wt, $delay, $unit);
	}
	
	public Future<?> scheduleAtFixedRate(WorkTarget $wt, long $initialDelay, long $delay, TimeUnit $unit) {
		if ($wt.isDone()) return null;
		return $seshat.scheduleAtFixedRate($wt, $initialDelay, $delay, $unit);
	}
	
	public Future<?> scheduleWithFixedDelay(WorkTarget $wt, long $initialDelay, long $delay, TimeUnit $unit) {
		if ($wt.isDone()) return null;
		return $seshat.scheduleWithFixedDelay($wt, $initialDelay, $delay, $unit);
	}
	
	public void update(WorkTarget $wt) {
		doUpdate($wt);
	}
	
	private Future<?> doUpdate(WorkTarget $wt) {
		synchronized ($seshat) {
			// if the WorkTarget isn't ready, we don't care.
			//	even if we'd previously put it in the priority queue but it's not ready now, we leave it there because that datastructure isn't great at arbitrary removes, and we're fine waiting for it to bubble out and be dismissed eventually.
			if (!$wt.isReady()) return null;
			
			if ($waiting.remove($wt)) {
				Future<?> $fu = $seshat.submit($wt);
				$fuck.put($fu,$wt);
				return $fu;
			}
			return null;
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
