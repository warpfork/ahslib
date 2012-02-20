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

import us.exultant.ahs.terminal.*;
import java.util.*;

public class Container implements Component {
	private final List<Component>	$children	= new ArrayList<Component>();
	
	public Container() {}
	
	public void paint(Window $buffer) {
		paintSelf($buffer);
		paintChildren($buffer);
	}
	protected void paintSelf(Window $buffer) {}
	private final void paintChildren(Window $buffer) {
		for (int $n = $children.size() - 1; $n >= 0; $n--) {
			Component $x = $children.get($n);
			// we're going to tell them about our paintable area, and not our actual area.
			//$g3.specialize($x.size(this.getBound().getSize())).paintComponent($x);
			
			/* 
			 * scrolling: how does it work?
			 * 
			 * we're providing an illusion of more space than there really is.
			 * so we can do that with a buffer that noop's the shit that's out of our real bounds.
			 * the thing that makes them crazy though is that their contents are actually allowed to push out.
			 * and that gives me a brainhurt.
			 * also this makes a want for dirty area stuff -- if something's not under the scroll viewport, it's not work redrawing logic completing.
			 * 
			 * shapers can return bigger areas than their parents ask them to fit into.  that's not an issue.
			 * what's pear-shaped is when we want one guy to be, say, 100% the height of his parent... but the parent is a scroll interior that gets pushed out by another component.
			 * i suppose i can have redrawing start every time someone pushes something out.  that's... potentially functional.
			 * i mean, if you only have one pusher out of the whole bunch i guess you're good to go.
			 * but if you get two components which, say, have a rounding area and keep pushing it out one?  you're gonna die.
			 * 
			 */
		}
	}
}
