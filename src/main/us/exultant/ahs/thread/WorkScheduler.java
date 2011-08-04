package us.exultant.ahs.thread;

import java.util.concurrent.*;

/**
 *
 */
public interface WorkScheduler {
	public void schedule(WorkTarget $wt);
	
	public void schedule(WorkTarget $wt, long $delay, TimeUnit $unit);
	
	public void scheduleAtFixedRate(WorkTarget $wt, long $initialDelay, long $delay, TimeUnit $unit);
	
	public void scheduleWithFixedDelay(WorkTarget $wt, long $initialDelay, long $delay, TimeUnit $unit);
	
	public void update(WorkTarget $wt);
	
	
	
	static enum WTState {
		/**
		 * the scheduler has put a thread onto the job and it has stack frames in
		 * the execution of the work.
		 */
		RUNNING,
		/**
		 * the work is wedged in some sort of queue immediating leading up to
		 * running (for example, in the executor since it's already been lifted
		 * out of the priority queue, or a clock-based task instead of an event
		 * triggered task); in this state, the readiness has already been checked
		 * and the order in which execution begins reletive to other work targets
		 * has been essentially finalized (i.e. priority or readiness changes are
		 * no longer noticed).
		 */
		SCHEDULED,
		/**
		 * the work is in the priority queue and can be pulled into the scheduled
		 * queue any time a running job relinquishes its power.
		 */
		READY,
		/**
		 * the work has not identified itself as having things to do immediately,
		 * so it will not be scheduled.
		 */
		WAITING,
		/**
		 * the work was running, but made a blocking call which returned thread
		 * power to the scheduler.  not currently used.
		 */
		PARKED,
	}
	
	
	
	
	
	//TODO:AHS:THREAD: i'm really not sure what i'm going to do about defaults for this stuff. 
//	/**
//	 * <p>
//	 * Gets a "default" WorkScheduler that is a singleton to the VM.
//	 * </p>
//	 * 
//	 * <p>
//	 * This method is performs lazy instantiation, is thread-safe, and the returned
//	 * WorkScheduler is already started with its own complement of worker threads when
//	 * returned.
//	 * </p>
//	 * 
//	 * @return the single default WorkScheduler for this VM.
//	 */
//	public static WorkScheduler getDefault() {
//		return SingletonHolder.INSTANCE;
//	}
//
//	private static class SingletonHolder {
//		public static final WorkScheduler INSTANCE = new WorkScheduler();
//	}
}
