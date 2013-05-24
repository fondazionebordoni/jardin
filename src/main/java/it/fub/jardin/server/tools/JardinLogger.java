package it.fub.jardin.server.tools;

import it.fub.jardin.client.model.User;
import it.fub.utilities.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.Random;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.log4j.PropertyConfigurator;

public class JardinLogger {

  // private static dataserverLog4jLogger locator = new
  // dataserverLog4jLogger();

  // formato della data
  public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
  // formato della sessione
  public static final String SESSION_TIME = "yyyyMMddHHmmss";
  // acronimo del sottosistema di elaborazione
  private static Logger logger;

  private static String LOG_PROPERTIES_FILE;

  // valori da inserire nel thread context
  private static String logSubsystem;
  private static String sessionValue;
  private static String hostname;

  // private static String username;

  /**
   * This should be removed
   */
  public JardinLogger(String confDir, String subSystem, User user) {
    // TODO Auto-generated constructor stub
    logSubsystem = subSystem;

    if (confDir.startsWith("/")) {
      LOG_PROPERTIES_FILE = confDir;
    } else {
      LOG_PROPERTIES_FILE =
          getClass().getClassLoader().getResource(confDir).getFile();
    }

    LOG_PROPERTIES_FILE = LOG_PROPERTIES_FILE + "/log4j.properties";
    // if (user == null) {
    // username = "REGISTRAZIONE";
    // } else username = user.getUsername();

    initializeLogger();

  }

  public static void setContext(Hashtable threadContext) {

    if (threadContext == null
        || !threadContext.containsKey("log4j_initialized")) {

      MDC.put("hostname", hostname);
      // MDC.put("username", username);
      MDC.put("subsystem", logSubsystem);
      MDC.put("session", sessionValue);
      MDC.put("log4j_initialized", "yes");

    }
  }

  public static void init(String confDir, String subSystem) {
    logSubsystem = subSystem;

    if (confDir.startsWith("/")) {
      LOG_PROPERTIES_FILE = confDir;
    } else {
      LOG_PROPERTIES_FILE =
          Log4jLogger.class.getClassLoader().getResource(confDir).getFile();
    }

    LOG_PROPERTIES_FILE = LOG_PROPERTIES_FILE + "/log4j.properties";
    // if (user == null) {
    // username = "REGISTRAZIONE";
    // } else username = user.getUsername();

    // initializeLogger(username);
    initializeLogger();

  }

  private static void initializeLogger() {
    logger = Logger.getLogger("log4j dataserver logger");
    Properties logProperties = new Properties();

    hostname = null;
    try {
      hostname = InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }

    // username = user.getUsername();
    // MDC.put("hostname", hostname);
    // MDC.put("subsystem", logSubsystem);

    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
    SimpleDateFormat ssf = new SimpleDateFormat(SESSION_TIME);

    sessionValue = ssf.format(cal.getTime());

    // MDC.put("session", sessionValue);
    setContext(MDC.getContext());

    try {
      logProperties.load(new FileInputStream(LOG_PROPERTIES_FILE));
      PropertyConfigurator.configure(logProperties);
      logger.info("Attivazione delle " + sdf.format(cal.getTime()));
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException("Unable to load logging property "
          + LOG_PROPERTIES_FILE);
    }

  }

  public static void debug(String username, String string) {
    setContext(MDC.getContext());
    logger.debug("[" + username + "]: " + string);
  }

  public static void info(String username, String string) {
    setContext(MDC.getContext());
    logger.info("[" + username + "]: " + string);
  }

  public static void warn(String username, String string) {
    setContext(MDC.getContext());
    logger.warn("[" + username + "]: " + string);
  }

  public static void error(String username, String string) {
    setContext(MDC.getContext());
    logger.error("[" + username + "]: " + string);
  }

  public static void fatal(String username, String string) {
    setContext(MDC.getContext());
    logger.fatal("[" + username + "]: " + string);
  }

  public static void logStackTrace(Exception e) {
    setContext(MDC.getContext());
    StackTraceElement[] stackTrace = e.getStackTrace();
    logger.debug("Exception, cause: " + e.getCause() + ", Message: "
        + e.getMessage() + "stack trace follows:");
    for (StackTraceElement stackTraceElement : stackTrace) {
      logger.debug("    at " + stackTraceElement.toString());
    }
  }

  public static void logStackTrace(Throwable caught) {
    setContext(MDC.getContext());
    StackTraceElement[] stackTrace = caught.getStackTrace();
    logger.debug("Exception, cause: " + caught.getCause() + ", Message: "
        + caught.getMessage() + "stack trace follows:");
    for (StackTraceElement stackTraceElement : stackTrace) {
      logger.debug("    at " + stackTraceElement.toString());
    }
  }

}
