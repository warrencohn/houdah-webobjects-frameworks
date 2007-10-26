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

import com.houdah.appserver.support.ApplicationException;
import com.houdah.web.control.application.Session;
import com.houdah.web.control.components.ControllerPage;

import com.webobjects.appserver.WOContext;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOModelGroup;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOObjectStoreCoordinator;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableDictionary;

public abstract class AbstractPageController extends AbstractComponentController
{
	// Public class constants
	
	/**
	 * Key for the page's userInfo dictionary.
	 */
	public static final String	EDITING_CONTEXT_KEY	= "editingContext";
	
	
	
	// Private instance variables
	
	private EOEntity			entity;
	
	
	private ControllerPage		page;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor.
	 * 
	 * @param entityName
	 *            name of the entity to work on
	 * @param task
	 *            name of the task to perform
	 */
	public AbstractPageController(String entityName, String task)
	{
		super(entityName, task);
	}
	
	
	
	// Public instance methods
	
	
	// Public accessors
	
	public void setPage(ControllerPage page)
	{
		if ((this.page != null) && (this != this.page.controller())) {
			throw new IllegalArgumentException("Controller is already registered with a page");
		}
		
		this.page = page;
	}
	
	
	public ControllerPage page()
	{
		return this.page;
	}
	
	
	
	// Page controller methods
	
	public void willInitializePage()
	{
		checkAccessRights();
	}
	
	public EOEditingContext editingContext()
	{
		NSMutableDictionary userInfo = page().userInfo();
		EOEditingContext editingContext = (EOEditingContext) userInfo
				.valueForKey(EDITING_CONTEXT_KEY);
		
		if (editingContext == null) {
			editingContext = setEditingContext(createEditingContext());
		}
		
		return editingContext;
	}
	
	
	public EOEditingContext setEditingContext(EOEditingContext editingContext)
	{
		NSMutableDictionary userInfo = page().userInfo();
		
		if (editingContext != null) {
			session().lockEditingContext(editingContext);
			
			userInfo.takeValueForKey(editingContext, EDITING_CONTEXT_KEY);
		} else {
			userInfo.removeObjectForKey(EDITING_CONTEXT_KEY);
		}
		
		return editingContext;
	}
	
	
	public String pageName()
	{
		return ControllerPage.class.getName();
	}
	
	
	public EOEntity entity()
	{
		if (this.entity == null) {
			EOEditingContext editingContext = editingContext();
			EOModelGroup modelGroup = EOModelGroup
					.modelGroupForObjectStoreCoordinator((EOObjectStoreCoordinator) editingContext
							.rootObjectStore());
			
			this.entity = modelGroup.entityNamed(entityName());
		}
		
		return this.entity;
	}
	
	
	public void _willAwake()
	{
		super._willAwake();
		
		session().lockEditingContext(editingContext());
	}
	
	
	
	// Backtrack detection
	
	/**
	 * Returns the keys to the values that need to be persisted.<br/>
	 * 
	 * A null return value means that backtracking is prohibited.<br/> A
	 * NSArray.EmptyArray return value means that backtracking need not be
	 * handled.<br/>
	 * 
	 * The above 2 special cases are however best served by directly subclassing
	 * BackTrackComponent.
	 * 
	 * @see #needsStatePersistence
	 */
	public abstract NSArray getPersistentKeys();
	
	
	
	// Protected instance methods
	
	protected WOContext context()
	{
		return page().context();
	}
	
	
	protected Session session()
	{
		return (Session) context().session();
	}
	
	protected void checkAccessRights
	{
		if (!sessionController().securityManager().mayAccess(entityName(), task())) {
			throw new ApplicationException("Security exception",
										   ApplicationException.APPLICATION_ALIVE);
		}
	}
}