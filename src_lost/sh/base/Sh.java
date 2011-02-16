package ahs.sh.base;

import ahs.util.*;

/**
 * Decorates a ShInterface, providing it with services critical to instantiation and
 * exposing commonly used methods like getStdOut. A ShInterface should never be run
 * directly, but always wrapped and decorated by a Sh instance and run via the Sh
 * instance's run method.
 * 
 * @author hash
 * 
 */
public final class Sh implements Runnable {
	public Sh(ShProgram $raw, ShStream... $ins) {
		$x = $raw;
		$x.setInputStreams($ins);
		$x.getStreamRequest().fillRequest();
		$t = State.UNSTARTED;
	}
	
	private ShProgram	$x;
	private State		$t;	// i THINK checking this is an atomic operation, and so i haven't synchronized on it.  not sure if this is wish.  also, actually, not sure what "synchronized" means when you switch the value.
	private Object		$flare;	// wave this when changing state
	public static final ShStream PERPETUAL_NOTHING = new ShStream().close();
	//public static final int STDIN  = 0;
	public static final int STDOUT = 1;
	public static final int STDERR = 2;
	private static enum State {
		UNSTARTED, STARTED, FINISHED
	}
	
	public void run() {
		if ($t == State.UNSTARTED) {
			$t = State.STARTED;
			$flare.notifyAll();
			final ShStreamBundle $streams = $x.getStreamRequest();
			$streams.fillRequest();
			new Thread(new Runnable() {
				public void run() {
					$x.run();
					$streams.closeAll();
					$t = State.FINISHED;
					$flare.notifyAll();
				}
			}).start();
		}
	}
	
	public ShStream getOutputStream(int $fd) {
		return $x.getOutputStream($fd);
	}
	
	public ShStream getStdOut() {
		return $x.getOutputStream(STDOUT) == null ? PERPETUAL_NOTHING : $x.getOutputStream(STDOUT);
		// i suppose one might argue that it should be impossible for this stream to close until a program terminates, but f that.
	}
	
	public ShStream getStdErr() {
		return $x.getOutputStream(STDERR) == null ? PERPETUAL_NOTHING : $x.getOutputStream(STDERR);
		// i suppose one might argue that is should be impossible for this stream to close until a program terminates, but f that.
	}
	
	/**
	 * @return true if the process is running (i.e. has been started and has not yet finished); false otherwise.
	 */
	public boolean isRunning() {
		return ($t == State.STARTED);
	}
	
	/**
	 * @return true if the process has at any point been started -- that is, it returns true even if the process is finished.
	 */
	public boolean isStarted() {
		return ($t != State.UNSTARTED);
	}
	
	public boolean isFinished() {
		return ($t == State.FINISHED);
	}
	
	public void waitUntilStarted() {
		while (!isStarted())
			X.wait($flare);
	}
	
	public void waitUntilFinished() {
		while (!isFinished())
			X.wait($flare);
	}
}
