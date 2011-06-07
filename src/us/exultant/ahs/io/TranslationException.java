package us.exultant.ahs.io;

import java.io.*;

/**
 * <p>
 * Represents any form of error that arises when attempting to translation between
 * different representations of the same data. For example, most JSONException are
 * actually examples of TranslationException, and are often found as the "cause" of a
 * TranslationException. The same is true of virtually any kind of parsing error.
 * </p>
 * 
 * <p>
 * Note: the choice to extend IOException instead of simply Exception came after much
 * deliberation. The main reason that this hierarchy seems a logical choice is that
 * TranslationException most often are applicable in the same situations as other IO-like
 * exceptions (since its most common occurence is in serialization systems), and
 * translation can usually be visualized as something of a pipe itself; it has always been
 * in the IO package for a reason.
 * </p>
 * 
 * @author hash
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
