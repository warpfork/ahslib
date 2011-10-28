/*
 * Copyright 2010, 2011 Eric Myhre <http://exultant.us>
 * 
 * This file is part of AHSlib.
 *
 * AHSlib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License, or
 * (at the original copyright holder's option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package us.exultant.ahs.terminal;

import us.exultant.ahs.util.*;
import us.exultant.ahs.io.*;
import java.io.*;

public class Stty {
	public static synchronized void takeControl() {
		if (System.console() == null) throw new MajorBug("this program is not attached to a console, but is trying to set console modes");
		
		if ($once == true) {
			if ($restoreStty == null)
				throw new MajorBug("control of the console can only be acquired once!");	// this wouldn't be hard to support, actually, but i don't know why you'd want it and i don't wanna futz around with the shutdown hooks that much.
			else return;	// someone already did it, and we're still in control, so return quietly.
		}
		$once = true;
		
		// save the console settings from before we start mucking things up
		$restoreStty = stty("-g");
		
		// Add a shutdown hook to restore console's state when we exit.
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				releaseControl();
			}
		});
		
		// muck things up
		stty(
				"-icanon min 1 " +	// set the console to be character-buffered instead of line-buffered
				"-echo "		// disable character echoing
		);	// raw iutf8 icrnl opost isig
	}
	
	private static String $restoreStty = null;
	private static boolean $once = false;
	
	public static synchronized void releaseControl() {
		if ($restoreStty == null) return;	// someone already did it.
		stty($restoreStty);
		stty("echo");	// for some reason the restore string doesn't seem to do this (?@!??!)
		System.console().printf(TermCodes.REND_RESET+TermCodes.CLEAR_SCREEN+TermCodes.CSI+"f");
		$restoreStty = null;
	}
	
	static String stty(final String $args) {
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
