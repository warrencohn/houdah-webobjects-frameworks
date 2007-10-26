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

package com.houdah.web.view.form.descriptors;

import com.houdah.web.view.actions.Action;
import com.houdah.web.view.form.cells.HVCFormActionFieldCell;

import com.webobjects.foundation.NSMutableDictionary;

public class FormActionFieldDescriptor extends FormFieldDescriptor
{
	// Private instance variables

	private Action action;

	// Constructor

	/**
	 * Designated constructor
	 * 
	 * @param action
	 *            Action descriptor. Not null
	 */
	public FormActionFieldDescriptor(Action action)
	{
		super(null, action.cssClass());

		this.action = action;
	}

	// Public instance methods

	/**
	 * Accessor method.
	 * 
	 * @return the controller action
	 */
	public Action action()
	{
		return this.action;
	}

	/**
	 * CSS class to assign to the field
	 * 
	 * @return a CSS class name
	 */
	public String cssClass(NSMutableDictionary values)
	{
		return this.action.cssClass();
	}

	// Protected instance methods

	/**
	 * Determines the cell component used for display
	 * 
	 * @return a concrete subclass of the baseClass()
	 */
	protected Class cellClass()
	{
		return HVCFormActionFieldCell.class;
	}
}
