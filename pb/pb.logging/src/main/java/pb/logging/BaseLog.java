/**
 * Copyright (C) 2013 Oliver Schünemann
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License as published by the Free Software Foundation; either version 2 of 
 * the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details. 
 * 
 * You should have received a copy of the GNU General Public License along with this program; 
 * if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, 
 * Boston, MA 02110, USA 
 * 
 * @since 23.02.2013
 * @version 1.0
 * @author oliver
 */
package pb.logging;

import java.util.Dictionary;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Modified;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;

/**
 * Base class for logging. This can be used in each bundle that wants to log
 * something. The Logging Bundle must be included into every using bundle as
 * embedded dependency, that each bundle has its own instance of the static
 * field theLogService.
 * 
 * @author oliver
 */
@Properties(value = { @Property(name = "logLevel", intValue = LogService.LOG_INFO),
		@Property(name = "consoleLogging", boolValue = true) })
@Reference(name = "LogService", referenceInterface = LogService.class, bind = "bindLogService", unbind = "unbindLogService", cardinality = ReferenceCardinality.MANDATORY_UNARY, policy = ReferencePolicy.DYNAMIC)
public class BaseLog {

	protected static LogService theLogService = null;
	protected static Lock lock = new ReentrantLock();
	protected static int logLevel = LogService.LOG_INFO;
	protected static boolean consoleLogging = true;
	protected static String name = "";
	protected static String fill = "                   ";

	/**
	 * SCR activate and modified method. Reads the current loglevel of a
	 * component from the configuration.
	 * 
	 * @param context
	 */
	@Activate
	@Modified
	protected void activate(final ComponentContext context) {
		final Dictionary<?, ?> props = context.getProperties();
		try {
			final Object rawValue = props.get("logLevel");
			if (rawValue != null) {
				logLevel = (Integer) rawValue;
			}
		} catch (final ClassCastException cce) {
			logLevel = LogService.LOG_INFO;
		}
		try {
			final Object rawValue = props.get("consoleLogging");
			if (rawValue != null) {
				consoleLogging = (Boolean) rawValue;
			}
		} catch (final ClassCastException cce) {
			consoleLogging = true;
		}
		try {
			final Object rawValue = props.get("component.name");
			if (rawValue != null) {
				name = (String) rawValue;
				final int lastIndex = name.lastIndexOf('.');
				if (lastIndex > 0) {
					name = name.substring(0, lastIndex);
				}
			}
		} catch (final ClassCastException cce) {
			name = "";
		}
		info("Received LogLevel : " + logLevel);
	}

	/**
	 * Bind Method for the OSGi LogService
	 * 
	 * @param logService
	 */
	protected void bindLogService(final LogService logService) {
		lock.lock();
		try {
			theLogService = logService;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Unbind method for the OSGi LogService
	 * 
	 * @param logService
	 */
	protected void unbindLogService(final LogService logService) {
		lock.lock();
		try {
			theLogService = null;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Log Method doing the Logging on the LogService as well as the logging on
	 * the console
	 * 
	 * @param level
	 * @param formattedMessage
	 * @param exp
	 */
	@SuppressWarnings({ "PMD.SystemPrintln", "PMD.AvoidPrintStackTrace" })
	protected static void log(final int level, final String message, final Throwable exp,
			final Object... args) {
		lock.lock();
		String formattedMessage;
		if (args == null) {
			formattedMessage = message;
		} else {
			formattedMessage = String.format(message, args);
		}
		try {
			if (logLevel >= level) {
				if (theLogService != null) {
					theLogService.log(level, formattedMessage, exp);
				}
				if (consoleLogging) {
					final StringBuilder builder = new StringBuilder();
					if (name.length() < fill.length()) {
						builder.append("[" + name + fill.substring(name.length()) + "] ");
					} else {
						builder.append("[" + name.substring(0, name.length()) + "] ");
					}

					switch (level) {
					case LogService.LOG_DEBUG:
						builder.append("[DEBUG  ] ");
						break;
					case LogService.LOG_INFO:
						builder.append("[INFO   ] ");
						break;
					case LogService.LOG_WARNING:
						builder.append("[WARNING] ");
						break;
					case LogService.LOG_ERROR:
						builder.append("[ERROR  ] ");
						break;
					default:
						builder.append("[       ] ");
						break;
					}
					builder.append(formattedMessage);
					System.out.println(builder.toString());
					if (exp != null) {
						exp.printStackTrace();
					}
				}
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Wrapper Method doing debug logging
	 * 
	 * @param message
	 */
	public static void debug(final String message, final Object... args) {
		if (message != null) {
			log(LogService.LOG_DEBUG, String.format(message, args), null);
		}
	}

	/**
	 * Wrapper Method doing debug logging
	 * 
	 * @param message
	 * @param exp
	 */
	public static void debug(final String message, final Throwable exp, final Object... args) {
		if (message != null) {
			log(LogService.LOG_DEBUG, String.format(message, args), exp);
		}
	}

	/**
	 * Wrapper Method doing info logging
	 * 
	 * @param message
	 */
	public static void info(final String message, final Object... args) {
		if (message != null) {
			log(LogService.LOG_INFO, String.format(message, args), null);
		}

	}

	/**
	 * Wrapper Method doing info logging
	 * 
	 * @param message
	 * @param exp
	 */
	public static void info(final String message, final Throwable exp, final Object... args) {
		if (message != null) {
			log(LogService.LOG_INFO, String.format(message, args), exp);
		}
	}

	/**
	 * Wrapper Method doing warning logging
	 * 
	 * @param message
	 */
	public static void warning(final String message, final Object... args) {
		if (message != null) {
			log(LogService.LOG_WARNING, String.format(message, args), null);
		}
	}

	/**
	 * Wrapper Method doing warning logging
	 * 
	 * @param message
	 * @param exp
	 */
	public static void warning(final String message, final Throwable exp, final Object... args) {
		if (message != null) {
			log(LogService.LOG_WARNING, String.format(message, args), exp);
		}
	}

	/**
	 * Wrapper Method doing error logging
	 * 
	 * @param message
	 */
	public static void error(final String message, final Object... args) {
		if (message != null) {
			log(LogService.LOG_ERROR, String.format(message, args), null);
		}
	}

	/**
	 * Wrapper Method doing error logging
	 * 
	 * @param message
	 * @param exp
	 */
	public static void error(final String message, final Throwable exp, final Object... args) {
		if (message != null) {
			log(LogService.LOG_ERROR, String.format(message, args), exp);
		}
	}

}
