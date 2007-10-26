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

package com.houdah.eocontrol;

import com.webobjects.eocontrol.EOObjectStore;

public class EditingContextFactory
{
	// Private class variables
	
	private static EditingContextFactory	sharedInstance	= null;
	
	
	
	
	// Constructor
	
	public EditingContextFactory()
	{
		
	}
	
	
	
	// Public instance methods
	
	public final EditingContext editingContext()
	{
		return prepareEditingContext(createEditingContext());
	}
	
	
	public final EditingContext editingContext(EOObjectStore parentObjectStore)
	{
		return prepareEditingContext(createEditingContext(parentObjectStore));
	}
	
	
	
	// Protected instance methods
	
	protected EditingContext createEditingContext()
	{
		return new EditingContext();
	}
	
	
	protected EditingContext createEditingContext(EOObjectStore parentObjectStore)
	{
		return new EditingContext(parentObjectStore);
	}
	
	
	protected EditingContext prepareEditingContext(EditingContext editingContext)
	{
		return editingContext;
	}
	
	
	
	// Public class methods
	
	public static EditingContextFactory sharedInstance()
	{
		if (EditingContextFactory.sharedInstance == null) {
			setSharedInstance(new EditingContextFactory());
		}
		
		return EditingContextFactory.sharedInstance;
	}
	
	
	public static void setSharedInstance(EditingContextFactory factory)
	{
		EditingContextFactory.sharedInstance = factory;
	}
}