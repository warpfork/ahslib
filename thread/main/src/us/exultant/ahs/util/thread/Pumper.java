package us.exultant.ahs.util.thread;

/**
 * This interface groups classes that are designed to provide functionality for Pump
 * instances (but is syntactically no different than any other Runnable; it simply carries
 * the implication that the class has a constructor or initializer method somewhere that
 * accepts a Pump argument).
 * 
 * @author hash
 * 
 */
public interface Pumper extends Runnable {
	/**
	 * <p>
	 * Similar to the general contract of the <code>run()</code> method in the
	 * <code>Runnable</code> interface, calling <code>run()</code> on a
	 * <code>Pumper</code> causes the current thread to be consumed in the running of
	 * the <code>Pumper</code> until the <code>Pumper</code> either finishes or is
	 * stopped.
	 * </p>
	 */
        public void run();	
}
