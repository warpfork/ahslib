package us.exultant.ahs.anno;

import java.lang.annotation.*;

/**
 * <p>
 * "Deterministic" describes a function that will consistently return the same result when
 * given the same arguments, and has no external impacts.
 * </p>
 * 
 * <p>
 * A compiler aware of this property would be capable of avoiding redundant function calls
 * and performing memoization.
 * </p>
 * 
 * <p>
 * This definition is stronger than {@link Nullipotent} (and thus by extension, also
 * stronger than {@link Idempotent}. A Deterministic function is inherently Nullipotent
 * and Idempotent; it is also inherently trivially thread safe without any locking or
 * synchronization.
 * </p>
 * 
 * <h3>A note about "sameness"</h3>
 * 
 * <p>
 * "Same arguments" in this case refers neither to pointer-equality over time nor
 * equals()-equality; the best definition is that all functions that expose state of the
 * argument must also have the same results if they were to be currently called.
 * "Same result" means that the output must be either pointer-equals or completely
 * immutable. (Deterministic functions are easiest to see when they're based on primitive
 * types, since they're all obviously immutable and thus the "sameness" criteria become
 * more unambiguous.)
 * </p>
 * 
 * <p>
 * While it is considered valid to apply this annotation to a function with non-primitive
 * arguments as long as the definitions of "same" outlined above are respected, strictly
 * enforcing the contract of {@link Deterministic} using the JVM becomes difficult if not
 * impossible. The contract is only strictly enforceable if all the arguments are not
 * concurrently modifiable &mdash; while this is typically quite easy for a developer to
 * adhere to, as lamented in the documentation for {@link Immutable}, it is technically
 * impossible to enforce due to the power of reflection.
 * </p>
 * 
 * <h3>Other Names</h3>
 * 
 * <p>
 * This property is also known in computer science as being "
 * {@code referentially transparent}
 * ".  Some texts refer to a function with this property as being a "pure function".
 * </p>
 * 
 * <p>
 * The term "re-entrant" is also commonly used when referring to this property, although
 * that is technically inaccurate as "re-entrant" has its own specific meaning.
 * </p>
 * 
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 * 
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
@Implies({ Nullipotent.class, ThreadSafe.class })
public @interface Deterministic {
	/* unparameterized marker only */
}
