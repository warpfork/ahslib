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

import us.exultant.ahs.log.*;

public class WorkSchedulerFlexiblePriorityTest extends WorkSchedulerTest {
	public static void main(String... $args) {
		try {
			new WorkSchedulerFlexiblePriorityTest().run();
		} catch (Throwable $e) {	// this seems a bit daft, no?  but otherwise my eclipse console is missing some kinds of class cast exception, so... welp.
			$e.printStackTrace();
		}
	}
	
	public WorkSchedulerFlexiblePriorityTest() {
		super();
	}
	
	public WorkSchedulerFlexiblePriorityTest(Logger $log, boolean $enableConfirmation) {
		super($log, $enableConfirmation);
	}
	
	protected WorkScheduler makeScheduler(int $threads) {
		if ($threads == 0)
			return new WorkSchedulerFlexiblePriority(Math.max(4, Runtime.getRuntime().availableProcessors()));
		else
			return new WorkSchedulerFlexiblePriority($threads);
	}
}
