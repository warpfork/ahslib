package us.exultant.ahs.thread;

import us.exultant.ahs.core.*;
import java.util.concurrent.*;

// the idea of this is that given a set of futures, we might want to wait for the first one of the batch to be done.  so this would just do ordering funnywise compared to a regular pipe, and only return completed futures.

// this is obscenely similar to ExecutorCompletionService.  we might even just wrap that, actually.

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
