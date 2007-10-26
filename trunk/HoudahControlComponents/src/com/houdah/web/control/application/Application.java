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

import java.text.Format;

import com.houdah.web.control.components.ControllerPage;
import com.houdah.web.control.controllers.AbstractPageController;
import com.houdah.web.control.support.ExceptionHandler;
import com.houdah.web.view.form.values.Value;

import com.webobjects.appserver.WOApplication;
import com.webobjects.appserver.WOContext;

public abstract class Application extends com.houdah.appserver.application.Application
{
	// Public class constants
	
	public static final String	SEARCH_TASK			= "search";
	
	
	public static final String	LIST_TASK			= "list";
	
	
	public static final String	DETAIL_TASK			= "detail";
	
	
	public static final String	EDIT_TASK			= "edit";
	
	
	public static final String	QUICK_SEARCH_TASK	= "quickSearch";
	
	
	
	// Protected instance variables
	
	protected ThreadStorage		threadStorage;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor.
	 * 
	 */
	public Application()
	{
		this.threadStorage = new ThreadStorage();
		
		Value.setDefaultExceptionHandler(new ExceptionHandler(this));
	}
	
	
	
	// Public accessors
	
	public ThreadStorage threadStorage()
	{
		return this.threadStorage;
	}
	
	
	
	// Public abstract
	
	/**
	 * Produces a formatter for describing objects of a given entity.
	 * 
	 * @param entityName
	 *            Name of the entity owning the objects to format.
	 * @return a shared formatter instance
	 */
	public abstract Format entityDescriber(String entityName);
	
	
	
	/**
	 * Produces a formatter for identifying objects of a given entity.
	 * 
	 * @param entityName
	 *            Name of the entity owning the objects to format.
	 * @return a shared formatter instance
	 */
	public abstract Format entityIdentifier(String entityName);
	
	
	
	// Protected instance methods
	
	protected ControllerPage pageWithController(AbstractPageController controller, WOContext context)
	{
		String pageName = controller.pageName();
		ControllerPage controllerPage = (ControllerPage) pageWithName(pageName, context);
		
		controller.setPage(controllerPage);
		controllerPage.setController(controller);
		
		return controllerPage;
	}
	
	
	
	// Public class methods
	
	public static Application sharedInstance()
	{
		return (Application) WOApplication.application();
	}
}