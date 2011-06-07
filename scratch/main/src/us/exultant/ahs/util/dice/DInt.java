package us.exultant.ahs.util.dice;

import java.util.Random;

public class DInt {
	public DInt(int $max) {
		if ($max < 0) $max = 0;
		$sides = new int[$max];
		for (int $i = 0; $i < $max; $i++)
			$sides[$i] = $i+1;
		$r = new Random();
	}
	
	public DInt(int $min, int $max) {
		if ($max < 0) $max = 0;
		if ($min < 0) $min = 0;
		if ($max <= $min) $max = $min + 1;
		int $range = $max-$min;
		$sides = new int[$range];
		for (int $i = 0; $i < $range; $i++)
			$sides[$i] = $min+$i;
		$r = new Random();
	}

	public DInt(int[] $sides) {
		this.$sides = $sides;
		$r = new Random();
	}
	
	private int[] $sides;
	private Random $r;
	
	public int roll() {
		return $sides[$r.nextInt($sides.length)];
	}
}
