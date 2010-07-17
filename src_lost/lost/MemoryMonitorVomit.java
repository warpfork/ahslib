package ahs.lost;

import ahs.util.*;
import java.lang.management.*;
import java.util.*;

public class MemoryMonitorVomit extends Thread {
	public MemoryMonitorVomit() { this(2000); }
	public MemoryMonitorVomit(int $ms) { this.$ms = $ms; }
	
	private int $ms;
	
	public void run() {
		while (true) {
			List<MemoryPoolMXBean> $pools = ManagementFactory.getMemoryPoolMXBeans();
			for (MemoryPoolMXBean $pool : $pools) {
				X.saye("MEM pool \""+$pool.getName()+"\"");
				X.saye("MEM\tusage\t"+vom($pool.getUsage()));
				X.saye("MEM\tgc\t"+vom($pool.getCollectionUsage()));
				X.saye("MEM\tpeak\t"+vom($pool.getPeakUsage()));
			}
			X.saye("");
			X.chill($ms);
		}
	}
	
	private String vom(MemoryUsage $use) {
		if ($use == null) return "(null)";
		StringBuilder $sb = new StringBuilder();
		$sb.append("init=").append($use.getInit()/1024).append("K\t");
		$sb.append("used=").append($use.getUsed()/1024).append("K\t");
		$sb.append("commit=").append($use.getCommitted()/1024).append("K\t");
		$sb.append("max=").append($use.getMax()/1024).append("K\t");
		return $sb.toString();
	}
}
