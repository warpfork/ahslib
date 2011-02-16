package ahs.lost.toy;

import ahs.test.TestCase;

public class StaticFieldInheritanceProof extends TestCase {
	
	
	
	public void test() {
		Super $a = new Super();
		Sub $b = new Sub();
		Super $c = $b;
		assertEquals(0,$a.getVal());
		assertEquals(0,$b.getVal());
		assertEquals(0,$c.getVal());
		
		assertEquals(0,$a.getVar());
		assertEquals(0,$b.getVar());
		assertEquals(0,$c.getVar());
	}
	
	
	
	
	public static class Super {
		public static final int VAL = 0;
		public static int Var = 0;
		
		public int getVal() {
			return VAL;
		}
		
		public int getVar() {
			return Var;
		}
	}
	
	public static class Sub extends Super {
		public static final int VAL = 77;
		public static int Var = 77;
	}
	
}
