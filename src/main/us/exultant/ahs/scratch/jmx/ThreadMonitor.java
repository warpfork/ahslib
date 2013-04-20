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

import us.exultant.ahs.scratch.view.*;

import java.lang.management.*;

public class ThreadMonitor {
	public static ThreadMXBean getBean() {
		return ManagementFactory.getThreadMXBean();
	}
	public static long getTotalStartedThreadCount() {
		return getBean().getTotalStartedThreadCount();
	}
	
	private static ConsoleTable $ct;
	private static void init() {
		if ($ct == null) $ct = new ConsoleTable(3).setSizes(5,38,20).setPrefixes("id=","name=","state=");
	}
	
	
	
	public static void report() {
		System.err.println("THREADS:");
		System.err.print(getReport());
	}
	
	public static String getReport() {
		init();
		ThreadMXBean $bean = getBean();
		StringBuilder $sb = new StringBuilder();
		for (long $id : $bean.getAllThreadIds()) {
			ThreadInfo $ti = $bean.getThreadInfo($id);
			$sb.append("  ").append($ct.toString(Long.toString($id),$ti.getThreadName(),$ti.getThreadState().toString())).append('\n');
		}
		return $sb.toString();
	}
}
