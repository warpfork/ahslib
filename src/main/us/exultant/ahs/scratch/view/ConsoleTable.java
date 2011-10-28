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

package us.exultant.ahs.scratch.view;

import java.io.*;

// buffering / nonbuffering
// tab interpretation; comma'd; varargs
// explict vs implied size (last row always stretch)

public class ConsoleTable {
	public ConsoleTable(int $cols) {
		this.$cols = $cols;
		$sizes = new int[$cols];
		$prefixes = new String[$cols];
		for (int $i = 0; $i < $cols; $i++)
			$prefixes[$i] = "";
	}
	
	private int		$cols;
	private int[]		$sizes;
	private String[]	$prefixes;
	
	public String toString(String... $vals) {
		StringBuilder $sb = new StringBuilder(); 
		for (int $i = 0; $i < $vals.length && $i < $cols; $i++) {
			$sb.append($prefixes[$i]);
			$sb.append($vals[$i]);
			for (int $j = $sizes[$i] - $vals[$i].length(); $j > 0; $j--)
				$sb.append(' ');
		}
		return $sb.toString();
	}
	
	public void println(String... $vals) {
		println(System.out,$vals);
	}
	
	public void println(PrintStream $out, String... $vals) {
		$out.println(toString($vals));
	}
	
	public ConsoleTable setSizes(int... $sizes) {
		for (int $i = 0; $i < $sizes.length && $i < $cols; $i++)
			this.$sizes[$i] = $sizes[$i];
		return this;
	}
	
	public ConsoleTable setAllSizes(int $size) {
		for (int $i = 0; $i < $cols; $i++)
			$sizes[$i] = $size;
		return this;
	}
	
	public ConsoleTable setPrefixes(String... $prefixes) {
		for (int $i = 0; $i < $prefixes.length && $i < $cols; $i++)
			this.$prefixes[$i] = $prefixes[$i];
		return this;
	}
}
