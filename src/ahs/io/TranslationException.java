package ahs.io;

/**
 * Represents any form of error that arises when attempting to translation between
 * different representations of the same data.  For example, most JSONException are
 * actually examples of TranslationException, and are often found as the "cause" of a
 * TranslationException.  The same is true of virtually any kind of parsing error.
 * 
 * @author hash
 * 
 */
public class TranslationException extends Exception {
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
