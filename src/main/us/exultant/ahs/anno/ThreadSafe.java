package us.exultant.ahs.anno;

import java.lang.annotation.*;

/**
 * <p>
 * "Thread safe" defines a function that is safe to call from any thread. The function has
 * been designed to produce valid results even when multiple threads are operating on the
 * same data, either because it is composed only of deterministic calls, or uses only
 * volatile variables, or promises to internally handle all appropriate locking without
 * bothering you about it.
 * </p>
 *
 * <p>
 * This can be a complex topic. Reading the javadocs of the annotated function to ensure
 * proper use is recommended.
 * </p>
 *
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 *
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD})	// i would put ElementType.CONSTRUCTOR here, but honestly?  you should NEVER do anything in a constructor that's not Nullipotent, or you're just fucking nuts and there's no salvation for you.
public @interface ThreadSafe {
	/* unparameterized marker only */

	// There was quite a bit of time where we considered using a string parameter to this annotation to describe locks that need to be held.
	// It got very confusing and has been abandoned.
	// If that concept is resurrected in the future, it will likely be as a separate annotation (probably called "HoldLock" or "GuardedBy" or something along those lines).
	// There is also debate whether or not separate annotations should be produced to mark those locks will be acquired automatically (as a purely informative service), versus those which the library developer is explicitly informing client code to be sure to grab.
}
