package us.exultant.ahs.core.anno;

/**
 * "Idempotent" describes an operation that will produce the same results if executed once
 * or multiple times.
 * 
 * A method may have side effects and still be idempotent as long as the modified state
 * and returned data from the first call is the same for all subsequent calls.
 * 
 * See the <a href=
 * "https://secure.wikimedia.org/wikipedia/en/wiki/Idempotence#Computer_science_meaning"
 * >wikipedia article</a> for examples and extended definition.
 * 
 * @author hash
 * 
 */
public @interface Idempotent {
	
}
