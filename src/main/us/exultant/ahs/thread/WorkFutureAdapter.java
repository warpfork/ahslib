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
	
	public Boolean isFinishedGracefully() throws CancellationException, InterruptedException {
		switch (getState()) {
			case FINISHED:
				try {
					get();
					return true;
				} catch (ExecutionException $e) {
					return false;
				}
			case CANCELLED:
				throw new CancellationException();
			default:
				return null;
		}
	}
}
