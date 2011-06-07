package us.exultant.ahs.util.thread;

/**
 * This is the noun, not the verb.
 * 
 * @author hash
 *
 */
public interface Pump {
	/**
	 * <p>
	 * Causes the current thread to be consumed in the running of the
	 * <code>Pump</code> as much as per the <code>Runnable.run()</code> method, except
	 * this method should return after the <code>Pump</code> completes a given number
	 * of its internal cycles (or fewer if it so desires).
	 * </p>
	 * 
	 * <p>
	 * Note that the actual time that will be consumed by this call still has no
	 * limit; if for example the <code>Pump</code> is assigned to some sort of
	 * blocking I/O channel, the call may wait indefinitely in the same fashion as the
	 * underlying channel.
	 * </p>
	 * 
	 * <p>
	 * This interface provides the the argument for specifying number of times to be
	 * run as a kindness to both Pump implementations which will be able to be more
	 * efficient when allocating some resources or buffers in blocks and reusing them
	 * across cycles, and also as a kindness to Pumper implementations which may be
	 * responsible for running multiple Pump instances wanting to have control over
	 * how its attention is weighted. However, if the Pump implementer is truly lazy,
	 * there's nothing that requires them to heed the <code>$times</code> argument at
	 * all.
	 * </p>
	 * 
	 * @param $times
	 *                the number of internal cycles the <code>Pump</code> should
	 *                complete before returning from this call.
	 */
	public void run(final int $times);
	
	/**
	 * <p>
	 * Implementers use this method to allow the pump's <code>run()</code> method to
	 * know when to stop spinning.
	 * </p>
	 * 
	 * <p>
	 * Once this method returns true, it should never again return false.
	 * </p>
	 * 
	 * @return false if the pump should continue to call <code>run(int)</code>
	 *         cyclically; true to when the cycle should exit at its earliest
	 *         opportunity.
	 */
	public abstract boolean isDone();
}
