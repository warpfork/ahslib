package ahs.io.codec;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Encodable {
	String value() default DEFAULT;	// this specifies the value that the encoder should use for a classname if the encoder has no other more specific instructions.
					// it's completely legit for an encoder implementation to wayside this flag completely
	String[] styles() default { DEFAULT };
	public static final String DEFAULT = "$";
	public static final String NONE = "!";
	
	boolean all_fields() default false;
}
