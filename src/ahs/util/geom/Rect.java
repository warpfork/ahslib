package ahs.util.geom;

/**
 * Extends java.awt.Rectangle to make it sane by adding methods that actually have the
 * same primitive type for getting and setting.
 * 
 * @author hash
 * 
 */
public class Rect extends java.awt.Rectangle {
	public Rect() {
		this(0,0,0,0);	
	}
	public Rect(int $x, int $y, int $w, int $h) {
		super($x,$y,$w,$h);
	}
	public Rect(Rect $r) {
		this($r.x(),$r.y(),$r.w(),$r.h());
	}
	public Rect(java.awt.Rectangle $r) {
		this((int)$r.getX(),(int)$r.getY(),(int)$r.getWidth(),(int)$r.getHeight());
	}
	public Rect(java.awt.Point $p) {
		this((int)$p.getX(),(int)$p.getY(),0,0);
	}
	public Rect(java.awt.Dimension $d) {
		this(0,0,(int)$d.getWidth(),(int)$d.getHeight());
	}
	public Rect(java.awt.Point $p, java.awt.Dimension $d) {
		this((int)$p.getX(),(int)$p.getY(),(int)$d.getWidth(),(int)$d.getHeight());
	}
	
	public java.awt.Rectangle toAwt() {
		return new java.awt.Rectangle(x(),y(),w(),h());
	}
	
	public Rect clone() {
		return new Rect(this);
	}
	public Rect deepClone() {
		return new Rect(this);
	}
	public boolean equals(Rect $r) {
		if ($r == null) return false;
		return (this.x()==$r.x() && this.y()==$r.y() && this.w()==$r.w() && this.h()==$r.h());
	}
	public int setX(int $n) {
		return this.x = $n;
	}
	public int setY(int $n) {
		return this.y = $n;
	}
	public int setWidth(int $n) {
		return this.width = $n;
	}
	public int setHeight(int $n) {
		return this.height = $n;
	}
	public int x() {
		return (int)this.getX();
	}
	public int x(int $n) {
		return this.setX($n);
	}
	public int y() {
		return (int)this.getY();
	}
	public int y(int $n) {
		return this.setY($n);
	}
	public int w() {
		return (int)this.getWidth();
	}
	public int w(int $n) {
		return this.setWidth($n);
	}
	public int h() {
		return (int)this.getHeight();
	}
	public int h(int $n) {
		return this.setHeight($n);
	}
	
	private static final long serialVersionUID = 1L;
}
