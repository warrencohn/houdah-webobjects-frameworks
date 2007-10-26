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

import com.webobjects.eoaccess.EODatabase;
import com.webobjects.eoaccess.EODatabaseContext;
import com.webobjects.eoaccess.EORelationship;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.eocontrol.EOFaulting;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.eocontrol.EOGlobalID;
import com.webobjects.foundation.NSArray;

/**
 * Custom subclass to EODatabaseContext.<br/>
 * 
 * This class needs to be registered as default database context class by
 * calling EODatabaseContext.setContextClassToRegister(DatabaseContext.class);
 * This is best done in the application's main (entry point) method.
 */
public class DatabaseContext extends EODatabaseContext
{
	// Constructor
	
	public DatabaseContext(EODatabase database)
	{
		super(database);
	}
	
	
	
	// Public instance methods
	
	/**
	 * Internal method that handles prefetching of to-many relationships.<br/> //
	 * TBD This is a workaround to what looks like a bug in WO 5.1 & WO 5.2.
	 * Remove as soon as it's no longer needed
	 * 
	 * The problem is that even refreshing fetches don't refresh the to-many
	 * relationships they prefetch.
	 */
	public void _followToManyRelationshipWithFetchSpecification(EORelationship relationship,
			EOFetchSpecification fetchspecification, NSArray objects,
			EOEditingContext editingcontext)
	{
		int count = objects.count();
		
		for (int i = 0; i < count; i++) {
			EOEnterpriseObject object = (EOEnterpriseObject) objects.objectAtIndex(i);
			EOGlobalID sourceGlobalID = editingcontext.globalIDForObject(object);
			String relationshipName = relationship.name();
			
			if (!object.isFault()) {
				EOFaulting toManyArray = (EOFaulting) object.storedValueForKey(relationshipName);
				
				if (!toManyArray.isFault()) {
					EOFaulting tmpToManyArray = (EOFaulting) arrayFaultWithSourceGlobalID(
							sourceGlobalID, relationshipName, editingcontext);
					
					
					// Turn the existing array back into a fault by assigning it
					// the fault handler of the newly created fault
					toManyArray.turnIntoFault(tmpToManyArray.faultHandler());
				}
			}
		}
		
		super._followToManyRelationshipWithFetchSpecification(relationship, fetchspecification,
				objects, editingcontext);
	}
	
	
	public void setDelegate(Object obj)
	{
		lock();
		
		try {
			super.setDelegate(obj);
		} finally {
			unlock();
		}
	}
}