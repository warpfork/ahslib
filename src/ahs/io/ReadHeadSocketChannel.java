package ahs.io;

import ahs.util.*;
import ahs.util.thread.*;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;

/**
 * We assume you like your channels nonblocking.
 * 
 * @author hash
 * 
 */
public class ReadHeadSocketChannel extends ReadHeadAdapter<SocketChannel> {
	public ReadHeadSocketChannel(InetSocketAddress $localBinding, PumperSelector $ps) throws IOException {
		$ssc = ServerSocketChannel.open();
		$ssc.configureBlocking(false);
		$ssc.socket().bind($localBinding);
		$ps.register($ssc, getPump());
	}
	
	private final ServerSocketChannel	$ssc;
	
	public ServerSocketChannel getServerSocketChannel() {
		return $ssc;
	}
	
	protected SocketChannel getChunk() throws IOException {
		SocketChannel $sc = $ssc.accept();
		if ($sc == null) return null;
		$sc.configureBlocking(false);
		return $sc;
	}
	
	public void close() throws IOException {
		$ssc.close();
	}
}
