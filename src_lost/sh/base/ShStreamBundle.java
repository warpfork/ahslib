package ahs.sh.base;

import java.util.*;

/**
 * This class is used by jsh "programs" to request and recieve instantiated output
 * streams. It is necessary to bundle output streams in this way so that they can be
 * cleaned up properly when the "program" exits, even if the "program" itself ends
 * unexpectedly or fails to clean up properly after itself.
 * 
 * @author hash
 * 
 */
public class ShStreamBundle {
	/**
	 * Used by "programs" to request the number of output streams they will require.
	 * Typically, this is two -- one for stdout and one for stderr.
	 * 
	 * @param $streamsRequested
	 * @return a ShStreamBundle that will be initialized with the requested number of streams 
	 */
	public static ShStreamBundle makeRequest(int $streamsRequested) {
		return new ShStreamBundle($streamsRequested);
	}
	
	/**
	 * PACKAGE-PROTECTED method used by the Sh class to construct the streams
	 * requested.
	 */
	void fillRequest() {
		int $z = $m.size();
		$m.clear();
		for (int $i = 0; $i < $z; $i++)
			$m.add(new ShStream());
		$x = Collections.unmodifiableList($m);
	}
	
	private ShStreamBundle(int $streamsRequested) {
		$m = new ArrayList<ShStream>($streamsRequested);
		for (int $i = 0; $i < $streamsRequested; $i++)
			$m.add(null);
		$x = null;	// it already was, but i just want this to be perfectly clear.
	}
	
	private List<ShStream> $m;	// modifiable
	private List<ShStream> $x;	// unmodifiable
	//  
	
	/**
	 * Most implementors of the ShProgram interface will probably be able to get away
	 * with pointing their <code>getOutputStream(int)</code> method straight at
	 * <code>getStreams().get(int)</code>.
	 * 
	 * @return after the bundle has been properly intialized via the Sh class, an
	 *         unmodifiable list of ShStream objects (of the number requested) is
	 *         returned. Otherwise, if Sh has not initialized this bundle, returns
	 *         null.
	 */
	public List<ShStream> getStreams() {
		return $x;
	}
	
	/**
	 * Not atomic.
	 */
	public void closeAll() {
		for (ShStream $ss : $m) $ss.close();
	}
}
