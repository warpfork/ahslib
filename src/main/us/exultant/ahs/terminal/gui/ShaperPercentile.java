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