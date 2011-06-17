package us.exultant.ahs.io;

import us.exultant.ahs.core.*;
import java.io.*;

public interface Transport<$T> extends Flow<$T> {
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
}
