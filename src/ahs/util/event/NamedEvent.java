package ahs.util.event;

import ahs.util.*;

// there is a SERIOUS limitation on the utility of this that comes from basic problems of generics in the Java language.
// consider the following code that WORKS:

//public static void fuck(String $wef) {
//	EventDispatch<NamedEvent<String>> $ed = new EventDispatch<NamedEvent<String>>();
//	Listener<NamedEvent<String>> $t = new Listener<NamedEvent<String>>() {
//		public void hear(NamedEvent<String> $x) {
//		}
//	};
//	NamedEvent<String> $u = new NamedEvent<String>($wef);
//	$t.hear($u);
//	$ed.put($u, $t);
//}

// and compare to this code that DOES NOT:

//public static void fuck(String $wef) {
//	EventDispatch<NamedEvent> $ed = new EventDispatch<NamedEvent>();
//	Listener<NamedEvent<String>> $t = new Listener<NamedEvent<String>>() {
//		public void hear(NamedEvent<String> $x) {
//		}
//	};
//	NamedEvent<String> $u = new NamedEvent<String>($wef);
//	$t.hear($u);
//	$ed.put($u, $t);
//}

// if you watch to be able to disbatch multiple event types, you can run into difficulties with nested generics.



public class NamedEvent<$T> implements Event {
	public NamedEvent(String $name) {
		this.$name = $name.intern();
		// no data.
	}
	
	public NamedEvent(String $name, $T $data) {
		this.$name = $name.intern();
		this.$data = $data;
	}
	
	private String $name;
	private $T $data;
	
	public $T getData() {
		return $data;
	}
	
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		NamedEvent<?> $other = (NamedEvent<?>) obj;
		if (this.$name == null) {
			if ($other.$name != null) return false;
		} else if (this.$name != $other.$name) return false;	// can use pointer-equals here because name is always interned.
		return true;
	}
	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.$name == null) ? 0 : this.$name.hashCode());
		return result;
	}
}
