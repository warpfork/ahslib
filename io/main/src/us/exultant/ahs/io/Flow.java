package us.exultant.ahs.io;

/**
 * Pairs a source and a sink, typically as in a Socket.
 * 
 * @author hash
 * 
 */
public interface Flow<$T> {	// or Membrane, or Doublet, or something...
	public static class Basic<$T> implements Flow<$T> {
		public Basic(ReadHead<$T> $src, WriteHead<$T> $sink) {
			SRC = $src;
			SINK = $sink;
		}
		
		public final ReadHead<$T> SRC;
		public final WriteHead<$T> SINK;
		
		public ReadHead<$T> source() {
			return SRC;
		}
		
		public WriteHead<$T> sink() {
			return SINK;
		}
	}
	

	public ReadHead<$T> source();
	public WriteHead<$T> sink();
}
