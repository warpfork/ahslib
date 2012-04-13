package us.exultant.ahs.io;

import us.exultant.ahs.core.*;
import us.exultant.ahs.thread.*;

public class InputSystem<$T> {
	// three kinds of base chunking:
	//   - file (er, whole?)
	//   - frames
	//   - http
	//   - lines
	//   - of course this has to be extensible, but those are the obvious minimal needed ones
	//  does this mean we'll have an interface called BaseChunker?  yus
	
	// conceal the WorkTarget completely.
	// the WorkFuture can be exposed (cancellation is a valid desire) but should pretty much never be used.
	//    er, actually rethink this.  you could cancel or become done if the readhead is closed.
	
	// we'll take the Scheduler as an argument, but 99% of the time we can use a default global scheduler.
	// same with the Selector.
	
	// about the base input (file vs network) uh... yeah good luck with that.  i'll try to do it all with channels i guess.
	//   a more exotic thing you could keep in mind is keyboard or mouse events.
	//      actually that's kind of a crappy idea.  those would be ridiculous things to throw this heavyweight of a solution at.  make them look like a readhead?  sure, great.  but what on earth would you need to involve a worktarget and a scheduler for when all you really need is for the source listener to stack something in a datapipe?
	
	
	
	public ReadHead<$T> getReadHead() {
		return null;
	}
	
	public WorkFuture<Void> getWorkFuture() {
		return null;
	}
}
