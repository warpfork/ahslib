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

package us.exultant.ahs.scratch.jmx;

import us.exultant.ahs.util.*;
import us.exultant.ahs.scratch.view.gui.simple.*;
import us.exultant.ahs.scratch.view.gui.simple.Multibuffer.*;

public class Console {
	public Console(String $windowTitle) {
		final long $start = System.currentTimeMillis();
		final Tab $memory = new Tab("memory");
		final Tab $thread = new Tab("threads");
		final StringBuilder $sb = new StringBuilder();
		
		new Multibuffer($windowTitle).addTab($memory).addTab($thread).update();
		
		new Thread("ConsoleUpdateThread") {
			public void run() {
				long $delta;
				String $header;
				for (int $cycles = 0; true; $cycles++) {
					$delta = (System.currentTimeMillis() - $start);
					$sb.delete(0, $sb.length());
					$sb.append("run time: ").append($cycles).append("cycles; ").append($delta).append("ms; ").append($delta/1000).append("seconds");
					$header = $sb.toString();
					
					$memory.getTextArea().setText(
							"        --------    MEMORY    --------        " + $header + "\n\n" +
							MemoryMonitor.getAccumulatedUsageReport(MemoryMonitor.Resolution.MEGA) + "\n\n" +
							MemoryMonitor.getAllReport(MemoryMonitor.Resolution.KILO)
					);
					
					$thread.getTextArea().setText(
							"        --------    THREADS    --------        " + $header + "\n\n" +
							ThreadMonitor.getReport()
					);
					
					X.chill(500);
				}
			}
		}.start();
	}
	
	
	
	
	public static void main(String... $args) {
		new Console("console");
	}
}
