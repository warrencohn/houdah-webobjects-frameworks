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

package com.houdah.web.view.table.cells;

import java.text.Format;

import com.houdah.foundation.ForwardException;
import com.houdah.foundation.KVCUtility;
import com.houdah.web.view.table.descriptors.TableColumnRowTextDescriptor;

import com.webobjects.appserver.WOContext;

public class HVCTableColumnRowTextCell extends TableColumnRowCell
{
	// Private class constants
	
	private static final long	serialVersionUID	= 2093647424327990413L;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 * 
	 * @param context
	 *            the context in which this component is instantiated
	 */
	public HVCTableColumnRowTextCell(WOContext context)
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
		
		TableColumnRowTextDescriptor cellDescriptor = (TableColumnRowTextDescriptor) cellDescriptor();
		String keyPath = cellDescriptor.keyPath();
		
		if (keyPath == null) {
			return object.toString();
		}
		
		object = KVCUtility.sharedInstance().valueForKeyPath(object, keyPath);
		
		if (object == null) {
			return null;
		}
		
		Format formatter = cellDescriptor.formatter();
		
		if (formatter == null) {
			return object.toString();
		}
		
		try {
			return formatter.format(object);
		} catch (Exception exception) {
			throw new ForwardException(exception);
		}
	}
}