package us.exultant.ahs.util;

import us.exultant.ahs.log.*;
import us.exultant.ahs.test.*;
import java.util.*;

public class InternTest extends TestCase {
	public static void main(String... $args) {
		new InternTest().run();
	}
	
	public InternTest() {
		super(new Logger(Logger.LEVEL_DEBUG), true);
	}
	
	public InternTest(Logger $log, boolean $enableConfirmation) {
		super($log, $enableConfirmation);
	}
	
	public List<Unit> getUnits() {
		return Arr.asList(
				new TestNormalEquals(),
				new TestString(),
				new TestStringLiteral()//,
				//new TestAlternativeEquals()
		);
	}
	
	private static class T {
		public T() {
			this.$a = new Object();
			this.$b = new Object();
		}
		public static T copyPartial(T $t) {
			T $v = new T();
			$v.$a = $t.$a;
			return $v;
		}
		public static T copyTotal(T $t) {
			T $v = new T();
			$v.$a = $t.$a;
			$v.$b = $t.$b;
			return $v;
		}
		private Object $a;
		private Object $b;
		
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			T other = (T) obj;
			if (this.$a == null && other.$a != null) return false;
			if (!this.$a.equals(other.$a)) return false;
			// ignore $b
			return true;
		}
		public int hashCode() {
			return 31 + ((this.$a == null) ? 0 : this.$a.hashCode());
		}
		/**
		 * Note: this comparator imposes orderings that are inconsistent with equals.
		 * Note: this comparator does not maintain monotonicity or other sanity to its ordering and is only valid for equality.
		 */
		public static class Comparator implements java.util.Comparator<T> {
			public int compare(T $o1, T $o2) {
				if ($o1 == $o2) return 0;
				if ($o2 == null) return -1;
				if ($o1.$a == null && $o2.$a != null) return -1;
				if (!$o1.$a.equals($o2.$a)) return -1;
				if ($o1.$b == null && $o2.$b != null) return -1;
				if (!$o1.$b.equals($o2.$b)) return -1;
				return 0;
			}
		}
	}
	
	private class TestNormalEquals extends TestCase.Unit {
		private Intern<T> $intern = new Intern<T>();
		private T $canon1 = new T();
		private T $canon2 = new T();
		private T $alt1 = T.copyPartial($canon1);
		private T $alt2 = T.copyTotal($canon2);
		public Object call() {
			assertSame($canon1, $intern.intern($canon1));
			assertSame($canon2, $intern.intern($canon2));
			assertSame($canon1, $intern.intern($canon1));	// repeated canonicalization should of course have no effect.

			assertSame($canon1, $intern.intern($alt1));
			assertSame($canon2, $intern.intern($alt2));
			
			return null;
		}
	}
	
	private class TestString extends TestCase.Unit {
		private Intern<String> $intern = new Intern.Strings();
		private String $canon1 = Integer.toBinaryString(new Random().nextInt());
		private String $canon2;
		private String $alt1 = String.copyValueOf($canon1.toCharArray());
		private String $alt2;
		public Object call() {
			$canon2 = Integer.toBinaryString(new Random().nextInt());
			$alt2 = String.copyValueOf($canon2.toCharArray());
			
			assertNotSame($canon1, $intern.intern($canon1));	// honestly, these both throw me.  i thought it shouldn't be interned in the jvm global pool yet, so it should return the same peq obj.  apparently not.  i guess the last occurance of "this" in the second paragram of String.intern's javadocs is a bit ambiguous.
			assertNotSame($canon2, $intern.intern($canon2));	// honestly, these both throw me.  i thought it shouldn't be interned in the jvm global pool yet, so it should return the same peq obj.  apparently not.  i guess the last occurance of "this" in the second paragram of String.intern's javadocs is a bit ambiguous.
			
			// using String.intern gives you the same thing as an Intern.String will.
			assertNotSame($canon1, $alt1);
			$canon1 = $canon1.intern();
			assertNotSame($canon1, $alt1);
			$alt1 = $alt1.intern();
			assertSame($canon1, $alt1);
			
			// and using Intern.String does do a String.intern for you as well.
			assertNotSame($canon2, $alt2);
			$canon2 = $canon2.intern();
			assertSame($canon2, $intern.intern($alt2));
			
			
			
			return null;
		}
	}
	
	private class TestStringLiteral extends TestCase.Unit {
		private Intern<String> $intern = new Intern.Strings();
		private String $canon1 = "canon";
		private String $alt1 = "canon";
		public Object call() {
			assertSame($canon1, $intern.intern($canon1));	// this is the same...
			assertSame($canon1, $intern.intern($alt1));	// and this is the same...
			assertSame($canon1, $alt1);			// ...and those were both trivial because they were already literals to the jvm.
			
			return null;
		}
	}
	
	private class TestAlternativeEquals extends TestCase.Unit {
		private Intern<T> $intern = new Intern<T>();
		public Object call() {
			//TODO:AHS:
			return null;
		}
	}
}
