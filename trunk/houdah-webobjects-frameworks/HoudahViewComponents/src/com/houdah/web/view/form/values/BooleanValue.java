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

package com.houdah.web.view.form.values;

import com.houdah.web.view.form.descriptors.FormValueFieldDescriptor;

import com.webobjects.foundation.NSArray;

public class BooleanValue extends Value
{
	// Private instance variables
	
	private NSArray	trueValues;
	
	
	private Object	falseValue;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 * 
	 * @param cellDescriptor
	 *            cell descriptor
	 * @param valueDelegate
	 *            an object informally implementing the ValueDelegate interface
	 */
	public BooleanValue(FormValueFieldDescriptor cellDescriptor, Object valueDelegate)
	{
		super(cellDescriptor, valueDelegate);
	}
	
	
	
	// Protected instance methods
	
	protected void init()
	{
		this.trueValues = trueValues();
		this.falseValue = falseValue();
		
		super.init();
	}
	
	
	
	// Protected instance methods
	
	protected NSArray trueValues()
	{
		return new NSArray(Boolean.TRUE);
	}
	
	
	protected Object falseValue()
	{
		return Boolean.FALSE;
	}
	
	
	protected Object value(Object rawValue) throws Exception
	{
		Boolean bool = (Boolean) rawValue;
		
		return bool.booleanValue() ? this.trueValues.objectAtIndex(0) : this.falseValue;
	}
	
	
	protected Object rawValue(Object value)
	{
		return ((value != null) && (this.trueValues.containsObject(value))) ? Boolean.TRUE
				: Boolean.FALSE;
	}
}