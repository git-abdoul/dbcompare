/*
 * Copyright (c) 2012 by La Banque Postale
 * Program SIAM
 * All rights reserved
 */
package lu.sgbt.dbcompare.generator;

/**
 * Database Information
 * 
 * 
 */
public class DbInfo {

	/** Production Env DB Name */
//	private static final String PROD_DBNAME = "PROD";
	
	private String dbName;
	private String dbUrl;
	private String dbUser;
	private String dbPwd;
	private String dbPrefixSchema;

	/**
	 * @param dbName
	 * @param dbUrl
	 * @param dbUser
	 * @param dbPwd
	 */
	public DbInfo(String dbName, String dbUrl, String dbUser, String dbPwd, String dbPrefixSchema) {
		super();
		this.dbName = dbName;
		this.dbUrl = dbUrl;
		this.dbUser = dbUser;
		this.dbPwd = dbPwd;
		this.dbPrefixSchema = dbPrefixSchema;
	}

	public String getDbName() {
		return dbName;
	}

	public String getDbUrl() {
		return dbUrl;
	}

	public String getDbUser() {
		return dbUser;
	}

	public String getDbPwd() {
		return dbPwd;
	}
	
	public String getDbPrefixSchema() {
		return dbPrefixSchema;
	}

	/**
	 * Returns a boolean true if env name is Prod
	 * @return
	 */
//	public boolean isProd() {
//		return PROD_DBNAME.equals(getDbName()) ;
//	}

	@Override
	public String toString() {
		String toString = "DbInfo [dbName=" + dbName + ", dbUrl=" + dbUrl + ", dbUser="
				+ dbUser + ", dbPwd=" + dbPwd + "]";
		return toString;
	}
	
	

}
