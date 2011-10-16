package us.exultant.ahs.terminal;

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
		try {
			return new Scanner(Runtime.getRuntime().exec(new String[] { "tput cols" }).getInputStream()).nextInt();
		} catch (IOException $e) {
			return -1;
		}
	}

	public int getHeight() {
		try {
			return new Scanner(Runtime.getRuntime().exec(new String[] { "tput lines" }).getInputStream()).nextInt();
		} catch (IOException $e) {
			return -1;
		}
	}
}
