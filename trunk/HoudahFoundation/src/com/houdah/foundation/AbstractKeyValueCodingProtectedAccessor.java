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

package com.houdah.foundation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import com.webobjects.foundation.NSKeyValueCoding;

/**
 * This is a workaround for the fact that KeyValueCodingProtectedAccessor cannot
 * access protected fields and methods which are declared in a superclass of
 * object when that superclass is located in a different package from
 * KeyValueCodingProtectedAccessor (and object's) package.
 * 
 * This bug has been reported to Apple. It remains current as of WebObjects
 * 5.3.x.
 */
public abstract class AbstractKeyValueCodingProtectedAccessor extends
		NSKeyValueCoding.ValueAccessor
{
	// Private class constants
	
	private static final HashMap	cache	= new HashMap();
	
	
	
	
	// Protected instance methods
	
	protected NSKeyValueCoding.ValueAccessor accessor(Field field)
	{
		NSKeyValueCoding.ValueAccessor accessor = (NSKeyValueCoding.ValueAccessor) AbstractKeyValueCodingProtectedAccessor.cache
				.get(field);
		
		if (accessor == null) {
			accessor = determineAccessor(field);
			
			AbstractKeyValueCodingProtectedAccessor.cache.put(field, accessor);
		}
		
		return accessor;
	}
	
	
	protected NSKeyValueCoding.ValueAccessor accessor(Method method)
	{
		NSKeyValueCoding.ValueAccessor accessor = (NSKeyValueCoding.ValueAccessor) AbstractKeyValueCodingProtectedAccessor.cache
				.get(method);
		
		if (accessor == null) {
			accessor = determineAccessor(method);
			
			AbstractKeyValueCodingProtectedAccessor.cache.put(method, accessor);
		}
		
		return accessor;
	}
	
	
	
	// Private instance methods
	
	private NSKeyValueCoding.ValueAccessor determineAccessor(Field field)
	{
		if (Modifier.isPublic(field.getModifiers())) {
			return this;
		} else {
			try {
				
				String fieldName = field.getName();
				Class targetClass = field.getDeclaringClass();
				
				while (targetClass != null) {
					Field[] fields = targetClass.getDeclaredFields();
					int fCount = fields.length;
					
					for (int f = 0; f < fCount; f++) {
						Field currentField = fields[f];
						
						if (fieldName.equals(currentField.getName())) {
							Class declaringClass = currentField.getDeclaringClass();
							NSKeyValueCoding.ValueAccessor accessor = protectedAccessorForPackageNamed(declaringClass
									.getPackage().getName());
							
							return accessor;
						}
					}
					
					targetClass = targetClass.getSuperclass();
				}
				
				throw new NoSuchFieldException(fieldName);
			} catch (SecurityException se) {
				throw new ForwardException(se);
			} catch (NoSuchFieldException nsfe) {
				throw new ForwardException(nsfe);
			}
		}
	}
	
	
	private NSKeyValueCoding.ValueAccessor determineAccessor(Method method)
	{
		if (Modifier.isPublic(method.getModifiers())) {
			return this;
		} else {
			try {
				String methodName = method.getName();
				Class targetClass = method.getDeclaringClass();
				
				while (targetClass != null) {
					Method[] methods = targetClass.getDeclaredMethods();
					int fCount = methods.length;
					
					for (int f = 0; f < fCount; f++) {
						Method currentMethod = methods[f];
						
						if (methodName.equals(currentMethod.getName())) {
							Class declaringClass = currentMethod.getDeclaringClass();
							NSKeyValueCoding.ValueAccessor accessor = protectedAccessorForPackageNamed(declaringClass
									.getPackage().getName());
							
							return accessor;
						}
					}
					
					targetClass = targetClass.getSuperclass();
				}
				
				throw new NoSuchMethodException(methodName);
			} catch (SecurityException se) {
				throw new ForwardException(se);
			} catch (NoSuchMethodException nsme) {
				throw new ForwardException(nsme);
			}
		}
	}
}