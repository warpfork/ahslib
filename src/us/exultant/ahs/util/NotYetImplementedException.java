package us.exultant.ahs.util;

/**
 * This exists to differenciate between situations where UnsupportedOperationException
 * means that something isn't supported by some particular implementation because it's
 * undefined or some such: a method throwing an ImBored exception generally means that
 * it's entirely possible to implement that method, but that it wasn't completed due to
 * time constraints.
 * 
 * @author hash
 * 
 */
public class NotYetImplementedException extends UnsupportedOperationException {
	public NotYetImplementedException() {
	}
	
	public NotYetImplementedException(String $message) {
		super($message);
	}
	
	public NotYetImplementedException(Throwable $cause) {
		super($cause);
	}
	
	public NotYetImplementedException(String $message, Throwable $cause) {
		super($message, $cause);
	}
}
