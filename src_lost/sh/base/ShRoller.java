package ahs.sh.base;

import java.util.*;

/**
 * Accumulates factories for ShPrograms (i.e., ShInstantiators) and maps them by names.
 * Calls to run a program of a given name looks up the appropriate ShInstantiator and
 * hands the call off to it, eventually resulting in the return of a Sh object ready for
 * launch. If no factory was available for a program name (or the factory fails), null is
 * returned.
 * 
 * @author hash
 * 
 */
public final class ShRoller {
	// this class might end up getting away with being package-protected.
	// the hectic part is that all of the mk methods can be static at this point; they just need to pull ShInstantiator objects out of a map and hand calls off.
	// users want to see the mk interfaces from this, but developers just want to see the Instantiator interface.  a pickle.
	
	public ShRoller() {
		$x = new HashMap<String,ShInstantiator>();
	}
	
	/**
	 * Puts the (name,factory) pair in the map kept by this roller, allowing future
	 * invocations of a program with the given name to return new processes from this
	 * roller ready for execution. Existing entries with the same name will be
	 * siliently replaced.
	 * 
	 * @param $programName
	 * @param $programFactory
	 * @return this roller, for invocation chaining.
	 */
	public ShRoller addFactory(String $programName, ShInstantiator $programFactory) {
		synchronized ($x) {
			$x.put($programName, $programFactory);
		}
		return this;
	}
	
	private Map<String,ShInstantiator>	$x;
	private static final Map<String,String>	EMPTY_MAP	= Collections.emptyMap();	// for env when nothing else is specified
	

	public Sh mk(String $cmd) {
		return mk(new String[] { $cmd }, EMPTY_MAP, (ShStream[])null);
	}
	
	public Sh mk(ShStream $cmdarray) {
		return mk($cmdarray.readAll(), EMPTY_MAP, (ShStream[])null);
	}
	
	public Sh mk(String[] $cmdarray, Map<String,String> $env, ShStream... $ins) {
		if ($cmdarray.length < 1) return null;
		ShInstantiator $fab = $x.get($cmdarray[0]);
		if ($fab == null) return null;
		ShProgram $core = $fab.mk($cmdarray, $env);
		if ($core == null) return null;
		return new Sh($core, $ins);
	}
	
}
