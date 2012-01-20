package us.exultant.ahs.anno;

import java.lang.annotation.*;

/**
 * Denotes that a method returns a pointer to the same object that the method was called
 * on. Methods with this type of signature are said to be supporting "chaining", because
 * it creates a distinctive form of syntactical sugar.
 * 
 * @author hash
 * 
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface ChainableInvocation {
	/* unparameterized marker only */
}
