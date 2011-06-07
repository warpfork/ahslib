package us.exultant.ahs.util.event;

public interface EventSource {
	// i forget why i originally wanted this interface.
	// in the case of what i imagine applets doing, 
	//	this would translate the javascript shiv data into events and just hand them to an EventDispatch, i guess.
	// for a while in the back of my head i assumed that EventDispatch would have some idea that this class existed but now i'm not sure. 
	//	it seems more likely that EventDispatch will just be ultra thread-safe 
	//		(and assume that its listeners are either reentrant or do something fancy to sync themselves) 
	//	and accept events from all over the place instead of trying to do any sort of Pipe and Pump action.
}
