package us.exultant.ahs.anno;

import java.lang.annotation.*;
import java.util.*;

/**
 * <p>
 * Denotes that a type is completely Immutable. To be Immutable, a class must abide by the
 * following restrictions:
 * <ul>
 * <li>exposes no fields as public, protected, or package visible
 * <li>exposes no methods that readily allow mutation of the object's fields
 * <li>all fields must contain only other Immutable or primitive types
 * </ul>
 * </p>
 * 
 * <p>
 * Functions on an Immutable object that take either no arguments or only primitives and
 * other Immutable objects (and also reference no global variables) are inherently
 * {@link Deterministic} since they only have visibility to data that is immutable.
 * </p>
 * 
 * <h3>Immutability and Unmodifiability</h3>
 * 
 * <p>
 * Note that the objects returned from methods such as
 * {@link Collections#unmodifiableCollection(Collection)} are NOT considered immutable!
 * <b>Unmodifiable and Immutable are different.</b> Unmodifiable means that an object
 * cannot cause it's own state to change, but it does not imply that some <i>other</i>
 * object cannot cause this object's state to change (i.e. as long as the argument to the
 * {@link Collections#unmodifiableCollection(Collection)} function remains, then the
 * returned Collection can be modified by that original Collection). Unmodifiability does
 * not have the same implications about thread safety as Immutability.
 * </p>
 * 
 * <h3>Enforceability</h3>
 * 
 * <p>
 * Technically, this is a difficult if not impossible thing to completely enforce using
 * the JVM itself, due to the giant can of worms that is reflection. The three criteria
 * defined earlier are considered sufficient for this annotation to be valid, even though it
 * is technically feasible to violate the concept using reflection.
 * </p>
 * 
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 * 
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Immutable {
	/* unparameterized marker only */
}
