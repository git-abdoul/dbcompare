/*
 * Copyright (c) 2012 by La Banque Postale
 * Program SIAM
 * All rights reserved
 */
package lu.sgbt.dbcompare.generator;


/**
 * 
 *
 */
public interface ILineProcessor {

	/** Initialize Concrete Parameter Object */
	AbstractParameter initializeParameter() ;
	
	/** Process Input Line, using and updating given parameter<BR>
	 * 
	 * @param inputLine the input Line to deal with
	 * @param param the param to use and update
	 */
	void processLine(String inputLine, AbstractParameter param) ;

	/**
	 * Transform given Abstract Output into a Concrete one know by that processor<BR>
	 * Concrete LineProcessor can define proper output type
	 * 
	 * @param param the input parameter to transform
	 * @return a concrete parameter 
	 */
	AbstractParameter transformOutput(AbstractParameter param) ;
	
}
