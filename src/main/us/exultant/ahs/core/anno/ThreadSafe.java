package us.exultant.ahs.core.anno;

import java.lang.annotation.*;

@Documented()
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})	// REFT doesn't make sense on a whole class obviously, but the other two?  maaaybe might.
public @interface ThreadSafe {
	Type how();
	
	public enum Type {
		/**
		 * <p>
		 * Declares that this entire code block is
		 * {@code referentially transparent} &mdash; that is, it neither reads nor
		 * writes any memory outside of its own scope. Specifically, it must treat
		 * its arguments as read-only, and only write memory it allocates
		 * internally, and its return value must either be one of its arguments or
		 * a piece of new memory. As long as the arguments to a referentially
		 * transparent method are not themselves subject to concurrent
		 * modification, referentially transparent method is trivially thread-safe
		 * under all conditions and need perform zero locking.
		 * </p>
		 * 
		 * <p>
		 * Referentially transparent functions are the holy grail of multithreaded
		 * performance: they are trivially thread-safe under all conditions and
		 * need perform zero locking.
		 * </p>
		 * 
		 * <p>
		 * A re-entrant code block is inherently nullipotent and idempotent (and
		 * should thus also be annotated with {@link Nullipotent}). A
		 * {@code referentially transparent} expression also deterministic by
		 * definition, as well as a valid candidate for memoization.
		 * </p>
		 * 
		 * <p>
		 * This concept is often mistakenly referred to as being re-entrant. See
		 * this <a href=
		 * "https://secure.wikimedia.org/wikipedia/en/wiki/Reentrancy_%28computing%29"
		 * >wikipedia article</a> for a discussion of the subtle reasons this
		 * terminology is inaccurate.
		 * </p>
		 */
		REFTRANSPARENT,
		
		/**
		 * <p>
		 * Declares that this entire block of code is synchronized: only one
		 * thread may ever enter it at once, and none of the memory referenced
		 * within this code block is capable of being altered by any other thread.
		 * </p>
		 * 
		 * <p>
		 * Synchronized functions are generally the most dead-simple way to get a
		 * job done safely in a multithreaded world, but often don't perform
		 * terribly well since they essentially produce areas of code where the
		 * entire program must act as if single-threaded. They also tend to face a
		 * danger of deadlocks if used wantonly.
		 * </p>
		 * 
		 * <p>
		 * <b><i>Note that a java method declared as {@code synchronized} does not
		 * automatically qualify for this flag!</i></b> If {@link Object#wait()},
		 * {@link Object#wait(long)}, or {@link Object#wait(long,int)} is called
		 * at any point within a java {@code synchronized} block, that code block
		 * does not qualify to be descibed with this level of type safety. Why?
		 * Because those wait methods allow other threads to aquire the monitor
		 * lock on that object.
		 * </p>
		 */
		SYNCHRONIZED,
		
		
		/**
		 * <p>
		 * Declares that this block of code is <i>conditionally</i> thread safe,
		 * and that you must hold a particular lock when calling it.
		 * </p>
		 */
		HOLDLOCK,
		
		/**
		 * <p>
		 * Declares that this block of code is safe to call from any thread: it
		 * may perform some kind of internal locking at some points and be
		 * referrentially transparent at others, and so in order to design a
		 * high-performance system you'll likely need to read the documentation
		 * completely in order to reason validly about the system.
		 * </p>
		 * 
		 * <p>
		 * Methods with delicate locking may perform badly if used incorrectly,
		 * but they still should NOT be capable of deadlock, no matter how they
		 * are used.
		 * </p>
		 */
		DELICATE
	}
}
