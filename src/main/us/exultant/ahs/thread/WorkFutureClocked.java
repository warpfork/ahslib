package us.exultant.ahs.thread;

import java.util.concurrent.*;

class WorkFutureClocked<$V> extends WorkFuture<$V> implements ScheduledFuture<$V> {
	WorkFutureClocked<$V> makeDelayed(WorkTarget<$V> $wt, long $ns) {
		return new WorkFutureClocked<$V>($wt, $ns, 0);
	}
	WorkFutureClocked<$V> makeFixedRate(WorkTarget<$V> $wt, long $ns, long $period) {
		return new WorkFutureClocked<$V>($wt, $ns, $period);
	}
	WorkFutureClocked<$V> makeFixedDelay(WorkTarget<$V> $wt, long $ns, long $period) {
		return new WorkFutureClocked<$V>($wt, $ns, -$period);
	}
	private WorkFutureClocked(WorkTarget<$V> $wt, long $ns, long $period) {
		super($wt);
		this.$time = $ns;
		this.$period = $period;
	}
	
	/** The time the task is enabled to execute in nanoTime units */
	private long		$time;
	
	/**
	 * Period in nanoseconds for repeating tasks. A positive value indicates
	 * fixed-rate execution; a negative value indicates fixed-delay execution; a value
	 * of 0 indicates a non-repeating task.
	 */
	private final long	$period;
	
	public long getDelay(TimeUnit $unit) {
		return $unit.convert($time - System.nanoTime(), TimeUnit.NANOSECONDS);
	}
	
	public int compareTo(Delayed $o) {
		throw new UnsupportedOperationException("Use a Comparator.");
	}
	
	/**
	 * Returns true if this is a periodic (not a one-shot) action.
	 * 
	 * @return true if periodic
	 */
	public final boolean isPeriodic() {
		return $period != 0;
	}
	
	
	
	/**
	 * Sets the next time to run for a periodic task.
	 */
	void setNextRunTime() {
		if ($period > 0) $time += $period;
		else $time = System.nanoTime() + -$period;
		// DL's ScheduledThreadPoolExecutor has some fiddly bits here with a triggerTime and overflowFree function... but I don't really see why.  The problems don't show up unless you're scheduling tasks something like 22,900 TERAYEARS in the future, and at that point I think it's frankly clear that you're asking for trouble. 
	}
}
