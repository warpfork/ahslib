package ahs.applet;

import ahs.applet.command.*;

public interface DomContact {
	public void execute(JsCommand<?> $jsc);	// gives the JSC access to its private member, which in turn lets the JSC eval js strings.
	
	// USAGE:
	//   JsCommand<Bees> $jsc = new FuckWitThisPropCmd(...);
	//   $domContact.execute($jsc);		// inside, this is giving the $jsc an Exposure refernce and run()ing it
	//   Bees $answer = $jsc.get();
	
	
	
	/**
	 * Every DomContact implementor should also have a nested class that implements
	 * Exposure. It is recommended that a singleton instance of Exposure be
	 * maintained, and be private to the DomContact. The DomContact can then give a
	 * pointer to the Exposure to JsCommands during their execution; this prevents
	 * leakage of references to the Exposure instance except when a JsCommand chains
	 * invocations to other JsCommands (which is fine) or when a JsCommand keeps a
	 * pointer to the Exposure and returns it later (which is... inadvisable and ought
	 * not be done).
	 */// i might be doing a noob here.  Exposure might be the only thing that needs to be an interface; after that DomContact can pretty much be a final class.  we'll see.
	public interface Exposure {
		public Object eval(String... $strs);	// i still don't really know what's going on with this Object return type, to be honest, but it's what JSO does so i'm going with it until it bites me.
	}
}
