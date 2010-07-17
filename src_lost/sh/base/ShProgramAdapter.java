package ahs.sh.base;

/**
 * <p>
 * Provides the typical environment for a program -- there will zero or one input streams,
 * and two output streams (stdout and stderr). Of the methods prototyped by the ShProgram
 * interface, only run still need be overriden by implementors.
 * </p>
 * 
 * <p>
 * The standard input stream can be accessed by extenders of this adapter via the
 * protected ShStream field <code>$stdin</code>; stdout and stderr can be written to via
 * the protected methods <code>out_write(String...)</code> and
 * <code>err_write(String...)</code>, respectively. The actual ShStream objects for stdout
 * and stderr can also be accessed via calling the <code>getOutputStream(int)</code>
 * method with the appropriate index.
 * </p>
 * 
 * <p>
 * Extenders of this class will likely also wish to include a nested class in their design
 * that implements ShInstantiator, as well as take care of issues with the "name" of their
 * program that arise in the instantiator.
 * </p>
 * 
 * @author hash
 * 
 */
public abstract class ShProgramAdapter implements ShProgram {
	public ShProgramAdapter() {
		$out = ShStreamBundle.makeRequest(2);
	}
	
	/**
	 * The input stream for this program. Should not be null when run is called unless
	 * the contract regarding method invocation order laid out in ShProgram is being
	 * disobeyed.
	 */
	protected ShStream $stdin;
	private ShStreamBundle $out;
	
	/**
	 * Accepts one or zero input streams.  Zero causes a blank, empty, closed stream to be used.
	 */
	public void setInputStreams(ShStream... $ins) {
		if ($stdin != null) throw new IllegalStateException("cannot set input stream twice.");
		if ($ins == null) $stdin = Sh.PERPETUAL_NOTHING;
		if ($ins.length > 1) throw new IllegalStateException("cannot set more than one input stream to this program.");
		if ($ins.length == 0) $stdin = Sh.PERPETUAL_NOTHING;
		$stdin = $ins[0];
	}

	public ShStreamBundle getStreamRequest() {
		return $out;
	}
	
	public ShStream getOutputStream(int $fd) {
		if ($out.getStreams() == null) throw new IllegalStateException("cannot get output streams before they are initialized.");
		return $out.getStreams().get($fd-Sh.STDOUT);	// since STDOUT is 1, an $fd of 1 should return the first/index-zero item in the list.
	}
	
	/**
	 * Writes directly to stdout, following contract for write(*) in ShStream.
	 */
	protected void out_write(String... $ss) {
		getOutputStream(Sh.STDOUT).write($ss);
	}
	
	/**
	 * Writes directly to stderr, following contract for write(*) in ShStream.
	 */
	protected void err_write(String... $ss) {
		getOutputStream(Sh.STDERR).write($ss);
	}
}
