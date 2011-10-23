package us.exultant.ahs.terminal.gui;

import us.exultant.ahs.terminal.*;
import java.awt.*;

/**
 * <p>
 * Buffers a 2-D character array with graphical attributes (i.e. a {@link Palette}).
 * Typically, one of these with a single backing array is produced for every draw to be
 * rendered on the terminal, then various other Window objects as "views" to the first are
 * spawned for each component to be drawn, always with a clip assigned to it so that
 * components cannot draw outside of their allocated area.
 * </p>
 * 
 * <p>
 * A window clipped to 2,1,6,4 will be able to draw on the starred area:
 * 
 * <pre>
 *  012345678
 * 0.........
 * 1..******.
 * 2..******.
 * 3..******.
 * 4..******.
 * 5.........
 * 6.........
 * 7.........
 * </pre>
 * 
 * Placing a character in relative position 3,1 after the clip will place the character in
 * 5,2 globally.
 * </p>
 * 
 * @author hash
 * 
 */
public class Window {
	public Window(int $w, int $h) {
		$chars = new char[$w][$h];
		$pales = new Palette[$w][$h];
		$clip = new Rectangle(0,0,$w,$h);
	}
	
	public Window clip(Rectangle $r) {
		return new Window(this, $r);
	}
	private Window(Window $parent, Rectangle $newClip) {
		$chars = $parent.$chars;
		$pales = $parent.$pales;
		$clip = new Rectangle(
				$parent.$clip.x + $newClip.x,
				$parent.$clip.y + $newClip.y,
				Math.min($parent.$clip.width-$newClip.x, $newClip.width),
				Math.min($parent.$clip.height-$newClip.y, $newClip.height)
		);	// i could also throw illegal argument exceptions if those min clauses actually mattered, i suppose.
	}
	
	private final char[][]		$chars;
	/**
	 * I'll be the first to admit that this seems like a rather obscenely large amount
	 * of memory to throw at this (it's substantially larger than what we're likely to
	 * actually issue as escape sequences to the console when we dump out the actual
	 * render). But I can't think of any significantly better idea at the moment, and
	 * we're still talking kilobytes here (not megs), even for large screens.
	 */
	private final Palette[][]	$pales;
	
	private final Rectangle		$clip;

	protected int absX(int $x) {
		if ($x > $clip.width) return -1;
		return ($x + $clip.x);
	}
	
	protected int absY(int $y) {
		if ($y > $clip.height) return -1;
		return ($y + $clip.y);
	}
	
	public void print(int $x, int $y, String $str, Palette $pal) {
		final int $l = Math.min($clip.width-$x, $str.length());
		for (int $i = $x; $i < $l; $i++) {
			$chars[$i+$clip.x][$y+$clip.y] = $str.charAt($i);
			$pales[$i+$clip.x][$y+$clip.y] = $pal;	//TODO: merge this somehow.  and hopefully without spamming too many new palettes all the time?
		}
	}
	
	public void printVertical(int $x, int $y, String $str, Palette $pal) {
		//TODO
	}
}
