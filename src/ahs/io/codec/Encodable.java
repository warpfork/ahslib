package ahs.io.codec;

import java.lang.annotation.*;

/**
 * <p>
 * Encoders and decoders based on reflective annotation (namely
 * {@link ahs.io.codec.eon.EonRAE} and {@link ahs.io.codec.eon.EonRAD}) check for this
 * annotation's presense on a class in order to see if they are allowed to operate on it
 * (and how). Classes wishing to be available for reflective annotation based encoders and
 * decoders should be annotated with this type, and also provide a constructor
 * <code>private ClassName(Encodable $x)</code> (which generally has an empty body).
 * </p>
 * 
 * <p>
 * Unfortunately, it's generally impossible to declare final fields which are subject to
 * this kind of shortcut in encoding -- if such final fields are a need, then a custom
 * encoder class must be involved that can interact with the class more directly.
 * </p>
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Encodable {
	/**
	 * <p>
	 * This string specifies the value that the encoder should use for a classname if
	 * the encoder has no other more specific instructions. However, this is only an
	 * advisory: it's completely legit for an encoder implementation to wayside this
	 * flag completely (though {@link ahs.io.codec.eon.EonRAE} and
	 * {@link ahs.io.codec.eon.EonRAD} respect it).
	 * </p>
	 * 
	 * <p>
	 * Two special values exist, declared in {@link #DEFAULT} and {@link #NONE}.
	 * <tt>DEFAULT</tt> instructs the use of the cannonical class name of the
	 * annotated class and is the default if no value is explicitly provided;
	 * <tt>NONE</tt> instructs that NO type name is to be inserted into the encoding,
	 * nor checked upon decoding.
	 * </p>
	 */
	String value() default DEFAULT;
	String[] styles() default { DEFAULT };
	public static final String DEFAULT = "$";
	public static final String NONE = "!";
	
	/**
	 * <p>
	 * Setting this flag to true causes encoders to assume that every non-static field
	 * in a class is subject to encoding. If left false (the default), encoders skip
	 * all fields not annotated with {@link Enc}.
	 * </p>
	 */
	boolean all_fields() default false;
}
