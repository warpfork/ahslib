package us.exultant.ahs.anno;

import java.lang.annotation.*;

/**
 * Meta-annotation to state that one annotation implies the properties of others.
 * {@link Deterministic}ness implying (or in alternate words, being a stronger property
 * than) {@link Nullipotent}cy is an example of this.
 * 
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 * 
 */
@Documented
@Target(ElementType.ANNOTATION_TYPE)
public @interface Implies {
	Class<? extends Annotation>[] value();
}
