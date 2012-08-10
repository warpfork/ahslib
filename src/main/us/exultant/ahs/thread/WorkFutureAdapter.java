package us.exultant.ahs.thread;

import java.util.concurrent.*;

/**
 * For when you need to create an implementor of WorkFuture, this does as much of the
 * common code as possible. (Mostly it implements all the status checking convenience
 * methods for you based on the return of {@link #getState()}, which you still must
 * provide when implemented the interface.)
 * 
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 * 
 */
abstract public class WorkFutureAdapter<$V> implements WorkFuture<$V> {
	public boolean isCancelled() {
		return getState() == State.CANCELLED;
	}
	
	public boolean isDone() {
		switch (getState()) {
			case FINISHED:
				return true;
			case CANCELLED:
				return true;
			default:
				return false;
		}
	}
	
	public Boolean isFinishedGracefully() throws CancellationException {
		switch (getState()) {
			case FINISHED:
				boolean $interrupted = false;
				while (true) {
					try {
						get();
						return true;
					} catch (ExecutionException $e) {
						return false;
					} catch (InterruptedException $e) {
						$interrupted = true;
					} finally {
						if ($interrupted) Thread.currentThread().interrupt();
					}
				}
			case CANCELLED:
				throw new CancellationException();
			default:
				return null;
		}
	}
}
