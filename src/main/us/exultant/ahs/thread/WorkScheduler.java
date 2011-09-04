package us.exultant.ahs.thread;

import us.exultant.ahs.core.*;
import java.util.concurrent.*;

/**
 *
 * <h3>Relationship between WorkScheduler, WorkTarget, and the Future</h3>
 *
 */
public interface WorkScheduler {
	/**
	 * Arranges for a {@link WorkTarget} to be executed at the convenience of this
	 * scheduler. The {@link Future#get()} method of the returned {@link Future} will
	 * return once {@link WorkTarget#isDone()} returns true, yielding the last result
	 * returned by the {@link WorkTarget#call()} method.
	 * 
	 * The WorkTarget will be checked for
	 * 
	 * It is only appropriate to schedule a single WorkTarget with a single
	 * WorkScheduler at any one time. This condition is not checked in the code; it is
	 * up to the developer to maintain sanity.  Note that if a WorkTarget leaves the scheduler by finishing with an exception (
	 * 
	 * @param <$V>
	 *                The type returned by a WorkTarget. (This is often {@link Void}
	 *                in the case of WorkTarget that are designed to be called
	 *                repeatedly, since they tend to ferry data in and out via
	 *                {@link ReadHead} and {@link WriteHead} instead of simply
	 *                returning one piece of data.)
	 * @param $wt
	 *                The WorkTarget to schedule.
	 * @return A {@link Future} representing the state of the progress of the
	 *         WorkTarget.
	 */
	public <$V> WorkFuture<$V> schedule(WorkTarget<$V> $wt);
	
	public <$V> WorkFuture<$V> schedule(WorkTarget<$V> $wt, long $delay, TimeUnit $unit);
	
	public <$V> WorkFuture<$V> scheduleAtFixedRate(WorkTarget<$V> $wt, long $initialDelay, long $delay, TimeUnit $unit);
	// so really, one has to admit that we could just wrap the clock stuff up with WorkTarget's isReady(), because, well, that just makes sense.  Well, except for the fact that that would make it really hard for us to sort them based on next-ready time, which is a pretty big deal.  so yes, we still do need a type that handles that explicitly.
	public <$V> WorkFuture<$V> scheduleWithFixedDelay(WorkTarget<$V> $wt, long $initialDelay, long $delay, TimeUnit $unit);
	
	public <$V> void update(WorkFuture<$V> $fut);	//TODO:AHS:THREAD:PLAN: so it's the future that's going to hold the magic data that lets us index into our heaps fast... but it's the worktarget that has, well, its own pointer to self, and is also what most other folks point to (the folks who are event listeners are the only ones that are super important).  so, that sucks.  soln?  hashmap, that's the best that can be done.  fortunately it only has to swim when we add or finish tasks rather than on every update or schedule or etc.  and most event listeners CAN get the Future pointer since they've been designed to be set after WT init, so really that's fine, just mildly annoying in its inelegance and unintuitiveness to someone new to the library.
}
