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

public interface Terminal {
	public void print(String $s);
	
	public Palette getPalette();
	
	public Palette setPalette(Palette $p);
	
	public Cursor cursor();
	
	/** Clears the drawable area and resets the cursor to the home position at the top left of the screen. */
	public void clear();
	
	/** Returns the width of the drawable area as best as can be determined.  This is a best-effort method -- the return may be -1 if we have no clue, and may not update instantly after the terminal size is externally modified. */
	public int getWidth();
	
	/** Returns the height of the drawable area as best as can be determined.  This is a best-effort method -- the return may be -1 if we have no clue, and may not update instantly after the terminal size is externally modified. */
	public int getHeight();
	
	
	
	public static interface Cursor {	// i'd like to be able to have component-like boxes that can confine the cursor... but we're talking about a system that regularly screws up the distinction between input and output channels, and so i'm deeply afraid we really might not be able to do that.
		/** Places the cursor at an arbitrary horizontal and vertical position.  The values are 0-based and counted in user-space (i.e. increasing {@code $y} is moving down). */
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
	
	
	
	public static enum Color {
		CLEAR	(9),
		BLACK	(0),
		RED	(1),
		GREEN	(2),
		YELLOW	(3),
		BLUE	(4),
		PURPLE	(5),
		CYAN	(6),
		WHITE	(7);
		
		Color(int $c) { this.$c = $c; }
		private final int $c;
		public int code() { return $c; }
	}
	
	
	
	public static enum Event {
		INPUT,
		RESIZE,
		CURSOR,
	}
}
