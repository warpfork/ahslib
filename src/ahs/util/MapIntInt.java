package ahs.util;

import java.util.HashMap;
import java.util.Map;

public class MapIntInt extends HashMap<Integer,Integer> {
	public void add(int $i, int $n) {
		Integer $v = this.get(new Integer($i));
		if ($v == null) {
			this.put(new Integer($i),new Integer($n));
		} else {
			this.put(new Integer($i),new Integer($v.intValue()+$n));
		}
	}
	
	public void increment(int $i) {
		add($i,1);
	}

	public void decrement(int $i) {
		add($i,-1);
	}
	
	public int set(int $k, int $v) {
		Integer $old = super.put(new Integer($k),new Integer($v));
		if ($old == null) return 0;
		return $old.intValue();
	}
	
	public int get(int $i) {
		Integer $r = super.get(new Integer($i));
		if ($r == null) return 0;
		return $r.intValue();
	}
	
	public void addIn(MapIntInt $other) {
		for (Integer $i : $other.keySet()) {
			this.add($i.intValue(),$other.get($i).intValue());
		}
	}
	
	public void reset() {
		this.clear();
	}
	
	public String toString() {
		String $v = "[";
		for (Map.Entry<Integer,Integer> $entry : this.entrySet())
			$v += $entry.getKey()+":"+$entry.getValue()+"; ";
		return $v + "]";
	}
	
	/**
	 * Returns a string of comma-separated values, starting from the key 0 (inclusive) to $m (exclusive).
	 */
	public String toString(int $m) {
		String $v = "[";
		for (int $i = 0; $i < $m-1; $i++)
			$v += get($i)+",";
		$v += get($m-1);
		return $v + "]";
	}
}
