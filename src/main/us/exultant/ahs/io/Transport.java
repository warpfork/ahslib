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
