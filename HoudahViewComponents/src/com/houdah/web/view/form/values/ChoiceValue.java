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

import java.util.Enumeration;

import com.houdah.web.view.form.descriptors.FormValueFieldDescriptor;

import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableSet;
import com.webobjects.foundation.NSSet;

public class ChoiceValue extends Value
{
	// Private instance variables
	
	private NSArray	valueList;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 * 
	 * @param cellDescriptor
	 *            cell descriptor
	 * @param valueDelegate
	 *            an object informally implementing the ValueDelegate interface
	 */
	public ChoiceValue(FormValueFieldDescriptor cellDescriptor,
			Object valueDelegate)
	{
		super(cellDescriptor, valueDelegate);
	}
	
	
	
	// Protected instance methods
	
	protected void init()
	{
		super.init();
		
		this.valueList = null;
		
		setValue(NSSet.EmptySet);
	}
	
	
	
	// Public instance methods
	
	public NSArray valueList()
	{
		return this.valueList;
	}
	
	
	public void setValueList(NSArray valueList)
	{
		this.valueList = valueList;
	}
	
	
	public void setValue(Object value)
	{
		if (value == null) {
			super.setValue(new NSSet());
		} else if (value instanceof NSSet) {
			NSSet set = (NSSet) value;
			
			super.setValue(set.immutableClone());
		} else if (value instanceof NSArray) {
			NSArray array = (NSArray) value;
			NSMutableSet set = new NSMutableSet(array.count());
			
			set.addObjectsFromArray(array);
			
			super.setValue(set.immutableClone());
		} else {
			super.setValue(new NSSet(value));
		}
	}
	
	
	public void setRawValue(Object rawValue)
	{
		if (rawValue == null) {
			super.setRawValue(new NSSet());
		} else if (rawValue instanceof NSSet) {
			NSSet set = (NSSet) rawValue;
			
			super.setRawValue(set.immutableClone());
		} else if (rawValue instanceof NSArray) {
			NSArray array = (NSArray) rawValue;
			NSMutableSet set = new NSMutableSet(array.count());
			
			set.addObjectsFromArray(array);
			
			super.setRawValue(set.immutableClone());
		} else {
			super.setRawValue(new NSSet(rawValue));
		}
	}
	
	
	
	// Protected instance methods
	
	protected Object value(Object rawValue) throws Exception
	{
		if (rawValue != null) {
			NSSet set = (NSSet) rawValue;
			int count = set.count();
			Enumeration enumeration = set.objectEnumerator();
			NSMutableSet value = new NSMutableSet(count);
			NSArray list = valueList();
			
			while (enumeration.hasMoreElements()) {
				String indexString = (String) enumeration.nextElement();
				int index = Integer.parseInt(indexString);
				
				value.addObject(list.objectAtIndex(index));
			}
			
			return value.immutableClone();
		} else {
			return NSSet.EmptySet;
		}
	}
	
	
	protected Object rawValue(Object value)
	{
		if (value != null) {
			NSSet set = (NSSet) value;
			int count = set.count();
			Enumeration enumeration = set.objectEnumerator();
			NSMutableSet rawValue = new NSMutableSet(count);
			NSArray list = valueList();
			
			while (enumeration.hasMoreElements()) {
				Object object = (Object) enumeration.nextElement();
				int index = list.indexOfObject(object);
				
				rawValue.addObject(String.valueOf(index));
			}
			
			return rawValue.immutableClone();
		} else {
			return NSSet.EmptySet;
		}
	}
}