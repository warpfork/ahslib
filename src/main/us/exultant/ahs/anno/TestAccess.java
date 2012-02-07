package us.exultant.ahs.anno;

import java.lang.annotation.*;

/**
 * Expresses that an element's exposure is only intended for use in testing, and (even if,
 * strictly speaking, access rules allow it) it is not appropriate to refer to in any
 * non-testing code.
 * 
 * @author hash
 * 
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD})
public @interface TestAccess {
	/* unparameterized marker only */
}
