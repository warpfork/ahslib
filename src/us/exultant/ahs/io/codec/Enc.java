package us.exultant.ahs.io.codec;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Enc {
	String value() default "";	// I wish this could just be null.
	String[] selected() default { DEFAULT };
	public static final String DEFAULT = "$";
	public static final String SELECTED = "!";
	// someday it might behoove us to have an instantiableClass field here.  otherwise, how can we deal with generic interfaces (List<?> being a prime example)?
	//   i mean, we can detect that specific case since it's so common, but what about when people specifically want a linked list for performance reasons?
	//      they can revert to having some sort of assertInvariants method that takes care of it, sure (such a method should exist anyway), but still, ugh. 
}
