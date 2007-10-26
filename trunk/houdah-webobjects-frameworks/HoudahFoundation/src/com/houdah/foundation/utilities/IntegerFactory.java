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

/**
 * Optimzation tool: Factory class for Integer objects.
 * 
 * @author bernard
 */
public class IntegerFactory
{
	// Protected class constants
	
	protected static final int			NEGATIVE_CAPACITY	= 128;
	
	
	protected static final int			POSITIVE_CAPACITY	= 128;
	
	
	protected static final int			MIN_YEAR			= 1850;
	
	
	protected static final int			MAX_YEAR			= 3001;
	
	
	protected static final Integer[]	NEGATIVE_INTEGERS	= new Integer[NEGATIVE_CAPACITY];
	
	
	protected static final Integer[]	POSITIVE_INTEGERS	= new Integer[POSITIVE_CAPACITY];
	
	
	protected static final Integer[]	YEAR_INTEGERS		= new Integer[MAX_YEAR
																	- MIN_YEAR];
	
	
	protected static boolean			hasBeenSetUp		= false;
	
	
	
	
	// Constructor
	
	/**
	 * You wouldn't dare to instantiate
	 */
	private IntegerFactory()
	{
	}
	
	
	
	// Public class methods
	
	public static Integer integerForInt(int i)
	{
		if (!hasBeenSetUp) {
			setUp();
		}
		
		if (i < 0) {
			int index = -i - 1;
			
			if (index < NEGATIVE_CAPACITY) {
				return NEGATIVE_INTEGERS[index];
			}
		} else {
			if (i < POSITIVE_CAPACITY) {
				return POSITIVE_INTEGERS[i];
			} else if ((i > MIN_YEAR) && (i < MAX_YEAR)) {
				return YEAR_INTEGERS[i - MIN_YEAR];
			}
			
		}
		
		return new Integer(i);
	}
	
	
	
	// Protected class methods
	
	protected static synchronized void setUp()
	{
		if (!hasBeenSetUp) {
			for (int n = 0; n < NEGATIVE_CAPACITY; n++) {
				NEGATIVE_INTEGERS[n] = new Integer(-n - 1);
			}
			
			for (int p = 0; p < POSITIVE_CAPACITY; p++) {
				POSITIVE_INTEGERS[p] = new Integer(p);
			}
			
			for (int y = MIN_YEAR; y < MAX_YEAR; y++) {
				YEAR_INTEGERS[y - MIN_YEAR] = new Integer(y);
			}
			
			hasBeenSetUp = true;
		}
	}
}