package us.exultant.ahs.core;

public interface Translator<$FROM, $TO> {
	public $TO translate($FROM $x) throws TranslationException;
	
	/**
	 * Implements a no-op translator.
	 * 
	 * @author hash
	 *
	 * @param <$T>
	 */
	public static final class Noop<$T> implements Translator<$T,$T> {
		/**
		 * @return the same object as given, unmodified.
		 */
		public $T translate($T $x) {
			return $x;
		}
	}
}
