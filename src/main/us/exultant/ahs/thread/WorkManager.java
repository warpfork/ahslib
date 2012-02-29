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
import java.util.*;
import java.util.concurrent.*;

/**
 * Facade class with a default scheduler and quick methods for scheduling tasks.
 * 
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 *
 */
public class WorkManager {
	public static <$T> Future<Void> scheduleOnePerCore(Factory<WorkTarget<$T>> $wtf) {
		return scheduleOnePerCore($wtf, getDefaultScheduler());
	}
	public static <$T> Future<Void> scheduleOnePerCore(final Factory<WorkTarget<$T>> $wtf, final WorkScheduler $ws) {
		final int $n = Runtime.getRuntime().availableProcessors();
		List<WorkFuture<$T>> $fa = new ArrayList<WorkFuture<$T>>($n);
		for (int $i = 0; $i < $n; $i++)
			$fa.add($ws.schedule($wtf.make(), ScheduleParams.NOW));	// i assume it wouldn't often make sense to schedule the same task at the same time on multiple cores if it's clock-based
		return null;	//FIXME:AHS:THREAD: return an aggregate future
	}
	
	
	
	// factory methods for wrappers can also go in this class.
	
	
	
	
	
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
	public static WorkScheduler getDefaultScheduler() {
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder {
		public static final WorkScheduler INSTANCE = new WorkSchedulerFlexiblePriority(Math.max(4, Runtime.getRuntime().availableProcessors())).start();
	}
}
