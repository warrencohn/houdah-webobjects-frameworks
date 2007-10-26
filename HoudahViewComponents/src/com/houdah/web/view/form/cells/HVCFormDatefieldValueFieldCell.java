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

import com.houdah.web.view.form.descriptors.FormFieldDescriptor;
import com.houdah.web.view.form.values.Value;

import com.webobjects.appserver.WOContext;

public class HVCFormDatefieldValueFieldCell extends HVCFormTextfieldValueFieldCell
{
	// Private class constants
	
	private static final long	serialVersionUID	= 5034357855222317009L;
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 * 
	 * @param context
	 *            the context in which this component is instantiated
	 */
	public HVCFormDatefieldValueFieldCell(WOContext context)
	{
		super(context);
	}
	
	
	
	// Protected instance methods
	
	protected String textValue() throws Exception
	{
		return (String) value().rawValue();
	}
	
	
	protected void setTextValue(String textValue)
	{
		value().setRawValue(textValue);
	}
	
	
	protected String otherTagString()
	{
		return super.otherTagString();
	}
	
	
	
	/**
	 * Value to display. Available during the request-reponse loop.
	 * 
	 * @return value
	 */
	protected Value value()
	{
		return super.value();
	}
	
	
	
	/**
	 * Descriptor upon which to base the cell. Available during the
	 * request-reponse loop.
	 * 
	 * @return static descriptor object
	 */
	protected FormFieldDescriptor cellDescriptor()
	{
		return super.cellDescriptor();
	}
}