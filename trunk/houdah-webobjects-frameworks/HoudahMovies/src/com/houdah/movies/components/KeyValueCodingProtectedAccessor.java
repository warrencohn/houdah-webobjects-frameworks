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

package com.houdah.movies.components;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.webobjects.foundation.NSKeyValueCoding;

public class KeyValueCodingProtectedAccessor extends
		NSKeyValueCoding.ValueAccessor
{
	
	public KeyValueCodingProtectedAccessor()
	{
		super();
	}
	
	
	public Object fieldValue(Object object, Field field)
			throws IllegalArgumentException, IllegalAccessException
	{
		return field.get(object);
	}
	
	
	public void setFieldValue(Object object, Field field, Object value)
			throws IllegalArgumentException, IllegalAccessException
	{
		field.set(object, value);
	}
	
	
	public Object methodValue(Object object, Method method)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException
	{
		return method.invoke(object, null);
	}
	
	
	public void setMethodValue(Object object, Method method, Object value)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException
	{
		method.invoke(object, new Object[] { value });
	}
}
