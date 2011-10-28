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

package us.exultant.ahs.scratch.applet.command;

import us.exultant.ahs.scratch.applet.*;
import us.exultant.ahs.scratch.applet.DomContact.Exposure;

import java.util.concurrent.*;

public interface JsCommand<$V> extends Future<$V> {
	
	public void run(DomContact.Exposure $power);
	
	// get methods are all purely as per the Future interface.
	
	
	
	
	/**
	 * This adapter lets one forgo the annoyance of caching results and thread control
	 * and such as the JsCommand interface tends to require and just provide a single
	 * method with a return.
	 */
	public static abstract class Adapter<$V> implements JsCommand<$V> {
		protected Adapter() {
			$cunt = new FutureFuck();
		}
		
		/**
		 * This is the only method that extenders of the adapter should need to
		 * provide.
		 */
		protected abstract $V execute(DomContact.Exposure $power) throws Exception;
		
		
		
		/**
		 * This is what DomContact calls when told to execute this JsCommand.
		 */
		public void run(Exposure $power) {
			if (!start($power)) return;	// we just ignore you if you said something stupid
			$cunt.run();
		}
		private synchronized boolean start(Exposure $power) { 
			if (isStarted()) // wtf are you trying to repeatedly start for you bastard
				return false;
			$cunt.configureExposure($power);
			return true;
		}
		
		
		
		private FutureFuck		$cunt;
		private static final Runnable	GODDAMNIT = new Runnable() { public void run() { /* NOTHING. */ } };
		
		private class FutureFuck extends FutureTask<$V> {
			public FutureFuck() {
				super(GODDAMNIT, null);	// just giving a null callable actually causes a null pointer exception right in the constructor even though i'm explicitly overridding all the methods that could ever touch it.  sigh.
			}
			
			private DomContact.Exposure $power;
			
			public void configureExposure(DomContact.Exposure $power) {
				this.$power = $power;
			}
			
			public void run() {
				if ($power == null) throw new IllegalStateException("Exposure must be configured by now.");	// this is pretty much a MajorBug since this method shouldn't ever be visible except to a class that knows exactly what it's doing.
				try {
					set(execute($power));
				} catch (Throwable $e) {
					setException($e);
				}
			}
		}
		
		
		// these are the methods required by Future interface:
		
		public synchronized boolean cancel(boolean $mayInterruptIfRunning) {
			return this.$cunt.cancel($mayInterruptIfRunning);
		}
		
		public synchronized boolean isCancelled() {
			return this.$cunt.isCancelled();
		}
		
		public synchronized $V get() throws InterruptedException, ExecutionException {
			return this.$cunt.get();
		}
		
		public synchronized $V get(long $timeout, TimeUnit $unit) throws InterruptedException, ExecutionException, TimeoutException {
			return this.$cunt.get($timeout, $unit);
		}
		
		public synchronized boolean isDone() {
			return this.$cunt.isDone();
		}
		
		// end Future interface provision.
		
		public synchronized boolean isStarted() {	// do not know why a method like this doesn't exist in Future.  very odd exclusion in my mind.
			return (this.$cunt.$power != null);
		}
	}
}
