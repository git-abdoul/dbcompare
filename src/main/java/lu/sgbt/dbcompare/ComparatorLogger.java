/*
 * Copyright (c) 2012 by La Banque Postale
 * Program SIAM
 * All rights reserved
 */
package lu.sgbt.dbcompare;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

/**
 * Comparator Logger<BR>
 * Used to initialize the Log4J Logger Configuration<BR>
 * Target is to have Console Appender and File Appender if not configured<BR>
 * 
 * 
 * 
 */
public class ComparatorLogger {
	
	/**
	 * Configure the Comparator Logger<BR>
	 * Add a File and a Console Appender<BR> 
	 * @param home the Comparator home directory 
	 * @param configName the config file name 
	 */
	public static void configureLogger(String home, String configName) {
		String filePath = buildLogFilePath(home, configName) ;
		if (!isConfigured()) {
			ConsoleAppender consoleAppender = buildConsoleAppender();
			LogManager.getRootLogger().addAppender(consoleAppender) ;
			FileAppender fileAppender = buildFileAppender(filePath);
			LogManager.getRootLogger().addAppender(fileAppender) ;
		}
		LogManager.getRootLogger().setLevel(Level.INFO) ;
	}


	/**
	 * Build the Log File Path using given parameter 
	 * @param home the Comparator home
	 * @param configName the configuration file name
	 * @return the file path of the log file 
	 */
	private static String buildLogFilePath(String home, String configName) {
		// Initialize Log Directory
		String directory = home + File.separator + "logs";
		// Initialize Date to use for file name
		Date now = new Date() ;
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd.HHmmss.SSS") ;
		String nowString = format.format(now) ;
		// Build file name 
		String fileName = configName + "_" + nowString + ".log";
		// build and return file path
		return directory + File.separator + fileName;
	}


	/**
	 * Build the File Appender
	 * @param filePath the file path to use 
	 * @return a File Appender
	 */
	private static FileAppender buildFileAppender(String filePath) {
		FileAppender fileAppender = new FileAppender();
		fileAppender.setFile(filePath) ;
		fileAppender.setName("ComparatorFileAppender") ;
		fileAppender.setLayout(new SimpleLayout()) ;
		fileAppender.activateOptions() ;
		return fileAppender;
	}


	/**
	 * Build the Console Appender
	 * @return a Console Appender
	 */
	private static ConsoleAppender buildConsoleAppender() {
		ConsoleAppender consoleAppender = new ConsoleAppender();
		consoleAppender.setName("ComparatorConsoleAppender") ;
		consoleAppender.setLayout(new SimpleLayout()) ;
		consoleAppender.activateOptions() ;
		return consoleAppender;
	}
	

	/**
	 * Returns true if it appears that log4j have been previously configured.<BR>
	 * If there are only ConsoleAppender, it is considered as not configured<BR>
	 * @return a boolean true if already configured, false otherwise
	 */
	private static boolean isConfigured() {
		Enumeration<?> appenders =  LogManager.getRootLogger().getAllAppenders();
		if (appenders.hasMoreElements()) {
			// Check that the Appender is not the ConsoleAppender
			if (appenders.nextElement() instanceof ConsoleAppender && !appenders.hasMoreElements()) {
				// There is only one appenders in that Logger and it is the ConsoleAppender
				return isCurrentLoggersConfigured();
			} else {
				return true ;
			}
		} else {
			return isCurrentLoggersConfigured();
		}
	}


	/**
	 * Return a boolean true if CurrentLoggers are configured, false otherwise<BR>
	 * If there are only ConsoleAppender, it is considered as not configured<BR>
	 * @return boolean true if CurrentLoggers are configured, false otherwise
	 */
	private static boolean isCurrentLoggersConfigured() {
		Enumeration<?> loggers = LogManager.getCurrentLoggers();
		while (loggers.hasMoreElements()) {
			Logger currentLogger = (Logger) loggers.nextElement();
			Enumeration<?> allAppenders = currentLogger.getAllAppenders();
			if (allAppenders.hasMoreElements() &&  (!(allAppenders.nextElement() instanceof ConsoleAppender) || allAppenders.hasMoreElements())) {
				return true;
			}
		}
		return false ;
	}

}
