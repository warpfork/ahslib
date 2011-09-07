package us.exultant.ahs.thread;

import us.exultant.ahs.core.*;
import java.util.concurrent.*;

/**
 *
 * <h3>Relationship between WorkScheduler, WorkTarget, and the Future</h3>
 *
 */
public interface WorkScheduler {
	/**
	 * <p>
	 * Arranges for a {@link WorkTarget} to be executed at the convenience of this
	 * scheduler. The {@link Future#get()} method of the returned {@link Future} will
	 * return once {@link WorkTarget#isDone()} returns true, yielding the last result
	 * returned by the {@link WorkTarget#call()} method.
	 * </p>
	 * 
	 * <p>
	 * The WorkTarget will be checked for readiness immediately upon call of this
	 * method, and thereafter be run by a when the scheduler has resources available
	 * according to the priority of the WorkTarget. If the WorkTarget is not ready at
	 * the time of first scheduling, the scheduler is not required to run it until
	 * after an invocation of {@link #update(WorkFuture)} (with an argument of the
	 * WorkFuture returned by the first scheduling call) that occurs when the
	 * WorkTarget is ready.
	 * </p>
	 * 
	 * <p>
	 * If a WorkTarget represents a task which can be performed by several threads at
	 * once (a common example being some sort of server can respond to one client
	 * independantly per thread), then several instances of the same WorkTarget
	 * implementation should be created and each of them scheduled &mdash one per
	 * thread which should be able to respond. (If you want as many threads as
	 * possible to be able to perform a type of work, consider creating a
	 * {@link Factory} for the WorkTarget and using the
	 * {@link WorkManager#scheduleOnePerCore(Factory,WorkScheduler)} helper method.)
	 * </p>
	 * 
	 * <p>
	 * It is only appropriate to schedule a single WorkTarget once and with a single
	 * WorkScheduler at any one time. This condition is not checked in the code; it is
	 * up to the developer to maintain sanity.
	 * </p>
	 * 
	 * @param <$V>
	 *                The type returned by a WorkTarget. (This is often {@link Void}
	 *                in the case of WorkTarget that are designed to be called
	 *                repeatedly, since they tend to ferry data in and out via
	 *                {@link ReadHead} and {@link WriteHead} instead of simply
	 *                returning one piece of data.)
	 * @param $wt
	 *                The WorkTarget to schedule.
	 * @return A {@link Future} representing the state of the progress of the
	 *         WorkTarget.
	 */
	public <$V> WorkFuture<$V> schedule(WorkTarget<$V> $wt);
	
	public <$V> WorkFuture<$V> schedule(WorkTarget<$V> $wt, long $delay, TimeUnit $unit);
	
	public <$V> WorkFuture<$V> scheduleAtFixedRate(WorkTarget<$V> $wt, long $initialDelay, long $delay, TimeUnit $unit);
	
	public <$V> WorkFuture<$V> scheduleWithFixedDelay(WorkTarget<$V> $wt, long $initialDelay, long $delay, TimeUnit $unit);
	
	public <$V> void update(WorkFuture<$V> $fut);
}
