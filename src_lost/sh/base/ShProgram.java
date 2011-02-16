package ahs.sh.base;

import java.util.*;

/**
 * <p>
 * Any "shell program" should implement this interface. It provides the basic prototypes
 * required by the system to configure the "program" prior to running as well as specifies
 * the run method itself (which corresponds to the "main" method of the program).
 * </p>
 * 
 * <p>
 * Note that despite the fact that this interface extends the <code>Runnable</code>
 * interface, instances should never have their <code>run()</code> method called; the
 * ShProgram instance should always be decorated by a Sh and invoked via that wrapping.
 * </p>
 * 
 * <p>
 * Implementors may expect that the <code>setInputStreams</code> will be invoked first;
 * the <code>getStreamRequest</code> method second; and finally that ShStreamBundle to
 * then be filled with usable streams for output. The <code>run</code> and
 * <code>getOutputStream</code> methods will ONLY be called after this point (assuming
 * that the Sh class is running things as it should be); if either <code>run</code> or
 * <code>getOutputStream</code> are invoked before the other three actions are completed,
 * the implementor is perfectly within their rights to throw all sorts of exceptions (
 * <code>IllegalStateException</code> in particular).
 * </p>
 * 
 * @author hash
 * 
 */
public interface ShProgram extends Runnable {
	public void setEnvironment(Map<String,String> $env);
	
	/**
	 * Implementors of the ShProgram inteface will recieve their input streams via
	 * this method. It is not expected that this method will be invoked more than once
	 * in the lifetime of a given ShProgram instance, and so repeated invocations may
	 * be ignored at the implementor's leisure.
	 * 
	 * @param $ins
	 */
	public void setInputStreams(ShStream... $ins);
	
	/**
	 * Implementors of the ShProgram interface must request their output streams via
	 * the ShStreamBundle returned by this method. Generally, they might expect this
	 * method to be called only once, but it should never return different
	 * ShStreamBundle instances regardless of how many times it is called.
	 * 
	 * @return a ShStreamBundle requesting streams.
	 */
	public ShStreamBundle getStreamRequest();
	
	/**
	 * <p>
	 * Implementors of the ShProgram interface should put the "main" method of their
	 * program here.
	 * </p>
	 * 
	 * <p>
	 * This method will be invoked in its own thread, and after the ShStreamBundle
	 * returned by <code>getStreamRequest()</code> has been filled with usable streams
	 * for output. It should return only when the program has finished execution and
	 * should NOT return if this program has launched other threads/programs; output
	 * streams will be closed immediately after the return of this method.
	 * </p>
	 */
	public void run();
	
	/**
	 * <p>
	 * Return an output stream matching the given "file descriptor" number (or null if
	 * there is no such stream). Any invocation of this function with a particular
	 * number must always return the same stream (or if it returns null, must always
	 * return null).
	 * </p>
	 * 
	 * <p>
	 * Implementors are allowed to expect that <code>getStreamRequest()</code> will be
	 * invoked (and that stream bundle initialized) before this method is ever called.
	 * Thus, most implementors of the ShProgram interface will probably be able to get
	 * away with pointing this method straight at <code>getStreams().get(int)</code>
	 * on the ShStreamBundle returned by <code>getStreamRequest()</code>.
	 * </p>
	 * 
	 * @param $fd
	 *                the "file descriptor" number of the stream requested
	 * @return the stream pointed to by $fd, or null if there is no such stream.
	 */
	public ShStream getOutputStream(int $fd);
	
	// the one problem i have with this is that it's highly likely that the program will internally want to refer to something like STDOUT.write(*) instead of getOutput(1).write(*).
	// i suppose the default adapter for this interface will provide methods like that, since technically the entire concept of "STDOUT" is optional.
}
