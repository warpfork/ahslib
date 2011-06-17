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
public class ImBored extends UnsupportedOperationException {
	public ImBored() {
	}
	
	public ImBored(String $message) {
		super($message);
	}
	
	public ImBored(Throwable $cause) {
		super($cause);
	}
	
	public ImBored(String $message, Throwable $cause) {
		super($message, $cause);
	}
}
