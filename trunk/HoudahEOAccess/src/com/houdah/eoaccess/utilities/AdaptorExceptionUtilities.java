/*
 * Modified MIT License
 * 
 * Copyright (c) 2006-2007 Houdah Software s.à r.l.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * Except as contained in this notice, the name(s) of the above copyright holders
 * shall not be used in advertising or otherwise to promote the sale, use or other 
 * dealings in this Software without prior written authorization.
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
**/

package com.houdah.eoaccess.utilities;

import java.sql.SQLException;

import com.houdah.foundation.ForwardException;

import com.webobjects.eoaccess.EOAdaptor;
import com.webobjects.eoaccess.EOAdaptorChannel;
import com.webobjects.eoaccess.EOAdaptorOperation;
import com.webobjects.eoaccess.EODatabaseContext;
import com.webobjects.eoaccess.EODatabaseOperation;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOGeneralAdaptorException;
import com.webobjects.eocontrol.EOGlobalID;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.jdbcadaptor.JDBCAdaptor;
import com.webobjects.jdbcadaptor.JDBCAdaptorException;
import com.webobjects.jdbcadaptor.JDBCPlugIn;

/**
 * Repository of tools for working with EOGeneralAdaptorExceptions.<br/>
 * 
 * @author Bernard
 */

public class AdaptorExceptionUtilities
{
	// Public class constants
	
	/** Useful constant for error type. * */
	public static final int	UNKOWN_DB_ERROR				= -1;
	
	
	
	/** Useful constant for error type. * */
	public static final int	OPTIMISTIC_LOCK_FAILURE		= 0;
	
	
	
	/** Useful constant for error type. * */
	public static final int	UNIQUE_CONSTRAINT_VIOLATED	= 1;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 */
	private AdaptorExceptionUtilities()
	{
		throw new IllegalStateException("Do not instantiate this utility class");
	}
	
	
	
	// Public class methods
	
	
	/**
	 * Called when a database operation error needs to be interpreted.<br/>
	 * 
	 * @see com.webobjects.eoaccess.EOAdaptorChannel
	 * @param exception
	 *            the exception to interpret
	 * @return the type of the error
	 */
	public static int typeOfException(EOGeneralAdaptorException exception)
	{
		int type = UNKOWN_DB_ERROR;
		Object userInfo = exception.userInfo();
		
		if ((userInfo != null) && (userInfo instanceof NSDictionary)) {
			NSDictionary dictionary = (NSDictionary) userInfo;
			Object failureKey = dictionary.objectForKey(EOAdaptorChannel.AdaptorFailureKey);
			
			if (EOAdaptorChannel.AdaptorOptimisticLockingFailure.equals(failureKey)) {
				type = OPTIMISTIC_LOCK_FAILURE;
			} else {
				EOAdaptorOperation operation = (EOAdaptorOperation) dictionary
						.objectForKey(EOAdaptorChannel.FailedAdaptorOperationKey);
				EOEntity entity = operation.entity();
				EOAdaptor adaptor = EOAdaptor.adaptorWithModel(entity.model());
				
				if (adaptor instanceof JDBCAdaptor) {
					JDBCAdaptor jdbcAdaptor = (JDBCAdaptor) adaptor;
					JDBCPlugIn jdbcPlugIn = jdbcAdaptor.plugIn();
					JDBCAdaptorException jdbcException = (JDBCAdaptorException) operation
							.exception();
					SQLException sqlException = jdbcException.sqlException();
					int vendorError = sqlException.getErrorCode();
					
					if (jdbcPlugIn.databaseProductName().indexOf("Oracle") != -1) {
						if (vendorError == 1) {
							type = UNIQUE_CONSTRAINT_VIOLATED;
						}
					}
				}
			}
		}
		
		return type;
	}
	
	
	
	/**
	 * Returns the global ID of the object affected by a failed database
	 * operation.
	 * 
	 * @param exception
	 *            the exception to analyze
	 * @return a global ID or null
	 */
	public static EOGlobalID affectedGlobalID(EOGeneralAdaptorException exception)
	{
		return failedOperation(exception).globalID();
	}
	
	
	
	/**
	 * Returns the object affected by a failed database operation.
	 * 
	 * @param exception
	 *            the exception to analyze
	 * @return the affected object or null
	 */
	public static Object affectedObject(EOGeneralAdaptorException exception)
	{
		return failedOperation(exception).object();
	}
	
	
	
	// Protected class methods
	
	protected static EODatabaseOperation failedOperation(EOGeneralAdaptorException exception)
	{
		try {
			NSDictionary userInfo = (NSDictionary) exception.userInfo();
			EODatabaseOperation failedOperation = (EODatabaseOperation) userInfo
					.objectForKey(EODatabaseContext.FailedDatabaseOperationKey);
			
			return failedOperation;
		} catch (Exception e) {
			exception.printStackTrace();
			
			throw new ForwardException(e);
		}
	}
}