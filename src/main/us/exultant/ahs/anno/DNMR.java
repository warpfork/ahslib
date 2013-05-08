package us.exultant.ahs.anno;

import java.lang.annotation.*;

/**
 * Expresses a desire that the object returned from the annotated function should not be
 * mutated. The object itself is not immutable, but the class exposing it wishes to treat
 * it as such &mdash; in other words, the annotated function is quite capable of badly
 * breaking abstraction, and the designer of that interface is politely asking you to
 * behave like an adult instead wasting time making the interface childproof.
 *
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 *
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface DNMR {

}
