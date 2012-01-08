package us.exultant.ahs.core.anno;

import java.lang.annotation.*;

/**
 * <p>
 * </p>
 * 
 * <p>
 * If {@link #value()} is not the empty string, it means that this function is
 * <i>conditionally</i> thread safe, and that you must hold a particular lock when calling
 * it. The string should describe where to access the appropriate lock, and be formatted
 * in the same way as arguments to javadoc's link tag (in other words, if a function on
 * this object returns the appropriate lock, the string should be "#getLock()" for
 * example). If the object thusly referred to is an instance of
 * {@link java.util.concurrent.locks.Lock}, then it should be used as such; other wise,
 * the locking should be done by synchronizing on that object's monitor.
 * </p>
 * 
 * <p>
 * Note that if {@link #value()} is the empty string, then that signifies that this method
 * is constructed in such a way that immune to deadlock! However, if {@link #value()} does
 * define a lock that must be held for thread safety, then this annotation does <i>not</i>
 * guarantee immunity from deadlock. FIXME this is stupid and wrong. you're muddying the
 * water: you need a way to declare that something must be held, and something must be
 * held AND isn't done for you such that YOU must be sure to grab it yourself. one of them
 * is a curiosity, and need not end up in published javadoc; the other is damn important
 * to document.
 * </p>
 * 
 * TODO is there any way we should think about referring to permits or semaphores? it's an
 * interesting thought, but probably not. those are just way too complex and powerful to
 * reduce to this level.
 * 
 * @author hash
 * 
 */
@Documented()
@Target({ElementType.METHOD})	// i would put ElementType.CONSTRUCTOR here, but honestly?  you should NEVER do anything in a constructor that's not Nullipotent, or you're just fucking nuts and there's no salvation for you.
public @interface ThreadSafe {
	String[] value() default "";
	
	public enum Type {
		/**
		 * <p>
		 * Declares that this entire block of code is synchronized: only one
		 * thread may ever enter it at once, and none of the memory referenced
		 * within this code block is capable of being altered by any other thread.
		 * </p>
		 * 
		 * <p>
		 * Synchronized functions are generally the most dead-simple way to get a
		 * job done safely in a multithreaded world, but often don't perform
		 * terribly well since they essentially produce areas of code where the
		 * entire program must act as if single-threaded. They also tend to face a
		 * danger of deadlocks if used wantonly.
		 * </p>
		 * 
		 * <p>
		 * <b><i>Note that a java method declared as {@code synchronized} does not
		 * automatically qualify for this flag!</i></b> If {@link Object#wait()},
		 * {@link Object#wait(long)}, or {@link Object#wait(long,int)} is called
		 * at any point within a java {@code synchronized} block, that code block
		 * does not qualify to be descibed with this level of type safety. Why?
		 * Because those wait methods allow other threads to aquire the monitor
		 * lock on that object.
		 * </p>
		 */
		SYNCHRONIZED,
	}
}
