/*
 * Copyright (c) 2012 by La Banque Postale
 * Program SIAM
 * All rights reserved
 */
package lu.sgbt.dbcompare.generator;

import java.util.StringTokenizer;

import org.apache.log4j.Logger;


/**
 * EnvLine resource Parser<BR>
 * Process input Line and 
 * 
 * 
 *
 */
public class EnvLineParser implements ILineProcessor {

	/** Local Logger */
	private static final Logger LOG = Logger.getLogger(EnvLineParser.class) ;
	
	/** Env list Delimiter */
	private static final String DELIM = "|";
	/** Expected Number of columns in environment list file */
	private static final int NB_COLUMNS = 5;

	/* (non-Javadoc)
	 * @see com.lbp.tools.comparator.LineProcessor#processLine(java.lang.String, com.lbp.tools.comparator.AbstractLineProcessorParam)
	 */
	public void processLine(String inputLine, AbstractParameter param) {
		StringTokenizer tokenizer = new StringTokenizer(inputLine, DELIM) ;
		if (tokenizer.countTokens() == NB_COLUMNS) {
			String dbName = tokenizer.nextToken() ;
			String dbUrl = tokenizer.nextToken() ;
			String dbUser = tokenizer.nextToken() ;
			String dbPwd = tokenizer.nextToken() ;
			String dbPrefixSchema = tokenizer.nextToken() ;
			((EnvLineParameter)param).getDbInfoList().add(new DbInfo(dbName, dbUrl, dbUser, dbPwd, dbPrefixSchema)) ;
		} else {
			LOG.error(String.format("Current Line [%s] does not have proper number of parameter [%s]", inputLine, NB_COLUMNS)) ;
		}
	}

	/* (non-Javadoc)
	 * @see com.lbp.tools.comparator.LineProcessor#initializeParameter()
	 */
	public EnvLineParameter initializeParameter() {
		return new EnvLineParameter() ;
	}

	/* (non-Javadoc)
	 * @see com.lbp.tools.comparator.ILineProcessor#transformOutput(com.lbp.tools.comparator.AbstractParameter)
	 */
	public EnvLineParameter transformOutput(AbstractParameter param) {
		return (EnvLineParameter) param;
	}
}