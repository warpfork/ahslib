package us.exultant.ahs.thread;

import java.util.*;

public interface WorkTarget {
	/**
	 * <p>
	 * Defines whether or not this WorkTarget currently has enough data available to
	 * complete some atom of work immediately if powered.
	 * </p>
	 * 
	 * <p>
	 * The return value of this method is used by {@link WorkScheduler} to determine
	 * whether or not it is presently appropriate to consider scheduling this
	 * WorkTarget to be powered by a thread. {@link WorkScheduler} checks this method
	 * under one of two conditions: either when triggered (i.e. by a callback on a
	 * {@link Pipe} that feeds work data for this target; this is the preferred
	 * mechanism) or when polled (in which case this WorkTarget must have been
	 * specially registered with the WorkScheduler for polling-based readiness checks;
	 * this mechanism can be useful if work data is produced at such a high volume
	 * that the reduced synchronization is a performance improvement, but the
	 * event-based mechanism is generally preferred and will have better performance
	 * when the system is at rest).
	 * </p>
	 * 
	 * <p>
	 * The answer to this question is based on a best-effort system and may not be
	 * exactly true under all circumstances due to the concurrent nature of the
	 * question. For example, this WorkTarget may claim to be ready when asked by the
	 * WorkScheduler, but then may turn out to not find work when it's actually
	 * powered because some other WorkTarget has drained from the some Pipe that
	 * provides the flow of work input data to both WorkTarget.
	 * </p>
	 * 
	 * @return true if this WorkTarget has at least one atom of work available; false
	 *         otherwise.
	 */
	public boolean isReady();
	
	/**
	 * <p>
	 * Causes the current thread to be consumed in the running of the
	 * <code>WorkTarget</code>, as per {@link Runnable#run()} method.
	 * </p>
	 * 
	 * <p>
	 * In order for a complete system of scheduled WorkTargets to work smoothly and
	 * with high efficiency, all actions taken by this method should as much as
	 * possible make full utilitization of the available processor resources and
	 * should not resorting to waiting or use blocking operations. (In a practical
	 * sence, this interface cannot impose a strict limit on the actual time that will
	 * be consumed by this call; if for example the <code>WorkTarget</code> is
	 * assigned to some sort of blocking I/O channel, the call may wait indefinitely
	 * in the same fashion as the underlying channel. However, this sort of behavior
	 * should be avoided at all costs since {@link WorkScheduler} assigns threads
	 * based on the assumption that this sort of idiocy will not be performed.)
	 * </p>
	 */
	//	 * <p>
	//	 * This interface provides the the argument for specifying number of times to be
	//	 * run as a kindness to both WorkAtom implementations which will be able to be
	//	 * more efficient when allocating some resources or buffers in blocks and reusing
	//	 * them across cycles, and also as a kindness to WorkScheduler implementations
	//	 * which may be responsible for running multiple WorkAtom instances wanting to
	//	 * have some vague control over how its attention is batched. However, if the
	//	 * WorkAtom implementer is truly lazy, there's nothing that requires them to heed
	//	 * the <code>$times</code> argument at all.
	//	 * </p>
	//	 * 
	//	 * @param $times
	//	 *                the number of internal cycles the <code>WorkAtom</code> should
	//	 *                complete before returning from this call.
	// the whole scheme of batching things makes it impossible for this to implement Runnable, which is... well, it makes things ishy when i compose my WorkScheduler of ThreadPoolExecutor.
	//  and really, if you have a WorkTarget doing atoms so small that it is a serious efficiency concern to process more than one atom at a time under a single lock... well, do it.  the scheduler doesn't actually really want to know about that (it did in mcon, but not everything is mcon).
	public void run();
	
	/**
	 * <p>
	 * Implementers use this method to allow the WorkTarget's <code>run()</code>
	 * method to know when to stop spinning.
	 * </p>
	 * 
	 * <p>
	 * Once this method returns true, it should never again return false.
	 * </p>
	 * 
	 * @return false if the WorkScheduler should continue to call
	 *         <code>run(int)</code> cyclically; true to when the cycle should exit at
	 *         its earliest opportunity.
	 */
	public boolean isDone();
	
	/**
	 * <p>
	 * Estimates the priority with which this WorkTarget should be scheduled relative
	 * to other WorkTarget at the same WorkScheduler; a higher priority indicates that
	 * this WorkTarget should be scheduled preferentially.
	 * </p>
	 * 
	 * <p>
	 * WorkScheduler may use this priority to the best of its ability in scheduling
	 * work for powering by the scheduler's threads, but the priority at any point in
	 * time is at most a best-effort basis and not a guarantee of order due to the
	 * concurrent nature of the scheduling and furthermore because a WorkScheduler
	 * implmentation may balance concerns other than priority (such as tasks scheduled
	 * based on wall-clock time).
	 * </p>
	 * 
	 * <p>
	 * This priority may change over time (this could be useful for example in a
	 * program which has several work buffers and always wishes to service the fullest
	 * one, or similiar the least recently served one); WorkScheduler who obey the
	 * priority hint will do their best to respond to this in a timely manner every
	 * time they are told to {@link WorkScheduler#update(WorkTarget)} their
	 * relationship with this WorkTarget.
	 * </p>
	 * 
	 * <p>
	 * Another example of effective use of the priority system is a server application
	 * which is composed of three types of task: nonblocking reads, some application
	 * logic, and nonblocking writes of the result. By simply giving writes the
	 * highest priority, application logic a medium priority, and reads the lowest
	 * priority, the entire application becomes optomized to keep all of its pipes
	 * between these task as small as possible, and furthermore if the reads happen to
	 * be based on some system including a sliding window (namely, TCP), the TCP
	 * buffer naturally fills in the OS kernel layer and causes the TCP window to
	 * automatically adapt when the application level logic is over burdened &mdash;
	 * all with no special effort from the application designer except for setting
	 * those three priorities.
	 * </p>
	 * 
	 * @return an integer representing the priority with which this WorkTarget should
	 *         currently be considered if it has work ready.
	 */
	public int getPriority();
	
	

	/**
	 * <p>
	 * Compares two WorkTarget based on their priority alone. This is useful for
	 * priority queues.
	 * </p>
	 * 
	 * <p>
	 * Note: this comparator imposes orderings that are inconsistent with equals.
	 * </p>
	 * 
	 * <p>
	 * Implementation note: this comparator is implemented as simple integer math
	 * without overflow checking, so if it is applied to datasets which contain
	 * priority values that can have a difference larger than
	 * {@link Integer#MAX_VALUE} results are unpleasant. Basically, keep your priority
	 * values between a billion and negative one billion and you'll be fine.
	 * </p>
	 * 
	 * @author hash
	 * 
	 */
	public static class PriorityComparator implements Comparator<WorkTarget> {
		public int compare(WorkTarget $o1, WorkTarget $o2) {
			// we could of course just compare Math.max(prio,2^31)... but i'm intending to use this in the tight bit of a synchronization block that the whole vm pivots around.  so, we're going with the 99% solution here because it runs faster.
			return $o1.getPriority() - $o2.getPriority();
		}
	}
}
