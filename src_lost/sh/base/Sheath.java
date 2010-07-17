package ahs.sh.base;

import java.util.*;

/**
 * <p>
 * Wraps around external programs from the underlying shell or system, providing access to
 * familiar UNIX utility staples like <code>grep</code> within the same framework of java
 * interfaces as programs implemented completely in jsh (provided one is running a system
 * with such programs installed and accessible via the <code>Runtime.exec(*)</code>
 * method).
 * </p>
 * 
 * @author hash
 * 
 */

//ok get this straight in your head.  there is -A- sheath instance per program about to be run
//	thus one puts env in there, just like input streams.
//the -Instatiator- is the part that needs to have a crazy map.
public class Sheath extends ShProgramAdapter {
	public static Sheath getDefaultConfiguration() {
		return null;
	}
	
	public void registerMe(ShRoller $roller) {
		// register the INSTANTIATOR, strangely.
	}
	
	/**
	 * Environment will be empty. If you want the "natural" environment to leak
	 * through, you must use one of the methods that allows you to set the environment
	 * and invoke it with <code>null</code>.
	 * 
	 * @param $programName
	 *                the name of external program, as well as the name to be used for
	 *                it interally.
	 */
	public void addProgram(String $programName) {
		addProgram($programName, null, null);
	}
	public void addProgram(String $programName, Map<String,String> $env, String $cwd) {
		//TODO:AHS:SH:
		// shit son, we need practically one object per program anyway.
		//FIXME:AHS:SH: environment shouldn't be set per program factory!  it should be set at runtime!  jesus.
		//FIXME:AHS:SH: actually path should be too.
	}
	
	
	
	


	public void setEnvironment(Map<String,String> $env) {
		//FIXME:AHS:SH: this actually goes in the adapter (which should just poo down to subclasses).
	}
	
	public void run() {
		//TODO:AHS:SH:
		// incidentally, i don't think there's any way to deal with both stdout and stderr without spawning a new thread for one of them.
	}
	
	public class Instatiator implements ShInstantiator {
		public ShProgram mk(String[] $cmdarray, Map<String,String> $env) {
			//TODO:AHS:SH:
			return null;
		}
	}
}
