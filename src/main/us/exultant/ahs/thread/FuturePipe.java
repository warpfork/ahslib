package us.exultant.ahs.thread;

import us.exultant.ahs.core.*;
import java.util.concurrent.*;

// the idea of this is that given a set of futures, we might want to wait for the first one of the batch to be done.  so this would just do ordering funnywise compared to a regular pipe, and only return completed futures.

// this is obscenely similar to ExecutorCompletionService.  we might even just wrap that, actually.
//   note however that our semantics for close again come in handy here: there's a difference between a ExecutorCompletionService that returns null and a FuturePipe that's closed.  The ECS tends to require that you load all your Futures into the ECS before you start polling them for completion so you can tell for sure when you're actually done; the FuturePipe supplies a thread-safe closing operation that deals with that problem and thus broadens the range of applications significantly -- in particular, think of a series of futures which can't all be loaded into a scheduler at once for some reason (maybe they can programmatically generate more of their own type, for example); a person can still use a FuturePipe to deal with this, whereas an ExecutorCompletionService may stumble quite seriously.

public class FuturePipe<$T> implements Flow<Future<$T>> {

	public ReadHead<Future<$T>> source() {
		//TODO
		return null;
	}

	public WriteHead<Future<$T>> sink() {
		//TODO
		return null;
	}

}
