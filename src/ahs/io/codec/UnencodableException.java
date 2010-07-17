package ahs.io.codec;

import ahs.io.*;

/**
 * <p>
 * This class provides type-grouping for all exceptions that arise in the course of
 * attempting to translate an object to an encoded form (typically when data simply cannot
 * be represented in a given scheme, such as trying to store a double in a field meant for
 * an int, or a infinite number anywhere in a JSON scheme).
 * </p>
 * 
 * <p>
 * UnencodableException should be used where applicable in preference to the more general
 * TranslationException because applications may want to treat UnencodableException events
 * significantly differently (particularly since UnencodableException tend to only crop up
 * when type safety has already failed in some part of the code that should have been
 * better designed before compile time in the first place).
 * </p>
 * 
 * @author hash
 * 
 */
public class UnencodableException extends TranslationException {
	private UnencodableException() {
		super();
	}
	
	private UnencodableException(String $arg0, Throwable $arg1) {
		super($arg0, $arg1);
	}
	
	private UnencodableException(String $arg0) {
		super($arg0);
	}
	
	private UnencodableException(Throwable $arg0) {
		super($arg0);
	}
}
