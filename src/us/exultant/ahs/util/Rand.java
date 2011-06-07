package us.exultant.ahs.util;

import java.util.Random;

/**
 * Decorator pattern. Adds common functions for common requests like getting a random int
 * from within a given range.
 * 
 * @author hash
 * 
 */
public class Rand extends Random {
	/**
	 * Decorates the given instance of Random.
	 * @param $r
	 */
	public Rand(Random $r) {
		this.$r = $r;
	}
	
	Random $r;
	
	public int nextInt(int $bottom, int $top) {
		return nextInt($top - $bottom) + $bottom;	
	}
	
	////////////////////////////////////////////////////////////////
	//// DELGATES
	
	/**
	 * @param $seed
	 * @see java.util.Random#Random(long)
	 */
	public Rand(long $seed) {
		$r = new Random($seed);
	}
	
	/**
	 * @see java.util.Random#Random()
	 */
	public Rand() {
		$r = new Random();
	}
	
	public boolean equals(Object $arg0) {
		return this.$r.equals($arg0);
	}

	public int hashCode() {
		return this.$r.hashCode();
	}

	public boolean nextBoolean() {
		return this.$r.nextBoolean();
	}

	public void nextBytes(byte[] $bytes) {
		this.$r.nextBytes($bytes);
	}

	public double nextDouble() {
		return this.$r.nextDouble();
	}

	public float nextFloat() {
		return this.$r.nextFloat();
	}

	public synchronized double nextGaussian() {
		return this.$r.nextGaussian();
	}

	public int nextInt() {
		return this.$r.nextInt();
	}

	public int nextInt(int $n) {
		return this.$r.nextInt($n);
	}

	public long nextLong() {
		return this.$r.nextLong();
	}

	public synchronized void setSeed(long $seed) {
		this.$r.setSeed($seed);
	}

	public String toString() {
		return this.$r.toString();
	}
}
