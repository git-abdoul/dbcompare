/*
 * Copyright (c) 2012 by La Banque Postale
 * Program SIAM
 * All rights reserved
 */
package lu.sgbt.dbcompare.generator;

import java.util.ArrayList;
import java.util.List;


/**
 * Parameter used by the EnvLine Processor
 * 
 *
 */
public class EnvLineParameter extends AbstractParameter {

	private List<DbInfo> dbInfoList = new  ArrayList<DbInfo>() ;

	public List<DbInfo> getDbInfoList() {
		return dbInfoList;
	}
	
}
