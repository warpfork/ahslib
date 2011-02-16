package ahs.log;

import ahs.util.*;
import ahs.util.thread.*;

import java.io.*;
import java.util.*;

/**
 * Logging level is fixed to absolute none. Change providers to this in deployment
 * products to try to gain a tiny, tiny, tiny amount of perforance.
 **/
public class Logger_None extends Logger {
	public static SyncFreeProvider<Logger> makeProvider() {
		return new SyncFreeProvider<Logger>(new Factory<Logger>() {
			public Logger make() {
				return new Logger_None();
			}
		});
	}

	public void NONE() {}
	public void set(int $level) {}
	public void setLogger(Writer $logger) {}
	public void DEBUG() {}
	public void debug(String $category, String $message, Throwable $ex) {}
	public void debug(String $category, String $message) {}
	public void debug(String $message, Throwable $ex) {}
	public void debug(Class<?> $category, Throwable $ex) {}
	public void debug(String $message) {}
	public void debug(Class<?> $category, String $message, Throwable $ex) {}
	public void debug(Object $category, String $message, Throwable $ex) {}
	public void debug(Class<?> $category, String $message) {}
	public void debug(Object $category, String $message) {}
	public void ERROR() {}
	public void error(String $category, String $message, Throwable $ex) {}
	public void error(String $category, String $message) {}
	public void error(String $message, Throwable $ex) {}
	public void error(Class<?> $category, Throwable $ex) {}
	public void error(String $message) {}
	public void error(Class<?> $category, String $message, Throwable $ex) {}
	public void error(Object $category, String $message, Throwable $ex) {}
	public void error(Class<?> $category, String $message) {}
	public void error(Object $category, String $message) {}
	public void INFO() {}
	public void info(String $category, String $message, Throwable $ex) {}
	public void info(String $category, String $message) {}
	public void info(String $message, Throwable $ex) {}
	public void info(Class<?> $category, Throwable $ex) {}
	public void info(String $message) {}
	public void info(Class<?> $category, String $message, Throwable $ex) {}
	public void info(Object $category, String $message, Throwable $ex) {}
	public void info(Class<?> $category, String $message) {}
	public void info(Object $category, String $message) {}
	public void TRACE() {}
	public void trace(String $category, String $message, Throwable $ex) {}
	public void trace(String $category, String $message) {}
	public void trace(String $message, Throwable $ex) {}
	public void trace(Class<?> $category, Throwable $ex) {}
	public void trace(String $message) {}
	public void trace(Class<?> $category, String $message, Throwable $ex) {}
	public void trace(Object $category, String $message, Throwable $ex) {}
	public void trace(Class<?> $category, String $message) {}
	public void trace(Object $category, String $message) {}
	public void WARN() {}
	public void warn(String $category, String $message, Throwable $ex) {}
	public void warn(String $category, String $message) {}
	public void warn(String $message, Throwable $ex) {}
	public void warn(Class<?> $category, Throwable $ex) {}
	public void warn(String $message) {}
	public void warn(Class<?> $category, String $message, Throwable $ex) {}
	public void warn(Object $category, String $message, Throwable $ex) {}
	public void warn(Class<?> $category, String $message) {}
	public void warn(Object $category, String $message) {}
}
