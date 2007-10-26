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

package com.houdah.web.control.descriptors;

import java.text.Format;

import com.houdah.web.control.application.Application;
import com.houdah.web.view.actions.Action;
import com.houdah.web.view.form.descriptors.FormTextfieldActionFieldDescriptor;
import com.houdah.web.view.form.values.Value;

public class FormQuickSearchFieldDescriptor extends FormTextfieldActionFieldDescriptor implements
		IdentifyDescriptor
{
	// Public class constants
	
	public static final String	QUICK_SEARCH_ACTION				= "quickSearchAction";
	
	
	public static final String	QUICK_SEARCH_LOUPE_CSS_CLASS	= "quickSearchLoupe";
	
	
	
	// Private instance variables
	
	private String				entityName;
	
	
	
	
	// Constructors
	
	
	/**
	 * Designated constructor
	 * 
	 * @param key
	 *            used for value look-up/storage in the form's value dictionary
	 * @param label
	 *            for display
	 * @param formatter
	 *            value transformer
	 * @param entityName
	 *            entity name for the quick search component
	 * @param size
	 *            size of the textfield in number of characters across
	 * @param maxLength
	 *            maximum number of characters accepted by the textfield
	 * @param actionLabel
	 *            label of the action to perform
	 * @param cssClass
	 *            CSS class to apply to the field
	 */
	public FormQuickSearchFieldDescriptor(String key, String label, String entityName, int size,
			Integer maxLength, String actionLabel, String cssClass)
	{
		super(key, label, Application.sharedInstance().entityDescriber(entityName), size,
				maxLength, null, new Action(QUICK_SEARCH_ACTION, actionLabel, null,
						QUICK_SEARCH_LOUPE_CSS_CLASS), cssClass);
		
		this.entityName = entityName;
	}
	
	
	
	// Public instance methods
	
	public String entityName()
	{
		return this.entityName;
	}
	
	
	
	/**
	 * Lookup method
	 * 
	 * @param value
	 *            lookup key
	 * @return matching formatter, may be null
	 */
	public Format parseFormatter(Value value)
	{
		// Parsing should be handled by the delegate: AbstractController
		return null;
	}
	
}
