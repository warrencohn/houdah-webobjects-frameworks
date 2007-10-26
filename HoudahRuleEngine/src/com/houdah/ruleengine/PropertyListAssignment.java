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

import com.webobjects.eocontrol.EOKeyValueUnarchiver;
import com.webobjects.foundation.NSCoder;
import com.webobjects.foundation.NSPropertyListSerialization;

public class PropertyListAssignment extends SimpleAssignment implements
		Assignment
{
	
	
	// Constructors
	
	/**
	 * Designated constructor.
	 * 
	 */
	public PropertyListAssignment(String keyPath, Object value)
	{
		super(keyPath, value);
	}
	
	
	
	// Public instance methods
	
	public Object fireInContext(RuleContext context)
	{
		Object value = super.fireInContext(context);
		
		return NSPropertyListSerialization
				.propertyListFromString((String) value);
	}
	
	
	public Object clone()
	{
		PropertyListAssignment clone = new PropertyListAssignment(keyPath(),
				value());
		
		return clone;
	}
	
	
	
	// Conformance with NSCoding
	
	public Class classForCoder()
	{
		return getClass();
	}
	
	
	public static Object decodeObject(NSCoder coder)
	{
		return new PropertyListAssignment((String) coder.decodeObject(), coder
				.decodeObject());
	}
	
	
	
	// Conformance with KeyValueCodingArchiving
	
	public static Object decodeWithKeyValueUnarchiver(
			EOKeyValueUnarchiver keyValueUnarchiver)
	{
		return new SimpleAssignment((String) keyValueUnarchiver
				.decodeObjectForKey(KEYPATH_KEY), keyValueUnarchiver
				.decodeObjectForKey(VALUE_KEY));
	}
	
	
	
	// Public class methods
	
	public static PropertyListAssignment propertyListAssignment(String keyPath, Object value)
	{
		return new PropertyListAssignment(keyPath, NSPropertyListSerialization
				.stringFromPropertyList(value));
	}
	
}
