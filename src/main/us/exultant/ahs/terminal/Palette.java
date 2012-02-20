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

import us.exultant.ahs.terminal.Terminal.Color;

public class Palette {
	/**
	 * @param $fg
	 */
	public Palette(Color $fg) {
		this($fg, Color.CLEAR);
	}
	
	/**
	 * @param $bg
	 * @param $fg
	 */
	public Palette(Color $fg, Color $bg) {
		this($fg, $bg, false, false);
	}
	
	/**
	 * @param $bg
	 * @param $fg
	 * @param $bold
	 * @param $underline
	 */
	public Palette(Color $fg, Color $bg, Boolean $bold, Boolean $underline) {
		this.$fg = $fg;
		this.$bg = $bg;
		this.$bold = $bold;
		this.$underline = $underline;
	}
	
	private Palette(Palette $x) {
		this.$fg = $x.$fg;
		this.$bg = $x.$bg;
		this.$bold = $x.$bold;
		this.$underline = $x.$underline;
	}
	
	// we really could cram all of these into 30 bits if we felt like it
	// and all 900 possible forms of this could fit into 35kb as is; 10kb if we bitcrammed (i'm counting 8 bytes for pointers to ever object, which becomes the heavier part.  so if we just made an int representation outright, it'd get way smaller.) 
	/** Treat as read-only.  Null indicates "no change". */
	Color	$fg;
	/** Treat as read-only.  Null indicates "no change". */
	Color	$bg;
	/** Treat as read-only.  Null indicates "no change". */
	Boolean	$bold;
	/** Treat as read-only.  Null indicates "no change". */
	Boolean	$underline;
	
	/** Forks a new Palette with all the same settings as the subject except for the requested change. */
	public Palette setForeground(Color $fg) {
		if (this.$fg == $fg) return this;
		Palette $v = new Palette(this); $v.$fg = $fg; return $v;
	}
	
	/** Forks a new Palette with all the same settings as the subject except for the requested change. */
	public Palette setBackground(Color $bg) {
		if (this.$bg == $bg) return this;
		Palette $v = new Palette(this); $v.$bg = $bg; return $v;
	}
	
	/** Forks a new Palette with all the same settings as the subject except for the requested change. */
	public Palette setBold(Boolean $bold) {
		if (this.$bold == $bold) return this;
		Palette $v = new Palette(this); $v.$bold = $bold; return $v;
	}
	
	/** Forks a new Palette with all the same settings as the subject except for the requested change. */
	public Palette setUnderline(Boolean $underline) {
		if (this.$underline == $underline) return this;
		Palette $v = new Palette(this); $v.$underline = $underline; return $v;
	}
	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.$bg == null) ? 0 : this.$bg.hashCode());
		result = prime * result + ((this.$bold == null) ? 0 : this.$bold.hashCode());
		result = prime * result + ((this.$fg == null) ? 0 : this.$fg.hashCode());
		result = prime * result + ((this.$underline == null) ? 0 : this.$underline.hashCode());
		return result;
	}
	
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Palette other = (Palette) obj;
		if (this.$bg != other.$bg) return false;
		if (this.$bold == null) {
			if (other.$bold != null) return false;
		} else if (!this.$bold.equals(other.$bold)) return false;
		if (this.$fg != other.$fg) return false;
		if (this.$underline == null) {
			if (other.$underline != null) return false;
		} else if (!this.$underline.equals(other.$underline)) return false;
		return true;
	}
}
