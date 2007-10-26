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

import com.houdah.appserver.components.Page;
import com.houdah.web.view.form.descriptors.FormValueFieldDescriptor;
import com.houdah.web.view.form.values.Value;
import com.houdah.web.view.form.values.ValueContainer;

import com.webobjects.appserver.WOContext;

public class FormValueFieldCell extends FormFieldCell
{
	// Private class constants
	
	private static final long	serialVersionUID	= -3257382311112084398L;
	
	
	
	// Private instance variables
	
	/**
	 * Value dictionary to display
	 */
	private ValueContainer		values;
	
	
	
	/**
	 * Cache of the value. Available during the request-response loop.
	 */
	private Value				value;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 * 
	 * @param context
	 *            the context in which this component is instantiated
	 */
	public FormValueFieldCell(WOContext context)
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
		this.values = (ValueContainer) valueForBinding("values");
		
		super.awake();
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.appserver.WOComponent#reset()
	 */
	public void reset()
	{
		this.values = null;
		this.value = null;
		
		super.reset();
	}
	
	
	
	// Protected instance methods
	
	protected String otherTagString()
	{
		String otherTagString = "";
		
		if (value().readOnly()) {
			otherTagString += "disabled ";
		}
		
		FormValueFieldDescriptor descriptor = (FormValueFieldDescriptor) cellDescriptor();
		String id = descriptor.key() + descriptor.hashCode();
		Page page = (Page) context().page();
		
		if (page.claimID(id)) {
			otherTagString += "id='" + id + "'";
		}
		
		return otherTagString;
	}
	
	
	
	/**
	 * Value dictionary to display. Available during the request-reponse loop.
	 * 
	 * @return value
	 */
	protected ValueContainer values()
	{
		return this.values;
	}
	
	
	
	/**
	 * Value to display. Available during the request-reponse loop.
	 * 
	 * @return value
	 */
	protected Value value()
	{
		if (this.value == null) {
			FormValueFieldDescriptor formValueFieldDescriptor = (FormValueFieldDescriptor) cellDescriptor();
			
			this.value = (Value) values().value(formValueFieldDescriptor);
		}
		
		return this.value;
	}
}
