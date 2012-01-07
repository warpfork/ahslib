package us.exultant.ahs.core.anno;

import java.lang.annotation.*;

/**
 * <p>
 * "Deterministic" describes a function that will consistently return the same result when
 * given the same arguments, and has no external impacts.
 * </p>
 * 
 * <p>
 * A compiler aware of this property would be capable of avoiding redundant function calls
 * and performing inlining and memoization.
 * </p>
 * 
 * <p>
 * This definition is stronger than Idempotent. A Deterministic function is inherently
 * Idempotent.
 * </p>
 * 
 * <p>
 * "Same arguments" in this case refers neither to pointer-equality over time nor
 * equals()-equality; the best definition is that all functions that expose state of the
 * argument must also have the same results if they were to be currently called.
 * </p>
 * TODO clarify "same" for output as well (must it be pointer-eq?)
 * 
 * @author hash
 * 
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface Deterministic {
	
}
