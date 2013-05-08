package us.exultant.ahs.thread;

/**
 * <p>
 * You can't actually rely on {@link Thread#interrupt()} to make a thread joinable in
 * reasonable time, or indeed ever. This is simple proof of that; you will never see the
 * stack trace from an InterruptedException printed, and the program will never exit of
 * its own accord; it will sit there happily eating up a whole core of your machine until
 * you forcibly terminate the process.
 * </p>
 *
 * <p>
 * If there was any sort of blocking IO call or call to {@link Object#wait()} or its
 * family going on within the Runnable, those would cause an InterruptedException to be
 * thrown, and the whole system might be capable of being stopped. However, if that call
 * was itself wrapped in a try/catch block that simply eats the InterruptedException and
 * continues merrily on its way (and let's be honest, that's an incredibly common behavior
 * for any programmer who's not deeply familiar with concurrent design patterns), you are
 * still stuck in an unpleasant situation where your interruption attempts are
 * ineffectual.
 * </p>
 *
 * @author Eric Myhre <tt>hash@exultant.us</tt>
 *
 */
public class UninterruptablityDemo {
	public static void main(String... $args) {
		Thread $t = new Thread(new Runnable() { public void run() {
			while (true);
		}});
		$t.start();
		$t.interrupt();
		try {
			$t.join();
		} catch (InterruptedException $e) {
			$e.printStackTrace();
		}
	}
}
