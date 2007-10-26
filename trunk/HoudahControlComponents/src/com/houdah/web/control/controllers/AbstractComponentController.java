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

package com.houdah.web.control.controllers;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eocontrol.EOEditingContext;

public abstract class AbstractComponentController extends AbstractController
{
	// Constructor
	
	/**
	 * Designated constructor.
	 * 
	 * @param entityName
	 *            name of the entity to work on
	 * @param task
	 *            name of the task to perform
	 */
	public AbstractComponentController(String entityName, String task)
	{
		super(entityName, task);
	}
	
	
	
	// Public instance methods
	
	// Abstract methods
	
	public abstract String controllerComponentName();
	
	
	
	// Component controller methods
	
	public void _willAwake()
	{
	}
	
	
	public void willAwake()
	{
	}
	
	
	public void willTakeValuesFromRequest(WORequest request, WOContext context)
	{
	}
	
	
	public void willInvokeAction(WORequest request, WOContext context)
	{
	}
	
	
	public void willAppendToResponse(WOResponse response, WOContext context)
	{
	}
	
	
	public void willSleep()
	{
	}
	
	
	public void willReset()
	{
	}
	
	
	public void didInitializePage()
	{
	}
	
	
	public void didAwake()
	{
	}
	
	
	public void didTakeValuesFromRequest(WORequest request, WOContext context)
	{
	}
	
	
	public void didInvokeAction(WORequest request, WOContext context, WOActionResults actionResults)
	{
	}
	
	
	public void didAppendToResponse(WOResponse response, WOContext context)
	{
	}
	
	
	public void didSleep()
	{
	}
	
	
	public void _didSleep()
	{
	}
	
	
	public void didReset()
	{
	}
	
	
	
	// Protected instance methods
	
	protected EOEditingContext createEditingContext()
	{
		return sessionController().createEditingContext(null);
	}
}