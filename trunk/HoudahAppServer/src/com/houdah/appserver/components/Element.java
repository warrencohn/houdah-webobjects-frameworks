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

package com.houdah.appserver.components;

import com.webobjects.appserver.WOActionResults;
import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;

public abstract class Element extends Component
{
	// Private class constants
	
	private static final String	CONTROLLER_ACTION_PREFIX	= "controllerAction_";
	
	
	
	
	// Constructor
	
	/**
	 * @param context
	 *            the context in which this component is instantiated
	 */
	public Element(WOContext context)
	{
		super(context);
	}
	
	
	
	// Public instance methods
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.appserver.WOComponent#isStateless()
	 */
	public boolean isStateless()
	{
		return true;
	}
	
	
	
	/**
	 * Perform an action declared by the owning controller
	 * 
	 * @param actionName
	 *            the name of the action to perform
	 * @return result of the action as returned by the controller
	 */
	public WOActionResults performControllerAction(String actionName)
	{
		WOActionResults actionResults = null;
		WOComponent parent = parent();
		
		if (parent instanceof Element) {
			actionResults = performParentAction(CONTROLLER_ACTION_PREFIX
					+ actionName);
		} else {
			actionResults = performParentAction(actionName);
		}
		
		return actionResults;
	}
	
	
	
	/**
	 * Lookup the owning controller.
	 * 
	 * @return the closest parent which may assume the role of controller
	 */
	public WOComponent controller()
	{
		WOComponent parent = parent();
		
		if (parent instanceof Element) {
			return ((Element) parent).controller();
		} else {
			return parent;
		}
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.webobjects.appserver.WOComponent#handleQueryWithUnboundKey(java.lang.String)
	 */
	public Object handleQueryWithUnboundKey(String key)
	{
		if (key.startsWith(CONTROLLER_ACTION_PREFIX)) {
			return performControllerAction(key
					.substring(CONTROLLER_ACTION_PREFIX.length()));
		} else {
			return super.handleQueryWithUnboundKey(key);
		}
	}
	
}
