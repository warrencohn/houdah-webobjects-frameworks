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

import com.houdah.eocontrol.utilities.ControlUtilities;
import com.houdah.eovalidation.control.ValidatingEditingContext;

import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.eocontrol.EOObjectStore;
import com.webobjects.foundation.NSMutableArray;

public class AuditingEditingContext extends ValidatingEditingContext
{
	// Private class constants
	
	private static final long	serialVersionUID	= 5328201534976033544L;
	
	
	
	// Private instance variables
	
	private AuditUser			auditUser;
	
	
	
	
	// Constructor
	
	public AuditingEditingContext()
	{
		initAuditingEditingContext();
	}
	
	
	public AuditingEditingContext(EOObjectStore parentStore)
	{
		super(parentStore);
		
		initAuditingEditingContext();
	}
	
	
	
	// Public instance methods
	
	public AuditUser auditUser()
	{
		return this.auditUser;
	}
	
	
	public void setAuditUser(AuditUser auditUser)
	{
		if ((auditUser == null) || (auditUser.editingContext() == this)) {
			this.auditUser = auditUser;
		} else {
			lock();
			
			try {
				this.auditUser = (AuditUser) ControlUtilities
						.localInstanceOfObject(this, auditUser);
			} finally {
				unlock();
			}
		}
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.eocontrol.EOEditingContext#objectWillChange(java.lang.Object)
	 */
	public void objectWillChange(Object object)
	{
		if (object instanceof ChangeableRecord) {
			((ChangeableRecord) object).objectWillChange();
		}
		
		super.objectWillChange(object);
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.eocontrol.EOEditingContext#saveChangesInEditingContext(com.webobjects.eocontrol.EOEditingContext)
	 */
	public void saveChangesInEditingContext(EOEditingContext editingContext)
	{
		// Prevent duplication of audit trail when using nested editing contexts
		NSMutableArray affectedObjects = null;
		
		if (editingContext.parentObjectStore() == this) {
			if (editingContext instanceof AuditingEditingContext) {
				affectedObjects = new NSMutableArray();
				
				affectedObjects.addObjectsFromArray(editingContext.updatedObjects());
				affectedObjects.addObjectsFromArray(editingContext.insertedObjects());
			}
		}
		
		super.saveChangesInEditingContext(editingContext);
		
		if (affectedObjects != null) {
			int aCount = affectedObjects.count();
			
			for (int a = 0; a < aCount; a++) {
				EOEnterpriseObject object = (EOEnterpriseObject) affectedObjects.objectAtIndex(a);
				EOEnterpriseObject localObject = ControlUtilities.localInstanceOfObject(this,
						object);
				
				if (localObject instanceof ChangeableRecord) {
					((ChangeableRecord) localObject).purgeAuditTrail();
				}
			}
		}
	}
	
	
	
	// Protected instance methods
	
	protected void initAuditingEditingContext()
	{
		/*
		 * // Prevent multiple calls to audit trail mechanisms // by not
		 * activating if nested in another AuditingEditingContext boolean
		 * parentIsAuditing = false; EOObjectStore parentStore =
		 * parentObjectStore();
		 * 
		 * while (parentStore instanceof EOEditingContext) { if (parentStore
		 * instanceof AuditingEditingContext) { parentIsAuditing = true; break; }
		 * 
		 * parentStore = ((EOEditingContext) parentStore).parentObjectStore(); }
		 * 
		 * if (!parentIsAuditing) { addEditor(new AuditingEditor()); }
		 */
		addEditor(new AuditingEditor());
	}
}