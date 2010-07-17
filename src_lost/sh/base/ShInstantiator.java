package ahs.sh.base;

import java.util.*;

/**
 * <p>
 * This interface specificies the prototype for the factory method that must be
 * implemented by classes that will produce new instances of jsh programs in preparation
 * for their execution.
 * </p>
 * 
 * <p>
 * Typically, there will be one of these for every "program", and it will register itself
 * with a string naming the "program" to a more-or-less global factory. The first String
 * in every $cmdarray should presumably be the same as the program name. XXX:AHS:SH: intern.
 * </p>
 * 
 * <p>
 * The "typical" pattern should account for 99% of implementors. However, some special
 * cases exist. An example is the Instantiator used to interface with programs via the
 * actual underlying system shell; that Instantiator is capable of accepting more than one
 * String as the program name in the zeroth index of the $cmdarray, and is capable of
 * returning different ShInterface objects with significantly different behavior because
 * of it.
 * </p>
 * 
 * @author hash
 * 
 */
public interface ShInstantiator {
	public ShProgram mk(String[] $cmdarray, Map<String,String> $env);	//FIXME:AHS:SH: env on runtime, not here.
}
