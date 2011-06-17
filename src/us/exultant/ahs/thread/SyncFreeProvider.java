package us.exultant.ahs.thread;

import us.exultant.ahs.core.*;
import us.exultant.ahs.util.*;

import java.lang.reflect.*;
import java.util.*;

/**
 * <p>
 * SyncFreeProvider is intended to ease multithreaded programming and help reduce common
 * bottlenecks by providing a unique object for each thread that requests one; this means
 * that each thread requesting an object from a SyncFreeProvider will pay the construction
 * cost for that object once in the lifetime of the thread.
 * </p>
 * 
 * <p>
 * One caveat: there is not easy or completely reliable mechanism for removing objects
 * cached for threads which are no longer active (other than clearing the entire map for
 * all threads), so if this is to be applied where many threads are used, each thread
 * should probably clear itself at the end of its execution lest memory be eaten... but
 * then, you should always be using a ThreadPool of some sort for efficiency anyway, right?
 * Right.
 * </p>
 * 
 * @author hash
 * 
 * @param <$T>
 *                The type of object which will be provided.
 */
public class SyncFreeProvider<$T> {
	/**
	 * Constructs a SyncFreeProvider that constructs new versions of the reference
	 * object for each thread that requests one via the reference object's clone
	 * method.
	 * 
	 * @param $ref
	 *                Must implement a reasonable clone method.
	 */
	public SyncFreeProvider($T $ref) {
		$dat = new HashMap<Thread, $T>();
		$fact = new Fact<$T>($ref);
	}
	
	/**
	 * Constructs a SyncFreeProvider that constructs new objects for threads via the
	 * make method provided by the Factory instance.
	 * 
	 * @param $factory
	 *                The general contract for the SyncFreeProvider class requires
	 *                that this return objects that are not pointer-equals; typically
	 *                this need only be a tiny snippet wrapping a constructor or
	 *                another form of factory pattern.
	 */
	public SyncFreeProvider(Factory<$T> $factory) {
		$dat = new HashMap<Thread, $T>();
		$fact = $factory;
	}
	
	private Map<Thread, $T> $dat;
	private Factory<$T> $fact;
	
	private $T safeGet(Thread $t) {
		synchronized ($dat) {
			$T $r = $dat.get($t);
			if ($r == null) {
				$r = $fact.make();
				$dat.put($t, $r);
			}
			return $r;
		}
	}
	
	/**
	 * <p>
	 * Returns an object which is specific to this thread. Subsequent invocations of
	 * this method from the same thread are guaranteed to return the same object;
	 * invocations of this method from a different thread will normally return objects
	 * that may be equal (via the method) but should point to unique objects.
	 * </p>
	 * 
	 * <p>
	 * As long as this thread never gives that object to any other thread after
	 * retrieving it from this method (and the factory given to the SyncFreeProvider
	 * at construction time follows the general contract), that returned object should
	 * never require synchronization when accessed or modified.
	 * </p>
	 * 
	 * @return an object of type $T specific to this thread.
	 */
	public $T get() {
		return safeGet(Thread.currentThread());
	}
	
	/**
	 * Clear the object associated with the current thread (such that the next
	 * invocation to the get method from this thread would return a new object).
	 */
	public void clear() {
		synchronized ($dat) {
			$dat.remove(Thread.currentThread());
		}
	}
	
	/**
	 * Clear the object associated with the given thread (such that the next
	 * invocation to the get method from thiat thread would return a new object).
	 */
	public void clear(Thread $thread) {
		synchronized ($dat) {
			$dat.remove($thread);
		}
	}
	
	/**
	 * Clears all stored objects for all threads. Any future invocation of the get
	 * method will return a new object.
	 */
	public void clearAll() {
		synchronized ($dat) {
			$dat.clear();
		}
	}
	
	/**
	 * Not yet implemented.
	 */
	public void clearExpired() {
		throw new ImBored();
		//ThreadGroup $tg2 = Thread.currentThread().getThreadGroup(), $tg = $tg2;
		//while ($tg2 != null) {
		//	$tg = $tg2;
		//	$tg2 = $tg.getParent();
		//}
		//Thread[] $r = new Thread[256];
		//$tg.enumerate($r, true);
		//TODO:AHS: finish
	}
	
	
	private static class Fact<$T> implements Factory<$T> {
		public Fact($T $ref) { this.$ref = $ref; }
		private $T $ref;
		
		@SuppressWarnings("unchecked")	// LIES.  IT'S FINE.
		public $T make() {
			try {
				Method $meth = $ref.getClass().getDeclaredMethod("clone");
				$meth.setAccessible(true);
				return ($T)$meth.invoke($ref);
			} catch (IllegalAccessException $e) {
				X.cry($e);
			} catch (IllegalArgumentException $e) {
				X.cry($e);	// also, not possible.
			} catch (InvocationTargetException $e) {
				X.cry($e);
			} catch (NoSuchMethodException $e) {
				X.cry($e);	// also, not possible.
			} catch (SecurityException $e) {
				X.cry($e);
			}
			return null;	// not actually possible.
		}
	}
}
