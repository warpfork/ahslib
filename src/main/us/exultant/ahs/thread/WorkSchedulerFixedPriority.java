package us.exultant.ahs.thread;

import java.util.*;
import java.util.concurrent.*;

public class WorkSchedulerFixedPriority implements WorkScheduler {
	private WorkSchedulerFixedPriority() {
		this.$ready = new PriorityQueue<WorkTarget>();
		this.$waiting = new HashSet<WorkTarget>();
	}
	
	private final PriorityQueue<WorkTarget>	$ready;
	private final Set<WorkTarget>		$waiting;
	
	public void submit(WorkTarget $wt) {
		$waiting.add($wt);
		update($wt);
	}
	
	public void update(WorkTarget $wt) {
		if (!$wt.isReady()) return;	// even if it's not ready now but we'd previously put it in the priority queue, we leave it there because that datastructure isn't great at arbitrary removes, and we're fine waiting for it to bubble out and be dismissed eventually.
		
		if ($waiting.remove($wt)) $ready.add($wt);
	}
	
	
	
	
	private class Seshat extends ScheduledThreadPoolExecutor {
		public Seshat() {
			super(Runtime.getRuntime().availableProcessors());
		}
		public Seshat(int $corePoolSize) {
			super($corePoolSize);
		}
		
		protected void afterExecute(Runnable r, Throwable t) {
			super.afterExecute(r, t);
			//TODO:AHS:THREAD: mark as waiting, call update, and then check for scheduled work, then pull from ready work unless we get some
		}
		
		protected void beforeExecute(Thread t, Runnable r) {
			super.beforeExecute(t, r);
			//TODO:AHS:THREAD: mark as running (if we end up needing to track that explicitly)
		}
	}
}
