/*
 * Copyright 2010, 2011 Eric Myhre <http://exultant.us>
 * 
 * This file is part of AHSlib.
 *
 * AHSlib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License, or
 * (at the original copyright holder's option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package us.exultant.ahs.thread;

import us.exultant.ahs.core.*;
import us.exultant.ahs.util.*;
import java.util.*;
import java.util.concurrent.*;
import org.slf4j.*;

/**
 * Facade class with a default scheduler and quick methods for scheduling tasks.
 * 
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 *
 */
public class WorkManager {
	static final Logger LOG = LoggerFactory.getLogger(WorkManager.class);
	
	/**
	 * Uses a {@link Factory} to produce one {@link WorkTarget} instance per core on
	 * the machine and schedules them with the {@link #getDefaultScheduler() default
	 * scheduler}, and returns an {@link WorkFutureAggregate} that allows you to track
	 * their overall progress.
	 * 
	 * @param $wtf
	 * @return an {@link WorkFutureAggregate}
	 */
	public static <$T> Future<Void> scheduleOnePerCore(Factory<WorkTarget<$T>> $wtf) {
		return scheduleOnePerCore($wtf, getDefaultScheduler());
	}
	
	/**
	 * Uses a {@link Factory} to produce one {@link WorkTarget} instance per core on
	 * the machine and schedules them with the {@link WorkScheduler} provided, and
	 * returns an {@link WorkFutureAggregate} that allows you to track their overall
	 * progress.
	 * 
	 * @param $wtf
	 * @param $ws
	 * @return an {@link WorkFutureAggregate}
	 */
	public static <$T> Future<Void> scheduleOnePerCore(final Factory<WorkTarget<$T>> $wtf, final WorkScheduler $ws) {
		final int $n = Runtime.getRuntime().availableProcessors();
		List<WorkFuture<$T>> $fa = new ArrayList<WorkFuture<$T>>($n);
		for (int $i = 0; $i < $n; $i++)
			$fa.add($ws.schedule($wtf.make(), ScheduleParams.NOW));	// i assume it wouldn't often make sense to schedule the same task at the same time on multiple cores if it's clock-based
		return new WorkFutureAggregate<$T>($fa);
	}
	
	
	
	// factory methods for wrappers can also go in this class.
	
	
	
	/**
	 * <p>
	 * This factory method produces a {@link Listener} which upon being called will
	 * invoke {@link WorkFuture#update()} on the given {@link WorkFuture}.
	 * </p>
	 * 
	 * <p>
	 * Typically, you'll want to invoke this with the
	 * <code>WorkManager.&lt;$TYPE&gt;updater($workFuture)</code> syntax in order to
	 * get a Listener of the appropriate generic type for your requirements.
	 * </p>
	 * 
	 * @param $wf
	 *                the WorkFuture instance to invoke {@link WorkFuture#update()}
	 *                upon
	 * @return a new Listener
	 */
	public static <$T> Listener<$T> updater(WorkFuture<?> $wf) {
		return new WorkFutureUpdater<$T>($wf);
	}
	private final static class WorkFutureUpdater<$T> implements Listener<$T> {
		public WorkFutureUpdater(WorkFuture<?> $wf) {
			this.$wf = $wf;
		}
		
		private final WorkFuture<?>	$wf;
		
		public final void hear($T $x) {
			$wf.update();
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
	 * <p>
	 * In the interest of simplicity, periodic flushing of this WorkScheduler has been
	 * enabled via
	 * {@link WorkManager#periodicallyFlush(WorkScheduler, long, TimeUnit)}; flushing
	 * will take place every 2ms.
	 * </p>
	 * 
	 * <p>
	 * The {@link WorkScheduler#completed()} ReadHead of this scheduler is set up with
	 * a listener that consumes completed tasks, and issues a warning to the
	 * WorkManager's logger if any tasks ended by throwing an Exception (exactly as if
	 * {@link #attachFailureLogger(WorkScheduler)} had been applied to the scheduler).
	 * </p>
	 * 
	 * @return the single default WorkScheduler for this VM.
	 */
	public static WorkScheduler getDefaultScheduler() {
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder {
		public static final WorkScheduler INSTANCE;
		static {
			INSTANCE = new WorkSchedulerFlexiblePriority(Math.max(4, Runtime.getRuntime().availableProcessors()*2));
			attachFailureLogger(INSTANCE);
			periodicallyFlush(INSTANCE, 2, TimeUnit.MILLISECONDS);
			INSTANCE.start();
		}
	}
	
	/**
	 * <p>
	 * Attaches a listener to the {@link WorkScheduler#completed()} ReadHead of this
	 * scheduler that consumes completed tasks, and issues a message to WorkManager's
	 * logger at the ERROR level if any tasks ended by throwing an Exception. (The
	 * same effect can be achieved using other loggers by using
	 * {@link WorkFailureLogger}.)
	 * </p>
	 * 
	 * <p>
	 * Removing or replacing this behavior only requires changing the listener on
	 * {@link WorkScheduler#completed()} ReadHead as is usual for any ReadHead.
	 * </p>
	 * 
	 * @param $scheduler
	 */
	public static void attachFailureLogger(WorkScheduler $scheduler) {
		$scheduler.completed().setListener(WorkFailureLogger.SingletonHolder.INSTANCE);
	}
	
	public static class WorkFailureLogger implements Listener<ReadHead<WorkFuture<Object>>> {
		static class SingletonHolder { static final WorkFailureLogger INSTANCE = new WorkFailureLogger(LOG); }
		
		public WorkFailureLogger(Logger $log) { this.$log = $log; }
		private final Logger $log;
		
		public void hear(ReadHead<WorkFuture<Object>> $x) {
			boolean $interrupted = false;
			try {
				for (WorkFuture<Object> $wf : $x.readAllNow()) {
					switch ($wf.getState()) {
						case FINISHED:
							while (true) {
								try {
									$wf.get();
								} catch (InterruptedException $e) {
									$interrupted = true;
									continue;
								} catch (ExecutionException $e) {
									$log.error("WorkFuture {} terminated with exception: ", $wf, $e);
								}
								break;
							} break;
						case CANCELLED:
							/* i presume this was on purpose and thus you don't want to hear about it. */
							break;
						default:
							throw new MajorBug();
					}
				}
			} finally {
				if ($interrupted) Thread.currentThread().interrupt();
			}
		}
	}
	
	
	
	/**
	 * Schedules a task on the scheduler to periodically flush itself.
	 * 
	 * @return the WorkFuture of the flush task.
	 */
	public static WorkFuture<Void> periodicallyFlush(WorkScheduler $scheduler, long $time, TimeUnit $timeunit) {
		return $scheduler.schedule(new SchedulerFlushWorkTarget($scheduler), ScheduleParams.makeFixedDelay($time, $timeunit));
	}
	
	static class SchedulerFlushWorkTarget implements WorkTarget<Void> {
		SchedulerFlushWorkTarget(WorkScheduler $scheduler) { this.$scheduler = $scheduler; }
		private final WorkScheduler $scheduler;
		public Void call() { $scheduler.flush(); return null; }
		public boolean isDone() { return false; }
		public boolean isReady() { return true; }
		public int getPriority() { return -100000; }
	}

}
