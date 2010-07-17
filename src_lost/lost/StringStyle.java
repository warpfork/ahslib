package ahs.lost;

import java.awt.*;

public class StringStyle {
	private Font		$f	= new Font("monospaced", Font.PLAIN, 12);
	private Color		$c	= new Color(255,255,255);

	public StringStyle()			{						}
	public StringStyle(StringStyle $x)	{ $f = $x.getFont(); $c = $x.getColor();	}

	public Color getColor()	{ return this.$c;		}
	public Font getFont()		{ return this.$f;		}
	public String getFontFace()	{ return this.$f.getName();	}
	public int getFontSize()		{ return this.$f.getSize();	}
	public boolean isBold()			{ return this.$f.isBold();	}
	public boolean isItalic()		{ return this.$f.isItalic();	}

	public void setColor(Color $x)		{ this.$c = $x;							}
	public void setFont(Font $x)		{ this.$f = $x;							}
	public void setFontFace(String $x)	{ this.$f = new Font($x, $f.getStyle(), $f.getSize());	}
	public void setFontSize(int $x)			{ this.$f = new Font($f.getName(), $f.getStyle(), $x);	}
	public void setBold(boolean $x) {
		if ($x == false && this.$f.isItalic() == false) {
			this.$f = new Font($f.getName(), Font.PLAIN, $f.getSize());
		} else if ($x == false && this.$f.isItalic() == true) {
			this.$f = new Font($f.getName(), Font.ITALIC, $f.getSize());
		} else if ($x == true && this.$f.isItalic() == false) {
			this.$f = new Font($f.getName(), Font.BOLD, $f.getSize());
		} else if ($x == true && this.$f.isItalic() == true) {
			this.$f = new Font($f.getName(), Font.BOLD + Font.ITALIC, $f.getSize());
		}
	}
	public void setItalic(boolean $x) {
		if ($x == false && this.$f.isBold() == false) {
			this.$f = new Font($f.getName(), Font.PLAIN, $f.getSize());
		} else if ($x == false && this.$f.isBold() == true) {
			this.$f = new Font($f.getName(), Font.BOLD, $f.getSize());
		} else if ($x == true && this.$f.isBold() == false) {
			this.$f = new Font($f.getName(), Font.ITALIC, $f.getSize());
		} else if ($x == true && this.$f.isBold() == true) {
			this.$f = new Font($f.getName(), Font.BOLD + Font.ITALIC, $f.getSize());
		}	
	}
}
