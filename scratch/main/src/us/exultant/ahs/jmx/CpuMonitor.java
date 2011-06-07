package us.exultant.ahs.jmx;

import java.lang.management.*;
import com.sun.management.OperatingSystemMXBean;

public class CpuMonitor {
	public CpuMonitor() {
		$processCpuTimePrev = OSB.getProcessCpuTime();
		$upTimePrev = RTB.getUptime();
	}
	
	
	private static final OperatingSystemMXBean	OSB	= (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
	private static final RuntimeMXBean		RTB	= ManagementFactory.getRuntimeMXBean();
	private static final int			nCPUs	= OSB.getAvailableProcessors();
	
	/**
	 * This replicates some code from the jconsole application (or anyway, it does
	 * according to some dude on the sun-cum-oracle forums who appears to be a
	 * developer for it), so essentially it gives you the same thing that jconsole's
	 * CPU graph would.
	 * 
	 * have a sneaking suspicion that some of the things it relies on aren't meant to
	 * be relied upon in production code, but in practice I guess it works.
	 * 
	 * @return bees, NOT divided by the number of cores -- so if you have an eight
	 *         core machine, you can get 800%.
	 */
	public float getTotalUsage() {
		$processCpuTime = OSB.getProcessCpuTime();
		$upTime = RTB.getUptime();
		long $elapsedCpu = $processCpuTime - $processCpuTimePrev;
		long $elapsedTime = $upTime - $upTimePrev;
		// elapsedCpu is in ns and elapsedTime is in ms.
		// cpuUsage could go higher than 100% because elapsedTime and elapsedCpu are not fetched simultaneously. Limit to 100% to maintain the general appearance of sanity (even if it's a lie).
		// and i don't know why i should need the zero side boundary, but i see jconsole f up at the end of its life sometime with violently negative percentages, so ima drop this in here.
		float $cpuUsage = Math.max(0F, Math.min(100F * nCPUs, $elapsedCpu / ($elapsedTime * 10000F)));
		
		$processCpuTimePrev = $processCpuTime;
		$upTimePrev = $upTime;
		
		return $cpuUsage;
	}
	private long	$processCpuTimePrev;
	private long	$processCpuTime;
	private long	$upTimePrev;
	private long	$upTime;
	
	/**
	 * @return a float between 0 and 100 to be interpreted as a percentage. On a
	 *         four-core machine, full utilization of one core alone will be reported
	 *         as 25%.
	 */
	public float getTotalUsageNormalized() {
		return Math.min(100F, getTotalUsage() / nCPUs);
	}
	
	// i'd really like to be able to do something to get stats per core, but that seems to be more or less impossible.
}
