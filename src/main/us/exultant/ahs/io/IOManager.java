/*
 * Copyright 2010 - 2013 Eric Myhre <http://exultant.us>
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

package us.exultant.ahs.io;

import us.exultant.ahs.thread.*;

public class IOManager {
	/**
	 * <p>
	 * Gets a "default" {@link SelectionSignaller} that is a singleton to the VM.
	 * </p>
	 * 
	 * <p>
	 * This method is performs lazy instantiation, is thread-safe, and the returned
	 * SelectionSignaller is already scheduled for execution in a private and
	 * reasonably configured thread pool. In other words, there's nothing to worry
	 * about.
	 * </p>
	 * 
	 * @return the single default SelectionSignaller for this VM.
	 */
	public static SelectionSignaller getDefaultSelectionSignaller() {
		return SingletonHolder_SelectionSignaller.INSTANCE;
	}

	private static class SingletonHolder_SelectionSignaller {
		public static final SelectionSignaller INSTANCE = new SelectionSignaller();
		static {
			INSTANCE.schedule(new WorkSchedulerFlexiblePriority(1).start(), ScheduleParams.NOW);
		}
	}
}
