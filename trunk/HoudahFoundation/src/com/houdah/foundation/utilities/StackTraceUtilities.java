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

package com.houdah.foundation.utilities;

public class StackTraceUtilities
{
	// Protected class variables
	
	protected static ClassGetter	classGetter	= null;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 */
	private StackTraceUtilities()
	{
		throw new IllegalStateException("Do not instantiate this utility class");
	}
	
	
	
	// Public class methods
	/**
	 * Determines the class of the caller
	 * 
	 * @return the currently active class
	 */
	public static Class getCurrentClass()
	{
		if (classGetter == null) {
			classGetter = new ClassGetter();
		}
		
		return classGetter.getCurrentClass();
	}
	
	
	
	/**
	 * Determines the class of the caller's caller
	 * 
	 * @return the caller class
	 */
	public static Class getCallingClass()
	{
		if (classGetter == null) {
			classGetter = new ClassGetter();
		}
		
		return classGetter.getCallingClass();
	}
	
	
	
	
	// Inner classes
	
	private static class ClassGetter extends SecurityManager
	{
		public Class getCurrentClass()
		{
			return getClassContext()[2];
		}
		
		
		public Class getCallingClass()
		{
			Class[] classContext = getClassContext();
			
			if (classContext.length > 3) {
				return classContext[3];
			} else {
				return null;
			}
		}
	}
}