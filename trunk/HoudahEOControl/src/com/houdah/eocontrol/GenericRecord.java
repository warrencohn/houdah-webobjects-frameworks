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

package com.houdah.eocontrol;

import com.webobjects.eocontrol.EOClassDescription;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOGenericRecord;
import com.webobjects.eocontrol.EOGlobalID;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;

public class GenericRecord extends EOGenericRecord
{
	// Private class constants
	
	private static final long	serialVersionUID	= -1190771590830521977L;
	
	
	
	
	// Constructors
	
	public GenericRecord()
	{
	}
	
	
	public GenericRecord(EOClassDescription classDescription)
	{
		super(classDescription);
	}
	
	
	
	// Public instance methods
	
	/** Convenience method: delete this object
	 */
	public void delete()
	{
		editingContext().deleteObject(this);
	}
	
	
	/** Convenience method: get global ID for this object
	 */
	public EOGlobalID globalID()
	{
		return editingContext().globalIDForObject(this);
	}
	
	
	
	// Public instance methods
	
	
	/**
	 * Checks if this is a newly created object.
	 * 
	 * @return true if the object is newly created or deleted
	 */
	public boolean isNew()
	{
		EOEditingContext editingContext = editingContext();
		
		return ((editingContext == null) || editingContext().globalIDForObject(this).isTemporary());
	}	
	
	/**
	 * Checks if this is a modified object.
	 * 
	 * @return true if the object has been modified from a previous version
	 *         (i.e. not new)
	 */
	public boolean isUpdated()
	{
		NSDictionary changes = changes();
		
		return ((changes != null) && (changes.count() > 0));
	}
	
	
	/**
	 * Checks if this is a deleted object.
	 * 
	 * @return true if the object has been deleted
	 */
	public boolean isDeleted()
	{
        EOGlobalID globalID = __globalID(); 
        EOEditingContext editingContext = editingContext(); 
        boolean isDeleted = false; 
        
        if (editingContext == null) { 
                isDeleted = (globalID != null) && (!globalID.isTemporary()); 
        } else { 
                NSArray deletedObjects = editingContext.deletedObjects(); 
                isDeleted = deletedObjects.containsObject(this); 
        } 
        
        return isDeleted; 
	}
	
	
	/**
	 * Gets a snapshot of the object as prior to a modification.
	 * 
	 * @return the snapshot, null for new objects and objects with no editing context (e.g. deleted)
	 */
	public NSDictionary committedSnapshot()
	{
		if ((editingContext() == null) || isNew()) {
			return null;
		}
		
		return editingContext().committedSnapshotForObject(this);
	}
	
	
	/**
	 * Gets changes made since the last saved snapshot.
	 * 
	 * @return the changes, null for objects with no editing context (e.g. deleted)
	 */
	public NSDictionary changes()
	{
		if (editingContext() == null) {
			return null;
		}
		
		NSDictionary committedSnapshot = committedSnapshot();
		
		if (committedSnapshot == null) {
			return snapshot();
		}
		
		return changesFromSnapshot(committedSnapshot);
	}
}