package com.redcontagion;

import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;

import com.zekrom_64.ze.base.ZEApplication;
import com.zekrom_64.ze.base.err.ZEngineException;
import com.zekrom_64.ze.base.err.ZEngineInternalException;
import com.zekrom_64.ze.glfw.GLFWWindow;

public class RedContagion extends ZEApplication {
	
	/** The main logger used by Red Contagion */
	public static final Logger rcLogger = Logger.getLogger("Red Contagion");
	/** The formatter used by Red Contagion for logging */
	public static final Formatter logFormatter = new Formatter() {

		@Override
		public String format(LogRecord record) {
			Calendar c = Calendar.getInstance();
			String prefix = 
					"[" + c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.MONTH) + "/" + c.get(Calendar.YEAR)
				+ " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE) + ":" + c.get(Calendar.SECOND)
				+ " (" + Thread.currentThread().getName() + ":" + record.getLoggerName() + ":" + record.getLevel()
				+ ")]: ";
			String[] strings = record.getMessage().split("\\\n");
			StringBuffer sb = new StringBuffer();
			for(String s : strings) sb.append(prefix).append(s).append('\n');
			return sb.toString();
		}
		
	};
	/** The handler used by Red Contagion for logging */
	public static final Handler rcLogHandler;
	private static final StreamHandler consoleHandler = new StreamHandler(System.out, logFormatter) {
		
		@Override
		public void publish(LogRecord record) {
			Level level = record.getLevel();
			if (level==Level.SEVERE||level==Level.WARNING) this.setOutputStream(System.err);
			else this.setOutputStream(System.out);
			super.publish(record);
		}
		
	};
	private static final Handler fileHandler;
	/** The filtering level used by Red Contagion for logging */
	public static final Level rcLogLevel;
	
	/** The game window, fullscreen or otherwise. */
	public static final GLFWWindow window = new GLFWWindow();
	/** The game settings file, stores some important information */
	public static final Properties settings = new Properties();
	
	/** The app instance */
	public static RedContagion app = null;
	/** The render system */
	public static RedContagionRender render = null;
	
	static {
		Handler __fileHandler = null;
		// If logging to file is enabled, try to create the log file
		if (!tryGetBoolProperty("rc.nolog", false)) {
			try {
				new File("logs").mkdir();
				Calendar c = Calendar.getInstance();
				String filename = c.get(Calendar.DAY_OF_MONTH) + "." + c.get(Calendar.MONTH) + "."
						+ c.get(Calendar.YEAR) + "-%u.txt";
				__fileHandler = new FileHandler("logs/" + filename);
			} catch (IOException e) {
				System.err.println("Failed to open log file!");
				e.printStackTrace();
			}
		}
		// If the file handler was not created, make a dummy handler
		if (__fileHandler==null) __fileHandler = new Handler() {

			@Override
			public void publish(LogRecord record) { }

			@Override
			public void flush() { }

			@Override
			public void close() throws SecurityException { }
			
		};
		fileHandler = __fileHandler;
		// The public handler combines the console and file handlers
		rcLogHandler = new Handler() {

			@Override
			public void publish(LogRecord record) {
				consoleHandler.publish(record);
				fileHandler.publish(record);
				flush();
			}

			@Override
			public void flush() {
				consoleHandler.flush();
				fileHandler.flush();
			}

			@Override
			public void close() throws SecurityException {
				consoleHandler.close();
				fileHandler.close();
			}
			
		};
		
		Level level = Level.INFO;
		try {
			// If an overriding logging level is defined, use it
			String levelstr = tryGetProperty("rc.loglevel", "info");
			level = Level.parse(levelstr.toUpperCase());
		} catch (IllegalArgumentException e) {}
		rcLogLevel = level;
		
		// Setup the logging system
		fileHandler.setLevel(Level.ALL);
		fileHandler.setFormatter(logFormatter);
		consoleHandler.setLevel(level);
		consoleHandler.setFormatter(logFormatter);		
		rcLogHandler.setLevel(level);
		rcLogger.setLevel(Level.ALL);
		rcLogger.addHandler(rcLogHandler);
		rcLogger.setUseParentHandlers(false); // Don't use the default console handler!
		
		// Load the settings
		try {
			settings.load(new FileInputStream("settings.properties"));
		} catch (Exception e) {
			rcLogger.log(Level.SEVERE, "Failed to read settings");
			e.printStackTrace();
		}
		
		// Add the main shutdown hook
		Runtime.getRuntime().addShutdownHook(new Thread("Shutdown") {
			
			@Override
			public void run() {
				rcLogHandler.close();
			}
			
		});
	}
	
	/** Attempts to get a property from the settings file, then the system properties.
	 * If no property is found, a default value is returned.
	 * 
	 * @param name Name of the property
	 * @param def Default value
	 * @return Retrieved property
	 */
	public static String tryGetProperty(String name, String def) {
		String prop = settings.getProperty(name);
		if (prop==null) {
			prop = System.getProperty(name);
			if (prop!=null) return prop;
			settings.setProperty(name, def);
			return def;
		}
		return prop;
	}
	
	/** Attempts to get a boolean property from the settings file, then the system properties.
	 * If no property is found, a default value is returned.
	 * 
	 * @param name Name of the property
	 * @param def Default value
	 * @return Retrieved property
	 */
	public static boolean tryGetBoolProperty(String name, boolean def) {
		String prop = settings.getProperty(name);
		if (prop==null)	{
			prop = System.getProperty(name);
			if (prop==null) {
				settings.setProperty(name, Boolean.toString(def));
				return def;
			}
		}
		return Boolean.parseBoolean(prop);
	}
	
	/** Attempts to get an integer property from the settings file, then the system properties.
	 * If no property is found, or the property is an invalid int value, a default value is returned.
	 * 
	 * @param name Name of the property
	 * @param def Default value
	 * @return Retrieved property
	 */
	public static int tryGetIntProperty(String name, int def) {
		String prop = settings.getProperty(name);
		if (prop==null) {
			prop = System.getProperty(name);
			if (prop==null) {
				settings.setProperty(name, Integer.toString(def));
				return def;
			}
		}
		try {
			return Integer.parseInt(prop);
		} catch (NumberFormatException e) {
			settings.setProperty(name, Integer.toString(def));
			return def;
		}
	}
	
	private static void init() {
		// Initialize GLFW
		if (!GLFW.glfwInit()) {
			rcLogger.log(Level.SEVERE, "Failed to initialize GLFW");
			return;
		}

		// Get the available monitors
		PointerBuffer pMonitors = GLFW.glfwGetMonitors();
		long[] monitors = new long[pMonitors.capacity()];
		pMonitors.get(monitors);
		
		// If a monitor to fullscreen on is defined
		String fullscreenMonitorName = settings.getProperty("monitor");
		long monitor = 0;
		if (fullscreenMonitorName!=null) {
			// Iterate all monitors to try to find the old monitor
			for(long mon : monitors) {
				String name = GLFW.glfwGetMonitorName(mon);
				if (name!=null&&name.equals(fullscreenMonitorName)) monitor = mon;
			}
			// If the monitor was not found, use the primary monitor
			if (monitor==0) monitor = GLFW.glfwGetPrimaryMonitor();
		}
		
		// Get the window width and height
		int windowWidth = -1, windowHeight = -1;
		windowWidth = tryGetIntProperty("width", -1);
		windowHeight = tryGetIntProperty("height", -1);
		
		// If the width or height was undefined
		if (windowWidth==-1||windowHeight==-1) {
			// If the window was fullscreen, use the size of the monitor
			if (monitor!=0) {
				GLFWVidMode mode = GLFW.glfwGetVideoMode(monitor);
				windowWidth = mode.width();
				windowHeight = mode.height();
			// Else, use the default 800x600 window
			} else {
				windowWidth = 800;
				windowHeight = 600;
			}
		}
		
		// Create the window
		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE);
		window.remakeWindow("Red Contagion", windowWidth, windowHeight, monitor);
		
		if (monitor!=0) window.focus(); // Focus if in windowed mode
		// Try to create the renderer
		try {
			render = RedContagionRender.getRender();
		} catch (ZEngineException e) {
			rcLogger.log(Level.SEVERE, "Failed to initialize", e);
		}
		
		// Create the application!
		ZEApplicationInfo appInfo = new ZEApplicationInfo(
			"Red Contagion",
			"ZEngine",
			render.getBackend(),
			render.getOutput()
		);
		app = new RedContagion(appInfo);
	}
	
	private static void deinit() {
		rcLogger.log(Level.ALL, "Shutting down...");
		rcLogger.log(Level.ALL, "Saving settings");
		// Save properties of the window
		Dimension windowSize = window.getSize();
		settings.setProperty("width", Integer.toString(windowSize.width));
		settings.setProperty("height", Integer.toString(windowSize.height));
		long monitor = window.getGLFWMonitor();
		if (monitor!=0) settings.setProperty("monitor", GLFW.glfwGetMonitorName(monitor));
		else settings.remove("monitor");
		
		window.destroy();
		
		// Save the settings file
		try (FileOutputStream stream = new FileOutputStream("settings.properties")) {
			settings.store(stream, null);
		} catch (Exception e) {
			rcLogger.log(Level.SEVERE, "Failed to save settings", e);
		}		
	}
	
	public static void main(String[] args) {
		rcLogger.log(Level.ALL, "Starting Red Contagion");
		init();
		
		while(!window.isCloseRequested()) {
			
			GLFW.glfwPollEvents();
		}
		
		deinit();
		rcLogger.log(Level.ALL, "Goodbye!");
	}
	
	private RedContagion(ZEApplicationInfo appinfo) {
		super(appinfo);
	}

	@Override
	public void onInternalException(ZEngineInternalException exception) {
		rcLogger.log(Level.SEVERE, "Internal exception thrown", exception);
		super.onInternalException(exception);
	}

}
