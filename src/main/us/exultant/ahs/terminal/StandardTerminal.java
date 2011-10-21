package us.exultant.ahs.terminal;

import us.exultant.ahs.util.*;
import java.io.*;
import java.util.*;

public class StandardTerminal implements Terminal {
	public static StandardTerminal get() {
		return SingletonHolder.INSTANCE;
	}
	private static class SingletonHolder {
		public static final StandardTerminal INSTANCE = new StandardTerminal();
	}
	private StandardTerminal() {
		Stty.getAngry();
		
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
		$console.printf($p.code());
		return $v;
	}
	
	
	
	private class OurCursor implements Cursor {
		public void place(int $x, int $y) {
			$console.printf(TermCodes.CSI+(($y<1)?"":$y)+";"+(($x<1)?"":$x)+"f");
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
	protected void cacheDimensions() {
//		try {
//			$dimensions.width = new Scanner(Runtime.getRuntime().exec(new String[] { "tput cols" }).getInputStream()).nextInt();
//			$dimensions.height = new Scanner(Runtime.getRuntime().exec(new String[] { "tput lines" }).getInputStream()).nextInt();
//		} catch (IOException $e) {}
		
		// need to be able handle both output formats:
		// speed 9600 baud; 24 rows; 140 columns;
		// and:
		// speed 38400 baud; rows = 49; columns = 111; ypixels = 0; xpixels = 0;
		String $props = Stty.stty("-a");
		X.saye($props);
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
	}
}
