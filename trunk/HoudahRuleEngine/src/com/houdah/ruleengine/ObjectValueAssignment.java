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
import com.webobjects.foundation.NSKeyValueCodingAdditions;

/**
 * Assignement that allows values to be keyPaths calling back into the context
 * or a previously computed value.
 * 
 * Examples:<br/>
 * <ul>
 * <li>A value of "#value" produces "value"
 * <li>A value of "key.path" produces context.valueForKeyPath("key.path")
 * <li>A value of "@key.path" produces
 * context.valueForKeyPath(context.valueForKeyPath("key.path"));
 * <li>A value of "key1.path1/key2.path2"
 * context.valueForKeyPath("key1.path1").valueForKeyPath("key2.path2");
 * <li>A value of "key1.path1/@key2.path2"
 * context.valueForKeyPath("key1.path1").valueForKeyPath(context.valueForKeyPath("key2.path2"));
 * <li>...
 * </ul>
 * 
 * @author bernard
 */
public class ObjectValueAssignment implements Assignment
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
	public ObjectValueAssignment(String keyPath, Object value)
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
		String value = (String) value();
		
		return evaluate(context, value, context);
	}
	
	
	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(keyPath());
		buffer.append(" = evaluate(\"");
		buffer.append(value());
		buffer.append("\")");
		
		return buffer.toString();
	}
	
	
	public boolean equals(Object object)
	{
		if ((object != null) && (getClass() == object.getClass())) {
			ObjectValueAssignment other = (ObjectValueAssignment) object;
			
			return ((this.keyPath.equals(other.keyPath)) && (this.value.equals(other.value)));
		}
		
		return false;
	}
	
	
	public int hashCode()
	{
		return this.keyPath.hashCode();
	}
	
	
	public Object clone()
	{
		ObjectValueAssignment clone = new ObjectValueAssignment(keyPath(), value());
		
		return clone;
	}
	
	
	
	// Conformance with NSCoding
	
	public Class classForCoder()
	{
		return getClass();
	}
	
	
	public static Object decodeObject(NSCoder coder)
	{
		return new ObjectValueAssignment((String) coder.decodeObject(), coder.decodeObject());
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
	
	
	public static Object decodeWithKeyValueUnarchiver(EOKeyValueUnarchiver keyValueUnarchiver)
	{
		return new ObjectValueAssignment((String) keyValueUnarchiver
				.decodeObjectForKey(KEYPATH_KEY), keyValueUnarchiver.decodeObjectForKey(VALUE_KEY));
	}
	
	
	
	// Public class methods
	
	public static ObjectValueAssignment objectValueAssignment(String keyPath, Object value)
	{
		return new ObjectValueAssignment(keyPath, value);
	}
	
	
	
	// Private class methods
	
	private static Object evaluate(Object currentObject, String currentPath, RuleContext context)
	{
		int slashIndex = currentPath.indexOf('/');
		
		if (slashIndex == -1) {
			String path = currentPath;
			char firstChar = path.charAt(0);
			
			if (firstChar == '@') {
				String subPath = path.substring(1, path.length());
				
				path = (String) context.valueForKeyPath(subPath);
			} else if (firstChar == '#') {
				return path.substring(1, path.length());
			}
			
			return NSKeyValueCodingAdditions.Utility.valueForKeyPath(currentObject, path);
		} else {
			String prefix = currentPath.substring(0, slashIndex);
			Object object = evaluate(currentObject, prefix, context);
			
			if (object != null) {
				String suffix = currentPath.substring(slashIndex + 1);
				
				return evaluate(object, suffix, context);
			} else {
				return null;
			}
		}
	}
}