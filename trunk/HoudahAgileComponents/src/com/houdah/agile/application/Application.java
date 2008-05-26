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

package com.houdah.agile.application;

import java.text.Format;

import com.houdah.foundation.FormatterFactory;
import com.houdah.foundation.KeyFormatter;
import com.houdah.foundation.formatters.KeyFormatterFormatter;
import com.houdah.ruleengine.RuleContext;
import com.houdah.ruleengine.RuleModel;
import com.houdah.ruleengine.RuleModelUtilities;

import com.webobjects.appserver.WOApplication;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSMutableSet;
import com.webobjects.foundation.NSSet;

public abstract class Application extends com.houdah.web.control.application.Application
{
	// Public class constants
	
	public static final String		DESCRIBE_TASK	= "describe";
	
	
	public static final String		IDENTIFY_TASK	= "identify";
	
	
	
	// Protected instance variables
	
	protected RuleModel				ruleModel;
	
	
	protected RuleContext			ruleContext;
	
	
	protected RuleContext			formatterRuleContext;
	
	
	protected NSMutableDictionary	formattersForDescription;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor.
	 */
	public Application()
	{
		NSSet includeFiles = new NSSet(new Object[] { "labels.dictionary" });
		
		this.ruleModel = RuleModelUtilities.loadFromBundles("agile", null, null, includeFiles);
		this.ruleContext = createRuleContext();
		this.formatterRuleContext = createNestedRuleContext();
		this.formattersForDescription = new NSMutableDictionary();
	}
	
	
	
	// Public accessors
	
	public RuleContext ruleContext()
	{
		return this.ruleContext;
	}
	
	
	
	// Public instance methods
	
	
	/**
	 * Produces a formatter for describing objects of a given entity.
	 * 
	 * @param entityName
	 *            Name of the entity owning the objects to format.
	 * @return a shared formatter instance
	 */
	public Format entityDescriber(String entityName)
	{
		return formatterForEntityAndTask(entityName, DESCRIBE_TASK);
	}
	
	
	
	/**
	 * Produces a formatter for identifying objects of a given entity.
	 * 
	 * @param entityName
	 *            Name of the entity owning the objects to format.
	 * @return a shared formatter instance
	 */
	public Format entityIdentifier(String entityName)
	{
		return formatterForEntityAndTask(entityName, IDENTIFY_TASK);
	}
	
	
	
	// Protected instance methods
	
	protected RuleContext createRuleContext()
	{
		RuleContext newRuleContext = new RuleContext(this.ruleModel);
		
		newRuleContext.takeValueForKey(this, "application");
		
		return newRuleContext;
	}
	
	
	protected RuleContext createNestedRuleContext()
	{
		RuleContext newRuleContext = new RuleContext(ruleContext());
		
		return newRuleContext;
	}
	
	
	protected Format formatterForEntityAndTask(String entityName, String task)
	{
		RuleContext localContext = this.formatterRuleContext;
		
		synchronized (localContext) {
			localContext.takeValueForKey(entityName, "entityName");
			localContext.takeValueForKey(task, "task");
			
			NSArray formatter = (NSArray) localContext.valueForKey("formatter");
			
			if (formatter != null) {
				return formatterForDescription(formatter);
			} else {
				String formatterName = (String) localContext.valueForKey("formatterName");
				
				return FormatterFactory.sharedInstance().lookup(formatterName);
			}
		}
	}
	
	
	protected NSSet excludeModelNames()
	{
		NSMutableSet set = new NSMutableSet();
		
		set.addObject("validationArguments");
		set.addObject("validationRules");
		set.addObject("validation");
		set.addObject("messages");
		
		return set;
	}
	
	
	
	// Private instance methods
	
	private Format formatterForDescription(NSArray description)
	{
		Format formatter = (Format) this.formattersForDescription.objectForKey(description);
		
		if (formatter == null) {
			int dCount = description.count();
			NSMutableArray keyFormatters = new NSMutableArray(dCount);
			
			for (int d = 0; d < dCount; d++) {
				NSDictionary element = (NSDictionary) description.objectAtIndex(d);
				String keyPath = (String) element.objectForKey("keyPath");
				NSArray nestedDescription = (NSArray) element.objectForKey("formatter");
				String formatterName = (String) element.objectForKey("formatterName");
				Format nestedFormatter = null;
				
				if (nestedDescription != null) {
					nestedFormatter = formatterForDescription(nestedDescription);
				} else if (formatterName != null) {
					nestedFormatter = FormatterFactory.sharedInstance().lookup(formatterName);
				}
				
				keyFormatters.addObject(new KeyFormatter(keyPath, nestedFormatter));
			}
			
			formatter = new KeyFormatterFormatter(keyFormatters);
			
			this.formattersForDescription.setObjectForKey(formatter, description);
		}
		
		return formatter;
	}
	
	
	
	// Public class methods
	
	public static Application agileInstance()
	{
		return (Application) WOApplication.application();
	}
}