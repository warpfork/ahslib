package ahs.io;

/**
 * <p>
 * The complement of ReadHead.
 * </p>
 * 
 * @author hash
 */
public interface WriteHead<$T> {
	public void write($T $chunk);
	
	public void writeAll($T... $chunks);
	
	public void flush();
	
	public boolean hasRoom();
	
	
}
