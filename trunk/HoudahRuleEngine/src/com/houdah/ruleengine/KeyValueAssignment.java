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

package com.houdah.ruleengine;

import com.webobjects.eocontrol.EOKeyValueArchiver;
import com.webobjects.eocontrol.EOKeyValueUnarchiver;
import com.webobjects.foundation.NSCoder;

public class KeyValueAssignment implements Assignment
{
	// Protected class constants
	
	protected static final String	KEYPATH_KEY	= "keyPath";
	
	
	protected static final String	VALUE_KEY	= "value";
	
	
	
	// Private instance variables
	
	private String					keyPath;
	
	
	private Object					value;
	
	
	
	
	// Constructors
	
	/**
	 * Designated constructor.
	 * 
	 */
	public KeyValueAssignment(String keyPath, Object value)
	{
		this.keyPath = keyPath;
		this.value = value;
	}
	
	
	
	// Public accessors
	
	public String keyPath()
	{
		return this.keyPath;
	}
	
	
	public Object value()
	{
		return this.value;
	}
	
	
	
	// Public instance methods
	
	public Object fireInContext(RuleContext context)
	{
		// Treat the value of this key as a key path within the context. Return
		// whatever value is at that key path within the context. When using
		// KeyAssignments, be careful not to create circular rule dependencies;
		
		Object myValue = value();
		
		if (myValue instanceof String) {
			return context.valueForKeyPath((String) myValue);
		} else {
			return null;
		}
	}
	
	
	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(keyPath());
		buffer.append(" = ");
		
		if (value() instanceof String) {
			buffer.append("\"");
			buffer.append(value());
			buffer.append("\"");
		} else {
			buffer.append(value());
		}
		
		return buffer.toString();
	}
	
	
	public boolean equals(Object object)
	{
		if ((object != null) && (getClass() == object.getClass())) {
			KeyValueAssignment other = (KeyValueAssignment) object;
			
			return ((this.keyPath.equals(other.keyPath)) && (this.value
					.equals(other.value)));
		}
		
		return false;
	}
	
	
	public int hashCode()
	{
		return this.keyPath.hashCode();
	}
	
	
	public Object clone()
	{
		KeyValueAssignment clone = new KeyValueAssignment(keyPath(), value());
		
		return clone;
	}
	
	
	
	// Conformance with NSCoding
	
	public Class classForCoder()
	{
		return getClass();
	}
	
	
	public static Object decodeObject(NSCoder coder)
	{
		return new KeyValueAssignment((String) coder.decodeObject(), coder
				.decodeObject());
	}
	
	
	public void encodeWithCoder(NSCoder coder)
	{
		coder.encodeObject(this.keyPath);
		coder.encodeObject(this.value);
	}
	
	
	
	// Conformance with KeyValueCodingArchiving
	
	public void encodeWithKeyValueArchiver(EOKeyValueArchiver keyValueArchiver)
	{
		keyValueArchiver.encodeObject(this.keyPath, KEYPATH_KEY);
		keyValueArchiver.encodeObject(this.value, VALUE_KEY);
	}
	
	
	public static Object decodeWithKeyValueUnarchiver(
			EOKeyValueUnarchiver keyValueUnarchiver)
	{
		return new KeyValueAssignment((String) keyValueUnarchiver
				.decodeObjectForKey(KEYPATH_KEY), keyValueUnarchiver
				.decodeObjectForKey(VALUE_KEY));
	}
	
	
	
	// Public class methods
	
	public static KeyValueAssignment keyValueAssignment(String keyPath, Object value)
	{
		return new KeyValueAssignment(keyPath, value);
	}
}