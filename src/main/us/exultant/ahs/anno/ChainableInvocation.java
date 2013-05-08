package us.exultant.ahs.anno;

import java.lang.annotation.*;

/**
 * <p>
 * Denotes that a method returns a pointer to the same object that the method was called
 * on. Methods with this type of signature are said to be supporting "chaining", because
 * it creates a distinctive form of syntactical sugar.
 * </p>
 *
 * <p>
 * If used on a static method, it typically means that the first argument is the one
 * returned. This is a common pattern with methods that build Strings by accepting a
 * {@link StringBuilder} as a buffer, for example.
 * </p>
 *
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 *
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface ChainableInvocation {
	/* unparameterized marker only */
}
