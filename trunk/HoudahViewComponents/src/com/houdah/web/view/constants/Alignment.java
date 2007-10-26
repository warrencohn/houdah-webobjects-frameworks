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

package com.houdah.web.view.constants;

import com.houdah.foundation.KVCObject;

public final class Alignment extends KVCObject
{
	// Public class constants
	
	/**
	 * Possible values for alignment
	 */
	public static final Alignment	LEFT	= new Alignment("left"),
			CENTER = new Alignment("center"), RIGHT = new Alignment("right");
	
	
	
	// Private instance variables
	
	/*
	 * Internal identification
	 */
	private String					value;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 */
	private Alignment(String value)
	{
		this.value = value;
	}
	
	
	
	// Public instance methods
	
	public String value()
	{
		return this.value;
	}
	
	
	public String toString()
	{
		return this.value;
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		return this.value.hashCode();
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other)
	{
		return ((this == other) || ((other instanceof Alignment) && (((Alignment) other).value == this.value)));
	}
	
	
	
	// Public class methods
	
	public static Alignment defaultAlignment()
	{
		return Alignment.LEFT;
	}
	
}