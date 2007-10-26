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

package com.houdah.ruleengine;

import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableDictionary;

/**
 * Subclass of RuleContext.<br/>
 * 
 * Keeps tabs on locally used (set or inferred) values.
 * 
 * @author bernard
 * 
 */
public class TracingRuleContext extends RuleContext
{
	// Private instance variables
	
	private NSMutableDictionary	localValues;
	
	
	
	
	// Constructors
	
	/**
	 * Constructor.
	 * 
	 */
	public TracingRuleContext(RuleContext parentContext)
	{
		super(parentContext);
		
		init();
	}
	
	
	
	/**
	 * Constructor.
	 * 
	 */
	public TracingRuleContext(RuleModel model)
	{
		super(model);
		
		init();
	}
	
	
	
	// Initialization
	
	protected void init()
	{
		this.localValues = new NSMutableDictionary();
	}
	
	
	
	// Public instance methods
	
	/**
	 * Determines the values actually used by this context.
	 * 
	 * @return a dictionary of values indexed by their query keys
	 */
	public NSDictionary localValues()
	{
		NSMutableDictionary returnValue = this.localValues.mutableClone();
		RuleContext parentContext = parentContext();
		
		if (parentContext instanceof TracingRuleContext) {
			returnValue
					.addEntriesFromDictionary(((TracingRuleContext) parentContext).localValues());
		}
		
		return returnValue;
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.houdah.ruleengine.RuleContext#valueForKey(java.lang.String)
	 */
	public Object valueForKey(String key)
	{
		Object value = super.valueForKey(key);
		
		if (value != null) {
			this.localValues.setObjectForKey(value, key);
		}
		
		return value;
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.houdah.ruleengine.RuleContext#takeValueForKey(java.lang.Object,
	 *      java.lang.String)
	 */
	public void takeValueForKey(Object value, String key)
	{
		if (value != null) {
			this.localValues.setObjectForKey(value, key);
		} else {
			this.localValues.removeObjectForKey(key);
		}
		
		super.takeValueForKey(value, key);
	}
	
	
	public void takeValuesFromDictionary(NSDictionary dictionary)
	{
		this.localValues.addEntriesFromDictionary(dictionary);
		
		super.takeValuesFromDictionary(dictionary);
	}
}