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

package com.houdah.agile.controllers;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.houdah.foundation.AbstractKeyValueCodingProtectedAccessor;

import com.webobjects.foundation.NSKeyValueCoding;

/**
 * Support for key-value coding access to protected methods and variables. <br/>
 * 
 * Concrete implementation of NSKeyValueCoding.ValueAccessor as suggested in the
 * documentation of that class. A similar implementation is provided by Apple
 * for the default package.
 * 
 * @see com.houdah.foundation.AbstractKeyValueCodingProtectedAccessor
 * @see com.webobjects.foundation.NSKeyValueCoding$ValueAccessor
 */
public class KeyValueCodingProtectedAccessor extends AbstractKeyValueCodingProtectedAccessor
{
	
	
	// Public instance methods
	
	public Object fieldValue(Object object, Field field) throws IllegalAccessException
	{
		NSKeyValueCoding.ValueAccessor accessor = (NSKeyValueCoding.ValueAccessor) accessor(field);
		
		if (accessor == this) {
			return field.get(object);
		} else {
			return accessor.fieldValue(object, field);
		}
	}
	
	
	public void setFieldValue(Object object, Field field, Object value)
			throws IllegalAccessException
	{
		NSKeyValueCoding.ValueAccessor accessor = (NSKeyValueCoding.ValueAccessor) accessor(field);
		
		if (accessor == this) {
			field.set(object, value);
		} else {
			accessor.setFieldValue(object, field, value);
		}
	}
	
	
	public Object methodValue(Object object, Method method) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException
	{
		NSKeyValueCoding.ValueAccessor accessor = (NSKeyValueCoding.ValueAccessor) accessor(method);
		
		if (accessor == this) {
			return method.invoke(object, (Object[]) null);
		} else {
			return accessor.methodValue(object, method);
		}
	}
	
	
	public void setMethodValue(Object object, Method method, Object value)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		NSKeyValueCoding.ValueAccessor accessor = (NSKeyValueCoding.ValueAccessor) accessor(method);
		
		if (accessor == this) {
			method.invoke(object, new Object[] { value });
		} else {
			accessor.setMethodValue(object, method, value);
		}
	}
}