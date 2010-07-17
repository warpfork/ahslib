package ahs.io;

import ahs.util.*;

import java.io.*;
import java.lang.annotation.*;
import java.net.*;

/**
 * <p>
 * An interface for all transports for any single generic type, regardless of whether the
 * underlying transport be a blocking socket, nonblocking nio socket, or some sort of
 * internal pipe. Collaborates with the Listener interface.
 * </p>
 * 
 * <p>
 * This interface should be implemented by any network communication system, pipes, and
 * even file I/O systems (for ByteBuffer or String). It's also not uncommon for layers of
 * protocols to be built on top of each other, which each layer implementing this
 * interface for a different generic type.
 * </p>
 * 
 * @author hash
 * 
 * @param <$M> the message type
 */
public interface Transport<$M> {
	/**
	 * Connects the underlying transport. Takes no action if already invoked on this
	 * object before.
	 * 
	 * @throws IOException
	 */
	public void connect() throws IOException;
	
	/**
	 * @return true if transport has completed connection and may now be established;
	 *         false otherwise.
	 */
	public boolean isConnected();
	
	/**
	 * Establishes protocol, connecting the underlying transport first if necessary.
	 * Takes no action if already invoked on this object before.
	 * 
	 * @throws IOException
	 */
	public void establish() throws IOException;
	
	/**
	 * @return true if transport has completed establishment and may now send
	 *         messages; false otherwise.
	 */
	public boolean isEstablished();
	
	/**
	 * Disconnect.
	 * 
	 * @throws IOException
	 *                 typical example when using java.net.Socket is when other
	 *                 threads are blocked on the underlying transport.
	 */
	public void disconnect() throws IOException;
	
	/**
	 * Sends a message across this transport, immediately and within this thread.
	 * 
	 * @param $m
	 * @throws IOException
	 */
	public void send($M $m) throws IOException;
	
	/**
	 * If the connection is already established and in use, be very, very wary of
	 * trying to invoke this from a thread other than the one pushing the underlying
	 * transport. You will almost certainly have threadfuck.
	 * 
	 * @param $ml
	 */
	public void setListener(Listener<$M> $ml);	//SOMEDAY:AHS: make a more beautiful adapter class that will readily buffer things and pushed messages in a synchronized way even when the listener is changing.
	
	/**
	 * Adapter pattern. Takes no action on connect, establish, or disconnect, and
	 * claims to be always connected. Most of this behavior should honestly be
	 * overridden anyway, unless you have specialized applications like an
	 * intermediate layer which doesn't have objects existing unless they are
	 * established.
	 * 
	 * @author hash
	 * 
	 * @param <$H>
	 */
	public static class Adapter<$H> implements Transport<$H> {
		/** {@inheritDoc} */
		public void connect() throws IOException {}

		/** {@inheritDoc} */
		public boolean isConnected() {
			return true;
		}
		
		/** {@inheritDoc} */
		public void establish() throws IOException {}

		/** {@inheritDoc} */
		public boolean isEstablished() {
			return true;
		}

		/** {@inheritDoc} */
		public void disconnect() throws IOException {}

		/** {@inheritDoc} */
		public void send($H $m) throws IOException {}

		/** {@inheritDoc} */
		public void setListener(Listener<$H> $ml) {}
	}
}
