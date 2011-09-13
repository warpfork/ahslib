package us.exultant.grid.terminal;

import java.io.*;

public class StandardTerminal implements Terminal {
	public StandardTerminal() {
		$console = System.console();
		$cursor = new StdCursor();
		$palette = new StdPalette();
	}
	
	private final Console	$console;
	private final Cursor	$cursor;
	private final Palette	$palette;
	
	public Cursor cursor() {
		return $cursor;
	}
	
	public Palette palette() {
		return $palette;
	}
	
	public void clear() {
		$console.printf(
				TermCodes.CLEAR_SCREEN+	// duh
				TermCodes.CSI+"f"	// also reset cursor to 1,1 (some terminals do this with just the clear screen code, so we shoot for consistency here) (we don't use the normal function for cursor positioning because we can skip some characters by taking advantage of the "1;1" default args).
		);
	}
	
	

	public class StdCursor implements Cursor {
		public void place(int $x, int $y) {
			$console.printf(TermCodes.CSI+$y+";"+$x+"f");
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
			$console.printf(TermCodes.CSI+$n+"E");
		}
		
		public void linePrev(int $n) {
			$console.printf(TermCodes.CSI+$n+"F");
		}
	}
	
	
	
	public class StdPalette implements Palette {
		public void bold(boolean $n) {
			//TODO
			
		}
		
		public void blink(boolean $n) {
			//TODO
			
		}
		
		public void underline(boolean $n) {
			//TODO
			
		}
		
		public void background(Color $c) {
			//TODO
			
		}
		
		public void foreground(Color $c) {
			//TODO
			
		}
	}



	public int getWidth() {
		return 80; //TODO
	}

	public int getHeight() {
		return 25; //TODO
	}
}
