package us.exultant.ahs.thread;

import us.exultant.ahs.core.*;
import java.util.concurrent.*;

/**
 * <p>
 * A WorkScheduler is a thread pooling and work organizing mechanism.
 * </p>
 * 
 * <p>
 * Every running JVM should aim to have exactly one WorkScheduler. A default singleton is
 * available from {@link WorkManager#getDefaultScheduler()}; use this whenever possible.
 * </p>
 * 
 * <div style="border:1px solid; margin:1em; padding:1em;">
 * <h3>Relationship between WorkScheduler, WorkTarget, and the Future</h3>
 * 
 * <p>
 * {@link WorkTarget} represents runnable logic that performs some work when given a
 * thread. A {@link WorkScheduler} provides threads to all of the WorkTargets that it
 * manages in the best order it knows how &mdash corraling tasks with clock-based
 * schedules and tasks of various priorities, all of which may or may not be ready to
 * perform some work at any given time. A WorkScheduler returns a {@link WorkFuture} when
 * given a WorkTarget to manage; this behaves pretty much exactly like you'd expect any
 * {@link Future} implementation to behave. WorkTargets and WorkFutures thusly should
 * stand in a one-to-one and onto relationship.
 * </p>
 * 
 * <p>
 * It is only appropriate to schedule a single WorkTarget once and with a single
 * WorkScheduler at any one time. This condition is not checked in the code; it is up to
 * the developer to maintain sanity.
 * </p>
 * 
 * <p>
 * If a WorkTarget represents a task which can be performed by several threads at once (a
 * common example being some sort of server can respond to one client independantly per
 * thread), then several instances of the same WorkTarget implementation should be created
 * and each of them scheduled &mdash one per thread which should be able to respond. (If
 * you want as many threads as possible to be able to perform a type of work, consider
 * creating a {@link Factory} for the WorkTarget and using the
 * {@link WorkManager#scheduleOnePerCore(Factory,WorkScheduler)} helper method.)
 * </p>
 * </div>
 */
public interface WorkScheduler {
	/**
	 * <p>
	 * Arranges for a {@link WorkTarget} to be executed at the convenience of this
	 * scheduler and in accordance with the specifications made by the
	 * {@link ScheduleParams}. The {@link Future#get()} method of the returned
	 * {@link Future} will return once the WorkTarget is considered complete by the
	 * scheduler and will no longer be run, yielding the last result returned by the
	 * {@link WorkTarget#call()} method.
	 * </p>
	 * 
	 * <p>
	 * The javadocs of the {@link ScheduleParams} class explains when a WorkScheduler
	 * implementation should check for a WorkTarget to be shifted from waiting into
	 * the scheduled-and-ready-to-be-run queue. As a general rule though, any task
	 * that isn't triggered based on a wall-clock time will only be checked when the
	 * {@link #update(WorkFuture)} method is called.
	 * </p>
	 * 
	 * @param <$V>
	 *                The type returned by a WorkTarget.
	 * @param $work
	 *                The WorkTarget to schedule.
	 * @param $when
	 *                The parameters defining whether or not the work should be
	 *                scheduled in a clock-based fashion, and if so, how.
	 * @return A {@link Future} representing the state of the progress of there
	 *         WorkTarget.
	 */
	public <$V> WorkFuture<$V> schedule(WorkTarget<$V> $work, ScheduleParams $when);
	
	/**
	 * Call this method to have the WorkScheduler check the WorkTarget associated with
	 * the given Future for readiness to be scheduled.
	 * 
	 * @param <$V>
	 * @param $fut
	 *                The Future returned from an earlier invocation of the
	 *                {@link #schedule(WorkTarget, ScheduleParams)} method on this
	 *                same WorkScheduler.
	 */
	public <$V> void update(WorkFuture<$V> $fut);
}
