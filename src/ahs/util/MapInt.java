package ahs.util;

import java.util.*;
import java.util.concurrent.atomic.*;

public class MapInt {
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
}
