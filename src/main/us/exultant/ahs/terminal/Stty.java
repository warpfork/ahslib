package us.exultant.ahs.terminal;

import us.exultant.ahs.io.*;
import java.io.*;

class Stty {
	public static void getAngry() {
		// save the console settings from before we start mucking things up
		final String $restoreStty = stty("-g");
		
		// Add a shutdown hook to restore console's state when we exit.
		Runtime.getRuntime().addShutdownHook(new Thread() { public void run() { stty($restoreStty); } });
		
		// muck things up
		stty(
				"-icanon min 1" +	// set the console to be character-buffered instead of line-buffered
				"-echo"			// disable character echoing
		);	// raw iutf8 icrnl opost isig
	}
	
	private static String stty(final String $args) {
		try {
			Process $proc = Runtime.getRuntime().exec(new String[] { "sh", "-c", "stty " + $args + " < /dev/tty" });
			$proc.waitFor();
			return IOForge.readString($proc.getInputStream());
		} catch (IOException $e) {
			throw new Error("failed to control console mode", $e);
		} catch (InterruptedException $e) {
			throw new Error("failed to control console mode", $e);
		}
	}	
}
