/*
 * Copyright 2010, 2011 Eric Myhre <http://exultant.us>
 * 
 * This file is part of AHSlib.
 *
 * AHSlib is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License, or
 * (at the original copyright holder's option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package us.exultant.ahs.thread;

import us.exultant.ahs.core.*;
import java.util.concurrent.*;

/**
 * Given a set of {@link Future} entered into the {@link WriteHead} sink of this
 * {@link Flow}, the source {@link ReadHead} will return the same Futures once they are
 * completed, and in the order of completion.
 */
//this is obscenely similar to ExecutorCompletionService.
//note however that our semantics for close again come in handy here: 
//  there's a difference between a ExecutorCompletionService that returns null and a FuturePipe that's closed.
//    The ECS tends to require that you load all your Futures into the ECS before you start polling them for completion so you can tell for sure when you're actually done;
//    the FuturePipe supplies a thread-safe closing operation that deals with that problem and thus broadens the range of applications significantly.
//      in particular, think of a series of futures which can't all be loaded into a scheduler at once for some reason (maybe they can programmatically generate more of their own type, for example); a person can still use a FuturePipe to deal with this, whereas an ExecutorCompletionService may stumble quite seriously.
//perhaps most importantly, ECS is kinda ridiculous in that you can't use it to watch something for completion unless you schedule it with that very ECS.  that's EXTREMELY limiting, and implies splitting things into different thread pools depending on what kind of notifications you require out of them (which, hello, is completely contrary to the whole point of POOLS of threads), and just generally sucks.
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
