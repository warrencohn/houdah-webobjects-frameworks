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

package com.houdah.eoaccess.databaseContext;

import com.webobjects.eoaccess.EODatabaseChannel;
import com.webobjects.eoaccess.EODatabaseContext;
import com.webobjects.eocontrol.EOGlobalID;
import com.webobjects.foundation.NSLog;
import com.webobjects.foundation.NSNotification;

/**
 * Default database context delegate.
 * 
 * @author bernard
 */
public class DatabaseContextDelegate implements
		DatabaseContextDelegateInterface
{
	// Protected class constants
	
	/**
	 * The maximum number of database channels to open per database context.<br/>
	 * Apparently a channel is needed per level of editing context nesting.
	 */
	protected static final int	MAX_CHANNELS	= 2;
	
	
	
	
	// Public constructor
	
	/**
	 * Designated constructor
	 */
	public DatabaseContextDelegate()
	{
		super();
	}
	
	
	
	// Public instance methods
	
	/**
	 * EODatabaseContext.Delegate implementation.<br/>
	 * 
	 * Called if a fault fired but no corresponding database row could be found.
	 * In EOF 5, no exception is throw, but an empty enterprise object is
	 * returned. This may lead to an exception later on when the application
	 * later tries to save an object graph that requires the missing fault.
	 */
	public boolean databaseContextFailedToFetchObject(
			EODatabaseContext databaseContext, Object object,
			EOGlobalID globalID)
	{
		throw new IllegalStateException(
				"INTEGRITY ERROR: failed to retrieve object for global ID "
						+ globalID);
	}
	
	
	
	/**
	 * Listener for EODatabaseContext.DatabaseChannelNeededNotification
	 * notifications.<br/>
	 * 
	 * Creates database channels as needed up to a limit of MAX_CHANNELS per
	 * database context
	 */
	public void createAdditionalDatabaseChannel(NSNotification notification)
	{
		EODatabaseChannel databaseChannel;
		EODatabaseContext databaseContext = (EODatabaseContext) notification
				.object();
		
		if (databaseContext != null) {
			if (databaseContext.registeredChannels().count() < MAX_CHANNELS) {
				databaseChannel = new EODatabaseChannel(databaseContext);
				
				if (databaseChannel != null) {
					databaseContext.registerChannel(databaseChannel);
					
					NSLog.out
							.appendln("Application - createAdditionalDatabaseChannel: channel count = "
									+ databaseContext.registeredChannels()
											.count());
				}
			} else {
				NSLog.out
						.appendln("Application - createAdditionalDatabaseChannel: channel count limit exceeded");
			}
		}
	}
}