package ahs.io.codec.eon;

import ahs.io.codec.*;
import ahs.io.codec.json.*;

import java.util.*;

public final class Eon {
	private Eon() {}	// thou shalt not instantiate me
	
	public static final String MAGICWORD_NAME = "$";
	public static final String MAGICWORD_CLASS = "#";
	public static final String MAGICWORD_DATA = "%";
	
	public static String getKlass(Class<?> $c) {
		String[] $arrg = $c.getCanonicalName().split("\\Q.\\E");
		return $arrg[$arrg.length-1];
	}
	public static String getKlass(Object $x) {
		return getKlass($x.getClass());
	}

	public static <$TA extends EonArray<$TM,$TA>, $TM extends EonObject<$TM,$TA>> $TM fill($TM $holder, Object $class, String $name, $TM $data) {
		if ($class != null) $holder.putKlass($class);
		if ($name != null)  $holder.putName($name);
		if ($data != null)  $holder.putData($data);
		return $holder;
	}
	public static  <$TA extends EonArray<$TM,$TA>, $TM extends EonObject<$TM,$TA>> $TM fill($TM $holder, String $class, String $name, $TM $data) {
		if ($class != null) $holder.putKlass($class);
		if ($name != null)  $holder.putName($name);
		if ($data != null)  $holder.putData($data);
		return $holder;
	}
	public static <$TA extends EonArray<$TM,$TA>, $TM extends EonObject<$TM,$TA>> $TM fill($TM $holder, Object $class, String $name, $TA $data) {
		if ($class != null) $holder.putKlass($class);
		if ($name != null)  $holder.putName($name);
		if ($data != null)  $holder.putData($data);
		return $holder;
	}
	public static <$TA extends EonArray<$TM,$TA>, $TM extends EonObject<$TM,$TA>> $TM fill($TM $holder, String $class, String $name, $TA $data) {
		if ($class != null) $holder.putKlass($class);
		if ($name != null)  $holder.putName($name);
		if ($data != null)  $holder.putData($data);
		return $holder;
	}
	public static <$TA extends EonArray<$TM,$TA>, $TM extends EonObject<$TM,$TA>> $TM fill($TM $holder, Object $class, String $name, String $data) {
		if ($class != null) $holder.putKlass($class);
		if ($name != null)  $holder.putName($name);
		if ($data != null)  $holder.putData($data);
		return $holder;
	}
	public static <$TA extends EonArray<$TM,$TA>, $TM extends EonObject<$TM,$TA>> $TM fill($TM $holder, String $class, String $name, String $data) {
		if ($class != null) $holder.putKlass($class);
		if ($name != null)  $holder.putName($name);
		if ($data != null)  $holder.putData($data);
		return $holder;
	}
	public static <$TA extends EonArray<$TM,$TA>, $TM extends EonObject<$TM,$TA>> $TM fill($TM $holder, Object $class, String $name, byte[] $data) {
		if ($class != null) $holder.putKlass($class);
		if ($name != null)  $holder.putName($name);
		if ($data != null)  $holder.putData($data);
		return $holder;
	}
	public static <$TA extends EonArray<$TM,$TA>, $TM extends EonObject<$TM,$TA>> $TM fill($TM $holder, String $class, String $name, byte[] $data) {
		if ($class != null) $holder.putKlass($class);
		if ($name != null)  $holder.putName($name);
		if ($data != null)  $holder.putData($data);
		return $holder;
	}
	public static <$TA extends EonArray<$TM,$TA>, $TM extends EonObject<$TM,$TA>> $TM fill($TM $holder, Map<String,String> $map) throws UnencodableException {
		for (Map.Entry<String,String> $ent : $map.entrySet())
			$holder.put($ent.getKey(),$ent.getValue());
		return $holder;
	}
}
