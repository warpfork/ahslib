/**
 * Rapidly develop reusable and thread-safe components that can be easily assembled and
 * reassembled into high-performance systems using the interfaces in this package.
 * 
 * <h2>Flows and Pipes</h2>
 * 
 * <p>
 * {@link us.exultant.ahs.thread.DataPipe} is the standard implementation of an in-program
 * pipe. It produces a paired {@link us.exultant.ahs.core.ReadHead} and
 * {@link us.exultant.ahs.core.WriteHead} that can shuttle data between as many reading
 * and writing threads as you please.
 * </p>
 * 
 * <p>
 * The nearest equivalent in the java standard library to AHSlib pipes is
 * {@link java.util.concurrent.BlockingQueue}; {@link us.exultant.ahs.thread.DataPipe}
 * offers the following major advantage: DataPipe supports a concept of closing! Closing
 * an AHSlib pipe can be performed from either side and is atomic, and supported
 * regardless of how many reading and writing threads there are. (The javadocs for
 * BlockingQueue suggest implementing this using "poison" objects, but this approach fails
 * spectacularly if you need to support a flexible number of reader threads, often
 * requires awkwardly bending the type system, and requires the reading and writing sides
 * of the system to have uncomfortably large amounts of information about each other.)
 * </p>
 * 
 * 
 * <h2>Work Systems</h2>
 * 
 * <p>
 * There are three core interfaces that work together to give the user the ability to
 * define any kind of work, how different pieces of work are related and hand off data to
 * each other in work flows, and how all of that should get scheduled.
 * </p>
 * 
 * <p>
 * In short: a {@link us.exultant.ahs.thread.WorkTarget} defines some task, a
 * {@link us.exultant.ahs.thread.WorkFuture} lets you track task progress and plan
 * reactions, and a {@link us.exultant.ahs.thread.WorkScheduler} is in charge of getting
 * stuff done. (These are not dissimilar to {@link java.util.concurrent.Callable},
 * {@link java.util.concurrent.Future}, and {@link java.util.concurrent.Executor}; the
 * AHSlib implementations generally have more powerful features.)
 * </p>
 * 
 * <p>
 * Design choices made throughout this package bear a great deal of resemblance to a
 * "actor" patterns, so if you're familiar with that concept, a lot of this will look
 * pretty conventional.
 * </p>
 * 
 * 
 * <h3>Work Target</h3>
 * 
 * <p>
 * A {@link us.exultant.ahs.thread.WorkTarget} is an interface that a developer implements
 * in order to define some task. It's similar to the
 * {@link java.util.concurrent.Callable} interface in <tt>java.util.concurrent</tt>, but
 * with a number of notable extensions that make it much more valuable &mdash;
 * specifically, WorkTarget contains functions that let you define when that task can make
 * some progress, and when that task is done. These functions make it possible to build a
 * scheduling system that only allocates processing resources to where they can currently
 * be used.
 * </p>
 * 
 * 
 * <h3>Work Future</h3>
 * 
 * <p>
 * A {@link us.exultant.ahs.thread.WorkFuture} allows running an asynchronous task and
 * option either waiting (blockingly) for it to complete, or registering a listener to be
 * notified when the task becomes complete (following a more nonblocking/event-based
 * design pattern). WorkFuture extends {@link java.util.concurrent.Future} and thus uses
 * the same API for waiting blockingly for task completion; the nonblocking/event-based
 * side of the equation is where AHSlib needed to go beyond the offerings of the existing
 * java concurrent classes.
 * </p>
 * 
 * <p>
 * If your application needs to wait for the completion of several tasks, you can use
 * either a {@link us.exultant.ahs.thread.FuturePipe} (which will return WorkFutures to
 * you in the order they are finished) or an
 * {@link us.exultant.ahs.thread.WorkFutureAggregate} (which is itself a WorkFuture, so
 * you can either block on it until completed, or register completion listeners with it).
 * </p>
 * 
 * <p>
 * WorkFuture most commonly show up when scheduling a WorkTarget with a WorkScheduler
 * produces one, but a WorkFuture can also represent other kinds of delayed system.
 * </p>
 * 
 * <p>
 * {@link us.exultant.ahs.thread.WorkFuture} provides stronger guarantees than
 * {@link java.util.concurrent.Future} about the whether or not a thread has actually
 * cleared the area the WorkFuture describes. Standard {@link java.util.concurrent.Future}
 * implementations will return from their {@link java.util.concurrent.Future#get()} method
 * immediately if they are cancelled. Suppose that you had a scheduled a Callable whose
 * job it is to push things into a database, and your program waits for that task to be
 * done and then closes the db connection... also suppose you happen to use a database
 * connector that will happily deadlock if you attempt to close a connection that's in the
 * middle of a query (some very popular ones do this!). If you set this up with the usual
 * java components and your database worker is cancelled, your process is in for a world
 * of hurt! AHSlib behaves more politely. Completion listeners won't fire and the
 * {@link us.exultant.ahs.thread.WorkFuture#get()} method won't return until there's no
 * thread anywhere inside the work. This saves you from deadlock possibilities and
 * unpleasant surprises when trying to cancel work that's taking too long actually causes
 * it to take forever instead!
 * </p>
 * 
 * 
 * <h3>Work Scheduler</h3>
 * 
 * <p>
 * A {@link us.exultant.ahs.thread.WorkScheduler} provides threads to all of the
 * {@link us.exultant.ahs.thread.WorkTarget}s that it manages in the best order it knows
 * how to, corraling tasks with clock-based schedules and tasks of various priorities, all
 * of which may or may not be ready to perform some work at any given time.
 * </p>
 * 
 * <p>
 * One of the major reasons that AHSlib's threading module came into being is a big piece
 * of missing functionality in {@link java.util.concurrent.Executor} implementations like
 * {@link java.util.concurrent.ScheduledThreadPoolExecutor}: you cannot scheduler a timed
 * task and get completion notifications; you have to choose one or the other!
 * {@link java.util.concurrent.FutureTask} has this {@code done()} method you can override
 * for the purpose of getting completion notifications... but it turns out that this is a
 * useless misdirection in practice, because when you hand such a FutureTask to an
 * ExecutorService... the submit methods implemented in AbstractExecutorService will
 * happily ignore you and wrap whatever it is (in this case, your FutureTask would look
 * like a Runnable) in a new FutureTask. So if you think you can override that done()
 * method to give you an event-based kind of completion notification... unless I've missed
 * something in my reading, you're more or less out of luck with the standard library
 * here. With AHSlib's work systems, you can have your cake, and eat it too: all
 * WorkScheduler implementations support scheduling timed tasks, and WorkFuture can always
 * be asked to call an event listener when a task is finished.
 * </p>
 * 
 */
package us.exultant.ahs.thread;
