package ahs.io;

import ahs.util.*;

import java.io.*;
import java.util.*;

/**
 * <p>
 * The complement of ReadHead.
 * </p>
 * 
 * <p>
 * WriteHead are typically expected to do most of their operations in the current thread
 * and complete them before they return. In situtations where this is untennable for some
 * reason,
 * </p>
 * 
 * @author hash
 */
public interface WriteHead<$T> {
	public void write($T $chunk) throws IOException;
	
	public void writeAll(Collection<? extends $T>  $chunks) throws IOException;
	
	public void flush();
	
	public boolean hasRoom();
	
	//public void setExceptionHandler(ExceptionHandler<IOException> $eh);	// what?
}
