package us.exultant.ahs.anno;

import java.lang.annotation.*;

/**
 * <p>
 * "Idempotent" describes an operation that will produce the same results if executed once
 * or multiple times.
 * </p>
 * 
 * <p>
 * A method may have side effects and still be idempotent as long as the modified state
 * and returned data from the first call is the same for all subsequent calls. (For
 * example, a method that sets an integer to 3 is idempotent; a method that increments an
 * integer each time it is called is NOT idempotent.) Note that idempotency need not be
 * composable: if you have a method that sets an integer to 3, and another method that
 * sets the same integer to 5, each is idempotent, even though when called repeatedly in
 * varying orders they produce different results.
 * </p>
 * 
 * <p>
 * Idempotency is not itself a kind of thread safety, but is a property that is frequently
 * of great use in constructing high performance thread-safe systems.
 * </p>
 * 
 * <p>
 * See the <a href=
 * "https://secure.wikimedia.org/wikipedia/en/wiki/Idempotence#Computer_science_meaning"
 * >wikipedia article</a> for examples and extended definition.
 * </p>
 * 
 * @author hash
 * 
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface Idempotent {
	/* unparameterized marker only */
}
