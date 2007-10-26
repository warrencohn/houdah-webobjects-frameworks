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

package com.houdah.web.control.application;

import com.houdah.foundation.KVCObject;
import com.houdah.foundation.utilities.DictionaryUtilities;

import com.webobjects.foundation.NSKeyValueCoding;
import com.webobjects.foundation.NSMutableDictionary;

public class SecurityManager extends KVCObject
{
	// Private instance variables
	
	private NSMutableDictionary	entities;
	
	
	
	
	// Constructor
	
	public SecurityManager()
	{
		this.entities = new NSMutableDictionary();
	}
	
	
	
	// Public instance methods
	
	public boolean mayAccess(String entity, String task)
	{
		return entity(entity).mayAccess(task);
	}
	
	
	public void grantAccess(String entity, String task)
	{
		entity(entity).grantAccess(task);
	}
	
	
	public void revokeAccess(String entity, String task)
	{
		entity(entity).revokeAccess(task);
	}
	
	
	
	// Key-value coding
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.foundation.NSKeyValueCoding.ErrorHandling#handleQueryWithUnboundKey(java.lang.String)
	 */
	public Object handleQueryWithUnboundKey(String key)
	{
		return entity(key);
	}
	
	
	
	// Protected instance methods
	
	protected Entity entityWithName(String name)
	{
		return new Entity(name);
	}
	
	
	
	// Private instance methods
	
	private Entity entity(String name)
	{
		Entity entity = (Entity) this.entities.objectForKey(name);
		
		if (entity == null) {
			entity = entityWithName(name);
			
			this.entities.setObjectForKey(entity, name);
		}
		
		return entity;
	}
	
	
	
	
	// Inner classes
	
	public static class Entity implements NSKeyValueCoding
	{
		// Private instance variables
		
		private String				name;
		
		
		private NSMutableDictionary	rights;
		
		
		
		
		// Constructor
		
		protected Entity(String name)
		{
			this.name = name;
			this.rights = new NSMutableDictionary();
		}
		
		
		
		// Public instance methods
		
		public boolean mayAccess(String task)
		{
			return Boolean.TRUE.equals(rightForTask(task));
		}
		
		
		public void grantAccess(String task)
		{
			setRightForTask(Boolean.TRUE, task);
		}
		
		
		public void revokeAccess(String task)
		{
			setRightForTask(Boolean.FALSE, task);
		}
		
		
		public String name()
		{
			return this.name;
		}
		
		
		public int hashCode()
		{
			return name().hashCode();
		}
		
		
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see com.houdah.foundation.KVCObject#valueForKey(java.lang.String)
		 */
		public Object valueForKey(String key)
		{
			return mayAccess(key) ? Boolean.TRUE : Boolean.FALSE;
		}
		
		
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see com.houdah.foundation.KVCObject#takeValueForKey(java.lang.Object,
		 *      java.lang.String)
		 */
		public void takeValueForKey(Object value, String key)
		{
			setRightForTask((Boolean) value, key);
		}
		
		// Protected instance methods
		
		protected Boolean rightForTask(String task)
		{
			return (Boolean) this.rights.objectForKey(task);
		}
		
		protected void setRightForTask(Boolean right, String task)
		{
			DictionaryUtilities.safeSetObjectForKey(this.rights, right, task);
		}
	}
}