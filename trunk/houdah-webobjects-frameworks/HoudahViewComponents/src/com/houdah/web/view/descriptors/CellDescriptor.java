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

package com.houdah.web.view.descriptors;

import com.houdah.web.view.components.Cell;

public abstract class CellDescriptor extends Descriptor
{
	// Private instance variables
	
	private String	elementName;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor.
	 */
	public CellDescriptor()
	{
		super();
	}
	
	
	
	// Public instance methods
	
	/**
	 * Determines the cell component used for display
	 * 
	 * @return name of the display cell element
	 */
	public final String elementName()
	{
		if (this.elementName == null) {
			Class baseClass = baseClass();
			Class cellClass = cellClass();
			
			if (!Cell.class.isAssignableFrom(baseClass)) {
				throw new IllegalArgumentException(
						"The base class must be a subclass of "
								+ Cell.class.getName());
			}
			if (!baseClass.isAssignableFrom(cellClass)) {
				throw new IllegalArgumentException(
						"The cell class must be a subclass of "
								+ baseClass.getName());
			}
			
			this.elementName = cellClass.getName();
		}
		
		return this.elementName;
	}
	
	
	
	// Protected instance methods
	
	/**
	 * Determines the class the element must be a subclass of
	 * 
	 * @return a concrete or abstract subclass of the Cell element
	 */
	protected abstract Class baseClass();
	
	
	
	/**
	 * Determines the cell component used for display
	 * 
	 * @return a concrete subclass of the baseClass()
	 */
	protected abstract Class cellClass();
}
