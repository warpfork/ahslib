package us.exultant.ahs.scratch.jmx;

import java.lang.management.*;
import java.util.*;
import us.exultant.ahs.util.*;
import us.exultant.ahs.scratch.view.*;

public class MemoryMonitor {
	public static List<MemoryPoolMXBean> getBeans() {
		return ManagementFactory.getMemoryPoolMXBeans();
	}
	
	private static ConsoleTable $ct;
	public enum Resolution {
		BYTES	{ public String toString() { return "b";  } public int factor() { return 1;  } },
		KILO	{ public String toString() { return "kb"; } public int factor() { return 1024;  } },
		MEGA	{ public String toString() { return "mb"; } public int factor() { return 1024*1024;  } },
		GIGA	{ public String toString() { return "gb"; } public int factor() { return 1024*1024*1024;  } };
		public abstract int factor();
	}
	private static void init() {
		if ($ct == null) $ct = new ConsoleTable(4).setSizes(20,20,20,20).setPrefixes("init=","used=","commit=","max=");
	}
	
	
	
	public static void reportAll(Resolution $res) {
		for (MemoryPoolMXBean $pool : getBeans()) {
			X.saye("MEM POOL \""+$pool.getName()+"\"");
			X.saye("MEM    usage:   "+getReport($pool.getUsage(), $res));
			X.saye("MEM       gc:   "+getReport($pool.getCollectionUsage(), $res));
			X.saye("MEM     peak:   "+getReport($pool.getPeakUsage(), $res));
		}
	}
	
	public static String getAllReport(Resolution $res) {
		StringBuilder $sb = new StringBuilder();
		for (MemoryPoolMXBean $pool : getBeans()) {
			$sb.append("MEM POOL \"").append($pool.getName()).append("\"").append('\n');
			$sb.append("MEM    usage:   ").append(getReport($pool.getUsage(), $res)).append('\n');
			$sb.append("MEM       gc:   ").append(getReport($pool.getCollectionUsage(), $res)).append('\n');
			$sb.append("MEM     peak:   ").append(getReport($pool.getPeakUsage(), $res)).append('\n');
		}
		return $sb.toString();
	}
	
	public static void report(MemoryUsage $use, Resolution $res) {
		System.err.println(getReport($use, $res));
	}
	
	public static String getReport(MemoryUsage $use, Resolution $res) {
		init();
		if ($use == null) return $ct.toString("(null)","(null)","(null)","(null)");
		return $ct.toString(
				$use.getInit()/$res.factor()+$res.toString(),
				$use.getUsed()/$res.factor()+$res.toString(),
				$use.getCommitted()/$res.factor()+$res.toString(),
				$use.getMax()/$res.factor()+$res.toString()
		);
	}
	
	
	
	public static void reportAccumulatedUsage(Resolution $res) {
		System.err.println("memory useage totals:  "+getAccumulatedUsageReport($res));
	}
	public static String getAccumulatedUsageReport(Resolution $res) {
		init();
		long $init=0, $used=0, $comm=0, $maxx=0;
		for (MemoryPoolMXBean $pool : getBeans()) {
			MemoryUsage $use = $pool.getUsage();
			$init += $use.getInit();
			$used += $use.getUsed();
			$comm += $use.getCommitted();
			$maxx += $use.getMax();
		}
		return $ct.toString(
				$init/$res.factor()+$res.toString(),
				$used/$res.factor()+$res.toString(),
				$comm/$res.factor()+$res.toString(),
				$maxx/$res.factor()+$res.toString()
		);
	}
}
