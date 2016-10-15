/*
 * Copyright (c) 2012 by La Banque Postale
 * Program SIAM
 * All rights reserved
 */
package lu.sgbt.dbcompare;

import java.io.IOException;


import org.apache.log4j.Logger;


/**
 * File Configuration Generator Starter 
 * 
 *
 */
public final class StartFileConfigurationGenerator {
	
	/** Local Logger */
	private static final Logger LOG = Logger.getLogger(StartFileConfigurationGenerator.class) ;


	/**
	 * Main Method
	 * 
	 * @param args
	 *            command line arguments
	 * @throws IOException 
	 */
	public static void main(String[] args) {
		StartFileConfigurationGenerator main = new StartFileConfigurationGenerator();
		main.start();
	}
	
	/**
	 * Start the File Configuration Generator
	 */
	public void start() {
		FileConfigurationGenerator main = new FileConfigurationGenerator();
		try {
			main.process();
		} catch (IOException e) {
			LOG.error("Failed to generate files.", e) ;
		}
	}

}
