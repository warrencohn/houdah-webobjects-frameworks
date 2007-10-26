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

package com.houdah.web.view.form.cells;

import com.houdah.web.view.form.descriptors.FormMultipleValueFieldDescriptor;
import com.houdah.web.view.form.values.ChoiceValue;

import com.webobjects.appserver.WOContext;
import com.webobjects.foundation.NSSet;

public class FormMultipleValueFieldCell extends FormValueFieldCell
{
	// Private class constants
	
	private static final long	serialVersionUID	= 4484421305152730125L;
	
	
	
	// Private instance variables
	
	/**
	 * Loop index
	 */
	private int					currentIndex;
	
	
	
	/**
	 * Instance variable for the wrapper element ID
	 */
	private String				wrapperElementID;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 * 
	 * @param context
	 *            the context in which this component is instantiated
	 */
	public FormMultipleValueFieldCell(WOContext context)
	{
		super(context);
	}
	
	
	
	// Public instance methods
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.appserver.WOComponent#awake()
	 */
	public void awake()
	{
		super.awake();
		
		this.currentIndex = 0;
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.appserver.WOComponent#reset()
	 */
	public void reset()
	{
		this.currentIndex = -1;
		this.wrapperElementID = null;
		
		super.reset();
	}
	
	
	
	// Protected instance methods
	
	protected int currentIndex()
	{
		return this.currentIndex;
	}
	
	
	protected void setCurrentIndex(int currentIndex)
	{
		this.currentIndex = currentIndex;
	}
	
	
	protected String wrapperElementID()
	{
		return this.wrapperElementID;
	}
	
	
	protected void setWrapperElementID(String wrapperElementID)
	{
		this.wrapperElementID = wrapperElementID;
	}
	
	
	protected String currentItemId()
	{
		return ((FormMultipleValueFieldDescriptor) cellDescriptor()).key()
				+ "." + currentIndex();
	}
	
	
	protected boolean isChecked()
	{
		NSSet set = (NSSet) value().rawValue();
		
		return set.containsObject(String.valueOf(currentIndex()));
	}
	
	
	protected String otherTagString()
	{
		String otherTagString = super.otherTagString();
		
		if (isChecked()) {
			otherTagString += " checked";
		}
		
		return otherTagString;
	}
	
	
	protected String itemDisplayString()
	{
		ChoiceValue choiceValue = (ChoiceValue) value();
		int index = currentIndex();
		Object currentItem = choiceValue.valueList().objectAtIndex(index);
		
		return value().displayString(currentItem);
	}
}