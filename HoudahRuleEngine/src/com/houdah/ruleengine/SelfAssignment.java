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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.houdah.foundation.ForwardException;
import com.houdah.foundation.KVCObject;

import com.webobjects.eocontrol.EOKeyValueArchiver;
import com.webobjects.eocontrol.EOKeyValueUnarchiver;
import com.webobjects.foundation.NSCoder;

public abstract class SelfAssignment extends KVCObject implements Assignment
{
	// Protected class constants
	
	protected static final String	KEYPATH_KEY	= "keyPath";
	
	
	protected static final String	VALUE_KEY	= "value";
	
	
	
	// Private instance variables
	
	private String					keyPath;
	
	
	private String					value;
	
	
	
	
	// Constructors
	
	/**
	 * Designated constructor.
	 * 
	 */
	public SelfAssignment(String keyPath, String value)
	{
		this.keyPath = keyPath;
		this.value = value;
	}
	
	
	
	// Public accessors
	
	public String keyPath()
	{
		return this.keyPath;
	}
	
	
	public String value()
	{
		return this.value;
	}
	
	
	
	// Public instance methods
	
	public Object fireInContext(RuleContext context)
	{
		return valueForKeyPath(this.value());
	}
	
	
	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(keyPath());
		buffer.append(" = ");
		buffer.append("(");
		buffer.append(value());
		buffer.append(")");
		
		return buffer.toString();
	}
	
	
	public boolean equals(Object object)
	{
		if ((object != null) && (getClass() == object.getClass())) {
			SelfAssignment other = (SelfAssignment) object;
			
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
		Class assignmentClass = getClass();
		
		try {
			Constructor constructor = assignmentClass
					.getConstructor(new Class[] { String.class, String.class });
			
			SelfAssignment clone = (SelfAssignment) constructor
					.newInstance(new String[] { keyPath(), value() });
			
			return clone;
		} catch (NoSuchMethodException nsme) {
			throw new ForwardException(nsme);
		} catch (InvocationTargetException ite) {
			throw new ForwardException(ite);
		} catch (InstantiationException ie) {
			throw new ForwardException(ie);
		} catch (IllegalAccessException iae) {
			throw new ForwardException(iae);
		}
	}
	
	
	
	// Conformance with NSCoding
	
	public Class classForCoder()
	{
		return getClass();
	}
	
	
	public static Object decodeObject(Class assignmentClass, NSCoder coder)
	{
		try {
			Constructor constructor = assignmentClass
					.getConstructor(new Class[] { String.class, String.class });
			
			SelfAssignment clone = (SelfAssignment) constructor
					.newInstance(new String[] { (String) coder.decodeObject(),
							(String) coder.decodeObject() });
			
			return clone;
		} catch (NoSuchMethodException nsme) {
			throw new ForwardException(nsme);
		} catch (InvocationTargetException ite) {
			throw new ForwardException(ite);
		} catch (InstantiationException ie) {
			throw new ForwardException(ie);
		} catch (IllegalAccessException iae) {
			throw new ForwardException(iae);
		}
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
	
	
	public static Object decodeWithKeyValueUnarchiver(Class assignmentClass,
			EOKeyValueUnarchiver keyValueUnarchiver)
	{
		try {
			Constructor constructor = assignmentClass
					.getConstructor(new Class[] { String.class, String.class });
			
			SelfAssignment clone = (SelfAssignment) constructor
					.newInstance(new String[] {
							(String) keyValueUnarchiver
									.decodeObjectForKey(KEYPATH_KEY),
							(String) keyValueUnarchiver
									.decodeObjectForKey(VALUE_KEY) });
			
			return clone;
		} catch (NoSuchMethodException nsme) {
			throw new ForwardException(nsme);
		} catch (InvocationTargetException ite) {
			throw new ForwardException(ite);
		} catch (InstantiationException ie) {
			throw new ForwardException(ie);
		} catch (IllegalAccessException iae) {
			throw new ForwardException(iae);
		}
	}
}
