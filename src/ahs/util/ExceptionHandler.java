package ahs.util;

public interface ExceptionHandler<$T extends Throwable> extends Listener<$T> {
	/**
	 * Hear (and respond to) the given Throwable.
	 * 
	 * @param $e
	 */
	public void hear($T $e);
	
	
	
	
	// unfortunately, this isn't very useful unless you can accept a super-general handler, which is something you'd usually prefer to avoid.
	public static final ExceptionHandler<? extends Throwable> STDERR = new ExceptionHandler<Throwable>() {
		/**
		 * Punts the Throwable's stack trace to the standard error stream.
		 */
		public void hear(Throwable $e) {
			$e.printStackTrace();
		}
	};
}
