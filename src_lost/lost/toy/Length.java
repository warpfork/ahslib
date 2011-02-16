package ahs.lost.toy;

import java.lang.ref.WeakReference;

/**
 * This is a (relatively silly) example of a design pattern that uses weak references to
 * cache data in such a way that unnecessary recomputations can be sometimes avoided, but
 * the memory can also be reclaimed during garbage collection as necessary.
 * 
 * Mind you, the way it's been done here, the translated/cached object will ALWAYS be
 * cleared by the GC, as there are no strong references to it at all. If we returned the
 * actual Length objects instead of just doubles, then the GC would be a little more
 * relenting.
 * 
 * SoftReference might be a better choice in situations such as this, as that shouldn't
 * be cleared by the GC unless an OutOfMemoryException is near on the horizon.
 * 
 * @author hash
 * 
 */
public interface Length {
	public double inMeters();
	public double inFeet();

	public static class Meters {
		public Meters(double $m) {
			$x = $m;
		}
		public Meters(Feet $f) {
			$x = $f.inFeet() / 3.1;
		}
		
		private double $x;
		private WeakReference<Feet> $r;
		
		public double inMeters() {
			return $x;
		}
		public double inFeet() {
			if ($r.get() == null) $r = new WeakReference<Feet>(new Feet(this));
			return $r.get().inFeet();
		}
	}
	
	public static class Feet {
		public Feet(double $m) {
			$x = $m;
		}
		public Feet(Meters $f) {
			$x = $f.inMeters() * 3.1;
		}
		
		private double $x;
		private WeakReference<Meters> $r;
		
		public double inMeters() {
			if ($r.get() == null) $r = new WeakReference<Meters>(new Meters(this));
			return $r.get().inMeters();
		}
		public double inFeet() {
			return $x;
		}
	}
}
