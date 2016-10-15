/*
 * Copyright (c) 2012 by La Banque Postale
 * Program SIAM
 * All rights reserved
 */
package lu.sgbt.dbcompare.generator;

import java.io.BufferedReader;
import java.io.IOException;


/**
 * BufferedReaderProcessor<BR>
 * This class is able to process an input BufferedReader using an external LineProcessor<BR>
 * It returns as result of the Processing an AbstractLineProcessorParam<BR>
 * 
 */
public class BufferedReaderProcessor {

	/**
	 * Process the incoming BufferedReader using given Line Processor
	 * @param bufferedReader the buffered reader to process
	 * @param lineProcessor the line processor to use
	 * @return an AbstractLineProcessorParam as result of the processing
	 * @throws IOException
	 */
	public static AbstractParameter processBufferedReader(BufferedReader bufferedReader, ILineProcessor lineProcessor) throws IOException {
		String currentLine =null ;
		AbstractParameter parameter = lineProcessor.initializeParameter() ;
		while ((currentLine = bufferedReader.readLine()) != null) {
			lineProcessor.processLine(currentLine, parameter) ;
		}
		bufferedReader.close() ;
		return parameter ;
	}
	
}
