package us.exultant.ahs.core;

import java.io.*;

/**
 * <p>
 * Represents any form of error that arises when attempting to translation between
 * different representations of the same data. For example, most JSONException are
 * actually examples of TranslationException, and are often found as the "cause" of a
 * TranslationException. The same is true of virtually any kind of parsing error.
 * </p>
 * 
 * @author hash
 * @see UnencodableException
 * 
 */
public class TranslationException extends IOException {
	public TranslationException() {
		super();
	}
	
	public TranslationException(String $arg0) {
		super($arg0);
	}
	
	public TranslationException(Throwable $arg0) {
		super($arg0);
	}
	
	public TranslationException(String $arg0, Throwable $arg1) {
		super($arg0, $arg1);
	}
}
