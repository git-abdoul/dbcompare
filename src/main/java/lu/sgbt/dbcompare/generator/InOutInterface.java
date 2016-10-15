/*
 * Copyright (c) 2012 by La Banque Postale
 * Program SIAM
 * All rights reserved
 */
package lu.sgbt.dbcompare.generator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

/**
 * In/Out Interface<BR>
 * Manage File/Resource loading and Output Generations 
 * 
 * 
 *
 */
public class InOutInterface {
	
	/** Local Logger */
	private static final Logger LOG = Logger.getLogger(InOutInterface.class) ;
	
	/** Ouput Directory Path Name */
	private static final String OUTPUT_DIR_PATH = "./target/comparator/";

	/** Singleton Instance */
	private static InOutInterface instance = new InOutInterface()  ; 
	
	/** 
	 * Singletong, Private Constructor
	 */
	private InOutInterface() {
	}
	
	public static InOutInterface getInstance() {
		return instance ;
	}

	/**
	 * @param fileName
	 * @param result
	 * @throws IOException
	 */
	public void writeFile(String fileName, String result) throws IOException {
		// Define output Directory
		File outputDirectory = new File(OUTPUT_DIR_PATH) ;
		// Generate Output
		if (!outputDirectory.exists() && outputDirectory.mkdirs()) {
			LOG.info(String.format("Directory [%s] Created !", outputDirectory.getAbsolutePath()));
		}
		File outputFile = new File(outputDirectory, fileName) ;
		FileWriter fileWriter = new FileWriter(outputFile) ;
		BufferedWriter writer = new BufferedWriter(fileWriter) ;
		writer.write(result) ;
		writer.flush() ;
		writer.close() ;
	}

	/**
	 * @return a BufferedReader of the given resource name
	 */
	public  BufferedReader buildBufferedReader(String resourceName) {
		InputStream envConfig = this.getClass().getResourceAsStream(resourceName) ;
		InputStreamReader inputStreamReader = new InputStreamReader(envConfig) ;
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader) ;
		return bufferedReader;
	}
	
	


	/**
	 * Read given resource and return its content as String
	 * @param resourceName
	 * @return a String the resource content
	 * @throws IOException
	 */
	public String readResource(String resourceName) throws IOException {
		InputStream envConfig = this.getClass().getResourceAsStream(resourceName) ;
		InputStreamReader inputStreamReader = new InputStreamReader(envConfig) ;
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader) ;
		String currentLine =null ;
		StringBuffer buffer = new StringBuffer() ;
		while ((currentLine = bufferedReader.readLine()) != null) {
			buffer.append(currentLine).append('\n') ;
		}
		return buffer.toString() ;
	}
	

}
