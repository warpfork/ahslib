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

package us.exultant.ahs.terminal.gui;

import java.awt.*;
import java.awt.geom.*;

public class ShaperPercentile implements Shaper {
	public ShaperPercentile(double $x, double $y, double $w, double $h) {
		this(new Rectangle2D.Double($x, $y, $w, $h));
	}
	
	public ShaperPercentile(Rectangle2D $r) {
		$percent = (Rectangle2D) $r.clone();
	}
	
	private Rectangle2D	$percent;
	
	public Rectangle size(Dimension $d) {
		return new Rectangle(
				(int) ($percent.getX() * $d.getWidth()),
				(int) ($percent.getY() * $d.getHeight()),
				(int) ($percent.getWidth() * $d.getWidth()),
				(int) ($percent.getHeight() * $d.getHeight())
		);
	}
}