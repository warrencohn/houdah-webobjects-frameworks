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

package com.houdah.eovalidation.control;

import com.houdah.eocontrol.EditingContext;
import com.houdah.ruleengine.RuleContext;
import com.houdah.ruleengine.RuleModel;
import com.houdah.ruleengine.RuleModelUtilities;

import com.webobjects.eocontrol.EOObjectStore;
import com.webobjects.foundation.NSSet;

public class ValidatingEditingContext extends EditingContext
{
	// Private class constants
	
	private static final long	serialVersionUID	= -5904849962942051953L;
	
	
	
	// Protected class variables
	
	protected static RuleModel	validationModel;
	
	
	
	// Protected instance variables
	
	protected RuleContext		validationContext;
	
	
	
	
	// Constructors
	
	public ValidatingEditingContext()
	{
		initValidatingEditingContext();
	}
	
	
	public ValidatingEditingContext(EOObjectStore parentStore)
	{
		super(parentStore);
		
		initValidatingEditingContext();
	}
	
	
	
	// Public instance methods
	
	/**
	 * Return the rule context to be used for object validation.
	 * 
	 * @return a rule context using a model containing validation rules and
	 *         arguments
	 */
	public RuleContext validationContext()
	{
		return this.validationContext;
	}
	
	
	
	// Protected instance methods
	
	protected void initValidatingEditingContext()
	{
		this.validationContext = new RuleContext(ruleModel());
	}
	
	
	
	// Protected class methods
	
	protected static RuleModel ruleModel()
	{
		if (ValidatingEditingContext.validationModel == null) {
			synchronized (ValidatingEditingContext.class) {
				if (ValidatingEditingContext.validationModel == null) {
					NSSet includeFiles = new NSSet(new Object[] { "labels.dictionary" });
					RuleModel newRuleModel = RuleModelUtilities.loadFromBundles("valid", null,
							null, includeFiles);
					
					ValidatingEditingContext.validationModel = newRuleModel;
				}
			}
		}
		
		return ValidatingEditingContext.validationModel;
	}
}