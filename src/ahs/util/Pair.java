package ahs.util;

public class Pair<A, B> {
	public Pair(A $a, B $b) {
		this.$a = $a;
		this.$b = $b;
	}
	
	public final A $a;
	public final B $b;
	
	public A getA() { return $a; }
	public B getB() { return $b; }
}
