package ahs.util.thread;

/**
 * <p>
 * Basic adapter for the Pumper interface.
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
