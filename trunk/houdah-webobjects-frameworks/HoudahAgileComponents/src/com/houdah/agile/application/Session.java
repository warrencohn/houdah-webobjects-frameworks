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

import java.lang.reflect.Constructor;

import com.houdah.foundation.ForwardException;
import com.houdah.ruleengine.RuleContext;
import com.houdah.web.control.controllers.AbstractController;

import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableDictionary;

public class Session extends com.houdah.web.control.application.Session
{
	// Protected class constants
	
	protected static final String	RULE_CONTEXT_KEY	= "ruleContext";
	
	
	
	// Private class constants
	
	private static final long		serialVersionUID	= -7844676096857925572L;
	
	
	
	// Protected instance variables
	
	protected RuleContext			ruleContext;
	
	
	
	
	// Private instance variables
	
	// Constructor
	
	/**
	 * Designated constructor.
	 * 
	 */
	public Session()
	{
		this.ruleContext = createRuleContext();
	}
	
	
	
	// Public accessors
	
	public RuleContext ruleContext()
	{
		return this.ruleContext;
	}
	
	
	
	// Public instance methods
	
	public NSDictionary controllerDescriptionWithEntityAndTask(String entityName, String task)
	{
		RuleContext localRuleContext = createNestedRuleContext();
		
		localRuleContext.takeValueForKey(entityName, "entityName");
		localRuleContext.takeValueForKey(task, "task");
		
		String controllerClass = (String) localRuleContext.valueForKey("controllerClass");
		
		if (controllerClass == null) {
			throw new IllegalArgumentException("No controller class defined for: (" + entityName
					+ ", " + task + ")");
		}
		
		NSMutableDictionary description = new NSMutableDictionary();
		
		description.setObjectForKey(entityName, ENTITY_NAME_KEY);
		description.setObjectForKey(task, TASK_KEY);
		description.setObjectForKey(controllerClass, CONTROLLER_CLASS_KEY);
		description.setObjectForKey(localRuleContext, RULE_CONTEXT_KEY);
		
		return description;
	}
	
	
	
	/**
	 * Utility method: meant only to be called from
	 * controllerWithEntityAndTask(entityName, task)
	 * 
	 * @param controllerName
	 *            name of the controller to instantiate
	 * @param description
	 *            dictionary containing at least the 'controllerName',
	 *            'entityName' and 'task' keys
	 * @return an instance of AbstractController
	 */
	public AbstractController controllerWithDescription(NSDictionary description)
	{
		try {
			String entityName = (String) description.objectForKey(ENTITY_NAME_KEY);
			String task = (String) description.objectForKey(TASK_KEY);
			String controllerName = (String) description.objectForKey(CONTROLLER_CLASS_KEY);
			RuleContext ruleContext = (RuleContext) description.objectForKey(RULE_CONTEXT_KEY);
			
			assert entityName != null;
			assert task != null;
			assert controllerName != null;
			
			AbstractController controller = null;
			
			if (ruleContext != null) {
				try {
					Class controllerClass = Class.forName(controllerName);
					Constructor constructor = controllerClass.getConstructor(new Class[] {
							String.class, String.class, RuleContext.class });
					controller = (AbstractController) constructor.newInstance(new Object[] {
							entityName, task, ruleContext });
				} catch (NoSuchMethodException nsme) {
					controller = null;
				}
			}
			
			if (controller == null) {
				Class controllerClass = Class.forName(controllerName);
				Constructor constructor = controllerClass.getConstructor(new Class[] {
						String.class, String.class });
				controller = (AbstractController) constructor.newInstance(new Object[] {
						entityName, task });
			}
			
			return controller;
		} catch (Exception e) {
			throw new ForwardException(e);
		}
	}
	
	
	
	// Request-response loop checkpoints
	
	public void awake()
	{
		super.awake();
	}
	
	
	public void sleep()
	{
		super.sleep();
	}
	
	
	
	// Protected instance methods
	
	protected RuleContext createRuleContext()
	{
		Application application = Application.agileInstance();
		RuleContext newRuleContext = new RuleContext(application.ruleContext());
		
		newRuleContext.takeValueForKey(this, "session");
		
		return newRuleContext;
	}
	
	
	protected RuleContext createNestedRuleContext()
	{
		RuleContext newRuleContext = new RuleContext(ruleContext());
		
		return newRuleContext;
	}
}