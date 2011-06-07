package us.exultant.ahs.util;

import java.util.*;

/**
 * Main point of this class: same as the standard ArrayList, but WITHOUT the ability to
 * change capacity.
 * 
 * None of the boolean returning functions really bother to do what they're supposed to.
 * Basically, if you give them good indexes, they're going to mutate, and if you give them
 * bad ones, you're going to crash. So deal with it.
 * 
 * @author hash
 * 
 * @param <$C>
 */
public class Array<$C> implements List<$C> {
	public Array(int $size) {
		construct($size);
	}
	
	public Array($C[] $c) {
		construct($c.length);
		for (int $i = 0; $i < $c.length; $i++)
			$arr[$i] = $c[$i];
		$p = $c.length;
	}
	
	public Array(Collection<? extends $C> $c) {
		construct($c.size());
		addAll($c);
	}
	
	public Array(Collection<? extends $C> $c, int $size) {
		construct($size);
		addAll($c);
	}

	@SuppressWarnings("unchecked")
	private void construct(int $size) {
		$arr = ($C[]) new Object[$size];
		$p = 0;
	}
	
	private $C[]	$arr;
	private int	$p;	// index of top; used for operations like "add".
	
	
	
	

	public boolean add($C $x) {
		$arr[$p] = $x;
		$p++;
		return true;
	}

	public void add(int $i, $C $x) {
		throw new ahs.util.ImBored();
	}

	public boolean addAll(Collection<? extends $C> $c) {
		for ($C $x : $c)
			add($x);
		return true;
	}

	public boolean addAll(int $arg0, Collection<? extends $C> $arg1) {
		throw new ahs.util.ImBored();
	}

	public void clear() {
		for (int $i = 0; $i < $arr.length; $i++)
			$arr[$i] = null;
		$p = 0;
	}

	public boolean contains(Object $x) {
		for (int $i = 0; $i < $arr.length; $i++)
			if ($arr[$i].equals($x)) return true;
		return false;
	}

	public boolean containsAll(Collection<?> $c) {
		for (Object $x : $c)
			if (!contains($x)) return false;
		return true;
	}

	public $C get(int $i) {
		return $arr[$i];
	}

	public int indexOf(Object $x) {
		for (int $i = 0; $i < $arr.length; $i++)
			if ($arr[$i].equals($x)) return $i;
		return -1;
	}

	public boolean isEmpty() {
		return ($p == 0);
	}

	public Iterator<$C> iterator() {
		throw new ahs.util.ImBored();
	}

	public int lastIndexOf(Object $arg0) {
		throw new ahs.util.ImBored();
	}

	public ListIterator<$C> listIterator() {
		throw new ahs.util.ImBored();
	}

	public ListIterator<$C> listIterator(int $arg0) {
		throw new ahs.util.ImBored();
	}
	
	public void move(int $oldStart, int $newStart, int $length) {
		System.arraycopy($arr,$oldStart,$arr,$newStart,$length);
	}

	public boolean remove(Object $x) {
		for (int $i = 0; $i < $arr.length; $i++)
			if ($arr[$i].equals($x)) {
				$arr[$i] = null;
				move($i+1,$i,$arr.length-$i);
				return true;
			}
		return false;
	}

	public $C remove(int $i) {
		$C $old = $arr[$i];
		$arr[$i] = null;
		move($i+1,$i,$arr.length-$i);
		return $old;
	}

	public boolean removeAll(Collection<?> $c) {
		boolean $v = false;
		for (Object $x : $c)
			if (remove($x)) $v = true;
		return $v;
	}

	public boolean retainAll(Collection<?> $arg0) {
		throw new ahs.util.ImBored();
	}

	public $C set(int $i, $C $x) {
		$C $old = $arr[$i];
		$arr[$i] = $x;
		return $old;
	}

	public int size() {
		return $p;
	}
	
	public int capacity() {
		return $arr.length;
	}

	public List<$C> subList(int $arg0, int $arg1) {
		throw new ahs.util.ImBored();
	}
	
	public $C[] getArray() {
		return $arr;
	}
	
	public Object[] toArray() {
		throw new ahs.util.ImBored();
	}

	public <T> T[] toArray(T[] $arg0) {
		throw new ahs.util.ImBored();
	}
	
	
	
	/** {@inheritDoc} */
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(this.$arr);
		result = prime * result + this.$p;
		return result;
	}

	/** {@inheritDoc} */
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Array<?> other = (Array<?>) obj;
		if (!Arrays.equals(this.$arr, other.$arr)) return false;
		if (this.$p != other.$p) return false;
		return true;
	}
}
