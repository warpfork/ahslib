package us.exultant.ahs.thread;

import us.exultant.ahs.core.*;
import java.io.*;

/**
 * <p>
 * Ferries data from an {@link #intake()} method to an {@link #output(Object)} method,
 * using a thread to perform some translation during the journey.
 * </p>
 * 
 * <p>
 * This is commonly used in the form of linking the {@link ReadHead} of one buffer to the
 * {@link WriteHead} of another, which is concretely provided by the {@link Teamster}
 * class.
 * </p>
 * 
 * @author hash
 * 
 * @param <$FROM>
 * @param <$TO>
 */
public abstract class Toaster<$FROM, $TO> implements Pump {
	public Toaster(Translator<$FROM, $TO> $trans) {
		this.$trans = $trans;
	}
	
	protected final Translator<$FROM,$TO>	$trans;
	protected ExceptionHandler<IOException>	$eh;
	
	public abstract boolean isDone();
	public abstract $FROM intake();
	public abstract void output($TO $x);
	
	public synchronized void run(final int $times) {
		for (int $i = 0; $i < $times; $i++) {
			if (isDone()) break;
			
			$FROM $a = intake();
			if ($a == null) break;
			
			$TO $b;
			try {
				$b = $trans.translate($a);
				
				if ($b == null) break;
				
				output($b);
			} catch (IOException $e) {
				// this error handling is the SAME for both errors in the translator and errors from writing the the sink.
				ExceptionHandler<IOException> $dated_eh = $eh;
				if ($dated_eh != null) $dated_eh.hear($e);
				break;
			}
		}
	}
}
