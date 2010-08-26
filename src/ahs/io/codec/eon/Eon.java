package ahs.io.codec.eon;

import ahs.io.codec.*;
import ahs.io.codec.json.*;

import java.util.*;

public final class Eon {
	private Eon() {}	// thou shalt not instantiate me

	public static final String MAGICWORD_CLASS = "#";
	public static final String MAGICWORD_NAME = "$";
	public static final String MAGICWORD_DATA = "%";
	public static final String MAGICWORD_HINT = "!";
	
	public static String getKlass(Class<?> $c) {
		String[] $arrg = $c.getCanonicalName().split("\\Q.\\E");
		return $arrg[$arrg.length-1];
	}
	public static String getKlass(Object $x) {
		return getKlass($x.getClass());
	}
	
	public static <$TM extends EonObject> $TM fill($TM $holder, Map<String,String> $map) throws UnencodableException {
		for (Map.Entry<String,String> $ent : $map.entrySet())
			$holder.put($ent.getKey(),$ent.getValue());
		return $holder;
	}
}
