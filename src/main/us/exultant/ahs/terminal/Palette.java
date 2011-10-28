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
import static us.exultant.ahs.terminal.TermCodes.*;

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
	
	private Color	$fg;
	private Color	$bg;
	private Boolean	$bold;
	private Boolean	$underline;
	
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
	
	
	
	public String code() {	// i only want this to be visible to child packages :(
		return
		(($fg == null) ? "" : CSI+"3"+$fg.code()+"m") +
		(($bg == null) ? "" : CSI+"4"+$bg.code()+"m") +
		(($bold == null) ? "" : ($bold) ? CSI+"1m" : CSI+"22m") +
		(($underline == null) ? "" : ($underline) ? REND_UNDERLINE_ON : REND_UNDERLINE_OFF);
	}
	
	//XXX:AHS:TERM: public String code(Palette $delta) {}
	
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
	
	//XXX:AHS:TERMINAL: would be nice to have something that can diff from an assumed palette to keep the number of characters we have to pump out to a minimum.  (on the other hand that can't be cached statically quite as handily.  well, maybe.)
}
