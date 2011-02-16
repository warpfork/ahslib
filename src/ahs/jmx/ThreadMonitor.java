package ahs.jmx;

import ahs.jmx.MemoryMonitor.*;
import ahs.view.*;

import java.lang.management.*;
import java.util.*;

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
