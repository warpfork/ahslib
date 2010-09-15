package ahs.util;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.*;

public class MapInt {
	public static <$K> Map<$K,AtomicInteger> decorate(Map<$K,AtomicInteger> $m) {
		return new AtomicDecorator<$K>($m, 0);
	}
	
	
	
	/**
	 * Adds the value of every key in the second map to the keys in the first map. If
	 * a key is not present in the first map, it is added with a default operation
	 * before the sum operation is performed.
	 * 
	 * @return the first map (modified).
	 */
	public static <$TK,$K extends $TK> Map<$K,AtomicInteger> addAll(Map<$K,AtomicInteger> $m, Map<$TK,AtomicInteger> $t) {
		for (Map.Entry<$TK,AtomicInteger> $ent : $t.entrySet()) {
			AtomicInteger $i = $m.get($ent);
			if ($i == null) $i = new AtomicInteger();
			$i.addAndGet($ent.getValue().get());
		}
		return $m;
	}
	
	private static class AtomicDecorator<$K> implements Map<$K,AtomicInteger> {
		public AtomicDecorator(Map<$K,AtomicInteger> $core, final int $default) {
			this.$core = $core;
			this.$default = $default;
		}
		
		private final int	$default;
		Map<$K,AtomicInteger>	$core;
		
		
		public void add($K $key, int $i) {
			safeGet($key).addAndGet($i);
		}
		public void add($K $key, AtomicInteger $i) {
			safeGet($key).addAndGet($i.get());
		}
		
		public void addAll(Map<? extends $K,AtomicInteger> $t) {
			for (Map.Entry<? extends $K,AtomicInteger> $ent : $t.entrySet())
				safeGet($ent.getKey()).addAndGet($ent.getValue().get());
		}
		
		private AtomicInteger safeGet($K $key) {
			AtomicInteger $i = get($key);
			if ($i == null) {
				$i = makeDefault();
				put($key,$i);
			}
			return $i;
		}
		private AtomicInteger makeDefault() {
			return new AtomicInteger($default);
		}
		
		
		
		// DELEGATES:
		
		public void clear() {
			$core.clear();
		}
		
		public boolean containsKey(Object $key) {
			return $core.containsKey($key);
		}

		public boolean containsValue(Object $value) {
			return $core.containsValue($value);
		}

		public Set<Entry<$K,AtomicInteger>> entrySet() {
			return $core.entrySet();
		}

		public AtomicInteger get(Object $key) {
			return $core.get($key);
		}

		public boolean isEmpty() {
			return $core.isEmpty();
		}

		public Set<$K> keySet() {
			return $core.keySet();
		}

		public AtomicInteger put($K $key, AtomicInteger $value) {
			return $core.put($key, $value);
		}

		public void putAll(Map<? extends $K,? extends AtomicInteger> $m) {
			$core.putAll($m);
		}

		public AtomicInteger remove(Object $key) {
			return $core.remove($key);
		}

		public int size() {
			return $core.size();
		}

		public Collection<AtomicInteger> values() {
			return $core.values();
		}

		public boolean equals(Object $arg0) {
			return $core.equals($arg0);
		}

		public int hashCode() {
			return $core.hashCode();
		}

		public String toString() {
			return $core.toString();
		}
	}
}
