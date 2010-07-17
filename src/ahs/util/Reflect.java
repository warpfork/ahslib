package ahs.util;

import java.lang.reflect.*;

public class Reflect {
	public static String getShortClassName(Class<?> $class) {
		return Strings.getPartAfterLast($class.getCanonicalName(),".");
	}
	
	public static void vom(Class<?> $k) {
		X.saye("CLASSN:: "+$k.getCanonicalName());
		for (Constructor<?> $c : $k.getConstructors())
			X.saye("CONSTR:: "+$c.toGenericString());
		for (Type $i : $k.getGenericInterfaces())
			X.saye("INTERF:: "+$i.toString());
	}
}
