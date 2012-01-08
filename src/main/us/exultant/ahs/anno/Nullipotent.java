package us.exultant.ahs.anno;

import java.lang.annotation.*;

/**
 * <p>
 * "Nullipotent" describes an operation that will produce the same results if executed
 * zero or multiple times. ("Getter" methods tend to be nullipotent, for example.) A
 * method may NOT have side effects and be considered nullipotent.
 * </p>
 * 
 * <p>
 * This definition is clearly stronger than {@link Idempotent}. It is weaker than
 * {@link Deterministic}, because a Nullipotent function is allowed to examine state
 * outside of itself, and thus may return different results over time even when given the
 * same arguments.
 * </p>
 * 
 * @author hash
 * 
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
@Implies(Idempotent.class)
public @interface Nullipotent {
	/* unparameterized marker only */
}
