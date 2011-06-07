package us.exultant.ahs.util.dice;

import java.util.Random;

public class Dn<$C> {
	// n-Dimensional Die
	// accepts an argument in the form of a number 'n', 'dn', or an array containing a list of all sides.
	// call ->roll(); to get a random value from the die.
	// IEM ((.2008.04.12...23:54:52))
	
	
//	public Dn() {
//		$sides = new Integer[]{1,2,3,4,5,6};
//	}
	
	private $C[] $sides;
	private Random $r;
	
	public Dn($C[] $sides) {
		this.$sides = $sides;
		$r = new Random();
	}
	
	public $C roll() {
		return $sides[$r.nextInt($sides.length)];
	}
}
