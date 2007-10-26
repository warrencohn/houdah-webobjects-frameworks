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

package com.houdah.web.control.components;

import com.houdah.appserver.components.Page;
import com.houdah.web.control.application.Session;
import com.houdah.web.control.controllers.AbstractPageController;
import com.houdah.web.control.controllers.SessionController;
import com.houdah.web.control.support.Warning;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSArray;

public class ControllerPage extends Page
{
	// Private class constants
	
	private static final long		serialVersionUID		= 8228367844459601249L;
	
	
	
	// Private instance variables
	
	private AbstractPageController	controller;
	
	
	private boolean					isAwake					= false;
	
	
	private boolean					needsToInitialize		= true;
	
	
	private boolean					showNavigationControls	= true;
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 * 
	 * @param context
	 *            the context in which this component is instantiated
	 */
	public ControllerPage(WOContext context)
	{
		super(context);
	}
	
	
	
	// Public accessors
	
	public void setController(AbstractPageController controller)
	{
		if ((this.controller != null) && (this != controller.page())) {
			throw new IllegalArgumentException("Page already has a controller");
		}
		
		this.controller = controller;
	}
	
	
	public AbstractPageController controller()
	{
		return this.controller;
	}
	
	
	public boolean showNavigationControls()
	{
		return this.showNavigationControls;
	}
	
	
	public void setShowNavigationControls(boolean showNavigationControls)
	{
		this.showNavigationControls = showNavigationControls;
	}
	
	
	
	// Protected accessors
	
	protected SessionController sessionController()
	{
		return ((Session) session()).sessionController();
	}
	
	
	
	// Public instance methods
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.appserver.WOComponent#awake()
	 */
	public final void awake()
	{
		super.awake();
		
		this.isAwake = true;
		
		initializePage();
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.appserver.WOComponent#takeValuesFromRequest(com.webobjects.appserver.WORequest,
	 *      com.webobjects.appserver.WOContext)
	 */
	public void takeValuesFromRequest(WORequest request, WOContext context)
	{
		initializePage();
		
		super.takeValuesFromRequest(request, context);
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.appserver.WOComponent#invokeAction(com.webobjects.appserver.WORequest,
	 *      com.webobjects.appserver.WOContext)
	 */
	public WOActionResults invokeAction(WORequest request, WOContext context)
	{
		initializePage();
		
		try {
			return super.invokeAction(request, context);
		} catch (Warning w) {
			sessionController().dispatchWarningMessage(w.getLocalizedMessage());
			
			return context().page();
		} catch (RuntimeException re) {
			throw re;
		}
	}
	
	
	public void appendToResponse(WOResponse aResponse, WOContext aContext)
	{
		initializePage();
		
		super.appendToResponse(aResponse, aContext);
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.appserver.WOComponent#awake()
	 */
	public final void sleep()
	{
		this.isAwake = false;
		
		super.sleep();
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.appserver.WOComponent#handleQueryWithUnboundKey(java.lang.String)
	 */
	public Object handleQueryWithUnboundKey(String key)
	{
		return this.controller.valueForKey(key);
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.appserver.WOComponent#handleTakeValueForUnboundKey(java.lang.Object,
	 *      java.lang.String)
	 */
	public void handleTakeValueForUnboundKey(Object object, String key)
	{
		this.controller.takeValueForKey(object, key);
	}
	
	
	
	// Protected instance methods
	
	protected void initializePage()
	{
		if (this.needsToInitialize && (this.controller != null) && this.isAwake) {
			this.needsToInitialize = false;
			
			this.controller.willInitializePage();
			
			doInitializePage();
			
			this.controller.didInitializePage();
		}
	}
	
	
	
	/**
	 * Initilize page on first wake
	 * 
	 */
	protected void doInitializePage()
	{
	}
	
	
	
	// Backtrack detection
	
	/**
	 * Determines if the component state needs to detect backtracking.<br/>
	 * 
	 * The sleepInContext and awakeInContext methods get called only if this
	 * method returns true.
	 * 
	 * @return true
	 */
	protected boolean needsBackTrackDetection()
	{
		return true;
	}
	
	
	
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
	protected NSArray getPersistentKeys()
	{
		if (this.controller != null) {
			return this.controller.getPersistentKeys();
		}
		
		return NSArray.EmptyArray;
	}
}