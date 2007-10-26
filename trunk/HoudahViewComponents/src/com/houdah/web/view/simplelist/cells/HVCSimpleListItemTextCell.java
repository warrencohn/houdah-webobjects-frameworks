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

package com.houdah.web.view.simplelist.cells;

import java.text.Format;

import com.houdah.web.view.simplelist.descriptors.SimpleListItemTextDescriptor;

import com.webobjects.appserver.WOContext;

public class HVCSimpleListItemTextCell extends SimpleListItemCell
{
	// Private class constants
	
	private static final long	serialVersionUID	= 1266140280349201466L;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 * 
	 * @param context
	 *            the context in which this component is instantiated
	 */
	public HVCSimpleListItemTextCell(WOContext context)
	{
		super(context);
	}
	
	
	
	// Protected instance methods
	
	/**
	 * Builds the String used to display the target object in this cell
	 * 
	 * @return the formatted display String
	 */
	protected String displayString()
	{
		Object object = displayedObject();
		
		if (object == null) {
			return null;
		}
		
		SimpleListItemTextDescriptor cellDescriptor = (SimpleListItemTextDescriptor) cellDescriptor();
		Format formatter = cellDescriptor.formatter();
		
		if (formatter != null) {
			return formatter.format(object);
		}
		
		return object.toString();
	}
}