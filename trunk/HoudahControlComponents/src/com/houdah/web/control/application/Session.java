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

package com.houdah.web.control.application;

import java.lang.reflect.Constructor;
import java.util.Enumeration;

import com.houdah.foundation.ForwardException;
import com.houdah.web.control.components.ControllerPage;
import com.houdah.web.control.controllers.AbstractController;
import com.houdah.web.control.controllers.AbstractPageController;
import com.houdah.web.control.controllers.SessionController;

import com.webobjects.appserver.WOContext;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSDictionary;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;

public abstract class Session extends com.houdah.appserver.application.Session
{
	// Protected class constants
	
	protected static final String	ENTITY_NAME_KEY			= "entity";
	
	
	protected static final String	TASK_KEY				= "task";
	
	
	protected static final String	CONTROLLER_CLASS_KEY	= "controllerClass";
	
	
	
	// Private class constants
	
	private static final long		serialVersionUID		= -1395563267760962650L;
	
	
	
	// Private instance variables
	
	private NSMutableDictionary		userInfo;
	
	
	private SessionController		sessionController;
	
	
	private NSMutableArray			lockedContexts;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor.
	 * 
	 */
	public Session()
	{
		super();
		
		this.userInfo = new NSMutableDictionary();
		this.lockedContexts = new NSMutableArray();
	}
	
	
	
	// Public accessors
	
	public NSMutableDictionary userInfo()
	{
		return this.userInfo;
	}
	
	
	public SessionController sessionController()
	{
		if (this.sessionController == null) {
			this.sessionController = new SessionController(this);
		}
		
		return this.sessionController;
	}
	
	
	public void setSessionController(SessionController sessionController)
	{
		this.sessionController = sessionController;
	}
	
	
	
	// Public instance methods
	
	public EOEditingContext lockEditingContext(EOEditingContext editingContext)
	{
		editingContext.lock();
		
		this.lockedContexts.addObject(editingContext);
		
		return editingContext;
	}
	
	
	
	// Request-response loop checkpoints
	
	// Request-response loop checkpoints
	
	public void awake()
	{
		super.awake();
		
		Application application = Application.sharedInstance();
		ThreadStorage threadStorage = application.threadStorage();
		
		threadStorage.setSession(this);
		threadStorage.setContext(context());
	}
	
	
	public void sleep()
	{
		Application application = Application.sharedInstance();
		ThreadStorage threadStorage = application.threadStorage();
		
		threadStorage.setSession(null);
		threadStorage.setContext(null);
		
		Enumeration contextEnuneration = this.lockedContexts.objectEnumerator();
		
		while (contextEnuneration.hasMoreElements()) {
			EOEditingContext editingContext = (EOEditingContext) contextEnuneration.nextElement();
			
			editingContext.unlock();
		}
		
		this.lockedContexts.removeAllObjects();
		
		super.sleep();
	}
	
	
	public void terminate()
	{
		super.terminate();
	}
	
	
	
	// Public abstract methods
	
	/**
	 * To be implemented by subclasses.
	 * 
	 * Builds a dictionary containing at least the 'controllerName',
	 * 'entityName' and 'task' keys.
	 * 
	 * @param entityName
	 *            name of the entity to work on
	 * @param task
	 *            name of the task to perform
	 * @return a dictionary containing at least the 'controllerName',
	 *         'entityName' and 'task' keys
	 */
	public abstract NSDictionary controllerDescriptionWithEntityAndTask(String entityName,
			String task);
	
	
	
	// Public instance methods
	
	/**
	 * Instantiation of a new page.
	 * 
	 * @param entityName
	 *            name of the entity to work on
	 * @param task
	 *            name of the task to perform
	 * @param context
	 *            current context
	 * @return a page controller with matching page
	 */
	public ControllerPage pageWithEntityAndTask(String entityName, String task, WOContext context)
	{
		AbstractPageController controller = (AbstractPageController) controllerWithEntityAndTask(
				entityName, task);
		String pageName = controller.pageName();
		ControllerPage page = (ControllerPage) Application.application().pageWithName(pageName,
				context);
		
		page.setController(controller);
		controller.setPage(page);
		
		return page;
	}
	
	
	
	/**
	 * Instantiation of a new controller.
	 * 
	 * @param entityName
	 *            name of the entity to work on
	 * @param task
	 *            name of the task to perform
	 * @return a controller with no component or page attached yet
	 */
	public AbstractController controllerWithEntityAndTask(String entityName, String task)
	{
		NSDictionary description = controllerDescriptionWithEntityAndTask(entityName, task);
		
		return controllerWithDescription(description);
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
			String controllerClassName = (String) description.objectForKey(CONTROLLER_CLASS_KEY);
			
			assert entityName != null;
			assert task != null;
			assert controllerClassName != null;
			
			Class controllerClass = Class.forName(controllerClassName);
			Constructor constructor = controllerClass.getConstructor(new Class[] { String.class,
					String.class });
			AbstractController controller = (AbstractController) constructor
					.newInstance(new Object[] { entityName, task });
			
			return controller;
		} catch (Exception e) {
			throw new ForwardException(e);
		}
	}
}