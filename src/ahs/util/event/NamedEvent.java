package ahs.util.event;

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
