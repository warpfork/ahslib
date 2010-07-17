package ahs.lost.toy;

public class GenericToy {
	public static void main(String[] args) {
		A $a = new B();
		B $b = (B) x($a);
		//$b = x($a);		// not ok
		$b = x(new B());
		//$b = x(new A());	// not ok... obviously
		$b = (B) x(new A());	// ok on compile time, amazingly
		$b = (B) new A();	// ok on compile time, amazingly
		
	}
	
	public static <T> T x(T $x) {
		return $x;
	}
	
	@SuppressWarnings("unchecked")
	private static <V,X> V x(X $x, V $o) {
		return (V) $x;	// though cool, unfortunately this approach can't really get you anywhere.
		// even if you try to wrap this function with something else that uses reflection to find the most precise class, 
		//  that function in turn still can't have a sufficient specific generic return type.
	}
		
	private static class A {}
	private static class B extends A {}
}
