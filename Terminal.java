package us.exultant.grid.terminal;

public interface Terminal {
	
	public Cursor cursor();
	
	public static interface Cursor {
		/** Places the cursor at an arbitrary horizontal and vertical position.  The values are 1-based and counted in user-space (i.e. increasing {@code $y} is moving down). */
		public void place(int $x, int $y);
		
		/** Moves the cursor {@code $n} cells up from its current location. If the cursor is already at the edge of the screen, calling this method has no effect. */
		public void shiftUp(int $n);
		/** Moves the cursor {@code $n} cells down from its current location. If the cursor is already at the edge of the screen, calling this method has no effect. */
		public void shiftDown(int $n);
		/** Moves the cursor {@code $n} cells left/backward from its current location. If the cursor is already at the edge of the screen, calling this method has no effect. */
		public void shiftLeft(int $n);
		/** Moves the cursor {@code $n} cells right/forward from its current location. If the cursor is already at the edge of the screen, calling this method has no effect. */
		public void shiftRight(int $n);
		
		/** Moves cursor to beginning of the line {@code $n} lines down. */
		public void lineNext(int $n);
		/** Moves cursor to beginning of the line {@code $n} lines up. */
		public void linePrev(int $n);
	}
	
	
	
	public Palette palette();
	
	public static interface Palette {
		public void bold(boolean $n);
		public void blink(boolean $n);
		public void underline(boolean $n);
		public void background(Color $c);
		public void foreground(Color $c);
	}
	
	public static enum Color {
		BLACK, RED, GREEN, YELLOW, BLUE, MAGENTA, CYAN, WHITE
	}
	
	
	
	public void clear();
}
