package us.exultant.ahs.io;

import us.exultant.ahs.thread.*;
import java.io.*;
import java.nio.channels.*;

class WorkTargetChannelCloser extends WorkTargetAdapterFollowup<Void> {
	WorkTargetChannelCloser(InputSystem<?> $iosys) {
		super($iosys.getFuture(), 0);
		this.$channel = $iosys.getChannel();
	}
	
	WorkTargetChannelCloser(OutputSystem<?> $iosys) {
		super($iosys.getFuture(), 0);
		this.$channel = $iosys.getChannel();
	}
	
	private final Channel $channel;
	
	protected Void run() throws IOException {
		$channel.close();
		return null;
	}
}
