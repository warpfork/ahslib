package us.exultant.ahs.thread;

/**
 * <p>
 * Basic adapter for the <code>Pumper</code> interface for either running a pumpable task
 * in the current thread or in a new thread.
 * </p>
 * 
 * <p>
 * <code>run()</code> runs the <code>Pump</code> until it reports that it is done;
 * <code>start()</code> does so in a new thread that terminates when the <code>Pump</code>
 * reports it is done.
 * </p>
 * 
 * @author hash
 */
public class PumperBasic implements Pumper {
	public PumperBasic(Pump $pump) {
		$p = $pump;
	}
	
	private Pump $p; 
	
	/** {@inheritDoc} */
	public void run() {
		while (!$p.isDone())
			$p.run(Integer.MAX_VALUE);
	}
	
	/**
	 * Starts the pump in a brand new thread of its own.
	 */
	public synchronized void start() {
		new Thread(this, "AnonymousPumper").start();
	}
}
