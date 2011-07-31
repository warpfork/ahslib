package us.exultant.ahs.thread;

import java.util.*;
import java.util.concurrent.*;

/**
 *
 **/
public class WorkScheduler {
	
	
	//private final PriorityQueue<WorkTarget>	$ready;
	//private final Set<WorkTarget>			$waiting;
	// OKAY, SO:
	//	there is an enum possible states:
	//		RUNNING		the scheduler has put a thread onto the job and it has stack frames in the execution of the work.
	//		SCHEDULED	the work is wedged in some sort of queue immediating leading up to running (for example, in the executor since it's already been lifted out of the priority queue, or a clock-based task instead of an event triggered task); in this state, the readiness has already been checked and the order in which execution begins reletive to other work targets has been essentially finalized (i.e. priority or readiness changes are no longer noticed).
	//		READY		the work is in the priority queue and can be pulled into the scheduled queue any time a running job relinquishes its power.
	//		WAITING		the work has not identified itself as having things to do immediately, so it will not be scheduled.
	//		there may be more of these i haven't identified yet.  for example, if i let some calls become blocking within an actor but while returning thread power to the scheduler, those work would probably be considered to be in a distinctly different state than these -- PARKED, perhaps.
	//	we want priorities and we want to be able to update priorities in place.
	//		we're probably going to have to implement our own heap for this, because we want to be able to update the priority of a single dude in place by just sifting up or down without a linear walk of the whole heap to find him.
	//	all of the above: only sometimes.  prioritization like that involves  nontrivial memory overhead and extra object allocations and synchronization all over the place.
	//		so, simplier implementations must exist under the same interface.
	//		doing just non-updatable priorities should be relatively easy
	//		doing no priorities?  might as well provide that implementation, but i don't think it'll really be all that much lighter weight.
	private final Seshat			$seshat;
	
	public void submit(WorkTarget $wt) {
		$waiting.add($wt);
		update($wt);
	}
	
	public void update(WorkTarget $wt) {
		// if it's running, ignore (if it's recurrent it'll be updated when its put back in the piles after finishing this atom of run).
		
		// if it's not ready yet, remove it from that set and put it in the heap
		
		// if it wasn't unready and also wasn't in the heap and also isn't running, poop.
		
		// sift it up or down as necessary from its present location
		
		//TODO
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
