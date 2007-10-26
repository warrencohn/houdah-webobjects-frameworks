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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.houdah.foundation.ForwardException;
import com.houdah.web.view.form.descriptors.FormValueFieldDescriptor;

import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableDictionary;


public class ValueContainer
{
	// Protected instance variables
	
	protected NSMutableDictionary	values;
	
	
	protected Object				valueDelegate;
	
	
	
	
	// Constructor
	
	public ValueContainer(Object valueDelegate)
	{
		this.values = new NSMutableDictionary();
		this.valueDelegate = valueDelegate;
	}
	
	
	
	// Public instance methods
	
	public Value value(FormValueFieldDescriptor descriptor)
	{
		String key = descriptor.key();
		Value value = (Value) this.values.objectForKey(key);
		
		if (value == null) {
			value = createValue(descriptor);
			
			this.values.setObjectForKey(value, key);
		}
		
		return value;
	}
	
	
	public Object clone()
	{
		ValueContainer clone = new ValueContainer(this.valueDelegate);
		
		clone.values = this.values.mutableClone();
		
		return clone;
	}
	
	
	public NSDictionary dictionary()
	{
		return this.values.immutableClone();
	}
	
	// Protected instance methods
	
	/** Creates a fresh value. Called only when needed (lazily)
	 * 
	 * @param descriptor the descriptor to attach the value to
	 * @return the newly created value
	 */
	protected Value createValue(FormValueFieldDescriptor descriptor)
	{
		Value value = null;
		Class valueClass = descriptor.valueClass();
		
		try {
			Constructor constructor = valueClass
					.getConstructor(new Class[] {
							FormValueFieldDescriptor.class, Object.class });
			
			value = (Value) constructor.newInstance(new Object[] {
					descriptor, this.valueDelegate });
		} catch (NoSuchMethodException nme) {
			throw new ForwardException(nme);
		} catch (IllegalAccessException iae) {
			throw new ForwardException(iae);
		} catch (InstantiationException ie) {
			throw new ForwardException(ie);
		} catch (InvocationTargetException ite) {
			throw new ForwardException(ite);
		}
		
		return value;
	}
}