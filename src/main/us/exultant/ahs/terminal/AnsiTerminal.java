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

import static us.exultant.ahs.terminal.TermCodes.*;
import java.awt.*;
import java.io.*;
import java.util.*;

/**
 * Implements interface to a terminal that understands ANSI control sequences that is on
 * the System.console.
 */
public class AnsiTerminal implements Terminal {
	public static AnsiTerminal get() {
		return SingletonHolder.INSTANCE;
	}
	private static class SingletonHolder {
		public static final AnsiTerminal INSTANCE = new AnsiTerminal();
	}
	private AnsiTerminal() {
		Stty.takeControl();
		
		$console = System.console();
		$cursor = new OurCursor();
		cacheDimensions();
		
		clear();
	}
	
	private final Console	$console;
	private final Cursor	$cursor;
	private Palette		$palette;
	
	public void print(String $s) {
		$console.printf($s);
	}
	
	public Palette getPalette() {
		return $palette;
	}
	
	public Palette setPalette(Palette $p) {
		Palette $v = $palette;
		$palette = $p;
		$console.printf(encodePalette($p));
		return $v;
	}
	
	public void render(Window $buffer, Rectangle $region) {
		$region.x = Math.max(0, $region.x);
		$region.y = Math.max(0, $region.y);
		$region.width = Math.min(this.getWidth()-$region.x, $region.width);
		$region.height = Math.min(this.getHeight()-$region.y, $region.height);
		
		final StringBuilder $sb = new StringBuilder();
		Palette $prevPalette = null;		// use this to shortcut out of redundant escape sequences and checks whenever possible
		final int $ymax = $region.y + $region.height;
		final int $xmax = $region.x + $region.width;
		for (int $y = $region.y; $y < $ymax; $y++) {
			$sb.setLength(0);
			this.cursor().place($region.x, $y);
			final char[] $charsRow = $buffer.$chars[$y];
			final Palette[] $palesRow = $buffer.$pales[$y];
			for (int $x = $region.x; $x < $xmax; $x++) {
				final Palette $palette = $palesRow[$x];
				if (!$palette.equals($prevPalette)) $sb.append(encodePalette($palette));	//TODO:AHS:TERM: we could be doing substantially better delta'ing here.  also, the null selections are being allowed to go through here with an utterly odd concept of previous setting that's worse than useless for all practical purposes.
				$sb.append(($charsRow[$x] == 0x0) ? ' ' : $charsRow[$x]);			//XXX:AHS:TERM: we're... kinda assuming that our application is going to be nice enough to give us one graphical character per char.  The alternative is to reset the cursor position with every single character we output, which... would certainly work, but would add at least 6 extra bytes of crap to write to the terminal per every single real character.
				$prevPalette = $palette;
			}
			this.print($sb.toString());
		}
	}
	
	
	
	private class OurCursor implements Cursor {
		public void place(int $x, int $y) {
			$console.printf(TermCodes.CSI+(($y<0)?"":++$y)+";"+(($x<0)?"":++$x)+"f");
		}
		
		public void shiftUp(int $n) {
			$console.printf(TermCodes.CSI+$n+"A");
		}
		
		public void shiftDown(int $n) {
			$console.printf(TermCodes.CSI+$n+"B");
		}
		
		public void shiftLeft(int $n) {
			$console.printf(TermCodes.CSI+$n+"D");
		}
		
		public void shiftRight(int $n) {
			$console.printf(TermCodes.CSI+$n+"C");
		}
		
		public void lineNext(int $n) {
			shiftDown($n);
			$console.printf("\r");
			// xterm supports this, but konsole doesn't.  i suppose i could check for compliance at startup and emulate it with asking current location and then doing a place, but... i really intend to avoid that sort of thing as strongly as possible.
			//$console.printf(TermCodes.CSI+$n+"E");
		}
		
		public void linePrev(int $n) {
			shiftUp($n);
			$console.printf("\r");
			// xterm supports this, but konsole doesn't.  i suppose i could check for compliance at startup and emulate it with asking current location and then doing a place, but... i really intend to avoid that sort of thing as strongly as possible.
			//$console.printf(TermCodes.CSI+$n+"F");
		}
	}
	
	public Cursor cursor() {
		return $cursor;
	}
	
	public void clear() {
		$console.printf(
				TermCodes.CLEAR_SCREEN+	// duh
				TermCodes.CSI+"f"	// also reset cursor to 1,1 (some terminals do this with just the clear screen code, so we shoot for consistency here) (we don't use the normal function for cursor positioning because we can skip some characters by taking advantage of the "1;1" default args).
		);
	}
	
	public int getWidth() {
		return $dimensions.width;
	}
	public int getHeight() {
		return $dimensions.height;
	}
	private java.awt.Dimension $dimensions = new java.awt.Dimension(80,24);
	/** We have little option for terminal resize detection except to have a WorkTarget deal with this frequently and emit events as necessary.  (Or write JNI to catch OS signals, which... no). */
	void cacheDimensions() {
//		try {
//			$dimensions.width = new Scanner(Runtime.getRuntime().exec(new String[] { "tput cols" }).getInputStream()).nextInt();
//			$dimensions.height = new Scanner(Runtime.getRuntime().exec(new String[] { "tput lines" }).getInputStream()).nextInt();
//		} catch (IOException $e) {}
		
		// need to be able handle both output formats:
		// speed 9600 baud; 24 rows; 140 columns;
		// and:
		// speed 38400 baud; rows = 49; columns = 111; ypixels = 0; xpixels = 0;
		String $props = Stty.stty("-a");
		for (StringTokenizer $toker = new StringTokenizer($props, ";\n"); $toker.hasMoreTokens();) {
			String $tok = $toker.nextToken().trim();
			if ($tok.startsWith("columns")) {
				$dimensions.width = Integer.parseInt($tok.substring($tok.lastIndexOf(" ")).trim());
			} else if ($tok.endsWith("columns")) {
				$dimensions.width = Integer.parseInt($tok.substring(0, $tok.indexOf(" ")).trim());
			} else if ($tok.startsWith("rows")) {
				$dimensions.height = Integer.parseInt($tok.substring($tok.lastIndexOf(" ")).trim());
			} else if ($tok.endsWith("rows")) {
				$dimensions.height = Integer.parseInt($tok.substring(0, $tok.indexOf(" ")).trim());
			}
		}
		// a good bet on what's normally available on a modern monitor seems to be about 60x200 as far as i can tell.  take that with a huge dose of ymmv of course.
	}
	
	private static String encodePalette(Palette $p) {
		return
		(($p.$fg == null) ? "" : CSI+"3"+$p.$fg.code()+"m") +
		(($p.$bg == null) ? "" : CSI+"4"+$p.$bg.code()+"m") +
		(($p.$bold == null) ? "" : ($p.$bold) ? CSI+"1m" : CSI+"22m") +
		(($p.$underline == null) ? "" : ($p.$underline) ? REND_UNDERLINE_ON : REND_UNDERLINE_OFF);
	}
}
