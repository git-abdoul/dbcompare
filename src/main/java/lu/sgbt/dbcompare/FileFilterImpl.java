/*
 *
 * Copyright (c) 2000 by Calypso Technology, Inc.
 * 595 Market Street, Suite 1980, San Francisco, CA  94105, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Calypso Technology, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Calypso Technology.
 *
 */
package lu.sgbt.dbcompare;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

/**
 * The Class FileFilterImpl.
 * 
 * @author Calypso Paris
 * @since 17/05/2010
 * @version 1.0
 */
public class FileFilterImpl implements FileFilter {

	/** The pattern. */
	private final transient String pattern;

	/**
	 * Instantiates a new file filter impl.
	 * 
	 * @param patternIn pattern
	 */
	public FileFilterImpl(final String patternIn) {
		this.pattern = patternIn;
	}

	/**
	 * Accept.
	 * 
	 * @param pathname file name
	 * @return true if accepted
	 */
	public boolean accept(final File pathname) {
		return Pattern.compile(pattern).matcher(pathname.getName()).find();
	}
}
