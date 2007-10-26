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

import com.houdah.appserver.components.Component;
import com.houdah.web.control.controllers.AbstractComponentController;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;

public abstract class ControllerComponent extends Component
{
	// Private instance variables
	
	private AbstractComponentController	controller;
	
	
	
	
	// Constructor
	
	/**
	 * Designated constructor
	 * 
	 * @param context
	 *            the context in which this component is instantiated
	 */
	public ControllerComponent(WOContext context)
	{
		super(context);
	}
	
	
	
	// Public instance methods
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.appserver.WOComponent#awake()
	 */
	public final void awake()
	{
		if (this.controller != null) {
			this.controller._willAwake();
			this.controller.willAwake();
		}
		
		doAwake();
		
		if (this.controller != null) {
			this.controller.didAwake();
		}
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.appserver.WOComponent#takeValuesFromRequest(com.webobjects.appserver.WORequest,
	 *      com.webobjects.appserver.WOContext)
	 */
	public final void takeValuesFromRequest(WORequest request, WOContext context)
	{
		this.controller.willTakeValuesFromRequest(request, context);
		
		doTakeValuesFromRequest(request, context);
		
		this.controller.didTakeValuesFromRequest(request, context);
	}
	
	
	public final WOActionResults invokeAction(WORequest request, WOContext context)
	{
		this.controller.willInvokeAction(request, context);
		
		WOActionResults actionResults = doInvokeAction(request, context);
		
		this.controller.didInvokeAction(request, context, actionResults);
		
		return actionResults;
	}
	
	
	public final void appendToResponse(WOResponse response, WOContext context)
	{
		this.controller.willAppendToResponse(response, context);
		
		doAppendToResponse(response, context);
		
		this.controller.didAppendToResponse(response, context);
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.appserver.WOComponent#sleep()
	 */
	public final void sleep()
	{
		this.controller.willSleep();
		
		doSleep();
		
		this.controller.didSleep();
		this.controller._didSleep();
	}
	
	
	public final void reset()
	{
		this.controller.willReset();
		
		doReset();
		
		this.controller.didReset();
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
	
	protected AbstractComponentController controller()
	{
		return this.controller;
	}
	
	
	protected void setController(AbstractComponentController controller)
	{
		this.controller = controller;
	}
	
	
	
	// Control points
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.appserver.WOComponent#awake()
	 */
	protected void doAwake()
	{
		super.awake();
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.appserver.WOComponent#takeValuesFromRequest(com.webobjects.appserver.WORequest,
	 *      com.webobjects.appserver.WOContext)
	 */
	protected void doTakeValuesFromRequest(WORequest request, WOContext context)
	{
		super.takeValuesFromRequest(request, context);
	}
	
	
	protected WOActionResults doInvokeAction(WORequest request, WOContext context)
	{
		return super.invokeAction(request, context);
	}
	
	
	protected void doAppendToResponse(WOResponse response, WOContext context)
	{
		super.appendToResponse(response, context);
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.appserver.WOComponent#sleep()
	 */
	protected void doSleep()
	{
		super.sleep();
	}
	
	
	protected void doReset()
	{
		super.reset();
	}
}
