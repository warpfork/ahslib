package us.exultant.ahs.thread;

import java.util.*;
import java.util.concurrent.*;

public class WorkScheduler {
	
	
	private final PriorityQueue<WorkTarget>	$q;		// we're probably going to have to implement our own heap for this, since we want to be able to update priorities in place.  well, sometimes -- maybe that should be optional, because it will be nontrivial memory overhead and extra object allocations.
	private final Set<WorkTarget>		$notReady;
	private final Seshat			$seshat;
	
	public void update(WorkTarget $wt) {
		//TODO
	}
	
	
	
	
	
	
	
	private class Seshat extends ScheduledThreadPoolExecutor {
		public Seshat() {
			super(Runtime.getRuntime().availableProcessors());	// i might actually make this min 4, because it's nicer for applications to be able to assume at least some concurrency, i.e. if some actors do end up blocking stupidly on UI action.
		}
		public Seshat(int $corePoolSize) {
			super($corePoolSize);
		}
		
		protected void afterExecute(Runnable r, Throwable t) {
			super.afterExecute(r, t);
			//TODO
		}
		
		protected void beforeExecute(Thread t, Runnable r) {
			super.beforeExecute(t, r);
			//TODO
		}
	}
	
	
	
	/**
	 * <p>
	 * Gets a "default" WorkScheduler that is a singleton to the VM.
	 * </p>
	 * 
	 * <p>
	 * This method is performs lazy instantiation, is thread-safe, and the returned
	 * WorkScheduler is already started with its own complement of worker threads when
	 * returned.
	 * </p>
	 * 
	 * @return the single default WorkScheduler for this VM.
	 */
	public static WorkScheduler getDefault() {
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder {
		public static final WorkScheduler INSTANCE = new WorkScheduler();
	}
}
