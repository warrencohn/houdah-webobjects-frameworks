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

package com.houdah.auditTrail;

import com.houdah.eovalidation.control.ValidatingRecord;

import com.webobjects.eocontrol.EOClassDescription;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSTimestamp;

/**
 * ChangeableRecord is the abstract superclass of any business object that can
 * be changed by the user. <br/> Its task is to set the user and the timestamp
 * of the modification on a business object whenever it is changed.
 * 
 * @author bernard
 */
public abstract class ChangeableRecord extends ValidatingRecord
{
	// Private class constants
	
	private static final long	serialVersionUID	= -3616276370310153073L;
	
	
	
	// Public class constants
	
	public static final String	AUDIT_DATE			= "auditDate";
	
	public static final String	AUDIT_USER			= "auditUser";
	
	
	
	// Private instance variables
	
	private NSTimestamp			nextAuditDate		= null;
	
	
	private AuditUser			nextAuditUser		= null;
	
	
	
	
	// Constructors
	
	public ChangeableRecord()
	{
	}
	
	
	public ChangeableRecord(EOClassDescription classDescription)
	{
		super(classDescription);
	}
	
	
	
	// Public instance methods
	
	/**
	 * Updates the enterprise object with values taken from a dictionary.<br/>
	 * 
	 * An update occurs only for field or relationships that have different
	 * values on the object and in the dictionary. This prevents unnecessary
	 * triggering of the audit trail mechanism.<br/>
	 * 
	 * Please use the public class constant field names for keys to the
	 * dictionary.<br/>
	 * 
	 * @param dictionary
	 *            gives new values for fields and relationships
	 * @return true, if an update was performed
	 */
	public boolean updateFromDictionary(NSDictionary dictionary)
	{
		boolean hasChanges = false;
		NSDictionary changes = changesFromSnapshot(dictionary);
		NSArray changesKeys = changes.allKeys();
		
		
		// Copy the properties
		for (int i = 0; i < changesKeys.count(); i++) {
			String key = (String) changesKeys.objectAtIndex(i);
			Object value = dictionary.objectForKey(key);
			
			
			// BUG WORKAROUND !!!
			if ((value != null) && (!value.equals(valueForKey(key)))) {
				takeValueForKey(value, key);
				hasChanges = true;
			}
		}
		
		return hasChanges;
	}
	
	
	
	// Public abstract instance methods
	
	public abstract NSTimestamp auditDate();
	
	
	public abstract void setAuditDate(NSTimestamp value);
	
	
	public abstract AuditUser auditUser();
	
	
	public abstract void setAuditUser(AuditUser value);
	
	
	
	// Protected instance methods related to change management
	
	
	/**
	 * Handles the event received from the AuditingEditingContext.<br/>
	 * <ul>
	 * <li>Sets the the user and the timestamp of the modification in case of
	 * an update by calling setDateAndUserOfLastChange().
	 * <li>Gives the object an opportunity to create an history object by
	 * calling createHistoricData().
	 * </ul>
	 * 
	 * @see #createHistoricData
	 * @see #setDateAndUserOfLastChange
	 * @see AuditingEditingContext#objectWillChange
	 */
	protected synchronized void objectWillChange()
	{
		EOEditingContext context = editingContext();
		
		if (context instanceof AuditingEditingContext) {
			if ((this.nextAuditDate == null) || (this.nextAuditUser == null)) {
				this.nextAuditUser = ((AuditingEditingContext) context).auditUser();
				this.nextAuditDate = new NSTimestamp();
			}
		}
	}
	
	
	
	/**
	 * Called by the AuditingEditor to inform the object that we are about to
	 * commit.
	 */
	protected synchronized void commitAuditTrail()
	{
		// changes the object by setting a user and a date
		recordAuditUserAndDate();
	}
	
	
	
	/**
	 * Called by the AuditingEditingContext after accepting previously
	 * historized changes from a child context.
	 */
	protected synchronized void purgeAuditTrail()
	{
		this.nextAuditDate = null;
		this.nextAuditUser = null;
	}
	
	
	
	// Private instance methods
	
	/**
	 * Sets the the user and the timestamp of the modification.
	 */
	private void recordAuditUserAndDate()
	{
		if (this.nextAuditDate != null) {
			setAuditDate(this.nextAuditDate);
			this.nextAuditDate = null;
		}
		
		if (this.nextAuditUser != null) {
			setAuditUser(this.nextAuditUser);
			this.nextAuditUser = null;
		}
	}
}